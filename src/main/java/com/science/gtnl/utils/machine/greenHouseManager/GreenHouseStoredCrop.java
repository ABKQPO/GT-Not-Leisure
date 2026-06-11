package com.science.gtnl.utils.machine.greenHouseManager;

import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;

import gregtech.api.util.GTUtility;

public class GreenHouseStoredCrop {

    private static final String NBT_SEED = "seed";
    private static final String NBT_BLOCK_UNDER = "blockUnder";

    private ItemStack seedStack;
    private ItemStack blockUnderStack;

    public GreenHouseStoredCrop(ItemStack seedStack, @Nullable ItemStack blockUnderStack) {
        this.seedStack = seedStack;
        this.blockUnderStack = blockUnderStack;
    }

    public ItemStack getSeedStack() {
        return seedStack;
    }

    public void setSeedStack(ItemStack seedStack) {
        this.seedStack = seedStack;
    }

    public ItemStack getBlockUnderStack() {
        return blockUnderStack;
    }

    public void setBlockUnderStack(ItemStack blockUnderStack) {
        this.blockUnderStack = blockUnderStack;
    }

    public int getSeedCount() {
        return CropsNHUtils.isStackValid(seedStack) ? seedStack.stackSize : 0;
    }

    public boolean hasBlockUnder() {
        return CropsNHUtils.isStackValid(blockUnderStack);
    }

    public boolean canStackSeeds(ItemStack stack) {
        return CropsNHUtils.isStackValid(seedStack) && GTUtility.areStacksEqual(seedStack, stack, false);
    }

    public boolean canStackBlockUnder(ItemStack stack) {
        if (!hasBlockUnder()) return false;
        return GTUtility.areStacksEqual(blockUnderStack, stack, false);
    }

    public boolean isValid() {
        ISeedData seedData = CropsNHUtils.getAnalyzedSeedData(seedStack);
        return seedData != null && getSeedCount() > 0;
    }

    public ItemStack removeSeeds(int amount) {
        if (!CropsNHUtils.isStackValid(seedStack) || amount <= 0) return null;
        int removed = Math.min(amount, seedStack.stackSize);
        ItemStack result = CropsNHUtils.copyStackWithSize(seedStack, removed);
        seedStack.stackSize -= removed;
        if (seedStack.stackSize <= 0) {
            seedStack = null;
        }
        return result;
    }

    public ItemStack removeBlockUnders(int amount) {
        if (!CropsNHUtils.isStackValid(blockUnderStack) || amount <= 0) return null;
        int removed = Math.min(amount, blockUnderStack.stackSize);
        ItemStack result = CropsNHUtils.copyStackWithSize(blockUnderStack, removed);
        blockUnderStack.stackSize -= removed;
        if (blockUnderStack.stackSize <= 0) {
            blockUnderStack = null;
        }
        return result;
    }

    public void clearIfEmpty() {
        if (seedStack != null && seedStack.stackSize <= 0) {
            seedStack = null;
        }
        if (blockUnderStack != null && blockUnderStack.stackSize <= 0) {
            blockUnderStack = null;
        }
    }

    public NBTTagCompound save() {
        NBTTagCompound tag = new NBTTagCompound();
        if (CropsNHUtils.isStackValid(seedStack)) {
            NBTTagCompound seedTag = new NBTTagCompound();
            seedStack.writeToNBT(seedTag);
            tag.setTag(NBT_SEED, seedTag);
        }
        if (CropsNHUtils.isStackValid(blockUnderStack)) {
            NBTTagCompound blockTag = new NBTTagCompound();
            blockUnderStack.writeToNBT(blockTag);
            tag.setTag(NBT_BLOCK_UNDER, blockTag);
        }
        return tag;
    }

    public static GreenHouseStoredCrop load(NBTTagCompound tag) {
        ItemStack seed = tag.hasKey(NBT_SEED, TAG_COMPOUND)
            ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag(NBT_SEED))
            : null;
        ItemStack blockUnder = tag.hasKey(NBT_BLOCK_UNDER, TAG_COMPOUND)
            ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag(NBT_BLOCK_UNDER))
            : null;
        return new GreenHouseStoredCrop(seed, blockUnder);
    }
}
