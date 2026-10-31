package com.science.gtnl.common.yogsothoth;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import com.science.gtnl.api.IYogTerminal;
import com.science.gtnl.common.item.items.ItemYogSothothTerminal;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;

public class YogToken {

    public static final String TAG = "gtnlYog";
    public static final String PENDING_TAG = "gtnlYogPending";
    public static final String LINK_TAG = "gtnlYogLink";
    public static final String NONCE_TAG = "gtnlYogNonce";

    public static boolean isYog(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemYogSothothTerminal;
    }

    public static boolean isStorable(ItemStack stack) {
        if (stack == null || stack.getItem() == null || stack.stackSize != 1 || isYog(stack) || link(stack) != null)
            return false;
        if (stack.getItem() instanceof IYogTerminal terminal) return terminal.canStoreInYog(stack);
        if (stack.getItem() instanceof IWirelessTermHandler) return true;
        return AEApi.instance()
            .registries()
            .wireless()
            .isWirelessTerminal(stack);
    }

    public static UUID id(ItemStack yog) {
        if (!isYog(yog) || yog.getTagCompound() == null) return null;
        return parse(
            yog.getTagCompound()
                .getCompoundTag(TAG)
                .getString("id"));
    }

    public static int generation(ItemStack yog) {
        if (!isYog(yog) || yog.getTagCompound() == null) return 0;
        return yog.getTagCompound()
            .getCompoundTag(TAG)
            .getInteger("gen");
    }

    public static void bind(ItemStack yog, UUID id, int generation) {
        if (yog.getTagCompound() == null) yog.setTagCompound(new NBTTagCompound());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", id.toString());
        tag.setInteger("gen", generation);
        yog.getTagCompound()
            .setTag(TAG, tag);
    }

    public static void unbind(ItemStack yog) {
        removeKey(yog, TAG);
    }

    public static int nonce(ItemStack yog) {
        return yog.getTagCompound() == null ? 0
            : yog.getTagCompound()
                .getInteger(NONCE_TAG);
    }

    static void bumpNonce(ItemStack yog) {
        if (yog.getTagCompound() == null) yog.setTagCompound(new NBTTagCompound());
        yog.getTagCompound()
            .setInteger(NONCE_TAG, nonce(yog) + 1);
    }

    public static NBTTagList pending(ItemStack yog) {
        if (!isYog(yog) || yog.getTagCompound() == null) return null;
        if (!yog.getTagCompound()
            .hasKey(PENDING_TAG, NBT.TAG_LIST)) return null;
        return yog.getTagCompound()
            .getTagList(PENDING_TAG, NBT.TAG_COMPOUND);
    }

    public static void clearPending(ItemStack yog) {
        removeKey(yog, PENDING_TAG);
    }

    public static void addPending(ItemStack yog, UUID batch, ItemStack... terminals) {
        if (yog.getTagCompound() == null) yog.setTagCompound(new NBTTagCompound());
        NBTTagList batches = yog.getTagCompound()
            .getTagList(PENDING_TAG, NBT.TAG_COMPOUND);
        NBTTagCompound entry = new NBTTagCompound();
        entry.setString("batch", batch.toString());
        NBTTagList items = new NBTTagList();
        for (ItemStack terminal : terminals) items.appendTag(terminal.writeToNBT(new NBTTagCompound()));
        entry.setTag("items", items);
        batches.appendTag(entry);
        yog.getTagCompound()
            .setTag(PENDING_TAG, batches);
    }

    public static int pendingCount(ItemStack yog) {
        NBTTagList batches = pending(yog);
        if (batches == null) return 0;
        int count = 0;
        for (int index = 0; index < batches.tagCount(); index++) count += batches.getCompoundTagAt(index)
            .getTagList("items", NBT.TAG_COMPOUND)
            .tagCount();
        return count;
    }

    public record Link(UUID id, int generation, int home, int index, NBTTagCompound base) {

        public ItemStack restoreBase() {
            return base == null ? null : ItemStack.loadItemStackFromNBT(base);
        }
    }

    public static Link link(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null
            || !stack.getTagCompound()
                .hasKey(LINK_TAG, NBT.TAG_COMPOUND))
            return null;
        NBTTagCompound tag = stack.getTagCompound()
            .getCompoundTag(LINK_TAG);
        UUID id = parse(tag.getString("id"));
        if (id == null) return null;
        return new Link(
            id,
            tag.getInteger("gen"),
            tag.getInteger("home"),
            tag.getInteger("index"),
            tag.hasKey("base", NBT.TAG_COMPOUND) ? tag.getCompoundTag("base") : null);
    }

    public static void attach(ItemStack stack, Link link) {
        if (stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(
            "id",
            link.id()
                .toString());
        tag.setInteger("gen", link.generation());
        tag.setInteger("home", link.home());
        tag.setInteger("index", link.index());
        if (link.base() != null) tag.setTag("base", link.base());
        stack.getTagCompound()
            .setTag(LINK_TAG, tag);
    }

    static void relink(ItemStack stack, int index) {
        if (link(stack) == null) return;
        stack.getTagCompound()
            .getCompoundTag(LINK_TAG)
            .setInteger("index", index);
    }

    public static void detach(ItemStack stack) {
        removeKey(stack, LINK_TAG);
    }

    public static UUID storageId(ItemStack stack) {
        UUID id = id(stack);
        if (id != null) return id;
        Link link = link(stack);
        return link == null ? null : link.id();
    }

    public static ItemStack unlinkedCopy(ItemStack stack) {
        if (stack == null) return null;
        ItemStack copy = stack.copy();
        detach(copy);
        return copy;
    }

    private static void removeKey(ItemStack stack, String key) {
        if (stack == null || stack.getTagCompound() == null) return;
        stack.getTagCompound()
            .removeTag(key);
        if (stack.getTagCompound()
            .hasNoTags()) stack.setTagCompound(null);
    }

    private static UUID parse(String id) {
        if (id == null || id.isEmpty()) return null;
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
