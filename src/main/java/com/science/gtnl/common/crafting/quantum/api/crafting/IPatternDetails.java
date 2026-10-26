package com.science.gtnl.common.crafting.quantum.api.crafting;

import java.util.List;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;

public interface IPatternDetails {

    IInput[] getInputs();

    default boolean isCrafting() {
        return false;
    }

    default boolean canSubstitute() {
        return false;
    }

    default GenericStack getPrimaryOutput() {
        return getOutputs().get(0);
    }

    List<GenericStack> getOutputs();

    interface IInput {

        GenericStack[] possibleInputs();

        long getMultiplier();

        boolean isValid(AEKey input, World level);

        @Nullable
        AEKey getRemainingKey(AEKey template);
    }
}
