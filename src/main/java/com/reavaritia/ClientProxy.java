package com.reavaritia;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.reavaritia.common.ItemLoader;
import com.reavaritia.common.SubscribeEventClientUtils;
import com.reavaritia.common.block.ExtremeAnvil.EntityExtremeAnvil;
import com.reavaritia.common.block.ExtremeAnvil.RenderExtremeAnvil;
import com.reavaritia.common.block.ExtremeAnvil.RenderFallingBlockExtremeAnvil;
import com.reavaritia.common.block.ExtremeAnvil.TileEntityExtremeAnvil;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fox.spiteful.avaritia.render.CosmicItemRenderer;

public class ClientProxy extends CommonProxy {

    public static int extremeAnvilRenderType;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        extremeAnvilRenderType = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderExtremeAnvil());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExtremeAnvil.class, new RenderExtremeAnvil());
        RenderingRegistry
            .registerEntityRenderingHandler(EntityExtremeAnvil.class, new RenderFallingBlockExtremeAnvil());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new SubscribeEventClientUtils());
        FMLCommonHandler.instance()
            .bus()
            .register(new SubscribeEventClientUtils());
        super.preInit(event);
    }

    @Override
    public void makeThingsPretty() {
        CosmicItemRenderer sparkly = new CosmicItemRenderer();
        MinecraftForgeClient.registerItemRenderer(ItemLoader.InfinitySword, sparkly);
        MinecraftForgeClient.registerItemRenderer(ItemLoader.MatterCluster, sparkly);
    }
}
