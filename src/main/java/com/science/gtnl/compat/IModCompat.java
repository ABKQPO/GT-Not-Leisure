package com.science.gtnl.compat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Entry point of one mod integration under {@code compat/<mod>/}. {@code CompatLoader} creates it only when that mod
 * is loaded, so its methods may use the mod's classes freely. {@code isEnabled} adds conditions besides the mod
 * itself. Errors are not caught: a broken integration fails the load instead of silently dropping recipes or quests.
 */
public interface IModCompat {

    default boolean isEnabled() {
        return true;
    }

    default void preInit() {}

    default void init() {}

    default void postInit() {}

    default void loadComplete() {}

    @SideOnly(Side.CLIENT)
    default void clientPreInit() {}

    @SideOnly(Side.CLIENT)
    default void clientInit() {}
}
