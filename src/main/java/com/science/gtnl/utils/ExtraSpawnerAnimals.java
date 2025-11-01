package com.science.gtnl.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;

import cpw.mods.fml.common.eventhandler.Event.Result;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class ExtraSpawnerAnimals {

    /** The 17x17 area around the player where mobs can spawn */
    public Object2BooleanOpenHashMap<ChunkCoordIntPair> eligibleChunksForSpawning = new Object2BooleanOpenHashMap<>();

    @Getter
    @Setter
    public float MAX_SPAWN_DISTANCE = 4.0F;
    @Getter
    @Setter
    public int maxAttempts = 6, maxSpawnSize = 1, maxSpawnMultiplier = 4, maxChunkRadius = 8;

    public static ChunkPosition getRandomSpawnPosition(World world, int chunkX, int chunkZ) {
        // 获取指定区块
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        // 随机选择区块内的位置
        int randomX = chunkX * 16 + world.rand.nextInt(16); // 随机X坐标
        int randomZ = chunkZ * 16 + world.rand.nextInt(16); // 随机Z坐标

        // 随机Y坐标（考虑区块的顶部或世界的最大高度）
        int maxHeight = (chunk == null) ? world.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1;
        int randomY = world.rand.nextInt(maxHeight);

        return new ChunkPosition(randomX, randomY, randomZ);
    }

    public int findChunksForSpawning(WorldServer world, boolean allowPeaceful, boolean allowHostile,
        boolean allowAnimals) {
        if (!allowPeaceful && !allowHostile) {
            return 0;
        } else {
            eligibleChunksForSpawning.clear(); // 清空可刷怪的区块列表
            int spawnCount = 0;

            // 遍历所有玩家
            for (EntityPlayer player : world.playerEntities) {
                int chunkX = MathHelper.floor_double(player.posX / 16.0D);
                int chunkZ = MathHelper.floor_double(player.posZ / 16.0D);
                int chunkRadius = maxChunkRadius; // 设置刷怪范围的半径

                // 遍历玩家周围的所有区块
                for (int x = -chunkRadius; x <= chunkRadius; ++x) {
                    for (int z = -chunkRadius; z <= chunkRadius; ++z) {
                        boolean isEdge = x == -chunkRadius || x == chunkRadius || z == -chunkRadius || z == chunkRadius;
                        ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(x + chunkX, z + chunkZ);

                        // 边缘区块标记为需要生成
                        if (!isEdge) {
                            eligibleChunksForSpawning.put(chunkCoords, false);
                        } else if (!eligibleChunksForSpawning.containsKey(chunkCoords)) {
                            eligibleChunksForSpawning.put(chunkCoords, true);
                        }
                    }
                }
            }

            ChunkCoordinates spawnPoint = world.getSpawnPoint();
            EnumCreatureType[] creatureTypes = EnumCreatureType.values();

            // 遍历所有的生物类型
            for (EnumCreatureType creatureType : creatureTypes) {
                // 根据不同类型的生物检查是否允许生成
                if ((!creatureType.getPeacefulCreature() || allowPeaceful)
                    && (creatureType.getPeacefulCreature() || allowHostile)
                    && (!creatureType.getAnimal() || allowAnimals)
                    && world.countEntities(creatureType, true)
                        <= creatureType.getMaxNumberOfCreature() * eligibleChunksForSpawning.size()
                            / 256
                            * maxSpawnMultiplier) {

                    // 打乱区块顺序，增加生成的随机性
                    List<ChunkCoordIntPair> tempList = new ArrayList<>(eligibleChunksForSpawning.keySet());
                    Collections.shuffle(tempList);

                    // 遍历所有可刷怪的区块
                    for (ChunkCoordIntPair chunk : tempList) {
                        if (!eligibleChunksForSpawning.getOrDefault(chunk, true)) {
                            ChunkPosition spawnPos = getRandomSpawnPosition(world, chunk.chunkXPos, chunk.chunkZPos);
                            int x = spawnPos.chunkPosX;
                            int y = spawnPos.chunkPosY;
                            int z = spawnPos.chunkPosZ;

                            // 检查是否可以在该位置生成生物
                            if (!world.getBlock(x, y, z)
                                .isNormalCube()
                                && world.getBlock(x, y, z)
                                    .getMaterial() == creatureType.getCreatureMaterial()) {

                                int spawned = 0;
                                int attemptCount = 0;

                                // 在当前位置随机生成生物
                                while (attemptCount < maxAttempts) {
                                    int newX = x + world.rand.nextInt(6) - world.rand.nextInt(6);
                                    int newY = y;
                                    int newZ = z + world.rand.nextInt(6) - world.rand.nextInt(6);

                                    if (canCreatureTypeSpawnAtLocation(creatureType, world, newX, newY, newZ)) {
                                        float spawnX = (float) newX + 0.5F;
                                        float spawnY = (float) newY;
                                        float spawnZ = (float) newZ + 0.5F;

                                        // 检查是否有玩家在附近
                                        if (world.getClosestPlayer(spawnX, spawnY, spawnZ, MAX_SPAWN_DISTANCE)
                                            == null) {
                                            // 计算与出生点的距离，限制生物的生成距离
                                            float distX = spawnX - (float) spawnPoint.posX;
                                            float distY = spawnY - (float) spawnPoint.posY;
                                            float distZ = spawnZ - (float) spawnPoint.posZ;
                                            float distanceSquared = distX * distX + distY * distY + distZ * distZ;

                                            if (distanceSquared >= MAX_SPAWN_DISTANCE * MAX_SPAWN_DISTANCE) {
                                                try {
                                                    BiomeGenBase.SpawnListEntry spawnEntry = world
                                                        .spawnRandomCreature(creatureType, newX, newY, newZ);

                                                    if (spawnEntry != null) {
                                                        EntityLiving entity = spawnEntry.entityClass
                                                            .getConstructor(new Class[] { World.class })
                                                            .newInstance(world);
                                                        entity.setLocationAndAngles(
                                                            spawnX,
                                                            spawnY,
                                                            spawnZ,
                                                            world.rand.nextFloat() * 360.0F,
                                                            0.0F);

                                                        Result canSpawnResult = ForgeEventFactory
                                                            .canEntitySpawn(entity, world, spawnX, spawnY, spawnZ);
                                                        if (canSpawnResult == Result.ALLOW
                                                            || (canSpawnResult == Result.DEFAULT
                                                                && entity.getCanSpawnHere())) {
                                                            ++spawned;
                                                            world.spawnEntityInWorld(entity);

                                                            if (!ForgeEventFactory.doSpecialSpawn(
                                                                entity,
                                                                world,
                                                                spawnX,
                                                                spawnY,
                                                                spawnZ)) {
                                                                entity.onSpawnWithEgg(null);
                                                            }

                                                            int maxSpawnSize = ForgeEventFactory
                                                                .getMaxSpawnPackSize(entity);
                                                            if (maxSpawnSize < this.maxSpawnSize)
                                                                maxSpawnSize = this.maxSpawnSize;
                                                            if (spawned >= maxSpawnSize) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } catch (ReflectiveOperationException ex) {
                                                    ex.printStackTrace();
                                                    throw new RuntimeException("Error during entity spawn", ex);
                                                }
                                            }
                                        }
                                    }

                                    ++attemptCount;
                                }

                                // 如果成功生成了生物，则增加生成计数
                                spawnCount += spawned;
                            }
                        }
                    }
                }
            }

            return spawnCount;
        }
    }

    /**
     * Returns whether or not the specified creature type can spawn at the specified location.
     */
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, int x, int y,
        int z) {
        Material creatureMaterial = creatureType.getCreatureMaterial();
        Block blockAtCurrentPosition = world.getBlock(x, y, z);
        Block blockBelow = world.getBlock(x, y - 1, z);
        Block blockAbove = world.getBlock(x, y + 1, z);

        if (creatureMaterial == Material.water) {
            return blockAtCurrentPosition.getMaterial()
                .isLiquid()
                && blockBelow.getMaterial()
                    .isLiquid()
                && !blockAbove.isNormalCube();
        }

        if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
            return false;
        }

        return blockBelow.canCreatureSpawn(creatureType, world, x, y - 1, z) && blockBelow != Blocks.bedrock
            && !blockAtCurrentPosition.isNormalCube()
            && !blockAtCurrentPosition.getMaterial()
                .isLiquid()
            && !blockAbove.isNormalCube();
    }
}
