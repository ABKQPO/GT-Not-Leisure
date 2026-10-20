package com.science.gtnl.client.celestweave;

import static com.science.gtnl.client.celestweave.CelestweaveContractTest.RESOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.check;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.near;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.science.gtnl.client.fx.FxAngle;
import com.science.gtnl.client.fx.FxEasing;
import com.science.gtnl.client.hud.HudEditor;
import com.science.gtnl.client.hud.HudElement;
import com.science.gtnl.client.hud.HudLayout;
import com.science.gtnl.client.hud.HudRegistry;
import com.science.gtnl.client.railgun.RailgunHudRenderer;
import com.science.gtnl.common.gui.modularui.wheel.WheelActionContext;
import com.science.gtnl.common.gui.modularui.wheel.WheelEntry;
import com.science.gtnl.common.gui.modularui.wheel.WheelHighlight;
import com.science.gtnl.common.gui.modularui.wheel.WheelMenuBuilder;
import com.science.gtnl.common.gui.modularui.wheel.WheelMenuModel;
import com.science.gtnl.common.gui.modularui.wheel.WheelNavigator;
import com.science.gtnl.common.gui.modularui.wheel.WheelOpenMode;
import com.science.gtnl.common.gui.modularui.wheel.WheelPageModel;
import com.science.gtnl.common.gui.modularui.wheel.WheelStyle;
import com.science.gtnl.common.gui.modularui.wheel.WheelWidget;
import com.science.gtnl.config.MainConfig;

public class WheelHudContracts {

    private WheelHudContracts() {}

    static void run() throws Exception {
        hud();
        hudLayout();
        hudInUse();
        wheel();
    }

    private static void hud() {
        near(1, CelestweaveHud.fade(-100), "hud awake before idle");
        near(1, CelestweaveHud.fade(0), "hud awake at idle edge");
        near(0.5, CelestweaveHud.fade(250), "hud fades halfway");
        near(0, CelestweaveHud.fade(10000), "hud fully faded");
        near(0, FxEasing.easeOutCubic(-1), "hud slide clamps low");
        near(1, FxEasing.easeOutCubic(2), "hud slide clamps high");
        check(FxEasing.easeOutCubic(0.5F) > 0.5F, "hud slide eases out");
        near(1, CelestweaveHud.segmentWeight(0, 0), "front hit lights front segment");
        near(0, CelestweaveHud.segmentWeight(0, 3), "front hit leaves back segment dark");
        near(1, CelestweaveHud.segmentWeight(Math.PI / 3, 1), "right hit lights right segment");
        near(1, CelestweaveHud.segmentWeight(-Math.PI / 3, 5), "left hit lights left segment");
        near(0.75, CelestweaveHud.segmentWeight(Math.PI / 2, 1), "side hit lights the front side segment");
        near(0.75, CelestweaveHud.segmentWeight(Math.PI / 2, 2), "side hit lights the back side segment");
        float[] core = CelestweaveHud.polygon(8, 0), ring = CelestweaveHud.polygon(8, Math.PI / 6);
        near(0, core[0], "core hexagon top vertex x");
        near(-8, core[1], "core hexagon top vertex y");
        near(8, ring[2], "ring hexagon right vertex x");
        near(0, ring[3], "ring hexagon right vertex y");
        check(CelestweaveHud.clipBelow(core, 9).length == 0, "empty core has no fill");
        check(Arrays.equals(CelestweaveHud.clipBelow(core, -9), core), "full core keeps the hexagon");
        float[] half = CelestweaveHud.clipBelow(core, 0);
        check(half.length >= 6, "half core is a polygon");
        for (int i = 1; i < half.length; i += 2) check(half[i] >= -1.0E-5F, "half core stays below the level");
        float[] level = CelestweaveHud.span(half, 0);
        check(level != null && Math.abs(level[0] + level[1]) < 1.0E-4F, "fill cap is centred");
        check(level != null && Math.abs(level[1] - 8 * Math.sqrt(3) / 2) < 1.0E-3F, "fill cap reaches the sides");
        check(CelestweaveHud.span(core, 3) == null, "no fill cap off the edge");
        near(50, CelestweaveHud.drainRate(1000, 0, 1000), "drain over one second");
        near(-50, CelestweaveHud.drainRate(0, 1000, 1000), "charge reads negative");
        near(0, CelestweaveHud.drainRate(1000, 0, 0), "no elapsed time has no rate");
        check("0:42".equals(CelestweaveHud.duration(42)), "short duration");
        check("1:00:05".equals(CelestweaveHud.duration(3605)), "hour duration");
        check(
            StatCollector.translateToLocal("item.gtnl.celestweave.hud.duration_capped")
                .equals(CelestweaveHud.duration(400000)),
            "capped duration");
        check(
            CelestweaveHud.duration(Double.POSITIVE_INFINITY)
                .isEmpty(),
            "no duration when not draining");
    }

    private static void hudLayout() throws ReflectiveOperationException {
        List<String> layoutFiles = new ArrayList<>();
        for (Field field : HudLayout.class.getDeclaredFields())
            if (field.getType() == String.class && Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                layoutFiles.add((String) field.get(null));
            }
        check(
            layoutFiles.equals(List.of("config/GTNotLeisure/hud.properties")),
            "HUD layout reads and writes only hud.properties");
        HudElement coreElement = CelestweaveHudElement.CORE.hud;
        HudLayout.Entry fresh = new HudLayout.Entry();
        var core = HudLayout.placement(coreElement, fresh, 480, 270, 1F);
        near(240 - 97 - 8, core.x(), "core keeps its place left of the hotbar");
        near(270 - 3 - 8, core.y(), "core keeps its place above the bottom edge");
        near(
            480 - 97 - 16,
            HudLayout.placement(coreElement, fresh, 960, 540, 2F)
                .x(),
            "core default follows the base scale");
        near(
            -coreElement.minX * 2,
            HudLayout.placement(coreElement, fresh, 480, 270, 2F)
                .x(),
            "oversized core readout kept on screen");
        var ring = HudLayout.placement(CelestweaveHudElement.SHIELD_RING.hud, fresh, 480, 270, 1F);
        near(240, ring.x(), "shield ring centred horizontally");
        near(135, ring.y(), "shield ring centred vertically");
        near(
            240 + 62,
            HudLayout.placement(CelestweaveHudElement.ALTIMETER.hud, fresh, 480, 270, 1F)
                .x(),
            "altimeter right of the crosshair");
        boolean[] reserved = { true };
        HudRegistry.reserveCrosshair(() -> reserved[0]);
        near(
            240 + 26 + 6.5,
            HudLayout.placement(RailgunHudRenderer.CHARGE, fresh, 480, 270, 1F)
                .x(),
            "lance charge clears the armor HUD");
        reserved[0] = false;
        near(
            240 + 10 + 6.5,
            HudLayout.placement(RailgunHudRenderer.CHARGE, fresh, 480, 270, 1F)
                .x(),
            "lance charge hugs the crosshair without armor");

        near(0, HudLayout.anchor(10, 300), "left third anchors left");
        near(0.5, HudLayout.anchor(150, 300), "middle third anchors centre");
        near(1, HudLayout.anchor(290, 300), "right third anchors right");

        HudLayout.Entry entry = new HudLayout.Entry();
        HudLayout.move(entry, 460, 250, 480, 270);
        var moved = HudLayout.placement(coreElement, entry, 480, 270, 1F);
        near(460, moved.x(), "dragged x round trip");
        near(250, moved.y(), "dragged y round trip");
        var wider = HudLayout.placement(coreElement, entry, 960, 540, 1F);
        near(940, wider.x(), "corner element keeps its distance to the right edge");
        near(520, wider.y(), "corner element keeps its distance to the bottom edge");
        HudLayout.move(entry, 5000, -5000, 480, 270);
        var clamped = HudLayout.placement(coreElement, entry, 480, 270, 1F);
        near(480 - coreElement.maxX, clamped.x(), "element kept inside the right edge");
        near(-coreElement.minY, clamped.y(), "element kept inside the top edge");

        near(HudLayout.MIN_SCALE, HudLayout.clampScale(0.1F), "scale floor");
        near(HudLayout.MAX_SCALE, HudLayout.clampScale(9F), "scale ceiling");
        near(1, HudLayout.clampScale(Float.NaN), "scale NaN falls back");

        HudElement altimeterElement = CelestweaveHudElement.ALTIMETER.hud;
        HudLayout.reset();
        HudLayout.move(altimeterElement, 100, 50, 480, 270);
        HudLayout.rescale(altimeterElement, 3);
        HudLayout.toggle(CelestweaveHudElement.DASH_ARC.hud);
        Properties saved = new Properties();
        HudLayout.write(saved);
        saved.setProperty("future_element.scale", "2.0");
        HudLayout.read(saved);
        var altimeter = HudLayout.entry(altimeterElement);
        check(altimeter.moved, "moved flag survives saving");
        near(1.3, altimeter.scale, "element scale survives saving");
        near(
            100,
            HudLayout.placement(altimeterElement, altimeter, 480, 270, 1F)
                .x(),
            "position survives saving");
        check(HudLayout.hidden(CelestweaveHudElement.DASH_ARC.hud), "hidden flag survives saving");
        check(!HudLayout.hidden(coreElement), "untouched element stays visible");
        Properties resaved = new Properties();
        HudLayout.write(resaved);
        check(
            "2.0".equals(resaved.getProperty("future_element.scale")),
            "layout of an element that is not registered yet survives saving");

        Properties broken = new Properties();
        broken.setProperty("core.moved", "true");
        broken.setProperty("core.x", "abc");
        broken.setProperty("core.y", "5");
        broken.setProperty("core.anchorX", "0.3");
        broken.setProperty("core.scale", "99");
        HudLayout.read(broken);
        var repaired = HudLayout.entry(coreElement);
        check(!repaired.moved, "unreadable position falls back to the default place");
        near(0.5, repaired.anchorX, "unknown anchor falls back to centre");
        near(HudLayout.MAX_SCALE, repaired.scale, "oversized scale clamped");
        HudLayout.reset();

        check("railgun_charge".equals(RailgunHudRenderer.CHARGE.id), "lance charge keeps its saved id");
        for (CelestweaveHudElement element : CelestweaveHudElement.values()) check(
            element.hud.id.equals(
                element.name()
                    .toLowerCase(Locale.ROOT)),
            "armor HUD element keeps its saved id " + element);
        check(
            "item.gtnl.celestweave.hud.element.core".equals(CelestweaveHudElement.CORE.hud.lang()),
            "armor HUD element keeps its lang key");
        check(
            "item.gtnl.railgun.hud.element.railgun_charge".equals(RailgunHudRenderer.CHARGE.lang()),
            "lance charge uses the railgun lang prefix");
        check(RailgunHudRenderer.CHARGE.hasRenderer(), "lance charge is drawn by the shared HUD overlay");
        check(!CelestweaveHudElement.CORE.hud.hasRenderer(), "armor HUD draws itself");

        HudElement plain = HudElement.builder("contract_plain")
            .bounds(-4, -4, 4, 4)
            .build();
        check((HudElement.DEFAULT_LANG_PREFIX + "contract_plain").equals(plain.lang()), "default HUD lang key");
        near(0.5, plain.anchorX, "default HUD anchor x");
        near(0.5, plain.anchorY, "default HUD anchor y");
        check(plain.isVisible(), "HUD element visible by default");
        near(1, plain.baseScale(), "HUD base scale defaults to 1");
        check(!plain.hasRenderer(), "HUD element without renderer draws itself");
        near(
            240,
            HudLayout.placement(plain, new HudLayout.Entry(), 480, 270, plain.baseScale())
                .x(),
            "default HUD element sits at the screen centre");
        check(
            rejects(
                () -> HudElement.builder("contract_flat")
                    .build()),
            "HUD element without bounds rejected");
        check(rejects(() -> HudElement.builder("")), "empty HUD id rejected");

        boolean[] shown = { false };
        double[] scale = { Double.NaN };
        HudElement toggled = HudElement.builder("contract_toggled")
            .bounds(-4, -4, 4, 4)
            .visibleWhen(() -> shown[0])
            .baseScale(() -> scale[0])
            .render((place, partialTicks) -> {})
            .build();
        HudLayout.reset();
        check(HudLayout.place(toggled, 480, 270) == null, "hidden by its own switch");
        shown[0] = true;
        near(
            1,
            HudLayout.place(toggled, 480, 270)
                .scale(),
            "invalid base scale falls back to 1");
        scale[0] = 2;
        near(
            2,
            HudLayout.place(toggled, 480, 270)
                .scale(),
            "base scale comes from the element");
        HudLayout.toggle(toggled);
        check(HudLayout.place(toggled, 480, 270) == null, "editor hide still applies");
        HudLayout.reset();

        HudRegistry.register(toggled);
        check(HudRegistry.byId("contract_toggled") == toggled, "registered HUD element found by id");
        check(rejects(() -> HudRegistry.register(toggled)), "duplicate HUD id rejected");
    }

    private static void hudInUse() {
        boolean[] used = { false }, shown = { false };
        HudElement equipped = HudElement.builder("contract_equipped")
            .bounds(-4, -4, 4, 4)
            .inUseWhen(() -> used[0])
            .visibleWhen(() -> shown[0])
            .baseScale(() -> 1.5)
            .render((place, partialTicks) -> {})
            .build();
        HudElement always = HudElement.builder("contract_always")
            .bounds(-4, -4, 4, 4)
            .build();
        check(always.isInUse(), "HUD element in use by default");
        HudRegistry.register(equipped);
        HudRegistry.register(always);
        HudLayout.reset();
        check(!equipped.isInUse(), "HUD element follows its in-use condition");
        check(
            !HudEditor.candidates(false)
                .contains(equipped),
            "editor leaves out an element that is not in use");
        check(
            HudEditor.candidates(false)
                .contains(always),
            "editor lists an element in use");
        check(
            HudEditor.candidates(true)
                .contains(equipped),
            "show all lists an element that is not in use");
        check(
            HudEditor.candidates(true)
                .size()
                == HudRegistry.elements()
                    .size(),
            "show all lists every registered element");
        shown[0] = true;
        check(HudLayout.place(equipped, 480, 270) == null, "element not in use is not drawn");
        used[0] = true;
        shown[0] = false;
        check(
            HudEditor.candidates(false)
                .contains(equipped),
            "editor lists an element in use with nothing to show");
        check(HudLayout.place(equipped, 480, 270) == null, "element in use with nothing to show is not drawn");
        shown[0] = true;
        check(HudLayout.place(equipped, 480, 270) != null, "element in use with something to show is drawn");

        boolean enabled = MainConfig.client.enableHud;
        float globalScale = MainConfig.client.hudScale;
        try {
            MainConfig.client.hudScale = 2F;
            near(
                3,
                HudLayout.place(equipped, 480, 270)
                    .scale(),
                "global HUD scale multiplies the element base scale");
            near(
                3,
                HudLayout.preview(equipped, 480, 270)
                    .scale(),
                "editor preview uses the global HUD scale");
            HudLayout.rescale(equipped, 5);
            near(
                4.5,
                HudLayout.place(equipped, 480, 270)
                    .scale(),
                "editor scale multiplies on top of the global HUD scale");
            MainConfig.client.hudScale = Float.NaN;
            near(
                2.25,
                HudLayout.place(equipped, 480, 270)
                    .scale(),
                "invalid global HUD scale falls back to 1");
            MainConfig.client.enableHud = false;
            check(!HudLayout.enabled(), "HUD switch comes from the config");
            check(HudLayout.place(equipped, 480, 270) == null, "HUD switch off hides an element in use");
            check(HudLayout.place(always, 480, 270) == null, "HUD switch off hides an element without conditions");
            check(
                HudEditor.candidates(false)
                    .contains(always),
                "editor still lists elements with the HUD switch off");
        } finally {
            MainConfig.client.enableHud = enabled;
            MainConfig.client.hudScale = globalScale;
            HudLayout.reset();
        }
        check(HudLayout.place(always, 480, 270) != null, "HUD switch on draws again");
    }

    private static boolean rejects(Runnable action) {
        try {
            action.run();
            return false;
        } catch (RuntimeException expected) {
            return true;
        }
    }

    private static void wheel() throws Exception {
        double quarter = Math.PI / 2;
        near(0, FxAngle.angleOf(0, -1), "wheel angle up");
        near(quarter, FxAngle.angleOf(1, 0), "wheel angle right");
        near(Math.PI, FxAngle.angleOf(0, 1), "wheel angle down");
        near(3 * quarter, FxAngle.angleOf(-1, 0), "wheel angle left");
        int[] expected = { 0, 1, 2, 3 };
        for (int sector = 0; sector < 4; sector++) {
            float angle = WheelWidget.slotAngle(sector, 4);
            check(WheelWidget.slotAt(angle, 4) == expected[sector], "wheel section centre " + sector);
            near(
                angle,
                FxAngle.angleOf(FxAngle.dirX(angle), FxAngle.dirY(angle)),
                "wheel direction round trip " + sector);
        }
        check(WheelWidget.slotAt(FxAngle.TAU - 0.01F, 4) == 0, "wheel section wraps past top");
        check(WheelWidget.slotAt((float) Math.PI / 4 - 0.01F, 4) == 0, "wheel section before boundary");
        check(WheelWidget.slotAt((float) Math.PI / 4 + 0.01F, 4) == 1, "wheel section after boundary");
        check(WheelWidget.slotAt(1, 0) == -1, "wheel section empty");
        for (float angle = -10; angle < 10; angle += 0.37F) {
            float signed = FxAngle.normalizeSigned(angle);
            check(signed >= -Math.PI - 1.0E-6 && signed <= Math.PI + 1.0E-6, "wheel signed angle range " + angle);
        }

        check(WheelMenuModel.pageCount(0, 8) == 1, "wheel empty menu has one page");
        check(WheelMenuModel.pageCount(8, 8) == 1, "wheel full page");
        check(WheelMenuModel.pageCount(9, 8) == 2, "wheel overflow page");
        WheelMenuModel model = WheelMenuBuilder.create()
            .slotsPerPage(4)
            .action("a", "A", context -> {})
            .display("b", "B", IDrawable.EMPTY)
            .submenu("c", "C", submenu -> submenu.action("c1", "C1", context -> {}))
            .action("d", "D", context -> {})
            .action("e", "E", context -> {})
            .build();
        check(model.pageCount(model.rootEntries()) == 2, "wheel model pages");
        WheelPageModel second = model.page(model.rootEntries(), 1);
        check(
            second.slots()
                .size() == 4,
            "wheel page padded to slots per page");
        check(
            second.slot(0)
                .id()
                .equals("e"),
            "wheel second page starts after first");
        check(
            second.slot(1)
                .isPlaceholder() && !second.isSelectable(1, WheelOpenMode.TAP),
            "wheel placeholder not selectable");
        WheelPageModel first = model.page(model.rootEntries(), 0);
        check(first.isSelectable(0, WheelOpenMode.HOLD), "wheel action selectable in hold");
        check(!first.isSelectable(1, WheelOpenMode.TAP), "wheel display entry not selectable");
        check(first.isSelectable(2, WheelOpenMode.TAP), "wheel submenu selectable in tap");
        check(!first.isSelectable(2, WheelOpenMode.HOLD), "wheel submenu not selectable in hold");
        WheelEntry submenu = first.slot(2);
        check(
            submenu.hasSubmenu() && submenu.submenu()
                .get(0)
                .id()
                .equals("c1"),
            "wheel submenu entries");

        boolean[] enabled = { false };
        String[] text = { "before" };
        WheelEntry dynamic = WheelEntry.action("dyn", "fixed", context -> {})
            .enabledWhen(() -> enabled[0])
            .labelFrom(() -> text[0]);
        check(!dynamic.isSelectable(WheelOpenMode.TAP), "wheel entry disabled until condition holds");
        enabled[0] = true;
        text[0] = "after";
        check(dynamic.isSelectable(WheelOpenMode.TAP), "wheel entry follows enabled condition");
        check(
            dynamic.label()
                .equals("after"),
            "wheel entry follows dynamic label");
        check(
            WheelEntry.action("plain", "plain", context -> {})
                .labelFrom(() -> null)
                .label()
                .isEmpty(),
            "wheel entry null label becomes empty");

        boolean[] shown = { false };
        WheelEntry hidden = WheelEntry.action("hidden", "hidden", context -> {})
            .visibleWhen(() -> shown[0]);
        check(!hidden.isVisible() && !hidden.isSelectable(WheelOpenMode.TAP), "wheel hidden entry not selectable");
        shown[0] = true;
        check(hidden.isVisible() && hidden.isSelectable(WheelOpenMode.TAP), "wheel entry follows visible condition");
        WheelMenuModel single = WheelMenuBuilder.create()
            .slotsPerPage(4)
            .action("x", "X", context -> {})
            .action("y", "Y", context -> {})
            .action("z", "Z", context -> {})
            .build();
        WheelPageModel fitted = single.page(single.rootEntries(), 0);
        check(fitted.slotCount() == 3, "wheel single page fits entry count " + fitted.slotCount());
        check(
            fitted.slots()
                .stream()
                .noneMatch(WheelEntry::isPlaceholder),
            "wheel single page has no placeholders");
        check(
            single.page(List.of(), 0)
                .slotCount() == 0,
            "wheel empty menu has no sections");
        check(second.slotCount() == 4, "wheel paged menu keeps full pages");

        List<WheelActionContext> triggered = new ArrayList<>();
        boolean[] extraShown = { true };
        WheelMenuModel navigable = WheelMenuBuilder.create()
            .slotsPerPage(4)
            .action("a", "A", triggered::add)
            .display("b", "B", IDrawable.EMPTY)
            .submenu(
                "c",
                "C",
                sub -> sub.action("c1", "C1", triggered::add)
                    .action("c2", "C2", triggered::add))
            .action("d", "D", triggered::add)
            .entry(
                WheelEntry.action("e", "E", triggered::add)
                    .visibleWhen(() -> extraShown[0]))
            .build();
        WheelNavigator tap = new WheelNavigator(navigable, WheelOpenMode.TAP);
        check(tap.pageCount() == 2 && tap.pageIndex() == 0, "wheel navigator starts on first of two pages");
        check(tap.turnPage(1) && tap.pageIndex() == 1, "wheel navigator turns forward");
        check(tap.turnPage(1) && tap.pageIndex() == 0, "wheel navigator wraps forward");
        check(tap.turnPage(-1) && tap.pageIndex() == 1, "wheel navigator wraps backward");
        extraShown[0] = false;
        check(tap.refresh(), "wheel navigator notices hidden entry");
        check(tap.pageCount() == 1 && tap.pageIndex() == 0, "wheel navigator clamps page after shrinking");
        check(tap.slotCount() == 4, "wheel navigator single page after shrinking");
        check(!tap.refresh(), "wheel navigator refresh is stable");
        check(tap.confirm(1) == WheelNavigator.Outcome.NONE, "wheel navigator ignores display entry");
        check(tap.confirm(-1) == WheelNavigator.Outcome.NONE, "wheel navigator ignores no selection");
        check(tap.confirm(0) == WheelNavigator.Outcome.ACTION, "wheel navigator runs action");
        check(
            triggered.size() == 1 && triggered.get(0)
                .entryId()
                .equals("a")
                && triggered.get(0)
                    .slotIndex() == 0
                && triggered.get(0)
                    .openMode() == WheelOpenMode.TAP,
            "wheel navigator action context");
        check(tap.confirm(2) == WheelNavigator.Outcome.SUBMENU, "wheel navigator enters submenu");
        check(tap.depth() == 2 && tap.slotCount() == 2, "wheel navigator submenu sections");
        check(
            tap.confirm(1) == WheelNavigator.Outcome.ACTION && triggered.get(1)
                .entryId()
                .equals("c2"),
            "wheel navigator submenu action");
        check(tap.back() && tap.depth() == 1, "wheel navigator returns from submenu");
        check(!tap.back(), "wheel navigator cannot leave root");
        WheelNavigator hold = new WheelNavigator(navigable, WheelOpenMode.HOLD);
        check(hold.confirm(2) == WheelNavigator.Outcome.NONE && hold.depth() == 1, "wheel hold mode skips submenu");
        check(!hold.turnPage(1), "wheel navigator single page does not turn");

        WheelStyle style = WheelStyle.DEFAULT;
        near(0.17F, style.innerRadiusScale, "wheel style inner radius");
        near(0.33F, style.outerRadiusScale, "wheel style outer radius");
        near(15F, style.minInnerRadius, "wheel style minimum inner radius");
        near(2F, style.separatorThickness, "wheel style separator");
        check(style.openDelayMs == 150L, "wheel style open delay");
        near(300F, style.openAnimationMs, "wheel style open animation");
        near(150F, style.closeAnimationMs, "wheel style close animation");
        near(5F, style.highlightLag, "wheel style selection speed");
        check(style.discColor == 0x88000000, "wheel style disc colour");
        check(style.frosted && style.frostedTint == 0x66FFFFFF, "wheel style frosted");
        check(style.lineColor == 0xFFFFFFFF, "wheel style line colour");
        check(style.textColor == 0xFDFDFD, "wheel style text colour");
        near(1.4F, style.titleScale, "wheel style title scale");
        check(style.highlight == WheelHighlight.ANNULAR_SECTOR, "wheel style selection effect");
        check(style.highlightColor == 0xFFFABC02, "wheel style selection colour");
        near(15F, style.deadZone, "wheel style dead zone");
        WheelStyle copy = style.toBuilder()
            .build();
        for (Field styleField : WheelStyle.class.getFields()) {
            if (Modifier.isStatic(styleField.getModifiers())) continue;
            check(
                styleField.get(style)
                    .equals(styleField.get(copy)),
                "wheel style toBuilder keeps " + styleField.getName());
        }
        check(
            WheelMenuBuilder.create()
                .build()
                .style() == WheelStyle.DEFAULT,
            "wheel builder defaults to default style");
        boolean rejected = false;
        try {
            WheelStyle.builder()
                .radii(0.3F, 0.2F, 10);
        } catch (IllegalArgumentException expectedFailure) {
            rejected = true;
        }
        check(rejected, "wheel style rejects outer radius smaller than inner");

        near(0, FxEasing.easeOutQuad(0F), "quad ease start");
        near(1, FxEasing.easeOutQuad(1F), "quad ease end");
        check(FxEasing.easeOutQuad(0.5F) > 0.5F, "quad ease eases out");
        near(0, FxEasing.smoothStep(-1F), "smooth step clamps low");
        near(1, FxEasing.smoothStep(2F), "smooth step clamps high");
        near(0.5, FxEasing.smoothStep(0.5F), "smooth step midpoint");

        String shaders = new String(
            Files.readAllBytes(SOURCES.resolve("client/fx/screen/FxShaders.java")),
            StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile("shader\\(\"([a-z_]+)\"\\)")
            .matcher(shaders);
        int found = 0;
        Pattern reserved = Pattern.compile("\\b(half|fixed|input|output|filter|sample|sizeof|cast)\\b");
        Set<String> screenSpace = Set.of("blur", "frosted");
        while (matcher.find()) {
            found++;
            String name = matcher.group(1);
            Path file = RESOURCES.resolve("shaders/fx/" + name + ".frag.glsl");
            check(Files.isRegularFile(file), "fx shader file " + file);
            if (!Files.isRegularFile(file)) continue;
            String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            check(source.startsWith("#version 120"), "fx shader version " + file);
            check(
                !reserved.matcher(source)
                    .find(),
                "fx shader uses a GLSL 1.20 reserved word " + file);
            if (!screenSpace.contains(name)) check(
                !source.contains("gl_FragCoord") && source.contains("local"),
                "fx shape shader must use local coordinates " + file);
            Matcher include = Pattern.compile("#include \"([^\"]+)\"")
                .matcher(source);
            while (include.find()) check(
                Files.isRegularFile(RESOURCES.resolve("shaders/fx/" + include.group(1))),
                "fx shader include " + include.group(1));
        }
        check(found == 7, "fx shaders declared " + found);
        check(Files.isRegularFile(RESOURCES.resolve("shaders/fx/shape.vert.glsl")), "fx vertex shader");
        check(
            Files.isRegularFile(Paths.get("src/main/resources/licenses/ANVILLIB-MIT.txt")),
            "AnvilLib licence notice");
    }
}
