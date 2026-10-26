package com.science.gtnl.loader;

import net.minecraftforge.client.MinecraftForgeClient;

import com.science.gtnl.client.railgun.RailgunClientFx;
import com.science.gtnl.common.railgun.RailgunServerEvents;
import com.science.gtnl.common.render.item.ItemElectromagneticRailgunRender;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RailgunLoader implements IContentLoader {

    @Override
    public String name() {
        return "railgun";
    }

    @Override
    public void preInit() {
        RailgunServerEvents.register();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        RailgunClientFx.register();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientInit() {
        MinecraftForgeClient
            .registerItemRenderer(ItemLoader.electromagneticRailgun, new ItemElectromagneticRailgunRender());
    }
}
