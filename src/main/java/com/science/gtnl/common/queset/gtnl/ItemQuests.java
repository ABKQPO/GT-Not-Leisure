package com.science.gtnl.common.queset.gtnl;

import static com.science.gtnl.common.queset.QuestCompat.quest;
import static com.science.gtnl.common.queset.QuestCompat.uuid;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.RewardBuilders;
import com.hfstudio.bqapi.api.builder.TaskBuilders;
import com.hfstudio.bqapi.api.definition.QuestDefinition;

public final class ItemQuests {

    private ItemQuests() {}

    public static final QuestDefinition WHAT_SORT_OF_MUSIC_DID_PEOPLE_LISTEN_TO_IN_THE_BRONZE_AGE_METAL_METAL = quest(
        "gtnl:gtnl/item/what_sort_of_music_did_people_listen_to_in_the_bronze_age_metal_metal",
        "sLmbjGYlTYuHxpFynCb6rQ==").icon("gregtech:gt.metaitem.01", 1, 11300)
            .prerequisite(uuid("EVhlJXyIR3SmEIhMTUdsWA=="))
            .task(
                TaskBuilders
                    .retrieval(
                        "gtnl:gtnl/item/what_sort_of_music_did_people_listen_to_in_the_bronze_age_metal_metal/task/0")
                    .slot(0)
                    .item("gregtech:gt.metaitem.01", 64, 11300)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .reward(
                RewardBuilders
                    .item(
                        "gtnl:gtnl/item/what_sort_of_music_did_people_listen_to_in_the_bronze_age_metal_metal/reward/0")
                    .slot(0)
                    .item("gregtech:gt.metaitem.01", 12, 17300)
                    .build())
            .build();

    public static final QuestDefinition THIS_TIME_IT_S_NOT_HUNGER_OR_THIRST = quest(
        "gtnl:gtnl/item/this_time_it_s_not_hunger_or_thirst",
        "IAzmh1pwRbayb3c436tQcw==").icon("minecraft:gold_ingot", 1, 0)
            .prerequisite(uuid("UODPZCyXT96ap5_4VL1FHA=="))
            .task(
                TaskBuilders.retrieval("gtnl:gtnl/item/this_time_it_s_not_hunger_or_thirst/task/0")
                    .slot(0)
                    .item("minecraft:gold_ingot", 12, 0)
                    .ignoreNbt(true)
                    .partialMatch(true)
                    .consume(false)
                    .groupDetect(false)
                    .autoConsume(false)
                    .build())
            .build();

    public static void contribute(ChapterBuilder chapter) {
        chapter.quest(WHAT_SORT_OF_MUSIC_DID_PEOPLE_LISTEN_TO_IN_THE_BRONZE_AGE_METAL_METAL.at(-12, 60, 24, 24));
        chapter.quest(THIS_TIME_IT_S_NOT_HUNGER_OR_THIRST.at(-12, 384, 24, 24));
    }
}
