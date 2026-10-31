package com.science.gtnl.common.item.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FakeItemSiren extends Item {

    public FakeItemSiren() {
        super();
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
