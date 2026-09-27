/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
package com.science.gtnl.common.render.tile;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.block.blocks.BlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.common.render.model.JsonBlockModel.Geometry;
import com.science.gtnl.common.render.model.PigmeeFumoModel;
import com.science.gtnl.common.render.model.PigmeeFumoRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The only world-geometry entry point for a placed Pigmee Fumo.
 *
 * <p>
 * When spinning, the model is always taken in its NORTH orientation and the accumulated spin angle is applied
 * as a Y rotation about the block centre, so the doll turns in place regardless of its placed facing. When
 * stopped, the persisted facing is used as-is and no spin rotation is applied.
 */
@SideOnly(Side.CLIENT)
public class PigmeeFumoRenderer extends TileEntitySpecialRenderer {

    /** Reused lightmap scratch; only ever touched on the render thread. */
    private final int[] brightness = new int[7];

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
        if (!(tile instanceof TileEntityPigmeeFumo fumo)) return;
        IBlockAccess world = fumo.getWorldObj();
        if (world == null) return;

        int metadata = fumo.getBlockMetadata();
        boolean spinning = fumo.isSpinning();
        ForgeDirection front = spinning ? ForgeDirection.NORTH : BlockPigmeeFumo.facingFromMetadata(metadata);
        Geometry model = PigmeeFumoModel.INSTANCE.get(front);
        if (model == null) return;

        brightness[6] = tile.getBlockType()
            .getMixedBrightnessForBlock(world, tile.xCoord, tile.yCoord, tile.zCoord);
        for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
            Block block = world
                .getBlock(tile.xCoord + face.offsetX, tile.yCoord + face.offsetY, tile.zCoord + face.offsetZ);
            if (block != null) {
                brightness[face.ordinal()] = block.getMixedBrightnessForBlock(
                    world,
                    tile.xCoord + face.offsetX,
                    tile.yCoord + face.offsetY,
                    tile.zCoord + face.offsetZ);
            }
        }

        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            if (spinning) {
                GL11.glTranslatef(0.5F, 0.0F, 0.5F);
                GL11.glRotatef(fumo.getRenderYRot(partialTick), 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
            }
            PigmeeFumoRenderHelper.drawWorld(model, brightness);
        } finally {
            GL11.glPopMatrix();
        }
    }
}
