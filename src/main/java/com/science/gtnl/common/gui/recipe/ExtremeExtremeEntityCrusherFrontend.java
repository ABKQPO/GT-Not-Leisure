package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtremeExtremeEntityCrusherFrontend extends GTNLRecipeFrontend {

    public ExtremeExtremeEntityCrusherFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(
            uiPropertiesBuilder.progressBarPos(position(26, 7)),
            neiPropertiesBuilder.recipeBackgroundSize(size(170, 100)));
    }

    @Override
    protected Slot logoSlot() {
        return new Slot(150, 102);
    }

    @Override
    protected List<Slot> itemInputSlots(int itemInputCount) {
        ObjectArrayList<Slot> positions = new ObjectArrayList<>();
        positions.add(new Slot(6, 7));
        return positions;
    }

    @Override
    protected List<Slot> itemOutputSlots(int itemOutputCount) {
        ObjectArrayList<Slot> positions = new ObjectArrayList<>();
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 9; i++) {
                positions.add(new Slot((6 + 18 * i), (25 + 18 * j)));
            }
        }
        return positions;
    }

    @Override
    protected List<Slot> fluidOutputSlots(int fluidOutputCount) {
        ObjectArrayList<Slot> positions = new ObjectArrayList<>();
        positions.add(new Slot(60, 7));
        return positions;
    }

    @Override
    protected boolean borderless() {
        return true;
    }
}
