package com.science.gtnl.common.wireless;

public interface IWirelessPathingState {

    boolean gtnl$isRepathPending();

    /**
     * Counts membership and wired connection changes of this grid; frequency-card connections are not counted.
     */
    long gtnl$topologyRevision();

    void gtnl$topologyChanged();
}
