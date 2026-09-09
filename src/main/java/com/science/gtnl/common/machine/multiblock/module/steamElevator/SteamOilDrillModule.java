package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import java.util.ArrayList;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

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

            int base = 250 * (1 << Math.max(0, mTier - 2));
            int amount = base * (1 + tVeinRNG.nextInt(4));
            fluidStack.add(new FluidStack(uoFluid.getFluid(), amount));
        }

        this.mOutputFluids = fluidStack.toArray(new FluidStack[0]);
        this.lEUt = GTValues.VP[mTier];
        this.mMaxProgresstime = 1200 / (mTier - 1);

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.recipe_type");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.recipe_type"));
        switch (mTier) {
            case 2 -> tt.addInfo(StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.tooltip.basic"));
            case 3 -> tt
                .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.tooltip.advanced"));
            case 4 -> tt.addInfo(StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.tooltip.elite"));
        }
        tt.addInfo(StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.tooltip.0"))
            .addInfo(StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.tooltip.1"))
            .addInfo(
                StatCollector.translateToLocalFormatted("gtnl.machine.steam_oil_drill_module.tooltip.2", mTier - 1))
            .addInfo(
                StatCollector.translateToLocalFormatted(
                    "gtnl.machine.steam_oil_drill_module.tooltip.3",
                    250 * (1 << Math.max(0, mTier - 2)),
                    1000 * (1 << Math.max(0, mTier - 2))))
            .addInfo(
                StatCollector
                    .translateToLocalFormatted("gtnl.machine.steam_oil_drill_module.tooltip.4", 1200 / (mTier - 1)))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }
}
