#version 120
#include "noise.glsl"

uniform float time;
uniform float progress;

varying vec2 uv;
varying vec4 tint;

void main() {
    vec2 point = uv * 2.0 - 1.0;
    float radius = length(point);
    float angle = atan(point.y, point.x);

    float wobble = (railgunFbm(vec2(angle * 2.5 + 4.0, time * 1.6)) - 0.5) * 0.22;
    float target = progress * (1.0 + wobble);

    float ring = smoothstep(target - 0.18, target, radius) * (1.0 - smoothstep(target, target + 0.12, radius));
    float flash = pow(max(0.0, 1.0 - radius * 2.4), 3.0) * (1.0 - progress);
    float fade = 1.0 - progress;

    vec3 color = mix(tint.rgb, vec3(1.0), flash * 0.6);
    float alpha = (ring * 1.7 + flash * 1.3) * fade;
    gl_FragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}
