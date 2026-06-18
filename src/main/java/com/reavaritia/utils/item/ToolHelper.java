package com.reavaritia.utils.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.reavaritia.common.items.InfinityAxe;
import com.reavaritia.common.items.MatterCluster;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ToolHelper {

    public static Item.ToolMaterial BLAZE = EnumHelper.addToolMaterial("BLAZE", 32, 7777, 9999F, 33.0F, 22);
    public static Item.ToolMaterial CRYSTAL = EnumHelper.addToolMaterial("CRYSTAL", 32, 8888, 9999F, 7.0F, 22);
    public static Item.ToolMaterial INFINITY = EnumHelper.addToolMaterial("INFINITY", 32, 9999, 9999F, 98F, 200);
    public static Material[] MATERIALS = new Material[] { Material.rock, Material.iron, Material.ice, Material.glass,
        Material.piston, Material.anvil, Material.grass, Material.ground, Material.sand, Material.snow,
        Material.craftedSnow, Material.clay };

    public static Set<EntityPlayer> hammering = new HashSet<>();
    public static Map<EntityPlayer, List<ItemStack>> hammerdrops = new WeakHashMap<>();

    public static void generateMatterCluster(World world, EntityPlayer player, Map<ItemStackWrapper, Integer> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        Object2IntOpenHashMap<ItemStackWrapper> clusterContents = new Object2IntOpenHashMap<>();
        items.forEach((key, count) -> {
            if (key == null || key.stack() == null || count == null || count <= 0) {
                return;
            }
            clusterContents.put(key, clusterContents.getInt(key) + count);
        });

        for (ItemStack cluster : MatterCluster.makeReAvaritiaClusters(clusterContents)) {
            EntityItem entity = new EntityItem(world, player.posX, player.posY + 0.5, player.posZ, cluster);
            entity.delayBeforeCanPickup = 0;
            world.spawnEntityInWorld(entity);
        }
    }

    public static void removeBlocksInIteration(EntityPlayer player, ItemStack stack, World world, int x, int y, int z,
        int xs, int ys, int zs, int xe, int ye, int ze, Block block, Material[] materialsListing, boolean silk,
        int fortune, boolean dispose) {
        float blockHardness = block == null ? 1F : block.getBlockHardness(world, x, y, z);

        if (!hammerdrops.containsKey(player) || hammerdrops.get(player) == null) {
            hammerdrops.put(player, new ArrayList<>());
        }

        if (!hammering.contains(player)) {
            hammering.add(player);
        }

        for (int x1 = xs; x1 < xe; x1++)
            for (int y1 = ys; y1 < ye; y1++) for (int z1 = zs; z1 < ze; z1++) removeBlockWithDrops(
                player,
                stack,
                world,
                x1 + x,
                y1 + y,
                z1 + z,
                block,
                materialsListing,
                silk,
                fortune,
                blockHardness,
                dispose);

        int meta = world.getBlockMetadata(x, y, z);
        if (!world.isRemote) world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));

        if (hammering.contains(player)) {
            hammering.remove(player);
        }

        if (!world.isRemote) {
            List<ItemStack> clusters = MatterCluster.makeReAvaritiaClusters(hammerdrops.get(player));

            for (ItemStack cluster : clusters) {
                EntityItem ent = new EntityItem(world, x, y, z, cluster);
                world.spawnEntityInWorld(ent);
            }
        }

        hammerdrops.put(player, null);
    }

    public static boolean isRightMaterial(Material material, Material[] materialsListing) {
        for (Material mat : materialsListing) if (material == mat) return true;

        return false;
    }

    public static void removeBlockWithDrops(EntityPlayer player, ItemStack stack, World world, int x, int y, int z,
        Block block, Material[] materialsListing, boolean silk, int fortune, float blockHardness, boolean dispose) {
        if (!world.blockExists(x, y, z)) return;

        Block blk = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (block != null && blk != block) return;

        Material mat = world.getBlock(x, y, z)
            .getMaterial();
        if (!world.isRemote && blk != null && !blk.isAir(world, x, y, z)) {
            if (blk == Blocks.grass && stack.getItem() instanceof InfinityAxe) world.setBlock(x, y, z, Blocks.dirt);
            if (!blk.canHarvestBlock(player, meta) || !isRightMaterial(mat, materialsListing)) return;

            if (!player.capabilities.isCreativeMode) {
                int localMeta = world.getBlockMetadata(x, y, z);
                blk.onBlockHarvested(world, x, y, z, localMeta, player);

                if (blk.removedByPlayer(world, player, x, y, z, true)) {
                    blk.onBlockDestroyedByPlayer(world, x, y, z, localMeta);

                    if (!dispose) {
                        if (blk.getPlayerRelativeBlockHardness(player, world, x, y, z) < 0
                            && blk.quantityDropped(world.rand) == 0) {
                            ItemStack drop = blk
                                .getPickBlock(raytraceFromEntity(world, player, true, 10), world, x, y, z, player);
                            if (drop == null) drop = new ItemStack(blk, 1, meta);
                            dropItem(drop, world, x, y, z);
                        }
                        blk.harvestBlock(world, player, x, y, z, localMeta);
                    }
                }

            } else world.setBlockToAir(x, y, z);
        }
    }

    public static MovingObjectPosition raytraceFromEntity(World world, Entity player, boolean wut, double range) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * f;
        if (!world.isRemote && player instanceof EntityPlayer) d1 += 1.62D;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        Vec3 vec31 = vec3.addVector(f7 * range, f6 * range, f8 * range);
        return world.rayTraceBlocks(vec3, vec31, wut);
    }

    public static void dropItem(ItemStack drop, World world, double x, double y, double z) {
        float f = 0.7F;
        double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, drop);
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
    }

    public static List<ItemStack> collateMatterClusterContents(Object2IntOpenHashMap<ItemStackWrapper> input) {
        List<ItemStack> collated = new ObjectArrayList<>();
        if (input == null) return collated;

        for (Entry<ItemStackWrapper, Integer> e : input.object2IntEntrySet()) {
            int count = e.getValue();
            ItemStackWrapper wrap = e.getKey();

            int size = wrap.stack()
                .getMaxStackSize();
            int fullStacks = count / size;

            for (int i = 0; i < fullStacks; i++) {
                count -= size;
                ItemStack stack = wrap.stack()
                    .copy();
                stack.stackSize = size;
                collated.add(stack);
            }

            if (count > 0) {
                ItemStack stack = wrap.stack()
                    .copy();
                stack.stackSize = count;
                collated.add(stack);
            }
        }

        return collated;
    }

    public static Object2IntOpenHashMap<ItemStackWrapper> collateMatterCluster(List<ItemStack> input) {
        Object2IntOpenHashMap<ItemStackWrapper> counts = new Object2IntOpenHashMap<>();

        if (input != null) {
            for (ItemStack stack : input) {
                ItemStackWrapper wrap = new ItemStackWrapper(stack);
                if (!counts.containsKey(wrap)) {
                    counts.put(wrap, 0);
                }

                counts.put(wrap, counts.get(wrap) + stack.stackSize);
            }
        }

        return counts;
    }
}
