#version 120

uniform vec2 center;
uniform float radius;

varying vec4 tint;
varying vec2 local;

void main() {
    float dist = distance(local, center);
    gl_FragColor = vec4(tint.rgb, tint.a * (1.0 - smoothstep(0.0, radius, dist)));
}
