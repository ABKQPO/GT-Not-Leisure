package com.science.gtnl.common.meminiterminal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.config.PinsRows;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.helpers.MonitorableAction;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;

public final class MiniTerminalMonitorableAdapter extends ContainerMEMonitorable implements AutoCloseable {

    private final EntityPlayerMP owner;
    private final List<IMEMonitor<?>> monitors = new ArrayList<>();
    private final IEnergySource guardedPower;
    private MiniTerminalCatalog catalog;
    private OperationContext operation;
    private boolean closed;
    private boolean pinsChanged;

    public MiniTerminalMonitorableAdapter(EntityPlayerMP player, WirelessTerminalGuiObject host) {
        super(player.inventory, host, false);
        owner = player;

        for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
            IMEMonitor<?> monitor = host.getMEMonitor(type);
            if (monitor != null) monitors.add(monitor);
        }
        guardedPower = guardPower(super.getPowerSource(), () -> operation != null, this::isOperationValid);
    }

    public void execute(MonitorableAction action, IAEStack<?> target, int pin, BooleanSupplier valid,
        InventoryAdaptor destination) {
        run(valid, destination, () -> {
            setTargetStack(target == null ? null : target.copy());
            doMonitorableAction(action, pin, owner);
            return null;
        }, null);
    }

    public ItemStack store(ItemStack reserved, BooleanSupplier valid) {
        if (reserved == null || reserved.stackSize <= 0) return reserved;
        return run(valid, null, () -> super.shiftStoreItem(reserved), reserved);
    }

    public boolean setPinSettings(MiniTerminalPinSettings settings, BooleanSupplier valid) {
        return run(valid, null, () -> {
            // Same guard as setPinsRows, so the session only saves counts that AE actually accepted.
            if (!isOperationValid()) return false;
            setPinsRows(settings.craftingRows(), settings.playerRows());
            // An exact count can change without crossing the boundary of an AE row.
            pinsChanged = true;
            return true;
        }, false);
    }

    public void pin(IAEStack<?> target, int index, BooleanSupplier valid) {
        run(valid, null, () -> {
            setPin(target, index);
            return null;
        }, null);
    }

    private <T> T run(BooleanSupplier valid, InventoryAdaptor destination, Supplier<T> action, T rejected) {
        if (closed || operation != null || !valid.getAsBoolean()) return rejected;
        operation = new OperationContext(valid, destination);
        try {
            return action.get();
        } finally {
            setTargetStack(null);
            operation = null;
        }
    }

    public boolean isOperationValid() {
        return !closed && operation != null && operation.valid.getAsBoolean();
    }

    public InventoryAdaptor inventoryDestination() {
        return operation == null ? null : operation.destination;
    }

    public void refund(ItemStack remainder) {
        if (remainder == null || remainder.stackSize <= 0) return;
        IAEItemStack returned = getItemMonitor()
            .injectItems(AEItemStack.create(remainder), Actionable.MODULATE, getActionSource());
        if (returned != null) Platform.addToPlayerInvOrDrop(owner, returned.getItemStack());
    }

    static IEnergySource guardPower(IEnergySource source, BooleanSupplier active, BooleanSupplier valid) {
        return (amount, mode, multiplier) -> {
            if (source == null || !active.getAsBoolean()) return 0;
            if (mode == Actionable.SIMULATE && !valid.getAsBoolean()) return 0;

            return source.extractAEPower(amount, mode, multiplier);
        };
    }

    @Override
    public IEnergySource getPowerSource() {

        return guardedPower == null ? super.getPowerSource() : guardedPower;
    }

    @Override
    public void doMonitorableAction(MonitorableAction action, int custom, EntityPlayerMP player) {
        if (player == owner && isOperationValid()) super.doMonitorableAction(action, custom, player);
    }

    @Override
    public void setPin(IAEStack<?> stack, int index) {
        if (!isOperationValid()) return;
        super.setPin(stack, index);
        pinsChanged = true;
    }

    @Override
    public void setPinsRows(PinsRows crafting, PinsRows player) {
        if (!isOperationValid()) return;
        if (crafting == getCraftingPinsRows() && player == getPlayerPinsRows()) return;
        super.setPinsRows(crafting, player);
        pinsChanged = true;
    }

    public boolean consumePinsChanged() {
        boolean changed = pinsChanged;
        pinsChanged = false;
        return changed;
    }

    void observeCatalog(MiniTerminalCatalog catalog) {
        this.catalog = closed ? null : catalog;
    }

    @Override
    protected void updateHeld(EntityPlayerMP player) {

    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        throw new IllegalStateException("A mini terminal operation adapter cannot own a container window");
    }

    @Override
    public void detectAndSendChanges() {

    }

    @Override
    protected void portableSourceTick() {

    }

    @Override
    protected void checkItem(Object target) {

    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return false;
    }

    @Override
    public void postChange(IBaseMonitor<IAEStack<?>> monitor, Iterable<IAEStack<?>> changes, BaseActionSource source) {
        if (closed || catalog == null) return;
        for (IAEStack<?> changed : changes) {
            catalog.changed(changed);
        }
    }

    @Override
    public void onListUpdate() {
        if (!closed && catalog != null) catalog.onListUpdate();
    }

    @Override
    public boolean isValid(Object token) {
        return !closed;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        close();
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        catalog = null;
        setValidContainer(false);
        try {

            for (IMEMonitor<?> monitor : monitors) {
                monitor.removeListener(this);
            }
        } finally {
            monitors.clear();
            setTargetStack(null);
        }
    }

    private record OperationContext(BooleanSupplier valid, InventoryAdaptor destination) {}
}
