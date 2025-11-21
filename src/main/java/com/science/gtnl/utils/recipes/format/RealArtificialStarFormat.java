package com.science.gtnl.utils.recipes.format;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class RealArtificialStarFormat implements INEISpecialInfoFormatter {

    public static final RealArtificialStarFormat INSTANCE = new RealArtificialStarFormat();

    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> msgs = new ArrayList<>();
        msgs.add(
            StatCollector.translateToLocal("NEI.RealArtificialStarGeneratingRecipes.specialValue")
                + recipeInfo.recipe.mSpecialValue
                + " × 2,147,483,647 EU");
        return msgs;
    }
}
