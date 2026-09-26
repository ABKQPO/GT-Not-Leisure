package com.science.gtnl.mixins.early.gregtech;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.api.mixinHelper.ICommonMetaTileEntityInventory;

import gregtech.api.metatileentity.CommonMetaTileEntity;
import lombok.Setter;

@Mixin(value = CommonMetaTileEntity.class, remap = false)
public abstract class MixinCommonMetaTileEntity implements ICommonMetaTileEntityInventory {

    @Setter
    @Mutable
    @Final
    @Shadow
    public ItemStack[] mInventory;
}
