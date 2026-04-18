package com.hfstudio.bqapi.api.builder.task;

import com.hfstudio.bqapi.api.builder.TaskBuilder;
import com.hfstudio.bqapi.api.definition.CheckboxTaskDefinition;

public final class CheckboxTaskBuilder extends TaskBuilder<CheckboxTaskDefinition, CheckboxTaskBuilder> {

    public CheckboxTaskBuilder(String id) {
        super(id);
    }

    @Override
    protected CheckboxTaskBuilder self() {
        return this;
    }

    @Override
    public CheckboxTaskDefinition build() {
        return new CheckboxTaskDefinition(id, slot);
    }
}
