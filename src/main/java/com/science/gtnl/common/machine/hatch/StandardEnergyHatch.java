package com.science.gtnl.common.machine.hatch;

import java.util.Locale;

import net.minecraft.util.StatCollector;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class StandardEnergyHatch extends MTEHatchEnergy {

    public StandardEnergyHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public StandardEnergyHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new StandardEnergyHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.energy." + GTValues.VN[mTier].toLowerCase(Locale.ROOT) + ".standard.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }
}
