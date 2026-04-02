package com.science.gtnl.common.render.tile;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import com.science.gtnl.ClientProxy;
import com.science.gtnl.common.render.model.WaterCandleModel;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@ThreadSafeISBRH(perThread = true)
public class WaterCandleRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    private final WaterCandleModel model = new WaterCandleModel();

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        RESOURCE_ROOT_ID + ":" + "textures/blocks/WaterCandle.png");

    @Override
    public int getRenderId() {
        return ClientProxy.waterCandleRenderID;
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
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0, 0.5F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        model.render(null, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5F, y, z + 0.5F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        bindTexture(TEXTURE);
        model.render(null, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}
