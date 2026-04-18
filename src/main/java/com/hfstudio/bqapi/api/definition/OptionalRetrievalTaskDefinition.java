package com.hfstudio.bqapi.api.definition;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

public final class OptionalRetrievalTaskDefinition extends RetrievalTaskDefinition {

    public OptionalRetrievalTaskDefinition(String id, int slot, List<NBTTagCompound> items, boolean ignoreNbt,
        boolean partialMatch, boolean consume, boolean groupDetect, boolean autoConsume) {
        super(id, slot, items, ignoreNbt, partialMatch, consume, groupDetect, autoConsume);
    }
}
