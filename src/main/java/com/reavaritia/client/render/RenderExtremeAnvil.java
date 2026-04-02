package com.reavaritia.client.render;

import static com.reavaritia.ReAvaritia.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.reavaritia.ClientProxy;
import com.reavaritia.client.ExtremeAnvilModel;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderExtremeAnvil extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    public ExtremeAnvilModel model = new ExtremeAnvilModel();

    public static ResourceLocation TEXTURE = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "textures/blocks/ExtremeAnvil.png");

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 1.5F, 0.5F);
        GL11.glRotatef(180, 1, 0, 1);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        model.render(null, 0, 0, 0, 0, 0, 0.065F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.extremeAnvilRenderType;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180, 0, 0, 1);
        int meta = te.getBlockMetadata() & 3;
        GL11.glRotatef(meta * 90, 0, 1, 0);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        this.bindTexture(TEXTURE);
        model.render(null, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}
