#version 120
#include "edge.glsl"

uniform vec2 center;
uniform float radius;

varying vec4 tint;
varying vec2 local;

void main() {
    float dist = distance(local, center);
    float aa = edgeWidth(local);
    gl_FragColor = vec4(tint.rgb, tint.a * (1.0 - smoothstep(radius - aa, radius + aa, dist)));
}
