package com.science.gtnl.common.metoolbox;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import com.science.gtnl.common.item.items.ItemMEToolbox;

import appeng.api.config.InsertionMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.PlayerSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.MonitorableAction;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.items.misc.ItemMEStackPacket;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.AdaptorIInventory;
import appeng.util.inv.AdaptorPlayerHand;
import appeng.util.inv.AdaptorPlayerInventory;
import appeng.util.inv.WrapperInventoryRange;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.eventhandler.Event;

final class ToolboxTransferService {

    private ToolboxTransferService() {}

    static void depositInventory(EntityPlayerMP player, ToolboxMonitorableAdapter terminal, Slot sourceSlot, int amount,
        BooleanSupplier valid) {
        ItemStack original = sourceSlot.getStack();
        if (original == null || original.stackSize <= 0 || amount <= 0 || !sourceSlot.canTakeStack(player)) return;
        InventoryAdaptor source = new AdaptorIInventory(
            new WrapperInventoryRange(sourceSlot.inventory, sourceSlot.getSlotIndex(), 1, false));
        try {
            depositFrom(player, terminal, source, original, amount, valid);
        } finally {
            sourceSlot.onSlotChanged();
        }
    }

    private static void depositFrom(EntityPlayerMP player, ToolboxMonitorableAdapter terminal, InventoryAdaptor source,
        ItemStack original, int amount, BooleanSupplier valid) {
        deposit(amount, valid, new IDepositAccess() {

            @Override
            public IAEItemStack reserve(int maximum) {

                return AEItemStack.create(source.removeItems(Math.min(maximum, original.stackSize), original, null));
            }

            @Override
            public IAEItemStack poweredInsert(IAEItemStack offered) {

                return AEItemStack.create(terminal.store(offered.getItemStack(), valid));
            }

            @Override
            public void restore(IAEItemStack remainder) {
                restoreToPlayer(player, source, remainder.getItemStack());
            }
        });
    }

    interface IDepositAccess {

        IAEItemStack reserve(int maximum);

        IAEItemStack poweredInsert(IAEItemStack offered);

        void restore(IAEItemStack remainder);
    }

    static void deposit(int maximum, BooleanSupplier valid, IDepositAccess access) {
        if (maximum <= 0 || !valid.getAsBoolean()) return;
        IAEItemStack reserved = access.reserve(maximum);
        if (reserved == null || reserved.getStackSize() <= 0) return;
        if (!valid.getAsBoolean()) {
            access.restore(reserved);
            return;
        }
        IAEItemStack remainder = access.poweredInsert(reserved);

        if (remainder != null && remainder.getStackSize() > 0) access.restore(remainder);
    }

    static boolean depositsCursor(ItemStack hand, MonitorableAction action) {
        return hand != null
            && (hand.getItem() instanceof ItemMEStackPacket || action == MonitorableAction.PICKUP_OR_SET_DOWN
                || action == MonitorableAction.SPLIT_OR_PLACE_SINGLE
                || action == MonitorableAction.ROLL_DOWN);
    }

    static void execute(EntityPlayerMP player, ToolboxMonitorableAdapter terminal, IAEItemStack target,
        MonitorableAction action, BooleanSupplier valid) {
        if (!valid.getAsBoolean()) return;
        try {
            boolean toInventory = action == MonitorableAction.SHIFT_CLICK || action == MonitorableAction.MOVE_REGION;
            InventoryAdaptor destination = toInventory
                ? new InventoryDestination(
                    new AdaptorPlayerInventory(player.inventory, false),
                    InsertionMode.DEFAULT,
                    false,
                    valid,
                    terminal::refund)
                : null;

            terminal.execute(action, target, -1, valid, destination);
        } finally {
            synchronize(player);
        }
    }

    static void extractInventory(EntityPlayerMP player, ToolboxMonitorableAdapter terminal, IAEItemStack target,
        int[] destinationSlots, boolean emptyOnly, boolean single, BooleanSupplier valid) {
        if (!valid.getAsBoolean()) return;
        InventoryAdaptor destination = new InventoryDestination(
            new WrapperInventoryRange(player.inventory, destinationSlots, false),
            emptyOnly ? InsertionMode.ONLY_EMPTY : InsertionMode.DEFAULT,
            single,
            valid,
            terminal::refund);

        ItemStack hand = player.inventory.getItemStack();
        ItemStack parked = hand != null && hand.getItem() instanceof ItemMEStackPacket ? hand : null;
        if (parked != null) player.inventory.setItemStack(null);
        try {
            terminal.execute(
                single ? MonitorableAction.SHIFT_CLICK : MonitorableAction.MOVE_REGION,
                target,
                -1,
                valid,
                destination);
        } finally {
            if (parked != null) restoreToPlayer(player, new AdaptorPlayerHand(player), parked);
            synchronize(player);
        }
    }

    private static void restoreToPlayer(EntityPlayerMP player, InventoryAdaptor preferred, ItemStack stack) {
        Platform.addToPlayerInvOrDrop(player, preferred.addItems(stack));
    }

    static void synchronize(EntityPlayerMP player) {
        player.inventory.markDirty();
        player.openContainer.detectAndSendChanges();
        if (player.openContainer != player.inventoryContainer) {

            player.inventoryContainer.detectAndSendChanges();
            player.sendContainerToPlayer(player.inventoryContainer);
        } else {
            player.updateHeldItem();
        }
    }

    static void pickup(EntityItemPickupEvent event) {
        if (!(event.entityPlayer instanceof EntityPlayerMP player) || player.isDead
            || event.getResult() != Event.Result.DEFAULT) return;
        EntityItem entity = event.item;
        if (entity.isDead || entity.delayBeforeCanPickup > 0) return;

        String owner = entity.func_145798_i();
        if (owner != null && entity.lifespan - entity.age > 200 && !owner.equals(player.getCommandSenderName())) return;
        ItemStack picked = entity.getEntityItem();
        if (picked == null || picked.stackSize <= 0 || ItemMEToolbox.isToolbox(picked)) return;

        for (int slot : ItemMEToolbox.getToolboxSlots(player)) {
            ItemStack toolbox = ItemMEToolbox.getToolboxStack(player, slot);
            if (!ItemMEToolbox.isPickupToMEEnabled(toolbox)) continue;

            if (ItemMEToolbox.findToolboxSlot(player, ItemMEToolbox.getToolboxId(toolbox)) != slot) return;
            insertPickup(event, player, toolbox, slot, picked);
            return;
        }
    }

    private static void insertPickup(EntityItemPickupEvent event, EntityPlayerMP player, ItemStack toolbox, int slot,
        ItemStack picked) {
        IWirelessTermHandler handler = (IWirelessTermHandler) toolbox.getItem();
        WirelessTerminalGuiObject host = new WirelessTerminalGuiObject(
            handler,
            toolbox,
            player,
            player.worldObj,
            slot,
            0,
            0);
        IGrid grid = host.getGrid();
        if (grid == null || !host.rangeCheck()) return;
        IGridNode node = host.getActionableNode();
        if (node == null || node.getGrid() != grid) return;
        IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
        ISecurityGrid security = grid.getCache(ISecurityGrid.class);
        if (energy == null || !energy.isNetworkPowered()
            || security == null
            || !security.hasPermission(player, SecurityPermissions.INJECT)) return;
        IMEMonitor<IAEItemStack> monitor = host.getItemInventory();
        if (monitor == null) return;

        IAEItemStack remainder = Platform
            .poweredInsert(host, monitor, AEItemStack.create(picked), new PlayerSource(player, host));
        int remaining = remainder == null ? 0 : (int) remainder.getStackSize();
        if (remaining == picked.stackSize) return;
        picked.stackSize = remaining;
        event.item.setEntityItemStack(picked);
        player.inventory.markDirty();

        if (remaining == 0) event.setResult(Event.Result.ALLOW);
    }

    static final class InventoryDestination extends AdaptorIInventory {

        private final InsertionMode insertionMode;
        private final boolean single;
        private final BooleanSupplier valid;
        private final Consumer<ItemStack> refund;
        private boolean halted;

        InventoryDestination(IInventory inventory, InsertionMode insertionMode, boolean single, BooleanSupplier valid,
            Consumer<ItemStack> refund) {
            super(inventory);
            this.insertionMode = insertionMode;
            this.single = single;
            this.valid = valid;
            this.refund = refund;
        }

        @Override
        public ItemStack simulateAdd(ItemStack offered) {
            if (offered == null || offered.stackSize <= 0) return null;
            if (halted || !valid.getAsBoolean()) {
                halted = true;
                return offered.copy();
            }
            ItemStack allowed = Platform.copyStackWithSize(offered, single ? 1 : offered.stackSize);
            ItemStack nativeRemainder = super.simulateAdd(allowed, insertionMode);
            if (!valid.getAsBoolean()) {
                halted = true;
                return offered.copy();
            }
            int remaining = offered.stackSize - allowed.stackSize
                + (nativeRemainder == null ? 0 : nativeRemainder.stackSize);
            return Platform.copyStackWithSize(offered, Math.max(0, remaining));
        }

        @Override
        public ItemStack addItems(ItemStack extracted) {
            if (extracted == null || extracted.stackSize <= 0) return null;
            ItemStack leftover;
            if (halted) {
                leftover = extracted.copy();
            } else {

                ItemStack allowed = Platform.copyStackWithSize(extracted, single ? 1 : extracted.stackSize);
                ItemStack nativeRemainder = super.addItems(allowed, insertionMode);
                int remaining = extracted.stackSize - allowed.stackSize
                    + (nativeRemainder == null ? 0 : nativeRemainder.stackSize);
                leftover = Platform.copyStackWithSize(extracted, Math.max(0, remaining));
            }
            if (single || leftover != null || !valid.getAsBoolean()) halted = true;
            if (leftover != null) refund.accept(leftover);

            return null;
        }
    }
}
