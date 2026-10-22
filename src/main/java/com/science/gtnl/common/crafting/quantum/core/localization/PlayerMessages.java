package com.science.gtnl.common.crafting.quantum.core.localization;

public enum PlayerMessages {

    CraftingNoPlan,
    CraftingNoNetOutput,
    CraftingTooLong;

    public String getTranslationKey() {
        return "gtnl.quantum_crafting.message." + name();
    }
}
