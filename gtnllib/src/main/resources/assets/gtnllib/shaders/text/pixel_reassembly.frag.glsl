#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float tile = max(2.0, textSize.y * 0.28);
    vec2 cell = floor(point / tile);
    float cycle = floor(time * 0.27);
    float phase = fract(time * 0.27);
    float scatter = smoothstep(0.06, 0.22, phase) * (1.0 - smoothstep(0.48, 0.72, phase));
    vec2 seed = cell + vec2(cycle * 17.0, cycle * 43.0);
    vec2 direction = vec2(hash(seed + vec2(11.0, 3.0)), hash(seed + vec2(2.0, 17.0))) * 2.0 - 1.0;
    vec2 displaced = point - direction * tile * scatter * 0.8;
    float fragment = coverage(displaced);
    float original = coverage(point);
    float seam = min(fract(point.x / tile), fract(point.y / tile));
    float glint = (1.0 - smoothstep(0.0, 0.12, seam)) * scatter;
    vec3 color = mix(palette[0], palette[1], hash(cell) * 0.55 + 0.25);
    color = mix(color, palette[2], glint * 0.7);
    outputColor(color, max(fragment, original * 0.58));
}
