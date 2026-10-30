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
    float cloud = noise(field * vec2(0.8, 1.9) + vec2(time * 0.2, -time * 0.12));
    float seamY = 0.5 + 0.13 * sin(field.x * 1.45 - time * 2.0);
    float rift = 1.0 - smoothstep(0.03, 0.16, abs(field.y - seamY));
    float original = coverage(point);
    float shifted = coverage(point + vec2(rift * unit * 0.09, 0.0));
    float ink = max(original, shifted * 0.8);
    vec2 starField = field * vec2(1.1, 2.2) + vec2(time * 0.1, 0.0);
    float starSeed = hash(floor(starField));
    vec2 starPoint = fract(starField) - 0.5;
    float star = step(0.9, starSeed) * exp(-dot(starPoint, starPoint) * 38.0);
    vec3 color = mix(palette[0], palette[1], 0.46 + cloud * 0.38);
    color = mix(color, palette[2], rift * 0.8);
    color = mix(color, palette[3], star * 0.9);
    outputColor(color, ink);
}
