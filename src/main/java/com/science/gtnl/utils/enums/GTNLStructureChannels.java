package com.science.gtnl.utils.enums;

import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.structurelib.StructureLibAPI;

import gregtech.api.structure.IStructureChannels;

/*
 * To unofficial addon authors: Do not add to this enum with EnumHelper or equivalent. Just copy this class into your
 * namespace, and replace the constants
 */
/*
 * Dev notes: Q1: central manage indicator item or in each blocks' constructor? A1: before this is merged #4067 happens.
 * we can build on this info and central manage it EDIT: I ended up with a registerAsIndicator() method here Q2: default
 * tooltip in MBTT builder? A2: Yes Q3: multi specific tier managed here or in individual controller? A3: here, because
 * it needs to be registered to a central location, so it would be nice to have a central location with an easy
 * overview. Plus, it's possible these multi-specific tiers would be become reused by others as development carries on,
 * e.g. PRASS_UNIT_CASING
 */
public enum GTNLStructureChannels implements IStructureChannels {

    // Order of enum constants does not matter
    STRUCTURE_RENDER("structure_render")
    //
    ;

    private final String channel;

    GTNLStructureChannels(String aChannel) {
        channel = aChannel;
    }

    @Override
    public String get() {
        return channel;
    }

    // Keyed by constant name, not channel id: several constants share the "casing" channel with different names
    @Override
    public String getDefaultTooltip() {
        return StatCollector.translateToLocal("channels.gtnl.subchannel." + name().toLowerCase(Locale.ROOT));
    }

    @Override
    public void registerAsIndicator(ItemStack indicator, int channelValue) {
        StructureLibAPI.registerChannelItem(get(), ModList.ScienceNotLeisure.ID, channelValue, indicator);
    }

    public static void register() {
        for (GTNLStructureChannels value : values()) {
            StructureLibAPI.registerChannelDescription(
                value.get(),
                ModList.ScienceNotLeisure.ID,
                "channels." + ModList.ScienceNotLeisure.ID + "." + value.get());
        }
    }
}
