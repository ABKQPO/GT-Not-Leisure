package com.science.gtnl.utils;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.machine.multiblock.QuantumComputer;

import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.WorldCoord;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.crafting.TileCraftingTile;

/**
 * A {@link CraftingCPUCluster} that is driven by a {@link QuantumComputer} instead of physical crafting tiles.
 * While owned, the core-based accessors (core tile, grid, world, ...) are rerouted to the owner machine so the
 * cluster works without any attached {@link TileCraftingTile}.
 */
public class ECraftingCPUCluster extends CraftingCPUCluster {

    @Nullable
    private QuantumComputer virtualCPUOwner = null;

    public ECraftingCPUCluster(final WorldCoord min, final WorldCoord max) {
        super(min, max);
    }

    public void setAvailableStorage(final long availableStorage) {
        this.availableStorage = availableStorage;
    }

    public void setAccelerators(final int accelerators) {
        this.accelerator = accelerators;
    }

    @Nullable
    public QuantumComputer getVirtualCPUOwner() {
        return this.virtualCPUOwner;
    }

    public void setVirtualCPUOwner(@Nullable final QuantumComputer virtualCPUOwner) {
        this.virtualCPUOwner = virtualCPUOwner;
        this.machineSrc = new MachineSource(virtualCPUOwner);
    }

    public void markDestroyed() {
        this.isDestroyed = true;
        this.isComplete = true;
    }

    public void setName(String name) {
        this.myName = name;
    }

    /**
     * Promotes this virtual CPU to a real tracked CPU once its first job is accepted. Merged jobs on an already
     * promoted CPU must not trigger the handover again, hence the isVirtualCPU guard.
     */
    @Override
    public ICraftingLink submitJob(final IGrid g, final ICraftingJob job, final BaseActionSource src,
        final ICraftingRequester requestingMachine) {
        final ICraftingLink link = super.submitJob(g, job, src, requestingMachine);
        if (link != null && this.virtualCPUOwner != null && this.virtualCPUOwner.isVirtualCPU(this)) {
            this.virtualCPUOwner.onVirtualCPUSubmitJob(job.getByteTotal());
        }
        return link;
    }

    @Override
    public void cancel() {
        super.cancel();
        if (this.virtualCPUOwner == null) return;
        if (isInventoryEmpty()) destroy();
    }

    /**
     * Recreated instead of inherited: the owner has no crafting tiles, so the parent's {@code getCore().isActive()}
     * guard cannot run and the activity check goes through the owner's grid proxy instead.
     */
    @Override
    public void updateCraftingLogic(final IGrid grid, final IEnergyGrid eg, final CraftingGridCache cc) {
        if (this.virtualCPUOwner == null) {
            super.updateCraftingLogic(grid, eg, cc);
            return;
        }

        if (this.myLastLink != null) {
            if (this.myLastLink.isCanceled()) {
                this.myLastLink = null;
                this.cancel();
            }
        }

        if (this.isComplete && !this.virtualCPUOwner.isVirtualCPU(this)) {
            // Ensure inventory is empty
            if (isInventoryEmpty()) {
                destroy();
                return;
            }
        }

        if (!this.virtualCPUOwner.getProxy()
            .isActive()) {
            return;
        }

        if (this.isComplete) {
            if (this.inventory.isEmpty()) {
                return;
            }

            this.storeItems();
            return;
        }

        this.waiting = false;
        if (this.waiting || this.tasks.isEmpty()) // nothing to do here...
        {
            return;
        }

        this.remainingOperations = this.accelerator + 1 - (this.usedOps[0] + this.usedOps[1] + this.usedOps[2]);
        final int started = this.remainingOperations;

        // Shallow copy tasks so we may remove them after visiting
        this.workableTasks.clear();
        this.workableTasks.putAll(this.tasks);
        this.knownBusyMediums.clear();
        if (this.remainingOperations > 0) {
            do {
                this.somethingChanged = false;
                this.executeCrafting(eg, cc);
            } while (this.somethingChanged && this.remainingOperations > 0);
        }
        this.usedOps[2] = this.usedOps[1];
        this.usedOps[1] = this.usedOps[0];
        this.usedOps[0] = started - this.remainingOperations;

        this.knownBusyMediums.clear();

        if (this.remainingOperations > 0 && !this.somethingChanged) {
            this.waiting = true;
        }
    }

    @Override
    public void destroy() {
        if (this.virtualCPUOwner == null) {
            super.destroy();
            return;
        }
        if (this.isDestroyed) {
            return;
        }
        this.virtualCPUOwner.onCPUDestroyed(this);
        super.destroy();
    }

    @Override
    public boolean isActive() {
        if (this.virtualCPUOwner == null) {
            return super.isActive();
        }
        return this.virtualCPUOwner.getProxy()
            .isActive();
    }

    @Override
    public IGrid getGrid() {
        if (this.virtualCPUOwner == null) {
            return super.getGrid();
        }
        IGridNode node = this.virtualCPUOwner.getProxy()
            .getNode();
        return node == null ? null : node.getGrid();
    }

    @Override
    protected TileCraftingTile getCore() {
        if (this.virtualCPUOwner == null) {
            return super.getCore();
        }
        return null;
    }

    @Override
    protected World getWorld() {
        if (this.virtualCPUOwner == null) {
            return super.getWorld();
        }
        return this.virtualCPUOwner.getBaseMetaTileEntity()
            .getWorld();
    }

    @Override
    public void markDirty() {
        if (this.virtualCPUOwner == null) {
            super.markDirty();
            return;
        }
        this.virtualCPUOwner.markDirty();
    }

    private boolean isInventoryEmpty() {
        IItemList<IAEItemStack> itemList = AEApi.instance()
            .storage()
            .createItemList();
        this.inventory.getAvailableItems(itemList);
        return itemList.isEmpty();
    }
}
