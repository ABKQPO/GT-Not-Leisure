#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float height = textSize.y;
    float flame = 0.0;
    for (int i = 0; i < 8; i++) {
        float angle = float(i) * 0.79 + time * 2.0;
        float distortion = sin((point.y + float(i) * 79.0 + time * 20.0) * 0.05);
        vec2 offset = vec2(cos(angle) * (10.0 + sin(time * 20.0)) * 0.4 + distortion * 2.0,
            -abs(sin(angle)) * 5.0) * height / 20.0;
        float scale = 1.03 * (0.95 + 0.05 * sin(time * 15.0 + float(i) * 1.58));
        flame += coverage((point - offset) / scale) * 0.16;
    }
    float mainAlpha = coverage(point);
    float edge = outline(point, height * 0.065);
    vec2 glowPoint = (point - textSize * 0.5) / vec2(textSize.x * 0.6 + height, height * 0.75);
    float bloom = exp(-dot(glowPoint, glowPoint) * 3.0) * 0.28;
    vec3 color = mix(palette[0], mix(palette[0], vec3(1.0), 0.67), edge);
    color = mix(color, vec3(0.015), mainAlpha);
    outputColor(color, max(mainAlpha, max(edge, max(flame, bloom))));
}
