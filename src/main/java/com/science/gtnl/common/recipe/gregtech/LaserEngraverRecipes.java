package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import tectech.thing.CustomItemList;

public class LaserEngraverRecipes implements IRecipePool {

    public RecipeMap<?> lER = RecipeMaps.laserEngraverRecipes;
    public RecipeMap<?> HOR = GTNLRecipeMaps.HardOverrideRecipes;

    public void recipeWithPurifiedWater(ItemStack[] inputs, ItemStack[] outputs, Materials lowTierWater,
        Materials highTierWater, int duration, int boostedDuration, long eut) {

        RecipeBuilder.builder()
            .itemInputs(inputs)
            .itemOutputs(outputs)
            .fluidInputs(lowTierWater.getFluid(100L))
            .duration(duration)
            .eut(eut)
            .addTo(lER);

        RecipeBuilder.builder()
            .itemInputs(inputs)
            .itemOutputs(outputs)
            .fluidInputs(highTierWater.getFluid(100L))
            .duration(boostedDuration)
            .eut(eut)
            .addTo(lER);
    }

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.NinefoldInputHatchUHV.get(1), ItemList.Quantum_Tank_IV.get(1))
            .itemOutputs(GTNLItemList.HumongousNinefoldInputHatch.get(1))
            .duration(200)
            .eut(7864320)
            .addTo(lER);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.NeutroniumWafer.get(1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.EnderPearl, 0))
            .itemOutputs(GTUtility.copyAmountUnsafe(64, ItemList.Circuit_Wafer_NAND.get(1)))
            .fluidInputs(Materials.Grade5PurifiedWater.getFluid(100))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(lER);

        recipeWithPurifiedWater(
            new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.EnderPearl, 0) },
            new ItemStack[] { ItemList.Circuit_Wafer_NAND.get(64) },
            Materials.Grade5PurifiedWater,
            Materials.Grade6PurifiedWater,
            200,
            100,
            TierEU.RECIPE_IV);

        recipeWithPurifiedWater(
            new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.EnderEye, 0) },
            new ItemStack[] { ItemList.Circuit_Wafer_NOR.get(64) },
            Materials.Grade5PurifiedWater,
            Materials.Grade6PurifiedWater,
            200,
            100,
            TierEU.RECIPE_IV);

        recipeWithPurifiedWater(
            new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.NetherStar, 0) },
            new ItemStack[] { ItemList.Circuit_Wafer_SoC2.get(16) },
            Materials.Grade5PurifiedWater,
            Materials.Grade6PurifiedWater,
            500,
            250,
            TierEU.RECIPE_IV);

        RecipeBuilder.builder()
            .itemInputs(
                GregtechItemList.Laser_Lens_Special.get(0),
                new ItemStack(Items.iron_ingot, 1),
                new ItemStack(Items.diamond, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.ExtraUtilities.ID, "unstableingot", 1))
            .duration(100)
            .eut(TierEU.RECIPE_LuV)
            .addTo(lER);

        RecipeBuilder.builder()
            .itemInputs(
                GregtechItemList.Laser_Lens_Special.get(0),
                new ItemStack(Blocks.iron_block, 1),
                new ItemStack(Blocks.diamond_block, 1))
            .itemOutputs(GTModHandler.getModItem(Mods.ExtraUtilities.ID, "decorativeBlock1", 1, 5))
            .duration(900)
            .eut(TierEU.RECIPE_LuV)
            .addTo(lER);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLItemList.NeutroniumBoule.get(1L),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.lens, 0),
                WerkstoffLoader.Fayalit.get(OrePrefixes.lens, 0),
                Mods.NewHorizonsCoreMod.isModLoaded() ? getMysteriousCrystalLens()
                    : GTOreDictUnificator.get(OrePrefixes.lens, Materials.GreenSapphire, 0))
            .itemOutputs(ItemList.Circuit_Silicon_Ingot6.get(2))
            .fluidInputs(Materials.UUMatter.getFluid(16000L))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .requiresCleanRoom()
            .addTo(lER);

        loadWirelessEnergyCoverRecipes();
    }

    @Optional.Method(modid = "dreamcraft")
    public ItemStack getMysteriousCrystalLens() {
        return com.dreammaster.gthandler.CustomItemList.MysteriousCrystalLens.get(0);
    }

    public void loadWirelessEnergyCoverRecipes() {
        for (int j = 0; j < 14; j++) {
            for (int i = 0; i < 13; i++) {
                if (j < 4 && i >= 4) continue;
                ItemStack energyDetector = i >= 4 ? CustomItemList.Machine_Multi_Transformer.get(1)
                    : ItemList.Cover_EnergyDetector.get(1);

                GTNLItemList[][] energyHatch;
                int hatchIndex;
                if (j < 4 || i < 4) {
                    energyHatch = GTNLItemList.ENERGY_HATCH;
                    hatchIndex = i;
                } else {
                    energyHatch = GTNLItemList.LASER_ENERGY_HATCH;
                    hatchIndex = i - 4;
                }

                GTNLItemList[] energyCover = i >= 2 ? GTNLItemList.WIRELESS_ENERGY_COVER_4A
                    : GTNLItemList.WIRELESS_ENERGY_COVER;

                RecipeBuilder.builder()
                    .itemInputs(
                        energyHatch[j >= 4 && i >= 4 ? j - 4 : j][hatchIndex].get(1),
                        energyCover[j].get(Math.min(1L << (i >= 2 ? i - 2 : i), 4L)),
                        energyDetector)
                    .itemOutputs(GTNLItemList.WIRELESS_ENERGY_HATCHES[j][i].get(1))
                    .duration(200)
                    .eut(GTValues.VP[j + 1])
                    .addTo(lER)
                    .addTo(HOR);
            }
        }
    }
}
