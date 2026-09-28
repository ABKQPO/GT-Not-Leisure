package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.item.ItemStack;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.item.BaubleItem;
import com.science.gtnl.utils.enums.GTNLItemList;

import baubles.api.BaubleType;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemElainaBrooch extends BaubleItem implements IBaubleExpanded {

    private static final String TITLE_TYPE = "title";
    private static final String[] BAUBLE_TYPES = { TITLE_TYPE };

    public ItemElainaBrooch() {
        BaubleExpandedSlots.tryRegisterType(TITLE_TYPE);
        setUnlocalizedName("gtnl.elaina_brooch");
        setTextureName(RESOURCE_ROOT_ID + ":elaina_brooch");
        setMaxStackSize(1);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "elaina_brooch");
        GTNLItemList.ElainaBrooch.set(new ItemStack(this));
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return null;
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return BAUBLE_TYPES;
    }
}
