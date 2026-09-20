#version 120
#include "common.glsl"
#include "rarity_primitives.glsl"

uniform bool flashActive;

void main() {
    float unit = textSize.y / 29.0;
    float frame = floor(time * 60.0);
    float flash = flashActive ? 1.0 : 0.0;
    // NextVector2Circular uses a random angle and radius within an 8 by 4.8 ellipse.
    float angle = hash(vec2(frame, 31.0)) * 6.283185307;
    vec2 jitter = vec2(cos(angle), sin(angle)) * hash(vec2(frame, 47.0)) * vec2(8.0, 4.8) * unit * flash;
    vec2 point = position() - jitter;
    if (drawShadow(point)) {
        gl_FragColor.rgb *= gl_FragColor.a;
        return;
    }
    float ink = coverage(point);
    // Terraria's mouse text brightness moves between 190 and 255, one step per tick.
    float brightness = (190.0 + abs(mod(frame, 130.0) - 65.0)) / 255.0;
    vec3 border = min(mix(floor(palette[0] * brightness * 255.0) / 255.0, palette[3], flash) * 2.0, vec3(1.0));
    vec3 shine = mix(palette[2], palette[4], flash);
    vec4 layer = vec4(0.0);
    overRarity(layer, border, outline(point, 2.0 * unit));
    overRarity(layer, palette[1], ink);
    float sourceX = (textOrigin.x + glyphStartX(point) + jitter.x) / unit;
    float sweep = pow(0.5 + 0.5 * sin(sourceX * 0.02 - time * 1.5), 120.0);
    overRarity(layer, shine, ink * sweep);
    outputRarity(layer);
}
