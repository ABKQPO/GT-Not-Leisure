uniform sampler2D textMask;
uniform vec2 maskSize;
uniform float maskResolution;
uniform vec2 textSize;
uniform vec2 textOrigin;
uniform float padding;
uniform float time;
uniform float opacity;
uniform bool shadowPass;
uniform int paletteCount;
uniform bool paletteOverridden;
uniform vec3 palette[8];
varying vec2 uv;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 base = floor(p);
    vec2 weight = fract(p);
    weight = weight * weight * (3.0 - 2.0 * weight);
    return mix(mix(hash(base), hash(base + vec2(1.0, 0.0)), weight.x),
        mix(hash(base + vec2(0.0, 1.0)), hash(base + vec2(1.0)), weight.x), weight.y);
}

vec2 position() {
    return vec2(uv.x, 1.0 - uv.y) * maskSize - padding;
}

vec2 localUV() {
    return position() / textSize;
}

float coverage(vec2 point) {
    vec2 coord = (point + padding) / maskSize;
    if (coord.x < 0.0 || coord.y < 0.0 || coord.x > 1.0 || coord.y > 1.0) return 0.0;
    return texture2D(textMask, vec2(coord.x, 1.0 - coord.y)).a;
}

vec3 gradient(float value) {
    float scaled = clamp(value, 0.0, 0.99999) * max(1.0, float(paletteCount - 1));
    int first = int(floor(scaled));
    return mix(palette[first], palette[(first + 1 < paletteCount ? first + 1 : paletteCount - 1)], fract(scaled));
}

vec3 cycle(float value) {
    float scaled = fract(value) * float(paletteCount);
    int first = int(floor(scaled));
    int second = first + 1;
    if (second >= paletteCount) second = 0;
    return mix(palette[first], palette[second], fract(scaled));
}

float outline(vec2 point, float radius) {
    float result = 0.0;
    for (int i = 0; i < 12; i++) {
        float angle = float(i) * 0.5235987756;
        result = max(result, coverage(point + vec2(cos(angle), sin(angle)) * radius));
    }
    return result;
}

bool drawShadow(vec2 point) {
    if (!shadowPass) return false;
    float alpha = coverage(point) * opacity * 0.65;
    if (alpha <= 0.001) discard;
    gl_FragColor = vec4(vec3(0.035), alpha);
    return true;
}

bool drawShadow() {
    return drawShadow(position());
}

void outputColor(vec3 rgb, float alpha) {
    if (alpha * opacity <= 0.001) discard;
    gl_FragColor = vec4(rgb, clamp(alpha * opacity, 0.0, 1.0));
}
