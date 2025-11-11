package com.science.gtnl.mixins.late.assembler;

import net.minecraft.inventory.IInventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "appeng.container.implementations.ContainerInterfaceTerminal$InvTracker", remap = false)
public interface AccessorInvTracker {

    @Accessor
    IInventory getPatterns();
}
