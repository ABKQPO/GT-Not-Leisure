package com.science.gtnl.common.part;

import static appeng.util.Platform.readStackNBT;
import static appeng.util.Platform.writeStackNBT;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.api.ICustomGui;
import com.science.gtnl.common.gui.GTNLGuiHandler;
import com.science.gtnl.common.part.universal.UniversalBusHelper;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.AccessRestriction;
import appeng.api.config.ExtractionMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.Upgrades;
import appeng.api.config.YesNo;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.IStorageBus;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageBusMonitor;
import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import appeng.api.util.IConfigManager;
import appeng.client.texture.CableBusTextures;
import appeng.core.settings.TickRates;
import appeng.hooks.TickHandler;
import appeng.me.GridAccessException;
import appeng.me.storage.MEInventoryHandler;
import appeng.me.storage.MEMonitorPassThrough;
import appeng.me.storage.StorageBusInventoryHandler;
import appeng.parts.automation.PartUpgradeable;
import appeng.tile.inventory.IAEStackInventory;
import appeng.util.IterationCounter;
import appeng.util.Platform;
import appeng.util.prioitylist.FuzzyPriorityList;
import appeng.util.prioitylist.IPartitionList;
import appeng.util.prioitylist.OreFilteredList;
import appeng.util.prioitylist.PrecisePriorityList;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class PartUniversalStorageBus extends PartUpgradeable implements IStorageBus, ICustomGui {

    public static final int CONFIG_SIZE = 63;
    public static final int BASE_SLOTS = 18;
    public static final String NBT_FILTER = "filter";
    public static final String NBT_FILTER_CACHE = "filterCache";
    public static final int MAX_DELAYED_RETRIES = 20;

    public final BaseActionSource source;
    public final IAEStack<?>[] filterCache = new IAEStack<?>[CONFIG_SIZE - BASE_SLOTS];
    public final IAEStackInventory config = new IAEStackInventory(this, CONFIG_SIZE) {

        @Override
        public void putAEStackInSlot(int n, IAEStack<?> aes) {
            super.putAEStackInSlot(n, aes);
            if (n >= BASE_SLOTS && n < BASE_SLOTS + getInstalledUpgrades(Upgrades.CAPACITY) * 9) {
                filterCache[n - BASE_SLOTS] = aes;
            }
            resetCache(true);
        }
    };

    public final Map<IAEStackType<?>, TypeChannel> channels = new Object2ObjectLinkedOpenHashMap<>();
    public final Set<IAEStackType<?>> readOncePass = new ObjectOpenHashSet<>();

    public int priority = 0;
    public boolean cached = false;
    public int handlerHash = 0;
    public boolean wasActive = false;
    public byte resetCacheLogic = 0;
    public boolean pendingDelayedCacheReset = false;
    public int delayedRetries = 0;
    public boolean needSyncGUI = false;
    public String oreFilterString = "";
    public String previousOreFilterString = "";

    public PartUniversalStorageBus(ItemStack is) {
        super(is);
        this.getConfigManager()
            .registerSetting(Settings.ACCESS, AccessRestriction.READ_WRITE);
        this.getConfigManager()
            .registerSetting(Settings.RESHUFFLE_ACCESS, AccessRestriction.READ_WRITE);
        this.getConfigManager()
            .registerSetting(Settings.EXTRACTION_MODE, ExtractionMode.LOOSE);
        this.getConfigManager()
            .registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.getConfigManager()
            .registerSetting(Settings.STORAGE_FILTER, StorageFilter.EXTRACTABLE_ONLY);
        this.getConfigManager()
            .registerSetting(Settings.STICKY_MODE, YesNo.NO);
        this.source = new MachineSource(this);

        // Restore the settings stored on a wrenched bus
        final NBTTagCompound tag = is.getTagCompound();
        if (tag != null) {
            if (tag.hasKey("priority")) this.priority = tag.getInteger("priority");
            if (tag.hasKey("config")) this.config.readFromNBT(tag, "config");
            if (tag.hasKey(NBT_FILTER)) this.previousOreFilterString = tag.getString(NBT_FILTER);
            if (tag.hasKey(NBT_FILTER_CACHE)) this.readFilterCache(tag.getCompoundTag(NBT_FILTER_CACHE));
            if (tag.hasKey("configManager")) this.getConfigManager()
                .readFromNBT(tag.getCompoundTag("configManager"));
            final String customName = is.hasDisplayName() ? is.getDisplayName() : null;
            this.is.setTagCompound(null);
            if (customName != null) this.setCustomName(customName);
        }
    }

    @Override
    public ItemStack getItemStack(final PartItemStack type) {
        if (type == PartItemStack.Wrench) {
            final NBTTagCompound tag = new NBTTagCompound();
            if (!this.config.isEmpty()) this.config.writeToNBT(tag, "config");
            if (this.priority != 0) tag.setInteger("priority", this.priority);
            if (!this.oreFilterString.isEmpty()) tag.setString(NBT_FILTER, this.oreFilterString);

            final NBTTagCompound filterCacheTag = new NBTTagCompound();
            this.writeFilterCache(filterCacheTag);
            tag.setTag(NBT_FILTER_CACHE, filterCacheTag);

            final NBTTagCompound configManagerTag = new NBTTagCompound();
            this.getConfigManager()
                .writeToNBT(configManagerTag);
            tag.setTag("configManager", configManagerTag);

            final ItemStack copy = this.is.copy();
            copy.setTagCompound(tag);
            if (this.hasCustomName()) copy.setStackDisplayName(this.getCustomName());
            return copy;
        }
        return super.getItemStack(type);
    }

    @Override
    @MENetworkEventSubscribe
    public void powerRender(final MENetworkPowerStatusChange c) {
        this.updateStatus();
    }

    @Override
    @MENetworkEventSubscribe
    public void chanRender(final MENetworkChannelsChanged changedChannels) {
        this.updateStatus();
    }

    public void updateStatus() {
        final boolean currentActive = this.getProxy()
            .isActive();
        if (this.wasActive != currentActive) {
            this.wasActive = currentActive;
            try {
                this.getProxy()
                    .getGrid()
                    .postEvent(new MENetworkCellArrayUpdate());
                this.getHost()
                    .markForUpdate();
            } catch (final GridAccessException ignored) {}
        }
    }

    @Override
    protected int getUpgradeSlots() {
        return 5;
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {
        final IPartHost host = this.getHost();
        if (host != null) {
            this.resetCache(true);
            host.markForSave();
        }
    }

    @Override
    public void upgradesChanged() {
        super.upgradesChanged();
        if (this.getInstalledUpgrades(Upgrades.ORE_FILTER) == 0) {
            if (!this.oreFilterString.isEmpty()) this.previousOreFilterString = this.oreFilterString;
            this.oreFilterString = "";
        } else if (this.oreFilterString.isEmpty()) {
            this.oreFilterString = this.previousOreFilterString;
        }
        for (int x = 0; x < this.getInstalledUpgrades(Upgrades.CAPACITY) * 9; x++) {
            final IAEStack<?> aes = this.filterCache[x];
            if (aes != null) this.config.putAEStackInSlot(x + BASE_SLOTS, aes);
        }
        this.needSyncGUI = true;
        this.resetCache(true);
    }

    public int getActiveConfigSlots() {
        return BASE_SLOTS + this.getInstalledUpgrades(Upgrades.CAPACITY) * 9;
    }

    @Override
    public boolean needSyncGUI() {
        return this.needSyncGUI;
    }

    @Override
    public void setNeedSyncGUI(boolean needSyncGUI) {
        this.needSyncGUI = needSyncGUI;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(final int newValue) {
        this.priority = newValue;
        this.getHost()
            .markForSave();
        this.resetCache(true);
    }

    public AccessRestriction getReshuffleAccess() {
        return (AccessRestriction) this.getConfigManager()
            .getSetting(Settings.RESHUFFLE_ACCESS);
    }

    @Override
    public String getFilter() {
        return this.oreFilterString;
    }

    @Override
    public void setFilter(final String filter) {
        this.oreFilterString = filter == null ? "" : filter;
        this.previousOreFilterString = this.oreFilterString;
        final IPartHost host = this.getHost();
        if (host != null) host.markForSave();
        this.resetCache(true);
    }

    public void readFilterCache(NBTTagCompound tag) {
        for (int x = 0; x < this.filterCache.length; x++) {
            final String key = "#" + x;
            this.filterCache[x] = tag.hasKey(key, NBT.TAG_COMPOUND) ? readStackNBT(tag.getCompoundTag(key), true)
                : null;
        }
    }

    public void writeFilterCache(NBTTagCompound tag) {
        for (int x = 0; x < this.filterCache.length; x++) {
            if (this.filterCache[x] != null) {
                final NBTTagCompound stackTag = new NBTTagCompound();
                writeStackNBT(this.filterCache[x], stackTag, true);
                tag.setTag("#" + x, stackTag);
            }
        }
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.config.readFromNBT(data, "config");
        this.priority = data.getInteger("priority");
        this.oreFilterString = data.getString(NBT_FILTER);
        this.previousOreFilterString = this.oreFilterString;
        if (data.hasKey("customName")) this.setCustomName(data.getString("customName"));
        this.readFilterCache(data.getCompoundTag(NBT_FILTER_CACHE));
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        this.config.writeToNBT(data, "config");
        data.setInteger("priority", this.priority);
        if (!this.oreFilterString.isEmpty()) data.setString(NBT_FILTER, this.oreFilterString);
        if (this.hasCustomName()) data.setString("customName", this.getCustomName());
        final NBTTagCompound filterCacheTag = new NBTTagCompound();
        this.writeFilterCache(filterCacheTag);
        data.setTag(NBT_FILTER_CACHE, filterCacheTag);
    }

    public void resetCache(final boolean fullReset) {
        final IPartHost host = this.getHost();
        if (host == null || host.getTile() == null
            || host.getTile()
                .getWorldObj() == null
            || host.getTile()
                .getWorldObj().isRemote) {
            return;
        }

        if (fullReset) {
            this.resetCacheLogic = 2;
        } else if (this.resetCacheLogic == 0) {
            // A neighbor update must not downgrade a pending configuration-driven full reset.
            this.resetCacheLogic = 1;
        }

        try {
            this.getProxy()
                .getTick()
                .alertDevice(
                    this.getProxy()
                        .getNode());
        } catch (final GridAccessException ignored) {}
    }

    public void resetCache() {
        final boolean fullReset = this.resetCacheLogic == 2;
        this.resetCacheLogic = 0;

        final Map<IAEStackType<?>, IItemList<?>> before = this.snapshotExposed();
        final Set<IMEInventoryHandler<?>> previousHandlers = this.getExposedHandlers();
        final int previousHandlerHash = this.handlerHash;

        this.cached = false;
        if (fullReset) {
            this.handlerHash = 0;
        }
        this.refreshChannels();

        if (!fullReset && this.channels.isEmpty() && this.handlerHash != 0 && this.handlerHash != previousHandlerHash) {
            // Some newly placed neighbors, such as GT tanks, expose storage only after placement initialization.
            this.scheduleDelayedCacheReset();
        }
        if (!previousHandlers.equals(this.getExposedHandlers()) || this.handlerHash != previousHandlerHash) {
            try {
                this.getProxy()
                    .getGrid()
                    .postEvent(new MENetworkCellArrayUpdate());
            } catch (final GridAccessException ignored) {}
        }

        for (TypeChannel channel : this.channels.values()) {
            if (channel.exposed && channel.monitor != null) channel.monitor.onTick();
        }

        final Map<IAEStackType<?>, IItemList<?>> after = this.snapshotExposed();
        final Set<IAEStackType<?>> types = new ObjectOpenHashSet<>(before.keySet());
        types.addAll(after.keySet());
        for (IAEStackType<?> type : types) {
            this.postListChanges(type, before.get(type), after.get(type));
        }
    }

    public void scheduleDelayedCacheReset() {
        if (this.pendingDelayedCacheReset || this.delayedRetries >= MAX_DELAYED_RETRIES) return;

        final IPartHost host = this.getHost();
        if (host == null) return;
        final TileEntity self = host.getTile();
        if (self == null) return;
        final World world = self.getWorldObj();
        if (world == null || world.isRemote) return;

        this.pendingDelayedCacheReset = true;
        this.delayedRetries++;
        TickHandler.INSTANCE.addCallable(world, w -> {
            try {
                this.resetCache(true);
            } finally {
                this.pendingDelayedCacheReset = false;
            }
            return true;
        });
    }

    public TileEntity getTarget() {
        final TileEntity self = this.getHost()
            .getTile();
        final ForgeDirection side = this.getSide();
        return self.getWorldObj()
            .getTileEntity(self.xCoord + side.offsetX, self.yCoord + side.offsetY, self.zCoord + side.offsetZ);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void refreshChannels() {
        if (this.cached) return;

        final boolean wasSleeping = !this.hasExposedMonitor();
        this.cached = true;

        final TileEntity target = this.getTarget();
        final int newHandlerHash = Platform.generateTileHash(target);
        if (this.handlerHash == newHandlerHash && this.handlerHash != 0 && !this.channels.isEmpty()) {
            return;
        }
        if (this.handlerHash != newHandlerHash) {
            this.delayedRetries = 0;
        }

        this.handlerHash = newHandlerHash;
        this.channels.clear();
        this.readOncePass.clear();
        this.readOncePass.addAll(UniversalBusHelper.getTypes());

        if (target != null) {
            final ForgeDirection targetSide = this.getSide()
                .getOpposite();
            final IConfigManager cm = this.getConfigManager();
            final AccessRestriction access = (AccessRestriction) cm.getSetting(Settings.ACCESS);
            final StorageFilter storageFilter = (StorageFilter) cm.getSetting(Settings.STORAGE_FILTER);
            final boolean inverted = this.getInstalledUpgrades(Upgrades.INVERTER) > 0;
            final boolean fuzzy = this.getInstalledUpgrades(Upgrades.FUZZY) > 0;
            final boolean oreFilter = this.getInstalledUpgrades(Upgrades.ORE_FILTER) > 0;
            boolean extractRights = access == AccessRestriction.READ;
            if (cm.getSetting(Settings.EXTRACTION_MODE) == ExtractionMode.STRICT) {
                extractRights |= access == AccessRestriction.READ_WRITE;
            }

            final Map<IAEStackType<?>, List<IAEStack<?>>> configured = UniversalBusHelper
                .groupConfig(this.config, this.getActiveConfigSlots());
            // With an ore filter card the items are filtered by the ore expression, item config slots are ignored.
            final OreFilteredList oreList = oreFilter ? new OreFilteredList(this.oreFilterString) : null;
            final boolean itemsByOre = oreList != null && !oreList.isEmpty();
            if (oreFilter) configured.remove(ITEM_STACK_TYPE);
            final boolean anyConfigured = !configured.isEmpty() || itemsByOre;

            for (IAEStackType<?> type : UniversalBusHelper.getTypes()) {
                final IMEInventory inv = UniversalBusHelper.getStorageInventory(target, targetSide, type, this.source);
                if (inv == null) continue;

                if (inv instanceof IStorageBusMonitor<?>m) {
                    m.setMode(storageFilter);
                    m.setActionSource(this.source);
                }
                if (inv instanceof MEMonitorPassThrough<?>h) {
                    h.setMode(storageFilter);
                }

                final List<IAEStack<?>> filters = configured.get(type);
                final boolean hasFilter = type == ITEM_STACK_TYPE && oreList != null ? itemsByOre : filters != null;
                final boolean exposed = inverted || !anyConfigured || hasFilter;

                final MEInventoryHandler handler = new StorageBusInventoryHandler(inv, type, this::getReshuffleAccess);
                handler.setBaseAccess(access);
                handler.setWhitelist(inverted ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST);
                handler.setPriority(this.priority);
                handler.setIsExtractFilterActive(extractRights);
                if (this.getInstalledUpgrades(Upgrades.STICKY) > 0) handler.setSticky(true);

                final IPartitionList partitionList;
                if (type == ITEM_STACK_TYPE && oreList != null) {
                    partitionList = oreList;
                } else {
                    final IItemList priorityList = type.createList();
                    if (filters != null) {
                        for (IAEStack<?> stack : filters) priorityList.add(stack);
                    }
                    partitionList = type == ITEM_STACK_TYPE && fuzzy
                        ? new FuzzyPriorityList(priorityList, (FuzzyMode) cm.getSetting(Settings.FUZZY_MODE))
                        : new PrecisePriorityList(priorityList);
                }
                handler.setPartitionList(partitionList);
                handler.setExtractPartitionList(partitionList);

                final TypeChannel channel = new TypeChannel(
                    type,
                    inv,
                    handler,
                    inv instanceof IStorageBusMonitor<?>m ? m : null,
                    exposed);
                if (exposed && inv instanceof IMEMonitor<?>m) {
                    m.addListener(this, handler);
                }
                this.channels.put(type, channel);
            }
        }

        if (this.channels.isEmpty() && target != null) {
            // The neighbor exists but may not expose storage until its placement initialization finishes.
            this.cached = false;
            this.scheduleDelayedCacheReset();
        }

        if (wasSleeping == this.hasExposedMonitor()) {
            try {
                final ITickManager tm = this.getProxy()
                    .getTick();
                if (this.hasExposedMonitor()) {
                    tm.wakeDevice(
                        this.getProxy()
                            .getNode());
                } else {
                    tm.sleepDevice(
                        this.getProxy()
                            .getNode());
                }
            } catch (final GridAccessException ignored) {}
        }
    }

    public boolean hasExposedMonitor() {
        for (TypeChannel channel : this.channels.values()) {
            if (channel.exposed && channel.monitor != null) return true;
        }
        return false;
    }

    public Set<IMEInventoryHandler<?>> getExposedHandlers() {
        final Set<IMEInventoryHandler<?>> handlers = new ReferenceOpenHashSet<>();
        for (TypeChannel channel : this.channels.values()) {
            if (channel.exposed) handlers.add(channel.handler);
        }
        return handlers;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<IAEStackType<?>, IItemList<?>> snapshotExposed() {
        final Map<IAEStackType<?>, IItemList<?>> result = new Object2ObjectLinkedOpenHashMap<>();
        for (TypeChannel channel : this.channels.values()) {
            if (!channel.exposed) continue;
            final IItemList list = channel.handler
                .getAvailableItems(channel.type.createList(), IterationCounter.fetchNewId());
            result.put(channel.type, list);
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void postListChanges(IAEStackType<?> type, IItemList<?> before, IItemList<?> after) {
        final IItemList changes = type.createPrimitiveList();
        if (before != null) {
            for (Object o : before) {
                final IAEStack<?> stack = ((IAEStack<?>) o).copy();
                stack.setStackSize(-stack.getStackSize());
                changes.add(stack);
            }
        }
        if (after != null) {
            for (Object o : after) {
                changes.add(((IAEStack<?>) o).copy());
            }
        }

        final List<IAEStack<?>> result = new ObjectArrayList<>();
        for (Object o : changes) {
            final IAEStack<?> stack = (IAEStack<?>) o;
            if (stack.getStackSize() != 0) result.add(stack);
        }
        if (!result.isEmpty()) {
            this.postChange(null, result, this.source);
        }
    }

    @Override
    public boolean isValid(final Object verificationToken) {
        for (TypeChannel channel : this.channels.values()) {
            if (channel.exposed && channel.handler == verificationToken) return true;
        }
        return false;
    }

    @Override
    public void postChange(final IBaseMonitor<IAEStack<?>> monitor, final Iterable<IAEStack<?>> change,
        final BaseActionSource actionSource) {
        try {
            if (!this.getProxy()
                .isActive()) return;

            final IAEStackType<?> type = this.resolveType(monitor, change);
            if (type == null) return;
            final TypeChannel channel = this.channels.get(type);
            final boolean firstPass = this.readOncePass.remove(type);

            Iterable<IAEStack<?>> filtered = change;
            if (!firstPass) {
                if (channel == null || !channel.exposed) return;
                final AccessRestriction access = (AccessRestriction) this.getConfigManager()
                    .getSetting(Settings.ACCESS);
                if (!access.hasPermission(AccessRestriction.READ) && !channel.handler.isVisible()) return;
                filtered = this.filterChanges(channel, change);
                if (filtered == null) return;
            }

            this.getProxy()
                .getStorage()
                .postAlterationOfStoredItems(type, filtered, this.source);
        } catch (final GridAccessException ignored) {}
    }

    public IAEStackType<?> resolveType(IBaseMonitor<?> monitor, Iterable<IAEStack<?>> change) {
        for (IAEStack<?> stack : change) {
            if (stack != null) return stack.getStackType();
        }
        if (monitor != null) {
            for (TypeChannel channel : this.channels.values()) {
                if (channel.inventory == monitor) return channel.type;
            }
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Iterable<IAEStack<?>> filterChanges(TypeChannel channel, Iterable<IAEStack<?>> change) {
        final MEInventoryHandler handler = channel.handler;
        if (!handler.isExtractFilterActive() || handler.getExtractPartitionList()
            .isEmpty()) {
            return change;
        }
        final Predicate condition = handler.getExtractFilterCondition();
        final List<IAEStack<?>> result = new ObjectArrayList<>();
        for (IAEStack<?> stack : change) {
            if (condition.test(stack)) result.add(stack);
        }
        return result.isEmpty() ? null : Collections.unmodifiableList(result);
    }

    @Override
    public void onListUpdate() {}

    @Override
    public @NotNull List<IMEInventoryHandler> getCellArray(final IAEStackType<?> type) {
        if (this.getProxy()
            .isActive()) {
            this.refreshChannels();
            final TypeChannel channel = this.channels.get(type);
            if (channel != null && channel.exposed) {
                return Collections.singletonList(channel.handler);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void saveChanges(final IMEInventory cellInventory) {}

    @Override
    public MEInventoryHandler<?> getInternalHandler() {
        this.refreshChannels();
        final TypeChannel channel = this.channels.get(ITEM_STACK_TYPE);
        return channel != null && channel.exposed ? channel.handler : null;
    }

    @Override
    public IAEStackType<?> getStackType() {
        return ITEM_STACK_TYPE;
    }

    public List<IAEStack<?>> getTargetContents() {
        this.refreshChannels();
        final List<IAEStack<?>> result = new ObjectArrayList<>();
        final boolean oreFilter = this.getInstalledUpgrades(Upgrades.ORE_FILTER) > 0;
        for (TypeChannel channel : this.channels.values()) {
            // Item config slots are ignored with an ore filter card
            if (oreFilter && channel.type == ITEM_STACK_TYPE) continue;
            if (channel.monitor != null) channel.monitor.onTick();
            result.addAll(UniversalBusHelper.listAvailable(channel.inventory, channel.type));
        }
        return result;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(
            TickRates.StorageBus.getMin(),
            TickRates.StorageBus.getMax(),
            !this.hasExposedMonitor(),
            true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        if (this.resetCacheLogic != 0) {
            this.resetCache();
        }

        TickRateModulation result = null;
        for (TypeChannel channel : this.channels.values()) {
            if (!channel.exposed || channel.monitor == null) continue;
            final TickRateModulation rate = channel.monitor.onTick();
            if (result == null || rate.ordinal() > result.ordinal()) result = rate;
        }
        return result == null ? TickRateModulation.SLEEP : result;
    }

    @Override
    public void onNeighborChanged() {
        this.resetCache(false);
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(3, 3, 15, 13, 13, 16);
        bch.addBox(2, 2, 14, 14, 14, 15);
        bch.addBox(5, 5, 12, 11, 11, 14);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(final IPartRenderHelper rh, final RenderBlocks renderer) {
        rh.setTexture(
            CableBusTextures.PartStorageSides.getIcon(),
            CableBusTextures.PartStorageSides.getIcon(),
            CableBusTextures.PartStorageBack.getIcon(),
            this.getItemStack()
                .getIconIndex(),
            CableBusTextures.PartStorageSides.getIcon(),
            CableBusTextures.PartStorageSides.getIcon());

        rh.setBounds(3, 3, 15, 13, 13, 16);
        rh.renderInventoryBox(renderer);

        rh.setBounds(2, 2, 14, 14, 14, 15);
        rh.renderInventoryBox(renderer);

        rh.setBounds(5, 5, 12, 11, 11, 14);
        rh.renderInventoryBox(renderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(final int x, final int y, final int z, final IPartRenderHelper rh,
        final RenderBlocks renderer) {
        this.setRenderCache(rh.useSimplifiedRendering(x, y, z, this, this.getRenderCache()));
        rh.setTexture(
            CableBusTextures.PartStorageSides.getIcon(),
            CableBusTextures.PartStorageSides.getIcon(),
            CableBusTextures.PartStorageBack.getIcon(),
            this.getItemStack()
                .getIconIndex(),
            CableBusTextures.PartStorageSides.getIcon(),
            CableBusTextures.PartStorageSides.getIcon());

        rh.setBounds(3, 3, 15, 13, 13, 16);
        rh.renderBlock(x, y, z, renderer);

        rh.setBounds(2, 2, 14, 14, 14, 15);
        rh.renderBlock(x, y, z, renderer);

        rh.setBounds(5, 5, 12, 11, 11, 13);
        rh.renderBlock(x, y, z, renderer);

        rh.setTexture(
            CableBusTextures.PartMonitorSidesStatus.getIcon(),
            CableBusTextures.PartMonitorSidesStatus.getIcon(),
            CableBusTextures.PartMonitorBack.getIcon(),
            this.getItemStack()
                .getIconIndex(),
            CableBusTextures.PartMonitorSidesStatus.getIcon(),
            CableBusTextures.PartMonitorSidesStatus.getIcon());

        rh.setBounds(5, 5, 13, 11, 11, 14);
        rh.renderBlock(x, y, z, renderer);

        this.renderLights(x, y, z, rh, renderer);
    }

    @Override
    public int cableConnectionRenderTo() {
        return 4;
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final Vec3 pos) {
        if (player.isSneaking()) return false;
        if (Platform.isClient()) return true;
        GTNLGuiHandler.openGui(
            player,
            GuiType.UniversalStorageBusGUI,
            this.getSide(),
            this.getHost()
                .getTile());
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getPartName() {
        return this.getItemStack()
            .getDisplayName();
    }

    @Override
    public ItemStack getPrimaryGuiIcon() {
        return GTNLItemList.PartUniversalStorageBus.get(1);
    }

    @Override
    public ItemStack getOriginGuiIcon() {
        return GTNLItemList.PartUniversalStorageBus.get(1);
    }

    @Override
    public void saveAEStackInv() {
        this.saveChanges();
    }

    @Override
    public IAEStackInventory getAEInventoryByName(StorageName name) {
        return name == StorageName.CONFIG ? this.config : null;
    }

    @Override
    @Optional.Method(modid = "BuildCraft|Transport")
    public ConnectOverride overridePipeConnection(final PipeType type, final ForgeDirection with) {
        return type == PipeType.ITEM && with == this.getSide() ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
    }

    public static final class TypeChannel {

        public final IAEStackType<?> type;
        @SuppressWarnings("rawtypes")
        public final IMEInventory inventory;
        @SuppressWarnings("rawtypes")
        public final MEInventoryHandler handler;
        public final IStorageBusMonitor<?> monitor;
        /** Whether the network can see this type through the bus; hidden types only serve the partition button. */
        public final boolean exposed;

        @SuppressWarnings("rawtypes")
        public TypeChannel(IAEStackType<?> type, IMEInventory inventory, MEInventoryHandler handler,
            IStorageBusMonitor<?> monitor, boolean exposed) {
            this.type = type;
            this.inventory = inventory;
            this.handler = handler;
            this.monitor = monitor;
            this.exposed = exposed;
        }
    }
}
