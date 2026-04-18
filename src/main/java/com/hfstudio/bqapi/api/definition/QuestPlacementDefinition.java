package com.hfstudio.bqapi.api.definition;

import java.util.Objects;

public final class QuestPlacementDefinition {

    private final QuestDefinition quest;
    private final int x;
    private final int y;
    private final int sizeX;
    private final int sizeY;

    public QuestPlacementDefinition(QuestDefinition quest, int x, int y, int sizeX, int sizeY) {
        this.quest = Objects.requireNonNull(quest, "quest");
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public QuestDefinition getQuest() {
        return quest;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }
}
