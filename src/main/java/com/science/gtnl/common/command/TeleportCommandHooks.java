package com.science.gtnl.common.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import com.gtnllib.utils.TeleporterUtils;
import com.science.gtnl.ScienceNotLeisure;

public class TeleportCommandHooks {

    public static boolean teleportToPlayer(ICommandSender sender, String[] args) {
        if (args.length < 1) return false;

        try {
            EntityPlayerMP target = null;
            if (args.length != 2 && args.length != 4) {
                if (sender instanceof EntityPlayerMP playerMP) {
                    target = playerMP;
                }
            } else {
                target = player(args[0]);
            }

            if (target == null) return false;

            if (args.length == 1 || args.length == 2) {
                EntityPlayerMP dest = player(args[args.length - 1]);
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

                return true;
            }
        } catch (Exception e) {
            ScienceNotLeisure.LOG.warn("[MixinCommandTeleport] Failed to execute custom teleport", e);
        }
        return false;
    }

    private static EntityPlayerMP player(String name) {
        return MinecraftServer.getServer()
            .getConfigurationManager()
            .func_152612_a(name);
    }
}
