package com.science.gtnl.utils.recipes.format;

import static gtnhintergalactic.recipe.IGRecipeMaps.*;
import static gtnhintergalactic.recipe.IGRecipeMaps.SPACE_LOCATION;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public class SpaceMinerFormat implements INEISpecialInfoFormatter {

    @Override
    @NotNull
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> specialInfo = new ArrayList<>();
        specialInfo.add(StatCollector.translateToLocalFormatted("ig.nei.module", recipeInfo.recipe.mSpecialValue));

        String neededProject = recipeInfo.recipe.getMetadata(SPACE_PROJECT);
        String neededProjectLocation = recipeInfo.recipe.getMetadata(SPACE_LOCATION);
        if (neededProject != null && !neededProject.isEmpty()) {
            specialInfo.add(
                String.format(
                    StatCollector.translateToLocal("ig.nei.spaceassembler.project"),
                    SpaceProjectManager.getProject(neededProject)
                        .getLocalizedName()));
            specialInfo.add(
                String.format(
                    StatCollector.translateToLocal("ig.nei.spaceassembler.projectAt"),
                    neededProjectLocation == null || neededProjectLocation.isEmpty()
                        ? StatCollector.translateToLocal("ig.nei.spaceassembler.projectAnyLocation")
                        : StatCollector.translateToLocal(
                            SpaceProjectManager.getLocation(neededProjectLocation)
                                .getUnlocalizedName())));
        }
        return specialInfo;
    }
}
