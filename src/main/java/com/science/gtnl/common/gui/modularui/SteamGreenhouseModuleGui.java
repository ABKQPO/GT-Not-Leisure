package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumChatFormatting;

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
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.EmptyWidget;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.cropsnh.utility.IFDropTable;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.gtnhlib.util.numberformatting.options.CompactOptions;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamGreenhouseModule;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseStoredCrop;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseViewMode;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.util.GTUtility;
import gregtech.common.modularui2.sync.Predicates;
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;

public class SteamGreenhouseModuleGui extends GTNLSteamMultiBlockBaseGui {

    private static final String VIEW_MODE_SYNC_KEY = "steamGreenhouseViewMode";
    private static final String CROP_SLOT_LIST_SYNC_KEY = "steamGreenhouseCropSlots";
    private static final String CROP_SLOT_WIDGET_SYNC_KEY = "steamGreenhouseCropSlotWidget";
    private static final String MAX_SEED_COUNT_SYNC_KEY = "steamGreenhouseMaxSeedCount";
    private static final String USED_SEED_COUNT_SYNC_KEY = "steamGreenhouseUsedSeedCount";
    private static final String DROP_TRACKER_SYNC_KEY = "steamGreenhouseDropTracker";
    private static final String CROP_SLOT_CLICK_SYNC_KEY = "steamGreenhouseCropSlotClick";
    private static final String BLOCK_SLOT_CLICK_SYNC_KEY = "steamGreenhouseBlockSlotClick";
    private static final int CROP_INSERTION_CLICK_FLAG = 1 << 30;
    private static final int BLOCK_INSERTION_CLICK_FLAG = 1 << 29;
    private static final CompactOptions COUNT_FORMAT = new CompactOptions().setDecimalPlaces(0)
        .disableExponentialFormatting();
    private static final int TERMINAL_WIDTH = 190;
    private static final int TERMINAL_HEIGHT = 94;
    private static final int TERMINAL_TEXT_WIDTH = TERMINAL_WIDTH - 4;
    private static final int TERMINAL_TEXT_HEIGHT = TERMINAL_HEIGHT - 8;
    private static final int INVENTORY_WIDTH = 162;
    private static final int INVENTORY_HEIGHT = 72;
    private static final int SLOT_SIZE = 18;
    private static final int SLOTS_PER_ROW = INVENTORY_WIDTH / SLOT_SIZE;
    private static final int VIEW_BUTTON_WIDTH = 54;
    private static final int VIEW_BUTTON_HEIGHT = 18;

    private final SteamGreenhouseModule multiblock;
    private DynamicSyncHandler cropInventoryWidgetSyncer;
    private GenericListSyncHandler<CropSlot> cropSlotSyncer;
    private IntSyncValue usedSeedCountSyncer;
    private IntSyncValue cropClickSyncer;
    private IntSyncValue blockClickSyncer;
    private PanelSyncManager mainSyncManager;

    public SteamGreenhouseModuleGui(SteamGreenhouseModule multiblock) {
        super(multiblock);
        this.multiblock = multiblock;
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        uiSettings.customContainer(
            () -> new GreenHouseModularContainer(
                multiblock,
                VIEW_MODE_SYNC_KEY,
                CROP_SLOT_LIST_SYNC_KEY,
                CROP_SLOT_WIDGET_SYNC_KEY,
                USED_SEED_COUNT_SYNC_KEY,
                false,
                true));
        return super.build(guiData, syncManager, uiSettings);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        mainSyncManager = syncManager;
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
            () -> multiblock.getGreenHouseViewMode()
                .ordinal(),
            value -> multiblock.setGreenHouseViewMode(GreenHouseViewMode.fromOrdinalWithoutBlocks(value))).allowC2S();
        syncManager.syncValue(VIEW_MODE_SYNC_KEY, viewModeSyncer);
        syncManager.syncValue(MAX_SEED_COUNT_SYNC_KEY, new IntSyncValue(multiblock::getMaxSeedCount));
        usedSeedCountSyncer = new IntSyncValue(multiblock::getTotalStoredCropCount);
        syncManager.syncValue(USED_SEED_COUNT_SYNC_KEY, usedSeedCountSyncer);
        cropClickSyncer = new IntSyncValue(() -> 0, this::handleCropSlotClick).allowC2S();
        syncManager.syncValue(CROP_SLOT_CLICK_SYNC_KEY, cropClickSyncer);
        blockClickSyncer = new IntSyncValue(() -> 0, this::handleBlockSlotClick).allowC2S();
        syncManager.syncValue(BLOCK_SLOT_CLICK_SYNC_KEY, blockClickSyncer);

        cropSlotSyncer = GenericListSyncHandler.<CropSlot>builder()
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
            return createCropInventoryWidgetFromPacket(panelSyncManager, packet);
        });
        syncManager.syncValue(CROP_SLOT_WIDGET_SYNC_KEY, cropInventoryWidgetSyncer);
        if (!syncManager.isClient()) {
            viewModeSyncer.setChangeListener(this::notifyCropInventoryUpdate);
            notifyCropInventoryUpdate();
        }
    }

    @Override
    protected ParentWidget<?> createTerminalParentWidget(ModularPanel panel, PanelSyncManager syncManager) {
        IntSyncValue viewModeSyncer = syncManager.findSyncHandler(VIEW_MODE_SYNC_KEY, IntSyncValue.class);
        DynamicSyncedWidget<?> cropInventoryWidget = createDynamicCropInventoryWidget(syncManager);
        cropInventoryWidget.pos((TERMINAL_WIDTH - INVENTORY_WIDTH) / 2, 12)
            .setEnabledIf(
                widget -> GreenHouseViewMode.fromOrdinalWithoutBlocks(viewModeSyncer.getIntValue())
                    != GreenHouseViewMode.STATUS);
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
                multiblock.supportsTerminalRightCornerColumn(),
                () -> createTerminalRightCornerColumn(panel, syncManager))
            .childIf(
                multiblock.supportsTerminalLeftCornerColumn(),
                () -> createTerminalLeftCornerColumn(panel, syncManager))
            .setEnabledIf(
                widget -> GreenHouseViewMode.fromOrdinalWithoutBlocks(viewModeSyncer.getIntValue())
                    == GreenHouseViewMode.STATUS);

        return new ParentWidget<>().size(getTerminalWidgetWidth(), TERMINAL_HEIGHT)
            .child(statusParent)
            .child(cropInventoryWidget);
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
            .paddingRight(2)
            .paddingLeft(4)
            .height(getTextBoxToInventoryGap())
            .child(createMachineModeRow(syncManager))
            .child(
                Flow.row()
                    .mainAxisAlignment(Alignment.MainAxis.END)
                    .crossAxisAlignment(Alignment.CrossAxis.CENTER)
                    .rightRel(0)
                    .coverChildrenWidth()
                    .fullHeight()
                    .child(createBlockInsertionSlotButton(syncManager)));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return super.createTerminalTextWidget(syncManager, parent).child(createProgressTextWidget(syncManager));
    }

    private Flow createMachineModeRow(PanelSyncManager syncManager) {
        return Flow.row()
            .coverChildrenWidth()
            .fullHeight()
            .childIf(!machineModeIcons.isEmpty(), () -> createModeSwitchButton(syncManager))
            .child(createViewModeToggle(syncManager));
    }

    private IWidget createViewModeToggle(PanelSyncManager syncManager) {
        IntSyncValue viewModeSyncer = syncManager.findSyncHandler(VIEW_MODE_SYNC_KEY, IntSyncValue.class);
        return new ButtonWidget<>().size(VIEW_BUTTON_WIDTH, VIEW_BUTTON_HEIGHT)
            .overlay(
                new DynamicDrawable(
                    () -> IKey.lang(getViewModeLangKey(viewModeSyncer.getIntValue()))
                        .asIcon()
                        .size(VIEW_BUTTON_WIDTH, VIEW_BUTTON_HEIGHT)))
            .onMousePressed(mouseButton -> {
                GreenHouseViewMode current = GreenHouseViewMode.fromOrdinalWithoutBlocks(viewModeSyncer.getIntValue());
                GreenHouseViewMode target = mouseButton == 1 ? current.previousWithoutBlocks()
                    : current.nextWithoutBlocks();
                viewModeSyncer.setIntValue(target.ordinal(), true, true);
                return true;
            })
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .marginTop(-1)
            .marginRight(-1);
    }

    private String getViewModeLangKey(int mode) {
        return switch (GreenHouseViewMode.fromOrdinalWithoutBlocks(mode)) {
            case STATUS -> "Info_EdenGarden_Status";
            default -> "Info_EdenGarden_Seeds";
        };
    }

    private DynamicSyncedWidget<?> createDynamicCropInventoryWidget(PanelSyncManager syncManager) {
        DynamicSyncHandler syncer = syncManager.findSyncHandler(CROP_SLOT_WIDGET_SYNC_KEY, DynamicSyncHandler.class);
        return new DynamicSyncedWidget<>().syncHandler(syncer)
            .initialChild(
                createCropInventoryWidget(
                    syncManager,
                    GreenHouseViewMode.fromOrdinalWithoutBlocks(
                        syncManager.findSyncHandler(VIEW_MODE_SYNC_KEY, IntSyncValue.class)
                            .getIntValue()),
                    getCropSlotSyncer(syncManager).getValue()))
            .size(INVENTORY_WIDTH, INVENTORY_HEIGHT);
    }

    private IWidget createCropInventoryWidget(PanelSyncManager syncManager, GreenHouseViewMode viewMode,
        List<CropSlot> cropSlots) {
        List<IWidget> buttons = new ArrayList<>();
        int storedSeedCount = 0;
        for (CropSlot cropSlot : cropSlots) {
            storedSeedCount += cropSlot.seedStack().stackSize;
            buttons.add(createCropSlotButton(cropSlot.index(), cropSlot.seedStack(), syncManager));
        }
        if (viewMode == GreenHouseViewMode.SEEDS && storedSeedCount < multiblock.getMaxSeedCount()) {
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

    private IWidget createCropSlotButton(int cropIndex, ItemStack stack, PanelSyncManager syncManager) {
        return createStackSlotWidget(() -> stack, () -> stack.stackSize).background(GTGuiTextures.SLOT_ITEM_DARK)
            .onMousePressed(mouseButton -> {
                if (cropClickSyncer == null) return true;
                cropClickSyncer.setIntValue(encodeCropSlotClick(cropIndex, mouseButton), true, true);
                return true;
            })
            .tooltipBuilder(tooltip -> addStackTooltip(tooltip, stack, syncManager))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private SlotLikeButtonWidget createStackSlotWidget(Supplier<ItemStack> stackSupplier, IntSupplier countSupplier) {
        return new SlotLikeButtonWidget(() -> createRenderStack(stackSupplier.get())) {

            @Override
            public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetThemeEntry) {
                super.draw(context, widgetThemeEntry);
                GuiDraw.drawScaledAlignedTextInBox(
                    formatCount(countSupplier.getAsInt()),
                    0,
                    0,
                    SLOT_SIZE,
                    SLOT_SIZE,
                    Alignment.BottomRight);
                if (!multiblock.isGreenHouseStorageEditable()) {
                    GuiDraw.drawRect(0, 0, SLOT_SIZE, SLOT_SIZE, 0x80000000);
                }
            }
        }.size(SLOT_SIZE, SLOT_SIZE);
    }

    private ItemStack createRenderStack(ItemStack stack) {
        if (CropsNHUtils.isStackInvalid(stack)) return null;
        ItemStack renderStack = stack.copy();
        renderStack.stackSize = 1;
        return renderStack;
    }

    private IWidget createInsertionSlotButton(PanelSyncManager syncManager) {
        IntSyncValue maxSeedCountSyncer = syncManager.findSyncHandler(MAX_SEED_COUNT_SYNC_KEY, IntSyncValue.class);
        IntSyncValue usedSeedCountSyncer = syncManager.findSyncHandler(USED_SEED_COUNT_SYNC_KEY, IntSyncValue.class);
        return createStackSlotWidget(
            () -> null,
            () -> maxSeedCountSyncer.getIntValue() - usedSeedCountSyncer.getIntValue())
                .background(GTGuiTextures.SLOT_ITEM_DARK)
                .onMousePressed(mouseButton -> {
                    if (cropClickSyncer == null) return true;
                    cropClickSyncer.setIntValue(encodeCropInsertionClick(mouseButton), true, true);
                    return true;
                })
                .tooltipBuilder(
                    tooltip -> tooltip.addLine(
                        IKey.str(
                            EnumChatFormatting.DARK_GREEN + "Remaining seed capacity: "
                                + (maxSeedCountSyncer.getIntValue() - usedSeedCountSyncer.getIntValue()))))
                .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createBlockInsertionSlotButton(PanelSyncManager syncManager) {
        return createStackSlotWidget(
            () -> getBlockInsertionStack(syncManager),
            () -> getBlockInsertionCount(syncManager))
                .background(GTGuiTextures.SLOT_ITEM_DARK, GTGuiTextures.OVERLAY_SLOT_BLOCK_STANDARD)
                .onMousePressed(mouseButton -> {
                    if (blockClickSyncer == null) return true;
                    blockClickSyncer.setIntValue(encodeBlockInsertionClick(mouseButton), true, true);
                    return true;
                })
                .tooltipBuilder(tooltip -> {
                    tooltip.setAutoUpdate(true);
                    tooltip.addLine(IKey.str(EnumChatFormatting.DARK_GREEN + getBlockInsertionTooltip(syncManager)));
                })
                .tooltipShowUpTimer(TOOLTIP_DELAY)
                .marginRight(0);
    }

    private int encodeCropSlotClick(int cropIndex, int mouseButton) {
        return ((cropIndex + 1) << 3) | ((mouseButton & 0x3) << 1) | (Interactable.hasShiftDown() ? 1 : 0);
    }

    private int encodeCropInsertionClick(int mouseButton) {
        return CROP_INSERTION_CLICK_FLAG | ((mouseButton & 0x3) << 1) | (Interactable.hasShiftDown() ? 1 : 0);
    }

    private int encodeBlockInsertionClick(int mouseButton) {
        return BLOCK_INSERTION_CLICK_FLAG | ((mouseButton & 0x3) << 1) | (Interactable.hasShiftDown() ? 1 : 0);
    }

    private void handleCropSlotClick(int encoded) {
        if (encoded == 0 || mainSyncManager == null || mainSyncManager.isClient()) return;
        if (!multiblock.isGreenHouseStorageEditable()) return;
        if (multiblock.getGreenHouseViewMode() != GreenHouseViewMode.SEEDS) return;

        int mouseButton = (encoded >>> 1) & 0x3;
        boolean shift = (encoded & 1) != 0;
        if ((encoded & CROP_INSERTION_CLICK_FLAG) != 0) {
            handleInsertionSlotClick(mouseButton, mainSyncManager);
            return;
        }

        int cropIndex = (encoded >>> 3) - 1;
        if (cropIndex < 0 || cropIndex >= multiblock.getStoredCrops()
            .size()) return;
        GreenHouseStoredCrop crop = multiblock.getStoredCrops()
            .get(cropIndex);
        if (crop == null || CropsNHUtils.isStackInvalid(crop.getSeedStack())) return;
        if (mouseButton == 2) {
            handleCreativeSeedCopy(crop.getSeedStack(), mainSyncManager);
            return;
        }
        if (shift) {
            extractSeedToInventory(cropIndex, mainSyncManager);
            return;
        }
        ItemStack cursorStack = mainSyncManager.getCursorItem();
        if (cursorStack != null) {
            insertCursorSeed(cursorStack, mouseButton == 1, mainSyncManager);
            return;
        }
        extractSeedToCursor(cropIndex, mainSyncManager);
    }

    private void handleBlockSlotClick(int encoded) {
        if (encoded == 0 || mainSyncManager == null || mainSyncManager.isClient()) return;
        if (!multiblock.isGreenHouseStorageEditable()) return;

        int mouseButton = (encoded >>> 1) & 0x3;
        boolean shift = (encoded & 1) != 0;
        ItemStack cursorStack = mainSyncManager.getCursorItem();
        if (cursorStack == null) {
            if (shift) {
                extractFirstBlockToInventory(mainSyncManager);
            } else {
                extractFirstBlockToCursor(mainSyncManager);
            }
            return;
        }
        insertCursorBlock(cursorStack, mouseButton == 1, mainSyncManager);
    }

    private void insertCursorBlock(ItemStack cursorStack, boolean singleItem, PanelSyncManager syncManager) {
        ItemStack stackToInsert = singleItem ? cursorStack.copy() : cursorStack;
        if (singleItem) stackToInsert.stackSize = 1;
        int before = stackToInsert.stackSize;
        multiblock.tryAddBlockUnderStack(stackToInsert, false);
        int inserted = before - stackToInsert.stackSize;
        if (inserted <= 0) return;
        if (singleItem) {
            cursorStack.stackSize--;
        }
        if (cursorStack.stackSize <= 0) {
            syncManager.setCursorItem(null);
        }
        updateHeldItem(syncManager);
        notifyCropSlotsChanged();
    }

    private void handleInsertionSlotClick(int mouseButton, PanelSyncManager syncManager) {
        ItemStack cursorStack = syncManager.getCursorItem();
        if (cursorStack != null) {
            insertCursorSeed(cursorStack, mouseButton == 1, syncManager);
        }
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
        notifyCropSlotsChanged();
    }

    private void extractSeedToCursor(int cropIndex, PanelSyncManager syncManager) {
        ItemStack removed = removeSeedStack(cropIndex);
        if (removed == null) return;
        syncManager.setCursorItem(removed);
        updateHeldItem(syncManager);
        notifyCropSlotsChanged();
    }

    private void extractFirstBlockToInventory(PanelSyncManager syncManager) {
        ItemStack removed = removeFirstBlockStack();
        if (removed == null) return;
        if (syncManager.getPlayer().inventory.addItemStackToInventory(removed)) {
            syncManager.getPlayer().inventoryContainer.detectAndSendChanges();
        } else {
            syncManager.getPlayer()
                .entityDropItem(removed, 0.0f);
        }
        notifyCropSlotsChanged();
    }

    private void extractFirstBlockToCursor(PanelSyncManager syncManager) {
        ItemStack removed = removeFirstBlockStack();
        if (removed == null) return;
        syncManager.setCursorItem(removed);
        updateHeldItem(syncManager);
        notifyCropSlotsChanged();
    }

    private ItemStack removeSeedStack(int cropIndex) {
        if (cropIndex < 0 || cropIndex >= multiblock.getStoredCrops()
            .size()) return null;
        GreenHouseStoredCrop crop = multiblock.getStoredCrops()
            .get(cropIndex);
        int removable = getRemovableSeedCount(crop);
        if (removable <= 0) return null;
        ItemStack removed = crop.removeSeeds(removable);
        if (crop.getSeedCount() <= 0) {
            multiblock.getStoredCrops()
                .remove(cropIndex);
        }
        return removed;
    }

    private int getRemovableSeedCount(GreenHouseStoredCrop crop) {
        if (crop == null || CropsNHUtils.isStackInvalid(crop.getSeedStack())) return 0;
        int removable = crop.getSeedStack()
            .getMaxStackSize();
        if (CropsNHUtils.isStackInvalid(crop.getBlockUnderStack())) return removable;
        int pairedSeedCount = Math.min(crop.getSeedCount(), crop.getBlockUnderStack().stackSize);
        removable = Math.min(removable, crop.getSeedCount() - pairedSeedCount);
        return Math.max(0, removable);
    }

    private ItemStack removeFirstBlockStack() {
        for (GreenHouseStoredCrop crop : multiblock.getStoredCrops()) {
            if (CropsNHUtils.isStackInvalid(crop.getBlockUnderStack())) continue;
            return crop.removeBlockUnders(
                crop.getBlockUnderStack()
                    .getMaxStackSize());
        }
        return null;
    }

    private void insertCursorSeed(ItemStack cursorStack, boolean singleItem, PanelSyncManager syncManager) {
        ItemStack stackToInsert = singleItem ? cursorStack.copy() : cursorStack;
        if (singleItem) stackToInsert.stackSize = 1;
        int before = stackToInsert.stackSize;
        multiblock.tryAddCropStack(stackToInsert, false);
        int inserted = before - stackToInsert.stackSize;
        if (inserted <= 0) return;
        if (singleItem) {
            cursorStack.stackSize--;
        }
        if (cursorStack.stackSize <= 0) {
            syncManager.setCursorItem(null);
        }
        updateHeldItem(syncManager);
        notifyCropSlotsChanged();
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
            cropInventoryWidgetSyncer.notifyUpdate(this::writeCropInventoryWidgetState);
        }
    }

    private void notifyCropSlotsChanged() {
        if (usedSeedCountSyncer != null) {
            usedSeedCountSyncer.notifyUpdate();
        }
        if (cropSlotSyncer != null) {
            cropSlotSyncer.notifyUpdate();
        }
        notifyCropInventoryUpdate();
    }

    private int getMissingBlockUnderCount(PanelSyncManager syncManager) {
        return getCropSlotSyncer(syncManager).getValue()
            .stream()
            .mapToInt(CropSlot::missingBlockUnderCount)
            .sum();
    }

    private ItemStack getBlockInsertionStack(PanelSyncManager syncManager) {
        return getCropSlotSyncer(syncManager).getValue()
            .stream()
            .map(CropSlot::blockUnderStack)
            .filter(CropsNHUtils::isStackValid)
            .findFirst()
            .orElse(null);
    }

    private int getBlockInsertionCount(PanelSyncManager syncManager) {
        ItemStack stack = getBlockInsertionStack(syncManager);
        if (CropsNHUtils.isStackInvalid(stack)) return getMissingBlockUnderCount(syncManager);
        return getCropSlotSyncer(syncManager).getValue()
            .stream()
            .map(CropSlot::blockUnderStack)
            .filter(CropsNHUtils::isStackValid)
            .filter(candidate -> GTUtility.areStacksEqual(stack, candidate, false))
            .mapToInt(candidate -> candidate.stackSize)
            .sum();
    }

    private String getBlockInsertionTooltip(PanelSyncManager syncManager) {
        ItemStack stack = getBlockInsertionStack(syncManager);
        if (CropsNHUtils.isStackInvalid(stack)) {
            return "Missing block-under capacity: " + getBlockInsertionCount(syncManager);
        }
        return "Stored block-under: " + getBlockInsertionCount(syncManager);
    }

    private IWidget createProgressTextWidget(PanelSyncManager syncManager) {
        IntSyncValue progressTimeSyncer = syncManager.findSyncHandler("progressTime", IntSyncValue.class);
        return IKey.dynamic(multiblock::generateCurrentRecipeInfoString)
            .asWidget()
            .fullWidth()
            .marginBottom(2)
            .setEnabledIf(
                widget -> progressTimeSyncer.getIntValue() > 0
                    && !Predicates.isNonEmptyList(syncManager.getSyncHandlerFromMapKey("itemOutput:0"))
                    && !Predicates.isNonEmptyList(syncManager.getSyncHandlerFromMapKey("fluidOutput:0")));
    }

    private void writeCropInventoryWidgetState(PacketBuffer buffer) throws IOException {
        GreenHouseViewMode viewMode = multiblock.getGreenHouseViewMode()
            .withoutBlocks();
        List<CropSlot> cropSlots = createCropSlots();
        buffer.writeVarIntToBuffer(viewMode.ordinal());
        buffer.writeVarIntToBuffer(cropSlots.size());
        for (CropSlot cropSlot : cropSlots) {
            CropSlot.write(buffer, cropSlot);
        }
    }

    private IWidget createCropInventoryWidgetFromPacket(PanelSyncManager syncManager, PacketBuffer buffer) {
        try {
            GreenHouseViewMode viewMode = GreenHouseViewMode.fromOrdinalWithoutBlocks(buffer.readVarIntFromBuffer());
            int size = buffer.readVarIntFromBuffer();
            List<CropSlot> slots = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                slots.add(CropSlot.read(buffer));
            }
            return createCropInventoryWidget(syncManager, viewMode, slots);
        } catch (Exception ignored) {
            return createCropInventoryWidget(
                syncManager,
                GreenHouseViewMode.fromOrdinalWithoutBlocks(
                    syncManager.findSyncHandler(VIEW_MODE_SYNC_KEY, IntSyncValue.class)
                        .getIntValue()),
                getCropSlotSyncer(syncManager).getValue());
        }
    }

    private String formatCount(int count) {
        return NumberFormatUtil.formatNumberCompact(Math.max(0, count), COUNT_FORMAT)
            .toLowerCase(Locale.ROOT);
    }

    private List<CropSlot> createCropSlots() {
        List<CropSlot> slots = new ArrayList<>();
        for (int i = 0; i < multiblock.getStoredCrops()
            .size(); i++) {
            GreenHouseStoredCrop crop = multiblock.getStoredCrops()
                .get(i);
            if (crop == null || CropsNHUtils.isStackInvalid(crop.getSeedStack())) continue;
            ItemStack seed = crop.getSeedStack()
                .copy();
            boolean needsBlockUnder = false;
            var seedData = CropsNHUtils.getAnalyzedSeedData(crop.getSeedStack());
            if (seedData != null) {
                needsBlockUnder = multiblock.needsBlockUnder(seedData);
            }
            ItemStack blockUnder = CropsNHUtils.isStackValid(crop.getBlockUnderStack()) ? crop.getBlockUnderStack()
                .copy() : null;
            slots.add(new CropSlot(i, seed, blockUnder, needsBlockUnder));
        }
        return slots;
    }

    private List<DropEntry> createDropEntries() {
        List<DropEntry> entries = new ArrayList<>();
        IFDropTable tracker = multiblock.getIndustrialFarmGuiDropTracker();
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
        multiblock.setIndustrialFarmGuiDropTracker(tracker);
    }

    @SuppressWarnings("unchecked")
    private GenericListSyncHandler<CropSlot> getCropSlotSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(CROP_SLOT_LIST_SYNC_KEY, GenericListSyncHandler.class);
    }

    private static boolean areCropSlotsEqual(CropSlot first, CropSlot second) {
        if (first == null || second == null) return first == second;
        return first.index() == second.index() && ItemStack.areItemStacksEqual(first.seedStack(), second.seedStack())
            && ItemStack.areItemStacksEqual(first.blockUnderStack(), second.blockUnderStack())
            && first.needsBlockUnder() == second.needsBlockUnder();
    }

    private static boolean areDropEntriesEqual(DropEntry first, DropEntry second) {
        if (first == null || second == null) return first == second;
        return Double.compare(first.chance(), second.chance()) == 0
            && ItemStack.areItemStacksEqual(first.stack(), second.stack());
    }

    public record CropSlot(int index, ItemStack seedStack, ItemStack blockUnderStack, boolean needsBlockUnder) {

        public int missingBlockUnderCount() {
            if (!needsBlockUnder) return 0;
            int blockCount = CropsNHUtils.isStackValid(blockUnderStack) ? blockUnderStack.stackSize : 0;
            return Math.max(0, seedStack.stackSize - blockCount);
        }

        public static void write(PacketBuffer buffer, CropSlot cropSlot) throws IOException {
            buffer.writeVarIntToBuffer(cropSlot.index());
            buffer.writeItemStackToBuffer(cropSlot.seedStack());
            buffer.writeItemStackToBuffer(cropSlot.blockUnderStack());
            buffer.writeBoolean(cropSlot.needsBlockUnder());
        }

        public static CropSlot read(PacketBuffer buffer) throws IOException {
            return new CropSlot(
                buffer.readVarIntFromBuffer(),
                buffer.readItemStackFromBuffer(),
                buffer.readItemStackFromBuffer(),
                buffer.readBoolean());
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
