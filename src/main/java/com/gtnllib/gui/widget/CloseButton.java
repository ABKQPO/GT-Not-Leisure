package com.gtnllib.gui.widget;

import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widgets.ButtonWidget;

public class CloseButton extends ButtonWidget<CloseButton> {

    public static final int SIZE = 11;

    private Runnable action = () -> getPanel().closeIfOpen();

    public CloseButton() {
        widgetTheme(IThemeApi.CLOSE_BUTTON);
        overlay(IKey.str("×"));
        size(SIZE, SIZE);
        onMousePressed(mouse -> {
            if (mouse != 0) return false;
            action.run();
            return true;
        });
    }

    public CloseButton action(Runnable action) {
        this.action = action;
        return this;
    }

    public CloseButton corner(int padding, LayoutTheme style) {
        int inset = inset(padding, style);
        return top(inset).right(inset);
    }

    public static int inset(int padding, LayoutTheme style) {
        return Math.max(0, padding - style.spacingSmall);
    }

    public static int reserve(int padding, LayoutTheme style) {
        return inset(padding, style) + SIZE + style.spacingSmall - padding;
    }
}
