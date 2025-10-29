package com.science.gtnl.loader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.Nxer.TwistSpaceTechnology.recipe.machineRecipe.expanded.AssemblyLineWithoutResearchRecipePool;
import com.Nxer.TwistSpaceTechnology.recipe.machineRecipe.expanded.CircuitAssemblyLineWithoutImprintRecipePool;
import com.reavaritia.ReAvaItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.item.items.Stick;
import com.science.gtnl.common.material.MaterialPool;
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
import com.science.gtnl.common.recipe.gregtech.ChemicalRecipes;
import com.science.gtnl.common.recipe.gregtech.CompressorRecipes;
import com.science.gtnl.common.recipe.gregtech.CrackingRecipes;
import com.science.gtnl.common.recipe.gregtech.CraftingTableRecipes;
import com.science.gtnl.common.recipe.gregtech.CuttingRecipes;
import com.science.gtnl.common.recipe.gregtech.DigesterRecipes;
import com.science.gtnl.common.recipe.gregtech.DissolutionTankRecipes;
import com.science.gtnl.common.recipe.gregtech.DistillationTowerRecipes;
import com.science.gtnl.common.recipe.gregtech.DistilleryRecipes;
import com.science.gtnl.common.recipe.gregtech.DragonEvolutionFusionCraftingRecipes;
import com.science.gtnl.common.recipe.gregtech.ElectrolyzerRecipes;
import com.science.gtnl.common.recipe.gregtech.FluidCannerRecipes;
import com.science.gtnl.common.recipe.gregtech.FluidExtraction;
import com.science.gtnl.common.recipe.gregtech.FluidExtractorRecipes;
import com.science.gtnl.common.recipe.gregtech.FluidSolidifierRecipes;
import com.science.gtnl.common.recipe.gregtech.FusionReactorRecipes;
import com.science.gtnl.common.recipe.gregtech.LaserEngraverRecipes;
import com.science.gtnl.common.recipe.gregtech.MixerRecipes;
import com.science.gtnl.common.recipe.gregtech.PCBFactoryRecipes;
import com.science.gtnl.common.recipe.gregtech.PlasmaForgeRecipes;
import com.science.gtnl.common.recipe.gregtech.PreciseAssemblerRecipes;
import com.science.gtnl.common.recipe.gregtech.SpaceAssemblerRecipes;
import com.science.gtnl.common.recipe.gregtech.TargetChamberRecipes;
import com.science.gtnl.common.recipe.gregtech.TranscendentPlasmaMixerRecipes;
import com.science.gtnl.common.recipe.gregtech.VacuumFreezerRecipes;
import com.science.gtnl.common.recipe.gregtech.VacuumFurnaceRecipes;
import com.science.gtnl.common.recipe.gregtech.multiDehydratorRecipes;
import com.science.gtnl.common.recipe.gregtech.remove.NewAlloyBlastSmelterRecipes;
import com.science.gtnl.common.recipe.gregtech.remove.NewFormingPressRecipes;
import com.science.gtnl.common.recipe.gregtech.remove.NewVacuumFurnaceRecipes;
import com.science.gtnl.common.recipe.gregtech.serverStart.CircuitAssemblerConvertRecipes;
import com.science.gtnl.common.recipe.gregtech.serverStart.CircuitAssemblyLineRecipes;
import com.science.gtnl.common.recipe.gtnl.AdvancedCircuitAssemblyLineRecipes;
import com.science.gtnl.common.recipe.gtnl.AlchemicChemistrySetRecipes;
import com.science.gtnl.common.recipe.gtnl.BloodDemonInjectionRecipes;
import com.science.gtnl.common.recipe.gtnl.CactusWonderFakeRecipes;
import com.science.gtnl.common.recipe.gtnl.CellRegulatorRecipes;
import com.science.gtnl.common.recipe.gtnl.CircuitNanitesDataRecipes;
import com.science.gtnl.common.recipe.gtnl.DecayHastenerRecipes;
import com.science.gtnl.common.recipe.gtnl.DesulfurizerRecipes;
import com.science.gtnl.common.recipe.gtnl.ElectrocellGeneratorRecipes;
import com.science.gtnl.common.recipe.gtnl.ElementCopyingRecipes;
import com.science.gtnl.common.recipe.gtnl.EternalGregTechWorkshopUpgradeRecipes;
import com.science.gtnl.common.recipe.gtnl.FallingTowerRecipes;
import com.science.gtnl.common.recipe.gtnl.FishingGroundRecipes;
import com.science.gtnl.common.recipe.gtnl.FuelRefiningComplexRecipes;
import com.science.gtnl.common.recipe.gtnl.GasCollectorRecipes;
import com.science.gtnl.common.recipe.gtnl.InfernalCokeRecipes;
import com.science.gtnl.common.recipe.gtnl.InfusionCraftingRecipes;
import com.science.gtnl.common.recipe.gtnl.IsaMillRecipes;
import com.science.gtnl.common.recipe.gtnl.LavaMakerRecipes;
import com.science.gtnl.common.recipe.gtnl.ManaInfusionRecipes;
import com.science.gtnl.common.recipe.gtnl.MatterFabricatorRecipes;
import com.science.gtnl.common.recipe.gtnl.MolecularTransformerRecipes;
import com.science.gtnl.common.recipe.gtnl.NaquadahReactorRecipes;
import com.science.gtnl.common.recipe.gtnl.NatureSpiritArrayRecipes;
import com.science.gtnl.common.recipe.gtnl.PetrochemicalPlantRecipes;
import com.science.gtnl.common.recipe.gtnl.PlatinumBasedTreatmentRecipes;
import com.science.gtnl.common.recipe.gtnl.PortalToAlfheimRecipes;
import com.science.gtnl.common.recipe.gtnl.PrimitiveBrickKilnRecipes;
import com.science.gtnl.common.recipe.gtnl.RareEarthCentrifugalRecipes;
import com.science.gtnl.common.recipe.gtnl.ReFusionReactorRecipes;
import com.science.gtnl.common.recipe.gtnl.RealArtificialStarRecipes;
import com.science.gtnl.common.recipe.gtnl.RockBreakerRecipes;
import com.science.gtnl.common.recipe.gtnl.RocketAssemblerRecipes;
import com.science.gtnl.common.recipe.gtnl.ShallowChemicalCouplingRecipes;
import com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes;
import com.science.gtnl.common.recipe.gtnl.ShimmerRecipes;
import com.science.gtnl.common.recipe.gtnl.SmeltingMixingFurnaceRecipes;
import com.science.gtnl.common.recipe.gtnl.SpaceDrillRecipes;
import com.science.gtnl.common.recipe.gtnl.SpaceMinerRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamCarpenterRecipe;
import com.science.gtnl.common.recipe.gtnl.SteamCrackerRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamExtractinatorRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamFusionReactorRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamGateAssemblerRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamManufacturerRecipes;
import com.science.gtnl.common.recipe.gtnl.SteamWoodcutterRecipes;
import com.science.gtnl.common.recipe.gtnl.TheTwilightForestRecipes;
import com.science.gtnl.common.recipe.oreDictionary.PortalToAlfheimOreRecipes;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipePool;
import com.science.gtnl.common.recipe.thaumcraft.TCResearches;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.machine.ProcessingArrayRecipeLoader;
import com.science.gtnl.utils.machine.oreProcessing.CheatOreProcessingRecipes;
import com.science.gtnl.utils.recipes.RecipeUtil;

import bartworks.API.recipe.BartWorksRecipeMaps;
import cpw.mods.fml.common.registry.VillagerRegistry;
import goodgenerator.loader.Loaders;
import goodgenerator.util.CrackRecipeAdder;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import tectech.thing.CustomItemList;

public class RecipeLoader {

    public static boolean recipesAdded;

    public static void loadCompleteInit() {
        IRecipePool[] recipePools = new IRecipePool[] { new CircuitAssemblerConvertRecipes() };

        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }
    }

    public static void loadRecipesServerStart() {
        if (recipesAdded) return;

        ProcessingArrayRecipeLoader.registerDefaultGregtechMaps();

        CrackRecipeAdder.reAddBlastRecipe(MaterialPool.MolybdenumDisilicide, 800, 1920, 2300, true);
        CrackRecipeAdder.reAddBlastRecipe(MaterialPool.HSLASteel, 1000, 480, 1711, true);
        CrackRecipeAdder.reAddBlastRecipe(MaterialPool.Germaniumtungstennitride, 800, 30720, 8200, true);

        if (MainConfig.enableChamberRecipesBuff) {
            registerBuffTargetChamberRecipe();
        }

        IRecipePool[] recipePools = new IRecipePool[] { new TCRecipePool(), new ChemicalRecipes(),
            new ElectrolyzerRecipes(), new MixerRecipes(), new multiDehydratorRecipes(), new AssemblerRecipes(),
            new AutoclaveRecipes(), new AlloyBlastSmelterRecipes(), new CompressorRecipes(),
            new ReFusionReactorRecipes(), new RealArtificialStarRecipes(), new PortalToAlfheimRecipes(),
            new NatureSpiritArrayRecipes(), new ManaInfusionRecipes(), new TranscendentPlasmaMixerRecipes(),
            new PlasmaForgeRecipes(), new CraftingTableRecipes(), new ChemicalBathRecipes(), new SteamCrackerRecipes(),
            new DesulfurizerRecipes(), new PetrochemicalPlantRecipes(), new FusionReactorRecipes(),
            new SmeltingMixingFurnaceRecipes(), new FluidExtraction(), new DigesterRecipes(),
            new DissolutionTankRecipes(), new CentrifugeRecipes(), new ChemicalDehydratorRecipes(),
            new RareEarthCentrifugalRecipes(), new MatterFabricatorRecipes(), new TheTwilightForestRecipes(),
            new IsaMillRecipes(), new CellRegulatorRecipes(), new VacuumFurnaceRecipes(), new FishingGroundRecipes(),
            new DistilleryRecipes(), new ElementCopyingRecipes(), new AlloySmelterRecipes(),
            new MolecularTransformerRecipes(), new NaquadahReactorRecipes(), new DragonEvolutionFusionCraftingRecipes(),
            new LaserEngraverRecipes(), new BacterialVatRecipes(), new CuttingRecipes(), new BlastFurnaceRecipes(),
            new FluidExtractorRecipes(), new DecayHastenerRecipes(), new PreciseAssemblerRecipes(),
            new FuelRefiningComplexRecipes(), new CrackingRecipes(), new DistillationTowerRecipes(),
            new SpaceMinerRecipes(), new SpaceDrillRecipes(), new SpaceAssemblerRecipes(), new PCBFactoryRecipes(),
            new PlatinumBasedTreatmentRecipes(), new ShallowChemicalCouplingRecipes(), new BloodDemonInjectionRecipes(),
            new AlchemicChemistrySetRecipes(), new AdvancedCircuitAssemblyLineRecipes(), new FallingTowerRecipes(),
            new AssemblingLineRecipes(), new GasCollectorRecipes(), new EternalGregTechWorkshopUpgradeRecipes(),
            new FluidCannerRecipes(), new VacuumFreezerRecipes(), new MeteorsRecipes(), new CheatOreProcessingRecipes(),
            new ShapedArcaneCraftingRecipes(), new InfusionCraftingRecipes(), new ShimmerRecipes(),
            new SteamManufacturerRecipes(), new SteamCarpenterRecipe(), new LavaMakerRecipes(),
            new SteamWoodcutterRecipes(), new SteamGateAssemblerRecipes(), new CactusWonderFakeRecipes(),
            new InfernalCokeRecipes(), new SteamFusionReactorRecipes(), new SteamExtractinatorRecipes(),
            new RockBreakerRecipes(), new PrimitiveBrickKilnRecipes(), new TargetChamberRecipes(),
            new ElectrocellGeneratorRecipes(), new RocketAssemblerRecipes(), new FluidSolidifierRecipes(),
            new BotaniaManaInfusionRecipes() };

        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }

        for (ItemStack stone : OreDictionary.getOres("stone")) {
            PortalToAlfheimOreRecipes.addManaInfusionOreRecipes(stone);
        }

        RecipeUtil.generateRecipesBioLab(BartWorksRecipeMaps.bioLabRecipes, RecipePool.LargeBioLabRecipes, true, 1.1);
        TCResearches.register();

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

                RecipePool.PlasmaCentrifugeRecipes.add(copiedRecipe);
            }
        }

        if (MainConfig.enableAssemblingLineRecipesTimeChange) {
            for (GTRecipe.RecipeAssemblyLine recipe : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
                int duration = recipe.mDuration;

                if (duration >= 200000) {
                    duration /= 100;
                } else if (duration >= 40000) {
                    duration /= 10;
                } else if (duration >= 10000) {
                    duration /= 4;
                } else if (duration >= 4000) {
                    duration /= 2;
                }

                recipe.mDuration = Math.max(1, duration);
            }
        }

        if (MainConfig.enableDeleteRecipe) {
            loadNewRemoveRecipes();
        }

        if (ModList.TwistSpaceTechnology.isModLoaded()) {
            AssemblyLineWithoutResearchRecipePool.loadRecipes();
        }

        recipesAdded = true;
    }

    public static void loadCircuitNanitesData(long worldSeed) {
        new CircuitNanitesDataRecipes(worldSeed).loadRecipes();
    }

    public static void loadNewRemoveRecipes() {
        IRecipePool[] recipePools = new IRecipePool[] { new NewAlloyBlastSmelterRecipes(),
            new NewVacuumFurnaceRecipes(), new NewFormingPressRecipes() };

        for (IRecipePool recipePool : recipePools) {
            recipePool.loadRecipes();
        }
    }

    public static void loadVillageTrade() {
        for (int id = 0; id < 5; id++) {
            registerTradeForVillager(id);
        }

        // for (int id : VillagerRegistry.getRegisteredVillagers()) {
        // registerTradeForVillager(id);
        // }
    }

    @SuppressWarnings("unchecked")
    public static void registerTradeForVillager(int villagerId) {
        VillagerRegistry.instance()
            .registerVillageTradeHandler(villagerId, (villager, recipeList, random) -> {
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.diamond, 3),
                        Stick.setDisguisedStack(
                            GTOreDictUnificator.get(OrePrefixes.plate, Materials.Diamond, 1),
                            9,
                            false)));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.emerald, 4),
                        Stick.setDisguisedStack(new ItemStack(Blocks.diamond_block, 1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.diamond, 1),
                        Stick.setDisguisedStack(new ItemStack(Items.emerald, 1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.emerald, 1),
                        Stick.setDisguisedStack(new ItemStack(Items.ender_eye, 1), 2, false)));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.diamond, 1),
                        Stick.setDisguisedStack(GTNLItemList.SatietyRing.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.diamond, 1),
                        new ItemStack(Items.emerald, 1),
                        Stick.setDisguisedStack(GTNLItemList.VeinMiningPickaxe.get(1), 1, true)));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Blocks.snow, 1),
                        Stick.setDisguisedStack(
                            GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.MagMatter, 1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.iron_ingot, 1),
                        GTModHandler.getModItem(Mods.Botania.ID, "bifrostPermPane", 1),
                        Stick.setDisguisedStack(
                            GTOreDictUnificator.get(GTModHandler.getModItem(Mods.Avaritia.ID, "Resource", 1, 6)))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.nether_star, 1),
                        Stick.setDisguisedStack(CustomItemList.astralArrayFabricator.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.compass, 1),
                        Stick.setDisguisedStack(ItemList.Transdimensional_Alignment_Matrix.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.redstone, 1),
                        Stick.setDisguisedStack(GregtechItemList.Laser_Lens_Special.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Blocks.redstone_block, 1),
                        Stick.setDisguisedStack(CustomItemList.Machine_DebugGenny.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Blocks.dispenser, 1),
                        Stick.setDisguisedStack(CustomItemList.Machine_Multi_EyeOfHarmony.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Blocks.furnace, 1),
                        Stick.setDisguisedStack(CustomItemList.Machine_Multi_ForgeOfGods.get(1))));
                recipeList.add(
                    new MerchantRecipe(new ItemStack(Blocks.coal_block, 1), Stick.setDisguisedStack(Loaders.AMForge)));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Blocks.tnt, 1),
                        Stick.setDisguisedStack(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockNuke", 1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.brick, 1),
                        Stick.setDisguisedStack(ItemList.Circuit_TranscendentMainframe.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.bucket, 1),
                        Stick.setDisguisedStack(ReAvaItemList.InfinityBucket.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.clock, 1),
                        Stick.setDisguisedStack(ReAvaItemList.ChronarchsClock.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.wooden_sword, 1),
                        Stick.setDisguisedStack(ReAvaItemList.InfinitySword.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.wooden_pickaxe, 1),
                        Stick.setDisguisedStack(ReAvaItemList.InfinityPickaxe.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.wooden_axe, 1),
                        Stick.setDisguisedStack(ReAvaItemList.InfinityAxe.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.wooden_shovel, 1),
                        Stick.setDisguisedStack(ReAvaItemList.InfinityShovel.get(1))));
                recipeList.add(
                    new MerchantRecipe(
                        new ItemStack(Items.wooden_hoe, 1),
                        Stick.setDisguisedStack(ReAvaItemList.InfinityHoe.get(1))));
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

                if (Mods.SGCraft.isModLoaded()) {
                    recipeList.add(
                        new MerchantRecipe(
                            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Naquadah, 9),
                            Stick.setDisguisedStack(GTModHandler.getModItem(Mods.SGCraft.ID, "stargateRing", 1, 0))));
                    recipeList.add(
                        new MerchantRecipe(
                            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.NaquadahEnriched, 9),
                            Stick.setDisguisedStack(GTModHandler.getModItem(Mods.SGCraft.ID, "stargateRing", 1, 1))));
                    recipeList.add(
                        new MerchantRecipe(
                            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Naquadria, 9),
                            Stick.setDisguisedStack(GTModHandler.getModItem(Mods.SGCraft.ID, "stargateBase", 1))));
                }
                if (Mods.EternalSingularity.isModLoaded()) {
                    recipeList.add(
                        new MerchantRecipe(
                            new ItemStack(Blocks.beacon, 1),
                            Stick.setDisguisedStack(
                                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 1))));
                }
            });
    }

    public static void loadCircuitRelatedRecipes() {
        RecipeUtil.copyAllRecipes(RecipePool.ConvertToCircuitAssembler, RecipeMaps.circuitAssemblerRecipes);

        new CircuitAssemblyLineRecipes().loadRecipes();

        if (!recipesAdded && ModList.TwistSpaceTechnology.isModLoaded()) {
            CircuitAssemblyLineWithoutImprintRecipePool.loadRecipes();
        }
    }

    public static void registerBuffTargetChamberRecipe() {
        Collection<GTRecipe> targetChamberRecipe = LanthanidesRecipeMaps.targetChamberRecipes.getAllRecipes();
        LanthanidesRecipeMaps.targetChamberRecipes.getBackend()
            .clearRecipes();

        Map<ItemStack, Integer> waferMultiplier = new HashMap<>() {

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
            for (Map.Entry<ItemStack, Integer> entry : waferMultiplier.entrySet()) {
                if (recipe.mInputs[1].isItemEqual(entry.getKey())) {
                    int multiplier = entry.getValue();
                    for (ItemStack itemStack : recipe.mOutputs) {
                        itemStack.stackSize *= multiplier;
                    }
                } else {
                    for (ItemStack itemStack : recipe.mOutputs) {
                        itemStack.stackSize *= 4;
                    }
                }
                break;
            }
            LanthanidesRecipeMaps.targetChamberRecipes.add(recipe);
        }
    }
}
