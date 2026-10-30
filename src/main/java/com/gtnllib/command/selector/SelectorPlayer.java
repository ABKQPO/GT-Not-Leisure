package com.gtnllib.command.selector;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class SelectorPlayer {

    public static List<Entity> matchPlayer(String name) {

        List<Entity> result = new ObjectArrayList<>();

        EntityPlayerMP player = MinecraftServer.getServer()
            .getConfigurationManager()
            .func_152612_a(name);

        if (player != null) {
            result.add(player);
        }

        return result;
    }
}
