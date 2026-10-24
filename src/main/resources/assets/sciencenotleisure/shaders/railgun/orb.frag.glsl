#version 120
#include "noise.glsl"

uniform float time;

varying vec2 uv;
varying vec4 tint;

void main() {
    float longitude = uv.x * 6.2831853 + time * 3.2;
    float latitude = (uv.y - 0.5) * 3.1415927;
    float band = cos(latitude);
    vec2 point = vec2(cos(longitude) * band, sin(longitude) * band);

    float swirl = railgunFbm(point * 2.3 + vec2(latitude * 1.9 - time * 1.2));
    float cell = smoothstep(0.18, 0.86, swirl);
    float arcs = pow(max(0.0, sin(latitude * 7.0 + cell * 5.0 - time * 8.0)), 5.0);
    float intensity = 0.55 + 0.9 * cell + arcs * 0.4;
    vec3 color = mix(tint.rgb, vec3(1.0), clamp(cell * 1.2, 0.0, 1.0));

    gl_FragColor = vec4(color, clamp(intensity * tint.a, 0.0, 1.0));
}
