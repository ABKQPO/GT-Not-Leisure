package com.hfstudio.bqapi.runtime.apply;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.hfstudio.bqapi.api.definition.QuestPlacementDefinition;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api.utils.UuidConverter;
import betterquesting.questing.QuestLineDatabase;
import betterquesting.questing.QuestLineEntry;

public final class ChapterApplier {

    public void apply(ChapterDefinition definition) {
        IQuestLine chapter = QuestLineDatabase.INSTANCE.get(definition.getUuid());
        if (chapter == null) {
            chapter = QuestLineDatabase.INSTANCE.createNew(definition.getUuid());
        }

        if (definition.getTemplateNbt() != null) {
            chapter.readFromNBT(definition.getTemplateNbt());
        } else {
            chapter.setProperty(NativeProps.NAME, definition.getId());
            chapter.setProperty(NativeProps.DESC, "");
        }
        if (definition.getIconNbt() != null) {
            BigItemStack icon = JsonHelper.JsonToItemStack(
                (NBTTagCompound) definition.getIconNbt()
                    .copy());
            if (icon == null) {
                throw new IllegalArgumentException("Unable to load chapter icon from NBT");
            }
            chapter.setProperty(NativeProps.ICON, icon);
        }
        chapter.clear();

        for (QuestPlacementDefinition placement : definition.getPlacements()) {
            chapter.put(
                placement.getQuest()
                    .getUuid(),
                new QuestLineEntry(placement.getX(), placement.getY(), placement.getSizeX(), placement.getSizeY()));
        }

        int index = resolveOrderIndex(definition);
        if (index >= 0) {
            QuestLineDatabase.INSTANCE.setOrderIndex(definition.getUuid(), index);
        } else {
            QuestLineDatabase.INSTANCE.getOrderIndex(definition.getUuid());
        }
    }

    private int resolveOrderIndex(ChapterDefinition definition) {
        String afterEncoded = definition.getOrderAfterEncoded();
        if (afterEncoded == null || afterEncoded.isEmpty()) {
            return -1;
        }

        UUID afterId = UuidConverter.decodeUuid(afterEncoded);
        int afterIndex = QuestLineDatabase.INSTANCE.getOrderIndex(afterId);
        if (afterIndex < 0) {
            return QuestLineDatabase.INSTANCE.getOrderedEntries()
                .size();
        }
        return afterIndex + 1;
    }
}
