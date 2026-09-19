package com.science.gtnl.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.ScienceNotLeisure;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/**
 * Original Author: Mekanism
 * License: MIT
 * Original Code Date: 2014-01-13
 */
public class CardboardBoxUtils {

    public static final int MAX_INLINE_DATA_SIZE = 16384;

    public static final String BLOCK_DATA_KEY = "blockData";
    public static final String TILE_TAG_KEY = "tileTag";
    public static final String TILE_ENTITY_ID_KEY = "tileEntityId";
    public static final String STORE_UUID_KEY = "storeUUID";

    private static final String STORE_DIRECTORY = "data";
    private static final String STORE_FILE_PREFIX = "CardboardBox_";
    private static final String STORE_FILE_SUFFIX = ".dat";

    public static Set<BlockInfo> CARDBOARD_BOX_IGNORE = new ObjectOpenHashSet<>();

    public static boolean isBlockCompatible(Item item, int meta) {
        for (BlockInfo i : CARDBOARD_BOX_IGNORE) {
            if (i.block == Block.getBlockFromItem(item) && (i.meta == OreDictionary.WILDCARD_VALUE || i.meta == meta)) {
                return false;
            }
        }

        return true;
    }

    public static void addBoxBlacklist(Block block, int meta) {
        CARDBOARD_BOX_IGNORE.add(new BlockInfo(block, meta));
    }

    public static void addBoxBlacklist(Item item, int meta) {
        if (item instanceof ItemBlock itemBlock) {
            Block block = itemBlock.field_150939_a;
            addBoxBlacklist(block, meta);
        }
    }

    public static void addBoxBlacklist(ItemStack stack) {
        if (stack == null) return;
        Item item = stack.getItem();
        if (item instanceof ItemBlock itemBlock) {
            Block block = itemBlock.field_150939_a;
            int meta = stack.getItemDamage();
            addBoxBlacklist(block, meta);
        }
    }

    public static void removeBoxBlacklist(Block block, int meta) {
        CARDBOARD_BOX_IGNORE.remove(new BlockInfo(block, meta));
    }

    public static Set<BlockInfo> getBoxIgnore() {
        return CARDBOARD_BOX_IGNORE;
    }

    public static void setBlockData(ItemStack itemstack, BlockData data) {
        if (itemstack.stackTagCompound == null) {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        itemstack.stackTagCompound.setTag(BLOCK_DATA_KEY, createBlockDataTag(data, true));
    }

    public static BlockData getBlockData(ItemStack itemstack) {
        if (itemstack.stackTagCompound == null || !itemstack.stackTagCompound.hasKey(BLOCK_DATA_KEY)) {
            return null;
        }

        return CardboardBoxUtils.BlockData.read(itemstack.stackTagCompound.getCompoundTag(BLOCK_DATA_KEY));
    }

    public static void discardStoredData(ItemStack itemstack) {
        if (itemstack.stackTagCompound == null || !itemstack.stackTagCompound.hasKey(BLOCK_DATA_KEY)) {
            return;
        }

        String uuid = itemstack.stackTagCompound.getCompoundTag(BLOCK_DATA_KEY)
            .getString(STORE_UUID_KEY);
        if (!uuid.isEmpty()) {
            deleteStoreFile(uuid);
        }
    }

    public static NBTTagCompound createBlockDataTag(BlockData data, boolean allowStoreFile) {
        NBTTagCompound dataTag = data.write(new NBTTagCompound());
        if (data.tileTag == null || getSerializedSize(dataTag) <= MAX_INLINE_DATA_SIZE) {
            return dataTag;
        }

        // the tile entity data does not fit into a packet, so it either moves into a file or is dropped
        String uuid = allowStoreFile ? UUID.randomUUID()
            .toString() : null;
        if (uuid != null && writeStoreFile(uuid, dataTag)) {
            dataTag.setString(STORE_UUID_KEY, uuid);
        }

        dataTag.removeTag(TILE_TAG_KEY);
        return dataTag;
    }

    private static int getSerializedSize(NBTTagCompound tag) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            CompressedStreamTools.write(tag, new DataOutputStream(buffer));
        } catch (IOException e) {
            ScienceNotLeisure.LOG.warn("Failed to measure cardboard box data", e);
            return 0;
        }

        return buffer.size();
    }

    private static File getStoreFile(String uuid) {
        File worldDirectory = DimensionManager.getCurrentSaveRootDirectory();
        if (worldDirectory == null) return null;

        return new File(new File(worldDirectory, STORE_DIRECTORY), STORE_FILE_PREFIX + uuid + STORE_FILE_SUFFIX);
    }

    private static boolean writeStoreFile(String uuid, NBTTagCompound tag) {
        File storeFile = getStoreFile(uuid);
        if (storeFile == null) return false;

        File storeDirectory = storeFile.getParentFile();
        if (storeDirectory != null && !storeDirectory.exists() && !storeDirectory.mkdirs()) return false;

        try {
            CompressedStreamTools.safeWrite(tag, storeFile);
            return true;
        } catch (IOException e) {
            ScienceNotLeisure.LOG.warn("Failed to write cardboard box data into " + storeFile, e);
            return false;
        }
    }

    private static NBTTagCompound readStoreFile(String uuid) {
        File storeFile = getStoreFile(uuid);
        if (storeFile == null || !storeFile.exists()) return null;

        try {
            return CompressedStreamTools.read(storeFile);
        } catch (IOException e) {
            ScienceNotLeisure.LOG.warn("Failed to read cardboard box data from " + storeFile, e);
            return null;
        }
    }

    private static void deleteStoreFile(String uuid) {
        File storeFile = getStoreFile(uuid);
        if (storeFile == null || !storeFile.exists()) return;

        if (!storeFile.delete()) {
            ScienceNotLeisure.LOG.warn("Failed to delete cardboard box data file " + storeFile);
        }
    }

    public static class BlockInfo {

        public Block block;
        public int meta;

        public BlockInfo(Block b, int j) {
            block = b;
            meta = j;
        }

        public static BlockInfo get(ItemStack stack) {
            return new BlockInfo(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BlockInfo && ((BlockInfo) obj).block == block && ((BlockInfo) obj).meta == meta;
        }

        @Override
        public int hashCode() {
            int code = 1;
            code = 31 * code + block.getUnlocalizedName()
                .hashCode();
            code = 31 * code + meta;
            return code;
        }
    }

    public static class BlockData {

        public Block block;
        public int meta;
        public int metaSpecial = -1;
        public NBTTagCompound tileTag;
        public String tileEntityId;

        public BlockData(Block block, int meta, int metaSpecial, NBTTagCompound nbtTags) {
            this.block = block;
            this.meta = meta;
            this.metaSpecial = metaSpecial;
            tileTag = nbtTags;
        }

        public BlockData() {}

        public void updateLocation(int x, int y, int z) {
            if (tileTag != null) {
                tileTag.setInteger("x", x);
                tileTag.setInteger("y", y);
                tileTag.setInteger("z", z);
            }
        }

        public String getTileEntityId() {
            if (tileEntityId != null) return tileEntityId;
            return tileTag == null ? null : tileTag.getString("id");
        }

        public NBTTagCompound write(NBTTagCompound nbtTags) {
            nbtTags.setInteger("id", Block.getIdFromBlock(block));
            nbtTags.setInteger("meta", meta);
            nbtTags.setInteger("metaSpecial", metaSpecial);

            String tileEntityId = getTileEntityId();
            if (tileEntityId != null && !tileEntityId.isEmpty()) {
                nbtTags.setString(TILE_ENTITY_ID_KEY, tileEntityId);
            }

            if (tileTag != null) {
                nbtTags.setTag(TILE_TAG_KEY, tileTag);
            }

            return nbtTags;
        }

        public static BlockData read(NBTTagCompound nbtTags) {
            BlockData data = new BlockData();

            data.block = Block.getBlockById(nbtTags.getInteger("id"));
            data.meta = nbtTags.getInteger("meta");
            data.metaSpecial = nbtTags.getInteger("metaSpecial");

            if (nbtTags.hasKey(TILE_ENTITY_ID_KEY)) {
                data.tileEntityId = nbtTags.getString(TILE_ENTITY_ID_KEY);
            }

            if (nbtTags.hasKey(TILE_TAG_KEY)) {
                data.tileTag = nbtTags.getCompoundTag(TILE_TAG_KEY);
            }

            if (nbtTags.hasKey(STORE_UUID_KEY)) {
                NBTTagCompound storeTag = readStoreFile(nbtTags.getString(STORE_UUID_KEY));
                if (storeTag != null && storeTag.hasKey(TILE_TAG_KEY)) {
                    data.tileTag = storeTag.getCompoundTag(TILE_TAG_KEY);
                }
            }

            return data;
        }
    }
}
