package com.science.gtnl;

import static com.gtnltest.ContractAssertions.check;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.science.gtnl.common.gui.recipe.EGTWUpgradeCostFrontend;
import com.science.gtnl.common.gui.recipe.ExtremeExtremeEntityCrusherFrontend;
import com.science.gtnl.common.gui.recipe.FallingTowerFrontend;
import com.science.gtnl.common.gui.recipe.IndustrialInfusionCraftingRecipesFrontend;
import com.science.gtnl.common.gui.recipe.SteamGateAssemblerFrontend;

import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class RecipeFrontendContracts {

    private static final Path BASELINE = Path.of("src/test/resources/compat/recipe-frontends.txt");

    public static void main(String[] args) throws Exception {
        List<String> actual = snapshot();
        if (args.length == 1 && args[0].equals("--record")) Files.write(BASELINE, actual);
        else check(
            Files.readAllLines(BASELINE)
                .equals(actual),
            "recipe slot positions and dimensions match before the adapter move");
    }

    public static void run() throws Exception {
        main(new String[0]);
    }

    private static List<String> snapshot() throws ReflectiveOperationException {
        var neiBuilder = NEIRecipePropertiesBuilder.class.getDeclaredConstructor();
        neiBuilder.setAccessible(true);
        List<BiFunction<BasicUIPropertiesBuilder, NEIRecipePropertiesBuilder, RecipeMapFrontend>> factories = List.of(
            SteamGateAssemblerFrontend::new,
            ExtremeExtremeEntityCrusherFrontend::new,
            FallingTowerFrontend::new,
            EGTWUpgradeCostFrontend::new,
            IndustrialInfusionCraftingRecipesFrontend::new);
        List<String> result = new ObjectArrayList<>();
        for (var factory : factories) {
            for (int capacity : new int[] { 1, 9, 25 }) {
                RecipeMapFrontend page = factory.apply(
                    BasicUIProperties.builder()
                        .maxItemInputs(capacity)
                        .maxItemOutputs(16)
                        .maxFluidInputs(2)
                        .maxFluidOutputs(2),
                    neiBuilder.newInstance());
                result.add(
                    page.getClass()
                        .getSimpleName() + " capacity="
                        + capacity);
                var dimensions = page.getNEIProperties().recipeBackgroundSize;
                result.add("size=" + dimensions.width + "," + dimensions.height);
                result.add("special=" + point(page.getSpecialItemPosition()));
                for (int count = 0; count <= 16; count++) {
                    result.add("items-in " + count + " " + points(page.getItemInputPositions(count)));
                    result.add("items-out " + count + " " + points(page.getItemOutputPositions(count)));
                    result.add("fluids-in " + count + " " + points(page.getFluidInputPositions(count)));
                    result.add("fluids-out " + count + " " + points(page.getFluidOutputPositions(count)));
                }
            }
        }
        return result;
    }

    private static String points(List<Pos2d> points) {
        return points.stream()
            .map(RecipeFrontendContracts::point)
            .toList()
            .toString();
    }

    private static String point(Pos2d point) {
        return point.x + "," + point.y;
    }
}
