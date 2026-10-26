package com.science.gtnl.common.crafting.quantum.crafting;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.core.localization.PlayerMessages;

public class CraftingCalculationFailure extends RuntimeException {

    private final PlayerMessages messageKey;
    @Nullable
    private final NoNetOutputCycle cycle;

    public CraftingCalculationFailure(PlayerMessages messageKey) {
        this(messageKey, null);
    }

    public CraftingCalculationFailure(PlayerMessages messageKey, @Nullable NoNetOutputCycle cycle) {
        super(messageKey.getTranslationKey());
        this.messageKey = messageKey;
        this.cycle = cycle;
    }

    /**
     * The cycle that made no net output, when that is what failed.
     */
    @Nullable
    public NoNetOutputCycle getCycle() {
        return this.cycle;
    }

    public PlayerMessages getMessageKey() {
        return this.messageKey;
    }

}
