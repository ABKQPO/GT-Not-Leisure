package com.science.gtnl.compat.newhorizons;

import net.minecraft.item.ItemStack;

import com.dreammaster.item.NHItemList;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

// Items of the NH core mod that GTNL uses when it is installed. Callable without the mod: its classes are only touched
// inside the optional method.
public enum NHCoreItem {

    ChargedCertusQuartzDust,
    CoinTechnician,
    EngineCore,
    ExtruderShapeBoat,
    MarshmallowFormMold,
    MoldBoots,
    MoldChestplate,
    MoldHelmet,
    MoldLeggings,
    MysteriousCrystalLens,
    PikoCircuit,
    TCetiESeaweedExtract,
    TwilightCrystal;

    public ItemStack get(int amount, ItemStack fallback) {
        return Mods.NewHorizonsCoreMod.isModLoaded() ? stack(amount) : fallback;
    }

    @Optional.Method(modid = "dreamcraft")
    private ItemStack stack(int amount) {
        NHItemList item = switch (this) {
            case ChargedCertusQuartzDust -> NHItemList.ChargedCertusQuartzDust;
            case CoinTechnician -> NHItemList.CoinTechnician;
            case EngineCore -> NHItemList.EngineCore;
            case ExtruderShapeBoat -> NHItemList.ExtruderShapeBoat;
            case MarshmallowFormMold -> NHItemList.MarshmallowFormMold;
            case MoldBoots -> NHItemList.MoldBoots;
            case MoldChestplate -> NHItemList.MoldChestplate;
            case MoldHelmet -> NHItemList.MoldHelmet;
            case MoldLeggings -> NHItemList.MoldLeggings;
            case MysteriousCrystalLens -> NHItemList.MysteriousCrystalLens;
            case PikoCircuit -> NHItemList.PikoCircuit;
            case TCetiESeaweedExtract -> NHItemList.TCetiESeaweedExtract;
            case TwilightCrystal -> NHItemList.TwilightCrystal;
        };
        return item.get(amount);
    }
}
