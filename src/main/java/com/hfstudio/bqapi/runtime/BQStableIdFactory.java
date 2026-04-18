package com.hfstudio.bqapi.runtime;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public final class BQStableIdFactory {

    private BQStableIdFactory() {}

    private static UUID stable(String prefix, String id) {
        Objects.requireNonNull(id, "id");
        return UUID.nameUUIDFromBytes((prefix + id).getBytes(StandardCharsets.UTF_8));
    }

    public static UUID chapter(String id) {
        return stable("chapter:", id);
    }

    public static UUID quest(String id) {
        return stable("quest:", id);
    }
}
