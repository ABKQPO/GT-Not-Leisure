package com.hfstudio.bqapi.api.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.Test;

import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.hfstudio.bqapi.api.definition.ItemRewardDefinition;
import com.hfstudio.bqapi.api.definition.LocationTaskDefinition;
import com.hfstudio.bqapi.api.definition.OptionalRetrievalTaskDefinition;
import com.hfstudio.bqapi.api.definition.QuestDefinition;
import com.hfstudio.bqapi.api.definition.QuestPlacementDefinition;
import com.hfstudio.bqapi.api.definition.RawRewardDefinition;
import com.hfstudio.bqapi.api.definition.RawTaskDefinition;
import com.hfstudio.bqapi.api.definition.RetrievalTaskDefinition;

public class DefinitionBuilderTest {

    @Test
    public void questPlacementKeepsQuestReusable() {
        QuestDefinition quest = Quests.quest("gtnl:large_steam_furnace")
            .build();
        QuestPlacementDefinition placement = quest.at(32, 48);
        ChapterDefinition chapter = Chapters.chapter("gtnl:steam_age")
            .quest(placement)
            .build();

        assertSame(quest, placement.getQuest());
        assertEquals(32, placement.getX());
        assertEquals(48, placement.getY());
        assertEquals(
            1,
            chapter.getPlacements()
                .size());
    }

    @Test
    public void taskBuildersKeepExplicitSlotsAndLocationLabelKeys() {
        RetrievalTaskDefinition retrieval = TaskBuilders.retrieval("gtnl:large_steam_furnace/retrieval")
            .slot(2)
            .item(new ItemStack(new Item()))
            .ignoreNbt(false)
            .partialMatch(false)
            .consume(false)
            .groupDetect(true)
            .autoConsume(true)
            .build();

        LocationTaskDefinition location = TaskBuilders.location("gtnl:large_steam_furnace/location")
            .slot(3)
            .labelKey("quest.gtnl.large_steam_furnace.location")
            .x(10)
            .y(64)
            .z(20)
            .dimension(0)
            .build();

        assertEquals(2, retrieval.getSlot());
        assertEquals(
            1,
            retrieval.getItems()
                .size());
        assertFalse(retrieval.isIgnoreNbt());
        assertFalse(retrieval.isPartialMatch());
        assertFalse(retrieval.isConsume());
        assertTrue(retrieval.isGroupDetect());
        assertTrue(retrieval.isAutoConsume());
        assertEquals("quest.gtnl.large_steam_furnace.location", location.getLabelKey());
    }

    @Test
    public void rawBuilderCopiesConfigNbt() {
        NBTTagCompound config = new NBTTagCompound();
        config.setString("mode", "strict");

        RawTaskDefinition raw = TaskBuilders.raw("gtnl:large_steam_furnace/raw")
            .slot(4)
            .factoryId("betterquesting:task")
            .configNbt(config)
            .build();

        config.setString("mode", "changed");

        assertEquals(4, raw.getSlot());
        assertEquals(
            "betterquesting:task",
            raw.getFactoryId()
                .toString());
        assertEquals(
            "strict",
            raw.getConfigNbt()
                .getString("mode"));
    }

    @Test
    public void rawRewardBuilderCopiesConfigNbt() {
        NBTTagCompound config = new NBTTagCompound();
        config.setString("mode", "strict");

        RawRewardDefinition raw = RewardBuilders.raw("gtnl:large_steam_furnace/raw_reward")
            .slot(5)
            .factoryId("bq_standard:item")
            .configNbt(config)
            .build();

        config.setString("mode", "changed");

        assertEquals(5, raw.getSlot());
        assertEquals(
            "bq_standard:item",
            raw.getFactoryId()
                .toString());
        assertEquals(
            "strict",
            raw.getConfigNbt()
                .getString("mode"));
    }

    @Test
    public void negativeTaskSlotIsRejected() {
        try {
            TaskBuilders.checkbox("gtnl:large_steam_furnace/checkbox")
                .slot(-1);
            fail("Expected negative task slots to be rejected");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void chapterAndQuestCanKeepExplicitLegacyIdsAndCustomPlacementSizes() {
        UUID chapterUuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID questUuid = UUID.fromString("22222222-2222-2222-2222-222222222222");

        QuestDefinition quest = Quests.quest("gtnl:large_steam_furnace")
            .uuid(questUuid)
            .build();
        QuestPlacementDefinition placement = quest.at(32, 48, 48, 36);
        ChapterDefinition chapter = Chapters.chapter("gtnl:steam_age")
            .uuid(chapterUuid)
            .icon(new ItemStack(Items.book))
            .quest(placement)
            .build();

        assertEquals(chapterUuid, chapter.getUuid());
        assertEquals(questUuid, quest.getUuid());
        assertEquals(48, placement.getSizeX());
        assertEquals(36, placement.getSizeY());
        assertNotNull(chapter.getIconNbt());
    }

    @Test
    public void questBuilderSupportsPrerequisitesRewardsAndOptionalRetrievalTasks() {
        UUID prerequisiteUuid = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID questUuid = UUID.fromString("44444444-4444-4444-4444-444444444444");
        QuestDefinition prerequisite = Quests.quest("gtnl:steam_age/root")
            .uuid(prerequisiteUuid)
            .build();

        OptionalRetrievalTaskDefinition optionalTask = TaskBuilders
            .optionalRetrieval("gtnl:large_steam_furnace/optional")
            .slot(1)
            .item(new ItemStack(Items.apple))
            .build();
        ItemRewardDefinition reward = RewardBuilders.item("gtnl:large_steam_furnace/reward")
            .slot(2)
            .item(new ItemStack(Items.carrot))
            .build();

        QuestDefinition quest = Quests.quest("gtnl:large_steam_furnace")
            .uuid(questUuid)
            .prerequisite(prerequisite)
            .main(true)
            .icon(new ItemStack(Items.compass))
            .task(optionalTask)
            .reward(reward)
            .build();

        assertEquals(questUuid, quest.getUuid());
        assertEquals(
            1,
            quest.getPrerequisites()
                .size());
        assertEquals(
            prerequisiteUuid,
            quest.getPrerequisites()
                .get(0));
        assertTrue(quest.isMain());
        assertNotNull(quest.getIconNbt());
        assertEquals(
            1,
            quest.getRewards()
                .size());
        assertEquals(
            1,
            optionalTask.getItems()
                .size());
        assertEquals(
            1,
            reward.getItems()
                .size());
    }
}
