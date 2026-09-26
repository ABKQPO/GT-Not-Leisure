package com.science.gtnl.common.machine.hatch;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;

public class MTEStronzeInputHatch extends MTEHatchInput {

    public MTEStronzeInputHatch(int id, String name, String regionalName, int tier) {
        super(id, name, regionalName, tier);
    }

    public MTEStronzeInputHatch(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new MTEStronzeInputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int getCapacity() {
        return 128_000;
    }

    @Override
    public String[] getDescription() {
        return new String[] { "%%%" + NumberFormatUtil.formatNumber(getCapacity()) + "%%%" };
    }

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long timer) {
        super.onPostTick(baseMetaTileEntity, timer);
        if (!baseMetaTileEntity.isServerSide()) return;
        drainFromFacedTank(baseMetaTileEntity);
    }

    private void drainFromFacedTank(IGregTechTileEntity baseMetaTileEntity) {
        ForgeDirection facing = baseMetaTileEntity.getFrontFacing();
        if (!(baseMetaTileEntity.getTileEntityAtSide(facing) instanceof IFluidHandler source)) return;

        FluidStack stored = getFillableStack();
        int free = 128_000 - (stored == null ? 0 : stored.amount);
        if (free <= 0) return;

        FluidStack offered = availableFluid(source, facing.getOpposite(), stored == null ? null : stored.getFluid());
        if (offered == null) return;

        offered.amount = Math.min(free, offered.amount);
        FluidStack probe = source.drain(facing.getOpposite(), offered, false);
        if (probe == null || probe.amount <= 0) return;

        int accepted = fill(probe, true);
        if (accepted <= 0) return;

        FluidStack actual = probe.copy();
        actual.amount = accepted;
        source.drain(facing.getOpposite(), actual, true);
        fill(actual, false);
    }

    private static FluidStack availableFluid(IFluidHandler source, ForgeDirection side, Fluid wanted) {
        FluidStack probe = source.drain(side, Integer.MAX_VALUE, false);
        if (probe == null || probe.amount <= 0) return null;
        if (wanted != null && probe.getFluid() != wanted) return null;
        return probe;
    }
}
