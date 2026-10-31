package com.science.gtnl.common.recipe.gtnl;

import static thaumcraft.common.config.ConfigBlocks.blockCosmeticSolid;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.objects.OreDictItemStack;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.SimpleRecipeMetadataKey;
import thaumcraft.api.aspects.AspectList;

public class InfusionCraftingRecipes implements IRecipePool {

    private static final ItemId COSMETIC_SOLID_OUTPUT = ItemId.create(new ItemStack(blockCosmeticSolid));
    public static final RecipeMetadataKey<AspectList> INFUSION_ASPECTS = SimpleRecipeMetadataKey
        .create(AspectList.class, "gtnl_infusion_aspects");

    public static final RecipeMetadataKey<String> INFUSION_RESEARCH = SimpleRecipeMetadataKey
        .create(String.class, "gtnl_infusion_research");

    private static final Set<String> TIERED_GLASS_ORES = Arrays.stream(GTValues.VN)
        .map(tier -> "blockGlass" + tier)
        .collect(Collectors.toSet());
    private static final String PLAIN_PANE_ORE = "paneGlassColorless", ANY_PANE_ORE = "paneGlass";

    private static Object[] createInputs(TCRecipeTools.InfusionCraftingRecipe recipe) {
        ItemStack[] inputs = TCRecipeTools.checkInputSpecial(recipe.getInputItem());
        Object[] converted = TCRecipeTools.toGTInputs(inputs);
        // Thaumcraft accepts pedestal items by ore name; only glass keeps that here, other components stay exact.
        for (int i = 1; i < inputs.length; i++) {
            if (inputs[i] == null || inputs[i].stackSize <= 0) continue;
            String glassOre = findGlassOreName(inputs[i]);
            if (glassOre != null) converted[i] = new OreDictItemStack(glassOre, inputs[i].stackSize);
        }
        return converted;
    }

    private static String findGlassOreName(ItemStack stack) {
        boolean plainPane = false;
        for (int oreId : OreDictionary.getOreIDs(stack)) {
            String oreName = OreDictionary.getOreName(oreId);
            if (TIERED_GLASS_ORES.contains(oreName)) return oreName;
            plainPane |= PLAIN_PANE_ORE.equals(oreName);
        }
        return plainPane ? ANY_PANE_ORE : null;
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

            boolean needsCircuit = COSMETIC_SOLID_OUTPUT.equals(ItemId.create(Recipe.getOutput()));
            if (needsCircuit) {
                TCRecipeTools.addArcaneRecipeWithCircuit(
                    IIC,
                    createInputs(Recipe),
                    new ItemStack[] { Recipe.getOutput() },
                    Recipe.getInputAspects(),
                    Recipe.getResearch(),
                    INFUSION_ASPECTS,
                    INFUSION_RESEARCH,
                    TCRecipeTools.computeAspectDuration(
                        Recipe.getInputAspects(),
                        TCRecipeTools.INFUSION_DURATION_TICKS_PER_ASPECT),
                    TierEU.RECIPE_LV,
                    11);
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
