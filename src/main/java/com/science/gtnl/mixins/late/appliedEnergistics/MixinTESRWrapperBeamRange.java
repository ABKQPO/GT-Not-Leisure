package com.science.gtnl.mixins.late.appliedEnergistics;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.common.render.beamformer.BeamFormerRenderHelper;

import appeng.api.parts.IPartHost;
import appeng.client.render.TESRWrapper;

@Mixin(value = TESRWrapper.class, remap = false)
public abstract class MixinTESRWrapperBeamRange {

    @Shadow
    @Final
    private double maxDistance;

    @Inject(method = "renderTileEntityAt", at = @At("HEAD"), remap = true)
    private void gtnl$renderDistantBeams(TileEntity tile, double x, double y, double z, float partialTicks,
        CallbackInfo ci) {
        if (Math.abs(x) <= maxDistance && Math.abs(y) <= maxDistance && Math.abs(z) <= maxDistance) return;
        if (!(tile instanceof IPartHost host)) return;
        // The normal wrapper skips this host, but its beam can still cross the camera frustum.
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (host.getPart(side) instanceof IBeamFormer former) {
                BeamFormerRenderHelper.renderDynamic(former, x, y, z, partialTicks);
            }
        }
    }
}
