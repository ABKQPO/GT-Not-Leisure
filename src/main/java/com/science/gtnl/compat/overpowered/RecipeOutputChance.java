package com.science.gtnl.compat.overpowered;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

import com.gtnllib.mod.ModList;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.ChanceBonusManager;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.StringUtils;

// GTNL's recipe output chance yields to Overpowered, which brings its own.
public class RecipeOutputChance {

    private RecipeOutputChance() {}

    public record Bonus(int tier, int baseTier, double bonus) {}

    public static boolean enabled() {
        return !ModList.Overpowered.isModLoaded() && MainConfig.machine.enableRecipeOutputChance;
    }

    public static Bonus bonusOf(TileEntity tile) {
        if (!(tile instanceof BaseMetaTileEntity baseMetaTileEntity)
            || !(baseMetaTileEntity.getMetaTileEntity() instanceof MTEMultiBlockBase mte)) return null;
        GTRecipe recipe = ChanceBonusManager.customProvider.getRecipeForMachine(mte);
        if (recipe == null) return null;
        int tier = GTUtility.getTier(mte.getMaxInputVoltage());
        int baseTier = GTUtility.getTier(recipe.mEUt);
        double bonus = tier <= baseTier ? 0.0 : (tier - baseTier) * MainConfig.machine.recipeOutputChance;
        return new Bonus(tier, baseTier, bonus);
    }

    public static String describe(Bonus bonus) {
        return String.format(
            StatCollector.translateToLocal("gtnl.waila.voltage_chance_bonus"),
            bonus.bonus(),
            StringUtils.voltageTooltipFormatted(bonus.tier()),
            StringUtils.voltageTooltipFormatted(bonus.baseTier()));
    }

    public static void writeWaila(NBTTagCompound tag, Bonus bonus) {
        NBTTagCompound chanceTag = new NBTTagCompound();
        chanceTag.setInteger("tier", bonus.tier());
        chanceTag.setInteger("baseTier", bonus.baseTier());
        chanceTag.setDouble("bonus", bonus.bonus());
        tag.setTag("outputChanceData", chanceTag);
    }

    public static Bonus readWaila(NBTTagCompound tag) {
        if (!tag.hasKey("outputChanceData")) return null;
        NBTTagCompound chanceTag = tag.getCompoundTag("outputChanceData");
        return new Bonus(chanceTag.getInteger("tier"), chanceTag.getInteger("baseTier"), chanceTag.getDouble("bonus"));
    }
}
