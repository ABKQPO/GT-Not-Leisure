package com.science.gtnl.common.machine.hatch;

import static gregtech.api.util.GTUtility.areStacksEqual;
import static gregtech.api.util.GTUtility.isStackInvalid;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.api.IInfinitySlot;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.gui.widgets.PhantomItemButton;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.util.GTUtility;

public class HumongousOutputBus extends MTEHatchOutputBus implements IAddGregtechLogo, IInfinitySlot {

    public int itemSlotAmount;

    public HumongousOutputBus(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            new String[] { StatCollector.translateToLocal("Tooltip_HumongousOutputBus_00"), "",
                StatCollector.translateToLocal("Tooltip_HumongousOutputBus_02"),
                StatCollector.translateToLocal("Tooltip_HumongousOutputBus_03") },
            aTier * aTier);
        this.itemSlotAmount = aTier * aTier;
        mDescriptionArray[1] = StatCollector.translateToLocal("Tooltip_HumongousOutputBus_01") + itemSlotAmount;
    }

    public HumongousOutputBus(String name, int aTier, String[] description, ITexture[][][] textures) {
        super(name, aTier, aTier * aTier, description, textures);
        this.itemSlotAmount = aTier * aTier;
        mDescriptionArray[1] = StatCollector.translateToLocal("Tooltip_HumongousOutputBus_01") + itemSlotAmount;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new HumongousOutputBus(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        if (mInventory != null && mInventory.length > 0) {
            NBTTagList itemList = new NBTTagList();
            for (short i = 0; i < mInventory.length; i++) {
                ItemStack stack = mInventory[i];
                if (stack != null && stack.stackSize > 0) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    HumongousDualInputHatch.writeItemStackToNBT(itemTag, stack, i);
                    itemList.appendTag(itemTag);
                }
            }
            aNBT.setTag("Inventory", itemList);
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
                    mInventory[slot] = HumongousDualInputHatch.readItemStackFromNBT(itemTag);
                }
            }
        }
    }

    @Override
    public boolean storePartial(ItemStack stack, boolean simulate) {
        markDirty();

        if (lockedItem != null && !lockedItem.isItemEqual(stack)) return false;

        int invLength = mInventory.length;

        for (int i = 0; i < invLength && stack.stackSize > 0; i++) {
            @Nullable
            ItemStack slot = mInventory[i];

            // the slot has an item and the stacks can't be merged; ignore it
            if (!isStackInvalid(slot) && !areStacksEqual(slot, stack)) continue;

            int inSlot = slot == null ? 0 : slot.stackSize;

            int toInsert = Math.min(Math.min(getInventoryStackLimit(), Integer.MAX_VALUE - inSlot), stack.stackSize);

            if (toInsert == 0) continue;

            if (!simulate) {
                // if the slot is invalid create an empty stack in it
                if (isStackInvalid(slot)) mInventory[i] = slot = stack.splitStack(0);

                slot.stackSize += toInsert;
            }

            stack.stackSize -= toInsert;
        }

        return stack.stackSize == 0;
    }

    @Override
    public int getInventoryStackLimit() {
        return Integer.MAX_VALUE;
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
                if (slotIndex <= itemSlotAmount) {
                    builder.widget(
                        SlotWidget.phantom(inventoryHandler, slotIndex)
                            .disableInteraction()
                            .setBackground(ModularUITextures.ITEM_SLOT)
                            .setPos(centerX + col * 18 + 14, centerY + row * 18));
                }
            }
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

        builder.widget(
            new PhantomItemButton(this).setPos(169 + 4 * (mTier - 1) + mTier / 2, 83 + 14 * (mTier - 1))
                .setBackground(PhantomItemButton.FILTER_BACKGROUND));

        addGregTechLogo(builder);
    }

    @Override
    public int getGUIWidth() {
        return super.getGUIWidth() + 9 * (mTier - 1);
    }

    @Override
    public int getGUIHeight() {
        return super.getGUIHeight() + 14 * (mTier - 1);
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
        base.markDirty();
    }
}
