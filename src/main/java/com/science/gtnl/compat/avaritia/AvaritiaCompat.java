package com.science.gtnl.compat.avaritia;

import net.minecraftforge.client.MinecraftForgeClient;

import com.science.gtnl.compat.IModCompat;
import com.science.gtnl.loader.ItemLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.render.FancyHaloRenderer;

public class AvaritiaCompat implements IModCompat {

    @Override
    @SideOnly(Side.CLIENT)
    public void clientInit() {
        MinecraftForgeClient.registerItemRenderer(ItemLoader.testItem, new FancyHaloRenderer());
        MinecraftForgeClient.registerItemRenderer(ItemLoader.metaItem, new FancyHaloRenderer());
    }
}
