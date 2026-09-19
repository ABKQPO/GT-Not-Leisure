package com.science.gtnl.utils;

import java.util.Set;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.util.GTUtility;

public class FluidIdUtils {

    public static boolean matchesAny(Set<GTUtility.FluidId> fluidIds, FluidStack fluidStack) {
        if (fluidIds == null || fluidStack == null) return false;

        for (GTUtility.FluidId fluidId : fluidIds) {
            if (fluidId.matches(fluidStack)) return true;
        }

        return false;
    }

    public static boolean matchesAny(Set<GTUtility.FluidId> fluidIds, Fluid fluid) {
        if (fluidIds == null || fluid == null) return false;

        for (GTUtility.FluidId fluidId : fluidIds) {
            if (fluidId.matches(fluid)) return true;
        }

        return false;
    }
}
