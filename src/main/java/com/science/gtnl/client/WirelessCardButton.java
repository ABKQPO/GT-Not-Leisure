package com.science.gtnl.client;

import static com.science.gtnl.client.WirelessCardGui.tr;
import static com.science.gtnl.common.wireless.WirelessCardActions.BACK;
import static com.science.gtnl.common.wireless.WirelessCardActions.COPY_POSITION;
import static com.science.gtnl.common.wireless.WirelessCardActions.HIGHLIGHT;
import static com.science.gtnl.common.wireless.WirelessCardActions.NEXT_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.PREVIOUS_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.REFRESH;
import static com.science.gtnl.common.wireless.WirelessCardActions.RENAME;
import static com.science.gtnl.common.wireless.WirelessCardActions.TELEPORT;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_AUTO;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_GROUPING;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_PAUSE;
import static com.science.gtnl.common.wireless.WirelessCardActions.isRowAction;
import static com.science.gtnl.common.wireless.WirelessCardActions.rowIndex;
import static com.science.gtnl.common.wireless.WirelessCardActions.type;

import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.science.gtnl.utils.ColorUtils;

import appeng.client.gui.ScreenColor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WirelessCardButton {

    public static final UITexture[] BUTTON_TEXTURES = { buttonTexture(0), buttonTexture(1), buttonTexture(2) };
    private static final UITexture TOGGLE_BACKGROUND = toggleTexture(255), TOGGLE_ON = toggleTexture(129),
        TOGGLE_OFF = toggleTexture(128);
    private static final UITexture TELEPORT_ICON = itemIcon("ender_pearl"), HIGHLIGHT_ICON = itemIcon("glowstone_dust"),
        COPY_ICON = itemIcon("paper");
    private static final IDrawable[] TINTED_BUTTONS = { tinted(BUTTON_TEXTURES[0]), tinted(BUTTON_TEXTURES[1]),
        tinted(BUTTON_TEXTURES[2]) };
    private static final IDrawable TINTED_PRESSED = tinted(GuiTextures.MC_BUTTON_PRESSED),
        TINTED_TOGGLE = tinted(TOGGLE_BACKGROUND);
    private static final int FADED_ICON = 0xA0C6C6C6, CHECK_COLOR = 0xFF55FF55, CHECK_WELL = 0xFF373737;
    public final int id;
    public final Widget<?> widget;
    private final WirelessCardGui panel;
    private final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;
    private boolean available, on;
    private String label = "";

    WirelessCardButton(int id, WirelessCardGui panel) {
        this.id = id;
        this.panel = panel;
        int actionType = type(id);
        if (id == TOGGLE_AUTO || actionType == TOGGLE_PAUSE) {
            widget = new ToggleButton() {

                @Override
                public boolean canBeSeen(IViewportStack stack) {
                    return isEnabled() && super.canBeSeen(stack);
                }

                @Override
                public @NotNull Result onMousePressed(int button) {
                    if (button != 0) return Result.ACCEPT;
                    return available ? super.onMousePressed(button) : Result.STOP;
                }
            }.value(new BoolValue.Dynamic(() -> on, ignored -> panel.action(id)))
                .background(false, TINTED_TOGGLE)
                .background(true, TINTED_TOGGLE)
                .hoverBackground(false, TINTED_TOGGLE)
                .hoverBackground(true, TINTED_TOGGLE)
                .overlay(false, TOGGLE_OFF)
                .overlay(true, TOGGLE_ON);
        } else {
            widget = new ActionButton().onMousePressed(button -> {
                if (button != 0) return false;
                panel.action(id);
                return true;
            });
            switch (actionType) {
                case OPEN_DETAILS -> widget.background(IDrawable.EMPTY)
                    .hoverBackground(IDrawable.EMPTY);
                case TELEPORT -> styleIcon(TELEPORT_ICON);
                case HIGHLIGHT -> styleIcon(HIGHLIGHT_ICON);
                case COPY_POSITION -> styleIcon(COPY_ICON);
                default -> styleTextButton();
            }
        }
        if (isRowAction(id) || id == TOGGLE_GROUPING || id == REFRESH || id == BACK)
            widget.tooltipBuilder(this::tooltip)
                .tooltipAutoUpdate(true);
    }

    private class ActionButton extends ButtonWidget<ActionButton> {

        @Override
        public boolean canBeSeen(IViewportStack stack) {
            return isEnabled() && super.canBeSeen(stack);
        }

        @Override
        public @NotNull Result onMousePressed(int button) {
            // Icon buttons sit on top of the row's open area, so a refused click must not fall through to it.
            return button == 0 && !available ? Result.STOP : super.onMousePressed(button);
        }
    }

    public static IDrawable background(Widget<?> widget, BooleanSupplier available, BooleanSupplier selected) {
        return (context, x, y, width, height, theme) -> {
            IDrawable texture = !available.getAsBoolean() ? TINTED_BUTTONS[0]
                : selected.getAsBoolean() ? TINTED_PRESSED : TINTED_BUTTONS[widget.isHovering() ? 2 : 1];
            texture.draw(context, x, y, width, height, theme);
        };
    }

    public static int labelColor(boolean hovered, boolean available) {
        return !available ? ColorUtils.guiButtonDisabled.getColor()
            : hovered ? ColorUtils.guiButtonHover.getColor() : ColorUtils.guiButtonNormal.getColor();
    }

    private void styleTextButton() {
        IDrawable background = background(widget, () -> available, () -> false);
        widget.background(background)
            .hoverBackground(background);
        if (id == REFRESH) widget.overlay(
            (context, x, y, width, height, theme) -> GuiTextures.REFRESH
                .draw(context, x + (width - 16) / 2, y + (height - 16) / 2, 16, 16, theme));
        else if (id == TOGGLE_GROUPING) widget.overlay(this::drawCheckBox);
        else widget.overlay(
            IKey.dynamic(() -> fontRendererObj.trimStringToWidth(label, Math.max(0, widget.getArea().width - 4)))
                .color(() -> labelColor(widget.isHovering(), available))
                .shadow(true));
    }

    private void drawCheckBox(GuiContext context, int x, int y, int width, int height, WidgetTheme theme) {
        int color = labelColor(widget.isHovering(), available), boxY = y + (height - 8) / 2;
        GuiDraw.drawRect(x + 4, boxY, 8, 8, 0xFF000000 | color);
        GuiDraw.drawRect(x + 5, boxY + 1, 6, 6, CHECK_WELL);
        if (on) GuiDraw.drawRect(x + 6, boxY + 2, 4, 4, CHECK_COLOR);
        panel.shadowText(fontRendererObj.trimStringToWidth(label, Math.max(0, width - 19)), x + 15, boxY, color);
    }

    private void styleIcon(UITexture icon) {
        widget.background(IDrawable.EMPTY)
            .hoverBackground(IDrawable.EMPTY)
            .overlay((context, x, y, width, height, theme) -> {
                if (available && widget.isHovering()) TINTED_TOGGLE.draw(context, x, y, width, height, theme);
                icon.draw(x, y, width, height);
                if (!available) GuiDraw.drawRect(x, y, width, height, FADED_ICON);
            });
    }

    private static IDrawable tinted(UITexture texture) {
        return (context, x, y, width, height, theme) -> {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            try {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                ScreenColor.setGuiColor();
                texture.draw(x, y, width, height);
            } finally {
                GL11.glPopAttrib();
            }
        };
    }

    private static boolean sameDimension(NBTTagCompound row) {
        var player = Minecraft.getMinecraft().thePlayer;
        return player != null && row.getInteger("dimension") == player.dimension;
    }

    private void tooltip(RichTooltip tooltip) {
        NBTTagCompound data = panel.view();
        NBTTagCompound row = isRowAction(id) ? data.getTagList("rows", 10)
            .getCompoundTagAt(rowIndex(id)) : new NBTTagCompound();
        String gray = EnumChatFormatting.GRAY.toString();
        switch (type(id)) {
            case OPEN_DETAILS -> {
                String name = WirelessCardGui.rowName(row, ItemStack.loadItemStackFromNBT(row.getCompoundTag("icon")));
                String targetName = row.getString("targetName");
                tooltip.addLine(targetName.isEmpty() || targetName.equals(name) ? name : name + " - " + targetName);
                tooltip.addLine(gray + row.getString("positionLabel"));
                tooltip.addLine(
                    EnumChatFormatting.YELLOW + (row.getBoolean("group")
                        ? tr(
                            "row_expand_hint",
                            row.getBoolean("category") ? row.getInteger("items") : row.getInteger("clusters"))
                        : tr("row_open_hint")));
            }
            case TELEPORT -> {
                tooltip.addLine(tr("teleport_tip"));
                tooltip.addLine(gray + row.getString("positionLabel"));
            }
            case HIGHLIGHT -> {
                tooltip.addLine(tr("highlight_tip"));
                tooltip.addLine(
                    sameDimension(row) ? gray + tr("highlight_range")
                        : EnumChatFormatting.RED
                            + tr("highlight_other_dimension", panel.dimensionName(row.getInteger("dimension"))));
            }
            case COPY_POSITION -> {
                tooltip.addLine(tr("copy_tip"));
                tooltip.addLine(
                    gray + row.getString("position") + (row.getBoolean("group") ? "" : " / " + row.getString("side")));
            }
            case TOGGLE_PAUSE -> tooltip.addLine(tr(row.getBoolean("paused") ? "resume_tip" : "pause_tip"));
            case TOGGLE_GROUPING -> {
                tooltip.addLine(
                    tr("merge_types") + ": "
                        + StatCollector
                            .translateToLocal(data.getBoolean("collapseTypes") ? "options.on" : "options.off"));
                tooltip.addLine(gray + tr("merge_types_tip"));
            }
            case REFRESH -> {
                tooltip.addLine(tr("refresh"));
                tooltip.addLine(gray + tr("refresh_tip"));
            }
            case BACK -> tooltip.addLine(tr("back_hint"));
            default -> {}
        }
    }

    void update(NBTTagCompound data, boolean ready, int visibleRows) {
        NBTTagList rows = data.getTagList("rows", 10);
        int rowCount = Math.min(visibleRows, rows.tagCount());
        int actionType = type(id);
        boolean visible = true, detail = data.getBoolean("detail"), group = data.getBoolean("group");
        available = ready;
        if (isRowAction(id)) {
            int index = rowIndex(id);
            if (index >= rowCount) {
                widget.setEnabled(false);
                return;
            }
            NBTTagCompound row = rows.getCompoundTagAt(index);
            if (actionType == TOGGLE_PAUSE) on = !row.getBoolean("paused");
            if (actionType == TOGGLE_PAUSE || actionType == HIGHLIGHT || actionType == TELEPORT)
                visible = !row.getBoolean("group");
            if (actionType == COPY_POSITION) visible = !row.getBoolean("category");
            if (actionType == OPEN_DETAILS) visible = !detail;
            if (actionType == HIGHLIGHT) available &= sameDimension(row);
        } else switch (id) {
            case TOGGLE_AUTO -> on = data.getBoolean("auto");
            case BACK -> visible = detail || group;
            case RENAME -> {
                visible = detail;
                available &= data.getBoolean("renameable");
            }
            case TOGGLE_GROUPING -> {
                visible = !detail && !group;
                on = data.getBoolean("collapseTypes");
            }
            case PREVIOUS_PAGE -> available &= data.getInteger("page") > 0;
            case NEXT_PAGE -> available &= data.getInteger("page") + 1 < data.getInteger("pages");
            default -> {}
        }
        widget.setEnabled(visible);
        label = switch (id) {
            case PREVIOUS_PAGE -> "<";
            case NEXT_PAGE -> ">";
            case BACK -> tr("back_compact");
            case RENAME -> StatCollector.translateToLocal("selectWorld.rename");
            case TOGGLE_GROUPING -> tr("merge_types");
            default -> "";
        };
    }

    private static UITexture buttonTexture(int state) {
        return UITexture.builder()
            .location(new ResourceLocation("minecraft", "textures/gui/widgets.png"))
            .imageSize(256, 256)
            .subAreaXYWH(0, 46 + state * 20, 200, 20)
            .adaptable(2)
            .build();
    }

    private static UITexture toggleTexture(int icon) {
        return UITexture.builder()
            .location(new ResourceLocation("appliedenergistics2", "textures/guis/states.png"))
            .imageSize(256, 256)
            .subAreaXYWH(icon % 16 * 16, icon / 16 * 16, 16, 16)
            .build();
    }

    private static UITexture itemIcon(String item) {
        return new UITexture(new ResourceLocation("minecraft", "items/" + item), 0, 0, 1, 1, null, true);
    }
}
