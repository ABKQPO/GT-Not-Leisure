package com.science.gtnl.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.GTNLItemList;
import com.science.gtnl.common.item.ItemLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public final class GTNLCreativeTabs {

    public static CreativeTabs GTNotLeisureItem = new CreativeTabs("GTNotLeisureItem") {

        @Override
        public Item getTabIconItem() {
            return ItemLoader.TestItem;
        }
    };

    public static CreativeTabs GTNotLeisureBlock = new CreativeTabs("GTNotLeisureBlock") {

        @Override
        public Item getTabIconItem() {
            return GTNLItemList.NewHorizonsCoil.getItem();
        }

        @SideOnly(Side.CLIENT)
        public int func_151243_f() {
            return 1;
        }
    };

    public static CreativeTabs ReAvaritia = new CreativeTabs("ReAvaritia") {

        @Override
        public Item getTabIconItem() {
            return ItemLoader.InfinityPickaxe;
        }
    };

    private static final List<ItemStack> GTNotLeisureMachineStack = new ArrayList<>();

    public static void addToMachineList(ItemStack stack) {
        GTNotLeisureMachineStack.add(stack);
    }

    public static CreativeTabs GTNotLeisureMachine = new CreativeTabs("GTNotLeisureMachine") {

        @Override
        public Item getTabIconItem() {
            return GTNLItemList.GenerationEarthEngine.getItem();
        }

        @SideOnly(Side.CLIENT)
        public int func_151243_f() {
            return 21006;
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> stackList) {
            stackList.addAll(GTNotLeisureMachineStack);
            super.displayAllReleventItems(stackList);
        }
    };

}
