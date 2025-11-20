package com.science.gtnl.common.recipe.gregtech.serverStart;

import java.util.HashSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ArrayListMultimap;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import bartworks.API.recipe.BartWorksRecipeMaps;
import bartworks.system.material.CircuitGeneration.CircuitImprintLoader;
import bartworks.util.BWUtil;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

public class CircuitAssemblyLineRecipes implements IRecipePool {

    public static ArrayListMultimap<NBTTagCompound, GTRecipe> recipeTagMap = ArrayListMultimap.create();
    public static HashSet<GTRecipe> ORIGINAL_CAL_RECIPES = new HashSet<>();
    public static HashSet<GTRecipe> MODIFIED_CAL_RECIPES = new HashSet<>();
    public static HashSet<GTRecipe> CONVERTED_CAL_RECIPES = new HashSet<>();

    @Override
    public void loadRecipes() {
        HashSet<GTRecipe> toRem = new HashSet<>();
        HashSet<GTRecipe> toAdd = new HashSet<>();
        deleteConvertedCALRecipes();
        rebuildCircuitAssemblerMap(toRem, toAdd);
        exchangeRecipesInList(toRem, toAdd);
    }

    private static void deleteConvertedCALRecipes() {
        BartWorksRecipeMaps.circuitAssemblyLineRecipes.getBackend()
            .removeRecipes(CONVERTED_CAL_RECIPES);
        recipeTagMap.clear();
        CONVERTED_CAL_RECIPES.clear();
    }

    private static void rebuildCircuitAssemblerMap(HashSet<GTRecipe> toRem, HashSet<GTRecipe> toAdd) {
        reAddOriginalRecipes();
        RecipePool.ConvertToCircuitAssembler.getAllRecipes()
            .forEach(e -> handleCircuitRecipeRebuilding(e, toRem, toAdd));
    }

    private static void exchangeRecipesInList(HashSet<GTRecipe> toRem, HashSet<GTRecipe> toAdd) {
        toAdd.forEach(RecipeMaps.circuitAssemblerRecipes::add);
        RecipeMaps.circuitAssemblerRecipes.getBackend()
            .removeRecipes(toRem);
        ORIGINAL_CAL_RECIPES.addAll(toRem);
        MODIFIED_CAL_RECIPES.addAll(toAdd);
    }

    private static void reAddOriginalRecipes() {
        RecipeMaps.circuitAssemblerRecipes.getBackend()
            .removeRecipes(MODIFIED_CAL_RECIPES);
        ORIGINAL_CAL_RECIPES.forEach(RecipeMaps.circuitAssemblerRecipes::add);
        ORIGINAL_CAL_RECIPES.clear();
        MODIFIED_CAL_RECIPES.clear();
    }

    private static void handleCircuitRecipeRebuilding(GTRecipe circuitRecipe, HashSet<GTRecipe> toRem,
        HashSet<GTRecipe> toAdd) {
        ItemStack[] outputs = circuitRecipe.mOutputs;
        if (outputs == null || outputs.length == 0 || outputs[0] == null) return;

        boolean isOrePass = isCircuitOreDict(outputs[0]);
        String unlocalizedName = outputs[0].getUnlocalizedName();
        if (isOrePass || unlocalizedName.contains("Circuit") || unlocalizedName.contains("circuit")) {

            CircuitImprintLoader.recipeTagMap
                .put(CircuitImprintLoader.getTagFromStack(outputs[0]), circuitRecipe.copy());

            Fluid solderIndalloy = FluidRegistry.getFluid("molten.indalloy140") != null
                ? FluidRegistry.getFluid("molten.indalloy140")
                : FluidRegistry.getFluid("molten.solderingalloy");

            Fluid solderUEV = FluidRegistry.getFluid("molten.mutatedlivingsolder") != null
                ? FluidRegistry.getFluid("molten.mutatedlivingsolder")
                : FluidRegistry.getFluid("molten.solderingalloy");

            FluidStack fluid = circuitRecipe.mFluidInputs != null && circuitRecipe.mFluidInputs.length > 0
                ? circuitRecipe.mFluidInputs[0]
                : null;

            if (fluid != null && (fluid.isFluidEqual(Materials.SolderingAlloy.getMolten(0))
                || fluid.isFluidEqual(new FluidStack(solderIndalloy, 0))
                || fluid.isFluidEqual(new FluidStack(solderUEV, 0)))) {

                GTRecipe newRecipe = CircuitImprintLoader.reBuildRecipe(circuitRecipe);
                if (newRecipe != null) {
                    BartWorksRecipeMaps.circuitAssemblyLineRecipes.addRecipe(newRecipe);
                    CONVERTED_CAL_RECIPES.add(newRecipe);
                    addCutoffRecipeToSets(toRem, toAdd, circuitRecipe);
                }

            } else if (circuitRecipe.mEUt > TierEU.IV) {
                toRem.add(circuitRecipe);
            }
        }
    }

    private static void addCutoffRecipeToSets(HashSet<GTRecipe> toRem, HashSet<GTRecipe> toAdd,
        GTRecipe circuitRecipe) {
        if (circuitRecipe.mEUt > TierEU.IV) {
            toRem.add(circuitRecipe);
            toAdd.add(circuitRecipe);
        }
    }

    private static boolean isCircuitOreDict(ItemStack item) {
        return BWUtil.isTieredCircuit(item) || BWUtil.getOreNames(item)
            .stream()
            .anyMatch(s -> "circuitPrimitiveArray".equals(s));
    }
}
