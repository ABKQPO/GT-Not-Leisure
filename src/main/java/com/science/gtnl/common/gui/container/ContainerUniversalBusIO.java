package com.science.gtnl.common.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEStack;
import appeng.container.implementations.ContainerUpgradeable;
import appeng.container.interfaces.IVirtualSlotHolder;
import appeng.parts.automation.PartSharedItemBus;
import appeng.tile.inventory.IAEStackInventory;
import appeng.util.Platform;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class ContainerUniversalBusIO extends ContainerUpgradeable implements IVirtualSlotHolder {

    private final PartSharedItemBus<?> bus;
    private final IAEStack<?>[] configClientSlot = new IAEStack<?>[9];

    public ContainerUniversalBusIO(InventoryPlayer ip, PartSharedItemBus<?> te) {
        super(ip, te);
        this.bus = te;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (Platform.isServer()) {
            this.updateVirtualSlots(StorageName.CONFIG, this.getConfig(), this.configClientSlot);
        }
    }

    @Override
    public void receiveSlotStacks(StorageName invName, Int2ObjectMap<IAEStack<?>> slotStacks) {
        final IAEStackInventory config = this.getConfig();
        for (var entry : slotStacks.int2ObjectEntrySet()) {
            config.putAEStackInSlot(entry.getIntKey(), entry.getValue());
        }
        if (Platform.isServer()) {
            this.updateVirtualSlots(StorageName.CONFIG, config, this.configClientSlot);
        }
    }

    public IAEStackInventory getConfig() {
        return this.bus.getAEInventoryByName(StorageName.CONFIG);
    }

    public PartSharedItemBus<?> getBus() {
        return this.bus;
    }
}
