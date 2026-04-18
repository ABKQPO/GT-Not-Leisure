package com.science.gtnl.common.queset.tier0999supercritical;

import static com.science.gtnl.common.queset.QuestCompat.quest;
import static com.science.gtnl.common.queset.QuestCompat.uuid;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.TaskBuilders;
import com.hfstudio.bqapi.api.definition.QuestDefinition;

public final class MiscQuests {

    private MiscQuests() {}

    public static final QuestDefinition WELCOME_TO_CHAPTER_0_999 = quest(
        "gtnl:tier0999supercritical/misc/welcome_to_chapter_0_999",
        "xNqiQ98pTSyopIdUBOWdmQ==").icon("IC2:fluidSteam", 1, 0)
            .prerequisite(uuid("J2A3CMyLQJWLceK9M83apg=="))
            .build();

    public static final QuestDefinition SUPERCRITICAL_STEAM = quest(
        "gtnl:tier0999supercritical/misc/supercritical_steam",
        "b5qQ79exTKGqlpoaJrdCfQ==").icon("GoodGenerator:supercriticalSteam", 1, 0)
            .prerequisite(uuid("xNqiQ98pTSyopIdUBOWdmQ=="))
            .task(
                TaskBuilders.checkbox("gtnl:tier0999supercritical/misc/supercritical_steam/task/0")
                    .slot(0)
                    .build())
            .build();

    public static final QuestDefinition THANKS_FOR_USING_THE_QUESTS = quest(
        "gtnl:tier0999supercritical/misc/thanks_for_using_the_quests",
        "5BjWblhEQuaPM0FR8og1Pw==").icon("minecraft:poisonous_potato", 1, 0)
            .prerequisite(uuid("4b1deRqfQ9ibu3FXSMQH4Q=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/misc/thanks_for_using_the_quests/task/0")
                    .slot(0)
                    .item("minecraft:poisonous_potato", 1, 0)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static void contribute(ChapterBuilder chapter) {
        chapter.quest(WELCOME_TO_CHAPTER_0_999.at(-12, -12, 24, 24));
        chapter.quest(SUPERCRITICAL_STEAM.at(-12, 24, 24, 24));
        chapter.quest(THANKS_FOR_USING_THE_QUESTS.at(-12, 408, 24, 24));
    }
}
