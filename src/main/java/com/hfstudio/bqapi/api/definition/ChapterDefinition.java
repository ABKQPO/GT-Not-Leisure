package com.hfstudio.bqapi.api.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.BQApiLangKeys;
import com.hfstudio.bqapi.runtime.BQStableIdFactory;

public final class ChapterDefinition {

    private final String id;
    private final UUID uuid;
    private final String orderAfterEncoded;
    private final NBTTagCompound iconNbt;
    private final NBTTagCompound templateNbt;
    private final List<QuestPlacementDefinition> placements;

    public ChapterDefinition(String id, String orderAfterEncoded, List<QuestPlacementDefinition> placements) {
        this(id, BQStableIdFactory.chapter(id), orderAfterEncoded, null, null, placements);
    }

    public ChapterDefinition(String id, UUID uuid, String orderAfterEncoded, NBTTagCompound iconNbt,
        NBTTagCompound templateNbt, List<QuestPlacementDefinition> placements) {
        this.id = Objects.requireNonNull(id, "id");
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        this.orderAfterEncoded = orderAfterEncoded;
        this.iconNbt = iconNbt == null ? null : (NBTTagCompound) iconNbt.copy();
        this.templateNbt = templateNbt == null ? null : (NBTTagCompound) templateNbt.copy();
        this.placements = Collections
            .unmodifiableList(new ArrayList<>(Objects.requireNonNull(placements, "placements")));
    }

    public String getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getOrderAfterEncoded() {
        return orderAfterEncoded;
    }

    public NBTTagCompound getIconNbt() {
        return iconNbt == null ? null : (NBTTagCompound) iconNbt.copy();
    }

    public NBTTagCompound getTemplateNbt() {
        return templateNbt == null ? null : (NBTTagCompound) templateNbt.copy();
    }

    public List<QuestPlacementDefinition> getPlacements() {
        return placements;
    }

    public String getNameLangKey() {
        return BQApiLangKeys.chapterName(uuid);
    }

    public String getDescLangKey() {
        return BQApiLangKeys.chapterDesc(uuid);
    }
}
