package com.science.gtnl.utils.machine.greenHouseManager.buckets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockVine;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucketFactory;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseDropTable;

public class GreenHouseFlowerBucket extends GreenHouseBucket {

    public static final GreenHouseBucketFactory factory = new Factory();
    public static final String NBT_IDENTIFIER = "FLOWER";
    public static final int REVISION_NUMBER = 1;

    public static class Factory implements GreenHouseBucketFactory {

        @Override
        public String getNBTIdentifier() {
            return NBT_IDENTIFIER;
        }

        @Override
        public GreenHouseBucket tryCreateBucket(IGreenHouse greenhouse, ItemStack input) {
            Item item = input.getItem();
            Block block = Block.getBlockFromItem(item);
            int meta = input.getItemDamage();

            boolean isValidPlant = item == Items.reeds || (item == Items.dye && meta == 3)
                || block == Blocks.cactus
                || block instanceof BlockFlower
                || block instanceof BlockMushroom
                || block instanceof BlockDoublePlant
                || block instanceof BlockVine;

            if (!isValidPlant) return null;

            return new GreenHouseFlowerBucket(input);
        }

        @Override
        public GreenHouseBucket restore(NBTTagCompound nbt) {
            return new GreenHouseFlowerBucket(nbt);
        }
    }

    private GreenHouseFlowerBucket(ItemStack input) {
        super(input, 1, null);
    }

    private GreenHouseFlowerBucket(NBTTagCompound nbt) {
        super(nbt);
    }

    @Override
    public NBTTagCompound save() {
        NBTTagCompound nbt = super.save();
        nbt.setInteger("version", REVISION_NUMBER);
        return nbt;
    }

    @Override
    public String getNBTIdentifier() {
        return NBT_IDENTIFIER;
    }

    @Override
    public void addProgress(double multiplier, GreenHouseDropTable tracker) {
        int dropCount = seedCount;
        Block block = Block.getBlockFromItem(seed.getItem());
        if (block instanceof BlockDoublePlant) dropCount *= 2;

        tracker.addDrop(seed, dropCount * multiplier);
    }

    @Override
    public boolean revalidate(IGreenHouse greenhouse) {
        return this.isValid();
    }
}
