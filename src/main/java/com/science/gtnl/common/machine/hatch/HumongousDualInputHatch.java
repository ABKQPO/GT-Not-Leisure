package com.science.gtnl.common.machine.hatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.api.mixinHelper.ISkipStackSizeCheck;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtility;

public class HumongousDualInputHatch extends DualInputHatch implements ISkipStackSizeCheck {

    public HumongousDualInputHatch(int id, String name, String nameRegional, int aTier) {
        super(id, name, nameRegional, aTier);
        this.mStoredFluid = new FluidStack[aTier];
        this.fluidTanks = new FluidStackTank[aTier];
        this.mCapacityPer = Integer.MAX_VALUE;
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
        this.disableSort = true;
    }

    public HumongousDualInputHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        this.mStoredFluid = new FluidStack[aTier];
        this.fluidTanks = new FluidStackTank[aTier];
        this.mCapacityPer = Integer.MAX_VALUE;
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
        this.disableSort = true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HumongousDualInputHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int getInventoryStackLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        if (mInventory != null && mInventory.length > 0) {
            NBTTagList itemList = new NBTTagList();
            for (short i = 0; i < mInventory.length; i++) {
                ItemStack stack = mInventory[i];
                if (stack != null) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    writeItemStackToNBT(itemTag, stack, i);
                    itemList.appendTag(itemTag);
                }
            }
            aNBT.setTag("Inventory", itemList);
        }

        if (mStoredFluid != null && mStoredFluid.length > 0) {
            for (short i = 0; i < mStoredFluid.length; i++) {
                FluidStack fluid = mStoredFluid[i];
                if (fluid != null) {
                    NBTTagCompound fluidTag = new NBTTagCompound();
                    fluid.writeToNBT(fluidTag);
                    aNBT.setTag("mFluid" + i, fluidTag);
                }
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        if (mInventory != null && mInventory.length > 0) {
            NBTTagList itemList = new NBTTagList();
            for (short i = 0; i < mInventory.length; i++) {
                ItemStack stack = mInventory[i];
                if (stack != null) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    writeItemStackToNBT(itemTag, stack, i);
                    itemList.appendTag(itemTag);
                }
            }
            aNBT.setTag("Inventory", itemList);
        }

        if (mStoredFluid != null && mStoredFluid.length > 0) {
            for (short i = 0; i < mStoredFluid.length; i++) {
                FluidStack fluid = mStoredFluid[i];
                if (fluid != null) {
                    NBTTagCompound fluidTag = new NBTTagCompound();
                    fluid.writeToNBT(fluidTag);
                    aNBT.setTag("mFluid" + i, fluidTag);
                }
            }
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        if (aNBT.hasKey("Inventory")) {
            NBTTagList itemList = aNBT.getTagList("Inventory", 10);
            for (int i = 0; i < itemList.tagCount(); i++) {
                NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
                int slot = itemTag.getInteger("IntSlot");
                if (slot >= 0 && slot < mInventory.length) {
                    mInventory[slot] = readItemStackFromNBT(itemTag);
                }
            }
        }

        if (mStoredFluid != null && mStoredFluid.length > 0) {
            for (short i = 0; i < mStoredFluid.length; i++) {
                String key = "mFluid" + i;
                if (aNBT.hasKey(key)) {
                    mStoredFluid[i] = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag(key));
                }
            }
        }
    }

    public static void writeItemStackToNBT(NBTTagCompound tag, ItemStack stack, int slot) {
        if (stack == null) return;
        tag.setInteger("id", Item.getIdFromItem(stack.getItem()));
        tag.setInteger("Damage", stack.getItemDamage());
        tag.setInteger("Count", stack.stackSize);
        tag.setInteger("IntSlot", slot);

        if (stack.stackTagCompound != null) {
            tag.setTag("tag", stack.stackTagCompound);
        }
    }

    public static ItemStack readItemStackFromNBT(NBTTagCompound tag) {
        if (!tag.hasKey("id") || !tag.hasKey("Count") || !tag.hasKey("Damage")) return null;

        int id = tag.getInteger("id");
        int meta = tag.getInteger("Damage");
        long count = tag.getInteger("Count");

        if (count < 0) return null;

        ItemStack stack = new ItemStack(Item.getItemById(id), Math.toIntExact(count), meta);

        if (tag.hasKey("tag")) {
            stack.stackTagCompound = tag.getCompoundTag("tag");
        }
        return stack;
    }

    @Override
    public void fillStacksIntoFirstSlots() {
        final int L = mInventory.length - 1;
        HashMap<GTUtility.ItemId, Integer> slots = new HashMap<>(L);
        HashMap<GTUtility.ItemId, ItemStack> stacks = new HashMap<>(L);
        List<GTUtility.ItemId> order = new ArrayList<>(L);
        List<Integer> validSlots = new ArrayList<>(L);
        for (int i = 0; i < L; i++) {
            if (!isValidSlot(i)) continue;
            validSlots.add(i);
            ItemStack s = mInventory[i];
            if (s == null) continue;
            GTUtility.ItemId sID = GTUtility.ItemId.createNoCopy(s);
            slots.merge(sID, s.stackSize, Integer::sum);
            if (!stacks.containsKey(sID)) stacks.put(sID, s);
            order.add(sID);
            mInventory[i] = null;
        }
        int slotindex = 0;
        for (GTUtility.ItemId sID : order) {
            int toSet = slots.get(sID);
            if (toSet == 0) continue;
            int slot = validSlots.get(slotindex);
            slotindex++;
            mInventory[slot] = stacks.get(sID)
                .copy();
            toSet = Math.toIntExact(toSet);
            mInventory[slot].stackSize = toSet;
            slots.merge(sID, toSet, (a, b) -> a - b);
        }
    }

    @Override
    public void onBlockDestroyed() {
        Arrays.fill(mInventory, null);
        Arrays.fill(mStoredFluid, null);
        super.onBlockDestroyed();
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        final int itemColumns = Math.max(1, mTier);
        final int itemRows = Math.max(1, mTier);

        final int totalWidth = 9 * itemColumns + 36;
        final int totalHeight = 5 * itemRows + 81;
        final int centerX = (176 - totalWidth) / 2;
        final int centerY = (166 - totalHeight) / 2;

        for (int row = 0; row < itemRows; row++) {
            for (int col = 0; col < itemColumns; col++) {
                int slotIndex = row * itemColumns + col;
                if (slotIndex < itemSlotAmount - 1) {
                    builder.widget(
                        SlotWidget.phantom(inventoryHandler, slotIndex)
                            .disableInteraction()
                            .setBackground(ModularUITextures.ITEM_SLOT)
                            .setPos(centerX + col * 18 + 5, centerY + row * 18));
                }
            }
        }

        for (int i = 0; i < mTier; i++) {
            builder.widget(
                new FluidSlotWidget(fluidTanks[i]).setBackground(ModularUITextures.FLUID_SLOT)
                    .setPos(centerX + 18 * itemColumns + 5, centerY + i * 18));
        }

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (clickData.mouseButton == 0) {
                refundAll();
            }
        })
            .setPlayClickSound(true)
            .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_EXPORT)
            .addTooltips(ImmutableList.of(StatCollector.translateToLocal("Button_Tooltip_HumongousDualInputHatch_00")))
            .setSize(16, 16)
            .setPos(170 + 4 * (mTier - 1) + mTier / 2, 102 + 14 * (mTier - 1)));

        addGregTechLogo(builder);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(169 + 4 * (mTier - 1) + mTier / 2, 120 + 14 * (mTier - 1)));
    }

    public void refundAll() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null) return;

        ForgeDirection front = base.getFrontFacing();
        TileEntity targetTile = base.getTileEntityAtSide(front);

        for (int i = 0; i < mInventory.length; i++) {
            ItemStack stack = mInventory[i];
            if (stack != null && stack.stackSize > 0) {
                ItemStack remaining = stack.copy();

                if (targetTile != null) {
                    if (targetTile instanceof ISidedInventory sidedInv) {
                        int[] slots = sidedInv.getAccessibleSlotsFromSide(
                            front.getOpposite()
                                .ordinal());
                        for (int slot : slots) {
                            if (sidedInv.canInsertItem(
                                slot,
                                remaining,
                                front.getOpposite()
                                    .ordinal())) {
                                ItemStack slotStack = sidedInv.getStackInSlot(slot);
                                int maxStack = slotStack != null ? slotStack.getMaxStackSize()
                                    : sidedInv.getInventoryStackLimit();

                                while (remaining != null && remaining.stackSize > 0) {
                                    if (slotStack == null) {
                                        int toMove = Math.min(maxStack, remaining.stackSize);
                                        ItemStack copy = remaining.copy();
                                        copy.stackSize = toMove;
                                        sidedInv.setInventorySlotContents(slot, copy);
                                        remaining.stackSize -= toMove;
                                        stack.stackSize -= toMove;
                                        mInventory[i] = stack;
                                        if (remaining.stackSize <= 0) remaining = null;
                                        break;
                                    } else if (GTUtility.areStacksEqual(slotStack, remaining, true)) {
                                        int space = maxStack - slotStack.stackSize;
                                        if (space > 0) {
                                            int move = Math.min(space, remaining.stackSize);
                                            slotStack.stackSize += move;
                                            remaining.stackSize -= move;
                                            stack.stackSize -= move;
                                            mInventory[i] = stack;
                                            if (remaining.stackSize <= 0) remaining = null;
                                        } else break;
                                    } else break;
                                }
                                if (remaining == null) break;
                            }
                        }
                    } else if (targetTile instanceof IInventory inv) {
                        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
                            ItemStack slotStack = inv.getStackInSlot(slot);
                            int maxStack = slotStack != null ? slotStack.getMaxStackSize()
                                : inv.getInventoryStackLimit();

                            while (remaining != null && remaining.stackSize > 0) {
                                if (slotStack == null) {
                                    int toMove = Math.min(maxStack, remaining.stackSize);
                                    ItemStack copy = remaining.copy();
                                    copy.stackSize = toMove;
                                    inv.setInventorySlotContents(slot, copy);
                                    remaining.stackSize -= toMove;
                                    stack.stackSize -= toMove;
                                    mInventory[i] = stack;
                                    if (remaining.stackSize <= 0) remaining = null;
                                    break;
                                } else if (GTUtility.areStacksEqual(slotStack, remaining, true)) {
                                    int space = maxStack - slotStack.stackSize;
                                    if (space > 0) {
                                        int move = Math.min(space, remaining.stackSize);
                                        slotStack.stackSize += move;
                                        remaining.stackSize -= move;
                                        stack.stackSize -= move;
                                        mInventory[i] = stack;
                                        if (remaining.stackSize <= 0) remaining = null;
                                    } else break;
                                } else break;
                            }
                            if (remaining == null) break;
                        }
                    }
                }

                if (stack != null && stack.stackSize > 0) {
                    int xBlock = base.getXCoord() + front.offsetX;
                    int yBlock = base.getYCoord() + front.offsetY;
                    int zBlock = base.getZCoord() + front.offsetZ;

                    if (base.getWorld()
                        .isAirBlock(xBlock, yBlock, zBlock)) {
                        double x = xBlock + 0.5;
                        double y = yBlock + 0.5;
                        double z = zBlock + 0.5;
                        base.getWorld()
                            .spawnEntityInWorld(new EntityItem(base.getWorld(), x, y, z, stack));
                        mInventory[i] = null;
                    }
                }

                if (stack.stackSize <= 0) mInventory[i] = null;
            }
        }

        if (targetTile instanceof IFluidHandler fluidHandler) {
            for (int i = 0; i < mStoredFluid.length; i++) {
                FluidStack fluid = mStoredFluid[i];
                if (fluid != null && fluid.amount > 0) {
                    int filled = fluidHandler.fill(front.getOpposite(), fluid.copy(), true);
                    if (filled > 0) {
                        fluid.amount -= filled;
                        if (fluid.amount <= 0) mStoredFluid[i] = null;
                    }
                }
            }
        }
        updateSlots();
        base.markDirty();
    }
}
