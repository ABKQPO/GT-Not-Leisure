package com.science.gtnl.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public class GTNLCreativeTabs {

    public static CreativeTabs GTNotLeisureItem = new CreativeTabs("gtnl.item_group.item") {

        @Override
        public Item getTabIconItem() {
            return ItemLoader.testItem;
        }
    };

    public static CreativeTabs GTNotLeisureBlock = new CreativeTabs("gtnl.item_group.block") {

        @Override
        public Item getTabIconItem() {
            return GTNLItemList.NewHorizonsCoil.getItem();
        }

        @SideOnly(Side.CLIENT)
        public int func_151243_f() {
            return 1;
        }
    };

    public static CreativeTabs GTNotLeisurePlayerDoll = new CreativeTabs("gtnl.item_group.player_doll") {

        @Override
        public Item getTabIconItem() {
            return GTNLItemList.PlayerDoll.getItem();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getBackgroundImageName() {
            return "item_search.png";
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }
    };

    private static final List<ItemStack> GTNotLeisureMachineStack = new ArrayList<>();

    public static void addToMachineList(ItemStack stack) {
        GTNotLeisureMachineStack.add(stack);
    }

    public static CreativeTabs GTNotLeisureMachine = new CreativeTabs("gtnl.item_group.machine") {

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

        @Override
        @SideOnly(Side.CLIENT)
        public String getBackgroundImageName() {
            return "item_search.png";
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }
    };

}
