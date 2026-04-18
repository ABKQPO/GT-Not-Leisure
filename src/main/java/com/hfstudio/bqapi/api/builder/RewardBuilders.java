package com.hfstudio.bqapi.api.builder;

import com.hfstudio.bqapi.api.builder.reward.ItemRewardBuilder;
import com.hfstudio.bqapi.api.builder.reward.RawRewardBuilder;

public final class RewardBuilders {

    private RewardBuilders() {}

    public static ItemRewardBuilder item(String id) {
        return new ItemRewardBuilder(id);
    }

    public static RawRewardBuilder raw(String id) {
        return new RawRewardBuilder(id);
    }
}
