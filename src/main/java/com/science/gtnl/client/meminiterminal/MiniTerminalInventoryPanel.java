package com.science.gtnl.client.meminiterminal;

import static appeng.client.gui.implementations.GuiMEMonitorable.keyBindPickBlockAction;
import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.layout.IViewport;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.api.widget.IDragResizeable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.HoveredWidgetList;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.meminiterminal.MiniTerminalPinSettings;

import appeng.api.config.ActionItems;
import appeng.api.config.OperationMode;
import appeng.api.config.PinSectionOrder;
import appeng.api.config.Settings;
import appeng.api.config.TerminalStyle;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.ScreenColor;
import appeng.client.gui.slots.VirtualMEMonitorableSlot;
import appeng.client.gui.slots.VirtualMEPinSlot;
import appeng.client.gui.slots.VirtualMESlot;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiScrollbar;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.client.gui.widgets.TypeToggleButton;
import appeng.client.me.ItemRepo;
import appeng.client.render.StackSizeRenderer;
import appeng.core.AEConfig;
import appeng.core.localization.ColorUtils;
import appeng.helpers.MonitorableAction;
import appeng.items.contents.PinList;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.Loader;

public final class MiniTerminalInventoryPanel extends MiniTerminalPanel {

    private static final UITexture MAGNET_CARD = UITexture.fullImage("ae2wct", "items/magnetCard");

    private static final UITexture TERMINAL_ICON = UITexture.builder()
        .location("appliedenergistics2", "guis/states")
        .imageSize(256, 256)
        .xy(8 * 16, 7 * 16, 16, 16)
        .build();

    private static final UITexture RESET_ICON = UITexture.builder()
        .location("appliedenergistics2", "guis/states")
        .imageSize(256, 256)
        .xy(0, 15 * 16, 16, 16)
        .build();

    private static final UITexture MOVE_ICON = UITexture.builder()
        .location("appliedenergistics2", "guis/states")
        .imageSize(256, 256)
        .xy(5 * 16, 6 * 16, 16, 16)
        .build();

    private static final UITexture PANEL_FILL = UITexture.builder()
        .location("appliedenergistics2", "guis/terminal")
        .imageSize(256, 256)
        .xy(4, 4, 1, 1)
        .build();

    private static final UITexture SCROLL_TRACK = UITexture.builder()
        .location("appliedenergistics2", "guis/terminal")
        .imageSize(256, 256)
        .xy(174, 17, 14, 54)
        .adaptable(1)
        .build();

    private static final UITexture THUMB_ACTIVE = UITexture.builder()
        .location("minecraft", "gui/container/creative_inventory/tabs")
        .imageSize(256, 256)
        .xy(232, 0, 12, 15)
        .adaptable(2)
        .build();

    private static final UITexture THUMB_DISABLED = UITexture.builder()
        .location("minecraft", "gui/container/creative_inventory/tabs")
        .imageSize(256, 256)
        .xy(244, 0, 12, 15)
        .adaptable(2)
        .build();

    private final GuiContainer inventory;
    private final MiniTerminalLayout layout = MiniTerminalClient.layoutState();
    private MiniTerminalModel model;
    private final MiniTerminalSearchField search;
    private final TerminalWidget terminal;
    private final Flow header;
    private final Flow controls;
    private final Set<Integer> consumedMouseButtons = new HashSet<>();
    private boolean resizing;

    public MiniTerminalInventoryPanel(GuiContainer inventory, MiniTerminalModel model) {
        super(MiniTerminalClient.layoutState());
        pos(0, 0).size(MiniTerminalLayout.WIDTH, 166);
        tooltipBuilder(tooltip -> {
            tooltip.addLine(tr("move"));
            if (layout.expanded()) tooltip.addLine(tr("resize"));
        });
        this.inventory = inventory;
        this.model = model;
        terminal = new TerminalWidget();
        terminal.full()
            .pos(0, 0)
            .invisible();
        search = new MiniTerminalSearchField(text -> this.model.search(text)).left(MiniTerminalLayout.SEARCH_X)
            .right(MiniTerminalLayout.SEARCH_RIGHT)
            .top(MiniTerminalLayout.HEADER_Y)
            .height(MiniTerminalLayout.HEADER_SIZE);
        search.setText(model.search());
        header = Flow.row()
            .invisible()
            .right(4 + MiniTerminalLayout.BUTTON_WIDTH)
            .top(MiniTerminalLayout.HEADER_Y)
            .size(2 * MiniTerminalLayout.BUTTON_WIDTH, MiniTerminalLayout.BUTTON_HEIGHT);
        controls = Flow.column()
            .invisible()
            .right(4)
            .top(MiniTerminalLayout.HEADER_Y + MiniTerminalLayout.BUTTON_HEIGHT)
            .size(MiniTerminalLayout.BUTTON_WIDTH, 6 * MiniTerminalLayout.BUTTON_HEIGHT);
        addButtons();
        child(terminal).child(search)
            .child(header)
            .child(controls);
    }

    private void addButtons() {
        header.child(new TerminalButton(null, null, button -> {
            if (button == 1) MiniTerminalClient.openFull();
            else if (GuiScreen.isShiftKeyDown()) MiniTerminalClient.begin();
            else MiniTerminalClient.selectNext();
        }, (tooltip, button) -> {
            tooltip.addLine(MiniTerminalClient.description());
            tooltip.addLine(
                EnumChatFormatting.GRAY + tr(
                    "status." + MiniTerminalClient.status()
                        .toLowerCase(Locale.ROOT))
                    + EnumChatFormatting.RESET);
            addKeybindTooltip(tooltip, "select", "full", "reconnect");
        }).syncState(button -> button.setEnabled(model.isDisplayOnline()))
            .overlayIcon(TERMINAL_ICON))
            .child(new TerminalButton(null, null, button -> {
                layout.reset();
                MiniTerminalClientConfig.saveWindow(layout);
            }, (tooltip, button) -> tooltip.addLine(tr("reset_window"))).overlayIcon(RESET_ICON));
        controls.child(
            new TerminalButton(
                Settings.SORT_BY,
                model.getSortBy(),
                button -> model.settings(button, GuiScreen.isShiftKeyDown()),
                (tooltip, button) -> {
                    tooltip.addLine(tr("settings"));
                    tooltip.addLine(
                        EnumChatFormatting.GRAY + settingLabel("sort", model.getSortBy())
                            + " / "
                            + settingLabel("direction", model.getSortDir())
                            + " / "
                            + settingLabel("view", model.getSortDisplay())
                            + EnumChatFormatting.RESET);
                    addKeybindTooltip(tooltip, "settings.sort", "settings.direction", "settings.view");
                }).syncState(button -> button.set(model.getSortBy())))
            .child(new FluidTypeButton())
            .child(
                new TerminalButton(
                    Settings.ACTIONS,
                    ActionItems.PINS,
                    terminal::changePinCount,
                    (tooltip, button) -> {
                        for (String line : button.getMessage()
                            .split("\n")) tooltip.addLine(line);
                    }).syncState(button -> button.setEnabled(model.isDisplayOnline())))
            .child(
                new TerminalButton(
                    Settings.OPERATION_MODE,
                    OperationMode.EMPTY,
                    button -> MiniTerminalClientConfig.toggleShiftClick(),
                    (tooltip, button) -> {
                        tooltip.addLine(tr("shift_click"));
                        tooltip.addLine(
                            EnumChatFormatting.GRAY
                                + tr(MiniTerminalClientConfig.shiftClickToME ? "shift_click.me" : "shift_click.vanilla")
                                + EnumChatFormatting.RESET);
                    }).syncState(button -> button.setEnabled(MiniTerminalClientConfig.shiftClickToME)))
            .child(
                new TerminalButton(null, null, button -> MiniTerminalClient.togglePickupToME(), (tooltip, button) -> {
                    tooltip.addLine(tr("pickup"));
                    tooltip.addLine(
                        EnumChatFormatting.GRAY + tr(MiniTerminalClient.pickupToME() ? "pickup.on" : "pickup.off")
                            + EnumChatFormatting.RESET);
                }).syncState(button -> button.setEnabled(MiniTerminalClient.pickupToME()))
                    .overlayIcon(MAGNET_CARD))
            .child(
                new TerminalButton(
                    Settings.TERMINAL_STYLE,
                    TerminalStyle.SMALL,
                    button -> MiniTerminalClient.toggleCollapsed(),
                    (tooltip, button) -> tooltip.addLine(tr("collapse"))));
    }

    @Override
    public void onClose() {
        resetInteraction();
        super.onClose();
        MiniTerminalClient.closeOverlay(this);
    }

    public GuiContainer inventory() {
        return inventory;
    }

    public MiniTerminalLayout layout() {
        return layout;
    }

    public Rectangle bounds() {
        Rectangle moving = movingBounds();
        return moving == null ? layout.bounds() : moving;
    }

    boolean intersects(int x, int y, int width, int height) {
        Rectangle moving = movingBounds();
        return moving == null ? layout.intersects(x, y, width, height) : moving.intersects(x, y, width, height);
    }

    boolean isMouseOver(int mouseX, int mouseY) {
        if (!isValid() || !isOpen() || !isEnabled()) return false;
        Rectangle moving = movingBounds();
        if (moving == null ? !layout.contains(mouseX, mouseY) : !moving.contains(mouseX, mouseY)) return false;
        for (ModularPanel panel : getScreen().getPanelManager()
            .getOpenPanels()) {
            if (panel == this) return true;
            if (panel.disablePanelsBelow() || panel.isAnyHovered()) return false;
        }
        return false;
    }

    public VirtualMESlot hoveredSlot(int mouseX, int mouseY) {
        if (!isMouseOver(mouseX, mouseY) || getContext().hasDraggable() || !layout.expanded()) return null;
        Rectangle bounds = layout.bounds();
        int x = mouseX - bounds.x;
        int y = mouseY - bounds.y;
        return terminal.displayedSlotAt(x, y);
    }

    public boolean isOnline() {
        return model.isOnline();
    }

    MEGuiTextField nativeSearchField() {
        return search.nativeField();
    }

    void applySearch(String text) {

        search.applySearch(text);
    }

    public void setModel(MiniTerminalModel model) {
        this.model = model;
        if (layout.expanded()) {
            model.viewport(layout.columns(), layout.rows());
            terminal.configureScrollbar();
        }
        search.setText(model.search());
    }

    public void syncLayout() {

        if (resizing || !isValid() || getContext().getStackSize() != 0 || getContext().hasDraggable()) return;
        resizing = true;
        try {

            boolean changed = requiresResize();
            if (changed) WidgetTree.resizeInternal(resizer(), false);
            MiniTerminalPinSettings pins = model.pinSettings()
                .clampToConfig();
            layout.configurePins(
                pins.craftingCount(),
                pins.playerCount(),
                AEConfig.instance.pinSectionOrder == PinSectionOrder.PLAYER_FIRST);
            changed |= layout.update(inventory, MiniTerminalClient.collapsed());
            Rectangle bounds = layout.bounds();
            if (getArea().x != bounds.x || getArea().y != bounds.y
                || getArea().width != bounds.width
                || getArea().height != bounds.height) {
                resizer().resetPosition();
                pos(bounds.x, bounds.y).size(bounds.width, bounds.height);
                changed = true;
            }
            resizeableOnDrag(layout.expanded());
            search.setEnabled(layout.expanded());
            header.setEnabled(layout.expanded());
            controls.setEnabled(layout.expanded());
            if (layout.expanded()) {
                model.viewport(layout.columns(), layout.rows());
                model.visibility(layout.visibleCraftingPins(), layout.visiblePlayerPins());
                terminal.configureScrollbar();
            } else if (search.isFocused()) {
                getContext().removeFocus();
            }
            if (changed) {
                WidgetTree.resizeInternal(resizer(), false);

                getScreen().onFrameUpdate();
            }
        } finally {
            resizing = false;
        }
    }

    void prepareFrame() {
        syncLayout();
        MiniTerminalClient.ensureSession();
    }

    void resetInteraction() {
        if (isValid() && isMoving() && getContext().hasDraggable()) getContext().dropDraggable(true);
        consumedMouseButtons.clear();
        terminal.releaseButton(terminal.pressedButton);
        if (search.isFocused()) getContext().removeFocus();
    }

    void detach() {
        resetInteraction();
        // MUI retains host screens when opening NEI recipes. Don't leave an inactive mini terminal panel in that stack.
        if (isValid() && !getScreen().isOverlay()) closeIfOpen();
    }

    @Override
    public void drawForeground(ModularGuiContext context) {
        super.drawForeground(context);
        // MUI suppresses NEI tooltips over its widgets; ordinary containers already render them natively.
        if (!getScreen().isOverlay()) {
            context.queueForegroundRender(() -> MiniTerminalNEIAdapter.drawMuiSlotTooltip(this));
        }
    }

    private boolean hasForeignFocus() {
        IWidget focused = getContext().getFocusedWidget()
            .getElement();
        return focused != null && focused.isValid() && focused.getPanel() != this;
    }

    @Override
    public boolean onMousePressed(int button) {
        consumedMouseButtons.remove(button);
        syncLayout();
        boolean inside = isMouseOver(getContext().getAbsMouseX(), getContext().getAbsMouseY());
        if (!inside) {
            if (getScreen().isOverlay() && search.isFocused()) getContext().removeFocus();
            return false;
        }
        boolean handled = MiniTerminalNEIAdapter.handleDrop(inventory, button);
        if (handled) getContext().removeFocus();
        if (!handled) {
            if (!layout.expanded() && button == 1) {
                MiniTerminalClient.openFull();
                handled = true;
            } else {

                super.onMousePressed(button);
                handled = true;
            }
        }
        if (handled) consumedMouseButtons.add(button);
        return handled;
    }

    @Override
    public boolean onKeyPressed(char character, int keyCode) {
        syncLayout();
        // ModularPanel dispatches to the screen's focused widget, even if another panel owns it.
        // Leave foreign focus to the host panel, instead of dispatching the same key twice.
        if (hasForeignFocus()) return false;
        if (MiniTerminalBogoCompat.handleSearchKey(inventory)) return true;
        boolean inside = isMouseOver(getContext().getAbsMouseX(), getContext().getAbsMouseY());
        boolean closeKey = keyCode == Keyboard.KEY_ESCAPE
            || keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode();
        if (!inside && !search.isFocused() && !closeKey) return false;
        boolean handled = super.onKeyPressed(character, keyCode);
        if (!handled && closeKey
            && !getScreen().isOverlay()
            && getScreen().getPanelManager()
                .getTopMostPanel() == this
            && !getContext().hasDraggable()) {
            // The mini terminal is part of the host GUI; E must not close just this auxiliary panel.
            getScreen().getMainPanel()
                .closeIfOpen();
            return true;
        }
        boolean block = inside && !closeKey;
        if (!handled && block) MiniTerminalNEIAdapter.handleKeyTyped(inventory, character, keyCode);
        return handled || block;
    }

    @Override
    public boolean onMouseRelease(int button) {
        boolean consumedPress = consumedMouseButtons.remove(button);
        boolean handled = !hasForeignFocus() && super.onMouseRelease(button);
        syncLayout();
        MiniTerminalClientConfig.saveWindow(layout);

        return terminal.releaseButton(button) || handled || consumedPress;
    }

    @Override
    public boolean onMouseScroll(UpOrDown direction, int amount) {
        syncLayout();
        return !hasForeignFocus() && isMouseOver(getContext().getAbsMouseX(), getContext().getAbsMouseY())
            && super.onMouseScroll(direction, amount);
    }

    @Override
    public boolean onKeyRelease(char character, int keyCode) {
        return !hasForeignFocus() && super.onKeyRelease(character, keyCode);
    }

    private static void addKeybindTooltip(RichTooltip tooltip, String... keys) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
            for (String key : keys) tooltip.addLine(tr(key));
        } else {
            tooltip.addLine(tr("keybinds.hint"));
        }
    }

    private static String tr(String key) {
        return StatCollector.translateToLocal("gtnl.meminiterminal." + key);
    }

    private static String settingLabel(String key, Enum<?> value) {
        return tr(
            key + "."
                + value.name()
                    .toLowerCase(Locale.ROOT));
    }

    private static GuiImgButton nativeButton(Enum<?> setting, Enum<?> value) {
        GuiImgButton button = new GuiImgButton(1, 1, setting, value);
        button.setHalfSize(true);
        return button;
    }

    private static final class TerminalButton extends ButtonWidget<TerminalButton> {

        private final GuiImgButton button;
        private Consumer<GuiImgButton> syncState = button -> {};
        private UITexture overlayIcon;

        TerminalButton(Enum<?> setting, Enum<?> value, IntConsumer onPress,
            BiConsumer<RichTooltip, GuiImgButton> describe) {
            button = nativeButton(setting, value);
            size(MiniTerminalLayout.BUTTON_WIDTH, MiniTerminalLayout.BUTTON_HEIGHT).invisible()
                .playClickSound(false)
                .onMouseScrolled((direction, amount) -> true)
                .onMousePressed(mouseButton -> {
                    onPress.accept(mouseButton);
                    return true;
                })
                .tooltipAutoUpdate(true)
                .tooltipBuilder(tooltip -> describe.accept(tooltip, button));
        }

        TerminalButton syncState(Consumer<GuiImgButton> syncState) {
            this.syncState = syncState;
            return this;
        }

        TerminalButton overlayIcon(UITexture overlayIcon) {
            this.overlayIcon = overlayIcon;
            return this;
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            syncState.accept(button);
            button.drawButton(Minecraft.getMinecraft(), context.getMouseX(), context.getMouseY());
            if (overlayIcon == null) return;
            if (!button.enabled) ScreenColor.setDimmedGuiColor();
            overlayIcon.draw(2, 2, 6, 6);
            ScreenColor.resetGuiColor();
        }
    }

    private final class FluidTypeButton extends ButtonWidget<FluidTypeButton> {

        private final TypeToggleButton button = new TypeToggleButton(
            0,
            0,
            FLUID_STACK_TYPE.getButtonTexture(),
            FLUID_STACK_TYPE.getButtonIcon(),
            FLUID_STACK_TYPE.getDisplayName());

        FluidTypeButton() {
            size(MiniTerminalLayout.BUTTON_WIDTH, MiniTerminalLayout.BUTTON_HEIGHT).invisible()
                .playClickSound(false)
                .onMouseScrolled((direction, amount) -> true)
                .onMousePressed(mouseButton -> {
                    if (mouseButton == 0 || mouseButton == 1) model.toggleFluids();
                    return true;
                })
                .tooltipAutoUpdate(true)
                .tooltipBuilder(tooltip -> {
                    button.setEnabled(model.showFluids());
                    for (String line : button.getMessage()
                        .split("\n")) tooltip.addLine(line);
                });
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            button.setEnabled(model.showFluids());
            float scale = 0.5F;
            GL11.glPushMatrix();
            try {
                GL11.glTranslatef(1, 1, 0);
                GL11.glScalef(scale, scale, 1);
                button.drawButton(
                    Minecraft.getMinecraft(),
                    (int) ((context.getMouseX() - 1) / scale),
                    (int) ((context.getMouseY() - 1) / scale));
            } finally {
                GL11.glPopMatrix();
            }
        }
    }

    private final class TerminalWidget extends Widget<TerminalWidget> implements Interactable, IViewport {

        private final Gui gui = new Gui();
        private final ResourceLocation texture = new ResourceLocation(
            "appliedenergistics2",
            "textures/guis/terminal.png");
        private VirtualMEPinSlot[] pins = new VirtualMEPinSlot[0];
        private VirtualMEMonitorableSlot[] directory = new VirtualMEMonitorableSlot[0];
        private final GuiImgButton dragButton = nativeButton(null, null);
        private final GuiImgButton expandButton = new GuiImgButton(5, 5, Settings.TERMINAL_STYLE, TerminalStyle.TALL);
        private ItemRepo previousRepo;
        private int previousColumns;
        private int previousRows;
        private int previousCraftingPins;
        private int previousPlayerPins;
        private PinSectionOrder previousPinOrder;
        private boolean draggingScroll;
        private final Set<VirtualMESlot> draggedSlots = new HashSet<>();
        private int pressedButton = -1;

        TerminalWidget() {
            tooltipPos(RichTooltip.Pos.NEXT_TO_MOUSE).tooltipAutoUpdate(true)
                .tooltipBuilder(tooltip -> {
                    if (!layout.expanded()) {
                        tooltip.addLine(tr(layout.hasRoom() ? "expand" : "no_room"));
                        tooltip.addLine(tr("full"));
                    } else {

                        int x = getContext().getAbsMouseX() - getArea().x;
                        int y = getContext().getAbsMouseY() - getArea().y;
                        VirtualMESlot slot = displayedSlotAt(x, y);

                        if (slot instanceof VirtualMEPinSlot && slot.getAEStack() == null) {
                            List<String> interactionTooltip = new ArrayList<>();
                            if (model.isOnline()) slot.addTooltip(interactionTooltip);
                            if (interactionTooltip.isEmpty()) tooltip.addLine(tr("pin"));
                        }
                    }
                });
        }

        private void slots() {
            if (previousRepo == model.repo() && previousColumns == layout.columns()
                && previousRows == layout.rows()
                && previousCraftingPins == layout.visibleCraftingPins()
                && previousPlayerPins == layout.visiblePlayerPins()
                && previousPinOrder == AEConfig.instance.pinSectionOrder) return;
            previousRepo = model.repo();
            previousColumns = layout.columns();
            previousRows = layout.rows();
            previousCraftingPins = layout.visibleCraftingPins();
            previousPlayerPins = layout.visiblePlayerPins();
            previousPinOrder = AEConfig.instance.pinSectionOrder;
            pins = new VirtualMEPinSlot[layout.pinCount()];
            directory = new VirtualMEMonitorableSlot[layout.catalogSlots()];
            draggedSlots.clear();
            for (int i = 0; i < pins.length; i++) {
                pins[i] = new VirtualMEPinSlot(
                    layout.slotX(i, true) + 1,
                    layout.slotY(i, true) + 1,
                    model.repo(),
                    layout.pinIndex(i),
                    model::isTypeVisible,
                    layout.pinIndex(i) < PinList.PLAYER_OFFSET);
            }
            for (int i = 0; i < directory.length; i++) {
                directory[i] = new VirtualMEMonitorableSlot(
                    layout.slotX(i, false) + 1,
                    layout.slotY(i, false) + 1,
                    model.repo(),
                    i,
                    model::isTypeVisible);
            }
        }

        private void configureScrollbar() {
            model.scrollbar()
                .setLeft(layout.scrollX())
                .setTop(layout.scrollY() + 1)
                .setWidth(MiniTerminalLayout.SCROLL_WIDTH)
                .setHeight(layout.scrollHeight() - 2);
        }

        @Override
        public void getSelfAt(IViewportStack stack, HoveredWidgetList widgets, int x, int y) {

            int localX = stack.unTransformX(x, y);
            int localY = stack.unTransformY(x, y);
            if (layout.dragHandleAt(localX, localY)
                || IDragResizeable.getDragResizeCorner(MiniTerminalInventoryPanel.this, getArea(), stack, x, y) != null)
                return;

            IViewport.super.getSelfAt(stack, widgets, x, y);
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            Minecraft mc = Minecraft.getMinecraft();
            int width = getArea().width;
            int height = getArea().height;
            if (width <= 0 || height <= 0) return;

            ScreenColor.setGuiColor();
            GuiTextures.MC_BACKGROUND.draw(0, 0, width, height);
            ScreenColor.resetGuiColor();
            if (!layout.expanded()) {
                expandButton.xPosition = (width - 8) / 2;
                expandButton.yPosition = (height - 8) / 2;
                expandButton.setHalfSize(true);
                expandButton.drawButton(mc, context.getMouseX(), context.getMouseY());
                return;
            }
            dragButton.xPosition = layout.dragHandleX() + 1;
            dragButton.yPosition = MiniTerminalLayout.HEADER_Y + 1;
            dragButton.drawButton(mc, context.getMouseX(), context.getMouseY());
            MOVE_ICON.draw(layout.dragHandleX() + 2, MiniTerminalLayout.HEADER_Y + 2, 6, 6);
            slots();
            ScreenColor.setGuiColor();
            mc.getTextureManager()
                .bindTexture(texture);
            for (VirtualMEPinSlot pin : pins) drawSlotBackground(pin);
            for (VirtualMEMonitorableSlot slot : directory) drawSlotBackground(slot);
            ScreenColor.resetGuiColor();
            VirtualMEPinSlot.drawSlotsBackground(pins, mc, 0);
            int mouseX = context.getMouseX();
            int mouseY = context.getMouseY();
            RenderHelper.enableGUIStandardItemLighting();
            for (VirtualMEPinSlot pin : pins) {
                pin.setShowAmount(model.isDisplayOnline());
                pin.setShowAmountAlways(model.isDisplayOnline());
                pin.setShowCraftableText(model.isDisplayOnline());
                pin.setShowCraftableIcon(model.isDisplayOnline());

                pin.drawStackAndOverlay(mc, mouseX + 1, mouseY + 1);
                if (!model.isDisplayOnline() && pin.getAEStack() != null) {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    StackSizeRenderer.drawStackSize(
                        pin.getX(),
                        pin.getY(),
                        "?",
                        mc.fontRenderer,
                        AEConfig.instance.getTerminalFontSize());
                }
            }
            if (model.isDisplayOnline()) {
                for (VirtualMEMonitorableSlot slot : directory) slot.drawStackAndOverlay(mc, mouseX + 1, mouseY + 1);
            }
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            if (!model.isDisplayOnline()) {
                ScreenColor.setGuiColor();
                PANEL_FILL.draw(
                    MiniTerminalLayout.GRID_X,
                    layout.catalogY(),
                    layout.columns() * MiniTerminalLayout.CELL,
                    layout.rows() * MiniTerminalLayout.CELL);
                ScreenColor.resetGuiColor();
                String state = tr(
                    "status." + MiniTerminalClient.status()
                        .toLowerCase(Locale.ROOT));
                List<?> lines = mc.fontRenderer
                    .listFormattedStringToWidth(state, layout.columns() * MiniTerminalLayout.CELL - 12);
                lines = lines
                    .subList(0, Math.min(lines.size(), Math.max(1, layout.rows() * MiniTerminalLayout.CELL / 10)));
                int centerX = MiniTerminalLayout.GRID_X + layout.columns() * MiniTerminalLayout.CELL / 2;
                int lineY = layout.catalogY() + (layout.rows() * MiniTerminalLayout.CELL - lines.size() * 10) / 2;
                for (Object line : lines) {
                    String text = line.toString();
                    mc.fontRenderer.drawString(
                        text,
                        centerX - mc.fontRenderer.getStringWidth(text) / 2,
                        lineY,
                        ColorUtils.guiTextColorGray.getColor());
                    lineY += 10;
                }
            }
            GuiScrollbar scrollbar = model.scrollbar();
            ScreenColor.setGuiColor();
            SCROLL_TRACK.draw(
                scrollbar.getLeft() - 1,
                scrollbar.getTop() - 1,
                scrollbar.getWidth() + 2,
                scrollbar.getHeight() + 2);
            ScreenColor.resetGuiColor();
            int maximum = model.maximumScroll();
            boolean active = model.isDisplayOnline() && maximum > 0;

            int offset = active ? scrollbar.getCurrentScroll() * (scrollbar.getHeight() - 15) / maximum : 0;
            (active ? THUMB_ACTIVE : THUMB_DISABLED)
                .draw(scrollbar.getLeft(), scrollbar.getTop() + offset, scrollbar.getWidth(), 15);
            GL11.glColor4f(1, 1, 1, 1);
        }

        private void drawSlotBackground(VirtualMESlot slot) {
            gui.drawTexturedModalRect(slot.getX() - 1, slot.getY() - 1, 8, 17, 18, 18);
        }

        @Override
        public Result onMousePressed(int button) {
            draggedSlots.clear();
            draggingScroll = false;
            pressedButton = button;
            int x = getContext().getMouseX();
            int y = getContext().getMouseY();
            if (!layout.expanded()) {
                if (button == 1 || !layout.hasRoom()) MiniTerminalClient.openFull();
                else MiniTerminalClient.toggleCollapsed();
                return Result.SUCCESS;
            }
            if (y < MiniTerminalLayout.PIN_Y) return Result.SUCCESS;
            if (model.scrollbar()
                .contains(x, y)) {
                model.scrollbar()
                    .click(null, x, y);
                draggingScroll = true;
                return Result.SUCCESS;
            }
            if (handleBogoInput(true)) return Result.SUCCESS;
            int pick = Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getKeyCode();
            VirtualMESlot clicked = clickSlot(x, y, Input.mouseButton(button, pick));
            if (clicked != null) draggedSlots.add(clicked);

            return Result.SUCCESS;
        }

        private void changePinCount(int button) {
            if (button != 0 && button != 1) return;

            MiniTerminalPinSettings current = model.pinSettings()
                .clampToConfig();
            boolean crafting = GuiScreen.isCtrlKeyDown();
            int count = crafting ? current.craftingCount() : current.playerCount();
            int max = crafting ? MiniTerminalPinSettings.maxCraftingCount() : MiniTerminalPinSettings.maxPlayerCount();
            // Step to the end of the next or previous visual row, so a width change never leaves a partial row.
            int columns = layout.columns();
            int next = button == 1 ? (count - 1) / columns * columns : (count / columns + 1) * columns;
            next = Math.max(0, Math.min(max, next));
            MiniTerminalPinSettings target = crafting ? new MiniTerminalPinSettings(next, current.playerCount())
                : new MiniTerminalPinSettings(current.craftingCount(), next);
            if (button == 0 && !layout.fitsPins(target.craftingCount(), target.playerCount())) return;
            if (target.equals(model.pinSettings())) return;
            MiniTerminalClient.setPinSettings(target);
        }

        private VirtualMESlot slotAt(int x, int y) {
            return model.isOnline() ? displayedSlotAt(x, y) : null;
        }

        private VirtualMESlot displayedSlotAt(int x, int y) {
            if (!layout.expanded()) return null;
            slots();

            for (VirtualMEPinSlot pin : pins) {
                if (pin.isHovered(x + 1, y + 1)) return pin;
            }
            if (model.isDisplayOnline()) {
                int column = Math.floorDiv(x - MiniTerminalLayout.GRID_X, MiniTerminalLayout.CELL);
                int row = Math.floorDiv(y - layout.catalogY(), MiniTerminalLayout.CELL);
                if (column >= 0 && column < previousColumns && row >= 0 && row < previousRows) {
                    VirtualMEMonitorableSlot candidate = directory[row * previousColumns + column];
                    if (candidate.isHovered(x + 1, y + 1)) return candidate;
                }
            }
            return null;
        }

        private VirtualMESlot clickSlot(int x, int y, int button) {
            VirtualMESlot slot = slotAt(x, y);
            if (slot == null) return null;
            int pin = slot instanceof VirtualMEPinSlot ? slot.getSlotIndex() : -1;
            IAEStack<?> stack = slot.getAEStack();
            ItemStack held = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
            boolean nonItemInteraction = Input.preferContainerInteraction(
                AEBaseGui.isCtrlKeyDown(),
                held != null && FLUID_STACK_TYPE.isContainerItemForType(held),
                Keyboard.isKeyDown(Keyboard.KEY_SPACE));
            if ((button == 0 || button == 1) && !Keyboard.isKeyDown(Keyboard.KEY_SPACE)
                && setPinFromHand(slot, held, nonItemInteraction)) return slot;
            MonitorableAction intent = Input.click(
                button,
                Keyboard.isKeyDown(Keyboard.KEY_SPACE),
                GuiScreen.isShiftKeyDown(),
                pin >= 0,
                held != null,
                nonItemInteraction,
                stack);
            if (intent == null) return null;
            MiniTerminalClient.send(intent, intent == MonitorableAction.UNSET_PIN ? null : stack, pin);
            return slot;
        }

        /**
         * Mirrors GuiMEMonitorable#handleSetPinAction: a held stack pins itself unless the pin already shows it, in
         * which case the click falls through to the normal slot action. AE's PinsHandler#setPin rejects stacks in
         * crafting-history pins, so those clicks are consumed without a request, the same outcome as AE's SET_PIN.
         */
        private boolean setPinFromHand(VirtualMESlot slot, ItemStack held, boolean nonItemInteraction) {
            if (!(slot instanceof VirtualMEPinSlot) || held == null) return false;
            IAEStack<?> pinned = slot.getAEStack();
            IAEStack<?> target;
            if (nonItemInteraction) {
                target = FLUID_STACK_TYPE.getStackFromContainerItem(held);
                if (pinned instanceof IAEFluidStack && (target == null || pinned.equals(target))) return false;
                // An empty container has nothing to pin, but must not fall through to a fill or drain either.
                if (target == null) return true;
            } else {
                if (pinned instanceof IAEItemStack item && item.getItemStack()
                    .isItemEqual(held)) return false;
                target = AEItemStack.create(held);
            }
            if (slot.getSlotIndex() >= PinList.PLAYER_OFFSET) MiniTerminalClient.sendPin(slot.getSlotIndex(), target);
            return true;
        }

        private boolean handleBogoInput(boolean fromMouse) {
            return MiniTerminalBogoCompat.handleMiniInput(inventory, fromMouse);
        }

        @Override
        public boolean onMouseRelease(int button) {
            return releaseButton(button);
        }

        private boolean releaseButton(int button) {

            if (button != pressedButton) return false;
            pressedButton = -1;
            draggingScroll = false;
            draggedSlots.clear();
            model.scrollbar()
                .release();
            return true;
        }

        @Override
        public void onMouseDrag(int button, long timeSinceClick) {
            if (draggingScroll) {
                model.scrollbar()
                    .clickMove(getContext().getMouseY());
                return;
            }
            if (handleBogoInput(true)) return;
            if ((button != 0 && button != 1)
                || (!Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !GuiScreen.isShiftKeyDown())) return;
            int x = getContext().getMouseX();
            int y = getContext().getMouseY();
            VirtualMESlot slot = slotAt(x, y);

            if (slot != null && !draggedSlots.contains(slot)) {
                VirtualMESlot clicked = clickSlot(x, y, button);
                if (clicked != null) draggedSlots.add(clicked);
            }
        }

        @Override
        public boolean onMouseScroll(UpOrDown direction, int amount) {

            int ticks = Loader.isModLoaded("lwjgl3ify") ? amount : (int) Platform.ceilDiv(amount, 120);
            VirtualMESlot slot = slotAt(getContext().getMouseX(), getContext().getMouseY());
            if (slot != null) {
                IAEStack<?> stack = slot.getAEStack();
                MonitorableAction intent = Input.wheel(
                    direction.modifier,
                    GuiScreen.isShiftKeyDown(),
                    Minecraft.getMinecraft().thePlayer.inventory.getItemStack() != null,
                    stack instanceof IAEItemStack);
                if (intent != null) {
                    int pin = slot instanceof VirtualMEPinSlot ? slot.getSlotIndex() : -1;
                    for (int i = 0; i < ticks; i++) MiniTerminalClient.send(intent, stack, pin);
                    return true;
                }
            }
            if (layout.expanded() && getContext().getMouseY() >= layout.catalogY()) {
                model.scrollbar()
                    .wheel(direction.modifier * ticks);
            }
            return true;
        }

        @Override
        public Result onKeyPressed(char typedChar, int keyCode) {
            Minecraft mc = Minecraft.getMinecraft();
            if (keyCode == Keyboard.KEY_ESCAPE || keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
                return Result.IGNORE;
            }
            if (handleBogoInput(false)) return Result.SUCCESS;
            if (MiniTerminalNEIAdapter.handleKeyTyped(inventory, typedChar, keyCode)) return Result.SUCCESS;
            if (keyCode == mc.gameSettings.keyBindPickBlock.getKeyCode()) {
                clickSlot(getContext().getMouseX(), getContext().getMouseY(), keyBindPickBlockAction);
            }

            return Result.STOP;
        }

    }

    static final class Input {

        private Input() {}

        static int mouseButton(int button, int pickKey) {
            return button == pickKey + 100 ? keyBindPickBlockAction : button;
        }

        /**
         * Mirrors GuiMEMonitorable#handleMonitorableSlotClick, which is private to AE's GUI. Container interaction is
         * only chosen while holding a fluid container, so Ctrl alone keeps the normal item actions.
         */
        static MonitorableAction click(int button, boolean space, boolean shift, boolean pin, boolean holding,
            boolean nonItemInteraction, IAEStack<?> stack) {

            if (space) return stack instanceof IAEItemStack ? MonitorableAction.MOVE_REGION : null;
            if (button == keyBindPickBlockAction) {
                return !holding && stack != null && stack.isCraftable() ? MonitorableAction.AUTO_CRAFT : null;
            }
            if (button != 0 && button != 1) return null;
            if (pin && button == 1 && shift && !holding) return MonitorableAction.UNSET_PIN;
            if (nonItemInteraction) {
                if (button == 0)
                    return shift ? MonitorableAction.FILL_CONTAINERS : MonitorableAction.FILL_SINGLE_CONTAINER;
                return shift ? MonitorableAction.DRAIN_CONTAINERS : MonitorableAction.DRAIN_SINGLE_CONTAINER;
            }
            if (button == 1) return shift ? MonitorableAction.PICKUP_SINGLE : MonitorableAction.SPLIT_OR_PLACE_SINGLE;
            if (shift) return MonitorableAction.SHIFT_CLICK;
            if (!holding && stack != null && stack.getStackSize() == 0) return MonitorableAction.AUTO_CRAFT;
            return MonitorableAction.PICKUP_OR_SET_DOWN;
        }

        static boolean preferContainerInteraction(boolean interactionKeyDown, boolean holdingFluidContainer,
            boolean spaceDown) {
            return interactionKeyDown && holdingFluidContainer && !spaceDown;
        }

        static MonitorableAction wheel(int direction, boolean shift, boolean holding, boolean item) {
            if (!shift || direction == 0) return null;
            if (direction > 0) return holding ? MonitorableAction.ROLL_DOWN : null;
            return item ? MonitorableAction.ROLL_UP : null;
        }
    }
}
