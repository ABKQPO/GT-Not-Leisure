package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandTeleport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.command.TeleportCommandHooks;

@Mixin(value = CommandTeleport.class, remap = true)
public abstract class MixinCommandTeleport {

    @Inject(method = "processCommand", at = @At("HEAD"), cancellable = true)
    private void onProcessCommand(ICommandSender sender, String[] args, CallbackInfo ci) {
        if (TeleportCommandHooks.teleportToPlayer(sender, args)) ci.cancel();
    }
}
