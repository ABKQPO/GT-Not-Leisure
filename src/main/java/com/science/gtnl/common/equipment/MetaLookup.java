package com.science.gtnl.common.equipment;

import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class MetaLookup<T extends IEquipmentVariant> {

    private final List<T> byMeta;

    public MetaLookup(T[] values) {
        int count = 0;
        for (T value : values) count = Math.max(count, value.meta() + 1);
        List<T> table = new ObjectArrayList<>(Collections.nCopies(count, null));
        for (T value : values) {
            if (table.get(value.meta()) != null)
                throw new IllegalStateException("Duplicate meta " + value.meta() + " for " + value.id());
            table.set(value.meta(), value);
        }
        this.byMeta = table;
    }

    public T byMeta(int meta) {
        return meta >= 0 && meta < byMeta.size() ? byMeta.get(meta) : null;
    }

    public int size() {
        return byMeta.size();
    }
}
