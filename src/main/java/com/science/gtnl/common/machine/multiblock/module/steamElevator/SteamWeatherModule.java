package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import javax.annotation.Nonnull;

import net.minecraft.util.StatCollector;

import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.mixins.late.Gregtech.AccessorProcessingLogic;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

public class SteamWeatherModule extends SteamElevatorModule {

    public SteamWeatherModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 1);
    }

    public SteamWeatherModule(String aName) {
        super(aName, 1);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamWeatherModule(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamWeatherModuleRecipeType");
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return null;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamWeatherModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamWeatherModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamWeatherModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamWeatherModule_02"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.SteamWeatherModuleFakeRecipes;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        // If no logic is found, try legacy checkRecipe
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        // inputs are consumed at this point
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        mOutputItems = processingLogic.getOutputItems();
        mOutputFluids = processingLogic.getOutputFluids();

        int specialValue = ((AccessorProcessingLogic) processingLogic).getLastRecipe().mSpecialValue;

        switch (specialValue) {
            case 1:
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setRaining(false);
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setThundering(false);
                break;
            case 2:
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setRaining(true);
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setRainTime(72000);
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setThundering(false);
                break;
            case 3:
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setRaining(true);
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setRainTime(72000);
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setThundering(true);
                getBaseMetaTileEntity().getWorld()
                    .getWorldInfo()
                    .setThunderTime(72000);
                break;
        }

        return result;
    }
}
