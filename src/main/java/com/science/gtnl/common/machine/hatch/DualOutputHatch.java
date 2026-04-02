package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_INPUT_HATCH_2x2;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.utils.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class DualOutputHatch extends MTEHatchOutput implements IAddUIWidgets {

    private final FluidStack[] mStoredFluid;
    private final FluidStackTank[] fluidTanks;
    public final int mCapacityPer;

    public DualOutputHatch(int aID, int aSlot, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            new String[] { StatCollector.translateToLocal("Tooltip_DualOutputHatch_00"), "",
                StatCollector.translateToLocal("Tooltip_DualOutputHatch_02_00") + GTUtility.formatNumbers(aSlot)
                    + StatCollector.translateToLocal("Tooltip_DualOutputHatch_02_01") },
            4);
        this.mStoredFluid = new FluidStack[aSlot];
        fluidTanks = new FluidStackTank[aSlot];
        mCapacityPer = getCapacityPerTank(aTier, aSlot);
        mDescriptionArray[1] = StatCollector.translateToLocal("Tooltip_DualOutputHatch_01")
            + GTUtility.formatNumbers(mCapacityPer)
            + "L";
    }

    public DualOutputHatch(String aName, int aSlot, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aSlot, aTier, aDescription, aTextures);
        this.mStoredFluid = new FluidStack[aSlot];
        fluidTanks = new FluidStackTank[aSlot];
        mCapacityPer = getCapacityPerTank(aTier, aSlot);
        for (int i = 0; i < aSlot; i++) {
            final int index = i;
            fluidTanks[i] = new FluidStackTank(
                () -> mStoredFluid[index],
                fluid -> mStoredFluid[index] = fluid,
                mCapacityPer);
        }
        mDescriptionArray[1] = StatCollector.translateToLocal("Tooltip_DualOutputHatch_01")
            + GTUtility.formatNumbers(mCapacityPer)
            + "L";
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DualOutputHatch(mName, getMaxType(), mTier, mDescriptionArray, mTextures);
    }

    public int getMaxType() {
        return mStoredFluid.length;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (mStoredFluid != null) {
            for (int i = 0; i < mStoredFluid.length; i++) {
                if (mStoredFluid[i] != null)
                    aNBT.setTag("mFluid" + i, mStoredFluid[i].writeToNBT(new NBTTagCompound()));
            }
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (mStoredFluid != null) {
            for (int i = 0; i < mStoredFluid.length; i++) {
                if (aNBT.hasKey("mFluid" + i)) {
                    mStoredFluid[i] = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("mFluid" + i));
                }
            }
        }
    }

    public int getCapacityPerTank(int aTier, int aSlot) {
        if (aSlot == 4) {
            return (int) (2000L * (1L << aTier));
        } else {
            return (int) (2000L * (1L << (aTier - 1)));
        }
    }

    @Override
    public FluidStack getFluid() {
        for (FluidStack tFluid : mStoredFluid) {
            if (tFluid != null && tFluid.amount > 0) return tFluid;
        }
        return null;
    }

    @Override
    public boolean canStoreFluid(@Nonnull FluidStack fluidStack) {
        if (GTModHandler.isSteam(fluidStack)) {
            if (!outputsSteam()) return false;
        } else {
            if (!outputsLiquids()) return false;
        }

        for (FluidStack slotFluid : mStoredFluid) {
            if (slotFluid == null || GTUtility.areFluidsEqual(slotFluid, fluidStack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int fill(FluidStack aFluid, boolean doFill) {
        if (aFluid == null || aFluid.getFluid()
            .getID() <= 0 || aFluid.amount <= 0 || !canTankBeFilled() || !isFluidInputAllowed(aFluid)) return 0;
        if (!hasFluid(aFluid) && getFirstEmptySlot() != -1) {
            int tFilled = Math.min(aFluid.amount, mCapacityPer);
            if (doFill) {
                FluidStack tFluid = aFluid.copy();
                tFluid.amount = tFilled;
                addFluid(tFluid, getFirstEmptySlot());
                getBaseMetaTileEntity().markDirty();
            }
            return tFilled;
        }
        if (hasFluid(aFluid)) {
            int tLeft = mCapacityPer - getFluidAmount(aFluid);
            int tFilled = Math.min(tLeft, aFluid.amount);
            if (doFill) {
                FluidStack tFluid = aFluid.copy();
                tFluid.amount = tFilled;
                addFluid(tFluid, getFluidSlot(tFluid));
                getBaseMetaTileEntity().markDirty();
            }
            return tFilled;
        }
        return 0;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (getFluid() == null || !canTankBeEmptied()) return null;
        if (getFluid().amount <= 0 && isFluidChangingAllowed()) {
            setFluid(null, getFluidSlot(getFluid()));
            getBaseMetaTileEntity().markDirty();
            return null;
        }
        FluidStack tRemove = getFluid().copy();
        tRemove.amount = Math.min(maxDrain, tRemove.amount);
        if (doDrain) {
            getFluid().amount -= tRemove.amount;
            getBaseMetaTileEntity().markDirty();
        }
        if (getFluid() == null || getFluid().amount <= 0 && isFluidChangingAllowed()) {
            setFluid(null, getFluidSlot(getFluid()));
            getBaseMetaTileEntity().markDirty();
        }
        return tRemove;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack aFluid, boolean doDrain) {
        if (aFluid == null || !hasFluid(aFluid)) return null;
        FluidStack tStored = mStoredFluid[getFluidSlot(aFluid)];
        if (tStored.amount <= 0 && isFluidChangingAllowed()) {
            setFluid(null, getFluidSlot(tStored));
            getBaseMetaTileEntity().markDirty();
            return null;
        }
        FluidStack tRemove = tStored.copy();
        tRemove.amount = Math.min(aFluid.amount, tRemove.amount);
        if (doDrain) {
            tStored.amount -= tRemove.amount;
            getBaseMetaTileEntity().markDirty();
        }
        if (tStored.amount <= 0 && isFluidChangingAllowed()) {
            setFluid(null, getFluidSlot(tStored));
            getBaseMetaTileEntity().markDirty();
        }
        return tRemove;
    }

    public int getFirstEmptySlot() {
        for (int i = 0; i < mStoredFluid.length; i++) {
            if (mStoredFluid[i] == null) return i;
        }
        return -1;
    }

    public boolean hasFluid(FluidStack aFluid) {
        if (aFluid == null) return false;
        for (FluidStack tFluid : mStoredFluid) {
            if (aFluid.isFluidEqual(tFluid)) return true;
        }
        return false;
    }

    public int getFluidSlot(FluidStack tFluid) {
        if (tFluid == null) return -1;
        for (int i = 0; i < mStoredFluid.length; i++) {
            if (tFluid.equals(mStoredFluid[i])) return i;
        }
        return -1;
    }

    public int getFluidAmount(FluidStack tFluid) {
        int tSlot = getFluidSlot(tFluid);
        if (tSlot != -1) {
            return mStoredFluid[tSlot].amount;
        }
        return 0;
    }

    public void setFluid(FluidStack aFluid, int aSlot) {
        if (aSlot < 0 || aSlot >= getMaxType()) return;
        mStoredFluid[aSlot] = aFluid;
    }

    public void addFluid(FluidStack aFluid, int aSlot) {
        if (aSlot < 0 || aSlot >= getMaxType()) return;
        if (aFluid.equals(mStoredFluid[aSlot])) mStoredFluid[aSlot].amount += aFluid.amount;
        if (mStoredFluid[aSlot] == null) mStoredFluid[aSlot] = aFluid.copy();
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        int SLOT_NUMBER = this.fluidTanks.length;
        final Pos2d[] positionsFour = new Pos2d[] { new Pos2d(70, 25), new Pos2d(88, 25), new Pos2d(70, 43),
            new Pos2d(88, 43), };
        final Pos2d[] positionsNine = new Pos2d[] { new Pos2d(61, 16), new Pos2d(79, 16), new Pos2d(97, 16),
            new Pos2d(61, 34), new Pos2d(79, 34), new Pos2d(97, 34), new Pos2d(61, 52), new Pos2d(79, 52),
            new Pos2d(97, 52) };

        if (SLOT_NUMBER == 4) {
            for (int i = 0; i < SLOT_NUMBER; i++) {
                builder.widget(
                    new FluidSlotWidget(fluidTanks[i]).setBackground(ModularUITextures.FLUID_SLOT)
                        .setPos(positionsFour[i]));
            }
        } else {
            for (int i = 0; i < SLOT_NUMBER; i++) {
                builder.widget(
                    new FluidSlotWidget(fluidTanks[i]).setBackground(ModularUITextures.FLUID_SLOT)
                        .setPos(positionsNine[i]));
            }
        }
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            mFluid = getFluid();
        }
        super.onPreTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && mStoredFluid != null) {
            for (int i = 0; i < getMaxType(); i++) {
                if (mStoredFluid[i] != null && mStoredFluid[i].amount <= 0) {
                    mStoredFluid[i] = null;
                }
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return aIndex >= getMaxType();
    }

    @Override
    public int getCapacity() {
        return mCapacityPer;
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_INPUT_HATCH_2x2) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_INPUT_HATCH_2x2) };
    }
}
