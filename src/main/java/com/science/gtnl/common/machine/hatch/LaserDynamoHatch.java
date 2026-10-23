package com.science.gtnl.common.machine.hatch;

import java.util.Locale;

import net.minecraft.util.StatCollector;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;

/**
 * Laser source hatch registered by GTNL. The GT class names itself from tier and amperage, so this only swaps in GTNL's
 * key.
 */
@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class LaserDynamoHatch extends MTEHatchDynamoTunnel {

    public LaserDynamoHatch(int aID, String aName, String aNameRegional, int aTier, int aAmp) {
        super(aID, aName, aNameRegional, aTier, aAmp);
    }

    public LaserDynamoHatch(String aName, int aTier, int aAmp, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aAmp, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LaserDynamoHatch(mName, mTier, Amperes, mDescriptionArray, mTextures);
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.laser_dynamo." + GTValues.VN[mTier].toLowerCase(Locale.ROOT) + "." + maxAmperes + ".name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }
}
