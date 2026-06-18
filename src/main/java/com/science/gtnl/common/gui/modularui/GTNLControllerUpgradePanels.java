package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Map;
import java.util.function.IntSupplier;

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
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
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
    private static final String UPGRADE_CONSUMED_SYNC_KEY = "gtnlUpgradeConsumed";
    private static final String PAID_COSTS_SYNC_KEY_PREFIX = "gtnlUpgradePaidCosts";
    private static final int COST_CELL_WIDTH = 36;
    private static final int BUTTON_SIZE = 18;
    private static final int PANEL_MARGIN = 5;

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
                (panelSyncManager,
                    panelHandler) -> createUpgradePanel(parent, syncManager, panelSyncManager, panelHandler, 0)));

        int maxPreviewLevel = controllerUpgrade.getMaxPreviewUpgradeLevel();
        for (int level = 1; level <= maxPreviewLevel; level++) {
            int previewLevel = level;
            String panelKey = getPreviewPanelKey(previewLevel);
            panelMap.put(
                panelKey,
                syncManager.syncedPanel(
                    panelKey,
                    true,
                    (panelSyncManager, panelHandler) -> createUpgradePanel(
                        parent,
                        syncManager,
                        panelSyncManager,
                        panelHandler,
                        previewLevel)));
        }
    }

    public void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            UPGRADE_CONSUMED_SYNC_KEY,
            new BooleanSyncValue(controllerUpgrade::isUpgradeConsumed, controllerUpgrade::setUpgradeConsumed)
                .allowC2S());
        for (int level = 0; level <= controllerUpgrade.getMaxPreviewUpgradeLevel(); level++) {
            int previewLevel = level;
            syncManager.syncValue(
                getPaidCostsSyncKey(previewLevel),
                new NBTTagSyncHandler(() -> createPaidCostsTag(previewLevel), ignored -> {}));
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
        PanelSyncManager panelSyncManager, IPanelHandler panelHandler, int previewLevel) {
        boolean previewMode = previewLevel > 0;
        ItemStack[] upgradeItems = getUpgradeItems(previewLevel);
        int costColumns = Math.max(1, Math.min(upgradeItems.length, controllerUpgrade.getUpgradeCostItemsPerRow()));
        int inputColumns = Math.max(1, controllerUpgrade.getUpgradeInputSlotsPerRow());
        int costRows = Math.max(1, (int) Math.ceil(upgradeItems.length / (double) costColumns));
        int inputRows = previewMode ? 0
            : Math.max(
                1,
                (int) Math.ceil(
                    controllerUpgrade.getUpgradeInputSlotHandler()
                        .getSlots() / (double) inputColumns));
        int width = Math.max(
            BUTTON_SIZE * 2 + PANEL_MARGIN * 2 + 160,
            costColumns * COST_CELL_WIDTH + controllerUpgrade.getUpgradeCostItemsPerRow() * ItemSlot.SIZE);
        int height = 60 + Math.max(0, Math.max(costRows, inputRows) - 1) * ItemSlot.SIZE;

        Dialog<?> panel = new Dialog<>("gtnl_controller_upgrade_" + previewLevel, null);
        panel.relative(parent)
            .size(width, height)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);

        panel.child(ButtonWidget.panelCloseButton());
        panel.child(createCostGrid(rootSyncManager, upgradeItems, previewLevel, costColumns).pos(PANEL_MARGIN, 6));

        if (!previewMode) {
            transferStoredItemsToInputHandler();
            panel.child(
                createInputGrid(panelSyncManager, inputColumns, inputRows)
                    .pos(PANEL_MARGIN + costColumns * COST_CELL_WIDTH, 6));
            panel.child(createConsumeButton(panelHandler, rootSyncManager, width).pos(10, height - 26));
        }

        int costGridWidth = costColumns * COST_CELL_WIDTH;
        int costGridHeight = costRows * ItemSlot.SIZE;

        panel.child(createPreviousButton(previewLevel).pos(PANEL_MARGIN, 6 + costGridHeight));

        panel.child(createNextButton(previewLevel).pos(PANEL_MARGIN + costGridWidth - BUTTON_SIZE, 6 + costGridHeight));

        return panel;
    }

    private Grid createCostGrid(PanelSyncManager syncManager, ItemStack[] upgradeItems, int previewLevel, int columns) {
        int rows = Math.max(1, (int) Math.ceil(upgradeItems.length / (double) columns));
        NBTTagSyncHandler paidCostsSyncer = getPaidCostsSyncer(syncManager, previewLevel);
        return new Grid().coverChildren()
            .gridOfWidthHeight(columns, rows, (x, y, index) -> {
                if (index >= upgradeItems.length || upgradeItems[index] == null) {
                    return new Widget<>().size(COST_CELL_WIDTH, ItemSlot.SIZE);
                }
                return createCostWidget(upgradeItems[index], () -> getPaidCost(paidCostsSyncer, index))
                    .size(COST_CELL_WIDTH, ItemSlot.SIZE);
            });
    }

    private Flow createCostWidget(ItemStack stack, IntSupplier paidCostSupplier) {
        return Flow.row()
            .size(COST_CELL_WIDTH, ItemSlot.SIZE)
            .child(
                new SlotLikeButtonWidget(stack).size(ItemSlot.SIZE)
                    .onMousePressed(mouseButton -> {
                        if (mouseButton == 0) {
                            GuiCraftingRecipe.openRecipeGui("item", stack);
                        } else if (mouseButton == 1) {
                            GuiUsageRecipe.openRecipeGui("item", stack);
                        }
                        return true;
                    })
                    .tooltipBuilder(tooltip -> tooltip.addFromItem(stack))
                    .tooltipAutoUpdate(true))
            .child(
                IKey.dynamic(() -> getRemainingCostText(stack, paidCostSupplier))
                    .asWidget()
                    .size(ItemSlot.SIZE)
                    .scale(0.8f)
                    .textAlign(Alignment.Center));
    }

    private String getRemainingCostText(ItemStack stack, IntSupplier paidCostSupplier) {
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
        GTNLMui2ItemHandlerAdapter adapter = new GTNLMui2ItemHandlerAdapter(inputHandler);
        syncManager.registerSlotGroup("gtnl_upgrade_input", rows);

        return new Grid().coverChildren()
            .gridOfWidthHeight(columns, rows, (x, y, index) -> {
                if (index >= inputHandler.getSlots()) {
                    return new Widget<>().size(ItemSlot.SIZE);
                }
                return new ItemSlot().slot(new ModularSlot(adapter, index).slotGroup("gtnl_upgrade_input"));
            });
    }

    private ButtonWidget<?> createConsumeButton(IPanelHandler panelHandler, PanelSyncManager rootSyncManager,
        int panelWidth) {
        BooleanSyncValue upgradeConsumedSyncer = rootSyncManager
            .findSyncHandler(UPGRADE_CONSUMED_SYNC_KEY, BooleanSyncValue.class);
        NBTTagSyncHandler paidCostsSyncer = getPaidCostsSyncer(rootSyncManager, 0);
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
                    panelHandler.closePanel();
                }
                paidCostsSyncer.notifyUpdate();
                upgradeConsumedSyncer.setBoolValue(controllerUpgrade.isUpgradeConsumed(), false, true);
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ButtonWidget<?> createPreviousButton(int previewLevel) {
        int previousLevel = previewLevel - 1;
        IPanelHandler previousPanel = previousLevel <= 0 ? panelMap.get(UPGRADE_CURRENT_PANEL_KEY)
            : panelMap.get(getPreviewPanelKey(previousLevel));
        boolean enabled = previewLevel > 0 && previousPanel != null;

        return new ButtonWidget<>().size(BUTTON_SIZE, BUTTON_SIZE)
            .background(enabled ? GTGuiTextures.BUTTON_STANDARD : GTGuiTextures.BUTTON_STANDARD_DISABLED)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_IMPORT)
            .onMousePressed(mouseButton -> {
                if (!enabled) return false;
                closeUpgradePanels();
                previousPanel.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(translateToLocal("gtnl.ui.controllerUpgrade.backToCurrent")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ButtonWidget<?> createNextButton(int previewLevel) {
        int nextLevel = previewLevel + 1;
        IPanelHandler nextPanel = panelMap.get(getPreviewPanelKey(nextLevel));
        boolean enabled = nextPanel != null;

        return new ButtonWidget<>().size(BUTTON_SIZE, BUTTON_SIZE)
            .background(enabled ? GTGuiTextures.BUTTON_STANDARD : GTGuiTextures.BUTTON_STANDARD_DISABLED)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_EXPORT)
            .onMousePressed(mouseButton -> {
                if (!enabled) return false;
                closeUpgradePanels();
                nextPanel.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(translateToLocal("gtnl.ui.controllerUpgrade.previewNext")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ItemStack[] getUpgradeItems(int previewLevel) {
        ItemStack[] items = previewLevel > 0 ? controllerUpgrade.getPreviewUpgradeRequiredItems(previewLevel)
            : controllerUpgrade.getUpgradeRequiredItems();
        return items == null ? new ItemStack[0] : items;
    }

    private int[] getUpgradePaidCosts(int previewLevel) {
        int[] paidCosts = previewLevel > 0 ? controllerUpgrade.getPreviewUpgradePaidCosts(previewLevel)
            : controllerUpgrade.getUpgradePaidCosts();
        return paidCosts == null ? new int[0] : paidCosts;
    }

    private NBTTagCompound createPaidCostsTag(int previewLevel) {
        NBTTagCompound tag = new NBTTagCompound();
        int[] paidCosts = getUpgradePaidCosts(previewLevel);
        for (int index = 0; index < paidCosts.length; index++) {
            tag.setInteger("cost" + index, paidCosts[index]);
        }
        return tag;
    }

    private NBTTagSyncHandler getPaidCostsSyncer(PanelSyncManager syncManager, int previewLevel) {
        return syncManager.findSyncHandler(getPaidCostsSyncKey(previewLevel), NBTTagSyncHandler.class);
    }

    private String getPaidCostsSyncKey(int previewLevel) {
        return PAID_COSTS_SYNC_KEY_PREFIX + previewLevel;
    }

    private void transferStoredItemsToInputHandler() {
        ItemStack[] storedItems = controllerUpgrade.getStoredUpgradeWindowItems();
        ItemStackHandler inputHandler = controllerUpgrade.getUpgradeInputSlotHandler();
        for (int index = 0; index < inputHandler.getSlots() && index < storedItems.length; index++) {
            if (storedItems[index] != null) {
                inputHandler.insertItem(index, storedItems[index], false);
                storedItems[index] = null;
            }
        }
    }

    private void closeUpgradePanels() {
        for (int level = 0; level <= controllerUpgrade.getMaxPreviewUpgradeLevel(); level++) {
            IPanelHandler panelHandler = panelMap
                .get(level == 0 ? UPGRADE_CURRENT_PANEL_KEY : getPreviewPanelKey(level));
            if (panelHandler != null) panelHandler.closePanel();
        }
    }

    private String getPreviewPanelKey(int previewLevel) {
        return UPGRADE_PANEL_KEY_PREFIX + "preview_" + previewLevel;
    }
}
