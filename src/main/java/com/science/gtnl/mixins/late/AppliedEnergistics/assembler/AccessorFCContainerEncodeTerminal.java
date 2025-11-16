package com.science.gtnl.mixins.late.AppliedEnergistics.assembler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.glodblock.github.client.gui.container.base.FCContainerEncodeTerminal;

import appeng.container.slot.SlotRestrictedInput;

@Mixin(value = FCContainerEncodeTerminal.class, remap = false)
public interface AccessorFCContainerEncodeTerminal {

    @Accessor
    SlotRestrictedInput getPatternSlotOUT();
}
