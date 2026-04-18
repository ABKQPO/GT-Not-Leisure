package com.hfstudio.bqapi.api.importer;

import java.util.Objects;

public final class ImportedQuestFolder {

    private final String modId;
    private final String rootPath;
    private final ClassLoader classLoader;

    ImportedQuestFolder(String modId, String rootPath, ClassLoader classLoader) {
        this.modId = normalizeModId(modId);
        this.rootPath = normalizeRootPath(rootPath);
        this.classLoader = classLoader == null ? ImportedQuestFolder.class.getClassLoader() : classLoader;
    }

    public String getModId() {
        return modId;
    }

    public String getRootPath() {
        return rootPath;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getClasspathRoot() {
        return "assets/" + modId + "/" + rootPath;
    }

    private static String normalizeModId(String modId) {
        String value = Objects.requireNonNull(modId, "modId")
            .trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("modId must not be empty");
        }
        return value;
    }

    private static String normalizeRootPath(String rootPath) {
        String value = Objects.requireNonNull(rootPath, "rootPath")
            .replace('\\', '/')
            .trim();
        while (value.startsWith("/")) {
            value = value.substring(1);
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("rootPath must not be empty");
        }
        return value;
    }

    public static final class Builder {

        private final String modId;
        private final String rootPath;
        private ClassLoader classLoader;

        public Builder(String modId, String rootPath) {
            this.modId = modId;
            this.rootPath = rootPath;
        }

        public Builder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public ImportedQuestFolder build() {
            return new ImportedQuestFolder(modId, rootPath, classLoader);
        }
    }
}
