package com.science.gtnl.loader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.hfstudio.bqapi.BQApi;
import com.hfstudio.bqapi.api.builder.Chapters;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;

import betterquesting.api.utils.UuidConverter;

public class QuestLoader {

    public static final UUID STEAM_AGE_UUID = UuidConverter.decodeUuid("AAAAAAAAAAAAAAAAAAAAAg==");

    public static final UUID COINS_UUID = UuidConverter.decodeUuid("AAAAAAAAAAAAAAAAAAAAEA==");

    public static final String RESOURCE_MOD_ID = "sciencenotleisure";
    public static final String RESOURCE_ROOT = "quest";

    public static boolean registered;

    public static final List<ChapterDefinition> CHAPTERS = Collections.unmodifiableList(
        Arrays.asList(
            Chapters.imported("GTNotLeisure75SteamAge")
                .resourceFolder(RESOURCE_MOD_ID, RESOURCE_ROOT)
                .lineDirectory("Tier075Superheat-GTNotLeisure75SteamAge==")
                .uuidFromResource()
                .orderAfter(STEAM_AGE_UUID)
                .build(),
            Chapters.imported("GTNotLeisure99SteamAge")
                .resourceFolder(RESOURCE_MOD_ID, RESOURCE_ROOT)
                .lineDirectory("Tier0999Supercri-GTNotLeisure99SteamAge==")
                .uuidFromResource()
                .orderAfter(UuidConverter.decodeUuid("GTNotLeisure75SteamAge=="))
                .build(),
            Chapters.imported("GTNotLeisureQuestsLine")
                .resourceFolder(RESOURCE_MOD_ID, RESOURCE_ROOT)
                .lineDirectory("GTNotLeisure-GTNotLeisureQuestsLine==")
                .uuidFromResource()
                .orderAfter(COINS_UUID)
                .build()));

    public QuestLoader() {}

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
