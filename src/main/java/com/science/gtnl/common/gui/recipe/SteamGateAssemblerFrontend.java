package com.science.gtnl.common.gui.recipe;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SteamGateAssemblerFrontend extends GTNLRecipeFrontend {

    public SteamGateAssemblerFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(
            uiPropertiesBuilder.progressBarPos(position(124, 175)),
            neiPropertiesBuilder.recipeBackgroundSize(size(170, 196)),
            Logo.STEAM);
    }

    @Override
    protected Slot logoSlot() {
        return new Slot(97, 175);
    }

    @Override
    protected List<Slot> itemInputSlots(int itemInputCount) {
        ObjectArrayList<Slot> positions = new ObjectArrayList<>();
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                positions.add(new Slot((6 + 18 * i), (7 + 18 * j)));
            }
        }
        return positions;
    }

    @Override
    protected List<Slot> itemOutputSlots(int itemOutputCount) {
        return Collections.singletonList(new Slot(150, 175));
    }

    @Override
    protected boolean borderless() {
        return true;
    }
}
