package com.science.gtnl;

import static com.gtnltest.TestSupport.check;

import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Check the actual distributable archives, including what the Java 8 loader sees. */
public final class ModuleArtifactTest {

    private static final Pattern VERSIONED = Pattern.compile("META-INF/versions/(\\d+)/.*");

    @Test
    void packagedModulesHaveIndependentMetadataAndResources() throws Exception {
        String[] libraries = System.getProperty("gtnl.test.libraryJars")
            .split(java.io.File.pathSeparator);
        String[] args = new String[1 + libraries.length];
        args[0] = System.getProperty("gtnl.test.hostJar");
        System.arraycopy(libraries, 0, args, 1, libraries.length);
        check(args.length == 4, "one host archive and the library's release, dev and source archives are supplied");
        try (JarFile host = new JarFile(args[0])) {
            binary(host, "sciencenotleisure", "com.science.gtnl.asm.GTNLEarlyCoreMod", "com/science/gtnl/");
            check(
                host.stream()
                    .noneMatch(
                        entry -> entry.getName()
                            .contains("com/gtnllib/")
                            || entry.getName()
                                .startsWith("assets/gtnllib/")
                            || entry.getName()
                                .startsWith("mixins.gtnllib.")),
                "the host jar does not embed a second library copy");
        }
        int binaries = 0, sources = 0;
        for (int i = 1; i < args.length; i++) {
            try (JarFile library = new JarFile(args[i])) {
                check(
                    library.stream()
                        .noneMatch(
                            entry -> entry.getName()
                                .contains("com/science/gtnl/")
                                || entry.getName()
                                    .contains("gregtech/")
                                || entry.getName()
                                    .contains("assets/sciencenotleisure/")),
                    "the library archive contains no host code or resources: " + args[i]);
                check(library.getEntry("LICENSE.txt") != null, "library distribution retains its license");
                check(
                    library.getEntry("licenses/ANVILLIB-MIT.txt") != null,
                    "library distribution retains the wheel's third-party license");
                if (args[i].endsWith("-sources.jar")) {
                    sources++;
                    check(
                        library.getEntry("com/gtnllib/GTNLLib.java") != null,
                        "the library source archive is complete");
                    continue;
                }
                binaries++;
                binary(library, "gtnllib", "com.gtnllib.mixins.GTNLLibEarlyCoreMod", "com/gtnllib/");
                check(
                    "gtnllib_at.cfg".equals(
                        library.getManifest()
                            .getMainAttributes()
                            .getValue("FMLAT"))
                        && library.getEntry("META-INF/gtnllib_at.cfg") != null,
                    "the library carries and declares its own access transformer");
            }
        }
        check(binaries == 2 && sources == 1, "release, dev and sources are distributed without test-fixture jars");
    }

    private static void binary(JarFile jar, String modId, String coremod, String packagePath) throws Exception {
        String label = jar.getName();
        check(
            coremod.equals(
                jar.getManifest()
                    .getMainAttributes()
                    .getValue("FMLCorePlugin")),
            label + " owns its coremod");
        check(jar.getEntry(coremod.replace('.', '/') + ".class") != null, label + " contains the declared coremod");
        check(
            "true".equals(
                jar.getManifest()
                    .getMainAttributes()
                    .getValue("Multi-Release")),
            label + " is multi-release");
        try (InputStreamReader reader = reader(jar, "mcmod.info")) {
            JsonArray metadata = new JsonParser().parse(reader)
                .getAsJsonArray();
            check(
                metadata.size() == 1 && modId.equals(
                    metadata.get(0)
                        .getAsJsonObject()
                        .get("modid")
                        .getAsString()),
                label + " declares exactly its own mod");
        }
        String refmap = "mixins." + modId + ".refmap.json";
        try (InputStreamReader reader = reader(jar, "mixins." + modId + ".json")) {
            check(
                refmap.equals(
                    new JsonParser().parse(reader)
                        .getAsJsonObject()
                        .get("refmap")
                        .getAsString()),
                label + " includes the convention plugin's default mixin configuration");
        }
        for (String phase : List.of("early", "late")) {
            for (String version : List.of("", ".j17", ".j21")) {
                try (InputStreamReader reader = reader(jar, "mixins." + modId + "." + phase + version + ".json")) {
                    check(
                        refmap.equals(
                            new JsonParser().parse(reader)
                                .getAsJsonObject()
                                .get("refmap")
                                .getAsString()),
                        label + " " + phase + version + " uses its own refmap");
                }
            }
        }
        try (InputStreamReader reader = reader(jar, refmap)) {
            JsonObject mappings = new JsonParser().parse(reader)
                .getAsJsonObject()
                .getAsJsonObject("mappings");
            check(
                !mappings.entrySet()
                    .isEmpty(),
                label + " includes generated mixin mappings");
            check(
                mappings.entrySet()
                    .stream()
                    .allMatch(
                        key -> key.getKey()
                            .replace('.', '/')
                            .startsWith(packagePath)),
                label + " refmap contains only its own mixins");
        }
        for (var entry : jar.stream()
            .filter(
                entry -> entry.getName()
                    .endsWith(".class"))
            .toList()) {
            String name = entry.getName();
            check(!name.contains("com/gtnltest/"), label + " excludes test helpers: " + name);
            Matcher version = VERSIONED.matcher(name);
            int maximum = version.matches() ? Integer.parseInt(version.group(1)) + 44 : 52;
            try (DataInputStream bytes = new DataInputStream(jar.getInputStream(entry))) {
                check(bytes.readInt() == 0xCAFEBABE, label + " has a valid class header: " + name);
                bytes.readUnsignedShort();
                check(bytes.readUnsignedShort() <= maximum, label + " keeps the advertised Java level: " + name);
            }
        }
    }

    private static InputStreamReader reader(JarFile jar, String name) throws Exception {
        var entry = jar.getEntry(name);
        if (entry == null) throw new AssertionError(jar.getName() + " is missing " + name);
        return new InputStreamReader(jar.getInputStream(entry), StandardCharsets.UTF_8);
    }
}
