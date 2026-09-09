package com.science.gtnl.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.glodblock.github.inventory.IAEFluidTank;
import com.glodblock.github.inventory.IDualHost;
import com.glodblock.github.util.RenderUtil;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.SwitchSuperDualInterfaceGuiPacket;
import com.science.gtnl.common.part.PartSuperDualInterface;
import com.science.gtnl.container.ContainerSuperDualInterfaceFluid;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.storage.data.IAEFluidStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.localization.GuiText;

public class GuiSuperDualInterfaceFluid extends AEBaseGui {

    private static final ResourceLocation TEX_BG = new ResourceLocation("ae2fc", "textures/gui/interface_fluid.png");
    private static final int TANK_X = 35;
    private static final int TANK_X_OFF = 18;
    private static final int TANK_Y = 53;
    private static final int TANK_WIDTH = 16;
    private static final int TANK_HEIGHT = 68;

    private final ContainerSuperDualInterfaceFluid cont;
    private GuiTabButton switcher;
    private GuiSuperInterface.GuiTextAeButton prevPage;
    private GuiSuperInterface.GuiTextAeButton nextPage;

    public GuiSuperDualInterfaceFluid(InventoryPlayer inventoryPlayer, IDualHost host) {
        super(new ContainerSuperDualInterfaceFluid(inventoryPlayer, host));
        this.cont = (ContainerSuperDualInterfaceFluid) this.inventorySlots;
        this.ySize = 231;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.switcher = new GuiTabButton(
            this.guiLeft + 154,
            this.guiTop,
            this.cont.getTile() instanceof PartSuperDualInterface ? GTNLItemList.PartSuperDualInterface.get(1)
                : GTNLItemList.SuperDualInterface.get(1),
            StatCollector.translateToLocal("gtnl.gui.super_dual_interface.tooltip.switch"),
            itemRender);
        this.buttonList.add(this.switcher);
        this.prevPage = new GuiSuperInterface.GuiTextAeButton(
            101,
            this.guiLeft - 36,
            this.guiTop + 152,
            16,
            16,
            "<",
            StatCollector.translateToLocal("gtnl.gui.super_interface.tooltip.previous_page"));
        this.nextPage = new GuiSuperInterface.GuiTextAeButton(
            102,
            this.guiLeft - 18,
            this.guiTop + 152,
            16,
            16,
            ">",
            StatCollector.translateToLocal("gtnl.gui.super_interface.tooltip.next_page"));
        this.buttonList.add(this.prevPage);
        this.buttonList.add(this.nextPage);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        fontRendererObj
            .drawString(getGuiDisplayName(I18n.format("gtnl.gui.super_dual_interface.name")), 8, 6, 0x404040);
        fontRendererObj.drawString(GuiText.inventory.getLocal(), 8, ySize - 94, 0x404040);
        fontRendererObj.drawString(
            StatCollector
                .translateToLocalFormatted("gtnl.gui.super_interface.page", cont.currentPage + 1, cont.maxPage),
            110,
            6,
            0x404040);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        IAEFluidTank fluidInv = cont.getTile()
            .getInternalFluid();
        mc.getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        int start = cont.currentPage * 6;
        for (int i = 0; i < 6; i++) {
            int slot = start + i;
            if (slot >= fluidInv.getSlots()) {
                continue;
            }
            if (!(cont.getTile() instanceof PartSuperDualInterface)) {
                fontRendererObj.drawString(String.valueOf(i + 1), TANK_X + i * TANK_X_OFF + 5, 22, 0x404040);
            }
            RenderUtil.renderFluidIntoGui(
                this,
                TANK_X + TANK_X_OFF * i,
                TANK_Y,
                TANK_WIDTH,
                TANK_HEIGHT,
                fluidInv.getFluidInSlot(slot),
                fluidInv.getTankInfo(ForgeDirection.UNKNOWN)[slot].capacity);
        }
        this.prevPage.enabled = cont.currentPage > 0;
        this.nextPage.enabled = cont.currentPage < cont.maxPage - 1;
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        mc.getTextureManager()
            .bindTexture(TEX_BG);
        drawTexturedModalRect(offsetX, offsetY, 0, 0, 176, ySize);
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn == this.switcher) {
            AEBaseGui.setSwitchingGuis(true);
            ScienceNotLeisure.network
                .sendToServer(new SwitchSuperDualInterfaceGuiPacket(GuiType.SuperDualInterfaceGUI));
        } else if (btn == this.prevPage) {
            cont.previousPage();
        } else if (btn == this.nextPage) {
            cont.nextPage();
        }
    }

    public void update(int id, IAEFluidStack stack) {
        if (id >= 100) {
            cont.getTile()
                .setConfig(id - 100, stack);
        } else {
            cont.getTile()
                .setFluidInv(id, stack);
        }
    }
}
