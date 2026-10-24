package com.gtnllib.gui.widget;

import com.cleanroommc.modularui.api.IJsonSerializable;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.DrawableSerialization;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.JsonHelper;
import com.google.gson.JsonObject;

public class FrameDrawable implements IDrawable, IJsonSerializable {

    public static final String TYPE = "gtnllib:frame";

    static {
        DrawableSerialization.registerDrawableType(TYPE, FrameDrawable.class, json -> new FrameDrawable());
    }

    private int top, bottom, border, corner, cornerLength, cornerThickness;

    public FrameDrawable fill(int color) {
        return fill(color, color);
    }

    public FrameDrawable fill(int top, int bottom) {
        this.top = top;
        this.bottom = bottom;
        return this;
    }

    public FrameDrawable border(int color) {
        this.border = color;
        return this;
    }

    public FrameDrawable corners(int color, int length, int thickness) {
        this.corner = color;
        this.cornerLength = length;
        this.cornerThickness = thickness;
        return this;
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        GuiDraw.drawRect(x, y, width, height, top, top, bottom, bottom);
        if (border != 0) {
            GuiDraw.drawRect(x, y, width, 1, border);
            GuiDraw.drawRect(x, y + height - 1, width, 1, border);
            GuiDraw.drawRect(x, y + 1, 1, height - 2, border);
            GuiDraw.drawRect(x + width - 1, y + 1, 1, height - 2, border);
        }
        if (cornerLength <= 0 || cornerThickness <= 0) return;
        for (int index = 0; index < 4; index++) {
            boolean left = index % 2 == 0, upper = index < 2;
            GuiDraw.drawRect(
                left ? x : x + width - cornerLength,
                upper ? y : y + height - cornerThickness,
                cornerLength,
                cornerThickness,
                corner);
            GuiDraw.drawRect(
                left ? x : x + width - cornerThickness,
                upper ? y : y + height - cornerLength,
                cornerThickness,
                cornerLength,
                corner);
        }
    }

    @Override
    public void loadFromJson(JsonObject json) {
        if (json.has("color")) fill(JsonHelper.getColor(json, top, "color"));
        top = JsonHelper.getColor(json, top, "colorTop");
        bottom = JsonHelper.getColor(json, bottom, "colorBottom");
        border = JsonHelper.getColor(json, border, "borderColor");
        corner = JsonHelper.getColor(json, corner, "cornerColor");
        cornerLength = JsonHelper.getInt(json, cornerLength, "cornerLength");
        cornerThickness = JsonHelper.getInt(json, cornerThickness, "cornerThickness");
    }

    @Override
    public boolean saveToJson(JsonObject json) {
        json.addProperty("colorTop", top);
        json.addProperty("colorBottom", bottom);
        json.addProperty("borderColor", border);
        json.addProperty("cornerColor", corner);
        json.addProperty("cornerLength", cornerLength);
        json.addProperty("cornerThickness", cornerThickness);
        return true;
    }
}
