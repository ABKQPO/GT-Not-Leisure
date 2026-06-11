package com.science.gtnl.common.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.MCHelper;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.ByteSyncValue;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.block.blocks.item.ItemBlockPlayerDoll;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public class PlayerDollGui {

    private static final int TEXT_COLOR = Color.WHITE.main;

    private final PlayerInventoryGuiData data;

    public PlayerDollGui(PlayerInventoryGuiData data) {
        this.data = data;
        ItemStack stack = data.getUsedItemStack();
        if (stack == null || !(stack.getItem() instanceof ItemBlockPlayerDoll)) {
            throw new IllegalStateException("Tried to open the player doll GUI without a player doll item");
        }
    }

    public ModularPanel build() {
        ModularPanel panel = GTGuis.createPopUpPanel("player_doll");
        panel.size(300, 97);

        ItemStack stack = data.getUsedItemStack();
        ByteSyncValue renderModeSync = new ByteSyncValue(
            () -> ItemBlockPlayerDoll.getRenderMode(stack),
            mode -> ItemBlockPlayerDoll.setRenderMode(stack, mode)).allowC2S();

        panel.child(createTextField(stack, "SkullOwner", 8, 8, 77));
        panel.child(createLabel("Tooltip_PlayerDoll_00", 88, 10, 160));

        panel.child(createTextField(stack, "SkinHttp", 8, 26, 197));
        panel.child(createLabel("Tooltip_PlayerDoll_02", 208, 28, 85));

        panel.child(createTextField(stack, "CapeHttp", 8, 44, 197));
        panel.child(createLabel("Tooltip_PlayerDoll_04", 208, 46, 85));

        panel.child(createRenderModeButton(renderModeSync).pos(64, 66));
        panel.child(createLabel("Tooltip_PlayerDoll_03", 85, 68, 160));

        panel.child(
            new ButtonWidget<>().size(48, 20)
                .pos(8, 62)
                .background(GTGuiTextures.BUTTON_STANDARD)
                .overlay(IKey.lang("Tooltip_PlayerDoll_01"))
                .onMousePressed(mouseButton -> {
                    MCHelper.closeScreen();
                    return true;
                }));

        return panel;
    }

    private TextFieldWidget createTextField(ItemStack stack, String key, int x, int y, int width) {
        return new TextFieldWidget()
            .value(
                SyncHandlers.string(() -> getString(stack, key), value -> setString(stack, key, value))
                    .allowC2S())
            .setTextAlignment(Alignment.CenterLeft)
            .setTextColor(TEXT_COLOR)
            .autoUpdateOnChange(true)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .pos(x, y)
            .size(width, 12);
    }

    private TextWidget<?> createLabel(String translationKey, int x, int y, int width) {
        return new TextWidget<>(IKey.lang(translationKey)).color(TEXT_COLOR)
            .pos(x, y)
            .size(width, 12);
    }

    private ButtonWidget<?> createRenderModeButton(ByteSyncValue renderModeSync) {
        return new ButtonWidget<>().size(16)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(new DynamicDrawable(() -> switch (renderModeSync.getByteValue()) {
            case ItemBlockPlayerDoll.RENDER_CAPE -> GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER;
            case ItemBlockPlayerDoll.RENDER_ELYTRA -> GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_UNPACKAGER;
            default -> GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT;
            }))
            .onMousePressed(mouseButton -> {
                if (mouseButton != 0 && mouseButton != 1) return false;
                byte mode = (byte) (renderModeSync.getByteValue() + 1);
                if (mode > ItemBlockPlayerDoll.RENDER_ELYTRA) {
                    mode = ItemBlockPlayerDoll.RENDER_OFF;
                }
                renderModeSync.setByteValue(mode);
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(getRenderModeTooltip(renderModeSync.getByteValue())))
            .tooltipAutoUpdate(true);
    }

    private static String getString(ItemStack stack, String key) {
        if (!stack.hasTagCompound()) return "";
        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey(key, 8)) return "";
        return nbt.getString(key);
    }

    private static void setString(ItemStack stack, String key, String value) {
        ItemBlockPlayerDoll.getOrCreateTag(stack)
            .setString(key, value);
    }

    private static String getRenderModeTooltip(byte mode) {
        return switch (mode) {
            case ItemBlockPlayerDoll.RENDER_CAPE -> StatCollector
                .translateToLocal("Waila_TileEntityPlayerDoll_03_Cape");
            case ItemBlockPlayerDoll.RENDER_ELYTRA -> StatCollector
                .translateToLocal("Waila_TileEntityPlayerDoll_03_Elytra");
            default -> StatCollector.translateToLocal("Waila_TileEntityPlayerDoll_03_Off");
        };
    }
}
