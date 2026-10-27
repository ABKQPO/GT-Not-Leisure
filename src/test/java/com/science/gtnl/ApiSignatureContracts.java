package com.science.gtnl;

import static com.gtnllib.GTNLLibContractTest.check;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApiSignatureContracts {

    public static final Path API_SOURCES = Paths.get("src/main/java/com/science/gtnl/api");
    public static final Path BASELINE = Paths.get("src/test/resources/com/science/gtnl/api-signatures.txt");
    public static final String DUMP_VARIABLE = "GTNL_API_SIGNATURE_DUMP";

    private ApiSignatureContracts() {}

    public static void run() throws IOException {
        Set<String> current = currentSignatures();
        if (System.getenv(DUMP_VARIABLE) != null) {
            Files.write(BASELINE, current, StandardCharsets.UTF_8);
        }
        if (!Files.exists(BASELINE)) {
            check(false, "the public API baseline " + BASELINE + " exists; run once with " + DUMP_VARIABLE + "=1");
            return;
        }
        List<String> missing = Files.readAllLines(BASELINE, StandardCharsets.UTF_8)
            .stream()
            .filter(line -> !line.isEmpty() && !current.contains(line))
            .collect(Collectors.toList());
        check(
            missing.isEmpty(),
            "public API signatures were removed or changed, which breaks addons built against older GTNL; keep the old"
                + " name or record the break in the migration table and refresh the baseline: "
                + missing);
    }

    public static Set<String> currentSignatures() throws IOException {
        Set<String> signatures = new TreeSet<>();
        try (Stream<Path> files = Files.walk(API_SOURCES)) {
            for (Path file : files.filter(
                path -> path.toString()
                    .endsWith(".java"))
                .collect(Collectors.toList())) {
                String relative = Paths.get("src/main/java")
                    .relativize(file)
                    .toString()
                    .replace('\\', '/');
                String name = relative.substring(0, relative.length() - ".java".length())
                    .replace('/', '.');
                try {
                    describe(Class.forName(name, false, ApiSignatureContracts.class.getClassLoader()), signatures);
                } catch (ClassNotFoundException | LinkageError error) {
                    signatures.add("unloadable " + name + ": " + error);
                }
            }
        }
        return signatures;
    }

    private static void describe(Class<?> type, Set<String> signatures) {
        if (!isVisible(type.getModifiers())) return;
        String owner = type.getName();
        signatures.add("type " + owner);
        for (Field field : type.getDeclaredFields()) {
            if (isVisible(field) && !field.isSynthetic()) signatures.add(
                owner + "."
                    + field.getName()
                    + ":"
                    + field.getType()
                        .getTypeName());
        }
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (isVisible(constructor) && !constructor.isSynthetic())
                signatures.add(owner + "#<init>(" + parameters(constructor.getParameterTypes()) + ")");
        }
        for (Method method : type.getDeclaredMethods()) {
            if (isVisible(method) && !method.isSynthetic() && !method.isBridge()) signatures.add(
                owner + "#"
                    + method.getName()
                    + "("
                    + parameters(method.getParameterTypes())
                    + ")"
                    + method.getReturnType()
                        .getTypeName());
        }
        for (Class<?> nested : type.getDeclaredClasses()) {
            describe(nested, signatures);
        }
    }

    private static boolean isVisible(Member member) {
        return isVisible(member.getModifiers());
    }

    private static boolean isVisible(int modifiers) {
        return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
    }

    private static String parameters(Class<?>[] types) {
        return Arrays.stream(types)
            .map(Class::getTypeName)
            .collect(Collectors.joining(","));
    }
}
