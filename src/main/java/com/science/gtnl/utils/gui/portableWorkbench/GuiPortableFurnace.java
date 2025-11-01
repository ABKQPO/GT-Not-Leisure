package com.science.gtnl.utils.gui.portableWorkbench;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableFurnace extends GuiContainer {

    public static final ResourceLocation furnaceGuiTextures = new ResourceLocation(
        "textures/gui/container/furnace.png");
    public EntityPlayer player;

    public GuiPortableFurnace(InventoryPlayer playerInventory) {
        super(new ContainerPortableFurnace(playerInventory, null, playerInventory.player.getHeldItem()));
        this.player = playerInventory.player;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("container.furnace");
        this.fontRendererObj
            .drawString(title, this.xSize / 2 - this.fontRendererObj.getStringWidth(title) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        this.mc.getTextureManager()
            .bindTexture(furnaceGuiTextures);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        ItemStack stack = player.getHeldItem();
        if (stack == null || stack.getItemDamage() != 2) return;
        if (!stack.hasTagCompound()) return;

        int cookTime = stack.getTagCompound()
            .getInteger("CookTime");
        int burnTime = stack.getTagCompound()
            .getInteger("BurnTime");
        int currentItemBurnTime = stack.getTagCompound()
            .getInteger("CurrentItemBurnTime");
        if (currentItemBurnTime == 0) currentItemBurnTime = 200;

        int cookWidth = cookTime * 24 / 200;
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, cookWidth + 1, 16);

        int burnHeight = burnTime * 13 / currentItemBurnTime;
        if (burnHeight > 0) {
            this.drawTexturedModalRect(x + 56, y + 36 + 12 - burnHeight, 176, 12 - burnHeight, 14, burnHeight + 1);
        }
    }
}
