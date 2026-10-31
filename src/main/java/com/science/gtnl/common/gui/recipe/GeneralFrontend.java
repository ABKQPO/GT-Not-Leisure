package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GeneralFrontend extends GTNLRecipeFrontend {

    public static final int COLUMNS = 4;
    public static final int TOP = 8;
    private final int itemRowCount;

    public GeneralFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
        this.itemRowCount = getItemRowCount();
    }

    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        int itemRowCount = getItemRowCount();
        return neiPropertiesBuilder.recipeBackgroundSize(size(170, 10 + (itemRowCount + getFluidRowCount()) * 18));
    }

    @Override
    protected Slot logoSlot() {
        return new Slot(79, 7);
    }

    private int getItemRowCount() {
        return (Math.max(uiProperties.maxItemInputs, uiProperties.maxItemOutputs) - 1) / COLUMNS + 1;
    }

    private int getFluidRowCount() {
        return (Math.max(uiProperties.maxFluidInputs, uiProperties.maxFluidOutputs) - 1) / COLUMNS + 1;
    }

    @Override
    protected List<Slot> itemInputSlots(int count) {
        return grid(count, 6, TOP, COLUMNS);
    }

    @Override
    protected List<Slot> itemOutputSlots(int count) {
        return grid(count, 98, TOP, COLUMNS);
    }

    @Override
    protected List<Slot> fluidInputSlots(int count) {
        return grid(count, 6, TOP + itemRowCount * 18, COLUMNS);
    }

    @Override
    protected List<Slot> fluidOutputSlots(int count) {
        return grid(count, 98, TOP + itemRowCount * 18, COLUMNS);
    }
}
