package com.science.gtnl.utils.machine.greenHouseManager.buckets;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseDropTable;
import com.science.gtnl.utils.machine.greenHouseManager.IGreenHouseBucketFactory;

import tb.common.block.BlockRainbowCactus;
import tb.init.TBBlocks;

public class GreenHouseRainbowCactusBucket extends GreenHouseBucket {

    private static final Random RANDOM = new Random();
    private static final String NBT_IDENTIFIER = "TB:RAINCACTI";
    private static final ArrayList<ItemStack> TEMP_DROPS = new ArrayList<>();

    public static final IGreenHouseBucketFactory factory = new Factory();

    public static class Factory implements IGreenHouseBucketFactory {

        @Override
        public String getNBTIdentifier() {
            return NBT_IDENTIFIER;
        }

        @Override
        public GreenHouseBucket tryCreateBucket(IGreenHouse greenhouse, ItemStack input) {
            Block block = Block.getBlockFromItem(input.getItem());
            return (block instanceof BlockRainbowCactus) ? new GreenHouseRainbowCactusBucket(input, 1) : null;
        }

        @Override
        public GreenHouseBucket restore(NBTTagCompound nbt) {
            return new GreenHouseRainbowCactusBucket(nbt);
        }
    }

    public GreenHouseRainbowCactusBucket(ItemStack seed, int seedCount) {
        super(seed, seedCount, null);
    }

    public GreenHouseRainbowCactusBucket(NBTTagCompound nbt) {
        super(nbt);
    }

    @Override
    public boolean revalidate(IGreenHouse greenhouse) {
        return this.isValid();
    }

    @Override
    public String getNBTIdentifier() {
        return NBT_IDENTIFIER;
    }

    @Override
    public void addProgress(double multiplier, GreenHouseDropTable tracker) {
        if (!this.isValid()) return;

        TEMP_DROPS.clear();
        ((BlockRainbowCactus) TBBlocks.rainbowCactus).addDyeDropsToOutput(RANDOM, TEMP_DROPS);

        for (ItemStack drop : TEMP_DROPS) {
            if (drop != null && drop.stackSize > 0) {
                tracker.addDrop(drop, drop.stackSize * multiplier * this.seedCount);
            }
        }
    }
}
