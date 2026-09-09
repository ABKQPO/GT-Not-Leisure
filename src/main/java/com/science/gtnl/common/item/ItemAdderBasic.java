package com.science.gtnl.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAdderBasic extends Item {

    public List<String> tooltips = new ArrayList<>();

    public String unlocalizedName;

    public ItemAdderBasic(String aName, CreativeTabs aCreativeTabs) {
        super();
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(aCreativeTabs);
        this.unlocalizedName = aName;
        GameRegistry.registerItem(this, unlocalizedName);
    }

    @Override
    public int getMetadata(int aMeta) {
        return aMeta;
    }

    @Override
    public Item setUnlocalizedName(String aUnlocalizedName) {
        this.unlocalizedName = aUnlocalizedName;
        return this;
    }

    @Override
    public String getUnlocalizedName(ItemStack aItemStack) {
        return "item.gtnl." + this.unlocalizedName + "." + aItemStack.getItemDamage();
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl." + this.unlocalizedName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> aList) {
        aList.add(new ItemStack(aItem, 1, 0));
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack aItemStack, EntityPlayer aEntityPlayer, List<String> aTooltipsList,
        boolean p_77624_4_) {
        if (!tooltips.isEmpty()) {
            aTooltipsList.addAll(tooltips);
        }
    }

}
