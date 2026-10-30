package com.gtnllib.client.hud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import net.minecraft.client.Minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.gtnllib.GTNLLib;
import com.gtnllib.utils.MathUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class HudLayout {

    public static final float MIN_SCALE = 0.5F, MAX_SCALE = 3F, SCALE_STEP = 0.1F, MIN_OPACITY = 0.1F,
        MAX_IDLE_SECONDS = 30F, MIN_ALPHA = 0.01F;
    private static final String FILE = "config/" + GTNLLib.CONFIG_DIRECTORY + "/hud.json", ELEMENTS = "elements";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static final Map<String, Entry> ENTRIES = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<String, HudTiming.Tracker> TRACKERS = new Object2ObjectOpenHashMap<>();
    private static boolean loaded;
    private static BooleanSupplier shown = () -> true;
    private static DoubleSupplier globalScale = () -> 1;

    public record Placement(float x, float y, float scale, float opacity, float level, float awake, boolean resting,
        HudElement element, Entry entry) {

        public float alpha() {
            return opacity * level;
        }

        public int color() {
            HudColor mode = HudLayout.color(element, entry);
            return (mode == null ? HudColor.THEME : mode).rgb();
        }

        public String style() {
            return HudLayout.style(element, entry);
        }

        public boolean on(String option) {
            return HudLayout.on(element, entry, option);
        }

        public double number(String option) {
            return HudLayout.number(element, entry, option);
        }

        public String choice(String option) {
            return HudLayout.choice(element, entry, option);
        }
    }

    public static class Entry {

        private static final String MOVED = "moved", ANCHOR_X = "anchorX", ANCHOR_Y = "anchorY", X = "x", Y = "y",
            SCALE = "scale", HIDDEN = "hidden", OPACITY = "opacity", COLOR = "color", TIMING = "timing",
            IDLE_SECONDS = "idleSeconds", IDLE_ALPHA = "idleAlpha", STYLE = "style", OPTIONS = "options";
        private static final Set<String> KNOWN = new ObjectOpenHashSet<>(
            Arrays.asList(
                MOVED,
                ANCHOR_X,
                ANCHOR_Y,
                X,
                Y,
                SCALE,
                HIDDEN,
                OPACITY,
                COLOR,
                TIMING,
                IDLE_SECONDS,
                IDLE_ALPHA,
                STYLE,
                OPTIONS));

        public boolean moved, hidden;
        public float anchorX, anchorY, x, y, scale = 1F, opacity = 1F, idleSeconds = Float.NaN, idleAlpha = Float.NaN;
        public String color, timing, style;
        public final Map<String, JsonPrimitive> options = new Object2ObjectLinkedOpenHashMap<>();
        private final Map<String, JsonElement> unknown = new Object2ObjectLinkedOpenHashMap<>();

        public Entry copy() {
            Entry copy = new Entry();
            copy.moved = moved;
            copy.hidden = hidden;
            copy.anchorX = anchorX;
            copy.anchorY = anchorY;
            copy.x = x;
            copy.y = y;
            copy.scale = scale;
            copy.opacity = opacity;
            copy.idleSeconds = idleSeconds;
            copy.idleAlpha = idleAlpha;
            copy.color = color;
            copy.timing = timing;
            copy.style = style;
            copy.options.putAll(options);
            copy.unknown.putAll(unknown);
            return copy;
        }

        public JsonObject write() {
            JsonObject json = new JsonObject();
            json.addProperty(MOVED, moved);
            json.addProperty(ANCHOR_X, anchorX);
            json.addProperty(ANCHOR_Y, anchorY);
            json.addProperty(X, x);
            json.addProperty(Y, y);
            json.addProperty(SCALE, scale);
            json.addProperty(HIDDEN, hidden);
            json.addProperty(OPACITY, opacity);
            if (color != null) json.addProperty(COLOR, color);
            if (timing != null) json.addProperty(TIMING, timing);
            if (Float.isFinite(idleSeconds)) json.addProperty(IDLE_SECONDS, idleSeconds);
            if (Float.isFinite(idleAlpha)) json.addProperty(IDLE_ALPHA, idleAlpha);
            if (style != null) json.addProperty(STYLE, style);
            if (!options.isEmpty()) {
                JsonObject values = new JsonObject();
                for (Map.Entry<String, JsonPrimitive> option : options.entrySet())
                    values.add(option.getKey(), option.getValue());
                json.add(OPTIONS, values);
            }
            for (Map.Entry<String, JsonElement> field : unknown.entrySet()) json.add(field.getKey(), field.getValue());
            return json;
        }

        public static Entry read(JsonObject json) {
            Entry entry = new Entry();
            entry.anchorX = anchor(number(json, ANCHOR_X, 0.5F));
            entry.anchorY = anchor(number(json, ANCHOR_Y, 0.5F));
            entry.x = number(json, X, Float.NaN);
            entry.y = number(json, Y, Float.NaN);
            entry.moved = bool(json, MOVED) && Float.isFinite(entry.x) && Float.isFinite(entry.y);
            if (!entry.moved) entry.x = entry.y = 0F;
            entry.scale = clampScale(number(json, SCALE, 1F));
            entry.hidden = bool(json, HIDDEN);
            entry.opacity = clampOpacity(number(json, OPACITY, 1F));
            entry.color = text(json, COLOR);
            entry.timing = text(json, TIMING);
            entry.idleSeconds = clampIdleSeconds(number(json, IDLE_SECONDS, Float.NaN));
            entry.idleAlpha = clampIdleAlpha(number(json, IDLE_ALPHA, Float.NaN));
            entry.style = text(json, STYLE);
            JsonElement options = json.get(OPTIONS);
            if (options != null && options.isJsonObject())
                for (Map.Entry<String, JsonElement> option : options.getAsJsonObject()
                    .entrySet())
                    if (option.getValue()
                        .isJsonPrimitive())
                        entry.options.put(
                            option.getKey(),
                            option.getValue()
                                .getAsJsonPrimitive());
            for (Map.Entry<String, JsonElement> field : json.entrySet())
                if (!KNOWN.contains(field.getKey())) entry.unknown.put(field.getKey(), field.getValue());
            return entry;
        }

        private static JsonPrimitive primitive(JsonObject json, String key) {
            JsonElement element = json.get(key);
            return element != null && element.isJsonPrimitive() ? element.getAsJsonPrimitive() : null;
        }

        private static float number(JsonObject json, String key, float fallback) {
            JsonPrimitive value = primitive(json, key);
            return value != null && value.isNumber() ? value.getAsFloat() : fallback;
        }

        private static boolean bool(JsonObject json, String key) {
            JsonPrimitive value = primitive(json, key);
            return value != null && value.isBoolean() && value.getAsBoolean();
        }

        private static String text(JsonObject json, String key) {
            JsonPrimitive value = primitive(json, key);
            return value != null && value.isString() ? value.getAsString() : null;
        }
    }

    static String id(Enum<?> value) {
        return value.name()
            .toLowerCase(Locale.ROOT);
    }

    static <E extends Enum<E>> E byId(E[] values, String id, E fallback) {
        for (E value : values) if (id(value).equals(id)) return value;
        return fallback;
    }

    public static Entry entry(HudElement element) {
        if (!loaded) load();
        return ENTRIES.computeIfAbsent(element.id, key -> new Entry());
    }

    public static void settings(BooleanSupplier shown, DoubleSupplier globalScale) {
        HudLayout.shown = shown;
        HudLayout.globalScale = globalScale;
    }

    public static boolean enabled() {
        return shown.getAsBoolean();
    }

    public static Placement place(HudElement element, int width, int height) {
        if (!enabled()) return null;
        HudTiming.Tracker tracker = TRACKERS.computeIfAbsent(element.id, key -> new HudTiming.Tracker());
        if (!element.isInUse()) {
            tracker.reset();
            return null;
        }
        Entry entry = entry(element);
        if (entry.hidden) return null;
        HudTiming timing = timing(element, entry);
        float awake = tracker
            .awake(timing, now(), element.isActive(), element.signature(), idleSeconds(element, entry));
        float level = timing.level(awake, idleAlpha(element, entry));
        if (entry.opacity * level <= MIN_ALPHA) return null;
        return placement(element, entry, width, height, baseScale(element), level, awake, timing != HudTiming.ACTIVE);
    }

    public static Placement preview(HudElement element, int width, int height) {
        return placement(element, entry(element), width, height, baseScale(element), 1F, 1F, true);
    }

    public static Placement placement(HudElement element, Entry entry, int width, int height, float base) {
        return placement(element, entry, width, height, base, 1F, 1F, true);
    }

    private static Placement placement(HudElement element, Entry entry, int width, int height, float base, float level,
        float awake, boolean resting) {
        float scale = base * entry.scale;
        float x = entry.moved ? width * entry.anchorX + entry.x : width * element.anchorX + element.defaultX(scale);
        float y = entry.moved ? height * entry.anchorY + entry.y : height * element.anchorY + element.defaultY(scale);
        return new Placement(
            clampX(element, x, scale, width),
            clampY(element, y, scale, height),
            scale,
            entry.opacity,
            level,
            awake,
            resting,
            element,
            entry);
    }

    private static float baseScale(HudElement element) {
        return element.baseScale() * HudElement.validScale((float) globalScale.getAsDouble());
    }

    private static long now() {
        return System.nanoTime() / 1_000_000L;
    }

    public static boolean hidden(HudElement element) {
        return entry(element).hidden;
    }

    public static float scale(HudElement element) {
        return entry(element).scale;
    }

    public static float opacity(HudElement element) {
        return entry(element).opacity;
    }

    public static HudColor color(HudElement element) {
        return color(element, entry(element));
    }

    public static HudTiming timing(HudElement element) {
        return timing(element, entry(element));
    }

    public static float idleSeconds(HudElement element) {
        return idleSeconds(element, entry(element));
    }

    public static float idleAlpha(HudElement element) {
        return idleAlpha(element, entry(element));
    }

    public static String style(HudElement element) {
        return style(element, entry(element));
    }

    public static boolean on(HudElement element, String option) {
        return on(element, entry(element), option);
    }

    public static double number(HudElement element, String option) {
        return number(element, entry(element), option);
    }

    public static String choice(HudElement element, String option) {
        return choice(element, entry(element), option);
    }

    private static HudColor color(HudElement element, Entry entry) {
        return element.colorable() ? byId(HudColor.values(), entry.color, element.defaultColor()) : null;
    }

    private static HudTiming timing(HudElement element, Entry entry) {
        return byId(HudTiming.values(), entry.timing, element.defaultTiming());
    }

    private static float idleSeconds(HudElement element, Entry entry) {
        return Float.isFinite(entry.idleSeconds) ? entry.idleSeconds : element.defaultIdleSeconds();
    }

    private static float idleAlpha(HudElement element, Entry entry) {
        return Float.isFinite(entry.idleAlpha) ? entry.idleAlpha : element.defaultIdleAlpha();
    }

    private static String style(HudElement element, Entry entry) {
        return element.style(entry.style)
            .id();
    }

    private static HudOption option(HudElement element, String id, HudOption.Kind kind) {
        HudOption option = element.option(id);
        if (option.kind != kind) throw new IllegalArgumentException("HUD option " + id + " is not a " + kind);
        return option;
    }

    private static boolean on(HudElement element, Entry entry, String id) {
        return option(element, id, HudOption.Kind.TOGGLE).on(entry.options.get(id));
    }

    private static double number(HudElement element, Entry entry, String id) {
        return option(element, id, HudOption.Kind.NUMBER).number(entry.options.get(id));
    }

    private static String choice(HudElement element, Entry entry, String id) {
        return option(element, id, HudOption.Kind.CHOICE).choice(entry.options.get(id));
    }

    public static void move(HudElement element, float x, float y, int width, int height) {
        move(entry(element), x, y, width, height);
    }

    public static void rescale(HudElement element, int steps) {
        setScale(element, entry(element).scale + steps * SCALE_STEP);
    }

    public static void setScale(HudElement element, float scale) {
        entry(element).scale = clampScale(Math.round(scale * 10) / 10F);
    }

    public static void toggle(HudElement element) {
        Entry entry = entry(element);
        entry.hidden = !entry.hidden;
    }

    public static void setOpacity(HudElement element, float opacity) {
        entry(element).opacity = clampOpacity(opacity);
    }

    public static void setColor(HudElement element, HudColor color) {
        if (element.colorable()) entry(element).color = id(color);
    }

    public static void setTiming(HudElement element, HudTiming timing) {
        entry(element).timing = id(timing);
    }

    public static void setIdleSeconds(HudElement element, float seconds) {
        entry(element).idleSeconds = clampIdleSeconds(seconds);
    }

    public static void setIdleAlpha(HudElement element, float alpha) {
        entry(element).idleAlpha = clampIdleAlpha(alpha);
    }

    public static void setStyle(HudElement element, String style) {
        entry(element).style = element.style(style)
            .id();
    }

    public static void setOn(HudElement element, String option, boolean on) {
        option(element, option, HudOption.Kind.TOGGLE);
        entry(element).options.put(option, new JsonPrimitive(on));
    }

    public static void setNumber(HudElement element, String option, double value) {
        entry(element).options
            .put(option, new JsonPrimitive(option(element, option, HudOption.Kind.NUMBER).snap(value)));
    }

    public static void setChoice(HudElement element, String option, String choice) {
        entry(element).options.put(
            option,
            new JsonPrimitive(option(element, option, HudOption.Kind.CHOICE).choice(new JsonPrimitive(choice))));
    }

    public static void resetElement(HudElement element) {
        if (!loaded) load();
        ENTRIES.remove(element.id);
    }

    public static void reset() {
        ENTRIES.clear();
        loaded = true;
    }

    public static Map<String, Entry> snapshot() {
        if (!loaded) load();
        Map<String, Entry> copy = new Object2ObjectLinkedOpenHashMap<>();
        for (Map.Entry<String, Entry> pair : ENTRIES.entrySet()) copy.put(
            pair.getKey(),
            pair.getValue()
                .copy());
        return copy;
    }

    public static void restore(Map<String, Entry> snapshot) {
        ENTRIES.clear();
        for (Map.Entry<String, Entry> pair : snapshot.entrySet()) ENTRIES.put(
            pair.getKey(),
            pair.getValue()
                .copy());
        loaded = true;
    }

    public static void move(Entry entry, float x, float y, int width, int height) {
        entry.anchorX = anchor(x, width);
        entry.anchorY = anchor(y, height);
        entry.x = x - width * entry.anchorX;
        entry.y = y - height * entry.anchorY;
        entry.moved = true;
    }

    public static float anchor(float position, int size) {
        return position < size / 3F ? 0F : position > size * 2 / 3F ? 1F : 0.5F;
    }

    private static float anchor(float value) {
        return value == 0F || value == 1F ? value : 0.5F;
    }

    public static float clampScale(float scale) {
        return Float.isFinite(scale) ? Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale)) : 1F;
    }

    public static float clampOpacity(float opacity) {
        return Float.isFinite(opacity) ? Math.max(MIN_OPACITY, Math.min(1F, opacity)) : 1F;
    }

    private static float clampIdleSeconds(float seconds) {
        return Float.isFinite(seconds) ? Math.max(0F, Math.min(MAX_IDLE_SECONDS, seconds)) : Float.NaN;
    }

    private static float clampIdleAlpha(float alpha) {
        return MathUtils.clampUnit(alpha, Float.NaN);
    }

    static float clampX(HudElement element, float x, float scale, int width) {
        return clamp(x, -element.minX * scale, width - element.maxX * scale);
    }

    static float clampY(HudElement element, float y, float scale, int height) {
        return clamp(y, -element.minY * scale, height - element.maxY * scale);
    }

    private static float clamp(float value, float low, float high) {
        return low > high ? (low + high) / 2F : Math.max(low, Math.min(high, value));
    }

    public static void save() {
        save(file());
    }

    public static void save(File file) {
        File temp = new File(file.getPath() + ".tmp");
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs())
                throw new IOException("Cannot create " + parent);
            try (Writer out = new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8)) {
                GSON.toJson(write(), out);
            }
            replace(temp, file);
        } catch (IOException | JsonIOException exception) {
            GTNLLib.LOG.warn("Could not save the HUD layout to {}", file, exception);
        }
    }

    private static void replace(File from, File to) throws IOException {
        try {
            Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException unsupported) {
            Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void load() {
        loaded = true;
        File file = file();
        if (!file.isFile()) return;
        try (Reader in = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonElement root = new JsonParser().parse(in);
            if (root.isJsonObject()) read(root.getAsJsonObject());
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            GTNLLib.LOG.warn("Could not read the HUD layout from {}", file, exception);
        }
    }

    private static File file() {
        return new File(Minecraft.getMinecraft().mcDataDir, FILE);
    }

    public static JsonObject write() {
        if (!loaded) load();
        JsonObject elements = new JsonObject();
        for (Map.Entry<String, Entry> pair : ENTRIES.entrySet()) elements.add(
            pair.getKey(),
            pair.getValue()
                .write());
        JsonObject root = new JsonObject();
        root.add(ELEMENTS, elements);
        return root;
    }

    public static void read(JsonObject root) {
        ENTRIES.clear();
        loaded = true;
        JsonElement elements = root.get(ELEMENTS);
        if (elements == null || !elements.isJsonObject()) return;
        for (Map.Entry<String, JsonElement> pair : elements.getAsJsonObject()
            .entrySet())
            if (pair.getValue()
                .isJsonObject())
                ENTRIES.put(
                    pair.getKey(),
                    Entry.read(
                        pair.getValue()
                            .getAsJsonObject()));
    }
}
