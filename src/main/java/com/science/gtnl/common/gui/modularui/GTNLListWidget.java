package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.widgets.ListWidget;

public class GTNLListWidget<I extends IWidget, W extends ListWidget<I, W>> extends ListWidget<I, W> {

    private int initialScrollY;

    public GTNLListWidget() {
        this(0);
    }

    public GTNLListWidget(int initialScrollY) {
        super();
        this.initialScrollY = initialScrollY;
    }

    @Override
    public void postResize() {
        super.postResize();
        if (initialScrollY > 0 && getScrollArea().getScrollY() != null) {
            getScrollArea().getScrollY()
                .scrollTo(getScrollArea(), initialScrollY);
            initialScrollY = 0;
        }
    }
}
