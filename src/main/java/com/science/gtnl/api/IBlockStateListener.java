package com.science.gtnl.api;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public interface IBlockStateListener {

    /**
     * Server-side invalidation hint for a registered region. Block updates supply the updated position;
     * chunk load/unload supplies the chunk origin at y=0, which may be outside the watched line.
     * Implementations should mark their cached result dirty and schedule a recheck, not assume that
     * this position identifies a changed block or that every block mutation produces this callback.
     */
    void onBlockChanged(BlockPos pos);
}
