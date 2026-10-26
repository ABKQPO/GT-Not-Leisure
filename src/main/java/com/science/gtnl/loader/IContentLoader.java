package com.science.gtnl.loader;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * One registration card per system. {@link ContentLoaders} calls every enabled card in a fixed order at each load
 * phase. Client phases are stripped on the dedicated server, so a card may call client classes only from them.
 */
public interface IContentLoader {

    /**
     * Name used in log lines and in the error raised when a phase of this card fails.
     */
    String name();

    default boolean isEnabled() {
        return true;
    }

    default void preInit() {}

    default void init() {}

    default void postInit() {}

    default void loadComplete() {}

    default void serverAboutToStart() {}

    default void serverStarting(FMLServerStartingEvent event) {}

    default void serverStopping() {}

    @SideOnly(Side.CLIENT)
    default void clientPreInit() {}

    @SideOnly(Side.CLIENT)
    default void clientInit() {}
}
