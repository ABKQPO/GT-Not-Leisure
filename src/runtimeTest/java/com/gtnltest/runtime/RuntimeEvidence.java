package com.gtnltest.runtime;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;
import com.gtnllib.GTNLLib;
import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

/** Records the code locations actually loaded by Forge, before any test scenario starts. */
final class RuntimeEvidence {

    private RuntimeEvidence() {}

    static void write(Path report, String runId, String phase) throws Exception {
        Map<String, Object> evidence = new Object2ObjectLinkedOpenHashMap<>();
        evidence.put("schemaVersion", 1);
        evidence.put("runId", runId);
        evidence.put("phase", phase);
        evidence.put("capturedAt", "Forge initialization, before scenarios");
        Map<String, Object> code = new Object2ObjectLinkedOpenHashMap<>();
        code.put("host", fingerprint(ScienceNotLeisure.class));
        code.put("library", fingerprint(GTNLLib.class));
        code.put("testMod", fingerprint(RuntimeTestMod.class));
        evidence.put("code", code);
        Map<String, String> mods = new Object2ObjectLinkedOpenHashMap<>();
        for (ModContainer mod : Loader.instance()
            .getModList()) mods.put(mod.getModId(), mod.getVersion());
        evidence.put("mods", mods);
        Files.createDirectories(report.getParent());
        Files.writeString(
            report.resolveSibling(phase + "-environment.json"),
            new GsonBuilder().setPrettyPrinting()
                .create()
                .toJson(evidence),
            StandardCharsets.UTF_8);
    }

    private static Map<String, Object> fingerprint(Class<?> type) throws Exception {
        URL source = type.getProtectionDomain()
            .getCodeSource()
            .getLocation();
        while ("jar".equals(source.getProtocol()))
            source = ((JarURLConnection) source.openConnection()).getJarFileURL();
        Path location = Path.of(source.toURI())
            .toRealPath();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        long count = 0;
        if (Files.isDirectory(location)) {
            try (Stream<Path> stream = Files.walk(location)) {
                List<Path> files = stream.filter(Files::isRegularFile)
                    .sorted(
                        java.util.Comparator.comparing(
                            path -> location.relativize(path)
                                .toString()
                                .replace('\\', '/')))
                    .toList();
                for (Path file : files) {
                    digest.update(
                        location.relativize(file)
                            .toString()
                            .replace('\\', '/')
                            .getBytes(StandardCharsets.UTF_8));
                    digest.update((byte) 0);
                    digest.update(
                        ByteBuffer.allocate(Long.BYTES)
                            .putLong(Files.size(file))
                            .array());
                    update(digest, file);
                }
                count = files.size();
            }
        } else {
            update(digest, location);
            count = 1;
        }
        RuntimeReport.require(count > 0, "loaded code location cannot be empty: " + type.getName());
        Map<String, Object> result = new Object2ObjectLinkedOpenHashMap<>();
        result.put("location", location.toString());
        result.put("files", count);
        result.put(
            "sha256",
            HexFormat.of()
                .formatHex(digest.digest()));
        return result;
    }

    private static void update(MessageDigest digest, Path file) throws Exception {
        try (InputStream input = Files.newInputStream(file)) {
            byte[] buffer = new byte[65536];
            int count;
            while ((count = input.read(buffer)) != -1) digest.update(buffer, 0, count);
        }
    }
}
