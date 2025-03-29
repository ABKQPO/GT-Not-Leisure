package com.science.gtnl.loader;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.common.machine.OreProcessing.OP_NormalProcessing;
import com.science.gtnl.common.machine.multiMachineClasses.GTNLProcessingArrayRecipeLoader;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.common.recipe.GTNL.CellRegulatorRecipes;
import com.science.gtnl.common.recipe.GTNL.DecayHastenerRecipes;
import com.science.gtnl.common.recipe.GTNL.DesulfurizerRecipes;
import com.science.gtnl.common.recipe.GTNL.ElementCopyingRecipes;
import com.science.gtnl.common.recipe.GTNL.FishingGroundRecipes;
import com.science.gtnl.common.recipe.GTNL.FuelRefiningComplexRecipes;
import com.science.gtnl.common.recipe.GTNL.IsaMillRecipes;
import com.science.gtnl.common.recipe.GTNL.ManaInfusionRecipes;
import com.science.gtnl.common.recipe.GTNL.MatterFabricatorRecipes;
import com.science.gtnl.common.recipe.GTNL.MolecularTransformerRecipes;
import com.science.gtnl.common.recipe.GTNL.NaquadahReactorRecipes;
import com.science.gtnl.common.recipe.GTNL.NatureSpiritArrayRecipes;
import com.science.gtnl.common.recipe.GTNL.PetrochemicalPlantRecipes;
import com.science.gtnl.common.recipe.GTNL.PlatinumBasedTreatmentRecipes;
import com.science.gtnl.common.recipe.GTNL.ShallowChemicalCouplingRecipes;
import com.science.gtnl.common.recipe.GTNL.PortalToAlfheimRecipes;
import com.science.gtnl.common.recipe.GTNL.RareEarthCentrifugalRecipes;
import com.science.gtnl.common.recipe.GTNL.ReFusionReactorRecipes;
import com.science.gtnl.common.recipe.GTNL.RealArtificialStarRecipes;
import com.science.gtnl.common.recipe.GTNL.SmeltingMixingFurnaceRecipes;
import com.science.gtnl.common.recipe.GTNL.SpaceDrillRecipes;
import com.science.gtnl.common.recipe.GTNL.SpaceMinerRecipes;
import com.science.gtnl.common.recipe.GTNL.SteamCrackerRecipes;
import com.science.gtnl.common.recipe.GTNL.TheTwilightForestRecipes;
import com.science.gtnl.common.recipe.GregTech.AlloyBlastSmelterRecipes;
import com.science.gtnl.common.recipe.GregTech.AlloySmelterRecipes;
import com.science.gtnl.common.recipe.GregTech.AssemblerRecipes;
import com.science.gtnl.common.recipe.GregTech.AssemblingLineRecipes;
import com.science.gtnl.common.recipe.GregTech.BacterialVatRecipes;
import com.science.gtnl.common.recipe.GregTech.BlastFurnaceRecipes;
import com.science.gtnl.common.recipe.GregTech.CentrifugeRecipes;
import com.science.gtnl.common.recipe.GregTech.ChemicalBathRecipes;
import com.science.gtnl.common.recipe.GregTech.ChemicalDehydratorRecipes;
import com.science.gtnl.common.recipe.GregTech.ChemicalRecipes;
import com.science.gtnl.common.recipe.GregTech.CompressorRecipes;
import com.science.gtnl.common.recipe.GregTech.CrackingRecipes;
import com.science.gtnl.common.recipe.GregTech.CraftingTableRecipes;
import com.science.gtnl.common.recipe.GregTech.CuttingRecipes;
import com.science.gtnl.common.recipe.GregTech.DigesterRecipes;
import com.science.gtnl.common.recipe.GregTech.DissolutionTankRecipes;
import com.science.gtnl.common.recipe.GregTech.DistillationTowerRecipes;
import com.science.gtnl.common.recipe.GregTech.DistilleryRecipes;
import com.science.gtnl.common.recipe.GregTech.DragonEvolutionFusionCraftingRecipes;
import com.science.gtnl.common.recipe.GregTech.ElectrolyzerRecipes;
import com.science.gtnl.common.recipe.GregTech.FluidExtraction;
import com.science.gtnl.common.recipe.GregTech.FluidExtractorRecipes;
import com.science.gtnl.common.recipe.GregTech.FusionReactorRecipes;
import com.science.gtnl.common.recipe.GregTech.LaserEngraverRecipes;
import com.science.gtnl.common.recipe.GregTech.MixerRecipes;
import com.science.gtnl.common.recipe.GregTech.PCBFactoryRecipes;
import com.science.gtnl.common.recipe.GregTech.PlasmaForgeRecipes;
import com.science.gtnl.common.recipe.GregTech.PreciseAssemblerRecipes;
import com.science.gtnl.common.recipe.GregTech.SpaceAssemblerRecipes;
import com.science.gtnl.common.recipe.GregTech.TranscendentPlasmaMixerRecipes;
import com.science.gtnl.common.recipe.GregTech.VacuumFurnaceRecipes;
import com.science.gtnl.common.recipe.GregTech.multiDehydratorRecipes;
import com.science.gtnl.common.recipe.IRecipePool;
import com.science.gtnl.common.recipe.Special.PortalToAlfheimOreRecipes;
import com.science.gtnl.common.recipe.Thaumcraft.InfusionCraftingRecipesPool;
import com.science.gtnl.common.recipe.Thaumcraft.ShapedArcaneCraftingRecipesPool;

import goodgenerator.util.CrackRecipeAdder;

public class RecipeLoader {

    public static void loadRecipes() {
        IRecipePool[] recipePools = new IRecipePool[] { new ChemicalRecipes(), new ElectrolyzerRecipes(),
            new MixerRecipes(), new multiDehydratorRecipes(), new AssemblerRecipes(), new AlloyBlastSmelterRecipes(),
            new AssemblingLineRecipes(), new CompressorRecipes(), new ReFusionReactorRecipes(),
            new RealArtificialStarRecipes(), new PortalToAlfheimRecipes(), new NatureSpiritArrayRecipes(),
            new ManaInfusionRecipes(), new TranscendentPlasmaMixerRecipes(), new PlasmaForgeRecipes(),
            new CraftingTableRecipes(), new ChemicalBathRecipes(), new SteamCrackerRecipes(), new DesulfurizerRecipes(),
            new PetrochemicalPlantRecipes(), new FusionReactorRecipes(), new SmeltingMixingFurnaceRecipes(),
            new FluidExtraction(), new DigesterRecipes(), new DissolutionTankRecipes(), new CentrifugeRecipes(),
            new ChemicalDehydratorRecipes(), new RareEarthCentrifugalRecipes(), new MatterFabricatorRecipes(),
            new TheTwilightForestRecipes(), new IsaMillRecipes(), new CellRegulatorRecipes(),
            new VacuumFurnaceRecipes(), new FishingGroundRecipes(), new DistilleryRecipes(),
            new ElementCopyingRecipes(), new AlloySmelterRecipes(), new MolecularTransformerRecipes(),
            new NaquadahReactorRecipes(), new DragonEvolutionFusionCraftingRecipes(), new LaserEngraverRecipes(),
            new BacterialVatRecipes(), new CuttingRecipes(), new BlastFurnaceRecipes(), new FluidExtractorRecipes(),
            new DecayHastenerRecipes(), new PreciseAssemblerRecipes(), new FuelRefiningComplexRecipes(),
            new CrackingRecipes(), new DistillationTowerRecipes(), new SpaceMinerRecipes(), new SpaceDrillRecipes(),
            new SpaceAssemblerRecipes(), new PCBFactoryRecipes(), new PlatinumBasedTreatmentRecipes(),
            new ShallowChemicalCouplingRecipes() };

        new OP_NormalProcessing().enumOreProcessingRecipes();
        new ShapedArcaneCraftingRecipesPool().loadRecipes();
        new InfusionCraftingRecipesPool().loadRecipes();
        GTNLProcessingArrayRecipeLoader.registerDefaultGregtechMaps();

        CrackRecipeAdder.reAddBlastRecipe(MaterialPool.MolybdenumDisilicide, 800, 1920, 2300, true);
        CrackRecipeAdder.reAddBlastRecipe(MaterialPool.HSLASteel, 1000, 480, 1711, true);
        CrackRecipeAdder.reAddBlastRecipe(MaterialPool.Germaniumtungstennitride, 800, 30720, 8200, true);

        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

        for (ItemStack stone : OreDictionary.getOres("stone")) {
            PortalToAlfheimOreRecipes.addManaInfusionOreRecipes(stone);
        }
    }
}
