package com.science.gtnl.common.queset.tier075superheated;

import static com.science.gtnl.common.queset.QuestCompat.quest;
import static com.science.gtnl.common.queset.QuestCompat.uuid;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.RewardBuilders;
import com.hfstudio.bqapi.api.builder.TaskBuilders;
import com.hfstudio.bqapi.api.definition.QuestDefinition;

public final class MachineQuests {

    private MachineQuests() {}

    public static final QuestDefinition THESE_THINGS_DO_GROW_ON_TREES = quest(
        "gtnl:tier075superheated/machine/these_things_do_grow_on_trees",
        "iRXG2kSfT2irUO527ughPQ==").icon("sciencenotleisure:blockGiantBrickuoiaSapling", 1, 0)
            .prerequisite(uuid("hEsmeH74S4aOOrROI1AHxQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/these_things_do_grow_on_trees/task/0")
                    .slot(0)
                    .item("sciencenotleisure:blockGiantBrickuoiaSapling", 1, 0)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition FUSING = quest(
        "gtnl:tier075superheated/machine/fusing",
        "sd5zePjNStSSuoW04WegtA==").icon("gregtech:gt.blockmachines", 1, 31086)
            .prerequisite(uuid("eHaeoPROSSWmX6tTXJaX5w=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/fusing/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 31086)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition BLENDING = quest(
        "gtnl:tier075superheated/machine/blending",
        "1QM7Ia08R1Gr_s_bDHiSyA==").icon("gregtech:gt.blockmachines", 1, 31084)
            .prerequisite(uuid("eHaeoPROSSWmX6tTXJaX5w=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/blending/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 31084)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition ROCK_BREAKING = quest(
        "gtnl:tier075superheated/machine/rock_breaking",
        "1aEuzPlnRQaY6k1o9X19ww==").icon("gregtech:gt.blockmachines", 1, 21121)
            .prerequisite(uuid("6A4wnmpkTOa08s8Yuttsvw=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/rock_breaking/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21121)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition STRONZE = quest(
        "gtnl:tier075superheated/machine/stronze",
        "QEJyflMpRDiutYQqGI8oUg==").icon("bartworks:gt.bwMetaGeneratedingot", 1, 25110)
            .prerequisite(uuid("sd5zePjNStSSuoW04WegtA=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/stronze/task/0")
                    .slot(0)
                    .item("bartworks:gt.bwMetaGeneratedingot", 1, 25110)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .task(
                TaskBuilders.optionalRetrieval("gtnl:tier075superheated/machine/stronze/task/1")
                    .slot(1)
                    .item("gregtech:gt.blockmachines", 1, 22536)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition BETTER_BLA_WAIT_A_MINUTE = quest(
        "gtnl:tier075superheated/machine/better_bla_wait_a_minute",
        "RA9eXJDzS32-ustOxyHgrQ==").icon("gregtech:gt.blockmachines", 1, 140)
            .prerequisite(uuid("9hskX6EZQeG8jrVDU0K_hw=="))
            .prerequisite(uuid("QEJyflMpRDiutYQqGI8oUg=="))
            .task(
                TaskBuilders.checkbox("gtnl:tier075superheated/machine/better_bla_wait_a_minute/task/0")
                    .slot(0)
                    .build())
            .reward(
                RewardBuilders.item("gtnl:tier075superheated/machine/better_bla_wait_a_minute/reward/0")
                    .slot(0)
                    .item("bartworks:gt.bwMetaGeneratedingot", 12, 25111)
                    .build())
            .build();

    public static final QuestDefinition BREEL = quest(
        "gtnl:tier075superheated/machine/breel",
        "9hskX6EZQeG8jrVDU0K_hw==").icon("bartworks:gt.bwMetaGeneratedingot", 1, 25111)
            .prerequisite(uuid("1QM7Ia08R1Gr_s_bDHiSyA=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/breel/task/0")
                    .slot(0)
                    .item("bartworks:gt.bwMetaGeneratedingot", 1, 25111)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .task(
                TaskBuilders.optionalRetrieval("gtnl:tier075superheated/machine/breel/task/1")
                    .slot(1)
                    .item("gregtech:gt.blockmachines", 1, 22541)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition CONFORMING_OR_PRESSING_IDK = quest(
        "gtnl:tier075superheated/machine/conforming_or_pressing_idk",
        "rcMamffGTseFyACA3i7zUQ==").icon("gregtech:gt.blockmachines", 1, 21086)
            .prerequisite(uuid("LDodKZyAQhiA1kdr4HxoWg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/conforming_or_pressing_idk/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21086)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition THE_UNMATCHED_POWER_OF_THE_SUN = quest(
        "gtnl:tier075superheated/machine/the_unmatched_power_of_the_sun",
        "a7jn4e-OSfKElQ5WhlUlMw==").icon("gregtech:gt.blockmachines", 1, 21116)
            .prerequisite(uuid("LDodKZyAQhiA1kdr4HxoWg=="))
            .prerequisite(uuid("SQo2-hr-TCasVo8_la0HKQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/the_unmatched_power_of_the_sun/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21116)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition FORGE_OF_THE_GODS = quest(
        "gtnl:tier075superheated/machine/forge_of_the_gods",
        "SQo2-hr-TCasVo8_la0HKQ==").icon("gregtech:gt.blockmachines", 1, 21124)
            .prerequisite(uuid("9hskX6EZQeG8jrVDU0K_hw=="))
            .prerequisite(uuid("QEJyflMpRDiutYQqGI8oUg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/forge_of_the_gods/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21124)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition CLASSIC_COKE = quest(
        "gtnl:tier075superheated/machine/classic_coke",
        "qa6pQlvZRB6OvjdaBdVw9Q==").icon("gregtech:gt.blockmachines", 1, 21126)
            .prerequisite(uuid("9hskX6EZQeG8jrVDU0K_hw=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/classic_coke/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21126)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition REGROWTH = quest(
        "gtnl:tier075superheated/machine/regrowth",
        "W1dUSfYBRYqIQo71IVV8MQ==").icon("gregtech:gt.blockmachines", 1, 21122)
            .prerequisite(uuid("KwkFvZTRRuuj8-M3KHy66A=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/regrowth/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21122)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition CARPENTRY = quest(
        "gtnl:tier075superheated/machine/carpentry",
        "KwkFvZTRRuuj8-M3KHy66A==").icon("gregtech:gt.blockmachines", 1, 21118)
            .prerequisite(uuid("LDodKZyAQhiA1kdr4HxoWg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/carpentry/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21118)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition EXTRACTINATING = quest(
        "gtnl:tier075superheated/machine/extractinating",
        "SU-S_BoeStacdLPKW0_CGQ==").icon("gregtech:gt.blockmachines", 1, 21123)
            .prerequisite(uuid("LDodKZyAQhiA1kdr4HxoWg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/extractinating/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21123)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition PIPELESS = quest(
        "gtnl:tier075superheated/machine/pipeless",
        "wAKom1nMT9egqN5LW6Z_9A==").icon("gregtech:gt.blockmachines", 1, 22576)
            .prerequisite(uuid("J2A3CMyLQJWLceK9M83apg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/pipeless/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 22576)
                    .item("gregtech:gt.blockmachines", 1, 22577)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition MASS_PRODUCTION = quest(
        "gtnl:tier075superheated/machine/mass_production",
        "J2A3CMyLQJWLceK9M83apg==").icon("gregtech:gt.blockmachines", 1, 21120)
            .prerequisite(uuid("KL3toa-sTKq8ojVmlG3Jdw=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/mass_production/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21120)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition LAVA_FOREVER = quest(
        "gtnl:tier075superheated/machine/lava_forever",
        "yKPAFjuuRa-ct5flqbtjQA==").icon("gregtech:gt.blockmachines", 1, 21119)
            .prerequisite(uuid("J2A3CMyLQJWLceK9M83apg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/lava_forever/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21119)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition CACTUS_WONDER = quest(
        "gtnl:tier075superheated/machine/cactus_wonder",
        "SOJOLd3iRqmyYwt0H4f4BQ==").icon("gregtech:gt.blockmachines", 1, 21117)
            .prerequisite(uuid("J2A3CMyLQJWLceK9M83apg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/machine/cactus_wonder/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21117)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static void contribute(ChapterBuilder chapter) {
        chapter.quest(THESE_THINGS_DO_GROW_ON_TREES.at(-84, 24, 24, 24));
        chapter.quest(FUSING.at(-48, 24, 24, 24));
        chapter.quest(BLENDING.at(24, 24, 24, 24));
        chapter.quest(ROCK_BREAKING.at(-120, 60, 24, 24));
        chapter.quest(STRONZE.at(-48, 60, 24, 24));
        chapter.quest(BETTER_BLA_WAIT_A_MINUTE.at(-12, 60, 24, 24));
        chapter.quest(BREEL.at(24, 60, 24, 24));
        chapter.quest(CONFORMING_OR_PRESSING_IDK.at(-120, 96, 24, 24));
        chapter.quest(THE_UNMATCHED_POWER_OF_THE_SUN.at(-48, 96, 24, 24));
        chapter.quest(FORGE_OF_THE_GODS.at(-12, 96, 24, 24));
        chapter.quest(CLASSIC_COKE.at(24, 96, 24, 24));
        chapter.quest(REGROWTH.at(-156, 132, 24, 24));
        chapter.quest(CARPENTRY.at(-120, 132, 24, 24));
        chapter.quest(EXTRACTINATING.at(-84, 156, 24, 24));
        chapter.quest(PIPELESS.at(-48, 192, 24, 24));
        chapter.quest(MASS_PRODUCTION.at(-12, 192, 24, 24));
        chapter.quest(LAVA_FOREVER.at(24, 192, 24, 24));
        chapter.quest(CACTUS_WONDER.at(-12, 228, 24, 24));
    }
}
