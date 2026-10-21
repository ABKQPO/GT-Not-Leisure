package com.gtnllib.gui.theme;

import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.widget.Widget;

public enum ButtonPurpose {

    NORMAL(IThemeApi.BUTTON),
    PRIMARY(IThemeApi.BUTTON.createSubKey("primary")),
    TAB(IThemeApi.TOGGLE_BUTTON),
    DANGER(IThemeApi.BUTTON.createSubKey("danger")),
    ICON(IThemeApi.BUTTON.createSubKey("icon"));

    public final WidgetThemeKey<?> key;

    ButtonPurpose(WidgetThemeKey<?> key) {
        this.key = key;
    }

    public <W extends Widget<?>> W apply(W widget) {
        widget.widgetTheme(key);
        return widget;
    }
}
