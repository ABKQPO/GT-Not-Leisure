package com.science.gtnl.loader;

import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class LoadCompleteRecipeScheduler {

    private static final LoadCompleteRecipeScheduler INSTANCE = new LoadCompleteRecipeScheduler();

    private static boolean scheduled;
    private static boolean loaded;

    private LoadCompleteRecipeScheduler() {}

    public static void schedule() {
        if (scheduled || loaded) return;

        scheduled = true;
        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        loadRecipesAfterCompleteInit();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        loadRecipesAfterCompleteInit();
    }

    private void loadRecipesAfterCompleteInit() {
        if (loaded) return;

        loaded = true;
        FMLCommonHandler.instance()
            .bus()
            .unregister(this);

        ScienceNotLeisure.LOG.info("GTNL: Loading complete-init recipes after all mod load-complete handlers.");
        RecipeLoader.loadCompleteInit();
    }
}
