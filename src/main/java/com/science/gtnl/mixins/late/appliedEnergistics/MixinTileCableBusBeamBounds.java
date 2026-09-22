package com.science.gtnl.mixins.late.appliedEnergistics;

import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.common.render.beamformer.BeamFormerRenderHelper;

import appeng.parts.CableBusContainer;
import appeng.tile.AEBaseTile;
import appeng.tile.networking.TileCableBus;

@Mixin(value = TileCableBus.class, remap = false)
public abstract class MixinTileCableBusBeamBounds extends AEBaseTile {

    @Shadow
    public abstract CableBusContainer getCableBus();

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bounds = super.getRenderBoundingBox();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (getCableBus().getPart(side) instanceof IBeamFormer former && former.shouldRenderBeam()) {
                bounds = bounds.func_111270_a(BeamFormerRenderHelper.getRenderBoundingBox(former));
            }
        }
        return bounds;
    }

    @Inject(method = "getMaxRenderDistanceSquared", at = @At("RETURN"), cancellable = true, remap = true)
    private void gtnl$includeBeamDistance(CallbackInfoReturnable<Double> cir) {
        double distanceSquared = cir.getReturnValueD();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (getCableBus().getPart(side) instanceof IBeamFormer former && former.shouldRenderBeam()) {
                double distance = former.getBeamLength() + 16.0;
                distanceSquared = Math.max(distanceSquared, distance * distance);
            }
        }
        cir.setReturnValue(distanceSquared);
    }
}
