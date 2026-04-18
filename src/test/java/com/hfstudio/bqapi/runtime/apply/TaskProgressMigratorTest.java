package com.hfstudio.bqapi.runtime.apply;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import bq_standard.tasks.TaskCheckbox;

public class TaskProgressMigratorTest {

    @Test
    public void compatibleTasksCarryProgressAcrossRebuild() {
        UUID user = UUID.fromString("00000000-0000-0000-0000-000000000001");
        TaskCheckbox source = new TaskCheckbox();
        source.setComplete(user);

        TaskCheckbox target = new TaskCheckbox();
        TaskProgressMigrator.copyCompatibleProgress(source, target);

        assertTrue(target.isComplete(user));
    }
}
