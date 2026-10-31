package com.science.gtnl.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Mouse;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.gui.container.ContainerUniversalStorageBus;
import com.science.gtnl.common.packet.SwitchToCustomGuiPacket;
import com.science.gtnl.common.packet.UniversalStorageBusActionPacket;
import com.science.gtnl.common.part.PartUniversalStorageBus;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.AccessRestriction;
import appeng.api.config.ActionItems;
import appeng.api.config.ExtractionMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.Upgrades;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiUpgradeable;
import appeng.client.gui.slots.VirtualMEPhantomSlot;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketConfigButton;
import appeng.tile.inventory.IAEStackInventory;

public class GuiUniversalStorageBus extends GuiUpgradeable implements IBookmarkGroupDropTarget {

    private final ContainerUniversalStorageBus container;
    private final PartUniversalStorageBus storageBus;
    private final VirtualMEPhantomSlot[] configSlots = new VirtualMEPhantomSlot[PartUniversalStorageBus.CONFIG_SIZE];

    private GuiImgButton rwMode;
    private GuiImgButton storageFilter;
    private GuiImgButton partition;
    private GuiImgButton clear;
    private GuiImgButton extractionMode;
    private GuiImgButton reshuffleAccess;
    private GuiTabButton priority;

    public GuiUniversalStorageBus(InventoryPlayer inventoryPlayer, PartUniversalStorageBus te) {
        super(new ContainerUniversalStorageBus(inventoryPlayer, te));
        this.container = (ContainerUniversalStorageBus) this.inventorySlots;
        this.storageBus = te;
        this.ySize = 251;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.initVirtualSlots();
    }

    @Override
    public void addButtons() {
        this.clear = new GuiImgButton(this.guiLeft - 18, this.guiTop + 8, Settings.ACTIONS, ActionItems.CLOSE);
        this.partition = new GuiImgButton(this.guiLeft - 18, this.guiTop + 28, Settings.ACTIONS, ActionItems.WRENCH);
        this.rwMode = new GuiImgButton(
            this.guiLeft - 18,
            this.guiTop + 48,
            Settings.ACCESS,
            AccessRestriction.READ_WRITE);
        this.extractionMode = new GuiImgButton(
            this.guiLeft - 18,
            this.guiTop + 68,
            Settings.EXTRACTION_MODE,
            ExtractionMode.LOOSE);
        this.storageFilter = new GuiImgButton(
            this.guiLeft - 18,
            this.guiTop + 88,
            Settings.STORAGE_FILTER,
            StorageFilter.EXTRACTABLE_ONLY);
        this.fuzzyMode = new GuiImgButton(
            this.guiLeft - 18,
            this.guiTop + 108,
            Settings.FUZZY_MODE,
            FuzzyMode.IGNORE_ALL);
        this.oreFilter = new GuiImgButton(
            this.guiLeft - 18,
            this.guiTop + 108,
            Settings.ACTIONS,
            ActionItems.ORE_FILTER);
        this.reshuffleAccess = new GuiImgButton(
            this.guiLeft - 18,
            this.guiTop + 108,
            Settings.RESHUFFLE_ACCESS,
            AccessRestriction.READ_WRITE);
        this.priority = new GuiTabButton(
            this.guiLeft + 154,
            this.guiTop,
            2 + 4 * 16,
            GuiText.Priority.getLocal(),
            itemRender);

        this.buttonList.add(this.priority);
        this.buttonList.add(this.storageFilter);
        this.buttonList.add(this.fuzzyMode);
        this.buttonList.add(this.rwMode);
        this.buttonList.add(this.extractionMode);
        this.buttonList.add(this.partition);
        this.buttonList.add(this.clear);
        this.buttonList.add(this.oreFilter);
        this.buttonList.add(this.reshuffleAccess);
    }

    @Override
    public void handleButtonVisibility() {
        super.handleButtonVisibility();
        this.reshuffleAccess.yPosition = this.guiTop + (this.fuzzyMode.visible || this.oreFilter.visible ? 128 : 108);
    }

    private void initVirtualSlots() {
        final IAEStackInventory config = this.container.getConfig();
        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 9; x++) {
                final int index = x + y * 9;
                // Every registered stack type is accepted: items, fluids, essentia, ...
                final VirtualMEPhantomSlot slot = new VirtualMEPhantomSlot(
                    8 + x * 18,
                    29 + y * 18,
                    config,
                    index,
                    (s, type, mouseButton) -> true);
                this.configSlots[index] = slot;
                this.registerVirtualSlots(slot);
            }
        }
    }

    private void updateSlotVisibility() {
        final int activeSlots = this.storageBus.getActiveConfigSlots();
        for (VirtualMEPhantomSlot slot : this.configSlots) {
            slot.setHidden(slot.getSlotIndex() >= activeSlots);
        }
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.fontRendererObj.drawString(this.getGuiDisplayName(this.storageBus.getPartName()), 8, 6, 4210752);
        this.fontRendererObj.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);

        this.fuzzyMode.set(this.container.getFuzzyMode());
        this.storageFilter.set(this.container.storageFilter);
        this.rwMode.set(this.container.rwMode);
        this.extractionMode.set(this.container.extractionMode);
        this.extractionMode.setEnabled(this.container.rwMode == AccessRestriction.READ_WRITE);
        this.partition.set(this.container.partitionMode);
        this.reshuffleAccess.set(this.container.reshuffleAccess);

        this.updateSlotVisibility();
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);

        final int capacity = this.bc.getInstalledUpgrades(Upgrades.CAPACITY);
        for (int i = 0; i < 7; i++) {
            // rows above the unlocked capacity are drawn faded out
            final int textureY = i >= capacity + 2 ? 46 : 28;
            this.drawTexturedModalRect(offsetX + 7, offsetY + 28 + 18 * i, 7, textureY, 162, 18);
        }
    }

    @Override
    public String getBackground() {
        return "guis/storagebus.png";
    }

    @Override
    public void actionPerformed(final GuiButton btn) {
        if (btn == this.oreFilter) {
            // AE2 could not return from its ore filter GUI to this one, so open it through the GTNL GUI handler
            AEBaseGui.setSwitchingGuis(true);
            ScienceNotLeisure.network
                .sendToServer(new SwitchToCustomGuiPacket(GuiType.UniversalOreFilterGUI, this.storageBus.getSide()));
            return;
        }
        super.actionPerformed(btn);

        final boolean backwards = Mouse.isButtonDown(1);

        if (btn == this.partition) {
            ScienceNotLeisure.network.sendToServer(
                new UniversalStorageBusActionPacket(
                    backwards ? UniversalStorageBusActionPacket.Action.PARTITION_CLEAR
                        : UniversalStorageBusActionPacket.Action.PARTITION));
        } else if (btn == this.clear) {
            ScienceNotLeisure.network
                .sendToServer(new UniversalStorageBusActionPacket(UniversalStorageBusActionPacket.Action.CLEAR));
        } else if (btn == this.priority) {
            final ForgeDirection side = this.storageBus.getSide();
            AEBaseGui.setSwitchingGuis(true);
            ScienceNotLeisure.network.sendToServer(new SwitchToCustomGuiPacket(GuiType.CustomPriorityGUI, side));
        } else if (btn == this.rwMode || btn == this.extractionMode
            || btn == this.storageFilter
            || btn == this.reshuffleAccess) {
                NetworkHandler.instance
                    .sendToServer(new PacketConfigButton(((GuiImgButton) btn).getSetting(), backwards));
            }
    }

    @Override
    public int handleBookmarkGroupDrop(int mouseX, int mouseY, Iterable<ItemStack> stacks) {
        final int x = mouseX - this.guiLeft + 1;
        final int y = mouseY - this.guiTop + 1;
        this.updateSlotVisibility();

        int slotIndex = -1;
        for (VirtualMEPhantomSlot slot : this.configSlots) {
            if (!slot.isHidden() && slot.isHovered(x, y)) {
                slotIndex = slot.getSlotIndex();
                break;
            }
        }
        if (slotIndex < 0) return -1;

        int placed = 0;
        for (ItemStack stack : stacks) {
            if (slotIndex >= this.configSlots.length || this.configSlots[slotIndex].isHidden()) break;
            this.configSlots[slotIndex++].handleMouseClicked(stack, false, 0);
            placed++;
        }
        return placed;
    }
}
