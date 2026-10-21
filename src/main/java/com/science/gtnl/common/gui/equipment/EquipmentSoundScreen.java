package com.science.gtnl.common.gui.equipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.sizer.Unit;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.EquipmentSound;
import com.science.gtnl.common.gui.equipment.EquipmentTheme.Colors;
import com.science.gtnl.config.EquipmentSoundConfig;
import com.science.gtnl.config.EquipmentSoundConfig.Channel;
import com.science.gtnllib.gui.layout.Layout;
import com.science.gtnllib.gui.layout.LayoutMath;
import com.science.gtnllib.gui.layout.Size;
import com.science.gtnllib.gui.widget.CloseButton;
import com.science.gtnllib.gui.widget.LayoutTheme;
import com.science.gtnllib.gui.widget.SlideSwitch;
import com.science.gtnllib.gui.widget.StepSlider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EquipmentSoundScreen extends ModularScreen {

    private static final int TITLE_HEIGHT = 20, TITLE_RULE = 7, TEXT_HEIGHT = 10, LABEL_WIDTH = 96, SLIDER_WIDTH = 92,
        TAG_WIDTH = 19;
    private static final int STEPS = 20, MILESTONE_EVERY = 5;
    private static final String HINT = "item.gtnl.equipment.gui.sound.hint";

    private final Arrangement arrangement;

    public EquipmentSoundScreen() {
        this(new Arrangement(EquipmentTheme.layout()));
    }

    private EquipmentSoundScreen(Arrangement arrangement) {
        super(ScienceNotLeisure.MODID, panel(arrangement, EquipmentTheme.colors()));
        this.arrangement = arrangement;
        useTheme(EquipmentTheme.selected());
    }

    public static void open() {
        ClientGUI.open(new EquipmentSoundScreen());
    }

    private static ModularPanel panel(Arrangement arrangement, Colors colors) {
        LayoutTheme style = arrangement.style;
        int pad = style.spacingLarge;
        ModularPanel panel = new ModularPanel("equipment_sound").width(arrangement::width, Unit.Measure.PIXEL)
            .height(arrangement::height, Unit.Measure.PIXEL);
        panel.child(
            Layout.label(IKey.lang("item.gtnl.equipment.gui.sound"), style)
                .pos(pad + style.spacingSmall, pad)
                .width(() -> arrangement.width() - pad * 2 - CloseButton.SIZE - style.spacingMedium, Unit.Measure.PIXEL)
                .height(TEXT_HEIGHT));
        panel.child(
            new Rectangle().horizontalGradient(colors.accent, 0)
                .asWidget()
                .pos(pad, pad + TITLE_HEIGHT - TITLE_RULE)
                .width(() -> (arrangement.width() - pad * 2) / 2D, Unit.Measure.PIXEL)
                .height(1));
        panel.child(new CloseButton().corner(pad, style));
        EquipmentSound.Group[] groups = EquipmentSound.Group.values();
        for (int index = 0; index < groups.length; index++) {
            final int card = index;
            panel.child(
                card(colors, groups[index], arrangement).left(() -> arrangement.x(card), Unit.Measure.PIXEL)
                    .top(() -> arrangement.y(card), Unit.Measure.PIXEL)
                    .height(() -> arrangement.cardHeight(card), Unit.Measure.PIXEL));
        }
        panel.child(
            IKey.lang(HINT)
                .color(colors.hint)
                .shadow(false)
                .asWidget()
                .left(pad + style.spacingSmall)
                .top(arrangement::hintTop, Unit.Measure.PIXEL)
                .width(() -> arrangement.width() - pad * 2, Unit.Measure.PIXEL)
                .height(() -> arrangement.hintLines() * TEXT_HEIGHT, Unit.Measure.PIXEL));
        return panel;
    }

    private static ParentWidget<?> card(Colors colors, EquipmentSound.Group group, Arrangement arrangement) {
        LayoutTheme style = arrangement.style;
        Layout rows = Layout.rows(style, arrangement.cardWidth)
            .inset(style.spacingMedium);
        List<IWidget> lines = new ArrayList<>();
        lines.add(row(rows, arrangement, "gtnl.gui.config.sound.all", group.all, () -> true));
        for (EquipmentSound sound : group.sounds())
            lines.add(row(rows, arrangement, sound.lang(), sound.channel, () -> group.all.enabled));
        return Layout.card(IKey.lang(group.lang()), colors.accent, style, arrangement.cardWidth, 0)
            .child(
                Flow.column()
                    .pos(0, Layout.cardHeader(style))
                    .size(arrangement.cardWidth, lines.size() * style.rowHeight)
                    .children(lines));
    }

    private static IWidget row(Layout rows, Arrangement arrangement, String lang, Channel channel,
        BooleanSupplier audible) {
        return rows.line(
            arrangement.style.spacingMedium,
            Arrays.asList(Size.fixed(arrangement.labelWidth), Size.fixed(SLIDER_WIDTH)),
            Arrays.asList(
                new SlideSwitch().value(() -> channel.enabled)
                    .liveWhen(audible)
                    .label(IKey.dynamic(() -> EquipmentGui.tr(lang)))
                    .onMousePressed(button -> {
                        if (button != 0) return false;
                        channel.enabled = !channel.enabled;
                        EquipmentSoundConfig.save();
                        return true;
                    }),
                new StepSlider().value(() -> channel.volume, value -> channel.volume = (float) value)
                    .range(0, 1, STEPS)
                    .milestones(MILESTONE_EVERY)
                    .format(value -> Math.round(value * 100) + "%")
                    .activeWhen(() -> channel.enabled && audible.getAsBoolean())
                    .tagWidth(TAG_WIDTH)

                    .onRelease(EquipmentSoundConfig::save)));
    }

    @Override
    public void onResize(int width, int height) {
        arrangement.fit(width, height);
        super.onResize(width, height);
    }

    @Override
    public void onClose() {
        EquipmentSoundConfig.save();
    }

    private static class Arrangement {

        private final LayoutTheme style;
        private final int labelWidth, cardWidth;
        private final int[] widths, heights;
        private final String hint;
        private boolean stacked;
        private int hintLines = 1;

        private Arrangement(LayoutTheme style) {
            this.style = style;
            EquipmentSound.Group[] groups = EquipmentSound.Group.values();
            int widest = Layout.textWidth(EquipmentGui.tr("gtnl.gui.config.sound.all"), style);
            for (EquipmentSound.Group group : groups) for (EquipmentSound sound : group.sounds())
                widest = Math.max(widest, Layout.textWidth(EquipmentGui.tr(sound.lang()), style));
            labelWidth = Size.content(SlideSwitch.widthFor(widest))
                .atLeast(LABEL_WIDTH)
                .resolve(0);
            cardWidth = style.spacingMedium * 3 + labelWidth + SLIDER_WIDTH;
            widths = new int[groups.length];
            heights = new int[groups.length];
            for (int i = 0; i < groups.length; i++) {
                widths[i] = cardWidth;
                heights[i] = Layout.cardHeader(style) + (groups[i].sounds()
                    .size() + 1) * style.rowHeight + style.spacingMedium;
            }
            hint = EquipmentGui.tr(HINT);
            fit(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        private void fit(int screenWidth, int screenHeight) {
            int pad = style.spacingLarge, columnLines = Layout.textLines(hint, columnWidth() - pad * 2, style);
            stacked = LayoutMath
                .stacks(widths, heights, style.spacingLarge, screenWidth - pad * 2, screenHeight - chrome(columnLines));
            hintLines = stacked ? columnLines : Layout.textLines(hint, rowWidth() - pad * 2, style);
        }

        private int chrome(int lines) {
            return style.spacingLarge + TITLE_HEIGHT
                + style.spacingMedium * 2
                + style.spacingSmall
                + lines * TEXT_HEIGHT;
        }

        private int rowWidth() {
            return style.spacingLarge * 2 + LayoutMath.total(widths, style.spacingLarge);
        }

        private int columnWidth() {
            return style.spacingLarge * 2 + LayoutMath.widest(widths);
        }

        private int cardsHeight() {
            return stacked ? LayoutMath.total(heights, style.spacingLarge) : LayoutMath.widest(heights);
        }

        private int width() {
            return stacked ? columnWidth() : rowWidth();
        }

        private int height() {
            return chrome(hintLines) + cardsHeight();
        }

        private int x(int card) {
            int x = style.spacingLarge;
            if (!stacked) for (int i = 0; i < card; i++) x += widths[i] + style.spacingLarge;
            return x;
        }

        private int y(int card) {
            int y = style.spacingLarge + TITLE_HEIGHT;
            if (stacked) for (int i = 0; i < card; i++) y += heights[i] + style.spacingLarge;
            return y;
        }

        private int cardHeight(int card) {
            return stacked ? heights[card] : LayoutMath.widest(heights);
        }

        private int hintTop() {
            return style.spacingLarge + TITLE_HEIGHT + cardsHeight() + style.spacingMedium;
        }

        private int hintLines() {
            return hintLines;
        }
    }
}
