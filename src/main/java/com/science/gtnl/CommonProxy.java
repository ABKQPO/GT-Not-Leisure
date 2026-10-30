package com.science.gtnl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.science.gtnl.common.gui.GTNLGuiHandler;
import com.science.gtnl.common.render.entity.EntityParticleBeam;
import com.science.gtnl.loader.ContentLoaders;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy implements IGuiHandler {

    public void preInit(FMLPreInitializationEvent event) {
        ContentLoaders.preInit();
    }

    public void init(FMLInitializationEvent event) {
        ContentLoaders.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        ContentLoaders.postInit();
    }

    public void completeInit(FMLLoadCompleteEvent event) {
        ContentLoaders.loadComplete();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return GTNLGuiHandler.serverElement(ID, player, world, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void setTimeStopVisual(boolean stopped) {}

    public boolean isCurrentClientWorld(World world) {
        return false;
    }

    public EntityPlayer getEntityPlayerFromContext(final MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    public EntityParticleBeam particleBeam(double x, double y, double z, double masterX, double masterY, double masterZ,
        double radius, EntityParticleBeam oldBeam, boolean render) {
        return null;
    }
}
