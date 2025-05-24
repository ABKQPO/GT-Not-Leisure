package com.science.gtnl.common.block.blocks.artificialStarRender;

import static com.science.gtnl.common.block.blocks.artificialStarRender.RealArtificialStarRender.STAR_MODEL;
import static com.science.gtnl.common.block.blocks.artificialStarRender.RealArtificialStarRender.STAR_TEXTURE;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.block.Casings.BasicBlocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemBlockArtificialStarRender implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item.getItem() != Item.getItemFromBlock(BasicBlocks.BlockArtificialStarRender)) {
            return false;
        }
        return switch (type) {
            case ENTITY, EQUIPPED, EQUIPPED_FIRST_PERSON, INVENTORY -> true;
            default -> false;
        };
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (item.getItem() != Item.getItemFromBlock(BasicBlocks.BlockArtificialStarRender)) return;
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glTranslated(0.5, 0.5, 0.5);
        GL11.glScaled(0.25, 0.25, 0.25);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotated(5, 1, 1, 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(STAR_TEXTURE);
        STAR_MODEL.renderAll();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

    }
}
