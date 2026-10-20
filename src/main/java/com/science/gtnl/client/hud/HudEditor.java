package com.science.gtnl.client.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;

import com.cleanroommc.modularui.factory.ClientGUI;
import com.science.gtnl.client.hud.HudLayout.Placement;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HudEditor extends GuiScreen {

    private static final String LANG = "gtnl.hud.editor.";
    private static final int RESET = 0, DONE = 1, SHOW_ALL = 2, SNAP = 4, BUTTON_WIDTH = 100, BUTTON_HEIGHT = 20,
        BUTTON_Y = 22, BUTTON_GAP = 4;
    private static final int SHADE = 0x30000000, GUIDE = 0x80FF3D9A, FRAME = 0x60FFFFFF, ACTIVE = 0xFFFF3D9A,
        HIDDEN = 0x80808080;

    private HudElement dragging;
    private float grabX, grabY;
    private boolean snapX, snapY, showAll;

    public static void open() {
        ClientGUI.open(new HudEditor());
    }

    public static boolean isOpen() {
        return Minecraft.getMinecraft().currentScreen instanceof HudEditor;
    }

    public static List<HudElement> candidates(boolean showAll) {
        List<HudElement> candidates = new ArrayList<>();
        for (HudElement element : HudRegistry.elements()) if (showAll || element.isInUse()) candidates.add(element);
        return candidates;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int left = width / 2 - (BUTTON_WIDTH * 3 + BUTTON_GAP * 2) / 2, stride = BUTTON_WIDTH + BUTTON_GAP;
        buttonList.add(new GuiButton(SHOW_ALL, left, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, showAllLabel()));
        buttonList.add(new GuiButton(RESET, left + stride, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, tr("reset")));
        buttonList.add(new GuiButton(DONE, left + stride * 2, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, tr("done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == RESET) HudLayout.reset();
        else if (button.id == DONE) mc.displayGuiScreen(null);
        else if (button.id == SHOW_ALL) {
            showAll = !showAll;
            button.displayString = showAllLabel();
        }
    }

    private String showAllLabel() {
        return tr(showAll ? "in_use_only" : "show_all");
    }

    @Override
    public void onGuiClosed() {
        HudLayout.save();
    }

    private Placement place(HudElement element) {
        return HudLayout.preview(element, width, height);
    }

    private float[] frame(HudElement element) {
        Placement place = place(element);
        float scale = place.scale();
        return new float[] { place.x() + element.minX * scale, place.y() + element.minY * scale,
            place.x() + element.maxX * scale, place.y() + element.maxY * scale };
    }

    private HudElement elementAt(int mouseX, int mouseY) {
        HudElement best = null;
        float smallest = Float.MAX_VALUE;
        for (HudElement element : candidates(showAll)) {
            float[] frame = frame(element);
            if (mouseX < frame[0] || mouseX > frame[2] || mouseY < frame[1] || mouseY > frame[3]) continue;
            float area = (frame[2] - frame[0]) * (frame[3] - frame[1]);
            if (area < smallest) {
                smallest = area;
                best = element;
            }
        }
        return best;
    }

    private boolean overButton(int mouseX, int mouseY) {
        for (Object button : buttonList) if (((GuiButton) button).mousePressed(mc, mouseX, mouseY)) return true;
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (overButton(mouseX, mouseY)) return;
        HudElement element = elementAt(mouseX, mouseY);
        if (element == null) return;
        if (button == 0) {
            Placement place = place(element);
            dragging = element;
            grabX = mouseX - place.x();
            grabY = mouseY - place.y();
        } else if (button == 1) HudLayout.toggle(element);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long held) {
        if (dragging == null || button != 0) return;
        float scale = place(dragging).scale();
        float x = HudLayout.clamp(mouseX - grabX, -dragging.minX * scale, width - dragging.maxX * scale);
        float y = HudLayout.clamp(mouseY - grabY, -dragging.minY * scale, height - dragging.maxY * scale);
        snapX = Math.abs(x - width / 2F) <= SNAP;
        snapY = Math.abs(y - height / 2F) <= SNAP;
        HudLayout.move(dragging, snapX ? width / 2F : x, snapY ? height / 2F : y, width, height);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if (which != 0) return;
        dragging = null;
        snapX = snapY = false;
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0 || dragging != null) return;
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        HudElement element = elementAt(mouseX, mouseY);
        if (element != null) HudLayout.rescale(element, wheel > 0 ? 1 : -1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, SHADE);
        if (snapX) drawRect(width / 2, 0, width / 2 + 1, height, GUIDE);
        if (snapY) drawRect(0, height / 2, width, height / 2 + 1, GUIDE);
        HudElement focus = dragging != null ? dragging : elementAt(mouseX, mouseY);
        List<HudElement> candidates = candidates(showAll);
        for (HudElement element : candidates) {
            boolean hidden = HudLayout.hidden(element);
            if (!hidden && mc.thePlayer != null) element.preview(place(element), partialTicks);
            outline(frame(element), element == focus ? ACTIVE : hidden ? HIDDEN : FRAME);
        }
        if (focus != null) label(focus);
        if (candidates.isEmpty()) drawCenteredString(fontRendererObj, tr("empty"), width / 2, height / 2, 0xFFFFFF);
        drawCenteredString(fontRendererObj, tr("help"), width / 2, 8, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void outline(float[] frame, int color) {
        int x0 = Math.round(frame[0]), y0 = Math.round(frame[1]), x1 = Math.round(frame[2]), y1 = Math.round(frame[3]);
        drawRect(x0, y0, x1, y0 + 1, color);
        drawRect(x0, y1 - 1, x1, y1, color);
        drawRect(x0, y0, x0 + 1, y1, color);
        drawRect(x1 - 1, y0, x1, y1, color);
    }

    private void label(HudElement element) {
        String text = StatCollector.translateToLocal(element.lang()) + " ×"
            + String.format(Locale.ROOT, "%.1f", HudLayout.scale(element));
        if (HudLayout.hidden(element)) text += " " + tr("hidden");
        float[] frame = frame(element);
        int x = Math.round((frame[0] + frame[2]) / 2), y = Math.round(frame[1]) - 11;
        if (y < BUTTON_Y + BUTTON_HEIGHT + 2) y = Math.round(frame[3]) + 3;
        drawCenteredString(fontRendererObj, text, x, y, 0xFFFFFF);
    }

    private static String tr(String key) {
        return StatCollector.translateToLocal(LANG + key);
    }
}
