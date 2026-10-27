package com.science.gtnl.compat.pamsharvestcraft;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.compat.newhorizons.NHCoreRecipes;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;

public class PamsHarvestCraftRecipes {

    private PamsHarvestCraftRecipes() {}

    public static void fishingGround() {
        if (!Mods.PamsHarvestCraft.isModLoaded()) return;

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.fishing_rod, 0))
            .circuit(4)
            .itemOutputs(
                ModsItemlist.PamsHarvestCraftAnchovyrawItem.get(16),
                ModsItemlist.PamsHarvestCraftBassrawItem.get(16),
                ModsItemlist.PamsHarvestCraftCalamarirawItem.get(16),
                ModsItemlist.PamsHarvestCraftCarprawItem.get(16),
                ModsItemlist.PamsHarvestCraftCatfishrawItem.get(16),
                ModsItemlist.PamsHarvestCraftCharrrawItem.get(16),
                ModsItemlist.PamsHarvestCraftClamrawItem.get(16),
                ModsItemlist.PamsHarvestCraftCrabrawItem.get(16),
                ModsItemlist.PamsHarvestCraftCrayfishrawItem.get(16),
                ModsItemlist.PamsHarvestCraftEelrawItem.get(16),
                ModsItemlist.PamsHarvestCraftFrograwItem.get(16),
                ModsItemlist.PamsHarvestCraftGreenheartfishItem.get(16),
                ModsItemlist.PamsHarvestCraftGrouperrawItem.get(16),
                ModsItemlist.PamsHarvestCraftHerringrawItem.get(16),
                ModsItemlist.PamsHarvestCraftJellyfishrawItem.get(16),
                ModsItemlist.PamsHarvestCraftMudfishrawItem.get(16),
                ModsItemlist.PamsHarvestCraftOctopusrawItem.get(16),
                ModsItemlist.PamsHarvestCraftPerchrawItem.get(16),
                ModsItemlist.PamsHarvestCraftScalloprawItem.get(16),
                ModsItemlist.PamsHarvestCraftShrimprawItem.get(16),
                ModsItemlist.PamsHarvestCraftSnailrawItem.get(16),
                ModsItemlist.PamsHarvestCraftSnapperrawItem.get(16),
                ModsItemlist.PamsHarvestCraftTilapiarawItem.get(16),
                ModsItemlist.PamsHarvestCraftTroutrawItem.get(16),
                ModsItemlist.PamsHarvestCraftTunarawItem.get(16),
                ModsItemlist.PamsHarvestCraftTurtlerawItem.get(16),
                ModsItemlist.PamsHarvestCraftWalleyerawItem.get(16))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500,
                2500)
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNLRecipeMaps.FishingGroundRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.fishing_rod, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatRaw, 16L))
            .circuit(5)
            .itemOutputs(
                ModsItemlist.PamsHarvestCraftAnchovyrawItem.get(32),
                ModsItemlist.PamsHarvestCraftBassrawItem.get(32),
                ModsItemlist.PamsHarvestCraftCalamarirawItem.get(32),
                ModsItemlist.PamsHarvestCraftCarprawItem.get(32),
                ModsItemlist.PamsHarvestCraftCatfishrawItem.get(32),
                ModsItemlist.PamsHarvestCraftCharrrawItem.get(32),
                ModsItemlist.PamsHarvestCraftClamrawItem.get(32),
                ModsItemlist.PamsHarvestCraftCrabrawItem.get(32),
                ModsItemlist.PamsHarvestCraftCrayfishrawItem.get(32),
                ModsItemlist.PamsHarvestCraftEelrawItem.get(32),
                ModsItemlist.PamsHarvestCraftFrograwItem.get(32),
                ModsItemlist.PamsHarvestCraftGreenheartfishItem.get(32),
                ModsItemlist.PamsHarvestCraftGrouperrawItem.get(32),
                ModsItemlist.PamsHarvestCraftHerringrawItem.get(32),
                ModsItemlist.PamsHarvestCraftJellyfishrawItem.get(32),
                ModsItemlist.PamsHarvestCraftMudfishrawItem.get(32),
                ModsItemlist.PamsHarvestCraftOctopusrawItem.get(32),
                ModsItemlist.PamsHarvestCraftPerchrawItem.get(32),
                ModsItemlist.PamsHarvestCraftScalloprawItem.get(32),
                ModsItemlist.PamsHarvestCraftShrimprawItem.get(32),
                ModsItemlist.PamsHarvestCraftSnailrawItem.get(32),
                ModsItemlist.PamsHarvestCraftSnapperrawItem.get(32),
                ModsItemlist.PamsHarvestCraftTilapiarawItem.get(32),
                ModsItemlist.PamsHarvestCraftTroutrawItem.get(32),
                ModsItemlist.PamsHarvestCraftTunarawItem.get(32),
                ModsItemlist.PamsHarvestCraftTurtlerawItem.get(32),
                ModsItemlist.PamsHarvestCraftWalleyerawItem.get(32))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500,
                7500)
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNLRecipeMaps.FishingGroundRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Items.fishing_rod, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatCooked, 16L))
            .circuit(6)
            .itemOutputs(
                ModsItemlist.PamsHarvestCraftAnchovyrawItem.get(64),
                ModsItemlist.PamsHarvestCraftBassrawItem.get(64),
                ModsItemlist.PamsHarvestCraftCalamarirawItem.get(64),
                ModsItemlist.PamsHarvestCraftCarprawItem.get(64),
                ModsItemlist.PamsHarvestCraftCatfishrawItem.get(64),
                ModsItemlist.PamsHarvestCraftCharrrawItem.get(64),
                ModsItemlist.PamsHarvestCraftClamrawItem.get(64),
                ModsItemlist.PamsHarvestCraftCrabrawItem.get(64),
                ModsItemlist.PamsHarvestCraftCrayfishrawItem.get(64),
                ModsItemlist.PamsHarvestCraftEelrawItem.get(64),
                ModsItemlist.PamsHarvestCraftFrograwItem.get(64),
                ModsItemlist.PamsHarvestCraftGreenheartfishItem.get(64),
                ModsItemlist.PamsHarvestCraftGrouperrawItem.get(64),
                ModsItemlist.PamsHarvestCraftHerringrawItem.get(64),
                ModsItemlist.PamsHarvestCraftJellyfishrawItem.get(64),
                ModsItemlist.PamsHarvestCraftMudfishrawItem.get(64),
                ModsItemlist.PamsHarvestCraftOctopusrawItem.get(64),
                ModsItemlist.PamsHarvestCraftPerchrawItem.get(64),
                ModsItemlist.PamsHarvestCraftScalloprawItem.get(64),
                ModsItemlist.PamsHarvestCraftShrimprawItem.get(64),
                ModsItemlist.PamsHarvestCraftSnailrawItem.get(64),
                ModsItemlist.PamsHarvestCraftSnapperrawItem.get(64),
                ModsItemlist.PamsHarvestCraftTilapiarawItem.get(64),
                ModsItemlist.PamsHarvestCraftTroutrawItem.get(64),
                ModsItemlist.PamsHarvestCraftTunarawItem.get(64),
                ModsItemlist.PamsHarvestCraftTurtlerawItem.get(64),
                ModsItemlist.PamsHarvestCraftWalleyerawItem.get(64))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000,
                9000)
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNLRecipeMaps.FishingGroundRecipes);

        NHCoreRecipes.fishingGround();
    }
}
