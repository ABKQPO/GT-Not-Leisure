package com.hfstudio.bqapi.api.definition;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public final class RawTaskDefinition extends TaskDefinition {

    private final ResourceLocation factoryId;
    private final NBTTagCompound configNbt;

    public RawTaskDefinition(String id, int slot, ResourceLocation factoryId, NBTTagCompound configNbt) {
        super(id, slot);
        this.factoryId = Objects.requireNonNull(factoryId, "factoryId");
        this.configNbt = (NBTTagCompound) Objects.requireNonNull(configNbt, "configNbt")
            .copy();
    }

    public ResourceLocation getFactoryId() {
        return factoryId;
    }

    public NBTTagCompound getConfigNbt() {
        return (NBTTagCompound) configNbt.copy();
    }
}
