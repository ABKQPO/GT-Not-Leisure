package com.science.gtnl.common.wireless;

import java.util.List;

public class WirelessCardSelection {

    record Candidate<T> (T card, boolean held, boolean owned, boolean bound, boolean automatic) {}

    record Selection<T> (T card, boolean ambiguous) {}

    private WirelessCardSelection() {}

    static <T> Selection<T> select(List<Candidate<T>> cards, boolean connecting) {
        T selected = null;
        int count = 0;
        for (Candidate<T> candidate : cards) {
            if (!candidate.owned() || connecting && (!candidate.bound() || !candidate.automatic())) continue;
            if (candidate.held()) return new Selection<>(candidate.card(), false);
            selected = candidate.card();
            count++;
        }
        return new Selection<>(count == 1 ? selected : null, count > 1);
    }
}
