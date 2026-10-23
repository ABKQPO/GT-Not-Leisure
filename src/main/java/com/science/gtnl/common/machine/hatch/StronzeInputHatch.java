package com.science.gtnl.common.machine.hatch;

import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;

@IMetaTileEntity.SkipGenerateName
@IMetaTileEntity.SkipGenerateDescription
public class StronzeInputHatch extends MTEHatchInput {

    public StronzeInputHatch(int id, String name, String regionalName, int tier) {
        super(id, name, regionalName, tier);
    }

    public StronzeInputHatch(String name, int tier, String[] description, ITexture[][][] textures) {
        super(name, tier, description, textures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new StronzeInputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int getCapacity() {
        return 128_000;
    }

    @Override
    public String getLocalNameKey() {
        if (!hasOwnLocalName()) return super.getLocalNameKey();
        return "gtnl.hatch.stronze.input_hatch.steam.name";
    }

    @Override
    public String getLocalName() {
        if (!hasOwnLocalName()) return super.getLocalName();
        return StatCollector.translateToLocal(getLocalNameKey());
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("gtnl.hatch.stronze.input_hatch.steam.tooltip.0"),
            StatCollector.translateToLocal("gtnl.hatch.stronze.input_hatch.steam.tooltip.1"),
            StatCollector.translateToLocal("gtnl.hatch.stronze.input_hatch.steam.tooltip.2"),
            StatCollector.translateToLocal("gtnl.hatch.stronze.input_hatch.steam.tooltip.3") };
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

        int accepted = fill(probe, false);
        if (accepted <= 0) return;

        FluidStack actual = probe.copy();
        actual.amount = accepted;
        FluidStack drained = source.drain(facing.getOpposite(), actual, true);
        if (drained != null && drained.amount > 0 && fill(drained, true) > 0) {
            notifyWatchers();
        }
    }

    private static FluidStack availableFluid(IFluidHandler source, ForgeDirection side, Fluid wanted) {
        FluidStack probe = source.drain(side, Integer.MAX_VALUE, false);
        if (probe == null || probe.amount <= 0) return null;
        if (wanted != null && probe.getFluid() != wanted) return null;
        return probe.copy();
    }
}
