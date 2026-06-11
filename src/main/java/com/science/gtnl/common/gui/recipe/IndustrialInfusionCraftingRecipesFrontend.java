package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;

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

    public IndustrialInfusionCraftingRecipesFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
        this.itemRowCount = getItemRowCount();
    }

    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        return neiPropertiesBuilder.recipeBackgroundSize(new Size(170, 10 + (getItemRowCount() * 18)));
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
}
