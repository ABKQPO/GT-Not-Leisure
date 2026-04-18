package com.hfstudio.bqapi.api.definition;

import java.util.Objects;

public abstract class RewardDefinition {

    private final String id;
    private final int slot;

    protected RewardDefinition(String id, int slot) {
        this.id = Objects.requireNonNull(id, "id");
        this.slot = slot;
    }

    public String getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }
}
