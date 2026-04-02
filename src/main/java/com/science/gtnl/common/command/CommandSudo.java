package com.science.gtnl.common.command;

import static com.science.gtnl.ScienceNotLeisure.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.science.gtnl.common.packet.SudoPacket;

public class CommandSudo extends CommandBase {

    @Override
    public String getCommandName() {
        return "sudo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sudo <player> <message>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("Usage: /sudo <player> <message>"));
            return;
        }

        EntityPlayerMP target;
        try {
            target = getPlayer(sender, args[0]);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("Player not found: " + args[0]));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]);
            if (i < args.length - 1) sb.append(" ");
        }
        String msg = sb.toString();

        network.sendTo(new SudoPacket(msg), target);

        StringBuilder executedCommand = new StringBuilder("/sudo ");
        for (String arg : args) {
            executedCommand.append(arg)
                .append(" ");
        }
        sender.addChatMessage(
            new ChatComponentText(
                "Successfully executed: " + executedCommand.toString()
                    .trim()));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (EntityPlayerMP obj : MinecraftServer.getServer()
                .getConfigurationManager().playerEntityList) {
                names.add(obj.getCommandSenderName());
            }
            return names;
        }
        return null;
    }
}
