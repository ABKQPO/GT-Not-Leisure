package com.science.gtnl.common.item.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.common.gui.GTNLGuiHandler;
import com.science.gtnl.utils.enums.GuiType;

public class PortableCellWorkbenchItem extends Item {

    public PortableCellWorkbenchItem() {
        setMaxStackSize(1);
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.portable_cell_workbench";
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            GTNLGuiHandler
                .openGui(player, GuiType.PortableCellWorkbenchGUI, null, world, player.inventory.currentItem, 0, 0);
        }
        return stack;
    }
}
