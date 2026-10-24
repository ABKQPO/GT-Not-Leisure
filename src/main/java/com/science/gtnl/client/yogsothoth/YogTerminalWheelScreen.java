package com.science.gtnl.client.yogsothoth;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.utils.Alignment;
import com.gtnllib.gui.wheel.WheelEntry;
import com.gtnllib.gui.wheel.WheelMenuBuilder;
import com.gtnllib.gui.wheel.WheelMenuModel;
import com.gtnllib.gui.wheel.WheelOpenMode;
import com.gtnllib.gui.wheel.WheelScreen;
import com.science.gtnl.common.item.items.ItemYogSothothTerminal;
import com.science.gtnl.common.yogsothoth.YogTerminalActionPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class YogTerminalWheelScreen extends WheelScreen {

    private static final String LANG = "gtnl.yog_sothoth_terminal.wheel.";
    private static final int DETAIL_GAP = 8, DETAIL_WIDTH = 320, DETAIL_HEIGHT = 10;
    private static final int SELF_INDEX = -1;

    private final UUID id;

    private YogTerminalWheelScreen(KeyBinding key, YogTerminalClient.Token token) {
        super(menu(token.id(), token.view()), WheelOpenMode.HOLD, key);
        this.id = token.id();
        below(
            IKey.dynamic(this::detail)
                .alignment(Alignment.Center)
                .asWidget()
                .size(DETAIL_WIDTH, DETAIL_HEIGHT),
            DETAIL_GAP);
    }

    public static boolean open(KeyBinding key) {
        YogTerminalClient.Token token = YogTerminalClient.heldToken();
        if (token == null) return false;
        YogTerminalClient.View view = token.view();
        if (view == null || view.terminals.isEmpty()) return false;
        new YogTerminalWheelScreen(key, token).open();
        return true;
    }

    private static WheelMenuModel menu(UUID id, YogTerminalClient.View view) {
        WheelMenuBuilder builder = WheelMenuBuilder.create();
        builder.entry(
            WheelEntry
                .action(
                    "yog_self",
                    "",
                    new ItemDrawable(new ItemStack(ItemYogSothothTerminal.INSTANCE)),
                    context -> select(id, SELF_INDEX))
                .labelFrom(() -> tr("self"))
                .onRightClick(context -> select(id, SELF_INDEX)));
        int count = view == null ? 0 : view.terminals.size();
        for (int slot = 0; slot < count; slot++) {
            int index = slot;
            builder.entry(
                WheelEntry.action("yog_" + index, "", icon(id, index), context -> select(id, index))
                    .labelFrom(() -> label(id, index))
                    .onRightClick(context -> use(id, index))
                    .visibleWhen(() -> entry(id, index) != null));
        }
        return builder.build();
    }

    private static IDrawable icon(UUID id, int index) {
        return new DynamicDrawable(() -> {
            ItemStack stack = entry(id, index);
            return stack == null ? IDrawable.EMPTY : new ItemDrawable(stack);
        });
    }

    private static ItemStack entry(UUID id, int index) {
        YogTerminalClient.View view = YogTerminalClient.view(id);
        return view == null ? null : view.get(index);
    }

    private static String label(UUID id, int index) {
        ItemStack stack = entry(id, index);
        if (stack == null) return "";
        YogTerminalClient.View view = YogTerminalClient.view(id);
        String name = stack.getDisplayName();
        if (view != null && index == view.active) name = EnumChatFormatting.LIGHT_PURPLE + name;
        return name + "\n" + EnumChatFormatting.GRAY + tr("open_hint");
    }

    private String detail() {
        YogTerminalClient.View view = YogTerminalClient.view(id);
        if (view == null) return "";
        ItemStack active = view.get(view.active);
        return active == null ? tr("detail.self")
            : StatCollector.translateToLocalFormatted(LANG + "detail", active.getDisplayName());
    }

    private static void select(UUID id, int index) {
        YogTerminalClient.Token token = YogTerminalClient.heldToken();
        if (token != null && token.id()
            .equals(id)) YogTerminalClient.send(YogTerminalActionPacket.disguise(id, token.location(), index));
    }

    private static void use(UUID id, int index) {
        YogTerminalClient.Token token = YogTerminalClient.heldToken();
        if (token != null && token.id()
            .equals(id)) YogTerminalClient.use(token, index);
    }

    private static String tr(String key) {
        return StatCollector.translateToLocal(LANG + key);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null) {
            close();
            return;
        }
        YogTerminalClient.Token held = YogTerminalClient.heldToken();
        if (held == null || !held.id()
            .equals(id)) close();
    }
}
