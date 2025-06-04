package com.science.gtnl.common.recipe.GTNL;

import static com.science.gtnl.loader.IScriptLoader.missing;
import static gregtech.api.enums.Mods.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

import com.dreammaster.item.NHItemList;
import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class FishingGroundRecipes implements IRecipePool {

    final RecipeMap<?> FGR = RecipeRegister.FishingGroundRecipes;

    @Override
    public void loadRecipes() {

        ItemStack ZeroPointModule = ItemList.ZPM.get(1);
        NBTTagCompound ZeroPointModuleType = ZeroPointModule.getTagCompound();
        if (ZeroPointModuleType != null) {
            ZeroPointModuleType.setLong("GT.ItemCharge", 2000000000000L);
        } else {
            ZeroPointModuleType = new NBTTagCompound();
            ZeroPointModuleType.setLong("GT.ItemCharge", 2000000000000L);
            ZeroPointModule.setTagCompound(ZeroPointModuleType);
        }

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), GTUtility.copyAmount(0, new ItemStack(Items.fishing_rod, 1)))
            .itemOutputs(
                new ItemStack(Items.fish, 16, 0),
                new ItemStack(Items.fish, 16, 1),
                new ItemStack(Items.fish, 16, 2),
                new ItemStack(Items.fish, 8, 3))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(2500, 2500, 2500, 1000)
            .specialValue(0)

            .duration(1000)
            .eut(512)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTUtility.copyAmount(0, new ItemStack(Items.fishing_rod, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatRaw, 16L))
            .itemOutputs(
                new ItemStack(Items.fish, 32, 0),
                new ItemStack(Items.fish, 32, 1),
                new ItemStack(Items.fish, 32, 2),
                new ItemStack(Items.fish, 16, 3))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(7500, 7500, 7500, 5000)
            .specialValue(0)

            .duration(1000)
            .eut(512)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTUtility.copyAmount(0, new ItemStack(Items.fishing_rod, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatCooked, 16L))
            .itemOutputs(
                new ItemStack(Items.fish, 64, 0),
                new ItemStack(Items.fish, 64, 1),
                new ItemStack(Items.fish, 64, 2),
                new ItemStack(Items.fish, 64, 3))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(9000, 9000, 9000, 9000)
            .specialValue(0)

            .duration(2000)
            .eut(512)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(7), ItemList.ActivatedCarbonFilterMesh.get(1))
            .itemOutputs(
                new ItemStack(Items.rotten_flesh, 16),
                new ItemStack(Items.bone, 16),
                new ItemStack(Items.stick, 16),
                new ItemStack(Items.string, 16),
                new ItemStack(Items.dye, 16, 0),
                new ItemStack(Items.feather, 12),
                new ItemStack(Items.gunpowder, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 12L),
                new ItemStack(Blocks.tripwire_hook, 6),
                new ItemStack(Items.bowl, 6),
                new ItemStack(Items.leather, 6),
                new ItemStack(Items.book, 4),
                new ItemStack(Items.fishing_rod, 1),
                new ItemStack(Items.bow, 1),
                new ItemStack(Items.leather_boots, 1),
                new ItemStack(Items.name_tag, 1),
                new ItemStack(Items.potionitem, 1, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.QuartzSand, 4L),
                new ItemStack(Items.clay_ball, 4),
                new ItemStack(Items.coal, 4, 0),
                new ItemStack(Items.sugar, 4),
                new ItemStack(Items.redstone, 4),
                new ItemStack(Items.dye, 4, 4),
                GTOreDictUnificator.get(OrePrefixes.nugget, Materials.PolyvinylChloride, 2L),
                new ItemStack(Items.gold_nugget, 2),
                new ItemStack(Items.diamond, 1),
                new ItemStack(Items.emerald, 1),
                new ItemStack(Items.golden_apple, 1),
                ZeroPointModule)
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(
                8000,
                8000,
                8000,
                8000,
                8000,
                7500,
                7500,
                7500,
                6000,
                6000,
                5500,
                5000,
                3000,
                3000,
                3000,
                1500,
                3000,
                4000,
                4000,
                4000,
                4000,
                4000,
                3500,
                4000,
                3000,
                3500,
                2000,
                2500,
                500,
                500,
                100,
                5)
            .specialValue(0)

            .duration(3000)
            .eut(2048)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTUtility.copyAmount(0, ItemList.ActivatedCarbonFilterMesh.get(1)))
            .itemOutputs(
                new ItemStack(Blocks.waterlily, 32),
                new ItemStack(Blocks.vine, 32),
                GTModHandler.getModItem(TwilightForest.ID, "tile.HugeLilyPad", 32),
                GTModHandler.getModItem(BiomesOPlenty.ID, "lilyBop", 32, 0, missing),
                GTModHandler.getModItem(BiomesOPlenty.ID, "lilyBop", 32, 1, missing),
                GTModHandler.getModItem(BiomesOPlenty.ID, "lilyBop", 32, 2, missing),
                GTModHandler.getModItem(BiomesOPlenty.ID, "coral1", 16, 12, missing),
                GTModHandler.getModItem(BiomesOPlenty.ID, "coral1", 16, 13, missing),
                GTModHandler.getModItem(BiomesOPlenty.ID, "coral1", 16, 14, missing),
                GTModHandler.getModItem(BiomesOPlenty.ID, "coral1", 16, 15, missing),
                GTModHandler.getModItem(PamsHarvestCraft.ID, "seaweedItem", 64, missing),
                GTModHandler.getModItem(PamsHarvestCraft.ID, "waterchestnutItem", 16, missing),
                GTModHandler.getModItem(PamsHarvestCraft.ID, "riceItem", 16, missing),
                GTModHandler.getModItem(PamsHarvestCraft.ID, "cranberryItem", 16, missing))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(6000, 6000, 3000, 4000, 4000, 4000, 2500, 2500, 2500, 2500, 7500, 5000, 5000, 5000)
            .specialValue(0)

            .duration(1500)
            .eut(2048)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(9), NHItemList.MaceratedPlantmass.getIS(16))
            .itemOutputs(
                GregtechItemList.AlgaeBiomass.get(64),
                GregtechItemList.GreenAlgaeBiomass.get(64),
                GregtechItemList.BrownAlgaeBiomass.get(64),
                GregtechItemList.GoldenBrownAlgaeBiomass.get(64),
                GregtechItemList.RedAlgaeBiomass.get(64))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .specialValue(0)

            .duration(200)
            .eut(2048)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 32L),
                GTModHandler.getModItem(PamsHarvestCraft.ID, "seaweedItem", 64, missing))
            .itemOutputs(
                GTModHandler.getModItem(GalaxySpace.ID, "tcetiedandelions", 64, 0, missing),
                GTModHandler.getModItem(GalaxySpace.ID, "tcetiedandelions", 64, 3, missing),
                GTModHandler.getModItem(GalaxySpace.ID, "tcetiedandelions", 64, 4, missing),
                GTModHandler.getModItem(GalaxySpace.ID, "tcetiedandelions", 64, 5, missing))
            .fluidInputs(FluidRegistry.getFluidStack("unknownnutrientagar", 4000))
            .specialValue(0)

            .duration(1000)
            .eut(122880)
            .addTo(FGR);

        if (PamsHarvestCraft.isModLoaded()) {

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(4),
                    GTUtility.copyAmount(0, new ItemStack(Items.fishing_rod, 1)))
                .itemOutputs(
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "anchovyrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "bassrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "calamarirawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "carprawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "catfishrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "charrrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "clamrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "crabrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "crayfishrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "eelrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "frograwItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "greenheartfishItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "grouperrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "herringrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "jellyfishrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "mudfishrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "octopusrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "perchrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "scalloprawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "shrimprawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "snailrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "snapperrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "tilapiarawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "troutrawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "tunarawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "turtlerawItem", 16),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "walleyerawItem", 16))
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
                .specialValue(0)

                .duration(1000)
                .eut(512)
                .addTo(FGR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(5),
                    GTUtility.copyAmount(0, new ItemStack(Items.fishing_rod, 1)),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatRaw, 16L))
                .itemOutputs(
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "anchovyrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "bassrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "calamarirawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "carprawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "catfishrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "charrrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "clamrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "crabrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "crayfishrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "eelrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "frograwItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "greenheartfishItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "grouperrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "herringrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "jellyfishrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "mudfishrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "octopusrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "perchrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "scalloprawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "shrimprawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "snailrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "snapperrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "tilapiarawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "troutrawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "tunarawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "turtlerawItem", 32),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "walleyerawItem", 32))
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
                .specialValue(0)

                .duration(1000)
                .eut(512)
                .addTo(FGR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(6),
                    GTUtility.copyAmount(0, new ItemStack(Items.fishing_rod, 1)),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatCooked, 16L))
                .itemOutputs(
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "anchovyrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "bassrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "calamarirawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "carprawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "catfishrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "charrrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "clamrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "crabrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "crayfishrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "eelrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "frograwItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "greenheartfishItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "grouperrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "herringrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "jellyfishrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "mudfishrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "octopusrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "perchrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "scalloprawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "shrimprawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "snailrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "snapperrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "tilapiarawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "troutrawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "tunarawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "turtlerawItem", 64),
                    GTModHandler.getModItem(PamsHarvestCraft.ID, "walleyerawItem", 64))
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
                .specialValue(0)

                .duration(1000)
                .eut(512)
                .addTo(FGR);

        }
    }
}
