package com.science.gtnl.common.queset.tier0999supercritical;

import static com.science.gtnl.common.queset.QuestCompat.quest;
import static com.science.gtnl.common.queset.QuestCompat.uuid;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.RewardBuilders;
import com.hfstudio.bqapi.api.builder.TaskBuilders;
import com.hfstudio.bqapi.api.definition.QuestDefinition;

public final class MachineQuests {

    private MachineQuests() {}

    public static final QuestDefinition OUT_OF_THE_STREAM = quest(
        "gtnl:tier0999supercritical/machine/out_of_the_stream",
        "Wj4COkJ5Rm60BH_z_HP6qQ==").icon("gregtech:gt.blockmachines", 1, 22578)
            .prerequisite(uuid("mJ4z0kDxSO-dsZtZ5BOMvQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/out_of_the_stream/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 22578)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition COMPRESSED_STEAM = quest(
        "gtnl:tier0999supercritical/machine/compressed_steam",
        "mJ4z0kDxSO-dsZtZ5BOMvQ==").icon("bartworks:gt.bwMetaGeneratedingot", 1, 25109)
            .prerequisite(uuid("b5qQ79exTKGqlpoaJrdCfQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/compressed_steam/task/0")
                    .slot(0)
                    .item("bartworks:gt.bwMetaGeneratedingot", 1, 25109)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .task(
                TaskBuilders.optionalRetrieval("gtnl:tier0999supercritical/machine/compressed_steam/task/1")
                    .slot(1)
                    .item("gregtech:gt.blockmachines", 1, 22531)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition INTO_THE_STREAM = quest(
        "gtnl:tier0999supercritical/machine/into_the_stream",
        "iW4bJsRGQSu7_FiR1DjquA==").icon("gregtech:gt.blockmachines", 1, 22579)
            .prerequisite(uuid("mJ4z0kDxSO-dsZtZ5BOMvQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/into_the_stream/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 22579)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition SCRUB_A_TINY_DUB_DUB = quest(
        "gtnl:tier0999supercritical/machine/scrub_a_tiny_dub_dub",
        "R0otWWS8TFO1NYHPHe-Csg==").icon("gregtech:gt.blockmachines", 1, 31082)
            .prerequisite(uuid("mJ4z0kDxSO-dsZtZ5BOMvQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/scrub_a_tiny_dub_dub/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 31082)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition A_LITLE_BIT_OF_SEPARATION_ANXIETY = quest(
        "gtnl:tier0999supercritical/machine/a_litle_bit_of_separation_anxiety",
        "YjHEc8t4RS21nAOL0hCiFw==").icon("gregtech:gt.blockmachines", 1, 31080)
            .prerequisite(uuid("mJ4z0kDxSO-dsZtZ5BOMvQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/a_litle_bit_of_separation_anxiety/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 31080)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition NOT_THAT_BIG_OF_A_CUBE = quest(
        "gtnl:tier0999supercritical/machine/not_that_big_of_a_cube",
        "W3u_ktw5RviSiDwklXqVrQ==").icon("gregtech:gt.blockmachines", 1, 31041)
            .prerequisite(uuid("mJ4z0kDxSO-dsZtZ5BOMvQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/not_that_big_of_a_cube/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 31041)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition A_SMALL_BENDING_MACHINE = quest(
        "gtnl:tier0999supercritical/machine/a_small_bending_machine",
        "eUPfiUfVSlarTsGoAt8JVg==").icon("gregtech:gt.blockmachines", 1, 31083)
            .prerequisite(uuid("mJ4z0kDxSO-dsZtZ5BOMvQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/a_small_bending_machine/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 31083)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition MOUNTAINS_AT_MAX_A_PILE_OF_BRONZE = quest(
        "gtnl:tier0999supercritical/machine/mountains_at_max_a_pile_of_bronze",
        "riE5LIUFRYmWlIzd4wiOng==").icon("gregtech:gt.blockmachines", 1, 31086)
            .prerequisite(uuid("F8kpJGKLR2i1taFBEEQ7DQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/mountains_at_max_a_pile_of_bronze/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 31086)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition INFINITE_CREATION = quest(
        "gtnl:tier0999supercritical/machine/infinite_creation",
        "aEoijugLS7G7R1qPnNXMZg==").icon("gregtech:gt.blockmachines", 1, 21114)
            .prerequisite(uuid("F8kpJGKLR2i1taFBEEQ7DQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/infinite_creation/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21114)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition ENDGAME_STEAM_GENERATION = quest(
        "gtnl:tier0999supercritical/machine/endgame_steam_generation",
        "d1lb3xB9T-G5sS47GLYcEQ==").icon("gregtech:gt.blockmachines", 1, 21125)
            .prerequisite(uuid("F8kpJGKLR2i1taFBEEQ7DQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/endgame_steam_generation/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 21125)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition RING_BLOCK = quest(
        "gtnl:tier0999supercritical/machine/ring_block",
        "4rdyNXpwQLG6n1QdWRK9JA==").icon("sciencenotleisure:tile.MetaCasing", 1, 21)
            .prerequisite(uuid("LaMMK-_LTE-sNUjubbQZgQ=="))
            .prerequisite(uuid("9AR6cFFdTZmSTXNewYnGPw=="))
            .prerequisite(uuid("NbqZdOqbRuG8Xv-KYCajbA=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/ring_block/task/0")
                    .slot(0)
                    .item("sciencenotleisure:tile.MetaCasing", 1, 21)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition CHEVRON_BLOCK = quest(
        "gtnl:tier0999supercritical/machine/chevron_block",
        "BOWDVT5YQcOzjteLg6VPkQ==").icon("sciencenotleisure:tile.MetaCasing", 1, 22)
            .prerequisite(uuid("X020oIz9SCiiJhbmCGmLtQ=="))
            .prerequisite(uuid("4rdyNXpwQLG6n1QdWRK9JA=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/chevron_block/task/0")
                    .slot(0)
                    .item("sciencenotleisure:tile.MetaCasing", 1, 22)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition THE_STEAMGATE = quest(
        "gtnl:tier0999supercritical/machine/the_steamgate",
        "4b1deRqfQ9ibu3FXSMQH4Q==").icon("gregtech:gt.blockmachines", 1, 21113)
            .main(true)
            .prerequisite(uuid("n2WTjOt3TBK8X2b2W6TLZA=="))
            .prerequisite(uuid("BOWDVT5YQcOzjteLg6VPkQ=="))
            .prerequisite(uuid("3QfgXMVkTwyZTuuRKJgCKw=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/machine/the_steamgate/task/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 2, 21113)
                    .item("sciencenotleisure:tile.MetaCasing", 14, 22)
                    .item("sciencenotleisure:tile.MetaCasing", 48, 21)
                    .item("sciencenotleisure:MetaItem", 1, 81)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .reward(
                RewardBuilders.item("gtnl:tier0999supercritical/machine/the_steamgate/reward/0")
                    .slot(0)
                    .item("gregtech:gt.blockmachines", 1, 22501)
                    .build())
            .build();

    public static void contribute(ChapterBuilder chapter) {
        chapter.quest(OUT_OF_THE_STREAM.at(-48, 60, 24, 24));
        chapter.quest(COMPRESSED_STEAM.at(-12, 60, 24, 24));
        chapter.quest(INTO_THE_STREAM.at(24, 60, 24, 24));
        chapter.quest(SCRUB_A_TINY_DUB_DUB.at(-84, 96, 24, 24));
        chapter.quest(A_LITLE_BIT_OF_SEPARATION_ANXIETY.at(-48, 96, 24, 24));
        chapter.quest(NOT_THAT_BIG_OF_A_CUBE.at(24, 96, 24, 24));
        chapter.quest(A_SMALL_BENDING_MACHINE.at(60, 96, 24, 24));
        chapter.quest(MOUNTAINS_AT_MAX_A_PILE_OF_BRONZE.at(-48, 132, 24, 24));
        chapter.quest(INFINITE_CREATION.at(-12, 132, 24, 24));
        chapter.quest(ENDGAME_STEAM_GENERATION.at(24, 132, 24, 24));
        chapter.quest(RING_BLOCK.at(-12, 204, 24, 24));
        chapter.quest(CHEVRON_BLOCK.at(-12, 240, 24, 24));
        chapter.quest(THE_STEAMGATE.at(-24, 276, 48, 48));
    }
}
