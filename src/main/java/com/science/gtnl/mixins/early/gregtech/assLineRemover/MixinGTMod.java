package com.science.gtnl.mixins.early.gregtech.assLineRemover;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.AssLineRecipeHook;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gregtech.GTMod;

@Mixin(value = GTMod.class, remap = false)
public class MixinGTMod {

    @Inject(method = "onPreInitialization", at = @At("HEAD"))
    public void science$loadHook(FMLPreInitializationEvent aEvent, CallbackInfo ci) {
        if (MainConfig.recipe.enableDeleteRecipe) {
            AssLineRecipeHook.loadAndInit();
        }
    }
}
