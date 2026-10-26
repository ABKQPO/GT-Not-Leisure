#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float facetSize = max(3.0, textSize.y * 0.48);
    vec2 grid = point / facetSize;
    vec2 cell = floor(grid);
    vec2 within = fract(grid);
    float diagonal = within.x + within.y - 1.0;
    float side = step(0.0, diagonal);
    float facet = hash(cell * 1.7 + side * 7.0);
    float edge = 1.0 - smoothstep(0.0, 0.075, abs(diagonal));
    float motion = sin(time * 1.4 + facet * 6.28318);
    vec2 refraction = vec2(motion, -motion * 0.45) * textSize.y * 0.012;
    float ink = max(coverage(point), coverage(point + refraction) * 0.8);
    vec3 color = mix(gradient(facet * 0.72 + 0.1), palette[3], edge * 0.58);
    color *= 0.79 + 0.21 * max(0.0, motion);
    outputColor(color, ink);
}
