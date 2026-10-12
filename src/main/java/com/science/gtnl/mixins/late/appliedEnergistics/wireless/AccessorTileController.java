package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import org.spongepowered.asm.mixin.Mixin;

import appeng.tile.networking.TileController;

@Mixin(value = TileController.class, remap = false)
public interface AccessorTileController {
}
