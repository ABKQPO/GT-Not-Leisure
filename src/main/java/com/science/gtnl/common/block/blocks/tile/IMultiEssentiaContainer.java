package com.science.gtnl.common.block.blocks.tile;

import thaumcraft.api.aspects.IAspectContainer;

public interface IMultiEssentiaContainer extends IAspectContainer {

    /**
     * 返回所有源质共享的总容量。
     * Returns the capacity shared by every stored aspect.
     */
    int getEssentiaCapacity();

    /**
     * 返回当前储存的源质总量。
     * Returns the total amount of essentia currently stored.
     */
    int getTotalAmount();
}
