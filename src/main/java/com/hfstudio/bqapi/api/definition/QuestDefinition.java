package com.hfstudio.bqapi.api.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.BQApiLangKeys;
import com.hfstudio.bqapi.runtime.BQStableIdFactory;

import betterquesting.api.questing.IQuest;

public final class QuestDefinition {

    private final String id;
    private final UUID uuid;
    private final List<TaskDefinition> tasks;
    private final List<UUID> prerequisites;
    private final Map<UUID, IQuest.RequirementType> requirementTypes;
    private final List<RewardDefinition> rewards;
    private final NBTTagCompound iconNbt;
    private final NBTTagCompound templateNbt;
    private final boolean main;

    public QuestDefinition(String id, List<TaskDefinition> tasks, List<UUID> prerequisites) {
        this(
            id,
            BQStableIdFactory.quest(id),
            tasks,
            prerequisites,
            Collections.emptyMap(),
            Collections.emptyList(),
            null,
            null,
            false);
    }

    public QuestDefinition(String id, UUID uuid, List<TaskDefinition> tasks, List<UUID> prerequisites,
        Map<UUID, IQuest.RequirementType> requirementTypes, List<RewardDefinition> rewards, NBTTagCompound iconNbt,
        NBTTagCompound templateNbt, boolean main) {
        this.id = Objects.requireNonNull(id, "id");
        this.uuid = Objects.requireNonNull(uuid, "uuid");
        this.tasks = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(tasks, "tasks")));
        this.prerequisites = Collections
            .unmodifiableList(new ArrayList<>(Objects.requireNonNull(prerequisites, "prerequisites")));
        this.requirementTypes = Collections.unmodifiableMap(
            new LinkedHashMap<>(Objects.requireNonNull(requirementTypes, "requirementTypes")));
        this.rewards = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(rewards, "rewards")));
        this.iconNbt = iconNbt == null ? null : (NBTTagCompound) iconNbt.copy();
        this.templateNbt = templateNbt == null ? null : (NBTTagCompound) templateNbt.copy();
        this.main = main;
    }

    public QuestPlacementDefinition at(int x, int y) {
        return new QuestPlacementDefinition(this, x, y, 24, 24);
    }

    public QuestPlacementDefinition at(int x, int y, int sizeX, int sizeY) {
        return new QuestPlacementDefinition(this, x, y, sizeX, sizeY);
    }

    public String getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<TaskDefinition> getTasks() {
        return tasks;
    }

    public List<UUID> getPrerequisites() {
        return prerequisites;
    }

    public Map<UUID, IQuest.RequirementType> getRequirementTypes() {
        return requirementTypes;
    }

    public IQuest.RequirementType getRequirementType(UUID prerequisiteUuid) {
        IQuest.RequirementType type = requirementTypes.get(prerequisiteUuid);
        return type == null ? IQuest.RequirementType.NORMAL : type;
    }

    public List<RewardDefinition> getRewards() {
        return rewards;
    }

    public NBTTagCompound getIconNbt() {
        return iconNbt == null ? null : (NBTTagCompound) iconNbt.copy();
    }

    public NBTTagCompound getTemplateNbt() {
        return templateNbt == null ? null : (NBTTagCompound) templateNbt.copy();
    }

    public boolean isMain() {
        return main;
    }

    public String getNameLangKey() {
        return BQApiLangKeys.questName(uuid);
    }

    public String getDescLangKey() {
        return BQApiLangKeys.questDesc(uuid);
    }
}
