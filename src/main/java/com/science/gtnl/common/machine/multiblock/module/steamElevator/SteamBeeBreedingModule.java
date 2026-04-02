package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static forestry.api.apiculture.BeeManager.beeRoot;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.plugins.PluginApiculture;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

public class SteamBeeBreedingModule extends SteamElevatorModule {

    public SteamBeeBreedingModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 8);
    }

    public SteamBeeBreedingModule(String aName) {
        super(aName, 8);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamBeeBreedingModule(this.mName);
    }

    @Override
    public int getTierRecipes() {
        return 8;
    }

    @Override
    public String getMachineType() {
        return "SteamBeeBreedingModuleRecipeType";
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamBeeBreedingModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeeBreedingModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeeBreedingModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeeBreedingModule_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeeBreedingModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeeBreedingModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamBeeBreedingModule_05"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        ItemStack itemStack = getControllerSlot();

        if (beeRoot.getType(itemStack) != EnumBeeType.QUEEN
            || !depleteInput(PluginApiculture.items.royalJelly.getItemStack(64))
            || !depleteInput(PluginApiculture.items.royalJelly.getItemStack(64))) {
            this.updateSlots();
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        this.lEUt = -GTValues.V[6];
        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;
        this.mMaxProgresstime = 12000;
        this.mOutputItems = new ItemStack[] { createIgnobleCopy(itemStack) };
        this.updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public ItemStack createIgnobleCopy(ItemStack queenStack) {
        IBee princess = beeRoot.getMember(queenStack);
        princess.setIsNatural(false);
        return beeRoot.getMemberStack(princess, EnumBeeType.PRINCESS.ordinal());
    }

    @Override
    public int getMachineEffectRange() {
        return 0;
    }
}
