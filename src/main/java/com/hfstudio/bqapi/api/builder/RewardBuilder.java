package com.hfstudio.bqapi.api.builder;

import java.util.Objects;

import com.hfstudio.bqapi.api.definition.RewardDefinition;

public abstract class RewardBuilder<T extends RewardDefinition, B extends RewardBuilder<T, B>> {

    protected final String id;
    protected int slot = -1;

    protected RewardBuilder(String id) {
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
