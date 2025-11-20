package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeBuilder.MINUTES;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.material.RecipePool;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsBotania;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class ElementCopyingRecipes implements IRecipePool {

    public RecipeMap<?> ECR = RecipePool.ElementCopyingRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potassium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Rubidium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Caesium, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(Materials.Hydrogen.getGas(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Beryllium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Strontium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barium, 16),
                MaterialsElements.getInstance().RADIUM.getDust(16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Scandium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Yttrium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Dysprosium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Terbium, 16),
                MaterialPool.Actinium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 16),
                WerkstoffLoader.Zirconium.get(OrePrefixes.dust, 16),
                MaterialsElements.getInstance().HAFNIUM.getDust(16),
                MaterialPool.Rutherfordium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Erbium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tantalum, 16),
                MaterialPool.Dubnium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thulium, 16),
                MaterialPool.Seaborgium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ytterbium, 16),
                MaterialPool.Technetium.get(OrePrefixes.dust, 16),
                MaterialsElements.getInstance().RHENIUM.getDust(16),
                MaterialPool.Bohrium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 16),
                MaterialsElements.getInstance().RUTHENIUM.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 16),
                MaterialPool.Hassium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(Materials.Oxygen.getGas(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 16),
                WerkstoffLoader.Rhodium.get(OrePrefixes.dust, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 16),
                MaterialPool.Meitnerium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(Materials.Nitrogen.getGas(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 16),
                MaterialPool.Nihonium.get(OrePrefixes.dust, 16),
                MaterialPool.Darmstadtium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(Materials.Argon.getGas(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 16),
                MaterialPool.Roentgenium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(WerkstoffLoader.Xenon.getFluidOrGas(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cadmium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 16),
                MaterialPool.Copernicium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(WerkstoffLoader.Krypton.getFluidOrGas(16000), Materials.Mercury.getFluid(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Boron, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 16),
                MaterialsElements.getInstance().THALLIUM.getDust(16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(WerkstoffLoader.Oganesson.getFluidOrGas(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 16),
                MaterialsElements.getInstance().GERMANIUM.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Flerovium, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 16),
                MaterialPool.Moscovium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 16),
                MaterialsElements.getInstance().SELENIUM.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tellurium, 16),
                MaterialsElements.getInstance().POLONIUM.getDust(16),
                MaterialPool.Livermorium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 16),
                WerkstoffMaterialPool.Iodine.get(OrePrefixes.dust, 16),
                MaterialPool.Astatine.get(OrePrefixes.dust, 16),
                MaterialPool.Tennessine.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(
                new FluidStack(MaterialsElements.getInstance().BROMINE.getFluid(), 16000),
                Materials.Fluorine.getGas(16000),
                Materials.Chlorine.getGas(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 16),
                MaterialPool.Francium.get(OrePrefixes.dust, 16),
                MaterialsElements.getInstance().PROTACTINIUM.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .fluidOutputs(
                MaterialsElements.getInstance().NEON.getFluidStack(16000),
                Materials.Helium.getGas(16000),
                Materials.Radon.getGas(16000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                MaterialsElements.getInstance().NEPTUNIUM.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 16),
                MaterialsElements.getInstance().CURIUM.getDust(16),
                MaterialPool.Berkelium.get(OrePrefixes.dust, 16),
                WerkstoffLoader.Californium.get(OrePrefixes.dust, 16),
                MaterialPool.Einsteinium.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                MaterialsElements.getInstance().FERMIUM.getDust(16),
                MaterialPool.Mendelevium.get(OrePrefixes.dust, 16),
                MaterialPool.Nobelium.get(OrePrefixes.dust, 16),
                MaterialPool.Lawrencium.get(OrePrefixes.dust, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Praseodymium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Promethium, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedOrder, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Forcillium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Forcicium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 16),
                WerkstoffLoader.Tiberium.get(OrePrefixes.dust, 16),
                GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Force, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vinteum, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Orichalcum, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.Eternity, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.Universium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.MagMatter, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.TranscendentMetal, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.SixPhasedCopper, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.SpaceTime, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.Creon, 16),
                GGMaterial.metastableOganesson.get(OrePrefixes.dust, 16),
                GGMaterial.shirabon.get(OrePrefixes.dust, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 16),
                GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.dust, 16),
                MaterialsElements.STANDALONE.HYPOGEN.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CosmicNeutronium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.TengamPurified, 16),
                GGMaterial.orundum.get(OrePrefixes.dust, 16),
                MaterialsElements.STANDALONE.DRAGON_METAL.getDust(16),
                MaterialsElements.STANDALONE.RHUGNOR.getDust(16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                MaterialsAlloy.ENERGYCRYSTAL.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vyroxeres, 16),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vyroxeres, 16),
                MaterialsElements.STANDALONE.RUNITE.getDust(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ceruclase, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tartarite, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsBotania.Terrasteel, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsBotania.ElvenElementium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsBotania.Manasteel, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsBotania.BotaniaDragonstone, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsBotania.Livingrock, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsBotania.Livingwood, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsBotania.GaiaSpirit, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, MaterialsBotania.Dreamwood, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.FierySteel, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steeleaf, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.IronWood, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Knightmetal, 16))
            .fluidInputs(Materials.UUMatter.getFluid(1))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(ECR);
    }
}
