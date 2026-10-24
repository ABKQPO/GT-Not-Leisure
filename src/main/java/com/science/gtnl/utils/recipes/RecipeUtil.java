package com.science.gtnl.utils.recipes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RecipeUtil {

    private static final ObjectArrayList<ItemStack> mEmptyItems = new ObjectArrayList<>();

    static {
        mEmptyItems.add(ItemList.Cell_Empty.get(1));
        mEmptyItems.add(new ItemStack(Items.bowl));
        mEmptyItems.add(new ItemStack(Items.bucket));
        mEmptyItems.add(new ItemStack(Items.glass_bottle));
    }

    public static synchronized void copyAllRecipes(RecipeMap<?> fromMap, RecipeMap<?> toMap) {
        for (GTRecipe recipe : fromMap.getAllRecipes()) {
            if (recipe != null) {
                GTRecipe copiedRecipe = recipe.copy();
                if (copiedRecipe != null) {
                    copiedRecipe.setNeiDesc(recipe.getNeiDesc());
                    toMap.add(copiedRecipe);
                }
            }
        }
    }

    public static synchronized void removeMatchingRecipes(RecipeMap<?> sourceMap, RecipeMap<?> targetMap) {
        for (GTRecipe source : sourceMap.getAllRecipes()) {
            List<GTRecipe> matches = targetMap.getAllRecipes()
                .stream()
                .filter(target -> recipesAreEquivalent(source, target))
                .toList();

            for (GTRecipe match : matches) {
                targetMap.getBackend()
                    .removeRecipe(match);
            }
        }
    }

    public static boolean recipesAreEquivalent(GTRecipe r1, GTRecipe r2) {
        return itemsMatch(r1.mInputs, r2.mInputs) && itemsMatch(r1.mOutputs, r2.mOutputs)
            && fluidsMatch(r1.mFluidInputs, r2.mFluidInputs)
            && fluidsMatch(r1.mFluidOutputs, r2.mFluidOutputs);
    }

    public static boolean itemsMatch(ItemStack[] a, ItemStack[] b) {
        if (a == null || b == null) return a == b;
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (!ItemStack.areItemStacksEqual(a[i], b[i])) return false;
        }
        return true;
    }

    public static boolean fluidsMatch(FluidStack[] a, FluidStack[] b) {
        if (a == null || b == null) return a == b;
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (!a[i].isFluidEqual(b[i]) || a[i].amount != b[i].amount) return false;
        }
        return true;
    }

    public static synchronized void generateRecipesNotUsingCells(RecipeMap<?> aInputs, RecipeMap<?> aOutputs,
        boolean specialItem) {
        generateRecipesNotUsingCells(aInputs, aOutputs, specialItem, 0);
    }

    public static synchronized void generateRecipesBioLab(RecipeMap<?> aInputs, RecipeMap<?> aOutputs,
        boolean specialItem, double chanceMultiplier) {
        int aRecipesHandled = 0;
        int aInvalidRecipesToConvert = 0;
        int aOriginalCount = aInputs.getAllRecipes()
            .size();

        Map<String, GTRecipe> uniqueRecipes = new Object2ObjectOpenHashMap<>();

        for (GTRecipe x : aInputs.getAllRecipes()) {
            if (x == null) {
                aInvalidRecipesToConvert++;
                continue;
            }

            // 克隆原配方数据
            ItemStack[] aInputItems = x.mInputs.clone();
            ItemStack[] aOutputItems = x.mOutputs.clone();
            FluidStack[] aInputFluids = x.mFluidInputs.clone();
            FluidStack[] aOutputFluids = x.mFluidOutputs.clone();
            ItemStack aSpecialItems = (x.mSpecialItems instanceof ItemStack) ? (ItemStack) x.mSpecialItems : null;

            List<ItemStack> aInputItemsMap = new ObjectArrayList<>();
            List<ItemStack> aOutputItemsMap = new ObjectArrayList<>();
            List<FluidStack> aInputFluidsMap = new ObjectArrayList<>();
            List<FluidStack> aOutputFluidsMap = new ObjectArrayList<>();

            if (aInputItems.length >= 4 && aInputItems[2] != null
                && aInputItems[3] != null
                && GTUtility.areStacksEqual(aInputItems[2], aInputItems[3])) {
                aInputItemsMap.add(GTUtility.getIntegratedCircuit(1));
            }

            // 物品->流体转换并过滤
            for (ItemStack in : aInputItems) {
                FluidStack found = getFluidFromItemStack(in);
                if (found == null) {
                    if (!isEmptyCell(in)) {
                        aInputItemsMap.add(in);
                    }
                } else {
                    found.amount *= in.stackSize;
                    aInputFluidsMap.add(found);
                }
            }

            for (ItemStack out : aOutputItems) {
                FluidStack found = getFluidFromItemStack(out);
                if (found == null) {
                    if (!isEmptyCell(out)) aOutputItemsMap.add(out);
                } else {
                    found.amount *= out.stackSize;
                    aOutputFluidsMap.add(found);
                }
            }

            if (specialItem && aSpecialItems != null) {
                aInputItemsMap.add(new ItemStack(aSpecialItems.getItem(), 0));
            }

            Collections.addAll(aInputFluidsMap, aInputFluids);
            Collections.addAll(aOutputFluidsMap, aOutputFluids);

            // 构造新数组
            ItemStack[] newItemsIn = aInputItemsMap.toArray(new ItemStack[0]);
            ItemStack[] newItemsOut = aOutputItemsMap.toArray(new ItemStack[0]);
            FluidStack[] newFluidsIn = aInputFluidsMap.toArray(new FluidStack[0]);
            FluidStack[] newFluidsOut = aOutputFluidsMap.toArray(new FluidStack[0]);

            if (!(ItemUtils.checkForInvalidItems(newItemsIn) && ItemUtils.checkForInvalidItems(newItemsOut))) {
                aInvalidRecipesToConvert++;
                continue; // Skip this recipe entirely if we find an item we don't like
            }

            int[] newInputChances = scaleChances(x.mInputChances, chanceMultiplier);
            int[] newOutputChances = scaleChances(x.mOutputChances, chanceMultiplier);
            int[] newFluidInputChances = scaleChances(x.mFluidInputChances, chanceMultiplier);
            int[] newFluidOutputChances = scaleChances(x.mFluidOutputChances, chanceMultiplier);

            GTRecipe newRecipe = new GTRecipe(
                false,
                newItemsIn,
                newItemsOut,
                specialItem ? null : x.mSpecialItems,
                newInputChances,
                newOutputChances,
                newFluidInputChances,
                newFluidOutputChances,
                newFluidsIn,
                newFluidsOut,
                x.mDuration,
                x.mEUt,
                x.mSpecialValue);
            newRecipe.isNBTSensitive = true;
            if (x.owners != null) {
                newRecipe.owners = new ObjectArrayList<>(x.owners);
            }

            // 构建包含 NBT 的唯一键
            StringBuilder key = new StringBuilder();
            // 输入物品与NBT
            for (ItemStack s : newItemsIn) {
                key.append(s.getUnlocalizedName())
                    .append('@')
                    .append(s.stackSize)
                    .append('@')
                    .append(
                        s.hasTagCompound() ? s.getTagCompound()
                            .toString() : "null")
                    .append(';');
            }
            // 输出物品与NBT
            for (ItemStack s : newItemsOut) {
                key.append(s.getUnlocalizedName())
                    .append('@')
                    .append(s.stackSize)
                    .append('@')
                    .append(
                        s.hasTagCompound() ? s.getTagCompound()
                            .toString() : "null")
                    .append(';');
            }
            // 输入流体
            for (FluidStack f : newFluidsIn) {
                key.append(
                    f.getFluid()
                        .getName())
                    .append('@')
                    .append(f.amount)
                    .append(f.tag != null ? '@' + f.tag.toString() : '@' + "null")
                    .append(';');
            }
            // 输出流体
            for (FluidStack f : newFluidsOut) {
                key.append(
                    f.getFluid()
                        .getName())
                    .append('@')
                    .append(f.amount)
                    .append(f.tag != null ? '@' + f.tag.toString() : '@' + "null")
                    .append(';');
            }

            String recipeKey = key.toString();
            GTRecipe existing = uniqueRecipes.get(recipeKey);
            // 保留 EUt 最低
            if (existing == null || newRecipe.mEUt < existing.mEUt) {
                uniqueRecipes.put(recipeKey, newRecipe);
            }
            aRecipesHandled++;
        }

        // 输出去重后的配方
        for (GTRecipe r : uniqueRecipes.values()) {
            aOutputs.add(r);
        }

        ScienceNotLeisure.LOG.info("Generated Recipes for {}", aOutputs.unlocalizedName);
        ScienceNotLeisure.LOG.info("Original Map contains {} recipes.", aOriginalCount);
        ScienceNotLeisure.LOG.info("Output Map contains {} recipes.", aRecipesHandled);
        ScienceNotLeisure.LOG.info("There were {} invalid recipes.", aInvalidRecipesToConvert);
    }

    public static synchronized void generateRecipesNotUsingCells(RecipeMap<?> aInputs, RecipeMap<?> aOutputs,
        boolean specialItem, double chanceMultiplier) {
        int aRecipesHandled = 0;
        int aInvalidRecipesToConvert = 0;
        int aOriginalCount = aInputs.getAllRecipes()
            .size();

        Map<String, GTRecipe> uniqueRecipes = new Object2ObjectOpenHashMap<>();

        for (GTRecipe x : aInputs.getAllRecipes()) {
            if (x == null) {
                aInvalidRecipesToConvert++;
                continue;
            }

            // 克隆原配方数据
            ItemStack[] aInputItems = x.mInputs.clone();
            ItemStack[] aOutputItems = x.mOutputs.clone();
            FluidStack[] aInputFluids = x.mFluidInputs.clone();
            FluidStack[] aOutputFluids = x.mFluidOutputs.clone();
            ItemStack aSpecialItems = (x.mSpecialItems instanceof ItemStack) ? (ItemStack) x.mSpecialItems : null;

            List<ItemStack> aInputItemsMap = new ObjectArrayList<>();
            List<ItemStack> aOutputItemsMap = new ObjectArrayList<>();
            List<FluidStack> aInputFluidsMap = new ObjectArrayList<>();
            List<FluidStack> aOutputFluidsMap = new ObjectArrayList<>();

            // 物品->流体转换并过滤
            for (ItemStack in : aInputItems) {
                FluidStack found = getFluidFromItemStack(in);
                if (found == null) {
                    if (!isEmptyCell(in)) {
                        aInputItemsMap.add(in);
                    }
                } else {
                    found.amount *= in.stackSize;
                    aInputFluidsMap.add(found);
                }
            }

            for (ItemStack out : aOutputItems) {
                FluidStack found = getFluidFromItemStack(out);
                if (found == null) {
                    if (!isEmptyCell(out)) aOutputItemsMap.add(out);
                } else {
                    found.amount *= out.stackSize;
                    aOutputFluidsMap.add(found);
                }
            }

            if (specialItem && aSpecialItems != null) {
                aInputItemsMap.add(new ItemStack(aSpecialItems.getItem(), 0));
            }

            Collections.addAll(aInputFluidsMap, aInputFluids);
            Collections.addAll(aOutputFluidsMap, aOutputFluids);

            // 构造新数组
            ItemStack[] newItemsIn = aInputItemsMap.toArray(new ItemStack[0]);
            ItemStack[] newItemsOut = aOutputItemsMap.toArray(new ItemStack[0]);
            FluidStack[] newFluidsIn = aInputFluidsMap.toArray(new FluidStack[0]);
            FluidStack[] newFluidsOut = aOutputFluidsMap.toArray(new FluidStack[0]);

            if (!(ItemUtils.checkForInvalidItems(newItemsIn) && ItemUtils.checkForInvalidItems(newItemsOut))) {
                aInvalidRecipesToConvert++;
                continue; // Skip this recipe entirely if we find an item we don't like
            }

            int[] newInputChances = scaleChances(x.mInputChances, chanceMultiplier);
            int[] newOutputChances = scaleChances(x.mOutputChances, chanceMultiplier);
            int[] newFluidInputChances = scaleChances(x.mFluidInputChances, chanceMultiplier);
            int[] newFluidOutputChances = scaleChances(x.mFluidOutputChances, chanceMultiplier);

            GTRecipe newRecipe = new GTRecipe(
                false,
                newItemsIn,
                newItemsOut,
                specialItem ? null : x.mSpecialItems,
                newInputChances,
                newOutputChances,
                newFluidInputChances,
                newFluidOutputChances,
                newFluidsIn,
                newFluidsOut,
                x.mDuration,
                x.mEUt,
                x.mSpecialValue);
            if (x.owners != null) {
                newRecipe.owners = new ObjectArrayList<>(x.owners);
            }

            // 构建包含 NBT 的唯一键
            StringBuilder key = new StringBuilder();
            // 输入物品与NBT
            for (ItemStack s : newItemsIn) {
                key.append(s.getUnlocalizedName())
                    .append('@')
                    .append(s.stackSize)
                    .append('@')
                    .append(
                        s.hasTagCompound() ? s.getTagCompound()
                            .toString() : "null")
                    .append(';');
            }
            // 输出物品与NBT
            for (ItemStack s : newItemsOut) {
                key.append(s.getUnlocalizedName())
                    .append('@')
                    .append(s.stackSize)
                    .append('@')
                    .append(
                        s.hasTagCompound() ? s.getTagCompound()
                            .toString() : "null")
                    .append(';');
            }
            // 输入流体
            for (FluidStack f : newFluidsIn) {
                key.append(
                    f.getFluid()
                        .getName())
                    .append('@')
                    .append(f.amount)
                    .append(f.tag != null ? '@' + f.tag.toString() : '@' + "null")
                    .append(';');
            }
            // 输出流体
            for (FluidStack f : newFluidsOut) {
                key.append(
                    f.getFluid()
                        .getName())
                    .append('@')
                    .append(f.amount)
                    .append(f.tag != null ? '@' + f.tag.toString() : '@' + "null")
                    .append(';');
            }

            String recipeKey = key.toString();
            GTRecipe existing = uniqueRecipes.get(recipeKey);
            // 保留 EUt 最低
            if (existing == null || newRecipe.mEUt < existing.mEUt) {
                uniqueRecipes.put(recipeKey, newRecipe);
            }
            aRecipesHandled++;
        }

        // 输出去重后的配方
        for (GTRecipe r : uniqueRecipes.values()) {
            aOutputs.add(r);
        }

        ScienceNotLeisure.LOG.info("Generated Recipes for {}", aOutputs.unlocalizedName);
        ScienceNotLeisure.LOG.info("Original Map contains {} recipes.", aOriginalCount);
        ScienceNotLeisure.LOG.info("Output Map contains {} recipes.", aRecipesHandled);
        ScienceNotLeisure.LOG.info("There were {} invalid recipes.", aInvalidRecipesToConvert);
    }

    private static int[] scaleChances(int[] chances, double multiplier) {
        if (chances == null) {
            return null;
        }

        int[] result = new int[chances.length];

        if (multiplier <= 0) {
            System.arraycopy(chances, 0, result, 0, chances.length);
            return result;
        }

        for (int i = 0; i < chances.length; i++) {
            result[i] = Math.min((int) (chances[i] * multiplier), 10000);
        }

        return result;
    }

    private static boolean isEmptyCell(ItemStack aCell) {
        if (aCell == null) {
            return false;
        }
        for (ItemStack emptyItem : mEmptyItems) {
            emptyItem.stackSize = aCell.stackSize;
            if (GTUtility.areStacksEqual(emptyItem, aCell)) {
                return true;
            }

        }
        return false;
    }

    private static synchronized FluidStack getFluidFromItemStack(final ItemStack ingot) {
        if (ingot == null) {
            return null;
        }
        return GTUtility.getFluidForFilledItem(ingot, true);
    }

    public static boolean isValidForSpaceStation(int dimId) {
        return DimensionManager.getProvider(dimId)
            .getClass()
            .getName()
            .endsWith("Space")
            || DimensionManager.getProvider(dimId)
                .getClass()
                .getName()
                .endsWith("SS")
            || DimensionManager.getProvider(dimId)
                .getClass()
                .getName()
                .contains("SpaceStation")
            || DimensionManager.getProvider(dimId)
                .getClass()
                .getName()
                .contains("WorldProvider");
    }

    public static boolean isValidForMothership(int dimId) {
        return DimensionManager.getProvider(dimId)
            .getClass()
            .getName()
            .contains("Mothership");
    }

    @NotNull
    public static final CheckRecipeResult NOT_IN_SPACE_STATION = SimpleCheckRecipeResult
        .ofFailure("not_in_space_station");
}
