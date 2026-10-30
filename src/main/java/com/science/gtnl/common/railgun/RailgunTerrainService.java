package com.science.gtnl.common.railgun;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeHooks;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.registry.GameRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class RailgunTerrainService {

    private record Position(int x, int y, int z, double order) {}

    private record Job(int dimension, UUID player, ObjectArrayFIFOQueue<Position> queue, double maxHardness) {}

    private static String[] protectedSource;
    private static Set<String> protectedMods = Set.of();
    private static final double EDGE_JITTER = 1.5;
    private static final int CHECKS_PER_BREAK = 8;
    private static final double MAX_RADIUS = 10;
    private static final int MAX_PENDING_JOBS = 128, MAX_BLOCKS_PER_TICK = 10000, MAX_Y = 255;
    private static final int SPARK_ONE_IN = 12, SPARK_COUNT = 2;
    private static final double SPARK_SPREAD = 0.3, SPARK_SPEED = 0.05;
    private static final double OBSIDIAN_HARDNESS = 50;
    private static final ObjectArrayList<Job> PENDING = new ObjectArrayList<>();
    private static final Map<Block, String> MOD_IDS = new Reference2ObjectOpenHashMap<>();

    public static void queueSphere(WorldServer world, Vec3d center, double radius, double hardness,
        EntityPlayerMP player) {
        if (!MainConfig.item.railgun.enableTerrain || !center.finite()
            || radius <= 0
            || radius > MAX_RADIUS
            || PENDING.size() >= MAX_PENDING_JOBS) return;
        List<Position> positions = new ObjectArrayList<>();
        int minX = MathHelper.floor_double(center.x() - radius), maxX = MathHelper.floor_double(center.x() + radius);
        int minY = Math.max(0, MathHelper.floor_double(center.y() - radius)),
            maxY = Math.min(MAX_Y, MathHelper.floor_double(center.y() + radius));
        int minZ = MathHelper.floor_double(center.z() - radius), maxZ = MathHelper.floor_double(center.z() + radius);
        for (int x = minX; x <= maxX; x++) for (int y = minY; y <= maxY; y++) for (int z = minZ; z <= maxZ; z++) {
            double distance = Math.sqrt(new Vec3d(x + 0.5, y + 0.5, z + 0.5).distanceToSqr(center));
            if (distance <= radius)
                positions.add(new Position(x, y, z, distance + world.rand.nextDouble() * EDGE_JITTER));
        }
        positions.sort(Comparator.comparingDouble(Position::order));
        ObjectArrayFIFOQueue<Position> queue = new ObjectArrayFIFOQueue<>(positions.size());
        for (Position next : positions) queue.enqueue(next);
        PENDING.add(new Job(world.provider.dimensionId, player.getUniqueID(), queue, hardness));
    }

    public static void unload(int dimension) {
        PENDING.removeIf(job -> job.dimension == dimension);
    }

    public static void tick() {
        var config = MainConfig.item.railgun;
        if (!config.enableTerrain) {
            PENDING.clear();
            return;
        }
        int budget = Math.max(1, Math.min(MAX_BLOCKS_PER_TICK, config.terrainBlocksPerTick));
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
                Position position = job.queue.dequeue();
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
                    if (config.enableTerrainDrops) block.dropBlockAsItem(world, x, y, z, metadata, 0);
                    if (world.rand.nextInt(SPARK_ONE_IN) == 0) world.func_147487_a(
                        "fireworksSpark",
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        SPARK_COUNT,
                        SPARK_SPREAD,
                        SPARK_SPREAD,
                        SPARK_SPREAD,
                        SPARK_SPEED);
                }
            }
            if (job.queue.isEmpty()) iterator.remove();
        }
    }

    private static Set<String> protectedMods() {
        String[] source = MainConfig.item.railgun.terrainProtectedMods;
        if (source != protectedSource) {
            Set<String> mods = new ObjectOpenHashSet<>();
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
        String modId = MOD_IDS.computeIfAbsent(block, RailgunTerrainService::modId);
        if (modId.isEmpty() || protectedMods().contains(modId)) return false;
        return block != Blocks.bedrock && block != Blocks.end_portal_frame
            && (block != Blocks.obsidian || maxHardness >= OBSIDIAN_HARDNESS);
    }

    private static String modId(Block block) {
        var id = GameRegistry.findUniqueIdentifierFor(block);
        return id == null ? "" : id.modId.toLowerCase(Locale.ROOT);
    }
}
