package com.science.gtnl.common.command;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;

import com.gtnllib.command.selector.EntitySelector;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class KillCommandHooks {

    private KillCommandHooks() {}

    public static void kill(ICommandSender sender, String[] args) {
        List<Entity> targets;

        if (args.length == 0) {
            Entity self = CommandBase.getCommandSenderAsPlayer(sender);
            targets = Collections.singletonList(self);
        } else {
            targets = EntitySelector.matchEntities(sender, args[0]);
        }

        if (targets.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        int killed = 0;
        Entity last = null;

        for (Entity entity : targets) {

            if (entity == null || entity.isDead) {
                continue;
            }

            last = entity;

            if (entity instanceof EntityLivingBase) {
                entity.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
            } else {
                entity.setDead();
            }

            killed++;
        }

        if (killed <= 0) {
            throw new PlayerNotFoundException();
        }

        if (killed == 1 && last != null) {
            sender.addChatMessage(
                new ChatComponentTranslation("commands.kill.success.single", last.getCommandSenderName()));

        } else {
            sender.addChatMessage(new ChatComponentTranslation("commands.kill.success.multiple", killed));
        }
    }

    public static List<String> tabComplete(String[] args) {
        String currentArg = args[0];

        if (args.length == 1 && !currentArg.contains("[")) {
            List<String> list = new ObjectArrayList<>();
            Collections.addAll(list, "@p", "@a", "@r", "@e", "@s", "@n");
            Collections.addAll(
                list,
                MinecraftServer.getServer()
                    .getAllUsernames());
            return CommandBase.getListOfStringsMatchingLastWord(args, list.toArray(new String[0]));
        }

        if (args.length == 1 && currentArg.contains("[")) {
            String prefix = currentArg.substring(0, currentArg.lastIndexOf("[") + 1);
            String suffix = currentArg.substring(currentArg.lastIndexOf("[") + 1);

            String lastPart = suffix.contains(",") ? suffix.substring(suffix.lastIndexOf(",") + 1) : suffix;

            List<String> suggestions = new ObjectArrayList<>();

            if (lastPart.contains("=")) {
                String key = lastPart.substring(0, lastPart.indexOf("=") + 1);
                String valuePrefix = lastPart.substring(lastPart.indexOf("=") + 1);

                if (key.equals("type=")) {
                    String[] types = entityTypes();
                    for (String t : types) {
                        if (t.startsWith(valuePrefix)) {
                            suggestions.add(prefix + suffix.substring(0, suffix.lastIndexOf(valuePrefix)) + t);
                        }
                    }
                }
            } else {
                String[] keys = { "type=", "name=", "r=", "rm=", "limit=", "sort=" };
                for (String k : keys) {
                    if (k.startsWith(lastPart)) {
                        String base = currentArg.substring(0, currentArg.length() - lastPart.length());
                        suggestions.add(base + k);
                    }
                }
            }
            return suggestions;
        }

        return null;
    }

    private static String[] entityTypes() {
        Set<String> set = EntityList.stringToClassMapping.keySet();

        List<String> list = new ObjectArrayList<>(set);

        list.add("player");

        return list.toArray(new String[0]);
    }
}
