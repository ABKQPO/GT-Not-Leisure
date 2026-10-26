#version 120
#include "common.glsl"

void main() {
    if (shadowPass) {
        outputColor(vec3(0.015), coverage(position()) * 0.28);
        return;
    }
    vec2 point = position();
    float unit = max(1.0, textSize.y);
    vec2 field = point / unit;
    float ink = coverage(point);
    float flow = noise(field * vec2(0.75, 2.8) + vec2(time * 0.22, -time * 1.1));
    float fissure = 1.0 - smoothstep(
        0.04, 0.26, abs(sin(field.x * 2.4 + field.y * 2.1 + sin(field.x * 0.75 - time * 0.9) * 0.6)));
    float heat = smoothstep(0.28, 0.72, flow + fissure * 0.18);
    vec2 emberField = field * vec2(1.4, 3.1) - vec2(0.0, time * 0.85);
    vec2 emberPoint = fract(emberField) - 0.5;
    float ember = step(0.9, hash(floor(emberField))) * exp(-dot(emberPoint, emberPoint) * 36.0);
    vec3 color = mix(palette[1], palette[2], heat);
    color = mix(color, palette[3], max(fissure * 0.84, ember * 0.9));
    outputColor(color, ink);
}
