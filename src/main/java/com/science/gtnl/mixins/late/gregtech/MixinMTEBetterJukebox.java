package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;

import gregtech.common.tileentities.machines.basic.MTEBetterJukebox;

@Mixin(value = MTEBetterJukebox.class, remap = false)
@Deprecated
public class MixinMTEBetterJukebox {

    // TODO: Remove after confirming GT5U MusicRecordMetadataProvider support fully replaces this compatibility mixin.
}
