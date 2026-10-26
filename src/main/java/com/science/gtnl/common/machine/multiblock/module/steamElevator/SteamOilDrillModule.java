package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;

import gregtech.GTMod;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GTUODimension;
import gregtech.api.objects.GTUOFluid;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;

@IMetaTileEntity.SkipGenerateDescription
public class SteamOilDrillModule extends SteamElevatorModuleBase {

    public static XSTR tVeinRNG = new XSTR(System.nanoTime());
    private static final int BASE_FLUID_AMOUNT = 250;
    private static final int MAX_AMOUNT_ROLL = 4;
    private static final int BASE_CYCLE_TICKS = 1200;
    public static final String[] TIER_TITLES = { "basic", "advanced", "elite" };

    public SteamOilDrillModule(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public SteamOilDrillModule(String aName, int aTier) {
        super(aName, aTier);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamOilDrillModule(this.mName, this.mTier);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        int dimensionId = getBaseMetaTileEntity().getWorld().provider.dimensionId;
        GTUODimension dimension = GTMod.proxy.mUndergroundOil.GetDimension(dimensionId);
        if (dimension == null) return CheckRecipeResultRegistry.NO_RECIPE;
        ArrayList<FluidStack> fluidStack = new ArrayList<>();

        for (int i = 0; i < mTier - 1; i++) {
            GTUOFluid uoFluid = dimension.getRandomFluid(tVeinRNG);

            if (uoFluid == null || uoFluid.getFluid() == null) {
                continue;
            }

            int amount = fluidAmountBase() * (1 + tVeinRNG.nextInt(MAX_AMOUNT_ROLL));
            fluidStack.add(new FluidStack(uoFluid.getFluid(), amount));
        }

        this.mOutputFluids = fluidStack.toArray(new FluidStack[0]);
        this.lEUt = GTValues.VP[mTier];
        this.mMaxProgresstime = cycleTicks();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    private int fluidAmountBase() {
        return BASE_FLUID_AMOUNT * (1 << Math.max(0, mTier - 2));
    }

    private int cycleTicks() {
        return BASE_CYCLE_TICKS / (mTier - 1);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.recipe_type");
    }

    @Override
    public TooltipStats getTooltipStats() {
        return null;
    }

    @Override
    public Map<String, Object> getTooltipVars() {
        return ImmutableMap.of(
            "tier_title",
            TIER_TITLES[Math.min(Math.max(mTier - 2, 0), TIER_TITLES.length - 1)],
            "max_fluids",
            mTier - 1,
            "min_amount",
            fluidAmountBase(),
            "max_amount",
            fluidAmountBase() * MAX_AMOUNT_ROLL,
            "cycle",
            cycleTicks());
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }
}
