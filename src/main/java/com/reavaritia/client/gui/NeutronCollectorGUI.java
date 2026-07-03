package com.reavaritia.client.gui;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.reavaritia.common.blocks.tile.TileEntityNeutronCollector;
import com.reavaritia.container.ContainerNeutronItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NeutronCollectorGUI extends GuiContainer {

    public static ResourceLocation GuiTextures = new ResourceLocation(
        "reavaritia",
        "textures/gui/neutron_collector.png");
    public TileEntityNeutronCollector machine;

    public NeutronCollectorGUI(InventoryPlayer player, TileEntityNeutronCollector machine) {
        super(new ContainerNeutronItem(player, machine));
        this.machine = machine;
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal("container." + machine.machineType);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = getLocalizedName();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj
            .drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        int progressBarX = 100;
        int progressBarY = 32;
        int progressBarWidth = 4;
        int progressBarHeight = 18;
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        if (mouseX >= k + progressBarX && mouseX <= k + progressBarX + progressBarWidth
            && mouseY >= l + progressBarY
            && mouseY <= l + progressBarY + progressBarHeight) {
            float percentage = machine.getProgressPercentage();
            String progressText = String
                .format(StatCollector.translateToLocal("GUI_NeutronCollector_Progress") + "%.2f%%", percentage);
            drawHoveringText(List.of(progressText), mouseX - k, mouseY - l, fontRendererObj);
        }
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(GuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        int progress = this.machine.getProgressScaled(18);
        if (progress > 16) {
            progress = 16;
        }

        this.drawTexturedModalRect(k + 100, l + 48 - progress, 177, 1, 4, progress);
    }
}
