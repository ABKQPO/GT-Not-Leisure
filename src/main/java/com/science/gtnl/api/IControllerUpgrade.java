package com.science.gtnl.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.reavaritia.utils.item.ToolHelper;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GTUtility;

public interface IControllerUpgrade {

    int UPGRADE_WINDOW_ID = 11;

    ItemStack[] getUpgradeRequiredItems();

    ItemStack[] getStoredUpgradeWindowItems();

    int[] getUpgradePaidCosts();

    ItemStackHandler getUpgradeInputSlotHandler();

    boolean isUpgradeConsumed();

    void setUpgradeConsumed(boolean consumed);

    default int getUpgradeProgress() {
        return 0;
    }

    default void setUpgradeProgress(int progress) {}

    default void dropStoredUpgradeItems(IGregTechTileEntity gtTE) {
        if (gtTE == null) return;
        if (gtTE.isClientSide()) return;

        ItemStack[] items = getStoredUpgradeWindowItems();
        if (items == null) return;

        World world = gtTE.getWorld();
        double x = gtTE.getXCoord() + 0.5;
        double y = gtTE.getYCoord() + 0.5;
        double z = gtTE.getZCoord() + 0.5;

        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];

            if (stack != null && stack.stackSize > 0) {
                ToolHelper.dropItem(stack.copy(), world, x, y, z);
                items[i] = null;
            }
        }
    }

    default void saveUpgradeNBTData(NBTTagCompound aNBT) {
        NBTTagCompound upgradeWindowStorageNBTTag = new NBTTagCompound();
        boolean hasUpgradeItem = false;
        ItemStackHandler inputHandler = getUpgradeInputSlotHandler();
        ItemStack[] storedItems = getStoredUpgradeWindowItems();

        for (int storageIndex = 0; storageIndex < inputHandler.getSlots(); storageIndex++) {
            ItemStack itemStack = inputHandler.getStackInSlot(storageIndex);
            if (itemStack == null && storageIndex < storedItems.length) {
                itemStack = storedItems[storageIndex];
            }
            if (itemStack != null) {
                hasUpgradeItem = true;
                upgradeWindowStorageNBTTag
                    .setInteger(storageIndex + "stacksizeOfStoredUpgradeItems", itemStack.stackSize);
                aNBT.setTag(storageIndex + "storedUpgradeItem", itemStack.writeToNBT(new NBTTagCompound()));
            }
        }

        if (hasUpgradeItem) {
            aNBT.setTag("upgradeWindowStorage", upgradeWindowStorageNBTTag);
        }

        int[] paidCosts = getUpgradePaidCosts();
        if (paidCosts != null && paidCosts.length > 0) {
            NBTTagCompound paidCostTag = new NBTTagCompound();
            boolean hasCost = false;

            for (int i = 0; i < paidCosts.length; i++) {
                if (paidCosts[i] != 0) {
                    hasCost = true;
                    paidCostTag.setInteger("cost" + i, paidCosts[i]);
                }
            }

            if (hasCost) {
                aNBT.setTag("paidCosts", paidCostTag);
            }
        }

        if (isUpgradeConsumed()) {
            aNBT.setBoolean("upgradeConsumed", true);
        }
    }

    default void loadUpgradeNBTData(NBTTagCompound aNBT) {
        NBTTagCompound tempItemTag = aNBT.getCompoundTag("upgradeWindowStorage");
        ItemStack[] storedItems = getStoredUpgradeWindowItems();
        for (int index = 0; index < storedItems.length; index++) {
            int stackSize = tempItemTag.getInteger(index + "stacksizeOfStoredUpgradeItems");
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag(index + "storedUpgradeItem"));
            if (itemStack != null) {
                storedItems[index] = itemStack.splitStack(stackSize);
            }
        }

        NBTTagCompound paidCostTag = aNBT.getCompoundTag("paidCosts");
        int[] paidCosts = getUpgradePaidCosts();
        for (int i = 0; i < paidCosts.length; i++) {
            paidCosts[i] = paidCostTag.getInteger("cost" + i);
        }

        setUpgradeConsumed(aNBT.getBoolean("upgradeConsumed"));
    }

    int getGUIWidth();

    int getGUIHeight();

    int getGUIColorization();

    default int getUpgradeWindowId() {
        return UPGRADE_WINDOW_ID;
    }

    default int getPreviewUpgradeWindowId() {
        return getPreviewUpgradeWindowId(1);
    }

    default int getPreviewUpgradeWindowId(int previewLevel) {
        return UPGRADE_WINDOW_ID + previewLevel;
    }

    String getUpgradeButtonTooltip();

    default boolean isUpgradeButtonEnabled() {
        return true;
    }

    default int getUpgradeInputSlotsPerRow() {
        return 4;
    }

    default int getUpgradeCostItemsPerRow() {
        return 5;
    }

    default int getConsumeWindowWidth() {
        return Math.min(getUpgradeRequiredItems().length, getUpgradeCostItemsPerRow()) * 36
            + getUpgradeCostItemsPerRow() * 18;
    }

    default int getConsumeWindowHeight() {
        int rows = (int) Math.ceil(getUpgradeRequiredItems().length / (double) getUpgradeCostItemsPerRow());
        int slotRows = (int) Math.ceil(getUpgradeInputSlotHandler().getSlots() / (double) getUpgradeInputSlotsPerRow());
        return 60 + Math.max(0, Math.max(rows, slotRows) - 1) * 18;
    }

    default ItemStack[] getPreviewUpgradeRequiredItems() {
        return new ItemStack[0];
    }

    default int[] getPreviewUpgradePaidCosts() {
        return new int[getPreviewUpgradeRequiredItems().length];
    }

    default ItemStack[] getPreviewUpgradeRequiredItems(int previewLevel) {
        if (previewLevel == 1) return getPreviewUpgradeRequiredItems();
        return new ItemStack[0];
    }

    default int[] getPreviewUpgradePaidCosts(int previewLevel) {
        if (previewLevel == 1) return getPreviewUpgradePaidCosts();
        return new int[getPreviewUpgradeRequiredItems(previewLevel).length];
    }

    default int getMaxPreviewUpgradeLevel() {
        int maxLevel = 0;
        for (int level = 1; level <= 64; level++) {
            ItemStack[] previewItems = getPreviewUpgradeRequiredItems(level);
            if (previewItems == null || previewItems.length == 0) break;
            maxLevel = level;
        }
        return maxLevel;
    }

    default int getMaximumUpgradePreviewLevel() {
        return getMaxPreviewUpgradeLevel();
    }

    default int getUpgradeDisplayPageCount() {
        return getMaximumUpgradePreviewLevel() + 1;
    }

    default int getCurrentUpgradeDisplayPage() {
        return 0;
    }

    default ItemStack[] getUpgradeDisplayItems(int displayPage) {
        return displayPage == 0 ? getUpgradeRequiredItems() : getPreviewUpgradeRequiredItems(displayPage);
    }

    default int[] getUpgradeDisplayPaidCosts(int displayPage) {
        return displayPage == 0 ? getUpgradePaidCosts() : getPreviewUpgradePaidCosts(displayPage);
    }

    default int getMaxUpgradeRequiredItemCount() {
        int maxItemCount = 0;
        for (int displayPage = 0; displayPage < getUpgradeDisplayPageCount(); displayPage++) {
            ItemStack[] upgradeItems = getUpgradeDisplayItems(displayPage);
            if (upgradeItems != null) maxItemCount = Math.max(maxItemCount, upgradeItems.length);
        }
        return maxItemCount;
    }

    default boolean hasPreviewUpgradeWindow() {
        return getMaxPreviewUpgradeLevel() > 0;
    }

    default boolean tryConsumeItems() {
        boolean allFulfilled = true;

        for (int i = 0; i < getUpgradeRequiredItems().length; i++) {
            ItemStack required = getUpgradeRequiredItems()[i];
            if (required == null) continue;
            int requiredAmount = required.stackSize;
            int alreadyPaid = getUpgradePaidCosts()[i];

            if (alreadyPaid >= requiredAmount) continue;

            int remainingToPay = requiredAmount - alreadyPaid;

            for (int slot = 0; slot < getUpgradeInputSlotHandler().getSlots(); slot++) {
                ItemStack slotStack = getUpgradeInputSlotHandler().getStackInSlot(slot);
                if (slotStack == null) continue;

                if (GTUtility.areStacksEqual(slotStack, required)) {
                    int extract = Math.min(remainingToPay, slotStack.stackSize);
                    getUpgradeInputSlotHandler().extractItem(slot, extract, false);
                    alreadyPaid += extract;
                    remainingToPay -= extract;
                    getUpgradePaidCosts()[i] = alreadyPaid;
                    if (remainingToPay <= 0) break;
                }
            }

            if (alreadyPaid < requiredAmount) {
                allFulfilled = false;
            }
        }

        return allFulfilled;
    }
}
