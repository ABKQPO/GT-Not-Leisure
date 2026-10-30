package com.gtnllib.client.fx.world;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FxEffects {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new FxEffects());
        MinecraftForge.EVENT_BUS.register(new FxArcRenderer());
        MinecraftForge.EVENT_BUS.register(new FxShockwaveRenderer());
        MinecraftForge.EVENT_BUS.register(new FxMoteRenderer());
    }

    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
        if (!event.world.isRemote) return;
        FxArcRenderer.clear();
        FxShockwaveRenderer.clear();
        FxMoteRenderer.clear();
    }

    @SubscribeEvent
    public void reload(TextureStitchEvent.Post event) {
        FxRenderHelper.clearSprites();
    }
}
