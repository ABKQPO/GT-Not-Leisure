package com.science.gtnl.utils.appliedEnergistics;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

public class InterfaceNameControllerContext {

    private static final ThreadLocal<MTEMultiBlockBase> CURRENT = new ThreadLocal<>();

    private InterfaceNameControllerContext() {}

    public static MTEMultiBlockBase enter(MTEMultiBlockBase controller) {
        MTEMultiBlockBase previous = CURRENT.get();
        CURRENT.set(controller);
        return previous;
    }

    public static void restore(MTEMultiBlockBase previous) {
        if (previous == null) CURRENT.remove();
        else CURRENT.set(previous);
    }

    public static MTEMultiBlockBase current() {
        return CURRENT.get();
    }
}
