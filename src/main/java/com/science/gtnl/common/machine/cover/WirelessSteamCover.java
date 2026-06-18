package com.science.gtnl.common.machine.cover;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.science.gtnl.common.gui.WirelessSteamCoverGui;
import com.science.gtnl.common.gui.WirelessSteamCoverUIFactory;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.enums.SteamTypes;
import com.science.gtnl.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.api.covers.CoverContext;
import gregtech.api.gui.modularui.CoverUIBuildContext;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IMachineProgress;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.CommonMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.common.covers.CoverLegacyData;
import gregtech.common.gui.modularui.cover.base.CoverBaseGui;

public class WirelessSteamCover extends CoverLegacyData {

    public WirelessSteamCover(CoverContext context) {
        super(context);
    }

    @Override
    public boolean allowsCopyPasteTool() {
        return false;
    }

    @Override
    public boolean allowsTickRateAddition() {
        return false;
    }

    @Override
    public void doCoverThings(byte aInputRedstone, long aTimer) {
        if (aInputRedstone == 0 && aTimer % 100 == 0) {
            ICoverable coverable = coveredTile.get();
            if (coverable instanceof IMachineProgress machineProgress && machineProgress.isAllowedToWork()) {
                tryFetchingSteam(machineProgress);
            }
        }
    }

    public void tryFetchingSteam(IMachineProgress tileEntity) {
        if (tileEntity instanceof BaseMetaTileEntity baseTile
            && baseTile.getMetaTileEntity() instanceof CommonMetaTileEntity commonMetaTile) {
            FluidStack fluid = commonMetaTile.getFluid();
            if (fluid != null
                && !GTUtility.areFluidsEqual(fluid, new FluidStack(SteamTypes.values()[coverData].fluid, 1))) {
                return;
            }
            int capacity = commonMetaTile.getCapacity();
            int fluidAmount = fluid != null ? commonMetaTile.getFluidAmount() : 0;
            int availableSteam = SteamWirelessNetworkManager.getUserSteamInt(Utils.getOwner(tileEntity));
            int current = Math.max(0, Math.min(availableSteam, capacity - fluidAmount));

            if (!SteamWirelessNetworkManager.addSteamToGlobalSteamMap(Utils.getOwner(tileEntity), -current)) return;
            commonMetaTile.fill(new FluidStack(getSteamMode().fluid, current), true);
        }
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocal("PipelessSteamCover");
    }

    public SteamTypes getSteamMode() {
        return SteamTypes.values()[coverData];
    }

    public void setSteamMode(SteamTypes type) {
        coverData = type.ordinal();
    }

    @Override
    public void onCoverScrewdriverClick(EntityPlayer aPlayer, float aX, float aY, float aZ) {
        coverData = (coverData + (aPlayer.isSneaking() ? -1 : 1)) % SteamTypes.values().length;
        if (coverData < 0) {
            coverData = SteamTypes.values().length - 1;
        }

        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocalFormatted(
                "Info_PipelessSteamCover_00",
                SteamTypes.values()[coverData].fluid.getLocalizedName()));
    }

    @Override
    public boolean hasCoverGUI() {
        return true;
    }

    @Override
    protected @NotNull CoverBaseGui<?> getCoverGui() {
        return new WirelessSteamCoverGui(this);
    }

    @Override
    @Deprecated
    public ModularWindow createWindow(CoverUIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after the mui2 WirelessSteamCoverGui is validated in all cover opening paths.
        return new WirelessSteamCoverUIFactory(buildContext).createWindow();
    }

    @Override
    public boolean alwaysLookConnected() {
        return true;
    }

    @Override
    public int getMinimumTickRate() {
        return 20;
    }
}
