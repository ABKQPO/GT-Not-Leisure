package com.science.gtnl.utils.recipes.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class ElectrocellGeneratorMetadata extends RecipeMetadataKey<Long> implements INEISpecialInfoFormatter {

    public static final ElectrocellGeneratorMetadata INSTANCE = new ElectrocellGeneratorMetadata();

    public ElectrocellGeneratorMetadata() {
        super(Long.class, "electricellgeneratorfrontend_metadata");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        long generatorEUt = cast(value, 1L);
        recipeInfo.drawText(StatCollector.translateToLocalFormatted("ElectrocellGeneratorMetadata", generatorEUt));
    }

    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> specialInfo = new ArrayList<>();
        specialInfo.add(
            StatCollector.translateToLocalFormatted(
                "NEI.ElectrocellGenerator.specialValue",
                recipeInfo.recipe.mSpecialValue / 100D));
        return specialInfo;
    }

}
