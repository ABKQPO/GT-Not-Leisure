package com.science.gtnl.utils.enums;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fluids.Fluid;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.science.gtnl.common.material.GTNLMaterials;

import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;

public enum SteamTypes {

    STEAM("Steam", Materials.Steam.mGas, 1),
    SH_STEAM("Superheated Steam", GTModHandler.getSuperHeatedSteam(1)
        .getFluid(), 10),
    DSC_STEAM("Dense Supercritical Steam", Materials.DenseSupercriticalSteam.mGas, 50),
    CM_STEAM("Compressed Steam", GTNLMaterials.CompressedSteam.getMolten(1)
        .getFluid(), 1000);

    public static final SteamTypes[] VALUES = values();
    public static final Set<Fluid> SUPPORTED_FLUIDS = createSupportedFluids();
    public static final Map<Fluid, SteamTypes> TYPES_BY_FLUID = createTypesByFluid();

    public final String displayName;
    public final Fluid fluid;
    public final int efficiencyFactor;
    public final BigInteger networkSteamPerLiter;

    SteamTypes(String name, Fluid fluid, int efficiency) {
        this.displayName = name;
        this.fluid = fluid;
        this.efficiencyFactor = efficiency;
        this.networkSteamPerLiter = BigInteger.valueOf(efficiency);
    }

    public static List<SteamTypes> getSupportedTypes() {
        return Arrays.asList(VALUES);
    }

    public static Set<Fluid> getSupportedFluids() {
        return SUPPORTED_FLUIDS;
    }

    public static SteamTypes fromFluid(Fluid fluid) {
        return fluid == null ? null : TYPES_BY_FLUID.get(fluid);
    }

    private static Map<Fluid, SteamTypes> createTypesByFluid() {
        ImmutableMap.Builder<Fluid, SteamTypes> builder = ImmutableMap.builder();
        for (SteamTypes steamType : VALUES) {
            if (steamType.fluid != null) builder.put(steamType.fluid, steamType);
        }
        return builder.build();
    }

    private static Set<Fluid> createSupportedFluids() {
        ImmutableSet.Builder<Fluid> builder = ImmutableSet.builder();
        for (SteamTypes steamType : VALUES) {
            if (steamType.fluid != null) builder.add(steamType.fluid);
        }
        return builder.build();
    }
}
