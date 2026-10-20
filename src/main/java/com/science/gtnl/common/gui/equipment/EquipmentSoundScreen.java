package com.science.gtnl.common.gui.equipment;

import java.util.List;
import java.util.function.BooleanSupplier;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.EquipmentSound;
import com.science.gtnl.common.gui.modularui.widget.StepSlider;
import com.science.gtnl.config.EquipmentSoundConfig;
import com.science.gtnl.config.EquipmentSoundConfig.Channel;
import com.science.gtnl.utils.RenderUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EquipmentSoundScreen extends ModularScreen {

    private static final int PAD = 10, TITLE_HEIGHT = 20, CARD_GAP = 10, CARD_PAD = 5, CARD_HEADER = 17, ROW = 16,
        ROW_HEIGHT = 14, LABEL_WIDTH = 96, GAP = 4, SLIDER_WIDTH = 92, HINT_HEIGHT = 16, CLOSE_SIZE = 11,
        SWITCH_WIDTH = 12, SWITCH_HEIGHT = 6, LABEL_INDENT = 18, CORNER = 6, TAG_WIDTH = 19;
    private static final int STEPS = 20, MILESTONE_EVERY = 5;
    private static final int ACCENT = 0xFFFF3D9A, ACCENT_DIM = 0x80FF3D9A, BACK_TOP = 0xF2211A27,
        BACK_BOTTOM = 0xF20D0A11, CARD = 0x50000000, CARD_EDGE = 0x24FFFFFF, HOVER = 0x26FF3D9A,
        SWITCH_OFF = 0xFF3A3640, KNOB = 0xFFF2EEF4, KNOB_OFF = 0xFF8C8794, TITLE = 0xFFFFFFFF, TEXT_ON = 0xFFF4F0F6,
        TEXT_MUTED = 0xFF6E6876, TEXT_OFF = 0xFF9A94A2, HINT = 0xFF8E8796, TRACK = 0xFF1A1420, CURSOR = 0xFFFFE6F1,
        CURSOR_TEXT = 0xFF4A0A2A, TICK = 0x40FFFFFF, MILESTONE = 0x99FFFFFF;

    public EquipmentSoundScreen() {
        super(ScienceNotLeisure.MODID, panel());
    }

    public static void open() {
        ClientGUI.open(new EquipmentSoundScreen());
    }

    private static ModularPanel panel() {
        EquipmentSound.Group[] groups = EquipmentSound.Group.values();
        int rows = 0;
        for (EquipmentSound.Group group : groups) rows = Math.max(
            rows,
            group.sounds()
                .size() + 1);
        int cardWidth = CARD_PAD * 2 + LABEL_WIDTH + GAP + SLIDER_WIDTH;
        int cardHeight = CARD_HEADER + rows * ROW + CARD_PAD;
        int width = PAD * 2 + cardWidth * groups.length + CARD_GAP * (groups.length - 1);
        int height = PAD + TITLE_HEIGHT + cardHeight + HINT_HEIGHT + PAD / 2;
        ModularPanel panel = new ModularPanel("equipment_sound").size(width, height)
            .disableThemeBackground(true)
            .disableHoverBackground()
            .background(EquipmentSoundScreen::drawFrame);
        panel.child(
            IKey.lang("item.gtnl.equipment.gui.sound")
                .color(TITLE)
                .shadow(true)
                .asWidget()
                .pos(PAD + 2, PAD)
                .size(width - PAD * 2 - CLOSE_SIZE - 4, 10));
        panel.child(closeButton(panel).pos(width - PAD - CLOSE_SIZE + 2, PAD - 2));
        for (int index = 0; index < groups.length; index++)
            card(panel, groups[index], PAD + index * (cardWidth + CARD_GAP), PAD + TITLE_HEIGHT, cardWidth, cardHeight);
        panel.child(
            IKey.lang("item.gtnl.equipment.gui.sound.hint")
                .color(HINT)
                .asWidget()
                .pos(PAD + 2, height - PAD / 2 - HINT_HEIGHT + 5)
                .size(width - PAD * 2, 10));
        return panel;
    }

    private static void drawFrame(GuiContext context, int x, int y, int width, int height, WidgetTheme theme) {
        GuiDraw.drawRect(x, y, width, height, BACK_TOP, BACK_TOP, BACK_BOTTOM, BACK_BOTTOM);
        GuiDraw.drawRect(x, y, width, 1, ACCENT_DIM);
        GuiDraw.drawRect(x, y + height - 1, width, 1, ACCENT_DIM);
        GuiDraw.drawRect(x, y + 1, 1, height - 2, ACCENT_DIM);
        GuiDraw.drawRect(x + width - 1, y + 1, 1, height - 2, ACCENT_DIM);
        for (int corner = 0; corner < 4; corner++) {
            boolean left = corner % 2 == 0, top = corner < 2;
            GuiDraw.drawRect(left ? x : x + width - CORNER, top ? y : y + height - 2, CORNER, 2, ACCENT);
            GuiDraw.drawRect(left ? x : x + width - 2, top ? y : y + height - CORNER, 2, CORNER, ACCENT);
        }
        GuiDraw.drawRect(x + PAD, y + PAD + TITLE_HEIGHT - 7, (width - PAD * 2) / 2F, 1, ACCENT, 0, ACCENT, 0);
    }

    private static ButtonWidget<?> closeButton(ModularPanel panel) {
        return new ButtonWidget<>().size(CLOSE_SIZE, CLOSE_SIZE)
            .disableThemeBackground(true)
            .disableHoverBackground()
            .background(IDrawable.EMPTY)
            .hoverBackground(
                (IDrawable) (context, x, y, width, height, theme) -> GuiDraw.drawRect(x, y, width, height, HOVER))
            .overlay(
                IKey.str("×")
                    .color(TITLE))
            .onMousePressed(button -> {
                if (button != 0) return false;
                Interactable.playButtonClickSound();
                panel.closeIfOpen();
                return true;
            });
    }

    private static void card(ModularPanel panel, EquipmentSound.Group group, int x, int y, int width, int height) {
        panel.child(((IDrawable) (context, dx, dy, w, h, theme) -> {
            GuiDraw.drawRect(dx, dy, w, h, CARD);
            GuiDraw.drawRect(dx, dy, w, 1, CARD_EDGE);
            GuiDraw.drawRect(dx, dy + h - 1, w, 1, CARD_EDGE);
            GuiDraw.drawRect(dx, dy + 3, 2, CARD_HEADER - 7, ACCENT);
        }).asWidget()
            .pos(x, y)
            .size(width, height));
        panel.child(
            IKey.lang(group.lang())
                .color(ACCENT)
                .shadow(true)
                .asWidget()
                .pos(x + CARD_PAD + 2, y + 4)
                .size(width - CARD_PAD * 2, 10));
        int rowX = x + CARD_PAD, rowY = y + CARD_HEADER;
        row(panel, rowX, rowY, "gtnl.gui.config.sound.all", group.all, () -> true);
        List<EquipmentSound> sounds = group.sounds();
        for (int i = 0; i < sounds.size(); i++) {
            EquipmentSound sound = sounds.get(i);
            row(panel, rowX, rowY + (i + 1) * ROW, sound.lang(), sound.channel, () -> group.all.enabled);
        }
    }

    private static void row(ModularPanel panel, int x, int y, String lang, Channel channel, BooleanSupplier audible) {
        panel.child(
            new ButtonWidget<>().pos(x, y)
                .size(LABEL_WIDTH, ROW_HEIGHT)
                .disableThemeBackground(true)
                .disableHoverBackground()
                .background(toggle(lang, channel, audible, false))
                .hoverBackground(toggle(lang, channel, audible, true))
                .onMousePressed(button -> {
                    if (button != 0) return false;
                    channel.enabled = !channel.enabled;
                    Interactable.playButtonClickSound();
                    EquipmentSoundConfig.save();
                    return true;
                }));
        panel.child(
            new StepSlider().value(() -> channel.volume, value -> channel.volume = (float) value)
                .range(0, 1, STEPS)
                .milestones(MILESTONE_EVERY)
                .format(value -> Math.round(value * 100) + "%")
                .activeWhen(() -> channel.enabled && audible.getAsBoolean())
                .colors(TRACK, ACCENT, CURSOR, CURSOR_TEXT)
                .tickColors(TICK, MILESTONE)
                .tagWidth(TAG_WIDTH)
                .compactDigits(true)
                .onRelease(EquipmentSoundConfig::save)
                .pos(x + LABEL_WIDTH + GAP, y)
                .size(SLIDER_WIDTH, ROW_HEIGHT));
    }

    private static IDrawable toggle(String lang, Channel channel, BooleanSupplier audible, boolean hover) {
        return (context, x, y, width, height, theme) -> {
            if (hover) GuiDraw.drawRect(x, y, width, height, HOVER);
            boolean on = channel.enabled, heard = on && audible.getAsBoolean();
            int sx = x + 3, sy = y + (height - SWITCH_HEIGHT) / 2;
            GuiDraw.drawRect(sx, sy, SWITCH_WIDTH, SWITCH_HEIGHT, !on ? SWITCH_OFF : heard ? ACCENT : ACCENT_DIM);
            GuiDraw.drawRect(
                on ? sx + SWITCH_WIDTH - SWITCH_HEIGHT : sx,
                sy,
                SWITCH_HEIGHT,
                SWITCH_HEIGHT,
                on ? KNOB : KNOB_OFF);
            RenderUtils.drawTextLine(
                StatCollector.translateToLocal(lang),
                x + LABEL_INDENT,
                y + 1,
                width - LABEL_INDENT,
                height,
                Alignment.CenterLeft,
                !on ? TEXT_OFF : heard ? TEXT_ON : TEXT_MUTED,
                heard);
        };
    }

    @Override
    public void onClose() {
        EquipmentSoundConfig.save();
    }
}
