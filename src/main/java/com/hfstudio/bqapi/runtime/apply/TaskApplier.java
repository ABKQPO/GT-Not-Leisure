package com.hfstudio.bqapi.runtime.apply;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

import com.hfstudio.bqapi.api.definition.CheckboxTaskDefinition;
import com.hfstudio.bqapi.api.definition.LocationTaskDefinition;
import com.hfstudio.bqapi.api.definition.OptionalRetrievalTaskDefinition;
import com.hfstudio.bqapi.api.definition.RawTaskDefinition;
import com.hfstudio.bqapi.api.definition.RetrievalTaskDefinition;
import com.hfstudio.bqapi.api.definition.TaskDefinition;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.questing.tasks.TaskRegistry;
import bq_standard.tasks.TaskCheckbox;
import bq_standard.tasks.TaskLocation;
import bq_standard.tasks.TaskOptionalRetrieval;
import bq_standard.tasks.TaskRetrieval;

public final class TaskApplier {

    public Map<Integer, ITask> buildTasks(Iterable<TaskDefinition> definitions) {
        Map<Integer, ITask> tasks = new LinkedHashMap<>();
        int nextAutoSlot = 0;

        for (TaskDefinition definition : definitions) {
            int slot = definition.getSlot();
            if (slot < 0) {
                while (tasks.containsKey(nextAutoSlot)) {
                    nextAutoSlot++;
                }
                slot = nextAutoSlot++;
            }
            if (tasks.containsKey(slot)) {
                throw new IllegalArgumentException("Duplicate task slot: " + slot);
            }
            tasks.put(slot, createTask(definition));
        }

        return tasks;
    }

    private ITask createTask(TaskDefinition definition) {
        if (definition instanceof CheckboxTaskDefinition) {
            return new TaskCheckbox();
        }
        if (definition instanceof OptionalRetrievalTaskDefinition optionalRetrieval) {
            TaskOptionalRetrieval task = new TaskOptionalRetrieval();
            applyRetrievalSettings(task, optionalRetrieval);
            return task;
        }
        if (definition instanceof RetrievalTaskDefinition retrieval) {
            TaskRetrieval task = new TaskRetrieval();
            applyRetrievalSettings(task, retrieval);
            return task;
        }
        if (definition instanceof LocationTaskDefinition location) {
            TaskLocation task = new TaskLocation();
            task.name = location.getLabelKey();
            task.x = location.getX();
            task.y = location.getY();
            task.z = location.getZ();
            task.dim = location.getDimension();
            task.range = location.getRange();
            task.visible = location.isVisible();
            task.hideInfo = location.isHideInfo();
            task.invert = location.isInvert();
            task.taxiCab = location.isTaxiCab();
            return task;
        }
        if (definition instanceof RawTaskDefinition raw) {
            ITask task = TaskRegistry.INSTANCE.createNew(raw.getFactoryId());
            if (task == null) {
                throw new IllegalArgumentException("Unknown BetterQuesting task factory: " + raw.getFactoryId());
            }
            task.readFromNBT(raw.getConfigNbt());
            return task;
        }

        throw new IllegalArgumentException(
            "Unsupported task definition: " + definition.getClass()
                .getName());
    }

    private void applyRetrievalSettings(TaskRetrieval task, RetrievalTaskDefinition retrieval) {
        task.ignoreNBT = retrieval.isIgnoreNbt();
        task.partialMatch = retrieval.isPartialMatch();
        task.consume = retrieval.isConsume();
        task.groupDetect = retrieval.isGroupDetect();
        task.autoConsume = retrieval.isAutoConsume();

        for (NBTTagCompound itemNbt : retrieval.getItems()) {
            BigItemStack stack = JsonHelper.JsonToItemStack((NBTTagCompound) itemNbt.copy());
            if (stack == null) {
                throw new IllegalArgumentException("Unable to load retrieval item from NBT");
            }
            task.requiredItems.add(stack);
        }
    }
}
