package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ContainerRepair.class)
public interface AccessorContainerRepair {

    @Accessor("inputSlots")
    IInventory getInputSlots();

    @Accessor("outputSlot")
    IInventory getOutputSlots();

    @Accessor("inputSlots")
    void setInputSlots(IInventory value);

    @Accessor("outputSlot")
    void setOutputSlots(IInventory value);

}
