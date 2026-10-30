package com.gtnllib;

import static com.gtnllib.GTNLLibContractTest.SOURCES;
import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.compact;
import static com.gtnllib.GTNLLibContractTest.near;
import static com.gtnllib.GTNLLibContractTest.read;
import static com.gtnllib.GTNLLibContractTest.rejects;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.hud.HudColor;
import com.gtnllib.client.hud.HudElement;
import com.gtnllib.client.hud.HudElement.Choice;
import com.gtnllib.client.hud.HudElement.Setting;
import com.gtnllib.client.hud.HudHistory;
import com.gtnllib.client.hud.HudLayout;
import com.gtnllib.client.hud.HudOption;
import com.gtnllib.client.hud.HudRegistry;
import com.gtnllib.client.hud.HudSnap;
import com.gtnllib.client.hud.HudSnap.Box;
import com.gtnllib.client.hud.HudSnap.Snap;
import com.gtnllib.client.hud.HudTiming;
import com.gtnllib.config.GTNLLibConfig;
import com.gtnllib.gui.theme.LivePalette;

public class HudContracts {

    private static final int W = 480, H = 270;
    private static final String LABEL = "label";
    private static final HudElement GAUGE = HudElement.builder("contract_gauge")
        .anchor(0.5F, 1F)
        .bounds(-92, -12, 10, 10)
        .style(HudElement.STANDARD_STYLE)
        .color(HudColor.ACCENT)
        .timing(HudTiming.CHANGED)
        .idle(3, 0.35F)
        .option(HudOption.toggle(LABEL, "gtnllib.hud.option.contract_label", true))
        .build();
    private static final HudElement METER = HudElement.builder("contract_meter")
        .bounds(-42, -47, 24, 49)
        .style(HudElement.STANDARD_STYLE)
        .color(HudColor.ACCENT)
        .timing(HudTiming.ACTIVE)
        .build();
    private static final HudElement ARC = box("contract_arc").build();

    static void run() throws Exception {
        hud();
        hudLayout();
        hudSaveFile();
        hudInUse();
        hudTiming();
        hudSettings();
        hudEditing();
        hudSources();
    }

    private static void hud() {
        near(1, HudTiming.fade(-100), "hud awake before idle");
        near(1, HudTiming.fade(0), "hud awake at idle edge");
        near(0.5, HudTiming.fade(250), "hud fades halfway");
        near(0, HudTiming.fade(10000), "hud fully faded");
        near(0, FxEasing.easeOutCubic(-1), "hud slide clamps low");
        near(1, FxEasing.easeOutCubic(2), "hud slide clamps high");
        check(FxEasing.easeOutCubic(0.5F) > 0.5F, "hud slide eases out");
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

    private static void hudSaveFile() throws IOException {
        Map<String, HudLayout.Entry> before = HudLayout.snapshot();
        Path folder = Files.createTempDirectory("gtnllib-hud");
        File target = folder.resolve("hud.json")
            .toFile();
        File temp = new File(target.getPath() + ".tmp");
        try {
            HudLayout.read(json("{\"elements\":{\"saved_once\":{\"moved\":true,\"x\":3,\"y\":4}}}"));
            HudLayout.save(target);
            check(target.isFile() && json(read(target.toPath())).equals(saved()), "the HUD layout saves to its file");
            HudLayout.read(json("{\"elements\":{}}"));
            HudLayout.save(target);
            check(
                target.isFile() && json(read(target.toPath())).equals(saved()),
                "a second save replaces the old HUD file");
            check(!temp.exists(), "saving leaves no temporary HUD file behind");
            check(
                compact(read(SOURCES.resolve("client/hud/HudLayout.java"))).contains("StandardCopyOption.ATOMIC_MOVE"),
                "the HUD file is swapped in one step, so a crash while saving keeps the old layout");
        } finally {
            Files.deleteIfExists(temp.toPath());
            Files.deleteIfExists(target.toPath());
            Files.deleteIfExists(folder);
            HudLayout.restore(before);
        }
    }

    private static void hudLayout() throws ReflectiveOperationException {
        Field file = HudLayout.class.getDeclaredField("FILE");
        file.setAccessible(true);
        check("config/GTNotLeisure/hud.json".equals(file.get(null)), "HUD layout saves to hud.json");

        near(0, HudLayout.anchor(10, 300), "left third anchors left");
        near(0.5, HudLayout.anchor(150, 300), "middle third anchors centre");
        near(1, HudLayout.anchor(290, 300), "right third anchors right");

        HudLayout.Entry entry = new HudLayout.Entry();
        HudLayout.move(entry, 460, 250, W, H);
        var moved = HudLayout.placement(GAUGE, entry, W, H, 1F);
        near(460, moved.x(), "dragged x round trip");
        near(250, moved.y(), "dragged y round trip");
        var wider = HudLayout.placement(GAUGE, entry, 960, 540, 1F);
        near(940, wider.x(), "corner element keeps its distance to the right edge");
        near(520, wider.y(), "corner element keeps its distance to the bottom edge");
        HudLayout.move(entry, 5000, -5000, W, H);
        var clamped = HudLayout.placement(GAUGE, entry, W, H, 1F);
        near(480 - GAUGE.maxX, clamped.x(), "element kept inside the right edge");
        near(-GAUGE.minY, clamped.y(), "element kept inside the top edge");

        near(HudLayout.MIN_SCALE, HudLayout.clampScale(0.1F), "scale floor");
        near(HudLayout.MAX_SCALE, HudLayout.clampScale(9F), "scale ceiling");
        near(1, HudLayout.clampScale(Float.NaN), "scale NaN falls back");
        near(HudLayout.MIN_OPACITY, HudLayout.clampOpacity(0F), "opacity floor keeps the element visible");
        near(1, HudLayout.clampOpacity(5F), "opacity ceiling");
        near(1, HudLayout.clampOpacity(Float.NaN), "opacity NaN falls back");

        HudLayout.reset();
        HudLayout.move(METER, 100, 50, W, H);
        HudLayout.rescale(METER, 3);
        HudLayout.toggle(ARC);
        HudLayout.setOpacity(METER, 0.55F);
        HudLayout.setColor(METER, HudColor.THEME);
        HudLayout.setTiming(METER, HudTiming.CHANGED);
        HudLayout.setIdleSeconds(METER, 7F);
        HudLayout.setIdleAlpha(METER, 0.25F);
        HudLayout.setOn(GAUGE, LABEL, false);
        JsonObject saved = saved();
        JsonObject future = new JsonObject();
        future.addProperty("scale", 2.0);
        future.addProperty("glow", "neon");
        saved.getAsJsonObject("elements")
            .add("future_element", future);
        saved.getAsJsonObject("elements")
            .getAsJsonObject(GAUGE.id)
            .addProperty("futureField", 5);
        HudLayout.read(json(saved.toString()));
        var meter = HudLayout.entry(METER);
        check(meter.moved, "moved flag survives saving");
        near(1.3, meter.scale, "element scale survives saving");
        near(
            100,
            HudLayout.placement(METER, meter, W, H, 1F)
                .x(),
            "position survives saving");
        check(HudLayout.hidden(ARC), "hidden flag survives saving");
        check(!HudLayout.hidden(GAUGE), "untouched element stays visible");
        near(0.55, HudLayout.opacity(METER), "opacity survives saving");
        check(HudLayout.color(METER) == HudColor.THEME, "color mode survives saving");
        check(HudLayout.timing(METER) == HudTiming.CHANGED, "display timing survives saving");
        near(7, HudLayout.idleSeconds(METER), "stay time survives saving");
        near(0.25, HudLayout.idleAlpha(METER), "faded opacity survives saving");
        check(!HudLayout.on(GAUGE, LABEL), "element option survives saving");
        check(
            HudLayout.color(GAUGE) == HudColor.ACCENT && HudLayout.timing(GAUGE) == HudTiming.CHANGED,
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
        JsonObject savedGauge = resaved.getAsJsonObject("elements")
            .getAsJsonObject(GAUGE.id);
        check(
            savedGauge != null && savedGauge.has("futureField")
                && savedGauge.get("futureField")
                    .getAsInt() == 5,
            "unknown fields of a registered element survive saving");

        HudLayout.read(
            json(
                "{\"elements\":{\"" + GAUGE.id
                    + "\":{\"moved\":true,\"x\":\"abc\",\"y\":5,\"anchorX\":0.3,\"scale\":99,"
                    + "\"opacity\":-1,\"color\":\"rainbow\",\"timing\":\"sometimes\",\"idleSeconds\":999,"
                    + "\"idleAlpha\":\"dim\",\"style\":\"fancy\",\"options\":{\""
                    + LABEL
                    + "\":\"yes\"}},\"odd\":5}}"));
        var repaired = HudLayout.entry(GAUGE);
        check(!repaired.moved, "unreadable position falls back to the default place");
        near(0.5, repaired.anchorX, "unknown anchor falls back to centre");
        near(HudLayout.MAX_SCALE, repaired.scale, "oversized scale clamped");
        near(HudLayout.MIN_OPACITY, repaired.opacity, "negative opacity clamped");
        check(HudLayout.color(GAUGE) == HudColor.ACCENT, "unknown color mode falls back to the element default");
        check(HudLayout.timing(GAUGE) == HudTiming.CHANGED, "unknown timing falls back to the element default");
        near(HudLayout.MAX_IDLE_SECONDS, HudLayout.idleSeconds(GAUGE), "oversized stay time clamped");
        near(0.35, HudLayout.idleAlpha(GAUGE), "unreadable faded opacity falls back to the element default");
        check(
            HudElement.STANDARD_STYLE.id()
                .equals(HudLayout.style(GAUGE)),
            "unknown style falls back to the default style");
        check(HudLayout.on(GAUGE, LABEL), "option of the wrong type falls back");
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

        boolean enabled = GTNLLibConfig.enableHud;
        float globalScale = GTNLLibConfig.hudScale;
        try {
            GTNLLibConfig.hudScale = 2F;
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
            GTNLLibConfig.hudScale = Float.NaN;
            near(
                2.25,
                HudLayout.place(equipped, W, H)
                    .scale(),
                "invalid global HUD scale falls back to 1");
            GTNLLibConfig.enableHud = false;
            check(!HudLayout.enabled(), "HUD switch comes from the config");
            check(HudLayout.place(equipped, W, H) == null, "HUD switch off hides an element in use");
            check(HudLayout.place(always, W, H) == null, "HUD switch off hides an element without conditions");
            check(
                HudRegistry.candidates(false)
                    .contains(always),
                "editor still lists elements with the HUD switch off");
        } finally {
            GTNLLibConfig.enableHud = enabled;
            GTNLLibConfig.hudScale = globalScale;
            HudLayout.reset();
        }
        check(HudLayout.place(always, W, H) != null, "HUD switch on draws again");
    }

    private static void hudTiming() {
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
    }

    private static void hudSettings() {
        check(
            HudColor.guide() == 0x80FFFFFF && HudColor.selection() == 0xFFFFFFFF,
            "hud editor framework colors are neutral");
        Choice round = new Choice("round", "gtnllib.hud.style.contract_round");
        HudElement dual = box("contract_dual").style(round)
            .color(HudColor.THEME)
            .option(HudOption.toggle("glow", "gtnllib.hud.option.contract_glow", true))
            .option(HudOption.number("size", "gtnllib.hud.option.contract_size", 1, 3, 4, 2))
            .option(
                HudOption.choice(
                    "shape",
                    "gtnllib.hud.option.contract_shape",
                    "hex",
                    new Choice("hex", "gtnllib.hud.option.contract_hex"),
                    new Choice("star", "gtnllib.hud.option.contract_star")))
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
        HudElement single = box("contract_single").timing(HudTiming.ACTIVE)
            .build();
        check(
            !single.settings(HudTiming.ACTIVE)
                .contains(Setting.STYLE),
            "a single style shows no style setting");
        HudLayout.setColor(single, HudColor.ACCENT);
        check(HudLayout.color(single) == null, "an element without colors ignores a color mode");
        HudLayout.reset();

        String accentLang = HudColor.ACCENT.lang();
        LivePalette palette = new LivePalette(() -> "gtnllib:contract_missing", () -> true);
        try {
            check(HudColor.THEME.rgb() == 0xFFFFFF, "without a palette both color modes are neutral");
            palette.accentFrom(() -> 0xFFFFFFFF);
            HudColor.source(palette, "gtnllib.hud.color.contract_glow");
            check(HudColor.ACCENT.rgb() == 0xFFFFFF, "follow accent uses the raw accent color, even pure white");
            palette.accentFrom(() -> 0xFF123456);
            check(HudColor.ACCENT.rgb() == 0x123456, "follow accent tracks the accent color");
            palette.accentFrom(() -> LivePalette.NO_ACCENT);
            check(HudColor.ACCENT.rgb() == HudColor.THEME.rgb(), "follow accent without an accent uses the theme");
            check(
                "gtnllib.hud.color.contract_glow".equals(HudColor.ACCENT.lang()),
                "follow accent label comes from its source");
            check("gtnllib.hud.color.theme".equals(HudColor.THEME.lang()), "follow theme label is the framework's");
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

    private static void hudSources() throws IOException {
        String editor = compact(read(SOURCES.resolve("client/hud/HudEditor.java"))),
            overlay = compact(read(SOURCES.resolve("client/hud/HudOverlay.java")));
        check(
            editor.contains("publicstaticvoidopen(){pending=true;}") && overlay.contains("HudEditor.openPending()")
                && overlay.contains("TickEvent.Phase.END"),
            "the HUD editor opens after the current frame, so MUI2 click callbacks can open it");
    }
}
