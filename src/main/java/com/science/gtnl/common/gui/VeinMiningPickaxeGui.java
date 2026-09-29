package com.science.gtnl.common.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.item.items.VeinMiningPickaxe;
import com.science.gtnl.config.MainConfig;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public class VeinMiningPickaxeGui {

    private static final String PREFIX = "gtnl.gui.vein_mining_pickaxe.";

    private final ItemStack stack;
    private final PanelSyncManager syncManager;

    public VeinMiningPickaxeGui(PlayerInventoryGuiData data, PanelSyncManager syncManager) {
        stack = data.getUsedItemStack();
        if (stack == null || !(stack.getItem() instanceof VeinMiningPickaxe)) {
            throw new IllegalStateException("Tried to open the Vein Mining Pickaxe GUI without the pickaxe");
        }
        this.syncManager = syncManager;
    }

    public ModularPanel build() {
        ModularPanel panel = GTGuis.createPopUpPanel("vein_mining_pickaxe");
        panel.size(254, 159);

        DoubleSyncValue rangeSlider = new DoubleSyncValue(
            () -> VeinMiningPickaxe.getRange(stack),
            value -> VeinMiningPickaxe.setRange(stack, (int) Math.round(value))).allowC2S();
        IntSyncValue rangeField = new IntSyncValue(
            () -> VeinMiningPickaxe.getRange(stack),
            value -> VeinMiningPickaxe.setRange(stack, value)).allowC2S();
        DoubleSyncValue amountSlider = new DoubleSyncValue(
            () -> VeinMiningPickaxe.getAmount(stack),
            value -> VeinMiningPickaxe.setAmount(stack, (int) Math.round(value))).allowC2S();
        IntSyncValue amountField = new IntSyncValue(
            () -> VeinMiningPickaxe.getAmount(stack),
            value -> VeinMiningPickaxe.setAmount(stack, value)).allowC2S();
        BooleanSyncValue preciseMode = new BooleanSyncValue(
            () -> VeinMiningPickaxe.isPreciseMode(stack),
            value -> VeinMiningPickaxe.setPreciseMode(stack, value)).allowC2S();
        BooleanSyncValue chainEnabled = new BooleanSyncValue(
            () -> VeinMiningPickaxe.isChainEnabled(stack),
            value -> VeinMiningPickaxe.setChainEnabled(stack, value)).allowC2S();

        syncManager.syncValue("rangeSlider", rangeSlider);
        syncManager.syncValue("rangeField", rangeField);
        syncManager.syncValue("amountSlider", amountSlider);
        syncManager.syncValue("amountField", amountField);
        syncManager.syncValue("preciseMode", preciseMode);
        syncManager.syncValue("chainEnabled", chainEnabled);

        panel.child(
            IKey.lang(PREFIX + "title")
                .asWidget()
                .pos(8, 6)
                .size(238, 16)
                .textAlign(Alignment.Center));
        addNumberRow(
            panel,
            "range",
            30,
            rangeSlider,
            rangeField,
            -1,
            Math.max(-1, MainConfig.item.vein_miner_pickaxe.maxRange),
            1);
        addNumberRow(
            panel,
            "amount",
            62,
            amountSlider,
            amountField,
            0,
            Math.max(0, MainConfig.item.vein_miner_pickaxe.maxAmount),
            1000);

        panel.child(
            IKey.lang(PREFIX + "chain")
                .asWidget()
                .pos(10, 99)
                .size(108, 18)
                .textAlign(Alignment.CenterLeft)
                .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(PREFIX + "chain.tooltip"))));
        panel.child(createToggleButton(chainEnabled, 124, 98, "chain.tooltip"));
        panel.child(
            IKey.lang(PREFIX + "precise")
                .asWidget()
                .pos(10, 131)
                .size(108, 18)
                .textAlign(Alignment.CenterLeft)
                .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(PREFIX + "precise.tooltip"))));
        panel.child(createToggleButton(preciseMode, 124, 130, "precise.tooltip"));
        return panel;
    }

    private ButtonWidget<?> createToggleButton(BooleanSyncValue value, int x, int y, String tooltipKey) {
        return new ButtonWidget<>().pos(x, y)
            .size(70, 18)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(
                IKey.dynamic(
                    () -> StatCollector.translateToLocal(PREFIX + (value.getBoolValue() ? "enabled" : "disabled"))))
            .onMousePressed(button -> {
                if (button != 0) return false;
                value.setBoolValue(!value.getBoolValue());
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(PREFIX + tooltipKey)));
    }

    private void addNumberRow(ModularPanel panel, String name, int y, DoubleSyncValue slider, IntSyncValue field,
        int minimum, int maximum, int scrollStep) {
        String key = PREFIX + name;
        panel.child(
            IKey.lang(key)
                .asWidget()
                .pos(10, y)
                .size(70, 18)
                .textAlign(Alignment.CenterLeft)
                .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(key + ".tooltip"))));
        panel.child(new SliderWidget() {

            @Override
            public boolean onMouseScroll(UpOrDown direction, int amount) {
                long next = Math.round(slider.getDoubleValue()) + (long) direction.modifier * amount * scrollStep;
                slider.setDoubleValue(Math.max(minimum, Math.min(maximum, next)));
                return true;
            }
        }.pos(84, y)
            .size(110, 18)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .sliderSize(10, 18)
            .bounds(minimum, maximum)
            .value(slider)
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(key + ".tooltip"))));
        panel.child(
            new TextFieldWidget().value(field)
                .numbersInt(minimum, maximum)
                .scrollValues(scrollStep, scrollStep * 10, scrollStep * 100, scrollStep * 1000)
                .setTextAlignment(Alignment.Center)
                .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                .pos(202, y)
                .size(43, 18)
                .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(key + ".tooltip"))));
    }
}
