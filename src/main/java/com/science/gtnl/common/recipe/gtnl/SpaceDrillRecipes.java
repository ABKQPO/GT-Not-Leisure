package com.science.gtnl.common.recipe.gtnl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.ResourceCollectionModuleMetadata;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialsElements;

public class SpaceDrillRecipes implements IRecipePool {

    public ResourceCollectionModuleMetadata MINER_TIER = ResourceCollectionModuleMetadata.INSTANCE;
    public RecipeMap<?> SDR = GTNLRecipeMaps.SpaceDrillRecipes;

    public void addSpaceDrillOutputRecipe(int circuit, ItemStack drone, FluidStack fuel, FluidStack output,
        int minerTier, int duration, long eut) {
        RecipeBuilder.builder()
            .itemInputs(
                (circuit < 25) ? GTUtility.getIntegratedCircuit(circuit)
                    : ItemUtils.getIntegratedCircuitPlus(circuit - 25),
                GTUtility.copyAmountUnsafe(0, drone))
            .fluidInputs(fuel)
            .fluidOutputs(output)
            .specialValue(1)
            .metadata(MINER_TIER, minerTier)
            .duration(duration)
            .eut(eut)
            .addTo(SDR);
    }

    @Desugar
    public record FuelVariant(FluidStack fuel, int baseDuration) {}

    @Desugar
    public record MinerTierData(int tier, ItemStack drone, int eut) {}

    @Override
    public void loadRecipes() {
        List<List<FluidStack>> tierOutputFluids = List.of(
            Collections.unmodifiableList(
                Arrays.asList(
                    Materials.Hydrogen.getGas(50000000),
                    Materials.Helium.getGas(50000000),
                    Materials.Nitrogen.getGas(50000000),
                    Materials.Methane.getGas(5000000),
                    Materials.SulfurDioxide.getGas(5000000),
                    Materials.CarbonDioxide.getGas(5000000),
                    Materials.NitrogenDioxide.getGas(5000000),
                    Materials.Ammonia.getGas(5000000),
                    Materials.Chlorine.getGas(50000000),
                    Materials.Fluorine.getGas(50000000),
                    Materials.CarbonMonoxide.getGas(50000000),
                    Materials.Oxygen.getGas(50000000))),
            Collections.unmodifiableList(
                Arrays.asList(
                    FluidRegistry.getFluidStack("unknowwater", 500000),
                    WerkstoffLoader.Neon.getFluidOrGas(500000),
                    Materials.Argon.getGas(500000),
                    WerkstoffLoader.Krypton.getFluidOrGas(500000),
                    WerkstoffLoader.Xenon.getFluidOrGas(500000),
                    Materials.Radon.getGas(500000),
                    Materials.Helium3.getGas(50000000))),
            Collections.unmodifiableList(
                Arrays.asList(
                    Materials.Deuterium.getGas(5000000),
                    Materials.Tritium.getGas(5000000),
                    Materials.HeavyFuel.getFluid(5000000),
                    Materials.LightFuel.getFluid(5000000),
                    Materials.Naphtha.getFluid(5000000),
                    Materials.Gas.getGas(50000000),
                    new FluidStack(GTPPFluids.CoalGas, 5000000),
                    new FluidStack(MaterialsElements.getInstance().BROMINE.getFluid(), 5000000),
                    Materials.Oil.getFluid(5000000),
                    Materials.OilHeavy.getFluid(500000),
                    Materials.Lava.getFluid(50000000),
                    Materials.SaltWater.getFluid(50000000),
                    GTModHandler.getDistilledWater(50000000),
                    new FluidStack(GTPPFluids.Pyrotheum, 500000),
                    new FluidStack(GTPPFluids.Cryotheum, 500000),
                    GTModHandler.getLiquidDNA(500000),
                    Materials.HydrochloricAcid.getFluid(5000000),
                    Materials.SulfuricAcid.getFluid(5000000),
                    Materials.NitricAcid.getFluid(5000000),
                    Materials.HydrofluoricAcid.getFluid(5000000),
                    Materials.PhosphoricAcid.getFluid(5000000),
                    Materials.PhthalicAcid.getFluid(5000000))));

        List<MinerTierData> minerTiers = Arrays.asList(
            new MinerTierData(1, ItemList.MiningDroneUV.get(1), (int) TierEU.RECIPE_UV),
            new MinerTierData(2, ItemList.MiningDroneUHV.get(1), (int) TierEU.RECIPE_UHV),
            new MinerTierData(3, ItemList.MiningDroneUEV.get(1), (int) TierEU.RECIPE_UEV),
            new MinerTierData(4, ItemList.MiningDroneUIV.get(1), (int) TierEU.RECIPE_UIV),
            new MinerTierData(5, ItemList.MiningDroneUMV.get(1), (int) TierEU.RECIPE_UMV),
            new MinerTierData(6, ItemList.MiningDroneUXV.get(1), (int) TierEU.RECIPE_UXV));

        List<List<FuelVariant>> tierFuelVariants = Arrays.asList(
            Arrays.asList(
                new FuelVariant(Materials.GasolinePremium.getFluid(10000), 600),
                new FuelVariant(new FluidStack(GTPPFluids.RP1RocketFuel, 6000), 300)),
            Arrays.asList(
                new FuelVariant(new FluidStack(GTPPFluids.DenseHydrazineFuelMixture, 10000), 300),
                new FuelVariant(new FluidStack(GTPPFluids.CN3H7O3RocketFuel, 6000), 150)),
            Arrays.asList(
                new FuelVariant(new FluidStack(GTPPFluids.H8N4C2O4RocketFuel, 10000), 150),
                new FuelVariant(GGMaterial.thoriumBasedLiquidFuel.getFluidOrGas(6000), 75)));

        for (int tierIndex = 0; tierIndex < minerTiers.size(); tierIndex++) {
            MinerTierData tierData = minerTiers.get(tierIndex);
            List<FluidStack> outputs = tierIndex < tierOutputFluids.size() ? tierOutputFluids.get(tierIndex)
                : Collections.emptyList();
            List<FuelVariant> fuels = tierIndex < tierFuelVariants.size() ? tierFuelVariants.get(tierIndex)
                : Collections.emptyList();

            for (int i = 0; i < outputs.size(); i++) {
                FluidStack output = outputs.get(i);
                if (output == null) continue;
                for (FuelVariant fuelVariant : fuels) {
                    if (fuelVariant.fuel == null) continue;
                    int duration = fuelVariant.baseDuration / tierData.tier;
                    addSpaceDrillOutputRecipe(
                        i,
                        tierData.drone,
                        fuelVariant.fuel,
                        output,
                        tierData.tier,
                        duration,
                        tierData.eut);
                }
            }
        }

        addSpaceDrillOutputRecipe(
            23,
            minerTiers.get(5).drone,
            GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(10000),
            Materials.WhiteDwarfMatter.getMolten(50000),
            6,
            750,
            TierEU.RECIPE_UXV);

        addSpaceDrillOutputRecipe(
            24,
            minerTiers.get(5).drone,
            GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(10000),
            Materials.BlackDwarfMatter.getMolten(50000),
            6,
            750,
            TierEU.RECIPE_UXV);
    }
}
