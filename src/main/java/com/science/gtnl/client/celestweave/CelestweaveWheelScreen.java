package com.science.gtnl.client.celestweave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.hud.HudEditor;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.gui.equipment.EquipmentSoundScreen;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.gui.modularui.wheel.WheelEntry;
import com.science.gtnl.common.gui.modularui.wheel.WheelMenuBuilder;
import com.science.gtnl.common.gui.modularui.wheel.WheelMenuModel;
import com.science.gtnl.common.gui.modularui.wheel.WheelOpenMode;
import com.science.gtnl.common.gui.modularui.wheel.WheelScreen;
import com.science.gtnl.common.gui.modularui.wheel.WheelStyle;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveWheelScreen extends WheelScreen {

    private static final int[] PIECE = { 0, 1, 3, 2 };
    private static final int TITLE_GAP = 8;
    private static final int HUD_WIDTH = 72, HUD_HEIGHT = 14, HUD_GAP = 22, BUTTON_SPACING = 4;

    public CelestweaveWheelScreen(KeyBinding key) {
        super(menu(), WheelOpenMode.HOLD, key);
        useTheme(EquipmentTheme.selected());
        below(
            IKey.str(StatCollector.translateToLocal("item.gtnl.celestweave.wheel.title"))
                .asWidget(),
            TITLE_GAP);
        int shift = (HUD_WIDTH + BUTTON_SPACING) / 2;
        below(button("gtnl.hud.editor.open", HudEditor::open), HUD_GAP, -shift);
        below(button("item.gtnl.celestweave.wheel.sound", EquipmentSoundScreen::open), HUD_GAP, shift);
    }

    private static WheelMenuModel menu() {
        WheelMenuBuilder builder = WheelMenuBuilder.create()
            .slotsPerPage(PIECE.length)
            .style(
                WheelStyle.builder()
                    .highlightColor(EquipmentTheme.colors().accent)
                    .build());
        for (int piece : PIECE) {
            String label = StatCollector.translateToLocal("item.gtnl.celestweave.wheel.piece." + piece);
            builder.entry(
                WheelEntry
                    .action(
                        "piece_" + piece,
                        label,
                        new DynamicDrawable(() -> new ItemDrawable(piece(piece))),
                        context -> open(piece))
                    .visibleWhen(() -> piece(piece) != null));
        }
        return builder.build();
    }

    private static ItemStack piece(int piece) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        return player == null ? null : CelestweaveData.piece(player, piece);
    }

    private static void open(int piece) {
        if (piece(piece) == null) return;
        ScienceNotLeisure.network.sendToServer(new CelestweaveKeyPacket(CelestweaveKeyPacket.OPEN_PIECE, piece));
    }

    private ButtonWidget<?> button(String lang, Runnable open) {
        return new ButtonWidget<>().size(HUD_WIDTH, HUD_HEIGHT)
            .overlay(IKey.str(StatCollector.translateToLocal(lang)))
            .onMousePressed(button -> {
                openNext(open);
                return true;
            });
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null || !CelestweaveData.wearsAny(player)) close();
    }
}
