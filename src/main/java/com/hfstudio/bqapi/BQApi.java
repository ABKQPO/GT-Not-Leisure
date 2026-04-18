package com.hfstudio.bqapi;

import net.minecraft.server.MinecraftServer;

import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.hfstudio.bqapi.api.importer.ImportedQuestFolder;
import com.hfstudio.bqapi.api.importer.ImportedQuestFolders;
import com.hfstudio.bqapi.runtime.BQDefinitionRegistry;
import com.hfstudio.bqapi.runtime.BQRuntimeApplier;
import com.hfstudio.bqapi.runtime.importer.BQImportedQuestLoader;

import java.util.List;

public final class BQApi {

    private static final BQDefinitionRegistry REGISTRY = new BQDefinitionRegistry();

    private BQApi() {}

    public static void register(ChapterDefinition chapter) {
        REGISTRY.register(chapter);
    }

    public static List<ChapterDefinition> importFolder(String modId, String rootPath) {
        return importFolder(ImportedQuestFolders.folder(modId, rootPath).build());
    }

    public static List<ChapterDefinition> importFolder(ImportedQuestFolder folder) {
        return new BQImportedQuestLoader().importFolder(folder);
    }

    public static void registerImportedFolder(String modId, String rootPath) {
        registerImportedFolder(ImportedQuestFolders.folder(modId, rootPath).build());
    }

    public static void registerImportedFolder(ImportedQuestFolder folder) {
        for (ChapterDefinition chapter : importFolder(folder)) {
            register(chapter);
        }
    }

    public static void reinject(MinecraftServer server) {
        new BQRuntimeApplier(REGISTRY).applyAll(server);
    }
}
