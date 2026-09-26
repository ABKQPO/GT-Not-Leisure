package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemElainaBrooch extends Item {

    public ItemElainaBrooch() {
        setUnlocalizedName("gtnl.elaina_brooch");
        setTextureName(RESOURCE_ROOT_ID + ":elaina_brooch");
        setMaxStackSize(1);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "elaina_brooch");
        GTNLItemList.ElainaBrooch.set(new ItemStack(this));
    }
}
