package com.science.gtnl.common.wireless;

import java.util.Locale;

/** Validated view-only filters, applied on the server before pagination. */
public record WirelessViewFilter(String query, Integer dimension, int state) {

    public static WirelessViewFilter parse(String query, String dimension, int state) {
        if (query.length() > 64 || dimension.length() > 12 || state < 0 || state > 4)
            throw new IllegalArgumentException("Invalid filter");
        return new WirelessViewFilter(
            query.strip()
                .toLowerCase(Locale.ROOT),
            dimension.isBlank() ? null : Integer.valueOf(dimension.strip()),
            state);
    }

    public boolean matches(String name, int dimension, String position, boolean paused, String state) {
        return (this.dimension == null || this.dimension == dimension)
            && (query.isEmpty() || (name + " " + position).toLowerCase(Locale.ROOT)
                .contains(query))
            && switch (this.state) {
            case 1 -> paused;
            case 2 -> state.equals("conflict");
            case 3 -> !paused && state.equals("active");
            case 4 -> !paused && state.equals("waiting");
            default -> true;
            };
    }
}
