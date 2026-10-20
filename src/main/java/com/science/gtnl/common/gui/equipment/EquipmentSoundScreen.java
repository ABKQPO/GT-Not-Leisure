package com.science.gtnl.common.gui.equipment;

import java.util.List;
import java.util.function.BooleanSupplier;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.EquipmentSound;
import com.science.gtnl.common.gui.equipment.EquipmentTheme.Colors;
import com.science.gtnl.common.gui.modularui.widget.CardDrawable;
import com.science.gtnl.common.gui.modularui.widget.SlideSwitch;
import com.science.gtnl.common.gui.modularui.widget.StepSlider;
import com.science.gtnl.config.EquipmentSoundConfig;
import com.science.gtnl.config.EquipmentSoundConfig.Channel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EquipmentSoundScreen extends ModularScreen {

    private static final int PAD = 10, TITLE_HEIGHT = 20, TITLE_RULE = 7, CARD_GAP = 10, CARD_PAD = 5, CARD_HEADER = 17,
        ROW = 16, ROW_HEIGHT = 14, LABEL_WIDTH = 96, GAP = 4, SLIDER_WIDTH = 92, HINT_HEIGHT = 16, CLOSE_SIZE = 11,
        TAG_WIDTH = 19;
    private static final int STEPS = 20, MILESTONE_EVERY = 5;

    public EquipmentSoundScreen() {
        super(ScienceNotLeisure.MODID, panel(EquipmentTheme.colors()));
        useTheme(EquipmentTheme.selected());
    }

    public static void open() {
        ClientGUI.open(new EquipmentSoundScreen());
    }

    private static ModularPanel panel(Colors colors) {
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
        ModularPanel panel = new ModularPanel("equipment_sound").size(width, height);
        panel.child(
            IKey.lang("item.gtnl.equipment.gui.sound")
                .asWidget()
                .pos(PAD + 2, PAD)
                .size(width - PAD * 2 - CLOSE_SIZE - 4, 10));
        panel.child(
            new Rectangle().horizontalGradient(colors.accent, 0)
                .asWidget()
                .pos(PAD, PAD + TITLE_HEIGHT - TITLE_RULE)
                .size((width - PAD * 2) / 2, 1));
        panel.child(
            EquipmentGui.closeButton()
                .size(CLOSE_SIZE, CLOSE_SIZE)
                .pos(width - PAD - CLOSE_SIZE + 2, PAD - 2));
        for (int index = 0; index < groups.length; index++) card(
            panel,
            colors,
            groups[index],
            PAD + index * (cardWidth + CARD_GAP),
            PAD + TITLE_HEIGHT,
            cardWidth,
            cardHeight);
        panel.child(
            IKey.lang("item.gtnl.equipment.gui.sound.hint")
                .color(colors.hint)
                .shadow(false)
                .asWidget()
                .pos(PAD + 2, height - PAD / 2 - HINT_HEIGHT + 5)
                .size(width - PAD * 2, 10));
        return panel;
    }

    private static void card(ModularPanel panel, Colors colors, EquipmentSound.Group group, int x, int y, int width,
        int height) {
        panel.child(
            new Widget<>().widgetTheme(CardDrawable.KEY)
                .pos(x, y)
                .size(width, height));
        panel.child(
            IKey.lang(group.lang())
                .color(colors.accent)
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
            new SlideSwitch().value(() -> channel.enabled)
                .liveWhen(audible)
                .label(IKey.dynamic(() -> StatCollector.translateToLocal(lang)))
                .pos(x, y)
                .size(LABEL_WIDTH, ROW_HEIGHT)
                .onMousePressed(button -> {
                    if (button != 0) return false;
                    channel.enabled = !channel.enabled;
                    EquipmentSoundConfig.save();
                    return true;
                }));
        panel.child(
            new StepSlider().value(() -> channel.volume, value -> channel.volume = (float) value)
                .range(0, 1, STEPS)
                .milestones(MILESTONE_EVERY)
                .format(value -> Math.round(value * 100) + "%")
                .activeWhen(() -> channel.enabled && audible.getAsBoolean())
                .tagWidth(TAG_WIDTH)
                .compactDigits(true)
                .onRelease(EquipmentSoundConfig::save)
                .pos(x + LABEL_WIDTH + GAP, y)
                .size(SLIDER_WIDTH, ROW_HEIGHT));
    }

    @Override
    public void onClose() {
        EquipmentSoundConfig.save();
    }
}
