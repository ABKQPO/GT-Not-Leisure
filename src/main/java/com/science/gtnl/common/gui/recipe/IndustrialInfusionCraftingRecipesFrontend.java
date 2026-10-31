package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.recipe.gtnl.CrucibleCraftingRecipes;
import com.science.gtnl.common.recipe.gtnl.InfusionCraftingRecipes;
import com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;

import codechicken.nei.PositionedStack;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.GTNEIDefaultHandler;
import thaumcraft.api.aspects.AspectList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IndustrialInfusionCraftingRecipesFrontend extends GTNLRecipeFrontend {

    private static final int INPUT_X_ORIGIN = 6;
    private static final int INPUT_Y_ORIGIN = 8;
    private static final int SHAPED_INPUT_COLUMNS = 3;
    private static final int INFUSION_INPUT_COLUMNS = 5;
    private static final int OUTPUT_X_ORIGIN = 125;
    private static final int PROGRESS_OFFSET_X = 15;
    private static final int LEGACY_OUTPUT_Y_ORIGIN = 45;
    private static final int LEGACY_SPECIAL_Y_ORIGIN = 75;
    private static final int NEI_WINDOW_OFFSET_X = -5;
    private static final int NEI_WINDOW_OFFSET_Y = -11;
    private static final int SLOT_INSET = 1;
    private static final int ASPECT_COLUMNS = 10;
    private static final int ASPECT_ROWS = 2;
    private static final int ASPECT_SPACING = 16;
    private static final int LEGACY_ASPECT_TOP_PADDING = 4;

    private final int itemRowCount;

    public IndustrialInfusionCraftingRecipesFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
        this.itemRowCount = getItemRowCount();
    }

    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        int inputAreaHeight = INPUT_Y_ORIGIN + getItemRowCount() * 18;
        int aspectAreaHeight = getAspectTopPadding() + ASPECT_ROWS * ASPECT_SPACING + 4;

        return neiPropertiesBuilder.recipeBackgroundSize(size(170, inputAreaHeight + aspectAreaHeight));
    }

    @Override
    protected Slot progressOffset() {
        return new Slot(PROGRESS_OFFSET_X, usesAdjustedLayout() ? getOutputYOrigin() - 35 : 10);
    }

    private int getItemRowCount() {
        int columns = getInputColumnCount();
        return (Math.max(uiProperties.maxItemInputs, uiProperties.maxItemOutputs) - 1) / columns + 1;
    }

    private int getInputColumnCount() {
        return uiProperties.maxItemInputs == 9 ? SHAPED_INPUT_COLUMNS : INFUSION_INPUT_COLUMNS;
    }

    private boolean usesAdjustedLayout() {
        return uiProperties.maxItemInputs == 9 || uiProperties.maxItemInputs == 25;
    }

    private int getAspectTopPadding() {
        return usesAdjustedLayout() ? 0 : LEGACY_ASPECT_TOP_PADDING;
    }

    private int getOutputYOrigin() {
        return usesAdjustedLayout() ? INPUT_Y_ORIGIN + (getItemRowCount() - 1) * 9 : LEGACY_OUTPUT_Y_ORIGIN;
    }

    @Override
    protected List<Slot> itemInputSlots(int itemInputCount) {
        return grid(itemInputCount, INPUT_X_ORIGIN, INPUT_Y_ORIGIN, getInputColumnCount());
    }

    @Override
    protected List<Slot> itemOutputSlots(int itemOutputCount) {
        return grid(itemOutputCount, OUTPUT_X_ORIGIN, getOutputYOrigin(), INFUSION_INPUT_COLUMNS);
    }

    @Override
    protected Slot specialItemSlot() {
        int yOrigin = usesAdjustedLayout() ? getOutputYOrigin() + 18 : LEGACY_SPECIAL_Y_ORIGIN;
        return new Slot(OUTPUT_X_ORIGIN, yOrigin);
    }

    @Override
    protected Slot logoOffset() {
        if (!usesAdjustedLayout()) return null;
        int progressX = uiProperties.progressBarPos.x + PROGRESS_OFFSET_X;
        int progressY = uiProperties.progressBarPos.y + getOutputYOrigin() - 35;
        int logoX = progressX + (uiProperties.progressBarSize.width - uiProperties.logoSize.width) / 2;
        int logoY = progressY + uiProperties.progressBarSize.height;
        return new Slot(logoX - uiProperties.logoPos.x, logoY - uiProperties.logoPos.y);
    }

    @Override
    public void prepareRecipe(GTNEIDefaultHandler.CachedDefaultRecipe neiCachedRecipe) {
        super.prepareRecipe(neiCachedRecipe);

        AspectList aspects = neiCachedRecipe.mRecipe.getMetadata(ShapedArcaneCraftingRecipes.ARCANE_VIS);
        if (aspects == null) aspects = neiCachedRecipe.mRecipe.getMetadata(InfusionCraftingRecipes.INFUSION_ASPECTS);
        if (aspects == null) aspects = neiCachedRecipe.mRecipe.getMetadata(CrucibleCraftingRecipes.CRUCIBLE_ASPECTS);
        if (aspects == null || aspects.size() == 0) return;

        int aspectXOrigin;
        int aspectYOrigin;
        if (usesAdjustedLayout()) {
            aspectXOrigin = INPUT_X_ORIGIN + NEI_WINDOW_OFFSET_X + SLOT_INSET;
            aspectYOrigin = INPUT_Y_ORIGIN + itemRowCount * 18 + NEI_WINDOW_OFFSET_Y + SLOT_INSET;
        } else {
            aspectXOrigin = 5;
            aspectYOrigin = INPUT_Y_ORIGIN + itemRowCount * 18 + LEGACY_ASPECT_TOP_PADDING;
        }
        ItemStack[] aspectStacks = TCRecipeTools.createAspectDisplayStacks(aspects);
        for (int i = 0; i < aspectStacks.length; i++) {
            ItemStack stack = aspectStacks[i];
            if (stack == null) continue;

            int column = i % ASPECT_COLUMNS;
            int row = i / ASPECT_COLUMNS;
            neiCachedRecipe.mInputs.add(
                new PositionedStack(
                    stack,
                    aspectXOrigin + column * ASPECT_SPACING,
                    aspectYOrigin + row * ASPECT_SPACING,
                    false));
        }
    }
}
