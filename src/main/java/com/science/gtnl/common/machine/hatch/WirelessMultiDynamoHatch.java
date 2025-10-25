package com.science.gtnl.common.machine.hatch;

import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.BOLD;
import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.GRAY;
import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.GREEN;
import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.YELLOW;
import static gregtech.api.enums.GTValues.*;

import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtility;
import tectech.mechanics.pipe.IConnectsToEnergyTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessDynamoMulti;

public class WirelessMultiDynamoHatch extends MTEHatchWirelessDynamoMulti implements IConnectsToEnergyTunnel {

    public WirelessMultiDynamoHatch(int aID, String aName, String aNameRegional, int aTier, int aAmp) {
        super(aID, aName, aNameRegional, aTier, aAmp);
    }

    public WirelessMultiDynamoHatch(String aName, int aTier, int aAmp, String[] aDescription,
        ITexture[][][] aTextures) {
        super(aName, aTier, aAmp, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new WirelessMultiDynamoHatch(mName, mTier, Amperes, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isInputFacing(ForgeDirection side) {
        return side != getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean canConnect(ForgeDirection side) {
        return side != getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public long maxEUInput() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long maxAmperesIn() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isEnetInput() {
        return true;
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_WirelessMultiDynamoHatch_00"),
            StatCollector.translateToLocal("Tooltip_WirelessMultiDynamoHatch_01"),
            StatCollector.translateToLocal("Tooltip_WirelessMultiDynamoHatch_02"),
            AuthorColen + GRAY + BOLD + " & " + GREEN + BOLD + "Chrom",
            StatCollector.translateToLocal("gt.blockmachines.hatch.energytunnel.desc.1") + ": "
                + YELLOW
                + GTUtility.formatNumbers(maxAmperes * V[mTier])
                + GRAY
                + " EU/t" };
    }
}
