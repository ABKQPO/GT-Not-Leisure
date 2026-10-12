package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import org.spongepowered.asm.mixin.Mixin;

import com.science.gtnl.api.mixinHelper.ITileController;

import appeng.tile.networking.TileController;

@Mixin(value = TileController.class, remap = false)
public class MixinTileController implements ITileController {
}
