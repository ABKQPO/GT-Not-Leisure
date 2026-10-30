package com.gtnllib.gui.widget;

import java.util.List;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.value.IValue;
import com.cleanroommc.modularui.widgets.menu.DropdownWidget;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class CycleDropdown<T> extends DropdownWidget<T, CycleDropdown<T>> {

    private final List<T> choices = new ObjectArrayList<>();

    public CycleDropdown(String panelName, Class<T> valueType) {
        super(panelName, valueType);
    }

    public CycleDropdown<T> choices(List<T> choices) {
        if (this.choices.equals(choices)) return this;
        this.choices.clear();
        this.choices.addAll(choices);
        clearOptions().options(choices);
        deleteMenu();
        return this;
    }

    @Override
    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
        IValue<T> value = getValue();
        if (value == null || choices.isEmpty()) return false;
        int index = Math.max(0, choices.indexOf(value.getValue()));
        setValue(choices.get(Math.floorMod(index - scrollDirection.modifier, choices.size())), true);
        return true;
    }
}
