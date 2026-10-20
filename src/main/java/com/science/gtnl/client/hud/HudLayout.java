package com.science.gtnl.client.hud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import net.minecraft.client.Minecraft;

import com.science.gtnl.ScienceNotLeisure;

public class HudLayout {

    public static final float MIN_SCALE = 0.5F, MAX_SCALE = 3F, SCALE_STEP = 0.1F;
    private static final String FILE = "config/GTNotLeisure/celestweave_hud.properties";
    private static final Map<String, Entry> ENTRIES = new LinkedHashMap<>();
    private static boolean loaded;

    private HudLayout() {}

    public record Placement(float x, float y, float scale) {}

    public static class Entry {

        public boolean moved, hidden;
        public float anchorX, anchorY, x, y, scale = 1F;
    }

    public static Entry entry(HudElement element) {
        if (!loaded) load();
        return ENTRIES.computeIfAbsent(element.id, key -> new Entry());
    }

    public static Placement place(HudElement element, int width, int height) {
        if (!element.isVisible()) return null;
        Entry entry = entry(element);
        return entry.hidden ? null : placement(element, entry, width, height, element.baseScale());
    }

    public static Placement preview(HudElement element, int width, int height) {
        return placement(element, entry(element), width, height, element.baseScale());
    }

    public static boolean hidden(HudElement element) {
        return entry(element).hidden;
    }

    public static float scale(HudElement element) {
        return entry(element).scale;
    }

    public static void move(HudElement element, float x, float y, int width, int height) {
        move(entry(element), x, y, width, height);
    }

    public static void rescale(HudElement element, int steps) {
        Entry entry = entry(element);
        entry.scale = clampScale(Math.round((entry.scale + steps * SCALE_STEP) * 10) / 10F);
    }

    public static void toggle(HudElement element) {
        Entry entry = entry(element);
        entry.hidden = !entry.hidden;
    }

    public static void reset() {
        ENTRIES.clear();
        loaded = true;
    }

    public static Placement placement(HudElement element, Entry entry, int width, int height, float base) {
        float scale = base * entry.scale;
        float x = entry.moved ? width * entry.anchorX + entry.x : width * element.anchorX + element.defaultX(scale);
        float y = entry.moved ? height * entry.anchorY + entry.y : height * element.anchorY + element.defaultY(scale);
        return new Placement(
            clamp(x, -element.minX * scale, width - element.maxX * scale),
            clamp(y, -element.minY * scale, height - element.maxY * scale),
            scale);
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

    public static float clampScale(float scale) {
        return Float.isFinite(scale) ? Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale)) : 1F;
    }

    static float clamp(float value, float low, float high) {
        return low > high ? (low + high) / 2F : Math.max(low, Math.min(high, value));
    }

    public static void save() {
        Properties properties = new Properties();
        write(properties);
        File file = file();
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs())
                throw new IOException("Cannot create " + parent);
            try (OutputStream out = new FileOutputStream(file)) {
                properties.store(out, "HUD layout");
            }
        } catch (IOException exception) {
            ScienceNotLeisure.LOG.warn("Could not save the HUD layout to {}", file, exception);
        }
    }

    private static void load() {
        loaded = true;
        File file = file();
        if (!file.isFile()) return;
        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            properties.load(in);
            read(properties);
        } catch (IOException | IllegalArgumentException exception) {
            ScienceNotLeisure.LOG.warn("Could not read the HUD layout from {}", file, exception);
        }
    }

    private static File file() {
        return new File(Minecraft.getMinecraft().mcDataDir, FILE);
    }

    public static void write(Properties properties) {
        for (Map.Entry<String, Entry> pair : ENTRIES.entrySet()) {
            String id = pair.getKey();
            Entry entry = pair.getValue();
            properties.setProperty(id + ".moved", String.valueOf(entry.moved));
            properties.setProperty(id + ".anchorX", String.valueOf(entry.anchorX));
            properties.setProperty(id + ".anchorY", String.valueOf(entry.anchorY));
            properties.setProperty(id + ".x", String.valueOf(entry.x));
            properties.setProperty(id + ".y", String.valueOf(entry.y));
            properties.setProperty(id + ".scale", String.valueOf(entry.scale));
            properties.setProperty(id + ".hidden", String.valueOf(entry.hidden));
        }
    }

    public static void read(Properties properties) {
        ENTRIES.clear();
        for (String key : properties.stringPropertyNames()) {
            int dot = key.lastIndexOf('.');
            if (dot <= 0 || ENTRIES.containsKey(key.substring(0, dot))) continue;
            String id = key.substring(0, dot);
            Entry entry = new Entry();
            entry.anchorX = anchorValue(properties.getProperty(id + ".anchorX"));
            entry.anchorY = anchorValue(properties.getProperty(id + ".anchorY"));
            entry.x = number(properties.getProperty(id + ".x"), Float.NaN);
            entry.y = number(properties.getProperty(id + ".y"), Float.NaN);
            entry.moved = Boolean.parseBoolean(properties.getProperty(id + ".moved")) && Float.isFinite(entry.x)
                && Float.isFinite(entry.y);
            if (!entry.moved) entry.x = entry.y = 0F;
            entry.scale = clampScale(number(properties.getProperty(id + ".scale"), 1F));
            entry.hidden = Boolean.parseBoolean(properties.getProperty(id + ".hidden"));
            ENTRIES.put(id, entry);
        }
    }

    private static float anchorValue(String text) {
        float value = number(text, 0.5F);
        return value == 0F || value == 1F ? value : 0.5F;
    }

    private static float number(String text, float fallback) {
        if (text == null) return fallback;
        try {
            return Float.parseFloat(text.trim());
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }
}
