#version 120

uniform sampler2D source;
uniform vec2 sampleStep;

varying vec2 uv;

void main() {
    vec4 sum = texture2D(source, uv) * 0.227027;
    sum += texture2D(source, uv + sampleStep) * 0.1945946;
    sum += texture2D(source, uv - sampleStep) * 0.1945946;
    sum += texture2D(source, uv + sampleStep * 2.0) * 0.1216216;
    sum += texture2D(source, uv - sampleStep * 2.0) * 0.1216216;
    sum += texture2D(source, uv + sampleStep * 3.0) * 0.054054;
    sum += texture2D(source, uv - sampleStep * 3.0) * 0.054054;
    sum += texture2D(source, uv + sampleStep * 4.0) * 0.016216;
    sum += texture2D(source, uv - sampleStep * 4.0) * 0.016216;
    gl_FragColor = vec4(sum.rgb, 1.0);
}
