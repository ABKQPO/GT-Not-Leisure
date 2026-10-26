#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    vec2 coords = floor(localUV() * 560.0) / 560.0;
    float phase = time * 0.4;
    float band = floor(coords.y * 72.0) / 72.0 + floor(phase * 11.0) / 11.0;
    float glitch = noise(vec2(band) * 32.0);
    band = floor(coords.y * 36.0) / 72.0 + floor(phase * 16.5) / 16.5;
    glitch = (glitch + noise(vec2(band) * 32.0)) * 0.67;
    band = floor(coords.y * 28.8) / 72.0 + floor(phase * 14.3) / 14.3;
    glitch = (glitch + noise(vec2(band) * 32.0)) * 0.62;
    point.x += smoothstep(0.8, 1.0, glitch) * textSize.x * 0.05;
    float separation = (cos(time * 0.8) * 0.5 + 0.5) * textSize.y * 0.16;
    vec3 channels = vec3(coverage(point - vec2(separation, 0.0)),
        coverage(point + vec2(separation, 0.0)), coverage(point + vec2(0.0, separation)));
    float alpha = max(channels.r, max(channels.g, channels.b));
    outputColor(channels * palette[0], alpha);
}
