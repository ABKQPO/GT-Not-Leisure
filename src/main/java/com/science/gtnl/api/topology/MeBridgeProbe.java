package com.science.gtnl.api.topology;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;

public interface MeBridgeProbe {

    String id();

    boolean accepts(Class<? extends IGridHost> hostType);

    void probe(IGridNode node, IGrid grid, BridgeSink sink);
}
