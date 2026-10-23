package com.science.gtnl.common.meminiterminal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import com.science.gtnl.common.item.items.ItemMEMiniPortableTerminal;
import com.science.gtnl.utils.CreativeCursor;

import appeng.api.config.InsertionMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.PlayerSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
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

final class MiniTerminalTransferService {

    private static final Map<UUID, PickupHost> PICKUP_HOSTS = new HashMap<>();
    private static final Map<Container, Boolean> COVERS_MAIN_INVENTORY = new WeakHashMap<>();

    static void depositInventory(EntityPlayerMP player, MiniTerminalMonitorableAdapter terminal, Slot sourceSlot,
        int amount, BooleanSupplier valid) {
        int inventorySlot = ItemMEMiniPortableTerminal.playerInventoryIndex(player, sourceSlot);
        if (inventorySlot < 0) return;
        ItemStack original = player.inventory.mainInventory[inventorySlot];
        if (original == null || original.stackSize <= 0 || amount <= 0 || !sourceSlot.canTakeStack(player)) return;
        InventoryAdaptor source = new AdaptorIInventory(
            new WrapperInventoryRange(player.inventory, inventorySlot, 1, false));
        try {
            if (!valid.getAsBoolean()) return;
            ItemStack reserved = source.removeItems(Math.min(amount, original.stackSize), original, null);
            if (reserved == null || reserved.stackSize <= 0) return;
            ItemStack remainder = valid.getAsBoolean() ? terminal.store(reserved, valid) : reserved;
            if (remainder != null && remainder.stackSize > 0) restoreToPlayer(player, source, remainder);
        } finally {
            sourceSlot.onSlotChanged();
        }
    }

    static boolean depositsCursor(ItemStack hand, MonitorableAction action) {
        return hand != null
            && (hand.getItem() instanceof ItemMEStackPacket || action == MonitorableAction.PICKUP_OR_SET_DOWN
                || action == MonitorableAction.SPLIT_OR_PLACE_SINGLE
                || action == MonitorableAction.ROLL_DOWN);
    }

    static void execute(EntityPlayerMP player, MiniTerminalMonitorableAdapter terminal, IAEStack<?> target,
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

    static void extractInventory(EntityPlayerMP player, MiniTerminalMonitorableAdapter terminal, IAEItemStack target,
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
        Container open = player.openContainer;
        open.detectAndSendChanges();
        if (open != player.inventoryContainer) {
            player.inventoryContainer.detectAndSendChanges();
            if (!coversMainInventory(player, open)) {
                player.sendContainerToPlayer(player.inventoryContainer);
                return;
            }
        }
        CreativeCursor.sync(player);
    }

    private static boolean coversMainInventory(EntityPlayer player, Container container) {
        return COVERS_MAIN_INVENTORY.computeIfAbsent(container, key -> {
            boolean[] covered = new boolean[36];
            for (Object entry : key.inventorySlots) {
                Slot slot = (Slot) entry;
                int index = slot.getSlotIndex();
                if (slot.inventory == player.inventory && index >= 0 && index < covered.length) covered[index] = true;
            }
            for (int index = 0; index < covered.length; index++) {
                if (!covered[index]) return false;
            }
            return true;
        });
    }

    static void pickup(EntityItemPickupEvent event) {
        if (!(event.entityPlayer instanceof EntityPlayerMP player) || player.isDead
            || event.getResult() != Event.Result.DEFAULT) return;
        EntityItem entity = event.item;
        if (entity.isDead || entity.delayBeforeCanPickup > 0) return;

        String owner = entity.func_145798_i();
        if (owner != null && entity.lifespan - entity.age > 200 && !owner.equals(player.getCommandSenderName())) return;
        ItemStack picked = entity.getEntityItem();
        if (picked == null || picked.stackSize <= 0 || ItemMEMiniPortableTerminal.isMiniTerminal(picked)) return;

        List<Integer> slots = ItemMEMiniPortableTerminal.getTerminalSlots(player);
        for (int slot : slots) {
            ItemStack terminalStack = ItemMEMiniPortableTerminal.getTerminalStack(player, slot);
            if (!ItemMEMiniPortableTerminal.isPickupToMEEnabled(terminalStack)) continue;
            if (ItemMEMiniPortableTerminal.hasUniqueIdentity(player, terminalStack, slots))
                insertPickup(event, player, terminalStack, slot, picked);
            return;
        }
    }

    private static void insertPickup(EntityItemPickupEvent event, EntityPlayerMP player, ItemStack terminalStack,
        int slot, ItemStack picked) {
        WirelessTerminalGuiObject host = pickupHost(player, terminalStack, slot);
        if (MiniTerminalSessionService.networkStatus(player, host.getGrid(), host, SecurityPermissions.INJECT)
            != MiniTerminalSessionService.MiniTerminalStatus.ONLINE) return;
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

    private static WirelessTerminalGuiObject pickupHost(EntityPlayerMP player, ItemStack terminalStack, int slot) {
        WirelessTerminalGuiObject active = MiniTerminalSessionService.activeHost(player, terminalStack);
        if (active != null) return active;
        IWirelessTermHandler handler = (IWirelessTermHandler) terminalStack.getItem();
        String binding = handler.getEncryptionKey(terminalStack);
        PickupHost cached = PICKUP_HOSTS.get(player.getUniqueID());
        if (cached != null && cached.player() == player
            && cached.stack() == terminalStack
            && cached.slot() == slot
            && Objects.equals(cached.binding(), binding)
            && player.ticksExisted - cached.gridCheckTick() < 20) return cached.host();
        IGridNode node = MiniTerminalSessionService.resolveBindingNode(binding);
        IGrid grid = node == null ? null : node.getGrid();
        if (cached != null && cached.player() == player
            && cached.stack() == terminalStack
            && cached.slot() == slot
            && Objects.equals(cached.binding(), binding)
            && cached.host()
                .getGrid() == grid) {
            PICKUP_HOSTS.put(
                player.getUniqueID(),
                new PickupHost(player, terminalStack, slot, binding, cached.host(), player.ticksExisted));
            return cached.host();
        }
        WirelessTerminalGuiObject host = new WirelessTerminalGuiObject(
            handler,
            terminalStack,
            player,
            player.worldObj,
            slot,
            0,
            0);
        PICKUP_HOSTS
            .put(player.getUniqueID(), new PickupHost(player, terminalStack, slot, binding, host, player.ticksExisted));
        return host;
    }

    static void forget(EntityPlayer player) {
        PICKUP_HOSTS.remove(player.getUniqueID());
    }

    private record PickupHost(EntityPlayerMP player, ItemStack stack, int slot, String binding,
        WirelessTerminalGuiObject host, int gridCheckTick) {}

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
