package com.science.gtnl.common.machine.voidminer;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.api.IDimensionAbbreviationRegistry;
import com.science.gtnl.compat.extrautilities.ExtraUtilitiesContent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class VMTweakHelper {

    public static final String UNDERDARK_DIMENSION_NAME = "Underdark";

    public static final IDimensionAbbreviationRegistry DIM_MAPPING = new DimensionAbbreviationRegistry();

    public static void initializeDimensionMappings() {
        DIM_MAPPING.registerResolver(new ConfigDimensionAbbreviationResolver());
        DIM_MAPPING.registerResolver(new GalacticGregDimensionAbbreviationResolver());
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (world == null) return;

        bindUnderdarkDimension(world);
        DIM_MAPPING.getAbbreviation(world.provider.dimensionId);
    }

    public static void bindUnderdarkDimension(World world) {
        if (!ExtraUtilitiesContent.isUnderdark(world)) return;
        String abbreviation = DIM_MAPPING.getAbbreviationByName(UNDERDARK_DIMENSION_NAME);
        if (abbreviation == null) return;
        DIM_MAPPING.bind(world.provider.dimensionId, abbreviation);
    }
}
