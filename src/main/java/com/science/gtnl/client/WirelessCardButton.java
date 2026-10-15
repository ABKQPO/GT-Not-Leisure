package com.science.gtnl.client;

import static com.science.gtnl.common.wireless.WirelessCardActions.BACK;
import static com.science.gtnl.common.wireless.WirelessCardActions.COPY_POSITION;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_FILTER;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_VISUALISATION;
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.value.IntValue;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.science.gtnl.common.wireless.WirelessCardVisualisation;
import com.science.gtnl.utils.ColorUtils;

import appeng.client.gui.ScreenColor;
import appeng.core.localization.GuiText;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WirelessCardButton {

    public static final UITexture[] BUTTON_TEXTURES = { buttonTexture(0), buttonTexture(1), buttonTexture(2) };
    private static final UITexture TOGGLE_BACKGROUND = toggleTexture(255), TOGGLE_ON = toggleTexture(129),
        TOGGLE_OFF = toggleTexture(128);
    private static final IDrawable[] TINTED_BUTTONS = { tinted(BUTTON_TEXTURES[0]), tinted(BUTTON_TEXTURES[1]),
        tinted(BUTTON_TEXTURES[2]) };
    public final int id;
    public final Widget<?> widget;
    private final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;
    private boolean available, on;
    private String label = "";

    WirelessCardButton(int id, WirelessCardPanel panel) {
        this.id = id;
        if (id == TOGGLE_AUTO || type(id) == TOGGLE_PAUSE) {
            widget = new ToggleButton() {

                @Override
                public boolean canBeSeen(IViewportStack stack) {
                    return isEnabled() && super.canBeSeen(stack);
                }

                @Override
                public @NotNull Result onMousePressed(int button) {
                    return button == 0 && available ? super.onMousePressed(button) : Result.ACCEPT;
                }
            }.value(new BoolValue.Dynamic(() -> on, ignored -> panel.action(id)))
                .background(false, tinted(TOGGLE_BACKGROUND))
                .background(true, tinted(TOGGLE_BACKGROUND))
                .hoverBackground(false, tinted(TOGGLE_BACKGROUND))
                .hoverBackground(true, tinted(TOGGLE_BACKGROUND))
                .overlay(false, TOGGLE_OFF)
                .overlay(true, TOGGLE_ON);
        } else if (id == CYCLE_FILTER) {
            widget = new CycleButtonWidget() {

                @Override
                public boolean canBeSeen(IViewportStack stack) {
                    return isEnabled() && super.canBeSeen(stack);
                }

                @Override
                public @NotNull Result onMousePressed(int button) {
                    return button == 0 && available ? super.onMousePressed(button) : Result.ACCEPT;
                }
            }.stateCount(5)
                .value(
                    new IntValue.Dynamic(
                        () -> panel.view()
                            .getInteger("filterState"),
                        ignored -> panel.action(id)));
            styleTextButton();
        } else {
            widget = new ActionButton().onMousePressed(button -> {
                if (button != 0 || !available) return false;
                panel.action(id);
                return true;
            });
            if (type(id) == OPEN_DETAILS) widget.background(IDrawable.EMPTY)
                .hoverBackground(IDrawable.EMPTY);
            else styleTextButton();
        }
        if (type(id) == OPEN_DETAILS) widget.tooltipBuilder(tooltip -> {
            NBTTagCompound row = panel.view()
                .getTagList("rows", 10)
                .getCompoundTagAt(rowIndex(id));
            String name = WirelessCardPanel.rowName(row, ItemStack.loadItemStackFromNBT(row.getCompoundTag("icon")));
            String targetName = row.getString("targetName");
            tooltip.addLine(targetName.isEmpty() || targetName.equals(name) ? name : name + " - " + targetName);
            tooltip.addLine(row.getString("positionLabel"));
            if (row.getBoolean("group")) tooltip.addLine(tr("loaded", row.getInteger("clusters")));
        })
            .tooltipAutoUpdate(true);
        if (id == TOGGLE_GROUPING) widget
            .tooltipBuilder(
                tooltip -> tooltip.addLine(
                    GuiText.Types.getLocal() + ": "
                        + StatCollector.translateToLocal(
                            panel.view()
                                .getBoolean("collapseTypes") ? "options.on" : "options.off")))
            .tooltipAutoUpdate(true);
    }

    private static class ActionButton extends ButtonWidget<ActionButton> {

        @Override
        public boolean canBeSeen(IViewportStack stack) {
            return isEnabled() && super.canBeSeen(stack);
        }
    }

    private void styleTextButton() {
        IDrawable background = (context, x, y, width, height,
            theme) -> TINTED_BUTTONS[!available ? 0 : widget.isHovering() ? 2 : 1]
                .draw(context, x, y, width, height, theme);
        widget.background(background)
            .hoverBackground(background)
            .overlay(
                IKey.dynamic(() -> fontRendererObj.trimStringToWidth(label, Math.max(0, widget.getArea().width - 4)))
                    .color(
                        () -> !available ? ColorUtils.guiButtonDisabled.getColor()
                            : widget.isHovering() ? ColorUtils.guiButtonHover.getColor()
                                : ColorUtils.guiButtonNormal.getColor())
                    .shadow(true));
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

    void update(NBTTagCompound data, boolean ready, int visibleRows) {
        NBTTagList rows = data.getTagList("rows", 10);
        int rowCount = Math.min(visibleRows, rows.tagCount());
        int actionType = type(id);
        boolean visible = true;
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
        }
        available = ready;
        if (id == TOGGLE_AUTO) on = data.getBoolean("auto");
        if (id == BACK) visible = data.getBoolean("detail") || data.getBoolean("group");
        if (id == RENAME) {
            visible = data.getBoolean("detail");
            available &= data.getBoolean("renameable");
        }
        if (id == CYCLE_FILTER || id == TOGGLE_AUTO || id == CYCLE_VISUALISATION || id == TOGGLE_GROUPING)
            visible = !data.getBoolean("detail");
        if (id == TOGGLE_GROUPING) visible &= !data.getBoolean("typeGroup");
        if (actionType == OPEN_DETAILS) visible &= !data.getBoolean("detail");
        widget.setEnabled(visible);
        if (!visible) return;
        if (id == PREVIOUS_PAGE) available &= data.getInteger("page") > 0;
        if (id == NEXT_PAGE) available &= data.getInteger("page") + 1 < data.getInteger("pages");
        label = switch (actionType) {
            case PREVIOUS_PAGE -> "<";
            case NEXT_PAGE -> ">";
            case REFRESH -> tr("refresh");
            case BACK -> tr("back_compact");
            case RENAME -> StatCollector.translateToLocal("selectWorld.rename");
            case CYCLE_FILTER -> tr("filter." + data.getInteger("filterState"));
            case TOGGLE_GROUPING -> GuiText.Types.getLocal() + (data.getBoolean("collapseTypes") ? " +" : " −");
            case TELEPORT -> tr("teleport");
            case HIGHLIGHT -> tr("highlight");
            case COPY_POSITION -> tr("copy");
            default -> "";
        };
        if (id == CYCLE_VISUALISATION) {
            int mode = data.getInteger("visualMode");
            String label = StatCollector
                .translateToLocal(WirelessCardVisualisation.modeKey(mode) + (mode > 0 ? ".short" : ""));
            this.label = fontRendererObj.trimStringToWidth(
                widget.getArea().width < 102 ? label : tr("visualisation_button", label),
                widget.getArea().width - 8);
        }
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

    private static String tr(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("gtnl.wireless.gui." + key, args);
    }

}
