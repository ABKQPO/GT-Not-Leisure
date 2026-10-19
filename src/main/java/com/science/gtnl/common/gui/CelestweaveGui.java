package com.science.gtnl.common.gui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ColorPickerDialog;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveSettings;
import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnl.config.MainConfig;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public final class CelestweaveGui {

    private static final int STORED_EU_TEXTURE_WIDTH = 147;
    private static final int CONTENT_Y = 84;
    private static final int[] COLORS = { 0xFF3D9A, 0x3DE8FF, 0xFFC13D, 0xA35CFF, 0x4DFF88, 0xFF4040, 0xEEF0FF };
    private static final int[] EVENTS = { CelestweaveCosmetic.EVENT_HIT, CelestweaveCosmetic.EVENT_DASH,
        CelestweaveCosmetic.EVENT_LAND, CelestweaveCosmetic.EVENT_REFLECT, CelestweaveCosmetic.EVENT_WING,
        CelestweaveCosmetic.EVENT_ANTENNA, CelestweaveCosmetic.EVENT_LOW };
    private static final String[] EVENT_KEYS = { "hit", "dash", "land", "reflect", "wing", "antenna", "low" };
    private static final String[] PAGES = { "modules", "settings", "appearance" };

    private final PlayerInventoryGuiData data;
    private final PanelSyncManager sync;
    private final ItemStack stack;
    private final int armorType;
    private final boolean client;
    private final Map<CelestweaveModule, IntSyncValue> states = new EnumMap<>(CelestweaveModule.class);
    private int page;
    private ModuleHandler modules;
    private HolderHandler heldSlots;
    private int viewing;

    public CelestweaveGui(PlayerInventoryGuiData data, PanelSyncManager sync) {
        this.data = data;
        this.sync = sync;
        this.stack = data.getUsedItemStack();
        if (stack == null || !(stack.getItem() instanceof CelestweaveArmor armor))
            throw new IllegalStateException("Missing celestweave armor");
        this.armorType = armor.armorType;
        this.client = data.getPlayer().worldObj.isRemote;
    }

    private ItemStack armor() {
        ItemStack used = data.getUsedItemStack();
        return used != null && used.getItem() instanceof CelestweaveArmor ? used : stack;
    }

    private void changed() {
        data.getPlayer().inventory.markDirty();
    }

    private static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    public ModularPanel build() {
        ModularPanel panel = GTGuis.createPopUpPanel("celestweave_armor")
            .size(176, 272);
        modules = new ModuleHandler();
        sync.registerSlotGroup(new SlotGroup("celestweave_modules", CelestweaveData.MODULE_SLOTS, 0, true));
        boolean backpackButton = armorType == 1 && data.getSlotIndex() >= 36;
        panel.child(
            IKey.lang(stack.getUnlocalizedName() + ".name")
                .asWidget()
                .pos(8, 6)
                .size(backpackButton ? 106 : 160, 10));
        if (backpackButton) panel.child(
            new ButtonWidget<>().pos(116, 3)
                .size(52, 13)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .overlay(
                    IKey.dynamic(
                        () -> (installed(CelestweaveModule.BACKPACK) ? "" : "§8")
                            + tr("item.gtnl.celestweave.gui.open_backpack")))
                .onMousePressed(mouse -> {
                    if (mouse != 0 || !installed(CelestweaveModule.BACKPACK)) return false;
                    ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.BACKPACK));
                    return true;
                }));
        for (int i = 0; i < CelestweaveData.MODULE_SLOTS; i++) {
            final int slot = i;
            panel.child(
                new ItemSlot().slot(
                    new ModularSlot(modules, i).filter(item -> modules.isItemValid(slot, item))
                        .slotGroup("celestweave_modules"))
                    .pos(16 + i * 18, 18)
                    .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("item.gtnl.celestweave.gui.module_limits"))));
        }
        List<CelestweaveModule> holders = new ArrayList<>();
        for (CelestweaveModule module : CelestweaveModule.values())
            if (module.holdsItem && module.fits(armorType)) holders.add(module);
        if (!holders.isEmpty()) {
            HolderHandler held = new HolderHandler(holders.toArray(new CelestweaveModule[0]));
            heldSlots = held;
            sync.registerSlotGroup(new SlotGroup("celestweave_held", holders.size(), 1, true));
            for (int i = 0; i < holders.size(); i++) {
                final int slot = i;
                final CelestweaveModule module = holders.get(i);
                panel.child(
                    new ItemSlot().slot(
                        new ModularSlot(held, i).filter(item -> held.isItemValid(slot, item))
                            .slotGroup("celestweave_held"))
                        .pos(176 - 8 - 18 * (holders.size() - i), 38)
                        .tooltipBuilder(
                            tooltip -> tooltip.addLine(IKey.lang("item.gtnl.celestweave.gui.held." + module.id))));
            }
        }
        LongSyncValue energy = new LongSyncValue(() -> (long) energy());
        LongSyncValue capacity = new LongSyncValue(() -> (long) CelestweaveEnergy.capacity(armor()));
        sync.syncValue("energy", energy);
        sync.syncValue("capacity", capacity);
        panel.child(
            IKey.dynamic(
                () -> StatCollector.translateToLocalFormatted(
                    "item.gtnl.celestweave.tooltip.energy",
                    String.format("%,d", energy.getLongValue()),
                    String.format("%,d", capacity.getLongValue())))
                .asWidget()
                .pos(8, 42)
                .size(160 - 18 * holders.size(), 10));
        panel.child(
            new ProgressWidget()
                .value(
                    new DoubleValue.Dynamic(
                        () -> energy.getLongValue() / (double) Math.max(1, capacity.getLongValue()),
                        null))
                .texture(GTGuiTextures.PROGRESSBAR_STORED_EU, STORED_EU_TEXTURE_WIDTH)
                .pos(8, 58)
                .size(160, 5));
        for (int i = 0; i < PAGES.length; i++) {
            final int index = i;
            panel.child(
                new ButtonWidget<>().pos(8 + i * 54, 66)
                    .size(52, 14)
                    .background(GTGuiTextures.BUTTON_STANDARD)
                    .overlay(
                        IKey.dynamic(
                            () -> (page == index ? "§e" : "§7") + tr("item.gtnl.celestweave.gui.page." + PAGES[index])))
                    .onMousePressed(mouse -> {
                        if (mouse != 0) return false;
                        page = index;
                        return true;
                    }));
        }
        buildModulePage(panel);
        buildSettingsPage(panel);
        buildAppearancePage(panel);
        panel.bindPlayerInventory();
        return panel;
    }

    private double energy() {
        ItemStack armor = armor();
        return data.getSlotIndex() >= 36 ? CelestweaveEnergy.available(data.getPlayer(), armorType, armor)
            : CelestweaveEnergy.read(armor);
    }

    public void syncView(boolean bare) {
        if (client || modules == null) return;
        if (bare) {
            if (viewing++ > 0) return;
            if (heldSlots != null) heldSlots.view(true);
            modules.view(true);
        } else {
            if (viewing == 0 || --viewing > 0) return;
            modules.view(false);
            if (heldSlots != null) heldSlots.view(false);
        }
    }

    private int moduleState(CelestweaveModule module) {
        ItemStack armor = armor();
        if (!CelestweaveData.installed(armor)
            .contains(module)) return 0;
        return 1 | (CelestweaveData.active(armor, module) ? 2 : 0) | (CelestweaveData.shown(armor, module) ? 4 : 0);
    }

    private void setModuleState(CelestweaveModule module, int state) {
        ItemStack armor = armor();
        boolean changed = CelestweaveSettings
            .apply(armor, module, CelestweaveSettings.ENABLE, "", (state & 2) != 0 ? 1F : 0F);
        changed |= CelestweaveSettings.apply(armor, module, CelestweaveSettings.SHOW, "", (state & 4) != 0 ? 1F : 0F);
        if (changed) changed();
    }

    private boolean installed(CelestweaveModule module) {
        IntSyncValue state = states.get(module);
        return state != null && (state.getIntValue() & 1) != 0;
    }

    private void buildModulePage(ModularPanel panel) {
        int row = 0;
        for (CelestweaveModule module : CelestweaveModule.values()) {
            if (!module.toggleable() || !module.fits(armorType)) continue;
            IntSyncValue state = new IntSyncValue(() -> moduleState(module), value -> setModuleState(module, value))
                .allowC2S();
            sync.syncValue("module_" + module.id, state);
            states.put(module, state);
            panel.child(
                new ButtonWidget<>().pos(8 + (row % 2) * 81, CONTENT_Y + (row / 2) * 16)
                    .size(79, 14)
                    .background(GTGuiTextures.BUTTON_STANDARD)
                    .overlay(IKey.dynamic(() -> moduleLabel(module, state.getIntValue())))
                    .tooltipBuilder(tooltip -> {
                        tooltip.addLine(IKey.lang("item.gtnl.celestweave_module." + module.id + ".name"));
                        tooltip.addLine(IKey.lang("item.gtnl.celestweave_module." + module.id + ".tooltip"));
                        tooltip.addLine(
                            IKey.lang(
                                module.hideable() ? "item.gtnl.celestweave.gui.toggle_hint"
                                    : "item.gtnl.celestweave.gui.toggle_hint_enable"));
                    })
                    .setEnabledIf(widget -> page == 0)
                    .onMousePressed(mouse -> {
                        int value = state.getIntValue();
                        if ((value & 1) == 0 || mouse != 0 && mouse != 1) return false;
                        if (mouse == 1 && !module.hideable()) return false;
                        state.setIntValue(value ^ (mouse == 0 ? 2 : 4));
                        return true;
                    }));
            row++;
        }
    }

    private static String moduleLabel(CelestweaveModule module, int state) {
        String name = tr("item.gtnl.celestweave.short." + module.id);
        if ((state & 1) == 0) return "§8" + name;
        return ((state & 2) != 0 ? "§a" : "§c") + name
            + ((state & 4) != 0 ? "" : tr("item.gtnl.celestweave.gui.hidden_mark"));
    }

    private void buildSettingsPage(ModularPanel panel) {
        int row = 0;
        for (CelestweaveSettings.Option option : CelestweaveSettings.OPTIONS) {
            CelestweaveModule module = option.module();
            if (!module.fits(armorType)) continue;
            String syncKey = "option_" + module.id + "_" + option.key();
            String name = "item.gtnl.celestweave.option." + module.id
                + "."
                + option.key()
                    .toLowerCase();
            int y = CONTENT_Y + row * 16;
            row++;
            if (option.bool()) {
                addToggle(
                    panel,
                    syncKey,
                    name,
                    8,
                    y,
                    160,
                    () -> option.get(armor()) != 0,
                    enabled -> {
                        if (CelestweaveSettings
                            .apply(armor(), module, CelestweaveSettings.CFG, option.key(), enabled ? 1F : 0F))
                            changed();
                    },
                    () -> installed(module),
                    null).setEnabledIf(widget -> page == 1);
                continue;
            }
            IntSyncValue value = new IntSyncValue(
                () -> option.get(armor()),
                v -> {
                    if (CelestweaveSettings.apply(armor(), module, CelestweaveSettings.CFG, option.key(), v)) changed();
                }).allowC2S();
            sync.syncValue(syncKey, value);
            IntFunction<String> display = module == CelestweaveModule.REACH ? v -> String.valueOf(v + 1) : v -> "×" + v;
            stepper(
                panel,
                y,
                () -> (installed(module) ? "" : "§8") + tr(name) + ": " + display.apply(value.getIntValue()),
                value::getIntValue,
                v -> { if (installed(module)) value.setIntValue(v); },
                option.min(),
                option::max,
                1,
                1);
        }
        if (row == 0) panel.child(
            IKey.lang("item.gtnl.celestweave.gui.no_settings")
                .asWidget()
                .pos(8, CONTENT_Y + 2)
                .size(160, 10)
                .setEnabledIf(widget -> page == 1));
    }

    private ButtonWidget<?> addToggle(ModularPanel panel, String key, String lang, int x, int y, int width,
        BooleanSupplier getter, Consumer<Boolean> setter, BooleanSupplier allowed, String hint) {
        BooleanSyncValue value = new BooleanSyncValue(
            getter,
            enabled -> { if (allowed.getAsBoolean()) setter.accept(enabled); }).allowC2S();
        sync.syncValue(key, value);
        ButtonWidget<?> button = new ButtonWidget<>().pos(x, y)
            .size(width, 14)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(
                IKey.dynamic(() -> (allowed.getAsBoolean() ? value.getBoolValue() ? "§a" : "§7" : "§8") + tr(lang)))
            .tooltipBuilder(tooltip -> {
                if (hint != null) tooltip.addLine(IKey.lang(hint));
                tooltip.addLine(
                    IKey.dynamic(
                        () -> tr(
                            allowed.getAsBoolean()
                                ? value.getBoolValue() ? "item.gtnl.railgun.enabled" : "item.gtnl.railgun.disabled"
                                : "item.gtnl.railgun.unavailable")));
            })
            .onMousePressed(mouse -> {
                if (mouse != 0 || !allowed.getAsBoolean()) return false;
                value.setBoolValue(!value.getBoolValue());
                return true;
            });
        panel.child(button);
        return button;
    }

    private ButtonWidget<?> toggleButton(int x, int y, int width, java.util.function.Supplier<String> label,
        Runnable action, int visiblePage) {
        return new ButtonWidget<>().pos(x, y)
            .size(width, 14)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(IKey.dynamic(label))
            .setEnabledIf(widget -> page == visiblePage)
            .onMousePressed(mouse -> {
                if (mouse != 0) return false;
                action.run();
                return true;
            });
    }

    private void stepper(ModularPanel panel, int y, java.util.function.Supplier<String> label, IntSupplier getter,
        IntConsumer setter, int min, IntSupplier max, int step, int visiblePage) {
        panel.child(
            IKey.dynamic(label)
                .asWidget()
                .pos(10, y + 3)
                .size(122, 10)
                .setEnabledIf(widget -> page == visiblePage));
        panel.child(
            toggleButton(
                136,
                y,
                16,
                () -> "-",
                () -> setter.accept(Math.max(min, getter.getAsInt() - step)),
                visiblePage));
        panel.child(
            toggleButton(
                152,
                y,
                16,
                () -> "+",
                () -> setter.accept(Math.min(max.getAsInt(), getter.getAsInt() + step)),
                visiblePage));
    }

    private IntSyncValue cosmeticValue(String key, IntSupplier getter, float scale) {
        IntSyncValue value = new IntSyncValue(
            getter,
            v -> {
                if (CelestweaveSettings.apply(armor(), null, CelestweaveSettings.COSMETIC, key, v / scale)) changed();
            }).allowC2S();
        sync.syncValue("cosmetic_" + key, value);
        return value;
    }

    private void buildAppearancePage(ModularPanel panel) {
        if (armorType != 1) {
            panel.child(
                IKey.lang("item.gtnl.celestweave.gui.appearance_on_chest")
                    .asWidget()
                    .pos(8, CONTENT_Y + 2)
                    .size(160, 20)
                    .setEnabledIf(widget -> page == 2));
            return;
        }
        IntSyncValue color = cosmeticValue(
            CelestweaveSettings.GLOW_COLOR,
            () -> CelestweaveData.cosmetic(armor())
                .glowColor(),
            1F);
        panel.child(
            IKey.lang("item.gtnl.celestweave.gui.glow_color")
                .asWidget()
                .pos(10, CONTENT_Y + 3)
                .size(40, 10)
                .setEnabledIf(widget -> page == 2));
        IPanelHandler picker = !client ? null
            : IPanelHandler.simple(
                panel,
                (parent,
                    player) -> new ColorPickerDialog(
                        "celestweave_color_picker",
                        picked -> color.setIntValue(picked & 0xFFFFFF),
                        0xFF000000 | color.getIntValue(),
                        false).setDraggable(true)
                            .relative(panel)
                            .top(0)
                            .leftRel(1f),
                true);
        panel.child(
            new ButtonWidget<>().pos(148, CONTENT_Y)
                .size(20, 14)
                .background(
                    (context, x, y, width, height, theme) -> GuiDraw
                        .drawRect(x, y, width, height, 0xFF000000 | color.getIntValue()))
                .disableHoverBackground()
                .overlay(IKey.str("§0..."))
                .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("item.gtnl.celestweave.gui.custom_color")))
                .setEnabledIf(widget -> page == 2)
                .onMousePressed(mouse -> {
                    if (mouse != 0) return false;
                    if (picker == null) return false;
                    picker.deleteCachedPanel();
                    picker.openPanel();
                    return true;
                }));
        for (int i = 0; i < COLORS.length; i++) {
            final int rgb = COLORS[i];
            panel.child(
                new ButtonWidget<>().pos(50 + i * 14, CONTENT_Y + 1)
                    .size(12, 12)
                    .background(new Rectangle().color(0xFF000000 | rgb))
                    .overlay(IKey.dynamic(() -> color.getIntValue() == rgb ? "§0✔" : ""))
                    .setEnabledIf(widget -> page == 2)
                    .onMousePressed(mouse -> {
                        if (mouse != 0) return false;
                        color.setIntValue(rgb);
                        return true;
                    }));
        }
        int maxArcs = MainConfig.item.celestweave.maxIdleArcs;
        IntSyncValue idle = cosmeticValue(
            CelestweaveSettings.IDLE_ARCS,
            () -> CelestweaveData.cosmetic(armor())
                .idleArcs(),
            1F);
        stepper(
            panel,
            CONTENT_Y + 16,
            () -> tr("item.gtnl.celestweave.gui.idle_arcs") + ": "
                + (idle.getIntValue() < 0 ? tr("item.gtnl.celestweave.gui.by_tier") : idle.getIntValue()),
            idle::getIntValue,
            idle::setIntValue,
            -1,
            () -> maxArcs,
            1,
            2);
        floatStepper(panel, CONTENT_Y + 32, CelestweaveSettings.ARC_FREQ, "arc_freq", 25, 300, 25);
        floatStepper(panel, CONTENT_Y + 48, CelestweaveSettings.ARC_SPREAD, "arc_spread", 30, 250, 10);
        floatStepper(panel, CONTENT_Y + 64, CelestweaveSettings.ARC_BRIGHT, "arc_bright", 30, 160, 10);
        for (int i = 0; i < EVENTS.length; i++) {
            final int flag = EVENTS[i];
            final String key = "item.gtnl.celestweave.gui.event." + EVENT_KEYS[i];
            addToggle(
                panel,
                "cosmetic_event_" + EVENT_KEYS[i],
                key,
                8 + i * 23,
                CONTENT_Y + 82,
                22,
                () -> CelestweaveData.cosmetic(armor())
                    .event(flag),
                enabled -> {
                    int events = CelestweaveData.cosmetic(armor())
                        .arcEvents();
                    if (CelestweaveSettings.apply(
                        armor(),
                        null,
                        CelestweaveSettings.COSMETIC,
                        CelestweaveSettings.ARC_EVENTS,
                        enabled ? events | flag : events & ~flag)) changed();
                },
                () -> true,
                key + ".tooltip").setEnabledIf(widget -> page == 2);
        }
    }

    private void floatStepper(ModularPanel panel, int y, String key, String lang, int min, int max, int step) {
        IntSyncValue value = cosmeticValue(key, () -> {
            CelestweaveCosmetic cosmetic = CelestweaveData.cosmetic(armor());
            float current = switch (key) {
                case CelestweaveSettings.ARC_FREQ -> cosmetic.arcFreq();
                case CelestweaveSettings.ARC_SPREAD -> cosmetic.arcSpread();
                default -> cosmetic.arcBright();
            };
            return Math.round(current * 100F);
        }, 100F);
        stepper(
            panel,
            y,
            () -> tr("item.gtnl.celestweave.gui." + lang) + ": " + String.format("%.2f", value.getIntValue() / 100F),
            value::getIntValue,
            value::setIntValue,
            min,
            () -> max,
            step,
            2);
    }

    private final class ModuleHandler extends ItemStackHandler {

        private NBTTagList synced, seen;
        private int version;
        private final ItemStack[] viewSource = new ItemStack[CelestweaveData.MODULE_SLOTS],
            viewBare = new ItemStack[CelestweaveData.MODULE_SLOTS],
            hidden = new ItemStack[CelestweaveData.MODULE_SLOTS];

        ModuleHandler() {
            super(CelestweaveData.MODULE_SLOTS);
        }

        private NBTTagList modulesTag(ItemStack armor) {
            return CelestweaveData.root(armor)
                .getTagList(CelestweaveData.MODULES, 10);
        }

        private void reload() {
            ItemStack armor = armor();
            NBTTagList current = modulesTag(armor);
            if (current == seen) return;
            seen = current;
            if (current.equals(synced)) return;
            synced = (NBTTagList) current.copy();
            ItemStack[] loaded = CelestweaveData.moduleStacks(armor);
            for (int i = 0; i < loaded.length; i++)
                stacks.set(i, client ? loaded[i] : CelestweaveBackpack.restore(loaded[i]));
            version++;
        }

        void view(boolean bare) {
            if (bare) reload();
            for (int i = 0; i < getSlots(); i++) {
                if (!bare) {
                    stacks.set(i, hidden[i]);
                    hidden[i] = null;
                    continue;
                }
                ItemStack stack = stacks.get(i);
                hidden[i] = stack;
                if (stack != viewSource[i]) {
                    viewSource[i] = stack;
                    viewBare[i] = CelestweaveBackpack.strip(stack);
                }
                stacks.set(i, viewBare[i]);
            }
        }

        int find(CelestweaveModule module) {
            reload();
            for (int i = 0; i < getSlots(); i++) if (CelestweaveModule.of(stacks.get(i)) == module) return i;
            return -1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            reload();
            return super.getStackInSlot(slot);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack item) {
            reload();
            super.setStackInSlot(slot, item);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            reload();
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack item) {
            CelestweaveModule type = CelestweaveModule.of(item);
            if (type == null || !type.fits(armorType)) return false;
            for (int i = 0; i < getSlots(); i++) {
                if (i == slot) continue;
                CelestweaveModule other = CelestweaveModule.of(getStackInSlot(i));
                if (other == null) continue;
                if (other == type || type.group != CelestweaveModule.Group.NONE && other.group == type.group)
                    return false;
            }
            return true;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack item, boolean simulate) {
            if (item != null && !isItemValid(slot, item)) return item;
            reload();
            return super.insertItem(slot, item, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            ItemStack armor = armor();
            Set<String> before = new HashSet<>();
            if (!client) for (ItemStack module : CelestweaveData.moduleStacks(armor))
                before.add(CelestweaveBackpack.heldId(module));
            ItemStack[] out = stacks.toArray(new ItemStack[0]);
            for (int i = 0; i < out.length; i++)
                out[i] = client ? CelestweaveBackpack.strip(out[i]) : CelestweaveBackpack.store(out[i]);
            CelestweaveData.writeModuleStacks(armor, out);
            if (!client) {
                for (ItemStack module : out) before.remove(CelestweaveBackpack.heldId(module));
                for (String id : before) CelestweaveBackpack.discard(id);
            }
            seen = modulesTag(armor);
            synced = (NBTTagList) seen.copy();
            version++;
            changed();
        }
    }

    private final class HolderHandler extends ItemStackHandler {

        private final CelestweaveModule[] holders;
        private int pulled = -1;
        private final ItemStack[] viewSource, viewBare, hidden;

        HolderHandler(CelestweaveModule[] holders) {
            super(holders.length);
            this.holders = holders;
            viewSource = new ItemStack[holders.length];
            viewBare = new ItemStack[holders.length];
            hidden = new ItemStack[holders.length];
        }

        private void pull() {
            modules.reload();
            if (pulled == modules.version) return;
            pulled = modules.version;
            for (int i = 0; i < holders.length; i++) {
                int index = modules.find(holders[i]);
                stacks.set(i, index < 0 ? null : CelestweaveData.heldOf(modules.getStackInSlot(index)));
            }
        }

        void view(boolean bare) {
            if (bare) pull();
            for (int i = 0; i < holders.length; i++) {
                if (!bare) {
                    stacks.set(i, hidden[i]);
                    hidden[i] = null;
                    continue;
                }
                ItemStack stack = stacks.get(i);
                hidden[i] = stack;
                if (stack != viewSource[i]) {
                    viewSource[i] = stack;
                    viewBare[i] = CelestweaveBackpack.bare(stack);
                }
                stacks.set(i, viewBare[i]);
            }
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            pull();
            return super.getStackInSlot(slot);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack item) {
            pull();
            super.setStackInSlot(slot, item);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            pull();
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack item, boolean simulate) {
            if (item != null && !isItemValid(slot, item)) return item;
            pull();
            return super.insertItem(slot, item, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack item) {
            return slot >= 0 && slot < holders.length
                && holders[slot].accepts(item)
                && modules.find(holders[slot]) >= 0;
        }

        @Override
        protected void onContentsChanged(int slot) {
            int index = modules.find(holders[slot]);
            if (index < 0) return;
            ItemStack module = modules.getStackInSlot(index)
                .copy();
            CelestweaveData.withHeld(module, stacks.get(slot));
            modules.setStackInSlot(index, module);
            pulled = modules.version;
        }
    }
}
