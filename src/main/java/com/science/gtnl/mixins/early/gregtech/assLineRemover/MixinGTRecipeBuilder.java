package com.science.gtnl.mixins.early.gregtech.assLineRemover;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.recipe.AssLineRecipeHook;

import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;

@Mixin(value = GTRecipeBuilder.class, remap = false)
public class MixinGTRecipeBuilder {

    @Shadow
    protected ItemStack[] outputs;

    @Shadow
    @Nullable
    protected IRecipeMetadataStorage metadataStorage;

    @Shadow
    protected int duration;

    @Shadow
    protected int eut;

    @Inject(method = "addTo*", at = @At("HEAD"), cancellable = true)
    private void science$assLineRecipeHook(IRecipeMap recipeMap, CallbackInfoReturnable<Collection<GTRecipe>> cir) {
        if (recipeMap != GTRecipeConstants.AssemblyLine || this.metadataStorage == null) return;
        ItemStack researchItem = this.metadataStorage.getMetadata(GTRecipeConstants.RESEARCH_ITEM);
        if (researchItem == null) return;
        Scanning scanningData = this.metadataStorage
            .getMetadataOrDefault(GTRecipeConstants.SCANNING, new Scanning(0, 0));
        if (AssLineRecipeHook.removeGTRecipe(researchItem, scanningData.time, this.outputs, this.duration, this.eut)) {
            cir.setReturnValue(Collections.emptyList());
        }
    }
}
