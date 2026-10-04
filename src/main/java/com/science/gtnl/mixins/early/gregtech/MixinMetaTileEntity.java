package com.science.gtnl.mixins.early.gregtech;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.science.gtnl.api.mixinHelper.IMetaTileEntityInventoryHandler;

import gregtech.api.metatileentity.MetaTileEntity;
import lombok.Setter;

@Mixin(value = MetaTileEntity.class, remap = false)
public abstract class MixinMetaTileEntity implements IMetaTileEntityInventoryHandler {

    @Setter
    @Mutable
    @Final
    @Shadow
    public ItemStackHandler inventoryHandler;
}
