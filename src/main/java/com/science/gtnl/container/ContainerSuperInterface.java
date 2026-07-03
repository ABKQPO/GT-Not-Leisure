package com.science.gtnl.container;

import net.minecraft.entity.player.InventoryPlayer;

import com.science.gtnl.api.mixinHelper.IDualityInterface;
import com.science.gtnl.mixins.late.appliedEnergistics.AccessorContainerUpgradeable;

import appeng.api.config.Settings;
import appeng.api.config.SidelessMode;
import appeng.api.util.IConfigManager;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerInterface;
import appeng.container.slot.OptionalSlotFake;
import appeng.container.slot.OptionalSlotRestrictedInput;
import appeng.container.slot.SlotNormal;
import appeng.container.slot.SlotRestrictedInput;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.util.Platform;

public class ContainerSuperInterface extends ContainerInterface {

    public final DualityInterface myDuality;
    public int configSlots, storageSlots, patternSlots, upgradeSlots;

    public static final int PATTERNS_PER_PAGE = 36;
    public static final int CONFIG_PER_PAGE = 9;
    public int currentPage = 0;
    public final int maxPage;
    public final OptionalSlotRestrictedInput[][] patternSlotPages;
    public final OptionalSlotFake[][] configSlotPages;
    public final SlotNormal[][] storageSlotPages;

    public static final int SLOT_SIZE = 18;
    public static final int TITLE_HEIGHT = 15;
    public static final int SECTION_GAP = 3;
    public static final int PLAYER_INV_HEIGHT = 82;

    @GuiSync(19)
    public SidelessMode sidelessMode = SidelessMode.SIDELESS;

    public ContainerSuperInterface(InventoryPlayer ip, IInterfaceHost te) {
        super(ip, te);
        this.myDuality = te.getInterfaceDuality();
        var accessor = (IDualityInterface) myDuality;

        configSlots = accessor.getConfigSlots();
        storageSlots = accessor.getStorageSlots();
        patternSlots = accessor.getPatternSlots();
        upgradeSlots = accessor.getUpgradeSlots();

        maxPage = getMaxPages();
        patternSlotPages = new OptionalSlotRestrictedInput[maxPage][PATTERNS_PER_PAGE];
        configSlotPages = new OptionalSlotFake[maxPage][CONFIG_PER_PAGE];
        storageSlotPages = new SlotNormal[maxPage][CONFIG_PER_PAGE];

        this.refreshSlots(ip);
    }

    public void refreshSlots(InventoryPlayer ip) {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        this.bindPlayerInventory(ip, 0, this.getHeight() - PLAYER_INV_HEIGHT);

        this.addUpgradeSlots();

        if (this.hasToolbox()) {
            int size = this.getToolboxSize();
            // Shift the larger toolbox layout slightly downward.
            int yBias = size == 3 ? 0 : 7;
            for (int v = 0; v < size; v++) {
                for (int u = 0; u < size; u++) {
                    this.addSlotToContainer(
                        (new SlotRestrictedInput(
                            SlotRestrictedInput.PlacableItemType.UPGRADES,
                            ((AccessorContainerUpgradeable) this).getTbInventory(),
                            u + v * size,
                            186 + u * 18,
                            this.getHeight() - 82 - yBias + v * 18,
                            this.getInventoryPlayer())).setPlayerSide());
                }
            }
        }

        int patternStartY = TITLE_HEIGHT + (2 * SLOT_SIZE) + SECTION_GAP;

        for (int i = 0; i < PATTERNS_PER_PAGE * maxPage; i++) {
            int x = i % 9;
            int y = (i / 9) % 4;
            var g = i / PATTERNS_PER_PAGE;
            this.addSlotToContainer(
                (patternSlotPages[g][i % 36] = new OptionalSlotRestrictedInput(
                    SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN,
                    this.myDuality.getPatterns(),
                    this,
                    i,
                    8 + SLOT_SIZE * x,
                    patternStartY + y * SLOT_SIZE,
                    g,
                    ip)).setStackLimit(1));
            if (g > 0) {
                patternSlotPages[g][i % 36].xDisplayPosition = Integer.MIN_VALUE;
            }
        }

        for (int i = 0; i < CONFIG_PER_PAGE * maxPage; i++) {
            int x = i % 9;
            int p = i / CONFIG_PER_PAGE;

            OptionalSlotFake configSlot = new OptionalSlotFake(
                this.myDuality.getConfig(),
                this,
                i,
                8 + SLOT_SIZE * x,
                TITLE_HEIGHT,
                -1);
            configSlotPages[p][i % CONFIG_PER_PAGE] = configSlot;
            this.addSlotToContainer(configSlot);

            SlotNormal storageSlot = new SlotNormal(
                this.myDuality.getStorage(),
                i,
                8 + SLOT_SIZE * x,
                TITLE_HEIGHT + SLOT_SIZE);
            storageSlotPages[p][i % CONFIG_PER_PAGE] = storageSlot;
            this.addSlotToContainer(storageSlot);

            if (p > 0) {
                configSlot.xDisplayPosition = Integer.MIN_VALUE;
                storageSlot.xDisplayPosition = Integer.MIN_VALUE;
            }
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            setPageSlotsVisible(currentPage, false);
            currentPage--;
            setPageSlotsVisible(currentPage, true);
        }
    }

    public void nextPage() {
        if (currentPage < maxPage - 1) {
            setPageSlotsVisible(currentPage, false);
            currentPage++;
            setPageSlotsVisible(currentPage, true);
        }
    }

    private void setPageSlotsVisible(int page, boolean visible) {
        for (var slot : patternSlotPages[page]) {
            if (slot != null) slot.xDisplayPosition = visible ? slot.getX() : Integer.MIN_VALUE;
        }
        for (var slot : configSlotPages[page]) {
            if (slot != null) slot.xDisplayPosition = visible ? slot.getX() : Integer.MIN_VALUE;
        }
        for (var slot : storageSlotPages[page]) {
            if (slot != null) slot.xDisplayPosition = visible ? slot.getX() : Integer.MIN_VALUE;
        }
    }

    @Override
    public void setupConfig() {}

    @Override
    public void setupUpgrades() {}

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        super.loadSettingsFromHost(cm);
        if (cm.getSettings()
            .contains(Settings.SIDELESS_MODE)) {
            sidelessMode = (SidelessMode) cm.getSetting(Settings.SIDELESS_MODE);
        }
    }

    public void addUpgradeSlots() {
        for (int i = 0; i < upgradeSlots; i++) {
            this.addSlotToContainer(
                new SlotRestrictedInput(
                    SlotRestrictedInput.PlacableItemType.UPGRADES,
                    this.myDuality.getUpgrades(),
                    i,
                    187 + (i / 5) * SLOT_SIZE,
                    8 + (i % 5) * SLOT_SIZE,
                    this.getInventoryPlayer()));
        }
    }

    private boolean init;

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (init) return;
        init = true;
    }

    @Override
    public int availableUpgrades() {
        return upgradeSlots;
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        if (Platform.isClient()) {
            if (!isEmpty && !isConfigEmpty) {
                return idx == currentPage || idx < 0 || !init;
            } else return false;
        } else return true;
    }

    @Override
    public int getHeight() {
        return 211;
    }

    public int getMaxPages() {
        int pagesConfig = (int) Math.ceil((double) configSlots / CONFIG_PER_PAGE);
        int pagesStorage = (int) Math.ceil((double) storageSlots / CONFIG_PER_PAGE);
        int pagesPattern = (int) Math.ceil((double) patternSlots / PATTERNS_PER_PAGE);

        return Math.max(1, Math.max(pagesConfig, Math.max(pagesStorage, pagesPattern)));
    }
}
