package com.science.gtnl.mixins.early.gregtech;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gregtech.api.recipe.metadata.EmptyRecipeMetadataStorage;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.util.GTRecipe;

@Mixin(value = GTRecipe.class, remap = false)
public abstract class MixinGTRecipe {

    @Shadow
    @Final
    @Mutable
    private IRecipeMetadataStorage metadataStorage;

    @Inject(method = "<init>(Lgregtech/api/util/GTRecipe;Z)V", at = @At("TAIL"), remap = false)
    private void science$preserveCopiedMetadata(GTRecipe source, boolean shallow, CallbackInfo ci) {
        IRecipeMetadataStorage sourceMetadata = source.getMetadataStorage();
        metadataStorage = sourceMetadata == EmptyRecipeMetadataStorage.INSTANCE ? sourceMetadata
            : sourceMetadata.copy();
    }
}
