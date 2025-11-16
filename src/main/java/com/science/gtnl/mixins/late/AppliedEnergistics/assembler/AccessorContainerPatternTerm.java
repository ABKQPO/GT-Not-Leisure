package com.science.gtnl.mixins.late.AppliedEnergistics.assembler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.container.implementations.ContainerPatternTerm;
import appeng.container.slot.SlotRestrictedInput;

@Mixin(value = ContainerPatternTerm.class, remap = false)
public interface AccessorContainerPatternTerm {

    @Accessor
    SlotRestrictedInput getPatternSlotOUT();
}
