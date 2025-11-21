package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class IndustrialRockCrusherRecipes implements IRecipePool {

    public RecipeMap<?> IRCR = RecipePool.IndustrialRockCrusherRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.stone, 0))
            .itemOutputs(new ItemStack(Blocks.stone, 1))
            .duration(16)
            .eut(TierEU.RECIPE_ULV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.cobblestone, 0))
            .itemOutputs(new ItemStack(Blocks.cobblestone, 1))
            .duration(16)
            .eut(TierEU.RECIPE_ULV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.netherrack, 0))
            .itemOutputs(new ItemStack(Blocks.netherrack, 1))
            .duration(16)
            .eut(TierEU.RECIPE_ULV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.redstone, 0))
            .itemOutputs(new ItemStack(Blocks.obsidian, 1))
            .duration(16)
            .eut(TierEU.RECIPE_HV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(GregTechAPI.sBlockGranites, 0, 0))
            .itemOutputs(new ItemStack(GregTechAPI.sBlockGranites, 1, 0))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(GregTechAPI.sBlockGranites, 0, 1))
            .itemOutputs(new ItemStack(GregTechAPI.sBlockGranites, 1, 1))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(GregTechAPI.sBlockGranites, 0, 8))
            .itemOutputs(new ItemStack(GregTechAPI.sBlockGranites, 1, 8))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(GregTechAPI.sBlockGranites, 0, 9))
            .itemOutputs(new ItemStack(GregTechAPI.sBlockGranites, 1, 9))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(GregTechAPI.sBlockStones, 0, 0))
            .itemOutputs(new ItemStack(GregTechAPI.sBlockStones, 1, 0))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(GregTechAPI.sBlockStones, 0, 1))
            .itemOutputs(new ItemStack(GregTechAPI.sBlockStones, 1, 1))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(GregTechAPI.sBlockStones, 0, 8))
            .itemOutputs(new ItemStack(GregTechAPI.sBlockStones, 1, 8))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(GregTechAPI.sBlockStones, 0, 9))
            .itemOutputs(new ItemStack(GregTechAPI.sBlockStones, 1, 9))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "deepslate", 0))
            .itemOutputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "deepslate", 1))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "cobbled_deepslate", 0))
            .itemOutputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "cobbled_deepslate", 1))
            .duration(16)
            .eut(TierEU.RECIPE_EV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "blackstone", 0))
            .itemOutputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "blackstone", 1))
            .duration(16)
            .eut(TierEU.RECIPE_HV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Mods.Botania.ID, "stone", 0, 0))
            .itemOutputs(GTModHandler.getModItem(Mods.Botania.ID, "stone", 1, 0))
            .duration(16)
            .eut(TierEU.RECIPE_MV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Mods.Botania.ID, "stone", 0, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.Botania.ID, "stone", 1, 1))
            .duration(16)
            .eut(TierEU.RECIPE_MV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Mods.Botania.ID, "stone", 0, 2))
            .itemOutputs(GTModHandler.getModItem(Mods.Botania.ID, "stone", 1, 2))
            .duration(16)
            .eut(TierEU.RECIPE_MV)
            .addTo(IRCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Mods.Botania.ID, "stone", 0, 3))
            .itemOutputs(GTModHandler.getModItem(Mods.Botania.ID, "stone", 1, 3))
            .duration(16)
            .eut(TierEU.RECIPE_MV)
            .addTo(IRCR);
    }
}
