package com.hfstudio.bqapi;

import java.util.UUID;

import betterquesting.api.utils.UuidConverter;

public final class BQApiLangKeys {

    private BQApiLangKeys() {}

    private static String encode(UUID uuid) {
        return UuidConverter.encodeUuidStripPadding(uuid);
    }

    public static String questName(UUID uuid) {
        return "betterquesting.quest." + encode(uuid) + ".name";
    }

    public static String questDesc(UUID uuid) {
        return "betterquesting.quest." + encode(uuid) + ".desc";
    }

    public static String chapterName(UUID uuid) {
        return "betterquesting.questline." + encode(uuid) + ".name";
    }

    public static String chapterDesc(UUID uuid) {
        return "betterquesting.questline." + encode(uuid) + ".desc";
    }
}
