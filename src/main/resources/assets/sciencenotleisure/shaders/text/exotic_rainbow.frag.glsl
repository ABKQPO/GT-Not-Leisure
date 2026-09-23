#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float height = textSize.y;
    float phase = time + point.x * 0.005 * 20.0 / height;
    float stepIndex = floor((phase + 1.5) / 2.0);
    int index = int(mod(stepIndex, float(paletteCount)));
    vec3 color = palette[index];
    float sine = sin(time * 2.0 / 3.14159265) * 0.65;
    float pulse = sine * sine * sine * sine * sine;
    float radius = (4.0 + 16.0 * pulse) * height / 20.0;
    float glow = 0.0;
    for (int i = 0; i < 16; i++) {
        float angle = float(i) * 0.39269908 + time * 1.7;
        glow += coverage(point + vec2(cos(angle), sin(angle)) * radius) * 0.15;
    }
    float edge = outline(point, height * 0.1);
    float mainAlpha = coverage(point);
    vec3 rgb = mix(color, vec3(0.015), edge);
    rgb = mix(rgb, color, mainAlpha);
    outputColor(rgb, max(mainAlpha, max(edge, glow)));
}
