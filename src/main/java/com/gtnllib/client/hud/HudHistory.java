package com.gtnllib.client.hud;

import java.util.Objects;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;

public class HudHistory<T> {

    private final int limit;
    private final ObjectArrayFIFOQueue<T> undo = new ObjectArrayFIFOQueue<>(), redo = new ObjectArrayFIFOQueue<>();
    private Object group;

    public HudHistory(int limit) {
        if (limit < 1) throw new IllegalArgumentException("History needs room for one step");
        this.limit = limit;
    }

    public void record(Supplier<T> before, Object group) {
        if (group != null && group.equals(this.group)) return;
        undo.enqueueFirst(Objects.requireNonNull(before.get(), "before"));
        while (undo.size() > limit) undo.dequeueLast();
        redo.clear();
        this.group = group;
    }

    public void seal() {
        group = null;
    }

    public boolean canUndo() {
        return !undo.isEmpty();
    }

    public boolean canRedo() {
        return !redo.isEmpty();
    }

    public T undo(T current) {
        if (undo.isEmpty()) return null;
        group = null;
        redo.enqueueFirst(current);
        return undo.dequeue();
    }

    public T redo(T current) {
        if (redo.isEmpty()) return null;
        group = null;
        undo.enqueueFirst(current);
        return redo.dequeue();
    }
}
