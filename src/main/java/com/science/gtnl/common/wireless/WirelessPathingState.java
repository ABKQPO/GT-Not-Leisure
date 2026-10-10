package com.science.gtnl.common.wireless;

/** isNetworkBooting() alone does not cover a queued repath in AE2 rv3-beta-1080. */
public interface WirelessPathingState {

    boolean gtnl$isRepathPending();
}
