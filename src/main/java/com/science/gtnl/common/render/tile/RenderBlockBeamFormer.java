package com.science.gtnl.common.render.tile;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.block.blocks.BlockBeamFormer;
import com.science.gtnl.common.block.blocks.tile.TileEntityBeamFormer;
import com.science.gtnl.common.render.beamformer.BeaconRenderHelper;
import com.science.gtnl.common.render.beamformer.BeamFormerRenderHelper;

import appeng.client.render.BaseBlockRender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockBeamFormer extends BaseBlockRender<BlockBeamFormer, TileEntityBeamFormer> {

    private static final int[][] BOXES = { { 6, 6, 0, 10, 10, 1 }, { 6, 6, 1, 10, 10, 2 }, { 6, 5, 2, 10, 6, 3 },
        { 10, 7, 3, 11, 9, 5 }, { 7, 10, 3, 9, 11, 5 }, { 5, 7, 3, 6, 9, 5 }, { 7, 5, 3, 9, 6, 5 },
        { 6, 10, 2, 10, 11, 3 }, { 5, 6, 2, 6, 10, 3 }, { 10, 7, 1, 11, 9, 2 }, { 5, 7, 1, 6, 9, 2 },
        { 7, 5, 1, 9, 6, 2 }, { 7, 10, 1, 9, 11, 2 }, { 10, 6, 2, 11, 10, 3 } };

    private static final int[] STATUS_BOX = { 6, 6, 0, 10, 10, 1 };

    public RenderBlockBeamFormer() {
        super(true, 50);
    }

    @Override
    public boolean renderInWorld(BlockBeamFormer block, IBlockAccess world, int x, int y, int z,
        RenderBlocks renderer) {
        TileEntityBeamFormer te = block.getTileEntity(world, x, y, z);
        if (te == null) return false;

        ForgeDirection forward = te.getForward();
        IIcon status = BlockBeamFormer.iconStatusOff;
        boolean active;

        try {
            active = te.getProxy()
                .isActive();
        } catch (Exception e) {
            active = false;
        }

        if (active) {
            status = (te.connection != null || te.paired) ? BlockBeamFormer.iconStatusBeaming
                : BlockBeamFormer.iconStatusOn;
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

        for (int[] b : BOXES) {
            float[] bounds = transformBounds(b, forward);
            renderer.setOverrideBlockTexture(BlockBeamFormer.iconBase);
            renderer.setRenderBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
            renderer.renderStandardBlock(block, x, y, z);
        }

        {
            float[] bounds = transformBounds(STATUS_BOX, forward);
            renderer.setOverrideBlockTexture(status);
            renderer.setRenderBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (!(active && (te.connection != null || te.paired))) {
            float[] bounds = transformBounds(STATUS_BOX, forward);
            renderer.setOverrideBlockTexture(BlockBeamFormer.iconPrism);
            renderer.setRenderBounds(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
            renderer.renderStandardBlock(block, x, y, z);
        }

        renderer.clearOverrideBlockTexture();
        return true;
    }

    @Override
    public void renderTile(BlockBeamFormer block, TileEntityBeamFormer te, Tessellator tessellator, double x, double y,
        double z, float partialTicks, RenderBlocks renderer) {
        if (te == null || !te.shouldRenderBeam()) return;
        double offset = te.getClientOtherOffset();

        BeamFormerRenderHelper.StaticBloomMetadata metadata = BeamFormerRenderHelper.getBloomMetadata(te);
        float[] rgb = BeamFormerRenderHelper.getColor(te);

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glRotatef(metadata.yaw(), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(metadata.pitch(), 1.0F, 0.0F, 0.0F);
        GL11.glTranslated(-0.5, -0.35, -0.5);

        BeaconRenderHelper.renderBeamSegment(
            0,
            0,
            0,
            partialTicks,
            1,
            (double) te.getWorld()
                .getTotalWorldTime(),
            0,
            te.getBeamLength() + offset,
            rgb,
            0.12,
            0.15);

        GL11.glPopMatrix();
    }

    @Override
    public void renderInventory(BlockBeamFormer block, ItemStack item, RenderBlocks renderer,
        IItemRenderer.ItemRenderType type, final Object[] data) {
        GL11.glPushMatrix();
        Tessellator tess = Tessellator.instance;

        for (int[] b : BOXES) {
            renderer.setOverrideBlockTexture(BlockBeamFormer.iconBase);
            renderer
                .setRenderBounds(b[0] / 16.0F, b[1] / 16.0F, b[2] / 16.0F, b[3] / 16.0F, b[4] / 16.0F, b[5] / 16.0F);
            renderInventoryBox(block, renderer, tess);
        }

        renderer.setOverrideBlockTexture(BlockBeamFormer.iconStatusOff);
        renderer.setRenderBounds(
            STATUS_BOX[0] / 16.0F,
            STATUS_BOX[1] / 16.0F,
            STATUS_BOX[2] / 16.0F,
            STATUS_BOX[3] / 16.0F,
            STATUS_BOX[4] / 16.0F,
            STATUS_BOX[5] / 16.0F);
        renderInventoryBox(block, renderer, tess);

        renderer.setOverrideBlockTexture(BlockBeamFormer.iconPrism);
        renderer.setRenderBounds(
            STATUS_BOX[0] / 16.0F,
            STATUS_BOX[1] / 16.0F,
            STATUS_BOX[2] / 16.0F,
            STATUS_BOX[3] / 16.0F,
            STATUS_BOX[4] / 16.0F,
            STATUS_BOX[5] / 16.0F);
        renderInventoryBox(block, renderer, tess);

        renderer.clearOverrideBlockTexture();
        GL11.glPopMatrix();
    }

    private static float[] transformBounds(int[] box, ForgeDirection forward) {
        float x1 = box[0] / 16.0F;
        float y1 = box[1] / 16.0F;
        float z1 = box[2] / 16.0F;
        float x2 = box[3] / 16.0F;
        float y2 = box[4] / 16.0F;
        float z2 = box[5] / 16.0F;

        return switch (forward) {
            case SOUTH -> new float[] { x1, y1, z1, x2, y2, z2 };
            case NORTH -> new float[] { 1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1 };
            case EAST -> new float[] { z1, y1, 1 - x2, z2, y2, 1 - x1 };
            case WEST -> new float[] { 1 - z2, y1, x1, 1 - z1, y2, x2 };
            case UP -> new float[] { x1, z1, 1 - y2, x2, z2, 1 - y1 };
            case DOWN -> new float[] { x1, 1 - z2, y1, x2, 1 - z1, y2 };
            default -> new float[] { x1, y1, z1, x2, y2, z2 };
        };
    }

    private static void renderInventoryBox(BlockBeamFormer block, RenderBlocks renderer, Tessellator tess) {
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tess.startDrawingQuads();
        tess.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.overrideBlockTexture);
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.overrideBlockTexture);
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.overrideBlockTexture);
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.overrideBlockTexture);
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.overrideBlockTexture);
        tess.draw();

        tess.startDrawingQuads();
        tess.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.overrideBlockTexture);
        tess.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
