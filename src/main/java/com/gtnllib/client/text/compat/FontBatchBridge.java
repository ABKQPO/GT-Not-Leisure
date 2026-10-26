package com.gtnllib.client.text.compat;

/** Optional font-batch boundary without linking callers to Angelica classes. */
public interface FontBatchBridge {

    int gtnl$suspendBatch();

    void gtnl$resumeBatch(int depth);

    void gtnl$flushBatch();
}
