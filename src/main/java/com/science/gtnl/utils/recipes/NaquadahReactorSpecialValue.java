package com.science.gtnl.utils.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class NaquadahReactorSpecialValue implements INEISpecialInfoFormatter {

    public static final NaquadahReactorSpecialValue INSTANCE = new NaquadahReactorSpecialValue();

    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> msgs = new ArrayList<>();
        msgs.add(
            StatCollector.translateToLocal("NEI.NaquadahReactorRecipes.specialValue") + recipeInfo.recipe.mSpecialValue
                + " EU/t");
        return msgs;
    }
}
