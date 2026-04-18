package com.hfstudio.bqapi.api.builder;

public final class Chapters {

    private Chapters() {}

    public static ChapterBuilder chapter(String id) {
        return ChapterBuilder.chapter(id);
    }

    public static ImportedChapterBuilder imported(String id) {
        return new ImportedChapterBuilder(id);
    }
}
