package com.science.gtnl.common.queset.gtnl;

import static com.science.gtnl.common.queset.QuestCompat.chapter;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;

public final class GTNotLeisurePage {

    public static final ChapterDefinition CHAPTER = build();

    private GTNotLeisurePage() {}

    private static ChapterDefinition build() {
        ChapterBuilder chapter = chapter("gtnl:chapter/gt_not_leisure", "GTNotLeisureQuestsLinQ==")
            .orderAfterEncoded("GTNotLeisure99SteamAge==")
            .icon("sciencenotleisure:MetaItem", 1, 100);
        MachineQuests.contribute(chapter);
        ItemQuests.contribute(chapter);
        MiscQuests.contribute(chapter);
        return chapter.build();
    }
}
