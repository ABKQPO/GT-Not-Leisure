package com.gtnllib.gui.widget;

import com.cleanroommc.modularui.api.IJsonSerializable;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.DrawableSerialization;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.JsonHelper;
import com.google.gson.JsonObject;

public class CardDrawable implements IDrawable, IJsonSerializable {

    public static final String TYPE = "gtnllib:card";

    static {
        DrawableSerialization.registerDrawableType(TYPE, CardDrawable.class, json -> new CardDrawable());
    }

    public static final WidgetThemeKey<WidgetTheme> KEY = IThemeApi.get()
        .widgetThemeKeyBuilder("gtnllibCard", WidgetTheme.class)
        .defaultTheme(
            new WidgetTheme(
                0,
                0,
                new CardDrawable().fill(0x50000000)
                    .edge(0x24FFFFFF)
                    .accent(0xFFDDDDDD, 3, 2, 10),
                Color.WHITE.main,
                Color.WHITE.main,
                false,
                Color.WHITE.main))
        .register();

    private int fill, edge, accent, accentTop, accentWidth, accentHeight;

    public CardDrawable fill(int color) {
        this.fill = color;
        return this;
    }

    public CardDrawable edge(int color) {
        this.edge = color;
        return this;
    }

    public CardDrawable accent(int color, int top, int width, int height) {
        this.accent = color;
        this.accentTop = top;
        this.accentWidth = width;
        this.accentHeight = height;
        return this;
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        GuiDraw.drawRect(x, y, width, height, fill);
        GuiDraw.drawRect(x, y, width, 1, edge);
        GuiDraw.drawRect(x, y + height - 1, width, 1, edge);
        if (accentWidth > 0 && accentHeight > 0) GuiDraw.drawRect(x, y + accentTop, accentWidth, accentHeight, accent);
    }

    @Override
    public void loadFromJson(JsonObject json) {
        fill = JsonHelper.getColor(json, fill, "color");
        edge = JsonHelper.getColor(json, edge, "edgeColor");
        accent = JsonHelper.getColor(json, accent, "accentColor");
        accentTop = JsonHelper.getInt(json, accentTop, "accentTop");
        accentWidth = JsonHelper.getInt(json, accentWidth, "accentWidth");
        accentHeight = JsonHelper.getInt(json, accentHeight, "accentHeight");
    }

    @Override
    public boolean saveToJson(JsonObject json) {
        json.addProperty("color", fill);
        json.addProperty("edgeColor", edge);
        json.addProperty("accentColor", accent);
        json.addProperty("accentTop", accentTop);
        json.addProperty("accentWidth", accentWidth);
        json.addProperty("accentHeight", accentHeight);
        return true;
    }
}
