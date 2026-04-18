package com.science.gtnl.common.queset.tier0999supercritical;

import static com.science.gtnl.common.queset.QuestCompat.chapter;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;

public final class Tier0999SupercriticalPage {

    public static final ChapterDefinition CHAPTER = build();

    private Tier0999SupercriticalPage() {}

    private static ChapterDefinition build() {
        ChapterBuilder chapter = chapter("gtnl:chapter/tier0999_supercritical", "GTNotLeisure99SteamAgQ==")
            .orderAfterEncoded("GTNotLeisure75SteamAge==")
            .icon("sciencenotleisure:MetaItem", 1, 82);
        MachineQuests.contribute(chapter);
        ItemQuests.contribute(chapter);
        MiscQuests.contribute(chapter);
        return chapter.build();
    }
}
