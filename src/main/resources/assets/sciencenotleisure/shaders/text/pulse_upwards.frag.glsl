#version 120
#include "common.glsl"

// Adapted from Fargo's Souls Text.fx, distributed under the MIT license.
void main() {
    if (drawShadow()) return;
    vec2 glyphInterval = texture2D(textMask, uv).rg;
    // Fargo samples a small glyph rectangle within an atlas, not a full 0..1 ramp per letter.
    float glyphV = glyphInterval.x + clamp(localUV().y, 0.0, 1.0) * glyphInterval.y;
    float wave = fract(time + glyphV);
    vec3 color = mix(palette[(paletteCount > 1 ? 1 : 0)], palette[0], wave);
    outputColor(min(color * 2.0, vec3(1.0)), coverage(position()));
}
