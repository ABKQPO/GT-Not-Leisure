package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.science.gtnl.api.IControllerUpgrade;
import com.science.gtnl.common.gui.GTNLMui2Textures;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.synchandler.NBTTagSyncHandler;
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;

public class GTNLControllerUpgradePanels {

    public static final String UPGRADE_PANEL_KEY_PREFIX = "gtnl_controller_upgrade_";
    public static final String UPGRADE_CURRENT_PANEL_KEY = UPGRADE_PANEL_KEY_PREFIX + "current";
    public static final String UPGRADE_INPUT_SLOT_GROUP = "gtnl_upgrade_input";
    private static final String UPGRADE_CONSUMED_SYNC_KEY = "gtnlUpgradeConsumed";
    private static final String UPGRADE_PROGRESS_SYNC_KEY = "gtnlUpgradeProgress";
    private static final String CURRENT_PAID_COSTS_SYNC_KEY = "gtnlUpgradePaidCostsCurrent";
    private static final String DISPLAY_PAID_COSTS_SYNC_KEY_PREFIX = "gtnlUpgradePaidCostsPage";
    private static final int COST_CELL_WIDTH = 36;
    private static final int BUTTON_SIZE = 18;
    private static final int PANEL_MARGIN = 5;
    private static final int UPGRADE_INPUT_SHIFT_CLICK_PRIORITY = SlotGroup.STORAGE_SLOT_PRIO - 1;

    private final MTEMultiBlockBase multiblock;
    private final IControllerUpgrade controllerUpgrade;
    private final Map<String, IPanelHandler> panelMap;

    public GTNLControllerUpgradePanels(MTEMultiBlockBase multiblock, IControllerUpgrade controllerUpgrade,
        Map<String, IPanelHandler> panelMap) {
        this.multiblock = multiblock;
        this.controllerUpgrade = controllerUpgrade;
        this.panelMap = panelMap;
    }

    public void registerPanels(ModularPanel parent, PanelSyncManager syncManager) {
        panelMap.put(
            UPGRADE_CURRENT_PANEL_KEY,
            syncManager.syncedPanel(
                UPGRADE_CURRENT_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createUpgradePanel(parent, syncManager, panelSyncManager, true, 0)));

        for (int displayPage = 0; displayPage < controllerUpgrade.getUpgradeDisplayPageCount(); displayPage++) {
            int page = displayPage;
            String panelKey = getDisplayPanelKey(page);
            panelMap.put(
                panelKey,
                syncManager.syncedPanel(
                    panelKey,
                    true,
                    (panelSyncManager,
                        panelHandler) -> createUpgradePanel(parent, syncManager, panelSyncManager, false, page)));
        }
    }

    public void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            UPGRADE_CONSUMED_SYNC_KEY,
            new BooleanSyncValue(controllerUpgrade::isUpgradeConsumed, controllerUpgrade::setUpgradeConsumed));
        syncManager.syncValue(
            UPGRADE_PROGRESS_SYNC_KEY,
            new IntSyncValue(controllerUpgrade::getUpgradeProgress, controllerUpgrade::setUpgradeProgress));
        syncManager.syncValue(
            CURRENT_PAID_COSTS_SYNC_KEY,
            new NBTTagSyncHandler(() -> createPaidCostsTag(true, 0), ignored -> {}));
        for (int displayPage = 0; displayPage < controllerUpgrade.getUpgradeDisplayPageCount(); displayPage++) {
            int page = displayPage;
            syncManager.syncValue(
                getPaidCostsSyncKey(false, page),
                new NBTTagSyncHandler(() -> createPaidCostsTag(false, page), ignored -> {}));
        }
    }

    public IWidget createUpgradeButton(PanelSyncManager syncManager) {
        IPanelHandler upgradePanel = panelMap.get(UPGRADE_CURRENT_PANEL_KEY);
        BooleanSyncValue upgradeConsumedSyncer = syncManager
            .findSyncHandler(UPGRADE_CONSUMED_SYNC_KEY, BooleanSyncValue.class);

        return new ButtonWidget<>().size(BUTTON_SIZE, BUTTON_SIZE)
            .background(
                new DynamicDrawable(
                    () -> !controllerUpgrade.isUpgradeButtonEnabled() ? GTGuiTextures.BUTTON_STANDARD_DISABLED
                        : upgradeConsumedSyncer.getBoolValue() ? GTGuiTextures.BUTTON_STANDARD_PRESSED
                            : GTGuiTextures.BUTTON_STANDARD))
            .overlay(GTNLMui2Textures.OVERLAY_BUTTON_ARROW_GREEN_UP)
            .onMousePressed(mouseButton -> {
                if (!controllerUpgrade.isUpgradeButtonEnabled()) return false;
                if (upgradePanel != null) upgradePanel.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(controllerUpgrade.getUpgradeButtonTooltip()))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .setEnabledIf(widget -> controllerUpgrade.isUpgradeButtonEnabled());
    }

    protected ModularPanel createUpgradePanel(ModularPanel parent, PanelSyncManager rootSyncManager,
        PanelSyncManager panelSyncManager, boolean currentPanel, int displayPage) {
        int maxUpgradeItemCount = controllerUpgrade.getMaxUpgradeRequiredItemCount();
        int costColumns = Math.max(1, Math.min(maxUpgradeItemCount, controllerUpgrade.getUpgradeCostItemsPerRow()));
        int inputColumns = Math.max(1, controllerUpgrade.getUpgradeInputSlotsPerRow());
        int costRows = Math.max(1, (int) Math.ceil(maxUpgradeItemCount / (double) costColumns));
        int inputRows = currentPanel ? Math.max(
            1,
            (int) Math.ceil(
                controllerUpgrade.getUpgradeInputSlotHandler()
                    .getSlots() / (double) inputColumns)) : 0;
        int width = Math.max(
            BUTTON_SIZE * 2 + PANEL_MARGIN * 2 + 160,
            costColumns * COST_CELL_WIDTH + controllerUpgrade.getUpgradeCostItemsPerRow() * ItemSlot.SIZE);
        int height = 60 + Math.max(0, Math.max(costRows, inputRows) - 1) * ItemSlot.SIZE;

        Dialog<?> panel = new Dialog<>("gtnl_controller_upgrade_" + (currentPanel ? "current" : displayPage), null);
        panel.relative(parent)
            .size(width, height)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);

        panel.child(ButtonWidget.panelCloseButton());
        panel.child(createCostGrid(rootSyncManager, currentPanel, displayPage, costColumns, costRows).pos(PANEL_MARGIN, 6));

        if (currentPanel) {
            transferStoredItemsToInputHandler();
            panel.child(
                createInputGrid(panelSyncManager, inputColumns, inputRows)
                    .pos(PANEL_MARGIN + costColumns * COST_CELL_WIDTH, 6));
            panel.child(createConsumeButton(rootSyncManager, width).pos(10, height - 26));
        }

        int costGridWidth = costColumns * COST_CELL_WIDTH;
        int costGridHeight = costRows * ItemSlot.SIZE;

        panel.child(createPreviousButton(currentPanel, displayPage).pos(PANEL_MARGIN, 6 + costGridHeight));

        panel.child(
            createNextButton(currentPanel, displayPage).pos(PANEL_MARGIN + costGridWidth - BUTTON_SIZE, 6 + costGridHeight));

        return panel;
    }

    private Grid createCostGrid(PanelSyncManager syncManager, boolean currentPanel, int displayPage, int columns, int rows) {
        NBTTagSyncHandler paidCostsSyncer = getPaidCostsSyncer(syncManager, currentPanel, displayPage);
        return new Grid().coverChildren()
            .gridOfWidthHeight(columns, rows, (x, y, index) -> {
                return createCostWidget(
                    () -> getUpgradeItem(currentPanel, displayPage, index),
                    () -> getPaidCost(paidCostsSyncer, index)).size(COST_CELL_WIDTH, ItemSlot.SIZE);
            });
    }

    private Flow createCostWidget(Supplier<ItemStack> stackSupplier, IntSupplier paidCostSupplier) {
        return Flow.row()
            .size(COST_CELL_WIDTH, ItemSlot.SIZE)
            .child(
                new SlotLikeButtonWidget(stackSupplier).size(ItemSlot.SIZE)
                    .onMousePressed(mouseButton -> {
                        ItemStack stack = stackSupplier.get();
                        if (stack == null) return false;
                        if (mouseButton == 0) {
                            GuiCraftingRecipe.openRecipeGui("item", stack);
                        } else if (mouseButton == 1) {
                            GuiUsageRecipe.openRecipeGui("item", stack);
                        }
                        return true;
                    })
                    .tooltipBuilder(tooltip -> {
                        ItemStack stack = stackSupplier.get();
                        if (stack != null) tooltip.addFromItem(stack);
                    })
                    .tooltipAutoUpdate(true))
            .child(
                IKey.dynamic(() -> getRemainingCostText(stackSupplier.get(), paidCostSupplier))
                    .asWidget()
                    .size(ItemSlot.SIZE)
                    .scale(0.8f)
                    .textAlign(Alignment.Center));
    }

    private String getRemainingCostText(ItemStack stack, IntSupplier paidCostSupplier) {
        if (stack == null) return "";
        int paid = Math.max(0, paidCostSupplier.getAsInt());
        int remaining = Math.max(0, stack.stackSize - paid);
        EnumChatFormatting color = EnumChatFormatting.YELLOW;
        if (paid == 0) {
            color = EnumChatFormatting.RED;
        } else if (remaining == 0) {
            color = EnumChatFormatting.GREEN;
        }
        return color + "x" + remaining;
    }

    private int getPaidCost(NBTTagSyncHandler paidCostsSyncer, int costIndex) {
        NBTTagCompound paidCosts = paidCostsSyncer.getValue();
        return paidCosts == null ? 0 : paidCosts.getInteger("cost" + costIndex);
    }

    private Grid createInputGrid(PanelSyncManager syncManager, int columns, int rows) {
        ItemStackHandler inputHandler = controllerUpgrade.getUpgradeInputSlotHandler();
        GTNLMui2ItemHandlerAdapter adapter = new GTNLMui2ItemHandlerAdapter(inputHandler, this::markMultiblockDirty);
        syncManager.registerSlotGroup(UPGRADE_INPUT_SLOT_GROUP, rows, UPGRADE_INPUT_SHIFT_CLICK_PRIORITY);

        return new Grid().coverChildren()
            .gridOfWidthHeight(columns, rows, (x, y, index) -> {
                if (index >= inputHandler.getSlots()) {
                    return new Widget<>().size(ItemSlot.SIZE);
                }
                return new ItemSlot().slot(new ModularSlot(adapter, index).slotGroup(UPGRADE_INPUT_SLOT_GROUP));
            });
    }

    private ButtonWidget<?> createConsumeButton(PanelSyncManager rootSyncManager, int panelWidth) {
        BooleanSyncValue upgradeConsumedSyncer = rootSyncManager
            .findSyncHandler(UPGRADE_CONSUMED_SYNC_KEY, BooleanSyncValue.class);
        IntSyncValue upgradeProgressSyncer = rootSyncManager
            .findSyncHandler(UPGRADE_PROGRESS_SYNC_KEY, IntSyncValue.class);
        NBTTagSyncHandler paidCostsSyncer = getPaidCostsSyncer(rootSyncManager, true, 0);
        return new ButtonWidget<>().size(panelWidth - 20, 20)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(
                IKey.str(translateToLocal("gt.blockmachines.multimachine.FOG.consumeUpgradeMats"))
                    .scale(0.75f))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                IGregTechTileEntity baseMetaTileEntity = multiblock.getBaseMetaTileEntity();
                if (baseMetaTileEntity == null || !baseMetaTileEntity.isServerSide()) return;
                if (controllerUpgrade.tryConsumeItems()) {
                    controllerUpgrade.setUpgradeConsumed(true);
                }
                baseMetaTileEntity.markDirty();
                notifyPaidCostsUpdated(rootSyncManager, paidCostsSyncer);
                upgradeConsumedSyncer.setBoolValue(controllerUpgrade.isUpgradeConsumed(), false, true);
                upgradeProgressSyncer.notifyUpdate();
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private void notifyPaidCostsUpdated(PanelSyncManager syncManager, NBTTagSyncHandler currentPaidCostsSyncer) {
        currentPaidCostsSyncer.notifyUpdate();
        for (int displayPage = 0; displayPage < controllerUpgrade.getUpgradeDisplayPageCount(); displayPage++) {
            getPaidCostsSyncer(syncManager, false, displayPage)
                .notifyUpdate();
        }
    }

    private void markMultiblockDirty() {
        IGregTechTileEntity baseMetaTileEntity = multiblock.getBaseMetaTileEntity();
        if (baseMetaTileEntity != null && baseMetaTileEntity.isServerSide()) baseMetaTileEntity.markDirty();
    }

    private ButtonWidget<?> createPreviousButton(boolean currentPanel, int displayPage) {
        return new ButtonWidget<>().size(BUTTON_SIZE, BUTTON_SIZE)
            .background(
                new DynamicDrawable(
                    () -> canNavigateToPrevious(currentPanel, displayPage) ? GTGuiTextures.BUTTON_STANDARD
                        : GTGuiTextures.BUTTON_STANDARD_DISABLED))
            .overlay(GTGuiTextures.OVERLAY_BUTTON_IMPORT)
            .onMousePressed(mouseButton -> {
                int currentPage = controllerUpgrade.getCurrentUpgradeDisplayPage();
                int targetPage = currentPanel ? currentPage - 1 : displayPage - 1;
                IPanelHandler previousPanel = getPanelForDisplayPage(targetPage, currentPage);
                if (targetPage < 0 || previousPanel == null) return false;
                closeUpgradePanels();
                previousPanel.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(translateToLocal("gtnl.ui.controllerUpgrade.backToCurrent")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ButtonWidget<?> createNextButton(boolean currentPanel, int displayPage) {
        return new ButtonWidget<>().size(BUTTON_SIZE, BUTTON_SIZE)
            .background(
                new DynamicDrawable(
                    () -> canNavigateToNext(currentPanel, displayPage) ? GTGuiTextures.BUTTON_STANDARD
                        : GTGuiTextures.BUTTON_STANDARD_DISABLED))
            .overlay(GTGuiTextures.OVERLAY_BUTTON_EXPORT)
            .onMousePressed(mouseButton -> {
                int currentPage = controllerUpgrade.getCurrentUpgradeDisplayPage();
                int targetPage = currentPanel ? currentPage + 1 : displayPage + 1;
                IPanelHandler nextPanel = getPanelForDisplayPage(targetPage, currentPage);
                if (targetPage >= controllerUpgrade.getUpgradeDisplayPageCount() || nextPanel == null
                    || getUpgradeItems(false, targetPage).length == 0) return false;
                closeUpgradePanels();
                nextPanel.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(translateToLocal("gtnl.ui.controllerUpgrade.previewNext")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private boolean canNavigateToPrevious(boolean currentPanel, int displayPage) {
        int currentPage = controllerUpgrade.getCurrentUpgradeDisplayPage();
        int targetPage = currentPanel ? currentPage - 1 : displayPage - 1;
        return targetPage >= 0 && getPanelForDisplayPage(targetPage, currentPage) != null;
    }

    private boolean canNavigateToNext(boolean currentPanel, int displayPage) {
        int currentPage = controllerUpgrade.getCurrentUpgradeDisplayPage();
        int targetPage = currentPanel ? currentPage + 1 : displayPage + 1;
        return targetPage < controllerUpgrade.getUpgradeDisplayPageCount()
            && getPanelForDisplayPage(targetPage, currentPage) != null
            && getUpgradeItems(false, targetPage).length > 0;
    }

    private IPanelHandler getPanelForDisplayPage(int displayPage, int currentPage) {
        return displayPage == currentPage ? panelMap.get(UPGRADE_CURRENT_PANEL_KEY)
            : panelMap.get(getDisplayPanelKey(displayPage));
    }

    private ItemStack[] getUpgradeItems(boolean currentPanel, int displayPage) {
        ItemStack[] items = currentPanel ? controllerUpgrade.getUpgradeRequiredItems()
            : controllerUpgrade.getUpgradeDisplayItems(displayPage);
        return items == null ? new ItemStack[0] : items;
    }

    private ItemStack getUpgradeItem(boolean currentPanel, int displayPage, int itemIndex) {
        ItemStack[] upgradeItems = getUpgradeItems(currentPanel, displayPage);
        return itemIndex < upgradeItems.length ? upgradeItems[itemIndex] : null;
    }

    private int[] getUpgradePaidCosts(boolean currentPanel, int displayPage) {
        int[] paidCosts = currentPanel ? controllerUpgrade.getUpgradePaidCosts()
            : controllerUpgrade.getUpgradeDisplayPaidCosts(displayPage);
        return paidCosts == null ? new int[0] : paidCosts;
    }

    private NBTTagCompound createPaidCostsTag(boolean currentPanel, int displayPage) {
        NBTTagCompound tag = new NBTTagCompound();
        int[] paidCosts = getUpgradePaidCosts(currentPanel, displayPage);
        for (int index = 0; index < paidCosts.length; index++) {
            tag.setInteger("cost" + index, paidCosts[index]);
        }
        return tag;
    }

    private NBTTagSyncHandler getPaidCostsSyncer(PanelSyncManager syncManager, boolean currentPanel, int displayPage) {
        return syncManager.findSyncHandler(getPaidCostsSyncKey(currentPanel, displayPage), NBTTagSyncHandler.class);
    }

    private String getPaidCostsSyncKey(boolean currentPanel, int displayPage) {
        return currentPanel ? CURRENT_PAID_COSTS_SYNC_KEY : DISPLAY_PAID_COSTS_SYNC_KEY_PREFIX + displayPage;
    }

    private void transferStoredItemsToInputHandler() {
        ItemStack[] storedItems = controllerUpgrade.getStoredUpgradeWindowItems();
        ItemStackHandler inputHandler = controllerUpgrade.getUpgradeInputSlotHandler();
        boolean movedItem = false;
        for (int index = 0; index < inputHandler.getSlots() && index < storedItems.length; index++) {
            if (storedItems[index] != null) {
                inputHandler.insertItem(index, storedItems[index], false);
                storedItems[index] = null;
                movedItem = true;
            }
        }
        if (movedItem) markMultiblockDirty();
    }

    private void closeUpgradePanels() {
        IPanelHandler currentPanel = panelMap.get(UPGRADE_CURRENT_PANEL_KEY);
        if (currentPanel != null) currentPanel.closePanel();
        for (int displayPage = 0; displayPage < controllerUpgrade.getUpgradeDisplayPageCount(); displayPage++) {
            IPanelHandler panelHandler = panelMap.get(getDisplayPanelKey(displayPage));
            if (panelHandler != null) panelHandler.closePanel();
        }
    }

    private String getDisplayPanelKey(int displayPage) {
        return UPGRADE_PANEL_KEY_PREFIX + "display_" + displayPage;
    }
}
