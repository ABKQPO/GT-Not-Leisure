package com.science.gtnl.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.WirelessCardGuiPacket;
import com.science.gtnl.common.wireless.WirelessCardContainer;
import com.science.gtnl.common.wireless.WirelessCardLayout;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiToggleButton;
import appeng.core.localization.ColorUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class WirelessCardGui extends AEBaseGui {

    private static final int ROW_TOP = 82;
    private static final int ROW_HEIGHT = 24;
    private static final int CONTENT_X = 34;
    private int visibleRows = WirelessCardLayout.MIN_ROWS;
    private int layoutRetry;
    private final WirelessCardContainer container;
    private GuiTextField search, dimension, name;
    private int filterState;
    private String editTarget = "", serverName = "";

    public WirelessCardGui(EntityPlayer player, int location) {
        super(new WirelessCardContainer(player, location));
        container = (WirelessCardContainer) inventorySlots;
        xSize = 320;
        ySize = 232;
    }

    private static String tr(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("gtnl.wireless.gui." + key, args);
    }

    @Override
    public void initGui() {
        var layout = WirelessCardLayout.forScreen(width, height);
        xSize = layout.width();
        ySize = layout.height();
        visibleRows = layout.rows();
        layoutRetry = 0;
        super.initGui();
        buttonList.clear();
        int dx = xSize - 320;
        buttonList.add(toggleButton(2, 292 + dx, 10));
        for (int row = 0; row < visibleRows; row++) {
            int y = guiTop + ROW_TOP + 2 + row * ROW_HEIGHT;
            buttonList.add(toggleButton(10 + row, 292 + dx, ROW_TOP + 4 + row * ROW_HEIGHT));
            buttonList.add(new GuiButton(30 + row, guiLeft + actionX(), y, 30, 20, tr("copy")));
            buttonList.add(new GuiButton(40 + row, guiLeft + 258 + dx, y, 30, 20, tr("highlight")));
        }
        search = textField(search, 10, 62, 124 + dx, 64);
        dimension = textField(dimension, 148 + dx, 62, 34, 12);
        name = textField(name, 44, 62, 192 + dx, 32);
        buttonList.add(new GuiButton(5, guiLeft + 244 + dx, guiTop + 59, 64, 20, tr("save_name")));
        buttonList.add(new GuiButton(6, guiLeft + 264 + dx, guiTop + 59, 44, 20, tr("search")));
        buttonList.add(new GuiButton(7, guiLeft + 188 + dx, guiTop + 59, 72, 20, ""));
        int footer = guiTop + ySize - 27;
        buttonList.add(new GuiButton(0, guiLeft + 10, footer, 30, 20, "<"));
        buttonList.add(new GuiButton(1, guiLeft + 100, footer, 30, 20, ">"));
        buttonList.add(new GuiButton(3, guiLeft + 244 + dx, footer, 64, 20, tr("refresh")));
        buttonList.add(new GuiButton(4, guiLeft + 142, footer, 90, 20, tr("back")));
    }

    private int actionX() {
        return xSize - 96;
    }

    private int contentWidth() {
        return xSize - 198;
    }

    private boolean layoutReady() {
        return container.display.hasKey("session") && container.display.getInteger("pageSize") == visibleRows;
    }

    private GuiTextField textField(GuiTextField previous, int x, int y, int width, int max) {
        GuiTextField field = new GuiTextField(fontRendererObj, guiLeft + x, guiTop + y, width, 12);
        field.setMaxStringLength(max);
        field.setTextColor(ColorUtils.searchboxText.getColor());
        if (previous != null) field.setText(previous.getText());
        return field;
    }

    private GuiToggleButton toggleButton(int id, int x, int y) {
        GuiToggleButton button = new GuiToggleButton(guiLeft + x, guiTop + y, 129, 128, null, null) {

            @Override
            public String getMessage() {
                return displayString + (id >= 10 ? "\n" + tr("toggle_hint") : "");
            }
        };
        button.id = id;
        return button;
    }

    // This management screen retains its locked card container and has no interactive inventory slots.
    // Do not route inventory shortcuts through AEBaseGui's AEBaseContainer-specific packet handlers.
    @Override
    protected boolean enableSpaceClicking() {
        return false;
    }

    @Override
    protected boolean checkHotbarKeys(int keyCode) {
        return false;
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotIdx, int clickedButton, int clickType) {}

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!layoutReady()) return;
        if (button.id >= 30 && button.id < 30 + visibleRows) {
            var row = container.display.getTagList("rows", 10)
                .getCompoundTagAt(button.id - 30);
            setClipboardString(row.getString("position") + " / " + row.getString("side"));
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.copied"));
            return;
        }
        if (button.id == 7) filterState = (filterState + 1) % 5;
        if (button.id == 5) name.setFocused(false);
        ScienceNotLeisure.network.sendToServer(
            new WirelessCardGuiPacket(
                container.windowId,
                container.display,
                button.id == 7 ? 6 : button.id,
                button.id == 5 ? name.getText() : search.getText(),
                dimension.getText(),
                filterState));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        search.updateCursorCounter();
        dimension.updateCursorCounter();
        name.updateCursorCounter();
        if (!layoutReady() && container.display.hasKey("session") && layoutRetry-- <= 0) {
            ScienceNotLeisure.network
                .sendToServer(new WirelessCardGuiPacket(container.windowId, container.display, 8, "", "", visibleRows));
            layoutRetry = 10;
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        boolean detail = container.display.getBoolean("detail");
        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
            actionPerformed(new GuiButton(detail ? 5 : 6, 0, 0, ""));
            return;
        }
        if (key != Keyboard.KEY_ESCAPE && (detail ? name.textboxKeyTyped(character, key)
            : search.textboxKeyTyped(character, key) || dimension.textboxKeyTyped(character, key))) return;
        super.keyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (container.display.getBoolean("detail")) name.mouseClicked(mouseX, mouseY, button);
        else {
            search.mouseClicked(mouseX, mouseY, button);
            dimension.mouseClicked(mouseX, mouseY, button);
        }
        super.mouseClicked(mouseX, mouseY, button);
        int x = mouseX - guiLeft, y = mouseY - guiTop;
        if (button == 0 && layoutReady()
            && !container.display.getBoolean("detail")
            && x >= 10
            && x < 30
            && y >= ROW_TOP
            && y < ROW_TOP + visibleRows * ROW_HEIGHT) {
            int row = (y - ROW_TOP) / ROW_HEIGHT;
            if (row < container.display.getTagList("rows", 10)
                .tagCount())
                ScienceNotLeisure.network
                    .sendToServer(new WirelessCardGuiPacket(container.windowId, container.display, 20 + row));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        NBTTagCompound data = container.display;
        NBTTagList rows = data.getTagList("rows", 10);
        boolean detail = data.getBoolean("detail");
        if (detail && (!editTarget.equals(data.getString("detailTarget"))
            || !name.isFocused() && !serverName.equals(data.getString("name")))) {
            editTarget = data.getString("detailTarget");
            serverName = data.getString("name");
            name.setText(data.getBoolean("multipleNames") ? "" : serverName);
        }
        if (!detail) editTarget = "";
        for (Object object : buttonList) {
            GuiButton button = (GuiButton) object;
            button.enabled = layoutReady();
            if (button.id == 2) {
                button.displayString = tr(data.getBoolean("auto") ? "auto_on" : "auto_off");
                ((GuiToggleButton) button).setState(data.getBoolean("auto"));
            }
            if (button.id == 4) button.visible = data.getBoolean("detail");
            if (button.id == 5) button.visible = detail;
            if (button.id == 6 || button.id == 7) button.visible = !detail;
            if (button.id == 7) button.displayString = tr("filter." + filterState);
            if (button.id >= 30) button.visible = button.id % 10 < rows.tagCount();
            if (button.id >= 10 && button.id < 20) {
                int row = button.id - 10;
                button.visible = row < rows.tagCount();
                button.displayString = tr(
                    rows.getCompoundTagAt(row)
                        .getBoolean("paused") ? "resume" : "pause");
                ((GuiToggleButton) button).setState(
                    !rows.getCompoundTagAt(row)
                        .getBoolean("paused"));
            }
            if (button.id == 0) button.enabled &= data.getInteger("page") > 0;
            if (button.id == 1) button.enabled &= data.getInteger("page") + 1 < data.getInteger("pages");
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        int localX = mouseX - guiLeft, localY = mouseY - guiTop;
        if (localX >= 10 && localX < actionX() - 2
            && localY >= ROW_TOP
            && localY < ROW_TOP + visibleRows * ROW_HEIGHT) {
            int index = (localY - ROW_TOP) / ROW_HEIGHT;
            if (index < rows.tagCount()) {
                NBTTagCompound row = rows.getCompoundTagAt(index);
                ItemStack icon = ItemStack.loadItemStackFromNBT(row.getCompoundTag("icon"));
                List<String> lines = new ArrayList<>();
                if (!row.getString("name")
                    .isEmpty()) lines.add(row.getString("name"));
                if (row.getBoolean("multipleNames")) lines.add(tr("multiple_names"));
                lines.add(icon == null ? tr("unknown") : icon.getDisplayName());
                lines.add(row.getString("position") + " / " + row.getString("side"));
                lines.add(tr("counts", row.getInteger("entrances"), row.getInteger("nodes")));
                lines.add(deviceStatus(row));
                if (!detail && row.getInteger("devices") > 0) lines.add(tr("online_bar_hint"));
                lines.add(tr("consumer_hint"));
                if (row.getBoolean("runtime")) lines.add(entranceUsage(data, row));
                if (!row.getBoolean("stable")) lines.add(tr("stats_unstable"));
                lines.add(tr("state." + row.getString("state")));
                if (row.getBoolean("paused") && row.getBoolean("conflicted")) lines.add(tr("state.conflict"));
                lines.add(tr(row.getBoolean("runtime") ? "runtime_hint" : "cluster_hint"));
                if (!data.getBoolean("detail")) lines.add(tr("detail_hint"));
                tooltip(lines, mouseX, mouseY);
            }
        }
        if (localY >= 59 && localY < 79
            && localX >= 8
            && localX < xSize - 10
            && (detail ? !name.isFocused() : !search.isFocused() && !dimension.isFocused())) {
            tooltip(List.of(tr(detail ? "name_hint" : "search_hint")), mouseX, mouseY);
        }
        if (localY >= ROW_TOP && localY < ROW_TOP + ROW_HEIGHT * Math.min(visibleRows, rows.tagCount())
            && localX >= xSize - 62
            && localX < xSize - 32
            && (localY - ROW_TOP) % ROW_HEIGHT >= 2
            && (localY - ROW_TOP) % ROW_HEIGHT < 22) tooltip(List.of(tr("highlight_hint")), mouseX, mouseY);
        if (localX >= 10 && localX < xSize - 12 && localY >= 35 && localY < 45)
            tooltip(List.of(budgetText(data, false), tr("budget_bar_hint")), mouseX, mouseY);
    }

    private void tooltip(List<String> lines, int mouseX, int mouseY) {
        List<String> wrapped = new ArrayList<>();
        for (String line : lines)
            wrapped.addAll(fontRendererObj.listFormattedStringToWidth(line, Math.min(300, width - 24)));
        drawHoveringText(wrapped, mouseX, mouseY, fontRendererObj);
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        bindTexture("guis/wireless_network_manager.png");
        drawTextured9PatchRect(offsetX, offsetY, xSize, ySize, 0, 0, 106, 232);
        int count = Math.min(
            visibleRows,
            container.display.getTagList("rows", 10)
                .tagCount());
        for (int row = 0; row < count; row++) {
            int y = offsetY + ROW_TOP + ROW_HEIGHT - 1 + row * ROW_HEIGHT;
            drawRect(offsetX + 8, y, offsetX + xSize - 8, y + 1, ColorUtils.craftingDiagnosticTerminalLine.getColor());
        }
        if (container.display.getBoolean("detail")) name.drawTextBox();
        else {
            search.drawTextBox();
            dimension.drawTextBox();
        }
    }

    private static String deviceStatus(NBTTagCompound data) {
        return tr(
            "device_status",
            data.getInteger("online"),
            data.getInteger("devices"),
            data.getBoolean("stable") ? data.getInteger("missing") : "?");
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

    private static String budgetText(NBTTagCompound data, boolean compact) {
        return !data.getBoolean("bound") ? tr("bind_hint")
            : !data.getBoolean("sourceLoaded") ? tr("source_waiting")
                : !data.getBoolean("channels") ? tr("unlimited")
                    : tr(
                        compact ? "budget_compact" : "budget",
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

    /** A bounded ratio, never an inferred per-entrance capacity. Unknown and empty values show only the track. */
    private void ratioBar(int x, int y, int width, int height, int value, int total, int fill, int remainder) {
        drawRect(x, y, x + width, y + height, remainder | 0xFF000000);
        if (value < 0 || total <= 0) return;
        int filled = (int) Math.round(width * Math.max(0.0, Math.min(1.0, (double) value / total)));
        if (filled > 0) drawRect(x, y, x + filled, y + height, fill | 0xFF000000);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        // GuiContainer and NEI continue rendering after this hook; preserve their incoming GL state.
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            drawForeground();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private void drawDeviceIcon(ItemStack icon, int x, int y) {
        float brightnessX = OpenGlHelper.lastBrightnessX;
        float brightnessY = OpenGlHelper.lastBrightnessY;
        float renderZ = itemRender.zLevel;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glColor4f(1, 1, 1, 1);
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), icon, x, y);
        } finally {
            itemRender.zLevel = renderZ;
            GL11.glPopMatrix();
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
            .drawString(fontRendererObj.trimStringToWidth(budgetText(data, true), xSize - 116), 10, 36, budgetColor);
        ratioBar(
            actionX(),
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
        if (!data.getBoolean("detail") && search.getText()
            .isEmpty() && !search.isFocused())
            fontRendererObj.drawString(tr("search_placeholder"), 12, 64, ColorUtils.craftingTreeRequest.getColor());
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
                fontRendererObj.trimStringToWidth(state, 56),
                xSize - 158,
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
                    xSize - 158,
                    y + 19,
                    56,
                    2,
                    row.getBoolean("stable") ? row.getInteger("online") : -1,
                    row.getInteger("devices"),
                    goodColor(),
                    missingColor);
            }
        }
        if (rows.tagCount() == 0) drawEmptyMessage(data, textColor);
        fontRendererObj.drawString(
            (data.getInteger("page") + 1) + " / " + Math.max(1, data.getInteger("pages")),
            48,
            ySize - 20,
            textColor);
        if (!data.getBoolean("detail")) fontRendererObj.drawString(
            tr(data.getBoolean("filtered") ? "matched" : "loaded", data.getInteger("total")),
            142,
            ySize - 20,
            textColor);
    }
}
