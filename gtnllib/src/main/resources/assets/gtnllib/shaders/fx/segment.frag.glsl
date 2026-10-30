#version 120
#include "edge.glsl"

uniform vec2 from;
uniform vec2 to;
uniform float lineWidth;

varying vec4 tint;
varying vec2 local;

void main() {
    vec2 segment = to - from;
    float length2 = dot(segment, segment);
    float t = length2 > 0.0 ? clamp(dot(local - from, segment) / length2, 0.0, 1.0) : 0.0;
    float dist = distance(local, from + segment * t);
    float aa = edgeWidth(local);
    float halfWidth = lineWidth * 0.5;
    gl_FragColor = vec4(tint.rgb, tint.a * (1.0 - smoothstep(halfWidth - aa, halfWidth + aa, dist)));
}
