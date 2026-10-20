package com.science.gtnl.client.hud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class HudRegistry {

    private static final List<HudElement> ELEMENTS = new ArrayList<>();
    private static final List<BooleanSupplier> CROSSHAIR_USERS = new ArrayList<>();

    private HudRegistry() {}

    public static HudElement register(HudElement element) {
        if (byId(element.id) != null) throw new IllegalStateException("Duplicate HUD element " + element.id);
        ELEMENTS.add(element);
        return element;
    }

    public static List<HudElement> elements() {
        return Collections.unmodifiableList(ELEMENTS);
    }

    public static HudElement byId(String id) {
        for (HudElement element : ELEMENTS) if (element.id.equals(id)) return element;
        return null;
    }

    public static void reserveCrosshair(BooleanSupplier inUse) {
        CROSSHAIR_USERS.add(inUse);
    }

    public static boolean crosshairReserved() {
        for (BooleanSupplier user : CROSSHAIR_USERS) if (user.getAsBoolean()) return true;
        return false;
    }
}
