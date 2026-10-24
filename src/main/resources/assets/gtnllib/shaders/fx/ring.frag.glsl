#version 120
#include "edge.glsl"

uniform vec2 center;
uniform float innerRadius;
uniform float outerRadius;

varying vec4 tint;
varying vec2 local;

void main() {
    float dist = distance(local, center);
    float aa = edgeWidth(local);
    float mask = smoothstep(innerRadius - aa, innerRadius + aa, dist)
        * (1.0 - smoothstep(outerRadius - aa, outerRadius + aa, dist));
    gl_FragColor = vec4(tint.rgb, tint.a * mask);
}
