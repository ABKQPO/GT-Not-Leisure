package com.science.gtnl.loader;

import net.minecraft.util.StatCollector;

import com.Nxer.TwistSpaceTechnology.common.GTCMItemList;
import com.gtnllib.client.tooltip.AnimatedTooltipHandler;
import com.gtnllib.mod.ModList;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.cover.FluidCover;
import com.science.gtnl.common.machine.cover.VoidCover;
import com.science.gtnl.common.machine.cover.WirelessMultiEnergyCover;
import com.science.gtnl.common.machine.cover.WirelessSteamCover;
import com.science.gtnl.compat.vmtweak.VoidMinerTweak;
import com.science.gtnl.config.MachineConfig;
import com.science.gtnl.loader.machine.BasicMachineLoader;
import com.science.gtnl.loader.machine.HatchLoader;
import com.science.gtnl.loader.machine.MultiblockLoader;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GTNLStructureChannels;
import com.science.gtnl.utils.text.AnimatedText;

import bartworks.common.loaders.ItemRegistry;
import cpw.mods.fml.common.Optional;
import gregtech.api.covers.CoverRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.render.TextureFactory;
import gregtech.common.covers.CoverConveyor;
import gregtech.common.covers.CoverFluidRegulator;
import gregtech.common.covers.CoverPump;
import gregtech.common.covers.CoverSteamRegulator;
import gregtech.common.covers.CoverSteamValve;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import tectech.thing.CustomItemList;

public class MachineLoader implements IContentLoader {

    @Override
    public String name() {
        return "machines";
    }

    @Override
    public void init() {
        GTNLStructureChannels.register();
    }

    @Override
    public void postInit() {
        registry();
    }

    public static void registerCovers() {
        CoverRegistry.registerCover(
            GTNLItemList.HydraulicPump.get(1L),
            TextureFactory
                .of(Textures.BlockIcons.MACHINE_CASINGS[1][0], TextureFactory.of(Textures.BlockIcons.OVERLAY_PUMP)),
            context -> new CoverPump(context, 1048576, TextureFactory.of(Textures.BlockIcons.OVERLAY_PUMP)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicConveyor.get(1L),
            TextureFactory
                .of(Textures.BlockIcons.MACHINE_CASINGS[1][0], TextureFactory.of(Textures.BlockIcons.OVERLAY_CONVEYOR)),
            context -> new CoverConveyor(context, 1, 16, 64, TextureFactory.of(Textures.BlockIcons.OVERLAY_CONVEYOR)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicRegulator.get(1L),
            TextureFactory
                .of(Textures.BlockIcons.MACHINE_CASINGS[1][0], TextureFactory.of(Textures.BlockIcons.OVERLAY_PUMP)),
            context -> new CoverSteamRegulator(context, 1048576, TextureFactory.of(Textures.BlockIcons.OVERLAY_PUMP)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicSteamValve.get(1L),
            TextureFactory
                .of(Textures.BlockIcons.MACHINE_CASINGS[1][0], TextureFactory.of(Textures.BlockIcons.OVERLAY_VALVE)),
            context -> new CoverSteamValve(context, 16777216, TextureFactory.of(Textures.BlockIcons.OVERLAY_VALVE)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicSteamRegulator.get(1L),
            TextureFactory
                .of(Textures.BlockIcons.MACHINE_CASINGS[1][0], TextureFactory.of(Textures.BlockIcons.OVERLAY_VALVE)),
            context -> new CoverFluidRegulator(
                context,
                16777216,
                TextureFactory.of(Textures.BlockIcons.OVERLAY_VALVE)));

        CoverRegistry.registerCover(
            GTNLItemList.PipelessSteamCover.get(1L),
            TextureFactory.of(
                Textures.BlockIcons.MACHINE_CASINGS[1][0],
                TextureFactory.of(Textures.BlockIcons.OVERLAYS_ENERGY_ON_WIRELESS[0])),
            WirelessSteamCover::new,
            CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);

        CoverRegistry.registerCover(
            GTNLItemList.VoidCover.get(1L),
            TextureFactory
                .of(Textures.BlockIcons.MACHINE_CASINGS[1][0], TextureFactory.of(Textures.BlockIcons.ITEM_VOID_SIGN)),
            VoidCover::new,
            CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);

        CoverRegistry.registerCover(
            GTNLItemList.WaterCover.get(1L),
            TextureFactory
                .of(Textures.BlockIcons.MACHINE_CASINGS[4][0], TextureFactory.of(TexturesGtBlock.Overlay_Water)),
            context -> new FluidCover(context, Materials.Water.mFluid, "gtnl.item.meta_item.4.name"),
            CoverRegistry.PRIMITIVE_COVER_PLACER);

        for (int i = 0; i < 14; i++) {
            int tier = i + 1;
            CoverRegistry.registerCover(
                GTNLItemList.WIRELESS_ENERGY_COVER_4A[i].get(1),
                TextureFactory.of(
                    Textures.BlockIcons.MACHINE_CASINGS[1][0],
                    Textures.BlockIcons.OVERLAYS_ENERGY_ON_WIRELESS_4A[0]),
                context -> new WirelessMultiEnergyCover(context, (int) GTValues.V[tier], 4),
                CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);
        }

        // CoverRegistry.registerCover(
        // GTNLItemList.SteelTurbine.get(1L),
        // TextureFactory.of(TexturesGtBlock.Overlay_Water),
        // ctx -> new FluidCover(ctx, Materials.Water.mFluid, "InfinityWaterCover"),
        // CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);
    }

    public static void registry() {
        ScienceNotLeisure.LOG.info("GTNL Content | Registering MTE Block Machine.");
        HatchLoader.registry();
        MultiblockLoader.registry();
        BasicMachineLoader.registry();
        registerCovers();

        loadItemTooltip();
    }

    public static void loadItemTooltip() {

        if (VoidMinerTweak.enabled()) {
            AnimatedTooltipHandler.addItemTooltip(ItemRegistry.voidminer[0], AnimatedText.GT_NOT_LEISURE_CHANGE);
            AnimatedTooltipHandler.addItemTooltip(ItemRegistry.voidminer[1], AnimatedText.GT_NOT_LEISURE_CHANGE);
            AnimatedTooltipHandler.addItemTooltip(ItemRegistry.voidminer[2], AnimatedText.GT_NOT_LEISURE_CHANGE);
        }

        if (MachineConfig.enablePurificationPlantBuff) {
            AnimatedTooltipHandler.addItemTooltip(
                ItemList.Machine_Multi_PurificationPlant.get(1),
                () -> StatCollector.translateToLocal("gtnl.machine.purification_plant.tooltip.0"));
            AnimatedTooltipHandler.addItemTooltip(
                ItemList.Machine_Multi_PurificationPlant.get(1),
                () -> StatCollector.translateToLocal("gtnl.machine.purification_plant.tooltip.1"));
            AnimatedTooltipHandler.addItemTooltip(
                ItemList.Machine_Multi_PurificationPlant.get(1),
                () -> StatCollector.translateToLocal("gtnl.machine.purification_plant.tooltip.2"));
            AnimatedTooltipHandler
                .addItemTooltip(ItemList.Machine_Multi_PurificationPlant.get(1), AnimatedText.GT_NOT_LEISURE_CHANGE);
        }

        if (ModList.TwistSpaceTechnology.isModLoaded()) {
            loadTSTItemTooltip();
        }

        AnimatedTooltipHandler.addItemTooltip(
            CustomItemList.Machine_Multi_Research.get(1),
            () -> StatCollector.translateToLocal("gtnl.machine.research_station.tooltip.0"));
        AnimatedTooltipHandler
            .addItemTooltip(CustomItemList.Machine_Multi_Research.get(1), AnimatedText.GT_NOT_LEISURE_CHANGE);
    }

    @Optional.Method(modid = "TwistSpaceTechnology")
    public static void loadTSTItemTooltip() {
        AnimatedTooltipHandler
            .addItemTooltip(GTCMItemList.OreProcessingFactory.get(1), AnimatedText.GT_NOT_LEISURE_CHANGE);
    }

}
