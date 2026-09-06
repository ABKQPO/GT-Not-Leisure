package com.science.gtnl.common.gui.modularui;

import static forestry.api.apiculture.BeeManager.beeRoot;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DynamicLinkedSyncHandler;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.EmptyWidget;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamApiaryModule;

import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.SearchField;
import forestry.api.apiculture.EnumBeeType;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.util.GTUtility;
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class SteamApiaryModuleGui extends GTNLSteamMultiBlockBaseGui {

    private static final String INVENTORY_VIEW_SYNC_KEY = "steamApiaryInventoryView";
    private static final String PRIMARY_MODE_SYNC_KEY = "steamApiaryPrimaryMode";
    private static final String MAX_SLOTS_SYNC_KEY = "steamApiaryMaxSlots";
    private static final String USED_SLOTS_SYNC_KEY = "steamApiaryUsedSlots";
    private static final String RUNNING_SYNC_KEY = "steamApiaryRunning";
    private static final String BEE_SLOT_LIST_SYNC_KEY = "steamApiaryBeeSlots";
    private static final String BEE_SLOT_WIDGET_SYNC_KEY = "steamApiaryBeeSlotWidget";
    private static final String BEE_CLICK_SYNC_KEY = "steamApiaryBeeClick";
    private static final String DROP_PROGRESS_SYNC_KEY = "steamApiaryDropProgress";
    private static final String DROP_PROGRESS_WIDGET_SYNC_KEY = "steamApiaryDropProgressWidget";
    private static final int TERMINAL_WIDTH = 190;
    private static final int TERMINAL_HEIGHT = 94;
    private static final int TERMINAL_TEXT_WIDTH = TERMINAL_WIDTH - 4;
    private static final int TERMINAL_TEXT_HEIGHT = TERMINAL_HEIGHT - 8;
    private static final int INVENTORY_WIDTH = 162;
    private static final int INVENTORY_HEIGHT = 60;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = INVENTORY_WIDTH / SLOT_SIZE;
    private static final int MAX_VISIBLE_BEE_ENTRIES = SLOTS_PER_ROW * 3;
    private static final int VIEW_BUTTON_WIDTH = 54;
    private static final int VIEW_BUTTON_HEIGHT = 18;

    private final SteamApiaryModule steamApiary;
    private DynamicSyncHandler beeInventoryWidgetSyncer;
    private PanelSyncManager mainSyncManager;
    private IntSyncValue beeClickSyncer;
    private List<BeeSlot> beeSlots = new ArrayList<>();
    private int maxSlots;
    private int usedSlots;
    private boolean machineRunning;
    private List<BeeSlot> cachedFullBeeList;
    private long cachedBeeListTick = -1;

    public SteamApiaryModuleGui(SteamApiaryModule multiblock) {
        super(multiblock);
        this.steamApiary = multiblock;
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        steamApiary.refreshMaxSlotsForGui();
        return super.build(guiData, syncManager, uiSettings);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        GenericListSyncHandler<DropEntry> dropProgressSyncer = GenericListSyncHandler.<DropEntry>builder()
            .getter(this::createDropEntries)
            .setter(this::setClientDropEntries)
            .serializer(DropEntry::write)
            .deserializer(DropEntry::read)
            .equals(SteamApiaryModuleGui::areDropEntriesEqual)
            .build();
        syncManager.syncValue(DROP_PROGRESS_SYNC_KEY, dropProgressSyncer);
        syncManager.syncValue(
            DROP_PROGRESS_WIDGET_SYNC_KEY,
            new DynamicLinkedSyncHandler<>(dropProgressSyncer)
                .widgetProvider((panelSyncManager, syncValue) -> createDropProgressWidget(syncValue.getValue())));
        super.registerSyncValues(syncManager);

        mainSyncManager = syncManager;
        syncManager.syncValue(
            INVENTORY_VIEW_SYNC_KEY,
            new BooleanSyncValue(() -> steamApiary.isInInventory, value -> steamApiary.isInInventory = value)
                .allowC2S());
        syncManager.syncValue(
            PRIMARY_MODE_SYNC_KEY,
            new IntSyncValue(() -> steamApiary.mPrimaryMode, this::setPrimaryModeFromSync).allowC2S());

        IntSyncValue maxSlotsSyncer = new IntSyncValue(() -> {
            steamApiary.refreshMaxSlotsForGui();
            return steamApiary.mMaxSlots;
        }, value -> maxSlots = value);
        IntSyncValue usedSlotsSyncer = new IntSyncValue(() -> steamApiary.mStorage.size(), value -> usedSlots = value);
        syncManager.syncValue(MAX_SLOTS_SYNC_KEY, maxSlotsSyncer);
        syncManager.syncValue(USED_SLOTS_SYNC_KEY, usedSlotsSyncer);
        syncManager.syncValue(
            RUNNING_SYNC_KEY,
            new BooleanSyncValue(
                () -> steamApiary.mPrimaryMode == SteamApiaryModule.MODE_PRIMARY_OPERATING
                    && steamApiary.mMaxProgresstime > 0,
                value -> machineRunning = value));

        beeClickSyncer = new IntSyncValue(() -> 0, this::handleBeeClick).allowC2S();
        syncManager.syncValue(BEE_CLICK_SYNC_KEY, beeClickSyncer);

        GenericListSyncHandler<BeeSlot> beeSlotSyncer = GenericListSyncHandler.<BeeSlot>builder()
            .getter(this::buildVisibleAggregatedBeeList)
            .setter(value -> beeSlots = value)
            .serializer(BeeSlot::write)
            .deserializer(BeeSlot::read)
            .equals(SteamApiaryModuleGui::areBeeSlotsEqual)
            .immutableCopy()
            .build();
        syncManager.syncValue(BEE_SLOT_LIST_SYNC_KEY, beeSlotSyncer);

        beeInventoryWidgetSyncer = new DynamicSyncHandler().widgetProvider((unused, packet) -> {
            if (packet == null) return new EmptyWidget();
            return createBeeSlotGrid(packet.readInt());
        });
        syncManager.syncValue(BEE_SLOT_WIDGET_SYNC_KEY, beeInventoryWidgetSyncer);

        if (!syncManager.isClient()) {
            beeSlotSyncer.setChangeListener(this::notifyBeeInventoryUpdate);
            maxSlotsSyncer.setChangeListener(this::notifyBeeInventoryUpdate);
            usedSlotsSyncer.setChangeListener(this::notifyBeeInventoryUpdate);
            notifyBeeInventoryUpdate();
        }
        registerQueenBufferSlot(syncManager);
    }

    @Override
    protected ParentWidget<?> createTerminalParentWidget(ModularPanel panel, PanelSyncManager syncManager) {
        BooleanSyncValue inventoryViewSyncer = syncManager
            .findSyncHandler(INVENTORY_VIEW_SYNC_KEY, BooleanSyncValue.class);
        DynamicSyncedWidget<?> beeInventoryWidget = createDynamicBeeInventoryWidget(syncManager);
        beeInventoryWidget.pos((TERMINAL_WIDTH - INVENTORY_WIDTH) / 2, 16)
            .setEnabledIf(unused -> inventoryViewSyncer.getBoolValue());
        ParentWidget<?> statusParent = new ParentWidget<>().size(getTerminalWidgetWidth(), TERMINAL_HEIGHT)
            .paddingTop(4)
            .paddingBottom(4)
            .paddingLeft(4)
            .paddingRight(0)
            .widgetTheme(GTWidgetThemes.BACKGROUND_TERMINAL)
            .child(
                createTerminalTextWidget(syncManager, panel).size(TERMINAL_TEXT_WIDTH, TERMINAL_TEXT_HEIGHT)
                    .collapseDisabledChild())
            .childIf(
                steamApiary.supportsTerminalRightCornerColumn(),
                () -> createTerminalRightCornerColumn(panel, syncManager))
            .childIf(
                steamApiary.supportsTerminalLeftCornerColumn(),
                () -> createTerminalLeftCornerColumn(panel, syncManager))
            .setEnabledIf(unused -> !inventoryViewSyncer.getBoolValue());
        return new ParentWidget<>().size(getTerminalWidgetWidth(), getTerminalWidgetHeight())
            .child(statusParent)
            .child(beeInventoryWidget);
    }

    @Override
    protected int getTerminalRowHeight() {
        return TERMINAL_HEIGHT;
    }

    @Override
    protected int getTerminalWidgetHeight() {
        return TERMINAL_HEIGHT;
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        GenericListSyncHandler<DropEntry> dropProgressSyncer = getDropProgressSyncer(syncManager);
        IntSyncValue maxProgressTimeSyncer = (IntSyncValue) syncManager.getSyncHandlerFromMapKey("maxProgressTime:0");
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(this::createProgressText)
                .asWidget()
                .textAlign(Alignment.CenterLeft)
                .fullWidth()
                .height(10)
                .setEnabledIf(
                    widget -> maxProgressTimeSyncer.getIntValue() > 0 && dropProgressSyncer.getValue()
                        .isEmpty()))
            .child(createDropProgressWidget(syncManager));
    }

    @Override
    protected Flow createPanelGap(ModularPanel parent, PanelSyncManager syncManager) {
        return Flow.row()
            .fullWidth()
            .paddingRight(2)
            .paddingLeft(4)
            .height(getTextBoxToInventoryGap())
            .child(createMachineModeRow(syncManager))
            .child(createRightPanelGapRow(parent, syncManager));
    }

    private Flow createMachineModeRow(PanelSyncManager syncManager) {
        IntSyncValue primaryModeSyncer = syncManager.findSyncHandler(PRIMARY_MODE_SYNC_KEY, IntSyncValue.class);
        return Flow.row()
            .coverChildrenWidth()
            .fullHeight()
            .child(createPrimaryModeButton(syncManager, primaryModeSyncer))
            .child(createInventoryStatusToggle(syncManager));
    }

    private IWidget createInventoryStatusToggle(PanelSyncManager syncManager) {
        BooleanSyncValue inventoryViewSyncer = syncManager
            .findSyncHandler(INVENTORY_VIEW_SYNC_KEY, BooleanSyncValue.class);
        return new ButtonWidget<>().size(VIEW_BUTTON_WIDTH, VIEW_BUTTON_HEIGHT)
            .overlay(
                new DynamicDrawable(
                    () -> IKey.lang(
                        inventoryViewSyncer.getBoolValue() ? "kubatech.gui.text.inventory" : "kubatech.gui.text.status")
                        .asIcon()
                        .size(VIEW_BUTTON_WIDTH, VIEW_BUTTON_HEIGHT)))
            .onMousePressed(mouseButton -> {
                inventoryViewSyncer.setBoolValue(!inventoryViewSyncer.getBoolValue(), true, true);
                return true;
            })
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .marginTop(-1)
            .marginRight(-1);
    }

    private IWidget createPrimaryModeButton(PanelSyncManager syncManager, IntSyncValue primaryModeSyncer) {
        IntSyncValue maxProgressTimeSyncer = (IntSyncValue) syncManager.getSyncHandlerFromMapKey("maxProgressTime:0");
        return new ButtonWidget<>().size(VIEW_BUTTON_HEIGHT, VIEW_BUTTON_HEIGHT)
            .overlay(new DynamicDrawable(() -> switch (primaryModeSyncer.getIntValue()) {
            case SteamApiaryModule.MODE_PRIMARY_INPUT -> GTGuiTextures.OVERLAY_BUTTON_ALLOW_INPUT;
            case SteamApiaryModule.MODE_PRIMARY_OUTPUT -> GTGuiTextures.OVERLAY_BUTTON_ALLOW_OUTPUT;
            case SteamApiaryModule.MODE_PRIMARY_OPERATING -> GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_STEAM;
            default -> GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT;
            }))
            .onMousePressed(mouseButton -> {
                if (maxProgressTimeSyncer.getIntValue() > 0) return true;
                int current = primaryModeSyncer.getIntValue();
                int next = mouseButton == 1 ? (current + 2) % 3 : (current + 1) % 3;
                primaryModeSyncer.setIntValue(next, true, true);
                return true;
            })
            .tooltipBuilder(tooltip -> {
                tooltip.setAutoUpdate(true);
                tooltip.addLine(IKey.str(getPrimaryModeText(primaryModeSyncer.getIntValue())));
                if (maxProgressTimeSyncer.getIntValue() > 0) {
                    tooltip.addLine(
                        IKey.str(
                            EnumChatFormatting.RED
                                + StatCollector.translateToLocal("GT5U.gui.text.cannot_change_when_running")));
                }
            })
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private void setPrimaryModeFromSync(int value) {
        if (steamApiary.mMaxProgresstime > 0) return;
        steamApiary.mPrimaryMode = Math.floorMod(value, 3);
    }

    private DynamicSyncedWidget<?> createDynamicBeeInventoryWidget(PanelSyncManager syncManager) {
        DynamicSyncHandler syncer = syncManager.findSyncHandler(BEE_SLOT_WIDGET_SYNC_KEY, DynamicSyncHandler.class);
        return new DynamicSyncedWidget<>().syncHandler(syncer)
            .initialChild(createBeeSlotGrid(getActiveBeeSlotCount(beeSlots.size())))
            .size(INVENTORY_WIDTH, INVENTORY_HEIGHT);
    }

    private IWidget createBeeSlotGrid(int activeCount) {
        if (activeCount <= 0) return new EmptyWidget();
        Flow column = Flow.column()
            .size(INVENTORY_WIDTH, INVENTORY_HEIGHT)
            .crossAxisAlignment(Alignment.CrossAxis.START);
        for (int i = 0; i < activeCount; i += SLOTS_PER_ROW) {
            Flow row = Flow.row()
                .height(SLOT_SIZE);
            int rowEnd = Math.min(i + SLOTS_PER_ROW, activeCount);
            for (int j = i; j < rowEnd; j++) {
                row.child(createBeeSlot(j));
            }
            column.child(row);
        }
        return column;
    }

    private IWidget createBeeSlot(int index) {
        SlotLikeButtonWidget button = new SlotLikeButtonWidget(() -> getBeeStack(index)) {

            @Override
            public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetThemeEntry) {
                super.draw(context, widgetThemeEntry);
                int count = getBeeSlotCount(index);
                boolean isEmptySlot = index >= beeSlots.size();
                if (count > 1 || isEmptySlot) {
                    String format = isNEIFilteredOut(getBeeStack(index)) ? EnumChatFormatting.GRAY.toString() : null;
                    GuiDraw.drawStandardSlotAmountText(count, format, getArea());
                }
                if (machineRunning) {
                    GuiDraw.drawRect(0, 0, SLOT_SIZE, SLOT_SIZE, 0x80000000);
                }
            }
        };
        button.size(SLOT_SIZE, SLOT_SIZE)
            .onMousePressed(mouseButton -> {
                if (isBeeInventoryLocked()) return true;
                int encoded = ((index + 1) << 3) | (mouseButton << 1) | (Interactable.hasShiftDown() ? 1 : 0);
                beeClickSyncer.setIntValue(encoded, true, true);
                return true;
            })
            .tooltipBuilder(tooltip -> addBeeSlotTooltip(tooltip, index));
        return button;
    }

    private void addBeeSlotTooltip(RichTooltip tooltip, int index) {
        tooltip.setAutoUpdate(true);
        if (isBeeInventoryLocked()) {
            tooltip.addLine(
                IKey.str(
                    EnumChatFormatting.RED
                        + StatCollector.translateToLocal("kubatech.gui.text.mia.locked_while_running")));
            return;
        }
        if (index < beeSlots.size()) {
            BeeSlot beeSlot = beeSlots.get(index);
            tooltip.addLine(
                IKey.str(
                    beeSlot.stack()
                        .getDisplayName()));
            if (beeSlot.count() > 1) {
                tooltip.addLine(
                    IKey.str(
                        EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocalFormatted(
                            "kubatech.gui.tooltip.dynamic_inventory.identical_slots",
                            beeSlot.count())));
            }
            return;
        }
        tooltip.addLine(
            IKey.str(
                EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("kubatech.gui.tooltip.dynamic_inventory.empty_slot")));
        int remaining = Math.max(0, maxSlots - usedSlots);
        if (remaining > 1) {
            tooltip.addLine(
                IKey.str(
                    EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocalFormatted(
                        "kubatech.gui.tooltip.dynamic_inventory.identical_slots",
                        remaining)));
        }
    }

    private List<BeeSlot> buildAggregatedBeeList() {
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, ItemStack> stackMap = new HashMap<>();
        Map<String, Integer> firstSlotMap = new HashMap<>();
        for (int i = 0; i < steamApiary.mStorage.size(); i++) {
            SteamApiaryModule.BeeSimulator bee = steamApiary.mStorage.get(i);
            String key = bee.speciesKey != null ? bee.speciesKey
                : ItemId.createNoCopy(bee.queenStack)
                    .toString();
            countMap.merge(key, 1, Integer::sum);
            stackMap.putIfAbsent(key, createDisplayStack(bee.queenStack));
            firstSlotMap.putIfAbsent(key, i);
        }
        List<BeeSlot> result = new ArrayList<>(countMap.size());
        int visibleIndex = 0;
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            result.add(
                new BeeSlot(
                    visibleIndex++,
                    firstSlotMap.get(entry.getKey()),
                    entry.getValue(),
                    stackMap.get(entry.getKey())));
        }
        result.sort(
            Comparator.comparing(
                slot -> slot.stack()
                    .getDisplayName()));
        for (int i = 0; i < result.size(); i++) {
            BeeSlot slot = result.get(i);
            result.set(i, new BeeSlot(i, slot.realIndex(), slot.count(), slot.stack()));
        }
        return result;
    }

    private List<BeeSlot> getFullAggregatedBeeList() {
        if (mainSyncManager == null || mainSyncManager.isClient()) {
            return cachedFullBeeList != null ? cachedFullBeeList : buildAggregatedBeeList();
        }
        World world = steamApiary.getBaseMetaTileEntity()
            .getWorld();
        long currentTick = world != null ? world.getTotalWorldTime() : -1;
        if (cachedFullBeeList == null || cachedBeeListTick != currentTick) {
            cachedFullBeeList = buildAggregatedBeeList();
            cachedBeeListTick = currentTick;
        }
        return cachedFullBeeList;
    }

    private List<BeeSlot> buildVisibleAggregatedBeeList() {
        List<BeeSlot> full = getFullAggregatedBeeList();
        int end = Math.min(MAX_VISIBLE_BEE_ENTRIES, full.size());
        List<BeeSlot> page = new ArrayList<>(end);
        for (int i = 0; i < end; i++) {
            BeeSlot slot = full.get(i);
            page.add(new BeeSlot(i, slot.realIndex(), slot.count(), slot.stack()));
        }
        return page;
    }

    private void handleBeeClick(int encoded) {
        if (encoded == 0 || mainSyncManager == null || mainSyncManager.isClient() || isBeeInventoryLocked()) return;
        int visibleSlotIndex = (encoded >>> 3) - 1;
        int mouseButton = (encoded >>> 1) & 0x3;
        boolean shift = (encoded & 1) != 0;
        List<BeeSlot> serverSlots = getFullAggregatedBeeList();
        if (visibleSlotIndex < 0 || visibleSlotIndex >= serverSlots.size()
            || visibleSlotIndex >= MAX_VISIBLE_BEE_ENTRIES) {
            handleEmptySlotClick(mouseButton == 1, mainSyncManager);
        } else {
            handleOccupiedSlotClick(serverSlots.get(visibleSlotIndex), mouseButton, shift, mainSyncManager);
        }
        invalidateBeeListCache();
        notifyBeeInventoryUpdate();
    }

    private void handleOccupiedSlotClick(BeeSlot beeSlot, int mouseButton, boolean shift,
        PanelSyncManager syncManager) {
        if (mouseButton == 2) {
            creativePickBee(beeSlot, syncManager);
        } else if (shift) {
            extractBeeToInventory(beeSlot.realIndex(), syncManager);
        } else if (syncManager.getCursorItem() != null) {
            replaceBeeWithCursor(beeSlot.realIndex(), syncManager);
        } else {
            extractBeeToCursor(beeSlot.realIndex(), syncManager);
        }
    }

    private void handleEmptySlotClick(boolean singleQueen, PanelSyncManager syncManager) {
        insertCursorQueen(syncManager.getCursorItem(), singleQueen, syncManager);
    }

    private void creativePickBee(BeeSlot beeSlot, PanelSyncManager syncManager) {
        if (!syncManager.getPlayer().capabilities.isCreativeMode || syncManager.getCursorItem() != null) return;
        if (beeSlot.realIndex() >= steamApiary.mStorage.size()) return;
        ItemStack stack = steamApiary.mStorage.get(beeSlot.realIndex()).queenStack.copy();
        stack.stackSize = stack.getMaxStackSize();
        syncManager.setCursorItem(stack);
        updateHeldItem(syncManager);
    }

    private boolean isBeeInventoryLocked() {
        if (mainSyncManager != null && mainSyncManager.isClient()) {
            return machineRunning;
        }
        return steamApiary.isBeeInventoryLockedForGui();
    }

    private void extractBeeToInventory(int realIndex, PanelSyncManager syncManager) {
        ItemStack removed = removeBee(realIndex);
        if (removed == null) return;
        if (syncManager.getPlayer().inventory.addItemStackToInventory(removed)) {
            syncManager.getPlayer().inventoryContainer.detectAndSendChanges();
        } else {
            syncManager.getPlayer()
                .entityDropItem(removed, 0.0f);
        }
    }

    private void extractBeeToCursor(int realIndex, PanelSyncManager syncManager) {
        ItemStack removed = removeBee(realIndex);
        if (removed == null) return;
        syncManager.setCursorItem(removed);
        updateHeldItem(syncManager);
    }

    private void replaceBeeWithCursor(int realIndex, PanelSyncManager syncManager) {
        ItemStack cursorStack = syncManager.getCursorItem();
        if (cursorStack == null || cursorStack.stackSize != 1 || realIndex >= steamApiary.mStorage.size()) return;
        SteamApiaryModule.BeeSimulator bee = createBeeSimulator(cursorStack);
        if (bee == null) return;
        SteamApiaryModule.BeeSimulator removed = steamApiary.mStorage.remove(realIndex);
        steamApiary.mStorage.add(realIndex, bee);
        syncManager.setCursorItem(removed.queenStack);
        updateHeldItem(syncManager);
    }

    private void insertCursorQueen(@Nullable ItemStack cursorStack, boolean singleQueen, PanelSyncManager syncManager) {
        if (cursorStack == null || steamApiary.mStorage.size() >= steamApiary.mMaxSlots) return;
        if (beeRoot.getType(cursorStack) != EnumBeeType.QUEEN) return;
        ItemStack input = singleQueen ? cursorStack.copy() : cursorStack;
        if (singleQueen) {
            input.stackSize = 1;
        }
        SteamApiaryModule.BeeSimulator bee = createBeeSimulator(input);
        if (bee == null) return;
        steamApiary.mStorage.add(bee);
        if (singleQueen) {
            cursorStack.stackSize--;
        }
        if (cursorStack.stackSize <= 0) {
            syncManager.setCursorItem(null);
        } else {
            syncManager.setCursorItem(cursorStack);
        }
        updateHeldItem(syncManager);
    }

    private void registerQueenBufferSlot(PanelSyncManager syncManager) {
        ItemStackHandler queenBufferInventory = new ItemStackHandler(1) {

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        ModularSlot queenBufferSlot = new ModularSlot(queenBufferInventory, 0).filter(this::canAcceptQueen)
            .singletonSlotGroup(SlotGroup.STORAGE_SLOT_PRIO)
            .changeListener((newItem, onlyAmountChanged, client, init) -> {
                if (client || init || newItem == null) return;
                tryAddBeeToStorage(newItem.copy());
                queenBufferInventory.setStackInSlot(0, null);
            });
        syncManager.syncValue("steamApiaryQueenBuffer", new ItemSlotSH(queenBufferSlot));
    }

    private boolean canAcceptQueen(ItemStack stack) {
        if (stack == null || isBeeInventoryLocked()) return false;
        return beeRoot.getType(stack) == EnumBeeType.QUEEN && steamApiary.mStorage.size() < steamApiary.mMaxSlots;
    }

    private void tryAddBeeToStorage(ItemStack queenStack) {
        SteamApiaryModule.BeeSimulator bee = createBeeSimulator(queenStack);
        if (bee == null) return;
        steamApiary.mStorage.add(bee);
        invalidateBeeListCache();
        notifyBeeInventoryUpdate();
    }

    private @Nullable ItemStack removeBee(int realIndex) {
        if (realIndex < 0 || realIndex >= steamApiary.mStorage.size()) return null;
        return steamApiary.mStorage.remove(realIndex).queenStack;
    }

    private @Nullable SteamApiaryModule.BeeSimulator createBeeSimulator(ItemStack input) {
        World world = steamApiary.getBaseMetaTileEntity()
            .getWorld();
        SteamApiaryModule.BeeSimulator bee = new SteamApiaryModule.BeeSimulator(
            input,
            world,
            steamApiary.getTierRecipes());
        return bee.isValid ? bee : null;
    }

    private void notifyBeeInventoryUpdate() {
        if (beeInventoryWidgetSyncer != null) {
            int activeCount = getActiveBeeSlotCount(buildVisibleAggregatedBeeList().size());
            beeInventoryWidgetSyncer.notifyUpdate(buffer -> buffer.writeInt(activeCount));
        }
    }

    private int getActiveBeeSlotCount(int pagedListSize) {
        int currentUsedSlots = mainSyncManager != null && mainSyncManager.isClient() ? usedSlots
            : steamApiary.mStorage.size();
        int currentMaxSlots = mainSyncManager != null && mainSyncManager.isClient() ? maxSlots : steamApiary.mMaxSlots;
        boolean hasEmptySlot = currentUsedSlots < currentMaxSlots;
        return Math.min(MAX_VISIBLE_BEE_ENTRIES, pagedListSize + (hasEmptySlot ? 1 : 0));
    }

    private void invalidateBeeListCache() {
        cachedFullBeeList = null;
        cachedBeeListTick = -1;
    }

    private void updateHeldItem(PanelSyncManager syncManager) {
        if (syncManager.getPlayer() instanceof EntityPlayerMP playerMP) {
            playerMP.isChangingQuantityOnly = false;
            playerMP.updateHeldItem();
        }
    }

    private List<DropEntry> createDropEntries() {
        List<DropEntry> entries = new ArrayList<>(steamApiary.dropProgress.size());
        for (Map.Entry<ItemId, Double> drop : steamApiary.dropProgress.entrySet()) {
            ItemStack stack = SteamApiaryModule.BeeSimulator.dropstacks.get(drop.getKey());
            if (stack == null) continue;
            entries.add(new DropEntry(stack.copy(), drop.getValue()));
        }
        entries.sort(
            Comparator.comparing(
                entry -> entry.stack()
                    .getDisplayName()));
        return entries;
    }

    private void setClientDropEntries(List<DropEntry> entries) {
        HashMap<ItemStack, Double> progress = new HashMap<>(entries.size());
        for (DropEntry entry : entries) {
            progress.put(entry.stack(), entry.progress());
        }
        steamApiary.setGuiDropProgressFromMui2(progress);
    }

    private List<IWidget> createDropRows(List<DropEntry> drops) {
        if (drops == null || drops.isEmpty()) return List.of();
        Object2IntOpenHashMap<ItemId> outputCounts = steamApiary.getOutputItemCounts(steamApiary.mOutputItems);
        List<DropEntry> sortedDrops = new ArrayList<>(drops);
        sortedDrops.sort(
            Comparator.comparingInt((DropEntry entry) -> outputCounts.getInt(ItemId.createNoCopy(entry.stack())))
                .reversed());
        List<IWidget> rows = new ArrayList<>(sortedDrops.size() + 1);
        rows.add(
            IKey.dynamic(this::createProgressText)
                .asWidget()
                .textAlign(Alignment.CenterLeft)
                .fullWidth()
                .height(10));
        for (DropEntry drop : sortedDrops) {
            int outputSize = outputCounts.getInt(ItemId.createNoCopy(drop.stack()));
            if (outputSize != 0) {
                rows.add(createDropRow(drop.stack(), outputSize));
            }
        }
        return rows;
    }

    private IWidget createDropProgressWidget(PanelSyncManager syncManager) {
        DynamicLinkedSyncHandler<?> dropProgressWidgetSyncer = syncManager
            .findSyncHandler(DROP_PROGRESS_WIDGET_SYNC_KEY, DynamicLinkedSyncHandler.class);
        GenericListSyncHandler<DropEntry> dropProgressSyncer = getDropProgressSyncer(syncManager);
        return new DynamicSyncedWidget<>().syncHandler(dropProgressWidgetSyncer)
            .initialChild(createDropProgressWidget(List.of()))
            .fullWidth()
            .setEnabledIf(
                widget -> !dropProgressSyncer.getValue()
                    .isEmpty());
    }

    private IWidget createDropProgressWidget(List<DropEntry> drops) {
        return new ListWidget<>().fullWidth()
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .children(createDropRows(drops));
    }

    private IWidget createDropRow(ItemStack stack, long itemCount) {
        ItemStack displayStack = stack.copy();
        displayStack.stackSize = 1;
        String itemName = displayStack.getDisplayName();
        String itemAmountString = EnumChatFormatting.WHITE + " x "
            + EnumChatFormatting.GOLD
            + GTUtility.formatShortenedLong(itemCount)
            + EnumChatFormatting.WHITE
            + GTUtility.appendRate(false, itemCount, true, steamApiary.mMaxProgresstime);
        String lineText = EnumChatFormatting.AQUA + GTUtility.truncateText(itemName, 40 - itemAmountString.length())
            + itemAmountString;
        String lineTooltip = EnumChatFormatting.AQUA + itemName
            + "\n"
            + GTUtility.appendRate(false, itemCount, false, steamApiary.mMaxProgresstime);

        return Flow.row()
            .height(10)
            .fullWidth()
            .child(
                new ItemDisplayWidget().item(displayStack)
                    .size(8, 8)
                    .disableThemeBackground(true))
            .child(
                IKey.str(lineText)
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .tooltipBuilder(tooltip -> tooltip.addLine(lineTooltip))
                    .marginLeft(2)
                    .fullWidth());
    }

    private String createProgressText() {
        double maxProgressSeconds = Math.max(steamApiary.mMaxProgresstime / 20.0D, 0.05D);
        double progressPercent = steamApiary.mMaxProgresstime <= 0 ? 0.0D
            : (double) steamApiary.mProgresstime / steamApiary.mMaxProgresstime * 100.0D;
        return EnumChatFormatting.WHITE + StatCollector.translateToLocal("GT5U.gui.text.progress")
            + String.format("%,.2f", (double) steamApiary.mProgresstime / 20.0D)
            + "s / "
            + String.format("%,.2f", maxProgressSeconds)
            + "s ("
            + String.format("%,.1f", progressPercent)
            + "%)";
    }

    @SuppressWarnings("unchecked")
    private GenericListSyncHandler<DropEntry> getDropProgressSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(DROP_PROGRESS_SYNC_KEY, GenericListSyncHandler.class);
    }

    private static ItemStack createDisplayStack(ItemStack original) {
        ItemStack display = new ItemStack(original.getItem(), 1, original.getItemDamage());
        if (original.getTagCompound() == null) return display;

        NBTTagCompound originalTag = original.getTagCompound();
        NBTTagCompound displayTag = new NBTTagCompound();
        NBTTagCompound genomeTag = originalTag.getCompoundTag("Genome");
        if (!genomeTag.hasNoTags()) {
            NBTTagList chromosomes = genomeTag.getTagList("Chromosomes", 10);
            if (chromosomes.tagCount() > 1) {
                NBTTagList displayChromosomes = new NBTTagList();
                displayChromosomes.appendTag(
                    chromosomes.getCompoundTagAt(0)
                        .copy());
                displayChromosomes.appendTag(
                    chromosomes.getCompoundTagAt(1)
                        .copy());
                NBTTagCompound displayGenome = new NBTTagCompound();
                displayGenome.setTag("Chromosomes", displayChromosomes);
                displayTag.setTag("Genome", displayGenome);
            }
        }
        if (originalTag.hasKey("IsAnalyzed")) {
            displayTag.setBoolean("IsAnalyzed", originalTag.getBoolean("IsAnalyzed"));
        }
        display.setTagCompound(displayTag);
        return display;
    }

    private ItemStack getBeeStack(int index) {
        return index < beeSlots.size() ? beeSlots.get(index)
            .stack() : null;
    }

    private int getBeeSlotCount(int index) {
        if (index < beeSlots.size()) return beeSlots.get(index)
            .count();
        return Math.max(0, maxSlots - usedSlots);
    }

    private static boolean isNEIFilteredOut(ItemStack item) {
        if (!ModularUI.Mods.NEI.isLoaded()) return false;
        if (!SearchField.searchInventories()) return false;
        if (item == null) return !NEIClientConfig.getSearchExpression()
            .isEmpty();
        return !LayoutManager.searchField.getFilter()
            .matches(item);
    }

    private static String getPrimaryModeText(int mode) {
        return switch (mode) {
            case SteamApiaryModule.MODE_PRIMARY_INPUT -> StatCollector.translateToLocal("kubatech.gui.text.input");
            case SteamApiaryModule.MODE_PRIMARY_OUTPUT -> StatCollector.translateToLocal("kubatech.gui.text.output");
            case SteamApiaryModule.MODE_PRIMARY_OPERATING -> StatCollector
                .translateToLocal("kubatech.gui.text.operating");
            default -> "";
        };
    }

    private static boolean areBeeSlotsEqual(BeeSlot first, BeeSlot second) {
        if (first == null || second == null) return first == second;
        return first.visibleIndex() == second.visibleIndex() && first.realIndex() == second.realIndex()
            && first.count() == second.count()
            && ItemStack.areItemStacksEqual(first.stack(), second.stack());
    }

    private static boolean areDropEntriesEqual(DropEntry first, DropEntry second) {
        if (first == null || second == null) return first == second;
        return Double.compare(first.progress(), second.progress()) == 0
            && ItemStack.areItemStacksEqual(first.stack(), second.stack());
    }

    public record BeeSlot(int visibleIndex, int realIndex, int count, ItemStack stack) {

        public static void write(PacketBuffer buffer, BeeSlot slot) throws IOException {
            buffer.writeVarIntToBuffer(slot.visibleIndex());
            buffer.writeVarIntToBuffer(slot.realIndex());
            buffer.writeVarIntToBuffer(slot.count());
            buffer.writeItemStackToBuffer(slot.stack());
        }

        public static BeeSlot read(PacketBuffer buffer) throws IOException {
            return new BeeSlot(
                buffer.readVarIntFromBuffer(),
                buffer.readVarIntFromBuffer(),
                buffer.readVarIntFromBuffer(),
                buffer.readItemStackFromBuffer());
        }
    }

    public record DropEntry(ItemStack stack, double progress) {

        public static void write(PacketBuffer buffer, DropEntry entry) throws IOException {
            buffer.writeItemStackToBuffer(entry.stack());
            buffer.writeDouble(entry.progress());
        }

        public static DropEntry read(PacketBuffer buffer) throws IOException {
            return new DropEntry(buffer.readItemStackFromBuffer(), buffer.readDouble());
        }
    }
}
