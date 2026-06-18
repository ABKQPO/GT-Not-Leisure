package com.science.gtnl.common.machine.multiblock;

import java.math.BigInteger;
import java.util.ArrayList;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.common.gui.modularui.FOGModuleGui;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.WirelessNetworkManager;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;

public class FOGAlloyBlastSmelterModule extends MTEBaseModule {

    private long EUt = 0;
    private int currentParallel = 0;
    private long wirelessEUt = 0;

    public FOGAlloyBlastSmelterModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FOGAlloyBlastSmelterModule(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FOGAlloyBlastSmelterModule(mName);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {

                if (recipe.mEUt > getProcessingVoltage()) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                wirelessEUt = (long) recipe.mEUt * getActualParallel();
                if (WirelessNetworkManager.getUserEU(userUUID)
                    .compareTo(BigInteger.valueOf(wirelessEUt * recipe.mDuration)) < 0) {
                    return CheckRecipeResultRegistry.insufficientPower(wirelessEUt * recipe.mDuration);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUt(getSafeProcessingVoltage())
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setHeatOC(true)
                    .setHeatDiscount(true)
                    .setMachineHeat(Math.max(recipe.mSpecialValue, getHeatForOC()))
                    .setHeatDiscountMultiplier(getHeatEnergyDiscount())
                    .setDurationDecreasePerOC(getOverclockTimeFactor());

            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                if (!WirelessNetworkManager.addEUToGlobalEnergyMap(userUUID, -calculatedEut * duration)) {
                    return CheckRecipeResultRegistry.insufficientPower(calculatedEut * duration);
                }
                addToPowerTally(
                    BigInteger.valueOf(calculatedEut)
                        .multiply(BigInteger.valueOf(duration)));
                addToRecipeTally(calculatedParallels);
                currentParallel = calculatedParallels;
                EUt = calculatedEut;
                overwriteCalculatedEut(0);
                setCurrentRecipeHeat(recipe.mSpecialValue);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(Long.MAX_VALUE);
        logic.setAvailableAmperage(Integer.MAX_VALUE);
        logic.setAmperageOC(false);
        logic.setUnlimitedTierSkips();
        logic.setMaxParallel(getActualParallel());
        logic.setSpeedBonus(getSpeedBonus());
        logic.setEuModifier(getEnergyDiscount());
        logic.enablePerfectOverclock();
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>();
        str.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.progress",
                EnumChatFormatting.GREEN + NumberFormatUtil.formatNumber(mProgresstime / 20) + EnumChatFormatting.RESET,
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(mMaxProgresstime / 20)
                    + EnumChatFormatting.RESET));
        str.add(
            StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.currently_using",
                EnumChatFormatting.RED + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(EUt) : "0")
                    + EnumChatFormatting.RESET));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.max_parallel",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getActualParallel())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "GT5U.infodata.parallel.current",
                EnumChatFormatting.RESET
                    + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(currentParallel) : "0")));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.recipe_time",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getSpeedBonus())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.energy",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getEnergyDiscount())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.divisor.recipe_time.non_perfect_oc",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getOverclockTimeFactor())));
        return str.toArray(new String[0]);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("FOGAlloyBlastSmelterModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGAlloyBlastSmelterModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGAlloyBlastSmelterModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGAlloyBlastSmelterModule_02"))
            .addSeparator(EnumChatFormatting.AQUA, 74)
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGAlloyBlastSmelterModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGAlloyBlastSmelterModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGAlloyBlastSmelterModule_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGAlloyBlastSmelterModule_06"))
            .beginStructureBlock(7, 7, 13, false)
            .addStructureInfo(
                EnumChatFormatting.GOLD + "20"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_00"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "20"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_01"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "5"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_02"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "5"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_03"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "1"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_04"))
            .toolTipFinisher(EnumChatFormatting.AQUA, 74);
        return tt;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new FOGModuleGui(this);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.alloyBlastSmelterRecipes;
    }

    @Override
    public double getSpeedBonus() {
        return processingSpeedBonus / 15;
    }

    @Override
    public double getEnergyDiscount() {
        return energyDiscount / 5;
    }

}
