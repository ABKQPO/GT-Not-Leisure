package com.science.gtnl.common.queset.tier075superheated;

import static com.science.gtnl.common.queset.QuestCompat.chapter;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;

public final class Tier075SuperheatedPage {

    public static final ChapterDefinition CHAPTER = build();

    private Tier075SuperheatedPage() {}

    private static ChapterDefinition build() {
        ChapterBuilder chapter = chapter("gtnl:chapter/tier075_superheated", "GTNotLeisure75SteamAgQ==")
            .orderAfterEncoded("AAAAAAAAAAAAAAAAAAAAAg==")
            .icon("gregtech:gt.blockmachines", 1, 21119);
        MachineQuests.contribute(chapter);
        ItemQuests.contribute(chapter);
        MiscQuests.contribute(chapter);
        return chapter.build();
    }
}
