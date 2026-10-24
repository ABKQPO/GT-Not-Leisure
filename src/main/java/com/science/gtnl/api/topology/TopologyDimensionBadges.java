package com.science.gtnl.api.topology;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;

/**
 * Emblem block for a dimension, used to badge a link whose far end lives somewhere else.
 * <p>
 * Resolution is server side, because only there can a dimension actually be inspected. Explicit registrations win, so
 * an addon can give its own dimension a proper emblem; otherwise the biome at the far end supplies its surface block,
 * which already gives the right answer for most generated dimensions.
 */
public class TopologyDimensionBadges {

    private static final Map<Integer, ItemStack> OVERRIDES = new HashMap<>();
    private static final Map<Integer, ItemStack> RESOLVED = new HashMap<>();

    private TopologyDimensionBadges() {}

    public static synchronized void registerDefaults() {
        register(0, Blocks.grass);
        register(-1, Blocks.netherrack);
        register(1, Blocks.end_stone);
    }

    public static synchronized void register(int dimension, Block emblem) {
        register(dimension, emblem == null ? null : new ItemStack(emblem));
    }

    public static synchronized void register(int dimension, ItemStack emblem) {
        if (emblem == null || emblem.getItem() == null) OVERRIDES.remove(dimension);
        else OVERRIDES.put(dimension, strip(emblem));
        RESOLVED.remove(dimension);
    }

    /**
     * @param coord a position inside the dimension that is known to be loaded, used to sample its surface block.
     * @return the emblem for that dimension, or null when nothing sensible could be determined.
     */
    public static synchronized ItemStack resolve(GridCoord coord) {
        if (coord == null) return null;
        int dimension = coord.dimension();
        ItemStack override = OVERRIDES.get(dimension);
        if (override != null) return override;
        ItemStack cached = RESOLVED.get(dimension);
        if (cached != null) return cached;
        // A miss is never cached: the far side may simply not be loaded yet, and the next scan should try again.
        ItemStack sampled = sample(coord);
        if (sampled != null) RESOLVED.put(dimension, sampled);
        return sampled;
    }

    private static ItemStack sample(GridCoord coord) {
        World world = DimensionManager.getWorld(coord.dimension());
        if (world == null || !world.blockExists(coord.x(), coord.y(), coord.z())) return null;
        BiomeGenBase biome = world.getBiomeGenForCoords(coord.x(), coord.z());
        Block surface = biome == null ? null : biome.topBlock;
        return surface == null || Item.getItemFromBlock(surface) == null ? null : new ItemStack(surface);
    }

    private static ItemStack strip(ItemStack stack) {
        return new ItemStack(stack.getItem(), 1, stack.getItemDamage());
    }
}
