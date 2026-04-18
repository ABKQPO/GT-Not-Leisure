package com.science.gtnl.common.queset;

import java.util.UUID;

import com.hfstudio.bqapi.api.builder.ChapterBuilder;
import com.hfstudio.bqapi.api.builder.Chapters;
import com.hfstudio.bqapi.api.builder.QuestBuilder;
import com.hfstudio.bqapi.api.builder.Quests;

import betterquesting.api.utils.UuidConverter;

public final class QuestCompat {

    private QuestCompat() {}

    public static UUID uuid(String encoded) {
        return UuidConverter.decodeUuid(encoded);
    }

    public static ChapterBuilder chapter(String id, String encoded) {
        return Chapters.chapter(id)
            .uuid(uuid(encoded));
    }

    public static QuestBuilder quest(String id, String encoded) {
        return Quests.quest(id)
            .uuid(uuid(encoded));
    }
}
