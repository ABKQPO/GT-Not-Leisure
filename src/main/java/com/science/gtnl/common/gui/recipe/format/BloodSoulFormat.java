package com.science.gtnl.common.gui.recipe.format;

import java.util.List;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class BloodSoulFormat implements INEISpecialInfoFormatter {

    @NotNull
    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> specialInfo = new ObjectArrayList<>();
        if (recipeInfo.recipe.mSpecialValue > 0) {
            specialInfo.add(
                String.format(
                    StatCollector.translateToLocal("gtnl.nei.blood_soul_sacrificial_array.special_value"),
                    recipeInfo.recipe.mSpecialValue));
        }
        return specialInfo;
    }
}
