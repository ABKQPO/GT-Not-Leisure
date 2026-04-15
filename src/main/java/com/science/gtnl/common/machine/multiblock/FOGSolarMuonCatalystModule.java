package com.science.gtnl.common.machine.multiblock;

import java.math.BigInteger;
import java.util.ArrayList;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.api.mixinHelper.IFOGModule;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.metadata.SolorMuonCatalystMetadata;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.misc.WirelessNetworkManager;
import lombok.Getter;
import lombok.Setter;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;
import tectech.thing.metaTileEntity.multi.godforge.MTEForgeOfGods;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;

public class FOGSolarMuonCatalystModule extends MTEBaseModule implements IFOGModule {

    private long EUt = 0;
    private int currentParallel = 0;
    @Getter
    @Setter
    public MTEForgeOfGods master;

    public FOGSolarMuonCatalystModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FOGSolarMuonCatalystModule(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FOGSolarMuonCatalystModule(mName);
    }

    long wirelessEUt = 0;

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {

                if (recipe.mEUt > getProcessingVoltage()) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                wirelessEUt = (long) recipe.mEUt * getActualParallel();
                if (WirelessNetworkManager.getUserEU(userUUID)
                    .compareTo(BigInteger.valueOf(wirelessEUt * recipe.mDuration)) < 0) {
                    return CheckRecipeResultRegistry.insufficientPower(wirelessEUt * recipe.mDuration);
                }

                if (recipe.getMetadataOrDefault(SolorMuonCatalystMetadata.INSTANCE, false) && !isAllUpgrade()) {
                    return SimpleCheckRecipeResult.ofFailure("not_enough_upgrade");
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUt(getSafeProcessingVoltage())
                    .setHeatDiscountMultiplier(getHeatEnergyDiscount())
                    .setDurationDecreasePerOC(getOverclockTimeFactor())
                    .enablePerfectOC();
            }

            @NotNull
            @Override
            protected CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
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
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    public boolean isAllUpgrade() {
        if (master == null) return false;
        for (ForgeOfGodsUpgrade upgrade : ForgeOfGodsUpgrade.VALUES) {
            if (!master.isUpgradeActive(upgrade)) {
                return false;
            }
        }
        return master.getRingAmount() >= 3;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
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
        return GTNLRecipeMaps.SolarMuonCatalystRecipes;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -10;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>();
        str.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.progress",
                EnumChatFormatting.GREEN + GTUtility.formatNumbers(mProgresstime / 20) + EnumChatFormatting.RESET,
                EnumChatFormatting.YELLOW + GTUtility.formatNumbers(mMaxProgresstime / 20) + EnumChatFormatting.RESET));
        str.add(
            StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.currently_using",
                EnumChatFormatting.RED + (getBaseMetaTileEntity().isActive() ? GTUtility.formatNumbers(EUt) : "0")
                    + EnumChatFormatting.RESET));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.max_parallel",
                EnumChatFormatting.RESET + GTUtility.formatNumbers(getActualParallel())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "GT5U.infodata.parallel.current",
                EnumChatFormatting.RESET
                    + (getBaseMetaTileEntity().isActive() ? GTUtility.formatNumbers(currentParallel) : "0")));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.recipe_time",
                EnumChatFormatting.RESET + GTUtility.formatNumbers(getSpeedBonus())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.energy",
                EnumChatFormatting.RESET + GTUtility.formatNumbers(getEnergyDiscount())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.divisor.recipe_time.non_perfect_oc",
                EnumChatFormatting.RESET + GTUtility.formatNumbers(getOverclockTimeFactor())));
        return str.toArray(new String[0]);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("FOGSolarMuonCatalystModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGSolarMuonCatalystModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGSolarMuonCatalystModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGSolarMuonCatalystModule_02"))
            .addSeparator(EnumChatFormatting.AQUA, 74)
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGSolarMuonCatalystModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGSolarMuonCatalystModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGSolarMuonCatalystModule_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGSolarMuonCatalystModule_06"))
            .beginStructureBlock(7, 7, 13, false)
            .addStructureInfo(
                EnumChatFormatting.GOLD + "20"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGMachine_Casing_00"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "20"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGMachine_Casing_01"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "5"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGMachine_Casing_02"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "5"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGMachine_Casing_03"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "1"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGMachine_Casing_04"))
            .toolTipFinisher(EnumChatFormatting.AQUA, 74);
        return tt;
    }

}
