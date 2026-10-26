#version 120
#include "common.glsl"
#include "rarity_primitives.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float ink = coverage(point);
    float unit = max(1.0, textSize.y);
    float glyphX = glyphCenterX(point);
    float symbolX = (point.x - glyphX) / unit;
    float symbolY = point.y / unit;
    float zigzag = abs(symbolX + 0.085 * sin(symbolY * 23.0));
    float crossbar = abs(symbolY - 0.44 - abs(symbolX) * 0.38);
    float engraving = (1.0 - smoothstep(0.045, 0.11, zigzag))
        + (1.0 - smoothstep(0.035, 0.09, crossbar)) * step(abs(symbolX), 0.28);
    float reveal = smoothstep(0.2, 0.8, 0.5 + 0.5 * sin(time * 1.1 - glyphX / unit * 0.6));
    float glow = clamp(engraving, 0.0, 1.0) * reveal;
    vec3 color = mix(palette[0], palette[1], 0.38 + 0.22 * symbolY);
    color = mix(color, palette[2], glow * 0.8);
    outputColor(color, ink);
}
