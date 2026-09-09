package com.science.gtnl.common.material;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.common.widget.ProgressBar;
import com.science.gtnl.common.gui.recipe.BloodSoulFrontend;
import com.science.gtnl.common.gui.recipe.EGTWUpgradeCostFrontend;
import com.science.gtnl.common.gui.recipe.ElectrocellGeneratorFrontend;
import com.science.gtnl.common.gui.recipe.ExtremeExtremeEntityCrusherFrontend;
import com.science.gtnl.common.gui.recipe.FallingTowerFrontend;
import com.science.gtnl.common.gui.recipe.GTNLLogoFrontend;
import com.science.gtnl.common.gui.recipe.GeneralFrontend;
import com.science.gtnl.common.gui.recipe.IndustrialInfusionCraftingRecipesFrontend;
import com.science.gtnl.common.gui.recipe.RocketAssemblerBackend;
import com.science.gtnl.common.gui.recipe.RocketAssemblerFrontend;
import com.science.gtnl.common.gui.recipe.SpaceMinerFrontend;
import com.science.gtnl.common.gui.recipe.SteamGateAssemblerBackend;
import com.science.gtnl.common.gui.recipe.SteamGateAssemblerFrontend;
import com.science.gtnl.common.gui.recipe.SteamLogoFrontend;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.recipes.data.CircuitNanitesRecipeData;
import com.science.gtnl.utils.recipes.data.NanitesIntegratedProcessingRecipesData;
import com.science.gtnl.utils.recipes.format.NaquadahReactorFormat;
import com.science.gtnl.utils.recipes.format.RealArtificialStarFormat;
import com.science.gtnl.utils.recipes.format.SteamWeatherFormat;
import com.science.gtnl.utils.recipes.metadata.CircuitNanitesDataMetadata;
import com.science.gtnl.utils.recipes.metadata.FuelRefiningMetadata;
import com.science.gtnl.utils.recipes.metadata.IsaMillMetadata;
import com.science.gtnl.utils.recipes.metadata.NanitesIntegratedProcessingMetadata;
import com.science.gtnl.utils.recipes.metadata.NaquadahReactorMetadata;
import com.science.gtnl.utils.recipes.metadata.ResourceCollectionModuleMetadata;
import com.science.gtnl.utils.recipes.metadata.SolorMuonCatalystMetadata;
import com.science.gtnl.utils.recipes.metadata.SteamFusionMetadata;

import goodgenerator.api.recipe.ComponentAssemblyLineFrontend;
import goodgenerator.client.GUI.GGUITextures;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.util.GTRecipe;
import gregtech.nei.GTNEIDefaultHandler;
import gregtech.nei.formatter.HeatingCoilSpecialValueFormatter;

public class GTNLRecipeMaps {

    public static final UITexture PROGRESSBAR_GAS_COLLECTOR = UITexture
        .fullImage(ModList.ScienceNotLeisure.ID, "gui/progressbar/gas_collector");

    public static final RecipeMap<RecipeMapBackend> RecombinationFusionReactorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.recombination_fusion_reactor", RecipeMapBackend::new)
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.GenerationEarthEngine.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> FallingTowerRecipes = Mods.BloodMagic.isModLoaded()
        ? RecipeMapBuilder.of("gtnl.recipe.falling_tower")
            .maxIO(1, 81, 0, 0)
            .progressBar(GTUITextures.PROGRESSBAR_COMPRESS)
            .frontend(FallingTowerFrontend::new)
            .neiHandlerInfo(
                builder -> builder.setDisplayStack(GTNLItemList.BloodSoulSacrificialArray.get(1))
                    .setMaxRecipesPerPage(1))
            .build()
        : null;

    public static final RecipeMap<RecipeMapBackend> BloodDemonInjectionRecipes = Mods.BloodMagic.isModLoaded()
        ? RecipeMapBuilder.of("gtnl.recipe.blood_demon_injection")
            .maxIO(4, 1, 1, 1)
            .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
            .frontend(BloodSoulFrontend::new)
            .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.BloodSoulSacrificialArray.get(1)))
            .build()
        : null;

    public static final RecipeMap<RecipeMapBackend> AlchemicChemistrySetRecipes = Mods.BloodMagic.isModLoaded()
        ? RecipeMapBuilder.of("gtnl.recipe.alchemic_chemistry_set")
            .maxIO(5, 1, 1, 1)
            .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
            .frontend(BloodSoulFrontend::new)
            .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.BloodSoulSacrificialArray.get(1)))
            .build()
        : null;

    public static final RecipeMap<RecipeMapBackend> RealArtificialStarRecipes = RecipeMapBuilder
        .of("gtnl.recipe.real_artificial_star.generating")
        .maxIO(1, 1, 0, 0)
        .neiSpecialInfoFormatter(RealArtificialStarFormat.INSTANCE)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.RealArtificialStar.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> PortalToAlfheimRecipes = RecipeMapBuilder
        .of("gtnl.recipe.portal_to_alfheim", RecipeMapBackend::new)
        .maxIO(4, 36, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.TeleportationArrayToAlfheim.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> RuneAltarRecipes = RecipeMapBuilder
        .of("gtnl.recipe.rune_altar", RecipeMapBackend::new)
        .maxIO(8, 1, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.TeleportationArrayToAlfheim.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> NatureSpiritArrayRecipes = RecipeMapBuilder
        .of("gtnl.recipe.nature_spirit_array")
        .maxIO(1, 0, 0, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.TeleportationArrayToAlfheim.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> ManaInfusionRecipes = RecipeMapBuilder
        .of("gtnl.recipe.mana_infusion")
        .maxIO(4, 1, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.TeleportationArrayToAlfheim.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> LapotronChipRecipes = RecipeMapBuilder
        .of("gtnl.recipe.lapotron_chip")
        .maxIO(9, 9, 3, 3)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.LapotronChip.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamCrackerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.steam_cracker")
        .maxIO(1, 0, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamCracking.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> CheatOreProcessingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.cheat_ore_processing")
        .maxIO(1, 9, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.CheatOreProcessingFactory.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> DesulfurizerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.desulfurizer")
        .maxIO(0, 1, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.Desulfurizer.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> PetrochemicalPlantRecipes = RecipeMapBuilder
        .of("gtnl.recipe.petrochemical_plant", RecipeMapBackend::new)
        .maxIO(4, 4, 4, 12)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.PetrochemicalPlant.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> SmeltingMixingFurnaceRecipes = RecipeMapBuilder
        .of("gtnl.recipe.smelting_mixing_furnace", RecipeMapBackend::new)
        .maxIO(8, 4, 16, 4)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.SmeltingMixingFurnace.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<RecipeMapBackend> RareEarthCentrifugalRecipes = RecipeMapBuilder
        .of("gtnl.recipe.rare_earth_centrifugal", RecipeMapBackend::new)
        .maxIO(1, 17, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.RareEarthCentrifugal.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> IndustrialShapedArcaneCraftingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.industrial_shaped_arcane_crafting")
        .maxIO(9, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.IndustrialArcaneAssembler.get(1)))
        .build();

    public static RecipeMap<RecipeMapBackend> MatterFabricatorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.matter_fabricator")
        .maxIO(2, 1, 0, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.MatterFabricator.get(1)))
        .build();

    public static RecipeMap<RecipeMapBackend> TheTwilightForestRecipes = RecipeMapBuilder
        .of("gtnl.recipe.the_twilight_forest", RecipeMapBackend::new)
        .maxIO(4, 16, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.LibraryOfRuina.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> FishingGroundRecipes = RecipeMapBuilder
        .of("gtnl.recipe.fishing_ground", RecipeMapBackend::new)
        .maxIO(4, 32, 4, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.LibraryOfRuina.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> IndustrialInfusionCraftingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.industrial_infusion_crafting")
        .maxIO(25, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(IndustrialInfusionCraftingRecipesFrontend::new)
        .neiTransferRect(100, 45, 18, 72)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.IndustrialArcaneAssembler.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> IsaMillRecipes = RecipeMapBuilder.of("gtnl.recipe.isa_mill")
        .maxIO(2, 1, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.IsaMill.get(1)))
        .neiRecipeComparator(
            Comparator.<GTRecipe, Integer>comparing(recipe -> recipe.getMetadataOrDefault(IsaMillMetadata.INSTANCE, 0))
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static RecipeMap<RecipeMapBackend> CellRegulatorRecipes = RecipeMapBuilder.of("gtnl.recipe.cell_regulator")
        .maxIO(2, 0, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.FlotationCellRegulator.get(1)))
        .build();

    public static RecipeMap<RecipeMapBackend> WoodDistillationRecipes = RecipeMapBuilder
        .of("gtnl.recipe.wood_distillation", RecipeMapBackend::new)
        .maxIO(1, 1, 1, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.WoodDistillation.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> MolecularTransformerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.molecular_transformer")
        .maxIO(2, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.MolecularTransformer.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> NaquadahReactorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.naquadah_reactor")
        .maxIO(0, 0, 2, 1)
        .dontUseProgressBar()
        .neiSpecialInfoFormatter(new NaquadahReactorFormat())
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.LargeNaquadahReactor.get(1)))
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, Pair<Integer, Long>>comparing(
                    recipe -> recipe.getMetadataOrDefault(NaquadahReactorMetadata.INSTANCE, Pair.of(0, 0L)))
                .thenComparing(GTRecipe::compareTo))
        .addSpecialTexture(59, 20, 58, 42, GGUITextures.PICTURE_NAQUADAH_REACTOR)
        .build();

    public static RecipeMap<RecipeMapBackend> DecayHastenerRecipes = RecipeMapBuilder.of("gtnl.recipe.decay_hastener")
        .maxIO(1, 1, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.DecayHastener.get(1)))
        .build();

    public static RecipeMap<RecipeMapBackend> FuelRefiningComplexRecipes = RecipeMapBuilder
        .of("gtnl.recipe.fuel_refining_complex")
        .maxIO(4, 0, 8, 1)
        .frontend(GeneralFrontend::new)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.FuelRefiningComplex.get(1)))
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, Integer>comparing(recipe -> recipe.getMetadataOrDefault(FuelRefiningMetadata.INSTANCE, 0))
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static RecipeMap<RecipeMapBackend> SpaceMinerRecipes = RecipeMapBuilder.of("gtnl.recipe.space_miner")
        .maxIO(2, 9, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(SpaceMinerFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.ResourceCollectionModule.get(1)))
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, Integer>comparing(
                    recipe -> recipe.getMetadataOrDefault(ResourceCollectionModuleMetadata.INSTANCE, 0))
                .thenComparing(GTRecipe::compareTo))
        .neiRecipeComparator(
            Comparator.<GTRecipe, Integer>comparing(recipe -> recipe.mSpecialValue)
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static RecipeMap<RecipeMapBackend> SpaceDrillRecipes = RecipeMapBuilder.of("gtnl.recipe.space_drill")
        .maxIO(2, 0, 1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.ResourceCollectionModule.get(1)))
        .build();

    public static RecipeMap<RecipeMapBackend> PlatinumBasedTreatmentRecipes = RecipeMapBuilder
        .of("gtnl.recipe.platinum_based_treatment")
        .maxIO(8, 12, 4, 4)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.PlatinumBasedTreatment.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> ShallowChemicalCouplingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.shallow_chemical_coupling")
        .maxIO(16, 16, 16, 16)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.ShallowChemicalCoupling.get(1))
                .setMaxRecipesPerPage(1))
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .build();

    public static RecipeMap<RecipeMapBackend> TreeDiagramRecipes = RecipeMapBuilder.of("gtnl.recipe.tree_diagram")
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
            builder -> builder.setDisplayStack(GTNLItemList.TreeDiagram.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static final RecipeMap<SteamGateAssemblerBackend> SteamGateAssemblerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.steam_gate_assembler", SteamGateAssemblerBackend::new)
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
        .of("gtnl.recipe.cactus_wonder_fake")
        .maxIO(1, 0, 0, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_ARROW_2_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamCactusWonder.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamManufacturerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.steam_manufacturer")
        .maxIO(9, 1, 0, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamManufacturer.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamCarpenterRecipes = RecipeMapBuilder
        .of("gtnl.recipe.steam_carpenter")
        .maxIO(2, 2, 0, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_ARROW_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamCarpenter.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> LavaMakerRecipes = RecipeMapBuilder.of("gtnl.recipe.lava_maker")
        .maxIO(1, 0, 0, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_COMPRESS_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamLavaMaker.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> WoodcutterRecipes = RecipeMapBuilder.of("gtnl.recipe.woodcutter")
        .maxIO(1, 4, 0, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_ARROW_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamWoodcutter.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamExtractinatorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.steam_extractinator")
        .maxIO(1, 6, 1, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamExtractinator.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamFusionReactorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.steam_fusion_reactor")
        .maxIO(0, 0, 2, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamFusionReactor.get(1)))
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, Integer>comparing(recipe -> recipe.getMetadataOrDefault(SteamFusionMetadata.INSTANCE, 0))
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static final RecipeMap<RecipeMapBackend> InfernalCockRecipes = RecipeMapBuilder
        .of("gtnl.recipe.infernal_coke_oven")
        .maxIO(1, 1, 0, 1)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamInfernalCokeOven.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> RockBreakerRecipes = RecipeMapBuilder.of("gtnl.recipe.rock_breaker")
        .maxIO(2, 1, 0, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamRockBreaker.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> ExtremeExtremeEntityCrusherRecipes = RecipeMapBuilder
        .of("gtnl.recipe.extreme_extreme_entity_crusher")
        .maxIO(1, 36, 0, 1)
        .progressBar(GTUITextures.PROGRESSBAR_COMPRESS)
        .frontend(ExtremeExtremeEntityCrusherFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.HighwayToHell.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> LargeBioLabRecipes = RecipeMapBuilder.of("gtnl.recipe.large_bio_lab")
        .maxIO(6, 6, 3, 3)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.LargeBioLab.get(1)))
        .build();

    public static RecipeMap<RecipeMapBackend> GasCollectorRecipes = RecipeMapBuilder.of("gtnl.recipe.gas_collector")
        .maxIO(3, 3, 1, 1)
        .progressBar(PROGRESSBAR_GAS_COLLECTOR)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.LargeGasCollector.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> EternalGregTechWorkshopUpgradeRecipes = RecipeMapBuilder
        .of("gtnl.recipe.eternal_greg_tech_workshop_upgrade")
        .maxIO(20, 4, 0, 0)
        .addSpecialTexture(98, 40, 35, 13, GTUITextures.PICTURE_ARROW_GRAY)
        .dontUseProgressBar()
        .neiTransferRect(98, 40, 35, 13)
        .frontend(EGTWUpgradeCostFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.EternalGregTechWorkshop.get(1))
                .setHeight(314))
        .build();

    public static final RecipeMap<RecipeMapBackend> SteamWeatherModuleRecipes = RecipeMapBuilder
        .of("gtnl.recipe.steam_weather_module")
        .maxIO(4, 0, 0, 0)
        .progressBarSteam(GTUITextures.PROGRESSBAR_EXTRACT_STEAM)
        .frontend(SteamLogoFrontend::new)
        .neiSpecialInfoFormatter(new SteamWeatherFormat())
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.SteamWeatherModule.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> ShimmerRecipes = RecipeMapBuilder.of("gtnl.recipe.shimmer")
        .maxIO(1, 20, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_COMPRESS)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.ShimmerFluidBlock.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> PlasmaCentrifugeRecipes = RecipeMapBuilder
        .of("gtnl.recipe.plasma_centrifuge")
        .maxIO(1, 0, 4, 20)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.LargeGasCollector.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    public static RecipeMap<RecipeMapBackend> PlasmaCondensationRecipes = RecipeMapBuilder
        .of("gtnl.recipe.plasma_condensation")
        .maxIO(1, 1, 2, 2)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.CompoundExtremeCoolingUnit.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> PrimitiveBrickKilnRecipes = RecipeMapBuilder
        .of("gtnl.recipe.primitive_brick_kiln")
        .maxIO(9, 1, 1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.PrimitiveBrickKiln.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> ElectrocellGeneratorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.electrocell_generator")
        .maxIO(2, 1, 1, 2)
        .progressBar(GTUITextures.PROGRESSBAR_SIFT, ProgressBar.Direction.DOWN)
        .progressBarPos(78, 26)
        .frontend(ElectrocellGeneratorFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.ElectrocellGenerator.get(1)))
        .build();

    public static final RecipeMap<RocketAssemblerBackend> RocketAssemblerRecipes = RecipeMapBuilder
        .of("gtnl.recipe.rocket_assembler", RocketAssemblerBackend::new)
        .maxIO(52, 1, 0, 0)
        .dontUseProgressBar()
        .frontend(RocketAssemblerFrontend::new)
        .useSpecialSlot()
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.RocketAssembler.get(1))
                .setMaxRecipesPerPage(1))
        .disableRegisterNEI()
        .build();

    public static final RecipeMap<RecipeMapBackend> CircuitNanitesDataRecipes = RecipeMapBuilder
        .of("gtnl.recipe.circuit_nanites_data")
        .maxIO(1, 0, 0, 0)
        .dontUseProgressBar()
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, CircuitNanitesRecipeData>comparing(
                    recipe -> recipe
                        .getMetadataOrDefault(CircuitNanitesDataMetadata.INSTANCE, new CircuitNanitesRecipeData()))
                .thenComparing(GTRecipe::compareTo))
        .frontend((ui, nei) -> new GTNLLogoFrontend(ui, nei) {

            @Override
            @NotNull
            public List<Pos2d> getItemInputPositions(int itemInputCount) {
                return Collections.singletonList(new Pos2d(9, 13));
            }
        })
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.TreeDiagram.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> IndustrialRockCrusherRecipes = RecipeMapBuilder
        .of("gtnl.recipe.industrial_rock_crusher")
        .maxIO(1, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.LargeRockCrusher.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> PrecisionLaserEngraverRecipes = RecipeMapBuilder
        .of("gtnl.recipe.precision_laser_engraver")
        .maxIO(9, 3, 3, 3)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNLLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.EngravingLaserPlant.get(1)))
        .build();

    public static RecipeMap<RecipeMapBackend> NanitesIntegratedProcessingRecipes = RecipeMapBuilder
        .of("gtnl.recipe.nanites_integrated_processing")
        .maxIO(16, 16, 8, 8)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.NanitesIntegratedProcessingCenter.get(1))
                .setMaxRecipesPerPage(1))
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, NanitesIntegratedProcessingRecipesData>comparing(
                    recipe -> recipe.getMetadataOrDefault(
                        NanitesIntegratedProcessingMetadata.INSTANCE,
                        new NanitesIntegratedProcessingRecipesData(false, false, false)))
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static final RecipeMap<RecipeMapBackend> ElectricNeutronActivatorRecipes = RecipeMapBuilder
        .of("gtnl.recipe.electric_neutron_activator")
        .maxIO(9, 9, 1, 1)
        .frontend(GTNLLogoFrontend::new)
        .dontUseProgressBar()
        .addSpecialTexture(73, 22, 31, 21, GGUITextures.PICTURE_NEUTRON_ACTIVATOR)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.EngravingLaserPlant.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> MicroorganismMasterRecipes = RecipeMapBuilder
        .of("gtnl.recipe.microorganism_master")
        .maxIO(9, 1, 1, 1)
        .frontend(
            (uiPropertiesBuilder,
                neiPropertiesBuilder) -> new GTNLLogoFrontend(uiPropertiesBuilder, neiPropertiesBuilder) {

                    @Override
                    public void drawNEIOverlayForInput(GTNEIDefaultHandler.@NotNull FixedPositionedStack stack) {
                        super.drawNEIOverlayForInput(stack);
                        drawFluidOverlay(stack);
                    }

                    @Override
                    public void drawNEIOverlayForOutput(GTNEIDefaultHandler.@NotNull FixedPositionedStack stack) {
                        super.drawNEIOverlayForOutput(stack);
                        drawFluidOverlay(stack);
                    }

                    public void drawFluidOverlay(GTNEIDefaultHandler.FixedPositionedStack stack) {
                        if (stack.isFluid()) {
                            drawNEIOverlayText(
                                "+",
                                stack,
                                colorOverride.getTextColorOrDefault("nei_overlay_yellow", 0xFDD835),
                                0.5f,
                                true,
                                Alignment.TopRight);
                        }
                    }
                })
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.MicroorganismMaster.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> SolarMuonCatalystRecipes = RecipeMapBuilder
        .of("gtnl.recipe.solar_muon_catalyst", RecipeMapBackend::new)
        .maxIO(1, 0, 8, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNLItemList.FOGSolarMuonCatalystModule.get(1))
                .setMaxRecipesPerPage(1))
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, Boolean>comparing(
                    recipe -> recipe.getMetadataOrDefault(SolorMuonCatalystMetadata.INSTANCE, false))
                .thenComparing(GTRecipe::compareTo))
        .build();

    public static final RecipeMap<RecipeMapBackend> GrandAssemblyLineSpecialRecipes = RecipeMapBuilder
        .of("gtnl.recipe.grand_assembly_line_special")
        .maxIO(1, 1, 0, 0)
        .frontend(GTNLLogoFrontend::new)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNLItemList.GrandAssemblyLine.get(1)))
        .build();

    public static final RecipeMap<RecipeMapBackend> HardOverrideRecipes = RecipeMapBuilder
        .of("gtnl.recipe.hard_override")
        .maxIO(16, 16, 16, 16)
        .dontUseProgressBar()
        .disableRegisterNEI()
        .build();

}
