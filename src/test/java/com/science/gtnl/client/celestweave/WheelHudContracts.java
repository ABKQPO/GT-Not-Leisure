package com.science.gtnl.client.celestweave;

import static com.science.gtnl.client.celestweave.CelestweaveContractTest.RESOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.check;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.near;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.science.gtnl.client.fx.FxAngle;
import com.science.gtnl.client.fx.FxEasing;
import com.science.gtnl.client.hud.HudColor;
import com.science.gtnl.client.hud.HudElement;
import com.science.gtnl.client.hud.HudElement.Choice;
import com.science.gtnl.client.hud.HudElement.Setting;
import com.science.gtnl.client.hud.HudHistory;
import com.science.gtnl.client.hud.HudLayout;
import com.science.gtnl.client.hud.HudOption;
import com.science.gtnl.client.hud.HudRegistry;
import com.science.gtnl.client.hud.HudSnap;
import com.science.gtnl.client.hud.HudSnap.Box;
import com.science.gtnl.client.hud.HudSnap.Snap;
import com.science.gtnl.client.hud.HudTiming;
import com.science.gtnl.client.railgun.RailgunHudRenderer;
import com.science.gtnl.common.gui.modularui.theme.LivePalette;
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

    private static final int W = 480, H = 270;

    private WheelHudContracts() {}

    static void run() throws Exception {
        hud();
        hudLayout();
        hudInUse();
        hudTiming();
        hudSettings();
        hudEditing();
        hudSources();
        wheel();
    }

    private static void hud() {
        near(1, HudTiming.fade(-100), "hud awake before idle");
        near(1, HudTiming.fade(0), "hud awake at idle edge");
        near(0.5, HudTiming.fade(250), "hud fades halfway");
        near(0, HudTiming.fade(10000), "hud fully faded");
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

    private static HudElement.Builder box(String id) {
        return HudElement.builder(id)
            .bounds(-4, -4, 4, 4)
            .style(HudElement.STANDARD_STYLE);
    }

    private static JsonObject json(String text) {
        return new JsonParser().parse(text)
            .getAsJsonObject();
    }

    private static JsonObject saved() {
        return json(
            HudLayout.write()
                .toString());
    }

    private static void hudLayout() throws ReflectiveOperationException {
        Field file = HudLayout.class.getDeclaredField("FILE");
        file.setAccessible(true);
        check("config/GTNotLeisure/hud.json".equals(file.get(null)), "HUD layout saves to hud.json");
        HudElement coreElement = CelestweaveHudElement.CORE.hud;
        HudLayout.Entry fresh = new HudLayout.Entry();
        var core = HudLayout.placement(coreElement, fresh, W, H, 1F);
        near(240 - 97 - 8, core.x(), "core keeps its place left of the hotbar");
        near(270 - 3 - 8, core.y(), "core keeps its place above the bottom edge");
        near(
            480 - 97 - 16,
            HudLayout.placement(coreElement, fresh, 960, 540, 2F)
                .x(),
            "core default follows the base scale");
        near(
            -coreElement.minX * 2,
            HudLayout.placement(coreElement, fresh, W, H, 2F)
                .x(),
            "oversized core readout kept on screen");
        var ring = HudLayout.placement(CelestweaveHudElement.SHIELD_RING.hud, fresh, W, H, 1F);
        near(240, ring.x(), "shield ring centred horizontally");
        near(135, ring.y(), "shield ring centred vertically");
        near(
            240 + 62,
            HudLayout.placement(CelestweaveHudElement.ALTIMETER.hud, fresh, W, H, 1F)
                .x(),
            "altimeter right of the crosshair");
        boolean[] reserved = { true };
        HudRegistry.reserveCrosshair(() -> reserved[0]);
        near(
            240 + 26 + 6.5,
            HudLayout.placement(RailgunHudRenderer.CHARGE, fresh, W, H, 1F)
                .x(),
            "lance charge clears the armor HUD");
        reserved[0] = false;
        near(
            240 + 10 + 6.5,
            HudLayout.placement(RailgunHudRenderer.CHARGE, fresh, W, H, 1F)
                .x(),
            "lance charge hugs the crosshair without armor");

        near(0, HudLayout.anchor(10, 300), "left third anchors left");
        near(0.5, HudLayout.anchor(150, 300), "middle third anchors centre");
        near(1, HudLayout.anchor(290, 300), "right third anchors right");

        HudLayout.Entry entry = new HudLayout.Entry();
        HudLayout.move(entry, 460, 250, W, H);
        var moved = HudLayout.placement(coreElement, entry, W, H, 1F);
        near(460, moved.x(), "dragged x round trip");
        near(250, moved.y(), "dragged y round trip");
        var wider = HudLayout.placement(coreElement, entry, 960, 540, 1F);
        near(940, wider.x(), "corner element keeps its distance to the right edge");
        near(520, wider.y(), "corner element keeps its distance to the bottom edge");
        HudLayout.move(entry, 5000, -5000, W, H);
        var clamped = HudLayout.placement(coreElement, entry, W, H, 1F);
        near(480 - coreElement.maxX, clamped.x(), "element kept inside the right edge");
        near(-coreElement.minY, clamped.y(), "element kept inside the top edge");

        near(HudLayout.MIN_SCALE, HudLayout.clampScale(0.1F), "scale floor");
        near(HudLayout.MAX_SCALE, HudLayout.clampScale(9F), "scale ceiling");
        near(1, HudLayout.clampScale(Float.NaN), "scale NaN falls back");
        near(HudLayout.MIN_OPACITY, HudLayout.clampOpacity(0F), "opacity floor keeps the element visible");
        near(1, HudLayout.clampOpacity(5F), "opacity ceiling");
        near(1, HudLayout.clampOpacity(Float.NaN), "opacity NaN falls back");

        HudElement altimeterElement = CelestweaveHudElement.ALTIMETER.hud;
        HudLayout.reset();
        HudLayout.move(altimeterElement, 100, 50, W, H);
        HudLayout.rescale(altimeterElement, 3);
        HudLayout.toggle(CelestweaveHudElement.DASH_ARC.hud);
        HudLayout.setOpacity(altimeterElement, 0.55F);
        HudLayout.setColor(altimeterElement, HudColor.THEME);
        HudLayout.setTiming(altimeterElement, HudTiming.CHANGED);
        HudLayout.setIdleSeconds(altimeterElement, 7F);
        HudLayout.setIdleAlpha(altimeterElement, 0.25F);
        HudLayout.setOn(coreElement, CelestweaveHudElement.READOUT, false);
        JsonObject saved = saved();
        JsonObject future = new JsonObject();
        future.addProperty("scale", 2.0);
        future.addProperty("glow", "neon");
        saved.getAsJsonObject("elements")
            .add("future_element", future);
        saved.getAsJsonObject("elements")
            .getAsJsonObject("core")
            .addProperty("futureField", 5);
        HudLayout.read(json(saved.toString()));
        var altimeter = HudLayout.entry(altimeterElement);
        check(altimeter.moved, "moved flag survives saving");
        near(1.3, altimeter.scale, "element scale survives saving");
        near(
            100,
            HudLayout.placement(altimeterElement, altimeter, W, H, 1F)
                .x(),
            "position survives saving");
        check(HudLayout.hidden(CelestweaveHudElement.DASH_ARC.hud), "hidden flag survives saving");
        check(!HudLayout.hidden(coreElement), "untouched element stays visible");
        near(0.55, HudLayout.opacity(altimeterElement), "opacity survives saving");
        check(HudLayout.color(altimeterElement) == HudColor.THEME, "color mode survives saving");
        check(HudLayout.timing(altimeterElement) == HudTiming.CHANGED, "display timing survives saving");
        near(7, HudLayout.idleSeconds(altimeterElement), "stay time survives saving");
        near(0.25, HudLayout.idleAlpha(altimeterElement), "faded opacity survives saving");
        check(!HudLayout.on(coreElement, CelestweaveHudElement.READOUT), "element option survives saving");
        check(
            HudLayout.color(coreElement) == HudColor.ACCENT && HudLayout.timing(coreElement) == HudTiming.CHANGED,
            "unchanged settings keep the element defaults after saving");
        JsonObject resaved = saved();
        JsonObject kept = resaved.getAsJsonObject("elements")
            .getAsJsonObject("future_element");
        check(
            kept != null && kept.has("scale")
                && kept.get("scale")
                    .getAsDouble() == 2.0
                && kept.has("glow")
                && "neon".equals(
                    kept.get("glow")
                        .getAsString()),
            "layout of an element that is not registered yet survives saving");
        JsonObject savedCore = resaved.getAsJsonObject("elements")
            .getAsJsonObject("core");
        check(
            savedCore != null && savedCore.has("futureField")
                && savedCore.get("futureField")
                    .getAsInt() == 5,
            "unknown fields of a registered element survive saving");

        HudLayout.read(
            json(
                "{\"elements\":{\"core\":{\"moved\":true,\"x\":\"abc\",\"y\":5,\"anchorX\":0.3,\"scale\":99,"
                    + "\"opacity\":-1,\"color\":\"rainbow\",\"timing\":\"sometimes\",\"idleSeconds\":999,"
                    + "\"idleAlpha\":\"dim\",\"style\":\"fancy\",\"options\":{\"readout\":\"yes\"}},\"odd\":5}}"));
        var repaired = HudLayout.entry(coreElement);
        check(!repaired.moved, "unreadable position falls back to the default place");
        near(0.5, repaired.anchorX, "unknown anchor falls back to centre");
        near(HudLayout.MAX_SCALE, repaired.scale, "oversized scale clamped");
        near(HudLayout.MIN_OPACITY, repaired.opacity, "negative opacity clamped");
        check(HudLayout.color(coreElement) == HudColor.ACCENT, "unknown color mode falls back to the element default");
        check(HudLayout.timing(coreElement) == HudTiming.CHANGED, "unknown timing falls back to the element default");
        near(HudLayout.MAX_IDLE_SECONDS, HudLayout.idleSeconds(coreElement), "oversized stay time clamped");
        near(0.35, HudLayout.idleAlpha(coreElement), "unreadable faded opacity falls back to the element default");
        check(
            HudElement.STANDARD_STYLE.id()
                .equals(HudLayout.style(coreElement)),
            "unknown style falls back to the default style");
        check(HudLayout.on(coreElement, CelestweaveHudElement.READOUT), "option of the wrong type falls back");
        check(
            !HudLayout.snapshot()
                .containsKey("odd"),
            "an element entry that is not an object is skipped");
        HudLayout.read(json("{\"elements\":5}"));
        check(
            HudLayout.snapshot()
                .isEmpty(),
            "a broken elements block reads as empty");
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

        HudElement plain = box("contract_plain").build();
        check((HudElement.DEFAULT_LANG_PREFIX + "contract_plain").equals(plain.lang()), "default HUD lang key");
        near(0.5, plain.anchorX, "default HUD anchor x");
        near(0.5, plain.anchorY, "default HUD anchor y");
        check(plain.isActive(), "HUD element active by default");
        near(1, plain.baseScale(), "HUD base scale defaults to 1");
        check(!plain.hasRenderer(), "HUD element without renderer draws itself");
        check(plain.defaultTiming() == HudTiming.ALWAYS, "HUD element shows always by default");
        check(!plain.colorable(), "HUD element has no color modes unless it declares them");
        near(
            240,
            HudLayout.placement(plain, new HudLayout.Entry(), W, H, plain.baseScale())
                .x(),
            "default HUD element sits at the screen centre");
        check(
            rejects(
                () -> HudElement.builder("contract_flat")
                    .style(HudElement.STANDARD_STYLE)
                    .build()),
            "HUD element without bounds rejected");
        check(
            rejects(
                () -> HudElement.builder("contract_plain_look")
                    .bounds(-4, -4, 4, 4)
                    .build()),
            "HUD element without a style rejected");
        check(rejects(() -> HudElement.builder("")), "empty HUD id rejected");
        check(
            rejects(
                () -> box("contract_twice").style(HudElement.STANDARD_STYLE)
                    .build()),
            "repeated style id rejected");
        check(
            rejects(
                () -> box("contract_options").option(HudOption.toggle("a", "x", true))
                    .option(HudOption.toggle("a", "y", false))
                    .build()),
            "repeated option id rejected");
        check(rejects(() -> box("contract_idle").idle(-1, 0.5F)), "negative stay time rejected");
        check(rejects(() -> box("contract_idle").idle(3, 2)), "faded opacity above 1 rejected");

        boolean[] shown = { false };
        double[] scale = { Double.NaN };
        HudElement toggled = box("contract_toggled").timing(HudTiming.ACTIVE)
            .activeWhen(() -> shown[0])
            .baseScale(() -> scale[0])
            .render((place, partialTicks) -> {})
            .build();
        HudLayout.reset();
        check(HudLayout.place(toggled, W, H) == null, "hidden while nothing is going on");
        shown[0] = true;
        near(
            1,
            HudLayout.place(toggled, W, H)
                .scale(),
            "invalid base scale falls back to 1");
        scale[0] = 2;
        near(
            2,
            HudLayout.place(toggled, W, H)
                .scale(),
            "base scale comes from the element");
        HudLayout.toggle(toggled);
        check(HudLayout.place(toggled, W, H) == null, "editor hide still applies");
        HudLayout.reset();

        HudRegistry.register(toggled);
        check(HudRegistry.byId("contract_toggled") == toggled, "registered HUD element found by id");
        check(rejects(() -> HudRegistry.register(toggled)), "duplicate HUD id rejected");
    }

    private static void hudInUse() {
        boolean[] used = { false }, shown = { false };
        HudElement equipped = box("contract_equipped").timing(HudTiming.ACTIVE)
            .inUseWhen(() -> used[0])
            .activeWhen(() -> shown[0])
            .baseScale(() -> 1.5)
            .render((place, partialTicks) -> {})
            .build();
        HudElement always = box("contract_always").build();
        check(always.isInUse(), "HUD element in use by default");
        HudRegistry.register(equipped);
        HudRegistry.register(always);
        HudLayout.reset();
        check(!equipped.isInUse(), "HUD element follows its in-use condition");
        check(
            !HudRegistry.candidates(false)
                .contains(equipped),
            "editor leaves out an element that is not in use");
        check(
            HudRegistry.candidates(false)
                .contains(always),
            "editor lists an element in use");
        check(
            HudRegistry.candidates(true)
                .contains(equipped),
            "show all lists an element that is not in use");
        check(
            HudRegistry.candidates(true)
                .size()
                == HudRegistry.elements()
                    .size(),
            "show all lists every registered element");
        shown[0] = true;
        check(HudLayout.place(equipped, W, H) == null, "element not in use is not drawn");
        used[0] = true;
        shown[0] = false;
        check(
            HudRegistry.candidates(false)
                .contains(equipped),
            "editor lists an element in use with nothing to show");
        check(HudLayout.place(equipped, W, H) == null, "element in use with nothing going on is not drawn");
        shown[0] = true;
        check(HudLayout.place(equipped, W, H) != null, "element in use with something going on is drawn");
        HudLayout.setTiming(equipped, HudTiming.ALWAYS);
        shown[0] = false;
        check(HudLayout.place(equipped, W, H) != null, "always shown element is drawn with nothing going on");

        boolean enabled = MainConfig.client.enableHud;
        float globalScale = MainConfig.client.hudScale;
        try {
            MainConfig.client.hudScale = 2F;
            near(
                3,
                HudLayout.place(equipped, W, H)
                    .scale(),
                "global HUD scale multiplies the element base scale");
            near(
                3,
                HudLayout.preview(equipped, W, H)
                    .scale(),
                "editor preview uses the global HUD scale");
            HudLayout.rescale(equipped, 5);
            near(
                4.5,
                HudLayout.place(equipped, W, H)
                    .scale(),
                "editor scale multiplies on top of the global HUD scale");
            MainConfig.client.hudScale = Float.NaN;
            near(
                2.25,
                HudLayout.place(equipped, W, H)
                    .scale(),
                "invalid global HUD scale falls back to 1");
            MainConfig.client.enableHud = false;
            check(!HudLayout.enabled(), "HUD switch comes from the config");
            check(HudLayout.place(equipped, W, H) == null, "HUD switch off hides an element in use");
            check(HudLayout.place(always, W, H) == null, "HUD switch off hides an element without conditions");
            check(
                HudRegistry.candidates(false)
                    .contains(always),
                "editor still lists elements with the HUD switch off");
        } finally {
            MainConfig.client.enableHud = enabled;
            MainConfig.client.hudScale = globalScale;
            HudLayout.reset();
        }
        check(HudLayout.place(always, W, H) != null, "HUD switch on draws again");
    }

    private static void hudTiming() throws ReflectiveOperationException {
        HudTiming.Tracker changed = new HudTiming.Tracker();
        near(1, changed.awake(HudTiming.CHANGED, 1000, false, 7, 3), "first sight wakes the element");
        near(1, changed.awake(HudTiming.CHANGED, 3999, false, 7, 3), "element stays for its stay time");
        near(1, changed.awake(HudTiming.CHANGED, 4000, false, 7, 3), "element still shown at the end of its stay");
        near(0.5, changed.awake(HudTiming.CHANGED, 4250, false, 7, 3), "half faded halfway through the fade");
        near(0, changed.awake(HudTiming.CHANGED, 4500, false, 7, 3), "fully faded after the fade");
        near(0, changed.awake(HudTiming.CHANGED, 8000, false, 7, 3), "stays faded while nothing changes");
        near(1, changed.awake(HudTiming.CHANGED, 9000, false, 8, 3), "a change wakes it again");
        near(1, changed.awake(HudTiming.CHANGED, 20000, true, 8, 3), "being in use keeps it awake");
        near(1, changed.awake(HudTiming.CHANGED, 23000, false, 8, 3), "the stay counts from the end of the use");
        near(0, changed.awake(HudTiming.CHANGED, 23500, false, 8, 3), "and then it fades");
        changed.reset();
        near(1, changed.awake(HudTiming.CHANGED, 50000, false, 8, 3), "putting it back on wakes it");
        HudTiming.Tracker quick = new HudTiming.Tracker();
        near(1, quick.awake(HudTiming.CHANGED, 0, false, 1, 0), "zero stay starts fully shown");
        near(0.5, quick.awake(HudTiming.CHANGED, 250, false, 1, 0), "zero stay fades at once");
        near(0.35, HudTiming.CHANGED.level(0, 0.35F), "faded element rests at its faded opacity");
        near(0.35F + 0.65F * 0.5F, HudTiming.CHANGED.level(0.5F, 0.35F), "fading blends towards the faded opacity");
        near(1, HudTiming.CHANGED.level(1, 0.35F), "awake element is fully shown");

        HudTiming.Tracker always = new HudTiming.Tracker();
        near(1, always.awake(HudTiming.ALWAYS, 0, false, 0, 3), "always shown at first");
        near(1, always.awake(HudTiming.ALWAYS, 100000, false, 0, 3), "always shown long after");
        near(1, HudTiming.ALWAYS.level(1, 0.35F), "always shown ignores the faded opacity");

        HudTiming.Tracker active = new HudTiming.Tracker();
        near(0, active.awake(HudTiming.ACTIVE, 0, false, 0, 3), "only while in use hides with nothing going on");
        near(1, active.awake(HudTiming.ACTIVE, 10, true, 0, 3), "only while in use shows during use");
        near(0, active.awake(HudTiming.ACTIVE, 11, false, 0, 3), "only while in use hides the moment use stops");
        near(0, active.awake(HudTiming.ACTIVE, 12, false, 5, 3), "a change alone does not count as use");
        near(0, HudTiming.ACTIVE.level(0, 0.35F), "only while in use ignores the faded opacity");

        boolean[] busy = { false };
        int[] state = { 0 };
        HudElement fading = box("contract_fading").timing(HudTiming.CHANGED)
            .activeWhen(() -> busy[0])
            .changeWhen(() -> state[0])
            .idle(2, 0)
            .build();
        HudLayout.reset();
        var awake = HudLayout.place(fading, W, H);
        check(awake != null && awake.alpha() == 1F && awake.resting(), "fading element shows on first sight");
        HudLayout.setOpacity(fading, 0.5F);
        near(
            0.5,
            HudLayout.place(fading, W, H)
                .alpha(),
            "the player's opacity scales what the drawer gets");
        HudLayout.setTiming(fading, HudTiming.ACTIVE);
        check(HudLayout.place(fading, W, H) == null, "switching to only while in use hides it at once");
        busy[0] = true;
        check(
            !HudLayout.place(fading, W, H)
                .resting(),
            "only while in use never asks for the resting look");
        HudLayout.reset();

        check(CelestweaveHudElement.CORE.hud.defaultTiming() == HudTiming.CHANGED, "energy core fades after changes");
        near(3, CelestweaveHudElement.CORE.hud.defaultIdleSeconds(), "energy core stays three seconds as before");
        near(0.35, CelestweaveHudElement.CORE.hud.defaultIdleAlpha(), "energy core rests at 35% as before");
        for (CelestweaveHudElement element : new CelestweaveHudElement[] { CelestweaveHudElement.SHIELD_RING,
            CelestweaveHudElement.DASH_ARC, CelestweaveHudElement.ALTIMETER })
            check(element.hud.defaultTiming() == HudTiming.ACTIVE, element + " shows only while in use as before");
        for (CelestweaveHudElement element : CelestweaveHudElement.values())
            check(element.hud.defaultColor() == HudColor.ACCENT, element + " follows the armor glow by default");
        check(RailgunHudRenderer.CHARGE.defaultTiming() == HudTiming.ACTIVE, "lance charge shows only while charging");
        for (String removed : new String[] { "celestweaveHudIdleSeconds", "celestweaveHudIdleAlpha" }) check(
            Arrays.stream(MainConfig.Client.class.getFields())
                .noneMatch(
                    field -> field.getName()
                        .equals(removed)),
            "armor-only HUD fade config " + removed + " is gone");
    }

    private static void hudSettings() {
        Choice round = new Choice("round", "gtnl.hud.style.contract_round");
        HudElement dual = box("contract_dual").style(round)
            .color(HudColor.THEME)
            .option(HudOption.toggle("glow", "gtnl.hud.option.contract_glow", true))
            .option(HudOption.number("size", "gtnl.hud.option.contract_size", 1, 3, 4, 2))
            .option(
                HudOption.choice(
                    "shape",
                    "gtnl.hud.option.contract_shape",
                    "hex",
                    new Choice("hex", "gtnl.hud.option.contract_hex"),
                    new Choice("star", "gtnl.hud.option.contract_star")))
            .build();
        HudLayout.reset();
        check(
            HudElement.STANDARD_STYLE.id()
                .equals(HudLayout.style(dual)),
            "first declared style is the default");
        HudLayout.setStyle(dual, "round");
        check("round".equals(HudLayout.style(dual)), "player picks the second style");
        check(
            "round".equals(
                HudLayout.preview(dual, W, H)
                    .style()),
            "drawer receives the chosen style");
        HudLayout.setStyle(dual, "missing");
        check(
            HudElement.STANDARD_STYLE.id()
                .equals(HudLayout.style(dual)),
            "unknown style falls back to the default");
        HudLayout.setStyle(dual, "round");
        check(HudLayout.on(dual, "glow"), "switch option starts at its default");
        near(2, HudLayout.number(dual, "size"), "number option starts at its default");
        check("hex".equals(HudLayout.choice(dual, "shape")), "choice option starts at its default");
        HudLayout.setOn(dual, "glow", false);
        HudLayout.setNumber(dual, "size", 2.6);
        near(2.5, HudLayout.number(dual, "size"), "number option snaps to its steps");
        HudLayout.setNumber(dual, "size", 99);
        near(3, HudLayout.number(dual, "size"), "number option clamps to its range");
        HudLayout.setChoice(dual, "shape", "star");
        check("star".equals(HudLayout.choice(dual, "shape")), "choice option takes a listed choice");
        JsonObject saved = saved();
        HudLayout.reset();
        HudLayout.read(saved);
        var place = HudLayout.preview(dual, W, H);
        check("round".equals(place.style()), "chosen style survives saving");
        check(!place.on("glow"), "switch option survives saving and reaches the drawer");
        near(3, place.number("size"), "number option survives saving and reaches the drawer");
        check("star".equals(place.choice("shape")), "choice option survives saving and reaches the drawer");
        HudLayout.setChoice(dual, "shape", "nope");
        check("hex".equals(HudLayout.choice(dual, "shape")), "unlisted choice falls back to the default");
        check(rejects(() -> HudLayout.on(dual, "size")), "reading a number option as a switch is rejected");
        check(rejects(() -> HudLayout.number(dual, "unknown")), "reading an undeclared option is rejected");
        check(rejects(() -> HudOption.number("bad", "x", 3, 1, 4, 2)), "number option needs max above min");
        check(rejects(() -> HudOption.number("bad", "x", 1, 3, 4, 5)), "number option default must be in range");
        check(
            rejects(() -> HudOption.choice("bad", "x", "c", new Choice("a", "y"))),
            "choice option default must be listed");
        check(
            rejects(() -> HudOption.choice("bad", "x", "a", new Choice("a", "y"), new Choice("a", "z"))),
            "choice option ids must differ");
        check(rejects(() -> new Choice("", "x")), "empty choice id rejected");
        check(
            "3".equals(
                HudOption.number("n", "x", 0, 30, 30, 3)
                    .format(3)),
            "whole steps show whole numbers");
        check(
            "0.4".equals(
                HudOption.number("n", "x", 0, 1, 10, 0.4)
                    .format(0.4)),
            "tenth steps show one decimal");
        check(
            "0.35".equals(
                HudOption.number("n", "x", 0, 1, 20, 0.35)
                    .format(0.35)),
            "finer steps show two decimals");

        List<Setting> plainSettings = dual.settings(HudTiming.ALWAYS);
        check(
            plainSettings.contains(Setting.COLOR) && plainSettings.contains(Setting.STYLE),
            "an element with colors and two styles lists both settings");
        check(
            !plainSettings.contains(Setting.IDLE_SECONDS) && !plainSettings.contains(Setting.IDLE_ALPHA),
            "stay time is only listed when fading after changes");
        List<Setting> fadeSettings = dual.settings(HudTiming.CHANGED);
        check(
            fadeSettings.contains(Setting.IDLE_SECONDS) && fadeSettings.contains(Setting.IDLE_ALPHA),
            "fading after changes lists its stay time and faded opacity");
        check(
            dual.settings(HudTiming.ALWAYS)
                .containsAll(Arrays.asList(Setting.SHOWN, Setting.SCALE, Setting.OPACITY, Setting.TIMING)),
            "every element lists visibility, scale, opacity and timing");
        HudElement charge = RailgunHudRenderer.CHARGE;
        check(!charge.colorable(), "lance charge keeps its charge colors");
        check(
            !charge.settings(HudTiming.ACTIVE)
                .contains(Setting.COLOR),
            "lance charge shows no color setting");
        check(
            !charge.settings(HudTiming.ACTIVE)
                .contains(Setting.STYLE),
            "a single style shows no style setting");
        HudLayout.setColor(charge, HudColor.ACCENT);
        check(HudLayout.color(charge) == null, "an element without colors ignores a color mode");
        check(
            CelestweaveHudElement.CORE.hud.settings(HudTiming.CHANGED)
                .contains(Setting.COLOR),
            "armor elements show the color setting");
        check(
            CelestweaveHudElement.CORE.hud.options()
                .size() == 1 && HudLayout.on(CelestweaveHudElement.CORE.hud, CelestweaveHudElement.READOUT),
            "energy core shows its readout by default");
        HudLayout.reset();

        String accentLang = HudColor.ACCENT.lang();
        LivePalette palette = new LivePalette(() -> "gtnl:contract_missing", () -> true);
        try {
            check(HudColor.THEME.rgb() == 0xFFFFFF, "without a palette both color modes are neutral");
            palette.accentFrom(() -> 0xFFFFFFFF);
            HudColor.source(palette, "item.gtnl.contract.glow");
            check(HudColor.ACCENT.rgb() == 0xFFFFFF, "follow armor uses the raw glow color, even pure white");
            palette.accentFrom(() -> 0xFF123456);
            check(HudColor.ACCENT.rgb() == 0x123456, "follow armor tracks the glow color");
            palette.accentFrom(() -> LivePalette.NO_ACCENT);
            check(HudColor.ACCENT.rgb() == HudColor.THEME.rgb(), "follow armor without a chestplate uses the theme");
            check("item.gtnl.contract.glow".equals(HudColor.ACCENT.lang()), "follow armor label comes from its source");
            check("gtnl.hud.color.theme".equals(HudColor.THEME.lang()), "follow theme label is the framework's");
        } finally {
            HudColor.source(null, accentLang);
        }
    }

    private static void hudEditing() {
        HudElement wide = HudElement.builder("contract_wide")
            .bounds(-10, -5, 10, 5)
            .style(HudElement.STANDARD_STYLE)
            .build();
        Snap free = HudSnap.snap(Box.of(wide, 100, 100, 1F), Collections.emptyList(), W, H, HudSnap.RANGE);
        check(!free.snappedX() && !free.snappedY(), "far from every line nothing snaps");
        near(100, free.x(), "unsnapped x stays");
        near(100, free.y(), "unsnapped y stays");
        Snap centre = HudSnap.snap(Box.of(wide, 242, 133, 1F), Collections.emptyList(), W, H, HudSnap.RANGE);
        near(240, centre.x(), "element snaps to the vertical centre line");
        near(240, centre.guideX(), "guide drawn on the vertical centre line");
        near(135, centre.y(), "element snaps to the horizontal centre line");
        near(135, centre.guideY(), "guide drawn on the horizontal centre line");
        Snap left = HudSnap.snap(Box.of(wide, 12, 200, 1F), Collections.emptyList(), W, H, HudSnap.RANGE);
        near(10, left.x(), "left edge snaps to the screen edge");
        near(0, left.guideX(), "guide drawn on the screen edge");
        Snap right = HudSnap.snap(Box.of(wide, 468, 200, 1F), Collections.emptyList(), W, H, HudSnap.RANGE);
        near(470, right.x(), "right edge snaps to the screen edge");
        near(480, right.guideX(), "guide drawn on the right screen edge");
        Snap bottom = HudSnap.snap(Box.of(wide, 300, 263, 1F), Collections.emptyList(), W, H, HudSnap.RANGE);
        near(265, bottom.y(), "bottom edge snaps to the screen bottom");
        Box other = Box.of(wide, 300, 60, 1F);
        Snap beside = HudSnap.snap(Box.of(wide, 322, 150, 1F), Collections.singletonList(other), W, H, HudSnap.RANGE);
        near(320, beside.x(), "left edge snaps to another element's right edge");
        near(310, beside.guideX(), "guide drawn on the other element's edge");
        Snap aligned = HudSnap.snap(Box.of(wide, 150, 62, 1F), Collections.singletonList(other), W, H, HudSnap.RANGE);
        near(60, aligned.y(), "element lines up with another element");
        check(
            HudSnap.snap(Box.of(wide, 244, 100, 1F), Collections.emptyList(), W, H, HudSnap.RANGE)
                .snappedX(),
            "snapping reaches its full range");
        check(
            !HudSnap.snap(Box.of(wide, 244.5F, 100, 1F), Collections.emptyList(), W, H, HudSnap.RANGE)
                .snappedX(),
            "snapping stops beyond its range");
        Snap nearest = HudSnap.snap(Box.of(wide, 238.5F, 100, 1F), Collections.emptyList(), W, H, HudSnap.RANGE);
        near(240, nearest.x(), "the closest line wins");
        near(
            80,
            Box.of(wide, 100, 100, 2F)
                .minX(),
            "snap box follows the element scale");

        check(HudSnap.nudge(false) == 1 && HudSnap.nudge(true) == 10, "arrow keys nudge 1 pixel, 10 with Shift");
        HudLayout.reset();
        var start = HudLayout.preview(wide, W, H);
        float[] step = HudSnap.nudged(wide, start, 1, 0, W, H);
        near(241, step[0], "nudge right by one pixel");
        near(135, step[1], "nudge right keeps the height");
        float[] up = HudSnap.nudged(wide, start, 0, -HudSnap.NUDGE_FAST, W, H);
        near(125, up[1], "Shift nudge moves ten pixels");
        HudLayout.move(wide, step[0], step[1], W, H);
        near(
            241,
            HudLayout.preview(wide, W, H)
                .x(),
            "a nudge moves the element");
        HudLayout.move(wide, 468, 100, W, H);
        float[] wall = HudSnap.nudged(wide, HudLayout.preview(wide, W, H), HudSnap.NUDGE_FAST, 0, W, H);
        near(470, wall[0], "nudging stops at the screen edge");

        HudHistory<String> history = new HudHistory<>(3);
        check(!history.canUndo() && !history.canRedo(), "fresh history has nothing to undo or redo");
        history.record(() -> "a", null);
        history.record(() -> "b", null);
        check("b".equals(history.undo("c")), "undo returns the state before the last change");
        check("a".equals(history.undo("b")), "undo walks further back");
        check(history.undo("a") == null, "nothing left to undo");
        check("b".equals(history.redo("a")), "redo replays a change");
        check("c".equals(history.redo("b")), "redo replays the next change");
        check(history.redo("c") == null, "nothing left to redo");
        history.undo("c");
        history.record(() -> "b", null);
        check(!history.canRedo(), "a new change clears redo");
        HudHistory<String> grouped = new HudHistory<>(10);
        Object drag = new Object();
        int[] copies = { 0 };
        for (String state : new String[] { "start", "mid", "late" }) grouped.record(() -> {
            copies[0]++;
            return state;
        }, drag);
        check(copies[0] == 1, "a drag copies the layout only once");
        check("start".equals(grouped.undo("end")), "one drag is one undo step");
        check(!grouped.canUndo(), "the whole drag is undone at once");
        grouped.record(() -> "x", "slider");
        grouped.seal();
        grouped.record(() -> "y", "slider");
        check("y".equals(grouped.undo("z")) && "x".equals(grouped.undo("y")), "releasing a slider ends its step");
        HudHistory<Integer> limited = new HudHistory<>(2);
        for (int i = 1; i <= 3; i++) {
            final int state = i;
            limited.record(() -> state, null);
        }
        check(
            limited.undo(4) == 3 && limited.undo(3) == 2 && limited.undo(2) == null,
            "history keeps only its newest steps");
        check(rejects(() -> new HudHistory<String>(0)), "history needs room for a step");

        HudLayout.reset();
        Map<String, HudLayout.Entry> before = HudLayout.snapshot();
        HudLayout.setOpacity(wide, 0.5F);
        HudLayout.move(wide, 50, 50, W, H);
        Map<String, HudLayout.Entry> after = HudLayout.snapshot();
        HudLayout.restore(before);
        near(1, HudLayout.opacity(wide), "restoring a snapshot undoes a setting");
        near(
            240,
            HudLayout.preview(wide, W, H)
                .x(),
            "restoring a snapshot undoes a move");
        after.get(wide.id).opacity = 0.2F;
        HudLayout.restore(after);
        HudLayout.entry(wide).opacity = 0.9F;
        near(0.2, after.get(wide.id).opacity, "a snapshot is a copy of the layout");
        HudLayout.reset();
    }

    private static String compact(Path file) throws IOException {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8).replaceAll("\\s", "");
    }

    private static void hudSources() throws IOException {
        Path hud = SOURCES.resolve("client/hud");
        try (Stream<Path> files = Files.list(hud)) {
            for (Path file : (Iterable<Path>) files::iterator) {
                String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                check(
                    !source.contains("Celestweave") && !source.contains("Railgun")
                        && !source.contains("Equipment")
                        && !source.contains("celestweave")
                        && !source.contains("railgun"),
                    file.getFileName() + " names no feature");
            }
        }
        String editor = compact(hud.resolve("HudEditor.java")), overlay = compact(hud.resolve("HudOverlay.java"));
        check(
            editor.contains("publicstaticvoidopen(){pending=true;}") && overlay.contains("HudEditor.openPending()")
                && overlay.contains("TickEvent.Phase.END"),
            "the HUD editor opens after the current frame, so MUI2 click callbacks can open it");
        check(
            compact(SOURCES.resolve("common/gui/equipment/EquipmentGui.java"))
                .contains(".displayGuiScreen(null);HudEditor.open();"),
            "the equipment settings button closes its container before the editor opens");
        check(
            compact(SOURCES.resolve("client/celestweave/CelestweaveThemeAccent.java"))
                .contains("HudColor.source(EquipmentTheme.LIVE,"),
            "the armor glow is the HUD's follow-armor color");
        check(
            !compact(SOURCES.resolve("client/celestweave/CelestweaveHud.java")).contains("look.glowColor"),
            "the armor HUD takes its color from the player's color mode");
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
