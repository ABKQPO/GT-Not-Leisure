package com.science.gtnl.mixins.late.appliedEnergistics;

import net.minecraft.inventory.ISidedInventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.util.inv.WrapperMCISidedInventory;

@Mixin(value = WrapperMCISidedInventory.class, remap = false)
public interface AccessorWrapperMCISidedInventory {

    @Accessor("side")
    ISidedInventory getSide();
}
