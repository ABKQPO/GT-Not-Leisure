package com.science.gtnl.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;

public class CommandSuicide extends CommandBase {

    @Override
    public String getCommandName() {
        return "suicide";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.kill.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        var entityplayermp = getCommandSenderAsPlayer(sender);
        entityplayermp.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
        sender.addChatMessage(new ChatComponentTranslation("commands.kill.success"));
    }
}
