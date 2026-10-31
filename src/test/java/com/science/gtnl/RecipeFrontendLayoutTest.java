package com.science.gtnl;

import static com.gtnltest.TestSupport.check;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

@Tag("architecture")
public final class RecipeFrontendLayoutTest {

    private static final Path BASELINE = Path.of("src/test/resources/compat/recipe-frontends.txt");

    @Test
    void slotCoordinatesAndDimensionsMatchThePreRefactorBaseline() throws Exception {
        check(
            Files.readAllLines(BASELINE)
                .equals(snapshot()),
            "recipe slot positions and dimensions changed");
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
            .map(RecipeFrontendLayoutTest::point)
            .toList()
            .toString();
    }

    private static String point(Pos2d point) {
        return point.x + "," + point.y;
    }
}
