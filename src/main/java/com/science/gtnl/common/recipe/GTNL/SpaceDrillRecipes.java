package com.science.gtnl.common.recipe.GTNL;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.Utils.recipes.ResourceCollectionModuleTierKey;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.loader.RecipePool;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class SpaceDrillRecipes implements IRecipePool {

    public static final ResourceCollectionModuleTierKey MINER_TIER = ResourceCollectionModuleTierKey.INSTANCE;
    public static final RecipeMap<?> SDR = RecipePool.SpaceDrillRecipes;

    private static void addSpaceDrillOutputRecipe(int circuit, ItemStack drone, FluidStack fuel, FluidStack output,
        int minerTier, int duration, long eut) {
        GTValues.RA.stdBuilder()
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

    private static class FuelVariant {

        public final FluidStack fuel;
        public final int baseDuration;

        public FuelVariant(FluidStack fuel, int baseDuration) {
            this.fuel = fuel;
            this.baseDuration = baseDuration;
        }
    }

    private static class MinerTierData {

        public final int tier;
        public final ItemStack drone;
        public final int eut;

        public MinerTierData(int tier, ItemStack drone, int eut) {
            this.tier = tier;
            this.drone = drone;
            this.eut = eut;
        }
    }

    @Override
    public void loadRecipes() {
        int i;

        List<List<FluidStack>> tierOutputFluids = Collections.unmodifiableList(
            Arrays.asList(
                Collections.unmodifiableList(
                    Arrays.asList(
                        Materials.Hydrogen.getGas(10000000),
                        Materials.Helium.getGas(10000000),
                        Materials.Nitrogen.getGas(10000000),
                        Materials.Methane.getGas(10000000),
                        Materials.SulfurDioxide.getGas(10000000),
                        Materials.CarbonDioxide.getGas(10000000),
                        Materials.NitrogenDioxide.getGas(10000000),
                        Materials.Ammonia.getGas(10000000),
                        Materials.Chlorine.getGas(10000000),
                        Materials.Fluorine.getGas(10000000),
                        Materials.CarbonMonoxide.getGas(10000000),
                        Materials.Oxygen.getGas(10000000))),
                Collections.unmodifiableList(
                    Arrays.asList(
                        safeGetFluidStack("unknowwater"),
                        WerkstoffLoader.Neon.getFluidOrGas(10000000),
                        Materials.Argon.getGas(10000000),
                        WerkstoffLoader.Krypton.getFluidOrGas(10000000),
                        WerkstoffLoader.Xenon.getFluidOrGas(10000000),
                        Materials.Radon.getGas(10000000),
                        Materials.Helium_3.getGas(10000000))),
                Collections.unmodifiableList(
                    Arrays.asList(
                        Materials.Deuterium.getGas(10000000),
                        Materials.Tritium.getGas(10000000),
                        Materials.HeavyFuel.getFluid(10000000),
                        Materials.LightFuel.getFluid(10000000),
                        Materials.Naphtha.getFluid(10000000),
                        Materials.Gas.getGas(10000000),
                        new FluidStack(GTPPFluids.CoalGas, 10000000),
                        new FluidStack(MaterialsElements.getInstance().BROMINE.getFluid(), 10000000),
                        Materials.Methane.getGas(10000000),
                        Materials.Oil.getFluid(10000000),
                        Materials.OilHeavy.getFluid(10000000),
                        Materials.Lava.getFluid(10000000),
                        Materials.SaltWater.getFluid(10000000),
                        safeGetFluidStack("ic2distilledwater"),
                        safeGetFluidStack("pyrotheum"),
                        safeGetFluidStack("cryotheum"),
                        safeGetFluidStack("liquiddna"),
                        Materials.HydrochloricAcid.getFluid(10000000),
                        Materials.SulfuricAcid.getFluid(10000000),
                        Materials.NitricAcid.getFluid(10000000),
                        Materials.HydrofluoricAcid.getFluid(10000000),
                        Materials.PhosphoricAcid.getFluid(10000000),
                        Materials.PhthalicAcid.getFluid(10000000)))));

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
                new FuelVariant(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 6000), 300)),
            Arrays.asList(
                new FuelVariant(FluidUtils.getFluidStack(GTPPFluids.DenseHydrazineFuelMixture, 10000), 300),
                new FuelVariant(FluidUtils.getFluidStack(GTPPFluids.CN3H7O3RocketFuel, 6000), 150)),
            Arrays.asList(
                new FuelVariant(FluidUtils.getFluidStack(GTPPFluids.H8N4C2O4RocketFuel, 10000), 150),
                new FuelVariant(GGMaterial.thoriumBasedLiquidFuel.getFluidOrGas(6000), 75)));

        for (int tierIndex = 0; tierIndex < minerTiers.size(); tierIndex++) {
            MinerTierData tierData = minerTiers.get(tierIndex);
            List<FluidStack> outputs = tierIndex < tierOutputFluids.size() ? tierOutputFluids.get(tierIndex)
                : Collections.emptyList();
            List<FuelVariant> fuels = tierIndex < tierFuelVariants.size() ? tierFuelVariants.get(tierIndex)
                : Collections.emptyList();

            for (i = 0; i < outputs.size(); i++) {
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
            MaterialsUEVplus.WhiteDwarfMatter.getMolten(100000),
            6,
            750,
            TierEU.RECIPE_UXV);

        addSpaceDrillOutputRecipe(
            24,
            minerTiers.get(5).drone,
            GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(10000),
            MaterialsUEVplus.BlackDwarfMatter.getMolten(100000),
            6,
            750,
            TierEU.RECIPE_UXV);
    }

    private static FluidStack safeGetFluidStack(String name) {
        return FluidRegistry.getFluidStack(name, 10000000);
    }
}
