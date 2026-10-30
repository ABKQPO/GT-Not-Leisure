package com.gtnllib.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import net.minecraft.util.MathHelper;

public class MathUtils {

    public static float clampUnit(float value) {
        return MathHelper.clamp_float(value, 0F, 1F);
    }

    public static double clampUnit(double value) {
        return MathHelper.clamp_double(value, 0, 1);
    }

    public static float clampUnit(float value, float fallback) {
        return Float.isFinite(value) ? clampUnit(value) : fallback;
    }

    public static long hash(long a, long b, long c, long d) {
        long hash = a * 0x9E3779B97F4A7C15L + b * 0xC2B2AE3D27D4EB4FL
            + c * 0x165667B19E3779F9L
            + d * 0xD6E8FEB86659FD93L;
        hash = (hash ^ (hash >>> 30)) * 0xBF58476D1CE4E5B9L;
        hash = (hash ^ (hash >>> 27)) * 0x94D049BB133111EBL;
        return hash ^ (hash >>> 31);
    }

    public static double hashUnit(long a, long b, long c, long d) {
        return (hash(a, b, c, d) >>> 11) * 0x1.0p-53;
    }

    public static final double LOG2 = Math.log(2);
    public static final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);
    public static final BigInteger INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
    public static final BigInteger BIG_INTEGER_100 = BigInteger.valueOf(100);
    public static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    public static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    public static final String ZERO_STRING = "0";
    public static final String[] UNITS = { "", "K", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q" };
    public static final BigDecimal THOUSAND_DEC = BigDecimal.valueOf(1000);
    public static final DecimalFormat DF = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));

    public static long toLongSafe(BigInteger value) {
        if (value == null) return 0L;
        if (value.compareTo(LONG_MAX) > 0) {
            return Long.MAX_VALUE;
        }
        if (value.compareTo(LONG_MIN) < 0) {
            return Long.MIN_VALUE;
        }
        return value.longValue();
    }

    public static String shortFormat(long value) {
        double number = value;
        int unitIndex = 0;
        while (Math.abs(number) >= 1000 && unitIndex < UNITS.length - 1) {
            number /= 1000;
            unitIndex++;
        }

        String formatted = DF.format(number);
        if (formatted.endsWith(".00")) {
            formatted = formatted.substring(0, formatted.length() - 3);
        }

        return formatted + UNITS[unitIndex];
    }

    public static String shortFormat(BigInteger value) {
        BigDecimal decimal = new BigDecimal(value);
        int unitIndex = 0;

        while (decimal.compareTo(THOUSAND_DEC) >= 0 && unitIndex < UNITS.length - 1) {
            decimal = decimal.divide(THOUSAND_DEC, 2, RoundingMode.HALF_UP);
            unitIndex++;
        }

        String formatted = DF.format(decimal);
        if (formatted.endsWith(".00")) {
            formatted = formatted.substring(0, formatted.length() - 3);
        }

        return formatted + UNITS[unitIndex];
    }

    public static <T> T[] mergeArray(T[] array1, T[] array2) {
        if (array1 == null || array1.length < 1) {
            return array2;
        }
        if (array2 == null || array2.length < 1) {
            return array1;
        }
        T[] newArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);
        return newArray;
    }
}
