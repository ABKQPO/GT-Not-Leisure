package com.science.gtnl.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

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
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.EternalGregTechWorkshopUI;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.gui.modularui.GUITextureSet;
import gregtech.api.util.GTUtility;

public interface IControllerUpgradeable {

    ItemStack[] getUpgradeRequiredItems();

    ItemStack[] getStoredUpgradeWindowItems();

    int[] getUpgradePaidCosts();

    ItemStackHandler getUpgradeInputSlotHandler();

    boolean isUpgradeConsumed();

    void setUpgradeConsumed(boolean consumed);

    default void saveUpgradeNBTData(NBTTagCompound aNBT) {
        NBTTagCompound upgradeWindowStorageNBTTag = new NBTTagCompound();
        int storageIndex = 0;
        for (ItemStack itemStack : getUpgradeInputSlotHandler().getStacks()) {
            if (itemStack != null) {
                upgradeWindowStorageNBTTag
                    .setInteger(storageIndex + "stacksizeOfStoredUpgradeItems", itemStack.stackSize);
                aNBT.setTag(storageIndex + "storedUpgradeItem", itemStack.writeToNBT(new NBTTagCompound()));
            }
            storageIndex++;
        }
        aNBT.setTag("upgradeWindowStorage", upgradeWindowStorageNBTTag);

        NBTTagCompound paidCostTag = new NBTTagCompound();
        for (int i = 0; i < getUpgradePaidCosts().length; i++) {
            paidCostTag.setInteger("cost" + i, getUpgradePaidCosts()[i]);
        }
        aNBT.setTag("paidCosts", paidCostTag);

        aNBT.setBoolean("upgradeConsumed", isUpgradeConsumed());
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

    int getUpgradeWindowId();

    String getUpgradeButtonTooltip();

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

    default ModularWindow createConsumeWindow(EntityPlayer player) {
        final int WIDTH = getConsumeWindowWidth();
        final int HEIGHT = getConsumeWindowHeight();
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();

        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT)))
                .subtract(5, 0)
                .add(0, 4));

        ItemStack[] storedItems = getStoredUpgradeWindowItems();
        ItemStackHandler inputHandler = getUpgradeInputSlotHandler();
        int totalSlots = inputHandler.getSlots();
        for (int i = 0; i < totalSlots; i++) {
            if (i < storedItems.length && storedItems[i] != null) {
                inputHandler.insertItem(i, storedItems[i], false);
                storedItems[i] = null;
            }
        }

        int[] upgradePaidCosts = getUpgradePaidCosts();
        ItemStack[] upgradeItems = getUpgradeRequiredItems();
        int costPerRow = getUpgradeCostItemsPerRow();
        int inputSlotsPerRow = getUpgradeInputSlotsPerRow();
        int numCostRows = (int) Math.ceil(upgradeItems.length / (double) costPerRow);

        int slotX = 5 + Math.min(upgradeItems.length, costPerRow) * 36;
        int slotY = 6 + Math.max(0, numCostRows - 1) * 18;

        builder.widget(
            SlotGroup.ofItemHandler(inputHandler, inputSlotsPerRow)
                .startFromSlot(0)
                .endAtSlot(totalSlots - 1)
                .phantom(false)
                .background(getGUITextureSet().getItemSlot())
                .build()
                .setPos(slotX, slotY));

        for (int i = 0; i < upgradeItems.length; i++) {
            ItemStack stack = upgradeItems[i];
            int stackCost = upgradePaidCosts[i];
            Widget costWidget = EternalGregTechWorkshopUI.createExtraCostWidget(stack, () -> stackCost);
            costWidget.setPos(5 + 36 * (i % costPerRow), 6 + 18 * (i / costPerRow));
            builder.widget(costWidget);
        }

        builder.widget(new MultiChildWidget().addChild(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                if (tryConsumeItems()) {
                    setUpgradeConsumed(true);
                }
                EternalGregTechWorkshopUI.reopenWindow(widget, getUpgradeWindowId());
            }
        })
            .setPlayClickSound(true)
            .setBackground(GTUITextures.BUTTON_STANDARD)
            .setSize(WIDTH - 20, 20))
            .addChild(
                new TextWidget(StatCollector.translateToLocal("gt.blockmachines.multimachine.FOG.consumeUpgradeMats"))
                    .setTextAlignment(Alignment.Center)
                    .setScale(0.75f)
                    .setSize(WIDTH - 20, 20))
            .setPos(10, HEIGHT - 30)
            .setSize(WIDTH - 20, 20));

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

    default void createUpgradeButton(ModularWindow.Builder builder, UIBuildContext buildContext) {

        buildContext.addSyncedWindow(getUpgradeWindowId(), this::createConsumeWindow);
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
