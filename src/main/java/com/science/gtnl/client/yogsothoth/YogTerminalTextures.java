package com.science.gtnl.client.yogsothoth;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.cleanroommc.modularui.drawable.UITexture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class YogTerminalTextures {

    private static final String SHEET = "gui/yog_sothoth_terminal";
    static final UITexture BACKGROUND = sprite(0, 0, 16, 16, 4);
    static final UITexture SLOT = sprite(16, 0, 18, 18, 0);
    static final UITexture SLOT_ACTIVE = sprite(34, 0, 18, 18, 0);
    static final UITexture SLOT_LOCKED = sprite(52, 0, 18, 18, 0);
    static final UITexture SLOT_INSERT = sprite(70, 0, 18, 18, 0);
    static final UITexture BUTTON = sprite(88, 0, 10, 10, 0);
    static final UITexture BUTTON_HOVER = sprite(98, 0, 10, 10, 0);
    static final UITexture ICON_NEXT = sprite(0, 24, 8, 8, 0);
    static final UITexture ICON_RESET = sprite(8, 24, 8, 8, 0);
    static final UITexture ICON_COLLAPSE = sprite(16, 24, 8, 8, 0);
    static final UITexture ICON_WINDOW_SHOWN = sprite(24, 24, 8, 8, 0);
    static final UITexture ICON_WINDOW_HIDDEN = sprite(32, 24, 8, 8, 0);
    static final UITexture SCROLL_TRACK = sprite(0, 32, 8, 16, 2);
    static final UITexture SCROLL_THUMB = sprite(8, 32, 6, 15, 2);
    static final UITexture COMPACT = sprite(0, 48, 18, 18, 4);
    private static final RenderItem ITEM_RENDER = new RenderItem();

    private static UITexture sprite(int x, int y, int width, int height, int border) {
        UITexture.Builder builder = UITexture.builder()
            .location(RESOURCE_ROOT_ID, SHEET)
            .imageSize(128, 128)
            .xy(x, y, width, height);
        if (border > 0) builder.adaptable(border);
        return builder.build();
    }

    static UITexture slotFrame(YogTerminalClient.View view, int index, ItemStack stack) {
        if (view != null && YogTerminalClient.isSessionEntry(view, index)) return SLOT_LOCKED;
        if (view != null && index == view.active) return SLOT_ACTIVE;
        return stack == null ? SLOT_INSERT : SLOT;
    }

    static void drawItem(ItemStack stack, int x, int y, boolean overlay) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        try {
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            ITEM_RENDER.zLevel = 100.0F;
            ITEM_RENDER.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
            if (overlay) ITEM_RENDER.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
            ITEM_RENDER.zLevel = 0.0F;
        } finally {
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
}
