package com.hfstudio.bqapi.api.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.definition.ChapterDefinition;
import com.hfstudio.bqapi.api.definition.QuestPlacementDefinition;

import betterquesting.api.utils.BigItemStack;

public final class ChapterBuilder {

    private final String id;
    private UUID uuid;
    private String orderAfterEncoded;
    private NBTTagCompound iconNbt;
    private NBTTagCompound templateNbt;
    private final List<QuestPlacementDefinition> placements = new ArrayList<>();

    private ChapterBuilder(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    static ChapterBuilder chapter(String id) {
        return new ChapterBuilder(id);
    }

    public ChapterBuilder orderAfterEncoded(String orderAfterEncoded) {
        this.orderAfterEncoded = orderAfterEncoded;
        return this;
    }

    public ChapterBuilder uuid(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        return this;
    }

    public ChapterBuilder icon(ItemStack icon) {
        this.iconNbt = Objects.requireNonNull(icon, "icon")
            .writeToNBT(new NBTTagCompound());
        return this;
    }

    public ChapterBuilder icon(String itemId, int count, int damage) {
        this.iconNbt = SerializedItemNbt.of(Objects.requireNonNull(itemId, "itemId"), count, damage);
        return this;
    }

    public ChapterBuilder icon(BigItemStack icon) {
        this.iconNbt = Objects.requireNonNull(icon, "icon")
            .writeToNBT(new NBTTagCompound());
        return this;
    }

    public ChapterBuilder iconNbt(NBTTagCompound iconNbt) {
        this.iconNbt = iconNbt == null ? null : (NBTTagCompound) iconNbt.copy();
        return this;
    }

    public ChapterBuilder templateNbt(NBTTagCompound templateNbt) {
        this.templateNbt = templateNbt == null ? null : (NBTTagCompound) templateNbt.copy();
        return this;
    }

    public ChapterBuilder quest(QuestPlacementDefinition placement) {
        this.placements.add(Objects.requireNonNull(placement, "placement"));
        return this;
    }

    public ChapterDefinition build() {
        return new ChapterDefinition(
            id,
            uuid == null ? com.hfstudio.bqapi.runtime.BQStableIdFactory.chapter(id) : uuid,
            orderAfterEncoded,
            iconNbt,
            templateNbt,
            placements);
    }
}
