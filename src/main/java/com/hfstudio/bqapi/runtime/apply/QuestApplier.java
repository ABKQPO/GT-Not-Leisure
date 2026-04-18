package com.hfstudio.bqapi.runtime.apply;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.definition.QuestDefinition;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.storage.DBEntry;
import betterquesting.questing.QuestDatabase;

public final class QuestApplier {

    private final TaskApplier taskApplier;
    private final RewardApplier rewardApplier;

    public QuestApplier(TaskApplier taskApplier) {
        this.taskApplier = taskApplier;
        this.rewardApplier = new RewardApplier();
    }

    public void apply(QuestDefinition definition) {
        IQuest quest = QuestDatabase.INSTANCE.get(definition.getUuid());
        if (quest == null) {
            quest = QuestDatabase.INSTANCE.createNew(definition.getUuid());
        }

        Map<Integer, ITask> oldTasks = new HashMap<>();
        for (DBEntry<ITask> entry : quest.getTasks()
            .getEntries()) {
            oldTasks.put(entry.getID(), entry.getValue());
        }

        if (definition.getTemplateNbt() != null) {
            quest.readFromNBT(definition.getTemplateNbt());
        } else {
            quest.setProperty(NativeProps.NAME, definition.getId());
            quest.setProperty(NativeProps.DESC, "");
        }
        quest.setProperty(NativeProps.MAIN, definition.isMain());
        if (definition.getIconNbt() != null) {
            quest.setProperty(NativeProps.ICON, loadItem(definition.getIconNbt()));
        }
        quest.getRewards()
            .reset();

        applyTasks(quest, oldTasks, taskApplier.buildTasks(definition.getTasks()));
        applyRewards(quest, rewardApplier.buildRewards(definition.getRewards()));

        quest.setRequirements(definition.getPrerequisites());
        for (java.util.UUID requirement : definition.getPrerequisites()) {
            quest.setRequirementType(requirement, definition.getRequirementType(requirement));
        }
    }

    public void applyTasks(IQuest quest, Map<Integer, ITask> oldTasks, Map<Integer, ITask> rebuiltTasks) {
        quest.getTasks()
            .reset();
        rebuiltTasks.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                quest.getTasks()
                    .add(entry.getKey(), entry.getValue());
                TaskProgressMigrator.copyCompatibleProgress(oldTasks.get(entry.getKey()), entry.getValue());
            });
    }

    public void applyRewards(IQuest quest, Map<Integer, IReward> rebuiltRewards) {
        quest.getRewards()
            .reset();
        rebuiltRewards.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(
                entry -> quest.getRewards()
                    .add(entry.getKey(), entry.getValue()));
    }

    private BigItemStack loadItem(NBTTagCompound itemNbt) {
        BigItemStack stack = JsonHelper.JsonToItemStack((NBTTagCompound) itemNbt.copy());
        if (stack == null) {
            throw new IllegalArgumentException("Unable to load quest icon from NBT");
        }
        return stack;
    }
}
