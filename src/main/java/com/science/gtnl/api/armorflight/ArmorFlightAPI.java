package com.science.gtnl.api.armorflight;

import com.science.gtnl.common.armorflight.ArmorFlightService;

public final class ArmorFlightAPI {

    private static final IArmorFlightService SERVICE = new ArmorFlightService();

    private ArmorFlightAPI() {}

    public static IArmorFlightService service() {
        return SERVICE;
    }
}
