package com.gtnllib.client.hud;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.sizer.Unit;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.gtnllib.client.hud.HudElement.Choice;
import com.gtnllib.client.hud.HudElement.Setting;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.layout.Size;
import com.gtnllib.gui.theme.ButtonPurpose;
import com.gtnllib.gui.widget.CloseButton;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.SlideSwitch;
import com.gtnllib.gui.widget.StepSlider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class HudSettingsCard extends ParentWidget<HudSettingsCard> {

    private static final String LANG = "gtnllib.hud.editor.";
    private static final int WIDTH = 172, SLIDER_WIDTH = 84, TEXT_HEIGHT = 10, MAX_COLUMNS = 3, SCALE_STEPS = 25,
        OPACITY_STEPS = 18, IDLE_STEPS = 30, ALPHA_STEPS = 20, MILESTONES = 5;

    private final HudCanvas canvas;
    private final LayoutTheme style;
    private final int top, margin;
    private int content;
    private HudElement built;
    private boolean onLeft;

    public HudSettingsCard(HudCanvas canvas, LayoutTheme style, int top, int margin) {
        this.canvas = canvas;
        this.style = style;
        this.top = top;
        this.margin = margin;
        widgetTheme(IThemeApi.PANEL);
        width(WIDTH);
        height(this::cardHeight, Unit.Measure.PIXEL);
        left(this::cardLeft, Unit.Measure.PIXEL);
        top(top);
        setEnabledIf(card -> built != null && built == canvas.selected());
    }

    private double cardLeft() {
        return onLeft ? margin
            : getParent().getArea()
                .w() - WIDTH - margin;
    }

    private double cardHeight() {
        int available = getParent().getArea()
            .h() - top - margin;
        return Math.max(Layout.cardHeader(style), Math.min(Layout.cardHeader(style) + content, available));
    }

    public void refresh() {
        removeAll();
        HudElement element = canvas.selected();
        built = element;
        if (element != null) {
            onLeft = !canvas.onLeftHalf(element);
            build(element);
        }
        scheduleResize();
    }

    private void build(HudElement element) {
        int header = Layout.cardHeader(style);
        child(
            Layout.label(IKey.lang(element.lang()), style)
                .pos(style.padding, style.spacingMedium)
                .size(WIDTH - style.padding * 2 - CloseButton.SIZE - style.spacingSmall, TEXT_HEIGHT));
        child(
            new CloseButton().action(() -> canvas.select(null))
                .top(style.spacingMedium)
                .right(style.spacingMedium));
        Layout rows = Layout.rows(style, WIDTH);
        List<IWidget> lines = new ObjectArrayList<>();
        for (Setting setting : element.settings(HudLayout.timing(element)))
            lines.addAll(setting(rows, element, setting));
        for (HudOption option : element.options()) lines.addAll(option(rows, element, option));
        lines.add(
            rows.line(
                0,
                Collections.singletonList(Size.fill()),
                Collections.singletonList(button(ButtonPurpose.DANGER, LANG + "reset_element").onMousePressed(mouse -> {
                    if (mouse != 0) return false;
                    canvas.resetSelected();
                    return true;
                }))));
        content = lines.size() * style.rowHeight + style.spacingMedium;
        ListWidget<IWidget, ?> list = new ListWidget<>().pos(0, header)
            .width(WIDTH)
            .height(() -> cardHeight() - header, Unit.Measure.PIXEL);
        lines.forEach(list::child);
        child(list);
    }

    private List<IWidget> setting(Layout rows, HudElement element, Setting setting) {
        String lang = setting.lang();
        return switch (setting) {
            case SHOWN -> Collections.singletonList(
                toggle(
                    rows,
                    lang,
                    () -> !HudLayout.hidden(element),
                    () -> canvas.change(null, () -> HudLayout.toggle(element))));
            case SCALE -> Collections.singletonList(
                slider(
                    rows,
                    lang,
                    () -> HudLayout.scale(element),
                    value -> canvas.change(group(setting, element), () -> HudLayout.setScale(element, (float) value)),
                    HudLayout.MIN_SCALE,
                    HudLayout.MAX_SCALE,
                    SCALE_STEPS,
                    value -> String.format(Locale.ROOT, "×%.1f", value)));
            case OPACITY -> Collections.singletonList(
                slider(
                    rows,
                    lang,
                    () -> HudLayout.opacity(element),
                    value -> canvas.change(group(setting, element), () -> HudLayout.setOpacity(element, (float) value)),
                    HudLayout.MIN_OPACITY,
                    1,
                    OPACITY_STEPS,
                    HudSettingsCard::percent));
            case COLOR -> {
                List<Pick> picks = new ObjectArrayList<>();
                for (HudColor color : HudColor.values()) picks.add(
                    new Pick(
                        color.lang(),
                        () -> HudLayout.color(element) == color,
                        () -> canvas.change(null, () -> HudLayout.setColor(element, color))));
                yield picks(rows, lang, picks);
            }
            case TIMING -> {
                List<Pick> picks = new ObjectArrayList<>();
                for (HudTiming timing : HudTiming.values())
                    picks.add(new Pick(timing.lang(), () -> HudLayout.timing(element) == timing, () -> {
                        canvas.change(null, () -> HudLayout.setTiming(element, timing));
                        canvas.rebuild();
                    }));
                yield picks(rows, lang, picks);
            }
            case IDLE_SECONDS -> Collections.singletonList(
                slider(
                    rows,
                    lang,
                    () -> HudLayout.idleSeconds(element),
                    value -> canvas
                        .change(group(setting, element), () -> HudLayout.setIdleSeconds(element, (float) value)),
                    0,
                    HudLayout.MAX_IDLE_SECONDS,
                    IDLE_STEPS,
                    value -> Math.round(value) + "s"));
            case IDLE_ALPHA -> Collections.singletonList(
                slider(
                    rows,
                    lang,
                    () -> HudLayout.idleAlpha(element),
                    value -> canvas
                        .change(group(setting, element), () -> HudLayout.setIdleAlpha(element, (float) value)),
                    0,
                    1,
                    ALPHA_STEPS,
                    HudSettingsCard::percent));
            case STYLE -> {
                List<Pick> picks = new ObjectArrayList<>();
                for (Choice style : element.styles()) picks.add(
                    new Pick(
                        style.lang(),
                        () -> style.id()
                            .equals(HudLayout.style(element)),
                        () -> canvas.change(null, () -> HudLayout.setStyle(element, style.id()))));
                yield picks(rows, lang, picks);
            }
        };
    }

    private List<IWidget> option(Layout rows, HudElement element, HudOption option) {
        return switch (option.kind) {
            case TOGGLE -> Collections.singletonList(
                toggle(
                    rows,
                    option.lang,
                    () -> HudLayout.on(element, option.id),
                    () -> canvas
                        .change(null, () -> HudLayout.setOn(element, option.id, !HudLayout.on(element, option.id)))));
            case NUMBER -> Collections.singletonList(
                slider(
                    rows,
                    option.lang,
                    () -> HudLayout.number(element, option.id),
                    value -> canvas.change(
                        "option:" + option.id + ":" + element.id,
                        () -> HudLayout.setNumber(element, option.id, value)),
                    option.min,
                    option.max,
                    option.steps,
                    option::format));
            case CHOICE -> {
                List<Pick> picks = new ObjectArrayList<>();
                for (Choice choice : option.choices()) picks.add(
                    new Pick(
                        choice.lang(),
                        () -> choice.id()
                            .equals(HudLayout.choice(element, option.id)),
                        () -> canvas.change(null, () -> HudLayout.setChoice(element, option.id, choice.id()))));
                yield picks(rows, option.lang, picks);
            }
        };
    }

    private static String group(Setting setting, HudElement element) {
        return HudLayout.id(setting) + ":" + element.id;
    }

    private static String percent(double value) {
        return Math.round(value * 100) + "%";
    }

    private IWidget toggle(Layout rows, String lang, BooleanSupplier on, Runnable flip) {
        return rows.line(
            0,
            Collections.singletonList(Size.fill()),
            Collections.singletonList(
                new SlideSwitch().value(on)
                    .label(IKey.lang(lang))
                    .onMousePressed(mouse -> {
                        if (mouse != 0) return false;
                        flip.run();
                        return true;
                    })));
    }

    private IWidget slider(Layout rows, String lang, DoubleSupplier getter, DoubleConsumer setter, double min,
        double max, int steps, DoubleFunction<String> format) {
        return rows.line(
            style.spacingMedium,
            Arrays.asList(Size.fill(), Size.fixed(SLIDER_WIDTH)),
            Arrays.asList(
                Layout.label(IKey.lang(lang), style),
                new StepSlider().value(getter, setter)
                    .range(min, max, steps)
                    .milestones(MILESTONES)
                    .format(format)

                    .onRelease(canvas::seal)));
    }

    private List<IWidget> picks(Layout rows, String lang, List<Pick> picks) {
        List<IWidget> lines = new ObjectArrayList<>();
        lines.add(
            rows.line(
                0,
                Collections.singletonList(Size.fill()),
                Collections.singletonList(Layout.label(IKey.lang(lang), style))));
        List<ToggleButton> buttons = new ObjectArrayList<>();
        int widest = 0;
        for (Pick pick : picks) {
            widest = Math.max(
                widest,
                Size.content(Layout.textWidth(StatCollector.translateToLocal(pick.lang), style), style.padding)
                    .resolve(0));
            buttons.add(
                ButtonPurpose.TAB.apply(new ToggleButton())
                    .value(new BoolValue.Dynamic(pick.selected, on -> { if (on) pick.select.run(); }))
                    .overlay(Layout.caption(IKey.lang(pick.lang), style)));
        }
        lines.addAll(
            rows.grid(
                style.spacingSmall,
                rows.columns(style.spacingSmall, widest, Math.min(MAX_COLUMNS, picks.size())),
                buttons));
        return lines;
    }

    private ButtonWidget<?> button(ButtonPurpose purpose, String lang) {
        ButtonWidget<?> button = new ButtonWidget<>();
        return purpose.apply(button)
            .overlay(Layout.caption(IKey.lang(lang), style));
    }

    private record Pick(String lang, BooleanSupplier selected, Runnable select) {}
}
