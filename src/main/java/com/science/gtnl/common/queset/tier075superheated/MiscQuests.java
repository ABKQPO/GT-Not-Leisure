package com.science.gtnl.common.queset.tier075superheated;

import static com.science.gtnl.common.queset.QuestCompat.quest;
import static com.science.gtnl.common.queset.QuestCompat.uuid;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.RewardBuilders;
import com.hfstudio.bqapi.api.builder.TaskBuilders;
import com.hfstudio.bqapi.api.definition.QuestDefinition;

public final class MiscQuests {

    private MiscQuests() {}

    public static final QuestDefinition WELCOME_TO_CHAPTER_0_75 = quest(
        "gtnl:tier075superheated/misc/welcome_to_chapter_0_75",
        "eHaeoPROSSWmX6tTXJaX5w==").icon("Railcraft:fluid.steam", 1, 0)
            .prerequisite(uuid("hEsmeH74S4aOOrROI1AHxQ=="))
            .build();

    public static final QuestDefinition CREDITS = quest(
        "gtnl:tier075superheated/misc/credits",
        "po4Ov0zsTbmn_zpuPQTyvQ==").icon("TConstruct:heartCanister", 1, 1)
            .prerequisite(uuid("eHaeoPROSSWmX6tTXJaX5w=="))
            .reward(
                RewardBuilders.item("gtnl:tier075superheated/misc/credits/reward/0")
                    .slot(0)
                    .item("minecraft:poisonous_potato", 1, 0)
                    .build())
            .build();

    public static final QuestDefinition NOT_LOGISTICS = quest(
        "gtnl:tier075superheated/misc/not_logistics",
        "eV1BdX6qTiSM-DAQM11TLA==").icon("IC2:blockMetal", 1, 2)
            .prerequisite(uuid("LDodKZyAQhiA1kdr4HxoWg=="))
            .reward(
                RewardBuilders.item("gtnl:tier075superheated/misc/not_logistics/reward/0")
                    .slot(0)
                    .item("minecraft:tallgrass", 2, 2)
                    .build())
            .build();

    public static final QuestDefinition SUPERHEATED = quest(
        "gtnl:tier075superheated/misc/superheated",
        "KL3toa-sTKq8ojVmlG3Jdw==").icon("IC2:fluidSuperheatedSteam", 1, 0)
            .prerequisite(uuid("SU-S_BoeStacdLPKW0_CGQ=="))
            .prerequisite(uuid("SQo2-hr-TCasVo8_la0HKQ=="))
            .prerequisite(uuid("qa6pQlvZRB6OvjdaBdVw9Q=="))
            .task(
                TaskBuilders.checkbox("gtnl:tier075superheated/misc/superheated/task/0")
                    .slot(0)
                    .build())
            .build();

    public static final QuestDefinition THIS_WILL_NEVER_BE_BETTER_SMELTING = quest(
        "gtnl:tier075superheated/misc/this_will_never_be_better_smelting",
        "_SSkFY3PRCatDhZi1xU94g==").icon("minecraft:web", 1, 0)
            .prerequisite(uuid("KL3toa-sTKq8ojVmlG3Jdw=="))
            .reward(
                RewardBuilders.item("gtnl:tier075superheated/misc/this_will_never_be_better_smelting/reward/0")
                    .slot(0)
                    .item("minecraft:ghast_tear", 2, 0)
                    .build())
            .build();

    public static final QuestDefinition HOW_TO_EXTRACTINATE = quest(
        "gtnl:tier075superheated/misc/how_to_extractinate",
        "S-G6BF23RPOAb2ZwiZB7uw==").icon("gregtech:gt.metaitem.02", 1, 29500)
            .prerequisite(uuid("SU-S_BoeStacdLPKW0_CGQ=="))
            .task(
                TaskBuilders.checkbox("gtnl:tier075superheated/misc/how_to_extractinate/task/0")
                    .slot(0)
                    .build())
            .build();

    public static void contribute(ChapterBuilder chapter) {
        chapter.quest(WELCOME_TO_CHAPTER_0_75.at(-12, -12, 24, 24));
        chapter.quest(CREDITS.at(-12, 24, 24, 24));
        chapter.quest(NOT_LOGISTICS.at(-48, 132, 24, 24));
        chapter.quest(SUPERHEATED.at(-12, 156, 24, 24));
        chapter.quest(THIS_WILL_NEVER_BE_BETTER_SMELTING.at(24, 156, 24, 24));
        chapter.quest(HOW_TO_EXTRACTINATE.at(-84, 192, 24, 24));
    }
}
