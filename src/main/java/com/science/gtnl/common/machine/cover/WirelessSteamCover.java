package com.science.gtnl.common.machine.cover;

import java.math.BigInteger;

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
            SteamTypes steamType = getSteamMode();
            if (fluid != null && !GTUtility.areFluidsEqual(fluid, new FluidStack(steamType.fluid, 1))) {
                return;
            }
            int capacity = commonMetaTile.getCapacity();
            int fluidAmount = fluid != null ? commonMetaTile.getFluidAmount() : 0;
            BigInteger availableSteam = SteamWirelessNetworkManager.getUserSteam(Utils.getOwner(tileEntity));
            int current = availableSteam.divide(BigInteger.valueOf(steamType.efficiencyFactor))
                .min(BigInteger.valueOf(capacity - fluidAmount))
                .intValue();

            if (current <= 0) return;

            long steamCost = (long) current * steamType.efficiencyFactor;
            if (!SteamWirelessNetworkManager.addSteamToGlobalSteamMap(Utils.getOwner(tileEntity), -steamCost)) return;
            commonMetaTile.fill(new FluidStack(steamType.fluid, current), true);
        }
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocal("gtnl.cover.pipeless_steam.name");
    }

    public SteamTypes getSteamMode() {
        return SteamTypes.fromNetworkTypeId(coverData);
    }

    public void setSteamMode(SteamTypes type) {
        if (type == null || !type.networkConvertible) return;
        coverData = type.ordinal();
    }

    @Override
    public void onCoverScrewdriverClick(EntityPlayer aPlayer, float aX, float aY, float aZ) {
        coverData = (getSteamMode().ordinal() + (aPlayer.isSneaking() ? -1 : 1))
            % SteamTypes.NETWORK_CONVERTIBLE_TYPES.length;
        if (coverData < 0) {
            coverData = SteamTypes.NETWORK_CONVERTIBLE_TYPES.length - 1;
        }

        GTUtility.sendChatTrans(aPlayer, "gtnl.gui.wireless_steam.switch_to", getSteamMode().fluid.getLocalizedName());
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
