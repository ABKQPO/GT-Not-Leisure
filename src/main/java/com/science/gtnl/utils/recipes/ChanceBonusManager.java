package com.science.gtnl.utils.recipes;

import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;

import net.minecraft.item.ItemStack;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.config.MainConfig;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// Original code from Overpowered Mod. MIT License. 2025/4/18
public class ChanceBonusManager {

    public static ObjectArrayList<ChanceBonusProvider> bonusProviders = new ObjectArrayList<>();
    public static CustomChanceBonusProvider customProvider = new CustomChanceBonusProvider();

    public interface ChanceBonusProvider {

        Double getBonus(Object machine, int recipeTier, double prevMultiplier, GTRecipe recipe);
    }

    @Desugar
    public record ChanceBonusProviderContext(Object machine, int recipeTier, double prevChanceMultiplier,
        GTRecipe recipe) {}

    public static void addLastBonusProvider(ChanceBonusProvider provider) {
        bonusProviders.add(provider);
    }

    public static void addFirstBonusProvider(ChanceBonusProvider provider) {
        bonusProviders.add(0, provider);
    }

    public static void addLastBonusProvider(Function<ChanceBonusProviderContext, Double> provider) {
        bonusProviders.add(
            (machine, tier, prev, recipe) -> provider
                .apply(new ChanceBonusProviderContext(machine, tier, prev, recipe)));
    }

    public static void addFirstBonusProvider(Function<ChanceBonusProviderContext, Double> provider) {
        bonusProviders.add(
            0,
            (machine, tier, prev, recipe) -> provider
                .apply(new ChanceBonusProviderContext(machine, tier, prev, recipe)));
    }

    public static double getTierChanceBonus(int tier, int baseTier, double bonusPerTier) {
        return tier <= baseTier ? 0.0 : (tier - baseTier) * bonusPerTier;
    }

    public static Double getChanceBonus(Object machine, int recipeTier, double prevMultiplier, GTRecipe recipe) {
        for (ChanceBonusProvider provider : bonusProviders) {
            try {
                Double bonus = provider.getBonus(machine, recipeTier, prevMultiplier, recipe);
                if (bonus != null) return bonus;
            } catch (Exception e) {
                ScienceNotLeisure.LOG.warn("Error in chance bonus provider", e);
            }
        }
        return null;
    }

    public static OptionalDouble getChanceBonusOptional(Object machine, int recipeTier, double prevMultiplier,
        GTRecipe recipe) {
        Double result = getChanceBonus(machine, recipeTier, prevMultiplier, recipe);
        return result != null ? OptionalDouble.of(result) : OptionalDouble.empty();
    }

    public static int[] applyBonusChance(int[] chances, double bonus) {
        if (chances == null) return null;

        int[] result = new int[chances.length];
        int add = (int) (bonus * 10000);

        for (int i = 0; i < chances.length; i++) {
            result[i] = Math.min(chances[i] + add, 10000);
        }

        return result;
    }

    public static GTRecipe copyAndBonusChance(GTRecipe recipe, double bonus) {
        if (isBlacklisted(recipe)) return recipe;

        GTRecipe copy = recipe.copy();
        copy.mOutputChances = applyBonusChance(copy.mOutputChances, bonus);
        copy.mFluidOutputChances = applyBonusChance(copy.mFluidOutputChances, bonus);
        return copy;
    }

    static {
        ChanceBonusManager.addFirstBonusProvider(customProvider);
        addLastBonusProvider((machine, recipeTier, prevBonus, recipe) -> {
            if (machine instanceof MTEMultiBlockBase mte) {
                try {
                    int machineTier = GTUtility.getTier(Math.min(Integer.MAX_VALUE, mte.getMaxInputVoltage()));
                    int baseTier = GTUtility.getTier(recipe.mEUt);
                    double bonusPerTier = MainConfig.machine.recipeOutputChance / 100.0;

                    return getTierChanceBonus(Math.min(16, machineTier), baseTier, bonusPerTier);
                } catch (Exception e) {
                    ScienceNotLeisure.LOG.warn("Error reading MTEMultiBlockBase voltage tier:{}", mte, e);
                }
            }
            return null;
        });
    }

    public static List<RecipePair> BLACKLIST = new ObjectArrayList<>();

    static {
        addToBlacklist(
            WerkstoffLoader.PTSaltCrude.get(OrePrefixes.dust, 1),
            WerkstoffLoader.PTSaltRefined.get(OrePrefixes.dust, 1));
        addToBlacklist(
            WerkstoffLoader.PDSalt.get(OrePrefixes.dust, 1),
            WerkstoffLoader.PDMetallicPowder.get(OrePrefixes.dust, 1));
        addToBlacklist(null, null, GTNLRecipeMaps.FishingGroundRecipes);
    }

    public static void addToBlacklist(ItemStack input, ItemStack output) {
        BLACKLIST.add(new RecipePair(new ItemStack[] { input }, new ItemStack[] { output }, null));
    }

    public static void addToBlacklist(ItemStack[] input, ItemStack[] output) {
        addToBlacklist(input, output, null);
    }

    public static void addToBlacklist(ItemStack[] input, ItemStack[] output, RecipeMap<?> recipeMap) {
        BLACKLIST.add(new RecipePair(input, output, recipeMap));
    }

    public static boolean isBlacklisted(GTRecipe recipe) {
        if (recipe == null) return false;

        for (RecipePair pair : BLACKLIST) {
            boolean match = true;

            if (pair.recipeMap() != null) {
                if (recipe.getRecipeCategory() == null || recipe.getRecipeCategory().recipeMap != pair.recipeMap()) {
                    match = false;
                }
            }

            if (match && pair.input() != null && pair.input().length > 0) {
                if (recipe.mInputs == null) {
                    match = false;
                } else {
                    for (ItemStack required : pair.input()) {
                        boolean found = false;

                        for (ItemStack in : recipe.mInputs) {
                            if (in != null && required != null && in.isItemEqual(required)) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            match = false;
                            break;
                        }
                    }
                }
            }

            if (match && pair.output() != null && pair.output().length > 0) {
                if (recipe.mOutputs == null) {
                    match = false;
                } else {
                    for (ItemStack required : pair.output()) {
                        boolean found = false;

                        for (ItemStack out : recipe.mOutputs) {
                            if (out != null && required != null && out.isItemEqual(required)) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            match = false;
                            break;
                        }
                    }
                }
            }

            if (match) {
                return true;
            }
        }

        return false;
    }

    @Desugar
    public record RecipePair(ItemStack[] input, ItemStack[] output, RecipeMap<?> recipeMap) {}
}
