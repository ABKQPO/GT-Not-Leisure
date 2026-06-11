package com.science.gtnl.utils.enums;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.fluids.Fluid;

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

    public final String displayName;
    public final Fluid fluid;
    public final int efficiencyFactor;

    SteamTypes(String name, Fluid fluid, int efficiency) {
        this.displayName = name;
        this.fluid = fluid;
        this.efficiencyFactor = efficiency;
    }

    public static List<SteamTypes> getSupportedTypes() {
        return Arrays.asList(VALUES);
    }
}
