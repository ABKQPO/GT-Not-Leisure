package com.hfstudio.bqapi.api.builder.reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.builder.RewardBuilder;
import com.hfstudio.bqapi.api.definition.ItemRewardDefinition;

import betterquesting.api.utils.BigItemStack;

public final class ItemRewardBuilder extends RewardBuilder<ItemRewardDefinition, ItemRewardBuilder> {

    private final List<NBTTagCompound> items = new ArrayList<>();

    public ItemRewardBuilder(String id) {
        super(id);
    }

    public ItemRewardBuilder item(ItemStack stack) {
        this.items.add(
            Objects.requireNonNull(stack, "stack")
                .writeToNBT(new NBTTagCompound()));
        return this;
    }

    public ItemRewardBuilder item(String itemId, int count, int damage) {
        this.items.add(com.hfstudio.bqapi.api.builder.SerializedItemNbt.of(itemId, count, damage));
        return this;
    }

    public ItemRewardBuilder item(BigItemStack stack) {
        this.items.add(
            Objects.requireNonNull(stack, "stack")
                .writeToNBT(new NBTTagCompound()));
        return this;
    }

    @Override
    protected ItemRewardBuilder self() {
        return this;
    }

    @Override
    public ItemRewardDefinition build() {
        return new ItemRewardDefinition(id, slot, items);
    }
}
