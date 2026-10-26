package com.science.gtnl.utils.recipes.format;

import java.util.List;

import net.minecraft.util.StatCollector;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RealArtificialStarFormat implements INEISpecialInfoFormatter {

    public static final RealArtificialStarFormat INSTANCE = new RealArtificialStarFormat();

    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> msgs = new ObjectArrayList<>();
        msgs.add(
            StatCollector.translateToLocal("gtnl.nei.real_artificial_star.generating.special_value")
                + recipeInfo.recipe.mSpecialValue
                + " × 2,147,483,647 EU");
        return msgs;
    }
}
