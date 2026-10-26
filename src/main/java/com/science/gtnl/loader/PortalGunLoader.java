package com.science.gtnl.loader;

import net.minecraftforge.client.MinecraftForgeClient;

import com.science.gtnl.client.portalgun.PortalGunOrbTrail;
import com.science.gtnl.common.entity.EntityPortalGunOrb;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.common.portalgun.PortalGunPairs;
import com.science.gtnl.common.render.entity.PortalGunOrbRender;
import com.science.gtnl.common.render.entity.PortalGunPortalRender;
import com.science.gtnl.common.render.item.ItemPortalGunRender;
import com.science.gtnl.common.render.item.ItemPortalGunRender.Edition;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PortalGunLoader implements IContentLoader {

    @Override
    public String name() {
        return "portalgun";
    }

    @Override
    public void serverStopping() {
        PortalGunPairs.OPENED.clear();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        PortalGunOrbTrail.register();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientInit() {
        MinecraftForgeClient.registerItemRenderer(ItemLoader.portalGun, new ItemPortalGunRender(Edition.TECH));
        MinecraftForgeClient.registerItemRenderer(ItemLoader.portalGunC137, new ItemPortalGunRender(Edition.C137));
        RenderingRegistry.registerEntityRenderingHandler(EntityPortalGunOrb.class, new PortalGunOrbRender());
        RenderingRegistry.registerEntityRenderingHandler(EntityPortalGunPortal.class, new PortalGunPortalRender());
    }
}
