package com.science.gtnl.common.yogsothoth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public final class YogRecord {

    public final UUID id;
    UUID owner;
    int generation;
    long revision;
    // Bumped when entries are added or removed; within one structure, clients can be sent only changed entries.
    long structure;
    static long modifications;
    final List<ItemStack> terminals = new ArrayList<>();
    // Revision at which each entry last changed, parallel to terminals.
    private final List<Long> stamps = new ArrayList<>();
    final Set<String> batches = new HashSet<>();
    int active = -1;
    boolean hidden;
    int home = -1;
    UUID holder;
    // The Yog-Sothoth stack a disguise replaced, kept so reverting restores the same object screens hold.
    transient ItemStack base;
    private final List<ItemStack> snapshot = new ArrayList<>();

    YogRecord(UUID id, UUID owner) {
        this.id = id;
        this.owner = owner;
    }

    public int generation() {
        return generation;
    }

    public long revision() {
        return revision;
    }

    public int active() {
        return active;
    }

    public int size() {
        return terminals.size();
    }

    public ItemStack get(int index) {
        return index < 0 || index >= terminals.size() ? null : terminals.get(index);
    }

    boolean contains(ItemStack stack) {
        for (ItemStack terminal : terminals) if (terminal == stack) return true;
        return false;
    }

    void set(int index, ItemStack stack) {
        if (index >= terminals.size()) {
            add(stack);
            return;
        }
        terminals.set(index, stack);
        snapshot.set(index, stack.copy());
        touched(index);
    }

    int add(ItemStack stack) {
        terminals.add(stack);
        snapshot.add(stack.copy());
        stamps.add(0L);
        structure++;
        touched(terminals.size() - 1);
        return terminals.size() - 1;
    }

    ItemStack remove(int index) {
        ItemStack stack = get(index);
        if (stack == null) return null;
        terminals.remove(index);
        snapshot.remove(index);
        stamps.remove(index);
        if (active == index) clearActive();
        else if (active > index) {
            active--;
            YogToken.relink(terminals.get(active), active);
        }
        structure++;
        changed();
        return stack;
    }

    private void touched(int index) {
        changed();
        stamps.set(index, revision);
    }

    void clearActive() {
        active = -1;
        base = null;
        home = -1;
        holder = null;
    }

    void changed() {
        revision++;
        modifications++;
        YogTerminalStorage.markChanged();
    }

    void detectChanges() {
        for (int index = 0; index < terminals.size(); index++) {
            ItemStack stack = terminals.get(index);
            if (ItemStack.areItemStacksEqual(stack, snapshot.get(index))) continue;
            snapshot.set(index, stack.copy());
            touched(index);
        }
    }

    NBTTagList writeChangedSince(long since) {
        NBTTagList list = new NBTTagList();
        for (int index = 0; index < terminals.size(); index++) {
            if (stamps.get(index) <= since) continue;
            NBTTagCompound entry = YogToken.unlinkedCopy(terminals.get(index))
                .writeToNBT(new NBTTagCompound());
            entry.setInteger("slot", index);
            list.appendTag(entry);
        }
        return list;
    }

    NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", id.toString());
        if (owner != null) tag.setString("owner", owner.toString());
        tag.setInteger("gen", generation);
        tag.setTag("terminals", writeTerminals());
        NBTTagList appliedBatches = new NBTTagList();
        for (String batch : batches) appliedBatches.appendTag(new NBTTagString(batch));
        tag.setTag("batches", appliedBatches);
        tag.setInteger("active", active);
        tag.setBoolean("hidden", hidden);
        tag.setInteger("home", home);
        if (holder != null) tag.setString("holder", holder.toString());
        return tag;
    }

    NBTTagList writeTerminals() {
        NBTTagList list = new NBTTagList();
        for (ItemStack stack : terminals) list.appendTag(
            YogToken.unlinkedCopy(stack)
                .writeToNBT(new NBTTagCompound()));
        return list;
    }

    static YogRecord read(NBTTagCompound tag) {
        UUID id = UUID.fromString(tag.getString("id"));
        UUID owner = tag.hasKey("owner") ? UUID.fromString(tag.getString("owner")) : null;
        YogRecord record = new YogRecord(id, owner);
        record.generation = tag.getInteger("gen");
        readTerminals(tag.getTagList("terminals", NBT.TAG_COMPOUND), record.terminals);
        for (ItemStack stack : record.terminals) {
            record.snapshot.add(stack.copy());
            record.stamps.add(0L);
        }
        NBTTagList appliedBatches = tag.getTagList("batches", NBT.TAG_STRING);
        for (int index = 0; index < appliedBatches.tagCount(); index++)
            record.batches.add(appliedBatches.getStringTagAt(index));
        record.active = tag.getInteger("active");
        record.hidden = tag.getBoolean("hidden");
        record.home = tag.getInteger("home");
        record.holder = tag.hasKey("holder") ? UUID.fromString(tag.getString("holder")) : null;
        if (record.get(record.active) == null) record.clearActive();
        return record;
    }

    public static void readTerminals(NBTTagList list, List<ItemStack> into) {
        into.clear();
        for (int index = 0; index < list.tagCount(); index++) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(index));
            if (stack != null) into.add(stack);
        }
    }
}
