package com.science.gtnl.common.recipe.Special;

import static com.science.gtnl.Utils.item.TextHandler.texter;

import java.util.ArrayList;
import java.util.List;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class NaquadahReactorSpecialValue implements INEISpecialInfoFormatter {

    public static final NaquadahReactorSpecialValue INSTANCE = new NaquadahReactorSpecialValue();

    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> msgs = new ArrayList<>();
        msgs.add(
            texter("Production power: ", "NEI.NaquadahReactorRecipes.specialValue") + recipeInfo.recipe.mSpecialValue
                + " EU/t");
        return msgs;
    }
}
