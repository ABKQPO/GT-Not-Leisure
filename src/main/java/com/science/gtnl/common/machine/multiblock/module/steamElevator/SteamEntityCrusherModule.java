package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class SteamEntityCrusherModule extends SteamElevatorModule {

    public static Random random = new Random();

    public SteamEntityCrusherModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 1);
    }

    public SteamEntityCrusherModule(String aName) {
        super(aName, 1);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamEntityCrusherModule(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamEntityCrusherModuleRecipeType");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamEntityCrusherModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEntityCrusherModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEntityCrusherModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEntityCrusherModule_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEntityCrusherModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEntityCrusherModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEntityCrusherModule_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEntityCrusherModule_06"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getMachineEffectRange() {
        return 0;
    }

    @Override
    public double getEUtDiscount() {
        return 0.5 * (1 << (2 * Math.min(4, recipeOcCount)));
    }

    @Override
    public double getDurationModifier() {
        return 2 / Math.pow(2, Math.min(4, recipeOcCount));
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public int clampRecipeOcCount(int value) {
        return 0;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.ExtremeExtremeEntityCrusherRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 5;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        ItemStack[] outputItems = processingLogic.getOutputItems();
        ItemStack inputItem = ((GTNL_ProcessingLogic) processingLogic).getInputItems()[0];

        double multiplier = 2.0;

        for (ItemStack itemStack : getAllStoredInputs()) {
            if (GTUtility.areStacksEqual(inputItem, itemStack)) {
                multiplier += 0.5 * itemStack.stackSize;
                if (multiplier >= 34.0) {
                    multiplier = 34.0;
                    break;
                }
            }
        }

        if (GTUtility.areStacksEqual(inputItem, getControllerSlot())) {
            multiplier += 0.5 * getControllerSlot().stackSize;
            if (multiplier >= 34.0) {
                multiplier = 34.0;
            }
        }

        if (outputItems != null) {
            for (ItemStack itemStack : outputItems) {
                if (itemStack != null) {
                    if (random.nextDouble() * 100 < multiplier) {
                        itemStack.stackSize *= 2;
                    }
                }
            }
        }

        mOutputItems = outputItems;
        mOutputFluids = processingLogic.getOutputFluids();

        return result;
    }

}
