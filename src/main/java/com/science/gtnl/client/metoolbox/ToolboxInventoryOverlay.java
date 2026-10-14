package com.science.gtnl.client.metoolbox;

import static appeng.client.gui.implementations.GuiMEMonitorable.keyBindPickBlockAction;
import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.layout.IViewport;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.api.widget.IDragResizeable;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.HoveredWidgetList;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.ScienceNotLeisure;

import appeng.api.config.ActionItems;
import appeng.api.config.OperationMode;
import appeng.api.config.PinSectionOrder;
import appeng.api.config.PinsRows;
import appeng.api.config.Settings;
import appeng.api.config.TerminalStyle;
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
import cpw.mods.fml.common.Loader;

public final class ToolboxInventoryOverlay extends ModularScreen {

    private static final UITexture MAGNET_CARD = UITexture.fullImage("ae2wct", "items/magnetCard");

    private static final UITexture TOOLBOX_ICON = UITexture.builder()
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
    private final ToolboxForeground foreground = new ToolboxForeground(this);
    private final ToolboxLayout layout = ToolboxClient.layoutState();
    private final ToolboxPanel panel;
    private ToolboxTerminalModel model;
    private final ToolboxSearchField search;
    private final TerminalWidget terminal;
    private final Flow header;
    private final Flow controls;
    private final Set<Integer> consumedMouseButtons = new HashSet<>();
    private boolean resizing;

    public ToolboxInventoryOverlay(GuiContainer inventory, ToolboxTerminalModel model) {
        super(ScienceNotLeisure.MODID, new ToolboxPanel(ToolboxClient.layoutState()));
        panel = (ToolboxPanel) getMainPanel();
        panel.pos(0, 0)
            .size(ToolboxLayout.WIDTH, 166);
        panel.tooltipBuilder(tooltip -> {
            tooltip.addLine(tr("move"));
            if (layout.expanded()) tooltip.addLine(tr("resize"));
        });
        this.inventory = inventory;
        this.model = model;
        terminal = new TerminalWidget();
        terminal.full()
            .pos(0, 0)
            .invisible();
        search = new ToolboxSearchField(text -> this.model.search(text)).left(ToolboxLayout.SEARCH_X)
            .right(ToolboxLayout.SEARCH_RIGHT)
            .top(ToolboxLayout.HEADER_Y)
            .height(ToolboxLayout.HEADER_SIZE);
        search.setText(model.search());
        header = Flow.row()
            .invisible()
            .right(4 + ToolboxLayout.BUTTON_WIDTH)
            .top(ToolboxLayout.HEADER_Y)
            .size(2 * ToolboxLayout.BUTTON_WIDTH, ToolboxLayout.BUTTON_HEIGHT)
            .child(new TerminalButton(0, null, null))
            .child(new TerminalButton(4, null, null));
        controls = Flow.column()
            .invisible()
            .right(4)
            .top(ToolboxLayout.HEADER_Y + ToolboxLayout.BUTTON_HEIGHT)
            .size(ToolboxLayout.BUTTON_WIDTH, 6 * ToolboxLayout.BUTTON_HEIGHT)
            .child(new TerminalButton(1, Settings.SORT_BY, model.getSortBy()))
            .child(new FluidTypeButton())
            .child(new TerminalButton(2, Settings.ACTIONS, ActionItems.PINS))
            .child(new TerminalButton(3, Settings.OPERATION_MODE, OperationMode.EMPTY))
            .child(new TerminalButton(6, null, null))
            .child(new TerminalButton(5, Settings.TERMINAL_STYLE, TerminalStyle.SMALL));
        getMainPanel().child(terminal)
            .child(search)
            .child(header)
            .child(controls);
    }

    @Override
    public void onClose() {
        super.onClose();
        ToolboxClient.closeOverlay(this);
    }

    public GuiContainer inventory() {
        return inventory;
    }

    public ToolboxLayout layout() {
        return layout;
    }

    public Rectangle bounds() {
        Rectangle moving = panel.movingBounds();
        return moving == null ? layout.bounds() : moving;
    }

    public VirtualMESlot hoveredSlot(int mouseX, int mouseY) {
        syncLayout();
        if (getContext().hasDraggable() || !layout.expanded() || !layout.contains(mouseX, mouseY)) return null;
        Rectangle bounds = layout.bounds();
        int x = mouseX - bounds.x;
        int y = mouseY - bounds.y;
        return terminal.displayedSlotAt(x, y);
    }

    public boolean isOnline() {
        return model.isOnline();
    }

    ToolboxForeground foreground() {
        return foreground;
    }

    public boolean deferNativeTooltip() {
        return foreground.deferNativeTooltip();
    }

    MEGuiTextField nativeSearchField() {
        return search.nativeField();
    }

    void applySearch(String text) {

        search.applySearch(text);
    }

    public void setModel(ToolboxTerminalModel model) {
        this.model = model;
        if (layout.expanded()) {
            model.viewport(layout.columns(), layout.rows());
            terminal.configureScrollbar();
        }
        search.setText(model.search());
    }

    public void syncLayout() {

        if (resizing || getScreenWrapper() == null || getContext().getStackSize() != 0) return;
        if (!panel.isValid() || getContext().hasDraggable()) return;
        resizing = true;
        try {

            boolean changed = panel.requiresResize();
            if (changed) WidgetTree.resizeInternal(panel.resizer(), false);
            PinsRows craftingRows = PinsRows.fromOrdinal(
                Math.min(
                    model.craftingPinsRows()
                        .ordinal(),
                    AEConfig.instance.maxCraftingPinRows));
            PinsRows playerRows = PinsRows.fromOrdinal(
                Math.min(
                    model.playerPinsRows()
                        .ordinal(),
                    AEConfig.instance.maxPlayerPinRows));
            layout.configurePins(
                craftingRows.getSlotCount(),
                playerRows.getSlotCount(),
                AEConfig.instance.pinSectionOrder == PinSectionOrder.PLAYER_FIRST);
            changed |= layout.update(inventory, ToolboxClient.collapsed());
            Rectangle bounds = layout.bounds();
            if (panel.getArea().x != bounds.x || panel.getArea().y != bounds.y
                || panel.getArea().width != bounds.width
                || panel.getArea().height != bounds.height) {
                panel.resizer()
                    .resetPosition();
                panel.pos(bounds.x, bounds.y)
                    .size(bounds.width, bounds.height);
                changed = true;
            }
            panel.resizeableOnDrag(layout.expanded());
            search.setEnabled(layout.expanded());
            header.setEnabled(layout.expanded());
            controls.setEnabled(layout.expanded());
            if (layout.expanded()) {
                model.viewport(layout.columns(), layout.rows());
                model.visibility(layout.visibleCraftingPins(), layout.visiblePlayerPins());
                terminal.configureScrollbar();
            } else {
                getContext().removeFocus();
            }
            if (changed) {
                WidgetTree.resizeInternal(panel.resizer(), false);

                super.onFrameUpdate();
            }
        } finally {
            resizing = false;
        }
    }

    @Override
    public void drawScreen() {
        syncLayout();
        ToolboxClient.ensureSession();
        foreground.queueCursor();
        super.drawScreen();
    }

    @Override
    public void drawForeground() {
        super.drawForeground();
        foreground.queueTooltip();
    }

    @Override
    public boolean onMousePressed(int button) {
        consumedMouseButtons.remove(button);
        syncLayout();
        boolean inside = bounds().contains(getContext().getAbsMouseX(), getContext().getAbsMouseY());
        if (inside && inventory instanceof IMuiScreen host) {

            host.getScreen()
                .getContext()
                .removeFocus();
        }
        boolean handled = ToolboxNEIAdapter.handleDrop(inventory, button);
        if (!handled) {
            if (inside && !layout.expanded() && button == 1) {
                ToolboxClient.openFull();
                handled = true;
            } else {

                handled = super.onMousePressed(button) || inside;
            }
        }
        if (handled) consumedMouseButtons.add(button);
        return handled;
    }

    @Override
    public boolean onKeyPressed(char character, int keyCode) {
        syncLayout();
        if (ToolboxBogoCompat.handleSearchKey(inventory)) return true;
        boolean handled = super.onKeyPressed(character, keyCode);

        boolean block = bounds().contains(getContext().getAbsMouseX(), getContext().getAbsMouseY())
            && keyCode != Keyboard.KEY_ESCAPE
            && keyCode != Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode();
        if (!handled && block) ToolboxNEIAdapter.handleKeyTyped(inventory, character, keyCode);
        return handled || block;
    }

    @Override
    public boolean onMouseRelease(int button) {
        boolean consumedPress = consumedMouseButtons.remove(button);
        boolean handled = super.onMouseRelease(button);
        syncLayout();
        ToolboxClientConfig.saveWindow(layout);

        return terminal.releaseButton(button) || handled || consumedPress;
    }

    @Override
    public boolean onMouseScroll(UpOrDown direction, int amount) {
        syncLayout();
        return super.onMouseScroll(direction, amount);
    }

    private static String tr(String key) {
        return StatCollector.translateToLocal("gtnl.metoolbox." + key);
    }

    private static String settingLabel(String key, Enum<?> value) {
        return tr(
            key + "."
                + value.name()
                    .toLowerCase(java.util.Locale.ROOT));
    }

    private static GuiImgButton nativeButton(Enum<?> setting, Enum<?> value) {
        GuiImgButton button = new GuiImgButton(1, 1, setting, value);
        button.setHalfSize(true);
        return button;
    }

    private final class TerminalButton extends ButtonWidget<TerminalButton> {

        private final int index;
        private final GuiImgButton button;

        TerminalButton(int index, Enum<?> setting, Enum<?> value) {
            this.index = index;
            button = nativeButton(setting, value);
            size(ToolboxLayout.BUTTON_WIDTH, ToolboxLayout.BUTTON_HEIGHT).invisible()
                .playClickSound(false)
                .onMouseScrolled((direction, amount) -> true)
                .onMousePressed(mouseButton -> {
                    if (index == 6) ToolboxClient.togglePickupToME();
                    else if (index == 5) ToolboxClient.toggleCollapsed();
                    else if (index == 4) {
                        layout.reset();
                        ToolboxClientConfig.saveWindow(layout);
                    } else if (index == 3) ToolboxClientConfig.toggleShiftClick();
                    else if (index == 2) terminal.changePinRows(mouseButton);
                    else if (index == 1) model.settings(mouseButton, GuiScreen.isShiftKeyDown());
                    else if (mouseButton == 1) ToolboxClient.openFull();
                    else if (GuiScreen.isShiftKeyDown()) ToolboxClient.begin();
                    else ToolboxClient.selectNext();
                    return true;
                })
                .tooltipAutoUpdate(true)
                .tooltipBuilder(tooltip -> {
                    if (index == 6) {
                        tooltip.addLine(tr("pickup"));
                        tooltip.addLine(tr(ToolboxClient.pickupToME() ? "pickup.on" : "pickup.off"));
                    } else if (index == 5) tooltip.addLine(tr("collapse"));
                    else if (index == 4) tooltip.addLine(tr("reset_window"));
                    else if (index == 3) {
                        tooltip.addLine(tr("shift_click"));
                        tooltip
                            .addLine(tr(ToolboxClientConfig.shiftClickToME ? "shift_click.me" : "shift_click.vanilla"));
                    } else if (index == 2) {
                        for (String line : button.getMessage()
                            .split("\n")) tooltip.addLine(line);
                    } else if (index == 1) {
                        tooltip.addLine(tr("settings"));
                        tooltip.addLine(
                            settingLabel("sort", model.getSortBy()) + " / "
                                + settingLabel("direction", model.getSortDir())
                                + " / "
                                + settingLabel("view", model.getSortDisplay()));
                    } else {
                        tooltip.addLine(ToolboxClient.description());
                        tooltip.addLine(
                            tr(
                                "status." + ToolboxClient.status()
                                    .toLowerCase(java.util.Locale.ROOT)));
                        tooltip.addLine(tr("select"));
                        tooltip.addLine(tr("reconnect"));
                    }
                });
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            if (index == 0 || index == 2) button.setEnabled(model.isDisplayOnline());
            else if (index == 1) button.set(model.getSortBy());
            else if (index == 3) button.setEnabled(ToolboxClientConfig.shiftClickToME);
            else if (index == 6) button.setEnabled(ToolboxClient.pickupToME());
            button.drawButton(Minecraft.getMinecraft(), context.getMouseX(), context.getMouseY());
            if (index == 4) RESET_ICON.draw(2, 2, 6, 6);
            if (index == 0 || index == 6) {
                if (!button.enabled) ScreenColor.setDimmedGuiColor();
                (index == 0 ? TOOLBOX_ICON : MAGNET_CARD).draw(2, 2, 6, 6);
                ScreenColor.resetGuiColor();
            }
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
            size(ToolboxLayout.BUTTON_WIDTH, ToolboxLayout.BUTTON_HEIGHT).invisible()
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
                .setWidth(ToolboxLayout.SCROLL_WIDTH)
                .setHeight(layout.scrollHeight() - 2);
        }

        @Override
        public void getSelfAt(IViewportStack stack, HoveredWidgetList widgets, int x, int y) {

            int localX = stack.unTransformX(x, y);
            int localY = stack.unTransformY(x, y);
            if (layout.dragHandleAt(localX, localY)
                || IDragResizeable.getDragResizeCorner(panel, getArea(), stack, x, y) != null) return;

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
            dragButton.yPosition = ToolboxLayout.HEADER_Y + 1;
            dragButton.drawButton(mc, context.getMouseX(), context.getMouseY());
            MOVE_ICON.draw(layout.dragHandleX() + 2, ToolboxLayout.HEADER_Y + 2, 6, 6);
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
                    ToolboxLayout.GRID_X,
                    layout.catalogY(),
                    layout.columns() * ToolboxLayout.CELL,
                    layout.rows() * ToolboxLayout.CELL);
                ScreenColor.resetGuiColor();
                String state = tr(
                    "status." + ToolboxClient.status()
                        .toLowerCase(java.util.Locale.ROOT));
                List<?> lines = mc.fontRenderer
                    .listFormattedStringToWidth(state, layout.columns() * ToolboxLayout.CELL - 12);
                lines = lines.subList(0, Math.min(lines.size(), Math.max(1, layout.rows() * ToolboxLayout.CELL / 10)));
                int centerX = ToolboxLayout.GRID_X + layout.columns() * ToolboxLayout.CELL / 2;
                int lineY = layout.catalogY() + (layout.rows() * ToolboxLayout.CELL - lines.size() * 10) / 2;
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
                if (button == 1 || !layout.hasRoom()) ToolboxClient.openFull();
                else ToolboxClient.toggleCollapsed();
                return Result.SUCCESS;
            }
            if (y < ToolboxLayout.PIN_Y) return Result.SUCCESS;
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

        private void changePinRows(int button) {
            if (button != 0 && button != 1) return;

            int craft = Math.min(
                model.craftingPinsRows()
                    .ordinal(),
                AEConfig.instance.maxCraftingPinRows);
            int player = Math.min(
                model.playerPinsRows()
                    .ordinal(),
                AEConfig.instance.maxPlayerPinRows);
            int delta = button == 1 ? -1 : 1;
            if (GuiScreen.isCtrlKeyDown())
                craft = Math.max(0, Math.min(AEConfig.instance.maxCraftingPinRows, craft + delta));
            else player = Math.max(0, Math.min(AEConfig.instance.maxPlayerPinRows, player + delta));
            PinsRows craftingRows = PinsRows.fromOrdinal(craft);
            PinsRows playerRows = PinsRows.fromOrdinal(player);
            if (button == 0 && !layout.fitsPins(craftingRows.getSlotCount(), playerRows.getSlotCount())) return;
            if (craftingRows == model.craftingPinsRows() && playerRows == model.playerPinsRows()) return;
            ToolboxClient.setPinRows(craftingRows, playerRows);
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
                int column = Math.floorDiv(x - ToolboxLayout.GRID_X, ToolboxLayout.CELL);
                int row = Math.floorDiv(y - layout.catalogY(), ToolboxLayout.CELL);
                if (column >= 0 && column < previousColumns && row >= 0 && row < previousRows) {
                    VirtualMEMonitorableSlot candidate = directory[row * previousColumns + column];
                    if (candidate.isHovered(x + 1, y + 1)) return candidate;
                }
                for (VirtualMEMonitorableSlot entry : directory) {
                    if (entry.isHovered(x + 1, y + 1)) return entry;
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
            boolean nonItemInteraction = AEBaseGui.isCtrlKeyDown();
            MonitorableAction intent = Input.click(
                button,
                Keyboard.isKeyDown(Keyboard.KEY_SPACE),
                GuiScreen.isShiftKeyDown(),
                pin >= 0,
                held != null,
                nonItemInteraction,
                stack);
            if (intent == null) return null;
            ToolboxClient.send(intent, intent == MonitorableAction.UNSET_PIN ? null : stack, pin);
            return slot;
        }

        private boolean handleBogoInput(boolean fromMouse) {
            return ToolboxBogoCompat.handleMiniInput(inventory, fromMouse);
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
                    for (int i = 0; i < ticks; i++) ToolboxClient.send(intent, stack, pin);
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
            if (ToolboxNEIAdapter.handleKeyTyped(inventory, typedChar, keyCode)) return Result.SUCCESS;
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
