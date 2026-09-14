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
import gregtech.api.util.GTUtility;

public enum SteamTypes {

    STEAM("Steam", Materials.Steam.mGas, 1, true),
    SH_STEAM("Superheated Steam", GTModHandler.getSuperHeatedSteam(1)
        .getFluid(), 10, true),
    DSC_STEAM("Dense Supercritical Steam", Materials.DenseSupercriticalSteam.mGas, 50, true),
    CM_STEAM("Compressed Steam", GTNLMaterials.CompressedSteam.getMolten(1)
        .getFluid(), 1000, false);

    public static final SteamTypes[] VALUES = values();
    public static final SteamTypes[] NETWORK_CONVERTIBLE_TYPES = Arrays.stream(VALUES)
        .filter(steamType -> steamType.networkConvertible)
        .toArray(SteamTypes[]::new);
    public static final Set<GTUtility.FluidId> SUPPORTED_FLUIDS = createSupportedFluids();
    public static final Map<GTUtility.FluidId, SteamTypes> TYPES_BY_FLUID = createTypesByFluid();

    public final String displayName;
    public final GTUtility.FluidId fluid;
    public final int efficiencyFactor;
    public final BigInteger networkSteamPerLiter;
    public final boolean networkConvertible;

    SteamTypes(String name, Fluid fluid, int efficiency, boolean networkConvertible) {
        this(name, GTUtility.FluidId.create(fluid), efficiency, networkConvertible);
    }

    SteamTypes(String name, GTUtility.FluidId fluid, int efficiency, boolean networkConvertible) {
        this.displayName = name;
        this.fluid = fluid;
        this.efficiencyFactor = efficiency;
        this.networkSteamPerLiter = BigInteger.valueOf(efficiency);
        this.networkConvertible = networkConvertible;
    }

    public static List<SteamTypes> getSupportedTypes() {
        return Arrays.asList(VALUES);
    }

    public static Set<GTUtility.FluidId> getSupportedFluids() {
        return SUPPORTED_FLUIDS;
    }

    public static SteamTypes fromFluid(Fluid fluid) {
        return fluid == null ? null : TYPES_BY_FLUID.get(GTUtility.FluidId.create(fluid));
    }

    public static SteamTypes fromFluidId(GTUtility.FluidId fluidId) {
        return fluidId == null ? null : TYPES_BY_FLUID.get(fluidId);
    }

    public static SteamTypes fromNetworkTypeId(int id) {
        return id >= 0 && id < NETWORK_CONVERTIBLE_TYPES.length ? NETWORK_CONVERTIBLE_TYPES[id] : STEAM;
    }

    private static Map<GTUtility.FluidId, SteamTypes> createTypesByFluid() {
        ImmutableMap.Builder<GTUtility.FluidId, SteamTypes> builder = ImmutableMap.builder();
        for (SteamTypes steamType : VALUES) {
            if (steamType.fluid != null) builder.put(steamType.fluid, steamType);
        }
        return builder.build();
    }

    private static Set<GTUtility.FluidId> createSupportedFluids() {
        ImmutableSet.Builder<GTUtility.FluidId> builder = ImmutableSet.builder();
        for (SteamTypes steamType : VALUES) {
            if (steamType.fluid != null) builder.add(steamType.fluid);
        }
        return builder.build();
    }
}
