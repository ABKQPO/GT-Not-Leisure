#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    vec2 local = localUV();
    float ink = coverage(point);
    float edge = outline(point, textSize.y * 0.055);
    float cloud = noise(local * vec2(5.0, 3.0) + vec2(time * 0.08, -time * 0.05));
    float riftLine = 0.5 + sin(local.x * 8.0 + time * 1.35) * 0.12;
    float rift = 1.0 - smoothstep(0.0, 0.12, abs(local.y - riftLine));
    vec3 color = mix(gradient(cloud * 0.72), palette[min(paletteCount - 1, 2)], rift * 0.72);
    float cell = hash(floor(local * vec2(18.0, 9.0)) + floor(time * 0.2));
    float star = step(0.93, cell) * (0.45 + 0.55 * sin(time * 2.0 + cell * 31.0));
    color += vec3(star * 0.45);
    color += palette[min(paletteCount - 1, 1)] * cloud * 0.16;
    outputColor(color, max(ink, max(edge * 0.72, ink * rift * 0.65)));
}
