package com.science.gtnl.client.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.gui.FontRenderer;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import com.gtnewhorizon.gtnhlib.util.font.IFontParameters;
import com.science.gtnl.utils.text.effect.EffectTextParser;
import com.science.gtnl.utils.text.effect.EffectTextParser.Run;
import com.science.gtnl.utils.text.effect.TextEffectStyle;
import com.science.gtnl.utils.text.effect.TextEffects;

/** Shared visible layout for drawing, wrapping, and trimming marked strings. */
public class EffectTextLayout {

    private EffectTextLayout() {}

    public static Layout create(FontRenderer font, String text) {
        IFontParameters parameters = (IFontParameters) font;
        List<Glyph> glyphs = new ArrayList<>();
        String formatting = "";
        boolean bold = false;
        float lineWidth = 0;
        float width = 0;
        int lines = 1;
        boolean spaced = false;
        for (Run run : EffectTextParser.parse(text)
            .runs()) {
            String value = FontRendering.preprocessText(run.text());
            for (int i = 0; i < value.length();) {
                char character = value.charAt(i);
                if (character == '\u00a7' && i + 1 < value.length()) {
                    char code = Character.toLowerCase(value.charAt(i + 1));
                    int length = 2;
                    if (code == 'g' && i + 30 <= value.length()
                        && isHexColor(value, i + 2)
                        && isHexColor(value, i + 16)) {
                        length = 30;
                    } else if (code == 'u' && i + 16 <= value.length() && isHexColor(value, i + 2)) {
                        length = 16;
                    } else if (code == 'x' && isHexColor(value, i)) {
                        length = 14;
                        if (FontRendering.hexColorResetsStyles()) {
                            formatting = "";
                            bold = false;
                        }
                    } else if (code == 'r' || "0123456789abcdef".indexOf(code) >= 0) {
                        formatting = "";
                        bold = false;
                    } else if (code == 'l') {
                        bold = true;
                    }
                    String token = value.substring(i, i + length);
                    if (length > 2 || "klmno".indexOf(code) < 0 || !formatting.contains(token)) formatting += token;
                    i += length;
                    continue;
                }
                int count = Character.charCount(value.codePointAt(i));
                String visible = value.substring(i, i + count);
                float glyphWidth = 0;
                for (int part = 0; part < count; part++) {
                    float partWidth = Math.max(0, parameters.getCharWidthFine(visible.charAt(part)));
                    glyphWidth += partWidth + (bold && partWidth > 0 ? 1 : 0);
                }
                if (character == '\n') {
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
                glyphs.add(new Glyph(visible, formatting, run.style(), glyphWidth));
                i += count;
            }
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

    private static boolean isHexColor(String text, int offset) {
        if (offset + 14 > text.length()) return false;
        if (text.charAt(offset) != '\u00a7' || Character.toLowerCase(text.charAt(offset + 1)) != 'x') return false;
        for (int i = offset + 2; i < offset + 14; i += 2) {
            if (text.charAt(i) != '\u00a7' || Character.digit(text.charAt(i + 1), 16) < 0) return false;
        }
        return true;
    }

    public static String trim(FontRenderer font, String text, int width, boolean reverse) {
        if (width <= 0) return "";
        Layout layout = create(font, text);
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
        return encode(glyphs, start, end);
    }

    public static List<String> wrap(FontRenderer font, String text, int width) {
        Layout layout = create(font, text);
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
            if (!Objects.equals(active, glyph.style())) {
                if (active != null) result.append(EffectTextParser.CLOSE);
                active = glyph.style();
                if (active != null) result.append(TextEffects.opening(active));
            }
            if (!Objects.equals(formatting, glyph.formatting())) {
                if (formatting != null) result.append('\u00a7')
                    .append('r');
                formatting = glyph.formatting();
                result.append(formatting);
            }
            result.append(TextEffects.escapeLiteral(glyph.text()));
        }
        if (active != null) result.append(EffectTextParser.CLOSE);
        return result.toString();
    }

    public record Glyph(String text, String formatting, TextEffectStyle style, float width) {}

    public record Layout(List<Glyph> glyphs, float width, float height, float spacing, int lines) {}
}
