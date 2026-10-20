package com.science.gtnllib.client.hud;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Supplier;

public class HudHistory<T> {

    private final int limit;
    private final Deque<T> undo = new ArrayDeque<>(), redo = new ArrayDeque<>();
    private Object group;

    public HudHistory(int limit) {
        if (limit < 1) throw new IllegalArgumentException("History needs room for one step");
        this.limit = limit;
    }

    public void record(Supplier<T> before, Object group) {
        if (group != null && group.equals(this.group)) return;
        undo.push(Objects.requireNonNull(before.get(), "before"));
        while (undo.size() > limit) undo.removeLast();
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
        redo.push(current);
        return undo.pop();
    }

    public T redo(T current) {
        if (redo.isEmpty()) return null;
        group = null;
        undo.push(current);
        return redo.pop();
    }
}
