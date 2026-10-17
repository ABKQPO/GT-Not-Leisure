#version 120
#include "noise.glsl"

// Charge orb: hot core, turbulent shell, rotating rings and voltage spokes.
// The charge level arrives as the vertex alpha and the tint carries the tier palette, so every orb shares a
// single draw call and the pattern stays stable while the orb moves.

uniform float time;

varying vec2 uv;
varying vec4 tint;

void main() {
    vec2 point = uv * 2.0 - 1.0;
    float radius = length(point);
    if (radius > 1.0) discard;

    float strength = tint.a;
    float angle = atan(point.y, point.x);

    float swirl = railgunFbm(vec2(angle * 1.6 + 11.0, radius * 3.0 - time * 2.2));
    float turbulence = smoothstep(0.2, 0.9, swirl);

    float core = pow(max(0.0, 1.0 - radius), 2.6);
    float shell = smoothstep(0.35, 1.0, radius) * turbulence;
    float rings = pow(max(0.0, sin(radius * 16.0 - time * 5.0)), 6.0) * core;
    float spokes = pow(max(0.0, sin(angle * 7.0 + time * 2.6)), 10.0) * smoothstep(0.25, 0.8, radius);

    float intensity = core * 1.6 + shell * 0.85 + rings * 0.55 + spokes * strength * 0.9;
    intensity *= 1.0 - smoothstep(0.72, 1.0, radius);

    vec3 color = mix(tint.rgb, vec3(1.0), core);
    gl_FragColor = vec4(color, clamp(intensity * (0.55 + 0.45 * strength), 0.0, 1.0));
}
