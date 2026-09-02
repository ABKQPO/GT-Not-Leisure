package com.science.gtnl.client.gui.portableWorkbench;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.item.items.PortableChestType;
import com.science.gtnl.container.portableWorkbench.ContainerPortableChest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableChest extends GuiContainer {

    public PortableChestType type;

    public GuiPortableChest(PortableChestType type, InventoryPlayer player, ItemStack stack) {
        super(new ContainerPortableChest(player, stack, type));
        this.type = type;
        this.xSize = type.xSize;
        this.ySize = type.ySize;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(type.guiResource);

        if (type == PortableChestType.NETHERITE || type == PortableChestType.DARKSTEEL) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(guiLeft, guiTop, 0, 0.0, 0.0);
            tessellator.addVertexWithUV(guiLeft, guiTop + ySize, 0, 0.0, 1.0);
            tessellator.addVertexWithUV(guiLeft + xSize, guiTop + ySize, 0, 1.0, 1.0);
            tessellator.addVertexWithUV(guiLeft + xSize, guiTop, 0, 1.0, 0.0);
            tessellator.draw();
            return;
        }

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    public static class Copper extends GuiPortableChest {

        public Copper(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.COPPER, player, stack);
        }
    }

    public static class Iron extends GuiPortableChest {

        public Iron(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.IRON, player, stack);
        }
    }

    public static class Silver extends GuiPortableChest {

        public Silver(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.SILVER, player, stack);
        }
    }

    public static class Steel extends GuiPortableChest {

        public Steel(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.STEEL, player, stack);
        }
    }

    public static class Gold extends GuiPortableChest {

        public Gold(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.GOLD, player, stack);
        }
    }

    public static class Diamond extends GuiPortableChest {

        public Diamond(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.DIAMOND, player, stack);
        }
    }

    public static class Crystal extends GuiPortableChest {

        public Crystal(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.CRYSTAL, player, stack);
        }
    }

    public static class Obsidian extends GuiPortableChest {

        public Obsidian(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.OBSIDIAN, player, stack);
        }
    }

    public static class Netherite extends GuiPortableChest {

        public Netherite(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.NETHERITE, player, stack);
        }
    }

    public static class DarkSteel extends GuiPortableChest {

        public DarkSteel(InventoryPlayer player, ItemStack stack) {
            super(PortableChestType.DARKSTEEL, player, stack);
        }
    }
}
