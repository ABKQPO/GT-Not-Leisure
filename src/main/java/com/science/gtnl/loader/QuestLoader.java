package com.science.gtnl.loader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hfstudio.bqapi.BQApi;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.science.gtnl.common.queset.gtnl.GTNotLeisurePage;
import com.science.gtnl.common.queset.tier075superheated.Tier075SuperheatedPage;
import com.science.gtnl.common.queset.tier0999supercritical.Tier0999SupercriticalPage;

public class QuestLoader {

    private static boolean registered;

    private static final List<ChapterDefinition> CHAPTERS = Collections.unmodifiableList(
        Arrays.asList(Tier075SuperheatedPage.CHAPTER, Tier0999SupercriticalPage.CHAPTER, GTNotLeisurePage.CHAPTER));

    private QuestLoader() {}

    public static void registry() {
        if (registered) {
            return;
        }
        for (ChapterDefinition chapter : CHAPTERS) {
            BQApi.register(chapter);
        }
        registered = true;
    }

    public static List<ChapterDefinition> chapters() {
        return CHAPTERS;
    }
}
