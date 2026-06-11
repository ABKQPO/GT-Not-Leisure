package com.science.gtnl.mixins.late.AppliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import appeng.tile.storage.TileIOPort;

@Mixin(value = TileIOPort.class, remap = false)
public abstract class MixinTileIOPort {

    @ModifyConstant(method = "tickingRequest", constant = @Constant(longValue = 536_870_912))
    private long gtnl$limitSuperluminalMoveBudget(long original) {
        return (Long.MAX_VALUE - 1) / 256L / 1000L;
    }
}
