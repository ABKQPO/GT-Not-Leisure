package com.science.gtnl.client.metoolbox;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import appeng.api.config.PinsRows;
import appeng.api.config.Settings;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.config.YesNo;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.util.IConfigManager;
import appeng.client.gui.widgets.GuiScrollbar;
import appeng.client.gui.widgets.ISortSource;
import appeng.client.me.ItemRepo;
import appeng.core.AEConfig;
import appeng.items.contents.PinList;
import appeng.items.tools.powered.ToolWirelessTerminal;
import appeng.util.AEStackTypeFilter;
import appeng.util.ConfigManager;
import appeng.util.Platform;
import appeng.util.TerminalSettings;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;

public final class ToolboxTerminalModel implements ISortSource {

    private final GuiScrollbar scrollbar = new GuiScrollbar();
    private final AEStackTypeFilter typeFilters = new AEStackTypeFilter();
    private int columns = ToolboxLayout.COLUMNS;
    private int rows = ToolboxLayout.ROWS;
    private int visibleCraftingCount;
    private int visiblePlayerCount = PinsRows.TWO.getSlotCount();
    private ItemRepo repo = createRepo();
    private Map<Integer, IAEStack<?>> entries = new HashMap<>();
    private Map<Integer, IAEStack<?>> pending;
    private long pendingRevision;
    private Map<Integer, IAEStack<?>> pendingDelta;
    private long pendingDeltaRevision;
    private IAEStack<?>[] pendingPins;
    private long pendingPinRevision;
    private PinsRows pendingCraftingPinsRows;
    private PinsRows pendingPlayerPinsRows;
    private Map<Integer, IAEStack<?>> stagedCatalog;
    private long stagedCatalogRevision;
    private IAEStack<?>[] stagedPins;
    private long stagedPinRevision;
    private PinsRows stagedCraftingPinsRows;
    private PinsRows stagedPlayerPinsRows;
    private int savedScroll = -1;
    private int catalogPart;
    private int catalogParts;
    private int pinPart;
    private int pinParts;
    private IAEStack<?>[] pins = new IAEStack<?>[PinList.TOTAL_SLOTS];
    private PinsRows craftingPinsRows = PinsRows.DISABLED;
    private PinsRows playerPinsRows = PinsRows.TWO;
    private SortOrder sort = SortOrder.NAME;
    private SortDir direction = SortDir.ASCENDING;
    private ViewItems display = ViewItems.ALL;
    private long catalogRevision;
    private long pinRevision;
    private final SessionViewState sessionState = new SessionViewState();
    private long pendingSettingsAction;

    private ItemRepo createRepo() {
        ItemRepo result = new ItemRepo(scrollbar, this);
        result.setRowSize(columns);

        result.setVisiblePinRows(
            (int) Platform.ceilDiv(visibleCraftingCount, 9),
            (int) Platform.ceilDiv(visiblePlayerCount, 9));
        return result;
    }

    public void readSettings(ItemStack toolbox) {
        if (toolbox != null && toolbox.getItem() instanceof ToolWirelessTerminal wireless) {
            sessionState.observeNetwork(wireless.getEncryptionKey(toolbox));
            repo.setPowered(sessionState.isDisplayOnline());

            if (pendingSettingsAction == 0) {
                TerminalSettings terminalSettings = new TerminalSettings();
                terminalSettings.readFromNBT(toolbox.getTagCompound());
                readTypeFilters(terminalSettings.getFilters(Minecraft.getMinecraft().thePlayer));
            }
            readSettings(wireless.getConfigManager(toolbox.copy()));
        }
    }

    public void handOffSettings() {

        pendingSettingsAction = 0;
    }

    public void readSettings(NBTTagCompound config) {
        if (pendingSettingsAction != 0) return;
        ConfigManager settings = new ConfigManager((manager, setting, value) -> {});
        settings.registerSetting(Settings.SORT_BY, SortOrder.NAME);
        settings.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);
        settings.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);
        settings.readFromNBT(config);
        AEStackTypeFilter filters = new AEStackTypeFilter();
        filters.readFromNBT(config);
        readTypeFilters(filters);
        readSettings(settings);
    }

    private void readTypeFilters(AEStackTypeFilter filters) {
        filters.setEnabled(ITEM_STACK_TYPE, true);
        if (typeFilters.equals(filters)) return;
        typeFilters.copyFrom(filters);
        refreshActiveView();
    }

    public boolean showFluids() {
        return typeFilters.isEnabled(FLUID_STACK_TYPE);
    }

    public boolean isTypeVisible(IAEStackType<?> type) {
        return (type == ITEM_STACK_TYPE || type == FLUID_STACK_TYPE) && typeFilters.isEnabled(type);
    }

    public void toggleFluids() {
        typeFilters.toggle(FLUID_STACK_TYPE);
        refreshActiveView();
        ToolboxClient.saveSettings();
    }

    public void readSettings(NBTTagCompound config, long acknowledgedAction) {

        if (pendingSettingsAction == 0 || acknowledgedAction < pendingSettingsAction) return;
        pendingSettingsAction = 0;
        readSettings(config);
    }

    public void settingsSubmitted(long actionId) {
        pendingSettingsAction = Math.max(pendingSettingsAction, actionId);
    }

    public boolean hasPendingSettings() {
        return pendingSettingsAction != 0;
    }

    private void readSettings(IConfigManager settings) {
        if (pendingSettingsAction != 0) return;
        SortOrder nextSort = (SortOrder) settings.getSetting(Settings.SORT_BY);
        SortDir nextDirection = (SortDir) settings.getSetting(Settings.SORT_DIRECTION);
        ViewItems nextDisplay = (ViewItems) settings.getSetting(Settings.VIEW_MODE);

        if (nextDisplay == ViewItems.FLOWING) nextDisplay = ViewItems.ALL;
        if (sort == nextSort && direction == nextDirection && display == nextDisplay) return;
        sort = nextSort;
        direction = nextDirection;
        display = nextDisplay;
        refreshActiveView();
    }

    public void beginSession() {
        if (savedScroll < 0) savedScroll = scrollbar.getCurrentScroll();
        sessionState.begin();
        catalogRevision = 0;
        pinRevision = 0;
        pending = null;
        pendingDelta = null;
        pendingPins = null;
        stagedCatalog = null;
        stagedPins = null;
        catalogPart = 0;
        pinPart = 0;

    }

    public boolean catalog(NBTTagCompound data) {
        long revision = data.getLong("revision");
        if (revision < catalogRevision) return true;
        if (!data.getBoolean("snapshot") && revision == catalogRevision) return true;
        int part = data.getInteger("part");
        int parts = data.getInteger("parts");
        if (part == 0) {
            catalogPart = 0;
            catalogParts = parts;
        }
        if (parts <= 0 || part != catalogPart
            || parts != catalogParts
            || part >= parts
            || data.getBoolean("last") != (part == parts - 1)) {
            pending = null;
            pendingDelta = null;
            return false;
        }
        catalogPart++;
        if (data.getBoolean("reset")) {
            if (savedScroll < 0) savedScroll = scrollbar.getCurrentScroll();
            pending = new HashMap<>();
            pendingDelta = null;
            pendingRevision = revision;
        }
        if (pending != null) {
            if (revision != pendingRevision) {
                pending = null;
                return false;
            }
            readEntries(data.getTagList("entries", 10), pending);
            if (data.getBoolean("last")) {
                Map<Integer, IAEStack<?>> complete = pending;
                pending = null;
                if (!sessionState.isOnline()) {
                    stagedCatalog = complete;
                    stagedCatalogRevision = revision;
                    commitSessionSnapshots();
                } else {
                    entries = complete;
                    catalogRevision = revision;
                    rebuild();
                    restoreScroll();
                }
            }
            return true;
        }
        if (revision <= catalogRevision) return true;
        if (!sessionState.isOnline()) return false;
        if (data.getBoolean("snapshot") || data.getLong("baseRevision") != catalogRevision) return false;
        if (pendingDelta == null) {
            pendingDelta = new HashMap<>();
            pendingDeltaRevision = revision;
        }
        if (pendingDeltaRevision != revision) {
            pendingDelta = null;
            return false;
        }
        NBTTagList changes = data.getTagList("entries", 10);
        for (int i = 0; i < changes.tagCount(); i++) {
            NBTTagCompound change = changes.getCompoundTagAt(i);
            IAEStack<?> stack = Platform.readStackNBT(change.getCompoundTag("stack"));
            if (stack == null) continue;
            pendingDelta.put(change.getInteger("id"), stack);
        }
        if (!data.getBoolean("last")) return true;
        for (Map.Entry<Integer, IAEStack<?>> change : pendingDelta.entrySet()) {
            IAEStack<?> stack = change.getValue();
            repo.postUpdate(stack);
            if (stack.getStackSize() == 0 && !stack.isCraftable()) entries.remove(change.getKey());
            else entries.put(change.getKey(), stack);
        }
        pendingDelta = null;
        catalogRevision = revision;
        repo.updateViewIfChanged();
        updateScroll();
        return true;
    }

    private static void readEntries(NBTTagList data, Map<Integer, IAEStack<?>> destination) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound entry = data.getCompoundTagAt(i);
            IAEStack<?> stack = Platform.readStackNBT(entry.getCompoundTag("stack"));
            if (stack == null || stack.getStackSize() == 0 && !stack.isCraftable()) {
                destination.remove(entry.getInteger("id"));
            } else {
                destination.put(entry.getInteger("id"), stack);
            }
        }
    }

    public boolean pins(NBTTagCompound data) {
        if (data.getLong("revision") < pinRevision) return true;
        long revision = data.getLong("revision");
        int craftingRows = data.getInteger("craftingRows");
        int playerRows = data.getInteger("playerRows");
        if (craftingRows < 0 || craftingRows >= PinsRows.values().length
            || playerRows < 0
            || playerRows >= PinsRows.values().length) {
            pendingPins = null;
            return false;
        }
        int part = data.getInteger("part");
        int parts = data.getInteger("parts");
        if (part == 0) {
            pinPart = 0;
            pinParts = parts;
        }
        if (parts <= 0 || part != pinPart
            || parts != pinParts
            || part >= parts
            || data.getBoolean("reset") != (part == 0)
            || data.getBoolean("last") != (part == parts - 1)) {
            pendingPins = null;
            return false;
        }
        pinPart++;
        if (data.getBoolean("reset")) {
            pendingPins = new IAEStack<?>[PinList.TOTAL_SLOTS];
            pendingPinRevision = revision;
            pendingCraftingPinsRows = PinsRows.fromOrdinal(craftingRows);
            pendingPlayerPinsRows = PinsRows.fromOrdinal(playerRows);
        }
        if (pendingPins == null || revision != pendingPinRevision
            || pendingCraftingPinsRows.ordinal() != craftingRows
            || pendingPlayerPinsRows.ordinal() != playerRows) {
            pendingPins = null;
            return false;
        }
        NBTTagList slots = data.getTagList("slots", 10);
        for (int i = 0; i < slots.tagCount(); i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);
            int index = slot.getInteger("index");
            if (index >= 0 && index < pins.length) {
                pendingPins[index] = Platform.readStackNBT(slot.getCompoundTag("stack"));
            }
        }
        if (!data.getBoolean("last")) return true;
        if (sessionState.isLoading()) {
            stagedPins = pendingPins;
            stagedPinRevision = revision;
            stagedCraftingPinsRows = pendingCraftingPinsRows;
            stagedPlayerPinsRows = pendingPlayerPinsRows;
            pendingPins = null;
            commitSessionSnapshots();
            return true;
        }
        pinRevision = revision;
        pins = pendingPins;
        craftingPinsRows = pendingCraftingPinsRows;
        playerPinsRows = pendingPlayerPinsRows;
        pendingPins = null;
        projectPins();
        updateScroll();
        return true;
    }

    private void commitSessionSnapshots() {
        if (stagedCatalog == null || stagedPins == null || !sessionState.isConnected()) return;
        entries = stagedCatalog;
        catalogRevision = stagedCatalogRevision;
        pins = stagedPins;
        pinRevision = stagedPinRevision;
        craftingPinsRows = stagedCraftingPinsRows;
        playerPinsRows = stagedPlayerPinsRows;
        stagedCatalog = null;
        stagedPins = null;
        sessionState.snapshotsCommitted();
        rebuild();
        restoreScroll();
    }

    private void restoreScroll() {
        if (savedScroll >= 0) scrollbar.setCurrentScroll(savedScroll);
        savedScroll = -1;
    }

    public void closeSession() {
        savedScroll = scrollbar.getCurrentScroll();
        sessionState.close();
        stagedCatalog = null;
        stagedPins = null;
        pending = null;
        pendingDelta = null;
        pendingPins = null;
    }

    private void projectPins() {
        repo.setAEPins(visiblePins(pins, visibleCraftingCount, visiblePlayerCount));
    }

    static IAEStack<?>[] visiblePins(IAEStack<?>[] snapshot, int craftingCount, int playerCount) {

        IAEStack<?>[] visible = new IAEStack<?>[snapshot.length];
        copyPins(snapshot, visible, 0, Math.min(PinList.CRAFTING_SLOTS, Math.max(0, craftingCount)));
        copyPins(snapshot, visible, PinList.PLAYER_OFFSET, Math.min(PinList.PLAYER_SLOTS, Math.max(0, playerCount)));
        return visible;
    }

    private static void copyPins(IAEStack<?>[] source, IAEStack<?>[] target, int start, int count) {
        int end = Math.min(source.length, start + count);
        for (int index = start; index < end; index++) {
            if (source[index] != null) target[index] = source[index].copy();
        }
    }

    public PinsRows craftingPinsRows() {
        return craftingPinsRows;
    }

    public PinsRows playerPinsRows() {
        return playerPinsRows;
    }

    public void visibility(int craftingCount, int playerCount) {
        int nextCrafting = Math.min(PinList.CRAFTING_SLOTS, Math.max(0, craftingCount));
        int nextPlayer = Math.min(PinList.PLAYER_SLOTS, Math.max(0, playerCount));
        if (visibleCraftingCount == nextCrafting && visiblePlayerCount == nextPlayer) return;
        visibleCraftingCount = nextCrafting;
        visiblePlayerCount = nextPlayer;
        repo.setVisiblePinRows(
            (int) Platform.ceilDiv(visibleCraftingCount, 9),
            (int) Platform.ceilDiv(visiblePlayerCount, 9));
        projectPins();
        refreshActiveView();
    }

    private void rebuild() {
        String search = repo.getSearchString();
        int previousScroll = scrollbar.getCurrentScroll();
        repo = createRepo();
        repo.setSearchString(search);
        repo.setPowered(sessionState.isDisplayOnline());
        for (IAEStack<?> stack : entries.values()) repo.postUpdate(stack);

        projectPins();
        updateScroll();
        scrollbar.setCurrentScroll(previousScroll);
    }

    public void update() {
        boolean pause = AEConfig.instance.settings.getSetting(Settings.PAUSE_WHEN_HOLDING_SHIFT) == YesNo.YES;
        repo.setPaused(pause && GuiScreen.isShiftKeyDown());
        if (repo.updateViewIfChanged()) updateScroll();
    }

    public void viewport(int columns, int rows) {
        int nextColumns = Math.max(1, columns);
        int nextRows = Math.max(1, rows);
        if (this.columns == nextColumns && this.rows == nextRows) return;
        long firstVisible = (long) scrollbar.getCurrentScroll() * this.columns;
        if (savedScroll >= 0) {

            savedScroll = (int) Math.min(Integer.MAX_VALUE, (long) savedScroll * this.columns / nextColumns);
        }
        this.columns = nextColumns;
        this.rows = nextRows;
        repo.setRowSize(nextColumns);
        updateScroll();
        scrollbar.setCurrentScroll((int) Math.min(Integer.MAX_VALUE, firstVisible / nextColumns));
    }

    public int maximumScroll() {
        return (int) Math.max(0, Platform.ceilDiv(repo.size(), columns) - rows);
    }

    private void updateScroll() {
        scrollbar.setRange(0, maximumScroll(), 1);
    }

    public void status(boolean online, String network) {
        sessionState.status(online, network);
        repo.setPowered(sessionState.isDisplayOnline());
        if (online) commitSessionSnapshots();
        else {
            stagedCatalog = null;
            stagedPins = null;
        }
    }

    public boolean isOnline() {
        return sessionState.isOnline();
    }

    public boolean isDisplayOnline() {
        return sessionState.isDisplayOnline();
    }

    public boolean isLoading() {
        return sessionState.isLoading();
    }

    public void retryAfterTimeout() {
        sessionState.retryAfterTimeout();
        repo.setPowered(false);
    }

    public ItemRepo repo() {
        return repo;
    }

    public GuiScrollbar scrollbar() {
        return scrollbar;
    }

    public String search() {
        return repo.getSearchString();
    }

    public void search(String text) {
        if (repo.getSearchString()
            .equals(text)) return;
        repo.setSearchString(text);
        refreshActiveView();
    }

    public int entryId(IAEStack<?> target) {
        if (!isOnline() || target == null) return -1;
        for (Map.Entry<Integer, IAEStack<?>> entry : entries.entrySet()) {
            if (entry.getValue()
                .isSameType((Object) target)) return entry.getKey();
        }
        return -1;
    }

    public long catalogRevision() {
        return catalogRevision;
    }

    public long pinRevision() {
        return pinRevision;
    }

    public void settings(int button, boolean shift) {
        if (shift) {
            EnumSet<?> views = EnumSet.copyOf(Settings.VIEW_MODE.getPossibleValues());

            views.remove(ViewItems.FLOWING);
            display = Platform.rotateEnum(display, false, views);
        } else if (button == 1) {
            direction = Platform.rotateEnum(direction, false, Settings.SORT_DIRECTION.getPossibleValues());
        } else {
            sort = Platform.rotateEnum(sort, false, Settings.SORT_BY.getPossibleValues());
        }
        refreshActiveView();
        ToolboxClient.saveSettings();
    }

    private void refreshActiveView() {
        if (repo.isPaused()) {
            try {

                repo.setPaused(false);
            } finally {
                repo.setPaused(true);
            }
        } else {
            repo.updateView();
        }
        updateScroll();
    }

    @Override
    public Enum<?> getSortBy() {
        return sort;
    }

    @Override
    public Enum<?> getSortDir() {
        return direction;
    }

    @Override
    public Enum<?> getSortDisplay() {
        return display;
    }

    @Override
    public Reference2BooleanMap<IAEStackType<?>> getTypeFilter() {
        return typeFilters.getFiltersMap();
    }

    static final class SessionViewState {

        private boolean connected;
        private boolean snapshotsReady;
        private boolean displayOnline;
        private boolean loading = true;
        private boolean unavailable;
        private String network;

        void begin() {
            connected = false;
            snapshotsReady = false;

            loading = !unavailable;
        }

        void close() {
            connected = false;
            snapshotsReady = false;
            loading = false;
        }

        void observeNetwork(String nextNetwork) {
            if (network != null && !network.equals(nextNetwork)) {
                displayOnline = false;
                snapshotsReady = false;
            }
            network = nextNetwork;
        }

        void status(boolean online, String nextNetwork) {
            observeNetwork(nextNetwork);
            connected = online;
            unavailable = !online;
            if (!online) {
                snapshotsReady = false;
                displayOnline = false;
            }
            loading = online && !snapshotsReady;
        }

        void snapshotsCommitted() {
            if (!connected) return;
            snapshotsReady = true;
            displayOnline = true;
            loading = false;
        }

        void retryAfterTimeout() {
            begin();
            displayOnline = false;
        }

        boolean isConnected() {
            return connected;
        }

        boolean isOnline() {
            return connected && snapshotsReady;
        }

        boolean isDisplayOnline() {
            return displayOnline;
        }

        boolean isLoading() {
            return loading;
        }
    }
}
