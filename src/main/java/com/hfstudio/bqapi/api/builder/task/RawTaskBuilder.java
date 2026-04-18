package com.hfstudio.bqapi.api.builder.task;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.hfstudio.bqapi.api.builder.TaskBuilder;
import com.hfstudio.bqapi.api.definition.RawTaskDefinition;

public final class RawTaskBuilder extends TaskBuilder<RawTaskDefinition, RawTaskBuilder> {

    private ResourceLocation factoryId;
    private NBTTagCompound configNbt = new NBTTagCompound();

    public RawTaskBuilder(String id) {
        super(id);
    }

    public RawTaskBuilder factoryId(String factoryId) {
        this.factoryId = new ResourceLocation(Objects.requireNonNull(factoryId, "factoryId"));
        return this;
    }

    public RawTaskBuilder factoryId(ResourceLocation factoryId) {
        this.factoryId = Objects.requireNonNull(factoryId, "factoryId");
        return this;
    }

    public RawTaskBuilder configNbt(NBTTagCompound configNbt) {
        this.configNbt = (NBTTagCompound) Objects.requireNonNull(configNbt, "configNbt")
            .copy();
        return this;
    }

    @Override
    protected RawTaskBuilder self() {
        return this;
    }

    @Override
    public RawTaskDefinition build() {
        return new RawTaskDefinition(id, slot, Objects.requireNonNull(factoryId, "factoryId"), configNbt);
    }
}
