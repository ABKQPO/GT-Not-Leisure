package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** Per-view translations supplied by its client. They affect text matching only, never network authority. */
final class WirelessSearchIndex<K> {

    static final int BATCH_SIZE = 32;
    static final int MAX_NAME_LENGTH = 512;
    private List<K> keys = List.of();
    private final Map<K, String> names = new HashMap<>();
    private final BitSet received = new BitSet();
    private long revision;

    boolean update(Collection<K> candidates) {
        var unique = new LinkedHashSet<>(candidates);
        if (unique.size() == keys.size() && unique.containsAll(keys)) return false;
        keys = List.copyOf(unique);
        names.keySet()
            .retainAll(unique);
        received.clear();
        for (int start = 0; start < keys.size(); start += BATCH_SIZE) {
            if (keys.subList(start, Math.min(keys.size(), start + BATCH_SIZE))
                .stream()
                .allMatch(names::containsKey)) received.set(start / BATCH_SIZE);
        }
        revision++;
        return true;
    }

    List<Integer> pendingBatches() {
        List<Integer> pending = new ArrayList<>();
        for (int start = 0; start < keys.size(); start += BATCH_SIZE) {
            if (!received.get(start / BATCH_SIZE)) pending.add(start);
        }
        return pending;
    }

    List<K> keys() {
        return keys;
    }

    long revision() {
        return revision;
    }

    String name(K key) {
        return names.getOrDefault(key, "");
    }

    boolean accept(long revision, int start, List<String> translations) {
        if (revision != this.revision || start < 0
            || start % BATCH_SIZE != 0
            || start >= keys.size()
            || translations.size() != Math.min(BATCH_SIZE, keys.size() - start)
            || received.get(start / BATCH_SIZE)
            || translations.stream()
                .anyMatch(name -> name == null || name.length() > MAX_NAME_LENGTH))
            return false;
        for (int i = 0; i < translations.size(); i++) names.put(keys.get(start + i), translations.get(i));
        received.set(start / BATCH_SIZE);
        return true;
    }
}
