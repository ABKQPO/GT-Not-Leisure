package com.science.gtnl.api;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public interface IBlockStateListener {

    void onBlockChanged(BlockPos pos);
}
