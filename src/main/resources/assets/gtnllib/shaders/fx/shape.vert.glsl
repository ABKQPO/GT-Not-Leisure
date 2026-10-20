#version 120

varying vec4 tint;
varying vec2 local;
varying vec2 uv;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    tint = gl_Color;
    local = gl_Vertex.xy;
    uv = gl_MultiTexCoord0.xy;
}
