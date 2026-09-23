#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float height = textSize.y;
    float pulse = sin(time * 2.5) * 0.5 + 0.5;
    float mainAlpha = coverage(point);
    float edge = outline(point, height * mix(0.06, 0.12, pulse));
    vec2 glowPoint = (point - vec2(textSize.x * 0.5, height * 0.4)) / vec2(textSize.x * 0.65 + height, height * 0.8);
    float glow = exp(-dot(glowPoint, glowPoint) * 3.5) * 0.35;
    vec3 outer = mix(palette[0], palette[(paletteCount > 1 ? 1 : 0)], sin(time * 2.0) * 0.5 + 0.5);
    float spark = 0.0;
    for (int i = 0; i < 12; i++) {
        float seed = float(i);
        float age = fract(time * 0.85 + hash(vec2(seed, 2.0)));
        vec2 center = vec2(hash(vec2(seed, floor(time * 0.85 + hash(vec2(seed, 2.0))))) * textSize.x,
            height * (0.65 - age * 0.9));
        vec2 delta = (point - center) / height;
        float angle = seed + time * (hash(vec2(seed, 4.0)) - 0.5) * 2.0;
        delta = mat2(cos(angle), -sin(angle), sin(angle), cos(angle)) * delta;
        float cross = exp(-abs(delta.x) * 120.0 - abs(delta.y) * 22.0)
            + exp(-abs(delta.y) * 120.0 - abs(delta.x) * 22.0);
        spark += cross * smoothstep(0.0, 0.3, age) * (1.0 - smoothstep(0.7, 1.0, age));
    }
    float alpha = max(mainAlpha, max(edge * 0.9, max(glow, spark)));
    vec3 rgb = mix(outer, palette[0] * 0.1, mainAlpha);
    rgb += outer * spark * 1.4;
    outputColor(rgb, alpha);
}
