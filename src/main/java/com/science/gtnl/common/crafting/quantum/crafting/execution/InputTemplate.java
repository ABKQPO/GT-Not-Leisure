package com.science.gtnl.common.crafting.quantum.crafting.execution;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;

/**
 * Represents a single "unit" of input for a slot in a crafting/processing pattern. How many units of input are required
 * for a pattern is returned by {@link IPatternDetails.IInput#getMultiplier()}.
 */
public record InputTemplate(AEKey key, long amount) {}
