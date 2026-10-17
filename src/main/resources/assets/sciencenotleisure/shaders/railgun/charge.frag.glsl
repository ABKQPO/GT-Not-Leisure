#version 120
#include "noise.glsl"

// Full screen charge overlay: converging rays, a breathing vignette and a drifting haze. progress scales the
// whole effect with the charge level so an empty charge stays visually silent.

uniform float time;
uniform float progress;
uniform vec2 screen;

varying vec2 uv;
varying vec4 tint;

void main() {
    vec2 point = (uv - 0.5) * vec2(screen.x / max(screen.y, 1.0), 1.0);
    float radius = length(point);
    float angle = atan(point.y, point.x);

    float edge = smoothstep(0.3, 0.95, radius);
    float rays = pow(max(0.0, sin(angle * 9.0 + time * 0.8)), 8.0);
    float haze = railgunFbm(vec2(angle * 2.2, radius * 3.5 - time * 2.0));

    float alpha = (edge * 0.22 + rays * edge * 0.4 + haze * edge * 0.16) * progress * tint.a;
    vec3 color = mix(tint.rgb, vec3(1.0), rays * 0.4);
    gl_FragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}
