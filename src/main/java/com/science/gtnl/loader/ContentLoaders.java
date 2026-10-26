package com.science.gtnl.loader;

import java.util.List;
import java.util.function.Consumer;

import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ContentLoaders {

    // Order is part of save compatibility: blocks and items keep their old registration order, new cards go last.
    public static final List<IContentLoader> LOADERS = List.of(
        new EffectLoader(),
        new EntityLoader(),
        new BlockLoader(),
        new ItemLoader(),
        new MaterialLoader(),
        new WorldLoader(),
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

    private static List<IContentLoader> enabled = List.of();

    private ContentLoaders() {}

    public static void preInit() {
        List<IContentLoader> on = new ObjectArrayList<>();
        for (IContentLoader loader : LOADERS) {
            if (loader.isEnabled()) on.add(loader);
            else ScienceNotLeisure.LOG.info("GTNL content loader {} is disabled", loader.name());
        }
        enabled = on;
        run("preInit", IContentLoader::preInit);
    }

    public static void init() {
        run("init", IContentLoader::init);
    }

    public static void postInit() {
        run("postInit", IContentLoader::postInit);
    }

    public static void loadComplete() {
        run("loadComplete", IContentLoader::loadComplete);
    }

    public static void serverAboutToStart() {
        run("serverAboutToStart", IContentLoader::serverAboutToStart);
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        run("serverStarting", loader -> loader.serverStarting(event));
    }

    public static void serverStopping() {
        run("serverStopping", IContentLoader::serverStopping);
    }

    @SideOnly(Side.CLIENT)
    public static void clientPreInit() {
        for (IContentLoader loader : enabled) {
            try {
                loader.clientPreInit();
            } catch (RuntimeException e) {
                throw failure(loader, "clientPreInit", e);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void clientInit() {
        for (IContentLoader loader : enabled) {
            try {
                loader.clientInit();
            } catch (RuntimeException e) {
                throw failure(loader, "clientInit", e);
            }
        }
    }

    private static void run(String phase, Consumer<IContentLoader> action) {
        for (IContentLoader loader : enabled) {
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
