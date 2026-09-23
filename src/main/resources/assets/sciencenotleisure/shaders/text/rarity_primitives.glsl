// Shared sprite-style primitives; colors are accumulated in premultiplied form.
float glyphCenterX(vec2 point) {
    vec2 coord = (point + padding) / maskSize;
    return glyphMetadata(vec2(coord.x, 1.0 - coord.y)).b * textSize.x;
}

float glyphStartX(vec2 point) {
    vec2 coord = vec2((point.x + padding) / maskSize.x, 1.0 - 0.5 / (maskSize.y * maskResolution));
    return glyphMetadata(coord).b * textSize.x;
}

vec2 turn(vec2 point, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return mat2(c, -s, s, c) * point;
}

float starLight(vec2 delta, vec2 radius) {
    vec2 q = abs(delta) / max(radius, vec2(0.001));
    return exp(-q.x * 14.0 - q.y * 2.0) + exp(-q.y * 14.0 - q.x * 2.0);
}

float textBloom(vec2 point) {
    vec2 q = (point - textSize * vec2(0.5, 0.333))
        / vec2(textSize.x * 0.6 + textSize.y * 0.2, textSize.y * 0.7);
    vec2 edgeDistance = min(point + padding, textSize + padding - point);
    vec2 fade = smoothstep(vec2(0.0), vec2(max(1.0, padding * 0.9)), edgeDistance);
    return exp(-dot(q, q) * 3.0) * fade.x * fade.y;
}

float orbitCoverage(vec2 point, float radius, float rotation) {
    float sum = 0.0;
    for (int i = 0; i < 8; i++) {
        float angle = float(i) * 0.79 + rotation;
        sum += coverage(point + vec2(cos(angle), sin(angle)) * radius);
    }
    return sum;
}

void overRarity(inout vec4 layer, vec3 color, float alpha) {
    alpha = clamp(alpha, 0.0, 1.0);
    layer.rgb = color * alpha + layer.rgb * (1.0 - alpha);
    layer.a = alpha + layer.a * (1.0 - alpha);
}

void outputRarity(vec4 layer) {
    layer *= opacity;
    if (max(layer.a, max(layer.r, max(layer.g, layer.b))) <= 0.001) discard;
    gl_FragColor = layer;
}
