package com.science.gtnl.loader;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

import com.gtnllib.command.CommandItemInfo;
import com.gtnllib.command.CommandSuicide;
import com.science.gtnl.client.command.MultiblockTooltipExportCommand;
import com.science.gtnl.common.command.CommandEnergyNetwork;
import com.science.gtnl.common.command.CommandPlaySound;
import com.science.gtnl.common.command.CommandSpoce;
import com.science.gtnl.common.command.CommandSudo;
import com.science.gtnl.common.render.SpoceRenderHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CommandLoader implements IContentLoader {

    @Override
    public String name() {
        return "commands";
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandEnergyNetwork());
        event.registerServerCommand(new CommandPlaySound());
        event.registerServerCommand(new CommandItemInfo());
        event.registerServerCommand(new CommandSudo());
        event.registerServerCommand(new CommandSuicide());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        MinecraftForge.EVENT_BUS.register(new SpoceRenderHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new SpoceRenderHandler());
        ClientCommandHandler.instance.registerCommand(new CommandSpoce());
        if ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            ClientCommandHandler.instance.registerCommand(new MultiblockTooltipExportCommand());
        }
    }
}
