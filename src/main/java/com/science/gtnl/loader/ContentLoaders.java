package com.science.gtnl.loader;

import java.util.List;
import java.util.function.Consumer;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContentLoaders {

    // Order is part of save compatibility: blocks and items keep their old registration order, new cards go last.
    // Block and item tables join through nested cards, so building this list does not create any item early.
    public static final List<IContentLoader> LOADERS = List.of(
        new EffectLoader(),
        new EntityLoader(),
        new BlockLoader.Card(),
        new ItemLoader.Card(),
        new MaterialLoader(),
        new MachineLoader(),
        new MELoader(),
        new CompatLoader(),
        new RecipeLoader(),
        new AchievementsLoader(),
        new CelestweaveLoader(),
        new RailgunLoader(),
        new PortalGunLoader(),
        new EquipmentLoader(),
        new WirelessLoader(),
        new MiniTerminalLoader(),
        new YogSothothLoader(),
        new QuantumLoader(),
        new BeamFormerLoader(),
        new SteamLoader(),
        new StellarLoader(),
        new GreenhouseLoader(),
        new DetravLoader(),
        new TickrateLoader(),
        new VoidMinerLoader(),
        new TextEffectLoader(),
        new CommandLoader(),
        new RenderLoader());

    public enum Phase {
        NOT_STARTED,
        PRE_INIT,
        INIT,
        POST_INIT,
        LOAD_COMPLETE
    }

    private static Phase phase = Phase.NOT_STARTED;

    public static Phase phase() {
        return phase;
    }

    public static void preInit() {
        advance(Phase.NOT_STARTED, Phase.PRE_INIT);
        run("preInit", IContentLoader::preInit);
    }

    public static void init() {
        advance(Phase.PRE_INIT, Phase.INIT);
        run("init", IContentLoader::init);
    }

    public static void postInit() {
        advance(Phase.INIT, Phase.POST_INIT);
        run("postInit", IContentLoader::postInit);
    }

    public static void loadComplete() {
        advance(Phase.POST_INIT, Phase.LOAD_COMPLETE);
        run("loadComplete", IContentLoader::loadComplete);
        CompatSnapshot.writeIfRequested();
    }

    public static void serverAboutToStart() {
        require(Phase.LOAD_COMPLETE, "serverAboutToStart");
        run("serverAboutToStart", IContentLoader::serverAboutToStart);
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        require(Phase.LOAD_COMPLETE, "serverStarting");
        run("serverStarting", loader -> loader.serverStarting(event));
    }

    public static void serverStopping() {
        run("serverStopping", IContentLoader::serverStopping);
    }

    @SideOnly(Side.CLIENT)
    public static void clientPreInit() {
        require(Phase.PRE_INIT, "clientPreInit");
        run("clientPreInit", IContentLoader::clientPreInit);
    }

    @SideOnly(Side.CLIENT)
    public static void clientInit() {
        require(Phase.INIT, "clientInit");
        run("clientInit", IContentLoader::clientInit);
    }

    private static void advance(Phase expected, Phase next) {
        require(expected, next.name());
        phase = next;
    }

    private static void require(Phase expected, String step) {
        if (phase != expected) throw new IllegalStateException(
            "GTNL content loading ran " + step + " while in phase " + phase + "; it must run in phase " + expected);
    }

    private static void run(String phase, Consumer<IContentLoader> action) {
        for (IContentLoader loader : LOADERS) {
            try {
                action.accept(loader);
            } catch (RuntimeException e) {
                throw failure(loader, phase, e);
            }
        }
    }

    private static IllegalStateException failure(IContentLoader loader, String phase, RuntimeException cause) {
        return new IllegalStateException("GTNL content loader " + loader.name() + " failed in " + phase, cause);
    }
}
