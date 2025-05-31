package com.science.gtnl.common.recipe.GTNL;

import static gregtech.api.enums.Mods.*;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.Utils.recipes.ResourceCollectionModuleTierKey;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsOres;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class SpaceMinerRecipes implements IRecipePool {

    final ResourceCollectionModuleTierKey MINER_TIER = ResourceCollectionModuleTierKey.INSTANCE;
    final RecipeMap<?> SMR = RecipeRegister.SpaceMinerRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Naquadah, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chromite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Plutonium, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.NaquadahEnriched, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Trinium, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Indium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Draconium, 1)),
                GTUtility
                    .copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.DraconiumAwakened, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.ElectrumFlux, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Naquadah, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chromite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Plutonium, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.NaquadahEnriched, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Trinium, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Indium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Draconium, 1)),
                GTUtility
                    .copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.DraconiumAwakened, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.ElectrumFlux, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Scheelite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tungstate, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lithium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tellurium, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tungsten, 1)),
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pitchblende, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bismuth, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Antimony, 1)),
                GTUtility.copyAmountUnsafe(70, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gallium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Scheelite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tungstate, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lithium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tellurium, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tungsten, 1)),
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pitchblende, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bismuth, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Antimony, 1)),
                GTUtility.copyAmountUnsafe(70, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gallium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(22),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Garnierite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Nickel, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cobaltite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pentlandite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Platinum, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Palladium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Rutile, 1)),
                GTUtility.copyAmountUnsafe(300, new ItemStack(Blocks.clay, 1)),
                GTUtility.copyAmountUnsafe(60, MaterialsOres.CROCROITE.getOre(1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(22),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Garnierite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Nickel, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cobaltite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pentlandite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Platinum, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Palladium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Rutile, 1)),
                GTUtility.copyAmountUnsafe(300, new ItemStack(Blocks.clay, 1)),
                GTUtility.copyAmountUnsafe(60, MaterialsOres.CROCROITE.getOre(1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(21),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(40, WerkstoffLoader.PTMetallicPowder.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Graphite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Diamond, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Coal, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Titanium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ardite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Manyullyn, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Opal, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(21),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(40, WerkstoffLoader.PTMetallicPowder.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Graphite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Diamond, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Coal, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Titanium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ardite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Manyullyn, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Opal, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(20),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Oilsands, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gold, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedGold, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bauxite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ilmenite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Aluminium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BlueTopaz, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Osmium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Iridium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(20),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Oilsands, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gold, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedGold, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bauxite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ilmenite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Aluminium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BlueTopaz, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Osmium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Iridium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(19),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Magnetite, 1)),
                GTUtility
                    .copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.VanadiumMagnetite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.CassiteriteSand, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GarnetSand, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Asbestos, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Diatomite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Shadow, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.NetherStar, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Quantium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(19),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Magnetite, 1)),
                GTUtility
                    .copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.VanadiumMagnetite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.CassiteriteSand, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GarnetSand, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Asbestos, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Diatomite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Shadow, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.NetherStar, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Quantium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(18),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Apatite, 1)),
                GTUtility
                    .copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.TricalciumPhosphate, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrochlore, 1)),
                GTUtility.copyAmountUnsafe(300, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sulfur, 1)),
                GTUtility.copyAmountUnsafe(200, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrite, 1)),
                GTUtility.copyAmountUnsafe(100, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sphalerite, 1)),
                GTUtility.copyAmountUnsafe(150, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lignite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cadmium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Zinc, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(18),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Apatite, 1)),
                GTUtility
                    .copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.TricalciumPhosphate, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrochlore, 1)),
                GTUtility.copyAmountUnsafe(300, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sulfur, 1)),
                GTUtility.copyAmountUnsafe(200, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrite, 1)),
                GTUtility.copyAmountUnsafe(100, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sphalerite, 1)),
                GTUtility.copyAmountUnsafe(150, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lignite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cadmium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Zinc, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Redstone, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ruby, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cinnabar, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.NetherQuartz, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Quartzite, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lanthanum, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Niobium, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ytterbium, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Yttrium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Redstone, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ruby, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cinnabar, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.NetherQuartz, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Quartzite, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lanthanum, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Niobium, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ytterbium, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Yttrium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(16),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(150, GTOreDictUnificator.get(OrePrefixes.ore, Materials.RockSalt, 1)),
                GTUtility.copyAmountUnsafe(10, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Salt, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lepidolite, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Spodumene, 1)),
                GTUtility.copyAmountUnsafe(140, WerkstoffLoader.Djurleit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(70, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Silicon, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.SiliconSG, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Saltpeter, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(16),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(150, GTOreDictUnificator.get(OrePrefixes.ore, Materials.RockSalt, 1)),
                GTUtility.copyAmountUnsafe(10, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Salt, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lepidolite, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Spodumene, 1)),
                GTUtility.copyAmountUnsafe(140, WerkstoffLoader.Djurleit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(70, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Silicon, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.SiliconSG, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Saltpeter, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(15),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(210, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BlueTopaz, 1)),
                GTUtility.copyAmountUnsafe(140, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Topaz, 1)),
                GTUtility
                    .copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BasalticMineralSand, 1)),
                GTUtility
                    .copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GraniticMineralSand, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.FullersEarth, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gypsum, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Phosphate, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Void, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thaumium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(15),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(210, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BlueTopaz, 1)),
                GTUtility.copyAmountUnsafe(140, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Topaz, 1)),
                GTUtility
                    .copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BasalticMineralSand, 1)),
                GTUtility
                    .copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GraniticMineralSand, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.FullersEarth, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gypsum, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Phosphate, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Void, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thaumium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(14),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Barite, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GarnetRed, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GarnetYellow, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Amethyst, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GreenSapphire, 1)),
                GTUtility.copyAmountUnsafe(20, WerkstoffLoader.Roquesit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Naquadria, 1)),
                GTUtility
                    .copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.MysteriousCrystal, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Oriharukon, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(14),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Barite, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GarnetRed, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GarnetYellow, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Amethyst, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GreenSapphire, 1)),
                GTUtility.copyAmountUnsafe(20, WerkstoffLoader.Roquesit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Naquadria, 1)),
                GTUtility
                    .copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.MysteriousCrystal, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Oriharukon, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(13),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Desh, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.CertusQuartz, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BrownLimonite, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cassiterite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BandedIron, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gold, 1)),
                GTUtility.copyAmountUnsafe(40, GGMaterial.orundum.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Uranium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Uranium235, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(13),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Desh, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.CertusQuartz, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BrownLimonite, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cassiterite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BandedIron, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gold, 1)),
                GTUtility.copyAmountUnsafe(40, GGMaterial.orundum.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Uranium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Uranium235, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(12),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(140, MaterialsOres.ZIRCON.getOre(1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.YellowLimonite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Kyanite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Mica, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bauxite, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Almandine, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Plutonium241, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Tiberium.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(40, WerkstoffLoader.Thorianit.get(OrePrefixes.ore, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(12),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(140, MaterialsOres.ZIRCON.getOre(1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.YellowLimonite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Kyanite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Mica, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bauxite, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Almandine, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Plutonium241, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Tiberium.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(40, WerkstoffLoader.Thorianit.get(OrePrefixes.ore, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Galena, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Silver, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lead, 1)),
                GTUtility.copyAmountUnsafe(100, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Molybdenite, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Molybdenum, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Magnesium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Manganese, 1)),
                GTUtility.copyAmountUnsafe(50, WerkstoffLoader.Fluorspar.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Vanadium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Galena, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Silver, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lead, 1)),
                GTUtility.copyAmountUnsafe(100, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Molybdenite, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Molybdenum, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Magnesium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Manganese, 1)),
                GTUtility.copyAmountUnsafe(50, WerkstoffLoader.Fluorspar.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Vanadium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lazurite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sodalite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lapis, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Calcite, 1)),
                GTUtility.copyAmountUnsafe(150, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Wulfenite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Quantium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Europium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Samarium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Strontium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lazurite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sodalite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lapis, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Calcite, 1)),
                GTUtility.copyAmountUnsafe(150, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Wulfenite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Quantium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Europium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Samarium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Strontium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(9), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Grossular, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrolusite, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tantalite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Magnetite, 1)),
                GTUtility
                    .copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.VanadiumMagnetite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gold, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.HeeEndium, 1)),
                GTUtility.copyAmountUnsafe(60, GTModHandler.getModItem(HardcoreEnderExpansion.ID, "end_powder_ore", 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cheese, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(9), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Grossular, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrolusite, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tantalite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Magnetite, 1)),
                GTUtility
                    .copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.VanadiumMagnetite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Gold, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.HeeEndium, 1)),
                GTUtility.copyAmountUnsafe(60, GTModHandler.getModItem(HardcoreEnderExpansion.ID, "end_powder_ore", 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cheese, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(8), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Beryllium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Emerald, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chalcopyrite, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Iron, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrite, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Copper, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Arsenic, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Barium, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lepidolite, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(8), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Beryllium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Emerald, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chalcopyrite, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Iron, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrite, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Copper, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Arsenic, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Barium, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lepidolite, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(7), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Saltpeter, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Diatomite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Electrotine, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Alunite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Coal, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Rubidium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ledox, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.CallistoIce, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Borax, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(7), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Saltpeter, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Diatomite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Electrotine, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Alunite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Coal, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Rubidium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ledox, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.CallistoIce, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Borax, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(6), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(250, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chalcopyrite, 1)),
                GTUtility.copyAmountUnsafe(10, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Zeolite, 1)),
                GTUtility.copyAmountUnsafe(10, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cassiterite, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Realgar, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cinnabar, 1)),
                GTUtility
                    .copyAmountUnsafe(80, GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockSkyStone", 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Americium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Dilithium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.MeteoricIron, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(6), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(250, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chalcopyrite, 1)),
                GTUtility.copyAmountUnsafe(10, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Zeolite, 1)),
                GTUtility.copyAmountUnsafe(10, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cassiterite, 1)),
                GTUtility.copyAmountUnsafe(50, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Realgar, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cinnabar, 1)),
                GTUtility
                    .copyAmountUnsafe(80, GTModHandler.getModItem(AppliedEnergistics2.ID, "tile.BlockSkyStone", 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Americium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Dilithium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.MeteoricIron, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(5), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Redstone, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ruby, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Grossular, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Spessartine, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Draconium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tantalite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Thulium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tantalum, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lutetium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(5), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Redstone, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ruby, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Grossular, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Spessartine, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Draconium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tantalite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Thulium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tantalum, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Lutetium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(4), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Soapstone, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Talc, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Glauconite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pentlandite, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Neodymium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Monazite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Adamantium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Vinteum, 1)),
                GTUtility.copyAmountUnsafe(60, MaterialsAlloy.KOBOLDITE.getOre(1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(4), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Soapstone, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Talc, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Glauconite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pentlandite, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Neodymium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Monazite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Adamantium, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Vinteum, 1)),
                GTUtility.copyAmountUnsafe(60, MaterialsAlloy.KOBOLDITE.getOre(1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(3), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bastnasite, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Molybdenum, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BrownLimonite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.YellowLimonite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BandedIron, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Malachite, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Holmium, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ichorium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.ShadowIron, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(3), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bastnasite, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Molybdenum, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BrownLimonite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.YellowLimonite, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BandedIron, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Malachite, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Holmium, 1)),
                GTUtility.copyAmountUnsafe(30, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ichorium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.ShadowIron, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Almandine, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrope, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sapphire, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GreenSapphire, 1)),
                GTUtility.copyAmountUnsafe(70, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Stibnite, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Uraninite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedAir, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedEarth, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedFire, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Almandine, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrope, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sapphire, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GreenSapphire, 1)),
                GTUtility.copyAmountUnsafe(70, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Stibnite, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Uraninite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedAir, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedEarth, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedFire, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(280, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tetrahedrite, 1)),
                GTUtility.copyAmountUnsafe(140, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Copper, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bentonite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Magnetite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Olivine, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GlauconiteSand, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedWater, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedEntropy, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedOrder, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(280, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tetrahedrite, 1)),
                GTUtility.copyAmountUnsafe(140, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Copper, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bentonite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Magnetite, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Olivine, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.GlauconiteSand, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedWater, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedEntropy, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfusedOrder, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(1),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(280, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tin, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cerium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Promethium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Praseodymium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Scandium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Dysprosium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Jasper, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tanzanite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Vulcanite, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(1),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(280, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tin, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Cerium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Promethium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Praseodymium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Scandium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Dysprosium, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Jasper, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tanzanite, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Vulcanite, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(2),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Jade, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Mithril, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tritanium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.DarkIron, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Firestone, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.FoolsRuby, 1)),
                GTUtility.copyAmountUnsafe(100, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Duralumin, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Forcicium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Forcillium, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(2),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Jade, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Mithril, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tritanium, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.DarkIron, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Firestone, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.FoolsRuby, 1)),
                GTUtility.copyAmountUnsafe(100, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Duralumin, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Forcicium, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Forcillium, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(3),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Orichalcum, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Olivine, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Vyroxeres, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Perlite, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chrysotile, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Trona, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Mirabilite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.DeepIron, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Electrum, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(3),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Orichalcum, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Olivine, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Vyroxeres, 1)),
                GTUtility.copyAmountUnsafe(240, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Perlite, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chrysotile, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Trona, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Mirabilite, 1)),
                GTUtility.copyAmountUnsafe(60, GTOreDictUnificator.get(OrePrefixes.ore, Materials.DeepIron, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Electrum, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(4),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, MaterialsOres.LAUTARITE.getOre(1)),
                GTUtility.copyAmountUnsafe(60, MaterialsOres.LEPERSONNITE.getOre(1)),
                GTUtility.copyAmountUnsafe(60, MaterialsOres.GADOLINITE_Y.getOre(1)),
                GTUtility.copyAmountUnsafe(40, MaterialsOres.ZIRCON.getOre(1)),
                GTUtility.copyAmountUnsafe(60, MaterialsOres.HONEAITE.getOre(1)),
                GTUtility.copyAmountUnsafe(40, MaterialsOres.ALBURNITE.getOre(1)),
                GTUtility.copyAmountUnsafe(50, MaterialMisc.RARE_EARTH_LOW.getOre(1)),
                GTUtility.copyAmountUnsafe(50, MaterialMisc.RARE_EARTH_MID.getOre(1)),
                GTUtility.copyAmountUnsafe(50, MaterialMisc.RARE_EARTH_HIGH.getOre(1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(4),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, MaterialsOres.LAUTARITE.getOre(1)),
                GTUtility.copyAmountUnsafe(60, MaterialsOres.LEPERSONNITE.getOre(1)),
                GTUtility.copyAmountUnsafe(60, MaterialsOres.GADOLINITE_Y.getOre(1)),
                GTUtility.copyAmountUnsafe(40, MaterialsOres.ZIRCON.getOre(1)),
                GTUtility.copyAmountUnsafe(60, MaterialsOres.HONEAITE.getOre(1)),
                GTUtility.copyAmountUnsafe(40, MaterialsOres.ALBURNITE.getOre(1)),
                GTUtility.copyAmountUnsafe(50, MaterialMisc.RARE_EARTH_LOW.getOre(1)),
                GTUtility.copyAmountUnsafe(50, MaterialMisc.RARE_EARTH_MID.getOre(1)),
                GTUtility.copyAmountUnsafe(50, MaterialMisc.RARE_EARTH_HIGH.getOre(1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(5),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bismutite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FluorBuergerit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.DescloiziteCUVO4.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.DescloiziteZNVO4.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Fayalit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Forsterit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FuchsitCR.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FuchsitAL.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Djurleit.get(OrePrefixes.ore, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(5),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bismutite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FluorBuergerit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.DescloiziteCUVO4.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.DescloiziteZNVO4.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Fayalit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Forsterit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FuchsitCR.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.FuchsitAL.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Djurleit.get(OrePrefixes.ore, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(6),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Wittichenit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.ChromoAluminoPovondrait.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.VanadioOxyDravit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Olenit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.RedZircon.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Hedenbergit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Prasiolite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(40, WerkstoffLoader.BArTiMaEuSNeK.get(OrePrefixes.ore, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(6),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Bornite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Wittichenit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.ChromoAluminoPovondrait.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.VanadioOxyDravit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Olenit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.RedZircon.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Hedenbergit.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(60, WerkstoffLoader.Prasiolite.get(OrePrefixes.ore, 1)),
                GTUtility.copyAmountUnsafe(40, WerkstoffLoader.BArTiMaEuSNeK.get(OrePrefixes.ore, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(7),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tartarite, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Neutronium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.CosmicNeutronium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BlackPlutonium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bedrockium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfinityCatalyst, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ichorium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Flerovium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.TengamRaw, 1)))
            .fluidInputs(Materials.GasolinePremium.getFluid(10000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(600)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemUtils.getIntegratedCircuitPlus(7),
                GTUtility.copyAmountUnsafe(0, ItemList.MiningDroneUV.get(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Tartarite, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Neutronium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.CosmicNeutronium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.BlackPlutonium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Bedrockium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.InfinityCatalyst, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Ichorium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.Flerovium, 1)),
                GTUtility.copyAmountUnsafe(20, GTOreDictUnificator.get(OrePrefixes.ore, Materials.TengamRaw, 1)))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000))
            .specialValue(1)
            .metadata(MINER_TIER, 1)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SMR);

    }
}
