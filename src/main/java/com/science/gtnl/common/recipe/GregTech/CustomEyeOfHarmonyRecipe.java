package com.science.gtnl.common.recipe.GregTech;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.recipes.EyeOfHarmonyRecipeFactory;

import gregtech.api.enums.MaterialsUEVplus;

public class CustomEyeOfHarmonyRecipe {

    @SuppressWarnings("unchecked")
    public static void loadRecipes() {
        EyeOfHarmonyRecipeFactory.addCustomRecipeEntry(
            new ItemStack(Blocks.dirt, 1),
            Lists.newArrayList(
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L),
                Pair.of(GTNLItemList.StargateSingularity.get(1), 1145141919810L)),
            Lists.newArrayList(
                Pair.of(MaterialsUEVplus.SpaceTime.getMolten(Integer.MAX_VALUE), (long) Integer.MAX_VALUE),
                Pair.of(MaterialsUEVplus.SpaceTime.getMolten(Integer.MAX_VALUE), (long) Integer.MAX_VALUE),
                Pair.of(MaterialsUEVplus.SpaceTime.getMolten(Integer.MAX_VALUE), (long) Integer.MAX_VALUE)),
            1,
            1145141919810L,
            19198101919810L,
            1145141919810L,
            1145141919810L,
            114514,
            0.01);
    }
}
