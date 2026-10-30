#version 120
#include "edge.glsl"

uniform sampler2D blurred;
uniform vec2 viewSize;
uniform vec2 center;
uniform float radius;

varying vec4 tint;
varying vec2 local;

void main() {
    float dist = distance(local, center);
    float aa = edgeWidth(local);
    float mask = 1.0 - smoothstep(radius - aa, radius + aa, dist);
    vec3 scene = texture2D(blurred, gl_FragCoord.xy / viewSize).rgb;
    gl_FragColor = vec4(scene * tint.rgb, mask * tint.a);
}
