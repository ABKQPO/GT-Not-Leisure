float edgeWidth(vec2 position) {
    return max(1.25 * length(dFdx(position)), 0.0001);
}
