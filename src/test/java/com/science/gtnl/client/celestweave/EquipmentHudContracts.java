package com.science.gtnl.client.celestweave;

import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.compact;
import static com.gtnllib.GTNLLibContractTest.near;
import static com.gtnllib.GTNLLibContractTest.read;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import net.minecraft.util.StatCollector;

import com.gtnllib.client.hud.HudColor;
import com.gtnllib.client.hud.HudElement;
import com.gtnllib.client.hud.HudElement.Setting;
import com.gtnllib.client.hud.HudLayout;
import com.gtnllib.client.hud.HudRegistry;
import com.gtnllib.client.hud.HudTiming;
import com.science.gtnl.client.railgun.RailgunHudRenderer;
import com.science.gtnl.config.ClientConfig;

public class EquipmentHudContracts {

    private static final int W = 480, H = 270;

    static void run() throws Exception {
        hud();
        hudLayout();
        hudTiming();
        hudSettings();
        hudSources();
    }

    private static void hud() {
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

    private static void hudLayout() {
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
    }

    private static void hudTiming() {
        check(CelestweaveHudElement.CORE.hud.defaultTiming() == HudTiming.CHANGED, "energy core fades after changes");
        near(3, CelestweaveHudElement.CORE.hud.defaultIdleSeconds(), "energy core stays three seconds as before");
        near(0.35, CelestweaveHudElement.CORE.hud.defaultIdleAlpha(), "energy core rests at 35% as before");
        for (CelestweaveHudElement element : new CelestweaveHudElement[] { CelestweaveHudElement.SHIELD_RING,
            CelestweaveHudElement.DASH_ARC, CelestweaveHudElement.ALTIMETER })
            check(element.hud.defaultTiming() == HudTiming.ACTIVE, element + " shows only while in use as before");
        for (CelestweaveHudElement element : CelestweaveHudElement.values())
            check(element.hud.defaultColor() == HudColor.ACCENT, element + " follows the armor glow by default");
        check(RailgunHudRenderer.CHARGE.defaultTiming() == HudTiming.ACTIVE, "lance charge shows only while charging");
        for (String removed : new String[] { "celestweaveHudIdleSeconds", "celestweaveHudIdleAlpha", "enableHud",
            "hudScale", "equipmentTheme" })
            check(
                Arrays.stream(ClientConfig.class.getFields())
                    .noneMatch(
                        field -> field.getName()
                            .equals(removed)),
                "config " + removed + " is no longer a GTNL setting");
    }

    private static void hudSettings() {
        HudElement charge = RailgunHudRenderer.CHARGE;
        check(!charge.colorable(), "lance charge keeps its charge colors");
        check(
            !charge.settings(HudTiming.ACTIVE)
                .contains(Setting.COLOR),
            "lance charge shows no color setting");
        check(
            CelestweaveHudElement.CORE.hud.settings(HudTiming.CHANGED)
                .contains(Setting.COLOR),
            "armor elements show the color setting");
        HudLayout.reset();
        check(
            CelestweaveHudElement.CORE.hud.options()
                .size() == 1 && HudLayout.on(CelestweaveHudElement.CORE.hud, CelestweaveHudElement.READOUT),
            "energy core shows its readout by default");
        HudLayout.reset();
    }

    private static void hudSources() throws IOException {
        check(
            !read(SOURCES.resolve("common/gui/equipment/EquipmentGui.java")).contains("HudEditor")
                && compact(read(SOURCES.resolve("client/equipment/EquipmentWheelScreen.java")))
                    .contains("button(HUD,HudEditor::open,"),
            "the HUD editor opens only from the armor wheel, not from the settings panels");
        check(
            compact(read(SOURCES.resolve("client/celestweave/CelestweaveThemeAccent.java")))
                .contains("HudColor.source(EquipmentTheme.LIVE,"),
            "the armor glow is the HUD's follow-armor color");
        check(
            !compact(read(SOURCES.resolve("client/celestweave/CelestweaveHud.java"))).contains("look.glowColor"),
            "the armor HUD takes its color from the player's color mode");
    }
}
