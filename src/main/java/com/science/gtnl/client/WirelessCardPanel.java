package com.science.gtnl.client;

import static com.science.gtnl.common.wireless.WirelessCardActions.BACK;
import static com.science.gtnl.common.wireless.WirelessCardActions.COPY_POSITION;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_VISUALISATION;
import static com.science.gtnl.common.wireless.WirelessCardActions.HIGHLIGHT;
import static com.science.gtnl.common.wireless.WirelessCardActions.NEXT_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.PREVIOUS_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.REFRESH;
import static com.science.gtnl.common.wireless.WirelessCardActions.TELEPORT;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_PAUSE;
import static com.science.gtnl.common.wireless.WirelessCardActions.isRowAction;
import static com.science.gtnl.common.wireless.WirelessCardActions.rowIndex;
import static com.science.gtnl.common.wireless.WirelessCardActions.type;
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

import com.cleanroommc.modularui.core.mixins.early.minecraft.GuiScreenAccessor;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.wireless.WirelessCardContainer;
import com.science.gtnl.common.wireless.WirelessCardLayout;

import appeng.core.localization.ColorUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class WirelessCardPanel extends ModularPanel {

    public static final int ROW_HEIGHT = 24, CONTENT_X = 34;
    public final Minecraft mc = Minecraft.getMinecraft();
    public final FontRenderer fontRendererObj = mc.fontRenderer;
    public final WirelessCardContainer container;
    public final List<WirelessCardButton> buttons = new ArrayList<>();
    public NBTTagCompound snapshot = new NBTTagCompound();
    public int xSize = 320, ySize = 232, visibleRows = WirelessCardLayout.MIN_ROWS;

    WirelessCardPanel(String name, WirelessCardContainer container) {
        super(name);
        this.container = container;
        background(
            UITexture.builder()
                .location(new ResourceLocation("appliedenergistics2", "textures/guis/wireless_network_manager.png"))
                .imageSize(256, 256)
                .subAreaXYWH(0, 0, 106, 232)
                .adaptable(4)
                .build());
        for (int id : new int[] { PREVIOUS_PAGE, NEXT_PAGE, REFRESH, BACK }) addButton(id);
        addRowButtons(TOGGLE_PAUSE, COPY_POSITION, HIGHLIGHT, TELEPORT);
    }

    abstract boolean isDetailView();

    abstract int rowTop();

    abstract void resizePanel(WirelessCardLayout layout);

    abstract void layoutButton(WirelessCardButton button, WirelessCardLayout layout);

    abstract boolean updatePanel();

    abstract void drawHeader(NBTTagCompound data, int color);

    abstract void confirm();

    boolean isGroupView() {
        return false;
    }

    boolean editingText() {
        return false;
    }

    void drawFooter(NBTTagCompound data, int color) {}

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public boolean onKeyPressed(char character, int key) {
        if (WirelessCardInput.visualisationKey(key) && !editingText()) {
            action(CYCLE_VISUALISATION);
            return true;
        }
        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
            confirm();
            return true;
        }
        return super.onKeyPressed(character, key);
    }

    public NBTTagCompound view() {
        // Freeze the overview behind a modal detail panel while only the active view is synchronized.
        return container.display.getBoolean("detail") == isDetailView() ? container.display : snapshot;
    }

    public boolean layoutReady() {
        return container.display.getBoolean("detail") == isDetailView() && container.display.hasKey("session")
            && view().getInteger("pageSize") == visibleRows;
    }

    public void resizeLayout(int width, int height) {
        var layout = WirelessCardLayout.forScreen(width, height);
        visibleRows = layout.rows();
        resizePanel(layout);
        size(xSize, ySize);
        for (var control : buttons) {
            int id = control.id;
            var button = control.widget;
            if (isRowAction(id)) {
                int y = rowTop() + rowIndex(id) * ROW_HEIGHT;
                switch (type(id)) {
                    case TOGGLE_PAUSE -> button.pos(xSize - 28, y + 4)
                        .size(16, 16);
                    case OPEN_DETAILS -> button.pos(10, y)
                        .size(rowStateX() - 10, ROW_HEIGHT);
                    case COPY_POSITION -> button.pos(xSize - 96, y + 2)
                        .size(30, 20);
                    case HIGHLIGHT -> button.pos(xSize - 62, y + 2)
                        .size(30, 20);
                    case TELEPORT -> button.pos(xSize - 130, y + 2)
                        .size(30, 20);
                }
            } else switch (id) {
                case PREVIOUS_PAGE -> button.pos(10, ySize - 27)
                    .size(18, 20);
                case NEXT_PAGE -> button.pos(76, ySize - 27)
                    .size(18, 20);
                case REFRESH -> button.pos(xSize - 76, ySize - 27)
                    .size(64, 20);
                case BACK -> button.pos(layout.footerInfoX(), ySize - 27)
                    .size(36, 20);
                default -> layoutButton(control, layout);
            }
        }
    }

    public final void addButton(int id) {
        var button = new WirelessCardButton(id, this);
        buttons.add(button);
        child(button.widget);
    }

    public final void addRowButtons(int... actions) {
        for (int row = 0; row < WirelessCardLayout.MAX_ROWS; row++) for (int action : actions) addButton(action + row);
    }

    public void action(int id) {
        if (!layoutReady()) return;
        snapshot = view();
        if (type(id) == COPY_POSITION && rowIndex(id) < visibleRows) {
            var row = view().getTagList("rows", 10)
                .getCompoundTagAt(rowIndex(id));
            GuiScreen.setClipboardString(
                row.getString("position") + (row.getBoolean("group") ? "" : " / " + row.getString("side")));
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.copied"));
            return;
        }
        sendAction(id);
    }

    public void sendAction(int id) {
        if (container.clientView()
            .navigate(id, isGroupView())) return;
        long target = isRowAction(id) ? view().getTagList("rows", 10)
            .getCompoundTagAt(rowIndex(id))
            .getLong("targetId") : 0;
        container.sync.request(type(id), target, "", 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (container.display.getBoolean("detail") == isDetailView()) snapshot = view();
        if (!updatePanel()) return;
        for (var button : buttons) button.update(view(), layoutReady(), visibleRows);
    }

    public static String tr(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("gtnl.wireless.gui." + key, args);
    }

    public TextFieldWidget textField(int max) {
        return new TextFieldWidget().background(
            new Rectangle().color(0xff000000),
            new Rectangle().color(0xffa0a0a0)
                .hollow(1))
            .setTooltipOverride(true)
            .value(new StringValue(""))
            .setMaxLength(max)
            .setTextColor(ColorUtils.searchboxText.getColor())
            .padding(2, 0);
    }

    int contentWidth() {
        return xSize - 212;
    }

    void drawRowMetrics(NBTTagCompound data, NBTTagCompound row, int y) {
        drawMetrics(row, CONTENT_X, y + 13, contentWidth(), true);
        int missingColor = row.getBoolean("stable") && row.getInteger("missing") > 0 ? badColor()
            : ColorUtils.craftingTreeTask.getColor();
        ratioBar(
            rowStateX(),
            y + 19,
            36,
            2,
            row.getBoolean("stable") ? row.getInteger("online") : -1,
            row.getInteger("devices"),
            goodColor(),
            missingColor);
    }

    static String entranceUsage(NBTTagCompound data, NBTTagCompound row) {
        if (!row.getBoolean("runtime")) return tr("no_runtime");
        if (!data.getBoolean("channels")) return tr("usage_disabled");
        return row.getInteger("usedChannels") < 0 ? tr("usage_waiting") : tr("usage", row.getInteger("usedChannels"));
    }

    static int goodColor() {
        return ColorUtils.craftConfirmPercent50.getColor();
    }

    static int badColor() {
        return ColorUtils.reshuffleStatusFailed.getColor();
    }

    static int waitingColor() {
        return ColorUtils.craftConfirmPercent25.getColor();
    }

    static int stateColor(NBTTagCompound row) {
        if (row.getBoolean("paused") || row.getBoolean("conflicted")) return badColor();
        if (row.getBoolean("stable") && row.getInteger("missing") > 0) return badColor();
        if (!row.getBoolean("stable") || !row.getString("state")
            .equals("active")) return waitingColor();
        // A live entrance is not proof of power or active devices.
        return row.getInteger("devices") > 0 && row.getInteger("online") == row.getInteger("devices") ? goodColor()
            : ColorUtils.guiTextColorGray.getColor();
    }

    static String budgetText(NBTTagCompound data) {
        return !data.getBoolean("bound") ? tr("bind_hint")
            : !data.getBoolean("sourceLoaded") ? tr("source_waiting")
                : !data.getBoolean("channels") ? tr("unlimited")
                    : tr(
                        "budget_compact",
                        data.getInteger("used") < 0 ? "?" : data.getInteger("used"),
                        data.getInteger("capacity"));
    }

    int textSegment(String text, int x, int y, int end, int color) {
        String fitted = fontRendererObj.trimStringToWidth(text, Math.max(0, end - x));
        fontRendererObj.drawString(fitted, x, y, color);
        return x + fontRendererObj.getStringWidth(fitted);
    }

    void drawMetrics(NBTTagCompound data, int x, int y, int width, boolean compact) {
        int end = x + width;
        String entrances = "  " + tr(
            compact ? "entrances_compact" : "entrances",
            compact ? data.getInteger("entrances")
                : data.getInteger("entrances") + " / " + data.getInteger("entranceLimit"));
        int metricsEnd = compact ? end : Math.max(x, end - fontRendererObj.getStringWidth(entrances));
        int textColor = ColorUtils.guiTextColorGray.getColor();
        int online = data.getInteger("online"), devices = data.getInteger("devices");
        int onlineColor = data.getBoolean("paused") ? badColor()
            : !data.getBoolean("stable") ? waitingColor() : online == devices && devices > 0 ? goodColor() : textColor;
        x = textSegment(
            tr(compact ? "online_compact" : "online_metric", online, devices),
            x,
            y,
            metricsEnd,
            onlineColor);
        int missingColor = !data.getBoolean("stable") ? waitingColor()
            : data.getInteger("missing") > 0 ? badColor() : textColor;
        x = textSegment(
            "  " + tr(
                compact ? "missing_compact" : "missing_metric",
                data.getBoolean("stable") ? data.getInteger("missing") : "?"),
            x,
            y,
            metricsEnd,
            missingColor);
        textSegment(entrances, x, y, end, textColor);
    }

    void ratioBar(int x, int y, int width, int height, int value, int total, int fill, int remainder) {
        drawRect(x, y, x + width, y + height, remainder | 0xFF000000);
        if (value < 0 || total <= 0) return;
        int filled = (int) Math.round(width * Math.max(0.0, Math.min(1.0, (double) value / total)));
        if (filled > 0) drawRect(x, y, x + filled, y + height, fill | 0xFF000000);
    }

    void drawDeviceIcon(ItemStack icon, int x, int y) {
        float brightnessX = OpenGlHelper.lastBrightnessX;
        float brightnessY = OpenGlHelper.lastBrightnessY;
        var itemRender = GuiScreenAccessor.getItemRender();
        float renderZ = itemRender.zLevel;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glColor4f(1, 1, 1, 1);
            // Isolate custom item renderers while retaining MUI's item layer and Forge glint cleanup.
            GuiDraw.drawItem(icon, x, y, 16, 16, getContext().getCurrentDrawingZ());
        } finally {
            itemRender.zLevel = renderZ;
            GL11.glPopAttrib();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
        }
    }

    void drawEmptyMessage(NBTTagCompound data, int color) {
        List<String> lines = fontRendererObj.listFormattedStringToWidth(
            tr(data.getBoolean("initializing") ? "initializing" : data.getBoolean("filtered") ? "no_matches" : "empty"),
            Math.min(300, xSize - 48));
        int lineHeight = fontRendererObj.FONT_HEIGHT + 3;
        int y = rowTop() + (visibleRows * ROW_HEIGHT - lines.size() * lineHeight) / 2;
        for (String line : lines) {
            fontRendererObj.drawString(line, (xSize - fontRendererObj.getStringWidth(line)) / 2, y, color);
            y += lineHeight;
        }
    }

    static String rowName(NBTTagCompound row, ItemStack icon) {
        String name = row.getString("name");
        if (name.isEmpty()) name = icon == null ? "" : icon.getDisplayName();
        return row.getBoolean("shared") ? tr("shared", name) : name;
    }

    int rowStateX() {
        return xSize - 172;
    }

    void drawRowTitle(NBTTagCompound data, NBTTagCompound row, ItemStack icon, int y) {
        String name = (row.getBoolean("group") ? "> " : "") + rowName(row, icon);
        String position = row.getString("positionLabel");
        String count = row.getBoolean("category") ? " ×" + row.getInteger("items")
            : row.getBoolean("group") ? " [" + row.getInteger("clusters") + "]" : "";
        int width = contentWidth() - fontRendererObj.getStringWidth(count);
        int nameColor = ColorUtils.guiTextColorBlack.getColor();
        if (data.getBoolean("detail") || name.isEmpty()) {
            fontRendererObj.drawString(fontRendererObj.trimStringToWidth(position, width), CONTENT_X, y, nameColor);
            return;
        }
        int nameReserve = Math.min(24, fontRendererObj.getStringWidth(name));
        String suffix = " ("
            + fontRendererObj
                .trimStringToWidth(position, Math.max(0, width - nameReserve - fontRendererObj.getStringWidth(" ()")))
            + ")";
        String title = fontRendererObj
            .trimStringToWidth(name, Math.max(0, width - fontRendererObj.getStringWidth(suffix)));
        fontRendererObj.drawString(title, CONTENT_X, y, nameColor);
        fontRendererObj.drawString(
            suffix,
            CONTENT_X + fontRendererObj.getStringWidth(title),
            y,
            ColorUtils.craftingTreeTask.getColor());
        if (!count.isEmpty()) fontRendererObj.drawString(
            count,
            CONTENT_X + fontRendererObj.getStringWidth(title) + fontRendererObj.getStringWidth(suffix),
            y,
            stateColor(row));
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> theme) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            int count = Math.min(
                visibleRows,
                view().getTagList("rows", 10)
                    .tagCount());
            for (int row = 0; row < count; row++) {
                int y = rowTop() + ROW_HEIGHT - 1 + row * ROW_HEIGHT;
                drawRect(8, y, xSize - 8, y + 1, ColorUtils.craftingDiagnosticTerminalLine.getColor());
            }
            drawForeground();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void drawForeground() {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Platform.setupDrawFont();
        NBTTagCompound data = view();
        int textColor = ColorUtils.guiTextColorGray.getColor();
        drawHeader(data, textColor);
        drawMetrics(data, 10, 47, xSize - 20, false);
        NBTTagList rows = data.getTagList("rows", 10);
        for (int index = 0; index < Math.min(visibleRows, rows.tagCount()); index++) {
            NBTTagCompound row = rows.getCompoundTagAt(index);
            int y = rowTop() + index * ROW_HEIGHT;
            ItemStack icon = ItemStack.loadItemStackFromNBT(row.getCompoundTag("icon"));
            if (icon != null) drawDeviceIcon(icon, 12, y + 4);
            drawRowTitle(data, row, icon, y + 2);
            String state = tr("short_state." + row.getString("state"));
            fontRendererObj.drawString(
                fontRendererObj.trimStringToWidth(state, 36),
                rowStateX(),
                y + (ROW_HEIGHT - fontRendererObj.FONT_HEIGHT) / 2,
                stateColor(row));
            drawRowMetrics(data, row, y);
        }
        if (rows.tagCount() == 0) drawEmptyMessage(data, textColor);
        String page = fontRendererObj
            .trimStringToWidth((data.getInteger("page") + 1) + "/" + Math.max(1, data.getInteger("pages")), 40);
        fontRendererObj.drawString(page, 52 - fontRendererObj.getStringWidth(page) / 2, ySize - 20, textColor);
        drawFooter(data, textColor);
    }

}
