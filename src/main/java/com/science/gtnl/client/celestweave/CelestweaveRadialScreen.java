package com.science.gtnl.client.celestweave;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.celestweave.CelestweaveServerEvents;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveRadialScreen extends GuiScreen {

    private static final float INNER = 28F, OUTER = 96F;
    private static final int SECTORS = 4;
    private static final double GAP = 0.02;
    private static final int[] PIECE = { 0, 1, 3, 2 };

    private final KeyBinding key;
    private int hovered = -1;

    public CelestweaveRadialScreen(KeyBinding key) {
        this.key = key;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private ItemStack piece(int type) {
        return mc.thePlayer == null ? null : CelestweaveServerEvents.piece(mc.thePlayer, type);
    }

    private static double center(int sector) {
        return Math.PI * 2 * sector / SECTORS - Math.PI / 2;
    }

    private int sectorAt(int mouseX, int mouseY) {
        double dx = mouseX - width / 2.0, dy = mouseY - height / 2.0;
        if (dx * dx + dy * dy < INNER * INNER) return -1;
        double angle = Math.atan2(dy, dx) + Math.PI / 2 + Math.PI / SECTORS;
        angle = (angle % (Math.PI * 2) + Math.PI * 2) % (Math.PI * 2);
        int sector = (int) (angle / (Math.PI * 2) * SECTORS) % SECTORS;
        return piece(PIECE[sector]) != null ? sector : -1;
    }

    private void open(int sector) {
        if (sector < 0 || piece(PIECE[sector]) == null) return;
        ScienceNotLeisure.network
            .sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.OPEN_PIECE, PIECE[sector]));
    }

    @Override
    public void updateScreen() {
        if (mc.thePlayer == null || !CelestweaveServerEvents.wearsAny(mc.thePlayer)) {
            mc.displayGuiScreen(null);
            return;
        }
        if (!GameSettings.isKeyDown(key)) {
            int sector = hovered;
            mc.displayGuiScreen(null);
            open(sector);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        int sector = sectorAt(mouseX, mouseY);
        if (button != 0 || sector < 0) return;
        mc.displayGuiScreen(null);
        open(sector);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        hovered = sectorAt(mouseX, mouseY);
        float cx = width / 2F, cy = height / 2F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);
        Tessellator tessellator = Tessellator.instance;
        for (int i = 0; i < SECTORS; i++) {
            double start = center(i) - Math.PI / SECTORS + GAP, end = center(i) + Math.PI / SECTORS - GAP;
            int segments = 16;
            tessellator.startDrawingQuads();
            if (i == hovered) tessellator.setColorRGBA(255, 61, 154, 160);
            else if (piece(PIECE[i]) != null) tessellator.setColorRGBA(20, 20, 30, 150);
            else tessellator.setColorRGBA(20, 20, 30, 70);
            for (int s = 0; s < segments; s++) {
                double a0 = start + (end - start) * s / segments, a1 = start + (end - start) * (s + 1) / segments;
                tessellator.addVertex(cx + Math.cos(a0) * INNER, cy + Math.sin(a0) * INNER, 0);
                tessellator.addVertex(cx + Math.cos(a0) * OUTER, cy + Math.sin(a0) * OUTER, 0);
                tessellator.addVertex(cx + Math.cos(a1) * OUTER, cy + Math.sin(a1) * OUTER, 0);
                tessellator.addVertex(cx + Math.cos(a1) * INNER, cy + Math.sin(a1) * INNER, 0);
            }
            tessellator.draw();
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        float radius = (INNER + OUTER) / 2F;
        for (int i = 0; i < SECTORS; i++) {
            ItemStack stack = piece(PIECE[i]);
            int x = (int) (cx + Math.cos(center(i)) * radius), y = (int) (cy + Math.sin(center(i)) * radius);
            String label = (stack != null ? "" : "§8")
                + StatCollector.translateToLocal("item.gtnl.celestweave.radial.piece." + PIECE[i]);
            fontRendererObj.drawStringWithShadow(label, x - fontRendererObj.getStringWidth(label) / 2, y + 6, 0xFFFFFF);
            if (stack == null) continue;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), stack, x - 8, y - 14);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        String title = StatCollector.translateToLocal("item.gtnl.celestweave.radial.title");
        fontRendererObj.drawStringWithShadow(
            title,
            (int) cx - fontRendererObj.getStringWidth(title) / 2,
            (int) (cy + OUTER + 8),
            0xFFFFFF);
        GL11.glDisable(GL11.GL_BLEND);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
