package com.science.gtnl.api.stellar;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.FluidDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;

public class StellarIrisNodeDisplay {

    private final String text;
    private final IDrawable icon;

    private StellarIrisNodeDisplay(String text, IDrawable icon) {
        this.text = text;
        this.icon = icon;
    }

    public static StellarIrisNodeDisplay text(String text) {
        return text == null || text.isEmpty() ? null : new StellarIrisNodeDisplay(text, null);
    }

    public static StellarIrisNodeDisplay item(ItemStack itemStack) {
        return itemStack == null ? null : new StellarIrisNodeDisplay(null, new ItemDrawable(itemStack.copy()));
    }

    public static StellarIrisNodeDisplay fluid(FluidStack fluidStack) {
        return fluidStack == null ? null : new StellarIrisNodeDisplay(null, new FluidDrawable(fluidStack.copy()));
    }

    public static StellarIrisNodeDisplay icon(IDrawable icon) {
        return icon == null ? null : new StellarIrisNodeDisplay(null, icon);
    }

    public String getText() {
        return text;
    }

    public IDrawable getIcon() {
        return icon;
    }

    public boolean isText() {
        return text != null;
    }
}
