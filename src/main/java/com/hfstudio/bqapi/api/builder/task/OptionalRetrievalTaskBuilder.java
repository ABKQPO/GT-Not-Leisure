package com.hfstudio.bqapi.api.builder.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.builder.TaskBuilder;
import com.hfstudio.bqapi.api.definition.OptionalRetrievalTaskDefinition;

import betterquesting.api.utils.BigItemStack;

public final class OptionalRetrievalTaskBuilder
    extends TaskBuilder<OptionalRetrievalTaskDefinition, OptionalRetrievalTaskBuilder> {

    private final List<NBTTagCompound> items = new ArrayList<>();
    private boolean ignoreNbt = true;
    private boolean partialMatch = true;
    private boolean consume = false;
    private boolean groupDetect = false;
    private boolean autoConsume = false;

    public OptionalRetrievalTaskBuilder(String id) {
        super(id);
    }

    public OptionalRetrievalTaskBuilder item(ItemStack stack) {
        this.items.add(
            Objects.requireNonNull(stack, "stack")
                .writeToNBT(new NBTTagCompound()));
        return this;
    }

    public OptionalRetrievalTaskBuilder item(String itemId, int count, int damage) {
        this.items.add(com.hfstudio.bqapi.api.builder.SerializedItemNbt.of(itemId, count, damage));
        return this;
    }

    public OptionalRetrievalTaskBuilder item(BigItemStack stack) {
        this.items.add(
            Objects.requireNonNull(stack, "stack")
                .writeToNBT(new NBTTagCompound()));
        return this;
    }

    public OptionalRetrievalTaskBuilder ignoreNbt(boolean ignoreNbt) {
        this.ignoreNbt = ignoreNbt;
        return this;
    }

    public OptionalRetrievalTaskBuilder partialMatch(boolean partialMatch) {
        this.partialMatch = partialMatch;
        return this;
    }

    public OptionalRetrievalTaskBuilder consume(boolean consume) {
        this.consume = consume;
        return this;
    }

    public OptionalRetrievalTaskBuilder groupDetect(boolean groupDetect) {
        this.groupDetect = groupDetect;
        return this;
    }

    public OptionalRetrievalTaskBuilder autoConsume(boolean autoConsume) {
        this.autoConsume = autoConsume;
        return this;
    }

    @Override
    protected OptionalRetrievalTaskBuilder self() {
        return this;
    }

    @Override
    public OptionalRetrievalTaskDefinition build() {
        return new OptionalRetrievalTaskDefinition(
            id,
            slot,
            items,
            ignoreNbt,
            partialMatch,
            consume,
            groupDetect,
            autoConsume);
    }
}
