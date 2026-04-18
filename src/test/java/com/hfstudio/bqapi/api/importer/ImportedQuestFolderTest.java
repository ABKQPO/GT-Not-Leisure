package com.hfstudio.bqapi.api.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.hfstudio.bqapi.BQApi;
import com.hfstudio.bqapi.api.builder.Chapters;
import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.hfstudio.bqapi.api.definition.QuestDefinition;
import com.hfstudio.bqapi.api.definition.RawRewardDefinition;
import com.hfstudio.bqapi.api.definition.RawTaskDefinition;

import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.NBTConverter;
import betterquesting.api.utils.UuidConverter;

public class ImportedQuestFolderTest {

    @Test
    public void importsWholeFolderKeepingOrderAndRawQuestContent() throws Exception {
        SamplePack pack = createSamplePack();
        try {
            List<ChapterDefinition> chapters = BQApi.importFolder(pack.folder);

            assertEquals(2, chapters.size());
            assertEquals(pack.alphaLineId, chapters.get(0).getUuid());
            assertEquals(pack.betaLineId, chapters.get(1).getUuid());
            assertEquals(UuidConverter.encodeUuid(pack.alphaLineId), chapters.get(1).getOrderAfterEncoded());

            QuestDefinition alphaQuest = chapters.get(0)
                .getPlacements()
                .get(0)
                .getQuest();
            assertEquals(pack.alphaQuestId, alphaQuest.getUuid());
            assertEquals(1, alphaQuest.getTasks().size());
            assertTrue(alphaQuest.getTasks().get(0) instanceof RawTaskDefinition);
            assertEquals(
                "bq_standard:checkbox",
                ((RawTaskDefinition) alphaQuest.getTasks().get(0)).getFactoryId()
                    .toString());

            assertEquals(1, alphaQuest.getRewards().size());
            assertTrue(alphaQuest.getRewards().get(0) instanceof RawRewardDefinition);
            assertEquals(
                "custommod:mystery",
                ((RawRewardDefinition) alphaQuest.getRewards().get(0)).getFactoryId()
                    .toString());

            QuestDefinition betaQuest = chapters.get(1)
                .getPlacements()
                .get(0)
                .getQuest();
            assertEquals(IQuest.RequirementType.HIDDEN, betaQuest.getRequirementType(pack.alphaQuestId));
            assertFalse(betaQuest.getTemplateNbt().hasNoTags());
        } finally {
            pack.close();
        }
    }

    @Test
    public void importedChapterBuilderCanBindCodeDefinedTabsToResourceLine() throws Exception {
        SamplePack pack = createSamplePack();
        try {
            ChapterDefinition chapter = Chapters.imported("test:custom_beta_tab")
                .resourceFolder(pack.folder)
                .lineDirectory("beta_line")
                .uuidFromResource()
                .icon("minecraft:compass", 1, 0)
                .build();

            assertEquals("test:custom_beta_tab", chapter.getId());
            assertEquals(pack.betaLineId, chapter.getUuid());
            assertEquals(UuidConverter.encodeUuid(pack.alphaLineId), chapter.getOrderAfterEncoded());
            assertNotNull(chapter.getIconNbt());
            assertEquals(1, chapter.getPlacements().size());
            assertEquals(pack.betaQuestId, chapter.getPlacements().get(0).getQuest().getUuid());
        } finally {
            pack.close();
        }
    }

    private SamplePack createSamplePack() throws Exception {
        Path tempRoot = Files.createTempDirectory("bq-import-pack");
        Path packRoot = tempRoot.resolve("assets")
            .resolve("testmod")
            .resolve("questpacks")
            .resolve("main");

        UUID alphaLineId = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UUID betaLineId = UUID.fromString("10000000-0000-0000-0000-000000000002");
        UUID alphaQuestId = UUID.fromString("20000000-0000-0000-0000-000000000001");
        UUID betaQuestId = UUID.fromString("20000000-0000-0000-0000-000000000002");

        writeString(
            packRoot.resolve("QuestLinesOrder.txt"),
            UuidConverter.encodeUuid(alphaLineId) + ": Alpha Line\n" + UuidConverter.encodeUuid(betaLineId) + ": Beta Line\n");

        writeJson(packRoot.resolve("QuestLines").resolve("alpha_line").resolve("QuestLine.json"), createQuestLineNbt(
            alphaLineId,
            "alpha.line.name",
            alphaQuestId,
            12,
            34,
            24,
            24));
        writeJson(
            packRoot.resolve("QuestLines").resolve("beta_line").resolve("QuestLine.json"),
            createQuestLineNbt(betaLineId, "beta.line.name", betaQuestId, 88, 120, 48, 36));

        writeJson(
            packRoot.resolve("Quests").resolve("alpha_line").resolve("alpha_quest.json"),
            createQuestNbt(alphaQuestId, "alpha.quest.name", null, IQuest.RequirementType.NORMAL, false, "bq_standard:checkbox"));
        writeJson(
            packRoot.resolve("Quests").resolve("beta_line").resolve("beta_quest.json"),
            createQuestNbt(
                betaQuestId,
                "beta.quest.name",
                alphaQuestId,
                IQuest.RequirementType.HIDDEN,
                true,
                "custommod:special_task"));

        URLClassLoader loader = new URLClassLoader(new URL[] { tempRoot.toUri().toURL() }, getClass().getClassLoader());
        ImportedQuestFolder folder = ImportedQuestFolders.folder("testmod", "questpacks/main")
            .classLoader(loader)
            .build();
        return new SamplePack(loader, folder, alphaLineId, betaLineId, alphaQuestId, betaQuestId);
    }

    private NBTTagCompound createQuestLineNbt(UUID lineId, String name, UUID questId, int x, int y, int sizeX,
        int sizeY) {
        NBTTagCompound lineNbt = new NBTTagCompound();
        lineNbt.setTag("properties", new NBTTagCompound());
        NBTTagList quests = new NBTTagList();
        NBTTagCompound placementNbt = new NBTTagCompound();
        placementNbt.setInteger("x", x);
        placementNbt.setInteger("y", y);
        placementNbt.setInteger("sizeX", sizeX);
        placementNbt.setInteger("sizeY", sizeY);
        NBTConverter.UuidValueType.QUEST.writeId(questId, placementNbt);
        quests.appendTag(placementNbt);
        lineNbt.setTag("quests", quests);
        NBTConverter.UuidValueType.QUEST_LINE.writeId(lineId, lineNbt);
        return lineNbt;
    }

    private NBTTagCompound createQuestNbt(UUID questId, String name, UUID prerequisite, IQuest.RequirementType type,
        boolean main, String taskFactoryId) {
        NBTTagCompound questNbt = new NBTTagCompound();
        questNbt.setTag("properties", new NBTTagCompound());

        NBTTagList tasks = new NBTTagList();
        NBTTagCompound taskNbt = new NBTTagCompound();
        taskNbt.setString("taskID", taskFactoryId);
        taskNbt.setInteger("index", 0);
        taskNbt.setString("marker", "from_test");
        tasks.appendTag(taskNbt);
        questNbt.setTag("tasks", tasks);

        NBTTagList rewards = new NBTTagList();
        NBTTagCompound rewardNbt = new NBTTagCompound();
        rewardNbt.setString("rewardID", "custommod:mystery");
        rewardNbt.setInteger("index", 1);
        rewardNbt.setString("marker", "reward_from_test");
        rewards.appendTag(rewardNbt);
        questNbt.setTag("rewards", rewards);

        NBTTagList prerequisites = new NBTTagList();
        if (prerequisite != null) {
            NBTTagCompound prerequisiteNbt = NBTConverter.UuidValueType.QUEST.writeId(prerequisite);
            prerequisiteNbt.setByte("type", type.id());
            prerequisites.appendTag(prerequisiteNbt);
        }
        questNbt.setTag("preRequisites", prerequisites);

        NBTConverter.UuidValueType.QUEST.writeId(questId, questNbt);
        return questNbt;
    }

    private void writeJson(Path path, NBTTagCompound nbt) throws IOException {
        writeString(path, NBTConverter.NBTtoJSON_Compound(nbt, new JsonObject(), true).toString());
    }

    private void writeString(Path path, String value) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, value.getBytes(StandardCharsets.UTF_8));
    }

    private static final class SamplePack implements AutoCloseable {

        private final URLClassLoader loader;
        private final ImportedQuestFolder folder;
        private final UUID alphaLineId;
        private final UUID betaLineId;
        private final UUID alphaQuestId;
        private final UUID betaQuestId;

        private SamplePack(URLClassLoader loader, ImportedQuestFolder folder, UUID alphaLineId, UUID betaLineId,
            UUID alphaQuestId, UUID betaQuestId) {
            this.loader = loader;
            this.folder = folder;
            this.alphaLineId = alphaLineId;
            this.betaLineId = betaLineId;
            this.alphaQuestId = alphaQuestId;
            this.betaQuestId = betaQuestId;
        }

        @Override
        public void close() throws IOException {
            loader.close();
        }
    }
}
