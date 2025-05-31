package com.science.gtnl.common.machine.hatch;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.Utils.item.ItemUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;

public class DualInputHatch extends MTEHatchInputBus implements IAddUIWidgets, IDualInputHatch, IAddGregtechLogo {

    private final FluidStack[] mStoredFluid;
    private final FluidStackTank[] fluidTanks;
    public final int mCapacityPer;
    public final int itemSlotAmount;

    private static final String TEXTURE_OVERLAY_DUAL_HATCH = RESOURCE_ROOT_ID + ":" + "iconsets/OVERLAY_DUAL_HATCH";
    private static Textures.BlockIcons.CustomIcon face;

    public static class Inventory implements IDualInputInventory {

        private final ItemStack[] itemInventory;
        private final FluidStack[] fluidInventory;

        public Inventory(ItemStack[] items, FluidStack[] fluid) {
            itemInventory = items;
            fluidInventory = fluid;
        }

        @Override
        public ItemStack[] getItemInputs() {
            if (isEmpty()) return new ItemStack[0];
            return Arrays.stream(itemInventory)
                .filter(Objects::nonNull)
                .toArray(ItemStack[]::new);
        }

        @Override
        public FluidStack[] getFluidInputs() {
            if (isEmpty()) return new FluidStack[0];
            return Arrays.stream(fluidInventory)
                .filter(Objects::nonNull)
                .toArray(FluidStack[]::new);
        }

        public boolean isEmpty() {
            if (itemInventory != null) {
                for (int i = 0; i < itemInventory.length - 1; i++) {
                    if (itemInventory[i] != null && itemInventory[i].stackSize > 0) {
                        return false;
                    }
                }
            }
            if (fluidInventory != null) {
                for (FluidStack fluid : fluidInventory) {
                    if (fluid != null && fluid.amount > 0) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    Inventory inventory;

    public DualInputHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            aTier * aTier + 1,
            new String[] { StatCollector.translateToLocal("Tooltip_DualInputHatch_00"), "", "" });
        this.mStoredFluid = new FluidStack[aTier];
        this.fluidTanks = new FluidStackTank[aTier];
        this.mCapacityPer = getCapacityPerTank(aTier);
        this.itemSlotAmount = aTier * aTier + 1;
        mDescriptionArray[1] = StatCollector.translateToLocal("Tooltip_DualInputHatch_01")
            + GTUtility.formatNumbers(itemSlotAmount - 1);
        mDescriptionArray[2] = StatCollector.translateToLocal("Tooltip_DualInputHatch_02_00")
            + GTUtility.formatNumbers(aTier)
            + StatCollector.translateToLocal("Tooltip_DualInputHatch_02_01")
            + GTUtility.formatNumbers(mCapacityPer)
            + "L";

        for (int i = 0; i < aTier; i++) {
            final int index = i;
            this.fluidTanks[i] = new FluidStackTank(
                () -> mStoredFluid[index],
                fluid -> mStoredFluid[index] = fluid,
                mCapacityPer);
        }
        this.inventory = new Inventory(mInventory, mStoredFluid);
    }

    public DualInputHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aTier * aTier + 1, aDescription, aTextures);
        this.mStoredFluid = new FluidStack[aTier];
        this.fluidTanks = new FluidStackTank[aTier];
        this.mCapacityPer = getCapacityPerTank(aTier);
        this.itemSlotAmount = aTier * aTier + 1;
        mDescriptionArray[1] = StatCollector.translateToLocal("Tooltip_DualInputHatch_01")
            + GTUtility.formatNumbers(itemSlotAmount - 1);
        mDescriptionArray[2] = StatCollector.translateToLocal("Tooltip_DualInputHatch_02_00")
            + GTUtility.formatNumbers(aTier)
            + StatCollector.translateToLocal("Tooltip_DualInputHatch_02_01")
            + GTUtility.formatNumbers(mCapacityPer)
            + "L";

        for (int i = 0; i < aTier; i++) {
            final int index = i;
            this.fluidTanks[i] = new FluidStackTank(
                () -> mStoredFluid[index],
                fluid -> mStoredFluid[index] = fluid,
                mCapacityPer);
        }
        this.inventory = new Inventory(mInventory, mStoredFluid);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new DualInputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public ItemStack[] getSharedItems() {
        return null;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (mStoredFluid != null) {
            for (int i = 0; i < getMaxType(); i++) {
                if (mStoredFluid[i] != null)
                    aNBT.setTag("mFluid" + i, mStoredFluid[i].writeToNBT(new NBTTagCompound()));
            }
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (mStoredFluid != null) {
            for (int i = 0; i < getMaxType(); i++) {
                if (aNBT.hasKey("mFluid" + i)) {
                    mStoredFluid[i] = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("mFluid" + i));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        super.registerIcons(aBlockIconRegister);
        face = new Textures.BlockIcons.CustomIcon(TEXTURE_OVERLAY_DUAL_HATCH);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(face) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(face) };
    }

    @Override
    public FluidStack getFluid() {
        for (FluidStack tFluid : mStoredFluid) {
            if (tFluid != null && tFluid.amount > 0) return tFluid;
        }
        return null;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return super.allowPullStack(aBaseMetaTileEntity, aIndex, side, aStack);
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return super.allowPutStack(aBaseMetaTileEntity, aIndex, side, aStack);
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

    public int getFirstEmptySlot() {
        for (int i = 0; i < getMaxType(); i++) {
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
        for (int i = 0; i < getMaxType(); i++) {
            if (tFluid.isFluidEqual(mStoredFluid[i])) return i;
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
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            mFluid = getFluid();
        }
        super.onPreTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public boolean justUpdated() {
        return false;
    }

    @Override
    public Iterator<? extends IDualInputInventory> inventories() {
        return Arrays.stream(new Inventory[] { inventory })
            .filter(Objects::nonNull)
            .iterator();
    }

    @Override
    public Optional<IDualInputInventory> getFirstNonEmptyInventory() {
        if (!inventory.isEmpty()) {
            return Optional.of(inventory);
        }
        return Optional.empty();
    }

    @Override
    public boolean supportsFluids() {
        return true;
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public int getCircuitSlot() {
        return itemSlotAmount - 1;
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
        FluidStack nowFluid = getFluid();
        if (nowFluid == null) return null;
        else return drain(ForgeDirection.UNKNOWN, new FluidStack(nowFluid.getFluid(), maxDrain), doDrain);
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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (aBaseMetaTileEntity.isServerSide()) {
            updateSlots();
        }
        super.onPostTick(aBaseMetaTileEntity, aTimer);
    }

    public void updateSlots() {
        if (mInventory != null) {
            for (int i = 0; i < mInventory.length - 1; i++)
                if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null;
        }
        if (mStoredFluid != null) {
            for (int i = 0; i < getMaxType(); i++) {
                if (mStoredFluid[i] != null && mStoredFluid[i].amount <= 0) mStoredFluid[i] = null;
            }
        }
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidTankInfo[] FTI = new FluidTankInfo[getMaxType()];
        for (int i = 0; i < getMaxType(); i++) {
            FTI[i] = new FluidTankInfo(mStoredFluid[i], mCapacityPer);
        }
        return FTI;
    }

    public int getMaxType() {
        return mStoredFluid.length;
    }

    public static int getCapacityPerTank(int aTier) {
        return (int) Math.pow(2, aTier - 1) * 8000;
    }

    @Override
    public void addUIWidgets(ModularWindow.@NotNull Builder builder, UIBuildContext buildContext) {
        final int itemColumns = Math.max(1, mTier);
        final int itemRows = Math.max(1, mTier);

        final int totalWidth = 9 * itemColumns + 36;
        final int totalHeight = 5 * itemRows + 81;
        final int centerX = (176 - totalWidth) / 2;
        final int centerY = (166 - totalHeight) / 2;

        // 物品槽布局
        for (int row = 0; row < itemRows; row++) {
            for (int col = 0; col < itemColumns; col++) {
                int slotIndex = row * itemColumns + col;
                if (slotIndex < itemSlotAmount - 1) { // 减去电路槽位
                    builder.widget(
                        new SlotWidget(inventoryHandler, slotIndex).setBackground(ModularUITextures.ITEM_SLOT)
                            .setPos(centerX + col * 18 + 5, centerY + row * 18));
                }
            }
        }

        // 流体槽布局（使用mTier确定数量）
        for (int i = 0; i < mTier; i++) {
            builder.widget(
                new FluidSlotWidget(fluidTanks[i]).setBackground(ModularUITextures.FLUID_SLOT)
                    .setPos(centerX + 18 * itemColumns + 5, centerY + i * 18));
        }

        addGregTechLogo(builder);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(170 + 4 * (mTier - 1) + mTier / 2, 102 + 14 * (mTier - 1)));
    }

    @Override
    public int getCircuitSlotX() {
        return 170 + 4 * (mTier - 1) + mTier / 2;
    }

    @Override
    public int getCircuitSlotY() {
        return 84 + 14 * (mTier - 1);
    }

    @Override
    public int getGUIWidth() {
        return super.getGUIWidth() + 9 * (mTier - 1);
    }

    @Override
    public int getGUIHeight() {
        return super.getGUIHeight() + 14 * (mTier - 1);
    }
}
