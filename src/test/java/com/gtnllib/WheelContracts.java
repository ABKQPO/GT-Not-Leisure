package com.gtnllib;

import static com.gtnllib.GTNLLibContractTest.RESOURCES;
import static com.gtnllib.GTNLLibContractTest.SOURCES;
import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.near;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.gtnllib.client.fx.FxAngle;
import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.gui.wheel.WheelActionContext;
import com.gtnllib.gui.wheel.WheelEntry;
import com.gtnllib.gui.wheel.WheelHighlight;
import com.gtnllib.gui.wheel.WheelMenuBuilder;
import com.gtnllib.gui.wheel.WheelMenuModel;
import com.gtnllib.gui.wheel.WheelNavigator;
import com.gtnllib.gui.wheel.WheelOpenMode;
import com.gtnllib.gui.wheel.WheelPageModel;
import com.gtnllib.gui.wheel.WheelStyle;
import com.gtnllib.gui.wheel.WheelWidget;

public class WheelContracts {

    private WheelContracts() {}

    static void run() throws Exception {
        wheel();
        closeKey();
        fit();
    }

    private static void fit() {
        int hotbar = 22, room = 104;
        near(504, WheelWidget.fitSize(960, 504, 0, WheelStyle.DEFAULT), "a wheel with nothing below keeps its size");
        WheelStyle style = WheelStyle.builder()
            .radii(0.125F, 0.24F, 15F)
            .build();
        for (int[] screen : new int[][] { { 960, 504 }, { 640, 360 }, { 1280, 680 } }) {
            float radius = WheelWidget.fitSize(screen[0], screen[1], room, style) * style.outerRadiusScale;
            check(
                screen[1] / 2F + radius + room <= screen[1] - hotbar,
                "the buttons below a " + screen[0] + "x" + screen[1] + " wheel end above the hotbar, radius " + radius);
            check(radius <= screen[1] * style.outerRadiusScale + 1.0E-3F, "a fitted wheel never grows past its style");
        }
        near(
            504,
            WheelWidget.fitSize(960, 504, room, style),
            "a wheel whose buttons already clear the hotbar keeps its size");
        near(
            48,
            WheelWidget.fitSize(427, 240, room, style) * style.outerRadiusScale,
            "a wheel on a screen too short for its buttons stops shrinking at a usable size");
        int above = 44;
        for (int[] screen : new int[][] { { 960, 504 }, { 640, 360 }, { 1280, 680 } }) {
            float radius = WheelWidget.fitSize(screen[0], screen[1], room, above, style) * style.outerRadiusScale;
            check(
                screen[1] / 2F - radius - above >= 0 && screen[1] / 2F + radius + room <= screen[1] - hotbar,
                "buttons above and below a " + screen[0]
                    + "x"
                    + screen[1]
                    + " wheel both stay on screen, radius "
                    + radius);
        }
        near(
            WheelWidget.fitSize(640, 360, room, style),
            WheelWidget.fitSize(640, 360, room, 0, style),
            "a wheel with nothing above sizes as before");
        check(
            WheelWidget.fitSize(640, 360, 0, 200, style) < WheelWidget.fitSize(640, 360, 0, 0, style),
            "a wheel shrinks to make room for tall buttons above it");
    }

    private static void closeKey() throws Exception {
        String screen = new String(
            Files.readAllBytes(SOURCES.resolve("gui/wheel/WheelScreen.java")),
            StandardCharsets.UTF_8).replaceAll("\\s", "");
        int start = screen.indexOf("publicbooleanonKeyPressed("), end = screen.indexOf("publicstaticclassCloser");
        check(start >= 0 && end > start, "wheel screen handles key presses before its closer class");
        if (start < 0 || end <= start) return;
        check(
            screen.substring(start, end)
                .contains("if(!Keyboard.isRepeatEvent())widget.dismiss();"),
            "wheel close key ignores key repeat, which ModularUI turns on while its screens are open");
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
        near(1F, WheelWidget.fitScale(1F, 100, 1, 9, 200, 100), "a short wheel title keeps its size");
        near(0.5F, WheelWidget.fitScale(1F, 300, 1, 9, 150, 100), "a wide wheel title shrinks to the circle");
        near(0.5F, WheelWidget.fitScale(1F, 50, 4, 9, 200, 18), "a wheel title with many lines shrinks to fit");
        near(1F, WheelWidget.fitScale(1F, 0, 0, 9, 10, 10), "an empty wheel title keeps its size");
        List<WheelActionContext> rightClicked = new ArrayList<>();
        WheelNavigator right = new WheelNavigator(
            WheelMenuBuilder.create()
                .slotsPerPage(4)
                .entry(
                    WheelEntry.action("r", "R", context -> {})
                        .onRightClick(rightClicked::add))
                .action("plain", "P", context -> {})
                .build(),
            WheelOpenMode.HOLD);
        check(
            right.rightClick(0) && rightClicked.size() == 1
                && rightClicked.get(0)
                    .entryId()
                    .equals("r"),
            "wheel right click runs the entry's right click action");
        check(
            !right.rightClick(1) && !right.rightClick(-1) && rightClicked.size() == 1,
            "wheel right click ignores entries without one and no selection");

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
                include.group(1)
                    .indexOf(':') < 0 && Files.isRegularFile(RESOURCES.resolve("shaders/fx/" + include.group(1))),
                "fx shader include " + include.group(1));
        }
        check(found == 7, "fx shaders declared " + found);
        check(Files.isRegularFile(RESOURCES.resolve("shaders/fx/shape.vert.glsl")), "fx vertex shader");
        check(
            Files.isRegularFile(Paths.get("src/main/resources/licenses/ANVILLIB-MIT.txt")),
            "AnvilLib licence notice");
    }
}
