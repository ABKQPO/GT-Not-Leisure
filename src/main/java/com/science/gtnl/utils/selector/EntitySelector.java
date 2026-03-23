package com.science.gtnl.utils.selector;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class EntitySelector {

    public static List<Entity> matchEntities(ICommandSender sender, String token) {

        if (!token.startsWith("@")) {
            return SelectorPlayer.matchPlayer(token);
        }

        SelectorParser parser = new SelectorParser(token);

        List<Entity> entities = new ArrayList<>();

        for (World world : MinecraftServer.getServer().worldServers) {
            entities.addAll(world.loadedEntityList);
        }

        entities = SelectorFilter.applyFilters(sender, parser, entities);

        return entities;
    }
}
