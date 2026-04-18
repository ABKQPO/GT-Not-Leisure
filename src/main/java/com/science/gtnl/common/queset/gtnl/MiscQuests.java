package com.science.gtnl.common.queset.gtnl;

import static com.science.gtnl.common.queset.QuestCompat.quest;
import static com.science.gtnl.common.queset.QuestCompat.uuid;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.RewardBuilders;
import com.hfstudio.bqapi.api.builder.TaskBuilders;
import com.hfstudio.bqapi.api.definition.QuestDefinition;

public final class MiscQuests {

    private MiscQuests() {}

    public static final QuestDefinition DEBUG_ENERGY_HATCH = quest(
        "gtnl:gtnl/misc/debug_energy_hatch",
        "OAXsDWupSL6SQUOppyETiw==").icon("gregtech:gt.blockmachines", 1, 21507)
            .prerequisite(uuid("Ux6sYrP5S7Ojh2qhxjmGGA=="))
            .task(
                TaskBuilders.retrieval("gtnl:gtnl/misc/debug_energy_hatch/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21507)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(true)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition DEBUG_RESEARCH_STATION = quest(
        "gtnl:gtnl/misc/debug_research_station",
        "HeboOppERVCR3DM2iVQ4pA==").icon("gregtech:gt.blockmachines", 1, 22582)
            .prerequisite(uuid("Ux6sYrP5S7Ojh2qhxjmGGA=="))
            .task(
                TaskBuilders.retrieval("gtnl:gtnl/misc/debug_research_station/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 22582)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(true)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition QUEST_UX6SYRP5S7OJH2QHXJMGGA = quest(
        "gtnl:gtnl/misc/quest_ux6syrp5s7ojh2qhxjmgga",
        "Ux6sYrP5S7Ojh2qhxjmGGA==").icon("Automagy:phialExtra", 1, 0)
            .main(true)
            .prerequisite(uuid("fX7XqF6FS1yfCjQ6ck41Ow=="))
            .task(
                TaskBuilders.checkbox("gtnl:gtnl/misc/quest_ux6syrp5s7ojh2qhxjmgga/task/0")
                    .slot(0)
                    .build())
            .reward(
                RewardBuilders.item("gtnl:gtnl/misc/quest_ux6syrp5s7ojh2qhxjmgga/reward/1")
                    .slot(1)
                    .item("ae2fc:walrus", 1, 0)
                    .build())
            .build();

    public static final QuestDefinition QUEST_FX7XQF6FS1YFCJQ6CK41OW = quest(
        "gtnl:gtnl/misc/quest_fx7xqf6fs1yfcjq6ck41ow",
        "fX7XqF6FS1yfCjQ6ck41Ow==").icon("sciencenotleisure:TestItem", 1, 0)
            .main(true)
            .prerequisite(uuid("AAAAAAAAAAAAAAAAAAAAAA=="))
            .task(
                TaskBuilders.checkbox("gtnl:gtnl/misc/quest_fx7xqf6fs1yfcjq6ck41ow/task/0")
                    .slot(0)
                    .build())
            .build();

    public static final QuestDefinition LET_S_START_THE_FUN = quest(
        "gtnl:gtnl/misc/let_s_start_the_fun",
        "EVhlJXyIR3SmEIhMTUdsWA==").icon("minecraft:fire", 1, 0)
            .main(true)
            .prerequisite(uuid("AAAAAAAAAAAAAAAAAAAAIw=="))
            .reward(
                RewardBuilders.item("gtnl:gtnl/misc/let_s_start_the_fun/reward/1")
                    .slot(1)
                    .item("etfuturum:rose", 1, 0)
                    .build())
            .build();

    public static final QuestDefinition I_M_TIRED_BOSS = quest(
        "gtnl:gtnl/misc/i_m_tired_boss",
        "KYUX4gk1QGqVuPD7vm3vvg==").icon("minecraft:potion", 1, 0)
            .prerequisite(uuid("sLmbjGYlTYuHxpFynCb6rQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:gtnl/misc/i_m_tired_boss/task/0")
                    .slot(0)
                    .item("minecraft:potion", 1, 0)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(true)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition ME_IS_HUNGRY = quest("gtnl:gtnl/misc/me_is_hungry", "UODPZCyXT96ap5_4VL1FHA==")
        .icon("harvestcraft:breadedporkchopItem", 1, 0)
        .prerequisite(uuid("KYUX4gk1QGqVuPD7vm3vvg=="))
        .task(
            TaskBuilders.retrieval("gtnl:gtnl/misc/me_is_hungry/task/0")
                .slot(0)
                .item("harvestcraft:breadedporkchopItem", 1, 0)
                .ignoreNbt(true)
                .partialMatch(true)
                .consume(true)
                .groupDetect(false)
                .autoConsume(false)
                .build())
        .build();

    public static final QuestDefinition NO_JOKES_IN_THIS_ONE_OR_DISCRIPTION = quest(
        "gtnl:gtnl/misc/no_jokes_in_this_one_or_discription",
        "BLoqkgLqSLmPobuYeMhXIw==").icon("gregtech:gt.blockmachines", 1, 21166)
            .prerequisite(uuid("IAzmh1pwRbayb3c436tQcw=="))
            .task(
                TaskBuilders.retrieval("gtnl:gtnl/misc/no_jokes_in_this_one_or_discription/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21166)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static void contribute(ChapterBuilder chapter) {
        chapter.quest(DEBUG_ENERGY_HATCH.at(372, -300, 24, 24));
        chapter.quest(DEBUG_RESEARCH_STATION.at(264, -84, 24, 24));
        chapter.quest(QUEST_UX6SYRP5S7OJH2QHXJMGGA.at(48, -72, 36, 36));
        chapter.quest(QUEST_FX7XQF6FS1YFCJQ6CK41OW.at(-24, -24, 48, 48));
        chapter.quest(LET_S_START_THE_FUN.at(-24, -24, 48, 48));
        chapter.quest(I_M_TIRED_BOSS.at(-12, 168, 24, 24));
        chapter.quest(ME_IS_HUNGRY.at(-12, 276, 24, 24));
        chapter.quest(NO_JOKES_IN_THIS_ONE_OR_DISCRIPTION.at(36, 420, 24, 24));
    }
}
