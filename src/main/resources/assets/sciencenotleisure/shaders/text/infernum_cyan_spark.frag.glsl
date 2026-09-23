#version 120
#include "common.glsl"
#include "rarity_primitives.glsl"

uniform sampler2D baseRarityGlow;
uniform sampler2D gleam;
uniform int particleCount;
uniform vec2 particlePosition[32];
uniform float particleScale[32];
uniform float particleRotation[32];
uniform float particleColorAmount[32];

const float TWO_PI = 6.28318530717958647692;

float baseGlow(vec2 point) {
    vec2 glowSize = vec2(textSize.x * 0.115 * 12.0, 0.6 * 66.0);
    vec2 glowCenter = vec2(textSize.x * 0.5, textSize.y / 3.0);
    vec2 local = (point - glowCenter) / glowSize;
    vec2 coord = vec2(0.5 + local.x, 0.5 + local.y);
    if (coord.x < 0.0 || coord.y < 0.0 || coord.x > 1.0 || coord.y > 1.0) return 0.0;
    vec4 texel = texture2D(baseRarityGlow, coord);
    return texel.r * texel.a;
}

void addGleam(inout vec4 layer, vec2 point, vec2 center, float scale, float rotation, float colorAmount) {
    if (scale <= 0.0) return;
    vec2 delta = point - center;
    float c = cos(rotation);
    float s = sin(rotation);
    vec2 local = vec2(c * delta.x + s * delta.y, -s * delta.x + c * delta.y);
    vec2 coord = vec2(0.5 + local.x / (72.0 * scale), 0.5 + local.y / (72.0 * scale));
    if (coord.x < 0.0 || coord.y < 0.0 || coord.x > 1.0 || coord.y > 1.0) return;
    vec4 texel = texture2D(gleam, coord);
    vec3 color = mix(palette[2], palette[3], colorAmount) * 0.5;
    layer.rgb += texel.rgb * texel.a * color;
}

void main() {
    if (drawShadow()) {
        gl_FragColor.rgb *= gl_FragColor.a;
        return;
    }

    vec2 point = position();
    float sine = 0.5 + 0.5 * sin(time * 2.5);
    float sineOffset = mix(0.5, 1.0, sine);
    vec4 layer = vec4(palette[0] * baseGlow(point) * 0.85, 0.0);

    for (int i = 0; i < 12; i++) {
        float angle = TWO_PI * float(i) / 12.0;
        vec2 offset = vec2(cos(angle), sin(angle)) * (2.0 * sineOffset);
        overRarity(layer, palette[1] * 0.9, coverage(point - offset));
    }

    overRarity(layer, mix(palette[0], vec3(0.0), 0.9), coverage(point) * 0.9);

    for (int i = 0; i < 32; i++) {
        if (i >= particleCount) break;
        addGleam(
            layer,
            point,
            textSize * 0.5 + particlePosition[i],
            particleScale[i],
            particleRotation[i],
            particleColorAmount[i]);
    }

    outputRarity(layer);
}
