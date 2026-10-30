#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float drift = textSize.y * 0.17;
    vec2 echoA = vec2(sin(time * 1.1), cos(time * 0.7)) * drift;
    vec2 echoB = vec2(sin(time * 0.8 + 2.1), cos(time * 1.2 + 1.3)) * drift * 1.5;
    float nearEcho = coverage(point - echoA);
    float farEcho = coverage(point - echoB);
    float ink = coverage(point);
    float echo = max(nearEcho * 0.42, farEcho * 0.25) * (1.0 - ink);
    vec3 color = mix(palette[1], palette[2], farEcho * 0.48);
    color = mix(color, palette[0], ink);
    outputColor(color, max(ink, echo));
}
