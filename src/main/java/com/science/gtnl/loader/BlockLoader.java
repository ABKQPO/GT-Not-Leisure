package com.science.gtnl.loader;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnllib.registry.Registrar;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.block.TileEntityMigrationHandler;
import com.science.gtnl.common.block.blocks.BlockArtificialStarRender;
import com.science.gtnl.common.block.blocks.BlockBeamFormer;
import com.science.gtnl.common.block.blocks.BlockCardboardBox;
import com.science.gtnl.common.block.blocks.BlockDimensionRespawnAnchor;
import com.science.gtnl.common.block.blocks.BlockDirePatternEncoder;
import com.science.gtnl.common.block.blocks.BlockEnderElevator;
import com.science.gtnl.common.block.blocks.BlockEssentiaHatch;
import com.science.gtnl.common.block.blocks.BlockEternalGregTechWorkshopRender;
import com.science.gtnl.common.block.blocks.BlockHoneyFluid;
import com.science.gtnl.common.block.blocks.BlockLaserBeacon;
import com.science.gtnl.common.block.blocks.BlockMEChisel;
import com.science.gtnl.common.block.blocks.BlockMultiEssentiaInputHatch;
import com.science.gtnl.common.block.blocks.BlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.BlockMultiEssentiaTube;
import com.science.gtnl.common.block.blocks.BlockNanoPhagocytosisPlantRender;
import com.science.gtnl.common.block.blocks.BlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.BlockPlayerDoll;
import com.science.gtnl.common.block.blocks.BlockPlayerLeash;
import com.science.gtnl.common.block.blocks.BlockSaplingBrickuoia;
import com.science.gtnl.common.block.blocks.BlockSearedLadder;
import com.science.gtnl.common.block.blocks.BlockShimmerFluid;
import com.science.gtnl.common.block.blocks.BlockSuperDenseEnergyCell;
import com.science.gtnl.common.block.blocks.BlockSuperDualInterface;
import com.science.gtnl.common.block.blocks.BlockSuperInterface;
import com.science.gtnl.common.block.blocks.BlockWaterCandle;
import com.science.gtnl.common.block.blocks.BlocksCompressedStargate;
import com.science.gtnl.common.block.blocks.item.ItemBlockArtificialStarRender;
import com.science.gtnl.common.block.blocks.item.ItemBlockBeamFormer;
import com.science.gtnl.common.block.blocks.item.ItemBlockCardboardBox;
import com.science.gtnl.common.block.blocks.item.ItemBlockDimensionRespawnAnchor;
import com.science.gtnl.common.block.blocks.item.ItemBlockDirePatternEncoder;
import com.science.gtnl.common.block.blocks.item.ItemBlockEssentiaHatch;
import com.science.gtnl.common.block.blocks.item.ItemBlockLaserBeacon;
import com.science.gtnl.common.block.blocks.item.ItemBlockMEChisel;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaInputHatch;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaTube;
import com.science.gtnl.common.block.blocks.item.ItemBlockNanoPhagocytosisPlantRender;
import com.science.gtnl.common.block.blocks.item.ItemBlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.item.ItemBlockPlayerDoll;
import com.science.gtnl.common.block.blocks.item.ItemBlockPlayerLeash;
import com.science.gtnl.common.block.blocks.item.ItemBlockSearedLadder;
import com.science.gtnl.common.block.blocks.item.ItemBlockSuperDualInterface;
import com.science.gtnl.common.block.blocks.item.ItemBlockSuperInterface;
import com.science.gtnl.common.block.blocks.tile.TileEntityArtificialStar;
import com.science.gtnl.common.block.blocks.tile.TileEntityBeamFormer;
import com.science.gtnl.common.block.blocks.tile.TileEntityCardboardBox;
import com.science.gtnl.common.block.blocks.tile.TileEntityDimensionRespawnAnchor;
import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.common.block.blocks.tile.TileEntityEnderElevator;
import com.science.gtnl.common.block.blocks.tile.TileEntityEssentiaHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityLaserBeacon;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaInputHatch;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaTube;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
import com.science.gtnl.common.block.blocks.tile.TileEntitySearedLadder;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperDenseEnergyCell;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperDualInterface;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperInterface;
import com.science.gtnl.common.block.blocks.tile.TileEntityWaterCandle;
import com.science.gtnl.common.block.casings.base.ItemBlockBase;
import com.science.gtnl.common.block.casings.base.MetaBlockBase;
import com.science.gtnl.common.block.casings.casing.MetaCasing;
import com.science.gtnl.common.block.casings.casing.MetaItemBlockCasing;
import com.science.gtnl.common.block.casings.column.ItemBlockColumn;
import com.science.gtnl.common.block.casings.column.MetaBlockColumn;
import com.science.gtnl.common.block.casings.glass.ItemBlockGlass;
import com.science.gtnl.common.block.casings.glass.MetaBlockGlass;
import com.science.gtnl.common.block.casings.glow.ItemBlockGlow;
import com.science.gtnl.common.block.casings.glow.MetaBlockGlow;
import com.science.gtnl.common.item.GTNLCreativeTabs;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.text.AnimatedText;
import com.science.gtnl.utils.text.AnimatedTooltipHandler;

import bartworks.common.loaders.ItemRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.util.GTRecipeBuilder;

public class BlockLoader {

    public static final Registrar BLOCKS = new Registrar(
        "gtnl.",
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        GTNLCreativeTabs.GTNotLeisureBlock);

    public static BlockSaplingBrickuoia saplingBrickuoia;
    public static BlockCardboardBox cardboardBox;
    public static BlockArtificialStarRender artificialStarRender;
    public static BlockLaserBeacon laserBeacon;
    public static BlockPlayerDoll playerDoll;
    public static BlockPigmeeFumo pigmeeFumo;
    public static BlockWaterCandle waterCandle;
    public static BlockSearedLadder searedLadder;
    public static BlockPlayerLeash playerLeash;
    public static BlockDirePatternEncoder direPatternEncoder;
    public static BlockMEChisel meChisel;
    public static BlockSuperInterface superInterface;
    public static BlockSuperDualInterface superDualInterface;
    public static BlockSuperDenseEnergyCell superDenseEnergyCell;
    public static BlockBeamFormer beamFormer;
    public static BlockNanoPhagocytosisPlantRender nanoPhagocytosisPlantRender;
    public static BlockEternalGregTechWorkshopRender eternalGregTechWorkshopRender;
    public static BlockDimensionRespawnAnchor dimensionRespawnAnchor;
    public static BlockEssentiaHatch essentiaHatch;
    public static BlockMultiEssentiaInputHatch multiEssentiaInputHatch;
    public static BlockMultiEssentiaJar multiEssentiaJar;
    public static BlockMultiEssentiaTube multiEssentiaTube;
    public static BlockEnderElevator enderElevatorBlock, enderElevatorSlab, enderElevatorCarpet;

    public static BlockHoneyFluid honeyFluidBlock;
    public static Fluid honeyFluid;
    public static BlockShimmerFluid shimmerFluidBlock;
    public static Fluid shimmerFluid;

    public static BlocksCompressedStargate compressedStargateTier0 = new BlocksCompressedStargate(0);
    public static BlocksCompressedStargate compressedStargateTier1 = new BlocksCompressedStargate(1);
    public static BlocksCompressedStargate compressedStargateTier2 = new BlocksCompressedStargate(2);
    public static BlocksCompressedStargate compressedStargateTier3 = new BlocksCompressedStargate(3);
    public static BlocksCompressedStargate compressedStargateTier4 = new BlocksCompressedStargate(4);
    public static BlocksCompressedStargate compressedStargateTier5 = new BlocksCompressedStargate(5);
    public static BlocksCompressedStargate compressedStargateTier6 = new BlocksCompressedStargate(6);
    public static BlocksCompressedStargate compressedStargateTier7 = new BlocksCompressedStargate(7);
    public static BlocksCompressedStargate compressedStargateTier8 = new BlocksCompressedStargate(8);
    public static BlocksCompressedStargate compressedStargateTier9 = new BlocksCompressedStargate(9);

    public static MetaBlockBase metaBlock = new MetaBlockBase("meta_block");
    public static MetaBlockGlow metaBlockGlow = new MetaBlockGlow("meta_block_glow");
    public static MetaBlockGlass metaBlockGlass = new MetaBlockGlass("meta_block_glass");
    public static MetaBlockColumn metaBlockColumn = new MetaBlockColumn("meta_block_column");
    public static MetaCasing metaCasing = new MetaCasing("meta_casing", (byte) 0);
    public static MetaCasing metaCasing02 = new MetaCasing(
        "meta_casing_02",
        "meta_casing02",
        (byte) 32,
        MetaCasing.TEXTURE_PAGE_INDEX);

    public static void registryBlocks() {
        playerLeash = BLOCKS.block("player_leash", new BlockPlayerLeash())
            .itemBlock(ItemBlockPlayerLeash.class)
            .texture("planks_oak")
            .bind(GTNLItemList.PlayerLeash::set)
            .register();
        searedLadder = BLOCKS.block("seared_ladder", new BlockSearedLadder())
            .itemBlock(ItemBlockSearedLadder.class)
            .texture(RESOURCE_ROOT_ID + ":seared_ladder/fancy_bricks")
            .tile(TileEntitySearedLadder.class, "seared_ladder_tile_entity")
            .bind(GTNLItemList.SearedLadder::set)
            .then(BlockSearedLadder::registerItemProperties)
            .register();
        direPatternEncoder = BLOCKS.block("dire_pattern_encoder", new BlockDirePatternEncoder())
            .itemBlock(ItemBlockDirePatternEncoder.class)
            .tile(TileEntityDirePatternEncoder.class, "dire_pattern_encoder_tile_entity")
            .bind(GTNLItemList.DirePatternEncoder::set)
            .register();
        meChisel = BLOCKS.block("me_chisel", new BlockMEChisel())
            .itemBlock(ItemBlockMEChisel.class)
            .noTexture()
            .tile(TileEntityMEChisel.class, "me_chisel_tile_entity")
            .bind(GTNLItemList.MEChisel::set)
            .then(BlockMEChisel::registerItemProperties)
            .register();
        superInterface = BLOCKS.block("super_interface", new BlockSuperInterface())
            .itemBlock(ItemBlockSuperInterface.class)
            .tile(TileEntitySuperInterface.class, "super_interface_tile_entity")
            .bind(GTNLItemList.SuperInterface::set)
            .register();
        superDualInterface = BLOCKS.block("super_dual_interface", new BlockSuperDualInterface())
            .itemBlock(ItemBlockSuperDualInterface.class)
            .tile(TileEntitySuperDualInterface.class, "super_dual_interface_tile_entity")
            .bind(GTNLItemList.SuperDualInterface::set)
            .register();
        BlockSuperDenseEnergyCell energyCell = new BlockSuperDenseEnergyCell();
        superDenseEnergyCell = BLOCKS.block("super_dense_energy_cell", energyCell)
            .itemBlock(energyCell.getItemBlockClass())
            .noTexture()
            .tile(TileEntitySuperDenseEnergyCell.class, "super_dense_energy_cell_tile_entity")
            .bind(GTNLItemList.SuperDenseEnergyCell::set)
            .register();
        beamFormer = BLOCKS.block("beam_former", new BlockBeamFormer())
            .itemBlock(ItemBlockBeamFormer.class)
            .texture(RESOURCE_ROOT_ID + ":beam_former/beam_former_block_model")
            .tile(TileEntityBeamFormer.class, "beam_former_tile_entity")
            .bind(GTNLItemList.BlockBeamFormer::set)
            .register();
        cardboardBox = BLOCKS.block("cardboard_box", new BlockCardboardBox())
            .itemBlock(ItemBlockCardboardBox.class)
            .noTexture()
            .tile(TileEntityCardboardBox.class, "cardboard_box_tile_entity")
            .bind(GTNLItemList.CardboardBox::set)
            .register();
        eternalGregTechWorkshopRender = new BlockEternalGregTechWorkshopRender();
        nanoPhagocytosisPlantRender = BLOCKS
            .block("nano_phagocytosis_plant_render", new BlockNanoPhagocytosisPlantRender())
            .itemBlock(ItemBlockNanoPhagocytosisPlantRender.class)
            .lang("gtnl.nano_phagocytosis_plant_renderer")
            .noTexture()
            .tile(TileEntityNanoPhagocytosisPlant.class, "nano_phagocytosis_plant_render_tile_entity")
            .bind(GTNLItemList.NanoPhagocytosisPlantRender::set)
            .register();
        artificialStarRender = BLOCKS.block("artificial_star_render", new BlockArtificialStarRender())
            .itemBlock(ItemBlockArtificialStarRender.class)
            .lang("gtnl.artificial_star_renderer")
            .noTexture()
            .tile(TileEntityArtificialStar.class, "artificial_star_render_tile_entity")
            .bind(GTNLItemList.ArtificialStarRender::set)
            .register();
        playerDoll = BLOCKS.block("player_doll", new BlockPlayerDoll())
            .itemBlock(ItemBlockPlayerDoll.class)
            .noTexture()
            .tab(GTNLCreativeTabs.GTNotLeisurePlayerDoll)
            .tile(TileEntityPlayerDoll.class, "player_doll_tile_entity")
            .bind(GTNLItemList.PlayerDoll::set)
            .register();
        pigmeeFumo = BLOCKS.block("pigmee_fumo", new BlockPigmeeFumo())
            .itemBlock(ItemBlockPigmeeFumo.class)
            .texture(RESOURCE_ROOT_ID + ":blocks/pigmee_fumo")
            .tile(TileEntityPigmeeFumo.class, "pigmee_fumo_tile_entity")
            .bind(GTNLItemList.PigmeeFumo::set)
            .then(BlockPigmeeFumo::registerItemProperties)
            .register();
        laserBeacon = BLOCKS.block("laser_beacon", new BlockLaserBeacon())
            .itemBlock(ItemBlockLaserBeacon.class)
            .noTexture()
            .tile(TileEntityLaserBeacon.class, "laser_beacon_tile_entity")
            .bind(GTNLItemList.LaserBeacon::set)
            .then(block -> GregTechAPI.registerMachineBlock(block, -1))
            .register();
        waterCandle = BLOCKS.block("water_candle", new BlockWaterCandle())
            .tile(TileEntityWaterCandle.class, "water_candle_tile_entity")
            .bind(GTNLItemList.WaterCandle::set)
            .register();
        dimensionRespawnAnchor = BLOCKS.block("dimension_respawn_anchor", new BlockDimensionRespawnAnchor())
            .itemBlock(ItemBlockDimensionRespawnAnchor.class)
            .noTexture()
            .tab(null)
            .tile(TileEntityDimensionRespawnAnchor.class, "dimension_respawn_anchor_tile_entity")
            .bind(GTNLItemList.DimensionRespawnAnchor::set)
            .register();

        essentiaHatch = BLOCKS.block("essentia_hatch", new BlockEssentiaHatch())
            .itemBlock(ItemBlockEssentiaHatch.class)
            .tab(GTNLCreativeTabs.GTNotLeisureMachine)
            .tile(TileEntityEssentiaHatch.class, "essentia_hatch_tile_entity")
            .bind(GTNLItemList.EssentiaHatch::set)
            .then(block -> GregTechAPI.registerMachineBlock(block, -1))
            .register();
        multiEssentiaInputHatch = BLOCKS.block("multi_essentia_input_hatch", new BlockMultiEssentiaInputHatch())
            .itemBlock(ItemBlockMultiEssentiaInputHatch.class)
            .texture(RESOURCE_ROOT_ID + ":essentia_hatch")
            .tab(GTNLCreativeTabs.GTNotLeisureMachine)
            .tile(TileEntityMultiEssentiaInputHatch.class, "multi_essentia_input_hatch_tile_entity")
            .bind(GTNLItemList.MultiEssentiaInputHatch::set)
            .then(block -> GregTechAPI.registerMachineBlock(block, -1))
            .register();
        multiEssentiaJar = BLOCKS.block("multi_essentia_jar", new BlockMultiEssentiaJar())
            .itemBlock(ItemBlockMultiEssentiaJar.class)
            .noTexture()
            .tile(TileEntityMultiEssentiaJar.class, "multi_essentia_jar_tile_entity")
            .bind(GTNLItemList.MultiEssentiaJar::set)
            .register();
        multiEssentiaTube = BLOCKS.block("multi_essentia_tube", new BlockMultiEssentiaTube())
            .itemBlock(ItemBlockMultiEssentiaTube.class)
            .noTexture()
            .tile(TileEntityMultiEssentiaTube.class, "multi_essentia_tube_tile_entity")
            .then(
                block -> GTNLItemList.MultiEssentiaTube
                    .set(new ItemStack(block, 1, BlockMultiEssentiaTube.BUFFER_METADATA)))
            .register();
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EssentiaHatch.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MultiEssentiaInputHatch.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.MultiEssentiaInputHatch.get(1), AnimatedText.AUTHOR_LINEBROKE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MultiEssentiaJar.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MultiEssentiaJar.get(1), AnimatedText.AUTHOR_LINEBROKE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MultiEssentiaTube.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MultiEssentiaTube.get(1), AnimatedText.AUTHOR_LINEBROKE);

        enderElevatorBlock = new BlockEnderElevator(0);
        enderElevatorSlab = new BlockEnderElevator(1);
        enderElevatorCarpet = new BlockEnderElevator(2);
        GTNLItemList.EnderElevatorBlock.set(new ItemStack(enderElevatorBlock));
        GTNLItemList.EnderElevatorSlab.set(new ItemStack(enderElevatorSlab));
        GTNLItemList.EnderElevatorCarpet.set(new ItemStack(enderElevatorCarpet));
        GameRegistry.registerTileEntity(TileEntityEnderElevator.class, "ender_elevator_tile_entity");

        GameRegistry.registerBlock(metaBlock, ItemBlockBase.class, "meta_block");
        GameRegistry.registerBlock(metaBlockGlow, ItemBlockGlow.class, "meta_block_glow");
        GameRegistry.registerBlock(metaBlockGlass, ItemBlockGlass.class, "meta_block_glass");
        GameRegistry.registerBlock(metaBlockColumn, ItemBlockColumn.class, "meta_block_column");
        GameRegistry.registerBlock(metaCasing, MetaItemBlockCasing.class, "meta_casing");
        GameRegistry.registerBlock(metaCasing02, MetaItemBlockCasing.class, "meta_casing_02");

        GTNLItemList.CompressedStargateTier0.set(new ItemStack(compressedStargateTier0));
        GTNLItemList.CompressedStargateTier1.set(new ItemStack(compressedStargateTier1));
        GTNLItemList.CompressedStargateTier2.set(new ItemStack(compressedStargateTier2));
        GTNLItemList.CompressedStargateTier3.set(new ItemStack(compressedStargateTier3));
        GTNLItemList.CompressedStargateTier4.set(new ItemStack(compressedStargateTier4));
        GTNLItemList.CompressedStargateTier5.set(new ItemStack(compressedStargateTier5));
        GTNLItemList.CompressedStargateTier6.set(new ItemStack(compressedStargateTier6));
        GTNLItemList.CompressedStargateTier7.set(new ItemStack(compressedStargateTier7));
        GTNLItemList.CompressedStargateTier8.set(new ItemStack(compressedStargateTier8));
        GTNLItemList.CompressedStargateTier9.set(new ItemStack(compressedStargateTier9));

        honeyFluid = registerFluid(
            new Fluid("honey").setUnlocalizedName("gtnl.fluid.honey")
                .setViscosity(6000)
                .setDensity(1500));
        honeyFluidBlock = new BlockHoneyFluid(honeyFluid);
        GTNLItemList.HoneyFluidBlock.set(new ItemStack(honeyFluidBlock));

        shimmerFluid = registerFluid(
            new Fluid("shimmer").setUnlocalizedName("gtnl.fluid.shimmer")
                .setViscosity(800));
        shimmerFluidBlock = new BlockShimmerFluid(shimmerFluid);
        GTNLItemList.ShimmerFluidBlock.set(new ItemStack(shimmerFluidBlock));

        GTNLItemList.ShirabonReinforcedBoronSilicateGlass.set(new ItemStack(ItemRegistry.bw_realglas2, 1, 6));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.ShirabonReinforcedBoronSilicateGlass.get(1),
            AnimatedText.GT_NOT_LEISURE_CHANGE);
        GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.set(new ItemStack(ItemRegistry.bw_realglas2, 1, 7));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.get(1),
            AnimatedText.GT_NOT_LEISURE_CHANGE);
    }

    private static Fluid registerFluid(Fluid fluid) {
        if (FluidRegistry.registerFluid(fluid)) {
            return fluid;
        }
        Fluid registeredFluid = FluidRegistry.getFluid(fluid.getName());
        return registeredFluid == null ? fluid : registeredFluid;
    }

    public static void registryBlockContainers() {

        GTNLItemList.TestMetaBlock01_0.set(ItemBlockBase.initMetaBlock(0));
        GTNLItemList.NewHorizonsCoil.set(
            ItemBlockBase.initMetaBlock(
                1,
                new String[] { AnimatedTooltipHandler.RESET + StatCollector.translateToLocal("gt.coilheattooltip") }));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "179,769,313,486,231,590,772,930,519,078,902,473,361,797,697,894,230,657,273,430,081,",
                1,
                80,
                AnimatedTooltipHandler.RED,
                AnimatedTooltipHandler.GOLD,
                AnimatedTooltipHandler.YELLOW,
                AnimatedTooltipHandler.GREEN,
                AnimatedTooltipHandler.AQUA,
                AnimatedTooltipHandler.BLUE,
                AnimatedTooltipHandler.LIGHT_PURPLE));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "157,732,675,805,500,963,132,708,477,322,407,536,021,120,113,879,871,393,357,658,789,",
                1,
                80,
                AnimatedTooltipHandler.GOLD,
                AnimatedTooltipHandler.YELLOW,
                AnimatedTooltipHandler.GREEN,
                AnimatedTooltipHandler.AQUA,
                AnimatedTooltipHandler.BLUE,
                AnimatedTooltipHandler.LIGHT_PURPLE,
                AnimatedTooltipHandler.RED));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "768,814,416,622,492,847,430,639,474,124,377,767,893,424,865,485,276,302,219,601,246,",
                1,
                80,
                AnimatedTooltipHandler.YELLOW,
                AnimatedTooltipHandler.GREEN,
                AnimatedTooltipHandler.AQUA,
                AnimatedTooltipHandler.BLUE,
                AnimatedTooltipHandler.LIGHT_PURPLE,
                AnimatedTooltipHandler.RED,
                AnimatedTooltipHandler.GOLD));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "094,119,453,082,952,085,005,768,838,150,682,342,462,881,473,913,110,540,827,237,163,",
                1,
                80,
                AnimatedTooltipHandler.GREEN,
                AnimatedTooltipHandler.AQUA,
                AnimatedTooltipHandler.BLUE,
                AnimatedTooltipHandler.LIGHT_PURPLE,
                AnimatedTooltipHandler.RED,
                AnimatedTooltipHandler.GOLD,
                AnimatedTooltipHandler.YELLOW));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "350,510,684,586,298,239,947,245,938,479,716,304,835,356,329,624,224,137,216"
                    + StatCollector.translateToLocal("gt.coilunittooltip"),
                1,
                80,
                AnimatedTooltipHandler.AQUA,
                AnimatedTooltipHandler.BLUE,
                AnimatedTooltipHandler.LIGHT_PURPLE,
                AnimatedTooltipHandler.RED,
                AnimatedTooltipHandler.GOLD,
                AnimatedTooltipHandler.YELLOW,
                AnimatedTooltipHandler.GREEN));

        GTNLItemList.StargateCoil.set(ItemBlockBase.initMetaBlock(2));
        GTNLItemList.BlackLampOff.set(
            ItemBlockBase.initMetaBlock(3, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.BlackLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                4,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.PinkLampOff.set(
            ItemBlockBase.initMetaBlock(5, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.PinkLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                6,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.RedLampOff.set(
            ItemBlockBase.initMetaBlock(7, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.RedLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                8,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.OrangeLampOff.set(
            ItemBlockBase.initMetaBlock(9, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.OrangeLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                10,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.YellowLampOff.set(
            ItemBlockBase
                .initMetaBlock(11, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.YellowLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                12,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.GreenLampOff.set(
            ItemBlockBase
                .initMetaBlock(13, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.GreenLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                14,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.LimeLampOff.set(
            ItemBlockBase
                .initMetaBlock(15, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.LimeLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                16,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.BlueLampOff.set(
            ItemBlockBase
                .initMetaBlock(17, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.BlueLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                18,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.LightBlueLampOff.set(
            ItemBlockBase
                .initMetaBlock(19, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.LightBlueLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                20,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.CyanLampOff.set(
            ItemBlockBase
                .initMetaBlock(21, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.CyanLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                22,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.BrownLampOff.set(
            ItemBlockBase
                .initMetaBlock(23, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.BrownLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                24,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.MagentaLampOff.set(
            ItemBlockBase
                .initMetaBlock(25, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.MagentaLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                26,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.PurpleLampOff.set(
            ItemBlockBase
                .initMetaBlock(27, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.PurpleLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                28,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.GrayLampOff.set(
            ItemBlockBase
                .initMetaBlock(29, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.GrayLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                30,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.LightGrayLampOff.set(
            ItemBlockBase
                .initMetaBlock(31, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.LightGrayLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                32,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.WhiteLampOff.set(
            ItemBlockBase
                .initMetaBlock(33, new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow") }));
        GTNLItemList.WhiteLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                34,
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.no_glow"),
                    StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.BlazeCubeBlock.set(ItemBlockBase.initMetaBlock(35));
        GTNLItemList.CompressedStargateCoil.set(ItemBlockBase.initMetaBlock(36));
        GTNLItemList.CompressedStargateCoil1.set(ItemBlockBase.initMetaBlock(37));
        GTNLItemList.CompressedStargateCoil2.set(ItemBlockBase.initMetaBlock(38));
        GTNLItemList.CompressedStargateCoil3.set(ItemBlockBase.initMetaBlock(39));
        GTNLItemList.CompressedStargateCoil4.set(ItemBlockBase.initMetaBlock(40));
        GTNLItemList.CompressedStargateCoil5.set(ItemBlockBase.initMetaBlock(41));
        GTNLItemList.CompressedStargateCoil6.set(ItemBlockBase.initMetaBlock(42));
        GTNLItemList.CompressedStargateCoil7.set(ItemBlockBase.initMetaBlock(43));
        GTNLItemList.CompressedStargateCoil8.set(ItemBlockBase.initMetaBlock(44));
        GTNLItemList.CompressedStargateCoil9.set(ItemBlockBase.initMetaBlock(45));

        GTNLItemList.FortifyGlowstone.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.FortifyGlowstone.getBlockMeta()));
        GTNLItemList.BlackLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.BlackLamp.getBlockMeta()));
        GTNLItemList.BlackLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.BlackLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.PinkLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.PinkLamp.getBlockMeta()));
        GTNLItemList.PinkLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.PinkLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.RedLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.RedLamp.getBlockMeta()));
        GTNLItemList.RedLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.RedLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.OrangeLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.OrangeLamp.getBlockMeta()));
        GTNLItemList.OrangeLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.OrangeLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.YellowLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.YellowLamp.getBlockMeta()));
        GTNLItemList.YellowLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.YellowLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.GreenLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.GreenLamp.getBlockMeta()));
        GTNLItemList.GreenLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.GreenLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.LimeLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.LimeLamp.getBlockMeta()));
        GTNLItemList.LimeLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.LimeLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.BlueLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.BlueLamp.getBlockMeta()));
        GTNLItemList.BlueLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.BlueLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.LightBlueLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.LightBlueLamp.getBlockMeta()));
        GTNLItemList.LightBlueLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.LightBlueLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.CyanLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.CyanLamp.getBlockMeta()));
        GTNLItemList.CyanLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.CyanLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.BrownLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.BrownLamp.getBlockMeta()));
        GTNLItemList.BrownLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.BrownLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.MagentaLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.MagentaLamp.getBlockMeta()));
        GTNLItemList.MagentaLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.MagentaLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.PurpleLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.PurpleLamp.getBlockMeta()));
        GTNLItemList.PurpleLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.PurpleLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.GrayLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.GrayLamp.getBlockMeta()));
        GTNLItemList.GrayLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.GrayLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.LightGrayLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.LightGrayLamp.getBlockMeta()));
        GTNLItemList.LightGrayLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.LightGrayLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));
        GTNLItemList.WhiteLamp.set(ItemBlockGlow.initMetaBlockGlow(GTNLCasings.WhiteLamp.getBlockMeta()));
        GTNLItemList.WhiteLampBorderless.set(
            ItemBlockGlow.initMetaBlockGlow(
                GTNLCasings.WhiteLampBorderless.getBlockMeta(),
                new String[] { StatCollector.translateToLocal("gtnl.block.lamp.borderless") }));

        GTNLItemList.GaiaGlass.set(ItemBlockGlass.initMetaBlockGlass(GTNLCasings.GaiaGlass.getBlockMeta()));
        GTNLItemList.TerraGlass.set(ItemBlockGlass.initMetaBlockGlass(GTNLCasings.TerraGlass.getBlockMeta()));
        GTNLItemList.FusionGlass.set(ItemBlockGlass.initMetaBlockGlass(GTNLCasings.FusionGlass.getBlockMeta()));
        GTNLItemList.ConcentratingSieveMesh
            .set(ItemBlockGlass.initMetaBlockGlass(GTNLCasings.ConcentratingSieveMesh.getBlockMeta()));

        GTNLItemList.BronzeBrickCasing.set(ItemBlockColumn.initMetaBlock(GTNLCasings.BronzeBrickCasing.getBlockMeta()));
        GTNLItemList.SteelBrickCasing.set(ItemBlockColumn.initMetaBlock(GTNLCasings.SteelBrickCasing.getBlockMeta()));
        GTNLItemList.CrushingWheels.set(ItemBlockColumn.initMetaBlock(GTNLCasings.CrushingWheels.getBlockMeta()));
        GTNLItemList.SolarBoilingCell.set(ItemBlockColumn.initMetaBlock(GTNLCasings.SolarBoilingCell.getBlockMeta()));
        GTNLItemList.BronzeMachineFrame
            .set(ItemBlockColumn.initMetaBlock(GTNLCasings.BronzeMachineFrame.getBlockMeta()));
        GTNLItemList.SteelMachineFrame.set(ItemBlockColumn.initMetaBlock(GTNLCasings.SteelMachineFrame.getBlockMeta()));

        GTNLItemList.TestCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.TestCasing.getBlockMeta(), metaCasing));
        GTNLItemList.SteamAssemblyCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.SteamAssemblyCasing.getBlockMeta(), metaCasing));
        GTNLItemList.HeatVent
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.HeatVent.getBlockMeta(), metaCasing));
        GTNLItemList.SlicingBlades
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.SlicingBlades.getBlockMeta(), metaCasing));
        GTNLItemList.NeutroniumPipeCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.NeutroniumPipeCasing.getBlockMeta(), metaCasing));
        GTNLItemList.NeutroniumGearbox
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.NeutroniumGearbox.getBlockMeta(), metaCasing));
        GTNLItemList.Laser_Cooling_Casing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.Laser_Cooling_Casing.getBlockMeta(), metaCasing));
        GTNLItemList.Antifreeze_Heatproof_Machine_Casing.set(
            MetaItemBlockCasing
                .initMetaBlockCasing(GTNLCasings.Antifreeze_Heatproof_Machine_Casing.getBlockMeta(), metaCasing));
        GTNLItemList.MolybdenumDisilicideCoil.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.MolybdenumDisilicideCoil.getBlockMeta(), metaCasing));
        GTNLItemList.EnergeticPhotovoltaicBlock.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.EnergeticPhotovoltaicBlock.getBlockMeta(), metaCasing));
        GTNLItemList.AdvancedPhotovoltaicBlock.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.AdvancedPhotovoltaicBlock.getBlockMeta(), metaCasing));
        GTNLItemList.VibrantPhotovoltaicBlock.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.VibrantPhotovoltaicBlock.getBlockMeta(), metaCasing));
        GTNLItemList.TungstensteelGearbox
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.TungstensteelGearbox.getBlockMeta(), metaCasing));
        GTNLItemList.DimensionallyStableCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.DimensionallyStableCasing.getBlockMeta(), metaCasing));
        GTNLItemList.PressureBalancedCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.PressureBalancedCasing.getBlockMeta(), metaCasing));
        GTNLItemList.ABSUltraSolidCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.ABSUltraSolidCasing.getBlockMeta(), metaCasing));
        GTNLItemList.GravitationalFocusingLensBlock.set(
            MetaItemBlockCasing
                .initMetaBlockCasing(GTNLCasings.GravitationalFocusingLensBlock.getBlockMeta(), metaCasing));
        GTNLItemList.GaiaStabilizedForceFieldCasing.set(
            MetaItemBlockCasing
                .initMetaBlockCasing(GTNLCasings.GaiaStabilizedForceFieldCasing.getBlockMeta(), metaCasing));
        GTNLItemList.HyperCore
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.HyperCore.getBlockMeta(), metaCasing));
        GTNLItemList.ChemicallyResistantCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.ChemicallyResistantCasing.getBlockMeta(), metaCasing));
        GTNLItemList.UltraPoweredCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.UltraPoweredCasing.getBlockMeta(), metaCasing));
        GTNLItemList.SteamgateRingBlock
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.SteamgateRingBlock.getBlockMeta(), metaCasing));
        GTNLItemList.SteamgateChevronBlock
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.SteamgateChevronBlock.getBlockMeta(), metaCasing));
        GTNLItemList.IronReinforcedWood
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.IronReinforcedWood.getBlockMeta(), metaCasing));
        GTNLItemList.BronzeReinforcedWood
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.BronzeReinforcedWood.getBlockMeta(), metaCasing));
        GTNLItemList.SteelReinforcedWood
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.SteelReinforcedWood.getBlockMeta(), metaCasing));
        GTNLItemList.BreelPipeCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.BreelPipeCasing.getBlockMeta(), metaCasing));
        GTNLItemList.StronzeWrappedCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.StronzeWrappedCasing.getBlockMeta(), metaCasing));
        GTNLItemList.HydraulicAssemblingCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.HydraulicAssemblingCasing.getBlockMeta(), metaCasing));
        GTNLItemList.HyperPressureBreelCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.HyperPressureBreelCasing.getBlockMeta(), metaCasing));
        GTNLItemList.BreelPlatedCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.BreelPlatedCasing.getBlockMeta(), metaCasing));
        GTNLItemList.SteamCompactPipeCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.SteamCompactPipeCasing.getBlockMeta(), metaCasing));
        GTNLItemList.VibrationSafeCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.VibrationSafeCasing.getBlockMeta(), metaCasing02));
        GTNLItemList.IndustrialSteamCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.IndustrialSteamCasing.getBlockMeta(), metaCasing02));
        GTNLItemList.AdvancedIndustrialSteamCasing.set(
            MetaItemBlockCasing
                .initMetaBlockCasing(GTNLCasings.AdvancedIndustrialSteamCasing.getBlockMeta(), metaCasing02));
        GTNLItemList.StainlessSteelGearBox.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.StainlessSteelGearBox.getBlockMeta(), metaCasing02));

        GTNLItemList.AssemblerMatrixFrame.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.AssemblerMatrixFrame.getBlockMeta(), metaCasing02));
        GTNLItemList.AssemblerMatrixWall
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.AssemblerMatrixWall.getBlockMeta(), metaCasing02));

        GTNLItemList.AssemblerMatrixPatternCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.AssemblerMatrixPatternCore.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector.translateToLocal("gtnl.block.assembler_matrix_pattern_core.tooltip.0") }));
        GTNLItemList.AssemblerMatrixCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.AssemblerMatrixCrafterCore.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector.translateToLocal("gtnl.block.assembler_matrix_crafter_core.tooltip.0") }));
        GTNLItemList.AssemblerMatrixSingularityCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.AssemblerMatrixSingularityCrafterCore.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector
                    .translateToLocal("gtnl.block.assembler_matrix_singularity_crafter_core.tooltip.0") }));
        GTNLItemList.AssemblerMatrixSpeedCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.AssemblerMatrixSpeedCore.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector.translateToLocal("gtnl.block.assembler_matrix_speed_core.tooltip.0") }));
        GTNLItemList.QuantumComputerCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.QuantumComputerCasing.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector.translateToLocalFormatted(
                    "gtnl.block.quantum_computer_casing.tooltip.0",
                    MainConfig.machine.quantum_computer.maxMultiblockSize,
                    MainConfig.machine.quantum_computer.maxMultiblockSize,
                    MainConfig.machine.quantum_computer.maxMultiblockSize) }));
        GTNLItemList.QuantumComputerUnit
            .set(MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.QuantumComputerUnit.getBlockMeta(), metaCasing02));
        GTNLItemList.QuantumComputerCraftingStorage128M.set(
            MetaItemBlockCasing
                .initMetaBlockCasing(GTNLCasings.QuantumComputerCraftingStorage128M.getBlockMeta(), metaCasing02));
        GTNLItemList.QuantumComputerCraftingStorage256M.set(
            MetaItemBlockCasing
                .initMetaBlockCasing(GTNLCasings.QuantumComputerCraftingStorage256M.getBlockMeta(), metaCasing02));
        GTNLItemList.QuantumComputerDataEntangler.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.QuantumComputerDataEntangler.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector.translateToLocalFormatted(
                    "gtnl.block.quantum_computer_data_entangler.tooltip.0",
                    MainConfig.machine.quantum_computer.maxDataEntangler) }));
        GTNLItemList.QuantumComputerAccelerator.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.QuantumComputerAccelerator.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector.translateToLocal("gtnl.block.quantum_computer_accelerator.tooltip.0") }));
        GTNLItemList.QuantumComputerMultiThreader.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.QuantumComputerMultiThreader.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector.translateToLocalFormatted(
                    "gtnl.block.quantum_computer_multi_threader.tooltip.0",
                    MainConfig.machine.quantum_computer.maxMultiThreader) }));
        GTNLItemList.QuantumComputerCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.QuantumComputerCore.getBlockMeta(),
                metaCasing02,
                new String[] { StatCollector.translateToLocal("gtnl.block.quantum_computer_core.tooltip.0") }));
        GTNLItemList.AssemblerMatrixDebugCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.AssemblerMatrixDebugCrafterCore.getBlockMeta(),
                metaCasing02,
                new String[] {
                    StatCollector.translateToLocal("gtnl.block.assembler_matrix_debug_crafter_core.tooltip.0") }));
        GTNLItemList.QuantumComputerSingularityCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                GTNLCasings.QuantumComputerSingularityCore.getBlockMeta(),
                metaCasing02,
                new String[] {
                    StatCollector.translateToLocal("gtnl.block.quantum_computer_singularity_core.tooltip.0") }));
        GTNLItemList.CompressedFurnaceCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(GTNLCasings.CompressedFurnaceCasing.getBlockMeta(), metaCasing02));
    }

    public static void registry() {
        registryBlocks();
        registryBlockContainers();
    }

    public static void registerTreeBrickuoia() {
        saplingBrickuoia = new BlockSaplingBrickuoia();
        GTNLItemList.SaplingBrickuoia.set(new ItemStack(saplingBrickuoia, 1));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.SaplingBrickuoia.get(1),
            () -> StatCollector.translateToLocal("gtnl.block.giant_brickuoia_sapling.tooltip.0"));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.SaplingBrickuoia.get(1),
            () -> StatCollector.translateToLocal("gtnl.block.giant_brickuoia_sapling.tooltip.1"));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SaplingBrickuoia.get(1), () -> "");
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.SaplingBrickuoia.get(1),
            () -> StatCollector.translateToLocal("gtnl.block.giant_brickuoia_sapling.tooltip.2"));
        OreDictionary.registerOre("treeSapling", new ItemStack(saplingBrickuoia, 1, GTRecipeBuilder.WILDCARD));
    }

    public static class Card implements IContentLoader {

        @Override
        public String name() {
            return "blocks";
        }

        @Override
        public void preInit() {
            registry();
            registerTreeBrickuoia();
        }

        @Override
        public void postInit() {
            TileEntityMigrationHandler.register();
        }
    }
}
