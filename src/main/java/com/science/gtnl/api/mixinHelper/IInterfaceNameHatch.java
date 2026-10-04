package com.science.gtnl.api.mixinHelper;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.RecipeMap;

public interface IInterfaceNameHatch {

    void setInterfaceNameController(MTEMultiBlockBase controller);

    RecipeMap<?> getInterfaceNameRecipeMap();

    default boolean handlesOwnInterfaceName() {
        return false;
    }
}
