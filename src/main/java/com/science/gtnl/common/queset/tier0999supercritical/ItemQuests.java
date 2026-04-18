package com.science.gtnl.common.queset.tier0999supercritical;

import static com.science.gtnl.common.queset.QuestCompat.quest;
import static com.science.gtnl.common.queset.QuestCompat.uuid;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.TaskBuilders;
import com.hfstudio.bqapi.api.definition.QuestDefinition;

public final class ItemQuests {

    private ItemQuests() {}

    public static final QuestDefinition ADVANCED_HYDRAULICS = quest(
        "gtnl:tier0999supercritical/item/advanced_hydraulics",
        "F8kpJGKLR2i1taFBEEQ7DQ==").icon("sciencenotleisure:MetaItem", 1, 95)
            .prerequisite(uuid("mJ4z0kDxSO-dsZtZ5BOMvQ=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/item/advanced_hydraulics/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 96)
                    .item("sciencenotleisure:MetaItem", 1, 97)
                    .item("sciencenotleisure:MetaItem", 1, 95)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition PLATE = quest(
        "gtnl:tier0999supercritical/item/plate",
        "NbqZdOqbRuG8Xv-KYCajbA==").icon("sciencenotleisure:MetaItem", 1, 86)
            .prerequisite(uuid("aEoijugLS7G7R1qPnNXMZg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/item/plate/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 86)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition FRAME = quest(
        "gtnl:tier0999supercritical/item/frame",
        "LaMMK-_LTE-sNUjubbQZgQ==").icon("sciencenotleisure:MetaItem", 1, 87)
            .prerequisite(uuid("aEoijugLS7G7R1qPnNXMZg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/item/frame/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 87)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition CHEVRON = quest(
        "gtnl:tier0999supercritical/item/chevron",
        "9AR6cFFdTZmSTXNewYnGPw==").icon("sciencenotleisure:MetaItem", 1, 82)
            .prerequisite(uuid("aEoijugLS7G7R1qPnNXMZg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/item/chevron/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 82)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition IRIS_BLADE = quest(
        "gtnl:tier0999supercritical/item/iris_blade",
        "md1HW0pQQ2OhgonRAL4joA==").icon("sciencenotleisure:MetaItem", 1, 84)
            .prerequisite(uuid("aEoijugLS7G7R1qPnNXMZg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/item/iris_blade/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 84)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition CHEVRON_UPGRADE = quest(
        "gtnl:tier0999supercritical/item/chevron_upgrade",
        "X020oIz9SCiiJhbmCGmLtQ==").icon("sciencenotleisure:MetaItem", 1, 83)
            .prerequisite(uuid("LaMMK-_LTE-sNUjubbQZgQ=="))
            .prerequisite(uuid("9AR6cFFdTZmSTXNewYnGPw=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/item/chevron_upgrade/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 83)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition IRIS_UPGRADE = quest(
        "gtnl:tier0999supercritical/item/iris_upgrade",
        "n2WTjOt3TBK8X2b2W6TLZA==").icon("sciencenotleisure:MetaItem", 1, 85)
            .prerequisite(uuid("md1HW0pQQ2OhgonRAL4joA=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/item/iris_upgrade/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 85)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition CORE_CRYSTAL = quest(
        "gtnl:tier0999supercritical/item/core_crystal",
        "3QfgXMVkTwyZTuuRKJgCKw==").icon("sciencenotleisure:MetaItem", 1, 88)
            .prerequisite(uuid("aEoijugLS7G7R1qPnNXMZg=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier0999supercritical/item/core_crystal/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 88)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static void contribute(ChapterBuilder chapter) {
        chapter.quest(ADVANCED_HYDRAULICS.at(-12, 96, 24, 24));
        chapter.quest(PLATE.at(-48, 168, 24, 24));
        chapter.quest(FRAME.at(-12, 168, 24, 24));
        chapter.quest(CHEVRON.at(24, 168, 24, 24));
        chapter.quest(IRIS_BLADE.at(-48, 204, 24, 24));
        chapter.quest(CHEVRON_UPGRADE.at(24, 204, 24, 24));
        chapter.quest(IRIS_UPGRADE.at(-48, 240, 24, 24));
        chapter.quest(CORE_CRYSTAL.at(24, 240, 24, 24));
    }
}
