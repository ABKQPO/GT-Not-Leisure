package com.science.gtnl.common.portalgun;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.science.gtnl.common.entity.EntityPortalGunPortal;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class PortalGunPairs {

    public static final PortalGunPairs OPENED = new PortalGunPairs();
    private static final double OVERLAP_MARGIN = 0.05;

    private final Map<UUID, List<Opened>> opened = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<UUID> waiting = new Object2IntOpenHashMap<>();
    private int lastPair, lastRequest;

    public record Opened(int dimension, int entityId, int pair) {}

    public void open(World world, PortalGunPlacement source, PortalGunPlacement destination, int lifetime, UUID gunId,
        Runnable failed) {
        WorldServer target = MinecraftServer.getServer()
            .worldServerForDimension(destination.dimension());
        if (target == null) {
            failed.run();
            return;
        }
        int request = request(gunId);
        // Why: given a callback, Forge reads a saved chunk off the server thread.
        target.theChunkProviderServer.loadChunk(
            MathHelper.floor_double(destination.x()) >> 4,
            MathHelper.floor_double(destination.z()) >> 4,
            () -> {
                if (!claim(gunId, request)) return;
                if (!openLoaded(world, target, source, destination, lifetime, gunId)) failed.run();
            });
    }

    public int request(UUID gunId) {
        int request = ++lastRequest;
        waiting.put(gunId, request);
        return request;
    }

    public boolean claim(UUID gunId, int request) {
        return waiting.remove(gunId, request);
    }

    private boolean openLoaded(World world, WorldServer target, PortalGunPlacement source,
        PortalGunPlacement destination, int lifetime, UUID gunId) {
        if (DimensionManager.getWorld(source.dimension()) != world
            || DimensionManager.getWorld(destination.dimension()) != target) return false;
        if (blockedByOtherGun(world, source, gunId) || blockedByOtherGun(target, destination, gunId)) return false;
        for (Opened old : take(gunId)) {
            if (find(old) instanceof EntityPortalGunPortal portal) portal.close();
        }
        int pair = ++lastPair;
        spawn(world, source, destination, lifetime, gunId, pair);
        spawn(target, destination, source, lifetime, gunId, pair);
        return true;
    }

    public void spawn(World world, PortalGunPlacement here, PortalGunPlacement link, int lifetime, UUID gunId,
        int pair) {
        EntityPortalGunPortal portal = new EntityPortalGunPortal(world, here, link, lifetime, gunId, pair);
        if (!world.spawnEntityInWorld(portal)) return;
        remember(gunId, new Opened(here.dimension(), portal.getEntityId(), pair));
        world.playSoundAtEntity(portal, "mob.endermen.portal", 0.8F, 0.6F);
    }

    public void close(UUID gunId, int pair) {
        for (Opened portal : pair(gunId, pair)) {
            if (find(portal) instanceof EntityPortalGunPortal partner && partner.pair() == pair) partner.close();
        }
    }

    public void remember(UUID gunId, Opened portal) {
        opened.computeIfAbsent(gunId, id -> new ObjectArrayList<>())
            .add(portal);
    }

    public void forget(UUID gunId, int entityId) {
        List<Opened> list = opened.get(gunId);
        if (list == null) return;
        list.removeIf(portal -> portal.entityId() == entityId);
        if (list.isEmpty()) opened.remove(gunId);
    }

    public List<Opened> take(UUID gunId) {
        List<Opened> list = opened.remove(gunId);
        return list == null ? new ObjectArrayList<>() : list;
    }

    public List<Opened> pair(UUID gunId, int pair) {
        List<Opened> found = new ObjectArrayList<>();
        for (Opened portal : opened.getOrDefault(gunId, new ObjectArrayList<>()))
            if (portal.pair() == pair) found.add(portal);
        return found;
    }

    public void clear() {
        opened.clear();
        waiting.clear();
    }

    private static boolean blockedByOtherGun(World world, PortalGunPlacement placement, UUID gunId) {
        AxisAlignedBB box = PortalGunGeometry.bounds(placement)
            .contract(OVERLAP_MARGIN, OVERLAP_MARGIN, OVERLAP_MARGIN);
        for (Object found : world.getEntitiesWithinAABB(EntityPortalGunPortal.class, box)) {
            if (found instanceof EntityPortalGunPortal other && !other.closing() && !gunId.equals(other.gunId()))
                return true;
        }
        return false;
    }

    private static Entity find(Opened opened) {
        WorldServer world = DimensionManager.getWorld(opened.dimension());
        return world == null ? null : world.getEntityByID(opened.entityId());
    }
}
