#version 120
#include "common.glsl"

// Adapted from Fargo's Souls Text.fx, distributed under the MIT license.
void main() {
    if (drawShadow()) return;
    float wave = fract(time + length(localUV() - vec2(0.5)));
    vec3 color = mix(palette[(paletteCount > 1 ? 1 : 0)], palette[0], wave);
    outputColor(min(color * palette[(paletteCount > 1 ? 1 : 0)] * 2.0, vec3(1.0)), coverage(position()));
}
