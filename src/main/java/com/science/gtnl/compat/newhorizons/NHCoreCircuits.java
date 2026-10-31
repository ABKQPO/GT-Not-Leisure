package com.science.gtnl.compat.newhorizons;

import net.minecraft.item.ItemStack;

import com.dreammaster.item.NHItemList;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;

public class NHCoreCircuits {

    public static ItemStack cheapestCircuit(Materials material) {
        return Mods.NewHorizonsCoreMod.isModLoaded() ? cheapestNHCircuit(material) : null;
    }

    @Optional.Method(modid = Mods.ModIDs.NEW_HORIZONS_CORE_MOD)
    private static ItemStack cheapestNHCircuit(Materials material) {
        if (material == Materials.ULV) return NHItemList.CircuitULV.get(1);
        if (material == Materials.LV) return NHItemList.CircuitLV.get(1);
        if (material == Materials.MV) return NHItemList.CircuitMV.get(1);
        if (material == Materials.HV) return NHItemList.CircuitHV.get(1);
        if (material == Materials.EV) return NHItemList.CircuitEV.get(1);
        if (material == Materials.IV) return NHItemList.CircuitIV.get(1);
        if (material == Materials.LuV) return NHItemList.CircuitLuV.get(1);
        if (material == Materials.ZPM) return NHItemList.CircuitZPM.get(1);
        if (material == Materials.UV) return NHItemList.CircuitUV.get(1);
        if (material == Materials.UHV) return NHItemList.CircuitUHV.get(1);
        if (material == Materials.UEV) return NHItemList.CircuitUEV.get(1);
        if (material == Materials.UIV) return NHItemList.CircuitUIV.get(1);
        if (material == Materials.UMV) return NHItemList.CircuitUMV.get(1);
        if (material == Materials.UXV) return NHItemList.CircuitUXV.get(1);
        if (material == Materials.MAX) return NHItemList.CircuitMAX.get(1);
        return null;
    }
}
