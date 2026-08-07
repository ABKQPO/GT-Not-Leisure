package com.science.gtnl.utils.machine.greenHouseManager;

import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.util.GTUtility;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GreenHouseStoredCrop {

    private static final String NBT_SEED = "seed";
    private static final String NBT_BLOCK_UNDER = "blockUnder";

    private ItemStack seedStack;
    private ItemStack blockUnderStack;

    public GreenHouseStoredCrop(ItemStack seedStack, @Nullable ItemStack blockUnderStack) {
        this.seedStack = seedStack;
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
            tag.setTag(NBT_SEED, ItemUtils.writeItemStackToNBT(seedStack));
        }
        if (CropsNHUtils.isStackValid(blockUnderStack)) {
            tag.setTag(NBT_BLOCK_UNDER, ItemUtils.writeItemStackToNBT(blockUnderStack));
        }
        return tag;
    }

    public static GreenHouseStoredCrop load(NBTTagCompound tag) {
        ItemStack seed = readStoredStack(tag, NBT_SEED);
        ItemStack blockUnder = readStoredStack(tag, NBT_BLOCK_UNDER);
        return new GreenHouseStoredCrop(seed, blockUnder);
    }

    private static ItemStack readStoredStack(NBTTagCompound tag, String key) {
        if (!tag.hasKey(key, TAG_COMPOUND)) return null;

        NBTTagCompound stackTag = tag.getCompoundTag(key);
        ItemStack stack = ItemUtils.readItemStackFromNBT(stackTag);
        if (stack != null && !stackTag.hasKey("IntCount") && stack.stackSize <= 0) {
            int legacyStackSize = Byte.toUnsignedInt((byte) stack.stackSize);
            stack.stackSize = legacyStackSize == 0 ? 256 : legacyStackSize;
        }
        return stack;
    }
}
