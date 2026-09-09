package com.science.gtnl.common.machine.hatch;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;

public class OriginalInputHatch extends MTEHatchInput {

    public OriginalInputHatch(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 0);
    }

    public OriginalInputHatch(String aName, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, 0, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OriginalInputHatch(mName, mDescriptionArray, mTextures);
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public int getCapacity() {
        return 8000;
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("gtnl.hatch.original_input.tooltip.0"),
            StatCollector.translateToLocal("gtnl.hatch.original_input.tooltip.1"),
            StatCollector.translateToLocalFormatted(
                "gtnl.hatch.original_input.tooltip.2",
                NumberFormatUtil.formatNumber(getCapacity())) };
    }
}
