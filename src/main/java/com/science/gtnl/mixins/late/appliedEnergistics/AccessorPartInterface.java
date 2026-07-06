package com.science.gtnl.mixins.late.appliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.helpers.DualityInterface;
import appeng.parts.misc.PartInterface;

@Mixin(value = PartInterface.class, remap = false)
public interface AccessorPartInterface {

    @Accessor("duality")
    DualityInterface getDuality();

    @Mutable
    @Accessor("duality")
    void setDuality(DualityInterface duality);
}
