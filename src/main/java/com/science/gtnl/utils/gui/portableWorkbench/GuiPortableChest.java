package com.science.gtnl.utils.gui.portableWorkbench;

import static com.science.gtnl.ScienceNotLeisure.*;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableChest extends GuiContainer {

    public GUI type;

    public GuiPortableChest(GUI type, InventoryPlayer player, ItemStack stack) {
        super(type.makeContainer(player, stack));
        this.type = type;
        this.xSize = type.xSize;
        this.ySize = type.ySize;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(type.guiResourceList.location);

        if (type == GUI.NETHERITE || type == GUI.DARKSTEEL) {
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

    public enum ResourceList {

        IRON(new ResourceLocation(RESOURCE_ROOT_ID, "textures/gui/chest/iron.png")),
        COPPER(new ResourceLocation(RESOURCE_ROOT_ID, "textures/gui/chest/copper.png")),
        STEEL(new ResourceLocation(RESOURCE_ROOT_ID, "textures/gui/chest/silver.png")),
        GOLD(new ResourceLocation(RESOURCE_ROOT_ID, "textures/gui/chest/gold.png")),
        DIAMOND(new ResourceLocation(RESOURCE_ROOT_ID, "textures/gui/chest/diamond.png")),
        NETHERITE(new ResourceLocation(RESOURCE_ROOT_ID, "textures/gui/chest/netherite.png")),
        SILVER(new ResourceLocation(RESOURCE_ROOT_ID, "textures/gui/chest/silver.png"));

        public final ResourceLocation location;

        ResourceList(ResourceLocation loc) {
            this.location = loc;
        }
    }

    public enum GUI {

        COPPER(184, 184, 9, 5, ResourceList.COPPER),
        IRON(184, 202, 9, 6, ResourceList.IRON),
        SILVER(184, 238, 9, 8, ResourceList.SILVER),
        STEEL(184, 238, 9, 8, ResourceList.STEEL),
        GOLD(184, 256, 9, 9, ResourceList.GOLD),
        DIAMOND(238, 256, 12, 9, ResourceList.DIAMOND),
        CRYSTAL(238, 256, 12, 9, ResourceList.DIAMOND),
        OBSIDIAN(238, 256, 12, 9, ResourceList.DIAMOND),
        NETHERITE(292, 256, 15, 9, ResourceList.NETHERITE),
        DARKSTEEL(292, 256, 15, 9, ResourceList.NETHERITE);

        public final int xSize;
        public final int ySize;
        public final int rows;
        public final int cols;
        public final ResourceList guiResourceList;

        GUI(int xSize, int ySize, int rows, int cols, ResourceList guiResourceList) {
            this.xSize = xSize;
            this.ySize = ySize;
            this.rows = rows;
            this.cols = cols;
            this.guiResourceList = guiResourceList;
        }

        public int getCapacity() {
            return rows * cols;
        }

        public Container makeContainer(InventoryPlayer player, ItemStack stack) {
            return new ContainerPortableChest(player, stack, xSize, ySize, rows, cols);
        }
    }

    public static class Copper extends GuiPortableChest {

        public Copper(InventoryPlayer player, ItemStack stack) {
            super(GUI.COPPER, player, stack);
        }
    }

    public static class Iron extends GuiPortableChest {

        public Iron(InventoryPlayer player, ItemStack stack) {
            super(GUI.IRON, player, stack);
        }
    }

    public static class Silver extends GuiPortableChest {

        public Silver(InventoryPlayer player, ItemStack stack) {
            super(GUI.SILVER, player, stack);
        }
    }

    public static class Steel extends GuiPortableChest {

        public Steel(InventoryPlayer player, ItemStack stack) {
            super(GUI.STEEL, player, stack);
        }
    }

    public static class Gold extends GuiPortableChest {

        public Gold(InventoryPlayer player, ItemStack stack) {
            super(GUI.GOLD, player, stack);
        }
    }

    public static class Diamond extends GuiPortableChest {

        public Diamond(InventoryPlayer player, ItemStack stack) {
            super(GUI.DIAMOND, player, stack);
        }
    }

    public static class Crystal extends GuiPortableChest {

        public Crystal(InventoryPlayer player, ItemStack stack) {
            super(GUI.CRYSTAL, player, stack);
        }
    }

    public static class Obsidian extends GuiPortableChest {

        public Obsidian(InventoryPlayer player, ItemStack stack) {
            super(GUI.OBSIDIAN, player, stack);
        }
    }

    public static class Netherite extends GuiPortableChest {

        public Netherite(InventoryPlayer player, ItemStack stack) {
            super(GUI.NETHERITE, player, stack);
        }
    }

    public static class DarkSteel extends GuiPortableChest {

        public DarkSteel(InventoryPlayer player, ItemStack stack) {
            super(GUI.DARKSTEEL, player, stack);
        }
    }
}
