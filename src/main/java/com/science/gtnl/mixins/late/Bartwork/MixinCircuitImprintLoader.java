package com.science.gtnl.mixins.late.Bartwork;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.loader.RecipeLoader;

import bartworks.system.material.CircuitGeneration.BWMetaItems;
import bartworks.system.material.CircuitGeneration.CircuitImprintLoader;
import bartworks.util.BWUtil;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;

@Mixin(value = CircuitImprintLoader.class, remap = false)
public abstract class MixinCircuitImprintLoader {

    @Inject(
        method = "run",
        at = @At(
            value = "INVOKE",
            target = "Lbartworks/system/material/CircuitGeneration/CircuitImprintLoader;exchangeRecipesInList(Ljava/util/HashSet;Ljava/util/HashSet;)V",
            shift = At.Shift.BEFORE))
    private static void injectCustomRecipeLoader(CallbackInfo ci) {
        RecipeLoader.loadCircuitRelatedRecipes();
    }

    /**
     * Overwrites the makeMoreExpensive method in CircuitImprintLoader.
     *
     * @reason Adjusts circuit recipe scaling to allow stack sizes up to Integer.MAX_VALUE
     *         instead of being capped at 64 or the item's max stack size. This enables
     *         more flexible and large-batch crafting behavior.
     * @author GTNotLeisure
     */
    @Overwrite
    private static void replaceComponents(ItemStack[] in, GTRecipe original, int index)
        throws ArrayIndexOutOfBoundsException {
        if (original.mInputs[index] != null && in[index] == null) {
            // big wires
            if (BWUtil.checkStackAndPrefix(original.mInputs[index])
                && GTOreDictUnificator.getAssociation(original.mInputs[index]).mPrefix == OrePrefixes.wireGt01) {
                in[index] = GTOreDictUnificator.get(
                    OrePrefixes.wireGt16,
                    GTOreDictUnificator.getAssociation(original.mInputs[index]).mMaterial.mMaterial,
                    original.mInputs[index].stackSize);
                // fine wires
            } else if (BWUtil.checkStackAndPrefix(original.mInputs[index])
                && GTOreDictUnificator.getAssociation(original.mInputs[index]).mPrefix == OrePrefixes.wireFine) {
                    in[index] = GTOreDictUnificator.get(
                        OrePrefixes.wireGt04,
                        GTOreDictUnificator.getAssociation(original.mInputs[index]).mMaterial.mMaterial,
                        original.mInputs[index].stackSize);
                    if (in[index] == null) {
                        in[index] = GTOreDictUnificator.get(
                            OrePrefixes.wireFine,
                            GTOreDictUnificator.getAssociation(original.mInputs[index]).mMaterial.mMaterial,
                            original.mInputs[index].stackSize * 16L);
                    }
                    // other components
                } else {
                    in[index] = original.mInputs[index].copy();
                    in[index].stackSize *= 16;

                    if (in[index].getItem() != ItemLoader.metaItem
                        && !(in[index].isItemEqual(
                            BWMetaItems.getCircuitParts()
                                .getStack(3)))
                        && (in[index].stackSize > in[index].getItem()
                            .getItemStackLimit() || in[index].stackSize > in[index].getMaxStackSize())) {
                        in[index].stackSize = in[index].getMaxStackSize();
                    }
                }
        }
    }
}
