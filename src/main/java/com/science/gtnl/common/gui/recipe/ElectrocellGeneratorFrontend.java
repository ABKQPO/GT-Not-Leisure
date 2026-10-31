package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.science.gtnl.utils.recipes.metadata.ElectrocellGeneratorMetadata;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElectrocellGeneratorFrontend extends GTNLRecipeFrontend {

    public ElectrocellGeneratorFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder.neiSpecialInfoFormatter(ElectrocellGeneratorMetadata.INSTANCE));
    }

    @Override
    protected List<Slot> itemInputSlots(int count) {
        return slots(43, 7, 115, 7);
    }

    @Override
    protected List<Slot> itemOutputSlots(int count) {
        return slots(79, 45);
    }

    @Override
    protected List<Slot> fluidInputSlots(int count) {
        return slots(79, 7);
    }

    @Override
    protected List<Slot> fluidOutputSlots(int count) {
        return slots(79, 63);
    }
}
