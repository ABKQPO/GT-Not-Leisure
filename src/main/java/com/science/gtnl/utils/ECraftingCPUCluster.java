package com.science.gtnl.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.machine.multiblock.QuantumComputer;
import com.science.gtnl.utils.crafting.VariantBudgets;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
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

    private static final String NBT_VARIANT_BUDGETS = "gtnl:variantBudgets";

    @Nullable
    private QuantumComputer virtualCPUOwner = null;

    // AE2 takes the first variant covering an input, which may starve another input planned with that variant
    private final VariantBudgets variantBudgets = new VariantBudgets();
    private final VariantBudgets stagedVariantBudgets = new VariantBudgets();

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
        final boolean idle = this.tasks.isEmpty() && this.waitingFor.isEmpty();
        final ICraftingLink link = super.submitJob(g, job, src, requestingMachine);
        if (link != null) {
            // A merged job adds its budgets to those of the running one
            if (idle) this.variantBudgets.clear();
            this.variantBudgets.addAll(this.stagedVariantBudgets);
        }
        this.stagedVariantBudgets.clear();
        if (link != null && this.virtualCPUOwner != null && this.virtualCPUOwner.isVirtualCPU(this)) {
            this.virtualCPUOwner.onVirtualCPUSubmitJob(job.getByteTotal());
        }
        return link;
    }

    public void stageVariantBudget(final ICraftingPatternDetails details, final IAEStack<?> input,
        final List<IAEStack<?>> variants) {
        this.stagedVariantBudgets.add(details, input, variants);
    }

    public void consumeVariantBudget(final ICraftingPatternDetails details, final InventoryCrafting table,
        final List<IAEStack<?>> inputs) {
        this.variantBudgets.consume(details, table, inputs);
    }

    // Falls back to AE2's choice once the budget is used up, so an unforeseen inventory never stalls the job
    @Override
    protected ArrayList<IAEStack<?>> getExtractItems(IAEStack ingredient, ICraftingPatternDetails patternDetails) {
        final IItemList<IAEStack<?>> budget = patternDetails.canSubstitute() && ingredient instanceof IAEItemStack
            ? this.variantBudgets.get(patternDetails, ingredient)
            : null;
        if (budget != null) {
            final ArrayList<IAEStack<?>> planned = getBudgetedExtractItems(
                (IAEItemStack) ingredient,
                patternDetails,
                budget);
            if (!planned.isEmpty()) return planned;
            // The planned variants are still to be crafted: taking another one now could leave a later input short
            if (VariantBudgets.total(budget) >= ingredient.getStackSize()) return planned;
        }
        return super.getExtractItems(ingredient, patternDetails);
    }

    private ArrayList<IAEStack<?>> getBudgetedExtractItems(final IAEItemStack ingredient,
        final ICraftingPatternDetails details, final IItemList<IAEStack<?>> budget) {
        final ArrayList<IAEStack<?>> list = new ArrayList<>();
        long needed = ingredient.getStackSize();
        for (IAEItemStack fuzz : this.inventory.findFuzzy(ingredient, FuzzyMode.IGNORE_ALL)) {
            if (fuzz.getStackSize() <= 0) continue;
            final long allowed = VariantBudgets.left(budget, fuzz);
            // The CPU never splits the input of a processing craft between variants
            if (allowed <= 0 || !details.isCraftable() && allowed < needed) continue;
            if (details.isCraftable() && !isValidForSlot(details, ingredient, fuzz)) continue;
            final IAEItemStack request = fuzz.copy();
            request.setStackSize(details.isCraftable() ? Math.min(needed, allowed) : needed);
            final IAEItemStack available = this.inventory.extractItems(request, Actionable.SIMULATE);
            if (available == null || available.getStackSize() <= 0) continue;
            if (!details.isCraftable()) {
                if (available.getStackSize() == needed) {
                    list.add(available);
                    return list;
                }
                continue;
            }
            list.add(available);
            needed -= available.getStackSize();
            if (needed <= 0) return list;
        }
        return new ArrayList<>();
    }

    private boolean isValidForSlot(final ICraftingPatternDetails details, final IAEItemStack ingredient,
        final IAEItemStack candidate) {
        final IAEStack<?>[] slots = details.getAEInputs();
        for (int slot = 0; slot < slots.length; slot++) {
            if (slots[slot] != null && slots[slot].equals(ingredient)) {
                return details.isValidItemForSlot(slot, candidate, this.getWorld());
            }
        }
        return false;
    }

    public boolean hasVariantBudget(final ICraftingPatternDetails details) {
        return details.canSubstitute() && this.variantBudgets.has(details);
    }

    public long getBudgetedAvailable(final ICraftingPatternDetails details, final IAEStack<?> input) {
        final IItemList<IAEStack<?>> budget = input instanceof IAEItemStack ? this.variantBudgets.get(details, input)
            : null;
        if (budget == null) {
            final IAEStack<?> available = this.inventory.getAvailableItem(
                input.copy()
                    .setStackSize(1));
            return available == null ? 0 : available.getStackSize();
        }
        final IAEItemStack item = (IAEItemStack) input;
        long result = 0;
        for (IAEItemStack fuzz : this.inventory.findFuzzy(item, FuzzyMode.IGNORE_ALL)) {
            if (fuzz.getStackSize() <= 0) continue;
            if (details.isCraftable() && !isValidForSlot(details, item, fuzz)) continue;
            final long usable = Math.min(fuzz.getStackSize(), VariantBudgets.left(budget, fuzz));
            result = details.isCraftable() ? result + usable : Math.max(result, usable);
        }
        return result;
    }

    @Override
    protected void completeJob() {
        super.completeJob();
        if (this.isComplete) this.variantBudgets.clear();
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        if (!this.variantBudgets.isEmpty()) data.setTag(NBT_VARIANT_BUDGETS, this.variantBudgets.writeToNBT());
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.variantBudgets.readFromNBT(data.getTagList(NBT_VARIANT_BUDGETS, Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public void cancel() {
        super.cancel();
        this.variantBudgets.clear();
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
