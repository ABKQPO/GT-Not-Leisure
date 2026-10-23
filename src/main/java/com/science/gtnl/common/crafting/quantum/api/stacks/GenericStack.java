package com.science.gtnl.common.crafting.quantum.api.stacks;

import java.util.Objects;

public record GenericStack(AEKey what, long amount) {

    public GenericStack {
        Objects.requireNonNull(what, "what");
    }
}
