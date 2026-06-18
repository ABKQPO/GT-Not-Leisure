package com.science.gtnl.common.machine.hatch;

import net.minecraft.util.StatCollector;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;

public class OriginalOutputHatch extends MTEHatchOutput {

    public OriginalOutputHatch(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 0);
    }

    public OriginalOutputHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OriginalOutputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean doesFillContainers() {
        return false;
    }

    @Override
    public int getCapacity() {
        return 4096000;
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_OriginalOutputHatch_00"), StatCollector
            .translateToLocalFormatted("Tooltip_OriginalOutputHatch_01", NumberFormatUtil.formatNumber(getCapacity())),
            StatCollector.translateToLocal("Tooltip_OriginalOutputHatch_02"),
            StatCollector.translateToLocal("Tooltip_OriginalOutputHatch_03"),
            StatCollector.translateToLocal("Tooltip_OriginalOutputHatch_04") };
    }
}
