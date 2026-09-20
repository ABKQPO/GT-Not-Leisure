package com.science.gtnl.client.text;

import com.science.gtnl.client.text.TextMaskCache.Mask;
import com.science.gtnl.utils.text.effect.TextEffectStyle;

public record TextRenderContext(Mask mask, TextEffectStyle style, float x, float y, int color, double seconds,
    boolean shadow) {}
