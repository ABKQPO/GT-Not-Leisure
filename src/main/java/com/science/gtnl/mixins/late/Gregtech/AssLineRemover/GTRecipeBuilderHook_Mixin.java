package com.science.gtnl.mixins.late.Gregtech.AssLineRemover;

import static com.science.gtnl.Utils.recipes.AssLineRecipeHook.RECIPE_TO_REMOVE;
import static gregtech.api.util.GTRecipeConstants.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.Utils.recipes.AssLineRecipeHook;

import cpw.mods.fml.common.registry.GameData;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;

@SuppressWarnings("UnusedMixin")
@Mixin(value = GTRecipeBuilder.class, remap = false)
public class GTRecipeBuilderHook_Mixin {

    @Shadow
    protected ItemStack[] outputs;

    @Shadow
    @Nullable
    protected IRecipeMetadataStorage metadataStorage;

    @Shadow
    protected int duration;

    @Shadow
    protected int eut;

    private static boolean cdddddd = true;

    @Inject(method = "addTo", at = @At("HEAD"), cancellable = true)
    private void science$assLineRecipeHook(IRecipeMap recipeMap, CallbackInfoReturnable<Collection<GTRecipe>> cir) {
        if (recipeMap == GTRecipeConstants.AssemblyLine) {
            if (this.metadataStorage != null) {
                ItemStack researchItem = this.metadataStorage.getMetadata(RESEARCH_ITEM);
                Scanning scanningData = this.metadataStorage.getMetadataOrDefault(SCANNING, new Scanning(0, 0));
                // noinspection DataFlowIssue
                int time = scanningData.time;
                if (researchItem != null) {
                    String name = GameData.getItemRegistry()
                        .getNameForObject(researchItem.getItem());
                    if (cdddddd) {
                        cdddddd = false;
                        System.out.println("xxxxxxxxxxxxxx  " + name);
                    }
                    int amount = researchItem.stackSize;
                    int meta = Items.feather.getDamage(researchItem);
                    // noinspection StringBufferReplaceableByString
                    String key = new StringBuilder().append(name)
                        .append("@")
                        .append(amount)
                        .append("@")
                        .append(meta)
                        .append("@")
                        .append(time)
                        .toString();
                    if (RECIPE_TO_REMOVE.containsKey(key)) {
                        ItemStack output = this.outputs[0];
                        name = GameData.getItemRegistry()
                            .getNameForObject(output.getItem());
                        amount = output.stackSize;
                        meta = Items.feather.getDamage(output);
                        // noinspection StringBufferReplaceableByString
                        String outputItemKey = new StringBuilder().append(name)
                            .append("@")
                            .append(amount)
                            .append("@")
                            .append(meta)
                            .append("@")
                            .append(this.duration)
                            .append("@")
                            .append(this.eut)
                            .toString();
                        for (AssLineRecipeHook.RemovalRecipe removalRecipe : RECIPE_TO_REMOVE.get(key)) {
                            if (Objects.equals(removalRecipe.removalKey, outputItemKey)) {
                                removalRecipe.hasNotBeenRemoved = false;
                                cir.setReturnValue(Collections.emptyList());
                            }
                        }
                    }
                }
            }
        }
    }
}
