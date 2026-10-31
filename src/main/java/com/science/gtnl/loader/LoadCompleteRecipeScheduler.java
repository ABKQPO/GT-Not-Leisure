package com.science.gtnl.loader;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregtech.GTMod;
import gregtech.api.recipe.RecipeMaps;
import gregtech.loaders.postload.recipes.FakeCuttingRecipes;

@EventBusSubscriber
public class LoadCompleteRecipeScheduler {

    private static boolean scheduled;
    private static boolean loaded;

    public static void schedule() {
        if (scheduled || loaded) return;

        scheduled = true;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        loadRecipesAfterCompleteInit();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        loadRecipesAfterCompleteInit();
    }

    private static void loadRecipesAfterCompleteInit() {
        if (!scheduled || loaded) return;

        loaded = true;

        ScienceNotLeisure.LOG.info("GTNL: Loading complete-init recipes after all mod load-complete handlers.");
        RecipeLoader.loadCompleteInit();
        RecipeMaps.cutterFakeRecipes.getBackend()
            .clearRecipes();
        new FakeCuttingRecipes().run();
        GTMod.proxy.reloadNEICache();
    }
}
