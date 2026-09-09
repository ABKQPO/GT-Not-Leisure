package com.reavaritia.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;

import com.reavaritia.common.ItemLoader;

public class ReAvaCreativeTabs {

    public static CreativeTabs ReAvaritia = new CreativeTabs("reavaritia") {

        @Override
        public String getTranslatedTabLabel() {
            return StatCollector.translateToLocal("reavaritia.item_group.reavaritia");
        }

        @Override
        public Item getTabIconItem() {
            return ItemLoader.InfinityPickaxe;
        }
    };

}
