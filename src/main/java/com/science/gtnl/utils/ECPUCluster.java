package com.science.gtnl.utils;

import javax.annotation.Nullable;

import com.science.gtnl.common.machine.multiblock.QuantumComputer;

import appeng.me.cluster.implementations.CraftingCPUCluster;

public interface ECPUCluster {

    static ECPUCluster from(final CraftingCPUCluster cluster) {
        return (ECPUCluster) (Object) cluster;
    }

    void ec$setAvailableStorage(final long availableStorage);

    void ec$setAccelerators(final int accelerators);

    QuantumComputer ec$getVirtualCPUOwner();

    void ec$setVirtualCPUOwner(@Nullable final QuantumComputer isVirtualCPUOwner);

    void ec$markDestroyed();

}
