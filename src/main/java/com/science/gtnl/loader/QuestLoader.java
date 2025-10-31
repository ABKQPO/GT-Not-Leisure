package com.science.gtnl.loader;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import gregtech.api.enums.Mods;

public class QuestLoader {

    private static final File CONFIG_ORDER_FILE = new File(
        "config/" + Mods.BetterQuesting.ID + "/DefaultQuests/QuestLinesOrder.txt");
    private static final File CONFIG_QUESTS_DIR = new File("config/" + Mods.BetterQuesting.ID + "/DefaultQuests");
    private static final String RESOURCE_ORDER_PATH = "/assets/" + RESOURCE_ROOT_ID + "/quest/QuestLinesOrder.txt";
    private static final String RESOURCE_QUESTS_PREFIX = "assets/" + RESOURCE_ROOT_ID + "/quest/DefaultQuests/";
    private static final File CONFIG_LANG_FILE = new File(
        "config/txloader/load/" + Mods.BetterQuesting.ID + "/lang/zh_CN.lang");
    private static final String RESOURCE_LANG_PATH = "/assets/" + RESOURCE_ROOT_ID + "/quest/lang/zh_CN.lang";

    public static void registry() {
        try {
            syncQuestLinesOrder();
            copyDefaultQuestsFromJar();
            mergeLangFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void syncQuestLinesOrder() throws IOException {
        List<String> lines = readFileLines();
        if (lines.isEmpty()) {
            lines = readResourceLines();
        }

        boolean modified = false;

        String lineSteamAge = "AAAAAAAAAAAAAAAAAAAAAg==";
        String lineSuperheated = "GTNotLeisure75SteamAge==: Tier 0.75 - Superheated";
        String lineSupercritical = "GTNotLeisure99SteamAge==: Tier 0.999... - Supercritical";
        String lineGTNL = "GTNotLeisureQuestsLine==: GTNotLeisure";

        int index = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i)
                .trim()
                .contains(lineSteamAge)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            List<String> insertList = new ArrayList<>();
            if (!lines.contains(lineSuperheated)) insertList.add(lineSuperheated);
            if (!lines.contains(lineSupercritical)) insertList.add(lineSupercritical);

            if (!insertList.isEmpty()) {
                lines.addAll(index + 1, insertList);
                modified = true;
                System.out.println("[QuestLoader] Inserted GTNotLeisure SteamAge tiers after " + lineSteamAge);
            }
        } else {
            System.err.println("[QuestLoader] Match line not found: " + lineSteamAge);
        }

        if (!lines.contains(lineGTNL)) {
            lines.add(lineGTNL);
            modified = true;
            System.out.println("[QuestLoader] Appended GTNotLeisureQuestsLine at the end.");
        }

        if (modified) {
            writeFileLines(lines);
            System.out.println("[QuestLoader] QuestLinesOrder.txt updated successfully.");
        } else {
            System.out.println("[QuestLoader] QuestLinesOrder.txt is already up-to-date.");
        }
    }

    private static void copyDefaultQuestsFromJar() throws IOException {
        String path = Objects.requireNonNull(
            QuestLoader.class.getResource(
                "/" + QuestLoader.class.getName()
                    .replace('.', '/') + ".class"))
            .toString();

        if (!path.startsWith("jar:file:")) {
            System.out.println("[QuestLoader] Not running from a jar file: " + path);
            return;
        }

        String jarPath = path.substring("jar:file:".length(), path.indexOf("!"));
        File jarFile = new File(jarPath);

        if (!jarFile.exists() || !jarFile.getName()
            .endsWith(".jar")) {
            System.out.println("[QuestLoader] Resolved jar path not found: " + jarPath);
            return;
        }

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;

                String name = entry.getName();
                if (!name.startsWith(RESOURCE_QUESTS_PREFIX)) continue;

                String relativePath = name.substring(RESOURCE_QUESTS_PREFIX.length());
                File targetFile = new File(CONFIG_QUESTS_DIR, relativePath);

                boolean shouldCopy = true;

                if (targetFile.exists()) {
                    try (InputStream compareStream = jar.getInputStream(entry)) {
                        if (compareFileContent(compareStream, targetFile)) {
                            shouldCopy = false;
                        }
                    }
                }

                if (shouldCopy) {
                    targetFile.getParentFile()
                        .mkdirs();
                    try (InputStream freshStream = jar.getInputStream(entry);
                        OutputStream out = new FileOutputStream(targetFile)) {
                        copyStream(freshStream, out);
                        System.out.println("[QuestLoader] Copied/Updated: " + targetFile.getName());
                    }
                }
            }
        }
    }

    private static boolean compareFileContent(InputStream in1, File file2) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash1;
            try (DigestInputStream dis1 = new DigestInputStream(in1, digest)) {
                while (dis1.read() != -1) {}
                hash1 = digest.digest();
            }

            digest.reset();

            byte[] hash2;
            try (InputStream fis = new FileInputStream(file2);
                DigestInputStream dis2 = new DigestInputStream(fis, digest)) {
                while (dis2.read() != -1) {}
                hash2 = digest.digest();
            }

            return Arrays.equals(hash1, hash2);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 algorithm not available", e);
        }
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    private static List<String> readResourceLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (InputStream is = QuestLoader.class.getResourceAsStream(RESOURCE_ORDER_PATH)) {
            if (is == null) throw new FileNotFoundException("Missing resource: " + RESOURCE_ORDER_PATH);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) lines.add(line.trim());
            }
        }
        return lines;
    }

    private static List<String> readFileLines() throws IOException {
        List<String> lines = new ArrayList<>();
        if (!CONFIG_ORDER_FILE.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(CONFIG_ORDER_FILE), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) lines.add(line.trim());
        }
        return lines;
    }

    private static void writeFileLines(List<String> lines) throws IOException {
        CONFIG_ORDER_FILE.getParentFile()
            .mkdirs();
        try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(CONFIG_ORDER_FILE), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static void mergeLangFile() throws IOException {
        Set<String> configSet = new HashSet<>();
        List<String> configLines = new ArrayList<>();

        if (CONFIG_LANG_FILE.exists()) {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(CONFIG_LANG_FILE), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    configLines.add(line);
                    configSet.add(line.trim());
                }
            }
        }

        InputStream is = QuestLoader.class.getResourceAsStream(RESOURCE_LANG_PATH);
        if (is == null) {
            System.err.println("[QuestLoader] Missing lang resource: " + RESOURCE_LANG_PATH);
            return;
        }

        List<String> newLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!configSet.contains(line)) {
                    configLines.add(line);
                    newLines.add(line);
                }
            }
        }

        if (!newLines.isEmpty()) {
            CONFIG_LANG_FILE.getParentFile()
                .mkdirs();
            try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(CONFIG_LANG_FILE), StandardCharsets.UTF_8))) {
                for (String line : configLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("[QuestLoader] zh_CN.lang updated with " + newLines.size() + " new lines.");
        } else {
            System.out.println("[QuestLoader] zh_CN.lang is up-to-date.");
        }
    }
}
