package com.science.gtnl.mixins.early.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import gregtech.api.metatileentity.MetaTileEntity;

@Mixin(value = MetaTileEntity.class, remap = false)
public interface AccessorMetaTileEntity {

    @Accessor("inventoryHandler")
    void setInventoryHandler(ItemStackHandler inventoryHandler);
}
