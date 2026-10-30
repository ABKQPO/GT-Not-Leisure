package com.gtnllib.command.selector;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class EntitySelector {

    public static List<Entity> matchEntities(ICommandSender sender, String token) {

        if (!token.startsWith("@")) {
            return SelectorPlayer.matchPlayer(token);
        }

        SelectorParser parser = new SelectorParser(token);

        List<Entity> entities = new ObjectArrayList<>();

        for (World world : MinecraftServer.getServer().worldServers) {
            entities.addAll(world.loadedEntityList);
        }

        entities = SelectorFilter.applyFilters(sender, parser, entities);

        return entities;
    }
}
