package com.gtnllib;

import static com.gtnllib.GTNLLibContractTest.RESOURCES;
import static com.gtnllib.GTNLLibContractTest.SOURCES;
import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.read;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnllib.client.hud.HudColor;
import com.gtnllib.client.hud.HudElement;
import com.gtnllib.client.hud.HudTiming;
import com.gtnllib.config.GTNLLibConfig;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class BoundaryContracts {

    private static final Pattern HOST_REFERENCE = Pattern.compile("\\bcom\\.science\\.|\\bgregtech\\.[a-z]");
    private static final Pattern FEATURE = Pattern.compile("Celestweave|Railgun|Equipment|celestweave|railgun");
    private static final Pattern NAMESPACED = Pattern.compile("\"([a-z0-9_.-]+):[a-z0-9_./-]+\"");
    private static final Pattern LOCATION_DOMAIN = Pattern.compile("new ResourceLocation\\(\\s*\"([^\"]*)\"\\s*,");
    private static final Pattern HOST_LITERAL = Pattern.compile("\"(?:gtnl\\.|gtnl:|sciencenotleisure)");
    private static final Pattern LANG_LITERAL = Pattern.compile("\"(gtnllib\\.[A-Za-z0-9_.]*)\"");
    private static final Pattern LEGACY_LANG_LITERAL = Pattern.compile("\"(gtnl\\.[A-Za-z0-9_.]*)\"");
    // Moved from GTNL with their keys: modpack translations override by key, so a new name would show English.
    private static final Set<String> LEGACY_LANG_KEYS = legacyLangKeys();
    private static final Pattern EMPTY_PRIVATE_CONSTRUCTOR = Pattern
        .compile("^[ \\t]*private[ \\t]+\\w+\\(\\)[ \\t]*\\{[ \\t]*\\}", Pattern.MULTILINE);
    private static final Pattern INCLUDE = Pattern.compile("#include \"([^\"]+)\"");
    private static final int MIN_SOURCES = 40;

    private static final Pattern LIB_IMPORT = Pattern
        .compile("^import (?:static )?com\\.gtnllib\\.((?:[a-z]\\w*\\.)*[a-z]\\w*)\\.[A-Z]", Pattern.MULTILINE);

    static void run() throws IOException {
        Set<String> used = new TreeSet<>();
        sources(used);
        resources();
        lang(used);
        packages();
    }

    private static void packages() throws IOException {
        Map<String, Set<String>> imports = new TreeMap<>();
        Set<String> configReaders = new TreeSet<>();
        for (Path file : files(SOURCES)) {
            if (!file.toString()
                .endsWith(".java")) continue;
            String from = packageOf(SOURCES.relativize(file.getParent()));
            Set<String> targets = imports.computeIfAbsent(from, key -> new TreeSet<>());
            Matcher matcher = LIB_IMPORT.matcher(read(file));
            while (matcher.find()) {
                String to = matcher.group(1)
                    .replace('.', '/');
                if (!to.equals(from)) targets.add(to);
                if (to.equals("config") && !from.equals("config")) configReaders.add(
                    file.getFileName()
                        .toString());
            }
        }
        check(
            imports.getOrDefault("gui/theme", Collections.emptySet())
                .contains("gui/widget"),
            "the package scan sees the imports between GTNLLib packages, so the cycle check has something to check");
        for (String start : imports.keySet()) {
            List<String> cycle = cycleFrom(start, start, imports, new ObjectArrayList<>(), new ObjectOpenHashSet<>());
            check(
                cycle == null,
                "GTNLLib package " + start + " is not part of an import cycle" + (cycle == null ? "" : ": " + cycle));
        }
        check(
            imports.getOrDefault("registry", Collections.emptySet())
                .isEmpty(),
            "the registry package sits at the bottom and imports no other GTNLLib package: " + imports.get("registry"));
        check(
            configReaders.equals(Collections.singleton("GTNLLibClient.java")),
            "only the GTNLLib client entry point reads the config and hands it to themes and the HUD: "
                + configReaders);
    }

    private static String packageOf(Path relative) {
        String name = relative.toString()
            .replace('\\', '/');
        return name.isEmpty() ? "root" : name;
    }

    private static List<String> cycleFrom(String start, String at, Map<String, Set<String>> imports, List<String> path,
        Set<String> seen) {
        path.add(at);
        for (String next : imports.getOrDefault(at, Collections.emptySet())) {
            if (next.equals(start)) {
                List<String> cycle = new ObjectArrayList<>(path);
                cycle.add(start);
                return cycle;
            }
            if (!seen.add(next)) continue;
            List<String> found = cycleFrom(start, next, imports, path, seen);
            if (found != null) return found;
        }
        path.remove(path.size() - 1);
        return null;
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
                !HOST_REFERENCE.matcher(source)
                    .find(),
                name + " references nothing from GTNL or GregTech, neither by import nor by full name");
            check(
                !FEATURE.matcher(source)
                    .find(),
                name + " names no feature");
            check(
                !source.toUpperCase(Locale.ROOT)
                    .contains("3D9A"),
                name + " has no hard-coded equipment pink");
            Matcher legacy = LEGACY_LANG_LITERAL.matcher(source);
            while (legacy.find()) {
                check(LEGACY_LANG_KEYS.contains(legacy.group(1)), name + " uses the GTNL lang key " + legacy.group(1));
                used.add(legacy.group(1));
            }
            check(
                !HOST_LITERAL.matcher(
                    LEGACY_LANG_LITERAL.matcher(source)
                        .replaceAll("\"\""))
                    .find(),
                name + " uses no GTNL lang key or resource namespace");
            check(
                !EMPTY_PRIVATE_CONSTRUCTOR.matcher(source)
                    .find(),
                name + " declares an empty private constructor; a class with only static members needs none");
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
            boolean lang = name.endsWith(".lang"), shader = name.endsWith(".glsl"),
                texture = name.startsWith("textures/") && name.endsWith(".png");
            check(lang || shader || texture, name + " is a GTNLLib lang file, shader or texture");
            if (lang) for (String key : langKeys(file)) check(
                key.startsWith(GTNLLib.MODID + ".") || LEGACY_LANG_KEYS.contains(key),
                name + " key " + key + " starts with the gtnllib prefix");
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

    private static Set<String> legacyLangKeys() {
        Set<String> keys = new TreeSet<>();
        String setting = "gtnl.gui.config.general.client.enable_text_effects";
        keys.add(setting);
        keys.add(setting + ".tooltip");
        for (String key : new String[] { "title", "palette", "bold", "italic" }) keys.add("gtnl.text_effect." + key);
        keys.add("gtnl.tooltip.press_shift");
        keys.add("gtnl.tooltip.press_ctrl");
        for (String key : new String[] { "usage", "player_only", "no_item", "header", "display_name",
            "item_stack_display_name", "unlocalized_inefficient_name", "unlocalized_name",
            "item_stack_unlocalized_name", "class", "id", "stack_size", "damage", "max_damage", "nbt_data", "nbt_entry",
            "no_nbt_data" }) keys.add("gtnl.command.item_info." + key);
        return Collections.unmodifiableSet(keys);
    }

    private static Set<String> langKeys(Path file) throws IOException {
        Set<String> keys = new ObjectOpenHashSet<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            int split = line.indexOf('=');
            if (split > 0 && !line.startsWith("#")) keys.add(
                line.substring(0, split)
                    .trim());
        }
        return keys;
    }
}
