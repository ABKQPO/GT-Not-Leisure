package com.gtnllib.gui.widget;

import java.util.List;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.cleanroommc.modularui.api.IJsonSerializable;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.DrawableSerialization;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.GlStateManager;
import com.cleanroommc.modularui.utils.JsonBuilder;
import com.cleanroommc.modularui.utils.JsonHelper;
import com.cleanroommc.modularui.utils.Platform;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class SkinDrawable implements IDrawable, IJsonSerializable {

    public static final String TYPE = "gtnllib:skin";
    private static final String TEXTURE = "texture", GLOW = "glow", WIDTH = "imageWidth", HEIGHT = "imageHeight",
        LEFT = "borderLeft", TOP = "borderTop", RIGHT = "borderRight", BOTTOM = "borderBottom", TILED = "tiled",
        GLOW_COLOR = "glowColor";

    static {
        DrawableSerialization.registerDrawableType(TYPE, SkinDrawable.class, json -> new SkinDrawable());
    }

    private ResourceLocation texture, glow;
    private int width = 1, height = 1, left, top, right, bottom, glowColor;
    private boolean tiled;

    public SkinDrawable texture(ResourceLocation texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        return this;
    }

    public SkinDrawable border(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        return this;
    }

    public SkinDrawable tiled(boolean tiled) {
        this.tiled = tiled;
        return this;
    }

    public SkinDrawable glow(ResourceLocation glow) {
        this.glow = glow;
        return this;
    }

    public SkinDrawable glowColor(int color) {
        this.glowColor = color;
        return this;
    }

    public SkinDrawable copy() {
        return new SkinDrawable().texture(texture, width, height)
            .border(left, top, right, bottom)
            .tiled(tiled)
            .glow(glow)
            .glowColor(glowColor);
    }

    public static int[] fit(int size, int start, int end) {
        if (start + end <= size) return new int[] { start, end };
        int fitted = start + end > 0 ? size * start / (start + end) : 0;
        return new int[] { fitted, size - fitted };
    }

    public List<Piece> pieces(int x, int y, int w, int h) {
        List<Piece> pieces = new ObjectArrayList<>();
        int[] across = fit(w, left, right), down = fit(h, top, bottom);
        int[] xs = { x, x + across[0], x + w - across[1], x + w };
        int[] ys = { y, y + down[0], y + h - down[1], y + h };
        int[] us = { 0, left, width - right, width }, vs = { 0, top, height - bottom, height };
        for (int row = 0; row < 3; row++) for (int column = 0; column < 3; column++) {
            int dw = xs[column + 1] - xs[column], dh = ys[row + 1] - ys[row];
            int sw = us[column + 1] - us[column], sh = vs[row + 1] - vs[row];
            if (dw <= 0 || dh <= 0 || sw <= 0 || sh <= 0) continue;
            pieces.add(
                new Piece(
                    xs[column],
                    ys[row],
                    dw,
                    dh,
                    us[column] / (float) width,
                    vs[row] / (float) height,
                    us[column + 1] / (float) width,
                    vs[row + 1] / (float) height,
                    tiled && (column == 1 || row == 1) ? sw : 0,
                    tiled && (column == 1 || row == 1) ? sh : 0));
        }
        return pieces;
    }

    public static SkinDrawable read(JsonObject json, String key, SkinDrawable fallback) {
        if (!json.has(key)) return fallback;
        return DrawableSerialization.deserialize(json.get(key)) instanceof SkinDrawable skin ? skin : fallback;
    }

    public static void write(JsonBuilder json, String key, SkinDrawable skin) {
        if (skin != null) json.add(key, DrawableSerialization.serialize(skin));
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        draw(x, y, width, height, glowColor);
    }

    public void draw(int x, int y, int width, int height, int glowColor) {
        if (texture == null || width <= 0 || height <= 0) return;
        List<Piece> pieces = pieces(x, y, width, height);
        layer(texture, Color.WHITE.main, pieces);
        if (glow != null && glowColor != 0) layer(glow, glowColor, pieces);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    private static void layer(ResourceLocation location, int color, List<Piece> pieces) {
        Platform.setupDrawTex(true);
        Minecraft.getMinecraft().renderEngine.bindTexture(location);
        Color.setGlColor(color);
        Platform.startDrawing(Platform.DrawMode.QUADS, Platform.VertexFormat.POS_TEX, buffer -> {
            for (Piece piece : pieces) {
                if (piece.tileWidth() > 0) GuiDraw.drawTiledTexture(
                    buffer,
                    piece.x(),
                    piece.y(),
                    piece.w(),
                    piece.h(),
                    piece.u0(),
                    piece.v0(),
                    piece.u1(),
                    piece.v1(),
                    piece.tileWidth(),
                    piece.tileHeight(),
                    0);
                else GuiDraw.drawTexture(
                    buffer,
                    piece.x(),
                    piece.y(),
                    piece.x() + piece.w(),
                    piece.y() + piece.h(),
                    piece.u0(),
                    piece.v0(),
                    piece.u1(),
                    piece.v1(),
                    0);
            }
        });
    }

    @Override
    public void loadFromJson(JsonObject json) {
        String location = JsonHelper.getString(json, null, TEXTURE);
        if (location != null) texture = new ResourceLocation(location);
        String glowLocation = JsonHelper.getString(json, null, GLOW);
        if (glowLocation != null) glow = glowLocation.isEmpty() ? null : new ResourceLocation(glowLocation);
        width = JsonHelper.getInt(json, width, WIDTH);
        height = JsonHelper.getInt(json, height, HEIGHT);
        left = JsonHelper.getInt(json, left, LEFT);
        top = JsonHelper.getInt(json, top, TOP);
        right = JsonHelper.getInt(json, right, RIGHT);
        bottom = JsonHelper.getInt(json, bottom, BOTTOM);
        tiled = JsonHelper.getBoolean(json, tiled, TILED);
        glowColor = JsonHelper.getColor(json, glowColor, GLOW_COLOR);
    }

    @Override
    public boolean saveToJson(JsonObject json) {
        if (texture != null) json.addProperty(TEXTURE, texture.toString());
        json.addProperty(GLOW, glow == null ? "" : glow.toString());
        json.addProperty(WIDTH, width);
        json.addProperty(HEIGHT, height);
        json.addProperty(LEFT, left);
        json.addProperty(TOP, top);
        json.addProperty(RIGHT, right);
        json.addProperty(BOTTOM, bottom);
        json.addProperty(TILED, tiled);
        json.addProperty(GLOW_COLOR, glowColor);
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SkinDrawable skin && Objects.equals(texture, skin.texture)
            && Objects.equals(glow, skin.glow)
            && width == skin.width
            && height == skin.height
            && left == skin.left
            && top == skin.top
            && right == skin.right
            && bottom == skin.bottom
            && tiled == skin.tiled
            && glowColor == skin.glowColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(texture, glow, width, height, left, top, right, bottom, tiled, glowColor);
    }

    public record Piece(int x, int y, int w, int h, float u0, float v0, float u1, float v1, int tileWidth,
        int tileHeight) {}
}
