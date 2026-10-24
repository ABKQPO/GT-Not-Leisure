#version 120
#include "edge.glsl"

const float PI = 3.14159265358979;
const float TAU = 6.28318530717959;

uniform vec2 center;
uniform float innerRadius;
uniform float outerRadius;
uniform float centerAngle;
uniform float halfRange;

varying vec4 tint;
varying vec2 local;

void main() {
    vec2 offset = local - center;
    float dist = length(offset);
    float aa = edgeWidth(local);
    float radial = smoothstep(innerRadius - aa, innerRadius + aa, dist)
        * (1.0 - smoothstep(outerRadius - aa, outerRadius + aa, dist));
    float angle = atan(offset.x, -offset.y);
    float diff = abs(mod(angle - centerAngle + PI, TAU) - PI);
    float angleAa = max(min(0.06, fwidth(diff) * 1.5), 0.0001);
    float angular = 1.0 - smoothstep(halfRange - angleAa, halfRange + angleAa, diff);
    gl_FragColor = vec4(tint.rgb, tint.a * radial * angular);
}
