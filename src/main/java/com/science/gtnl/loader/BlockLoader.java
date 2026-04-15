package com.science.gtnl.loader;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

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
import com.science.gtnl.common.block.blocks.BlockNanoPhagocytosisPlantRender;
import com.science.gtnl.common.block.blocks.BlockPlayerDoll;
import com.science.gtnl.common.block.blocks.BlockPlayerLeash;
import com.science.gtnl.common.block.blocks.BlockSaplingBrickuoia;
import com.science.gtnl.common.block.blocks.BlockSearedLadder;
import com.science.gtnl.common.block.blocks.BlockShimmerFluid;
import com.science.gtnl.common.block.blocks.BlockSuperInterface;
import com.science.gtnl.common.block.blocks.BlockWaterCandle;
import com.science.gtnl.common.block.blocks.BlocksCompressedStargate;
import com.science.gtnl.common.block.blocks.tile.TileEntityEnderElevator;
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
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.text.AnimatedText;
import com.science.gtnl.utils.text.AnimatedTooltipHandler;

import bartworks.common.loaders.ItemRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.util.GTRecipeBuilder;

public class BlockLoader {

    public static BlockSaplingBrickuoia saplingBrickuoia;
    public static BlockCardboardBox cardboardBox;
    public static BlockArtificialStarRender artificialStarRender;
    public static BlockLaserBeacon laserBeacon;
    public static BlockPlayerDoll playerDoll;
    public static BlockWaterCandle waterCandle;
    public static BlockSearedLadder searedLadder;
    public static BlockPlayerLeash playerLeash;
    public static BlockDirePatternEncoder direPatternEncoder;
    public static BlockMEChisel meChisel;
    public static BlockSuperInterface superInterface;
    public static BlockBeamFormer beamFormer;
    public static BlockNanoPhagocytosisPlantRender nanoPhagocytosisPlantRender;
    public static BlockEternalGregTechWorkshopRender eternalGregTechWorkshopRender;
    public static BlockDimensionRespawnAnchor dimensionRespawnAnchor;
    public static BlockEssentiaHatch essentiaHatch;
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

    public static MetaBlockBase metaBlock = new MetaBlockBase("MetaBlock");
    public static MetaBlockGlow metaBlockGlow = new MetaBlockGlow("MetaBlockGlow");
    public static MetaBlockGlass metaBlockGlass = new MetaBlockGlass("MetaBlockGlass");
    public static MetaBlockColumn metaBlockColumn = new MetaBlockColumn("MetaBlockColumn");
    public static MetaCasing metaCasing = new MetaCasing("MetaCasing", (byte) 0);
    public static MetaCasing metaCasing02 = new MetaCasing("MetaCasing02", (byte) 32);

    public static void registryBlocks() {
        playerLeash = new BlockPlayerLeash();
        searedLadder = new BlockSearedLadder();
        direPatternEncoder = new BlockDirePatternEncoder();
        meChisel = new BlockMEChisel();
        superInterface = new BlockSuperInterface();
        beamFormer = new BlockBeamFormer();
        cardboardBox = new BlockCardboardBox();
        eternalGregTechWorkshopRender = new BlockEternalGregTechWorkshopRender();
        nanoPhagocytosisPlantRender = new BlockNanoPhagocytosisPlantRender();
        artificialStarRender = new BlockArtificialStarRender();
        playerDoll = new BlockPlayerDoll();
        laserBeacon = new BlockLaserBeacon();
        waterCandle = new BlockWaterCandle();
        dimensionRespawnAnchor = new BlockDimensionRespawnAnchor();
        essentiaHatch = new BlockEssentiaHatch();

        enderElevatorBlock = new BlockEnderElevator(0);
        enderElevatorSlab = new BlockEnderElevator(1);
        enderElevatorCarpet = new BlockEnderElevator(2);
        GTNLItemList.EnderElevatorBlock.set(new ItemStack(enderElevatorBlock));
        GTNLItemList.EnderElevatorSlab.set(new ItemStack(enderElevatorSlab));
        GTNLItemList.EnderElevatorCarpet.set(new ItemStack(enderElevatorCarpet));
        GameRegistry.registerTileEntity(TileEntityEnderElevator.class, "EnderElevatorTileEntity");

        GameRegistry.registerBlock(metaBlock, ItemBlockBase.class, metaBlock.getUnlocalizedName());
        GameRegistry.registerBlock(metaBlockGlow, ItemBlockGlow.class, metaBlockGlow.getUnlocalizedName());
        GameRegistry.registerBlock(metaBlockGlass, ItemBlockGlass.class, metaBlockGlass.getUnlocalizedName());
        GameRegistry.registerBlock(metaBlockColumn, ItemBlockColumn.class, metaBlockColumn.getUnlocalizedName());
        GameRegistry.registerBlock(metaCasing, MetaItemBlockCasing.class, metaCasing.getUnlocalizedName());
        GameRegistry.registerBlock(metaCasing02, MetaItemBlockCasing.class, metaCasing02.getUnlocalizedName());

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

        honeyFluid = new Fluid("honey").setViscosity(6000)
            .setDensity(1500);
        FluidRegistry.registerFluid(honeyFluid);
        honeyFluidBlock = new BlockHoneyFluid(honeyFluid);
        GTNLItemList.HoneyFluidBlock.set(new ItemStack(honeyFluidBlock));

        shimmerFluid = new Fluid("shimmer").setViscosity(800);
        FluidRegistry.registerFluid(shimmerFluid);
        shimmerFluidBlock = new BlockShimmerFluid(shimmerFluid);
        GTNLItemList.ShimmerFluidBlock.set(new ItemStack(shimmerFluidBlock));

        GTNLItemList.ShirabonReinforcedBoronSilicateGlass.set(new ItemStack(ItemRegistry.bw_realglas2, 1, 1));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.ShirabonReinforcedBoronSilicateGlass.get(1),
            AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
        GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.set(new ItemStack(ItemRegistry.bw_realglas2, 1, 2));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.get(1),
            AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
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
            ItemBlockBase.initMetaBlock(3, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.BlackLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                4,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.PinkLampOff.set(
            ItemBlockBase.initMetaBlock(5, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.PinkLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                6,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.RedLampOff.set(
            ItemBlockBase.initMetaBlock(7, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.RedLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                8,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.OrangeLampOff.set(
            ItemBlockBase.initMetaBlock(9, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.OrangeLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                10,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.YellowLampOff.set(
            ItemBlockBase.initMetaBlock(11, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.YellowLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                12,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.GreenLampOff.set(
            ItemBlockBase.initMetaBlock(13, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.GreenLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                14,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.LimeLampOff.set(
            ItemBlockBase.initMetaBlock(15, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.LimeLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                16,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.BlueLampOff.set(
            ItemBlockBase.initMetaBlock(17, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.BlueLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                18,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.LightBlueLampOff.set(
            ItemBlockBase.initMetaBlock(19, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.LightBlueLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                20,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.CyanLampOff.set(
            ItemBlockBase.initMetaBlock(21, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.CyanLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                22,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.BrownLampOff.set(
            ItemBlockBase.initMetaBlock(23, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.BrownLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                24,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.MagentaLampOff.set(
            ItemBlockBase.initMetaBlock(25, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.MagentaLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                26,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.PurpleLampOff.set(
            ItemBlockBase.initMetaBlock(27, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.PurpleLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                28,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.GrayLampOff.set(
            ItemBlockBase.initMetaBlock(29, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.GrayLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                30,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.LightGrayLampOff.set(
            ItemBlockBase.initMetaBlock(31, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.LightGrayLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                32,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.WhiteLampOff.set(
            ItemBlockBase.initMetaBlock(33, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow") }));
        GTNLItemList.WhiteLampOffBorderless.set(
            ItemBlockBase.initMetaBlock(
                34,
                new String[] { StatCollector.translateToLocal("Tooltip_Lamp_NoGlow"),
                    StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
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

        GTNLItemList.FortifyGlowstone.set(ItemBlockGlow.initMetaBlockGlow(0));
        GTNLItemList.BlackLamp.set(ItemBlockGlow.initMetaBlockGlow(1));
        GTNLItemList.BlackLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(2, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.PinkLamp.set(ItemBlockGlow.initMetaBlockGlow(3));
        GTNLItemList.PinkLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(4, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.RedLamp.set(ItemBlockGlow.initMetaBlockGlow(5));
        GTNLItemList.RedLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(6, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.OrangeLamp.set(ItemBlockGlow.initMetaBlockGlow(7));
        GTNLItemList.OrangeLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(8, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.YellowLamp.set(ItemBlockGlow.initMetaBlockGlow(9));
        GTNLItemList.YellowLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(10, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.GreenLamp.set(ItemBlockGlow.initMetaBlockGlow(11));
        GTNLItemList.GreenLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(12, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.LimeLamp.set(ItemBlockGlow.initMetaBlockGlow(13));
        GTNLItemList.LimeLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(14, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.BlueLamp.set(ItemBlockGlow.initMetaBlockGlow(15));
        GTNLItemList.BlueLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(16, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.LightBlueLamp.set(ItemBlockGlow.initMetaBlockGlow(17));
        GTNLItemList.LightBlueLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(18, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.CyanLamp.set(ItemBlockGlow.initMetaBlockGlow(19));
        GTNLItemList.CyanLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(20, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.BrownLamp.set(ItemBlockGlow.initMetaBlockGlow(21));
        GTNLItemList.BrownLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(22, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.MagentaLamp.set(ItemBlockGlow.initMetaBlockGlow(23));
        GTNLItemList.MagentaLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(24, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.PurpleLamp.set(ItemBlockGlow.initMetaBlockGlow(25));
        GTNLItemList.PurpleLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(26, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.GrayLamp.set(ItemBlockGlow.initMetaBlockGlow(27));
        GTNLItemList.GrayLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(28, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.LightGrayLamp.set(ItemBlockGlow.initMetaBlockGlow(29));
        GTNLItemList.LightGrayLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(30, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));
        GTNLItemList.WhiteLamp.set(ItemBlockGlow.initMetaBlockGlow(31));
        GTNLItemList.WhiteLampBorderless.set(
            ItemBlockGlow
                .initMetaBlockGlow(32, new String[] { StatCollector.translateToLocal("Tooltip_Lamp_Borderless") }));

        GTNLItemList.GaiaGlass.set(ItemBlockGlass.initMetaBlockGlass(0));
        GTNLItemList.TerraGlass.set(ItemBlockGlass.initMetaBlockGlass(1));
        GTNLItemList.FusionGlass.set(ItemBlockGlass.initMetaBlockGlass(2));
        GTNLItemList.ConcentratingSieveMesh.set(ItemBlockGlass.initMetaBlockGlass(3));

        GTNLItemList.BronzeBrickCasing.set(ItemBlockColumn.initMetaBlock(0));
        GTNLItemList.SteelBrickCasing.set(ItemBlockColumn.initMetaBlock(1));
        GTNLItemList.CrushingWheels.set(ItemBlockColumn.initMetaBlock(2));
        GTNLItemList.SolarBoilingCell.set(ItemBlockColumn.initMetaBlock(3));
        GTNLItemList.BronzeMachineFrame.set(ItemBlockColumn.initMetaBlock(4));
        GTNLItemList.SteelMachineFrame.set(ItemBlockColumn.initMetaBlock(5));

        GTNLItemList.TestCasing.set(MetaItemBlockCasing.initMetaBlockCasing(0, metaCasing));
        GTNLItemList.SteamAssemblyCasing.set(MetaItemBlockCasing.initMetaBlockCasing(1, metaCasing));
        GTNLItemList.HeatVent.set(MetaItemBlockCasing.initMetaBlockCasing(2, metaCasing));
        GTNLItemList.SlicingBlades.set(MetaItemBlockCasing.initMetaBlockCasing(3, metaCasing));
        GTNLItemList.NeutroniumPipeCasing.set(MetaItemBlockCasing.initMetaBlockCasing(4, metaCasing));
        GTNLItemList.NeutroniumGearbox.set(MetaItemBlockCasing.initMetaBlockCasing(5, metaCasing));
        GTNLItemList.Laser_Cooling_Casing.set(MetaItemBlockCasing.initMetaBlockCasing(6, metaCasing));
        GTNLItemList.Antifreeze_Heatproof_Machine_Casing.set(MetaItemBlockCasing.initMetaBlockCasing(7, metaCasing));
        GTNLItemList.MolybdenumDisilicideCoil.set(MetaItemBlockCasing.initMetaBlockCasing(8, metaCasing));
        GTNLItemList.EnergeticPhotovoltaicBlock.set(MetaItemBlockCasing.initMetaBlockCasing(9, metaCasing));
        GTNLItemList.AdvancedPhotovoltaicBlock.set(MetaItemBlockCasing.initMetaBlockCasing(10, metaCasing));
        GTNLItemList.VibrantPhotovoltaicBlock.set(MetaItemBlockCasing.initMetaBlockCasing(11, metaCasing));
        GTNLItemList.TungstensteelGearbox.set(MetaItemBlockCasing.initMetaBlockCasing(12, metaCasing));
        GTNLItemList.DimensionallyStableCasing.set(MetaItemBlockCasing.initMetaBlockCasing(13, metaCasing));
        GTNLItemList.PressureBalancedCasing.set(MetaItemBlockCasing.initMetaBlockCasing(14, metaCasing));
        GTNLItemList.ABSUltraSolidCasing.set(MetaItemBlockCasing.initMetaBlockCasing(15, metaCasing));
        GTNLItemList.GravitationalFocusingLensBlock.set(MetaItemBlockCasing.initMetaBlockCasing(16, metaCasing));
        GTNLItemList.GaiaStabilizedForceFieldCasing.set(MetaItemBlockCasing.initMetaBlockCasing(17, metaCasing));
        GTNLItemList.HyperCore.set(MetaItemBlockCasing.initMetaBlockCasing(18, metaCasing));
        GTNLItemList.ChemicallyResistantCasing.set(MetaItemBlockCasing.initMetaBlockCasing(19, metaCasing));
        GTNLItemList.UltraPoweredCasing.set(MetaItemBlockCasing.initMetaBlockCasing(20, metaCasing));
        GTNLItemList.SteamgateRingBlock.set(MetaItemBlockCasing.initMetaBlockCasing(21, metaCasing));
        GTNLItemList.SteamgateChevronBlock.set(MetaItemBlockCasing.initMetaBlockCasing(22, metaCasing));
        GTNLItemList.IronReinforcedWood.set(MetaItemBlockCasing.initMetaBlockCasing(23, metaCasing));
        GTNLItemList.BronzeReinforcedWood.set(MetaItemBlockCasing.initMetaBlockCasing(24, metaCasing));
        GTNLItemList.SteelReinforcedWood.set(MetaItemBlockCasing.initMetaBlockCasing(25, metaCasing));
        GTNLItemList.BreelPipeCasing.set(MetaItemBlockCasing.initMetaBlockCasing(26, metaCasing));
        GTNLItemList.StronzeWrappedCasing.set(MetaItemBlockCasing.initMetaBlockCasing(27, metaCasing));
        GTNLItemList.HydraulicAssemblingCasing.set(MetaItemBlockCasing.initMetaBlockCasing(28, metaCasing));
        GTNLItemList.HyperPressureBreelCasing.set(MetaItemBlockCasing.initMetaBlockCasing(29, metaCasing));
        GTNLItemList.BreelPlatedCasing.set(MetaItemBlockCasing.initMetaBlockCasing(30, metaCasing));
        GTNLItemList.SteamCompactPipeCasing.set(MetaItemBlockCasing.initMetaBlockCasing(31, metaCasing));
        GTNLItemList.VibrationSafeCasing.set(MetaItemBlockCasing.initMetaBlockCasing(0, metaCasing02));
        GTNLItemList.IndustrialSteamCasing.set(MetaItemBlockCasing.initMetaBlockCasing(1, metaCasing02));
        GTNLItemList.AdvancedIndustrialSteamCasing.set(MetaItemBlockCasing.initMetaBlockCasing(2, metaCasing02));
        GTNLItemList.StainlessSteelGearBox.set(MetaItemBlockCasing.initMetaBlockCasing(3, metaCasing02));

        GTNLItemList.AssemblerMatrixFrame.set(MetaItemBlockCasing.initMetaBlockCasing(4, metaCasing02));
        GTNLItemList.AssemblerMatrixWall.set(MetaItemBlockCasing.initMetaBlockCasing(5, metaCasing02));

        GTNLItemList.AssemblerMatrixPatternCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                6,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixPatternCore_00") }));
        GTNLItemList.AssemblerMatrixCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                7,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixCrafterCore_00") }));
        GTNLItemList.AssemblerMatrixSingularityCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                8,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixSingularityCrafterCore_00") }));
        GTNLItemList.AssemblerMatrixSpeedCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                9,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixSpeedCore_00") }));
        GTNLItemList.QuantumComputerCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                10,
                metaCasing02,
                new String[] { StatCollector.translateToLocalFormatted(
                    "Tooltip_QuantumComputerCasing_00",
                    MainConfig.machine.quantum_computer.maxMultiblockSize,
                    MainConfig.machine.quantum_computer.maxMultiblockSize,
                    MainConfig.machine.quantum_computer.maxMultiblockSize) }));
        GTNLItemList.QuantumComputerUnit.set(MetaItemBlockCasing.initMetaBlockCasing(11, metaCasing02));
        GTNLItemList.QuantumComputerCraftingStorage128M.set(MetaItemBlockCasing.initMetaBlockCasing(12, metaCasing02));
        GTNLItemList.QuantumComputerCraftingStorage256M.set(MetaItemBlockCasing.initMetaBlockCasing(13, metaCasing02));
        GTNLItemList.QuantumComputerDataEntangler.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                14,
                metaCasing02,
                new String[] { StatCollector.translateToLocalFormatted(
                    "Tooltip_QuantumComputerDataEntangler_00",
                    MainConfig.machine.quantum_computer.maxDataEntangler) }));
        GTNLItemList.QuantumComputerAccelerator.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                15,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_QuantumComputerAccelerator_00") }));
        GTNLItemList.QuantumComputerMultiThreader.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                16,
                metaCasing02,
                new String[] { StatCollector.translateToLocalFormatted(
                    "Tooltip_QuantumComputerMultiThreader_00",
                    MainConfig.machine.quantum_computer.maxMultiThreader) }));
        GTNLItemList.QuantumComputerCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                17,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_QuantumComputerCore_00") }));
        GTNLItemList.AssemblerMatrixDebugCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                18,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixDebugCrafterCore_00") }));
        GTNLItemList.QuantumComputerSingularityCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                19,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_QuantumComputerSingularityCore_00") }));
        GTNLItemList.CompressedFurnaceCasing.set(MetaItemBlockCasing.initMetaBlockCasing(20, metaCasing02));
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
            () -> StatCollector.translateToLocal("Tooltip_GiantBrickuoiaSapling_00"));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.SaplingBrickuoia.get(1),
            () -> StatCollector.translateToLocal("Tooltip_GiantBrickuoiaSapling_01"));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SaplingBrickuoia.get(1), () -> "");
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.SaplingBrickuoia.get(1),
            () -> StatCollector.translateToLocal("Tooltip_GiantBrickuoiaSapling_02"));
        OreDictionary.registerOre("treeSapling", new ItemStack(saplingBrickuoia, 1, GTRecipeBuilder.WILDCARD));
    }
}
