package com.science.gtnl.mixins.late.tecTech;

import org.spongepowered.asm.mixin.Mixin;

import gregtech.common.gui.modularui.multiblock.MTEResearchStationGui;

/**
 * MTEResearchStationGui no longer has createPowerPanelButton in the new GT5U API.
 * The base MTEMultiBlockBaseGui handles power panel buttons generically.
 */
@Mixin(value = MTEResearchStationGui.class, remap = false)
public abstract class MixinMTEResearchStationGui {

}
