package com.science.gtnl.common.gui.recipe;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.tuple.Pair;

import com.github.bsideup.jabel.Desugar;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.enums.TieredVariant;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// GT's frontend API still takes ModularUI 1 types. Keep them in this class so that pages describe slots in plain
// numbers and a GT move to ModularUI 2 only changes this file.
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTNLRecipeFrontend extends RecipeMapFrontend {

    public static final UITexture PICTURE_GTNL_LOGO = UITexture.fullImage(ScienceNotLeisure.MODID, "gui/picture/logo");
    public static final UITexture PICTURE_GTNL_STEAM_LOGO = UITexture
        .fullImage(ScienceNotLeisure.MODID, "gui/picture/steam_logo");

    public enum Logo {
        GTNL,
        STEAM
    }

    @Desugar
    public record Slot(int x, int y) {}

    private final Logo logo;

    public GTNLRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        this(uiPropertiesBuilder, neiPropertiesBuilder, Logo.GTNL);
    }

    public GTNLRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder, Logo logo) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
        this.logo = logo;
    }

    public static List<Slot> grid(int count, int x, int y, int columns) {
        List<Slot> slots = new ObjectArrayList<>();
        for (Pos2d pos : UIHelper.getGridPositions(count, x, y, columns)) slots.add(new Slot(pos.x, pos.y));
        return slots;
    }

    public static List<Slot> slots(int... coordinates) {
        List<Slot> slots = new ObjectArrayList<>();
        for (int i = 0; i + 1 < coordinates.length; i += 2) slots.add(new Slot(coordinates[i], coordinates[i + 1]));
        return slots;
    }

    public static Size size(int width, int height) {
        return new Size(width, height);
    }

    public static Pos2d position(int x, int y) {
        return new Pos2d(x, y);
    }

    protected boolean borderless() {
        return false;
    }

    protected Slot progressOffset() {
        return null;
    }

    protected Slot logoOffset() {
        return null;
    }

    protected Slot specialItemSlot() {
        return null;
    }

    @Override
    public Pos2d getSpecialItemPosition() {
        Slot at = specialItemSlot();
        return at == null ? super.getSpecialItemPosition() : position(at.x(), at.y());
    }

    @Override
    public void addProgressBar(ModularWindow.Builder builder, GTNEIDefaultHandler.NEITemplateContext ctx) {
        Slot offset = progressOffset();
        if (offset == null) {
            super.addProgressBar(builder, ctx);
            return;
        }
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
                position(offset.x(), offset.y())));
    }

    @Override
    public ModularWindow.Builder createNEITemplate(GTNEIDefaultHandler.NEITemplateContext ctx) {
        if (!borderless()) return super.createNEITemplate(ctx);

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

    protected Slot logoSlot() {
        return new Slot(uiProperties.logoPos.x, uiProperties.logoPos.y);
    }

    protected List<Slot> itemInputSlots(int count) {
        return null;
    }

    protected List<Slot> itemOutputSlots(int count) {
        return null;
    }

    protected List<Slot> fluidInputSlots(int count) {
        return null;
    }

    protected List<Slot> fluidOutputSlots(int count) {
        return null;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        Slot at = logoSlot();
        Slot offset = logoOffset();
        if (offset != null) windowOffset = position(offset.x(), offset.y());
        builder.widget(
            new DrawableWidget().setDrawable(logo == Logo.STEAM ? PICTURE_GTNL_STEAM_LOGO : PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(new Pos2d(at.x(), at.y()).add(windowOffset)));
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        List<Slot> slots = itemInputSlots(itemInputCount);
        return slots == null ? super.getItemInputPositions(itemInputCount) : positions(slots);
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        List<Slot> slots = itemOutputSlots(itemOutputCount);
        return slots == null ? super.getItemOutputPositions(itemOutputCount) : positions(slots);
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        List<Slot> slots = fluidInputSlots(fluidInputCount);
        return slots == null ? super.getFluidInputPositions(fluidInputCount) : positions(slots);
    }

    @Override
    public List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        List<Slot> slots = fluidOutputSlots(fluidOutputCount);
        return slots == null ? super.getFluidOutputPositions(fluidOutputCount) : positions(slots);
    }

    private static List<Pos2d> positions(List<Slot> slots) {
        List<Pos2d> result = new ObjectArrayList<>(slots.size());
        for (Slot slot : slots) result.add(new Pos2d(slot.x(), slot.y()));
        return result;
    }
}
