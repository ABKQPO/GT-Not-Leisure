package com.science.gtnl.common.machine.multiblock;

import java.math.BigInteger;
import java.util.Random;

import com.science.gtnl.common.machine.multiblock.GrandAssemblyLineOverclockTool.EnergyTotals;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLineOverclockTool.Result;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLineOverclockTool.Settlement;

public final class GrandAssemblyLineOverclockContracts {

    private GrandAssemblyLineOverclockContracts() {}

    public static void main(String[] args) {
        continuesPastThirtySevenAmps();
        respectsPowerAndDurationLimits();
        preventsLongPowerOverflow();
        wirelessUsesExactEnergy();
        settlesEnergyAboveLongRange();
        settlesWirelessPowerAboveLongRange();
        preservesMaximumDuration();
        paysForFractionalTicks();
        mixedTasksConserveEnergy();
        System.out.println("Grand Assembly Line overclock and energy contracts passed");
    }

    private static void continuesPastThirtySevenAmps() {
        long uiv = 33_554_432L;
        Result result = GrandAssemblyLineOverclockTool
            .calculate(uiv, 100_000, uiv, 1_048_576, 1, 1, 11, true, true, true, 50);
        check(result.overclocks() == 4, "the 37.12 A reproduction must continue past the int limit");
        check(result.power() == 4_982_161_920L, "the fourth overclock must use 148.48 A UIV");
        check(result.duration() == 155, "the next overclock stops at the configured 50 tick floor");
    }

    private static void respectsPowerAndDurationLimits() {
        Result result = GrandAssemblyLineOverclockTool.applyWiredOverclock(100, 800, 6400, 4, 50, 4);
        check(result.power() == 1600 && result.duration() == 50, "ordinary perfect overclocks remain unchanged");
        check(result.addedInstantPower() == 6000, "all parallels must be included in the reserved power");
        result = GrandAssemblyLineOverclockTool.applyWiredOverclock(100, 800, 6399, 4, 50, 4);
        check(result.power() == 400 && result.duration() == 200, "one EU/t short must reject the next overclock");
        result = GrandAssemblyLineOverclockTool.applyWiredOverclock(100, 20, 6400, 1, 50, 2);
        check(result.power() == 100 && result.duration() == 20, "a speed-discounted short recipe is not extended");
    }

    private static void preventsLongPowerOverflow() {
        long initial = Long.MAX_VALUE / 4;
        Result result = GrandAssemblyLineOverclockTool.applyWiredOverclock(initial, 800, Long.MAX_VALUE, 1, 1, 4);
        check(result.overclocks() == 1 && result.power() == initial * 4, "the last representable long overclock fits");
        check(result.addedInstantPower() == initial * 3, "incremental power stays exact near Long.MAX_VALUE");
        result = GrandAssemblyLineOverclockTool.applyWiredOverclock(initial, 800, Long.MAX_VALUE, 2, 1, 4);
        check(result.overclocks() == 0, "parallel multiplication cannot wrap around the power budget");
        result = GrandAssemblyLineOverclockTool
            .applyWirelessOverclock(initial, 800, BigInteger.ONE.shiftLeft(100), 2, 1, 4);
        check(result.overclocks() == 1 && result.power() > 0, "wireless power cannot wrap even with unlimited funds");
    }

    private static void wirelessUsesExactEnergy() {
        long power = 1_500_000_000L;
        int parallel = 16_000_000;
        BigInteger budget = new BigInteger("24000000000000000000");
        Result result = GrandAssemblyLineOverclockTool.applyWirelessOverclock(power, 1000, budget, parallel, 50, 4);
        check(result.power() == 24_000_000_000L && result.duration() == 62, "wireless perfect overclock remains exact");
        BigInteger energy = GrandAssemblyLineOverclockTool.getTotalEnergy(result.power(), result.duration(), parallel);
        check(energy.equals(new BigInteger("23808000000000000000")), "wireless debit retains every energy bit");

        // Here even a single parallel's energy is larger than a long.
        power = Long.MAX_VALUE / 8;
        BigInteger nextEnergy = GrandAssemblyLineOverclockTool.getTotalEnergy(power * 4, 100, 1);
        result = GrandAssemblyLineOverclockTool
            .applyWirelessOverclock(power, 200, nextEnergy.subtract(BigInteger.ONE), 1, 50, 2);
        check(result.overclocks() == 0, "insufficient wireless funds must reject an overflowing long product");
        result = GrandAssemblyLineOverclockTool.applyWirelessOverclock(power, 200, nextEnergy, 1, 50, 2);
        check(
            result.overclocks() == 1 && result.duration() == 100,
            "exact wireless funds allow one standard overclock");
    }

    private static void settlesEnergyAboveLongRange() {
        EnergyTotals totals = new EnergyTotals();
        totals.add(1_500_000_000L, 400, 16_000_000);
        check(
            totals.energy()
                .equals(new BigInteger("9600000000000000000")),
            "wired total energy cannot wrap negative");
        Settlement result = totals.settleWired(30_000_000_000_000_000L);
        check(
            result.power() == 24_000_000_000_000_000L && result.duration() == 400,
            "large jobs cannot finish for 1 EU in 1 tick");
    }

    private static void settlesWirelessPowerAboveLongRange() {
        EnergyTotals totals = new EnergyTotals();
        totals.add(24_000_000_000L, 62, 16_000_000);
        check(totals.weightedDuration() == 62, "large wireless energy must not shorten 62 ticks to 13");
        totals = new EnergyTotals();
        totals.add(Long.MAX_VALUE, 128, 2);
        check(totals.weightedDuration() == 128, "wireless total power may also exceed Long.MAX_VALUE");
        check(
            totals.energy()
                .equals(
                    BigInteger.valueOf(Long.MAX_VALUE)
                        .multiply(BigInteger.valueOf(256))),
            "wireless energy stays exact");
    }

    private static void preservesMaximumDuration() {
        EnergyTotals totals = new EnergyTotals();
        totals.add(Long.MAX_VALUE, Integer.MAX_VALUE, 1);
        Settlement result = totals.settleWired(Long.MAX_VALUE);
        check(
            result.power() == Long.MAX_VALUE && result.duration() == Integer.MAX_VALUE,
            "maximum power and duration remain representable");
    }

    private static void paysForFractionalTicks() {
        EnergyTotals totals = new EnergyTotals();
        totals.add(1, 2, 1);
        totals.add(1, 3, 1);
        Settlement result = totals.settleWired(2);
        check(result.power() == 2 && result.duration() == 3, "five EU on a two EU/t supply needs three ticks");
    }

    private static void mixedTasksConserveEnergy() {
        Random random = new Random(37);
        for (int sample = 0; sample < 1000; sample++) {
            EnergyTotals totals = new EnergyTotals();
            long supply = 0;
            for (int task = 0; task < 4; task++) {
                long power = 1 + random.nextInt(1_000_000_000);
                int parallel = 1 + random.nextInt(1_000_000);
                totals.add(power, 1 + random.nextInt(Integer.MAX_VALUE - 1), parallel);
                supply += power * parallel;
            }
            Settlement result = totals.settleWired(supply);
            BigInteger paid = GrandAssemblyLineOverclockTool.getTotalEnergy(result.power(), result.duration(), 1);
            check(result.power() > 0 && result.power() <= supply, "settlement stays within the supply");
            check(
                result.duration() > 0 && paid.compareTo(totals.energy()) >= 0,
                "mixed jobs cannot lose energy to rounding or overflow");
            check(
                paid.subtract(totals.energy())
                    .compareTo(BigInteger.valueOf(result.power())) < 0,
                "rounding costs less than one extra tick");
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
