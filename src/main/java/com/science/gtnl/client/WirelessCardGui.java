package com.science.gtnl.client;

import static net.minecraft.client.gui.Gui.drawRect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.core.mixins.early.minecraft.GuiScreenAccessor;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.drawable.text.TextRenderer;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.wireless.WirelessCardContainer;
import com.science.gtnl.common.wireless.WirelessCardLayout;
import com.science.gtnl.common.wireless.WirelessCardVisualisation;

import appeng.client.gui.ScreenColor;
import appeng.core.localization.ColorUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class WirelessCardGui extends ModularPanel {

    private static final int ROW_TOP = 82;
    private static final int ROW_HEIGHT = 24;
    private static final int CONTENT_X = 34;
    private static final UITexture[] BUTTON_TEXTURES = { buttonTexture(0), buttonTexture(1), buttonTexture(2) };
    private static final UITexture TOGGLE_BACKGROUND = toggleTexture(255);
    private static final UITexture TOGGLE_ON = toggleTexture(129);
    private static final UITexture TOGGLE_OFF = toggleTexture(128);
    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fontRendererObj = mc.fontRenderer;
    private final WirelessCardContainer container;
    private final List<CardButton> buttons = new ArrayList<>();
    private final TextFieldWidget search, dimension, name;
    private int xSize = 320, ySize = 232;
    private int visibleRows = WirelessCardLayout.MIN_ROWS;
    private int layoutRetry, filterState;
    private String editTarget = "", serverName = "";

    public WirelessCardGui(WirelessCardContainer container) {
        super("wireless_card");
        this.container = container;
        background(
            UITexture.builder()
                .location(new ResourceLocation("appliedenergistics2", "textures/guis/wireless_network_manager.png"))
                .imageSize(256, 256)
                .subAreaXYWH(0, 0, 106, 232)
                .adaptable(4)
                .build());
        search = textField(64).hintText(tr("search_placeholder"))
            .hintColor(ColorUtils.craftingTreeRequest.getColor());
        dimension = textField(12);
        name = textField(32);
        child(search).child(dimension)
            .child(name);
        for (int id = 0; id < 10; id++) if (id != 8) addButton(id);
        for (int row = 0; row < WirelessCardLayout.MAX_ROWS; row++) {
            for (int base : new int[] { 10, 20, 30, 40, 50 }) addButton(base + row);
        }
    }

    public ModularScreen screen() {
        return new ModularScreen(ScienceNotLeisure.MODID, this) {

            @Override
            public void onResize(int width, int height) {
                resizeLayout(width, height);
                super.onResize(width, height);
            }

            @Override
            public boolean onKeyPressed(char character, int key) {
                if (WirelessCardInput.visualisationKey(key) && !editingText()) {
                    action(9);
                    return true;
                }
                if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
                    action(container.display.getBoolean("detail") ? 5 : 6);
                    return true;
                }
                return super.onKeyPressed(character, key);
            }

            @Override
            public boolean onMousePressed(int button) {
                if (button > 1 && WirelessCardInput.visualisationKey(button - 100) && !editingText()) {
                    action(9);
                    return true;
                }
                return super.onMousePressed(button);
            }
        };
    }

    private static String tr(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("gtnl.wireless.gui." + key, args);
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

    private void resizeLayout(int width, int height) {
        var layout = WirelessCardLayout.forScreen(width, height);
        xSize = layout.width();
        ySize = layout.height();
        visibleRows = layout.rows();
        layoutRetry = 0;
        size(xSize, ySize);
        int dx = xSize - 320;
        search.pos(10, 62)
            .size(124 + dx, 12);
        dimension.pos(148 + dx, 62)
            .size(34, 12);
        name.pos(44, 62)
            .size(192 + dx, 12);
        for (var button : buttons) {
            int id = button.id;
            if (id >= 10) {
                int y = ROW_TOP + id % 10 * ROW_HEIGHT;
                switch (id / 10) {
                    case 1 -> button.pos(xSize - 28, y + 4)
                        .size(16, 16);
                    case 2 -> button.pos(10, y)
                        .size(20, ROW_HEIGHT);
                    case 3 -> button.pos(xSize - 96, y + 2)
                        .size(30, 20);
                    case 4 -> button.pos(xSize - 62, y + 2)
                        .size(30, 20);
                    case 5 -> button.pos(xSize - 130, y + 2)
                        .size(30, 20);
                }
            } else switch (id) {
                case 0 -> button.pos(10, ySize - 27)
                    .size(18, 20);
                case 1 -> button.pos(76, ySize - 27)
                    .size(18, 20);
                case 2 -> button.pos(xSize - 28, 10)
                    .size(16, 16);
                case 3 -> button.pos(xSize - 76, ySize - 27)
                    .size(64, 20);
                case 4 -> button.pos(layout.footerInfoX(), ySize - 27)
                    .size(36, 20);
                case 5 -> button.pos(244 + dx, 59)
                    .size(64, 20);
                case 6 -> button.pos(264 + dx, 59)
                    .size(44, 20);
                case 7 -> button.pos(188 + dx, 59)
                    .size(72, 20);
                case 9 -> button.pos(layout.visualisationX(), ySize - 27)
                    .size(layout.visualisationWidth(), 20);
            }
        }
    }

    private TextFieldWidget textField(int max) {
        return new TextFieldWidget() {

            @Override
            public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> theme) {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                try {
                    drawRect(0, 0, getArea().width, getArea().height, 0xffa0a0a0);
                    drawRect(1, 1, getArea().width - 1, getArea().height - 1, 0xff000000);
                } finally {
                    GL11.glPopAttrib();
                }
            }

            @Override
            public void drawForeground(ModularGuiContext context) {}
        }.value(new StringValue(""))
            .setMaxLength(max)
            .setTextColor(ColorUtils.searchboxText.getColor())
            .padding(2, 0);
    }

    private void addButton(int id) {
        CardButton button = new CardButton(id);
        buttons.add(button);
        child(button);
    }

    private final class CardButton extends ButtonWidget<CardButton> {

        private final int id;
        private final TextRenderer text = new TextRenderer();
        private boolean available, on;
        private String label = "";

        CardButton(int id) {
            this.id = id;
            text.setShadow(true);
            background(IDrawable.EMPTY).hoverBackground(IDrawable.EMPTY);
            onMousePressed(button -> {
                if (button != 0 || !available) return false;
                action(id);
                return true;
            });
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> theme) {
            if (id >= 20 && id < 30) return;
            int width = getArea().width, height = getArea().height;
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            try {
                // MUI's text-field stencil leaves texturing disabled. Its drawables establish their own
                // render state, whereas GuiButton.drawButton assumes the vanilla GUI has already done so.
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                ScreenColor.setGuiColor();
                if (id == 2 || id >= 10 && id < 20) {
                    TOGGLE_BACKGROUND.draw(0, 0, width, height);
                    ScreenColor.resetGuiColor();
                    (on ? TOGGLE_ON : TOGGLE_OFF).draw(0, 0, width, height);
                } else {
                    BUTTON_TEXTURES[!available ? 0 : isHovering() ? 2 : 1].draw(0, 0, width, height);
                    text.setColor(!available ? 0xffa0a0a0 : isHovering() ? 0xffffffA0 : 0xffe0e0e0);
                    text.setAlignment(Alignment.Center, width, height);
                    text.drawSimple(fontRendererObj.trimStringToWidth(label, Math.max(0, width - 4)));
                }
            } finally {
                GL11.glPopAttrib();
            }
        }
    }

    private int contentWidth() {
        return xSize - 212;
    }

    private boolean layoutReady() {
        return container.display.hasKey("session") && container.display.getInteger("pageSize") == visibleRows;
    }

    private boolean editingText() {
        return container.display.getBoolean("detail") ? name.isFocused() : search.isFocused() || dimension.isFocused();
    }

    private void action(int id) {
        if (!layoutReady()) return;
        if (id >= 30 && id < 30 + visibleRows) {
            var row = container.display.getTagList("rows", 10)
                .getCompoundTagAt(id - 30);
            GuiScreen.setClipboardString(row.getString("position") + " / " + row.getString("side"));
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.copied"));
            return;
        }
        if (id == 7) filterState = (filterState + 1) % 5;
        String text = id == 5 ? name.getText() : search.getText();
        if (id == 5) getContext().removeFocus();
        container.sync.request(id == 7 ? 6 : id, text, dimension.getText(), filterState);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!layoutReady() && container.display.hasKey("session") && layoutRetry-- <= 0) {
            container.sync.request(8, "", "", visibleRows);
            layoutRetry = 10;
        }
        NBTTagCompound data = container.display;
        boolean detail = data.getBoolean("detail");
        if (detail ? search.isFocused() || dimension.isFocused() : name.isFocused()) getContext().removeFocus();
        if (detail && (!editTarget.equals(data.getString("detailTarget"))
            || !name.isFocused() && !serverName.equals(data.getString("name")))) {
            editTarget = data.getString("detailTarget");
            serverName = data.getString("name");
            String text = data.getBoolean("multipleNames") ? "" : serverName;
            name.getStringValue()
                .setStringValue(text);
            name.setText(text);
        }
        if (!detail) editTarget = "";
        search.setEnabled(!detail);
        dimension.setEnabled(!detail);
        name.setEnabled(detail);
        NBTTagList rows = data.getTagList("rows", 10);
        for (var button : buttons) {
            int id = button.id;
            boolean visible = id < 10 || id % 10 < Math.min(visibleRows, rows.tagCount());
            button.available = layoutReady();
            if (id == 2) button.on = data.getBoolean("auto");
            if (id == 4 || id == 5) visible = detail;
            if (id == 6 || id == 7) visible = !detail;
            if (id >= 20 && id < 30) visible &= !detail;
            if (id >= 10 && id < 20) button.on = !rows.getCompoundTagAt(id - 10)
                .getBoolean("paused");
            if (id == 0) button.available &= data.getInteger("page") > 0;
            if (id == 1) button.available &= data.getInteger("page") + 1 < data.getInteger("pages");
            button.label = switch (id) {
                case 0 -> "<";
                case 1 -> ">";
                case 3 -> tr("refresh");
                case 4 -> tr("back_compact");
                case 5 -> tr("save_name");
                case 6 -> tr("search");
                case 7 -> tr("filter." + filterState);
                default -> id >= 50 ? tr("teleport") : id >= 40 ? tr("highlight") : id >= 30 ? tr("copy") : "";
            };
            if (id == 9) {
                int mode = data.getInteger("visualMode");
                String label = StatCollector
                    .translateToLocal(WirelessCardVisualisation.modeKey(mode) + (mode > 0 ? ".short" : ""));
                button.label = fontRendererObj.trimStringToWidth(
                    button.getArea().width < 102 ? label : tr("visualisation_button", label),
                    button.getArea().width - 8);
            }
            button.setEnabled(visible);
        }
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> theme) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            int count = Math.min(
                visibleRows,
                container.display.getTagList("rows", 10)
                    .tagCount());
            for (int row = 0; row < count; row++) {
                int y = ROW_TOP + ROW_HEIGHT - 1 + row * ROW_HEIGHT;
                drawRect(8, y, xSize - 8, y + 1, ColorUtils.craftingDiagnosticTerminalLine.getColor());
            }
            drawForeground();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static String entranceUsage(NBTTagCompound data, NBTTagCompound row) {
        if (!row.getBoolean("runtime")) return tr("no_runtime");
        if (!data.getBoolean("channels")) return tr("usage_disabled");
        return row.getInteger("usedChannels") < 0 ? tr("usage_waiting") : tr("usage", row.getInteger("usedChannels"));
    }

    private static int goodColor() {
        return ColorUtils.craftConfirmPercent50.getColor();
    }

    private static int badColor() {
        return ColorUtils.reshuffleStatusFailed.getColor();
    }

    private static int waitingColor() {
        return ColorUtils.craftConfirmPercent25.getColor();
    }

    private static int stateColor(NBTTagCompound row) {
        if (row.getBoolean("paused") || row.getBoolean("conflicted")) return badColor();
        if (row.getBoolean("stable") && row.getInteger("missing") > 0) return badColor();
        if (!row.getBoolean("stable") || !row.getString("state")
            .equals("active")) return waitingColor();
        // A live entrance is not proof of power or active devices.
        return row.getInteger("devices") > 0 && row.getInteger("online") == row.getInteger("devices") ? goodColor()
            : ColorUtils.guiTextColorGray.getColor();
    }

    private static String budgetText(NBTTagCompound data) {
        return !data.getBoolean("bound") ? tr("bind_hint")
            : !data.getBoolean("sourceLoaded") ? tr("source_waiting")
                : !data.getBoolean("channels") ? tr("unlimited")
                    : tr(
                        "budget_compact",
                        data.getInteger("used") < 0 ? "?" : data.getInteger("used"),
                        data.getInteger("capacity"));
    }

    private int textSegment(String text, int x, int y, int end, int color) {
        String fitted = fontRendererObj.trimStringToWidth(text, Math.max(0, end - x));
        fontRendererObj.drawString(fitted, x, y, color);
        return x + fontRendererObj.getStringWidth(fitted);
    }

    private void drawMetrics(NBTTagCompound data, int x, int y, int width, boolean compact) {
        int end = x + width;
        int textColor = ColorUtils.guiTextColorGray.getColor();
        int online = data.getInteger("online"), devices = data.getInteger("devices");
        int onlineColor = data.getBoolean("paused") ? badColor()
            : !data.getBoolean("stable") ? waitingColor() : online == devices && devices > 0 ? goodColor() : textColor;
        x = textSegment(tr(compact ? "online_compact" : "online_metric", online, devices), x, y, end, onlineColor);
        int missingColor = !data.getBoolean("stable") ? waitingColor()
            : data.getInteger("missing") > 0 ? badColor() : textColor;
        x = textSegment(
            "  " + tr(
                compact ? "missing_compact" : "missing_metric",
                data.getBoolean("stable") ? data.getInteger("missing") : "?"),
            x,
            y,
            end,
            missingColor);
        textSegment(
            "  " + tr(compact ? "entrances_compact" : "entrances", data.getInteger("entrances")),
            x,
            y,
            end,
            textColor);
    }

    private void ratioBar(int x, int y, int width, int height, int value, int total, int fill, int remainder) {
        drawRect(x, y, x + width, y + height, remainder | 0xFF000000);
        if (value < 0 || total <= 0) return;
        int filled = (int) Math.round(width * Math.max(0.0, Math.min(1.0, (double) value / total)));
        if (filled > 0) drawRect(x, y, x + filled, y + height, fill | 0xFF000000);
    }

    private void drawDeviceIcon(ItemStack icon, int x, int y) {
        float brightnessX = OpenGlHelper.lastBrightnessX;
        float brightnessY = OpenGlHelper.lastBrightnessY;
        var itemRender = GuiScreenAccessor.getItemRender();
        float renderZ = itemRender.zLevel;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glColor4f(1, 1, 1, 1);
            // Use MUI's item layer and Forge glint cleanup, while isolating custom item renderers
            // from the following text, rows and NEI. The icon remains 16x16 GUI pixels.
            GuiDraw.drawItem(icon, x, y, 16, 16, getContext().getCurrentDrawingZ());
        } finally {
            itemRender.zLevel = renderZ;
            GL11.glPopAttrib();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
        }
    }

    private void drawEmptyMessage(NBTTagCompound data, int color) {
        List<String> lines = fontRendererObj.listFormattedStringToWidth(
            tr(data.getBoolean("filtered") ? "no_matches" : "empty"),
            Math.min(300, xSize - 48));
        int lineHeight = fontRendererObj.FONT_HEIGHT + 3;
        int y = ROW_TOP + (visibleRows * ROW_HEIGHT - lines.size() * lineHeight) / 2;
        for (String line : lines) {
            fontRendererObj.drawString(line, (xSize - fontRendererObj.getStringWidth(line)) / 2, y, color);
            y += lineHeight;
        }
    }

    private void drawForeground() {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Platform.setupDrawFont();
        NBTTagCompound data = container.display;
        int textColor = ColorUtils.guiTextColorGray.getColor();
        fontRendererObj.drawString(
            fontRendererObj.trimStringToWidth(tr(data.getBoolean("detail") ? "detail_title" : "title"), xSize - 140),
            10,
            11,
            textColor);
        String autoLabel = fontRendererObj.trimStringToWidth(tr(data.getBoolean("auto") ? "auto_on" : "auto_off"), 94);
        fontRendererObj.drawString(
            autoLabel,
            xSize - 32 - fontRendererObj.getStringWidth(autoLabel),
            10 + (16 - fontRendererObj.FONT_HEIGHT) / 2,
            textColor);
        String source = data.getBoolean("bound") ? tr("source", data.getString("source")) : tr("unbound");
        fontRendererObj.drawString(fontRendererObj.trimStringToWidth(source, xSize - 20), 10, 25, textColor);
        boolean budgetKnown = data.getBoolean("sourceLoaded") && data.getBoolean("channels")
            && data.getBoolean("stable")
            && data.getInteger("used") >= 0
            && data.getInteger("capacity") > 0;
        int budgetColor = !budgetKnown ? textColor
            : data.getInteger("used") >= data.getInteger("capacity") ? badColor() : goodColor();
        fontRendererObj
            .drawString(fontRendererObj.trimStringToWidth(budgetText(data), xSize - 116), 10, 36, budgetColor);
        ratioBar(
            xSize - 96,
            37,
            84,
            6,
            budgetKnown ? data.getInteger("used") : -1,
            data.getInteger("capacity"),
            budgetColor,
            ColorUtils.craftingTreeTask.getColor());
        drawMetrics(data, 10, 47, xSize - 20, false);
        fontRendererObj.drawString(
            data.getBoolean("detail") ? tr("name_label") : "D",
            data.getBoolean("detail") ? 10 : xSize - 182,
            64,
            textColor);
        NBTTagList rows = data.getTagList("rows", 10);
        for (int index = 0; index < Math.min(visibleRows, rows.tagCount()); index++) {
            NBTTagCompound row = rows.getCompoundTagAt(index);
            int y = ROW_TOP + index * ROW_HEIGHT;
            ItemStack icon = ItemStack.loadItemStackFromNBT(row.getCompoundTag("icon"));
            if (icon != null) drawDeviceIcon(icon, 12, y + 4);
            String title = data.getBoolean("detail") || row.getString("name")
                .isEmpty() ? row.getString("position") : row.getString("name");
            fontRendererObj.drawString(
                fontRendererObj.trimStringToWidth(title, contentWidth()),
                CONTENT_X,
                y + 2,
                ColorUtils.guiTextColorBlack.getColor());
            String state = tr("short_state." + row.getString("state"));
            fontRendererObj.drawString(
                fontRendererObj.trimStringToWidth(state, 36),
                xSize - 172,
                y + (ROW_HEIGHT - fontRendererObj.FONT_HEIGHT) / 2,
                stateColor(row));
            if (data.getBoolean("detail")) {
                fontRendererObj.drawString(
                    fontRendererObj.trimStringToWidth(entranceUsage(data, row), contentWidth()),
                    CONTENT_X,
                    y + 13,
                    stateColor(row));
            } else {
                drawMetrics(row, CONTENT_X, y + 13, contentWidth(), true);
                int missingColor = row.getBoolean("stable") && row.getInteger("missing") > 0 ? badColor()
                    : ColorUtils.craftingTreeTask.getColor();
                ratioBar(
                    xSize - 172,
                    y + 19,
                    36,
                    2,
                    row.getBoolean("stable") ? row.getInteger("online") : -1,
                    row.getInteger("devices"),
                    goodColor(),
                    missingColor);
            }
        }
        if (rows.tagCount() == 0) drawEmptyMessage(data, textColor);
        String page = fontRendererObj
            .trimStringToWidth((data.getInteger("page") + 1) + "/" + Math.max(1, data.getInteger("pages")), 40);
        fontRendererObj.drawString(page, 52 - fontRendererObj.getStringWidth(page) / 2, ySize - 20, textColor);
        if (!data.getBoolean("detail")) {
            var layout = new WirelessCardLayout(xSize, ySize, visibleRows);
            fontRendererObj.drawString(
                fontRendererObj.trimStringToWidth(
                    tr(data.getBoolean("filtered") ? "matched" : "loaded", data.getInteger("total")),
                    layout.visualisationX() - layout.footerInfoX() - 4),
                layout.footerInfoX(),
                ySize - 20,
                textColor);
        }
    }
}
