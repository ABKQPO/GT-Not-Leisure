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
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.gui.equipment.EquipmentSoundScreen;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnllib.client.hud.HudEditor;
import com.science.gtnllib.gui.layout.Layout;
import com.science.gtnllib.gui.layout.LayoutMath;
import com.science.gtnllib.gui.layout.LayoutTheme;
import com.science.gtnllib.gui.layout.Size;
import com.science.gtnllib.gui.theme.ButtonPurpose;
import com.science.gtnllib.gui.wheel.WheelEntry;
import com.science.gtnllib.gui.wheel.WheelMenuBuilder;
import com.science.gtnllib.gui.wheel.WheelMenuModel;
import com.science.gtnllib.gui.wheel.WheelOpenMode;
import com.science.gtnllib.gui.wheel.WheelScreen;
import com.science.gtnllib.gui.wheel.WheelStyle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveWheelScreen extends WheelScreen {

    private static final int[] PIECE = { 0, 1, 3, 2 };
    private static final int TITLE_GAP = 8;
    private static final int BUTTON_WIDTH = 72, BUTTON_GAP = 22;
    private static final String HUD = HudEditor.OPEN_LANG, SOUND = "item.gtnl.celestweave.wheel.sound";

    public CelestweaveWheelScreen(KeyBinding key) {
        super(menu(), WheelOpenMode.HOLD, key);
        useTheme(EquipmentTheme.selected());
        below(
            IKey.str(tr("item.gtnl.celestweave.wheel.title"))
                .asWidget(),
            TITLE_GAP);
        LayoutTheme style = EquipmentTheme.layout();
        int width = Size
            .content(
                LayoutMath.widest(Layout.textWidth(tr(HUD), style), Layout.textWidth(tr(SOUND), style)),
                style.padding)
            .atLeast(BUTTON_WIDTH)
            .resolve(0), shift = (width + style.spacingMedium) / 2;
        below(button(HUD, HudEditor::open, width, style), BUTTON_GAP, -shift);
        below(button(SOUND, EquipmentSoundScreen::open, width, style), BUTTON_GAP, shift);
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

    private static String tr(String lang) {
        return StatCollector.translateToLocal(lang);
    }

    private ButtonWidget<?> button(String lang, Runnable open, int width, LayoutTheme style) {
        return ButtonPurpose.NORMAL.apply(new ButtonWidget<>())
            .size(width, style.buttonHeight)
            .overlay(Layout.caption(IKey.str(tr(lang)), style))
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
