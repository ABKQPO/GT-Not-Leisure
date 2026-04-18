package com.science.gtnl.common.queset.tier075superheated;

import static com.science.gtnl.common.queset.QuestCompat.quest;
import static com.science.gtnl.common.queset.QuestCompat.uuid;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.TaskBuilders;
import com.hfstudio.bqapi.api.definition.QuestDefinition;

public final class ItemQuests {

    private ItemQuests() {}

    public static final QuestDefinition CLAY_COMPOUND = quest(
        "gtnl:tier075superheated/item/clay_compound",
        "6A4wnmpkTOa08s8Yuttsvw==").icon("gregtech:gt.metaitem.01", 1, 11402)
            .prerequisite(uuid("sd5zePjNStSSuoW04WegtA=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/item/clay_compound/task/0")
                    .slot(0)
                    .item("gregtech:gt.metaitem.01", 1, 11402)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static final QuestDefinition YOU_RE_GOING_TO_LOVE_THIS = quest(
        "gtnl:tier075superheated/item/you_re_going_to_love_this",
        "LDodKZyAQhiA1kdr4HxoWg==").icon("sciencenotleisure:MetaItem", 1, 89)
            .prerequisite(uuid("6A4wnmpkTOa08s8Yuttsvw=="))
            .task(
                TaskBuilders.retrieval("gtnl:tier075superheated/item/you_re_going_to_love_this/task/0")
                    .slot(0)
                    .item("sciencenotleisure:MetaItem", 1, 89)
                    .item("sciencenotleisure:MetaItem", 1, 93)
                    .item("sciencenotleisure:MetaItem", 1, 90)
                    .item("sciencenotleisure:MetaItem", 1, 91)
                    .item("sciencenotleisure:MetaItem", 1, 92)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static void contribute(ChapterBuilder chapter) {
        chapter.quest(CLAY_COMPOUND.at(-84, 60, 24, 24));
        chapter.quest(YOU_RE_GOING_TO_LOVE_THIS.at(-84, 96, 24, 24));
    }
}
