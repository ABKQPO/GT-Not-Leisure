package com.science.gtnl.common.gui.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.TieredVariant;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SteamGateAssemblerFrontend extends RecipeMapFrontend {

    /**
     * The 9x9 item input grid occupies y 7-169. The progress bar, the GTNL logo and the output slot are placed at
     * y=175 and reach down to y=193, so the recipe background has to cover that row as well. With the old value of
     * 170 the whole bottom row was drawn outside the recipe window, right on top of the NEI info lines
     * (total EU / usage / duration) that start below the background.
     */
    public static final int RECIPE_BACKGROUND_HEIGHT = 196;

    /**
     * NEI only hit-tests the slots inside the area declared by the NEI handler info, and GT registers every recipe
     * handler with a height of 135. The last input row and the output slot are drawn below that line, but NEI never
     * reports them under the mouse, so they cannot be clicked, hovered or shown in tooltips. The map registration has
     * to override the height with this value.
     * <p>
     * The output slot ends at NEI-space y = 175 - 11 (NEI window offset) + 18 = 182. NEI draws its favorite button
     * 31 px above the handler bottom and the overlay button 18 px above it, so a height of 213 keeps both buttons
     * clear of the output slot. Otherwise clicking the right half of the product would toggle a NEI button instead.
     */
    public static final int NEI_HANDLER_HEIGHT = 213;

    public SteamGateAssemblerFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(
            uiPropertiesBuilder.progressBarPos(new Pos2d(124, 175)),
            neiPropertiesBuilder.recipeBackgroundSize(new Size(170, RECIPE_BACKGROUND_HEIGHT)));
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_STEAM_LOGO)
                .setSize(18, 18)
                .setPos(new Pos2d(97, 175).add(windowOffset)));
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        ArrayList<Pos2d> positions = new ArrayList<>();
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                positions.add(new Pos2d((6 + 18 * i), (7 + 18 * j)));
            }
        }
        return positions;
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return Collections.singletonList(new Pos2d(150, 175));
    }

    @Override
    public ModularWindow.Builder createNEITemplate(GTNEIDefaultHandler.NEITemplateContext ctx) {
        // Remove the regular background texture with the border.
        ModularWindow.Builder builder = ModularWindow.builder(neiProperties.recipeBackgroundSize);

        if (uiProperties.useProgressBar) {
            addProgressBar(builder, ctx);
        }

        UIHelper.forEachSlots(
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(ctx.itemInputsInventory, i)
                    .setBackground(backgrounds)
                    .setPos(pos)
                    .setSize(18, 18)),
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(ctx.itemOutputsInventory, i)
                    .setBackground(backgrounds)
                    .setPos(pos)
                    .setSize(18, 18)),
            (i, backgrounds, pos) -> {
                if (uiProperties.useSpecialSlot) builder.widget(
                    SlotWidget.phantom(ctx.specialSlotInventory, 0)
                        .setBackground(backgrounds)
                        .setPos(pos)
                        .setSize(18, 18));
            },
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(ctx.fluidInputsInventory, i)
                    .setBackground(backgrounds)
                    .setPos(pos)
                    .setSize(18, 18)),
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(ctx.fluidOutputsInventory, i)
                    .setBackground(backgrounds)
                    .setPos(pos)
                    .setSize(18, 18)),
            ModularUITextures.ITEM_SLOT,
            ModularUITextures.FLUID_SLOT,
            uiProperties,
            uiProperties.maxItemInputs,
            uiProperties.maxItemOutputs,
            uiProperties.maxFluidInputs,
            uiProperties.maxFluidOutputs,
            TieredVariant.STANDARD,
            ctx.windowOffset);

        addGregTechLogo(builder, ctx.windowOffset);

        for (Pair<IDrawable, Pair<Size, Pos2d>> specialTexture : uiProperties.specialTextures) {
            builder.widget(
                new DrawableWidget().setDrawable(specialTexture.getLeft())
                    .setSize(
                        specialTexture.getRight()
                            .getLeft())
                    .setPos(
                        specialTexture.getRight()
                            .getRight()
                            .add(ctx.windowOffset)));
        }

        return builder;
    }
}
