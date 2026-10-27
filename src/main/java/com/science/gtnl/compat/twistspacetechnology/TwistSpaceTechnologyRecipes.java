package com.science.gtnl.compat.twistspacetechnology;

import net.minecraft.util.StatCollector;

import com.gtnllib.mod.ModList;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

public class TwistSpaceTechnologyRecipes {

    private TwistSpaceTechnologyRecipes() {}

    public static void realArtificialStar() {
        if (!ModList.TwistSpaceTechnology.isModLoaded()) return;

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.TwistSpaceAntimatter.get(1))
            .specialValue(3)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(GTNLRecipeMaps.RealArtificialStarRecipes);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.TwistSpaceAntimatterFuelRod.get(1))
            .itemOutputs(
                ModsItemlist.TwistSpaceStellarStructuralFrameMaterial.get(1)
                    .setStackDisplayName(
                        StatCollector.translateToLocal("gtnl.nei.real_antimatter_fuel_rod_generating_recipe.0")))
            .specialValue(1024)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(GTNLRecipeMaps.RealArtificialStarRecipes);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.TwistSpaceStrangeAnnihilationFuelRod.get(1))
            .itemOutputs(
                ModsItemlist.TwistSpaceStellarStructuralFrameMaterial.get(1)
                    .setStackDisplayName(
                        StatCollector.translateToLocal("gtnl.nei.real_antimatter_fuel_rod_generating_recipe.0")))
            .specialValue(32768)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(GTNLRecipeMaps.RealArtificialStarRecipes);
    }
}
