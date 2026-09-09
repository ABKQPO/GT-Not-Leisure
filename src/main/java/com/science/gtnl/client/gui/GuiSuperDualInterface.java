package com.science.gtnl.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntitySuperDualInterface;
import com.science.gtnl.common.packet.SwitchSuperDualInterfaceGuiPacket;
import com.science.gtnl.common.packet.SwitchToCustomGuiPacket;
import com.science.gtnl.common.part.PartSuperDualInterface;
import com.science.gtnl.container.ContainerSuperInterface;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.AdvancedBlockingMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.InsertionMode;
import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.SidelessMode;
import appeng.api.config.Upgrades;
import appeng.api.config.YesNo;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiUpgradeable;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiSimpleImgButton;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.client.gui.widgets.GuiToggleButton;
import appeng.core.AELog;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketConfigButton;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.helpers.IInterfaceHost;
import appeng.parts.AEBasePart;

public class GuiSuperDualInterface extends GuiUpgradeable {

    private final IInterfaceHost host;
    private final ContainerSuperInterface container;
    private GuiTabButton priority;
    private GuiTabButton switcher;
    private GuiImgButton blockMode;
    private GuiImgButton smartBlockMode;
    private GuiToggleButton interfaceMode;
    private GuiImgButton insertionMode;
    private GuiSimpleImgButton doublePatterns;
    private GuiToggleButton patternOptimization;
    private GuiImgButton advancedBlockingMode;
    private GuiImgButton sidelessMode;
    private GuiImgButton lockCraftingMode;
    private GuiImgButton fuzzyMode;
    private GuiSuperInterface.GuiTextAeButton prevPage;
    private GuiSuperInterface.GuiTextAeButton nextPage;

    public GuiSuperDualInterface(InventoryPlayer inventoryPlayer, IInterfaceHost host) {
        super(new ContainerSuperInterface(inventoryPlayer, host));
        this.host = host;
        this.container = (ContainerSuperInterface) this.inventorySlots;
        this.ySize = container.getHeight();
    }

    @Override
    public void addButtons() {
        int btnX = this.guiLeft - 18;
        int offset = 8;

        this.priority = new GuiTabButton(
            this.guiLeft + 154,
            this.guiTop,
            2 + 4 * 16,
            GuiText.Priority.getLocal(),
            itemRender);
        this.buttonList.add(this.priority);

        this.switcher = new GuiTabButton(
            this.guiLeft + 132,
            this.guiTop,
            this.host instanceof PartSuperDualInterface ? GTNLItemList.PartSuperDualInterface.get(1)
                : GTNLItemList.SuperDualInterface.get(1),
            StatCollector.translateToLocal("gtnl.gui.super_interface.tooltip.switch"),
            itemRender);
        this.buttonList.add(this.switcher);

        this.blockMode = new GuiImgButton(btnX, this.guiTop + offset, Settings.BLOCK, YesNo.NO);
        this.buttonList.add(this.blockMode);

        this.smartBlockMode = new GuiImgButton(btnX - 18, this.guiTop + offset, Settings.SMART_BLOCK, YesNo.NO);
        this.buttonList.add(this.smartBlockMode);

        offset += 18;
        this.interfaceMode = new GuiToggleButton(
            btnX,
            this.guiTop + offset,
            84,
            85,
            GuiText.InterfaceTerminal.getLocal(),
            GuiText.InterfaceTerminalHint.getLocal());
        this.buttonList.add(this.interfaceMode);

        offset += 18;
        this.insertionMode = new GuiImgButton(
            btnX,
            this.guiTop + offset,
            Settings.INSERTION_MODE,
            InsertionMode.DEFAULT);
        this.buttonList.add(this.insertionMode);

        offset += 18;
        this.doublePatterns = new GuiSimpleImgButton(btnX, this.guiTop + offset, 71, "");
        this.buttonList.add(this.doublePatterns);

        offset += 18;
        this.patternOptimization = new GuiToggleButton(
            btnX,
            this.guiTop + offset,
            178,
            194,
            GuiText.PatternOptimization.getLocal(),
            GuiText.PatternOptimizationHint.getLocal());
        this.buttonList.add(this.patternOptimization);

        offset += 18;
        this.advancedBlockingMode = new GuiImgButton(
            btnX,
            this.guiTop + offset,
            Settings.ADVANCED_BLOCKING_MODE,
            AdvancedBlockingMode.DEFAULT);
        this.buttonList.add(this.advancedBlockingMode);

        offset += 18;
        if (host instanceof TileEntitySuperDualInterface) {
            this.sidelessMode = new GuiImgButton(
                btnX,
                this.guiTop + offset,
                Settings.SIDELESS_MODE,
                SidelessMode.SIDELESS);
            this.buttonList.add(this.sidelessMode);
            offset += 18;
        }

        this.lockCraftingMode = new GuiImgButton(
            btnX,
            this.guiTop + offset,
            Settings.LOCK_CRAFTING_MODE,
            LockCraftingMode.NONE);
        this.buttonList.add(this.lockCraftingMode);

        offset += 18;
        this.fuzzyMode = new GuiImgButton(btnX, this.guiTop + offset, Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.buttonList.add(this.fuzzyMode);

        offset += 18 * 2;
        this.prevPage = new GuiSuperInterface.GuiTextAeButton(
            101,
            btnX - 18,
            this.guiTop + offset,
            16,
            16,
            "<",
            StatCollector.translateToLocal("gtnl.gui.super_interface.tooltip.previous_page"));
        this.nextPage = new GuiSuperInterface.GuiTextAeButton(
            102,
            btnX,
            this.guiTop + offset,
            16,
            16,
            ">",
            StatCollector.translateToLocal("gtnl.gui.super_interface.tooltip.next_page"));
        this.buttonList.add(prevPage);
        this.buttonList.add(nextPage);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        if (this.blockMode != null) this.blockMode.set(container.bMode);
        if (this.smartBlockMode != null) this.smartBlockMode.set(container.sbMode);
        if (this.interfaceMode != null) this.interfaceMode.setState(container.iTermMode == YesNo.YES);
        if (this.insertionMode != null) this.insertionMode.set(container.insertionMode);
        if (this.patternOptimization != null)
            this.patternOptimization.setState(container.patternOptimization == YesNo.YES);
        if (this.advancedBlockingMode != null) this.advancedBlockingMode.set(container.advancedBlockingMode);
        if (this.lockCraftingMode != null) this.lockCraftingMode.set(container.lockCraftingMode);
        if (this.fuzzyMode != null) this.fuzzyMode.set(container.fuzzyMode);
        if (this.sidelessMode != null && host instanceof TileEntitySuperDualInterface tile) {
            this.sidelessMode.set(container.sidelessMode);
        }
        if (this.doublePatterns != null) {
            this.doublePatterns.enabled = container.isAllowedToMultiplyPatterns;
            this.doublePatterns.setTooltip(
                ButtonToolTips.DoublePatterns.getLocal() + "\n"
                    + (this.doublePatterns.enabled ? ButtonToolTips.DoublePatternsHint.getLocal()
                        : ButtonToolTips.OptimizePatternsNoReq.getLocal()));
        }

        this.fontRendererObj.drawString(
            getGuiDisplayName(StatCollector.translateToLocal("gtnl.gui.super_dual_interface.name")),
            8,
            6,
            4210752);
        String pageLabel = StatCollector.translateToLocalFormatted(
            "gtnl.gui.super_interface.page",
            container.currentPage + 1,
            container.getMaxPages());
        this.fontRendererObj.drawString(pageLabel, 100, 6, 4210752);

        this.prevPage.enabled = container.currentPage > 0;
        this.nextPage.enabled = container.currentPage < container.getMaxPages() - 1;
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.drawTexturedModalRect(offsetX + 7, offsetY + 14, 7, 71, 162, 18);
        this.drawTexturedModalRect(offsetX + 7, offsetY + 32, 7, 107, 162, 18);
        int patternStartY = 14 + 36 + 3;
        for (int i = 4; i > 0; i--) {
            this.drawTexturedModalRect(offsetX + 7, offsetY + patternStartY + (4 - i) * 18, 7, 107, 162, 18);
        }
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        boolean backwards = Mouse.isButtonDown(1);
        if (btn == prevPage) {
            container.previousPage();
        } else if (btn == nextPage) {
            container.nextPage();
        } else if (btn == this.priority) {
            ForgeDirection side = ForgeDirection.UNKNOWN;
            if (this.bc instanceof AEBasePart part) {
                side = part.getSide();
            }
            AEBaseGui.setSwitchingGuis(true);
            ScienceNotLeisure.network.sendToServer(new SwitchToCustomGuiPacket(GuiType.CustomPriorityGUI, side));
        } else if (btn == this.switcher) {
            AEBaseGui.setSwitchingGuis(true);
            ScienceNotLeisure.network
                .sendToServer(new SwitchSuperDualInterfaceGuiPacket(GuiType.SuperDualInterfaceFluidGUI));
        } else if (btn == this.interfaceMode) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(Settings.INTERFACE_TERMINAL, backwards));
        } else if (btn == this.blockMode) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(this.blockMode.getSetting(), backwards));
        } else if (btn == this.smartBlockMode) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(this.smartBlockMode.getSetting(), backwards));
        } else if (btn == this.insertionMode) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(this.insertionMode.getSetting(), backwards));
        } else if (btn == this.doublePatterns) {
            try {
                int val = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 1 : 0;
                if (backwards) val |= 0b10;
                NetworkHandler.instance
                    .sendToServer(new PacketValueConfig("Interface.DoublePatterns", String.valueOf(val)));
            } catch (Throwable e) {
                AELog.debug(e);
            }
        } else if (btn == this.patternOptimization) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(Settings.PATTERN_OPTIMIZATION, backwards));
        } else if (btn == this.advancedBlockingMode) {
            NetworkHandler.instance
                .sendToServer(new PacketConfigButton(this.advancedBlockingMode.getSetting(), backwards));
        } else if (btn == this.sidelessMode) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(this.sidelessMode.getSetting(), backwards));
        } else if (btn == this.lockCraftingMode) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(this.lockCraftingMode.getSetting(), backwards));
        } else if (btn == this.fuzzyMode) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(this.fuzzyMode.getSetting(), backwards));
        }
    }

    @Override
    public String getBackground() {
        return "guis/super_interface.png";
    }

    @Override
    public void handleButtonVisibility() {
        super.handleButtonVisibility();
        if (this.advancedBlockingMode != null) {
            this.advancedBlockingMode.setVisibility(this.bc.getInstalledUpgrades(Upgrades.ADVANCED_BLOCKING) > 0);
        }
        if (this.lockCraftingMode != null) {
            this.lockCraftingMode.setVisibility(this.bc.getInstalledUpgrades(Upgrades.LOCK_CRAFTING) > 0);
        }
        if (this.fuzzyMode != null) {
            this.fuzzyMode.setVisibility(this.bc.getInstalledUpgrades(Upgrades.FUZZY) > 0);
        }
    }
}
