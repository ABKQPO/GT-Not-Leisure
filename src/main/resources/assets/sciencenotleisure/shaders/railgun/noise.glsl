// Value noise shared by the railgun effects. Included after the #version directive of each fragment shader.

float railgunHash(vec2 point) {
    return fract(sin(dot(point, vec2(127.1, 311.7))) * 43758.5453);
}

float railgunNoise(vec2 point) {
    vec2 cell = floor(point);
    vec2 local = fract(point);
    vec2 smoothLocal = local * local * (3.0 - 2.0 * local);
    float cornerA = railgunHash(cell);
    float cornerB = railgunHash(cell + vec2(1.0, 0.0));
    float cornerC = railgunHash(cell + vec2(0.0, 1.0));
    float cornerD = railgunHash(cell + vec2(1.0, 1.0));
    return mix(mix(cornerA, cornerB, smoothLocal.x), mix(cornerC, cornerD, smoothLocal.x), smoothLocal.y);
}

float railgunFbm(vec2 point) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int octave = 0; octave < 3; octave++) {
        value += amplitude * railgunNoise(point);
        point *= 2.03;
        amplitude *= 0.5;
    }
    return value;
}
