package com.science.gtnl.common.machine;

import static com.gtnltest.TestSupport.check;

import java.math.BigInteger;
import java.util.UUID;
import java.util.stream.Stream;

import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import com.science.gtnl.common.machine.purification.PurificationUnitState;
import com.science.gtnl.common.machine.voidminer.VoidMinerState;

@Tag("machine")
public final class MachineStateTest {

    @TestFactory
    Stream<DynamicTest> scenarios() {
        return Stream.of(
            DynamicTest.dynamicTest("voidMinerTransitions", MachineStateTest::voidMinerTransitions),
            DynamicTest.dynamicTest("purificationTransitions", MachineStateTest::purificationTransitions));
    }

    private static void voidMinerTransitions() {
        VoidMinerState state = new VoidMinerState();
        check(!state.hasOverride(), "a new miner has no dimension override");
        check(!state.switchDropMapDimension("Overworld"), "initial drop-map selection does not invalidate a filter");
        check(state.getDimensionChangeVersion() == 0, "initial selection preserves the old version semantics");
        check(!state.switchDropMapDimension("Overworld"), "reselecting a dimension is not a change");
        check(state.switchDropMapDimension("Moon"), "switching an existing map invalidates its filter");
        check(state.getDimensionChangeVersion() == 1, "one switch creates one invalidation");
        check(state.clearDropMapDimension(), "clearing an active map invalidates its filter");
        check(!state.clearDropMapDimension() && state.getDimensionChangeVersion() == 2, "repeated clear is idempotent");

        // These are the keys already present in released saves, independent of the current writer.
        NBTTagCompound legacy = new NBTTagCompound();
        legacy.setString("mLastDimensionOverride", "Moon");
        legacy.setString("vmTweak$activeDropMapDimension", "Moon");
        state.readDimensionFromNBT(legacy);
        check(
            state.isOverride("Moon") && state.getActiveDropMapDimension()
                .equals("Moon"),
            "restore both dimensions");
        check(state.getDimensionChangeVersion() == 2, "loading saved dimensions is not a live filter invalidation");
        NBTTagCompound saved = new NBTTagCompound();
        state.writeDimensionToNBT(saved);
        check(legacy.equals(saved), "dimension save keys and values remain backward compatible");
        state.setLastDimensionOverride("");
        check(!state.hasOverride(), "empty overrides are normalized");
        state.setLastDimensionOverride(null);
        check(!state.hasOverride(), "missing overrides are normalized");
        state.warn("vmtweak.gui.override.error");
        state.clearWarning();
        check(
            state.getWarning()
                .isEmpty(),
            "a successful recalculation clears the previous warning");
    }

    private static void purificationTransitions() {
        PurificationUnitState state = new PurificationUnitState();
        state.setMaxParallel(0);
        check(state.getMaxParallel() == 1, "configured parallelism has a lower bound of one");
        state.setMaxParallel(8);
        check(state.limitParallel(20) == 8, "available inputs are capped by configured parallelism");
        check(state.limitParallel(3) == 3, "input shortage lowers effective parallelism");
        check(state.limitParallel(0) == 0, "zero available inputs remains a valid no-work result");
        state.resetParallel();
        check(state.getEffectiveParallel() == 1 && state.getMaxParallel() == 8, "reset preserves configuration");
        BigInteger hugeCost = BigInteger.valueOf(Long.MAX_VALUE)
            .multiply(BigInteger.TEN);
        state.addCost(hugeCost);
        state.addCost(BigInteger.ONE);
        check(
            state.getCostingEU()
                .equals(hugeCost.add(BigInteger.ONE)),
            "wireless cost accumulates beyond long range");
        state.resetCost();
        check(
            state.getCostingEU()
                .signum() == 0,
            "a new processing attempt clears the previous charge");
        UUID owner = new UUID(11, 17);
        state.bindOwner(owner);
        check(owner.equals(state.getOwnerUUID()), "wireless settlement retains its owner's identity");

        NBTTagCompound legacy = new NBTTagCompound();
        legacy.setBoolean("wirelessMode", true);
        legacy.setLong("configuredParallelLong", 12);
        legacy.setLong("effectiveParallelLong", 0);
        state.readFromNBT(legacy);
        check(
            state.isWirelessMode() && state.getMaxParallel() == 12 && state.getEffectiveParallel() == 0,
            "old saves restore mode, configured parallelism and the no-work state");
        NBTTagCompound saved = new NBTTagCompound();
        state.writeToNBT(saved);
        check(legacy.equals(saved), "purification save keys and values remain backward compatible");
        state.readFromNBT(new NBTTagCompound());
        check(state.isWirelessMode() && state.getMaxParallel() == 12, "missing legacy keys retain existing defaults");
    }
}
