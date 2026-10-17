#version 120
#include "noise.glsl"

// Long beam plasma. uv.x runs across the ribbon, uv.y is the distance along the beam in blocks so the
// scrolling filaments keep a constant world scale regardless of the shot distance.

uniform float time;

varying vec2 uv;
varying vec4 tint;

void main() {
    float across = abs(uv.x * 2.0 - 1.0);
    float core = pow(1.0 - across, 6.0);
    float halo = pow(1.0 - across, 2.0);

    float along = uv.y * 0.35;
    float streams = railgunFbm(vec2(uv.x * 4.0 + uv.y * 0.2, along - time * 3.4));
    streams = pow(clamp(streams, 0.0, 1.0), 2.4);

    float pulse = 0.55 + 0.45 * sin(along * 4.0 - time * 9.0);
    float energy = streams * halo * pulse * 1.7;

    vec3 color = mix(tint.rgb, vec3(1.0), core * 0.8);
    color *= 1.0 + 0.35 * streams;

    float alpha = (core * 1.3 + energy + halo * 0.1) * tint.a;
    gl_FragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}
