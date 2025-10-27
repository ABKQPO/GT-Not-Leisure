package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.GTValues.*;

import net.minecraft.util.StatCollector;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;

public class ExplosionDynamoHatch extends MTEHatchDynamo {

    public ExplosionDynamoHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            new String[] { StatCollector.translateToLocal("Tooltip_ExplosionDynamoHatch_00") });
    }

    public ExplosionDynamoHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ExplosionDynamoHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public long maxEUStore() {
        return Long.MAX_VALUE;
    }

    @Override
    public long maxAmperesOut() {
        return 64;
    }
}
