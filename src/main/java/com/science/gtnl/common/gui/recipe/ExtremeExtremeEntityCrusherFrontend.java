package com.science.gtnl.common.gui.recipe;

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

import gregtech.api.enums.TieredVariant;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtremeExtremeEntityCrusherFrontend extends GTNLRecipeFrontend {

    public ExtremeExtremeEntityCrusherFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(
            uiPropertiesBuilder.progressBarPos(new Pos2d(26, 7)),
            neiPropertiesBuilder.recipeBackgroundSize(new Size(170, 100)));
    }

    @Override
    protected Slot logoSlot() {
        return new Slot(150, 102);
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        ObjectArrayList<Pos2d> positions = new ObjectArrayList<>();
        positions.add(new Pos2d(6, 7));
        return positions;
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        ObjectArrayList<Pos2d> positions = new ObjectArrayList<>();
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 9; i++) {
                positions.add(new Pos2d((6 + 18 * i), (25 + 18 * j)));
            }
        }
        return positions;
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        ObjectArrayList<Pos2d> positions = new ObjectArrayList<>();
        positions.add(new Pos2d(60, 7));
        return positions;
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
