package com.science.gtnl.common.machine.multiblock;

import java.math.BigInteger;
import java.util.ArrayList;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.common.gui.modularui.FOGModuleGui;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.WirelessNetworkManager;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;

@IMetaTileEntity.SkipGenerateDescription
public class FOGAlloySmelterModule extends MTEBaseModule {

    public long EUt = 0;
    private int currentParallel = 0;
    private long wirelessEUt = 0;

    public FOGAlloySmelterModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FOGAlloySmelterModule(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FOGAlloySmelterModule(mName);
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
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.alloySmelterRecipes;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>();
        str.add(
            IGregTechDeviceInformation.encode(
                "GT5U.infodata.progress",
                EnumChatFormatting.GREEN + NumberFormatUtil.formatNumber(mProgresstime / 20) + EnumChatFormatting.RESET,
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(mMaxProgresstime / 20)
                    + EnumChatFormatting.RESET));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.currently_using",
                EnumChatFormatting.RED + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(EUt) : "0")
                    + EnumChatFormatting.RESET));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.max_parallel",
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(getActualParallel())));
        str.add(
            IGregTechDeviceInformation.encode(
                "GT5U.infodata.parallel.current",
                EnumChatFormatting.YELLOW
                    + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(currentParallel) : "0")));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.multiplier.recipe_time",
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(getSpeedBonus())));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.multiplier.energy",
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(getEnergyDiscount())));
        str.add(
            IGregTechDeviceInformation.encode(
                "tt.infodata.multi.divisor.recipe_time.non_perfect_oc",
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(getOverclockTimeFactor())));
        return str.toArray(new String[0]);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.fog_alloy_smelter_module.recipe_type"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.godforge.alloy_smelter_module.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.godforge.alloy_smelter_module.tooltip.1"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.godforge.alloy_smelter_module.tooltip.2"))
            .addSeparator(EnumChatFormatting.AQUA, 74)
            .addInfo(StatCollector.translateToLocal("gtnl.machine.godforge.alloy_smelter_module.tooltip.3"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.godforge.alloy_smelter_module.tooltip.4"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.godforge.alloy_smelter_module.tooltip.5"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.godforge.alloy_smelter_module.tooltip.6"))
            .beginStructureBlock(7, 7, 13, false)
            .addStructureInfo(
                EnumChatFormatting.GOLD + "20"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("gtnl.machine.godforge.module.casing.0"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "20"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("gtnl.machine.godforge.module.casing.1"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "5"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("gtnl.machine.godforge.module.casing.2"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "5"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("gtnl.machine.godforge.module.casing.3"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "1"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("gtnl.machine.godforge.module.casing.4"))
            .toolTipFinisher(EnumChatFormatting.AQUA, 74);
        return tt;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new FOGModuleGui(this);
    }

    @Override
    public double getSpeedBonus() {
        return processingSpeedBonus / 2;
    }

}
