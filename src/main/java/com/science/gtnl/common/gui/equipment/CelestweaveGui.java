package com.science.gtnl.common.gui.equipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
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
import com.cleanroommc.modularui.widget.sizer.Unit;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ColorPickerDialog;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
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
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnllib.gui.layout.Layout;
import com.science.gtnllib.gui.layout.Size;
import com.science.gtnllib.gui.theme.ButtonPurpose;
import com.science.gtnllib.gui.widget.CloseButton;
import com.science.gtnllib.gui.widget.SlideSwitch;
import com.science.gtnllib.gui.widget.StepSlider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CelestweaveGui extends EquipmentGui {

    private static final int[] COLORS = { 0xFF3D9A, 0x3DE8FF, 0xFFC13D, 0xA35CFF, 0x4DFF88, 0xFF4040, 0xEEF0FF };
    private static final String GUI_LANG = "item.gtnl.celestweave.gui.", UNAVAILABLE = "§7";
    private static final String SHORT_LANG = "item.gtnl.celestweave.short.";
    private static final int TABS_Y = 66, SLOTS_Y = 18, HELD_Y = 38, ENERGY_Y = 42, BACKPACK_WIDTH = 52,
        BACKPACK_HEIGHT = 13, FLAGS_PER_ROW = 7, FLAG_GAP = 1, SLIDER_WIDTH = 80, SWATCH_SIZE = 12;
    private static final float SLIDER_SHARE = 0.5F;
    private static final String[] PAGES = { "modules", "settings", "appearance" };
    private static final int APPEARANCE_PAGE = 2;
    private static final int INSTALLED = 1, ACTIVE = 2, SHOWN = 4;

    private final int piece;
    private final Map<CelestweaveModule, IntSyncValue> states = new EnumMap<>(CelestweaveModule.class);
    private final List<List<IWidget>> pages = new ArrayList<>();
    private int page;
    private ModuleHandler modules;
    private HolderHandler heldSlots;
    private int viewing;
    private int contentY;

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
        ModularPanel panel = panel("celestweave_armor", () -> contentY + pageHeight());
        modules = new ModuleHandler();
        boolean backpackButton = piece == CelestweaveArmor.CHEST
            && CelestweaveData.isArmorSlot(data.getPlayer(), data.getSlotIndex());
        int backpackWidth = buttonWidth(
            tr("item.gtnl.celestweave.gui.open_backpack"),
            style.spacingSmall,
            BACKPACK_WIDTH), closeInset = CloseButton.inset(style.padding, style);
        title(panel, stack.getUnlocalizedName() + ".name", backpackButton ? backpackWidth + style.spacingSmall : 0);
        if (backpackButton) panel.child(
            button(ButtonPurpose.NORMAL).top(closeInset + (CloseButton.SIZE - BACKPACK_HEIGHT) / 2)
                .right(closeInset + CloseButton.SIZE + style.spacingSmall)
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
        int shift = slotRowsShift(modules.getSlots());
        contentY = TABS_Y + shift + style.buttonHeight + style.spacingMedium;
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
                            .top(HELD_Y + shift)
                            .right(style.padding + SLOT_SIZE * (holders.size() - 1 - i))
                            .tooltipBuilder(
                                tooltip -> tooltip.addLine(IKey.lang("item.gtnl.celestweave.gui.held." + module.id))));
            }
        }
        energyBar(
            panel,
            this::energy,
            () -> CelestweaveEnergy.capacity(bound()),
            ENERGY_Y + shift,
            SLOT_SIZE * holders.size());
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
                .pos(0, TABS_Y + shift)
                .height(style.buttonHeight));
        pages.add(modulePage());
        pages.add(settingsPage());
        pages.add(appearancePage(panel));
        for (int i = 0; i < pages.size(); i++) attachPage(panel, i);
        bindFooter(panel);
        return panel;
    }

    private int pageHeight() {
        Layout rows = rows();
        int needed = 0;
        for (List<IWidget> lines : pages) needed = Math.max(needed, rows.stackHeight(lines));
        return fitHeight(contentY, needed);
    }

    private void attachPage(ModularPanel panel, int index) {
        List<IWidget> lines = pages.get(index);
        if (lines.isEmpty()) {
            pageNote(panel, index);
            return;
        }
        ListWidget<IWidget, ?> list = new ListWidget<>().pos(0, contentY)
            .width(this::panelWidth, Unit.Measure.PIXEL)
            .height(this::pageHeight, Unit.Measure.PIXEL)
            .setEnabledIf(widget -> page == index);
        lines.forEach(list::child);
        panel.child(list);
    }

    private void pageNote(ModularPanel panel, int index) {
        String lang = index == APPEARANCE_PAGE ? "item.gtnl.celestweave.gui.appearance_on_chest"
            : "item.gtnl.celestweave.gui.no_settings";
        panel.child(
            IKey.lang(lang)
                .asWidget()
                .pos(style.padding, contentY + style.spacingSmall)
                .width(this::contentWidth, Unit.Measure.PIXEL)
                .height(TEXT_HEIGHT * 2)
                .setEnabledIf(widget -> page == index));
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

    private List<IWidget> modulePage() {
        List<SlideSwitch> switches = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (CelestweaveModule module : CelestweaveModule.values()) {
            if (!module.fits(piece)) continue;
            IntSyncValue state = new IntSyncValue(() -> moduleState(module), value -> setModuleState(module, value))
                .allowC2S();
            sync.syncValue("module_" + module.id, state);
            states.put(module, state);
            labels.add(tr(SHORT_LANG + module.id));
            switches.add(
                new SlideSwitch().value(() -> (state.getIntValue() & ACTIVE) != 0)
                    .availableWhen(() -> (state.getIntValue() & INSTALLED) != 0)
                    .liveWhen(() -> (state.getIntValue() & SHOWN) != 0)
                    .label(IKey.dynamic(() -> tr(SHORT_LANG + module.id)))
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
        return Collections
            .singletonList(rows().adaptiveGrid(style.spacingSmall, widestSwitch(labels), MAX_COLUMNS, switches));
    }

    private static boolean hidden(int state) {
        return (state & INSTALLED) != 0 && (state & SHOWN) == 0;
    }

    private List<IWidget> settingsPage() {
        Layout rows = rows();
        List<IWidget> lines = new ArrayList<>();
        for (CelestweaveModule module : CelestweaveModule.values()) {
            if (!module.fits(piece)) continue;
            List<IWidget> sliders = new ArrayList<>();
            List<SlideSwitch> toggles = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            for (CelestweaveModuleSetting setting : CelestweaveModuleSetting.values()) {
                if (setting.module != module) continue;
                if (setting.bool()) {
                    toggles.add(settingToggle(setting));
                    labels.add(tr(setting.lang()));
                } else sliders.add(settingSlider(setting));
            }
            if (sliders.isEmpty() && toggles.isEmpty()) continue;
            lines.add(
                rows.section(
                    IKey.dynamic(() -> (installed(module) ? "" : UNAVAILABLE) + tr(SHORT_LANG + module.id)),
                    client ? accent() : 0));
            lines.addAll(sliders);
            if (!toggles.isEmpty())
                lines.add(rows.adaptiveGrid(style.spacingSmall, widestSwitch(labels), MAX_COLUMNS, toggles));
        }
        return lines;
    }

    @SideOnly(Side.CLIENT)
    private static int accent() {
        return EquipmentTheme.colors().accent;
    }

    private static String settingKey(CelestweaveModuleSetting setting) {
        return "setting_" + setting.module.id + "_" + setting.key;
    }

    private SlideSwitch settingToggle(CelestweaveModuleSetting setting) {
        return toggle(
            settingKey(setting),
            setting.lang(),
            () -> setting.on(bound()),
            enabled -> { if (CelestweaveData.setValue(bound(), setting, enabled ? 1F : 0F)) changed(); },
            () -> installed(setting.module),
            null);
    }

    private IWidget settingSlider(CelestweaveModuleSetting setting) {
        CelestweaveModule module = setting.module;
        IntSyncValue value = new IntSyncValue(
            () -> setting.get(bound()),
            v -> { if (CelestweaveData.setValue(bound(), setting, v)) changed(); }).allowC2S();
        sync.syncValue(settingKey(setting), value);
        return slider(
            IKey.dynamic(() -> (installed(module) ? "" : UNAVAILABLE) + tr(setting.lang())),
            value,
            setting.min,
            setting.max(),
            1,
            setting::display,
            () -> installed(module));
    }

    private static Size sliderSize() {
        return Size.percent(SLIDER_SHARE)
            .atLeast(SLIDER_WIDTH);
    }

    private Widget<?> sliderControl(IntSyncValue value, int min, int max, int step, IntFunction<String> format,
        BooleanSupplier active) {
        return client && max > min ? stepSlider(value, min, max, step, format, active)
            : Layout.label(IKey.dynamic(() -> format.apply(value.getIntValue())), style);
    }

    private IWidget slider(IKey label, IntSyncValue value, int min, int max, int step, IntFunction<String> format,
        BooleanSupplier active) {
        return rows().line(
            style.spacingMedium,
            Arrays.asList(Size.fill(), sliderSize()),
            Arrays.asList(
                Layout.label(label, style)
                    .paddingLeft(style.spacingSmall),
                sliderControl(value, min, max, step, format, active)));
    }

    @SideOnly(Side.CLIENT)
    private static StepSlider stepSlider(IntSyncValue value, int min, int max, int step, IntFunction<String> format,
        BooleanSupplier active) {
        return new StepSlider()
            .value(value::getIntValue, v -> { if (active.getAsBoolean()) value.setIntValue((int) Math.round(v)); })
            .range(min, max, Math.max(1, (max - min) / step))
            .format(v -> format.apply((int) Math.round(v)))
            .activeWhen(active);
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

    private List<IWidget> appearancePage(ModularPanel panel) {
        List<IWidget> lines = new ArrayList<>();
        if (piece != CelestweaveArmor.CHEST) return lines;
        for (CelestweaveCosmeticOption option : CelestweaveCosmeticOption.values()) {
            switch (option.kind) {
                case COLOR -> lines.add(colorRow(panel, option));
                case FLAGS -> lines.addAll(flagRows(option));
                case CHOICE -> lines.addAll(choiceRows(option));
                default -> lines.addAll(cosmeticRows(option));
            }
        }
        return lines;
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
            new ButtonWidget<>().height(SWATCH_SIZE)
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
        sizes.add(Size.fixed(SWATCH_SIZE));
        return rows().line(style.spacingSmall, sizes, cells);
    }

    @SideOnly(Side.CLIENT)
    private static void followGlow(ModularPanel panel, IntSyncValue color) {
        CelestweaveThemeAccent.editing(panel, color::getIntValue);
    }

    private List<IWidget> cosmeticRows(CelestweaveCosmeticOption option) {
        float scale = guiScale(option);
        IntSyncValue value = cosmeticValue(option);
        BooleanSupplier applies = () -> option.appliesTo(CelestweaveData.cosmetic(bound()));
        int min = Math.round(option.min * scale), step = Math.max(1, Math.round(option.step * scale));
        IKey label = IKey.dynamic(() -> (applies.getAsBoolean() ? "" : UNAVAILABLE) + tr(GUI_LANG + option.lang));
        if (option.minLabel == null) return Collections.singletonList(
            slider(label, value, min, Math.round(option.max() * scale), step, v -> cosmeticText(option, v), applies));
        BooleanSupplier automatic = () -> value.getIntValue() <= min;
        SlideSwitch toggle = new SlideSwitch().value(automatic)
            .label(IKey.lang(GUI_LANG + option.minLabel))
            .onMousePressed(mouse -> {
                if (mouse != 0) return false;
                value.setIntValue(automatic.getAsBoolean() ? min + step : min);
                return true;
            });
        return Collections.singletonList(
            rows().line(
                style.spacingMedium,
                Arrays.asList(
                    Size.fill(),
                    Size.content(SlideSwitch.widthFor(textWidth(tr(GUI_LANG + option.minLabel)))),
                    sliderSize()),
                Arrays.asList(
                    Layout.label(label, style)
                        .paddingLeft(style.spacingSmall),
                    toggle,
                    sliderControl(
                        value,
                        min + step,
                        Math.round(option.max() * scale),
                        step,
                        v -> cosmeticText(option, v),
                        () -> applies.getAsBoolean() && !automatic.getAsBoolean()))));
    }

    private List<IWidget> choiceRows(CelestweaveCosmeticOption option) {
        IntSyncValue value = cosmeticValue(option);
        List<String> names = option.names();
        List<ToggleButton> picks = new ArrayList<>();
        int widest = 0;
        for (int i = 0; i < names.size(); i++) {
            final int index = i;
            String key = GUI_LANG + option.lang + "." + names.get(i);
            widest = Math.max(
                widest,
                Size.content(textWidth(tr(key)), style.padding)
                    .resolve(0));
            picks.add(
                ButtonPurpose.TAB.apply(new ToggleButton())
                    .value(
                        new BoolValue.Dynamic(
                            () -> value.getIntValue() == index,
                            selected -> { if (selected) value.setIntValue(index); }))
                    .overlay(Layout.caption(IKey.lang(key), style)));
        }
        Layout rows = rows();
        return Arrays.asList(
            rows.line(
                0,
                Collections.singletonList(Size.fill()),
                Collections.singletonList(
                    Layout.label(IKey.lang(GUI_LANG + option.lang), style)
                        .paddingLeft(style.spacingSmall))),
            rows.adaptiveGrid(style.spacingSmall, widest, names.size(), picks));
    }

    private static String cosmeticText(CelestweaveCosmeticOption option, int value) {
        return switch (option.kind) {
            case DECIMAL -> String.format("%.2f", value / guiScale(option));
            default -> String.valueOf(value);
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

        private final BareView view = new BareView(CelestweaveData.moduleSlots(piece), CelestweaveBackpack::strip);

        ModuleHandler() {
            super(CelestweaveData.moduleSlots(piece), CelestweaveData.MODULE_STORAGE, CelestweaveModule::of);
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
