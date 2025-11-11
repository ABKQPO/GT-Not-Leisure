package com.science.gtnl.loader;

import static com.science.gtnl.utils.text.AnimatedTooltipHandler.*;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.BlockArtificialStarRender;
import com.science.gtnl.common.block.blocks.BlockCardboardBox;
import com.science.gtnl.common.block.blocks.BlockEternalGregTechWorkshopRender;
import com.science.gtnl.common.block.blocks.BlockHoneyFluid;
import com.science.gtnl.common.block.blocks.BlockLaserBeacon;
import com.science.gtnl.common.block.blocks.BlockNanoPhagocytosisPlantRender;
import com.science.gtnl.common.block.blocks.BlockPlayerDoll;
import com.science.gtnl.common.block.blocks.BlockPlayerLeash;
import com.science.gtnl.common.block.blocks.BlockShimmerFluid;
import com.science.gtnl.common.block.blocks.BlockWaterCandle;
import com.science.gtnl.common.block.blocks.BlocksCompressedStargate;
import com.science.gtnl.common.block.blocks.tile.TileEntityArtificialStar;
import com.science.gtnl.common.block.blocks.tile.TileEntityCardboardBox;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;
import com.science.gtnl.common.block.blocks.tile.TileEntityLaserBeacon;
import com.science.gtnl.common.block.blocks.tile.TileEntityNanoPhagocytosisPlant;
import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;
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
import com.science.gtnl.common.item.items.SaplingBrickuoia;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.text.AnimatedText;
import com.science.gtnl.utils.text.AnimatedTooltipHandler;

import bartworks.common.loaders.ItemRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.util.GTRecipeBuilder;

public class BlockLoader {

    public static Block SaplingBrickuoia;
    public static Block cardboardBox;
    public static Block blockArtificialStarRender;
    public static Block laserBeacon;
    public static Block playerDoll;
    public static Block waterCandle;
    public static Block blockPlayerLeash;
    public static Block blockNanoPhagocytosisPlantRender;
    public static Block blockEternalGregTechWorkshopRender;

    public static BlockFluidBase honeyFluidBlock;
    public static Fluid honeyFluid;
    public static BlockFluidBase shimmerFluidBlock;
    public static Fluid shimmerFluid;

    public static Block compressedStargateTier0 = new BlocksCompressedStargate(0);
    public static Block compressedStargateTier1 = new BlocksCompressedStargate(1);
    public static Block compressedStargateTier2 = new BlocksCompressedStargate(2);
    public static Block compressedStargateTier3 = new BlocksCompressedStargate(3);
    public static Block compressedStargateTier4 = new BlocksCompressedStargate(4);
    public static Block compressedStargateTier5 = new BlocksCompressedStargate(5);
    public static Block compressedStargateTier6 = new BlocksCompressedStargate(6);
    public static Block compressedStargateTier7 = new BlocksCompressedStargate(7);
    public static Block compressedStargateTier8 = new BlocksCompressedStargate(8);
    public static Block compressedStargateTier9 = new BlocksCompressedStargate(9);

    public static Block metaBlock = new MetaBlockBase("MetaBlock");
    public static Block metaBlockGlow = new MetaBlockGlow("MetaBlockGlow");
    public static Block metaBlockGlass = new MetaBlockGlass("MetaBlockGlass");
    public static Block metaBlockColumn = new MetaBlockColumn("MetaBlockColumn");
    public static MetaCasing metaCasing = new MetaCasing("MetaCasing", (byte) 0);
    public static MetaCasing metaCasing02 = new MetaCasing("MetaCasing02", (byte) 32);

    public static void registryBlocks() {

        blockPlayerLeash = new BlockPlayerLeash();

        cardboardBox = new BlockCardboardBox();
        GameRegistry.registerTileEntity(TileEntityCardboardBox.class, "CardboardBoxTileEntity");

        blockEternalGregTechWorkshopRender = new BlockEternalGregTechWorkshopRender();
        GameRegistry
            .registerTileEntity(TileEntityEternalGregTechWorkshop.class, "EternalGregTechWorkshopRenderTileEntity");

        blockNanoPhagocytosisPlantRender = new BlockNanoPhagocytosisPlantRender();
        GameRegistry.registerTileEntity(TileEntityNanoPhagocytosisPlant.class, "NanoPhagocytosisPlantRenderTileEntity");

        blockArtificialStarRender = new BlockArtificialStarRender();
        GameRegistry.registerTileEntity(TileEntityArtificialStar.class, "ArtificialStarRenderTileEntity");

        playerDoll = new BlockPlayerDoll();
        GameRegistry.registerTileEntity(TileEntityPlayerDoll.class, "PlayerDollTileEntity");

        laserBeacon = new BlockLaserBeacon();
        GameRegistry.registerTileEntity(TileEntityLaserBeacon.class, "LaserBeaconTileEntity");

        waterCandle = new BlockWaterCandle();
        GameRegistry.registerTileEntity(TileEntityWaterCandle.class, "WaterCandleTileEntity");

        GameRegistry
            .registerBlock(BlockLoader.metaBlock, ItemBlockBase.class, BlockLoader.metaBlock.getUnlocalizedName());
        GameRegistry.registerBlock(
            BlockLoader.metaBlockGlow,
            ItemBlockGlow.class,
            BlockLoader.metaBlockGlow.getUnlocalizedName());
        GameRegistry.registerBlock(
            BlockLoader.metaBlockGlass,
            ItemBlockGlass.class,
            BlockLoader.metaBlockGlass.getUnlocalizedName());
        GameRegistry.registerBlock(
            BlockLoader.metaBlockColumn,
            ItemBlockColumn.class,
            BlockLoader.metaBlockColumn.getUnlocalizedName());
        GameRegistry.registerBlock(
            BlockLoader.metaCasing,
            MetaItemBlockCasing.class,
            BlockLoader.metaCasing.getUnlocalizedName());
        GameRegistry.registerBlock(
            BlockLoader.metaCasing02,
            MetaItemBlockCasing.class,
            BlockLoader.metaCasing02.getUnlocalizedName());

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier0, "CompressedStargateTier0");
        GTNLItemList.CompressedStargateTier0.set(new ItemStack(BlockLoader.compressedStargateTier0));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier1, "CompressedStargateTier1");
        GTNLItemList.CompressedStargateTier1.set(new ItemStack(BlockLoader.compressedStargateTier1));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier2, "CompressedStargateTier2");
        GTNLItemList.CompressedStargateTier2.set(new ItemStack(BlockLoader.compressedStargateTier2));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier3, "CompressedStargateTier3");
        GTNLItemList.CompressedStargateTier3.set(new ItemStack(BlockLoader.compressedStargateTier3));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier4, "CompressedStargateTier4");
        GTNLItemList.CompressedStargateTier4.set(new ItemStack(BlockLoader.compressedStargateTier4));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier5, "CompressedStargateTier5");
        GTNLItemList.CompressedStargateTier5.set(new ItemStack(BlockLoader.compressedStargateTier5));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier6, "CompressedStargateTier6");
        GTNLItemList.CompressedStargateTier6.set(new ItemStack(BlockLoader.compressedStargateTier6));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier7, "CompressedStargateTier7");
        GTNLItemList.CompressedStargateTier7.set(new ItemStack(BlockLoader.compressedStargateTier7));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier8, "CompressedStargateTier8");
        GTNLItemList.CompressedStargateTier8.set(new ItemStack(BlockLoader.compressedStargateTier8));

        GameRegistry.registerBlock(BlockLoader.compressedStargateTier9, "CompressedStargateTier9");
        GTNLItemList.CompressedStargateTier9.set(new ItemStack(BlockLoader.compressedStargateTier9));

        honeyFluid = new Fluid("honey").setViscosity(6000)
            .setDensity(1500);
        FluidRegistry.registerFluid(honeyFluid);
        honeyFluidBlock = new BlockHoneyFluid(honeyFluid);
        GameRegistry.registerBlock(honeyFluidBlock, "honey");
        GTNLItemList.HoneyFluidBlock.set(new ItemStack(BlockLoader.honeyFluidBlock));

        shimmerFluid = new Fluid("shimmer").setViscosity(800);
        FluidRegistry.registerFluid(shimmerFluid);
        shimmerFluidBlock = new BlockShimmerFluid(shimmerFluid);
        GameRegistry.registerBlock(shimmerFluidBlock, "shimmer");
        GTNLItemList.ShimmerFluidBlock.set(new ItemStack(BlockLoader.shimmerFluidBlock));

        GTNLItemList.ShirabonReinforcedBoronSilicateGlass.set(new ItemStack(ItemRegistry.bw_realglas2, 1, 1));
        addItemTooltip(
            GTNLItemList.ShirabonReinforcedBoronSilicateGlass.get(1),
            AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
        GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.set(new ItemStack(ItemRegistry.bw_realglas2, 1, 2));
        addItemTooltip(
            GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.get(1),
            AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
    }

    public static void registryBlockContainers() {

        GTNLItemList.TestMetaBlock01_0.set(ItemBlockBase.initMetaBlock(0));
        GTNLItemList.NewHorizonsCoil.set(
            ItemBlockBase
                .initMetaBlock(1, new String[] { RESET + StatCollector.translateToLocal("gt.coilheattooltip") }));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "179,769,313,486,231,590,772,930,519,078,902,473,361,797,697,894,230,657,273,430,081,",
                1,
                80,
                RED,
                GOLD,
                YELLOW,
                GREEN,
                AQUA,
                BLUE,
                LIGHT_PURPLE));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "157,732,675,805,500,963,132,708,477,322,407,536,021,120,113,879,871,393,357,658,789,",
                1,
                80,
                GOLD,
                YELLOW,
                GREEN,
                AQUA,
                BLUE,
                LIGHT_PURPLE,
                RED));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "768,814,416,622,492,847,430,639,474,124,377,767,893,424,865,485,276,302,219,601,246,",
                1,
                80,
                YELLOW,
                GREEN,
                AQUA,
                BLUE,
                LIGHT_PURPLE,
                RED,
                GOLD));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "094,119,453,082,952,085,005,768,838,150,682,342,462,881,473,913,110,540,827,237,163,",
                1,
                80,
                GREEN,
                AQUA,
                BLUE,
                LIGHT_PURPLE,
                RED,
                GOLD,
                YELLOW));
        AnimatedTooltipHandler.addItemTooltip(
            GTNLItemList.NewHorizonsCoil.get(1),
            AnimatedTooltipHandler.animatedText(
                "350,510,684,586,298,239,947,245,938,479,716,304,835,356,329,624,224,137,216"
                    + StatCollector.translateToLocal("gt.coilunittooltip"),
                1,
                80,
                AQUA,
                BLUE,
                LIGHT_PURPLE,
                RED,
                GOLD,
                YELLOW,
                GREEN));

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

        GTNLItemList.TestCasing.set(MetaItemBlockCasing.initMetaBlockCasing(0, BlockLoader.metaCasing));
        GTNLItemList.SteamAssemblyCasing.set(MetaItemBlockCasing.initMetaBlockCasing(1, BlockLoader.metaCasing));
        GTNLItemList.HeatVent.set(MetaItemBlockCasing.initMetaBlockCasing(2, BlockLoader.metaCasing));
        GTNLItemList.SlicingBlades.set(MetaItemBlockCasing.initMetaBlockCasing(3, BlockLoader.metaCasing));
        GTNLItemList.NeutroniumPipeCasing.set(MetaItemBlockCasing.initMetaBlockCasing(4, BlockLoader.metaCasing));
        GTNLItemList.NeutroniumGearbox.set(MetaItemBlockCasing.initMetaBlockCasing(5, BlockLoader.metaCasing));
        GTNLItemList.Laser_Cooling_Casing.set(MetaItemBlockCasing.initMetaBlockCasing(6, BlockLoader.metaCasing));
        GTNLItemList.Antifreeze_Heatproof_Machine_Casing
            .set(MetaItemBlockCasing.initMetaBlockCasing(7, BlockLoader.metaCasing));
        GTNLItemList.MolybdenumDisilicideCoil.set(MetaItemBlockCasing.initMetaBlockCasing(8, BlockLoader.metaCasing));
        GTNLItemList.EnergeticPhotovoltaicBlock.set(MetaItemBlockCasing.initMetaBlockCasing(9, BlockLoader.metaCasing));
        GTNLItemList.AdvancedPhotovoltaicBlock.set(MetaItemBlockCasing.initMetaBlockCasing(10, BlockLoader.metaCasing));
        GTNLItemList.VibrantPhotovoltaicBlock.set(MetaItemBlockCasing.initMetaBlockCasing(11, BlockLoader.metaCasing));
        GTNLItemList.TungstensteelGearbox.set(MetaItemBlockCasing.initMetaBlockCasing(12, BlockLoader.metaCasing));
        GTNLItemList.DimensionallyStableCasing.set(MetaItemBlockCasing.initMetaBlockCasing(13, BlockLoader.metaCasing));
        GTNLItemList.PressureBalancedCasing.set(MetaItemBlockCasing.initMetaBlockCasing(14, BlockLoader.metaCasing));
        GTNLItemList.ABSUltraSolidCasing.set(MetaItemBlockCasing.initMetaBlockCasing(15, BlockLoader.metaCasing));
        GTNLItemList.GravitationalFocusingLensBlock
            .set(MetaItemBlockCasing.initMetaBlockCasing(16, BlockLoader.metaCasing));
        GTNLItemList.GaiaStabilizedForceFieldCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(17, BlockLoader.metaCasing));
        GTNLItemList.HyperCore.set(MetaItemBlockCasing.initMetaBlockCasing(18, BlockLoader.metaCasing));
        GTNLItemList.ChemicallyResistantCasing.set(MetaItemBlockCasing.initMetaBlockCasing(19, BlockLoader.metaCasing));
        GTNLItemList.UltraPoweredCasing.set(MetaItemBlockCasing.initMetaBlockCasing(20, BlockLoader.metaCasing));
        GTNLItemList.SteamgateRingBlock.set(MetaItemBlockCasing.initMetaBlockCasing(21, BlockLoader.metaCasing));
        GTNLItemList.SteamgateChevronBlock.set(MetaItemBlockCasing.initMetaBlockCasing(22, BlockLoader.metaCasing));
        GTNLItemList.IronReinforcedWood.set(MetaItemBlockCasing.initMetaBlockCasing(23, BlockLoader.metaCasing));
        GTNLItemList.BronzeReinforcedWood.set(MetaItemBlockCasing.initMetaBlockCasing(24, BlockLoader.metaCasing));
        GTNLItemList.SteelReinforcedWood.set(MetaItemBlockCasing.initMetaBlockCasing(25, BlockLoader.metaCasing));
        GTNLItemList.BreelPipeCasing.set(MetaItemBlockCasing.initMetaBlockCasing(26, BlockLoader.metaCasing));
        GTNLItemList.StronzeWrappedCasing.set(MetaItemBlockCasing.initMetaBlockCasing(27, BlockLoader.metaCasing));
        GTNLItemList.HydraulicAssemblingCasing.set(MetaItemBlockCasing.initMetaBlockCasing(28, BlockLoader.metaCasing));
        GTNLItemList.HyperPressureBreelCasing.set(MetaItemBlockCasing.initMetaBlockCasing(29, BlockLoader.metaCasing));
        GTNLItemList.BreelPlatedCasing.set(MetaItemBlockCasing.initMetaBlockCasing(30, BlockLoader.metaCasing));
        GTNLItemList.SteamCompactPipeCasing.set(MetaItemBlockCasing.initMetaBlockCasing(31, BlockLoader.metaCasing));
        GTNLItemList.VibrationSafeCasing.set(MetaItemBlockCasing.initMetaBlockCasing(0, BlockLoader.metaCasing02));
        GTNLItemList.IndustrialSteamCasing.set(MetaItemBlockCasing.initMetaBlockCasing(1, BlockLoader.metaCasing02));
        GTNLItemList.AdvancedIndustrialSteamCasing
            .set(MetaItemBlockCasing.initMetaBlockCasing(2, BlockLoader.metaCasing02));
        GTNLItemList.StainlessSteelGearBox.set(MetaItemBlockCasing.initMetaBlockCasing(3, BlockLoader.metaCasing02));

        GTNLItemList.AssemblerMatrixFrame.set(MetaItemBlockCasing.initMetaBlockCasing(4, BlockLoader.metaCasing02));
        GTNLItemList.AssemblerMatrixWall.set(MetaItemBlockCasing.initMetaBlockCasing(5, BlockLoader.metaCasing02));

        GTNLItemList.AssemblerMatrixPattern.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                6,
                BlockLoader.metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixPattern_00") }));
        GTNLItemList.AssemblerMatrixCrafter.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                7,
                BlockLoader.metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixCrafter_00") }));
        GTNLItemList.AssemblerMatrixSingularityCrafter.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                8,
                BlockLoader.metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixSingularityCrafter_00") }));
        GTNLItemList.AssemblerMatrixSpeed.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                9,
                BlockLoader.metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixSpeed_00") }));
    }

    public static void registry() {
        registryBlocks();
        registryBlockContainers();
    }

    public static void registerTreeBrickuoia() {
        SaplingBrickuoia = new SaplingBrickuoia();
        GTNLItemList.SaplingBrickuoia.set(new ItemStack(SaplingBrickuoia, 1));
        SaplingBrickuoia.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
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
        OreDictionary.registerOre("treeSapling", new ItemStack(SaplingBrickuoia, 1, GTRecipeBuilder.WILDCARD));
    }
}
