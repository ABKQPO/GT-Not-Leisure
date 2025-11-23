package com.science.gtnl.common.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.utils.DireCraftingPatternDetails;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.items.misc.ItemEncodedPattern;

public class ItemDireCraftPattern extends ItemEncodedPattern {

    public ItemDireCraftPattern() {
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "DireCraftPattern");
        this.setUnlocalizedName("DireCraftPattern");
    }

    @Override
    public ICraftingPatternDetails getPatternForItem(final ItemStack is, final World w) {
        try {
            return new DireCraftingPatternDetails(is);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    protected void getCheckedSubItems(Item sameItem, CreativeTabs creativeTab, List<ItemStack> itemStacks) {
        itemStacks.clear();
    }
}
