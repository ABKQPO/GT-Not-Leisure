package com.science.gtnl.common.part;

import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.common.part.universal.UniversalBusHelper;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.me.GridAccessException;
import appeng.parts.automation.PartBaseImportBus;
import appeng.util.InventoryAdaptor;
import appeng.util.Platform;
import appeng.util.inv.IInventoryDestination;
import appeng.util.inv.ItemSlot;
import appeng.util.item.AEItemStack;
import appeng.util.prioitylist.OreFilteredList;

public class PartUniversalImportBus extends PartBaseImportBus<IAEItemStack> {

    public PartUniversalImportBus(ItemStack is) {
        super(is);
    }

    @Override
    public Object getTarget() {
        final TileEntity self = this.getHost()
            .getTile();
        return this.getTileEntity(
            self,
            self.xCoord + this.getSide().offsetX,
            self.yCoord + this.getSide().offsetY,
            self.zCoord + this.getSide().offsetZ);
    }

    @Override
    public TickRateModulation doBusWork() {
        if (!this.getProxy()
            .isActive() || !this.canDoBusWork()) {
            return TickRateModulation.IDLE;
        }
        if (!(this.getTarget() instanceof TileEntity target)) {
            return TickRateModulation.SLEEP;
        }

        this.worked = false;
        try {
            final IStorageGrid storage = this.getProxy()
                .getStorage();
            final IEnergyGrid energy = this.getProxy()
                .getEnergy();
            final ForgeDirection targetSide = this.getSide()
                .getOpposite();
            final long operations = UniversalBusHelper.calculateOperations(this);
            final Map<IAEStackType<?>, List<IAEStack<?>>> configured = UniversalBusHelper
                .groupConfig(this.getAEInventoryByName(StorageName.CONFIG), this.availableSlots());
            // With an ore filter card the items are filtered by the ore expression, item config slots are ignored.
            final Predicate<IAEItemStack> orePredicate = this.getOrePredicate();
            if (this.getInstalledUpgrades(Upgrades.ORE_FILTER) > 0) configured.remove(ITEM_STACK_TYPE);
            final boolean anyConfigured = !configured.isEmpty() || orePredicate != null;

            for (IAEStackType<?> type : UniversalBusHelper.getTypes()) {
                final List<IAEStack<?>> filters = configured.get(type);
                final boolean itemsByOre = type == ITEM_STACK_TYPE && orePredicate != null;
                if (anyConfigured && filters == null && !itemsByOre) continue;

                final IMEMonitor<?> network = storage.getMEMonitor(type);
                if (network == null) continue;
                final long budget = operations * type.getAmountPerUnit();

                if (itemsByOre) {
                    this.importItemsByOre(
                        target,
                        targetSide,
                        orePredicate,
                        (IMEMonitor<IAEItemStack>) network,
                        energy,
                        budget);
                } else if (type == ITEM_STACK_TYPE) {
                    this.importItems(target, targetSide, filters, (IMEMonitor<IAEItemStack>) network, energy, budget);
                } else {
                    this.importGeneric(target, targetSide, type, filters, network, energy, budget);
                }
            }
        } catch (final GridAccessException ignored) {}

        return this.worked ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
    }

    private void importItems(TileEntity target, ForgeDirection targetSide, List<IAEStack<?>> filters,
        IMEMonitor<IAEItemStack> network, IEnergySource energy, long budget) {
        final InventoryAdaptor adaptor = InventoryAdaptor
            .getAdaptor(target, targetSide, InventoryAdaptor.ALLOW_ITEMS | InventoryAdaptor.FOR_EXTRACTS);
        if (adaptor == null) return;

        final boolean fuzzy = this.getInstalledUpgrades(Upgrades.FUZZY) > 0;
        final FuzzyMode fuzzyMode = (FuzzyMode) this.getConfigManager()
            .getSetting(Settings.FUZZY_MODE);
        final IInventoryDestination destination = this.createDestination(network);

        final List<IAEStack<?>> targets = filters == null ? Collections.singletonList(null) : filters;
        for (IAEStack<?> filter : targets) {
            final ItemStack filterStack = filter instanceof IAEItemStack ais ? ais.getItemStack() : null;
            while (budget > 0) {
                final long moved = this
                    .importItemBatch(adaptor, filterStack, fuzzy, fuzzyMode, destination, network, energy, budget);
                if (moved <= 0) break;
                budget -= moved;
                this.worked = true;
            }
            if (budget <= 0) return;
        }
    }

    private void importItemsByOre(TileEntity target, ForgeDirection targetSide, Predicate<IAEItemStack> orePredicate,
        IMEMonitor<IAEItemStack> network, IEnergySource energy, long budget) {
        final InventoryAdaptor adaptor = InventoryAdaptor
            .getAdaptor(target, targetSide, InventoryAdaptor.ALLOW_ITEMS | InventoryAdaptor.FOR_EXTRACTS);
        if (adaptor == null) return;

        // Collect first, removing items while iterating the adaptor is not safe
        final List<ItemStack> matches = new ArrayList<>();
        for (ItemSlot slot : adaptor) {
            if (!slot.isExtractable() || slot.getItemStack() == null) continue;
            if (orePredicate.test(slot.getAEItemStack())) matches.add(
                slot.getItemStack()
                    .copy());
        }

        final IInventoryDestination destination = this.createDestination(network);
        for (ItemStack filter : matches) {
            while (budget > 0) {
                final long moved = this.importItemBatch(
                    adaptor,
                    filter,
                    false,
                    FuzzyMode.IGNORE_ALL,
                    destination,
                    network,
                    energy,
                    budget);
                if (moved <= 0) break;
                budget -= moved;
                this.worked = true;
            }
            if (budget <= 0) return;
        }
    }

    private IInventoryDestination createDestination(IMEMonitor<IAEItemStack> network) {
        return stack -> {
            if (stack == null || stack.getItem() == null) return false;
            final IAEItemStack left = network.injectItems(AEItemStack.create(stack), Actionable.SIMULATE, this.mySrc);
            return left == null || left.getStackSize() != stack.stackSize;
        };
    }

    /**
     * Same semantics as the AE2 import bus: an empty expression means no ore filtering.
     */
    private Predicate<IAEItemStack> getOrePredicate() {
        if (this.getInstalledUpgrades(Upgrades.ORE_FILTER) == 0 || this.oreFilterString.isEmpty()) return null;
        if (this.filterPredicate == null) this.filterPredicate = OreFilteredList.makeFilter(this.oreFilterString);
        return this.filterPredicate;
    }

    @Override
    public void setFilter(String filter) {
        super.setFilter(filter == null ? "" : filter);
        this.saveChanges();
    }

    private long importItemBatch(InventoryAdaptor adaptor, ItemStack filter, boolean fuzzy, FuzzyMode fuzzyMode,
        IInventoryDestination destination, IMEMonitor<IAEItemStack> network, IEnergySource energy, long budget) {
        final int toSend = (int) Math.min(budget, 64);
        final ItemStack simulated = fuzzy && filter != null
            ? adaptor.simulateSimilarRemove(toSend, filter, fuzzyMode, destination)
            : adaptor.simulateRemove(toSend, filter, destination);
        if (simulated == null || simulated.stackSize <= 0) return 0;

        final IAEItemStack notStored = network
            .injectItems(AEItemStack.create(simulated), Actionable.SIMULATE, this.mySrc);
        long canStore = simulated.stackSize - (notStored == null ? 0 : notStored.getStackSize());
        canStore = Math.min(
            canStore,
            (long) (energy.extractAEPower(canStore, Actionable.SIMULATE, PowerMultiplier.CONFIG) + 0.01));
        if (canStore <= 0) return 0;

        // Remove exactly the simulated item, also when the filter was empty or fuzzy.
        final ItemStack removed = adaptor.removeItems((int) canStore, simulated, destination);
        if (removed == null || removed.stackSize <= 0) return 0;

        final IAEItemStack left = Platform.poweredInsert(energy, network, AEItemStack.create(removed), this.mySrc);
        if (left == null || left.getStackSize() <= 0) return removed.stackSize;

        final ItemStack notReturned = adaptor.addItems(left.getItemStack());
        if (notReturned != null && notReturned.stackSize > 0) {
            final IAEItemStack lost = network
                .injectItems(AEItemStack.create(notReturned), Actionable.MODULATE, this.mySrc);
            if (lost != null && lost.getStackSize() > 0) {
                final TileEntity self = this.getHost()
                    .getTile();
                Platform.spawnDrops(
                    self.getWorldObj(),
                    self.xCoord,
                    self.yCoord,
                    self.zCoord,
                    Collections.singletonList(lost.getItemStack()));
            }
        }
        return removed.stackSize - left.getStackSize();
    }

    @SuppressWarnings("rawtypes")
    private void importGeneric(TileEntity target, ForgeDirection targetSide, IAEStackType<?> type,
        List<IAEStack<?>> filters, IMEMonitor<?> network, IEnergySource energy, long budget) {
        final IMEInventory external = UniversalBusHelper.getTransferInventory(target, targetSide, type, this.mySrc);
        if (external == null) return;

        final List<IAEStack<?>> candidates = filters != null ? filters
            : UniversalBusHelper.listAvailable(external, type);
        for (IAEStack<?> candidate : candidates) {
            if (budget <= 0) return;
            final long moved = UniversalBusHelper.importInto(energy, external, network, candidate, budget, this.mySrc);
            if (moved > 0) {
                budget -= moved;
                this.worked = true;
            }
        }
    }

    @Override
    public boolean importStuff(Object myTarget, IAEItemStack whatToImport, IMEMonitor<IAEItemStack> inv,
        IEnergySource energy, FuzzyMode fzMode) {
        return true;
    }

    @Override
    public boolean doOreDict(Object myTarget, IMEMonitor<IAEItemStack> inv, IEnergyGrid energy, FuzzyMode fzMode) {
        return false;
    }

    @Override
    public int getPowerMultiplier() {
        return 1;
    }

    @Override
    public int calculateAmountToSend() {
        return (int) Math.min(Integer.MAX_VALUE, UniversalBusHelper.calculateOperations(this));
    }

    @Override
    public IMEMonitor<IAEItemStack> getMonitor() {
        try {
            return this.getProxy()
                .getStorage()
                .getItemInventory();
        } catch (final GridAccessException e) {
            return null;
        }
    }

    @Override
    public IAEStackType<IAEItemStack> getStackType() {
        return ITEM_STACK_TYPE;
    }

    @Override
    public void saveAEStackInv() {
        this.saveChanges();
    }

    @Override
    public boolean supportFuzzy() {
        return true;
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final Vec3 pos) {
        if (player.isSneaking()) return false;
        if (Platform.isClient()) return true;
        CommonProxy.openGui(
            player,
            GuiType.UniversalBusGUI,
            this.getSide(),
            this.getHost()
                .getTile());
        return true;
    }
}
