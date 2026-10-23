package com.gtnllib.gui.settings;

import java.util.List;

import net.minecraft.util.MathHelper;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.layout.Size;
import com.gtnllib.gui.widget.LayoutTheme;

/**
 * Three whole-block X, Y and Z fields for editing a position, laid out as cells of a {@link Layout#line} row. A field
 * left untouched gives back the exact coordinate it started with; a typed block gives the middle of that block on X
 * and Z and its floor on Y.
 */
public class BlockPosFields {

    private static final String[] AXES = { "X", "Y", "Z" };
    private static final double[] MIDDLE = { 0.5, 0, 0.5 };

    private final double[] start;
    private final TextFieldWidget[] fields = new TextFieldWidget[AXES.length];

    public BlockPosFields(double x, double y, double z, int horizontal, int minY, int maxY) {
        start = new double[] { x, y, z };
        int[] low = { -horizontal, minY, -horizontal }, high = { horizontal, maxY, horizontal };
        for (int i = 0; i < AXES.length; i++) {
            // Why: a text field resets its text when the screen opens unless the starting text is its bound value.
            fields[i] = new TextFieldWidget().numbersInt(low[i], high[i])
                .value(new StringValue(String.valueOf(MathHelper.floor_double(start[i]))));
        }
    }

    /**
     * Appends an axis letter and a field for each of X, Y and Z, with the fields sharing the room that is left.
     */
    public void addTo(List<Size> sizes, List<Widget<?>> cells, LayoutTheme style) {
        for (int i = 0; i < AXES.length; i++) {
            sizes.add(Size.fixed(Layout.textWidth(AXES[i], style)));
            cells.add(Layout.label(IKey.str(AXES[i]), style));
            sizes.add(Size.fill());
            cells.add(fields[i]);
        }
    }

    public double x() {
        return value(0);
    }

    public double y() {
        return value(1);
    }

    public double z() {
        return value(2);
    }

    /**
     * Reads a typed whole block, keeping the exact start when the block is unchanged or the text is not a number.
     */
    public static double typed(String text, double start, double middle) {
        try {
            int block = Integer.parseInt(text.replaceAll("[^0-9-]", ""));
            return block == MathHelper.floor_double(start) ? start : block + middle;
        } catch (NumberFormatException e) {
            return start;
        }
    }

    private double value(int axis) {
        return typed(fields[axis].getText(), start[axis], MIDDLE[axis]);
    }
}
