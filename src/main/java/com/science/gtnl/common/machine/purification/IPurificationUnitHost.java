package com.science.gtnl.common.machine.purification;

import java.util.ArrayList;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.mixinHelper.ICostingEUHolder;
import com.science.gtnl.api.mixinHelper.IPurificationUnitLongParallel;
import com.science.gtnl.api.mixinHelper.IWirelessMode;

import gregtech.api.util.GTRecipe;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationPlant;

/**
 * What GTNL adds to every GT purification unit through its mixin: the wireless and long parallel state, and access to
 * the unit's protected fields for {@link PurificationUnitHooks}.
 */
public interface IPurificationUnitHost extends IWirelessMode, ICostingEUHolder, IPurificationUnitLongParallel {

    PurificationUnitState gtnl$state();

    ArrayList<FluidStack> gtnl$storedFluids();

    void gtnl$setStoredFluids(ArrayList<FluidStack> fluids);

    GTRecipe gtnl$currentRecipe();

    void gtnl$setCurrentRecipe(GTRecipe recipe);

    void gtnl$setCurrentRecipeChance(float chance);

    int gtnl$effectiveParallel();

    MTEPurificationPlant gtnl$controller();

    boolean gtnl$supportsCraftingMEBuffer();
}
