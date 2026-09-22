package com.science.gtnl.client.text;

/** A registered effect shades an existing mask without owning text layout. */
public interface TextEffect extends AutoCloseable {

    void render(TextRenderContext context);

    default float padding(float height) {
        return height;
    }

    default int fallbackColor() {
        return 0xFFFFFF;
    }

    @Override
    default void close() {}
}
