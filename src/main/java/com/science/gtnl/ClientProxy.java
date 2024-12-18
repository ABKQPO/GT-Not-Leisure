package com.science.gtnl;

import com.science.gtnl.common.block.Render.MeteorMinerRenderer;
import com.science.gtnl.common.block.Render.RealArtificialStarRender;
import com.science.gtnl.common.block.Render.StarRender;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        new RealArtificialStarRender();
        new MeteorMinerRenderer();
        RenderingRegistry.registerBlockHandler(new StarRender());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

    }

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

}
