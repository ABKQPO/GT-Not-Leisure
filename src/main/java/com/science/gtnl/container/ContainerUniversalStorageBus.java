package com.science.gtnl.container;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import com.science.gtnl.common.part.PartUniversalStorageBus;

import appeng.api.config.AccessRestriction;
import appeng.api.config.ActionItems;
import appeng.api.config.ExtractionMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.Upgrades;
import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.IConfigManager;
import appeng.container.implementations.ContainerUpgradeable;
import appeng.container.interfaces.IVirtualSlotHolder;
import appeng.container.slot.SlotRestrictedInput;
import appeng.container.sync.SyncCodecs;
import appeng.container.sync.SyncRegistrar;
import appeng.container.sync.handlers.ConfigEnumSyncHandler;
import appeng.container.sync.handlers.ObjectSyncHandler;
import appeng.tile.inventory.IAEStackInventory;
import appeng.util.Platform;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class ContainerUniversalStorageBus extends ContainerUpgradeable implements IVirtualSlotHolder {

    /** Pending partition pages per player, so pressing the button again shows the next page. */
    private static final Map<EntityPlayer, Iterator<IAEStack<?>>> PARTITION_ITERATORS = new WeakHashMap<>();

    private final PartUniversalStorageBus storageBus;
    private final IAEStack<?>[] configClientSlot = new IAEStack[PartUniversalStorageBus.CONFIG_SIZE];

    public AccessRestriction rwMode = AccessRestriction.READ_WRITE;

    public StorageFilter storageFilter = StorageFilter.EXTRACTABLE_ONLY;

    public ActionItems partitionMode;

    public ExtractionMode extractionMode = ExtractionMode.LOOSE;

    public AccessRestriction reshuffleAccess = AccessRestriction.READ_WRITE;

    private final ConfigEnumSyncHandler<AccessRestriction> rwModeSync;
    private final ConfigEnumSyncHandler<StorageFilter> storageFilterSync;
    private final ObjectSyncHandler<ActionItems> partitionModeSync;
    private final ConfigEnumSyncHandler<ExtractionMode> extractionModeSync;
    private final ConfigEnumSyncHandler<AccessRestriction> reshuffleAccessSync;

    public ContainerUniversalStorageBus(InventoryPlayer ip, PartUniversalStorageBus te) {
        super(ip, te);
        this.storageBus = te;
        this.partitionMode = PARTITION_ITERATORS.containsKey(ip.player) ? ActionItems.NEXT_PARTITION
            : ActionItems.WRENCH;

        SyncRegistrar sync = this.syncRegistrar();
        this.rwModeSync = sync.configEnum(
            "rwMode",
            Settings.ACCESS,
            AccessRestriction.class,
            this.getUpgradeable()
                .getConfigManager());
        this.storageFilterSync = sync.configEnum(
            "storageFilter",
            Settings.STORAGE_FILTER,
            StorageFilter.class,
            this.getUpgradeable()
                .getConfigManager());
        this.partitionModeSync = sync
            .objectS2C("partitionMode", SyncCodecs.enumValue(ActionItems.class), this.partitionMode);
        this.extractionModeSync = sync.configEnum(
            "extractionMode",
            Settings.EXTRACTION_MODE,
            ExtractionMode.class,
            this.getUpgradeable()
                .getConfigManager());
        this.reshuffleAccessSync = sync.configEnum(
            "reshuffleAccess",
            Settings.RESHUFFLE_ACCESS,
            AccessRestriction.class,
            this.getUpgradeable()
                .getConfigManager());

        this.rwModeSync.onClientChange((oldValue, newValue) -> this.rwMode = newValue);
        this.storageFilterSync.onClientChange((oldValue, newValue) -> this.storageFilter = newValue);
        this.partitionModeSync.onClientChange((oldValue, newValue) -> this.partitionMode = newValue);
        this.extractionModeSync.onClientChange((oldValue, newValue) -> this.extractionMode = newValue);
        this.reshuffleAccessSync.onClientChange((oldValue, newValue) -> this.reshuffleAccess = newValue);
    }

    @Override
    public int getHeight() {
        return 251;
    }

    @Override
    public void setupConfig() {
        final IInventory upgrades = this.getUpgradeable()
            .getInventoryByName("upgrades");
        for (int slot = 0; slot < this.availableUpgrades(); slot++) {
            this.addSlotToContainer(
                new SlotRestrictedInput(
                    SlotRestrictedInput.PlacableItemType.UPGRADES,
                    upgrades,
                    slot,
                    187,
                    8 + 18 * slot,
                    this.getInventoryPlayer()).setNotDraggable());
        }
    }

    @Override
    public int availableUpgrades() {
        return 5;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            final IConfigManager cm = this.getUpgradeable()
                .getConfigManager();
            this.fzMode = (FuzzyMode) cm.getSetting(Settings.FUZZY_MODE);
            this.rwMode = (AccessRestriction) cm.getSetting(Settings.ACCESS);
            this.storageFilter = (StorageFilter) cm.getSetting(Settings.STORAGE_FILTER);
            this.extractionMode = (ExtractionMode) cm.getSetting(Settings.EXTRACTION_MODE);
            this.reshuffleAccess = (AccessRestriction) cm.getSetting(Settings.RESHUFFLE_ACCESS);

            this.rwModeSync.syncFromConfig();
            this.storageFilterSync.syncFromConfig();
            this.extractionModeSync.syncFromConfig();
            this.reshuffleAccessSync.syncFromConfig();
            this.partitionModeSync.set(this.partitionMode);

            this.updateVirtualSlots(StorageName.CONFIG, this.getConfig(), this.configClientSlot);
        }

        this.standardDetectAndSendChanges();
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        return this.getUpgradeable()
            .getInstalledUpgrades(Upgrades.CAPACITY) > idx - 2;
    }

    public void clear() {
        final IAEStackInventory inv = this.getConfig();
        for (int x = 0; x < inv.getSizeInventory(); x++) {
            inv.putAEStackInSlot(x, null);
        }
        this.detectAndSendChanges();
    }

    public void partition(boolean clearIterator) {
        final EntityPlayer player = this.getInventoryPlayer().player;
        if (clearIterator) {
            this.clearPartitionIterator(player);
            return;
        }

        Iterator<IAEStack<?>> iterator = PARTITION_ITERATORS.get(player);
        if (iterator == null) {
            final List<IAEStack<?>> contents = this.storageBus.getTargetContents();
            if (contents.isEmpty()) {
                this.clearPartitionIterator(player);
                return;
            }
            iterator = contents.iterator();
            PARTITION_ITERATORS.put(player, iterator);
            this.partitionMode = ActionItems.NEXT_PARTITION;
        }

        final IAEStackInventory inv = this.getConfig();
        boolean skip = false;
        for (int x = 0; x < inv.getSizeInventory(); x++) {
            if (!skip && this.isSlotEnabled(x / 9) && iterator.hasNext()) {
                final IAEStack<?> stack = iterator.next()
                    .copy();
                stack.setStackSize(1);
                inv.putAEStackInSlot(x, stack);
            } else {
                skip = true;
                inv.putAEStackInSlot(x, null);
            }
        }

        if (!iterator.hasNext()) this.clearPartitionIterator(player);
        this.detectAndSendChanges();
    }

    private void clearPartitionIterator(EntityPlayer player) {
        PARTITION_ITERATORS.remove(player);
        this.partitionMode = ActionItems.WRENCH;
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
        return this.storageBus.getAEInventoryByName(StorageName.CONFIG);
    }

    public PartUniversalStorageBus getStorageBus() {
        return this.storageBus;
    }
}
