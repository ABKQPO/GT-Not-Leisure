package com.science.gtnl.common.command;

import static com.science.gtnl.ScienceNotLeisure.network;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import com.science.gtnl.common.packet.SoundPacket;

public class CommandPlaySound extends CommandBase {

    @Override
    public String getCommandName() {
        return "gtnl_playsound";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/gtnl_playsound <MODID>:<sound_resource>/stop [volume] [pitch] [seekMs]";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("gtnlps");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 4) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (args[0].equalsIgnoreCase("stop")) {
            network.sendTo(new SoundPacket(), (EntityPlayerMP) sender);
        }

        ResourceLocation soundResource;
        try {
            soundResource = new ResourceLocation(args[0]);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("Invalid sound resource path: " + args[0]));
            return;
        }

        float volume = 1.0F;
        if (args.length >= 2) {
            try {
                volume = Float.parseFloat(args[1]);
                if (volume < 0.0F) volume = 0.0F;
                if (volume > 1.0F) volume = 1.0F;
            } catch (NumberFormatException e) {
                sender.addChatMessage(
                    new ChatComponentText("Invalid sound volume: " + args[1] + ", use default value 1.0"));
            }
        }

        float pitch = 1.0F;
        if (args.length >= 3) {
            try {
                pitch = Float.parseFloat(args[1]);
                if (pitch < 0.0F) pitch = 0.0F;
                if (pitch > 1.0F) pitch = 1.0F;
            } catch (NumberFormatException e) {
                sender.addChatMessage(
                    new ChatComponentText("Invalid sound pitch: " + args[1] + ", use default value 1.0"));
            }
        }

        long seekMs = 0L;
        if (args.length == 4) {
            try {
                seekMs = Long.parseLong(args[2]);
                if (seekMs < 0L) seekMs = 0L;
            } catch (NumberFormatException e) {
                sender.addChatMessage(new ChatComponentText("Invalid seekMs: " + args[2] + ", use default value 0"));
            }
        }

        network.sendTo(new SoundPacket(soundResource, volume, pitch, seekMs), (EntityPlayerMP) sender);
        sender.addChatMessage(
            new ChatComponentText("Sent sound effect playback synchronization request: " + soundResource));

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "sciencenotleisure:sus");
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "1.0", "0.5", "0.1");
        }
        if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, "1.0", "0.5", "0.1");
        }
        if (args.length == 4) {
            return getListOfStringsMatchingLastWord(args, "0", "1000", "5000");
        }
        return null;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
