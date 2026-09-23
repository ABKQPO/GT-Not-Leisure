package com.science.gtnl.utils.text.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.science.gtnl.ScienceNotLeisure;

/** Client-independent syntax and native formatting rules for effect text. */
public class TextEffectFormat {

    public static final String INLINE_OPEN = "\u00a7{";
    public static final String AMP_INLINE_OPEN = "&{";
    public static final int MAX_HEADER_LENGTH = 512;
    private static final Map<String, String> ALIASES = new HashMap<>(
        Map.ofEntries(
            Map.entry("ir", "infernum_red_rarity"),
            Map.entry("gc", "genesis_component_rarity_shader"),
            Map.entry("pc", "pulse_circle"),
            Map.entry("nb", "nameless_boss_bar_shader"),
            Map.entry("pu", "pulse_upwards"),
            Map.entry("cr", "calamity_red"),
            Map.entry("er", "exotic_rainbow"),
            Map.entry("sb", "superboss_rarity"),
            Map.entry("is", "infernum_spark_rarity"),
            Map.entry("kcs", "infernum_cyan_spark"),
            Map.entry("ba", "burnished_auric"),
            Map.entry("ec", "evercold_cyan"),
            Map.entry("ss", "starsilver_rarity"),
            Map.entry("nr", "nebula_rift"),
            Map.entry("ps", "prismatic_scan"),
            Map.entry("qg", "quantum_glitch"),
            Map.entry("mc", "molten_core")));
    private static final int[] VANILLA_COLORS = { 0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00,
        0xAAAAAA, 0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF };

    private TextEffectFormat() {}

    public static void registerAlias(String alias, String rendererId) {
        if (alias == null || !alias.matches("[a-z][a-z0-9_]{0,15}"))
            throw new IllegalArgumentException("Invalid text effect alias");
        new TextEffectStyle(rendererId, List.of(), 1);
        if (ALIASES.putIfAbsent(alias, rendererId) != null)
            throw new IllegalArgumentException("Text effect alias already registered: " + alias);
    }

    public static String aliasFor(String rendererId) {
        String shortest = "";
        for (Entry<String, String> entry : ALIASES.entrySet()) {
            String target = entry.getValue();
            if (target.indexOf(':') < 0) target = ScienceNotLeisure.MODID + ":" + target;
            String alias = entry.getKey();
            if (target.equals(rendererId) && (shortest.isEmpty() || alias.length() < shortest.length()
                || alias.length() == shortest.length() && alias.compareTo(shortest) < 0)) shortest = alias;
        }
        return shortest;
    }

    public static String normalizeAmpersand(String token) {
        if (token.length() < 2 || token.charAt(0) != '&') return token;
        char code = Character.toLowerCase(token.charAt(1));
        if (token.length() == 8 && code == '#') return hexToken(token.substring(2));
        if (token.length() == 18 && code == 'g')
            return "\u00a7g" + hexToken(token.substring(4, 10)) + hexToken(token.substring(12));
        if (token.length() == 10 && code == 'u') return "\u00a7u" + hexToken(token.substring(4));
        if (token.length() == 14 && code == 'x') return token.replace('&', '\u00a7');
        if (token.length() == 2 && "0123456789abcdefklmnor".indexOf(code) >= 0) return "\u00a7" + code;
        return token;
    }

    private static String hexToken(String hex) {
        StringBuilder result = new StringBuilder("\u00a7x");
        for (int i = 0; i < hex.length(); i++) result.append('\u00a7')
            .append(hex.charAt(i));
        return result.toString();
    }

    public static TextEffectStyle readInline(String header) {
        String[] fields = header.split(";", -1);
        if (fields.length > 3) return null;
        String id = fields[0].trim();
        id = ALIASES.getOrDefault(id, id);
        if (id.indexOf(':') < 0) id = ScienceNotLeisure.MODID + ":" + id;
        List<Integer> colors = List.of();
        float speed = 1;
        boolean hasColors = false;
        boolean hasSpeed = false;
        try {
            for (int i = 1; i < fields.length; i++) {
                String field = fields[i].trim();
                int separator = field.indexOf('=');
                if (separator < 0) {
                    if (field.startsWith("#") && !hasColors) {
                        colors = readColors(field);
                        hasColors = true;
                    } else if (!field.startsWith("#") && !hasSpeed) {
                        speed = Float.parseFloat(field);
                        hasSpeed = true;
                    } else return null;
                    continue;
                }
                String name = field.substring(0, separator)
                    .trim();
                String value = field.substring(separator + 1)
                    .trim();
                if ((name.equals("colors") || name.equals("color") || name.equals("c")) && !hasColors) {
                    colors = readColors(value);
                    hasColors = true;
                } else if ((name.equals("speed") || name.equals("s")) && !hasSpeed) {
                    speed = Float.parseFloat(value);
                    hasSpeed = true;
                } else return null;
            }
            return new TextEffectStyle(id, colors, speed);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static List<Integer> readColors(String value) {
        String[] entries = value.split(",", -1);
        if (entries.length > 8) throw new IllegalArgumentException("At most eight colors are supported");
        List<Integer> colors = new ArrayList<>(entries.length);
        for (String entry : entries) {
            String hex = entry.trim();
            if (hex.startsWith("#")) hex = hex.substring(1);
            if (hex.length() != 3 && hex.length() != 6)
                throw new IllegalArgumentException("Colors must contain three or six hexadecimal digits");
            for (int i = 0; i < hex.length(); i++) {
                if (Character.digit(hex.charAt(i), 16) < 0) throw new IllegalArgumentException("Invalid RGB color");
            }
            int color = Integer.parseInt(hex, 16);
            if (hex.length() == 3)
                color = (color >> 8 & 15) * 0x110000 | (color >> 4 & 15) * 0x1100 | (color & 15) * 0x11;
            colors.add(color);
        }
        return colors;
    }

    public static int nativeLength(String text, int offset) {
        if (offset + 1 >= text.length() || text.charAt(offset) != '\u00a7') return 0;
        char code = Character.toLowerCase(text.charAt(offset + 1));
        if (code == 'g' && hexColor(text, offset + 2) >= 0 && hexColor(text, offset + 16) >= 0) return 30;
        if (code == 'u' && hexColor(text, offset + 2) >= 0) return 16;
        if (code == 'x' && hexColor(text, offset) >= 0) return 14;
        return 2;
    }

    public static int hexColor(String text, int offset) {
        if (offset + 14 > text.length() || text.charAt(offset) != '\u00a7'
            || Character.toLowerCase(text.charAt(offset + 1)) != 'x') return -1;
        int color = 0;
        for (int i = offset + 2; i < offset + 14; i += 2) {
            int digit = Character.digit(text.charAt(i + 1), 16);
            if (text.charAt(i) != '\u00a7' || digit < 0) return -1;
            color = color << 4 | digit;
        }
        return color;
    }

    public static TextEffectStyle applyColor(TextEffectStyle style, String token) {
        char code = Character.toLowerCase(token.charAt(1));
        if (code == 'r') return null;
        if (style == null) return null;
        int color = "0123456789abcdef".indexOf(code);
        if (color >= 0) return style.withColors(VANILLA_COLORS[color]);
        if (token.length() == 14) return style.withColors(hexColor(token, 0));
        if (token.length() == 30) return style.withColors(hexColor(token, 2), hexColor(token, 16));
        return style;
    }

    public static String applyFormatting(String formatting, String token, boolean hexResetsStyles) {
        char code = Character.toLowerCase(token.charAt(1));
        if (code == 'r') return "";
        if ("0123456789abcdef".indexOf(code) >= 0) return token + retain(formatting, "zvu", true);
        if (token.length() == 14 || token.length() == 30) {
            return token + retain(formatting, hexResetsStyles && code == 'x' ? "zvu" : "klmnozvu", true);
        }
        if (code == 'u' && token.length() == 16) return retain(formatting, "u", false) + token;
        if ("zvu".indexOf(code) >= 0) {
            String remaining = retain(formatting, String.valueOf(code), false);
            return remaining.length() == formatting.length() ? formatting + token : remaining;
        }
        if (code == 'q') return retain(formatting, "gxq", false) + token;
        // Unknown extension codes may toggle registered Angelica effects, so retain every occurrence.
        return "klmno".indexOf(code) >= 0 && formatting.contains(token) ? formatting : formatting + token;
    }

    private static String retain(String formatting, String codes, boolean matching) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < formatting.length();) {
            int length = nativeLength(formatting, i);
            if (length == 0) break;
            boolean match = codes.indexOf(Character.toLowerCase(formatting.charAt(i + 1))) >= 0;
            if (match == matching) result.append(formatting, i, i + length);
            i += length;
        }
        return result.toString();
    }
}
