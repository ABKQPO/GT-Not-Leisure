package com.science.gtnl.utils.recipes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;

import net.minecraft.item.ItemStack;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

// Original code from Overpowered Mod. MIT License. 2025/4/18
public class ChanceBonusManager {

    public static LinkedList<ChanceBonusProvider> bonusProviders = new LinkedList<>();
    public static CustomChanceBonusProvider customProvider = new CustomChanceBonusProvider();

    public interface ChanceBonusProvider {

        Double getBonus(Object machine, int recipeTier, double prevMultiplier, GTRecipe recipe);
    }

    @Desugar
    public record ChanceBonusProviderContext(Object machine, int recipeTier, double prevChanceMultiplier,
        GTRecipe recipe) {}

    public static void addLastBonusProvider(ChanceBonusProvider provider) {
        bonusProviders.addLast(provider);
    }

    public static void addFirstBonusProvider(ChanceBonusProvider provider) {
        bonusProviders.addFirst(provider);
    }

    public static void addLastBonusProvider(Function<ChanceBonusProviderContext, Double> provider) {
        bonusProviders.addLast(
            (machine, tier, prev, recipe) -> provider
                .apply(new ChanceBonusProviderContext(machine, tier, prev, recipe)));
    }

    public static void addFirstBonusProvider(Function<ChanceBonusProviderContext, Double> provider) {
        bonusProviders.addFirst(
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

    public static GTRecipe copyAndBonusChance(GTRecipe recipe, double bonus) {
        if (isBlacklisted(recipe)) return recipe;

        if (recipe.mChances == null) return recipe;

        GTRecipe copy = recipe.copy();
        int[] newChances = new int[copy.mChances.length];
        for (int i = 0; i < copy.mChances.length; i++) {
            newChances[i] = Math.min((int) (copy.mChances[i] + (bonus * 10000)), 10000);
        }
        copy.mChances = newChances;
        return copy;
    }

    static {
        ChanceBonusManager.addFirstBonusProvider(customProvider);
        addLastBonusProvider((machine, recipeTier, prevBonus, recipe) -> {
            if (machine instanceof MTEMultiBlockBase mte) {
                try {
                    int machineTier = GTUtility.getTier(Math.min(Integer.MAX_VALUE, mte.getMaxInputVoltage()));
                    int baseTier = GTUtility.getTier(recipe.mEUt);
                    double bonusPerTier = MainConfig.recipeOutputChance / 100.0;

                    return getTierChanceBonus(Math.min(16, machineTier), baseTier, bonusPerTier);
                } catch (Exception e) {
                    ScienceNotLeisure.LOG.warn("Error reading MTEMultiBlockBase voltage tier:{}", mte, e);
                }
            }
            return null;
        });
    }

    public static List<RecipePair> BLACKLIST = new ArrayList<>();

    static {
        addToBlacklist(
            WerkstoffLoader.PTSaltCrude.get(OrePrefixes.dust, 1),
            WerkstoffLoader.PTSaltRefined.get(OrePrefixes.dust, 1));
        addToBlacklist(
            WerkstoffLoader.PDSalt.get(OrePrefixes.dust, 1),
            WerkstoffLoader.PDMetallicPowder.get(OrePrefixes.dust, 1));
    }

    public static void addToBlacklist(ItemStack input, ItemStack output) {
        BLACKLIST.add(new RecipePair(input, output));
    }

    private static boolean isBlacklisted(GTRecipe recipe) {
        if (recipe.mInputs.length == 0 || recipe.mOutputs.length == 0) return false;

        for (RecipePair pair : BLACKLIST) {
            if (recipe.mInputs[0].isItemEqual(pair.input) && recipe.mOutputs[0].isItemEqual(pair.output)) {
                return true;
            }
        }
        return false;
    }

    @Desugar
    public record RecipePair(ItemStack input, ItemStack output) {}

}
