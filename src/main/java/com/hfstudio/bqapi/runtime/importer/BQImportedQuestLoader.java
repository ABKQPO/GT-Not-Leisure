package com.hfstudio.bqapi.runtime.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonParser;
import com.hfstudio.bqapi.api.builder.Chapters;
import com.hfstudio.bqapi.api.builder.Quests;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.hfstudio.bqapi.api.definition.QuestDefinition;
import com.hfstudio.bqapi.api.definition.QuestPlacementDefinition;
import com.hfstudio.bqapi.api.definition.RawRewardDefinition;
import com.hfstudio.bqapi.api.definition.RawTaskDefinition;
import com.hfstudio.bqapi.api.importer.ImportedQuestFolder;

import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.NBTConverter;
import betterquesting.api.utils.UuidConverter;

public final class BQImportedQuestLoader {

    public List<ChapterDefinition> importFolder(ImportedQuestFolder folder) {
        Objects.requireNonNull(folder, "folder");
        try {
            ClasspathQuestResources resources = new ClasspathQuestResources(folder);
            Map<UUID, Integer> orderIndex = resources.readQuestLineOrder();
            List<ImportedLineData> lines = new ArrayList<>();
            for (String lineDirectory : resources.listDirectories("QuestLines")) {
                lines.add(readLine(resources, folder, lineDirectory));
            }

            lines.sort(Comparator.comparingInt(line -> orderIndex.getOrDefault(line.uuid, Integer.MAX_VALUE)));

            List<ChapterDefinition> chapters = new ArrayList<>(lines.size());
            UUID previous = null;
            for (ImportedLineData line : lines) {
                chapters.add(toChapterDefinition(defaultChapterId(folder, line.lineDirectory), line, line.uuid, previous));
                previous = line.uuid;
            }
            return chapters;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to import BetterQuesting folder " + folder.getClasspathRoot(), e);
        }
    }

    public ChapterDefinition importLine(String chapterId, ImportedQuestFolder folder, String lineDirectory,
        UUID explicitUuid, boolean uuidFromResource, String explicitOrderAfterEncoded, NBTTagCompound explicitIconNbt) {
        Objects.requireNonNull(chapterId, "chapterId");
        Objects.requireNonNull(folder, "folder");
        Objects.requireNonNull(lineDirectory, "lineDirectory");
        try {
            ClasspathQuestResources resources = new ClasspathQuestResources(folder);
            ImportedLineData line = readLine(resources, folder, lineDirectory);
            UUID previous = resolvePreviousLineUuid(resources.readQuestLineOrder(), resources, folder, lineDirectory);
            UUID chapterUuid = explicitUuid != null ? explicitUuid
                : uuidFromResource ? line.uuid : com.hfstudio.bqapi.runtime.BQStableIdFactory.chapter(chapterId);
            String orderAfterEncoded = explicitOrderAfterEncoded != null ? explicitOrderAfterEncoded
                : previous == null ? null : UuidConverter.encodeUuid(previous);
            return toChapterDefinition(chapterId, line, chapterUuid, orderAfterEncoded, explicitIconNbt);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                "Failed to import BetterQuesting line " + lineDirectory + " from " + folder.getClasspathRoot(),
                e);
        }
    }

    private ChapterDefinition toChapterDefinition(String chapterId, ImportedLineData line, UUID chapterUuid,
        UUID previousUuid) {
        return toChapterDefinition(
            chapterId,
            line,
            chapterUuid,
            previousUuid == null ? null : UuidConverter.encodeUuid(previousUuid),
            line.iconNbt);
    }

    private ChapterDefinition toChapterDefinition(String chapterId, ImportedLineData line, UUID chapterUuid,
        String orderAfterEncoded, NBTTagCompound iconNbt) {
        com.hfstudio.bqapi.api.builder.ChapterBuilder builder = Chapters.chapter(chapterId)
            .uuid(chapterUuid)
            .templateNbt(line.templateNbt);
        if (orderAfterEncoded != null) {
            builder.orderAfterEncoded(orderAfterEncoded);
        }
        if (iconNbt != null) {
            builder.iconNbt(iconNbt);
        }
        for (QuestPlacementDefinition placement : line.placements) {
            builder.quest(placement);
        }
        return builder.build();
    }

    private ImportedLineData readLine(ClasspathQuestResources resources, ImportedQuestFolder folder, String lineDirectory)
        throws IOException {
        NBTTagCompound lineNbt = resources.readNbt("QuestLines/" + lineDirectory + "/QuestLine.json");

        UUID lineUuid = NBTConverter.UuidValueType.QUEST_LINE.readId((NBTTagCompound) lineNbt.copy());
        Map<UUID, QuestDefinition> questsByUuid = new LinkedHashMap<>();
        for (String questFile : resources.listFiles("Quests/" + lineDirectory, ".json")) {
            QuestDefinition quest = readQuest(resources, folder, lineDirectory, questFile);
            questsByUuid.put(quest.getUuid(), quest);
        }

        List<QuestPlacementDefinition> placements = new ArrayList<>();
        NBTTagList questPlacements = lineNbt.getTagList("quests", 10);
        for (int i = 0; i < questPlacements.tagCount(); i++) {
            NBTTagCompound placementNbt = questPlacements.getCompoundTagAt(i);
            UUID questUuid = NBTConverter.UuidValueType.QUEST.readId((NBTTagCompound) placementNbt.copy());
            QuestDefinition quest = questsByUuid.get(questUuid);
            if (quest == null) {
                throw new IllegalArgumentException(
                    "Missing quest file for " + questUuid + " in line directory " + lineDirectory);
            }
            int sizeX = placementNbt.hasKey("size", 99) ? placementNbt.getInteger("size") : placementNbt.getInteger("sizeX");
            int sizeY = placementNbt.hasKey("size", 99) ? placementNbt.getInteger("size") : placementNbt.getInteger("sizeY");
            placements.add(quest.at(placementNbt.getInteger("x"), placementNbt.getInteger("y"), sizeX, sizeY));
        }

        return new ImportedLineData(
            lineDirectory,
            lineUuid,
            lineNbt,
            readItemProperty(lineNbt.getCompoundTag("properties"), "icon"),
            Collections.unmodifiableList(placements));
    }

    private QuestDefinition readQuest(ClasspathQuestResources resources, ImportedQuestFolder folder, String lineDirectory,
        String questFile) throws IOException {
        NBTTagCompound questNbt = resources.readNbt("Quests/" + lineDirectory + "/" + questFile);
        UUID questUuid = NBTConverter.UuidValueType.QUEST.readId((NBTTagCompound) questNbt.copy());
        NBTTagCompound propertiesNbt = questNbt.getCompoundTag("properties");

        String questId = defaultQuestId(folder, lineDirectory, questUuid);
        com.hfstudio.bqapi.api.builder.QuestBuilder builder = Quests.quest(questId)
            .uuid(questUuid)
            .templateNbt(questNbt)
            .main(readBooleanProperty(propertiesNbt, "isMain", false));

        NBTTagCompound iconNbt = readItemProperty(propertiesNbt, "icon");
        if (iconNbt != null) {
            builder.iconNbt(iconNbt);
        }

        NBTTagList prerequisites = questNbt.getTagList("preRequisites", 10);
        for (int i = 0; i < prerequisites.tagCount(); i++) {
            NBTTagCompound prerequisiteNbt = prerequisites.getCompoundTagAt(i);
            UUID prerequisite = NBTConverter.UuidValueType.QUEST.readId((NBTTagCompound) prerequisiteNbt.copy());
            IQuest.RequirementType type = prerequisiteNbt.hasKey("type", 99)
                ? IQuest.RequirementType.from(prerequisiteNbt.getByte("type"))
                : IQuest.RequirementType.NORMAL;
            builder.prerequisite(prerequisite, type);
        }

        NBTTagList tasks = questNbt.getTagList("tasks", 10);
        for (int i = 0; i < tasks.tagCount(); i++) {
            NBTTagCompound rawTaskNbt = tasks.getCompoundTagAt(i);
            builder.task(
                new RawTaskDefinition(
                    questId + "/task/" + i,
                    rawTaskNbt.hasKey("index", 99) ? rawTaskNbt.getInteger("index") : -1,
                    new ResourceLocation(rawTaskNbt.getString("taskID")),
                    stripKeys(rawTaskNbt, "taskID", "index")));
        }

        NBTTagList rewards = questNbt.getTagList("rewards", 10);
        for (int i = 0; i < rewards.tagCount(); i++) {
            NBTTagCompound rawRewardNbt = rewards.getCompoundTagAt(i);
            builder.reward(
                new RawRewardDefinition(
                    questId + "/reward/" + i,
                    rawRewardNbt.hasKey("index", 99) ? rawRewardNbt.getInteger("index") : -1,
                    new ResourceLocation(rawRewardNbt.getString("rewardID")),
                    stripKeys(rawRewardNbt, "rewardID", "index")));
        }

        return builder.build();
    }

    private boolean readBooleanProperty(NBTTagCompound propertiesNbt, String key, boolean defaultValue) {
        if (propertiesNbt == null) {
            return defaultValue;
        }
        NBTTagCompound domain = propertiesNbt.getCompoundTag("betterquesting");
        if (!domain.hasKey(key, 99)) {
            return defaultValue;
        }
        return domain.getBoolean(key);
    }

    private NBTTagCompound readItemProperty(NBTTagCompound propertiesNbt, String key) {
        if (propertiesNbt == null) {
            return null;
        }
        NBTTagCompound domain = propertiesNbt.getCompoundTag("betterquesting");
        if (!domain.hasKey(key, 10)) {
            return null;
        }
        return (NBTTagCompound) domain.getCompoundTag(key)
            .copy();
    }

    private UUID resolvePreviousLineUuid(Map<UUID, Integer> orderIndex, ClasspathQuestResources resources,
        ImportedQuestFolder folder, String lineDirectory) throws IOException {
        List<ImportedLineRef> refs = new ArrayList<>();
        for (String dir : resources.listDirectories("QuestLines")) {
            NBTTagCompound lineNbt = resources.readNbt("QuestLines/" + dir + "/QuestLine.json");
            UUID lineUuid = NBTConverter.UuidValueType.QUEST_LINE.readId((NBTTagCompound) lineNbt.copy());
            refs.add(new ImportedLineRef(dir, lineUuid));
        }
        refs.sort(Comparator.comparingInt(ref -> orderIndex.getOrDefault(ref.uuid, Integer.MAX_VALUE)));
        UUID previous = null;
        for (ImportedLineRef ref : refs) {
            if (ref.lineDirectory.equals(lineDirectory)) {
                return previous;
            }
            previous = ref.uuid;
        }
        throw new IllegalArgumentException(
            "Unable to resolve imported line directory " + lineDirectory + " in " + folder.getClasspathRoot());
    }

    private NBTTagCompound stripKeys(NBTTagCompound source, String... keys) {
        NBTTagCompound copy = (NBTTagCompound) source.copy();
        for (String key : keys) {
            copy.removeTag(key);
        }
        return copy;
    }

    private String defaultChapterId(ImportedQuestFolder folder, String lineDirectory) {
        return folder.getModId() + ":imported/" + sanitize(folder.getRootPath()) + "/" + sanitize(lineDirectory);
    }

    private String defaultQuestId(ImportedQuestFolder folder, String lineDirectory, UUID questUuid) {
        return defaultChapterId(folder, lineDirectory) + "/quest/"
            + UuidConverter.encodeUuidStripPadding(questUuid)
                .replace('=', '_');
    }

    private String sanitize(String value) {
        return value.replace('\\', '/')
            .replaceAll("[^a-zA-Z0-9/_\\-.]", "_");
    }

    private static final class ImportedLineData {

        private final String lineDirectory;
        private final UUID uuid;
        private final NBTTagCompound templateNbt;
        private final NBTTagCompound iconNbt;
        private final List<QuestPlacementDefinition> placements;

        private ImportedLineData(String lineDirectory, UUID uuid, NBTTagCompound templateNbt, NBTTagCompound iconNbt,
            List<QuestPlacementDefinition> placements) {
            this.lineDirectory = lineDirectory;
            this.uuid = uuid;
            this.templateNbt = (NBTTagCompound) templateNbt.copy();
            this.iconNbt = iconNbt == null ? null : (NBTTagCompound) iconNbt.copy();
            this.placements = placements;
        }
    }

    private static final class ImportedLineRef {

        private final String lineDirectory;
        private final UUID uuid;

        private ImportedLineRef(String lineDirectory, UUID uuid) {
            this.lineDirectory = lineDirectory;
            this.uuid = uuid;
        }
    }

    private static final class ClasspathQuestResources {

        private final ImportedQuestFolder folder;
        private final String classpathRoot;

        private ClasspathQuestResources(ImportedQuestFolder folder) {
            this.folder = folder;
            this.classpathRoot = folder.getClasspathRoot();
        }

        private NBTTagCompound readNbt(String relativePath) throws IOException {
            try (InputStream input = open(relativePath)) {
                if (input == null) {
                    throw new IOException("Missing resource " + resolve(relativePath));
                }
                String json = readString(input);
                return NBTConverter.JSONtoNBT_Object(new JsonParser().parse(json).getAsJsonObject(), new NBTTagCompound(), true);
            }
        }

        private Map<UUID, Integer> readQuestLineOrder() throws IOException {
            Map<UUID, Integer> order = new HashMap<>();
            try (InputStream input = open("QuestLinesOrder.txt")) {
                if (input == null) {
                    throw new IOException("Missing resource " + resolve("QuestLinesOrder.txt"));
                }
                int index = 0;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty()) {
                            continue;
                        }
                        String encoded = trimmed.split(":", 2)[0].trim();
                        order.put(UuidConverter.decodeUuid(encoded), index++);
                    }
                }
            }
            return order;
        }

        private List<String> listDirectories(String relativeDir) throws IOException {
            String prefix = normalize(relativeDir) + "/";
            Map<String, Boolean> children = listImmediateChildren(prefix);
            List<String> directories = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : children.entrySet()) {
                if (entry.getValue()) {
                    directories.add(entry.getKey());
                }
            }
            Collections.sort(directories);
            return directories;
        }

        private List<String> listFiles(String relativeDir, String suffix) throws IOException {
            String prefix = normalize(relativeDir) + "/";
            Map<String, Boolean> children = listImmediateChildren(prefix);
            List<String> files = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : children.entrySet()) {
                if (!entry.getValue() && entry.getKey().endsWith(suffix)) {
                    files.add(entry.getKey());
                }
            }
            Collections.sort(files);
            return files;
        }

        private Map<String, Boolean> listImmediateChildren(String relativeDirWithSlash) throws IOException {
            String prefix = resolve(relativeDirWithSlash);
            java.net.URL marker = folder.getClassLoader().getResource(prefix);
            if (marker != null && "file".equals(marker.getProtocol())) {
                return listFileChildren(marker);
            }
            return listJarChildren(prefix);
        }

        private Map<String, Boolean> listFileChildren(java.net.URL url) throws IOException {
            try {
                java.io.File dir = new java.io.File(url.toURI());
                Map<String, Boolean> children = new LinkedHashMap<>();
                java.io.File[] files = dir.listFiles();
                if (files == null) {
                    return children;
                }
                for (java.io.File file : files) {
                    children.put(file.getName(), file.isDirectory());
                }
                return children;
            } catch (Exception e) {
                throw new IOException("Failed to list directory " + url, e);
            }
        }

        private Map<String, Boolean> listJarChildren(String prefix) throws IOException {
            java.net.URL marker = folder.getClassLoader().getResource(resolve("QuestLinesOrder.txt"));
            if (marker == null) {
                throw new IOException("Missing marker resource " + resolve("QuestLinesOrder.txt"));
            }
            if ("file".equals(marker.getProtocol())) {
                try {
                    java.io.File root = new java.io.File(marker.toURI()).getParentFile();
                    Map<String, Boolean> children = new LinkedHashMap<>();
                    java.io.File dir = root;
                    String relative = prefix.substring(classpathRoot.length());
                    for (String segment : relative.split("/")) {
                        if (segment.isEmpty()) {
                            continue;
                        }
                        dir = new java.io.File(dir, segment);
                    }
                    java.io.File[] files = dir.listFiles();
                    if (files == null) {
                        return children;
                    }
                    for (java.io.File file : files) {
                        children.put(file.getName(), file.isDirectory());
                    }
                    return children;
                } catch (Exception e) {
                    throw new IOException("Failed to list directory for " + prefix, e);
                }
            }

            java.net.JarURLConnection connection = (java.net.JarURLConnection) marker.openConnection();
            Map<String, Boolean> children = new LinkedHashMap<>();
            try (java.util.jar.JarFile jar = connection.getJarFile()) {
                java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    java.util.jar.JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (!name.startsWith(prefix) || name.equals(prefix)) {
                        continue;
                    }
                    String remainder = name.substring(prefix.length());
                    int slash = remainder.indexOf('/');
                    if (slash < 0) {
                        children.put(remainder, false);
                    } else if (slash > 0) {
                        children.put(remainder.substring(0, slash), true);
                    }
                }
            }
            return children;
        }

        private InputStream open(String relativePath) {
            return folder.getClassLoader().getResourceAsStream(resolve(relativePath));
        }

        private String resolve(String relativePath) {
            String normalized = normalize(relativePath);
            if (normalized.isEmpty()) {
                return classpathRoot;
            }
            return classpathRoot + "/" + normalized;
        }

        private String normalize(String path) {
            String normalized = path.replace('\\', '/');
            while (normalized.startsWith("/")) {
                normalized = normalized.substring(1);
            }
            return normalized;
        }

        private String readString(InputStream input) throws IOException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                StringBuilder builder = new StringBuilder();
                char[] buffer = new char[1024];
                int read;
                while ((read = reader.read(buffer)) >= 0) {
                    builder.append(buffer, 0, read);
                }
                return builder.toString();
            }
        }
    }
}
