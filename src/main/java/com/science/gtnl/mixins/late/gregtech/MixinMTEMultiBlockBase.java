package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IMultiblockRecipeMap;
import com.science.gtnl.config.MainConfig;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.RecipeMap;

@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MixinMTEMultiBlockBase {

    @Shadow
    public abstract RecipeMap<?> getRecipeMap();

    @Inject(method = "addToMachineList", at = @At(value = "HEAD"))
    private void gtnl$HatchCheck(IGregTechTileEntity aTileEntity, int aBaseCasingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        gtnl$setRecipeMapNameIfValid(aTileEntity);
    }

    @Inject(method = "addInputBusToMachineList", at = @At(value = "HEAD"))
    private void gtnl$InputBusCheck(IGregTechTileEntity aTileEntity, int aBaseCasingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        gtnl$setRecipeMapNameIfValid(aTileEntity);
    }

    @Inject(method = "addInputHatchToMachineList", at = @At(value = "HEAD"))
    private void gtnl$InputHatchCheck(IGregTechTileEntity aTileEntity, int aBaseCasingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        gtnl$setRecipeMapNameIfValid(aTileEntity);
    }

    @Unique
    private void gtnl$setRecipeMapNameIfValid(IGregTechTileEntity aTileEntity) {
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        if (aTileEntity == null) return;
        if (getRecipeMap() == null) return;

        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity instanceof IMultiblockRecipeMap recipeMap) {
            recipeMap.gtnl$setRecipeMapName(getRecipeMap().unlocalizedName);
        }
    }

}
