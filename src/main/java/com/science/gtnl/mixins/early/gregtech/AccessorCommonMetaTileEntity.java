package com.science.gtnl.mixins.early.gregtech;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import gregtech.api.metatileentity.CommonMetaTileEntity;

@Mixin(value = CommonMetaTileEntity.class, remap = false)
public interface AccessorCommonMetaTileEntity {

    @Accessor("mInventory")
    ItemStack[] getInventory();

    @Accessor("mInventory")
    void setInventory(ItemStack[] inventory);
}
