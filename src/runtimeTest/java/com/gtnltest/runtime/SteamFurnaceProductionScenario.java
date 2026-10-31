package com.gtnltest.runtime;

import static com.gtnltest.runtime.RuntimeReport.require;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.common.machine.multiblock.steam.LargeSteamFurnace;
import com.science.gtnl.utils.enums.GTNLMachineID;
import com.science.gtnl.utils.enums.SteamTypes;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/** Builds the real StructureLib definition; never forces formation, recipe progress or output. */
final class SteamFurnaceProductionScenario implements RuntimeScenario {

    private static final int AMOUNT = 16, STEAM = 500_000;
    private IGregTechTileEntity controller, input, output, steam;
    private LargeSteamFurnace furnace;
    private int[] brokenCasing;
    private int stage, stageStarted, steamAfterProduction;

    public String id() {
        return "production.large_steam_furnace.structure-and-smelting";
    }

    public String scope() {
        return "native-structure-input-steam-output";
    }

    public int timeoutTicks() {
        return 900;
    }

    public void start(RuntimeFixture fixture) {
        int x = RuntimeFixture.X + 32, y = RuntimeFixture.Y, z = RuntimeFixture.Z;
        controller = fixture.machineAt(GTNLMachineID.LARGE_STEAM_FURNACE.ID, x, y, z);
        controller.disableWorking();
        furnace = (LargeSteamFurnace) controller.getMetaTileEntity();
        try {
            furnace.construct(new ItemStack(Items.stick, 1), false);
        } finally {
            fixture.trackStructure(x, y, z, 12);
        }
        List<int[]> casings = new ObjectArrayList<>();
        for (int dx = -12; dx <= 12; dx++) for (int dy = -12; dy <= 12; dy++) for (int dz = -12; dz <= 12; dz++)
            if (fixture.world.getBlock(x + dx, y + dy, z + dz) == GregTechAPI.sBlockCasings1
                && fixture.world.getBlockMetadata(x + dx, y + dy, z + dz) == 10)
                casings.add(new int[] { x + dx, y + dy, z + dz });
        require(
            casings.size() >= 32,
            "native construction did not place the required bronze casings: " + casings.size());
        input = hatch(fixture, GTNLMachineID.BREEL_STEAM_INPUT_BUS.ID, casings.get(0));
        output = hatch(fixture, GTNLMachineID.BREEL_STEAM_OUTPUT_BUS.ID, casings.get(1));
        steam = hatch(fixture, GTNLMachineID.BIG_STEAM_INPUT_HATCH.ID, casings.get(2));
        brokenCasing = casings.get(3);
    }

    private IGregTechTileEntity hatch(RuntimeFixture fixture, int id, int[] position) {
        return fixture.machineAt(id, position[0], position[1], position[2]);
    }

    public boolean tick(RuntimeFixture fixture, int ticks) {
        if (controller.getTimer() <= 5 || steam.getTimer() <= 5) return false;
        if (stage == 0) {
            require(
                furnace.checkStructure(true, controller),
                "native bronze furnace must form with actual steam ports; input=" + furnace.mSteamInputs
                    .size() + " output=" + furnace.mSteamOutputs.size() + " tier=" + furnace.tierMachine);
            fixture.world.setBlockToAir(brokenCasing[0], brokenCasing[1], brokenCasing[2]);
            require(!furnace.checkStructure(true, controller), "missing casing must invalidate the structure");
            input.setInventorySlotContents(0, new ItemStack(Blocks.cobblestone, AMOUNT));
            require(
                steam.fill(ForgeDirection.UNKNOWN, SteamTypes.STEAM.fluid.getFluidStack(STEAM), true) == STEAM,
                "native steam hatch must receive the production budget before testing the broken structure");
            controller.enableWorking();
            stage = 1;
            stageStarted = ticks;
        } else if (stage == 1 && ticks - stageStarted >= 30) {
            require(
                count(input, Blocks.cobblestone) == AMOUNT && count(output, Blocks.stone) == 0,
                "an incomplete structure must not consume input or create output");
            require(remainingSteam() == STEAM, "an incomplete structure must not consume its available steam");
            fixture.world
                .setBlock(brokenCasing[0], brokenCasing[1], brokenCasing[2], GregTechAPI.sBlockCasings1, 10, 3);
            require(furnace.checkStructure(true, controller), "restoring the casing must restore formation");
            stage = 2;
            stageStarted = ticks;
        } else if (stage == 2) {
            int produced = count(output, Blocks.stone);
            require(produced <= AMOUNT, "smelting duplicated output: " + produced);
            if (produced == AMOUNT) {
                require(count(input, Blocks.cobblestone) == 0, "completed output must consume the corresponding input");
                steamAfterProduction = remainingSteam();
                require(
                    steamAfterProduction > 0 && steamAfterProduction < STEAM,
                    "native production must consume steam from its hatch");
                stage = 3;
                stageStarted = ticks;
            } else require(
                ticks - stageStarted < 700,
                "formed furnace did not smelt: input=" + count(input, Blocks.cobblestone)
                    + " output="
                    + produced
                    + " steam="
                    + remainingSteam()
                    + " voltage="
                    + furnace.getMaxInputVoltage());
        } else if (stage == 3 && ticks - stageStarted >= 40) {
            require(
                count(output, Blocks.stone) == AMOUNT && count(input, Blocks.cobblestone) == 0,
                "completed recipe must not replay or duplicate output while idle");
            require(
                remainingSteam() == steamAfterProduction,
                "idle furnace must not keep paying for a completed recipe");
            return true;
        }
        return false;
    }

    private int count(IGregTechTileEntity tile, net.minecraft.block.Block block) {
        ItemStack expected = new ItemStack(block);
        int count = 0;
        for (int slot = 0; slot < tile.getSizeInventory(); slot++) {
            ItemStack stack = tile.getStackInSlot(slot);
            if (stack != null && stack.isItemEqual(expected)) count += stack.stackSize;
        }
        return count;
    }

    private int remainingSteam() {
        FluidStack remaining = steam.drain(ForgeDirection.UNKNOWN, Integer.MAX_VALUE, false);
        return remaining == null ? 0 : remaining.amount;
    }

    public String evidence() {
        return "Native structure formed, missing casing blocked input consumption, repaired structure smelted " + AMOUNT
            + " cobblestone into stone; steam paid="
            + (STEAM - steamAfterProduction)
            + "; another 40 idle ticks preserved output and steam";
    }
}
