package com.science.gtnl.client.text;

import java.util.List;

import com.science.gtnl.client.text.EffectTextLayout.Glyph;
import com.science.gtnl.client.text.EffectTextLayout.Layout;

/** Projects source-indexed text field ranges onto a complete formatted layout. */
public class EffectTextFieldView {

    private final String source;
    private final Layout layout;
    private final int[] sliceStarts = { -1, -1, -1 };
    private final int[] sliceEnds = { -1, -1, -1 };
    private final String[] slices = new String[3];
    private int nextSlice;

    public EffectTextFieldView(String source, Layout layout) {
        this.source = source;
        this.layout = layout;
    }

    public boolean matches(String text, Layout current) {
        return layout == current && source.equals(text);
    }

    /** Encodes only visible glyphs; declarations cut by the cursor never become literal text. */
    public String slice(int start, int end) {
        int first = firstGlyph(start);
        int last = lastGlyph(end);
        if (last <= first) return "";
        for (int i = 0; i < slices.length; i++) {
            if (sliceStarts[i] == first && sliceEnds[i] == last) return slices[i];
        }
        String value = EffectTextLayout.encode(layout.glyphs(), first, last);
        sliceStarts[nextSlice] = first;
        sliceEnds[nextSlice] = last;
        slices[nextSlice] = value;
        nextSlice = (nextSlice + 1) % slices.length;
        return value;
    }

    public int width(int start, int end) {
        float width = 0;
        boolean spaced = false;
        List<Glyph> glyphs = layout.glyphs();
        for (int i = firstGlyph(start), last = lastGlyph(end); i < last; i++) {
            Glyph glyph = glyphs.get(i);
            if (spaced && glyph.width() > 0) width += layout.spacing();
            width += glyph.width();
            spaced |= glyph.width() > 0;
        }
        return (int) Math.ceil(width);
    }

    /** Returns an original source slice so mouse and selection indices remain valid. */
    public String trim(int start, int end, int width, boolean reverse) {
        start = Math.max(0, Math.min(start, source.length()));
        end = Math.max(start, Math.min(end, source.length()));
        if (width <= 0) return "";
        List<Glyph> glyphs = layout.glyphs();
        int first = firstGlyph(start);
        int last = lastGlyph(end);
        float used = 0;
        boolean spaced = false;
        for (int i = reverse ? last - 1 : first; i >= first && i < last; i += reverse ? -1 : 1) {
            Glyph glyph = glyphs.get(i);
            float next = used + glyph.width() + (spaced && glyph.width() > 0 ? layout.spacing() : 0);
            if (glyph.text()
                .equals("\n") || Math.ceil(next) > width) {
                return reverse ? source.substring(glyph.sourceEnd(), end)
                    : source.substring(start, glyph.sourceStart());
            }
            used = next;
            spaced |= glyph.width() > 0;
        }
        return source.substring(start, end);
    }

    private int firstGlyph(int sourceStart) {
        List<Glyph> glyphs = layout.glyphs();
        int low = 0;
        int high = glyphs.size();
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (glyphs.get(middle)
                .sourceStart() < sourceStart) low = middle + 1;
            else high = middle;
        }
        return low;
    }

    private int lastGlyph(int sourceEnd) {
        List<Glyph> glyphs = layout.glyphs();
        int low = 0;
        int high = glyphs.size();
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (glyphs.get(middle)
                .sourceEnd() <= sourceEnd) low = middle + 1;
            else high = middle;
        }
        return low;
    }
}
