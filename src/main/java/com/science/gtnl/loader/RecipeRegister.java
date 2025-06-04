package com.science.gtnl.loader;

import static gregtech.api.enums.Mods.GregTech;

import java.util.Collections;
import java.util.Comparator;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.gui.BloodSoulFrontend;
import com.science.gtnl.Utils.gui.EGTWUpgradeCostFrontend;
import com.science.gtnl.Utils.gui.ExtremeExtremeEntityCrusherFrontend;
import com.science.gtnl.Utils.gui.FallingTowerFrontend;
import com.science.gtnl.Utils.gui.GTNLLogoFrontend;
import com.science.gtnl.Utils.gui.GeneralFrontend;
import com.science.gtnl.Utils.gui.IndustrialInfusionCraftingRecipesFrontend;
import com.science.gtnl.Utils.gui.SpaceMinerFrontend;
import com.science.gtnl.Utils.gui.SteamGateAssemblerFrontend;
import com.science.gtnl.Utils.gui.SteamLogoFrontend;
import com.science.gtnl.Utils.recipes.IsaMillTierKey;
import com.science.gtnl.Utils.recipes.NaquadahReactorSpecialValue;
import com.science.gtnl.Utils.recipes.RealArtificialStarSpecialValue;
import com.science.gtnl.Utils.recipes.ResourceCollectionModuleTierKey;
import com.science.gtnl.Utils.recipes.SteamFusionTierKey;

import goodgenerator.api.recipe.ComponentAssemblyLineFrontend;
import goodgenerator.client.GUI.GGUITextures;
import gregtech.api.enums.GTValues;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.util.GTRecipe;
import gregtech.nei.formatter.HeatingCoilSpecialValueFormatter;

public class RecipeRegister {

    public static final UITexture PROGRESSBAR_GAS_COLLECTOR = UITexture
        .fullImage(GregTech.ID, "gui/progressbar/gas_collector");

    public static final RecipeMap<RecipeMapBackend> RecombinationFusionReactorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.RecombinationFusionReactorRecipes", RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.GenerationEarthEngine.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> FallingTowerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.FallingTowerRecipes")
        .maxIO(1, 81, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_COMPRESS)
        .frontend(FallingTowerFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.BloodSoulSacrificialArray.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> BloodDemonInjectionRecipes = RecipeMapBuilder
        .of("gtnl.recipe.BloodDemonInjectionRecipes")
        .maxIO(4, 1, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(BloodSoulFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.BloodSoulSacrificialArray.get(1))
                .setMaxRecipesPerPage(2))
        .build();

    public static final RecipeMap<RecipeMapBackend> AlchemicChemistrySetRecipes = RecipeMapBuilder
        .of("gtnl.recipe.AlchemicChemistrySetRecipes")
        .maxIO(5, 1, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(BloodSoulFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.BloodSoulSacrificialArray.get(1))
                .setMaxRecipesPerPage(2))
        .build();

    public static final RecipeMap<RecipeMapBackend> RealArtificialStarRecipes = RecipeMapBuilder
        .of("gtnl.recipe.ArtificialStarGeneratingRecipes")
        .maxIO(1, 1, 0, 0)
        .neiSpecialInfoFormatter(RealArtificialStarSpecialValue.INSTANCE)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.RealArtificialStar.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> PortalToAlfheimRecipes = RecipeMapBuilder
        .of("gtnl.recipe.PortalToAlfheimRecipes", RecipeMapBackend::new)
        .maxIO(4, 36, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.TeleportationArrayToAlfheim.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> RuneAltarRecipes = RecipeMapBuilder
        .of("gtnl.recipe.RuneAltarRecipes", RecipeMapBackend::new)
        .maxIO(8, 1, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.TeleportationArrayToAlfheim.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> NatureSpiritArrayRecipes = RecipeMapBuilder
        .of("gtnl.recipe.NatureSpiritArrayRecipes")
        .maxIO(1, 0, 0, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.TeleportationArrayToAlfheim.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> ManaInfusionRecipes = RecipeMapBuilder
        .of("gtnl.recipe.ManaInfusionRecipes")
        .maxIO(4, 1, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.TeleportationArrayToAlfheim.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> LapotronChipRecipes = RecipeMapBuilder
        .of("gtnl.recipe.LapotronChipRecipes")
        .maxIO(9, 9, 3, 3)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.LapotronChip.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamCrackerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SteamCrackerRecipes")
        .maxIO(1, 0, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamCracking.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> CheatOreProcessingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.CheatOreProcessingRecipes")
        .maxIO(1, 9, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.CheatOreProcessingFactory.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> DesulfurizerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.DesulfurizerRecipes")
        .maxIO(0, 1, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.Desulfurizer.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> PetrochemicalPlantRecipes = RecipeMapBuilder
        .of("gtnl.recipe.PetrochemicalPlantRecipes", RecipeMapBackend::new)
        .maxIO(4, 4, 4, 12)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.PetrochemicalPlant.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> SmeltingMixingFurnaceRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SmeltingMixingFurnaceRecipes", RecipeMapBackend::new)
        .maxIO(8, 4, 16, 4)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SmeltingMixingFurnace.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> RareEarthCentrifugalRecipes = RecipeMapBuilder
        .of("gtnl.recipe.RareEarthCentrifugalRecipes", RecipeMapBackend::new)
        .maxIO(1, 17, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.RareEarthCentrifugal.get(1)))
        .build();

    public static RecipeMap<RecipeMapBackend> IndustrialShapedArcaneCraftingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.IndustrialShapedArcaneCraftingRecipes")
        .maxIO(9, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.IndustrialArcaneAssembler.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> MatterFabricatorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.MatterFabricatorRecipes")
        .maxIO(2, 1, 0, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.MatterFabricator.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> TheTwilightForestRecipes = RecipeMapBuilder
        .of("gtnl.recipe.TheTwilightForestRecipes", RecipeMapBackend::new)
        .maxIO(4, 16, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.LibraryOfRuina.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> FishingGroundRecipes = RecipeMapBuilder
        .of("gtnl.recipe.FishingGroundRecipes", RecipeMapBackend::new)
        .maxIO(4, 32, 4, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.LibraryOfRuina.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> IndustrialInfusionCraftingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.IndustrialInfusionCraftingRecipes")
        .maxIO(25, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(IndustrialInfusionCraftingRecipesFrontend::new)
        .neiTransferRect(100, 45, 18, 72)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.IndustrialArcaneAssembler.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> IsaMillRecipes = RecipeMapBuilder.of("gtnl.recipe.IsaMillRecipes")
        .maxIO(2, 1, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.IsaMill.get(1))
                .setMaxRecipesPerPage(1))
        .neiRecipeComparator(
            Comparator.<GTRecipe, Integer>comparing(recipe -> recipe.getMetadataOrDefault(IsaMillTierKey.INSTANCE, 0))
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static RecipeMap<RecipeMapBackend> CellRegulatorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.CellRegulatorRecipes")
        .maxIO(2, 0, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.FlotationCellRegulator.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> ElementCopyingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.ElementCopyingRecipes")
        .maxIO(3, 9, 1, 3)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.ElementCopying.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> WoodDistillationRecipes = RecipeMapBuilder
        .of("gtnl.recipe.WoodDistillationRecipes", RecipeMapBackend::new)
        .maxIO(1, 1, 1, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.WoodDistillation.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> MolecularTransformerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.MolecularTransformerRecipes")
        .maxIO(2, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.MolecularTransformer.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> NaquadahReactorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.NaquadahReactorRecipes")
        .maxIO(0, 0, 2, 1)
        .dontUseProgressBar()
        .neiSpecialInfoFormatter(NaquadahReactorSpecialValue.INSTANCE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.LargeNaquadahReactor.get(1))
                .setMaxRecipesPerPage(1))
        .addSpecialTexture(59, 20, 58, 42, GGUITextures.PICTURE_NAQUADAH_REACTOR)
        .build();

    public static RecipeMap<RecipeMapBackend> DecayHastenerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.DecayHastenerRecipes")
        .maxIO(1, 1, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.DecayHastener.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> GrandAssemblyLineRecipes = RecipeMapBuilder
        .of("gtnl.recipe.GrandAssemblyLineRecipes")
        .maxIO(16, 1, 4, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.GrandAssemblyLine.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> FuelRefiningComplexRecipes = RecipeMapBuilder
        .of("gtnl.recipe.FuelRefiningComplexRecipes")
        .maxIO(3, 0, 6, 1)
        .frontend(GeneralFrontend::new)
        .dontUseProgressBar()
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.FuelRefiningComplex.get(1))
                .setMaxRecipesPerPage(1))
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .build();

    public static RecipeMap<RecipeMapBackend> SpaceMinerRecipes = RecipeMapBuilder.of("gtnl.recipe.SpaceMinerRecipes")
        .maxIO(2, 9, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(SpaceMinerFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.ResourceCollectionModule.get(1))
                .setMaxRecipesPerPage(1))
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, Integer>comparing(
                    recipe -> recipe.getMetadataOrDefault(ResourceCollectionModuleTierKey.INSTANCE, 0))
                .thenComparing(GTRecipe::compareTo))
        .neiRecipeComparator(
            Comparator.<GTRecipe, Integer>comparing(recipe -> recipe.mSpecialValue)
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static RecipeMap<RecipeMapBackend> SpaceDrillRecipes = RecipeMapBuilder.of("gtnl.recipe.SpaceDrillRecipes")
        .maxIO(2, 0, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.ResourceCollectionModule.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> PlatinumBasedTreatmentRecipes = RecipeMapBuilder
        .of("gtnl.recipe.PlatinumBasedTreatmentRecipes")
        .maxIO(8, 12, 4, 4)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.PlatinumBasedTreatment.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> ShallowChemicalCouplingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.ShallowChemicalCouplingRecipes")
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.ShallowChemicalCoupling.get(1))
                .setMaxRecipesPerPage(1))
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .build();

    public static RecipeMap<RecipeMapBackend> AdvancedCircuitAssemblyLineRecipes = RecipeMapBuilder
        .of("gtnl.recipe.AdvancedCircuitAssemblyLineRecipes")
        .maxIO(12, 1, 12, 0)
        .neiTransferRect(70, 15, 18, 54)
        .neiSpecialInfoFormatter(
            recipeInfo -> Collections.singletonList(
                StatCollector.translateToLocalFormatted(
                    "value.component_assembly_line",
                    GTValues.VN[recipeInfo.recipe.mSpecialValue])))
        .dontUseProgressBar()
        .addSpecialTexture(70, 11, 72, 40, GGUITextures.PICTURE_COMPONENT_ASSLINE)
        .frontend(ComponentAssemblyLineFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.AdvancedCircuitAssemblyLine.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamGateAssemblerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SteamgateAssembler")
        .maxIO(81, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_COMPRESS)
        .frontend(SteamGateAssemblerFrontend::new)
        .slotOverlaysSteam(
            (index, isFluid, isOutput, isSpecial) -> !isFluid && !isOutput ? GTUITextures.OVERLAY_SLOT_COMPRESSOR_STEAM
                : null)
        .progressBarSteam(GTUITextures.PROGRESSBAR_COMPRESS_STEAM)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.SteamGateAssembler.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> CactusWonderFakeRecipes = RecipeMapBuilder
        .of("gtnl.recipe.CactusWonder")
        .maxIO(1, 0, 0, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_ARROW_2_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamCactusWonder.get(1))))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamManufacturerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SteamManufacturer")
        .maxIO(9, 1, 0, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamManufacturer.get(1))))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamCarpenterRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SteamCarpenter")
        .maxIO(2, 2, 0, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_ARROW_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamCarpenter.get(1))))
        .build();

    public static final RecipeMap<RecipeMapBackend> LavaMakerRecipes = RecipeMapBuilder.of("gtnl.recipe.LavaMaker")
        .maxIO(1, 0, 0, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_COMPRESS_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamLavaMaker.get(1))))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamWoodcutterRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SteamWoodcutter")
        .maxIO(1, 3, 0, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_ARROW_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamWoodcutter.get(1))))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamExtractinatorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SteamExtractinator")
        .maxIO(1, 6, 1, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamExtractinator.get(1))))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamFusionReactorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SteamFusion")
        .maxIO(0, 0, 2, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamFusionReactor.get(1))))
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, Integer>comparing(recipe -> recipe.getMetadataOrDefault(SteamFusionTierKey.INSTANCE, 0))
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static final RecipeMap<RecipeMapBackend> InfernalCockRecipes = RecipeMapBuilder
        .of("gtnl.recipe.InfernalCock")
        .maxIO(1, 1, 0, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamInfernalCokeOven.get(1))))
        .build();

    public static final RecipeMap<RecipeMapBackend> RockBreakerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.RockBreakerRecipes")
        .maxIO(2, 1, 0, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamRockBreaker.get(1))))
        .build();

    public static final RecipeMap<RecipeMapBackend> ConvertToCircuitAssembler = RecipeMapBuilder
        .of("gtnl.recipe.ConvertToCircuitAssembler")
        .frontend(GTNLLogoFrontend::new)
        .maxIO(6, 1, 1, 0)
        .disableRegisterNEI()
        .build();

    public static final RecipeMap<RecipeMapBackend> ExtremeExtremeEntityCrusherRecipes = RecipeMapBuilder
        .of("gtnl.recipe.ExtremeExtremeEntityCrusherRecipes")
        .maxIO(1, 36, 0, 1)
        .progressBar(GTUITextures.PROGRESSBAR_COMPRESS)
        .frontend(ExtremeExtremeEntityCrusherFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.PrimitiveDistillationTower.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> LargeBioLabRecipes = RecipeMapBuilder.of("gtnl.recipe.LargeBioLabRecipes")
        .maxIO(6, 6, 3, 3)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.LargeBioLab.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> GasCollectorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.GasCollectorRecipes")
        .maxIO(3, 3, 1, 1)
        .progressBar(PROGRESSBAR_GAS_COLLECTOR)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.LargeGasCollector.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> EternalGregTechWorkshopUpgradeRecipes = RecipeMapBuilder
        .of("gtnl.recipe.EternalGregTechWorkshopUpgradeRecipes")
        .maxIO(20, 4, 0, 0)
        .addSpecialTexture(98, 40, 35, 13, GTUITextures.PICTURE_ARROW_GRAY)
        .dontUseProgressBar()
        .neiTransferRect(98, 40, 35, 13)
        .frontend(EGTWUpgradeCostFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.EternalGregTechWorkshop.get(1))
                .setHeight(314))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamWeatherModuleFakeRecipes = RecipeMapBuilder
        .of("gtnl.recipe.SteamWeatherModuleFakeRecipes")
        .maxIO(9, 1, 0, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo((builder -> builder.setDisplayStack(GTNLItemList.SteamWeatherModule.get(1))))
        .build();
}
