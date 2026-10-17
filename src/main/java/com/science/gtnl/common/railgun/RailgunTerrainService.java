package com.science.gtnl.common.railgun;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeHooks;

import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.registry.GameRegistry;

public final class RailgunTerrainService {

    private record Position(int x, int y, int z, double order) {}

    private record Job(int dimension, UUID player, ArrayDeque<Position> queue, double maxHardness) {}

    private static String[] protectedSource;
    private static Set<String> protectedMods = Set.of();
    private static final double EDGE_JITTER = 1.5;
    private static final int CHECKS_PER_BREAK = 8;
    private static final ArrayDeque<Job> PENDING = new ArrayDeque<>();

    private RailgunTerrainService() {}

    public static void queueSphere(WorldServer world, RailgunVec center, double radius, double hardness,
        EntityPlayerMP player) {
        if (!MainConfig.item.railgun.terrainEnabled || !center.finite()
            || radius <= 0
            || radius > 10
            || PENDING.size() >= 128) return;
        List<Position> positions = new ArrayList<>();
        int minX = MathHelper.floor_double(center.x() - radius), maxX = MathHelper.floor_double(center.x() + radius);
        int minY = Math.max(0, MathHelper.floor_double(center.y() - radius)),
            maxY = Math.min(255, MathHelper.floor_double(center.y() + radius));
        int minZ = MathHelper.floor_double(center.z() - radius), maxZ = MathHelper.floor_double(center.z() + radius);
        for (int x = minX; x <= maxX; x++) for (int y = minY; y <= maxY; y++) for (int z = minZ; z <= maxZ; z++) {
            double distance = Math.sqrt(new RailgunVec(x + 0.5, y + 0.5, z + 0.5).distanceToSqr(center));
            if (distance <= radius)
                positions.add(new Position(x, y, z, distance + world.rand.nextDouble() * EDGE_JITTER));
        }
        positions.sort(Comparator.comparingDouble(Position::order));
        PENDING.add(new Job(world.provider.dimensionId, player.getUniqueID(), new ArrayDeque<>(positions), hardness));
    }

    public static void unload(int dimension) {
        PENDING.removeIf(job -> job.dimension == dimension);
    }

    public static void tick() {
        var config = MainConfig.item.railgun;
        if (!config.terrainEnabled) {
            PENDING.clear();
            return;
        }
        int budget = Math.max(1, Math.min(10000, config.terrainBlocksPerTick));
        int checks = budget * CHECKS_PER_BREAK;
        var iterator = PENDING.iterator();
        while (iterator.hasNext() && budget > 0 && checks > 0) {
            Job job = iterator.next();
            WorldServer world = DimensionManager.getWorld(job.dimension);
            EntityPlayerMP player = RailgunServerEvents.player(job.player);
            if (world == null || player == null || player.isDead || player.dimension != job.dimension) {
                iterator.remove();
                continue;
            }
            while (budget > 0 && checks > 0 && !job.queue.isEmpty()) {
                Position position = job.queue.poll();
                checks--;
                int x = position.x, y = position.y, z = position.z;
                if (!world.blockExists(x, y, z)) continue;
                if (!world.canMineBlock(player, x, y, z)) continue;
                Block block = world.getBlock(x, y, z);
                if (block.isAir(world, x, y, z) || !canBreak(world, x, y, z, block, job.maxHardness)) continue;
                if (ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.getGameType(), player, x, y, z)
                    .isCanceled()) continue;
                if (world.getBlock(x, y, z) != block || !canBreak(world, x, y, z, block, job.maxHardness)) continue;
                int metadata = world.getBlockMetadata(x, y, z);
                if (world.setBlockToAir(x, y, z)) {
                    budget--;
                    if (config.terrainDropItems) block.dropBlockAsItem(world, x, y, z, metadata, 0);
                    if (world.rand.nextInt(12) == 0)
                        world.func_147487_a("fireworksSpark", x + 0.5, y + 0.5, z + 0.5, 2, 0.3, 0.3, 0.3, 0.05);
                }
            }
            if (job.queue.isEmpty()) iterator.remove();
        }
    }

    private static Set<String> protectedMods() {
        String[] source = MainConfig.item.railgun.terrainProtectedMods;
        if (source != protectedSource) {
            Set<String> mods = new HashSet<>();
            if (source != null) for (String mod : source) if (mod != null && !mod.isBlank()) mods.add(
                mod.trim()
                    .toLowerCase(Locale.ROOT));
            protectedMods = mods;
            protectedSource = source;
        }
        return protectedMods;
    }

    public static boolean canBreak(WorldServer world, int x, int y, int z, Block block, double maxHardness) {
        if (block.hasTileEntity(world.getBlockMetadata(x, y, z)) || world.getTileEntity(x, y, z) != null) return false;
        float hardness = block.getBlockHardness(world, x, y, z);
        if (!Float.isFinite(hardness) || hardness < 0 || hardness > maxHardness) return false;
        var id = GameRegistry.findUniqueIdentifierFor(block);
        if (id == null || protectedMods().contains(id.modId.toLowerCase(Locale.ROOT))) return false;
        return block != Blocks.bedrock && block != Blocks.end_portal_frame
            && (block != Blocks.obsidian || maxHardness >= 50);
    }
}
