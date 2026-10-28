package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.science.gtnl.common.recipe.gtnl.CrucibleCraftingRecipes;
import com.science.gtnl.common.recipe.gtnl.InfusionCraftingRecipes;
import com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;

import codechicken.nei.PositionedStack;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;
import thaumcraft.api.aspects.AspectList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IndustrialInfusionCraftingRecipesFrontend extends GTNLLogoFrontend {

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
    private static final int SLOT_PITCH = 18;
    private static final int LEGACY_ASPECT_COLUMNS = 10, ADJUSTED_ASPECT_COLUMNS = 9;
    private static final int LEGACY_ASPECT_SPACING = 16;
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
        int aspectAreaHeight = getAspectTopPadding() + getAspectRowCount() * getAspectSpacing() + 4;

        return neiPropertiesBuilder.recipeBackgroundSize(new Size(170, inputAreaHeight + aspectAreaHeight));
    }

    @Override
    public void addProgressBar(ModularWindow.@NotNull Builder builder, GTNEIDefaultHandler.NEITemplateContext ctx) {
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
                new Pos2d(PROGRESS_OFFSET_X, usesAdjustedLayout() ? getOutputYOrigin() - 35 : 10)));
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

    private int getAspectSpacing() {
        return usesAdjustedLayout() ? SLOT_PITCH : LEGACY_ASPECT_SPACING;
    }

    private int getAspectColumnCount() {
        return usesAdjustedLayout() ? ADJUSTED_ASPECT_COLUMNS : LEGACY_ASPECT_COLUMNS;
    }

    private int getAspectRowCount() {
        return uiProperties.maxItemInputs == 9 ? 1 : 2;
    }

    private int getOutputYOrigin() {
        return usesAdjustedLayout() ? INPUT_Y_ORIGIN + (getItemRowCount() - 1) * 9 : LEGACY_OUTPUT_Y_ORIGIN;
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return UIHelper.getGridPositions(itemInputCount, INPUT_X_ORIGIN, INPUT_Y_ORIGIN, getInputColumnCount());
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return UIHelper.getGridPositions(itemOutputCount, OUTPUT_X_ORIGIN, getOutputYOrigin(), INFUSION_INPUT_COLUMNS);
    }

    @Override
    public Pos2d getSpecialItemPosition() {
        int yOrigin = usesAdjustedLayout() ? getOutputYOrigin() + 18 : LEGACY_SPECIAL_Y_ORIGIN;
        return new Pos2d(OUTPUT_X_ORIGIN, yOrigin);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        if (!usesAdjustedLayout()) {
            super.addGregTechLogo(builder, windowOffset);
            return;
        }

        int progressX = uiProperties.progressBarPos.x + PROGRESS_OFFSET_X;
        int progressY = uiProperties.progressBarPos.y + getOutputYOrigin() - 35;
        int logoX = progressX + (uiProperties.progressBarSize.width - uiProperties.logoSize.width) / 2;
        int logoY = progressY + uiProperties.progressBarSize.height;
        super.addGregTechLogo(builder, new Pos2d(logoX - uiProperties.logoPos.x, logoY - uiProperties.logoPos.y));
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
        int columns = getAspectColumnCount();
        int spacing = getAspectSpacing();
        for (int i = 0; i < aspectStacks.length; i++) {
            ItemStack stack = aspectStacks[i];
            if (stack == null) continue;

            neiCachedRecipe.mInputs.add(
                new PositionedStack(
                    stack,
                    aspectXOrigin + (i % columns) * spacing,
                    aspectYOrigin + (i / columns) * spacing,
                    false));
        }
    }
}
