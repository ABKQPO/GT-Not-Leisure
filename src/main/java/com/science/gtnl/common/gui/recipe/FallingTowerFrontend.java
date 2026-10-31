package com.science.gtnl.common.gui.recipe;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.science.gtnl.common.gui.recipe.format.BloodSoulFormat;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FallingTowerFrontend extends GTNLRecipeFrontend {

    public FallingTowerFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(
            uiPropertiesBuilder.progressBarPos(position(26, 7)),
            neiPropertiesBuilder.recipeBackgroundSize(size(170, 190))
                .neiSpecialInfoFormatter(new BloodSoulFormat()));
    }

    @Override
    protected Slot logoSlot() {
        return new Slot(150, 7);
    }

    @Override
    protected List<Slot> itemInputSlots(int itemInputCount) {
        return Collections.singletonList(new Slot(6, 7));
    }

    @Override
    protected List<Slot> itemOutputSlots(int itemOutputCount) {
        ObjectArrayList<Slot> positions = new ObjectArrayList<>();
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                positions.add(new Slot((6 + 18 * i), (25 + 18 * j)));
            }
        }
        return positions;
    }

    @Override
    protected boolean borderless() {
        return true;
    }
}
