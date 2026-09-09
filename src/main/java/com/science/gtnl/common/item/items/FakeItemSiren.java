package com.science.gtnl.common.item.items;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FakeItemSiren extends Item {

    public FakeItemSiren() {
        super();
        this.setUnlocalizedName("gtnl.fake_item_siren");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        this.setTextureName(RESOURCE_ROOT_ID + ":" + "fake_item_siren");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "fake_item_siren");
        GTNLItemList.FakeItemSiren.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "item.gtnl.fake_item_siren";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(getIconString());
    }

}
