#version 120

varying vec2 texCoord;

uniform float u_Time;
uniform vec2 u_ScreenSize;
uniform float u_Intensity;

float hash(vec2 point) {
    point = fract(point * vec2(123.34, 456.21));
    point += dot(point, point + 45.32);
    return fract(point.x * point.y);
}

float hash21(vec2 point) {
    point = fract(point * vec2(234.34, 435.345));
    point += dot(point, point + 34.23);
    return fract(point.x * point.y);
}

float noise(vec2 point) {
    vec2 integerPart = floor(point);
    vec2 fractionalPart = fract(point);
    fractionalPart = fractionalPart * fractionalPart * (3.0 - 2.0 * fractionalPart);

    float lowerLeft = hash(integerPart);
    float lowerRight = hash(integerPart + vec2(1.0, 0.0));
    float upperLeft = hash(integerPart + vec2(0.0, 1.0));
    float upperRight = hash(integerPart + vec2(1.0, 1.0));

    return mix(
        mix(lowerLeft, lowerRight, fractionalPart.x),
        mix(upperLeft, upperRight, fractionalPart.x),
        fractionalPart.y);
}

float fbm(vec2 point) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int index = 0; index < 4; index++) {
        value += amplitude * noise(point);
        point *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

float coarseFbm(vec2 point) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int index = 0; index < 3; index++) {
        value += amplitude * noise(point);
        point *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

float stars(vec2 uv, float density, float seed) {
    vec2 gridPosition = fract(uv * density) - 0.5;
    vec2 gridId = floor(uv * density);
    float randomValue = hash21(gridId + seed);
    if (randomValue <= 0.8) {
        return 0.0;
    }
    float size = (randomValue - 0.8) * 5.0;
    return smoothstep(0.1 * size + 0.02, 0.0, length(gridPosition));
}

void main() {
    float time = u_Time;
    vec2 aspect = vec2(u_ScreenSize.x / u_ScreenSize.y, 1.0);
    vec2 centered = (texCoord - 0.5) * aspect;
    vec3 color = vec3(0.01, 0.01, 0.02);

    float nebulaOne = fbm(centered * 2.0 + vec2(time * 0.005, time * 0.003));
    float nebulaTwo = fbm(centered * 1.5 + vec2(-time * 0.004, time * 0.006));
    float nebulaThree = fbm(centered * 3.0 + vec2(time * 0.003, -time * 0.004));
    nebulaOne = pow(nebulaOne, 2.0) * smoothstep(0.3, 0.7, nebulaOne);
    nebulaTwo = pow(nebulaTwo, 2.5) * smoothstep(0.35, 0.75, nebulaTwo);
    nebulaThree = pow(nebulaThree, 2.0) * smoothstep(0.25, 0.6, nebulaThree);

    color += vec3(0.15, 0.05, 0.25) * nebulaOne * 0.6;
    color += vec3(0.05, 0.10, 0.20) * nebulaTwo * 0.5;
    color += vec3(0.20, 0.08, 0.12) * nebulaThree * 0.4;
    color += vec3(0.4, 0.2, 0.5) * pow(nebulaOne, 4.0) * 0.6;
    color += vec3(0.2, 0.3, 0.5) * pow(nebulaTwo, 4.0) * 0.375;

    float dust = smoothstep(0.4, 0.6, coarseFbm(centered * 4.0 + vec2(time * 0.002, 0.0)));
    color *= 1.0 - dust * 0.3;

    color += vec3(0.6, 0.6, 0.7) * stars(texCoord + vec2(0.0, time * 0.001), 80.0, 1.0) * 0.3;
    color += vec3(0.8, 0.8, 0.9) * stars(texCoord + vec2(time * 0.002, 0.0), 40.0, 2.0) * 0.5;
    float thirdStarLayer = stars(texCoord, 20.0, 3.0);
    float twinkle = sin(time * 0.5 + hash(floor(texCoord * 20.0)) * 6.28) * 0.3 + 0.7;
    color += vec3(1.0, 0.95, 0.9) * thirdStarLayer * twinkle * 0.8;

    float coloredStar = stars(texCoord + 0.5, 15.0, 4.0);
    vec3 starColor = mix(vec3(1.0, 0.7, 0.5), vec3(0.7, 0.8, 1.0), hash(floor(texCoord * 15.0 + 0.5)));
    color += starColor * coloredStar * 0.6;
    color += vec3(0.3, 0.25, 0.35) * exp(-length(centered) * 2.0) * 0.15;

    color *= sin(time * 0.1 + coarseFbm(centered * 5.0) * 6.28) * 0.02 + 1.0;
    float vignette = pow(clamp(1.0 - length(centered) * 0.4, 0.0, 1.0), 1.2);
    color *= 0.7 + vignette * 0.3;
    gl_FragColor = vec4(clamp(color * u_Intensity, 0.0, 1.0), 1.0);
}
