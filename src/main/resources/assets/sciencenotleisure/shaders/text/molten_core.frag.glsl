#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    vec2 local = localUV();
    float ink = coverage(point);
    float edge = outline(point, textSize.y * 0.07);
    float heat = noise(local * vec2(6.0, 3.0) + vec2(0.0, -time * 0.18));
    float veins = smoothstep(0.34, 0.72, noise(local * vec2(10.0, 2.8) + vec2(time * 0.12, 0.0)));
    float flow = clamp(heat * 0.58 + veins * 0.42 + local.y * 0.16, 0.0, 0.999);
    vec3 color = gradient(flow);
    float ember = step(0.86, noise(local * vec2(16.0, 7.0) + time * 0.25));
    color += palette[min(paletteCount - 1, 3)] * ember * ink * 0.5;
    vec3 hotEdge = mix(palette[0], palette[min(paletteCount - 1, 2)], 0.7);
    color = mix(hotEdge, color, ink);
    outputColor(clamp(color, 0.0, 1.0), max(ink, edge * 0.82));
}
