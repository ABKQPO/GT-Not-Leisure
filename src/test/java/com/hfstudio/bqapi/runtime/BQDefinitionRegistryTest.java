package com.hfstudio.bqapi.runtime;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.UUID;

import org.junit.Test;

import com.hfstudio.bqapi.api.builder.Chapters;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;

public class BQDefinitionRegistryTest {

    @Test
    public void laterRegistrationReplacesExistingChapterWithSameUuid() {
        UUID sharedUuid = UUID.fromString("30000000-0000-0000-0000-000000000001");

        ChapterDefinition first = Chapters.chapter("test:first")
            .uuid(sharedUuid)
            .build();
        ChapterDefinition second = Chapters.chapter("test:second")
            .uuid(sharedUuid)
            .build();

        BQDefinitionRegistry registry = new BQDefinitionRegistry();
        registry.register(first);
        registry.register(second);

        Collection<ChapterDefinition> chapters = registry.getChapters();
        assertEquals(1, chapters.size());
        assertEquals("test:second", chapters.iterator().next().getId());
    }
}
