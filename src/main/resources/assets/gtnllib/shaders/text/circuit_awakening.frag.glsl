#version 120
#include "common.glsl"

void main() {
    if (drawShadow()) return;
    vec2 point = position();
    float ink = coverage(point);
    float unit = max(1.0, textSize.y);
    vec2 circuit = point / unit;
    float row = floor(circuit.y * 2.5);
    float lineY = abs(fract(circuit.y * 2.5) - 0.5);
    float track = 1.0 - smoothstep(0.12, 0.3, lineY);
    float junctionX = abs(fract(circuit.x * 1.8 + row * 0.37) - 0.5);
    float node = (1.0 - smoothstep(0.07, 0.21, junctionX)) * track;
    float pulse = exp(-abs(fract(circuit.x * 0.31 - time * 0.38 + row * 0.13) - 0.5) * 15.0);
    vec3 color = mix(palette[0], palette[1], track * 0.55);
    color = mix(color, palette[2], max(node * 0.55, track * pulse * 0.95));
    outputColor(color, ink);
}
