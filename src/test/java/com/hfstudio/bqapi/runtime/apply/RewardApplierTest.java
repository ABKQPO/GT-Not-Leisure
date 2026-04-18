package com.hfstudio.bqapi.runtime.apply;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import org.junit.Test;

import com.hfstudio.bqapi.api.builder.RewardBuilders;
import com.hfstudio.bqapi.api.definition.RawRewardDefinition;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.questing.rewards.RewardRegistry;
import net.minecraft.client.gui.GuiScreen;

public class RewardApplierTest {

    @Test
    public void rawRewardDefinitionsCanRebuildRegisteredRewards() {
        NBTTagCompound configNbt = new NBTTagCompound();
        configNbt.setString("marker", "from_raw_reward");

        RawRewardDefinition raw = RewardBuilders.raw("test:reward/raw")
            .slot(0)
            .factoryId(TestRewardFactory.INSTANCE.getRegistryName())
            .configNbt(configNbt)
            .build();

        if (RewardRegistry.INSTANCE.getFactory(TestRewardFactory.INSTANCE.getRegistryName()) == null) {
            RewardRegistry.INSTANCE.register(TestRewardFactory.INSTANCE);
        }

        IReward reward = new RewardApplier().buildRewards(Collections.singleton(raw))
            .get(0);

        assertTrue(reward instanceof TestReward);
        assertEquals("from_raw_reward", ((TestReward) reward).marker);
    }

    private static final class TestRewardFactory implements IFactoryData<IReward, NBTTagCompound> {

        private static final TestRewardFactory INSTANCE = new TestRewardFactory();

        @Override
        public ResourceLocation getRegistryName() {
            return new ResourceLocation("test", "dummy_reward");
        }

        @Override
        public TestReward createNew() {
            return new TestReward();
        }

        @Override
        public TestReward loadFromData(NBTTagCompound json) {
            TestReward reward = new TestReward();
            reward.readFromNBT(json);
            return reward;
        }
    }

    private static final class TestReward implements IReward {

        private String marker = "";

        @Override
        public String getUnlocalisedName() {
            return "test.reward";
        }

        @Override
        public ResourceLocation getFactoryID() {
            return TestRewardFactory.INSTANCE.getRegistryName();
        }

        @Override
        public boolean canClaim(EntityPlayer player, java.util.Map.Entry<java.util.UUID, IQuest> quest) {
            return true;
        }

        @Override
        public void claimReward(EntityPlayer player, java.util.Map.Entry<java.util.UUID, IQuest> quest) {}

        @Override
        public IGuiPanel getRewardGui(IGuiRect rect, java.util.Map.Entry<java.util.UUID, IQuest> quest) {
            return null;
        }

        @Override
        public GuiScreen getRewardEditor(GuiScreen parent, java.util.Map.Entry<java.util.UUID, IQuest> quest) {
            return null;
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt.setString("marker", marker);
            return nbt;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            marker = nbt.getString("marker");
        }
    }
}
