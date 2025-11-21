package com.science.gtnl.utils.recipes.format;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class BloodSoulFormat implements INEISpecialInfoFormatter {

    @NotNull
    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> specialInfo = new ArrayList<>();
        if (recipeInfo.recipe.mSpecialValue > 0) {
            specialInfo.add(
                String.format(
                    StatCollector.translateToLocal("NEI.BloodSoulSacrificialArray.specialValue"),
                    recipeInfo.recipe.mSpecialValue));
        }
        return specialInfo;
    }
}
