package com.science.gtnl.utils.text.effect;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** A linear parser that leaves malformed and unknown syntax as literal text. */
public class EffectTextParser {

    public static final String OPEN = "\u2063[gtnl:";
    public static final String CLOSE = "\u2063[/gtnl]\u2063";
    public static final String END = "]\u2063";

    private EffectTextParser() {}

    public static boolean containsMarkers(String text) {
        return text != null && text.indexOf('\u2063') >= 0;
    }

    public static Parsed parse(String text) {
        if (text == null || text.isEmpty()) return new Parsed("", List.of());
        if (!containsMarkers(text)) return new Parsed(text, List.of(new Run(text, null)));
        List<Run> runs = new ArrayList<>();
        Deque<TextEffectStyle> styles = new ArrayDeque<>();
        StringBuilder plain = new StringBuilder(text.length());
        StringBuilder run = new StringBuilder();
        for (int i = 0; i < text.length();) {
            if (text.startsWith("\u2063\u2063", i)) {
                run.append('\u2063');
                plain.append('\u2063');
                i += 2;
            } else if (text.startsWith(CLOSE, i) && !styles.isEmpty()) {
                flush(runs, run, styles.peek());
                styles.pop();
                i += CLOSE.length();
            } else if (text.startsWith(OPEN, i)) {
                int end = headerEnd(text, i + OPEN.length());
                TextEffectStyle style = end < 0 ? null : readStyle(text.substring(i + OPEN.length(), end));
                if (style != null) {
                    flush(runs, run, styles.peek());
                    styles.push(style);
                    i = end + END.length();
                } else {
                    run.append(text.charAt(i));
                    plain.append(text.charAt(i++));
                }
            } else {
                run.append(text.charAt(i));
                plain.append(text.charAt(i++));
            }
        }
        flush(runs, run, styles.peek());
        return new Parsed(plain.toString(), List.copyOf(runs));
    }

    private static void flush(List<Run> runs, StringBuilder text, TextEffectStyle style) {
        if (text.length() == 0) return;
        runs.add(new Run(text.toString(), style));
        text.setLength(0);
    }

    private static int headerEnd(String text, int start) {
        int limit = Math.min(text.length() - END.length(), start + 512);
        for (int i = start; i <= limit; i++) {
            if (text.startsWith(END, i)) return i;
        }
        return -1;
    }

    private static TextEffectStyle readStyle(String header) {
        String[] fields = header.split(";", -1);
        if (fields.length != 3) return null;
        try {
            List<Integer> colors = new ArrayList<>();
            if (!fields[2].isEmpty()) {
                String[] values = fields[2].split(",", -1);
                if (values.length > 8) return null;
                for (String value : values) {
                    if (value.isEmpty() || value.length() > 6) return null;
                    colors.add(Integer.parseInt(value, 16));
                }
            }
            return new TextEffectStyle(fields[0], colors, Float.parseFloat(fields[1]));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public record Run(String text, TextEffectStyle style) {}

    public record Parsed(String plainText, List<Run> runs) {}
}
