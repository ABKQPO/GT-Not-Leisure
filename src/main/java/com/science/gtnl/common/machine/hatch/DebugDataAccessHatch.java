package com.science.gtnl.common.machine.hatch;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;
import gregtech.api.util.GTRecipe;

public class DebugDataAccessHatch extends MTEHatchDataAccess {

    public DebugDataAccessHatch(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 14);
        mDescriptionArray[0] = StatCollector.translateToLocal("gtnl.hatch.debug_data_access.tooltip.0");
        mDescriptionArray[1] = StatCollector.translateToLocal("gtnl.hatch.debug_data_access.tooltip.1");
    }

    public DebugDataAccessHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DebugDataAccessHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public List<GTRecipe.RecipeAssemblyLine> getAssemblyLineRecipes() {
        return GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes;
    }
}
