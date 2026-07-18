package com.science.gtnl.mixins.late.gregtech;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import tectech.recipe.TecTechRecipeMaps;

@Mixin(value = MTEHatchDataAccess.class, remap = false)
public abstract class MixinMTEHatchDataAccess extends MTEHatch implements IAddUIWidgets {

    @Shadow
    private List<GTRecipe.RecipeAssemblyLine> cachedRecipes;

    private int science$cachedAssemblyLineRecipeCount = -1;
    private int science$cachedTecTechRecipeCount = -1;

    public MixinMTEHatchDataAccess(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount,
        String aDescription, ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Inject(method = "getAssemblyLineRecipes", at = @At("TAIL"), cancellable = true)
    public void getAssemblyLineRecipes(CallbackInfoReturnable<List<GTRecipe.RecipeAssemblyLine>> cir) {
        int assemblyLineRecipeCount = GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes.size();
        int tecTechRecipeCount = TecTechRecipeMaps.researchableALRecipeList.size();
        if (cachedRecipes != null && science$cachedAssemblyLineRecipeCount == assemblyLineRecipeCount
            && science$cachedTecTechRecipeCount == tecTechRecipeCount) {
            return;
        }

        cachedRecipes = new ArrayList<>();
        for (int i = 0; i < getSizeInventory(); i++) {
            cachedRecipes.addAll(AssemblyLineUtils.findALRecipeFromDataStick(getStackInSlot(i)));
        }
        science$cachedAssemblyLineRecipeCount = assemblyLineRecipeCount;
        science$cachedTecTechRecipeCount = tecTechRecipeCount;
        cir.setReturnValue(cachedRecipes);
    }

}
