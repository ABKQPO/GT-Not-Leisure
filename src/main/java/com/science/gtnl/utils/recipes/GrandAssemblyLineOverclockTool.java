package com.science.gtnl.utils.recipes;

import java.math.BigInteger;
import java.util.Locale;
import java.util.Scanner;

import gregtech.api.enums.GTValues;

public class GrandAssemblyLineOverclockTool {

    public static final int STANDARD_OVERCLOCK_FACTOR = 2;
    public static final int PERFECT_OVERCLOCK_FACTOR = 4;

    public static double getEUtDiscount(final int parallelTier) {
        return 0.8 - (parallelTier / 50.0) * (parallelTier >= 12 ? 0.2 : 1.0);
    }

    public static double getDurationModifier(final int parallelTier) {
        return (1.0 / 1.67 - (Math.max(0, parallelTier - 1) / 50.0)) * (parallelTier >= 12 ? 1.0 / 20.0 : 1.0);
    }

    public static long parseVoltage(final String value) {
        final String voltage = value.trim()
            .toUpperCase(Locale.ROOT);
        for (int tier = 0; tier < GTValues.VN.length; tier++) {
            if (GTValues.VN[tier].equalsIgnoreCase(voltage)) return GTValues.V[tier];
        }
        final String numericValue = voltage.endsWith("V") ? voltage.substring(0, voltage.length() - 1) : voltage;
        try {
            final long parsed = Long.parseLong(numericValue);
            if (parsed > 0) return parsed;
        } catch (final NumberFormatException ignored) {}
        throw new IllegalArgumentException("Unknown voltage tier or invalid voltage: " + value);
    }

    public static Result calculate(final long recipeEUt, final int recipeDuration, final long machineVoltage,
        final long machineAmperage, final int requestedParallel, final int maxParallel, final int parallelTier,
        final boolean perfectOverclock, final boolean applySpeedDiscount, final boolean applyEUtDiscount,
        final int minDuration) {
        if (recipeEUt <= 0 || recipeDuration <= 0
            || machineVoltage <= 0
            || machineAmperage <= 0
            || requestedParallel <= 0
            || maxParallel <= 0
            || parallelTier < 0
            || minDuration <= 0) {
            throw new IllegalArgumentException(
                "Power, duration, voltage, amperage, parallel values, and minimum duration must be positive; tier cannot be negative.");
        }

        final long discountedEUt = Math
            .max(1L, (long) (recipeEUt * (applyEUtDiscount ? getEUtDiscount(parallelTier) : 1.0)));
        final int discountedDuration = Math
            .max(1, (int) (recipeDuration * (applySpeedDiscount ? getDurationModifier(parallelTier) : 1.0)));
        final BigInteger maxPower = BigInteger.valueOf(machineVoltage)
            .multiply(BigInteger.valueOf(machineAmperage));
        final long powerLimit = maxPower.min(BigInteger.valueOf(Long.MAX_VALUE))
            .longValue();
        if (BigInteger.valueOf(recipeEUt)
            .compareTo(maxPower) > 0) {
            throw new IllegalArgumentException("Recipe EU/t exceeds machine voltage times amperage.");
        }
        final int parallelLimit = Math.min(requestedParallel, maxParallel);
        final int actualParallel = (int) Math.min(parallelLimit, powerLimit / discountedEUt);
        if (actualParallel <= 0) {
            throw new IllegalArgumentException("Machine power cannot run one parallel after discounts.");
        }
        final int overclockFactor = perfectOverclock ? PERFECT_OVERCLOCK_FACTOR : STANDARD_OVERCLOCK_FACTOR;
        final Result result = applyWiredOverclock(
            discountedEUt,
            discountedDuration,
            powerLimit,
            actualParallel,
            minDuration,
            overclockFactor);
        return new Result(
            result.power(),
            result.duration(),
            result.overclocks(),
            result.addedInstantPower(),
            actualParallel,
            maxParallel);
    }

    public static Result applyWiredOverclock(final long currentPower, final int currentDuration,
        final long availablePower, final int parallel, final int minDuration, final int overclockFactor) {
        if (currentPower <= 0 || currentDuration <= 0
            || availablePower <= 0
            || parallel <= 0
            || minDuration <= 0
            || overclockFactor <= 0) {
            throw new IllegalArgumentException("Overclock inputs must be positive.");
        }

        long power = currentPower;
        int duration = currentDuration;
        int overclocks = 0;
        BigInteger addedInstantPower = BigInteger.ZERO;
        final BigInteger powerLimit = BigInteger.valueOf(availablePower);
        final BigInteger baseInstantPower = BigInteger.valueOf(currentPower)
            .multiply(BigInteger.valueOf(parallel));
        while (power <= Integer.MAX_VALUE / 4L && duration / overclockFactor >= minDuration) {
            final long nextPower = power * 4L;
            final BigInteger addedPower = BigInteger.valueOf(power)
                .multiply(BigInteger.valueOf(3L))
                .multiply(BigInteger.valueOf(parallel));
            final BigInteger nextAddedInstantPower = addedInstantPower.add(addedPower);
            final BigInteger nextTotalPower = baseInstantPower.add(nextAddedInstantPower);
            if (nextTotalPower.compareTo(powerLimit) > 0) {
                break;
            }
            power = nextPower;
            duration /= overclockFactor;
            addedInstantPower = nextAddedInstantPower;
            overclocks++;
        }
        return new Result(power, Math.max(1, duration), overclocks, addedInstantPower.longValue(), parallel, parallel);
    }

    public static void main(final String[] args) {
        final Scanner input = new Scanner(System.in);
        try {
            final long recipeEUt = readLong(input, "Recipe EU/t");
            final int recipeDuration = readInt(input, "Recipe duration in ticks");
            final long machineVoltage = parseVoltage(
                readLine(input, "Machine voltage tier or value (e.g. EV or 8192)"));
            final long machineAmperage = readLong(input, "Machine amperage");
            final int requestedParallel = readInt(input, "Input-available parallel count");
            final int maxParallel = readInt(input, "Machine maximum parallel count");
            final int parallelTier = readInt(input, "Grand Assembly Line parallel module tier");
            final boolean perfectOverclock = readBoolean(input, "Perfect overclock (y/n)");
            final boolean applySpeedDiscount = readBoolean(input, "Apply speed discount (y/n)");
            final boolean applyEUtDiscount = readBoolean(input, "Apply EU/t discount (y/n)");
            final int minDuration = readInt(input, "Minimum recipe duration in ticks");
            final Result result = calculate(
                recipeEUt,
                recipeDuration,
                machineVoltage,
                machineAmperage,
                requestedParallel,
                maxParallel,
                parallelTier,
                perfectOverclock,
                applySpeedDiscount,
                applyEUtDiscount,
                minDuration);
            System.out.println("Overclocks: " + result.overclocks());
            System.out.println("Parallel used: " + result.parallel() + " / " + result.parallelLimit());
            System.out.println("Final EU/t per parallel: " + result.power());
            System.out.println(
                "Final total EU/t: " + BigInteger.valueOf(result.power())
                    .multiply(BigInteger.valueOf(result.parallel())));
            System.out.println("Final duration: " + result.duration() + " ticks");
            System.out.println(
                "Total energy: " + BigInteger.valueOf(result.power())
                    .multiply(BigInteger.valueOf(result.duration()))
                    .multiply(BigInteger.valueOf(result.parallel())) + " EU");
            System.out.println(
                "Machine power limit: " + BigInteger.valueOf(machineVoltage)
                    .multiply(BigInteger.valueOf(machineAmperage)) + " EU/t");
        } catch (final IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static long readLong(final Scanner input, final String prompt) {
        return Long.parseLong(readLine(input, prompt));
    }

    private static int readInt(final Scanner input, final String prompt) {
        return Integer.parseInt(readLine(input, prompt));
    }

    private static boolean readBoolean(final Scanner input, final String prompt) {
        final String value = readLine(input, prompt).toLowerCase(Locale.ROOT);
        if (value.equals("y") || value.equals("yes") || value.equals("true") || value.equals("1")) return true;
        if (value.equals("n") || value.equals("no") || value.equals("false") || value.equals("0")) return false;
        throw new IllegalArgumentException("Enter y/n, true/false, or 1/0 for: " + prompt);
    }

    private static String readLine(final Scanner input, final String prompt) {
        System.out.print(prompt + ": ");
        if (!input.hasNextLine()) throw new IllegalArgumentException("Missing input for: " + prompt);
        return input.nextLine()
            .trim();
    }

    public record Result(long power, int duration, int overclocks, long addedInstantPower, int parallel,
        int parallelLimit) {}
}
