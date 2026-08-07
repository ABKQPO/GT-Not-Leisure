package com.science.gtnl.common.gui.recipe;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;

import codechicken.nei.PositionedStack;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IndustrialInfusionCraftingRecipesFrontend extends GTNLLogoFrontend {

    private static final int xDirMaxCount = 5;
    private static final int yOrigin = 8;
    private final int itemRowCount;
    private static final int ASPECT_COLUMNS = 10;
    private static final int ASPECT_ROWS = 4;
    private static final int ASPECT_X_ORIGIN = 5;
    private static final int ASPECT_SPACING = 16;
    private static final int ASPECT_TOP_PADDING = 4;

    private final Set<GTNEIDefaultHandler.CachedDefaultRecipe> expandedRecipes = Collections
        .newSetFromMap(new WeakHashMap<>());

    public IndustrialInfusionCraftingRecipesFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
        this.itemRowCount = getItemRowCount();
    }

    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        int inputAreaHeight = yOrigin + getItemRowCount() * 18;
        int aspectAreaHeight = ASPECT_TOP_PADDING + ASPECT_ROWS * ASPECT_SPACING + 4;

        return neiPropertiesBuilder.recipeBackgroundSize(new Size(170, inputAreaHeight + aspectAreaHeight));
    }

    @Override
    public void addProgressBar(ModularWindow.@NotNull Builder builder,
        @NotNull GTNEIDefaultHandler.NEITemplateContext ctx) {
        super.addProgressBar(
            builder,
            new GTNEIDefaultHandler.NEITemplateContext(
                ctx.itemInputsInventory,
                ctx.itemOutputsInventory,
                ctx.specialSlotInventory,
                ctx.fluidInputsInventory,
                ctx.fluidOutputsInventory,
                ctx.progressSupplier,
                ctx.recipeSupplier,
                new Pos2d(15, 10)));
    }

    private int getItemRowCount() {
        return (Math.max(uiProperties.maxItemInputs, uiProperties.maxItemOutputs) - 1) / xDirMaxCount + 1;
    }

    @Override
    public @NotNull List<Pos2d> getItemInputPositions(int itemInputCount) {
        return UIHelper.getGridPositions(itemInputCount, 6, yOrigin, xDirMaxCount);
    }

    @Override
    public @NotNull List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return UIHelper.getGridPositions(itemOutputCount, 125, 45, xDirMaxCount);
    }

    @Override
    public @NotNull Pos2d getSpecialItemPosition() {
        return new Pos2d(125, 75);
    }

    @Override
    public void drawNEIOverlays(GTNEIDefaultHandler.CachedDefaultRecipe neiCachedRecipe) {
        if (expandedRecipes.add(neiCachedRecipe)
            && neiCachedRecipe.mRecipe.mSpecialItems instanceof ItemStack[]aspectStacks) {

            int aspectYOrigin = yOrigin + itemRowCount * 18 + ASPECT_TOP_PADDING;

            for (int i = 0; i < aspectStacks.length; i++) {
                ItemStack stack = aspectStacks[i];
                if (stack == null) continue;

                int column = i % ASPECT_COLUMNS;
                int row = i / ASPECT_COLUMNS;

                int x = ASPECT_X_ORIGIN + column * ASPECT_SPACING;
                int y = aspectYOrigin + row * ASPECT_SPACING;

                neiCachedRecipe.mInputs.add(new PositionedStack(stack.copy(), x, y, false));
            }
        }

        super.drawNEIOverlays(neiCachedRecipe);
    }
}
