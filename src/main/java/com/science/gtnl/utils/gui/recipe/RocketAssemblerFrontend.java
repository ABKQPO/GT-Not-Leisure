package com.science.gtnl.utils.gui.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.forge.IItemHandlerModifiable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.format.RocketAssemblerFormat;

import codechicken.nei.PositionedStack;
import gregtech.api.enums.SteamVariant;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RocketAssemblerFrontend extends RecipeMapFrontend {

    public static Object2IntOpenHashMap<GTRecipe> initializedRecipes = new Object2IntOpenHashMap<>();

    public RocketAssemblerFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(
            uiPropertiesBuilder,
            neiPropertiesBuilder.recipeBackgroundSize(new Size(170, 175))
                .neiSpecialInfoFormatter(new RocketAssemblerFormat()));
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(new Pos2d(134, 98).add(windowOffset)));
    }

    @Override
    public Pos2d getSpecialItemPosition() {
        return new Pos2d(9, 13);
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return new ArrayList<>();
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return Collections.singletonList(new Pos2d(-1000, -1000));
    }

    public static final int[][] rocketT1Inputs = new int[][] { { 121, 3 }, // 1 Lander
        { 121, 21 }, // 2 Control Computer
        { 103, 12 }, // 3 Fuel Canister
        { 139, 12 }, // 4 Fuel Canister
        { 103, 30 }, // 5 Empty (Fuel)
        { 139, 30 }, // 6 Empty (Fuel)
        { 40, 12 }, // 7 Nose Cone
        { 31, 30 }, // 8 Heavy Plating
        { 49, 30 }, // 9 Heavy Plating
        { 31, 48 }, // 10 Heavy Plating
        { 49, 48 }, // 11 Heavy Plating
        { 31, 66 }, // 12 Heavy Plating
        { 49, 66 }, // 13 Heavy Plating
        { 31, 84 }, // 14 Heavy Plating
        { 49, 84 }, // 15 Heavy Plating
        { 40, 102 }, // 16 Engine
        { 13, 84 }, // 17 Fin
        { 67, 84 }, // 18 Fin
        { 13, 102 }, // 19 Fin
        { 67, 102 }, // 20 Fin
        { 121, 39 } // 21 Chest
    };

    public static final int[][] rocketT2Inputs = new int[][] { { 121, 3 }, // 1 Lander
        { 121, 21 }, // 2 Control Computer
        { 103, 12 }, // 3 Fuel Canister
        { 139, 12 }, // 4 Fuel Canister
        { 103, 30 }, // 5 Fuel Canister
        { 139, 30 }, // 6 Empty (Fuel)
        { 40, 3 }, // 7 Nose Cone
        { 31, 21 }, // 8 Heavy Plating
        { 49, 21 }, // 9 Heavy Plating
        { 31, 39 }, // 10 Heavy Plating
        { 49, 39 }, // 11 Heavy Plating
        { 31, 57 }, // 12 Heavy Plating
        { 49, 57 }, // 13 Heavy Plating
        { 31, 75 }, // 14 Heavy Plating
        { 49, 75 }, // 15 Heavy Plating
        { 31, 93 }, // 16 Heavy Plating
        { 49, 93 }, // 17 Heavy Plating
        { 31, 111 }, // 18 Engine
        { 49, 111 }, // 19 Engine
        { 13, 75 }, // 20 Fin
        { 67, 75 }, // 21 Fin
        { 13, 93 }, // 22 Fin
        { 67, 93 }, // 23 Fin
        { 13, 111 }, // 24 Booster
        { 67, 111 }, // 25 Booster
        { 121, 39 } // 26 Chest
    };

    public static final int[][] rocketT3Inputs = new int[][] { { 121, 3 }, // 1 Lander
        { 121, 21 }, // 2 Control Computer
        { 103, 12 }, // 3 Fuel Canister
        { 139, 12 }, // 4 Fuel Canister
        { 103, 30 }, // 5 Fuel Canister
        { 139, 30 }, // 6 Fuel Canister
        { 40, 3 }, // 7 Nose Cone
        { 31, 21 }, // 8 Heavy Plating T2
        { 49, 21 }, // 9 Heavy Plating T2
        { 22, 39 }, // 10 Heavy Plating
        { 40, 39 }, // 11 Heavy Plating
        { 58, 39 }, // 12 Heavy Plating
        { 22, 57 }, // 13 Heavy Plating
        { 40, 57 }, // 14 Heavy Plating
        { 58, 57 }, // 15 Heavy Plating
        { 22, 75 }, // 16 Heavy Plating
        { 40, 75 }, // 17 Heavy Plating
        { 58, 75 }, // 18 Heavy Plating
        { 22, 93 }, // 19 Heavy Plating
        { 40, 93 }, // 20 Heavy Plating
        { 58, 93 }, // 21 Heavy Plating
        { 40, 111 }, // 22 Engine
        { 4, 75 }, // 23 Fin
        { 76, 75 }, // 24 Fin
        { 4, 93 }, // 25 Fin
        { 76, 93 }, // 26 Fin
        { 4, 111 }, // 27 Booster
        { 76, 111 }, // 28 Booster
        { 121, 39 } // 29 Chest
    };

    public static final int[][] rocketT4Inputs = new int[][] { { 121, 3 }, // 1 Lander
        { 121, 21 }, // 2 Control Computer
        { 103, 12 }, // 3 Fuel Canister
        { 139, 12 }, // 4 Fuel Canister
        { 103, 30 }, // 5 Fuel Canister
        { 139, 30 }, // 6 Fuel Canister
        { 49, 3 }, // 7 Nose Cone
        { 40, 21 }, // 8 Heavy Plating
        { 58, 21 }, // 9 Heavy Plating
        { 40, 39 }, // 10 Heavy Plating
        { 58, 39 }, // 11 Heavy Plating
        { 40, 57 }, // 12 Heavy Plating
        { 58, 57 }, // 13 Heavy Plating
        { 40, 75 }, // 14 Heavy Plating
        { 58, 75 }, // 15 Heavy Plating
        { 40, 93 }, // 16 Heavy Plating
        { 58, 93 }, // 17 Heavy Plating
        { 40, 111 }, // 18 Heavy Plating
        { 58, 111 }, // 19 Heavy Plating
        { 40, 129 }, // 20 Engine
        { 58, 129 }, // 21 Engine
        { 22, 75 }, // 22 Fin
        { 76, 75 }, // 23 Fin
        { 22, 93 }, // 24 Fin
        { 76, 93 }, // 25 Fin
        { 22, 111 }, // 26 Fin
        { 76, 111 }, // 27 Fin
        { 4, 129 }, // 28 Booster
        { 22, 129 }, // 29 Booster
        { 76, 129 }, // 30 Booster
        { 94, 129 }, // 31 Booster
        { 121, 39 } // 32 Chest
    };

    public static final int[][] rocketT5Inputs = new int[][] { { 121, 3 }, // 1 Lander
        { 121, 21 }, // 2 Control Computer
        { 103, 12 }, // 3 Fuel Canister
        { 139, 12 }, // 4 Fuel Canister
        { 103, 30 }, // 5 Fuel Canister
        { 139, 30 }, // 6 Fuel Canister
        { 49, 3 }, // 7 Nose Cone
        { 40, 21 }, // 8 Heavy Plating
        { 58, 21 }, // 9 Heavy Plating
        { 40, 39 }, // 10 Heavy Plating
        { 58, 39 }, // 11 Heavy Plating
        { 40, 57 }, // 12 Heavy Plating
        { 58, 57 }, // 13 Heavy Plating
        { 40, 75 }, // 14 Heavy Plating
        { 58, 75 }, // 15 Heavy Plating
        { 40, 93 }, // 16 Heavy Plating
        { 58, 93 }, // 17 Heavy Plating
        { 40, 111 }, // 18 Heavy Plating
        { 58, 111 }, // 19 Heavy Plating
        { 22, 129 }, // 20 Engine
        { 40, 129 }, // 21 Engine
        { 58, 129 }, // 22 Engine
        { 76, 129 }, // 23 Engine
        { 22, 75 }, // 22 Fin
        { 76, 75 }, // 23 Fin
        { 22, 93 }, // 24 Fin
        { 76, 93 }, // 25 Fin
        { 22, 111 }, // 26 Fin
        { 76, 111 }, // 27 Fin
        { 4, 111 }, // 28 Booster
        { 94, 111 }, // 29 Booster
        { 4, 129 }, // 30 Booster
        { 94, 129 }, // 31 Booster
        { 121, 39 } // 32 Chest
    };

    public static final int[][] rocketT6Inputs = new int[][] { { 121, 3 }, // 1 Lander
        { 121, 21 }, // 2 Control Computer
        { 103, 12 }, // 3 Fuel Canister
        { 139, 12 }, // 4 Fuel Canister
        { 103, 30 }, // 5 Fuel Canister
        { 139, 30 }, // 6 Fuel Canister
        { 58, 3 }, // 7 Nose Cone
        { 49, 21 }, // 8 Heavy Plating
        { 67, 21 }, // 9 Heavy Plating
        { 49, 39 }, // 10 Heavy Plating
        { 67, 39 }, // 11 Heavy Plating
        { 49, 57 }, // 12 Heavy Plating
        { 67, 57 }, // 13 Heavy Plating
        { 49, 75 }, // 14 Heavy Plating
        { 67, 75 }, // 15 Heavy Plating
        { 49, 93 }, // 16 Heavy Plating
        { 67, 93 }, // 17 Heavy Plating
        { 49, 111 }, // 18 Heavy Plating
        { 67, 111 }, // 19 Heavy Plating
        { 22, 129 }, // 20 Engine
        { 40, 129 }, // 21 Engine
        { 58, 129 }, // 22 Engine
        { 76, 129 }, // 23 Engine
        { 94, 129 }, // 24 Engine
        { 31, 57 }, // 25 Fin
        { 85, 57 }, // 26 Fin
        { 31, 75 }, // 27 Fin
        { 85, 75 }, // 28 Fin
        { 31, 93 }, // 29 Fin
        { 85, 93 }, // 30 Fin
        { 31, 111 }, // 31 Fin
        { 85, 111 }, // 32 Fin
        { 13, 93 }, // 33 Booster
        { 103, 93 }, // 34 Booster
        { 13, 111 }, // 35 Booster
        { 103, 111 }, // 36 Booster
        { 4, 129 }, // 37 Booster
        { 112, 129 }, // 38 Booster
        { 121, 39 } // 39 Chest
    };

    public static final int[][] rocketT7Inputs = new int[][] { { 121, 3 }, // 1 Lander
        { 121, 21 }, // 2 Control Computer
        { 103, 12 }, // 3 Fuel Canister
        { 139, 12 }, // 4 Fuel Canister
        { 103, 30 }, // 5 Fuel Canister
        { 139, 30 }, // 6 Fuel Canister
        { 49, 3 }, // 7 Nose Cone
        { 40, 39 }, // 8 Heavy Plating
        { 58, 39 }, // 9 Heavy Plating
        { 40, 57 }, // 10 Heavy Plating
        { 58, 57 }, // 11 Heavy Plating
        { 40, 75 }, // 12 Heavy Plating
        { 58, 75 }, // 13 Heavy Plating
        { 40, 93 }, // 14 Heavy Plating
        { 58, 93 }, // 15 Heavy Plating
        { 40, 111 }, // 16 Heavy Plating
        { 58, 111 }, // 17 Heavy Plating
        { 40, 129 }, // 18 Heavy Plating
        { 58, 129 }, // 19 Heavy Plating
        { 4, 147 }, // 20 Engine
        { 22, 147 }, // 21 Engine
        { 40, 147 }, // 22 Engine
        { 58, 147 }, // 23 Engine
        { 76, 147 }, // 24 Engine
        { 94, 147 }, // 25 Engine
        { 40, 21 }, // 26 Fin
        { 58, 21 }, // 27 Fin
        { 4, 93 }, // 28 Fin
        { 94, 93 }, // 29 Fin
        { 4, 111 }, // 30 Fin
        { 94, 111 }, // 31 Fin
        { 4, 129 }, // 32 Fin
        { 94, 129 }, // 33 Fin
        { 22, 75 }, // 34 Booster
        { 76, 75 }, // 35 Booster
        { 22, 93 }, // 36 Booster
        { 76, 93 }, // 37 Booster
        { 22, 111 }, // 38 Booster
        { 76, 111 }, // 39 Booster
        { 22, 129 }, // 40 Booster
        { 76, 129 }, // 41 Booster
        { 121, 39 } // 42 Chest
    };

    public static final int[][] rocketT8Inputs = new int[][] { { 121, 3 }, // 1 Lander
        { 121, 21 }, // 2 Control Computer
        { 103, 12 }, // 3 Fuel Canister
        { 139, 12 }, // 4 Fuel Canister
        { 103, 30 }, // 5 Fuel Canister
        { 139, 30 }, // 6 Fuel Canister
        { 58, 3 }, // 7 Nose Cone
        { 40, 39 }, // 8 Heavy Plating
        { 58, 39 }, // 9 Heavy Plating
        { 76, 39 }, // 10 Heavy Plating
        { 40, 57 }, // 11 Heavy Plating
        { 58, 57 }, // 12 Heavy Plating
        { 76, 57 }, // 13 Heavy Plating
        { 40, 75 }, // 14 Heavy Plating
        { 58, 75 }, // 15 Heavy Plating
        { 76, 75 }, // 16 Heavy Plating
        { 40, 93 }, // 17 Heavy Plating
        { 58, 93 }, // 18 Heavy Plating
        { 76, 93 }, // 19 Heavy Plating
        { 40, 111 }, // 20 Heavy Plating
        { 58, 111 }, // 21 Heavy Plating
        { 76, 111 }, // 22 Heavy Plating
        { 40, 129 }, // 23 Heavy Plating
        { 58, 129 }, // 24 Heavy Plating
        { 76, 129 }, // 25 Heavy Plating
        { 4, 147 }, // 26 Engine
        { 22, 147 }, // 27 Engine
        { 40, 147 }, // 28 Engine
        { 58, 147 }, // 29 Engine
        { 76, 147 }, // 30 Engine
        { 94, 147 }, // 31 Engine
        { 112, 147 }, // 32 Engine
        { 49, 21 }, // 33 Fin
        { 67, 21 }, // 34 Fin
        { 4, 75 }, // 35 Fin
        { 112, 75 }, // 36 Fin
        { 4, 93 }, // 37 Fin
        { 112, 93 }, // 38 Fin
        { 4, 111 }, // 39 Fin
        { 112, 111 }, // 40 Fin
        { 4, 129 }, // 41 Fin
        { 112, 129 }, // 42 Fin
        { 22, 57 }, // 43 Booster
        { 94, 57 }, // 44 Booster
        { 22, 75 }, // 45 Booster
        { 94, 75 }, // 46 Booster
        { 22, 93 }, // 47 Booster
        { 94, 93 }, // 48 Booster
        { 22, 111 }, // 49 Booster
        { 94, 111 }, // 50 Booster
        { 22, 129 }, // 51 Booster
        { 94, 129 }, // 52 Booster
        { 121, 39 } // 53 Chest
    };

    public static final int[] rocketT1Output = new int[] { 121, 62 };
    public static final int[] rocketT2Output = new int[] { 121, 62 };
    public static final int[] rocketT3Output = new int[] { 121, 62 };
    public static final int[] rocketT4Output = new int[] { 121, 62 };
    public static final int[] rocketT5Output = new int[] { 121, 62 };
    public static final int[] rocketT6Output = new int[] { 121, 62 };
    public static final int[] rocketT7Output = new int[] { 121, 62 };
    public static final int[] rocketT8Output = new int[] { 130, 62 };

    @Override
    public void drawNEIOverlays(GTNEIDefaultHandler.CachedDefaultRecipe neiCachedRecipe) {
        final GTRecipe recipe = neiCachedRecipe.mRecipe;
        ItemStack[] inputs = recipe.mInputs;
        ItemStack output = recipe.mOutputs[0];
        if (inputs == null || output == null) return;

        int[][] selectedInputs = null;
        int[] selectedOutput = null;
        int tier = recipe.mSpecialValue;

        switch (recipe.mSpecialValue) {
            case 1 -> {
                selectedInputs = rocketT1Inputs;
                selectedOutput = rocketT1Output;
            }
            case 2 -> {
                selectedInputs = rocketT2Inputs;
                selectedOutput = rocketT2Output;
            }
            case 3 -> {
                selectedInputs = rocketT3Inputs;
                selectedOutput = rocketT3Output;
            }
            case 4 -> {
                selectedInputs = rocketT4Inputs;
                selectedOutput = rocketT4Output;
            }
            case 5 -> {
                selectedInputs = rocketT5Inputs;
                selectedOutput = rocketT5Output;
            }
            case 6 -> {
                selectedInputs = rocketT6Inputs;
                selectedOutput = rocketT6Output;
            }
            case 7 -> {
                selectedInputs = rocketT7Inputs;
                selectedOutput = rocketT7Output;
            }
            case 8 -> {
                selectedInputs = rocketT8Inputs;
                selectedOutput = rocketT8Output;
            }
            default -> {}
        }

        if (selectedInputs == null || selectedOutput == null) {
            super.drawNEIOverlays(neiCachedRecipe);
            return;
        }

        if (!initializedRecipes.containsKey(recipe)) {

            for (int i = 0; i < inputs.length; i++) {
                ItemStack stack = inputs[i];
                if (stack == null) continue;
                neiCachedRecipe.mInputs
                    .add(new PositionedStack(stack, selectedInputs[i][0], selectedInputs[i][1], false));
            }

            neiCachedRecipe.mOutputs.add(new PositionedStack(output, selectedOutput[0], selectedOutput[1], false));

            initializedRecipes.put(recipe, tier);
        }

        super.drawNEIOverlays(neiCachedRecipe);
    }

    @Override
    public ModularWindow.Builder createNEITemplate(IItemHandlerModifiable itemInputsInventory,
        IItemHandlerModifiable itemOutputsInventory, IItemHandlerModifiable specialSlotInventory,
        IItemHandlerModifiable fluidInputsInventory, IItemHandlerModifiable fluidOutputsInventory,
        Supplier<Float> progressSupplier, Pos2d windowOffset) {
        // Override regular createNEITemplate method, so we can remove the background texture with the ugly border.
        ModularWindow.Builder builder = ModularWindow.builder(neiProperties.recipeBackgroundSize);

        // First draw progress bar in background
        if (uiProperties.useProgressBar) {
            addProgressBar(builder, progressSupplier, windowOffset);
        }

        UIHelper.forEachSlots(
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(itemInputsInventory, i)
                    .setBackground(backgrounds)
                    .setPos(pos)
                    .setSize(18, 18)),
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(itemOutputsInventory, i)
                    .setBackground(backgrounds)
                    .setPos(pos)
                    .setSize(18, 18)),
            (i, backgrounds, pos) -> {
                if (uiProperties.useSpecialSlot) builder.widget(
                    SlotWidget.phantom(specialSlotInventory, 0)
                        .setBackground(backgrounds)
                        .setPos(pos)
                        .setSize(18, 18));
            },
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(fluidInputsInventory, i)
                    .setBackground(backgrounds)
                    .setPos(pos)
                    .setSize(18, 18)),
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(fluidOutputsInventory, i)
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
            SteamVariant.NONE,
            windowOffset);

        addGregTechLogo(builder, windowOffset);

        for (Pair<IDrawable, Pair<Size, Pos2d>> specialTexture : uiProperties.specialTextures) {
            builder.widget(
                new DrawableWidget().setDrawable(specialTexture.getLeft())
                    .setSize(
                        specialTexture.getRight()
                            .getLeft())
                    .setPos(
                        specialTexture.getRight()
                            .getRight()
                            .add(windowOffset)));
        }

        return builder;
    }
}
