package com.science.gtnl.common.machine.hatch;

import java.util.Locale;

import net.minecraft.util.StatCollector;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessMulti;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class WirelessMultiEnergyHatch extends MTEHatchWirelessMulti {

    // The humongous hatch carries a name of its own instead of the tier and amperage pattern.
    public static final int HUMONGOUS_AMPERES = Integer.MAX_VALUE / 10;

    public WirelessMultiEnergyHatch(int aID, String aName, String aNameRegional, int aTier, int aAmp) {
        super(aID, aName, aNameRegional, aTier, aAmp);
    }

    public WirelessMultiEnergyHatch(String aName, int aTier, int aAmp, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, aTier, aAmp, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new WirelessMultiEnergyHatch(mName, mTier, Amperes, mDescriptionArray, mTextures);
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        if (maxAmperes == HUMONGOUS_AMPERES) return "gtnl.hatch.wireless_energy.humongous.name";
        return "gtnl.hatch.wireless_energy." + GTValues.VN[mTier].toLowerCase(Locale.ROOT) + "." + maxAmperes + ".name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }
}
