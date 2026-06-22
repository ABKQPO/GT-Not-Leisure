package com.science.gtnl.common.machine.monitor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import net.minecraft.util.StatCollector;

import gregtech.api.enums.GTValues;
import gregtech.api.util.GTUtility;

public class EnergyMonitorFormatter {

    private static final BigInteger TWENTY = BigInteger.valueOf(20L);
    private static final BigInteger SIXTY = BigInteger.valueOf(60L);
    private static final BigInteger THREE_THOUSAND_SIX_HUNDRED = BigInteger.valueOf(3600L);
    private static final BigInteger EIGHTY_SIX_THOUSAND_FOUR_HUNDRED = BigInteger.valueOf(86400L);
    private static final BigInteger TWO_MILLION_FIVE_HUNDRED_NINETY_TWO_THOUSAND = BigInteger.valueOf(2592000L);
    private static final BigInteger THIRTY_ONE_MILLION_FIVE_HUNDRED_THIRTY_SIX_THOUSAND = BigInteger.valueOf(31536000L);

    public static String formatBigInteger(BigInteger value) {
        if (value == null) {
            return "0";
        }
        BigInteger absolute = value.abs();
        String digits = absolute.toString();
        StringBuilder builder = new StringBuilder(digits.length() + digits.length() / 3 + 2);
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && (digits.length() - i) % 3 == 0) {
                builder.append(',');
            }
            builder.append(digits.charAt(i));
        }
        if (value.signum() < 0) {
            builder.insert(0, '-');
        }
        return builder.toString();
    }

    public static String formatCompactBigInteger(BigInteger value) {
        if (value == null) {
            return "0";
        }
        BigInteger absolute = value.abs();
        if (absolute.toString()
            .length() > 12) {
            String scientific = GTUtility.scientificFormat(absolute);
            return value.signum() < 0 ? "-" + scientific : scientific;
        }
        return formatBigInteger(value);
    }

    public static String formatPercentage(BigInteger numerator, BigInteger denominator) {
        if (numerator == null || denominator == null || denominator.signum() <= 0) {
            return "0.0%";
        }
        BigDecimal percentage = new BigDecimal(numerator).multiply(BigDecimal.valueOf(100L))
            .divide(new BigDecimal(denominator), 1, RoundingMode.HALF_UP);
        return percentage.toPlainString() + "%";
    }

    public static int getVoltageTier(BigInteger eutMagnitude) {
        if (eutMagnitude == null || eutMagnitude.signum() <= 0) {
            return 0;
        }
        long capped = eutMagnitude.min(BigInteger.valueOf(GTValues.V[GTValues.V.length - 1]))
            .longValue();
        return GTUtility.clamp(GTUtility.getTier(capped), 0, GTValues.V.length - 1);
    }

    public static String formatAmps(BigInteger eutMagnitude, int voltageTier) {
        if (eutMagnitude == null || eutMagnitude.signum() <= 0) {
            return "0.0";
        }
        long voltage = GTValues.V[GTUtility.clamp(voltageTier, 0, GTValues.V.length - 1)];
        if (voltage <= 0L) {
            return "0.0";
        }
        BigDecimal amps = new BigDecimal(eutMagnitude).divide(BigDecimal.valueOf(voltage), 1, RoundingMode.HALF_UP);
        return amps.toPlainString();
    }

    public static String formatDuration(BigInteger ticks) {
        if (ticks == null || ticks.signum() <= 0) {
            return "0" + StatCollector.translateToLocal("gtnl.energy_monitor.time.second");
        }
        BigInteger seconds = ticks.divide(TWENTY);
        if (seconds.signum() <= 0) {
            seconds = BigInteger.ONE;
        }

        BigInteger years = seconds.divide(THIRTY_ONE_MILLION_FIVE_HUNDRED_THIRTY_SIX_THOUSAND);
        seconds = seconds.remainder(THIRTY_ONE_MILLION_FIVE_HUNDRED_THIRTY_SIX_THOUSAND);
        BigInteger months = seconds.divide(TWO_MILLION_FIVE_HUNDRED_NINETY_TWO_THOUSAND);
        seconds = seconds.remainder(TWO_MILLION_FIVE_HUNDRED_NINETY_TWO_THOUSAND);
        BigInteger days = seconds.divide(EIGHTY_SIX_THOUSAND_FOUR_HUNDRED);
        seconds = seconds.remainder(EIGHTY_SIX_THOUSAND_FOUR_HUNDRED);
        BigInteger hours = seconds.divide(THREE_THOUSAND_SIX_HUNDRED);
        seconds = seconds.remainder(THREE_THOUSAND_SIX_HUNDRED);
        BigInteger minutes = seconds.divide(SIXTY);
        seconds = seconds.remainder(SIXTY);

        StringBuilder builder = new StringBuilder();
        appendTimeUnit(builder, years, "gtnl.energy_monitor.time.year");
        appendTimeUnit(builder, months, "gtnl.energy_monitor.time.month");
        appendTimeUnit(builder, days, "gtnl.energy_monitor.time.day");
        appendTimeUnit(builder, hours, "gtnl.energy_monitor.time.hour");
        appendTimeUnit(builder, minutes, "gtnl.energy_monitor.time.minute");
        appendTimeUnit(builder, seconds, "gtnl.energy_monitor.time.second");
        return builder.length() == 0 ? "0" + StatCollector.translateToLocal("gtnl.energy_monitor.time.second")
            : builder.toString();
    }

    private static void appendTimeUnit(StringBuilder builder, BigInteger value, String langKey) {
        if (value.signum() <= 0) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(value)
            .append(StatCollector.translateToLocal(langKey));
    }
}
