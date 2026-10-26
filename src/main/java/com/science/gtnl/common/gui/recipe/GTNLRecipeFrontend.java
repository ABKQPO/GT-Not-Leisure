package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.github.bsideup.jabel.Desugar;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// GT's frontend API still takes ModularUI 1 types. Keep them in this class so that pages describe slots in plain
// numbers and a GT move to ModularUI 2 only changes this file.
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTNLRecipeFrontend extends RecipeMapFrontend {

    public enum Logo {
        GTNL,
        STEAM
    }

    @Desugar
    public record Slot(int x, int y) {}

    private final Logo logo;

    public GTNLRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        this(uiPropertiesBuilder, neiPropertiesBuilder, Logo.GTNL);
    }

    public GTNLRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder, Logo logo) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
        this.logo = logo;
    }

    public static List<Slot> grid(int count, int x, int y, int columns) {
        List<Slot> slots = new ObjectArrayList<>();
        for (Pos2d pos : UIHelper.getGridPositions(count, x, y, columns)) slots.add(new Slot(pos.x, pos.y));
        return slots;
    }

    public static List<Slot> slots(int... coordinates) {
        List<Slot> slots = new ObjectArrayList<>();
        for (int i = 0; i + 1 < coordinates.length; i += 2) slots.add(new Slot(coordinates[i], coordinates[i + 1]));
        return slots;
    }

    public static Size size(int width, int height) {
        return new Size(width, height);
    }

    protected Slot logoSlot() {
        return new Slot(uiProperties.logoPos.x, uiProperties.logoPos.y);
    }

    protected List<Slot> itemInputSlots(int count) {
        return null;
    }

    protected List<Slot> itemOutputSlots(int count) {
        return null;
    }

    protected List<Slot> fluidInputSlots(int count) {
        return null;
    }

    protected List<Slot> fluidOutputSlots(int count) {
        return null;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        Slot at = logoSlot();
        builder.widget(
            new DrawableWidget()
                .setDrawable(logo == Logo.STEAM ? ItemUtils.PICTURE_GTNL_STEAM_LOGO : ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(new Pos2d(at.x(), at.y()).add(windowOffset)));
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        List<Slot> slots = itemInputSlots(itemInputCount);
        return slots == null ? super.getItemInputPositions(itemInputCount) : positions(slots);
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        List<Slot> slots = itemOutputSlots(itemOutputCount);
        return slots == null ? super.getItemOutputPositions(itemOutputCount) : positions(slots);
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        List<Slot> slots = fluidInputSlots(fluidInputCount);
        return slots == null ? super.getFluidInputPositions(fluidInputCount) : positions(slots);
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        List<Slot> slots = fluidOutputSlots(fluidOutputCount);
        return slots == null ? super.getFluidOutputPositions(fluidOutputCount) : positions(slots);
    }

    private static List<Pos2d> positions(List<Slot> slots) {
        List<Pos2d> result = new ObjectArrayList<>(slots.size());
        for (Slot slot : slots) result.add(new Pos2d(slot.x(), slot.y()));
        return result;
    }
}
