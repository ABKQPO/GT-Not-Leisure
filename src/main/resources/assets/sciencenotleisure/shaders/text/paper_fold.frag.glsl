#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float fold = point.x / max(3.0, textSize.y * 0.7) + time * 0.16;
    float facet = fract(fold);
    float lift = sin(facet * 3.14159265);
    float angle = sin(floor(fold) * 2.37 + time * 1.4);
    float bend = lift * angle * textSize.y * 0.045;
    float ink = max(coverage(point), coverage(point + vec2(bend, 0.0)) * 0.7);
    float crease = 1.0 - smoothstep(0.0, 0.08, min(facet, 1.0 - facet));
    vec3 paper = mix(palette[0], palette[1], 0.48 + 0.38 * angle * lift);
    paper = mix(paper, palette[2], max(0.0, angle) * lift * 0.48);
    paper *= 1.0 - crease * 0.27;
    outputColor(paper, ink);
}
