package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.TeleporterUtils;

@Mixin(CommandTeleport.class)
public abstract class MixinCommandTeleport {

    @Inject(method = "processCommand", at = @At("HEAD"), cancellable = true)
    private void onProcessCommand(ICommandSender sender, String[] args, CallbackInfo ci) {
        if (args.length < 1) return;

        try {
            EntityPlayerMP target = null;
            if (args.length != 2 && args.length != 4) {
                if (sender instanceof EntityPlayerMP playerMP) {
                    target = playerMP;
                }
            } else {
                target = getPlayer(sender, args[0]);
            }

            if (target == null) return;

            if (args.length == 1 || args.length == 2) {
                EntityPlayerMP dest = getPlayer(sender, args[args.length - 1]);
                if (dest == null) throw new PlayerNotFoundException();

                if (dest.dimension != target.dimension) {
                    MinecraftServer server = MinecraftServer.getServer();
                    WorldServer targetWorld = server.worldServerForDimension(dest.dimension);

                    server.getConfigurationManager()
                        .transferPlayerToDimension(
                            target,
                            dest.dimension,
                            new TeleporterUtils(targetWorld, dest.posX, dest.posY, dest.posZ));

                    target.playerNetServerHandler
                        .setPlayerLocation(dest.posX, dest.posY, dest.posZ, dest.rotationYaw, dest.rotationPitch);
                } else {
                    target.mountEntity(null);
                    target.playerNetServerHandler
                        .setPlayerLocation(dest.posX, dest.posY, dest.posZ, dest.rotationYaw, dest.rotationPitch);
                }

                ci.cancel();
            }
        } catch (Exception ignored) {}
    }

    private EntityPlayerMP getPlayer(ICommandSender sender, String name) {
        return MinecraftServer.getServer()
            .getConfigurationManager()
            .func_152612_a(name);
    }
}
