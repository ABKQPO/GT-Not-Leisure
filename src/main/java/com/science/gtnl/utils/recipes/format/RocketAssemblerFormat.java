package com.science.gtnl.utils.recipes.format;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class RocketAssemblerFormat implements INEISpecialInfoFormatter {

    @NotNull
    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> specialInfo = new ArrayList<>();
        if (recipeInfo.recipe.mSpecialValue > 1) {
            specialInfo.add(
                String.format(
                    StatCollector.translateToLocal("NEI.RocketAssembler.specialValue"),
                    recipeInfo.recipe.mSpecialValue));
        }
        return specialInfo;
    }
}
