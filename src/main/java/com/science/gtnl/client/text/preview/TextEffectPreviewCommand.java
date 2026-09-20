package com.science.gtnl.client.text.preview;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class TextEffectPreviewCommand extends CommandBase {

    private boolean pending;

    @Override
    public String getCommandName() {
        return "gtnltexteffects";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/gtnltexteffects";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (pending) return;
        pending = true;
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != Phase.END || !pending) return;
        pending = false;
        FMLCommonHandler.instance()
            .bus()
            .unregister(this);
        // GuiChat closes its screen after dispatching a command, so open after input processing.
        Minecraft.getMinecraft()
            .displayGuiScreen(new TextEffectPreview());
    }
}
