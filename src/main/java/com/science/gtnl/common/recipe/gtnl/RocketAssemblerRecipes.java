package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeBuilder.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.dreammaster.gthandler.CustomItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import galaxyspace.core.inventory.InventorySchematic;
import galaxyspace.core.recipe.RocketRecipes;
import galaxyspace.core.register.GSBlocks;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe;

public class RocketAssemblerRecipes implements IRecipePool {

    public static List<INasaWorkbenchRecipe> RECIPES_ROCKET_STEAM = new ArrayList<>();
    public RecipeMap<?> RAR = RecipePool.RocketAssemblerRecipes;

    public ItemStack[] itemStacks = new ItemStack[] {
        GTModHandler.getModItem(Mods.GalacticraftCore.ID, "item.schematic", 1, 1),
        GTModHandler.getModItem(Mods.GalacticraftMars.ID, "item.schematic", 1, 0),
        GTModHandler.getModItem(Mods.GalaxySpace.ID, "item.SchematicTier4", 1, 0),
        GTModHandler.getModItem(Mods.GalaxySpace.ID, "item.SchematicTier5", 1, 0),
        GTModHandler.getModItem(Mods.GalaxySpace.ID, "item.SchematicTier6", 1, 0),
        GTModHandler.getModItem(Mods.GalaxySpace.ID, "item.SchematicTier7", 1, 0),
        GTModHandler.getModItem(Mods.GalaxySpace.ID, "item.SchematicTier8", 1, 0) };

    public static void loadSteamRocketRecipe() {
        HashMap<Integer, ItemStack> input = new HashMap<>();
        HashMap<Integer, ItemStack> inputChest;
        input.put(1, GTModHandler.getModItem(Mods.StevesCarts2.ID, "CartModule", 1, 38));
        input.put(2, CustomItemList.EngineCore.get(1));
        input.put(3, GTModHandler.getModItem(Mods.IronTanks.ID, "diamondTank", 1));
        input.put(4, GTModHandler.getModItem(Mods.IronTanks.ID, "diamondTank", 1));

        input.put(7, GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.BlackSteel, 1));

        for (int i = 8; i <= 15; i++) {
            input.put(i, MaterialPool.CompressedSteam.get(OrePrefixes.plateSuperdense, 1));
        }

        input.put(16, GTModHandler.getModItem(Mods.GraviSuite.ID, "itemSimpleItem", 1, 6));

        for (int i = 17; i <= 20; i++) {
            input.put(i, GTModHandler.getModItem(Mods.Railcraft.ID, "machine.beta", 1, 7));
        }

        inputChest = new HashMap<>(input);
        inputChest.put(21, null);
        addSteamRocketRecipe(new NasaWorkbenchRecipe(GTNLItemList.SteamRocket.get(1), inputChest));
        inputChest = new HashMap<>(input);
        inputChest.put(21, new ItemStack(GSBlocks.ironChest, 1, 3));
        addSteamRocketRecipe(new NasaWorkbenchRecipe(GTNLItemList.SteamRocket.getWithMeta(1, 1), inputChest));
        inputChest = new HashMap<>(input);
        inputChest.put(21, new ItemStack(GSBlocks.ironChest));
        addSteamRocketRecipe(new NasaWorkbenchRecipe(GTNLItemList.SteamRocket.getWithMeta(1, 2), inputChest));
        inputChest = new HashMap<>(input);
        inputChest.put(21, new ItemStack(GSBlocks.ironChest, 1, 1));
        addSteamRocketRecipe(new NasaWorkbenchRecipe(GTNLItemList.SteamRocket.getWithMeta(1, 3), inputChest));

    }

    public static void addSteamRocketRecipe(INasaWorkbenchRecipe recipe) {
        RECIPES_ROCKET_STEAM.add(recipe);
    }

    public static List<INasaWorkbenchRecipe> getRocketSteamRecipes() {
        return RECIPES_ROCKET_STEAM;
    }

    public static ItemStack findMatchingSpaceshipSteamRecipe(InventorySchematic inventory) {
        for (INasaWorkbenchRecipe recipe : getRocketSteamRecipes()) {
            if (recipe.matches(inventory)) {
                return recipe.getRecipeOutput();
            }
        }
        return null;
    }

    @Override
    public void loadRecipes() {
        registerRocketRecipes(getRocketSteamRecipes(), 1);
        registerRocketRecipes(RocketRecipes.getRocketT1Recipes(), 1);
        registerRocketRecipes(RocketRecipes.getRocketT2Recipes(), 2);
        registerRocketRecipes(RocketRecipes.getRocketT3Recipes(), 3);
        registerRocketRecipes(RocketRecipes.getRocketT4Recipes(), 4);
        registerRocketRecipes(RocketRecipes.getRocketT5Recipes(), 5);
        registerRocketRecipes(RocketRecipes.getRocketT6Recipes(), 6);
        registerRocketRecipes(RocketRecipes.getRocketT7Recipes(), 7);
        registerRocketRecipes(RocketRecipes.getRocketT8Recipes(), 8);
    }

    public void registerRocketRecipes(List<INasaWorkbenchRecipe> recipes, int specialValue) {
        for (INasaWorkbenchRecipe recipe : recipes) {
            HashMap<Integer, ItemStack> inputs = recipe.getRecipeInput();

            int maxSlot = inputs.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0);
            ItemStack[] orderedInputs = new ItemStack[maxSlot];

            for (int slot = 1; slot <= maxSlot; slot++) {
                orderedInputs[slot - 1] = inputs.getOrDefault(slot, null);
            }

            RecipeBuilder recipeBuilder = new RecipeBuilder().itemInputsAllowNulls(orderedInputs)
                .itemOutputs(recipe.getRecipeOutput())
                .duration(recipe.getRecipeSize() * SECONDS)
                .specialValue(specialValue)
                .eut((int) TierEU.RECIPE_HV);

            if (specialValue - 2 >= 0) recipeBuilder.special(itemStacks[specialValue - 2]);

            recipeBuilder.addTo(RAR);
        }
    }

}
