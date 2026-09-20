package com.science.gtnl.common.machine.hatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.science.gtnl.common.gui.modularui.NuclearFluidHatchGui;

import gregtech.GTMod;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.render.TextureFactory;

public class NuclearFluidHatch extends MTEHatch {

    public int mFluidCapacity;

    public NuclearFluidHatch(int aID, String aName, String aNameRegional, int aTier, int aCapacity) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            0,
            new String[] { StatCollector.translateToLocal("gtnl.hatch.nuclear_fluid_hatch.tooltip.0"),
                StatCollector.translateToLocal("gtnl.hatch.nuclear_fluid_hatch.tooltip.1"),
                StatCollector.translateToLocal("gtnl.hatch.nuclear_fluid_hatch.tooltip.capacity") + " "
                    + aCapacity
                    + "L" });
        this.mFluidCapacity = aCapacity;
    }

    public NuclearFluidHatch(String aName, int aTier, int aCapacity, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
        this.mFluidCapacity = aCapacity;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NuclearFluidHatch(mName, mTier, mFluidCapacity, mDescriptionArray, mTextures);
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return facing == ForgeDirection.UP;
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public boolean canTankBeEmptied() {
        return false;
    }

    @Override
    public boolean isLiquidInput(ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack aFluid) {
        return true;
    }

    @Override
    public boolean doesEmptyContainers() {
        return false;
    }

    @Override
    public int getCapacity() {
        return mFluidCapacity;
    }

    public FluidStack getInputFluid() {
        return mFluid;
    }

    public FluidStack consumeInputFluid(int aAmount, boolean doDrain) {
        if (mFluid == null || mFluid.amount <= 0 || aAmount <= 0) return null;
        FluidStack removed = mFluid.copy();
        removed.amount = Math.min(aAmount, mFluid.amount);
        if (doDrain) {
            mFluid.amount -= removed.amount;
            if (mFluid.amount <= 0) mFluid = null;
            if (getBaseMetaTileEntity() != null) getBaseMetaTileEntity().markDirty();
        }
        return removed;
    }

    public FluidStack consumeInputFluid(FluidStack aFluid, boolean doDrain) {
        if (aFluid == null) return null;
        return consumeInputFluid(aFluid.amount, doDrain);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return inputHatchTextures(aBaseTexture);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return inputHatchTextures(aBaseTexture);
    }

    private ITexture[] inputHatchTextures(ITexture aBaseTexture) {
        byte color = getBaseMetaTileEntity().getColorization();
        ITexture coloredPipeOverlay = TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_COLORS[color + 1]);
        return GTMod.proxy.mRenderIndicatorsOnHatch
            ? new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN), coloredPipeOverlay,
                TextureFactory.of(Textures.BlockIcons.FLUID_IN_SIGN) }
            : new ITexture[] { aBaseTexture, TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_IN),
                coloredPipeOverlay };
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        openGui(aPlayer);
        return true;
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new NuclearFluidHatchGui(this).build(data, syncManager, uiSettings);
    }
}
