package com.science.gtnl.compat.nech;

import java.lang.reflect.Method;

import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class NechSearchMatcher {

    private Object api;
    private Method contains;

    public NechSearchMatcher() {
        if (!Loader.isModLoaded("nech")) return;
        try {
            Class<?> type = Class.forName("com.asdflj.nech.API");
            api = type.getField("INSTANCE")
                .get(null);
            contains = type.getMethod("contains", String.class, String.class);
        } catch (ReflectiveOperationException | LinkageError failure) {
            ScienceNotLeisure.LOG.warn("Cannot initialize optional Rework search", failure);
        }
    }

    public boolean contains(String text, String query) {
        if (contains == null) return false;
        try {
            return (boolean) contains.invoke(api, text, query);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            contains = null;
            ScienceNotLeisure.LOG.warn("Rework search failed; using literal matching", failure);
            return false;
        }
    }
}
