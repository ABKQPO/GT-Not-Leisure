package com.science.gtnl.common.gui.recipe;

import javax.annotation.ParametersAreNonnullByDefault;

import com.science.gtnl.utils.recipes.format.SpaceMinerFormat;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpaceMinerFrontend extends GTNLRecipeFrontend {

    public SpaceMinerFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder.neiSpecialInfoFormatter(new SpaceMinerFormat()));
    }
}
