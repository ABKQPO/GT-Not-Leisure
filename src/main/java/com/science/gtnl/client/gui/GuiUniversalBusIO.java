package com.science.gtnl.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.input.Mouse;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.SwitchToCustomGuiPacket;
import com.science.gtnl.common.part.PartUniversalExportBus;
import com.science.gtnl.container.ContainerUniversalBusIO;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiUpgradeable;
import appeng.client.gui.slots.VirtualMEPhantomSlot;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketConfigButton;
import appeng.parts.automation.PartSharedItemBus;
import appeng.tile.inventory.IAEStackInventory;

public class GuiUniversalBusIO extends GuiUpgradeable {

    public static final int[] SLOT_SEQUENCE = new int[] { 5, 3, 6, 1, 0, 2, 7, 4, 8 };

    public final PartSharedItemBus<?> bus;
    public final VirtualMEPhantomSlot[] virtualSlots = new VirtualMEPhantomSlot[9];
    public GuiImgButton schedulingMode;

    public GuiUniversalBusIO(InventoryPlayer inventoryPlayer, PartSharedItemBus<?> te) {
        super(new ContainerUniversalBusIO(inventoryPlayer, te));
        this.bus = te;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.initVirtualSlots();
    }

    @Override
    public void addButtons() {
        super.addButtons();
        if (this.bus instanceof PartUniversalExportBus) {
            this.schedulingMode = new GuiImgButton(
                this.guiLeft - 18,
                this.guiTop + 68,
                Settings.SCHEDULING_MODE,
                SchedulingMode.DEFAULT);
            this.buttonList.add(this.schedulingMode);
        }
    }

    public void initVirtualSlots() {
        final IAEStackInventory config = ((ContainerUniversalBusIO) this.inventorySlots).getConfig();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                final int index = SLOT_SEQUENCE[x + y * 3];
                // Every registered stack type is accepted: items, fluids, essentia, ...
                final VirtualMEPhantomSlot slot = new VirtualMEPhantomSlot(
                    62 + 18 * x,
                    22 + 18 * y,
                    config,
                    index,
                    (s, type, mouseButton) -> true);
                this.virtualSlots[index] = slot;
                this.registerVirtualSlots(slot);
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn == this.oreFilter) {
            // AE2 could not return from its ore filter GUI to this one, so open it through the GTNL GUI handler
            AEBaseGui.setSwitchingGuis(true);
            ScienceNotLeisure.network
                .sendToServer(new SwitchToCustomGuiPacket(GuiType.UniversalOreFilterGUI, this.bus.getSide()));
            return;
        }
        super.actionPerformed(btn);
        if (btn == this.schedulingMode) {
            NetworkHandler.instance
                .sendToServer(new PacketConfigButton(this.schedulingMode.getSetting(), Mouse.isButtonDown(1)));
        }
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        if (this.schedulingMode != null) {
            this.schedulingMode.set(this.cvb.getSchedulingMode());
        }
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);

        final int capacity = this.bc.getInstalledUpgrades(Upgrades.CAPACITY);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                final int index = SLOT_SEQUENCE[x + y * 3];
                final boolean enabled = isSlotEnabled(index, capacity);
                this.drawTexturedModalRect(offsetX + 61 + 18 * x, offsetY + 21 + 18 * y, enabled ? 61 : 79, 21, 18, 18);
            }
        }
    }

    @Override
    public void handleButtonVisibility() {
        super.handleButtonVisibility();
        final int capacity = this.bc.getInstalledUpgrades(Upgrades.CAPACITY);
        for (int i = 0; i < this.virtualSlots.length; i++) {
            if (this.virtualSlots[i] != null) this.virtualSlots[i].setHidden(!isSlotEnabled(i, capacity));
        }
        if (this.schedulingMode != null) {
            this.schedulingMode.setVisibility(capacity > 0);
        }
    }

    public static boolean isSlotEnabled(int index, int capacity) {
        if (index == 0) return true;
        if (index <= 4) return capacity > 0;
        return capacity > 1;
    }

    @Override
    public String getBackground() {
        return "guis/bus.png";
    }

    @Override
    public String getName() {
        return this.bus.getBusName();
    }
}
