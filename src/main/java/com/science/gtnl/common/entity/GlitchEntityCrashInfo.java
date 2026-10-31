package com.science.gtnl.common.entity;

import net.minecraft.crash.CrashReportCategory;

import gregtech.api.objects.XSTR;

public class GlitchEntityCrashInfo {

    public static final String[] ERRORS = { "ArrayIndexOutOfBoundsException", "ArrayStoreException",
        "BootstrapMethodError", "IllegalThreadStateException", "IllegalArgumentException", "ArithmeticException",
        "ClassCastException", "IllegalStateException", "UnsupportedOperationException", "NumberFormatException",
        "IndexOutOfBoundsException", "NullPointerException", "NoSuchMethodException", "ClassNotFoundException",
        "ConcurrentModificationException", "StackOverflowError", "OutOfMemoryError", "RuntimeException",
        "SecurityException", "StringIndexOutOfBoundsException", "ThreadDeath", "TypeNotPresentException",
        "UnknownError", "VirtualMachineError", "VerifyError", "AbstractMethodError" };

    private static String randomError() {
        return "java.lang." + ERRORS[new XSTR().nextInt(ERRORS.length)];
    }

    public static void write(CrashReportCategory category) {
        category.addCrashSection("Entity Type", randomError() + " (" + randomError() + ")");
        category.addCrashSection("Entity ID", randomError());
        category.addCrashSection("Entity Name", randomError());
        category
            .addCrashSection("Entity's Exact location", randomError() + ", " + randomError() + ", " + randomError());
        category.addCrashSection(
            "Entity's Block location",
            "World: (" + randomError()
                + ","
                + randomError()
                + ","
                + randomError()
                + "), "
                + "Chunk: (at "
                + randomError()
                + ","
                + randomError()
                + ","
                + randomError()
                + " in "
                + randomError()
                + ","
                + randomError()
                + "; contains blocks "
                + randomError()
                + ","
                + randomError()
                + ","
                + randomError()
                + " to "
                + randomError()
                + ","
                + randomError()
                + ","
                + randomError()
                + "), "
                + "Region: ("
                + randomError()
                + ","
                + randomError()
                + "; contains chunks "
                + randomError()
                + ","
                + randomError()
                + " to "
                + randomError()
                + ","
                + randomError()
                + ", blocks "
                + randomError()
                + ","
                + randomError()
                + ","
                + randomError()
                + " to "
                + randomError()
                + ","
                + randomError()
                + ","
                + randomError()
                + ")");
        category.addCrashSection("Entity's Momentum", randomError() + ", " + randomError() + ", " + randomError());
    }
}
