package com.science.gtnl.api.mixinHelper;

import java.util.Set;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.RecipeMap;

public interface IInterfaceNameHatch {

    void setInterfaceNameController(MTEMultiBlockBase controller);

    Set<MTEMultiBlockBase> getInterfaceNameControllers();

    RecipeMap<?> getInterfaceNameRecipeMap();

    default boolean handlesOwnInterfaceName() {
        return false;
    }
}
