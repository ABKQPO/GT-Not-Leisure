package com.science.gtnl.utils.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityDirePatternEncoder;
import com.science.gtnl.common.packet.EncodeDirePattern;

import appeng.api.config.ActionItems;
import appeng.api.config.Settings;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiImgButton;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDirePatternEncoder extends AEBaseGui {

    private static final ResourceLocation tex = new ResourceLocation("avaritia:textures/gui/dire_crafting_gui.png");

    private GuiImgButton encodeBtn;

    public GuiDirePatternEncoder(InventoryPlayer ip, TileEntityDirePatternEncoder anchor) {
        super(new ContainerDirePatternEncoder(ip, anchor));
        this.ySize = 256;
        this.xSize = 238;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.encodeBtn = new GuiImgButton(this.guiLeft + 210, this.guiTop + 130, Settings.ACTIONS, ActionItems.ENCODE);
        this.buttonList.add(this.encodeBtn);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {

    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(tex);
        int foo = (this.width - this.xSize) / 2;
        int bar = (this.height - this.ySize) / 2;
        // noinspection SuspiciousNameCombination
        this.drawTexturedModalRect(foo, bar, 0, 0, this.ySize, this.ySize);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button == encodeBtn) ScienceNotLeisure.network.sendToServer(new EncodeDirePattern());
    }
}
