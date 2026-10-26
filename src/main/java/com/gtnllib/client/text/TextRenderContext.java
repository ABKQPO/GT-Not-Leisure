package com.gtnllib.client.text;

import com.gtnllib.client.text.TextMaskCache.Mask;
import com.gtnllib.text.TextEffectStyle;

public record TextRenderContext(Mask mask, TextEffectStyle style, float x, float y, int color, double seconds,
    boolean shadow) {}
