package com.hfstudio.bqapi.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

import com.hfstudio.bqapi.BQApiLangKeys;

public class BQStableIdFactoryTest {

    @Test
    public void questUuidIsStableAndSeparatedFromChapterUuid() {
        UUID questA = BQStableIdFactory.quest("gtnl:large_steam_furnace");
        UUID questB = BQStableIdFactory.quest("gtnl:large_steam_furnace");
        UUID chapter = BQStableIdFactory.chapter("gtnl:large_steam_furnace");

        assertEquals(questA, questB);
        assertNotEquals(questA, chapter);
    }

    @Test
    public void questLangKeysFollowBetterQuestingFormat() {
        UUID quest = BQStableIdFactory.quest("gtnl:large_steam_furnace");
        assertEquals(
            "betterquesting.quest." + betterquesting.api.utils.UuidConverter.encodeUuidStripPadding(quest) + ".name",
            BQApiLangKeys.questName(quest));
        assertEquals(
            "betterquesting.quest." + betterquesting.api.utils.UuidConverter.encodeUuidStripPadding(quest) + ".desc",
            BQApiLangKeys.questDesc(quest));
    }

    @Test
    public void nullQuestIdIsRejected() {
        try {
            BQStableIdFactory.quest(null);
            fail("Expected a NullPointerException for null quest IDs");
        } catch (NullPointerException expected) {}
    }
}
