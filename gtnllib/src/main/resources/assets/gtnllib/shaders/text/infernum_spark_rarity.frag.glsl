#version 120
#include "common.glsl"
#include "rarity_primitives.glsl"

void renderInfernumSparks(float count, vec2 lifetimeRange, vec2 speedRange, float upwardBias,
    float particleBrightness, float colorRange) {
    if (drawShadow()) {
        gl_FragColor.rgb *= gl_FragColor.a;
        return;
    }
    vec2 point = position();
    float unit = textSize.y / 29.0;
    float pulse = 0.5 + 0.5 * sin(time * 2.5);
    vec4 layer = vec4(palette[1] * textBloom(point) * 0.85, 0.0);
    overRarity(layer, palette[0] * 0.9, outline(point, mix(1.0, 2.0, pulse) * unit));
    overRarity(layer, palette[1] * 0.1, coverage(point));
    for (int i = 0; i < 32; i++) {
        if (float(i) >= count) break;
        float seed = float(i);
        float life = mix(lifetimeRange.x, lifetimeRange.y, hash(vec2(seed, 31.0))) / 60.0;
        float period = life + 0.35;
        float cycle = time / period + hash(vec2(seed, 19.0));
        float age = fract(cycle) * period;
        if (age >= life) continue;
        float generation = floor(cycle);
        vec2 start = vec2(hash(vec2(seed, generation + 1.0)) * textSize.x,
            mix(0.2, 0.55, hash(vec2(seed, generation + 9.0))) * textSize.y);
        vec2 radial = start - textSize * 0.5;
        vec2 direction = radial / max(length(radial), 0.001);
        direction = turn(direction, (hash(vec2(seed, generation + 3.0)) - 0.5) * 0.3);
        vec2 velocity = direction * mix(speedRange.x, speedRange.y, hash(vec2(seed, 5.0)));
        velocity.y -= upwardBias;
        vec2 center = start + velocity * age * 60.0 * unit;
        float envelope = max(0.0, min(1.0, min(age, life - age) * 3.0));
        vec2 delta = turn(point - center, -atan(velocity.y, velocity.x) - 1.5707963268);
        float size = textSize.y * mix(0.18, 0.27, hash(vec2(seed, 8.0))) * envelope;
        float gleam = starLight(delta, vec2(size * 0.22, size));
        float blend = hash(vec2(seed, generation + 7.0)) * colorRange;
        blend = blend * blend * (3.0 - 2.0 * blend);
        layer.rgb += mix(palette[2], palette[3], blend) * gleam * envelope * particleBrightness;
    }
    outputRarity(layer);
}

void main() {
    renderInfernumSparks(32.0, vec2(32.0, 45.0), vec2(0.1, 0.3), 0.1, 0.5, 1.0);
}
