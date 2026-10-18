package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;

/**
 * Lays out item and fluid slots in 4 columns and sizes the NEI background from the map's max IO.
 * <p>
 * NEI only hit-tests slots inside the height declared by the handler info (GT registers 135 for every recipe map)
 * and anchors its favorite/overlay buttons 31/18 px above that height. The lowest slot row of this layout ends at
 * {@code backgroundHeight - 13} in NEI coordinates, so every map using this frontend has to declare a handler height
 * of {@code backgroundHeight + 18}; otherwise the bottom slot rows are drawn but never respond to the mouse.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GeneralFrontend extends RecipeMapFrontend {

    private static final int xDirMaxCount = 4;
    private static final int yOrigin = 8;
    private final int itemRowCount;

    public GeneralFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
        this.itemRowCount = getItemRowCount();
    }

    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        int itemRowCount = getItemRowCount();
        return neiPropertiesBuilder.recipeBackgroundSize(new Size(170, 10 + (itemRowCount + getFluidRowCount()) * 18));
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(new Pos2d(79, 7).add(windowOffset)));
    }

    private int getItemRowCount() {
        return (Math.max(uiProperties.maxItemInputs, uiProperties.maxItemOutputs) - 1) / xDirMaxCount + 1;
    }

    private int getFluidRowCount() {
        return (Math.max(uiProperties.maxFluidInputs, uiProperties.maxFluidOutputs) - 1) / xDirMaxCount + 1;
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        return UIHelper.getGridPositions(itemInputCount, 6, yOrigin, xDirMaxCount);
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        return UIHelper.getGridPositions(itemOutputCount, 98, yOrigin, xDirMaxCount);
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        return UIHelper.getGridPositions(fluidInputCount, 6, yOrigin + itemRowCount * 18, xDirMaxCount);
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        return UIHelper.getGridPositions(fluidOutputCount, 98, yOrigin + itemRowCount * 18, xDirMaxCount);
    }

}
