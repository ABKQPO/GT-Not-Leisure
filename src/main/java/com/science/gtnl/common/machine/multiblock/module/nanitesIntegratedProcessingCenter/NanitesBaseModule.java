package com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.recipes.data.NanitesIntegratedProcessingRecipesData;
import com.science.gtnl.utils.recipes.metadata.NanitesIntegratedProcessingMetadata;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;

public abstract class NanitesBaseModule<T extends NanitesBaseModule<T>> extends WirelessEnergyMultiMachineBase<T> {

    public boolean isConnected = false;
    public boolean isOreModule = false;
    public boolean isBioModule = false;
    public boolean isPolModule = false;
    public double setEUtDiscount = 0;
    public double setDurationModifier = 0;
    public int setMaxParallel = 0;
    public static final int HORIZONTAL_OFF_SET = 7;
    public static final int VERTICAL_OFF_SET = 17;
    public static final int DEPTH_OFF_SET = 0;
    public int mHeatingCapacity = 0;

    public NanitesBaseModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public NanitesBaseModule(String aName) {
        super(aName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("NanitesIntegratedProcessingCenterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(15, 18, 31, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && isConnected) {
            super.onPostTick(aBaseMetaTileEntity, aTick);
            if (mEfficiency < 0) mEfficiency = 0;
        }
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.NanitesIntegratedProcessingRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(mHeatingCapacity)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(true);
            }

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (wirelessMode && recipe.mEUt > GTValues.V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                NanitesIntegratedProcessingRecipesData data = recipe.getMetadataOrDefault(
                    NanitesIntegratedProcessingMetadata.INSTANCE,
                    new NanitesIntegratedProcessingRecipesData(false, false, false));

                if (data.bioengineeringModule && !isBioModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_bio_module");
                }
                if (data.oreExtractionModule && !isOreModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_ore_module");
                }
                if (data.polymerTwistingModule && !isPolModule) {
                    return SimpleCheckRecipeResult.ofFailure("missing_pol_module");
                }

                return recipe.mSpecialValue <= mHeatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    public void connect() {
        isConnected = true;
    }

    public void disconnect() {
        isConnected = false;
    }

    @Override
    public double getEUtDiscount() {
        return setEUtDiscount;
    }

    public void setEUtDiscount(double discount) {
        setEUtDiscount = discount;
    }

    @Override
    public double getDurationModifier() {
        return setDurationModifier;
    }

    public void setDurationModifier(double boost) {
        setDurationModifier = boost;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Math.max(super.getMaxParallelRecipes(), setMaxParallel);
    }

    public void setMaxParallel(int parallel) {
        setMaxParallel = parallel;
    }

    public int getHeatingCapacity() {
        return mHeatingCapacity;
    }

    public void setHeatingCapacity(int HeatingCapacity) {
        mHeatingCapacity = HeatingCapacity;
    }

}
