package com.science.gtnl.common.crafting.quantum.crafting;

import com.science.gtnl.common.crafting.quantum.core.localization.PlayerMessages;

public class CraftingCalculationFailure extends RuntimeException {

    private final PlayerMessages messageKey;

    public CraftingCalculationFailure(PlayerMessages messageKey) {
        super(messageKey.getTranslationKey());
        this.messageKey = messageKey;
    }

    public PlayerMessages getMessageKey() {
        return this.messageKey;
    }

}
