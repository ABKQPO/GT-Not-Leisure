#version 120
#include "common.glsl"
#include "rarity_primitives.glsl"

// Adapted from Calamity Overhaul's StarsilverRarity under the MIT license.
void main() {
    if (drawShadow()) {
        gl_FragColor.rgb *= gl_FragColor.a;
        return;
    }
    vec2 point = position();
    float unit = textSize.y / 29.0;
    float sourceX = (textOrigin.x + glyphCenterX(point)) / unit;
    float hue = fract(sourceX * 0.003 + time * 0.08);
    vec3 wheel = clamp(abs(mod(hue * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    vec3 spectral = paletteOverridden ? cycle(hue) : vec3(0.72) + (wheel - 0.5) * 0.392;
    float highlight = pow(0.5 + 0.5 * sin(sourceX * 0.045 - time * 2.0), 5.0);
    vec4 layer = vec4(0.0);
    overRarity(layer, palette[1], outline(point, 1.2 * unit));
    overRarity(layer, mix(palette[0], spectral, highlight * 0.75), coverage(point));
    for (int i = 0; i < 2; i++) {
        float seed = float(i);
        float period = 2.4 + hash(vec2(seed, 13.0)) * 1.6;
        float cycleTime = time / period + hash(vec2(seed, 17.0));
        float age = fract(cycleTime);
        if (age > 0.28) continue;
        float generation = floor(cycleTime);
        float intensity = sin(3.141592654 * age / 0.28);
        vec2 center = vec2(hash(vec2(generation, seed * 5.0 + 1.0)) * textSize.x,
            (0.2 + 0.5 * hash(vec2(generation, seed * 5.0 + 2.0))) * textSize.y);
        vec2 delta = turn(point - center, -intensity * 0.6);
        float glint = starLight(delta, vec2((9.0 + 4.0 * intensity) * unit * 0.5));
        layer.rgb += palette[2] * glint * intensity * 0.85;
    }
    outputRarity(layer);
}
