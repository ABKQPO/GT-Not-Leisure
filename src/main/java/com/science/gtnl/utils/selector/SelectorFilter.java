package com.science.gtnl.utils.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;

public class SelectorFilter {

    public static List<Entity> applyFilters(ICommandSender sender, SelectorParser parser, List<Entity> input) {

        List<Entity> result = new ArrayList<>(input);

        filterType(parser, result);
        filterName(parser, result);
        filterDistance(sender, parser, result);
        sort(parser, sender, result);
        limit(parser, result);

        return result;
    }

    /**
     * type filter
     *
     * type=zombie
     * type=!player
     * type=zombie,type=skeleton
     * type=!chicken,type=!cow
     */
    public static void filterType(SelectorParser parser, List<Entity> list) {

        List<String> types = parser.getAll("type");

        if (types == null || types.isEmpty()) {
            return;
        }

        Iterator<Entity> it = list.iterator();

        while (it.hasNext()) {

            Entity e = it.next();

            String id = EntityList.getEntityString(e);

            boolean remove = false;

            for (String type : types) {

                boolean invert = type.startsWith("!");

                if (invert) {
                    type = type.substring(1);
                }

                boolean match = false;

                if ("player".equalsIgnoreCase(type)) {

                    match = e instanceof EntityPlayer;

                } else if (id != null) {

                    match = id.equalsIgnoreCase(type);
                }

                if (invert == match) {
                    remove = true;
                    break;
                }
            }

            if (remove) {
                it.remove();
            }
        }
    }

    /**
     * name filter
     */
    public static void filterName(SelectorParser parser, List<Entity> list) {

        String name = parser.get("name");

        if (name == null) return;

        list.removeIf(e -> !name.equals(e.getCommandSenderName()));
    }

    /**
     * r / rm distance filter
     */
    public static void filterDistance(ICommandSender sender, SelectorParser parser, List<Entity> list) {

        String r = parser.get("r");
        String rm = parser.get("rm");

        if (r == null && rm == null) {
            return;
        }

        double max = Double.MAX_VALUE;
        double min = 0;

        try {

            if (r != null) {
                max = Double.parseDouble(r);
            }

            if (rm != null) {
                min = Double.parseDouble(rm);
            }

        } catch (NumberFormatException ignored) {
            return;
        }

        Iterator<Entity> it = list.iterator();

        int x = sender.getPlayerCoordinates().posX;
        int y = sender.getPlayerCoordinates().posY;
        int z = sender.getPlayerCoordinates().posZ;

        while (it.hasNext()) {

            Entity e = it.next();

            double d = e.getDistance(x, y, z);

            if (d > max || d < min) {
                it.remove();
            }
        }
    }

    /**
     * sort filter
     *
     * sort=random
     * sort=nearest
     * sort=furthest
     */
    public static void sort(SelectorParser parser, ICommandSender sender, List<Entity> list) {

        String sort = parser.get("sort");

        if (sort == null) return;

        if ("random".equals(sort)) {

            Collections.shuffle(list);
            return;
        }

        final int x = sender.getPlayerCoordinates().posX;
        final int y = sender.getPlayerCoordinates().posY;
        final int z = sender.getPlayerCoordinates().posZ;

        list.sort((a, b) -> {

            double da = a.getDistanceSq(x, y, z);
            double db = b.getDistanceSq(x, y, z);

            if ("nearest".equals(sort)) {
                return Double.compare(da, db);
            }

            if ("furthest".equals(sort)) {
                return Double.compare(db, da);
            }

            return 0;
        });
    }

    /**
     * limit filter
     */
    public static void limit(SelectorParser parser, List<Entity> list) {

        String limit = parser.get("limit");

        if (limit == null) return;

        int l;

        try {
            l = Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            return;
        }

        if (l < list.size()) {
            list.subList(l, list.size())
                .clear();
        }
    }
}
