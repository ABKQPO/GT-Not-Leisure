package com.gtnllib;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.gtnllib.client.GTNLLibClient;
import com.gtnllib.gui.widget.StepSlider;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class GTNLLibContractTest {

    public static final Path SOURCES = Paths.get("src/main/java/com/gtnllib");
    public static final Path RESOURCES = Paths.get("src/main/resources/assets/gtnllib");
    private static final List<String> FAILURES = new ObjectArrayList<>();
    private static int assertions;

    private GTNLLibContractTest() {}

    public static void main(String[] args) throws Exception {
        run();
        finish("GTNLLib");
    }

    public static void run() throws Exception {
        GTNLLibClient.connectConfig();
        HudContracts.run();
        WheelContracts.run();
        LayoutContracts.run();
        ThemeContracts.run();
        BoundaryContracts.run();
        FxContracts.run();
        RegistryContracts.run();
    }

    public static void finish(String suite) {
        if (!FAILURES.isEmpty()) {
            FAILURES.forEach(failure -> System.out.println("FAIL " + failure));
            throw new AssertionError(FAILURES.size() + " " + suite + " contracts failed");
        }
        System.out.println(suite + " contracts passed: " + assertions + " assertions");
    }

    public static void check(boolean condition, String message) {
        assertions++;
        if (!condition) FAILURES.add(message);
    }

    public static void near(double expected, double actual, String message) {
        check(Math.abs(expected - actual) < 1.0E-6, message + ": expected " + expected + ", got " + actual);
    }

    public static Object call(Class<?> owner, String name, Object target, Class<?>[] types, Object... args)
        throws ReflectiveOperationException {
        Method method = owner.getDeclaredMethod(name, types);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    public static boolean compactDrawable(String text) throws ReflectiveOperationException {
        Field chars = StepSlider.class.getDeclaredField("COMPACT_CHARS");
        chars.setAccessible(true);
        String charset = (String) chars.get(null);
        return text.chars()
            .allMatch(c -> charset.indexOf(c) >= 0);
    }

    public static Object field(Object target, String name) throws ReflectiveOperationException {
        Field field = target.getClass()
            .getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    public static String read(Path file) throws IOException {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    public static String compact(String source) {
        return source.replaceAll("\\s+", "");
    }

    public static boolean rejects(Runnable action) {
        try {
            action.run();
            return false;
        } catch (RuntimeException expected) {
            return true;
        }
    }
}
