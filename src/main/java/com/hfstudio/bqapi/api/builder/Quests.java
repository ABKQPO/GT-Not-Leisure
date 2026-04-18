package com.hfstudio.bqapi.api.builder;

public final class Quests {

    private Quests() {}

    public static QuestBuilder quest(String id) {
        return QuestBuilder.quest(id);
    }
}
