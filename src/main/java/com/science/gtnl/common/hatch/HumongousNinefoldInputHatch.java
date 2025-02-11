package com.science.gtnl.common.hatch;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;

public class HumongousNinefoldInputHatch extends NinefoldInputHatch {

    public HumongousNinefoldInputHatch(int aID, int aSlot, String aName, String aNameRegional) {
        super(aID, aSlot, aName, aNameRegional, 14);
    }

    public HumongousNinefoldInputHatch(String aName, int aSlot, int aTier, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, aSlot, aTier, aDescription, aTextures);
    }

    @Override
    public int getCapacityPerTank(int aTier, int aSlot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HumongousNinefoldInputHatch(mName, getMaxType(), mTier, mDescriptionArray, mTextures);
    }
}
