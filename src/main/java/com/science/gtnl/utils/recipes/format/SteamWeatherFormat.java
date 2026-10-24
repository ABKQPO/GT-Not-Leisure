package com.science.gtnl.utils.recipes.format;

import java.util.List;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class SteamWeatherFormat implements INEISpecialInfoFormatter {

    @Override
    @NotNull
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> specialInfo = new ObjectArrayList<>();
        specialInfo.add(
            StatCollector.translateToLocal("gtnl.nei.steam_weather.special_value." + recipeInfo.recipe.mSpecialValue));
        return specialInfo;
    }
}
