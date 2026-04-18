package com.hfstudio.bqapi.api.builder.reward;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.hfstudio.bqapi.api.builder.RewardBuilder;
import com.hfstudio.bqapi.api.definition.RawRewardDefinition;

public final class RawRewardBuilder extends RewardBuilder<RawRewardDefinition, RawRewardBuilder> {

    private ResourceLocation factoryId;
    private NBTTagCompound configNbt = new NBTTagCompound();

    public RawRewardBuilder(String id) {
        super(id);
    }

    public RawRewardBuilder factoryId(String factoryId) {
        this.factoryId = new ResourceLocation(Objects.requireNonNull(factoryId, "factoryId"));
        return this;
    }

    public RawRewardBuilder factoryId(ResourceLocation factoryId) {
        this.factoryId = Objects.requireNonNull(factoryId, "factoryId");
        return this;
    }

    public RawRewardBuilder configNbt(NBTTagCompound configNbt) {
        this.configNbt = (NBTTagCompound) Objects.requireNonNull(configNbt, "configNbt")
            .copy();
        return this;
    }

    @Override
    protected RawRewardBuilder self() {
        return this;
    }

    @Override
    public RawRewardDefinition build() {
        return new RawRewardDefinition(id, slot, Objects.requireNonNull(factoryId, "factoryId"), configNbt);
    }
}
