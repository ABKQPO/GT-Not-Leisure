package com.science.gtnl.mixins.late.AppliedEnergistics;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import appeng.api.config.FullnessMode;
import appeng.api.config.OperationMode;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.storage.TileIOPort;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;

@Mixin(value = TileIOPort.class, remap = false)
public abstract class MixinTileIOPort extends AENetworkInvTile
    implements IUpgradeableHost, IConfigManagerHost, IGridTickable {

    @Final
    @Shadow
    private AppEngInternalInventory cells;
    @Final
    @Shadow
    private ConfigManager manager;
    @Shadow
    private int[] moveQueue;

    @Shadow
    public abstract boolean moveSlot(final int x);

    @Shadow
    public abstract IMEInventory getInv(final ItemStack is, final StorageChannel chan);

    @Shadow
    public abstract long transferContents(final IEnergySource energy, final IMEInventory src,
        final IMEInventory destination, long itemsToMove, final StorageChannel chan);

    @Shadow
    public abstract boolean shouldMove(final IMEInventory<IAEItemStack> itemInv,
        final IMEInventory<IAEFluidStack> fluidInv, final boolean didWork);

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        if (!this.getProxy()
            .isActive()) {
            return TickRateModulation.IDLE;
        }

        long ItemsToMove = 256;

        switch (this.getInstalledUpgrades(Upgrades.SPEED)) {
            case 1 -> ItemsToMove *= 2;
            case 2 -> ItemsToMove *= 4;
            case 3 -> ItemsToMove *= 8;
        }

        switch (this.getInstalledUpgrades(Upgrades.SUPERSPEED)) {
            case 1 -> ItemsToMove *= 16;
            case 2 -> ItemsToMove *= 128;
            case 3 -> ItemsToMove *= 1024;
        }

        switch (this.getInstalledUpgrades(Upgrades.SUPERLUMINALSPEED)) {
            case 1 -> ItemsToMove *= 131_072;
            case 2 -> ItemsToMove *= 8_388_608;
            case 3 -> ItemsToMove = Long.MAX_VALUE / 2;
        }

        long maxMoved = ItemsToMove;

        try {
            final IMEInventory<IAEItemStack> itemNet = this.getProxy()
                .getStorage()
                .getItemInventory();
            final IMEInventory<IAEFluidStack> fluidNet = this.getProxy()
                .getStorage()
                .getFluidInventory();
            final IEnergySource energy = this.getProxy()
                .getEnergy();
            for (int x = 0; x < 6; x++) {
                final ItemStack is = this.cells.getStackInSlot(x);
                if (is != null) {
                    if (this.manager.getSetting(Settings.FULLNESS_MODE) != FullnessMode.HALF && moveQueue[x] == 1) {
                        moveQueue[x] = !this.moveSlot(x) ? 1 : 0;
                    } else {
                        if (ItemsToMove > 0) {
                            final IMEInventory<IAEItemStack> itemInv = this.getInv(is, StorageChannel.ITEMS);
                            final IMEInventory<IAEFluidStack> fluidInv = this.getInv(is, StorageChannel.FLUIDS);

                            if (this.manager.getSetting(Settings.OPERATION_MODE) == OperationMode.EMPTY) {
                                if (itemInv != null) {
                                    ItemsToMove = this
                                        .transferContents(energy, itemInv, itemNet, ItemsToMove, StorageChannel.ITEMS);
                                }
                                if (fluidInv != null) {
                                    ItemsToMove = this.transferContents(
                                        energy,
                                        fluidInv,
                                        fluidNet,
                                        ItemsToMove,
                                        StorageChannel.FLUIDS);
                                }
                            } else {
                                if (itemInv != null) {
                                    ItemsToMove = this
                                        .transferContents(energy, itemNet, itemInv, ItemsToMove, StorageChannel.ITEMS);
                                }
                                if (fluidInv != null) {
                                    ItemsToMove = this.transferContents(
                                        energy,
                                        fluidNet,
                                        fluidInv,
                                        ItemsToMove,
                                        StorageChannel.FLUIDS);
                                }
                            }

                            // If work is done, check if the cell should be moved and try to move it to the output
                            // If the cell failed to move, queue moving the cell before doing any further work on it
                            if (ItemsToMove > 0) {
                                if (this.shouldMove(itemInv, fluidInv, ItemsToMove != maxMoved)) {
                                    moveQueue[x] = !this.moveSlot(x) ? 1 : 0;
                                    if (moveQueue[x] == 1) {
                                        return TickRateModulation.IDLE;
                                    }
                                } else {
                                    // Try moving something else instead
                                    if (this.manager.getSetting(Settings.FULLNESS_MODE) != FullnessMode.HALF) {
                                        for (int y = x + 1; y < 6; y++) {
                                            if (moveQueue[y] == 1) {
                                                final ItemStack is2 = this.cells.getStackInSlot(x);
                                                if (is != null) {
                                                    moveQueue[y] = !this.moveSlot(y) ? 1 : 0;
                                                    if (moveQueue[y] == 1) {
                                                        return TickRateModulation.IDLE;
                                                    } else {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            return TickRateModulation.URGENT;
                        } else {
                            return TickRateModulation.URGENT;
                        }
                    }
                }
            }
        } catch (final GridAccessException e) {
            return TickRateModulation.IDLE;
        }

        // nothing left to do...
        return TickRateModulation.SLEEP;
    }
}
