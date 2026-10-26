package com.science.gtnl.common.gui.recipe;

import javax.annotation.ParametersAreNonnullByDefault;

import com.science.gtnl.common.gui.recipe.format.BloodSoulFormat;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BloodSoulFrontend extends GTNLRecipeFrontend {

    public BloodSoulFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder.neiSpecialInfoFormatter(new BloodSoulFormat()));
    }

}
