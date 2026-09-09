package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.CommonProxy;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GuiType;

import cpw.mods.fml.common.registry.GameRegistry;

public class PortableCellWorkbenchItem extends Item {

    public PortableCellWorkbenchItem() {
        setUnlocalizedName("gtnl.portable_cell_workbench");
        setTextureName(RESOURCE_ROOT_ID + ":portable_cell_workbench");
        setMaxStackSize(1);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "portable_cell_workbench");
        GTNLItemList.PortableCellWorkbench.set(new ItemStack(this));
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.portable_cell_workbench";
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            CommonProxy
                .openGui(player, GuiType.PortableCellWorkbenchGUI, null, world, player.inventory.currentItem, 0, 0);
        }
        return stack;
    }
}
