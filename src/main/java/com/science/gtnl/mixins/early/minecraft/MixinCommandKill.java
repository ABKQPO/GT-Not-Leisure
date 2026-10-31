package com.science.gtnl.mixins.early.minecraft;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandKill;
import net.minecraft.command.ICommandSender;

import org.spongepowered.asm.mixin.Mixin;

import com.science.gtnl.common.command.KillCommandHooks;

@Mixin(value = CommandKill.class, remap = true)
public abstract class MixinCommandKill extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        KillCommandHooks.kill(sender, args);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return KillCommandHooks.tabComplete(args);
    }
}
