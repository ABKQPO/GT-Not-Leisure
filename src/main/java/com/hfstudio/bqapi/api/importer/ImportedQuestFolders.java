package com.hfstudio.bqapi.api.importer;

public final class ImportedQuestFolders {

    private ImportedQuestFolders() {}

    public static ImportedQuestFolder.Builder folder(String modId, String rootPath) {
        return new ImportedQuestFolder.Builder(modId, rootPath);
    }
}
