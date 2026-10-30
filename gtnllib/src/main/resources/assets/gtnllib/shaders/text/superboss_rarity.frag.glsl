#version 120
#include "common.glsl"
#include "rarity_primitives.glsl"

void main() {
    if (drawShadow()) {
        gl_FragColor.rgb *= gl_FragColor.a;
        return;
    }
    vec2 point = position();
    float unit = textSize.y / 29.0;
    float radius = (3.0 + sin(time * 5.0)) * unit;
    vec4 layer = vec4(palette[0] * orbitCoverage(point, radius, time * 2.0) * 0.5, 0.0);
    overRarity(layer, min(palette[0] * 2.0, vec3(1.0)), outline(point, 2.0 * unit));
    layer.rgb += palette[1] * textBloom(point);
    overRarity(layer, vec3(0.0), coverage(point));
    float count = min(64.0, max(4.0, textSize.x / unit / 3.0));
    for (int i = 0; i < 64; i++) {
        float seed = float(i);
        if (seed >= count) break;
        float age = mod(time * 4.0 + hash(vec2(seed, 1.0)) * 43.98229715, 43.98229715);
        if (age >= 3.141592654) continue;
        vec2 center = vec2(hash(vec2(seed, 2.0)) * textSize.x,
            hash(vec2(seed, 3.0)) * textSize.y * 0.6 + (3.0 - age * 4.5) * unit);
        float fraction = age / 6.283185307;
        float stretch = (sin(age / 1.570796327) + 1.0) * 0.5;
        vec2 delta = point - center;
        float core = starLight(delta, vec2(max(0.02, fraction) * textSize.y * 0.45));
        float glow = starLight(delta, vec2(stretch, fraction * 0.5) * textSize.y * 0.9);
        vec3 white = vec3(200.0 / 255.0) + palette[1] * 0.05;
        layer.rgb += (white * core + palette[1] * glow * 1.25) * sin(age);
    }
    outputRarity(layer);
}
