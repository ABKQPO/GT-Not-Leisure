package com.science.gtnl.common.queset;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.science.gtnl.loader.QuestLoader;

import betterquesting.api.utils.UuidConverter;

public class QuestLoaderTest {

    @Test
    public void registryContainsAllThreeLegacyPagesAndPlacements() {
        Map<UUID, Integer> placementsByChapter = new HashMap<>();
        for (ChapterDefinition chapter : QuestLoader.chapters()) {
            placementsByChapter.put(
                chapter.getUuid(),
                chapter.getPlacements()
                    .size());
        }

        assertEquals(3, placementsByChapter.size());
        assertEquals(Integer.valueOf(107), placementsByChapter.get(uuid("GTNotLeisureQuestsLine==")));
        assertEquals(Integer.valueOf(26), placementsByChapter.get(uuid("GTNotLeisure75SteamAge==")));
        assertEquals(Integer.valueOf(24), placementsByChapter.get(uuid("GTNotLeisure99SteamAge==")));
    }

    public static UUID uuid(String encoded) {
        return UuidConverter.decodeUuid(encoded);
    }
}
