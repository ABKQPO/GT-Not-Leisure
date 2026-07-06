package com.science.gtnl.mixins.late.appliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseItemBlock;

@Mixin(value = AEBaseItemBlock.class, remap = false)
public interface AccessorAEBaseItemBlock {

    @Accessor("blockType")
    AEBaseBlock getBlockType();
}
