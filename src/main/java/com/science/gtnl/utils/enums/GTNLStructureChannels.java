package com.science.gtnl.utils.enums;

import net.minecraft.item.ItemStack;

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
    STRUCTURE_RENDER("structure_render", "Enable Machine Render"),
    COMPONENT_ASSEMBLY_LINE_CASING("component_casing", "Component Assembly Line Casing Tier"),
    NUCLEAR_REACTOR_TIER("nuclear_tier", "Nuclear Reactor Tier")
    //
    ;

    private final String channel;
    private final String defaultTooltip;

    GTNLStructureChannels(String aChannel, String defaultTooltip) {
        channel = aChannel;
        this.defaultTooltip = defaultTooltip;
    }

    @Override
    public String get() {
        return channel;
    }

    @Override
    public String getDefaultTooltip() {
        return defaultTooltip;
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
