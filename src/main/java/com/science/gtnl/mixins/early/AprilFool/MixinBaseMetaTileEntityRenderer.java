package com.science.gtnl.mixins.early.AprilFool;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;

import com.science.gtnl.common.render.tile.BallRenderer;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.common.render.BaseMetaTileEntityRenderer;

@Mixin(value = BaseMetaTileEntityRenderer.class, remap = false)
public abstract class MixinBaseMetaTileEntityRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float timeSinceLastTick) {
        if (!(te instanceof BaseMetaTileEntity baseTE)) return;
        BallRenderer.renderTileEntity(baseTE.getMetaTileEntity(), x, y, z, timeSinceLastTick);
    }
}
