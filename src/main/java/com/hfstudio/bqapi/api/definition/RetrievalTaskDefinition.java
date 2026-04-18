package com.hfstudio.bqapi.api.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;

public class RetrievalTaskDefinition extends TaskDefinition {

    private final List<NBTTagCompound> items;
    private final boolean ignoreNbt;
    private final boolean partialMatch;
    private final boolean consume;
    private final boolean groupDetect;
    private final boolean autoConsume;

    public RetrievalTaskDefinition(String id, int slot, List<NBTTagCompound> items, boolean ignoreNbt,
        boolean partialMatch, boolean consume, boolean groupDetect, boolean autoConsume) {
        super(id, slot);
        Objects.requireNonNull(items, "items");
        List<NBTTagCompound> copies = new ArrayList<>(items.size());
        for (NBTTagCompound item : items) {
            copies.add(
                (NBTTagCompound) Objects.requireNonNull(item, "item")
                    .copy());
        }
        this.items = Collections.unmodifiableList(copies);
        this.ignoreNbt = ignoreNbt;
        this.partialMatch = partialMatch;
        this.consume = consume;
        this.groupDetect = groupDetect;
        this.autoConsume = autoConsume;
    }

    public List<NBTTagCompound> getItems() {
        List<NBTTagCompound> copies = new ArrayList<>(items.size());
        for (NBTTagCompound item : items) {
            copies.add((NBTTagCompound) item.copy());
        }
        return Collections.unmodifiableList(copies);
    }

    public boolean isIgnoreNbt() {
        return ignoreNbt;
    }

    public boolean isPartialMatch() {
        return partialMatch;
    }

    public boolean isConsume() {
        return consume;
    }

    public boolean isGroupDetect() {
        return groupDetect;
    }

    public boolean isAutoConsume() {
        return autoConsume;
    }
}
