package com.science.gtnl.common.block.blocks.tile;

import java.util.Objects;

/**
 * Checked two-phase implementation for long-sized stack cache additions.
 */
public class ExactStackCacheCommitImpl implements ExactStackCacheCommit {

    @Override
    public <S> Result commit(S candidate, StackCache<S> cache) {
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(cache, "cache");

        long existingAmount = cache.getStoredAmount(candidate);
        long candidateAmount = cache.getCandidateAmount(candidate);
        if (existingAmount < 0 || candidateAmount <= 0) {
            return new Result(Failure.INVALID_QUANTITY, existingAmount, candidateAmount, existingAmount);
        }

        long resultingAmount;
        try {
            resultingAmount = Math.addExact(existingAmount, candidateAmount);
        } catch (ArithmeticException exception) {
            return new Result(Failure.LONG_OVERFLOW, existingAmount, candidateAmount, existingAmount);
        }

        cache.add(candidate);
        return new Result(Failure.NONE, existingAmount, candidateAmount, resultingAmount);
    }
}
