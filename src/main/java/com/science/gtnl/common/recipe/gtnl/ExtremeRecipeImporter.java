package com.science.gtnl.common.recipe.gtnl;

import static com.science.gtnl.ScienceNotLeisure.LOG;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.loader.RecipePool;

import fox.spiteful.avaritia.crafting.ExtremeCraftingManager;
import fox.spiteful.avaritia.crafting.ExtremeShapedOreRecipe;
import fox.spiteful.avaritia.crafting.ExtremeShapedRecipe;
import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class ExtremeRecipeImporter implements IRecipePool {

    public RecipeMap<?> SGAR = RecipePool.SteamGateAssemblerRecipes;

    @Override
    public void loadRecipes() {
        importRecipes(
            ExtremeCraftingManager.getInstance()
                .getRecipeList());
    }

    public void importRecipes(List<IRecipe> recipes) {

        for (IRecipe recipe : recipes) {
            if (recipe instanceof ExtremeShapedRecipe) {
                importShapedRecipe((ExtremeShapedRecipe) recipe);
            }
            if (recipe instanceof ExtremeShapedOreRecipe) {
                importShapedRecipe((ExtremeShapedOreRecipe) recipe);
            }
            if (recipe instanceof ShapelessOreRecipe) {
                importShapedRecipe((ShapelessOreRecipe) recipe);
            }
        }
    }

    private void importShapedRecipe(ExtremeShapedRecipe recipe) {
        ItemStack output = recipe.getRecipeOutput();
        if (output == null) {
            return;
        }

        ItemStack[] expandedInputs = expandRecipeInputs(recipe.recipeItems, recipe.recipeWidth, recipe.recipeHeight);

        createGTNHRecipe(expandedInputs, output);
    }

    private void importShapedRecipe(ExtremeShapedOreRecipe recipe) {
        ItemStack output = recipe.getRecipeOutput();
        if (output == null) {
            return;
        }

        try {
            GTValues.RA.stdBuilder()
                .itemInputs(recipe.getInput())
                .itemOutputs(output)
                .duration(128)
                .eut(0)
                .addTo(SGAR);
        } catch (Exception e) {
            ScienceNotLeisure.LOG
                .error("Failed to import ShapelessOreRecipe – output: {}, inputs: {}", output, recipe.getInput(), e);

        }
    }

    private void importShapedRecipe(ShapelessOreRecipe recipe) {
        ItemStack output = recipe.getRecipeOutput();
        if (output == null) {
            return;
        }

        try {
            GTValues.RA.stdBuilder()
                .itemInputs(
                    sortOutInputs(
                        recipe.getInput()
                            .toArray()))
                .itemOutputs(output)
                .duration(128)
                .eut(0)
                .addTo(SGAR);
        } catch (Exception e) {

            ScienceNotLeisure.LOG.error(
                "Failed to import ShapelessOreRecipe – output: {}, inputs: {}",
                output,
                sortOutInputs(
                    recipe.getInput()
                        .toArray()),
                e);
        }
    }

    public Object[] sortOutInputs(Object... in) {
        Map<Object, Integer> inputsMap = new LinkedHashMap<>();
        for (Object o : in) {
            if (o == null) continue;
            inputsMap.merge(o, 1, Integer::sum);
        }

        List<Object> sorted = new ArrayList<>();
        for (Map.Entry<Object, Integer> pair : inputsMap.entrySet()) {
            Object ingredient = pair.getKey();
            if (ingredient instanceof ItemStack i) {
                sorted.add(GTUtility.copyAmountUnsafe(pair.getValue(), i));
            } else if (ingredient instanceof ArrayList<?>list) {
                @SuppressWarnings("unchecked")
                ArrayList<ItemStack> itemList = (ArrayList<ItemStack>) list;

                String oreName = getOreNameByOreList(itemList);
                if (oreName != null) {
                    // if we managed to get the ore name of the list, we use the ore name.
                    sorted.add(new Object[] { oreName, pair.getValue() });
                } else {
                    // otherwise, just fallback to the first valid item in the list.
                    ItemStack firstValidItem = itemList.stream()
                        .filter(GTUtility::isStackValid)
                        .findFirst()
                        .orElse(null);
                    if (firstValidItem == null) throw new IllegalArgumentException();
                    ItemStack unifiedItem = GTOreDictUnificator.get(false, firstValidItem, true);
                    sorted.add(GTUtility.copyAmountUnsafe(pair.getValue(), unifiedItem));
                }
            } else {
                LOG.info("ERROR ExtremeCraftRecipeHandler.sortOutInputs catch unknown type");
            }
        }

        return sorted.toArray(new Object[0]);

    }

    public static @Nullable String getOreNameByOreList(ArrayList<ItemStack> oreList) {
        for (String oreName : OreDictionary.getOreNames()) {
            if (OreDictionary.getOres(oreName) == oreList) {
                return oreName;
            }
        }
        return null;
    }

    private ItemStack[] expandRecipeInputs(ItemStack[] original, int width, int height) {
        ItemStack[] expanded = new ItemStack[81];
        int xOffset = (9 - width) / 2;
        int yOffset = (9 - height) / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int expandedIndex = (y + yOffset) * 9 + (x + xOffset);
                int originalIndex = y * width + x;
                if (originalIndex < original.length) {
                    expanded[expandedIndex] = original[originalIndex];
                }
            }
        }

        return expanded;
    }

    private void createGTNHRecipe(ItemStack[] inputs, ItemStack output) {
        try {
            GTValues.RA.stdBuilder()
                .itemInputs((Object[]) inputs)
                .itemOutputs(output)
                .duration(128)
                .eut(0)
                .addTo(SGAR);
        } catch (Exception e) {

            ScienceNotLeisure.LOG
                .error("Failed to import ShapelessOreRecipe – output: {}, inputs: {}", output, inputs, e);

        }
    }
}
