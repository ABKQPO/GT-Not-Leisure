package com.science.gtnl.loader;

import net.minecraftforge.common.MinecraftForge;

import com.gtnllib.world.WorldListener;

import cpw.mods.fml.common.FMLCommonHandler;

public class WorldLoader implements IContentLoader {

    public static final WorldListener WORLD_LISTENER = new WorldListener();

    @Override
    public String name() {
        return "world";
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(WORLD_LISTENER);
        FMLCommonHandler.instance()
            .bus()
            .register(WORLD_LISTENER);
    }
}
