package com.science.gtnl.common.machine.hatch;

import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_NINE_HATCH;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_NINE_HATCH_COLOR;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;

public class NinefoldInputHatch extends MTEHatchMultiInput implements IAddUIWidgets, IAddGregtechLogo {

    private final FluidStack[] mStoredFluid;
    private final FluidStackTank[] fluidTanks;
    public final int mCapacityPer;

    public NinefoldInputHatch(int aID, int aSlot, String aName, String aNameRegional, int aTier) {
        super(aID, aSlot, aName, aNameRegional, aTier);
        this.mStoredFluid = new FluidStack[aSlot];
        fluidTanks = new FluidStackTank[aSlot];
        mCapacityPer = getCapacityPerTank(aTier, aSlot);
    }

    public NinefoldInputHatch(String aName, int aSlot, int aTier, String[] aDescription, ITexture[][][] aTextures) {
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
    }

    @Override
    public String[] getDescription() {

        ArrayList<String> desc = new ArrayList<>();

        desc.add(StatCollector.translateToLocal("Tooltip_NinefoldInputHatch_00"));
        desc.add(
            StatCollector.translateToLocal("Tooltip_NinefoldInputHatch_01") + GTUtility.formatNumbers(mCapacityPer)
                + "L");
        desc.add(
            StatCollector.translateToLocalFormatted(
                "Tooltip_NinefoldInputHatch_02",
                GTUtility.formatNumbers(mInventory.length)));

        return desc.toArray(new String[] {});
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new NinefoldInputHatch(mName, getMaxType(), mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public FluidStack[] getStoredFluid() {
        return mStoredFluid;
    }

    @Override
    public int getCapacityPerTank(int aTier, int aSlot) {
        return (int) (2000L * (1L << (aTier - 1)));
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        byte color = getBaseMetaTileEntity().getColorization();
        ITexture coloredPipeOverlay = TextureFactory.of(OVERLAY_FRONT_NINE_HATCH_COLOR[color + 1]);
        return new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_FRONT_NINE_HATCH), coloredPipeOverlay };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        byte color = getBaseMetaTileEntity().getColorization();
        ITexture coloredPipeOverlay = TextureFactory.of(OVERLAY_FRONT_NINE_HATCH_COLOR[color + 1]);
        return new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_FRONT_NINE_HATCH), coloredPipeOverlay };
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return aIndex >= 9;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        final int SLOT_NUMBER = 9;
        final Pos2d[] positions = new Pos2d[] { new Pos2d(61, 16), new Pos2d(79, 16), new Pos2d(97, 16),
            new Pos2d(61, 34), new Pos2d(79, 34), new Pos2d(97, 34), new Pos2d(61, 52), new Pos2d(79, 52),
            new Pos2d(97, 52) };

        for (int i = 0; i < SLOT_NUMBER; i++) {
            builder.widget(
                new FluidSlotWidget(fluidTanks[i]).setBackground(ModularUITextures.FLUID_SLOT)
                    .setPos(positions[i]));
        }
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

    @Override
    public int getMaxType() {
        return mStoredFluid.length;
    }

    @Override
    public FluidStack getFluid() {
        for (FluidStack tFluid : mStoredFluid) {
            if (tFluid != null && tFluid.amount > 0) return tFluid;
        }
        return null;
    }

    public FluidStack getFluid(int aSlot) {
        if (mStoredFluid == null || aSlot < 0 || aSlot >= getMaxType()) return null;
        return mStoredFluid[aSlot];
    }

    @Override
    public int getFluidAmount() {
        if (getFluid() != null) {
            return getFluid().amount;
        }
        return 0;
    }

    @Override
    public int getCapacity() {
        return mCapacityPer;
    }

    @Override
    public int getFirstEmptySlot() {
        for (int i = 0; i < mStoredFluid.length; i++) {
            if (mStoredFluid[i] == null) return i;
        }
        return -1;
    }

    @Override
    public boolean hasFluid(FluidStack aFluid) {
        if (aFluid == null) return false;
        for (FluidStack tFluid : mStoredFluid) {
            if (aFluid.isFluidEqual(tFluid)) return true;
        }
        return false;
    }

    @Override
    public int getFluidSlot(FluidStack tFluid) {
        if (tFluid == null) return -1;
        for (int i = 0; i < mStoredFluid.length; i++) {
            if (tFluid.equals(mStoredFluid[i])) return i;
        }
        return -1;
    }

    @Override
    public int getFluidAmount(FluidStack tFluid) {
        int tSlot = getFluidSlot(tFluid);
        if (tSlot != -1) {
            return mStoredFluid[tSlot].amount;
        }
        return 0;
    }

    @Override
    public void setFluid(FluidStack aFluid, int aSlot) {
        if (aSlot < 0 || aSlot >= getMaxType()) return;
        mStoredFluid[aSlot] = aFluid;
    }

    @Override
    public void addFluid(FluidStack aFluid, int aSlot) {
        if (aSlot < 0 || aSlot >= getMaxType()) return;
        if (aFluid.equals(mStoredFluid[aSlot])) mStoredFluid[aSlot].amount += aFluid.amount;
        if (mStoredFluid[aSlot] == null) mStoredFluid[aSlot] = aFluid.copy();
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            mFluid = getFluid();
        }
        super.onPreTick(aBaseMetaTileEntity, aTick);
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

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidTankInfo[] FTI = new FluidTankInfo[getMaxType()];
        for (int i = 0; i < getMaxType(); i++) {
            FTI[i] = new FluidTankInfo(mStoredFluid[i], mCapacityPer);
        }
        return FTI;
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

}
