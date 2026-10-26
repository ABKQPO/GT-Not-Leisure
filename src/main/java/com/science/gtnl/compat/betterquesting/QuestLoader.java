package com.science.gtnl.compat.betterquesting;

import java.util.List;
import java.util.UUID;

import com.gtnllib.mod.ModList;
import com.hfstudio.bqapi.BQApi;
import com.hfstudio.bqapi.api.builder.Chapters;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.compat.IModCompat;

import betterquesting.api.utils.UuidConverter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class QuestLoader implements IModCompat {

    public static final UUID STEAM_AGE_UUID = UuidConverter.decodeUuid("AAAAAAAAAAAAAAAAAAAAAg==");

    public static final String RESOURCE_ROOT = "quest";

    public static boolean registered;

    public static final List<ChapterDefinition> CHAPTERS = new ObjectArrayList<>();

    static {
        CHAPTERS.add(
            Chapters.imported("GTNotLeisure75SteamAge")
                .resourceFolder(ScienceNotLeisure.RESOURCE_ROOT_ID, RESOURCE_ROOT)
                .lineDirectory("Tier075Superheat-GTNotLeisure75SteamAge==")
                .uuidFromResource()
                .orderAfter(STEAM_AGE_UUID)
                .build());
        CHAPTERS.add(
            Chapters.imported("GTNotLeisure99SteamAge")
                .resourceFolder(ScienceNotLeisure.RESOURCE_ROOT_ID, RESOURCE_ROOT)
                .lineDirectory("Tier0999Supercri-GTNotLeisure99SteamAge==")
                .uuidFromResource()
                .orderAfter(UuidConverter.decodeUuid("GTNotLeisure75SteamAge=="))
                .build());
        CHAPTERS.add(
            Chapters.imported("GTNotLeisureQuestsLine")
                .resourceFolder(ScienceNotLeisure.RESOURCE_ROOT_ID, RESOURCE_ROOT)
                .lineDirectory("GTNotLeisure-GTNotLeisureQuestsLine==")
                .orderAfter(UuidConverter.decodeUuid("AAAAAAAAAAAAAAAAAAAAJQ==")) // Kill All The Things
                .uuidFromResource()
                .build());
    }

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

    @Override
    public boolean isEnabled() {
        return ModList.BetterQuestingAPI.isModLoaded();
    }

    @Override
    public void preInit() {
        registry();
    }
}
