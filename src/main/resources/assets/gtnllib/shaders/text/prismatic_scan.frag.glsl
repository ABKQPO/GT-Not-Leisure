#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    vec2 local = localUV();
    float unit = textSize.y / 96.0;
    float red = coverage(point + vec2(unit * (1.8 + sin(time * 2.0)), 0.0));
    float green = coverage(point);
    float blue = coverage(point - vec2(unit * (1.8 + sin(time * 2.0)), 0.0));
    float ink = max(red, max(green, blue));
    float scan = 1.0 - smoothstep(0.0, 0.16, abs(fract(local.x - time * 0.42) - 0.5));
    vec3 color = cycle(local.x * 0.75 + time * 0.12);
    color *= 0.78 + scan * 0.42;
    color.r += red * 0.18;
    color.g += green * 0.18;
    color.b += blue * 0.18;
    color = clamp(color, 0.0, 1.0);
    outputColor(color, ink);
}
