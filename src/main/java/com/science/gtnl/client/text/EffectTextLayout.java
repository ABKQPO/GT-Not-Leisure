package com.science.gtnl.client.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.gui.FontRenderer;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import com.gtnewhorizon.gtnhlib.util.font.IFontParameters;
import com.science.gtnl.utils.text.effect.EffectTextParser;
import com.science.gtnl.utils.text.effect.EffectTextParser.Cursor;
import com.science.gtnl.utils.text.effect.EffectTextParser.Token;
import com.science.gtnl.utils.text.effect.TextEffectStyle;
import com.science.gtnl.utils.text.effect.TextEffects;

/** Shared visible layout for drawing, wrapping, and trimming marked strings. */
public class EffectTextLayout {

    private EffectTextLayout() {}

    public static Layout create(FontRenderer font, String text) {
        IFontParameters parameters = (IFontParameters) font;
        List<Glyph> glyphs = new ArrayList<>();
        float lineWidth = 0;
        float width = 0;
        int lines = 1;
        boolean spaced = false;
        Cursor cursor = cursor(text);
        for (Token token; (token = cursor.next()) != null;) {
            if (token.formattingCode()) continue;
            String visible = token.text();
            String formatting = token.formatting();
            boolean bold = formatting.contains("\u00a7l") || formatting.contains("\u00a7L");
            float glyphWidth = 0;
            for (int part = 0; part < visible.length(); part++) {
                float partWidth = Math.max(0, parameters.getCharWidthFine(visible.charAt(part)));
                glyphWidth += partWidth + (bold && partWidth > 0 ? 1 : 0);
            }
            if (visible.equals("\n")) {
                glyphWidth = 0;
                width = Math.max(width, lineWidth);
                lineWidth = 0;
                lines++;
                spaced = false;
            } else if (glyphWidth > 0) {
                if (spaced) lineWidth += parameters.getGlyphSpacing();
                lineWidth += glyphWidth;
                spaced = true;
            }
            glyphs.add(new Glyph(visible, formatting, token.style(), glyphWidth, token.start(), token.end()));
        }
        return new Layout(
            List.copyOf(glyphs),
            Math.max(width, lineWidth),
            fontHeight(font),
            parameters.getGlyphSpacing(),
            lines);
    }

    public static float fontHeight(FontRenderer font) {
        return Math.max(1, font.FONT_HEIGHT * ((IFontParameters) font).getGlyphScaleY());
    }

    private static Cursor cursor(String text) {
        return new Cursor(
            text,
            FontRendering::preprocessText,
            FontRendering.hexColorResetsStyles(),
            identifier -> TextEffectRegistry.get(identifier) != null);
    }

    public static String continuation(String text) {
        Cursor cursor = cursor(text);
        while (cursor.next() != null) {
            // Consume the prefix to recover both native formatting and nested effect scopes.
        }
        return cursor.continuation();
    }

    /** Returns a source index rather than the length of re-encoded visible text. */
    public static int sizeToWidth(FontRenderer font, String text, int width) {
        Layout layout = EffectTextRenderer.INSTANCE.layout(font, text);
        float used = 0;
        boolean spaced = false;
        int lastSpace = -1;
        for (Glyph glyph : layout.glyphs()) {
            if (glyph.text()
                .equals("\n")) return glyph.sourceStart();
            if (glyph.text()
                .equals(" ")) lastSpace = glyph.sourceStart();
            used += glyph.width() + (spaced && glyph.width() > 0 ? layout.spacing() : 0);
            if (Math.ceil(used) > Math.max(0, width)) return lastSpace >= 0 ? lastSpace : glyph.sourceStart();
            spaced |= glyph.width() > 0;
        }
        return text.length();
    }

    public static int lastSpace(String text) {
        Cursor cursor = cursor(text);
        int last = -1;
        for (Token token; (token = cursor.next()) != null;) {
            if (!token.formattingCode() && token.text()
                .equals(" ")) last = token.start();
        }
        return last;
    }

    public static String trim(FontRenderer font, String text, int width, boolean reverse) {
        if (width <= 0) return "";
        Layout layout = EffectTextRenderer.INSTANCE.layout(font, text);
        List<Glyph> glyphs = layout.glyphs();
        int start = reverse ? glyphs.size() : 0;
        int end = start;
        float used = 0;
        boolean spaced = false;
        while (reverse ? start > 0 : end < glyphs.size()) {
            Glyph glyph = glyphs.get(reverse ? start - 1 : end);
            if (glyph.text()
                .equals("\n")) break;
            float next = used + glyph.width() + (spaced && glyph.width() > 0 ? layout.spacing() : 0);
            if (Math.ceil(next) > width) break;
            used = next;
            spaced |= glyph.width() > 0;
            if (reverse) start--;
            else end++;
        }
        if (reverse) {
            if (start == end) return "";
            return text.substring(
                start == 0 ? 0
                    : glyphs.get(start - 1)
                        .sourceEnd());
        }
        // Chat and text fields use this length to slice the original source.
        return text.substring(
            0,
            end < glyphs.size() ? glyphs.get(end)
                .sourceStart() : text.length());
    }

    public static List<String> wrap(FontRenderer font, String text, int width) {
        Layout layout = EffectTextRenderer.INSTANCE.layout(font, text);
        List<Glyph> glyphs = layout.glyphs();
        List<String> lines = new ArrayList<>();
        int start = 0;
        while (start < glyphs.size()) {
            int end = start;
            int lastSpace = -1;
            float used = 0;
            boolean spaced = false;
            while (end < glyphs.size()) {
                Glyph glyph = glyphs.get(end);
                if (glyph.text()
                    .equals("\n")) break;
                float next = used + glyph.width() + (spaced && glyph.width() > 0 ? layout.spacing() : 0);
                if (Math.ceil(next) > Math.max(0, width) && end > start) break;
                if (glyph.text()
                    .equals(" ")) lastSpace = end;
                used = next;
                spaced |= glyph.width() > 0;
                end++;
            }
            int next = end;
            if (end < glyphs.size()) {
                if (glyphs.get(end)
                    .text()
                    .equals("\n")) next++;
                else if (lastSpace >= start) {
                    end = lastSpace;
                    next = lastSpace + 1;
                }
            }
            lines.add(encode(glyphs, start, end));
            start = next;
        }
        if (glyphs.isEmpty() || glyphs.get(glyphs.size() - 1)
            .text()
            .equals("\n")) lines.add("");
        return lines;
    }

    public static String encode(List<Glyph> glyphs, int start, int end) {
        StringBuilder result = new StringBuilder();
        TextEffectStyle active = null;
        String formatting = null;
        for (int i = start; i < end; i++) {
            Glyph glyph = glyphs.get(i);
            boolean formattingChanged = !Objects.equals(formatting, glyph.formatting());
            if (!Objects.equals(active, glyph.style()) || formattingChanged) {
                if (active != null) result.append(EffectTextParser.CLOSE);
                active = null;
            }
            if (formattingChanged) {
                if (formatting != null) result.append('\u00a7')
                    .append('r');
                formatting = glyph.formatting();
                result.append(formatting);
            }
            if (!Objects.equals(active, glyph.style())) {
                active = glyph.style();
                if (active != null) result.append(TextEffects.opening(active));
            }
            result.append(TextEffects.escapeLiteral(glyph.text()));
        }
        if (active != null) result.append(EffectTextParser.CLOSE);
        return result.toString();
    }

    private static List<DrawRun> prepareRuns(List<Glyph> glyphs, float height, float spacing) {
        List<DrawRun> runs = new ArrayList<>();
        float x = 0;
        float y = 0;
        boolean spaced = false;
        for (int start = 0; start < glyphs.size();) {
            Glyph first = glyphs.get(start);
            if (first.text()
                .equals("\n")) {
                x = 0;
                y += height;
                spaced = false;
                start++;
                continue;
            }
            int end = start;
            float width = 0;
            StringBuilder text = new StringBuilder(first.formatting());
            while (end < glyphs.size()) {
                Glyph glyph = glyphs.get(end);
                if (glyph.text()
                    .equals("\n") || !Objects.equals(glyph.style(), first.style())
                    || !glyph.formatting()
                        .equals(first.formatting()))
                    break;
                if (end > start && glyph.width() > 0) width += spacing;
                width += glyph.width();
                text.append(glyph.text());
                end++;
            }
            if (spaced && width > 0) x += spacing;
            runs.add(new DrawRun(text.toString(), first.style(), x, y, width));
            x += width;
            spaced |= width > 0;
            start = end;
        }
        return List.copyOf(runs);
    }

    public record Glyph(String text, String formatting, TextEffectStyle style, float width, int sourceStart,
        int sourceEnd) {

        public Glyph(String text, String formatting, TextEffectStyle style, float width) {
            this(text, formatting, style, width, -1, -1);
        }
    }

    public record DrawRun(String text, TextEffectStyle style, float x, float y, float width) {}

    public record Layout(List<Glyph> glyphs, float width, float height, float spacing, int lines, List<DrawRun> runs) {

        public Layout(List<Glyph> glyphs, float width, float height, float spacing, int lines) {
            this(glyphs, width, height, spacing, lines, prepareRuns(glyphs, height, spacing));
        }
    }
}
