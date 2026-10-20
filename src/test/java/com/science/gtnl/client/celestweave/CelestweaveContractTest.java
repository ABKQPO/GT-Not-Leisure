package com.science.gtnl.client.celestweave;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CelestweaveContractTest {

    static final Path RESOURCES = Paths.get("src/main/resources/assets/sciencenotleisure");
    static final Path SOURCES = Paths.get("src/main/java/com/science/gtnl");
    private static final List<String> FAILURES = new ArrayList<>();
    private static int assertions;

    private CelestweaveContractTest() {}

    public static void main(String[] args) throws Exception {
        CelestweaveArmorContracts.run();
        RailgunContracts.run();
        WheelHudContracts.run();
        EquipmentResourceContracts.run();
        if (!FAILURES.isEmpty()) {
            FAILURES.forEach(failure -> System.out.println("FAIL " + failure));
            throw new AssertionError(FAILURES.size() + " Celestweave contracts failed");
        }
        System.out.println("Celestweave contracts passed: " + assertions + " assertions");
    }

    static void check(boolean condition, String message) {
        assertions++;
        if (!condition) FAILURES.add(message);
    }

    static void near(double expected, double actual, String message) {
        check(Math.abs(expected - actual) < 1.0E-6, message + ": expected " + expected + ", got " + actual);
    }

    static Object call(Class<?> owner, String name, Object target, Class<?>[] types, Object... args)
        throws ReflectiveOperationException {
        Method method = owner.getDeclaredMethod(name, types);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    static Object field(Object target, String name) throws ReflectiveOperationException {
        Field field = target.getClass()
            .getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    static <T> T roundTrip(T packet, Constructor<T> empty) throws ReflectiveOperationException {
        ByteBuf buffer = Unpooled.buffer();
        ((IMessage) packet).toBytes(buffer);
        T copy = empty.newInstance();
        ((IMessage) copy).fromBytes(buffer);
        check(
            buffer.readableBytes() == 0,
            packet.getClass()
                .getSimpleName() + " leaves no trailing bytes");
        return copy;
    }
}
