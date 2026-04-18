package com.hfstudio.bqapi.api.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;

public final class ItemRewardDefinition extends RewardDefinition {

    private final List<NBTTagCompound> items;

    public ItemRewardDefinition(String id, int slot, List<NBTTagCompound> items) {
        super(id, slot);
        Objects.requireNonNull(items, "items");
        List<NBTTagCompound> copies = new ArrayList<>(items.size());
        for (NBTTagCompound item : items) {
            copies.add(
                (NBTTagCompound) Objects.requireNonNull(item, "item")
                    .copy());
        }
        this.items = Collections.unmodifiableList(copies);
    }

    public List<NBTTagCompound> getItems() {
        List<NBTTagCompound> copies = new ArrayList<>(items.size());
        for (NBTTagCompound item : items) {
            copies.add((NBTTagCompound) item.copy());
        }
        return Collections.unmodifiableList(copies);
    }
}
