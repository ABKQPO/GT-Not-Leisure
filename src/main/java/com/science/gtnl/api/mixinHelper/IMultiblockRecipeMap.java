package com.science.gtnl.api.mixinHelper;

import gregtech.api.recipe.RecipeMap;

public interface IMultiblockRecipeMap {

    default void gtnl$setRecipeMapName(String recipeMap) {}

    default String gtnl$getRecipeMapName() {
        return "";
    }

    default void setRecipeMap(RecipeMap<?> recipeMap) {}

    default RecipeMap<?> getRecipeMap() {
        return null;
    }
}
