package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

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

public class SteamOilDrillModule extends SteamElevatorModule {

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

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamOilDrillModuleRecipeType");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamOilDrillModuleRecipeType"));
        switch (mTier) {
            case 2 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamOilDrillModuleI_00"));
            case 3 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamOilDrillModuleII_00"));
            case 4 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamOilDrillModuleIII_00"));
        }
        tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamOilDrillModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamOilDrillModule_01"))
            .addInfo(StatCollector.translateToLocalFormatted("Tooltip_SteamOilDrillModule_02", mTier - 1))
            .addInfo(
                StatCollector.translateToLocalFormatted(
                    "Tooltip_SteamOilDrillModule_03",
                    (int) (500d * Math.pow(2, mTier - 2)),
                    (int) (2500d * Math.pow(2, mTier - 2))))
            .addInfo(StatCollector.translateToLocalFormatted("Tooltip_SteamOilDrillModule_04", 1200 / (mTier - 1)))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        int dimensionId = getBaseMetaTileEntity().getWorld().provider.dimensionId;
        long worldDay = getBaseMetaTileEntity().getWorld()
            .getWorldTime() / 24000;
        GTUODimension dimension = GTMod.proxy.mUndergroundOil.GetDimension(dimensionId);
        if (dimension == null) return CheckRecipeResultRegistry.NO_RECIPE;
        ArrayList<FluidStack> fluidStack = new ArrayList<>();
        XSTR tVeinRNG = new XSTR(System.nanoTime());
        XSTR tAmountRNG = new XSTR(worldDay);

        for (int i = 0; i < mTier - 1; i++) {
            GTUOFluid uoFluid = dimension.getRandomFluid(tVeinRNG);

            if (uoFluid == null || uoFluid.getFluid() == null) {
                continue;
            }

            int amount = (int) (500d * Math.pow(2, mTier - 2) * (1 + tAmountRNG.nextInt(4)));
            fluidStack.add(new FluidStack(uoFluid.getFluid(), amount));
        }

        this.mOutputFluids = fluidStack.toArray(new FluidStack[0]);
        this.lEUt = GTValues.V[mTier + 1];
        this.mMaxProgresstime = 1200 / (mTier - 1);

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }
}
