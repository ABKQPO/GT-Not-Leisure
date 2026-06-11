package com.science.gtnl.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.reavaritia.utils.item.ToolHelper;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.EternalGregTechWorkshopUI;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.gui.modularui.GUITextureSet;
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

    default Pos2d getUpgradeButtonPos() {
        return new Pos2d(174, 110);
    }

    GUITextureSet getGUITextureSet();

    default int getUpgradeWindowId() {
        return UPGRADE_WINDOW_ID;
    }

    default int getPreviewUpgradeWindowId() {
        return getPreviewUpgradeWindowId(1);
    }

    default int getPreviewUpgradeWindowId(int previewLevel) {
        return getUpgradeWindowId() + previewLevel;
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

    @Deprecated
    default ModularWindow createConsumeWindow(EntityPlayer player) {
        // TODO: Remove this mui1 fallback after all controller upgrade windows are fully ported to mui2.
        return createUpgradeWindow(player, 0);
    }

    @Deprecated
    default ModularWindow createPreviewConsumeWindow(EntityPlayer player) {
        // TODO: Remove this mui1 fallback after all controller upgrade windows are fully ported to mui2.
        return createUpgradeWindow(player, 1);
    }

    @Deprecated
    default ModularWindow createPreviewConsumeWindow(EntityPlayer player, int previewLevel) {
        // TODO: Remove this mui1 fallback after all controller upgrade windows are fully ported to mui2.
        return createUpgradeWindow(player, previewLevel);
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

    default boolean hasPreviewUpgradeWindow() {
        return getMaxPreviewUpgradeLevel() > 0;
    }

    @Deprecated
    default ModularWindow createUpgradeWindow(EntityPlayer player, int previewLevel) {
        // TODO: Remove this mui1 fallback after all controller upgrade windows are fully ported to mui2.
        final boolean previewMode = previewLevel > 0;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();

        ItemStackHandler inputHandler = getUpgradeInputSlotHandler();
        int totalSlots = inputHandler.getSlots();
        int costPerRow = getUpgradeCostItemsPerRow();
        int inputSlotsPerRow = getUpgradeInputSlotsPerRow();
        int maxPreviewLevel = getMaxPreviewUpgradeLevel();

        int[] upgradePaidCosts = previewMode ? getPreviewUpgradePaidCosts(previewLevel) : getUpgradePaidCosts();
        ItemStack[] upgradeItems = previewMode ? getPreviewUpgradeRequiredItems(previewLevel)
            : getUpgradeRequiredItems();
        if (upgradePaidCosts == null) upgradePaidCosts = new int[0];
        if (upgradeItems == null) upgradeItems = new ItemStack[0];

        final int WIDTH = Math.min(upgradeItems.length, costPerRow) * 36 + costPerRow * 18;
        int rows = (int) Math.ceil(upgradeItems.length / (double) costPerRow);
        int slotRows = previewMode ? 0 : (int) Math.ceil(totalSlots / (double) inputSlotsPerRow);
        final int HEIGHT = 60 + Math.max(0, Math.max(rows, slotRows) - 1) * 18;

        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT)))
                .subtract(5, 0)
                .add(0, 4));

        if (!previewMode) {
            ItemStack[] storedItems = getStoredUpgradeWindowItems();
            for (int i = 0; i < totalSlots; i++) {
                if (i < storedItems.length && storedItems[i] != null) {
                    inputHandler.insertItem(i, storedItems[i], false);
                    storedItems[i] = null;
                }
            }
        }

        int slotX = 5 + Math.min(upgradeItems.length, costPerRow) * 36;

        if (!previewMode) {
            builder.widget(
                SlotGroup.ofItemHandler(inputHandler, inputSlotsPerRow)
                    .startFromSlot(0)
                    .endAtSlot(totalSlots - 1)
                    .phantom(false)
                    .background(getGUITextureSet().getItemSlot())
                    .build()
                    .setPos(slotX, 6));
        }

        for (int i = 0; i < upgradeItems.length; i++) {
            ItemStack stack = upgradeItems[i];
            int stackCost = i < upgradePaidCosts.length ? upgradePaidCosts[i] : 0;
            Widget costWidget = EternalGregTechWorkshopUI.createExtraCostWidget(stack, () -> stackCost);
            costWidget.setPos(5 + 36 * (i % costPerRow), 6 + 18 * (i / costPerRow));
            builder.widget(costWidget);
        }

        int costRows = Math.max(1, rows);
        int previousButtonX = 5;
        int nextButtonX = previousButtonX + 160;
        int switchButtonY = 6 + 18 * costRows + 2;

        final int targetPreviewLevel = previewLevel + 1;
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (previewLevel >= maxPreviewLevel) return;
            if (!widget.isClient()) {
                widget.getWindow()
                    .closeWindow();
                widget.getContext()
                    .openSyncedWindow(getPreviewUpgradeWindowId(targetPreviewLevel));
            }
        })
            .setBackground(
                (previewLevel < maxPreviewLevel) ? GTUITextures.BUTTON_STANDARD : GTUITextures.BUTTON_STANDARD_DISABLED,
                ModularUITextures.ARROW_RIGHT)
            .addTooltip(StatCollector.translateToLocal("gtnl.ui.controllerUpgrade.previewNext"))
            .setPos(nextButtonX, switchButtonY)
            .setSize(16, 16));

        final int previousWindowId = previewLevel == 1 ? getUpgradeWindowId()
            : getPreviewUpgradeWindowId(previewLevel - 1);
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!previewMode) return;
            if (!widget.isClient()) {
                widget.getWindow()
                    .closeWindow();
                widget.getContext()
                    .openSyncedWindow(previousWindowId);
            }
        })
            .setBackground(
                previewMode ? GTUITextures.BUTTON_STANDARD : GTUITextures.BUTTON_STANDARD_DISABLED,
                ModularUITextures.ARROW_LEFT)
            .addTooltip(StatCollector.translateToLocal("gtnl.ui.controllerUpgrade.backToCurrent"))
            .setPos(previousButtonX, switchButtonY)
            .setSize(16, 16));

        if (!previewMode) {
            builder.widget(new MultiChildWidget().addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
                if (!widget.isClient()) {
                    if (tryConsumeItems()) {
                        setUpgradeConsumed(true);
                        widget.getWindow()
                            .closeWindow();
                    } else {
                        EternalGregTechWorkshopUI.reopenWindow(widget, getUpgradeWindowId());
                    }
                }
            })
                .setPlayClickSound(true)
                .setBackground(GTUITextures.BUTTON_STANDARD)
                .setSize(WIDTH - 20, 20))
                .addChild(
                    new TextWidget(
                        StatCollector.translateToLocal("gt.blockmachines.multimachine.FOG.consumeUpgradeMats"))
                            .setTextAlignment(Alignment.Center)
                            .setScale(0.75f)
                            .setSize(WIDTH - 20, 20))
                .setPos(10, HEIGHT - 30)
                .setSize(WIDTH - 20, 20));
        }

        builder.widget(
            new ButtonWidget().setOnClick(
                (clickData, widget) -> {
                    if (!widget.isClient()) widget.getWindow()
                        .closeWindow();
                })
                .setBackground(ModularUITextures.VANILLA_BACKGROUND, new Text("x"))
                .setPos(WIDTH - 9, 0)
                .setSize(10, 10));

        return builder.build();
    }

    @Deprecated
    default void createUpgradeButton(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after all controller upgrade buttons are fully ported to mui2.
        buildContext.addSyncedWindow(getUpgradeWindowId(), this::createConsumeWindow);
        int maxPreviewLevel = getMaxPreviewUpgradeLevel();
        for (int level = 1; level <= maxPreviewLevel; level++) {
            final int previewLevel = level;
            buildContext.addSyncedWindow(
                getPreviewUpgradeWindowId(previewLevel),
                player -> createPreviewConsumeWindow(player, previewLevel));
        }
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::isUpgradeConsumed, this::setUpgradeConsumed));

        builder.widget(new ButtonWidget().setOnClick((click, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(getUpgradeWindowId());
            }
        })
            .setBackground(
                () -> new IDrawable[] {
                    isUpgradeConsumed() ? GTUITextures.BUTTON_STANDARD_PRESSED : GTUITextures.BUTTON_STANDARD,
                    GTUITextures.OVERLAY_BUTTON_ARROW_GREEN_UP })
            .addTooltip(getUpgradeButtonTooltip())
            .setTooltipShowUpDelay(5)
            .setPos(getUpgradeButtonPos())
            .setSize(16, 16));
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
