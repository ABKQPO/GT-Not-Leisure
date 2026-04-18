package com.hfstudio.bqapi.api.builder;

import com.hfstudio.bqapi.api.builder.task.CheckboxTaskBuilder;
import com.hfstudio.bqapi.api.builder.task.LocationTaskBuilder;
import com.hfstudio.bqapi.api.builder.task.OptionalRetrievalTaskBuilder;
import com.hfstudio.bqapi.api.builder.task.RawTaskBuilder;
import com.hfstudio.bqapi.api.builder.task.RetrievalTaskBuilder;

public final class TaskBuilders {

    private TaskBuilders() {}

    public static CheckboxTaskBuilder checkbox(String id) {
        return new CheckboxTaskBuilder(id);
    }

    public static RetrievalTaskBuilder retrieval(String id) {
        return new RetrievalTaskBuilder(id);
    }

    public static OptionalRetrievalTaskBuilder optionalRetrieval(String id) {
        return new OptionalRetrievalTaskBuilder(id);
    }

    public static LocationTaskBuilder location(String id) {
        return new LocationTaskBuilder(id);
    }

    public static RawTaskBuilder raw(String id) {
        return new RawTaskBuilder(id);
    }
}
