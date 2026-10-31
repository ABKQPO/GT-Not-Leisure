package com.science.gtnl.common.block.blocks.tile;

/**
 * Commits one positive stack amount to an existing long-sized cache only when the exact merged amount is representable.
 * The abstraction separates the arithmetic transaction from AE's runtime stack classes so overflow behavior can be
 * tested without starting FML.
 */
public interface ExactStackCacheCommit {

    /**
     * Identifies why a cache candidate was not committed.
     */
    enum Failure {

        /** The cache write completed after exact validation. */
        NONE,

        /** Existing or added quantities violated the positive stack contract. */
        INVALID_QUANTITY,

        /** Existing plus added quantity exceeded the signed long range. */
        LONG_OVERFLOW
    }

    /**
     * Public cache operations required for a two-phase exact commit.
     *
     * @param <S> candidate stack type
     */
    interface StackCache<S> {

        /**
         * Returns the precise amount already stored for the candidate's stack type.
         *
         * @param candidate stack type and metadata being committed
         * @return non-negative existing amount, or zero when absent
         */
        long getStoredAmount(S candidate);

        /**
         * Returns the positive amount carried by the candidate.
         *
         * @param candidate stack being committed
         * @return candidate amount
         */
        long getCandidateAmount(S candidate);

        /**
         * Performs the sole cache mutation after exact addition has succeeded.
         *
         * @param candidate validated stack to add
         */
        void add(S candidate);
    }

    /**
     * Immutable outcome of one cache commit attempt.
     */
    class Result {

        /** Failure status, or {@link Failure#NONE} after commit. */
        private final Failure failure;

        /** Amount observed before validation. */
        private final long existingAmount;

        /** Amount requested by the candidate. */
        private final long candidateAmount;

        /** Exact resulting amount, or the unchanged existing amount after rejection. */
        private final long resultingAmount;

        public Result(Failure failure, long existingAmount, long candidateAmount, long resultingAmount) {
            if (failure == null) throw new IllegalArgumentException("A cache commit result needs a failure status");
            this.failure = failure;
            this.existingAmount = existingAmount;
            this.candidateAmount = candidateAmount;
            this.resultingAmount = resultingAmount;
        }

        /**
         * Reports whether the cache mutation occurred.
         *
         * @return {@code true} only after exact validation and cache insertion
         */
        public boolean isCommitted() {
            return failure == Failure.NONE;
        }

        /**
         * Returns the rejection reason or {@link Failure#NONE}.
         *
         * @return commit status
         */
        public Failure getFailure() {
            return failure;
        }

        /**
         * Returns the amount observed before the attempt.
         *
         * @return original cache amount
         */
        public long getExistingAmount() {
            return existingAmount;
        }

        /**
         * Returns the candidate's requested addition.
         *
         * @return candidate amount
         */
        public long getCandidateAmount() {
            return candidateAmount;
        }

        /**
         * Returns the exact post-commit amount or unchanged amount after rejection.
         *
         * @return resulting cache amount
         */
        public long getResultingAmount() {
            return resultingAmount;
        }
    }

    /**
     * Validates and commits a candidate without mutating the cache on invalid quantity or long overflow.
     *
     * @param candidate stack to add
     * @param cache     public cache operations used by the transaction
     * @param <S>       candidate stack type
     * @return exact commit result
     */
    <S> Result commit(S candidate, StackCache<S> cache);
}
