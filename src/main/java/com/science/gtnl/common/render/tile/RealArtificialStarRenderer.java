package com.science.gtnl.common.render.tile;

import static tectech.rendering.EOH.EOHRenderingUtils.renderEOHStar;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

import org.joml.Matrix4f;

import com.science.gtnl.common.block.blocks.tile.TileEntityArtificialStar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RealArtificialStarRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityArtificialStar star) || star.getWorldObj() == null) return;

        Matrix4f modelMatrix = new Matrix4f().translation((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F)
            .rotate((float) Math.toRadians(star.rotation), 1.0F, 1.0F, 1.0F);
        double renderTime = star.getWorldObj()
            .getTotalWorldTime() + partialTicks;
        renderEOHStar(modelMatrix, ItemRenderType.ENTITY, renderTime, star.size);
    }
}
