package com.gtnltest.runtime;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

/** Explicit, reviewed non-container capabilities shared with the external report validator. */
final class GuiExpectations {

    private static final Map<String, String> NO_CONTAINER = load();

    private GuiExpectations() {}

    static String noContainerReason(String id) {
        return NO_CONTAINER.get(id);
    }

    private static Map<String, String> load() {
        try (var reader = new InputStreamReader(
            Objects.requireNonNull(GuiExpectations.class.getResourceAsStream("/gui-expectations.json")),
            StandardCharsets.UTF_8)) {
            JsonObject document = new Gson().fromJson(reader, JsonObject.class);
            RuntimeReport.require(
                document.get("schemaVersion")
                    .getAsInt() == 1,
                "Unknown GUI expectation schema");
            Map<String, String> reasons = new Object2ObjectOpenHashMap<>();
            document.getAsJsonObject("noContainer")
                .entrySet()
                .forEach(entry -> {
                    String reason = entry.getValue()
                        .getAsString();
                    RuntimeReport.require(!reason.isBlank(), "Empty no-container reason");
                    reasons.put(entry.getKey(), reason);
                });
            return Object2ObjectMaps.unmodifiable(new Object2ObjectOpenHashMap<>(reasons));
        } catch (Exception error) {
            throw new IllegalStateException("Cannot read reviewed GUI expectations", error);
        }
    }
}
