package com.science.gtnl.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dreammaster.scripts.IScriptLoader;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.recipe.script.ScriptAvaritia;
import com.science.gtnl.common.recipe.script.ScriptTinkersConstruct;

import gregtech.api.enums.Mods;

public class ScriptLoader {

    public static void registry() {

        List<IScriptLoader> scripts = new ArrayList<>(Arrays.asList(new ScriptTinkersConstruct()));

        if (Mods.Avaritia.isModLoaded()) scripts.add(new ScriptAvaritia());

        ArrayList<String> errored = new ArrayList<>();
        final long totalTimeStart = System.currentTimeMillis();
        for (IScriptLoader script : scripts) {
            if (script.isScriptLoadable()) {
                try {
                    final long timeStart = System.currentTimeMillis();
                    script.loadRecipes();
                    final long timeToLoad = System.currentTimeMillis() - timeStart;
                    ScienceNotLeisure.LOG.info("Loaded {} script in {} ms.", script.getScriptName(), timeToLoad);
                } catch (Exception ex) {
                    errored.add(script.getScriptName());
                    ScienceNotLeisure.LOG.error("There was an error while loading {}!", script.getScriptName(), ex);
                }
            } else {
                ScienceNotLeisure.LOG
                    .info("Missing dependencies to load {} script. It won't be loaded.", script.getScriptName());
            }
        }
        final long totalTimeToLoad = System.currentTimeMillis() - totalTimeStart;
        ScienceNotLeisure.LOG.info("Script loader took {} ms.", totalTimeToLoad);
        if (!errored.isEmpty()) throw new RuntimeException(
            "Scripts " + errored + " thrown an exception! Scroll up the log to see the stacktrace!");
    }
}
