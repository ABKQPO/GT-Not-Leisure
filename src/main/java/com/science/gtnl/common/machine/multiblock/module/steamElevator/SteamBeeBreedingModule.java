package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static forestry.api.apiculture.BeeManager.beeRoot;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.Map;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.plugins.PluginApiculture;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
public class SteamBeeBreedingModule extends SteamElevatorModuleBase {

    private static final int RUN_TICKS = 12000;

    public SteamBeeBreedingModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 8);
    }

    public SteamBeeBreedingModule(String aName) {
        super(aName, 8);
    }

    @Override
    public int getTierRecipes() {
        return 8;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamBeeBreedingModule(this.mName);
    }

    @Override
    public String getMachineType() {
        return "gtnl.machine.steam_bee_breeding_module.recipe_type";
    }

    @Override
    public TooltipStats getTooltipStats() {
        // Why: this machine runs its own process instead of recipes, so recipe numbers would mislead.
        return null;
    }

    @Override
    public Map<String, Object> getTooltipVars() {
        return ImmutableMap.of("run_seconds", RUN_TICKS / SECONDS);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
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
        this.mMaxProgresstime = RUN_TICKS;
        this.mOutputItems = new ItemStack[] { createIgnobleCopy(itemStack) };
        this.updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public ItemStack createIgnobleCopy(ItemStack queenStack) {
        IBee princess = beeRoot.getMember(queenStack);
        princess.setIsNatural(false);
        return beeRoot.getMemberStack(princess, EnumBeeType.PRINCESS.ordinal());
    }
}
