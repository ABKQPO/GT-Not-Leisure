package com.hfstudio.bqapi.api.builder;

import java.util.Objects;

import com.hfstudio.bqapi.api.definition.TaskDefinition;

public abstract class TaskBuilder<T extends TaskDefinition, B extends TaskBuilder<T, B>> {

    protected final String id;
    protected int slot = -1;

    protected TaskBuilder(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public B slot(int slot) {
        if (slot < 0) {
            throw new IllegalArgumentException("slot must be >= 0");
        }
        this.slot = slot;
        return self();
    }

    protected abstract B self();

    public abstract T build();
}
