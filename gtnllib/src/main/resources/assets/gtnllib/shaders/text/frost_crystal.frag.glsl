#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float ink = coverage(point);
    float edge = max(coverage(point + vec2(1.2, 0.0)), coverage(point - vec2(1.2, 0.0))) - ink;
    vec2 crystal = point / max(1.0, textSize.y) * 3.5;
    vec2 cell = floor(crystal);
    vec2 offset = fract(crystal) - 0.5;
    float grain = hash(cell);
    float branches = min(abs(offset.x), min(abs(offset.y), abs(abs(offset.x) - abs(offset.y)) * 0.71));
    float front = 0.5 + 0.5 * sin(time * 0.7);
    float frozen = 1.0 - smoothstep(front - 0.12, front + 0.08, point.x / max(1.0, textSize.x));
    float veins = (1.0 - smoothstep(0.045, 0.15, branches)) * step(0.38, grain);
    vec3 color = mix(palette[0], palette[1], 0.35 + frozen * 0.42);
    color = mix(color, palette[2], frozen * veins * 0.73);
    outputColor(color, max(ink, edge * frozen * 0.34));
}
