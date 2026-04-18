package com.hfstudio.bqapi.api.builder.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.builder.TaskBuilder;
import com.hfstudio.bqapi.api.definition.RetrievalTaskDefinition;

import betterquesting.api.utils.BigItemStack;

public final class RetrievalTaskBuilder extends TaskBuilder<RetrievalTaskDefinition, RetrievalTaskBuilder> {

    private final List<NBTTagCompound> items = new ArrayList<>();
    private boolean ignoreNbt = true;
    private boolean partialMatch = true;
    private boolean consume = false;
    private boolean groupDetect = false;
    private boolean autoConsume = false;

    public RetrievalTaskBuilder(String id) {
        super(id);
    }

    public RetrievalTaskBuilder item(ItemStack stack) {
        this.items.add(
            Objects.requireNonNull(stack, "stack")
                .writeToNBT(new NBTTagCompound()));
        return this;
    }

    public RetrievalTaskBuilder item(String itemId, int count, int damage) {
        this.items.add(com.hfstudio.bqapi.api.builder.SerializedItemNbt.of(itemId, count, damage));
        return this;
    }

    public RetrievalTaskBuilder item(BigItemStack stack) {
        this.items.add(
            Objects.requireNonNull(stack, "stack")
                .writeToNBT(new NBTTagCompound()));
        return this;
    }

    public RetrievalTaskBuilder ignoreNbt(boolean ignoreNbt) {
        this.ignoreNbt = ignoreNbt;
        return this;
    }

    public RetrievalTaskBuilder partialMatch(boolean partialMatch) {
        this.partialMatch = partialMatch;
        return this;
    }

    public RetrievalTaskBuilder consume(boolean consume) {
        this.consume = consume;
        return this;
    }

    public RetrievalTaskBuilder groupDetect(boolean groupDetect) {
        this.groupDetect = groupDetect;
        return this;
    }

    public RetrievalTaskBuilder autoConsume(boolean autoConsume) {
        this.autoConsume = autoConsume;
        return this;
    }

    @Override
    protected RetrievalTaskBuilder self() {
        return this;
    }

    @Override
    public RetrievalTaskDefinition build() {
        return new RetrievalTaskDefinition(id, slot, items, ignoreNbt, partialMatch, consume, groupDetect, autoConsume);
    }
}
