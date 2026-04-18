package com.hfstudio.bqapi.runtime.apply;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.definition.ItemRewardDefinition;
import com.hfstudio.bqapi.api.definition.RawRewardDefinition;
import com.hfstudio.bqapi.api.definition.RewardDefinition;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.questing.rewards.RewardRegistry;
import bq_standard.rewards.RewardItem;

public final class RewardApplier {

    public Map<Integer, IReward> buildRewards(Iterable<RewardDefinition> definitions) {
        Map<Integer, IReward> rewards = new LinkedHashMap<>();
        int nextAutoSlot = 0;

        for (RewardDefinition definition : definitions) {
            int slot = definition.getSlot();
            if (slot < 0) {
                while (rewards.containsKey(nextAutoSlot)) {
                    nextAutoSlot++;
                }
                slot = nextAutoSlot++;
            }
            if (rewards.containsKey(slot)) {
                throw new IllegalArgumentException("Duplicate reward slot: " + slot);
            }
            rewards.put(slot, createReward(definition));
        }

        return rewards;
    }

    private IReward createReward(RewardDefinition definition) {
        if (definition instanceof ItemRewardDefinition itemReward) {
            RewardItem reward = new RewardItem();
            for (NBTTagCompound itemNbt : itemReward.getItems()) {
                BigItemStack stack = JsonHelper.JsonToItemStack((NBTTagCompound) itemNbt.copy());
                if (stack == null) {
                    throw new IllegalArgumentException("Unable to load reward item from NBT");
                }
                reward.items.add(stack);
            }
            return reward;
        }
        if (definition instanceof RawRewardDefinition raw) {
            IReward reward = RewardRegistry.INSTANCE.createNew(raw.getFactoryId());
            if (reward == null) {
                throw new IllegalArgumentException("Unknown BetterQuesting reward factory: " + raw.getFactoryId());
            }
            reward.readFromNBT(raw.getConfigNbt());
            return reward;
        }

        throw new IllegalArgumentException(
            "Unsupported reward definition: " + definition.getClass()
                .getName());
    }
}
