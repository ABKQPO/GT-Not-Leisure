package com.science.gtnl.client.gui.portableWorkbench;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import com.science.gtnl.client.celestweave.CelestweaveBackpackClient;
import com.science.gtnl.container.portableWorkbench.ContainerPortableEnderChest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableEnderChest extends GuiChest {

    private final boolean backpack;

    public GuiPortableEnderChest(InventoryPlayer playerInv, IInventory enderInv) {
        this(playerInv, enderInv, false);
    }

    public GuiPortableEnderChest(InventoryPlayer playerInv, IInventory enderInv, boolean backpack) {
        super(playerInv, enderInv);
        this.inventorySlots = new ContainerPortableEnderChest(playerInv, enderInv);
        this.backpack = backpack;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (backpack) CelestweaveBackpackClient.addTabs(guiLeft, guiTop, buttonList);
    }
}
