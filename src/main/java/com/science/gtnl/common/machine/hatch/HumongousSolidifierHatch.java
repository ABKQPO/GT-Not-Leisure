package com.science.gtnl.common.machine.hatch;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.forge.IItemHandlerModifiable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.Utils.item.ItemUtils;

import ggfab.GGItemList;
import gregtech.api.enums.ItemList;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSolidifier;

public class HumongousSolidifierHatch extends MTEHatchSolidifier implements IAddGregtechLogo {

    private final FluidStackTank[] fluidTanks;
    private final FluidStack[] mStoredFluid;
    static final int moldSlot = 2;
    public final int mCapacityPer = Integer.MAX_VALUE;
    static final ItemStack[] solidifierMolds = { ItemList.Shape_Mold_Bottle.get(1), ItemList.Shape_Mold_Plate.get(1),
        ItemList.Shape_Mold_Ingot.get(1), ItemList.Shape_Mold_Casing.get(1), ItemList.Shape_Mold_Gear.get(1),
        ItemList.Shape_Mold_Gear_Small.get(1), ItemList.Shape_Mold_Credit.get(1), ItemList.Shape_Mold_Nugget.get(1),
        ItemList.Shape_Mold_Block.get(1), ItemList.Shape_Mold_Ball.get(1), ItemList.Shape_Mold_Cylinder.get(1),
        ItemList.Shape_Mold_Anvil.get(1), ItemList.Shape_Mold_Arrow.get(1), ItemList.Shape_Mold_Rod.get(1),
        ItemList.Shape_Mold_Bolt.get(1), ItemList.Shape_Mold_Round.get(1), ItemList.Shape_Mold_Screw.get(1),
        ItemList.Shape_Mold_Ring.get(1), ItemList.Shape_Mold_Rod_Long.get(1), ItemList.Shape_Mold_Rotor.get(1),
        ItemList.Shape_Mold_Turbine_Blade.get(1), ItemList.Shape_Mold_Pipe_Tiny.get(1),
        ItemList.Shape_Mold_Pipe_Small.get(1), ItemList.Shape_Mold_Pipe_Medium.get(1),
        ItemList.Shape_Mold_Pipe_Large.get(1), ItemList.Shape_Mold_Pipe_Huge.get(1),
        ItemList.Shape_Mold_ToolHeadDrill.get(1),

        GGItemList.SingleUseFileMold.get(1), GGItemList.SingleUseWrenchMold.get(1),
        GGItemList.SingleUseCrowbarMold.get(1), GGItemList.SingleUseWireCutterMold.get(1),
        GGItemList.SingleUseHardHammerMold.get(1), GGItemList.SingleUseSoftMalletMold.get(1),
        GGItemList.SingleUseScrewdriverMold.get(1), GGItemList.SingleUseSawMold.get(1) };

    public HumongousSolidifierHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, 14);
        this.mStoredFluid = new FluidStack[9];
        fluidTanks = new FluidStackTank[9];
    }

    public HumongousSolidifierHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, 14, aDescription, aTextures);
        this.mStoredFluid = new FluidStack[9];
        fluidTanks = new FluidStackTank[9];
        for (int i = 0; i < 9; i++) {
            final int index = i;
            fluidTanks[i] = new FluidStackTank(
                () -> mStoredFluid[index],
                fluid -> mStoredFluid[index] = fluid,
                mCapacityPer);
        }
    }

    @Override
    public int getCapacityPerTank(int aTier, int aSlot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HumongousSolidifierHatch(mName, 14, mDescriptionArray, mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_HumongousSolidifierHatch_00"),
            StatCollector.translateToLocal("Tooltip_HumongousSolidifierHatch_01") };
    }

    @Override
    public boolean isItemValidForSlot(int aIndex, ItemStack aStack) {
        if (aIndex == moldSlot && aStack != null) {
            for (final ItemStack itemStack : solidifierMolds) {
                if (GTUtility.areStacksEqual(itemStack, aStack, true)) {
                    return true;
                }
            }
        } else if (aIndex != moldSlot) {
            return super.isItemValidForSlot(aIndex, aStack);
        }

        return false;
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

        builder.widget(
            new SlotWidget(new MoldSlot(inventoryHandler, moldSlot)).setShiftClickPriority(-1)
                .setPos(125, 35)
                .setBackground(getGUITextureSet().getItemSlot(), GTUITextures.OVERLAY_SLOT_MOLD)
                .setSize(18, 18));
    }

    @Override
    public FluidStack getFluid() {
        for (FluidStack tFluid : mStoredFluid) {
            if (tFluid != null && tFluid.amount > 0) return tFluid;
        }
        return null;
    }

    private class MoldSlot extends BaseSlot {

        public MoldSlot(IItemHandlerModifiable inventory, int index) {
            super(inventory, index);
        }

        @Override
        public boolean isItemValidPhantom(ItemStack stack) {
            return super.isItemValidPhantom(stack) && getBaseMetaTileEntity().isItemValidForSlot(getSlotIndex(), stack);
        }

    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
    }

    public int getMaxType() {
        return 9;
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

    @Override
    public boolean isValidSlot(int aIndex) {
        return aIndex >= 9;
    }
}
