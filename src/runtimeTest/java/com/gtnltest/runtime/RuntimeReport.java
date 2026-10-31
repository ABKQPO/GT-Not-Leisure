package com.gtnltest.runtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.google.gson.GsonBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/** Scenario counts describe executed scenarios, never assertion or tick counts. */
public final class RuntimeReport {

    public enum Status {
        NOT_RUN,
        RUNNING,
        PASS,
        FAIL,
        TIMEOUT,
        SKIP
    }

    public static final class Result {

        public final String id;
        public final String scope;
        public Status status = Status.NOT_RUN;
        public int ticks;
        public String detail = "";
        public String screenshot;
        public String screenClass;
        public int windowId;

        Result(RuntimeScenario scenario) {
            id = scenario.id();
            scope = scenario.scope();
        }
    }

    public final int schemaVersion = 1;
    public final String runId = System.getenv("GTNL_TEST_RUN_ID");
    public final String phase = System.getenv("GTNL_TEST_PHASE");
    public final String executionScope = "Forge development runtime in an isolated generated world";
    public String state = "RUNNING";
    public final List<Result> results = new ObjectArrayList<>();

    public void write(Path destination) throws IOException {
        Files.createDirectories(destination.getParent());
        Path temporary = destination.resolveSibling(destination.getFileName() + ".tmp");
        Files.writeString(
            temporary,
            new GsonBuilder().setPrettyPrinting()
                .create()
                .toJson(this),
            StandardCharsets.UTF_8);
        Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
