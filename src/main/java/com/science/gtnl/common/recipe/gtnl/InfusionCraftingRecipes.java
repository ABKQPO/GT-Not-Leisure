package com.science.gtnl.common.recipe.gtnl;

import static thaumcraft.common.config.ConfigBlocks.blockCosmeticSolid;

import java.util.Arrays;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;

import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.SimpleRecipeMetadataKey;
import gregtech.api.util.GTUtility;
import thaumcraft.api.aspects.AspectList;

public class InfusionCraftingRecipes implements IRecipePool {

    private static final ItemId COSMETIC_SOLID_OUTPUT = ItemId.create(new ItemStack(blockCosmeticSolid));
    public static final RecipeMetadataKey<AspectList> INFUSION_ASPECTS = SimpleRecipeMetadataKey
        .create(AspectList.class, "gtnl_infusion_aspects");

    public static final RecipeMetadataKey<String> INFUSION_RESEARCH = SimpleRecipeMetadataKey
        .create(String.class, "gtnl_infusion_research");

    private static ItemStack[] createInputs(TCRecipeTools.InfusionCraftingRecipe recipe) {
        ItemStack[] inputs = TCRecipeTools.checkInputSpecial(recipe.getInputItem());
        if (!COSMETIC_SOLID_OUTPUT.equals(ItemId.create(recipe.getOutput()))) {
            return inputs;
        }

        ItemStack[] separatedInputs = Arrays.copyOf(inputs, inputs.length + 1);
        separatedInputs[inputs.length] = GTUtility.getIntegratedCircuit(11);
        return separatedInputs;
    }

    @Override
    public void loadRecipes() {
        TCRecipeTools.getInfusionCraftingRecipe();

        IRecipeMap IIC = GTNLRecipeMaps.IndustrialInfusionCraftingRecipes;

        for (TCRecipeTools.InfusionCraftingRecipe Recipe : TCRecipeTools.ICR) {
            if (TCRecipeTools.shouldSkipThaumcraftItem(
                Recipe.getOutput()
                    .getItem())) {
                continue;
            }

            TCRecipeTools.addArcaneRecipe(
                IIC,
                createInputs(Recipe),
                new ItemStack[] { Recipe.getOutput() },
                Recipe.getInputAspects(),
                Recipe.getResearch(),
                INFUSION_ASPECTS,
                INFUSION_RESEARCH,
                TCRecipeTools
                    .computeAspectDuration(Recipe.getInputAspects(), TCRecipeTools.INFUSION_DURATION_TICKS_PER_ASPECT),
                TierEU.RECIPE_LV);
        }
    }
}
