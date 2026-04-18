package com.hfstudio.bqapi.api.builder;

import java.util.Objects;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.hfstudio.bqapi.api.importer.ImportedQuestFolder;
import com.hfstudio.bqapi.api.importer.ImportedQuestFolders;
import com.hfstudio.bqapi.runtime.importer.BQImportedQuestLoader;

import betterquesting.api.utils.BigItemStack;

public final class ImportedChapterBuilder {

    private final String id;
    private ImportedQuestFolder folder;
    private String lineDirectory;
    private UUID uuid;
    private boolean uuidFromResource;
    private String orderAfterEncoded;
    private NBTTagCompound iconNbt;

    ImportedChapterBuilder(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public ImportedChapterBuilder resourceFolder(ImportedQuestFolder folder) {
        this.folder = Objects.requireNonNull(folder, "folder");
        return this;
    }

    public ImportedChapterBuilder resourceFolder(String modId, String rootPath) {
        return resourceFolder(ImportedQuestFolders.folder(modId, rootPath).build());
    }

    public ImportedChapterBuilder lineDirectory(String lineDirectory) {
        this.lineDirectory = Objects.requireNonNull(lineDirectory, "lineDirectory");
        return this;
    }

    public ImportedChapterBuilder uuid(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        return this;
    }

    public ImportedChapterBuilder uuidFromResource() {
        this.uuidFromResource = true;
        return this;
    }

    public ImportedChapterBuilder orderAfterEncoded(String orderAfterEncoded) {
        this.orderAfterEncoded = orderAfterEncoded;
        return this;
    }

    public ImportedChapterBuilder icon(ItemStack icon) {
        this.iconNbt = Objects.requireNonNull(icon, "icon")
            .writeToNBT(new NBTTagCompound());
        return this;
    }

    public ImportedChapterBuilder icon(String itemId, int count, int damage) {
        this.iconNbt = SerializedItemNbt.of(Objects.requireNonNull(itemId, "itemId"), count, damage);
        return this;
    }

    public ImportedChapterBuilder icon(BigItemStack icon) {
        this.iconNbt = Objects.requireNonNull(icon, "icon")
            .writeToNBT(new NBTTagCompound());
        return this;
    }

    public ChapterDefinition build() {
        if (folder == null) {
            throw new IllegalStateException("resource folder must be provided");
        }
        if (lineDirectory == null || lineDirectory.isEmpty()) {
            throw new IllegalStateException("line directory must be provided");
        }
        return new BQImportedQuestLoader().importLine(
            id,
            folder,
            lineDirectory,
            uuid,
            uuidFromResource,
            orderAfterEncoded,
            iconNbt);
    }
}
