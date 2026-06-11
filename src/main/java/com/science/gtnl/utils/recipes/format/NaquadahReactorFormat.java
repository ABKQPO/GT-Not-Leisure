package com.science.gtnl.utils.recipes.format;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.utils.recipes.metadata.NaquadahReactorMetadata;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class NaquadahReactorFormat implements INEISpecialInfoFormatter {

    @Override
    @NotNull
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> msgs = new ArrayList<>();
        msgs.add(
            StatCollector.translateToLocal("NEI.NaquadahReactorRecipes.specialValue") + NumberFormatUtil.formatNumber(
                recipeInfo.recipe.getMetadataOrDefault(NaquadahReactorMetadata.INSTANCE, Pair.of(0, 0L))
                    .getValue())
                + " EU/t");
        return msgs;
    }
}
