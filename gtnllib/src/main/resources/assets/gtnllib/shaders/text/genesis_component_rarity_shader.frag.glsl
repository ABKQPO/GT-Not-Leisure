#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    vec2 coords = localUV();
    vec2 relative = coords - 0.5;
    vec2 polar = vec2(atan(relative.y, relative.x) / 6.2831853 + 0.5, length(relative));
    float distortion = noise(polar * vec2(16.0, 1.6));
    float hue = pow(sin(time * 1.78 + coords.x * 5.0 + coords.y + distortion * 2.0) * 0.5 + 0.5, 3.1);
    float scaled = min(hue * float(paletteCount), float(paletteCount - 1));
    int first = int(floor(scaled));
    vec3 color = mix(palette[first], palette[(first + 1 < paletteCount ? first + 1 : paletteCount - 1)], fract(scaled));
    float pulse = fract(time * 1.4);
    float echo = coverage(point / (vec2(1.0) + vec2(0.1, 0.5) * pulse)) * pow(1.0 - pulse, 1.5);
    float mainAlpha = coverage(point);
    outputColor(mix(palette[0] * 0.7, color, mainAlpha), max(mainAlpha, echo * 0.6));
}
