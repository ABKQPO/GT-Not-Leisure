package com.science.gtnl.loader;

import java.util.Collections;
import java.util.List;

import com.hfstudio.bqapi.BQApi;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.hfstudio.bqapi.api.importer.ImportedQuestFolders;

public class QuestLoader {

    private static boolean registered;

    private static final List<ChapterDefinition> CHAPTERS = Collections.unmodifiableList(
        BQApi.importFolder(
            ImportedQuestFolders.folder("sciencenotleisure", "quest")
                .build()));

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
