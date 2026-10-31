package com.science.gtnl.common.gui.portalgun;

import static com.gtnllib.gui.widget.StepSlider.decimals;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleFunction;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.widget.sizer.Unit;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.gtnllib.config.GTNLLibConfig;
import com.gtnllib.gui.layout.Layout;
import com.gtnllib.gui.settings.SettingControls;
import com.gtnllib.gui.settings.SettingControls.Control;
import com.gtnllib.gui.widget.CloseButton;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.StepSlider;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.config.ClientConfig;
import com.science.gtnl.config.ConfigRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class PortalGunLookScreen extends ModularScreen {

    private static final String LANG = "item.gtnl.portal_gun.look.";
    private static final int WIDTH = 280, TEXT_HEIGHT = 10, SLIDER_WIDTH = 96, SCREEN_MARGIN = 16;
    private static final DoubleFunction<String> WHOLE = StepSlider.whole();

    private record Section(String title, List<Control> controls) {}

    private static int screenHeight = Integer.MAX_VALUE;

    private PortalGunLookScreen() {
        super(ScienceNotLeisure.MODID, panel());
        useTheme(EquipmentTheme.selected());
    }

    public static void open() {
        ClientGUI.open(new PortalGunLookScreen());
    }

    private static Control slider(String name, double step, DoubleFunction<String> format) {
        return SettingControls.slider(null, ClientConfig.class, name, step, format);
    }

    private static Control toggle(String name) {
        return SettingControls.toggle(null, ClientConfig.class, name);
    }

    private static List<Section> sections() {
        return Arrays.asList(
            new Section(
                "trail",
                Arrays.asList(
                    slider("portalGunTrailWidth", 0.005, decimals(3)),
                    slider("portalGunTrailSpray", 0.0025, decimals(4)),
                    slider("portalGunTrailGravity", 0.001, decimals(3)),
                    slider("portalGunTrailDotSize", 0.002, decimals(3)),
                    slider("portalGunTrailCount", 1, WHOLE),
                    slider("portalGunTrailDensity", 1, WHOLE),
                    slider("portalGunTrailFallDelay", 0.25, decimals(2)),
                    slider("portalGunTrailCutRange", 0.25, decimals(2)),
                    slider("portalGunTrailCutTicks", 1, WHOLE))),
            new Section(
                "effects",
                Arrays.asList(
                    toggle("enablePortalGunPuffs"),
                    toggle("enablePortalGunRings"),
                    toggle("enablePortalGunSparks"),
                    slider("portalGunSparkCount", 4, WHOLE))),
            new Section(
                "open_dots",
                Arrays.asList(
                    slider("portalGunOpenSpread", 0.005, decimals(3)),
                    slider("portalGunOpenSpray", 0.0025, decimals(4)),
                    slider("portalGunOpenJet", 0.01, decimals(2)),
                    slider("portalGunOpenDrag", 0.01, decimals(2)),
                    slider("portalGunOpenGravity", 0.001, decimals(3)),
                    slider("portalGunOpenDotSize", 0.001, decimals(3)),
                    slider("portalGunOpenHover", 0.5, decimals(1)),
                    slider("portalGunOpenCount", 1, WHOLE),
                    slider("portalGunOpenDensity", 1, WHOLE),
                    toggle("enablePortalGunOpenWholeFace"))),
            new Section(
                "arcs",
                Arrays.asList(
                    slider("portalGunOpenArcRate", 0.5, decimals(1)),
                    slider("portalGunOpenArcTicks", 0.5, decimals(1)),
                    slider("portalGunArcForkChance", 0.05, decimals(2)),
                    slider("portalGunOpenArcBend", 0.05, decimals(2)),
                    slider("portalGunOpenArcRise", 0.05, decimals(2)),
                    slider("portalGunArcHop", 0.5, decimals(1)),
                    slider("portalGunArcWidth", 0.001, decimals(3)),
                    slider("portalGunRingArcRate", 0.5, decimals(1)),
                    slider("portalGunCenterArcRate", 0.5, decimals(1)))),
            new Section(
                "pass",
                Arrays.asList(
                    slider("portalGunPassJelly", 0.01, decimals(2)),
                    slider("portalGunPassDotCount", 1, WHOLE),
                    slider("portalGunClosePop", 0.05, decimals(2)),
                    slider("portalGunCloseTicks", 1, WHOLE))),
            new Section(
                "held",
                Arrays.asList(
                    slider("portalGunHeldPitch", 1, WHOLE),
                    slider("portalGunHeldYaw", 1, WHOLE),
                    slider("portalGunHeldForward", 0.01, decimals(2)))),
            new Section(
                "others",
                Arrays.asList(
                    toggle("enablePortalGunShowOthersFx"),
                    SettingControls.slider(null, GTNLLibConfig.class, "maxWorldMotes", 128, WHOLE))));
    }

    private static List<Control> controls(List<Section> sections) {
        List<Control> controls = new ObjectArrayList<>();
        for (Section section : sections) controls.addAll(section.controls());
        return controls;
    }

    private static ModularPanel panel() {
        LayoutTheme style = EquipmentTheme.layout();
        int pad = style.padding, inner = WIDTH - pad * 2;
        int color = EquipmentTheme.colors().accent;
        Layout rows = Layout.rows(style, inner)
            .inset(0);
        List<Section> sections = sections();
        int tagWidth = SettingControls.tagWidth(controls(sections));
        List<IWidget> lines = new ObjectArrayList<>();
        for (Section section : sections) {
            lines.add(rows.section(IKey.lang(LANG + section.title()), color));
            for (Control control : section.controls())
                lines.add(control.widget(rows, style, SLIDER_WIDTH, tagWidth, PortalGunLookScreen::save));
        }
        int content = lines.size() * style.rowHeight, listTop = pad + TEXT_HEIGHT + style.spacingLarge;
        int footer = style.spacingMedium + TEXT_HEIGHT + style.spacingMedium + style.buttonHeight + pad;
        ModularPanel panel = new ModularPanel("portal_gun_look").width(WIDTH)
            .height(() -> Math.min(listTop + content + footer, screenHeight - SCREEN_MARGIN), Unit.Measure.PIXEL);
        panel.child(
            Layout.label(IKey.lang(LANG + "title"), style)
                .pos(pad, pad)
                .size(inner - CloseButton.SIZE - style.spacingMedium, TEXT_HEIGHT));
        panel.child(new CloseButton().corner(pad, style));
        ListWidget<IWidget, ?> list = new ListWidget<>().pos(pad, listTop)
            .width(inner)
            .height(() -> Math.min(content, screenHeight - SCREEN_MARGIN - listTop - footer), Unit.Measure.PIXEL);
        lines.forEach(list::child);
        panel.child(list);
        panel.child(
            IKey.lang(LANG + "hint")
                .color(EquipmentTheme.colors().hint)
                .shadow(false)
                .asWidget()
                .left(pad)
                .bottom(pad + style.buttonHeight + style.spacingMedium)
                .size(inner, TEXT_HEIGHT));
        panel.child(Layout.button(IKey.lang(LANG + "reset"), inner, style, () -> {
            restoreDefaults();
            save();
        })
            .left(pad)
            .bottom(pad));
        return panel;
    }

    private static void restoreDefaults() {
        for (Control control : controls(sections())) control.reset();
    }

    private static void save() {
        ConfigRegistry.save(ClientConfig.class, GTNLLibConfig.class);
    }

    @Override
    public void onResize(int width, int height) {
        screenHeight = height;
        super.onResize(width, height);
    }

    @Override
    public void onClose() {
        save();
    }
}
