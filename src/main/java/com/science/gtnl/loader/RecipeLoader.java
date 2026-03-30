package com.science.gtnl.loader;

import java.util.Collection;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.fluids.FluidStack;

import com.Nxer.TwistSpaceTechnology.common.recipeMap.GTCMRecipe;
import com.Nxer.TwistSpaceTechnology.recipe.machineRecipe.expanded.AssemblyLineWithoutResearchRecipePool;
import com.Nxer.TwistSpaceTechnology.recipe.machineRecipe.expanded.CircuitAssemblyLineWithoutImprintRecipePool;
import com.reavaritia.utils.enums.ReAvaItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.item.items.Stick;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.bloodMagic.MeteorsRecipes;
import com.science.gtnl.common.recipe.botania.BotaniaManaInfusionRecipes;
import com.science.gtnl.common.recipe.gregtech.AlloyBlastSmelterRecipes;
import com.science.gtnl.common.recipe.gregtech.AlloySmelterRecipes;
import com.science.gtnl.common.recipe.gregtech.AssemblerRecipes;
import com.science.gtnl.common.recipe.gregtech.AssemblingLineRecipes;
import com.science.gtnl.common.recipe.gregtech.AutoclaveRecipes;
import com.science.gtnl.common.recipe.gregtech.BacterialVatRecipes;
import com.science.gtnl.common.recipe.gregtech.BlastFurnaceRecipes;
import com.science.gtnl.common.recipe.gregtech.CentrifugeRecipes;
import com.science.gtnl.common.recipe.gregtech.ChemicalBathRecipes;
import com.science.gtnl.common.recipe.gregtech.ChemicalDehydratorRecipes;
import com.science.gtnl.common.recipe.gregtech.ChemicalPlantRecipes;
import com.science.gtnl.common.recipe.gregtech.ChemicalRecipes;
import com.science.gtnl.common.recipe.gregtech.CircuitAssemblerConvertRecipes;
import com.science.gtnl.common.recipe.gregtech.CircuitAssemblyLineRecipes;
import com.science.gtnl.common.recipe.gregtech.CompressorRecipes;
import com.science.gtnl.common.recipe.gregtech.CrackingRecipes;
import com.science.gtnl.common.recipe.gregtech.CuttingRecipes;
import com.science.gtnl.common.recipe.gregtech.CyclotronRecipes;
import com.science.gtnl.common.recipe.gregtech.DigesterRecipes;
import com.science.gtnl.common.recipe.gregtech.DissolutionTankRecipes;
import com.science.gtnl.common.recipe.gregtech.DistillationTowerRecipes;
import com.science.gtnl.common.recipe.gregtech.DistilleryRecipes;
import com.science.gtnl.common.recipe.gregtech.DragonEvolutionFusionCraftingRecipes;
import com.science.gtnl.common.recipe.gregtech.ElectricImplosionCompressorRecipes;
import com.science.gtnl.common.recipe.gregtech.ElectrolyzerRecipes;
import com.science.gtnl.common.recipe.gregtech.FluidCannerRecipes;
import com.science.gtnl.common.recipe.gregtech.FluidExtraction;
import com.science.gtnl.common.recipe.gregtech.FluidExtractorRecipes;
import com.science.gtnl.common.recipe.gregtech.FluidSolidifierRecipes;
import com.science.gtnl.common.recipe.gregtech.FormingPressRecipes;
import com.science.gtnl.common.recipe.gregtech.FusionReactorRecipes;
import com.science.gtnl.common.recipe.gregtech.HammerRecipes;
import com.science.gtnl.common.recipe.gregtech.LaserEngraverRecipes;
import com.science.gtnl.common.recipe.gregtech.MaceratorRecipes;
import com.science.gtnl.common.recipe.gregtech.MixerRecipes;
import com.science.gtnl.common.recipe.gregtech.NanoForgeRecipes;
import com.science.gtnl.common.recipe.gregtech.NuclearSaltProcessingPlantRecipes;
import com.science.gtnl.common.recipe.gregtech.PCBFactoryRecipes;
import com.science.gtnl.common.recipe.gregtech.PlasmaForgeRecipes;
import com.science.gtnl.common.recipe.gregtech.PreciseAssemblerRecipes;
import com.science.gtnl.common.recipe.gregtech.QuantumForceTransformerRecipes;
import com.science.gtnl.common.recipe.gregtech.ReactorProcessingUnitRecipes;
import com.science.gtnl.common.recipe.gregtech.SpaceAssemblerRecipes;
import com.science.gtnl.common.recipe.gregtech.TargetChamberRecipes;
import com.science.gtnl.common.recipe.gregtech.TranscendentPlasmaMixerRecipes;
import com.science.gtnl.common.recipe.gregtech.VacuumFreezerRecipes;
import com.science.gtnl.common.recipe.gregtech.VacuumFurnaceRecipes;
import com.science.gtnl.common.recipe.gtnl.AlchemicChemistrySetRecipes;
import com.science.gtnl.common.recipe.gtnl.BloodDemonInjectionRecipes;
import com.science.gtnl.common.recipe.gtnl.CactusWonderFakeRecipes;
import com.science.gtnl.common.recipe.gtnl.CellRegulatorRecipes;
import com.science.gtnl.common.recipe.gtnl.CircuitNanitesDataRecipes;
import com.science.gtnl.common.recipe.gtnl.CraftingTableRecipes;
import com.science.gtnl.common.recipe.gtnl.DecayHastenerRecipes;
import com.science.gtnl.common.recipe.gtnl.DesulfurizerRecipes;
import com.science.gtnl.common.recipe.gtnl.ElectricNeutronActivatorRecipes;
import com.science.gtnl.common.recipe.gtnl.ElectrocellGeneratorRecipes;
import com.science.gtnl.common.recipe.gtnl.ElementCopyingRecipes;
import com.science.gtnl.common.recipe.gtnl.EternalGregTechWorkshopUpgradeRecipes;
import com.science.gtnl.common.recipe.gtnl.FallingTowerRecipes;
import com.science.gtnl.common.recipe.gtnl.FishingGroundRecipes;
import com.science.gtnl.common.recipe.gtnl.FuelRefiningComplexRecipes;
import com.science.gtnl.common.recipe.gtnl.GasCollectorRecipes;
import com.science.gtnl.common.recipe.gtnl.GrandAssemblyLineSpecialRecipes;
import com.science.gtnl.common.recipe.gtnl.IndustrialRockCrusherRecipes;
import com.science.gtnl.common.recipe.gtnl.InfernalCokeRecipes;
import com.science.gtnl.common.recipe.gtnl.InfusionCraftingRecipes;
import com.science.gtnl.common.recipe.gtnl.IsaMillRecipes;
import com.science.gtnl.common.recipe.gtnl.LavaMakerRecipes;
import com.science.gtnl.common.recipe.gtnl.ManaInfusionRecipes;
import com.science.gtnl.common.recipe.gtnl.MatterFabricatorRecipes;
import com.science.gtnl.common.recipe.gtnl.MicroorganismMasterRecipes;
import com.science.gtnl.common.recipe.gtnl.MolecularTransformerRecipes;
import com.science.gtnl.common.recipe.gtnl.NanitesIntegratedProcessingRecipes;
import com.science.gtnl.common.recipe.gtnl.NaquadahReactorRecipes;
import com.science.gtnl.common.recipe.gtnl.NatureSpiritArrayRecipes;
import com.science.gtnl.common.recipe.gtnl.PetrochemicalPlantRecipes;
import com.science.gtnl.common.recipe.gtnl.PlatinumBasedTreatmentRecipes;
import com.science.gtnl.common.recipe.gtnl.PortalToAlfheimRecipes;
import com.science.gtnl.common.recipe.gtnl.PrecisionLaserEngraver;
import com.science.gtnl.common.recipe.gtnl.PrimitiveBrickKilnRecipes;
import com.science.gtnl.common.recipe.gtnl.RareEarthCentrifugalRecipes;
import com.science.gtnl.common.recipe.gtnl.ReFusionReactorRecipes;
import com.science.gtnl.common.recipe.gtnl.RealArtificialStarRecipes;
import com.science.gtnl.common.recipe.gtnl.RockBreakerRecipes;
import com.science.gtnl.common.recipe.gtnl.RocketAssemblerRecipes;
import com.science.gtnl.common.recipe.gtnl.RuneAltarRecipes;
import com.science.gtnl.common.recipe.gtnl.ShallowChemicalCouplingRecipes;
import com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes;
import com.science.gtnl.common.recipe.gtnl.ShimmerRecipes;
import com.science.gtnl.common.recipe.gtnl.SmeltingMixingFurnaceRecipes;
import com.science.gtnl.common.recipe.gtnl.SolarMuonCatalystRecipes;
import com.science.gtnl.common.recipe.gtnl.SpaceDrillRecipes;
import com.science.gtnl.common.recipe.gtnl.SpaceMinerRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamCarpenterRecipe;
import com.science.gtnl.common.recipe.gtnl.SteamCrackerRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamExtractinatorRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamFusionReactorRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamGateAssemblerRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamManufacturerRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamWeatherModuleRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamWoodcutterRecipes;
import com.science.gtnl.common.recipe.gtnl.TheTwilightForestRecipes;
import com.science.gtnl.common.recipe.gtnl.TreeDiagramRecipes;
import com.science.gtnl.common.recipe.thaumcraft.TCResearches;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.machine.ProcessingArrayRecipeLoader;
import com.science.gtnl.utils.machine.oreProcessing.CheatOreProcessingRecipes;
import com.science.gtnl.utils.recipes.RecipeUtil;
import com.science.gtnl.utils.recipes.RemoveRecipes;

import bartworks.API.recipe.BartWorksRecipeMaps;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.VillagerRegistry;
import goodgenerator.util.CrackRecipeAdder;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Mods;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import tectech.thing.CustomItemList;

public class RecipeLoader {

    public static boolean recipesAdded;

    public static void loadPostInit() {
        if (MainConfig.recipe.enableDeleteRecipe) {
            RemoveRecipes.removeCircuitAssemblerRecipes();
        }

        IRecipePool[] recipePools = new IRecipePool[] { new CircuitAssemblerConvertRecipes(),
            new GrandAssemblyLineSpecialRecipes() };

        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

        RecipeUtil.copyAllRecipes(GTNLRecipeMaps.ConvertToCircuitAssemblerRecipes, RecipeMaps.circuitAssemblerRecipes);
    }

    public static void loadServerStart() {
        RecipeUtil
            .removeMatchingRecipes(GTNLRecipeMaps.ConvertToCircuitAssemblerRecipes, RecipeMaps.circuitAssemblerRecipes);
        if (recipesAdded) return;
        if (MainConfig.recipe.enableDeleteRecipe) {
            RemoveRecipes.removeRecipes();
        }

        ProcessingArrayRecipeLoader.registerDefaultGregtechMaps();

        CrackRecipeAdder.reAddBlastRecipe(GTNLMaterials.MolybdenumDisilicide, 800, 1920, 2300, true);
        CrackRecipeAdder.reAddBlastRecipe(GTNLMaterials.HSLASteel, 1000, 480, 1711, true);
        CrackRecipeAdder.reAddBlastRecipe(GTNLMaterials.Germaniumtungstennitride, 800, 30720, 8200, true);

        if (MainConfig.recipe.enableChamberRecipesBuff) {
            loadBuffTargetChamberRecipe();
        }

        IRecipePool[] recipePools = new IRecipePool[] { new BotaniaManaInfusionRecipes(), new ChemicalRecipes(),
            new ElectrolyzerRecipes(), new MixerRecipes(), new AssemblerRecipes(), new AutoclaveRecipes(),
            new AlloyBlastSmelterRecipes(), new CompressorRecipes(), new ReFusionReactorRecipes(),
            new RealArtificialStarRecipes(), new PortalToAlfheimRecipes(), new NatureSpiritArrayRecipes(),
            new ManaInfusionRecipes(), new TranscendentPlasmaMixerRecipes(), new CraftingTableRecipes(),
            new ChemicalBathRecipes(), new SteamCrackerRecipes(), new DesulfurizerRecipes(),
            new PetrochemicalPlantRecipes(), new FusionReactorRecipes(), new SmeltingMixingFurnaceRecipes(),
            new FluidExtraction(), new DigesterRecipes(), new DissolutionTankRecipes(), new CentrifugeRecipes(),
            new ChemicalDehydratorRecipes(), new ChemicalPlantRecipes(), new RareEarthCentrifugalRecipes(),
            new MatterFabricatorRecipes(), new TheTwilightForestRecipes(), new IsaMillRecipes(),
            new CellRegulatorRecipes(), new VacuumFurnaceRecipes(), new FishingGroundRecipes(), new DistilleryRecipes(),
            new ElementCopyingRecipes(), new AlloySmelterRecipes(), new MolecularTransformerRecipes(),
            new NaquadahReactorRecipes(), new DragonEvolutionFusionCraftingRecipes(), new LaserEngraverRecipes(),
            new BacterialVatRecipes(), new CuttingRecipes(), new BlastFurnaceRecipes(), new FluidExtractorRecipes(),
            new ElectricImplosionCompressorRecipes(), new DecayHastenerRecipes(), new PreciseAssemblerRecipes(),
            new FuelRefiningComplexRecipes(), new CrackingRecipes(), new DistillationTowerRecipes(),
            new SpaceMinerRecipes(), new SpaceDrillRecipes(), new SpaceAssemblerRecipes(), new PCBFactoryRecipes(),
            new PlatinumBasedTreatmentRecipes(), new ShallowChemicalCouplingRecipes(), new TreeDiagramRecipes(),
            new AssemblingLineRecipes(), new GasCollectorRecipes(), new EternalGregTechWorkshopUpgradeRecipes(),
            new FluidCannerRecipes(), new VacuumFreezerRecipes(), new CheatOreProcessingRecipes(),
            new ShapedArcaneCraftingRecipes(), new InfusionCraftingRecipes(), new SteamManufacturerRecipes(),
            new SteamCarpenterRecipe(), new LavaMakerRecipes(), new SteamWoodcutterRecipes(),
            new SteamGateAssemblerRecipes(), new CactusWonderFakeRecipes(), new InfernalCokeRecipes(),
            new SteamFusionReactorRecipes(), new SteamExtractinatorRecipes(), new RockBreakerRecipes(),
            new PrimitiveBrickKilnRecipes(), new TargetChamberRecipes(), new ElectrocellGeneratorRecipes(),
            new FluidSolidifierRecipes(), new FormingPressRecipes(), new HammerRecipes(), new CyclotronRecipes(),
            new RuneAltarRecipes(), new IndustrialRockCrusherRecipes(), new PrecisionLaserEngraver(),
            new NanitesIntegratedProcessingRecipes(), new NanoForgeRecipes(), new SteamWeatherModuleRecipes(),
            new ElectricNeutronActivatorRecipes(), new ReactorProcessingUnitRecipes(),
            new NuclearSaltProcessingPlantRecipes(), new MaceratorRecipes(), new QuantumForceTransformerRecipes(),
            new MicroorganismMasterRecipes(), new SolarMuonCatalystRecipes() };

        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

        RecipeUtil
            .generateRecipesBioLab(BartWorksRecipeMaps.bioLabRecipes, GTNLRecipeMaps.LargeBioLabRecipes, true, 1.1);

        ShimmerRecipes.loadRecipes();

        // loadPlasmaCentrifugeRecipes();

        if (Mods.NewHorizonsCoreMod.isModLoaded()) {
            TCResearches.register();
            new PlasmaForgeRecipes().loadRecipes();
        }

        if (Mods.GalaxySpace.isModLoaded()) {
            new RocketAssemblerRecipes().loadRecipes();
        }

        if (Mods.BloodMagic.isModLoaded()) {
            new BloodDemonInjectionRecipes().loadRecipes();
            new AlchemicChemistrySetRecipes().loadRecipes();
            new MeteorsRecipes().loadRecipes();
            new FallingTowerRecipes().loadRecipes();
        }

        if (ModList.TwistSpaceTechnology.isModLoaded()) {
            loadTSTMegaAssemblyLineRecipes();
        }

        recipesAdded = true;
    }

    public static void loadPlasmaCentrifugeRecipes() {
        for (RecipeMap<?> map : new RecipeMap<?>[] { RecipeMaps.transcendentPlasmaMixerRecipes,
            RecipeMaps.fusionRecipes }) {
            for (GTRecipe recipe : map.getAllRecipes()) {
                if (recipe == null) continue;

                GTRecipe copiedRecipe = recipe.copy();
                if (copiedRecipe == null) continue;

                FluidStack[] newInputs = recipe.mFluidOutputs;
                FluidStack[] newOutputs = recipe.mFluidInputs;

                if (newOutputs != null) {
                    for (FluidStack stack : newOutputs) {
                        if (stack != null) {
                            stack.amount = (int) (stack.amount * 0.9);
                        }
                    }
                }

                copiedRecipe.mFluidInputs = newInputs;
                copiedRecipe.mFluidOutputs = newOutputs;
                copiedRecipe.mEUt = 0;
                copiedRecipe.mDuration = 200;

                GTNLRecipeMaps.PlasmaCentrifugeRecipes.add(copiedRecipe);
            }
        }
    }

    public static void loadCircuitNanitesData(long worldSeed) {
        new CircuitNanitesDataRecipes(worldSeed).loadRecipes();
    }

    public static void loadCircuitRelatedRecipes() {
        RecipeUtil.copyAllRecipes(GTNLRecipeMaps.ConvertToCircuitAssemblerRecipes, RecipeMaps.circuitAssemblerRecipes);

        new CircuitAssemblyLineRecipes().loadRecipes();

        if (ModList.TwistSpaceTechnology.isModLoaded()) {
            loadTSTAdvCircuitAssemblyLineRecipes();
        }
    }

    public static void loadBuffTargetChamberRecipe() {
        Collection<GTRecipe> targetChamberRecipe = LanthanidesRecipeMaps.targetChamberRecipes.getAllRecipes();
        LanthanidesRecipeMaps.targetChamberRecipes.getBackend()
            .clearRecipes();

        Object2IntMap<ItemStack> waferMultiplier = new Object2IntOpenHashMap<>() {

            {
                put(ItemList.Circuit_Silicon_Wafer2.get(1), 1);
                put(ItemList.Circuit_Silicon_Wafer3.get(1), 2);
                put(ItemList.Circuit_Silicon_Wafer4.get(1), 4);
                put(ItemList.Circuit_Silicon_Wafer5.get(1), 8);
                put(ItemList.Circuit_Silicon_Wafer6.get(1), 16);
                put(ItemList.Circuit_Silicon_Wafer7.get(1), 32);
            }
        };
        for (GTRecipe recipe : targetChamberRecipe) {

            int multiplier = 4;

            outer: for (ItemStack input : recipe.mInputs) {
                if (input == null) continue;

                for (Object2IntMap.Entry<ItemStack> entry : waferMultiplier.object2IntEntrySet()) {
                    if (GTUtility.areStacksEqual(input, entry.getKey(), true)) {
                        multiplier = entry.getIntValue();
                        break outer;
                    }
                }
            }

            for (ItemStack output : recipe.mOutputs) {
                output.stackSize *= multiplier;
            }

            LanthanidesRecipeMaps.targetChamberRecipes.add(recipe);
        }

    }

    @Optional.Method(modid = "TwistSpaceTechnology")
    public static void loadTSTMegaAssemblyLineRecipes() {
        AssemblyLineWithoutResearchRecipePool.loadRecipes();
        System.out.println("[GTNL] Register TwistSpaceTechnology MegaAssemblyLine recipes");
    }

    @Optional.Method(modid = "TwistSpaceTechnology")
    public static void loadTSTAdvCircuitAssemblyLineRecipes() {
        GTCMRecipe.advCircuitAssemblyLineRecipes.getBackend()
            .clearRecipes();
        CircuitAssemblyLineWithoutImprintRecipePool.loadRecipes();
        System.out.println("[GTNL] Register TwistSpaceTechnology AdvCircuitAssemblyLine recipes");
    }

    public static void loadVillageTrade() {
        for (int id = 0; id < 5; id++) {
            loadTradeForVillager(id);
        }

        // for (int id : VillagerRegistry.getRegisteredVillagers()) {
        // loadTradeForVillager(id);
        // }
    }

    @SuppressWarnings("unchecked")
    public static void loadTradeForVillager(int villagerId) {
        VillagerRegistry.instance()
            .registerVillageTradeHandler(villagerId, (villager, recipeList, random) -> {
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.iron_ingot, 1),
                        GTModHandler.getModItem(Mods.Botania.ID, "bifrostPermPane", 1),
                        Stick.setDisguisedStack(
                            GTOreDictUnificator.get(GTModHandler.getModItem(Mods.Avaritia.ID, "Resource", 1, 6)))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Blocks.dispenser, 1),
                        Stick.setDisguisedStack(CustomItemList.Machine_Multi_EyeOfHarmony.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.wooden_sword, 1),
                        Stick.setDisguisedStack(ReAvaItemList.InfinitySword.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.leather_helmet, 1),
                        Stick.setDisguisedStack(GTModHandler.getModItem(Mods.Avaritia.ID, "Infinity_Helm", 1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.leather_chestplate, 1),
                        Stick.setDisguisedStack(GTModHandler.getModItem(Mods.Avaritia.ID, "Infinity_Chest", 1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.leather_leggings, 1),
                        Stick.setDisguisedStack(GTModHandler.getModItem(Mods.Avaritia.ID, "Infinity_Pants", 1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.leather_boots, 1),
                        Stick.setDisguisedStack(GTModHandler.getModItem(Mods.Avaritia.ID, "Infinity_Shoes", 1))));
            });
    }
}
