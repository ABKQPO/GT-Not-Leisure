package com.science.gtnl.common.gui.equipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ColorPickerDialog;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.celestweave.CelestweaveThemeAccent;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.celestweave.CelestweaveBackpackData;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveModuleSetting;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.gui.modularui.layout.Layout;
import com.science.gtnl.common.gui.modularui.layout.Size;
import com.science.gtnl.common.gui.modularui.theme.ButtonPurpose;
import com.science.gtnl.common.gui.modularui.widget.SlideSwitch;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CelestweaveGui extends EquipmentGui {

    private static final int[] COLORS = { 0xFF3D9A, 0x3DE8FF, 0xFFC13D, 0xA35CFF, 0x4DFF88, 0xFF4040, 0xEEF0FF };
    private static final String GUI_LANG = "item.gtnl.celestweave.gui.", UNAVAILABLE = "§7";
    private static final int TABS_Y = 66, SLOTS_Y = 18, HELD_Y = 38, ENERGY_Y = 42, BACKPACK_Y = 3, BACKPACK_WIDTH = 52,
        BACKPACK_HEIGHT = 13, PAGE_ROWS = 7, FLAGS_PER_ROW = 7, FLAG_GAP = 1, STEP_WIDTH = 16, SWATCH_SIZE = 12,
        PICKER_WIDTH = 20;
    private static final int MODULES_PAGE = 0, SETTINGS_PAGE = 1, APPEARANCE_PAGE = 2;
    private static final String[] PAGES = { "modules", "settings", "appearance" };
    private static final int INSTALLED = 1, ACTIVE = 2, SHOWN = 4;

    private final int piece;
    private final Map<CelestweaveModule, IntSyncValue> states = new EnumMap<>(CelestweaveModule.class);
    private int page;
    private ModuleHandler modules;
    private HolderHandler heldSlots;
    private int viewing;
    private int contentY, pageHeight;

    public CelestweaveGui(PlayerInventoryGuiData data, PanelSyncManager sync) {
        super(data, sync);
        this.piece = ((CelestweaveArmor) stack.getItem()).armorType;
    }

    @Override
    public boolean holds(ItemStack stack) {
        return stack != null && stack.getItem() instanceof CelestweaveArmor;
    }

    @Override
    public ModularPanel build() {
        contentY = TABS_Y + style.buttonHeight + style.spacingMedium;
        pageHeight = PAGE_ROWS * style.rowHeight;
        ModularPanel panel = panel("celestweave_armor", contentY + pageHeight);
        modules = new ModuleHandler();
        boolean backpackButton = piece == CelestweaveArmor.CHEST
            && CelestweaveData.isArmorSlot(data.getPlayer(), data.getSlotIndex());
        int backpackWidth = buttonWidth(
            tr("item.gtnl.celestweave.gui.open_backpack"),
            style.spacingSmall,
            BACKPACK_WIDTH), backpackX = closeLeft() - style.spacingSmall - backpackWidth;
        title(
            panel,
            stack.getUnlocalizedName() + ".name",
            backpackButton ? backpackX - style.padding - style.spacingSmall : contentWidth());
        if (backpackButton) panel.child(
            button(ButtonPurpose.NORMAL).pos(backpackX, BACKPACK_Y)
                .size(backpackWidth, BACKPACK_HEIGHT)
                .overlay(
                    Layout.caption(
                        IKey.dynamic(
                            () -> (installed(CelestweaveModule.BACKPACK) ? "" : UNAVAILABLE)
                                + tr("item.gtnl.celestweave.gui.open_backpack")),
                        style))
                .onMousePressed(mouse -> {
                    if (mouse != 0 || !installed(CelestweaveModule.BACKPACK)) return false;
                    ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.BACKPACK));
                    return true;
                }));
        moduleSlots(panel, modules, "celestweave_modules", SLOTS_Y, "item.gtnl.celestweave.gui.module_limits");
        List<CelestweaveModule> holders = new ArrayList<>();
        for (CelestweaveModule module : CelestweaveModule.values())
            if (module.holdsItem && module.fits(piece)) holders.add(module);
        if (!holders.isEmpty()) {
            HolderHandler held = new HolderHandler(holders.toArray(new CelestweaveModule[0]));
            heldSlots = held;
            sync.registerSlotGroup(new SlotGroup("celestweave_held", holders.size(), 1, true));
            for (int i = 0; i < holders.size(); i++) {
                final int slot = i;
                final CelestweaveModule module = holders.get(i);
                panel
                    .child(
                        new ItemSlot()
                            .slot(
                                new ModularSlot(held, i).filter(candidate -> held.isItemValid(slot, candidate))
                                    .slotGroup("celestweave_held"))
                            .pos(WIDTH - style.padding - SLOT_SIZE * (holders.size() - i), HELD_Y)
                            .tooltipBuilder(
                                tooltip -> tooltip.addLine(IKey.lang("item.gtnl.celestweave.gui.held." + module.id))));
            }
        }
        energyBar(
            panel,
            this::energy,
            () -> CelestweaveEnergy.capacity(bound()),
            ENERGY_Y,
            contentWidth() - SLOT_SIZE * holders.size());
        List<ToggleButton> tabs = new ArrayList<>();
        for (int i = 0; i < PAGES.length; i++) {
            final int index = i;
            tabs.add(
                ButtonPurpose.TAB.apply(new ToggleButton())
                    .value(new BoolValue.Dynamic(() -> page == index, selected -> { if (selected) page = index; }))
                    .overlay(Layout.caption(IKey.lang("item.gtnl.celestweave.gui.page." + PAGES[index]), style)));
        }
        panel.child(
            rows().line(style.spacingSmall, Collections.nCopies(tabs.size(), Size.fill()), tabs)
                .pos(0, TABS_Y)
                .height(style.buttonHeight));
        buildModulePage(panel);
        buildSettingsPage(panel);
        buildAppearancePage(panel);
        bindFooter(panel);
        return panel;
    }

    private double energy() {
        ItemStack armor = bound();
        return CelestweaveData.isArmorSlot(data.getPlayer(), data.getSlotIndex())
            ? CelestweaveEnergy.available(data.getPlayer(), piece, armor)
            : EquipmentEnergy.read(armor);
    }

    @Override
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
        ItemStack armor = bound();
        if (!CelestweaveData.installed(armor)
            .contains(module)) return 0;
        return INSTALLED | (CelestweaveData.active(armor, module) ? ACTIVE : 0)
            | (CelestweaveData.shown(armor, module) ? SHOWN : 0);
    }

    private void setModuleState(CelestweaveModule module, int state) {
        ItemStack armor = bound();
        boolean changed = CelestweaveData.setEnabled(armor, module, (state & ACTIVE) != 0);
        changed |= CelestweaveData.setShown(armor, module, (state & SHOWN) != 0);
        if (changed) changed();
    }

    private boolean installed(CelestweaveModule module) {
        IntSyncValue state = states.get(module);
        return state != null && (state.getIntValue() & INSTALLED) != 0;
    }

    private void buildModulePage(ModularPanel panel) {
        List<SlideSwitch> switches = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (CelestweaveModule module : CelestweaveModule.values()) {
            if (!module.fits(piece)) continue;
            IntSyncValue state = new IntSyncValue(() -> moduleState(module), value -> setModuleState(module, value))
                .allowC2S();
            sync.syncValue("module_" + module.id, state);
            states.put(module, state);
            labels.add(tr("item.gtnl.celestweave.short." + module.id));
            switches.add(
                new SlideSwitch().value(() -> (state.getIntValue() & ACTIVE) != 0)
                    .availableWhen(() -> (state.getIntValue() & INSTALLED) != 0)
                    .liveWhen(() -> (state.getIntValue() & SHOWN) != 0)
                    .label(IKey.dynamic(() -> tr("item.gtnl.celestweave.short." + module.id)))
                    .tooltipAutoUpdate(true)
                    .tooltipBuilder(tooltip -> {
                        tooltip.addLine(IKey.lang("item.gtnl.celestweave_module." + module.id + ".name"));
                        tooltip.addLine(IKey.lang("item.gtnl.celestweave_module." + module.id + ".tooltip"));
                        tooltip.addLine(
                            IKey.lang(
                                module.hideable() ? "item.gtnl.celestweave.gui.toggle_hint"
                                    : "item.gtnl.celestweave.gui.toggle_hint_enable"));
                        if (hidden(state.getIntValue())) tooltip.addLine(IKey.lang("item.gtnl.celestweave.gui.hidden"));
                    })
                    .onMousePressed(mouse -> {
                        int value = state.getIntValue();
                        if ((value & INSTALLED) == 0 || mouse != 0 && mouse != 1) return false;
                        if (mouse == 1 && !module.hideable()) return false;
                        state.setIntValue(value ^ (mouse == 0 ? ACTIVE : SHOWN));
                        return true;
                    }));
        }
        Layout rows = rows();
        ListWidget<IWidget, ?> list = pageList(MODULES_PAGE);
        rows.grid(style.spacingSmall, rows.columns(style.spacingSmall, widestSwitch(labels), MAX_COLUMNS), switches)
            .forEach(list::child);
        panel.child(list);
    }

    private static boolean hidden(int state) {
        return (state & INSTALLED) != 0 && (state & SHOWN) == 0;
    }

    private ListWidget<IWidget, ?> pageList(int visiblePage) {
        return new ListWidget<>().pos(0, contentY)
            .size(WIDTH, pageHeight)
            .setEnabledIf(widget -> page == visiblePage);
    }

    private void pageNote(ModularPanel panel, String lang, int height, int visiblePage) {
        panel.child(
            IKey.lang(lang)
                .asWidget()
                .pos(style.padding, contentY + style.spacingSmall)
                .size(contentWidth(), height)
                .setEnabledIf(widget -> page == visiblePage));
    }

    private void buildSettingsPage(ModularPanel panel) {
        ListWidget<IWidget, ?> list = pageList(SETTINGS_PAGE);
        int rows = 0;
        for (CelestweaveModuleSetting setting : CelestweaveModuleSetting.values()) if (setting.module.fits(piece)) {
            list.child(settingRow(setting));
            rows++;
        }
        if (rows == 0) pageNote(panel, "item.gtnl.celestweave.gui.no_settings", TEXT_HEIGHT, SETTINGS_PAGE);
        else panel.child(list);
    }

    private IWidget settingRow(CelestweaveModuleSetting setting) {
        CelestweaveModule module = setting.module;
        String syncKey = "setting_" + module.id + "_" + setting.key;
        if (setting.bool()) return rows().line(
            0,
            Collections.singletonList(Size.fill()),
            Collections.singletonList(
                toggle(
                    syncKey,
                    setting.lang(),
                    () -> setting.on(bound()),
                    enabled -> { if (CelestweaveData.setValue(bound(), setting, enabled ? 1F : 0F)) changed(); },
                    () -> installed(module),
                    null)));
        IntSyncValue value = new IntSyncValue(
            () -> setting.get(bound()),
            v -> { if (CelestweaveData.setValue(bound(), setting, v)) changed(); }).allowC2S();
        sync.syncValue(syncKey, value);
        return stepper(
            () -> (installed(module) ? "" : UNAVAILABLE) + tr(setting.lang())
                + ": "
                + setting.display(value.getIntValue()),
            value::getIntValue,
            v -> { if (installed(module)) value.setIntValue(v); },
            setting.min,
            setting::max,
            1);
    }

    private ButtonWidget<?> stepButton(String label, Runnable action) {
        return button(ButtonPurpose.NORMAL).size(STEP_WIDTH, style.buttonHeight)
            .overlay(Layout.caption(IKey.str(label), style))
            .onMousePressed(mouse -> {
                if (mouse != 0) return false;
                action.run();
                return true;
            });
    }

    private IWidget stepper(Supplier<String> label, IntSupplier getter, IntConsumer setter, int min, IntSupplier max,
        int step) {
        Flow buttons = Flow.row()
            .size(STEP_WIDTH * 2, style.buttonHeight)
            .child(stepButton("-", () -> setter.accept(Math.max(min, getter.getAsInt() - step))))
            .child(stepButton("+", () -> setter.accept(Math.min(max.getAsInt(), getter.getAsInt() + step))));
        return rows().line(
            style.spacingMedium,
            Arrays.asList(Size.fill(), Size.fixed(STEP_WIDTH * 2)),
            Arrays.asList(
                Layout.label(IKey.dynamic(label), style)
                    .paddingLeft(style.spacingSmall),
                buttons));
    }

    private static float guiScale(CelestweaveCosmeticOption option) {
        return option.kind == CelestweaveCosmeticOption.Kind.DECIMAL ? 100F : 1F;
    }

    private IntSyncValue cosmeticValue(CelestweaveCosmeticOption option) {
        float scale = guiScale(option);
        IntSyncValue value = new IntSyncValue(
            () -> Math.round(
                CelestweaveData.cosmetic(bound())
                    .get(option) * scale),
            v -> { if (CelestweaveData.setCosmetic(bound(), option, v / scale)) changed(); }).allowC2S();
        sync.syncValue("cosmetic_" + option.key, value);
        return value;
    }

    private void buildAppearancePage(ModularPanel panel) {
        if (piece != CelestweaveArmor.CHEST) {
            pageNote(panel, "item.gtnl.celestweave.gui.appearance_on_chest", TEXT_HEIGHT * 2, APPEARANCE_PAGE);
            return;
        }
        ListWidget<IWidget, ?> list = pageList(APPEARANCE_PAGE);
        for (CelestweaveCosmeticOption option : CelestweaveCosmeticOption.values()) {
            switch (option.kind) {
                case COLOR -> list.child(colorRow(panel, option));
                case FLAGS -> flagRows(option).forEach(list::child);
                default -> list.child(stepperRow(option));
            }
        }
        panel.child(list);
    }

    private IWidget colorRow(ModularPanel panel, CelestweaveCosmeticOption option) {
        IntSyncValue color = cosmeticValue(option);
        if (client && option == CelestweaveCosmeticOption.GLOW_COLOR) followGlow(panel, color);
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
                            .leftRel(1f)
                            .background(IDrawable.EMPTY)
                            .disableThemeBackground(false),
                true);
        List<Widget<?>> cells = new ArrayList<>();
        List<Size> sizes = new ArrayList<>();
        cells.add(
            Layout.label(IKey.lang(GUI_LANG + option.lang), style)
                .paddingLeft(style.spacingSmall));
        sizes.add(Size.fill());
        for (int rgb : COLORS) {
            cells.add(
                new ButtonWidget<>().size(SWATCH_SIZE, SWATCH_SIZE)
                    .background(new Rectangle().color(0xFF000000 | rgb))
                    .disableHoverThemeBackground(true)
                    .overlay(IKey.dynamic(() -> color.getIntValue() == rgb ? "§0✔" : ""))
                    .onMousePressed(mouse -> {
                        if (mouse != 0) return false;
                        color.setIntValue(rgb);
                        return true;
                    }));
            sizes.add(Size.fixed(SWATCH_SIZE));
        }
        cells.add(
            new ButtonWidget<>()
                .background(
                    (context, x, top, width, height, theme) -> GuiDraw
                        .drawRect(x, top, width, height, 0xFF000000 | color.getIntValue()))
                .disableHoverBackground()
                .disableHoverThemeBackground(true)
                .overlay(IKey.str("§0..."))
                .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("item.gtnl.celestweave.gui.custom_color")))
                .onMousePressed(mouse -> {
                    if (mouse != 0) return false;
                    if (picker == null) return false;
                    picker.deleteCachedPanel();
                    picker.openPanel();
                    return true;
                }));
        sizes.add(Size.fixed(PICKER_WIDTH));
        return rows().line(style.spacingSmall, sizes, cells);
    }

    @SideOnly(Side.CLIENT)
    private static void followGlow(ModularPanel panel, IntSyncValue color) {
        CelestweaveThemeAccent.editing(panel, color::getIntValue);
    }

    private IWidget stepperRow(CelestweaveCosmeticOption option) {
        float scale = guiScale(option);
        IntSyncValue value = cosmeticValue(option);
        BooleanSupplier applies = () -> option.appliesTo(CelestweaveData.cosmetic(bound()));
        return stepper(
            () -> (applies.getAsBoolean() ? "" : UNAVAILABLE) + tr(GUI_LANG + option.lang)
                + ": "
                + cosmeticText(option, value.getIntValue()),
            value::getIntValue,
            v -> { if (applies.getAsBoolean()) value.setIntValue(v); },
            Math.round(option.min * scale),
            () -> Math.round(option.max() * scale),
            Math.max(1, Math.round(option.step * scale)));
    }

    private static String cosmeticText(CelestweaveCosmeticOption option, int value) {
        return switch (option.kind) {
            case CHOICE -> {
                List<String> names = option.names();
                yield tr(GUI_LANG + option.lang + "." + names.get(Math.max(0, Math.min(names.size() - 1, value))));
            }
            case DECIMAL -> String.format("%.2f", value / guiScale(option));
            default -> option.minLabel != null && value <= option.min ? tr(GUI_LANG + option.minLabel)
                : String.valueOf(value);
        };
    }

    private List<IWidget> flagRows(CelestweaveCosmeticOption option) {
        List<String> names = option.names();
        List<ToggleButton> flags = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            final int flag = option.flag(i);
            final String key = GUI_LANG + option.lang + "." + names.get(i);
            BooleanSyncValue value = syncToggle(
                "cosmetic_" + option.key + "_" + names.get(i),
                () -> (CelestweaveData.cosmetic(bound())
                    .getInt(option) & flag) != 0,
                enabled -> {
                    int mask = CelestweaveData.cosmetic(bound())
                        .getInt(option);
                    if (CelestweaveData.setCosmetic(bound(), option, enabled ? mask | flag : mask & ~flag)) changed();
                },
                () -> true);
            flags.add(
                new ToggleButton().value(value)
                    .overlay(Layout.caption(IKey.lang(key), style))
                    .tooltipBuilder(
                        tooltip -> stateTooltip(tooltip, key + ".tooltip", () -> true, value::getBoolValue)));
        }
        return rows().grid(FLAG_GAP, Math.min(FLAGS_PER_ROW, names.size()), flags);
    }

    private static class BareView {

        private final UnaryOperator<ItemStack> strip;
        private final ItemStack[] source, stripped, hidden;

        BareView(int size, UnaryOperator<ItemStack> strip) {
            this.strip = strip;
            source = new ItemStack[size];
            stripped = new ItemStack[size];
            hidden = new ItemStack[size];
        }

        void swap(List<ItemStack> stacks, boolean bare) {
            for (int i = 0; i < hidden.length; i++) {
                if (!bare) {
                    stacks.set(i, hidden[i]);
                    hidden[i] = null;
                    continue;
                }
                ItemStack stack = stacks.get(i);
                hidden[i] = stack;
                if (stack != source[i]) {
                    source[i] = stack;
                    stripped[i] = strip.apply(stack);
                }
                stacks.set(i, stripped[i]);
            }
        }
    }

    private class ModuleHandler extends ModuleSlots<CelestweaveModule> {

        private final BareView view = new BareView(CelestweaveData.MODULE_SLOTS, CelestweaveBackpack::strip);

        ModuleHandler() {
            super(CelestweaveData.MODULE_SLOTS, CelestweaveData.MODULE_STORAGE, CelestweaveModule::of);
        }

        @Override
        protected ItemStack[] load(ItemStack host) {
            ItemStack[] loaded = super.load(host);
            if (client) return loaded;
            CelestweaveBackpackData data = CelestweaveBackpackData.get();
            for (int i = 0; i < loaded.length; i++) loaded[i] = CelestweaveBackpack.restore(loaded[i], data);
            return loaded;
        }

        @Override
        protected void save(ItemStack host, ItemStack[] out) {
            if (client) for (int i = 0; i < out.length; i++) out[i] = CelestweaveBackpack.strip(out[i]);
            else out = CelestweaveBackpack
                .persist(CelestweaveData.moduleStacks(host), out, CelestweaveBackpackData.get());
            super.save(host, out);
        }

        @Override
        protected CelestweaveModule type(ItemStack candidate) {
            CelestweaveModule module = super.type(candidate);
            return module != null && module.fits(piece) ? module : null;
        }

        void view(boolean bare) {
            if (bare) reload();
            view.swap(stacks, bare);
        }

        int find(CelestweaveModule module) {
            reload();
            for (int i = 0; i < getSlots(); i++) if (CelestweaveModule.of(stacks.get(i)) == module) return i;
            return -1;
        }
    }

    private class HolderHandler extends ItemStackHandler {

        private final CelestweaveModule[] holders;
        private int pulled = -1;
        private final BareView view;

        HolderHandler(CelestweaveModule[] holders) {
            super(holders.length);
            this.holders = holders;
            view = new BareView(holders.length, CelestweaveBackpack::bare);
        }

        private void pull() {
            modules.reload();
            if (pulled == modules.version) return;
            pulled = modules.version;
            for (int i = 0; i < holders.length; i++) {
                if (holders[i].builtIn) {
                    ItemStack held = CelestweaveData.held(bound(), holders[i]);
                    stacks.set(i, held == null ? null : held.copy());
                    continue;
                }
                int index = modules.find(holders[i]);
                stacks.set(i, index < 0 ? null : CelestweaveData.heldOf(modules.getStackInSlot(index)));
            }
        }

        void view(boolean bare) {
            if (bare) pull();
            view.swap(stacks, bare);
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            pull();
            return super.getStackInSlot(slot);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack candidate) {
            pull();
            super.setStackInSlot(slot, candidate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            pull();
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack candidate, boolean simulate) {
            if (candidate != null && !isItemValid(slot, candidate)) return candidate;
            pull();
            return super.insertItem(slot, candidate, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack candidate) {
            return slot >= 0 && slot < holders.length
                && holders[slot].accepts(candidate)
                && (holders[slot].builtIn || modules.find(holders[slot]) >= 0);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (holders[slot].builtIn) {
                CelestweaveData.writeHeld(bound(), holders[slot], stacks.get(slot));
                changed();
                return;
            }
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
