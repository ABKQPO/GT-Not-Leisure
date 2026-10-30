#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    vec2 local = localUV();
    float row = floor(local.y * 22.0);
    float block = floor(time * 8.0);
    float jitter = (hash(vec2(row, block)) - 0.5) * textSize.y * 0.075;
    float red = coverage(point + vec2(jitter + textSize.y * 0.022, 0.0));
    float green = coverage(point + vec2(jitter, 0.0));
    float blue = coverage(point + vec2(jitter - textSize.y * 0.022, 0.0));
    float ink = max(red, max(green, blue));
    float scanline = 0.82 + 0.18 * sin(local.y * 140.0);
    float digitalBand = step(0.82, hash(vec2(floor(local.x * 11.0), row + block)));
    vec3 color = vec3(red, green, blue) * scanline;
    color += palette[min(paletteCount - 1, 2)] * digitalBand * ink * 0.38;
    float edge = outline(point + vec2(jitter, 0.0), textSize.y * 0.035);
    outputColor(clamp(color, 0.0, 1.0), max(ink, edge * 0.65));
}
