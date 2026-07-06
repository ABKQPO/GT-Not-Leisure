package com.science.gtnl.mixins.late.appliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.helpers.DualityInterface;
import appeng.tile.misc.TileInterface;

@Mixin(value = TileInterface.class, remap = false)
public interface AccessorTileInterface {

    @Accessor("duality")
    DualityInterface getDuality();

    @Mutable
    @Accessor("duality")
    void setDuality(DualityInterface duality);
}
