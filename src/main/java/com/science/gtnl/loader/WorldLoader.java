package com.science.gtnl.loader;

import net.minecraftforge.common.MinecraftForge;

import com.gtnllib.world.WorldListener;
import com.science.gtnl.common.world.GTNLWorldgenloader;
import com.science.gtnl.common.world.VoidWorldHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class WorldLoader implements IContentLoader {

    public static final VoidWorldHandler VOID_WORLD_HANDLER = new VoidWorldHandler();
    public static final GTNLWorldgenloader GTNL_WORLDGEN_LOADER = new GTNLWorldgenloader();
    public static final WorldListener WORLD_LISTENER = new WorldListener();

    @Override
    public String name() {
        return "world";
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(VOID_WORLD_HANDLER);
        FMLCommonHandler.instance()
            .bus()
            .register(VOID_WORLD_HANDLER);
        MinecraftForge.TERRAIN_GEN_BUS.register(GTNL_WORLDGEN_LOADER);
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(WORLD_LISTENER);
        FMLCommonHandler.instance()
            .bus()
            .register(WORLD_LISTENER);
    }
}
