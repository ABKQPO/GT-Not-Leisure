package com.science.gtnl.common.gui.modularui;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PlayerSlotGroup;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseViewMode;

import gregtech.api.recipe.check.CheckRecipeResult;

public class GreenHouseModularContainer extends ModularContainer {

    private final IGreenHouse greenHouse;
    private final String viewModeSyncKey;
    private final String cropSlotListSyncKey;
    private final String cropSlotWidgetSyncKey;
    private final String usedSeedCountSyncKey;
    private final boolean supportsBlockPanel;
    private final boolean fixedBlockSlot;

    public GreenHouseModularContainer(IGreenHouse greenHouse, String viewModeSyncKey, String cropSlotListSyncKey,
        String cropSlotWidgetSyncKey, String usedSeedCountSyncKey, boolean supportsBlockPanel, boolean fixedBlockSlot) {
        this.greenHouse = greenHouse;
        this.viewModeSyncKey = viewModeSyncKey;
        this.cropSlotListSyncKey = cropSlotListSyncKey;
        this.cropSlotWidgetSyncKey = cropSlotWidgetSyncKey;
        this.usedSeedCountSyncKey = usedSeedCountSyncKey;
        this.supportsBlockPanel = supportsBlockPanel;
        this.fixedBlockSlot = fixedBlockSlot;
    }

    @Override
    public @Nullable ItemStack transferStackInSlot(@NotNull EntityPlayer playerIn, int index) {
        ModularSlot slot = getModularSlot(index);
        if (!isPlayerSlot(slot) || !greenHouse.isGreenHouseStorageEditable()) {
            return super.transferStackInSlot(playerIn, index);
        }

        ItemStack stack = slot.getStack();
        if (CropsNHUtils.isStackInvalid(stack)) {
            return super.transferStackInSlot(playerIn, index);
        }

        ItemStack original = stack.copy();
        if (shouldHandleClientGreenHouseTransfer(stack)) {
            slot.putStack(null);
            slot.onSlotChanged();
            return original;
        }

        if (tryTransferToGreenHouse(stack)) {
            slot.putStack(stack.stackSize > 0 ? stack : null);
            slot.onSlotChanged();
            notifyGreenHouseSlotsChanged();
            return original;
        }

        return super.transferStackInSlot(playerIn, index);
    }

    private boolean isPlayerSlot(ModularSlot slot) {
        SlotGroup slotGroup = slot.getSlotGroup();
        return slotGroup != null && PlayerSlotGroup.NAME.equals(slotGroup.getName());
    }

    private boolean tryTransferToGreenHouse(ItemStack stack) {
        GreenHouseViewMode viewMode = getCurrentViewMode();
        if (viewMode == GreenHouseViewMode.SEEDS) {
            if (tryAddSeeds(stack)) return true;
            return fixedBlockSlot && tryAddBlockUnder(stack);
        }
        return supportsBlockPanel && viewMode == GreenHouseViewMode.BLOCKS && tryAddBlockUnder(stack);
    }

    private boolean shouldHandleClientGreenHouseTransfer(ItemStack stack) {
        if (!isClient()) return false;

        GreenHouseViewMode viewMode = getCurrentViewMode();
        if (viewMode == GreenHouseViewMode.BLOCKS && supportsBlockPanel) {
            return isPotentialBlockUnder(stack);
        }
        if (viewMode == GreenHouseViewMode.SEEDS) {
            return CropsNHUtils.getAnalyzedSeedData(stack) != null || fixedBlockSlot && isPotentialBlockUnder(stack);
        }
        return false;
    }

    private boolean isPotentialBlockUnder(ItemStack stack) {
        Block block = CropsNHUtils.getBlockFromItem(stack);
        return block != null && block.getMaterial() != Material.air;
    }

    private GreenHouseViewMode getCurrentViewMode() {
        PanelSyncManager syncManager = getSyncManager().getMainPSM();
        IntSyncValue viewModeSyncer = syncManager.findSyncHandler(viewModeSyncKey, IntSyncValue.class);
        return supportsBlockPanel ? GreenHouseViewMode.fromOrdinal(viewModeSyncer.getIntValue())
            : GreenHouseViewMode.fromOrdinalWithoutBlocks(viewModeSyncer.getIntValue());
    }

    private boolean tryAddSeeds(ItemStack stack) {
        int before = stack.stackSize;
        CheckRecipeResult result = greenHouse.tryAddCropStack(stack, false);
        return result.wasSuccessful() && stack.stackSize < before;
    }

    private boolean tryAddBlockUnder(ItemStack stack) {
        int before = stack.stackSize;
        CheckRecipeResult result = greenHouse.tryAddBlockUnderStack(stack, false);
        return result.wasSuccessful() && stack.stackSize < before;
    }

    private void notifyGreenHouseSlotsChanged() {
        if (isClient()) return;

        PanelSyncManager syncManager = getSyncManager().getMainPSM();
        IntSyncValue usedSeedCountSyncer = syncManager
            .findSyncHandlerNullable(usedSeedCountSyncKey, IntSyncValue.class);
        if (usedSeedCountSyncer != null) {
            usedSeedCountSyncer.notifyUpdate();
        }

        GenericListSyncHandler<?> cropSlotSyncer = syncManager
            .findSyncHandlerNullable(cropSlotListSyncKey, GenericListSyncHandler.class);
        if (cropSlotSyncer != null) {
            cropSlotSyncer.notifyUpdate();
        }

        DynamicSyncHandler cropInventoryWidgetSyncer = syncManager
            .findSyncHandlerNullable(cropSlotWidgetSyncKey, DynamicSyncHandler.class);
        if (cropInventoryWidgetSyncer != null) {
            cropInventoryWidgetSyncer.notifyUpdate(buffer -> {});
        }
    }
}
