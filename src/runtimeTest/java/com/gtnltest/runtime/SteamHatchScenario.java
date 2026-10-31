package com.gtnltest.runtime;

import static com.gtnltest.runtime.RuntimeReport.require;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.utils.enums.GTNLMachineID;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

final class SteamHatchScenario implements RuntimeScenario {

    private IGregTechTileEntity hatch;
    private boolean reloaded;

    public String id() {
        return "steam.hatch.simulation-payment-reload";
    }

    public void start(RuntimeFixture fixture) {
        hatch = fixture.machine(GTNLMachineID.BIG_STEAM_INPUT_HATCH.ID, 0);
    }

    public boolean tick(RuntimeFixture fixture, int ticks) {
        if (hatch.getTimer() <= 5) return false;
        if (!reloaded) {
            FluidStack steam = com.science.gtnl.utils.enums.SteamTypes.STEAM.fluid.getFluidStack(3000);
            require(steam != null, "steam must be registered by the real mod lifecycle");
            require(hatch.fill(ForgeDirection.UNKNOWN, steam, false) == 3000, "simulation must quote accepted steam");
            FluidStack empty = hatch.drain(ForgeDirection.UNKNOWN, 3000, false);
            require(empty == null || empty.amount == 0, "simulated fill must not create steam");
            require(
                hatch.fill(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.WATER, 1000), true) == 0,
                "steam hatch must reject water");
            require(
                hatch.fill(ForgeDirection.UNKNOWN, steam, true) == 3000,
                "committed fill must accept the quoted amount");
            require(
                hatch.drain(ForgeDirection.UNKNOWN, 1000, false).amount == 1000,
                "simulated drain must quote exact payment");
            require(
                hatch.drain(ForgeDirection.UNKNOWN, 4000, false).amount == 3000,
                "simulated drain must preserve balance");
            require(hatch.drain(ForgeDirection.UNKNOWN, 1000, true).amount == 1000, "committed drain pays once");
            hatch = fixture.reload(hatch);
            reloaded = true;
            return false;
        }
        require(
            hatch.drain(ForgeDirection.UNKNOWN, 4000, false).amount == 2000,
            "remaining steam survives a real tile NBT reload");
        require(
            hatch.drain(ForgeDirection.UNKNOWN, 4000, true).amount == 2000,
            "overlarge request drains only the remaining balance");
        FluidStack drained = hatch.drain(ForgeDirection.UNKNOWN, 1, true);
        require(drained == null || drained.amount == 0, "an empty hatch cannot pay twice");
        return true;
    }
}
