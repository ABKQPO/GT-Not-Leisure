package com.science.gtnl.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackLookup {

    private static final Map<String, Fluid> FLUID_CACHE = new ConcurrentHashMap<>();

    private FluidStackLookup() {}

    public static FluidStack getFluidStack(String fluidName, int amount) {
        Fluid fluid = FLUID_CACHE.computeIfAbsent(fluidName, FluidRegistry::getFluid);
        return getFluidStack(fluid, amount);
    }

    public static FluidStack getFluidStack(Fluid fluid, int amount) {
        return fluid == null ? null : new FluidStack(fluid, amount);
    }

    public static FluidStack getFluidStack(FluidStack fluidStack, int amount) {
        return fluidStack == null ? null : new FluidStack(fluidStack, amount);
    }
}
