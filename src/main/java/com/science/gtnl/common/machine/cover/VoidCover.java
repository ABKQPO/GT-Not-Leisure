package com.science.gtnl.common.machine.cover;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.forge.IItemHandlerModifiable;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.science.gtnl.api.IFluidsLockable;
import com.science.gtnl.utils.gui.VoidCoverUIFactory;

import gregtech.api.covers.CoverContext;
import gregtech.api.gui.modularui.CoverUIBuildContext;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IMachineProgress;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.util.GTUtility;
import gregtech.common.covers.CoverLegacyData;
import lombok.Getter;
import lombok.Setter;

public class VoidCover extends CoverLegacyData implements IFluidsLockable {

    @Getter
    @Setter
    public boolean isInputMode = false;

    public ItemStack[] lockedItems = new ItemStack[20];
    public IItemHandlerModifiable lockedInventoryHandler = new ItemStackHandler(lockedItems);

    public String[] lockedFluidNames = new String[20];

    public VoidCover(CoverContext context) {
        super(context);
    }

    @Override
    public boolean isRedstoneSensitive(long aTimer) {
        return false;
    }

    @Override
    public boolean allowsCopyPasteTool() {
        return false;
    }

    @Override
    public void doCoverThings(byte aInputRedstone, long aTimer) {
        if (aTimer % getTickRate() == 0) {
            ICoverable coverable = coveredTile.get();
            if (coverable instanceof IMachineProgress machineProgress) {
                if (machineProgress.isAllowedToWork() && machineProgress instanceof BaseMetaTileEntity baseTile) {
                    if (baseTile.getMetaTileEntity() instanceof MTEBasicMachine basicMachine) {
                        removeLockedItemsFromMachine(basicMachine);
                        removeLockedFluidsFromMachine(basicMachine);
                        basicMachine.markDirty();
                    } else if (baseTile.getMetaTileEntity() instanceof MTEHatch hatch) {
                        removeLockedItemsFromHatch(hatch);
                        removeLockedFluidsFromHatch(hatch);
                        hatch.markDirty();
                    }
                }
            }
        }
    }

    public void removeLockedItemsFromHatch(MTEHatch hatch) {
        for (int i = 0; i < hatch.mInventory.length; i++) {
            ItemStack stackInMachine = hatch.mInventory[i];
            if (stackInMachine == null) continue;

            for (ItemStack locked : lockedItems) {
                if (locked == null) continue;

                if (GTUtility.areStacksEqual(stackInMachine, locked, false)) {
                    hatch.mInventory[i] = null;
                }
            }
        }
    }

    public void removeLockedFluidsFromHatch(MTEHatch hatch) {
        for (String locked : lockedFluidNames) {
            if (locked == null) continue;

            if (hatch instanceof MTEHatchMultiInput multiInput) {
                for (int i = 0; i < multiInput.getMaxType(); i++) {
                    if (multiInput.getFluid(i) != null && multiInput.getFluid(i)
                        .getFluid()
                        .getName()
                        .equals(locked)) {
                        multiInput.setFluid(null, i);
                    }
                }
            } else {
                if (hatch.getDrainableStack() != null && hatch.getDrainableStack()
                    .getFluid()
                    .getName()
                    .equals(locked)) {
                    hatch.setDrainableStack(null);
                    break;
                }
            }
        }
    }

    public void removeLockedItemsFromMachine(MTEBasicMachine basicMachine) {
        int[] slots = getSlotPositions(basicMachine);

        for (int slotIndex : slots) {
            ItemStack stackInMachine = basicMachine.mInventory[slotIndex];
            if (stackInMachine == null) continue;

            for (ItemStack locked : lockedItems) {
                if (locked == null) continue;

                if (GTUtility.areStacksEqual(stackInMachine, locked, false)) {
                    basicMachine.mInventory[slotIndex] = null;
                }
            }
        }
    }

    public void removeLockedFluidsFromMachine(MTEBasicMachine basicMachine) {
        for (String locked : lockedFluidNames) {
            if (locked == null) continue;

            if (isInputMode) {
                if (basicMachine.getFillableStack() != null && basicMachine.getFillableStack()
                    .getFluid()
                    .getName()
                    .equals(locked)) {
                    basicMachine.setFillableStack(null);
                    break;
                }
            } else {
                if (basicMachine.getDrainableStack() != null && basicMachine.getDrainableStack()
                    .getFluid()
                    .getName()
                    .equals(locked)) {
                    basicMachine.setDrainableStack(null);
                    break;
                }
            }
        }
    }

    public int[] getSlotPositions(MTEBasicMachine basicMachine) {
        int[] slots;
        if (isInputMode) {
            slots = new int[basicMachine.mInputSlotCount];
            for (int i = 0; i < basicMachine.mInputSlotCount; i++) {
                slots[i] = MTEBasicMachine.OTHER_SLOT_COUNT + i;
            }
        } else {
            slots = new int[basicMachine.mOutputItems.length];
            for (int i = 0; i < basicMachine.mOutputItems.length; i++) {
                slots[i] = MTEBasicMachine.OTHER_SLOT_COUNT + basicMachine.mInputSlotCount + i;
            }
        }
        return slots;
    }

    @Override
    public String getLockedFluidName() {
        return lockedFluidNames[0];
    }

    @Override
    public String[] getLockedFluidNames() {
        return lockedFluidNames;
    }

    @Override
    public String getLockedFluidNames(int index) {
        return lockedFluidNames[index];
    }

    @Override
    public void setLockedFluidName(String lockedFluidName) {
        this.lockedFluidNames[0] = lockedFluidName;
    }

    @Override
    public void setLockedFluidNames(String[] lockedFluidNames) {
        if (lockedFluidNames == null) return;
        this.lockedFluidNames = lockedFluidNames;
    }

    @Override
    public void setLockedFluidNames(int index, String lockedFluidName) {
        if (lockedFluidName == null) return;
        this.lockedFluidNames[index] = lockedFluidName;
    }

    @Override
    public void lockFluid(boolean lock) {
        if (!lock) {
            setLockedFluidName(null);
        }
    }

    @Override
    public void lockFluids(boolean lock, int index) {
        if (!lock && this.lockedFluidNames != null) {
            this.lockedFluidNames[index] = null;
        }
    }

    @Override
    public boolean isFluidLocked() {
        if (lockedFluidNames == null) return false;
        for (String s : lockedFluidNames) {
            if (s != null && !s.isEmpty()) return true;
        }
        return false;
    }

    @Override
    public boolean isFluidsLocked() {
        if (lockedFluidNames == null) return false;
        for (String s : lockedFluidNames) {
            if (s != null && !s.isEmpty()) return true;
        }
        return false;
    }

    @Override
    public boolean acceptsFluidLock(String name) {
        return true;
    }

    @Override
    public boolean acceptsFluidsLock(String[] name) {
        return true;
    }

    @Override
    public boolean acceptsFluidsLock(String name, int index) {
        return true;
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocal("VoidCover");
    }

    @Override
    public boolean alwaysLookConnected() {
        return true;
    }

    @Override
    public int getMinimumTickRate() {
        return 1;
    }

    @Override
    public boolean hasCoverGUI() {
        return true;
    }

    @Override
    public ModularWindow createWindow(CoverUIBuildContext buildContext) {
        return new VoidCoverUIFactory(buildContext).createWindow();
    }

    @Override
    public void readDataFromNbt(NBTBase nbt) {
        if (nbt instanceof NBTTagCompound compound) {
            this.coverData = compound.getInteger("CoverData");
            this.isInputMode = compound.getBoolean("isInputMode");

            if (compound.hasKey("LockedItems", Constants.NBT.TAG_LIST)) {
                NBTTagList itemList = compound.getTagList("LockedItems", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < itemList.tagCount(); i++) {
                    NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
                    int slot = itemTag.getByte("Slot");
                    if (slot >= 0 && slot < lockedItems.length) {
                        lockedItems[slot] = ItemStack.loadItemStackFromNBT(itemTag);
                    }
                }
            }

            if (compound.hasKey("LockedFluidNames", Constants.NBT.TAG_LIST)) {
                NBTTagList fluidList = compound.getTagList("LockedFluidNames", Constants.NBT.TAG_STRING);
                lockedFluidNames = new String[fluidList.tagCount()];
                for (int i = 0; i < fluidList.tagCount(); i++) {
                    String s = fluidList.getStringTagAt(i);
                    lockedFluidNames[i] = GTUtility.isStringInvalid(s) ? null : s;
                }
            } else {
                lockedFluidNames = new String[20];
            }

        } else if (nbt instanceof NBTTagInt tagInt) {
            this.coverData = tagInt.func_150287_d();
        }
    }

    @Override
    public @NotNull NBTBase saveDataToNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("CoverData", this.coverData);
        compound.setBoolean("isInputMode", this.isInputMode);

        NBTTagList lockedItemList = new NBTTagList();
        for (int i = 0; i < lockedItems.length; i++) {
            if (lockedItems[i] == null) continue;
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) i);
            lockedItems[i].writeToNBT(itemTag);
            lockedItemList.appendTag(itemTag);
        }
        compound.setTag("LockedItems", lockedItemList);

        if (lockedFluidNames != null && lockedFluidNames.length > 0) {
            NBTTagList fluidList = new NBTTagList();
            for (String name : lockedFluidNames) {
                if (name == null) name = "";
                fluidList.appendTag(new NBTTagString(name));
            }
            compound.setTag("LockedFluidNames", fluidList);
        }

        return compound;
    }
}
