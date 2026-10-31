package com.science.gtnl.client.gui.portableWorkbench;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.api.mixinHelper.IInfinityChestGui;
import com.science.gtnl.common.gui.container.portableWorkbench.ContainerPortableInfinityChest;
import com.science.gtnl.common.gui.container.portableWorkbench.IPortableStorageHost;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortablePortableInfinityChest extends GuiPortableAvaritiaddonsChest implements IInfinityChestGui {

    public GuiPortablePortableInfinityChest(@NotNull final ItemStack stack, final InventoryPlayer inventoryPlayer) {
        super(new ContainerPortableInfinityChest(stack, inventoryPlayer));
    }

    public GuiPortablePortableInfinityChest(@NotNull final ItemStack stack, final InventoryPlayer inventoryPlayer,
        IPortableStorageHost host) {
        super(new ContainerPortableInfinityChest(stack, inventoryPlayer, host));
    }

    @Override
    @NotNull
    public String getContainerName() {
        return "container.InfinityChest";
    }

}
