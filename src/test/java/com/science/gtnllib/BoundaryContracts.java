package com.science.gtnllib;

import static com.science.gtnllib.GTNLLibContractTest.RESOURCES;
import static com.science.gtnllib.GTNLLibContractTest.SOURCES;
import static com.science.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnllib.GTNLLibContractTest.read;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.science.gtnllib.client.hud.HudColor;
import com.science.gtnllib.client.hud.HudElement;
import com.science.gtnllib.client.hud.HudTiming;
import com.science.gtnllib.config.GTNLLibConfig;

public class BoundaryContracts {

    private static final Pattern HOST_IMPORT = Pattern
        .compile("(?m)^\\s*import\\s+(?:static\\s+)?(?:com\\.science\\.gtnl\\.|gregtech\\.)");
    private static final Pattern FEATURE = Pattern.compile("Celestweave|Railgun|Equipment|celestweave|railgun");
    private static final Pattern NAMESPACED = Pattern.compile("\"([a-z0-9_.-]+):[a-z0-9_./-]+\"");
    private static final Pattern LOCATION_DOMAIN = Pattern.compile("new ResourceLocation\\(\\s*\"([^\"]*)\"\\s*,");
    private static final Pattern HOST_LITERAL = Pattern.compile("\"(?:gtnl\\.|gtnl:|sciencenotleisure)");
    private static final Pattern LANG_LITERAL = Pattern.compile("\"(gtnllib\\.[A-Za-z0-9_.]*)\"");
    private static final Pattern INCLUDE = Pattern.compile("#include \"([^\"]+)\"");
    private static final int MIN_SOURCES = 40;

    private BoundaryContracts() {}

    static void run() throws IOException {
        Set<String> used = new TreeSet<>();
        sources(used);
        resources();
        lang(used);
    }

    private static List<Path> files(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                .sorted()
                .collect(Collectors.toList());
        }
    }

    private static void sources(Set<String> used) throws IOException {
        List<Path> files = files(SOURCES);
        check(files.size() >= MIN_SOURCES, "the boundary scan sees every GTNLLib source, found " + files.size());
        for (Path file : files) {
            String source = read(file), name = SOURCES.relativize(file)
                .toString()
                .replace('\\', '/');
            check(
                !HOST_IMPORT.matcher(source)
                    .find(),
                name + " imports nothing from GTNL or GregTech");
            check(
                !FEATURE.matcher(source)
                    .find(),
                name + " names no feature");
            check(
                !source.toUpperCase(Locale.ROOT)
                    .contains("3D9A"),
                name + " has no hard-coded equipment pink");
            check(
                !HOST_LITERAL.matcher(source)
                    .find(),
                name + " uses no GTNL lang key or resource namespace");
            Matcher namespaced = NAMESPACED.matcher(source);
            while (namespaced.find()) check(
                GTNLLib.MODID.equals(namespaced.group(1)),
                name + " refers to " + namespaced.group() + " outside the gtnllib namespace");
            Matcher domain = LOCATION_DOMAIN.matcher(source);
            while (domain.find()) check(
                GTNLLib.MODID.equals(domain.group(1)),
                name + " loads a resource from " + domain.group(1) + " instead of gtnllib");
            Matcher lang = LANG_LITERAL.matcher(source);
            while (lang.find()) if (!lang.group(1)
                .endsWith(".")) used.add(lang.group(1));
        }
    }

    private static void resources() throws IOException {
        for (Path file : files(RESOURCES)) {
            String name = RESOURCES.relativize(file)
                .toString()
                .replace('\\', '/');
            boolean lang = name.endsWith(".lang"), shader = name.endsWith(".glsl");
            check(lang || shader, name + " is a GTNLLib lang file or shader");
            if (lang) for (String key : langKeys(file))
                check(key.startsWith(GTNLLib.MODID + "."), name + " key " + key + " starts with the gtnllib prefix");
            if (shader) {
                Matcher include = INCLUDE.matcher(read(file));
                while (include.find()) check(
                    include.group(1)
                        .indexOf(':') < 0 || include.group(1)
                            .startsWith(GTNLLib.MODID + ":"),
                    name + " includes " + include.group(1) + " from the gtnllib namespace");
            }
        }
    }

    private static void lang(Set<String> used) throws IOException {
        Set<String> expected = new TreeSet<>(used);
        for (String key : new String[] { "open", "help", "reset", "done", "hidden", "show_all", "in_use_only", "undo",
            "redo", "reset_element", "empty" }) expected.add("gtnllib.hud.editor." + key);
        for (HudElement.Setting setting : HudElement.Setting.values()) expected.add(setting.lang());
        for (HudTiming timing : HudTiming.values()) expected.add(timing.lang());
        for (HudColor color : HudColor.values()) expected.add(color.lang());
        expected.add(HudElement.STANDARD_STYLE.lang());
        String category = "gtnllib.gui.config." + GTNLLibConfig.CATEGORY;
        expected.add(category);
        expected.add(category + ".tooltip");
        for (Field field : GTNLLibConfig.class.getFields()) {
            Config.LangKey key = field.getAnnotation(Config.LangKey.class);
            if (key == null) continue;
            expected.add(key.value());
            expected.add(key.value() + ".tooltip");
        }
        Set<String> english = langKeys(RESOURCES.resolve("lang/en_US.lang")),
            chinese = langKeys(RESOURCES.resolve("lang/zh_CN.lang"));
        for (String key : expected) {
            check(english.contains(key), "GTNLLib en_US missing " + key);
            check(chinese.contains(key), "GTNLLib zh_CN missing " + key);
        }
        for (String key : english) check(chinese.contains(key), "GTNLLib zh_CN missing " + key);
        for (String key : chinese) check(english.contains(key), "GTNLLib en_US missing " + key);
    }

    private static Set<String> langKeys(Path file) throws IOException {
        Set<String> keys = new HashSet<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            int split = line.indexOf('=');
            if (split > 0 && !line.startsWith("#")) keys.add(
                line.substring(0, split)
                    .trim());
        }
        return keys;
    }
}
