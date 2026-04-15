package com.science.gtnl.utils;

import org.jetbrains.annotations.Nullable;

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

    void ec$setName(String name);
}
