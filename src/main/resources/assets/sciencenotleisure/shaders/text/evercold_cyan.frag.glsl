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
    vec4 layer = vec4(palette[1] * textBloom(point), 0.0);
    layer.rgb += palette[0] * orbitCoverage(point, (2.5 + sin(time * 5.0)) * unit, time * 2.0) * 0.5;
    overRarity(layer, min(palette[0] * 2.0, vec3(1.0)), outline(point, 2.0 * unit));
    overRarity(layer, vec3(0.0), coverage(point));
    float count = min(64.0, max(3.0, textSize.x / unit / 6.0 + 1.0));
    for (int i = 0; i < 64; i++) {
        float seed = float(i);
        if (seed >= count) break;
        float age = mod(time * 4.0 + hash(vec2(seed, 1.0)) * 6.283185307, 6.283185307);
        if (age >= 3.141592654) continue;
        vec2 center = vec2(hash(vec2(seed, 2.0)) * textSize.x + age * 2.0 * unit,
            hash(vec2(seed, 3.0)) * textSize.y * 0.6 + (age * 4.0 + 3.0) * unit);
        float fraction = age / 6.283185307;
        float rotation = time * mix(0.8, 1.5, hash(vec2(seed, 4.0)));
        vec2 delta = turn(point - center, -rotation);
        float core = starLight(delta, vec2(max(0.015, fraction) * textSize.y * 0.38));
        float wide = (sin(age / 1.570796327) + 1.0) * 0.2;
        float glow = starLight(delta, vec2(wide, max(0.015, fraction) * 0.15) * textSize.y);
        vec3 white = vec3(200.0 / 255.0) + palette[1] * 0.05;
        layer.rgb += (white * core + palette[1] * glow) * sin(age);
    }
    outputRarity(layer);
}
