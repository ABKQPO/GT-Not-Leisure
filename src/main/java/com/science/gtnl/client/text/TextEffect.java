package com.science.gtnl.client.text;

import com.science.gtnl.utils.ColorUtils;

/** A registered effect shades an existing mask without owning text layout. */
public interface TextEffect extends AutoCloseable {

    void render(TextRenderContext context);

    default float padding(float height) {
        return height;
    }

    default float padding(float width, float height) {
        return padding(height);
    }

    default int fallbackColor() {
        return ColorUtils.rgb(ColorUtils.effectWhite);
    }

    @Override
    default void close() {}
}
