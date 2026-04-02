package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.DireCraftingPatternDetails;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.items.misc.ItemEncodedPattern;
import cpw.mods.fml.common.registry.GameRegistry;

public class DireCraftPattern extends ItemEncodedPattern {

    public DireCraftPattern() {
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "DireCraftPattern");
        this.setUnlocalizedName("DireCraftPattern");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, getUnlocalizedName());
        GTNLItemList.DireCraftPattern.set(new ItemStack(this, 1));
    }

    @Override
    public ICraftingPatternDetails getPatternForItem(final ItemStack is, final World w) {
        try {
            return new DireCraftingPatternDetails(is);
        } catch (final Throwable t) {
            return null;
        }
    }
}
