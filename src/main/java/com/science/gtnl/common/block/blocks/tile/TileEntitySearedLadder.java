package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import mantle.blocks.abstracts.MultiServantLogic;

public class TileEntitySearedLadder extends MultiServantLogic {

    @Override
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y,
        int z) {
        // Preserve the smeltery master when a rotation is synchronized to the client.
        return oldBlock != newBlock;
    }
}
