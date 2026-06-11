package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.EmptyWidget;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.cropsnh.utility.IFDropTable;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamGreenhouseModule;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseStoredCrop;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseViewMode;

import gregtech.api.modularui2.GTGuiTextures;

public class SteamGreenhouseModuleGui extends GTNLSteamMultiBlockBaseGui {

    private static final String VIEW_MODE_SYNC_KEY = "steamGreenhouseViewMode";
    private static final String CROP_SLOT_LIST_SYNC_KEY = "steamGreenhouseCropSlots";
    private static final String CROP_SLOT_WIDGET_SYNC_KEY = "steamGreenhouseCropSlotWidget";
    private static final String MAX_SEED_COUNT_SYNC_KEY = "steamGreenhouseMaxSeedCount";
    private static final String USED_SEED_COUNT_SYNC_KEY = "steamGreenhouseUsedSeedCount";
    private static final String DROP_TRACKER_SYNC_KEY = "steamGreenhouseDropTracker";
    private static final int TERMINAL_WIDTH = 190;
    private static final int TERMINAL_HEIGHT = 94;
    private static final int INVENTORY_WIDTH = 162;
    private static final int INVENTORY_HEIGHT = 72;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = INVENTORY_WIDTH / SLOT_SIZE;

    private final SteamGreenhouseModule steamGreenhouse;
    private DynamicSyncHandler cropInventoryWidgetSyncer;

    public SteamGreenhouseModuleGui(SteamGreenhouseModule multiblock) {
        super(multiblock);
        this.steamGreenhouse = multiblock;
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return super.build(guiData, syncManager, uiSettings);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            DROP_TRACKER_SYNC_KEY,
            GenericListSyncHandler.<DropEntry>builder()
                .getter(this::createDropEntries)
                .setter(this::setClientDropEntries)
                .serializer(DropEntry::write)
                .deserializer(DropEntry::read)
                .equals(SteamGreenhouseModuleGui::areDropEntriesEqual)
                .immutableCopy()
                .build());
        super.registerSyncValues(syncManager);

        IntSyncValue viewModeSyncer = new IntSyncValue(
            () -> steamGreenhouse.getGreenHouseViewMode()
                .ordinal(),
            value -> steamGreenhouse.setGreenHouseViewMode(GreenHouseViewMode.fromOrdinal(value))).allowC2S();
        syncManager.syncValue(VIEW_MODE_SYNC_KEY, viewModeSyncer);
        syncManager.syncValue(MAX_SEED_COUNT_SYNC_KEY, new IntSyncValue(steamGreenhouse::getMaxSeedCount));
        syncManager.syncValue(USED_SEED_COUNT_SYNC_KEY, new IntSyncValue(steamGreenhouse::getTotalStoredCropCount));

        GenericListSyncHandler<CropSlot> cropSlotSyncer = GenericListSyncHandler.<CropSlot>builder()
            .getter(this::createCropSlots)
            .setter(value -> {})
            .serializer(CropSlot::write)
            .deserializer(CropSlot::read)
            .equals(SteamGreenhouseModuleGui::areCropSlotsEqual)
            .immutableCopy()
            .build();
        syncManager.syncValue(CROP_SLOT_LIST_SYNC_KEY, cropSlotSyncer);
        cropInventoryWidgetSyncer = new DynamicSyncHandler().widgetProvider((panelSyncManager, packet) -> {
            if (packet == null) return new EmptyWidget();
            return createCropInventoryWidget(syncManager);
        });
        syncManager.syncValue(CROP_SLOT_WIDGET_SYNC_KEY, cropInventoryWidgetSyncer);
        if (!syncManager.isClient()) {
            cropSlotSyncer.setChangeListener(this::notifyCropInventoryUpdate);
        }
    }

    @Override
    protected ParentWidget<?> createTerminalParentWidget(ModularPanel panel, PanelSyncManager syncManager) {
        IntSyncValue viewModeSyncer = syncManager.findSyncHandler(VIEW_MODE_SYNC_KEY, IntSyncValue.class);
        DynamicSyncedWidget<?> cropInventoryWidget = createDynamicCropInventoryWidget(syncManager);
        cropInventoryWidget.pos(10, 12)
            .setEnabledIf(
                widget -> GreenHouseViewMode.fromOrdinal(viewModeSyncer.getIntValue()) != GreenHouseViewMode.STATUS);
        return new ParentWidget<>().size(getTerminalWidgetWidth(), getTerminalWidgetHeight())
            .child(
                GTGuiTextures.PICTURE_SCREEN_BLACK.asWidget()
                    .pos(4, 4)
                    .size(TERMINAL_WIDTH, 85)
                    .setEnabledIf(
                        widget -> GreenHouseViewMode.fromOrdinal(viewModeSyncer.getIntValue())
                            == GreenHouseViewMode.STATUS))
            .child(cropInventoryWidget)
            .child(
                createTerminalTextWidget(syncManager, panel).pos(0, 7)
                    .size(TERMINAL_WIDTH, 79)
                    .collapseDisabledChild()
                    .setEnabledIf(
                        widget -> GreenHouseViewMode.fromOrdinal(viewModeSyncer.getIntValue())
                            == GreenHouseViewMode.STATUS))
            .childIf(
                steamGreenhouse.supportsTerminalRightCornerColumn(),
                () -> createTerminalRightCornerColumn(panel, syncManager))
            .childIf(
                steamGreenhouse.supportsTerminalLeftCornerColumn(),
                () -> createTerminalLeftCornerColumn(panel, syncManager));
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
    protected Flow createPanelGap(ModularPanel parent, PanelSyncManager syncManager) {
        return Flow.row()
            .fullWidth()
            .height(getTextBoxToInventoryGap())
            .paddingLeft(4)
            .paddingRight(25)
            .mainAxisAlignment(Alignment.MainAxis.END)
            .child(createViewModeToggle(syncManager))
            .child(createRightPanelGapRow(parent, syncManager));
    }

    private IWidget createViewModeToggle(PanelSyncManager syncManager) {
        IntSyncValue viewModeSyncer = syncManager.findSyncHandler(VIEW_MODE_SYNC_KEY, IntSyncValue.class);
        return new ButtonWidget<>().size(55, 16)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(
                new DynamicDrawable(
                    () -> IKey.lang(getViewModeLangKey(viewModeSyncer.getIntValue()))
                        .asIcon()
                        .size(55, 16)))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                GreenHouseViewMode next = GreenHouseViewMode.fromOrdinal(viewModeSyncer.getIntValue())
                    .next();
                viewModeSyncer.setIntValue(next.ordinal(), true, true);
                notifyCropInventoryUpdate();
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private String getViewModeLangKey(int mode) {
        return switch (GreenHouseViewMode.fromOrdinal(mode)) {
            case BLOCKS -> "Info_EdenGarden_Blocks";
            case STATUS -> "Info_EdenGarden_Status";
            default -> "Info_EdenGarden_Seeds";
        };
    }

    private DynamicSyncedWidget<?> createDynamicCropInventoryWidget(PanelSyncManager syncManager) {
        DynamicSyncHandler syncer = syncManager.findSyncHandler(CROP_SLOT_WIDGET_SYNC_KEY, DynamicSyncHandler.class);
        return new DynamicSyncedWidget<>().syncHandler(syncer)
            .initialChild(createCropInventoryWidget(syncManager))
            .size(INVENTORY_WIDTH, INVENTORY_HEIGHT);
    }

    private IWidget createCropInventoryWidget(PanelSyncManager syncManager) {
        GenericListSyncHandler<CropSlot> cropSlotSyncer = getCropSlotSyncer(syncManager);
        IntSyncValue viewModeSyncer = syncManager.findSyncHandler(VIEW_MODE_SYNC_KEY, IntSyncValue.class);
        GreenHouseViewMode viewMode = GreenHouseViewMode.fromOrdinal(viewModeSyncer.getIntValue());
        List<IWidget> buttons = new ArrayList<>();
        for (CropSlot cropSlot : cropSlotSyncer.getValue()) {
            ItemStack stack = viewMode == GreenHouseViewMode.BLOCKS ? cropSlot.blockUnderStack() : cropSlot.seedStack();
            if (stack == null && viewMode == GreenHouseViewMode.BLOCKS) continue;
            buttons.add(createCropSlotButton(cropSlot.index(), stack, syncManager, viewMode));
        }
        if (viewMode == GreenHouseViewMode.SEEDS
            && steamGreenhouse.getTotalStoredCropCount() < steamGreenhouse.getMaxSeedCount()) {
            buttons.add(createInsertionSlotButton(syncManager));
        }

        Flow column = Flow.column()
            .size(INVENTORY_WIDTH, INVENTORY_HEIGHT)
            .crossAxisAlignment(Alignment.CrossAxis.START);
        for (int i = 0; i < buttons.size(); i += SLOTS_PER_ROW) {
            Flow row = Flow.row()
                .height(SLOT_SIZE);
            for (int j = 0; j < SLOTS_PER_ROW && i + j < buttons.size(); j++) {
                row.child(buttons.get(i + j));
            }
            column.child(row);
        }
        return column;
    }

    private IWidget createCropSlotButton(int cropIndex, ItemStack stack, PanelSyncManager syncManager,
        GreenHouseViewMode viewMode) {
        return new ButtonWidget<>().size(SLOT_SIZE, SLOT_SIZE)
            .background(GTGuiTextures.SLOT_ITEM_DARK)
            .child(
                new ItemDisplayWidget().item(stack)
                    .displayAmount(true)
                    .disableThemeBackground(true)
                    .disableHoverThemeBackground(true)
                    .size(SLOT_SIZE, SLOT_SIZE))
            .child(
                GTGuiTextures.OVERLAY_SLOT_BLOCK_STANDARD.asWidget()
                    .size(SLOT_SIZE, SLOT_SIZE)
                    .setEnabledIf(widget -> viewMode == GreenHouseViewMode.BLOCKS))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                if (!steamGreenhouse.isGreenHouseStorageEditable()) return;
                if (viewMode == GreenHouseViewMode.BLOCKS) return;
                if (mouseData.mouseButton == 2) {
                    handleCreativeSeedCopy(stack, syncManager);
                    return;
                }
                if (mouseData.shift) {
                    extractSeedToInventory(cropIndex, syncManager);
                    return;
                }
                ItemStack cursorStack = syncManager.getCursorItem();
                if (cursorStack != null) {
                    insertCursorSeed(cursorStack, mouseData.mouseButton == 1, syncManager);
                    return;
                }
                extractSeedToCursor(cropIndex, syncManager);
            }))
            .tooltipBuilder(tooltip -> addStackTooltip(tooltip, stack, syncManager))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createInsertionSlotButton(PanelSyncManager syncManager) {
        IntSyncValue maxSeedCountSyncer = syncManager.findSyncHandler(MAX_SEED_COUNT_SYNC_KEY, IntSyncValue.class);
        IntSyncValue usedSeedCountSyncer = syncManager.findSyncHandler(USED_SEED_COUNT_SYNC_KEY, IntSyncValue.class);
        return new ButtonWidget<>().size(SLOT_SIZE, SLOT_SIZE)
            .background(GTGuiTextures.SLOT_ITEM_DARK)
            .child(
                IKey.dynamic(() -> String.valueOf(maxSeedCountSyncer.getIntValue() - usedSeedCountSyncer.getIntValue()))
                    .asWidget()
                    .size(SLOT_SIZE, SLOT_SIZE)
                    .textAlign(Alignment.BottomRight))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                if (!steamGreenhouse.isGreenHouseStorageEditable()) return;
                ItemStack cursorStack = syncManager.getCursorItem();
                if (cursorStack != null) {
                    insertCursorSeed(cursorStack, mouseData.mouseButton == 1, syncManager);
                }
            }))
            .tooltipBuilder(
                tooltip -> tooltip.addLine(
                    IKey.str(
                        EnumChatFormatting.DARK_GREEN + "Remaining seed capacity: "
                            + (maxSeedCountSyncer.getIntValue() - usedSeedCountSyncer.getIntValue()))))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private void handleCreativeSeedCopy(ItemStack stack, PanelSyncManager syncManager) {
        if (stack == null || !syncManager.getPlayer().capabilities.isCreativeMode
            || syncManager.getCursorItem() != null) return;
        ItemStack copy = stack.copy();
        copy.stackSize = copy.getMaxStackSize();
        syncManager.setCursorItem(copy);
        updateHeldItem(syncManager);
    }

    private void extractSeedToInventory(int cropIndex, PanelSyncManager syncManager) {
        ItemStack removed = removeSeedStack(cropIndex);
        if (removed == null) return;
        if (syncManager.getPlayer().inventory.addItemStackToInventory(removed)) {
            syncManager.getPlayer().inventoryContainer.detectAndSendChanges();
        } else {
            syncManager.getPlayer()
                .entityDropItem(removed, 0.0f);
        }
        notifyCropInventoryUpdate();
    }

    private void extractSeedToCursor(int cropIndex, PanelSyncManager syncManager) {
        ItemStack removed = removeSeedStack(cropIndex);
        if (removed == null) return;
        syncManager.setCursorItem(removed);
        updateHeldItem(syncManager);
        notifyCropInventoryUpdate();
    }

    private ItemStack removeSeedStack(int cropIndex) {
        if (cropIndex < 0 || cropIndex >= steamGreenhouse.getStoredCrops()
            .size()) return null;
        GreenHouseStoredCrop crop = steamGreenhouse.getStoredCrops()
            .get(cropIndex);
        ItemStack removed = crop.removeSeeds(
            crop.getSeedStack()
                .getMaxStackSize());
        if (crop.getSeedCount() <= 0) {
            steamGreenhouse.getStoredCrops()
                .remove(cropIndex);
        }
        return removed;
    }

    private void insertCursorSeed(ItemStack cursorStack, boolean singleItem, PanelSyncManager syncManager) {
        ItemStack stackToInsert = singleItem ? cursorStack.copy() : cursorStack;
        if (singleItem) stackToInsert.stackSize = 1;
        int before = stackToInsert.stackSize;
        steamGreenhouse.tryAddCropStack(stackToInsert, false);
        int inserted = before - stackToInsert.stackSize;
        if (inserted <= 0) return;
        if (singleItem) {
            cursorStack.stackSize--;
        }
        if (cursorStack.stackSize <= 0) {
            syncManager.setCursorItem(null);
        }
        updateHeldItem(syncManager);
        notifyCropInventoryUpdate();
    }

    private void updateHeldItem(PanelSyncManager syncManager) {
        if (syncManager.getPlayer() instanceof EntityPlayerMP playerMP) {
            playerMP.isChangingQuantityOnly = false;
            playerMP.updateHeldItem();
        }
    }

    private void addStackTooltip(RichTooltip tooltip, ItemStack stack, PanelSyncManager syncManager) {
        if (stack == null) return;
        List<String> lines = stack.getTooltip(syncManager.getPlayer(), false);
        if (!lines.isEmpty() && lines.get(0) != null) {
            lines.set(0, stack.stackSize + " x " + lines.get(0));
        }
        for (String line : lines) {
            tooltip.addLine(IKey.str(line));
        }
    }

    private void notifyCropInventoryUpdate() {
        if (cropInventoryWidgetSyncer != null) {
            cropInventoryWidgetSyncer.notifyUpdate(buffer -> {});
        }
    }

    private List<CropSlot> createCropSlots() {
        List<CropSlot> slots = new ArrayList<>();
        for (int i = 0; i < steamGreenhouse.getStoredCrops()
            .size(); i++) {
            GreenHouseStoredCrop crop = steamGreenhouse.getStoredCrops()
                .get(i);
            if (crop == null || CropsNHUtils.isStackInvalid(crop.getSeedStack())) continue;
            ItemStack seed = crop.getSeedStack()
                .copy();
            ItemStack block = CropsNHUtils.isStackValid(crop.getBlockUnderStack()) ? crop.getBlockUnderStack()
                .copy() : null;
            slots.add(new CropSlot(i, seed, block));
        }
        return slots;
    }

    private List<DropEntry> createDropEntries() {
        List<DropEntry> entries = new ArrayList<>();
        IFDropTable tracker = steamGreenhouse.getIndustrialFarmGuiDropTracker();
        for (Map.Entry<ItemStack, Double> drop : tracker.entrySet()) {
            entries.add(
                new DropEntry(
                    drop.getKey()
                        .copy(),
                    drop.getValue()));
        }
        entries.sort(
            Comparator.comparing(
                entry -> entry.stack()
                    .toString()
                    .toLowerCase()));
        return entries;
    }

    private void setClientDropEntries(List<DropEntry> entries) {
        IFDropTable tracker = new IFDropTable();
        for (DropEntry entry : entries) {
            tracker.addDrop(entry.stack(), entry.chance());
        }
        steamGreenhouse.setIndustrialFarmGuiDropTracker(tracker);
    }

    @SuppressWarnings("unchecked")
    private GenericListSyncHandler<CropSlot> getCropSlotSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(CROP_SLOT_LIST_SYNC_KEY, GenericListSyncHandler.class);
    }

    private static boolean areCropSlotsEqual(CropSlot first, CropSlot second) {
        if (first == null || second == null) return first == second;
        return first.index() == second.index() && ItemStack.areItemStacksEqual(first.seedStack(), second.seedStack())
            && ItemStack.areItemStacksEqual(first.blockUnderStack(), second.blockUnderStack());
    }

    private static boolean areDropEntriesEqual(DropEntry first, DropEntry second) {
        if (first == null || second == null) return first == second;
        return Double.compare(first.chance(), second.chance()) == 0
            && ItemStack.areItemStacksEqual(first.stack(), second.stack());
    }

    public record CropSlot(int index, ItemStack seedStack, ItemStack blockUnderStack) {

        public static void write(PacketBuffer buffer, CropSlot cropSlot) throws IOException {
            buffer.writeVarIntToBuffer(cropSlot.index());
            buffer.writeItemStackToBuffer(cropSlot.seedStack());
            buffer.writeItemStackToBuffer(cropSlot.blockUnderStack());
        }

        public static CropSlot read(PacketBuffer buffer) throws IOException {
            return new CropSlot(
                buffer.readVarIntFromBuffer(),
                buffer.readItemStackFromBuffer(),
                buffer.readItemStackFromBuffer());
        }
    }

    public record DropEntry(ItemStack stack, double chance) {

        public static void write(PacketBuffer buffer, DropEntry entry) throws IOException {
            buffer.writeItemStackToBuffer(entry.stack());
            buffer.writeDouble(entry.chance());
        }

        public static DropEntry read(PacketBuffer buffer) throws IOException {
            return new DropEntry(buffer.readItemStackFromBuffer(), buffer.readDouble());
        }
    }
}
