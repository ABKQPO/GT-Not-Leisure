package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util;

import static tectech.util.TTUtility.toExponentForm;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

public enum MilestoneFormatter {

    NONE,
    COMMA,
    EXPONENT;

    private static final NumberFormat COMMA_FORMAT = NumberFormat.getIntegerInstance(Locale.US);

    public static final MilestoneFormatter[] VALUES = values();

    public MilestoneFormatter cycle() {
        return switch (this) {
            case NONE -> COMMA;
            case COMMA -> EXPONENT;
            case EXPONENT -> NONE;
        };
    }

    public String format(Number number) {
        return switch (this) {
            case NONE -> number.toString();
            case COMMA -> {
                if (number instanceof BigInteger bi) yield COMMA_FORMAT.format(bi);
                yield COMMA_FORMAT.format(number.longValue());
            }
            case EXPONENT -> {
                if (number instanceof BigInteger bi) {
                    if (bi.compareTo(BigInteger.valueOf(1_000L)) > 0) {
                        yield toExponentForm(bi);
                    }
                    yield bi.toString();
                }
                long value = number.longValue();
                if (value > 1_000L) {
                    yield toExponentForm(value);
                }
                yield Long.toString(value);
            }
        };
    }
}
