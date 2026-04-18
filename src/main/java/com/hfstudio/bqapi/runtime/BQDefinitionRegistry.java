package com.hfstudio.bqapi.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.hfstudio.bqapi.api.definition.ChapterDefinition;

public final class BQDefinitionRegistry {

    private final Map<String, ChapterDefinition> chapters = new LinkedHashMap<>();
    private final Map<UUID, String> chapterIdsByUuid = new LinkedHashMap<>();

    public synchronized void register(ChapterDefinition chapter) {
        ChapterDefinition value = Objects.requireNonNull(chapter, "chapter");
        String existingId = chapterIdsByUuid.get(value.getUuid());
        if (existingId != null && !existingId.equals(value.getId())) {
            chapters.remove(existingId);
        }
        ChapterDefinition previous = chapters.put(value.getId(), value);
        if (previous != null && !previous.getUuid().equals(value.getUuid())) {
            chapterIdsByUuid.remove(previous.getUuid());
        }
        chapterIdsByUuid.put(value.getUuid(), value.getId());
    }

    public synchronized Collection<ChapterDefinition> getChapters() {
        return Collections.unmodifiableCollection(chapters.values());
    }
}
