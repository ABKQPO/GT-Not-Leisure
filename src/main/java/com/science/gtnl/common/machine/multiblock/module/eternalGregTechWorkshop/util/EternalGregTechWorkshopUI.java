package com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularUIContext;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicTextWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import gregtech.api.enums.VoidingMode;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.modularui.IControllerWithOptionalFeatures;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import tectech.TecTech;
import tectech.thing.gui.TecTechUITextures;

/**
 * Holds UI element builders and other conveniences shared between the primary Forge of the Gods and its modules.
 */
public class EternalGregTechWorkshopUI {

    public static ButtonWidget createInputSeparationButton(final IGregTechTileEntity tileEntity,
        final IControllerWithOptionalFeatures mte, IWidgetBuilder<?> builder) {
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            TecTech.proxy.playSound(tileEntity, "fx_click");
            mte.setInputSeparation(!mte.isInputSeparationEnabled());
        })
            .setPlayClickSound(false)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                if (mte.isInputSeparationEnabled()) {
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_INPUT_SEPARATION);
                } else {
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_INPUT_SEPARATION_OFF);
                }
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(
                new FakeSyncWidget.BooleanSyncer(mte::isInputSeparationEnabled, mte::setInputSeparation),
                builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.input_separation"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(mte.getInputSeparationButtonPos())
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    public static ButtonWidget createBatchModeButton(final IGregTechTileEntity tileEntity,
        final IControllerWithOptionalFeatures mte, IWidgetBuilder<?> builder) {
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            TecTech.proxy.playSound(tileEntity, "fx_click");
            mte.setBatchMode(!mte.isBatchModeEnabled());
        })
            .setPlayClickSound(false)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                if (mte.isBatchModeEnabled()) {
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_BATCH_MODE);
                } else {
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_BATCH_MODE_OFF);
                }
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(new FakeSyncWidget.BooleanSyncer(mte::isBatchModeEnabled, mte::setBatchMode), builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.batch_mode"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(mte.getBatchModeButtonPos())
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    public static ButtonWidget createLockToSingleRecipeButton(final IGregTechTileEntity tileEntity,
        final IControllerWithOptionalFeatures mte, IWidgetBuilder<?> builder) {
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            TecTech.proxy.playSound(tileEntity, "fx_click");
            mte.setRecipeLocking(!mte.isRecipeLockingEnabled());
        })
            .setPlayClickSound(false)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                if (mte.isRecipeLockingEnabled()) {
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_RECIPE_LOCKED);
                } else {
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_RECIPE_UNLOCKED);
                }
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(new FakeSyncWidget.BooleanSyncer(mte::isRecipeLockingEnabled, mte::setRecipeLocking), builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.lock_recipe"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(mte.getRecipeLockingButtonPos())
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    public static ButtonWidget createVoidExcessButton(final IGregTechTileEntity tileEntity,
        final IControllerWithOptionalFeatures mte, IWidgetBuilder<?> builder) {
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            TecTech.proxy.playSound(tileEntity, "fx_click");
            Set<VoidingMode> allowed = mte.getAllowedVoidingModes();
            switch (clickData.mouseButton) {
                case 0 -> mte.setVoidingMode(
                    mte.getVoidingMode()
                        .nextInCollection(allowed));
                case 1 -> mte.setVoidingMode(
                    mte.getVoidingMode()
                        .previousInCollection(allowed));
            }
            widget.notifyTooltipChange();
        })
            .setPlayClickSound(false)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(TecTechUITextures.BUTTON_CELESTIAL_32x32);
                switch (mte.getVoidingMode()) {
                    case VOID_NONE -> ret.add(TecTechUITextures.OVERLAY_BUTTON_VOIDING_OFF);
                    case VOID_ITEM -> ret.add(TecTechUITextures.OVERLAY_BUTTON_VOIDING_ITEMS);
                    case VOID_FLUID -> ret.add(TecTechUITextures.OVERLAY_BUTTON_VOIDING_FLUIDS);
                    case VOID_ALL -> ret.add(TecTechUITextures.OVERLAY_BUTTON_VOIDING_BOTH);
                }
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(
                new FakeSyncWidget.IntegerSyncer(
                    () -> mte.getVoidingMode()
                        .ordinal(),
                    val -> mte.setVoidingMode(VoidingMode.fromOrdinal(val))),
                builder)
            .dynamicTooltip(
                () -> Arrays.asList(
                    StatCollector.translateToLocal("GT5U.gui.button.voiding_mode"),
                    StatCollector.translateToLocal(
                        mte.getVoidingMode()
                            .getTransKey())))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(mte.getVoidingModeButtonPos())
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    public static ModularWindow createGeneralInfoWindow() {
        final Scrollable scrollable = new Scrollable().setVerticalScroll();
        final int WIDTH = 300;
        final int HEIGHT = 300;
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);

        builder.setDraggable(true);
        scrollable.widget(
            new TextWidget(EnumChatFormatting.BOLD + translateToLocal("gt.blockmachines.multimachine.FOG.introduction"))
                .setDefaultColor(EnumChatFormatting.DARK_PURPLE)
                .setTextAlignment(Alignment.TopCenter)
                .setPos(7, 13)
                .setSize(280, 15))
            .widget(
                new TextWidget(translateToLocal("gt.blockmachines.multimachine.FOG.introductioninfotext"))
                    .setDefaultColor(EnumChatFormatting.GOLD)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setPos(7, 30)
                    .setSize(280, 50))
            .widget(
                new TextWidget(
                    EnumChatFormatting.BOLD + translateToLocal("gt.blockmachines.multimachine.FOG.tableofcontents"))
                        .setDefaultColor(EnumChatFormatting.AQUA)
                        .setTextAlignment(Alignment.CenterLeft)
                        .setPos(7, 80)
                        .setSize(150, 15))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> scrollable.setVerticalScrollOffset(150))
                    .setBackground(
                        new Text(EnumChatFormatting.BOLD + translateToLocal("gt.blockmachines.multimachine.FOG.fuel"))
                            .alignment(Alignment.CenterLeft)
                            .color(0x55ffff))
                    .setPos(7, 95)
                    .setSize(150, 15))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> scrollable.setVerticalScrollOffset(434))
                    .setBackground(
                        new Text(
                            EnumChatFormatting.BOLD + translateToLocal("gt.blockmachines.multimachine.FOG.modules"))
                                .alignment(Alignment.CenterLeft)
                                .color(0x55ffff))
                    .setPos(7, 110)
                    .setSize(150, 15))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> scrollable.setVerticalScrollOffset(1088))
                    .setBackground(
                        new Text(
                            EnumChatFormatting.BOLD + translateToLocal("gt.blockmachines.multimachine.FOG.upgrades"))
                                .alignment(Alignment.CenterLeft)
                                .color(0x55ffff))
                    .setPos(7, 125)
                    .setSize(150, 15))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> scrollable.setVerticalScrollOffset(1412))
                    .setBackground(
                        new Text(
                            EnumChatFormatting.BOLD + translateToLocal("gt.blockmachines.multimachine.FOG.milestones"))
                                .alignment(Alignment.CenterLeft)
                                .color(0x55ffff))
                    .setPos(7, 140)
                    .setSize(150, 15))
            .widget(
                new TextWidget(
                    EnumChatFormatting.BOLD + "§N" + translateToLocal("gt.blockmachines.multimachine.FOG.fuel"))
                        .setDefaultColor(EnumChatFormatting.DARK_PURPLE)
                        .setTextAlignment(Alignment.TopCenter)
                        .setPos(127, 160)
                        .setSize(40, 15))
            .widget(
                new TextWidget(translateToLocal("gt.blockmachines.multimachine.FOG.fuelinfotext"))
                    .setDefaultColor(EnumChatFormatting.GOLD)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setPos(7, 177)
                    .setSize(280, 250))
            .widget(
                new TextWidget(
                    EnumChatFormatting.BOLD + "§N" + translateToLocal("gt.blockmachines.multimachine.FOG.modules"))
                        .setDefaultColor(EnumChatFormatting.DARK_PURPLE)
                        .setTextAlignment(Alignment.TopCenter)
                        .setPos(7, 440)
                        .setSize(280, 15))
            .widget(
                new TextWidget(translateToLocal("gt.blockmachines.multimachine.FOG.moduleinfotext"))
                    .setDefaultColor(EnumChatFormatting.GOLD)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setPos(7, 461)
                    .setSize(280, 620))
            .widget(
                new TextWidget(
                    EnumChatFormatting.BOLD + "§N" + translateToLocal("gt.blockmachines.multimachine.FOG.upgrades"))
                        .setDefaultColor(EnumChatFormatting.DARK_PURPLE)
                        .setTextAlignment(Alignment.TopCenter)
                        .setPos(7, 1098)
                        .setSize(280, 15))
            .widget(
                new TextWidget(translateToLocal("gt.blockmachines.multimachine.FOG.upgradeinfotext"))
                    .setDefaultColor(EnumChatFormatting.GOLD)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setPos(7, 1115)
                    .setSize(280, 290))
            .widget(
                new TextWidget(
                    EnumChatFormatting.BOLD + "§N" + translateToLocal("gt.blockmachines.multimachine.FOG.milestones"))
                        .setDefaultColor(EnumChatFormatting.DARK_PURPLE)
                        .setTextAlignment(Alignment.TopCenter)
                        .setPos(7, 1422)
                        .setSize(280, 15))
            .widget(
                new TextWidget(translateToLocal("gt.blockmachines.multimachine.FOG.milestoneinfotext"))
                    .setDefaultColor(EnumChatFormatting.GOLD)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setPos(7, 1439)
                    .setSize(280, 320))
            .widget(
                new TextWidget("").setPos(7, 1965)
                    .setSize(10, 10));

        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.BACKGROUND_GLOW_WHITE)
                .setPos(0, 0)
                .setSize(300, 300))
            .widget(
                scrollable.setSize(292, 292)
                    .setPos(4, 4))
            .widget(
                ButtonWidget.closeWindowButton(true)
                    .setPos(284, 4));

        return builder.build();
    }

    public static void reopenWindow(Widget widget, int windowId) {
        if (!widget.isClient()) {
            ModularUIContext ctx = widget.getContext();
            if (ctx.isWindowOpen(windowId)) {
                ctx.closeWindow(windowId);
            }
            ctx.openSyncedWindow(windowId);
        }
    }

    public static void closeWindow(Widget widget, int windowId) {
        if (!widget.isClient()) {
            ModularUIContext ctx = widget.getContext();
            if (ctx.isWindowOpen(windowId)) {
                ctx.closeWindow(windowId);
            }
        }
    }

    public static Widget getIndividualUpgradeGroup(EternalGregTechWorkshopUpgrade upgrade,
        Supplier<Integer> shardGetter, Runnable complete, Runnable respec, Supplier<Boolean> check) {
        MultiChildWidget widget = new MultiChildWidget();
        widget.setSize(upgrade.getWindowSize());

        Size windowSize = upgrade.getWindowSize();
        int w = windowSize.width;
        int h = windowSize.height;

        // Close window button
        widget.addChild(
            ButtonWidget.closeWindowButton(true)
                .setPos(w - 15, 3));

        // Background symbol
        widget.addChild(
            new DrawableWidget().setDrawable(upgrade.getSymbol())
                .setPos((int) ((1 - upgrade.getSymbolWidthRatio() / 2) * w / 2), h / 4)
                .setSize((int) (w / 2 * upgrade.getSymbolWidthRatio()), h / 2));

        // Background overlay
        widget.addChild(
            new DrawableWidget().setDrawable(upgrade.getOverlay())
                .setPos(w / 4, h / 4)
                .setSize(w / 2, h / 2));

        // Upgrade name title
        widget.addChild(
            new TextWidget(upgrade.getNameText()).setTextAlignment(Alignment.Center)
                .setDefaultColor(EnumChatFormatting.GOLD)
                .setSize(w - 15, 30)
                .setPos(9, 5));

        // Upgrade body text
        widget.addChild(
            new TextWidget(upgrade.getBodyText()).setTextAlignment(Alignment.Center)
                .setDefaultColor(EnumChatFormatting.WHITE)
                .setSize(w - 15, upgrade.getLoreYPos() - 30)
                .setPos(9, 30));

        // Lore Text
        widget.addChild(
            new TextWidget(EnumChatFormatting.ITALIC + upgrade.getLoreText()).setTextAlignment(Alignment.Center)
                .setDefaultColor(0xbbbdbd)
                .setSize(w - 15, (int) (h * 0.9) - upgrade.getLoreYPos())
                .setPos(9, upgrade.getLoreYPos()));

        // Shard cost text
        String costStr = " " + EnumChatFormatting.BLUE + upgrade.getShardCost();
        widget.addChild(
            new TextWidget(translateToLocal("gt.blockmachines.multimachine.FOG.shardcost") + costStr)
                .setTextAlignment(Alignment.Center)
                .setScale(0.7f)
                .setMaxWidth(70)
                .setDefaultColor(0x9c9c9c)
                .setPos(11, h - 25));

        // Available shards text
        widget.addChild(
            new TextWidget(translateToLocal("gt.blockmachines.multimachine.FOG.availableshards"))
                .setTextAlignment(Alignment.Center)
                .setScale(0.7f)
                .setMaxWidth(90)
                .setDefaultColor(0x9c9c9c)
                .setPos(w - 87, h - 25));

        // Available shards amount
        widget.addChild(
            TextWidget.dynamicText(() -> getAvailableShardsText(upgrade, shardGetter))
                .setTextAlignment(Alignment.Center)
                .setScale(0.7f)
                .setMaxWidth(90)
                .setDefaultColor(0x9c9c9c)
                .setPos(w - 27, h - 18));

        // Complete button group
        MultiChildWidget completeGroup = new MultiChildWidget();
        completeGroup.setPos(w / 2 - 21, (int) (h * 0.9));

        // Complete button
        completeGroup.addChild(new ButtonWidget().setOnClick(($, $$) -> {
            if (check.get()) {
                respec.run();
            } else {
                complete.run();
            }
        })
            .setSize(40, 15)
            .setBackground(
                () -> new IDrawable[] {
                    check.get() ? GTUITextures.BUTTON_STANDARD_PRESSED : GTUITextures.BUTTON_STANDARD })
            .dynamicTooltip(() -> constructionStatusString(check))
            .setTooltipShowUpDelay(TOOLTIP_DELAY));

        // Complete text overlay
        completeGroup.addChild(
            TextWidget.dynamicText(() -> constructionStatusText(check))
                .setTextAlignment(Alignment.Center)
                .setScale(0.7f)
                .setMaxWidth(36)
                .setPos(3, 5));

        widget.addChild(completeGroup);

        return widget;
    }

    public static Widget createMaterialInputButton(EternalGregTechWorkshopUpgrade upgrade, Supplier<Boolean> check,
        BiConsumer<Widget.ClickData, Widget> clickAction) {
        Size windowSize = upgrade.getWindowSize();
        int w = windowSize.width;
        int h = windowSize.height;

        return new ButtonWidget().setOnClick(clickAction)
            .setPlayClickSound(true)
            .setBackground(
                () -> new IDrawable[] { check.get() ? TecTechUITextures.BUTTON_BOXED_CHECKMARK_18x18
                    : TecTechUITextures.BUTTON_BOXED_EXCLAMATION_POINT_18x18 })
            .setPos(w / 2 - 40, (int) (h * 0.9))
            .setSize(15, 15)
            .dynamicTooltip(() -> upgradeMaterialRequirements(check))
            .addTooltip(
                EnumChatFormatting.GRAY + translateToLocal("fog.button.materialrequirements.tooltip.clickhere"));
    }

    private static Text getAvailableShardsText(EternalGregTechWorkshopUpgrade upgrade, Supplier<Integer> shardGetter) {
        EnumChatFormatting enoughShards = EnumChatFormatting.RED;
        if (shardGetter.get() >= upgrade.getShardCost()) {
            enoughShards = EnumChatFormatting.GREEN;
        }
        return new Text(enoughShards + Integer.toString(shardGetter.get()));
    }

    private static List<String> constructionStatusString(Supplier<Boolean> check) {
        if (check.get()) {
            return ImmutableList.of(translateToLocal("fog.upgrade.respec"));
        }
        return ImmutableList.of(translateToLocal("fog.upgrade.confirm"));
    }

    private static Text constructionStatusText(Supplier<Boolean> check) {
        if (check.get()) {
            return new Text(translateToLocal("fog.upgrade.respec"));
        }
        return new Text(translateToLocal("fog.upgrade.confirm"));
    }

    private static List<String> upgradeMaterialRequirements(Supplier<Boolean> check) {
        if (check.get()) {
            return ImmutableList.of(translateToLocal("fog.button.materialrequirementsmet.tooltip"));
        }
        return ImmutableList.of(translateToLocal("fog.button.materialrequirements.tooltip"));
    }

    public static Widget createExtraCostWidget(final ItemStack costStack, Supplier<Integer> paidAmount) {
        MultiChildWidget widget = new MultiChildWidget();
        widget.setSize(36, 18);

        if (costStack == null) {
            // Nothing to pay, so just create a simple disabled slot drawable
            widget.addChild(
                new DrawableWidget().setDrawable(GTUITextures.BUTTON_STANDARD_DISABLED)
                    .setSize(18, 18));
            return widget;
        }

        // Item slot
        ItemStackHandler handler = new ItemStackHandler(1);
        ItemStack handlerStack = costStack.copy();
        handlerStack.stackSize = Math.max(1, handlerStack.stackSize - paidAmount.get());
        handler.setStackInSlot(0, handlerStack);
        widget.addChild(
            new SlotWidget(handler, 0).setAccess(false, false)
                .setRenderStackSize(false)
                .disableInteraction()
                .setBackground(GTUITextures.BUTTON_STANDARD_PRESSED))
            .addChild(new ButtonWidget().setOnClick((clickData, w) -> {
                if (widget.isClient()) {
                    if (clickData.mouseButton == 0) {
                        GuiCraftingRecipe.openRecipeGui("item", handlerStack.copy());
                    } else if (clickData.mouseButton == 1) {
                        GuiUsageRecipe.openRecipeGui("item", handlerStack.copy());
                    }
                }
            })
                .setSize(16, 16)
                .setPos(1, 1));

        // Progress text
        widget.addChild(new DynamicTextWidget(() -> {
            int paid = paidAmount.get();
            EnumChatFormatting color = EnumChatFormatting.YELLOW;
            if (paid == 0) color = EnumChatFormatting.RED;
            else if (paid == costStack.stackSize) color = EnumChatFormatting.GREEN;
            return new Text(color + "x" + (costStack.stackSize - paid));
        }).setTextAlignment(Alignment.Center)
            .setScale(0.8f)
            .setPos(18, 5)
            .setSize(18, 9)
            .setEnabled(w -> paidAmount.get() < costStack.stackSize));

        // Completed checkmark
        widget.addChild(
            new DrawableWidget().setDrawable(TecTechUITextures.GREEN_CHECKMARK_11x9)
                .setPos(21, 5)
                .setSize(11, 9)
                .setEnabled(w -> paidAmount.get() >= costStack.stackSize));

        return widget;
    }
}
