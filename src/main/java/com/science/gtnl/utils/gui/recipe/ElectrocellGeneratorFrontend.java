package com.science.gtnl.utils.gui.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.metadata.ElectrocellGeneratorMetadata;

import codechicken.nei.PositionedStack;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.GTNEIDefaultHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElectrocellGeneratorFrontend extends RecipeMapFrontend {

    public static List<GTRecipe> initializedRecipes = new ArrayList<>();

    public ElectrocellGeneratorFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder.neiSpecialInfoFormatter(ElectrocellGeneratorMetadata.INSTANCE));
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(uiProperties.logoPos.add(windowOffset)));
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return ImmutableList.of(new Pos2d(43, 7), new Pos2d(115, 7));
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemInputCount) {
        return ImmutableList.of(new Pos2d(79, 45));
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidOutputCount) {
        return new ArrayList<>();
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int itemInputCount) {
        return ImmutableList.of(new Pos2d(79, 63), new Pos2d(79, 7));
    }

    @Override
    public void drawNEIOverlays(GTNEIDefaultHandler.CachedDefaultRecipe neiCachedRecipe) {
        final GTRecipe recipe = neiCachedRecipe.mRecipe;

        if (!initializedRecipes.contains(recipe)) {
            final FluidStack[] fluids = recipe.mFluidInputs;
            final int len = fluids.length;

            ItemStack[] itemStacks = new ItemStack[len];
            for (int i = 0; i < len; i++) {
                itemStacks[i] = GTUtility.getFluidDisplayStack(fluids[i], true);
            }

            PositionedStack stack = new PositionedStack(itemStacks, 75, -3, false);
            neiCachedRecipe.mInputs.add(stack);

            initializedRecipes.add(recipe);
        }

        super.drawNEIOverlays(neiCachedRecipe);
    }
}
