package com.science.gtnl.client.portalgun;

import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.compactDrawable;
import static com.gtnllib.GTNLLibContractTest.field;
import static com.gtnllib.GTNLLibContractTest.near;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.RESOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnllib.config.GTNLLibConfig;
import com.gtnllib.gui.settings.SettingControls;
import com.gtnllib.gui.settings.SettingControls.Control;
import com.gtnllib.gui.settings.SettingControls.Slider;
import com.gtnllib.gui.widget.StepSlider;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.portalgun.PortalGunPortalFx.Arc;
import com.science.gtnl.client.portalgun.PortalGunPortalFx.Dot;
import com.science.gtnl.client.portalgun.PortalGunPortalFx.Pose;
import com.science.gtnl.client.portalgun.PortalGunPortalFx.Shape;
import com.science.gtnl.common.entity.EntityPortalGunOrb;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.common.gui.portalgun.PortalGunLookScreen;
import com.science.gtnl.common.portalgun.PortalGunGeometry;
import com.science.gtnl.common.render.entity.PortalGunDoorRender;
import com.science.gtnl.common.render.entity.PortalGunPortalRender;
import com.science.gtnl.common.render.entity.PortalGunSpriteRender;
import com.science.gtnl.common.render.entity.PortalGunVortexRender;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;
import com.science.gtnl.utils.enums.PortalGunPortalEffect.Kind;

public class PortalGunLookContracts {

    private PortalGunLookContracts() {}

    public static void run() throws IOException {
        float widest = 0;
        for (int i = 0; i <= 100; i++) {
            float t = i / 100F;
            check(
                PortalGunSpriteRender.heightScale(t, 0) <= 1 + 1.0E-6,
                "the opening portal never grows taller than when open, so it does not dip into the floor, at " + t);
            check(
                PortalGunSpriteRender.heightScale(1, t) <= 1 + 1.0E-6
                    && PortalGunSpriteRender.widthScale(1, t) <= 1 + 1.0E-6,
                "the closing portal only shrinks at " + t);
            widest = Math.max(widest, PortalGunSpriteRender.widthScale(t, 0));
        }
        check(widest > 1.05F, "the opening portal still pops sideways, peak " + widest);
        near(1, PortalGunSpriteRender.heightScale(1, 0), "an open portal is drawn at full height");
        near(1, PortalGunSpriteRender.widthScale(1, 0), "an open portal is drawn at full width");
        near(0, PortalGunSpriteRender.heightScale(0, 0), "a new portal starts from nothing");
        near(0, PortalGunSpriteRender.heightScale(1, 1), "a closed portal is not drawn");
        portalDoorShape();
        portalDoorArcs();
        portalVortexShape();
        check(
            new MainConfig.Client().portalGunPortalEffect == PortalGunPortalEffect.PIXEL_SWIRL,
            "portals use the pixel swirl effect by default");
        Set<PortalGunPortalEffect> visited = EnumSet.noneOf(PortalGunPortalEffect.class);
        PortalGunPortalEffect cycled = PortalGunPortalEffect.PIXEL_SWIRL;
        for (int i = 0; i < PortalGunPortalEffect.values().length; i++) {
            visited.add(cycled);
            cycled = cycled.next();
        }
        check(
            cycled == PortalGunPortalEffect.PIXEL_SWIRL && visited.size() == PortalGunPortalEffect.values().length,
            "the wheel button steps through every portal effect and comes back");
        for (PortalGunPortalEffect effect : PortalGunPortalEffect.values()) portalEffect(effect);
        configEnumsStayLight(MainConfig.class);
        orbTrail();
        portalOpening();
        passAndClose();
        lookSettings();
        lookPage();
        shotArc();
    }

    private static void passAndClose() {
        MainConfig.Client defaults = new MainConfig.Client();
        near(0.12, defaults.portalGunPassJelly, "portals wobble 0.12 when something passes through by default");
        near(24, defaults.portalGunPassDotCount, "24 dots fly out when something passes through by default");
        near(0.2, defaults.portalGunClosePop, "closing portals pop 0.2 bigger by default");
        near(9, defaults.portalGunCloseTicks, "portals close in 9 ticks by default");
        try {
            Config.RangeInt range = MainConfig.Client.class.getField("portalGunCloseTicks")
                .getAnnotation(Config.RangeInt.class);
            check(
                EntityPortalGunPortal.CLOSE_LIMIT >= range.max()
                    && EntityPortalGunPortal.CLOSE_LIMIT >= PortalGunDoorRender.DOOR_OPEN_TICKS,
                "a closing portal lives long enough for the slowest close to play out");
        } catch (NoSuchFieldException e) {
            check(false, "the close time setting exists: " + e);
        }
        for (PortalGunPortalEffect effect : PortalGunPortalEffect.values()) {
            check(
                PortalGunPortalFx.pose(effect, Float.POSITIVE_INFINITY, 0, defaults)
                    .equals(Pose.REST),
                effect + " stands still when nothing passed through it");
            check(
                PortalGunPortalFx.pose(effect, -1, 0, defaults)
                    .equals(Pose.REST)
                    && PortalGunPortalFx.pose(effect, PortalGunPortalFx.JELLY_TICKS, 0, defaults)
                        .equals(Pose.REST),
                effect + " is still before a pass and settles after it");
            double early = 0, late = 0;
            for (int i = 0; i < 400; i++) {
                double since = i / 10.0;
                Pose pose = PortalGunPortalFx.pose(effect, since, 0, defaults);
                double off = Math
                    .max(Math.abs(pose.sx() - 1), Math.max(Math.abs(pose.sy() - 1), Math.abs(pose.push())));
                check(
                    off <= defaults.portalGunPassJelly + 1.0E-9,
                    effect + " wobbles no more than the setting at " + since);
                if (since < PortalGunPortalFx.JELLY_PERIOD) early = Math.max(early, off);
                else if (since >= 3 * PortalGunPortalFx.JELLY_PERIOD) late = Math.max(late, off);
            }
            check(early > defaults.portalGunPassJelly / 2, effect + " visibly wobbles right after a pass, " + early);
            check(late < early / 10, effect + " wobble dies away, " + late);
            check(
                PortalGunPortalFx.pose(effect, 1, 0.6F, defaults)
                    .push() == 0,
                effect + " stops wobbling once it starts closing");
            List<Dot> dots = PortalGunPortalFx.passDots(effect, 42, defaults);
            near(defaults.portalGunPassDotCount, dots.size(), effect + " throws the set number of pass dots");
            Shape full = PortalGunPortalFx.shape(effect, Float.MAX_VALUE / 2, 0);
            boolean face = true, out = true, soon = true;
            for (Dot dot : dots) {
                double x = dot.origin()
                    .x() / full.halfWidth(),
                    y = dot.origin()
                        .y() / full.halfHeight();
                face &= Math.hypot(x, y) <= PortalGunPortalFx.PASS_REACH + 1.0E-9;
                out &= dot.velocity()
                    .z() > 0;
                soon &= dot.born() >= 0 && dot.born() <= PortalGunPortalFx.PASS_SPREAD;
            }
            check(face, effect + " pass dots start on the portal face");
            check(out, effect + " pass dots fly out of the portal");
            check(soon, effect + " pass dots come out right as something passes");
            check(
                !dots.get(0)
                    .origin()
                    .equals(
                        PortalGunPortalFx.passDots(effect, 43, defaults)
                            .get(0)
                            .origin()),
                effect + " throws different dots on each pass");
        }
        Pose swirl = PortalGunPortalFx
            .pose(PortalGunPortalEffect.PIXEL_SWIRL, Float.POSITIVE_INFINITY, 0.25F, defaults);
        near(1 + defaults.portalGunClosePop, swirl.sy(), "a closing swirl first pops taller");
        near(1 + defaults.portalGunClosePop / 2, swirl.sx(), "and a little wider");
        check(PortalGunSpriteRender.heightScale(1, 0.25F) * swirl.sy() > 1.1, "the pop shows before the swirl shrinks");
        near(
            1,
            PortalGunPortalRender.shrink(PortalGunPortalRender.CLOSE_HOLD),
            "a closing portal keeps its size while it pops");
        near(0, PortalGunPortalRender.shrink(1), "then shrinks away");
        check(
            PortalGunPortalFx.pose(PortalGunPortalEffect.ELECTRIC_DOOR, Float.POSITIVE_INFINITY, 0.25F, defaults)
                .equals(Pose.REST),
            "the electric door does not pop when it closes");
        check(
            PortalGunPortalFx.closeTicks(PortalGunPortalEffect.ELECTRIC_DOOR, defaults)
                == PortalGunDoorRender.DOOR_OPEN_TICKS
                && PortalGunPortalFx.closeTicks(PortalGunPortalEffect.PIXEL_SWIRL, defaults)
                    == defaults.portalGunCloseTicks,
            "the electric door closes as long as it opens, the others in the set close time");
        for (int i = 0; i <= 64; i++) {
            float c = i / 64F;
            near(
                PortalGunDoorRender.doorWidth(1 - c, 0),
                PortalGunDoorRender.doorWidth(1, c),
                "the closing door plays its opening width backwards at " + c);
            near(
                PortalGunDoorRender.doorHeight(1 - c, 0),
                PortalGunDoorRender.doorHeight(1, c),
                "the closing door plays its opening height backwards at " + c);
        }
    }

    private static void lookSettings() {
        MainConfig.Client defaults = new MainConfig.Client();
        near(0.03, defaults.portalGunTrailWidth, "the trail starts 0.03 blocks wide by default");
        near(0.0125, defaults.portalGunTrailSpray, "trail dots drift sideways at 0.0125 by default");
        near(0.02, defaults.portalGunTrailGravity, "trail dots fall at 0.02 by default");
        near(0.028, defaults.portalGunTrailDotSize, "trail dots are 0.028 blocks by default");
        near(1, defaults.portalGunTrailCount, "one trail dot per spot by default");
        near(12, defaults.portalGunTrailDensity, "12 trail spots per block by default");
        near(1.5, defaults.portalGunTrailFallDelay, "trail dots float 1.5 ticks per block before falling by default");
        near(
            2,
            defaults.portalGunTrailCutRange,
            "trail dots within 2 blocks of the portal fade when it opens by default");
        near(4, defaults.portalGunTrailCutTicks, "the trail near the portal fades in 4 ticks by default");
        check(
            defaults.enablePortalGunPuffs && defaults.enablePortalGunRings
                && defaults.enablePortalGunSparks
                && defaults.enablePortalGunShowOthersFx,
            "puffs, rings, sparks and other players' effects are on by default");
        near(36, defaults.portalGunSparkCount, "36 landing sparks by default");
        near(15, defaults.portalGunHeldPitch, "the held gun tips 15 degrees by default");
        near(15, defaults.portalGunHeldYaw, "the held gun turns 15 degrees by default");
        near(0.15, defaults.portalGunHeldForward, "the held gun is pushed 0.15 forward by default");
        Map<String, String> english = langFile("en_US"), chinese = langFile("zh_CN");
        for (Field field : MainConfig.Client.class.getFields()) {
            if (!field.getName()
                .toLowerCase()
                .contains("portalgun")) continue;
            Config.LangKey key = field.getAnnotation(Config.LangKey.class);
            check(key != null, "the setting " + field.getName() + " has a lang key");
            if (key == null) continue;
            for (String lang : new String[] { key.value(), key.value() + ".tooltip" }) {
                check(english.containsKey(lang), "en_US.lang has " + lang);
                check(chinese.containsKey(lang), "zh_CN.lang has " + lang);
            }
            try {
                double value = ((Number) field.get(defaults)).doubleValue();
                Config.RangeDouble decimal = field.getAnnotation(Config.RangeDouble.class);
                Config.RangeInt whole = field.getAnnotation(Config.RangeInt.class);
                if (decimal != null) check(
                    value >= decimal.min() && value <= decimal.max(),
                    "the default of " + field.getName() + " is inside its range");
                if (whole != null) check(
                    value >= whole.min() && value <= whole.max(),
                    "the default of " + field.getName() + " is inside its range");
            } catch (ClassCastException | IllegalAccessException e) {
                check(
                    field.getType() == boolean.class || field.getType() == PortalGunPortalEffect.class,
                    "the setting " + field.getName() + " is a number, a switch or the portal effect");
            }
        }
        for (String server : new String[] { "orbgravity", "airopenticks" })
            for (String lang : new String[] { "gtnl.gui.config.general.item.portal_gun." + server,
                "gtnl.gui.config.general.item.portal_gun." + server + ".tooltip" }) {
                    check(english.containsKey(lang), "en_US.lang has " + lang);
                    check(chinese.containsKey(lang), "zh_CN.lang has " + lang);
                }
        for (String lang : new String[] { "item.gtnl.portal_gun.wheel.look", "item.gtnl.portal_gun.look.title",
            "item.gtnl.portal_gun.look.trail", "item.gtnl.portal_gun.look.effects", "item.gtnl.portal_gun.look.held",
            "item.gtnl.portal_gun.look.others", "item.gtnl.portal_gun.look.reset", "item.gtnl.portal_gun.look.hint" }) {
            check(english.containsKey(lang), "en_US.lang has " + lang);
            check(chinese.containsKey(lang), "zh_CN.lang has " + lang);
        }
    }

    private static void lookPage() {
        List<Control> controls = new ArrayList<>();
        try {
            Method method = PortalGunLookScreen.class.getDeclaredMethod("sections");
            method.setAccessible(true);
            for (Object section : (List<?>) method.invoke(null))
                for (Object control : (List<?>) accessor(section, "controls")) controls.add((Control) control);
        } catch (ReflectiveOperationException e) {
            check(false, "the look page's controls are readable: " + e);
            return;
        }
        Map<String, Control> shown = new HashMap<>();
        List<Field> settings = new ArrayList<>();
        try {
            for (Control control : controls) shown.put(control.lang(), control);
            for (Field field : MainConfig.Client.class.getFields()) if (field.getName()
                .toLowerCase()
                .contains("portalgun") && field.getType() != PortalGunPortalEffect.class) settings.add(field);
            settings.add(GTNLLibConfig.class.getField("maxWorldMotes"));
            for (Field field : settings) {
                Config.LangKey key = field.getAnnotation(Config.LangKey.class);
                Control control = key == null ? null : shown.get(key.value());
                check(control != null, "the look page shows the setting " + field.getName());
                if (!(control instanceof Slider slider)) continue;
                Config.RangeDouble decimal = field.getAnnotation(Config.RangeDouble.class);
                Config.RangeInt whole = field.getAnnotation(Config.RangeInt.class);
                check(
                    decimal != null ? slider.min() == decimal.min() && slider.max() == decimal.max()
                        : whole != null && slider.min() == whole.min() && slider.max() == whole.max(),
                    "the slider of " + field.getName() + " covers exactly the range the config allows");
            }
            check(
                shown.size() == settings.size(),
                "the look page shows only the portal gun look settings and the world mote cap, " + shown.keySet());
            sharedTag(controls);
            restoring(settings);
        } catch (ReflectiveOperationException e) {
            check(false, "the look page's controls are readable: " + e);
        }
    }

    private static void sharedTag(List<Control> controls) throws ReflectiveOperationException {
        String tooWide = null, undrawn = null;
        Locale saved = Locale.getDefault();
        // Why: German and many other locales write the decimal point as a comma.
        Locale.setDefault(Locale.GERMANY);
        try {
            int tag = SettingControls.tagWidth(controls);
            check(tag > 0, "every slider on the look page shares one value tag");
            for (Control control : controls) {
                if (!(control instanceof Slider slider)) continue;
                for (int i = 0; i <= slider.steps(); i++) {
                    String text = slider.format()
                        .apply(slider.min() + (slider.max() - slider.min()) * i / slider.steps());
                    if (tooWide == null && StepSlider.fittedTagWidth(text) > tag)
                        tooWide = text + " of " + slider.lang();
                    if (undrawn == null && !compactDrawable(text)) undrawn = text + " of " + slider.lang();
                }
            }
        } finally {
            Locale.setDefault(saved);
        }
        check(tooWide == null, "the shared value tag fits every slider value on the look page, not " + tooWide);
        check(
            undrawn == null,
            "the compact font draws every slider value on the look page, even with a comma locale, not " + undrawn);
    }

    private static void restoring(List<Field> settings) throws ReflectiveOperationException {
        MainConfig.Client defaults = new MainConfig.Client();
        Map<Field, Object> initial = new HashMap<>();
        for (Field field : settings) {
            boolean library = field.getDeclaringClass() == GTNLLibConfig.class;
            Object owner = library ? null : MainConfig.client;
            initial.put(field, library ? GTNLLibConfig.DEFAULT_MAX_WORLD_MOTES : field.get(defaults));
            if (field.getType() == boolean.class) field.setBoolean(owner, !field.getBoolean(owner));
            else if (field.getType() == int.class) field.setInt(owner, field.getInt(owner) + 1);
            else field.setDouble(owner, field.getDouble(owner) + 0.5);
        }
        Method method = PortalGunLookScreen.class.getDeclaredMethod("restoreDefaults");
        method.setAccessible(true);
        method.invoke(null);
        for (Field field : settings) {
            Object owner = field.getDeclaringClass() == GTNLLibConfig.class ? null : MainConfig.client;
            check(
                initial.get(field)
                    .equals(field.get(owner)),
                "restoring defaults on the look page resets " + field.getName());
        }
    }

    private static Object accessor(Object record, String name) throws ReflectiveOperationException {
        Method method = record.getClass()
            .getDeclaredMethod(name);
        method.setAccessible(true);
        return method.invoke(record);
    }

    private static void shotArc() {
        MainConfig.Item.PortalGun defaults = new MainConfig.Item.PortalGun();
        check(defaults.orbGravity > 0, "the portal gun shot falls on an arc by default");
        near(5, defaults.airOpenTicks, "the shot opens a portal in the air after 5 ticks by default");
        double saved = MainConfig.item.portal_gun.orbGravity;
        try {
            Field unsafeField = Class.forName("sun.misc.Unsafe")
                .getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            Object orb = unsafe.getClass()
                .getMethod("allocateInstance", Class.class)
                .invoke(unsafe, EntityPortalGunOrb.class);
            Method gravity = EntityPortalGunOrb.class.getDeclaredMethod("getGravityVelocity");
            gravity.setAccessible(true);
            MainConfig.item.portal_gun.orbGravity = 0.125;
            near(0.125, (float) gravity.invoke(orb), "the shot falls as fast as the server config says");
            MainConfig.item.portal_gun.orbGravity = 0;
            near(0, (float) gravity.invoke(orb), "a gravity of 0 makes the shot fly straight");
        } catch (ReflectiveOperationException e) {
            check(false, "the shot's gravity is readable: " + e);
        } finally {
            MainConfig.item.portal_gun.orbGravity = saved;
        }
    }

    private static Map<String, String> langFile(String name) {
        Map<String, String> entries = new HashMap<>();
        try {
            for (String line : Files
                .readAllLines(RESOURCES.resolve("lang/" + name + ".lang"), StandardCharsets.UTF_8)) {
                int split = line.indexOf('=');
                if (split > 0) entries.put(line.substring(0, split), line.substring(split + 1));
            }
        } catch (IOException e) {
            check(false, "the " + name + " lang file is readable: " + e);
        }
        return entries;
    }

    private static void portalOpening() {
        MainConfig.Client defaults = new MainConfig.Client();
        near(0.05, defaults.portalGunOpenSpread, "opening dots start 0.05 blocks off the edge by default");
        near(0.005, defaults.portalGunOpenSpray, "opening dots drift outward at 0.005 by default");
        near(0.17, defaults.portalGunOpenJet, "opening dots shoot out at 0.17 by default");
        near(0.96, defaults.portalGunOpenDrag, "opening dots keep 0.96 of their speed each tick by default");
        near(0.021, defaults.portalGunOpenGravity, "opening dots fall at 0.021 by default");
        near(0.025, defaults.portalGunOpenDotSize, "opening dots are 0.025 blocks by default");
        near(0, defaults.portalGunOpenHover, "opening dots move right away by default");
        near(1, defaults.portalGunOpenCount, "one opening dot per spot by default");
        near(6, defaults.portalGunOpenDensity, "6 opening dot spots per block of edge by default");
        check(!defaults.enablePortalGunOpenWholeFace, "opening dots come from the edge only by default");
        near(2, defaults.portalGunOpenArcRate, "2 opening arcs per tick by default");
        near(11.5, defaults.portalGunOpenArcTicks, "opening arcs crackle 11.5 ticks by default");
        near(0.75, defaults.portalGunArcForkChance, "three arcs in four fork by default");
        near(1.5, defaults.portalGunArcHop, "arcs jump every 1.5 ticks by default");
        near(0.006, defaults.portalGunArcWidth, "arcs have a 0.006 block core by default");
        near(0.8, defaults.portalGunOpenArcBend, "opening arcs bend 0.8 by default");
        near(0.6, defaults.portalGunOpenArcRise, "opening arcs rise 0.6 of their length by default");
        near(3, defaults.portalGunRingArcRate, "3 ring arc runs a second by default");
        near(0.5, defaults.portalGunCenterArcRate, "half a middle arc burst a second by default");
        for (PortalGunPortalEffect effect : PortalGunPortalEffect.values()) openingDots(effect, defaults);
        for (PortalGunPortalEffect effect : PortalGunPortalEffect.values()) openingArcs(effect, defaults);
        sharpArcs(defaults);
        Random r = new Random(5);
        List<Arc> arcs = new ArrayList<>();
        PortalGunPortalFx.bolt(arcs, r, new Vec3d(-0.3, 0, 0.03), new Vec3d(0.3, 0.1, 0.03), 0.1, 0.3, 0, false, 1);
        check(arcs.size() == 1, "an arc never forks when forks are off");
        List<Vec3d> points = arcs.get(0)
            .points();
        check(points.size() == 9, "an arc is a zigzag of 8 straight pieces, points " + points.size());
        check(
            points.get(0)
                .distanceToSqr(new Vec3d(-0.3, 0, 0.03)) < 1.0E-12
                && points.get(points.size() - 1)
                    .distanceToSqr(new Vec3d(0.3, 0.1, 0.03)) < 1.0E-12,
            "an arc runs exactly between the two points it joins");
        arcs.clear();
        PortalGunPortalFx.bolt(arcs, r, new Vec3d(-0.3, 0, 0.03), new Vec3d(0.3, 0.1, 0.03), 0.1, 0.3, 1, false, 1);
        check(
            arcs.size() == 2 && arcs.get(1)
                .fork(),
            "an arc always forks when the fork chance is 1");
    }

    private static void openingDots(PortalGunPortalEffect effect, MainConfig.Client defaults) {
        Shape full = PortalGunPortalFx.shape(effect, 1000, 0);
        int spots = Math.max(1, (int) Math.round(full.perimeter() * defaults.portalGunOpenDensity));
        List<Dot> dots = PortalGunPortalFx.dots(effect, 42, defaults);
        near(spots * defaults.portalGunOpenCount, dots.size(), effect + " throws a dot per spot along its edge");
        boolean timely = true, outward = true, onEdge = true;
        int faced = 0;
        for (Dot dot : dots) {
            timely &= dot.born() >= 0 && dot.born() <= PortalGunPortalFx.BURST;
            outward &= dot.velocity()
                .z() > 0
                && dot.origin()
                    .z() > 0;
            Shape then = PortalGunPortalFx.shape(effect, (float) dot.born(), 0);
            if (!faceOpen(then)) continue;
            faced++;
            onEdge &= reach(then, dot.origin()) > 0.99;
        }
        check(timely, effect + " throws every dot within the first " + PortalGunPortalFx.BURST + " ticks");
        check(outward, effect + " throws every dot out of the front of the portal");
        check(
            onEdge && (effect.kind != Kind.SPRITE || faced > 0),
            effect + " throws dots only from its edge when the whole-face switch is off");
        MainConfig.Client face = new MainConfig.Client();
        face.enablePortalGunOpenWholeFace = true;
        int inside = 0;
        for (long seed = 0; seed < 20; seed++) for (Dot dot : PortalGunPortalFx.dots(effect, seed, face)) {
            Shape then = PortalGunPortalFx.shape(effect, (float) dot.born(), 0);
            if (faceOpen(then) && reach(then, dot.origin()) < 0.5) inside++;
        }
        if (effect.kind == Kind.SPRITE)
            check(inside > 0, effect + " throws dots from the middle too when the whole-face switch is on");
    }

    private static boolean faceOpen(Shape shape) {
        return Math.min(shape.halfWidth(), shape.halfHeight()) >= PortalGunPortalFx.BALL;
    }

    private static double reach(Shape shape, Vec3d point) {
        double x = Math.abs(point.x()) / shape.halfWidth(), y = Math.abs(point.y()) / shape.halfHeight();
        return shape.rect() ? Math.max(x, y) : Math.hypot(x, y);
    }

    private static void openingArcs(PortalGunPortalEffect effect, MainConfig.Client defaults) {
        double hop = defaults.portalGunArcHop;
        MainConfig.Client quick = new MainConfig.Client();
        quick.portalGunArcHop = PortalGunPortalFx.RAMP / 3;
        double late = 0, first = 0, going = 0;
        int seeds = 400;
        for (long seed = 0; seed < seeds; seed++) {
            late += mainArcs(PortalGunPortalFx.openingArcs(effect, 3 * hop, seed, defaults));
            first += mainArcs(PortalGunPortalFx.openingArcs(effect, 0, seed, quick));
            going += mainArcs(PortalGunPortalFx.openingArcs(effect, PortalGunPortalFx.RAMP, seed, quick));
        }
        late /= seeds;
        first /= seeds;
        going /= seeds;
        double expected = defaults.portalGunOpenArcRate * hop;
        check(
            Math.abs(late - expected) < 0.25 * expected,
            effect + " crackles about " + expected + " arcs per jump once going, " + late);
        check(
            first < 0.5 * going,
            effect + " starts with few arcs when they jump fast, instead of all at once, " + first + " then " + going);
        check(
            PortalGunPortalFx.openingArcs(effect, defaults.portalGunOpenArcTicks, 3, defaults)
                .isEmpty()
                && PortalGunPortalFx.openingArcs(effect, -0.5, 3, defaults)
                    .isEmpty(),
            effect + " crackles only while the portal opens");
        MainConfig.Client noForks = new MainConfig.Client();
        noForks.portalGunArcForkChance = 0;
        boolean forked = false;
        for (long seed = 0; seed < 50; seed++)
            for (Arc arc : PortalGunPortalFx.openingArcs(effect, 3 * hop, seed, noForks)) forked |= arc.fork();
        check(!forked, effect + " opening arcs never fork when forks are off");
        int opened = PortalGunPortalFx.openedAt(effect);
        boolean early = false;
        for (long seed = 0; seed < 200; seed++)
            early |= !PortalGunPortalFx.idleArcs(effect, opened - 0.5, seed, defaults)
                .isEmpty();
        check(!early, effect + " keeps its ring and middle quiet until it is fully open");
        MainConfig.Client ring = new MainConfig.Client();
        ring.portalGunRingArcRate = 10;
        ring.portalGunCenterArcRate = 0;
        ring.portalGunArcForkChance = 0;
        Shape inner = PortalGunPortalFx.innerRing(effect, PortalGunPortalFx.shape(effect, 40, 0));
        int runs = 0;
        boolean onRing = true;
        for (long seed = 0; seed < 50; seed++) for (Arc arc : PortalGunPortalFx.idleArcs(effect, 40, seed, ring)) {
            runs++;
            onRing &= Math.abs(
                reach(
                    inner,
                    arc.points()
                        .get(0))
                    - 1)
                < 1.0E-6;
        }
        check(runs > 0, effect + " runs arcs round its inner ring once open");
        check(onRing, effect + " ring arcs jump between points on its inner ring");
        MainConfig.Client quiet = new MainConfig.Client();
        quiet.portalGunRingArcRate = 0;
        quiet.portalGunCenterArcRate = 0;
        boolean any = false;
        for (long seed = 0; seed < 50; seed++) any |= !PortalGunPortalFx.idleArcs(effect, 40, seed, quiet)
            .isEmpty();
        check(!any, effect + " stays quiet once open when ring and middle arcs are off");
    }

    private static void sharpArcs(MainConfig.Client defaults) {
        Vec3d a = new Vec3d(-0.3, 0, 0.03), b = new Vec3d(0.3, 0.1, 0.03);
        List<Arc> arcs = new ArrayList<>();
        PortalGunPortalFx.sharpBolt(arcs, new Random(9), a, b, 0.2, 0.8, 0.6, 0, false, 1);
        List<Vec3d> points = arcs.get(0)
            .points();
        check(
            arcs.size() == 1 && points.size() == 9
                && points.get(0)
                    .distanceToSqr(a) < 1.0E-12
                && points.get(8)
                    .distanceToSqr(b) < 1.0E-12,
            "a sharp opening arc is 8 straight pieces running exactly between its two points");
        arcs.clear();
        PortalGunPortalFx.sharpBolt(arcs, new Random(9), a, b, 0, 0, 0, 0, false, 1);
        double off = 0;
        for (Vec3d point : arcs.get(0)
            .points()) off = Math.max(off, Math.max(sideways(a, b, point), Math.abs(point.z() - 0.03)));
        check(off < 1.0E-9, "an opening arc with no bend and no rise is a straight line on the portal, off by " + off);
        MainConfig.Client flat = new MainConfig.Client(), tame = new MainConfig.Client();
        flat.portalGunOpenArcRise = 0;
        tame.portalGunOpenArcBend = 0.3;
        double peak = sharpness(defaults, true), lying = sharpness(flat, true);
        check(peak > 0.35, "opening arcs peak out of the portal by about 0.6 of their length, " + peak);
        check(lying < 1.0E-9, "opening arcs lie on the portal when they do not rise, " + lying);
        double bent = sharpness(defaults, false), straighter = sharpness(tame, false);
        check(
            bent > 1.8 * straighter,
            "harder bend swings opening arcs further sideways, " + bent + " against " + straighter);
    }

    private static double sharpness(MainConfig.Client settings, boolean height) {
        double sum = 0;
        int count = 0;
        for (long seed = 0; seed < 300; seed++)
            for (Arc arc : PortalGunPortalFx.openingArcs(PortalGunPortalEffect.PIXEL_SWIRL, 4.5, seed, settings)) {
                if (arc.fork() || arc.straightOut()) continue;
                List<Vec3d> points = arc.points();
                Vec3d first = points.get(0), last = points.get(points.size() - 1);
                double span = Math.hypot(last.x() - first.x(), last.y() - first.y()), most = 0;
                for (Vec3d point : points)
                    most = Math.max(most, height ? point.z() - first.z() : sideways(first, last, point));
                sum += most / span;
                count++;
            }
        return sum / Math.max(1, count);
    }

    private static double sideways(Vec3d a, Vec3d b, Vec3d point) {
        double dx = b.x() - a.x(), dy = b.y() - a.y();
        return Math.abs(dx * (point.y() - a.y()) - dy * (point.x() - a.x())) / Math.hypot(dx, dy);
    }

    private static int mainArcs(List<Arc> arcs) {
        int count = 0;
        for (Arc arc : arcs) if (!arc.fork()) count++;
        return count;
    }

    private static void orbTrail() {
        double depth = 0.9, blend = PortalGunOrbTrail.MUZZLE_BLEND;
        for (int i = 1; i < 10; i++) near(
            1 / depth,
            PortalGunOrbTrail.muzzleWeight(depth * i / 10, depth) / (depth * i / 10),
            "before the muzzle the orb is drawn on the line from the eye through the muzzle");
        near(1, PortalGunOrbTrail.muzzleWeight(depth, depth), "the drawn orb leaves the gun at its muzzle");
        double last = 1;
        for (int i = 0; i <= 100; i++) {
            double weight = PortalGunOrbTrail.muzzleWeight(depth + (blend - depth) * i / 100, depth);
            check(weight <= last + 1.0E-9, "past the muzzle the drawn orb only closes in on its real path, at " + i);
            last = weight;
        }
        near(0, last, "the drawn orb is back on its real path after the blend distance");
        near(0, PortalGunOrbTrail.muzzleWeight(50, depth), "far shots are drawn on their real path");
        check(
            PortalGunOrbTrail.MIN_MUZZLE_DEPTH > 0 && PortalGunOrbTrail.MIN_MUZZLE_DEPTH < blend / 2,
            "the muzzle depth is kept between the eye and halfway to the blend distance");
        double reach;
        try {
            Field speed = EntityPortalGunOrb.class.getDeclaredField("SPEED");
            speed.setAccessible(true);
            reach = new MainConfig.Item.PortalGun().airOpenTicks * speed.getFloat(null);
        } catch (ReflectiveOperationException e) {
            check(false, "the orb's speed is readable: " + e);
            return;
        }
        check(PortalGunOrbTrail.MUZZLE_BLEND < reach, "the orb rejoins its real path before it can open in the air");
        for (double ring : PortalGunOrbTrail.RING_DISTANCES) check(
            ring > PortalGunOrbTrail.TRAIL_START && ring < reach,
            "the ring at " + ring + " blocks shows after the trail starts and before the orb can open in the air");
    }

    private static void portalDoorShape() {
        float slit = PortalGunDoorRender.DOOR_SLIT, lastWidth = 0, lastHeight = 0;
        near(0, PortalGunDoorRender.doorWidth(0, 0), "a door portal starts from nothing");
        for (int i = 0; i <= 100; i++) {
            float t = i / 100F, width = PortalGunDoorRender.doorWidth(t, 0),
                height = PortalGunDoorRender.doorHeight(t, 0);
            if (t <= slit) near(0, height, "a door opens as a flat slit first, at " + t);
            check(width >= lastWidth && height >= lastHeight, "an opening door never shrinks, at " + t);
            lastWidth = width;
            lastHeight = height;
        }
        near(1, PortalGunDoorRender.doorWidth(slit, 0), "the slit reaches full width before the door grows");
        near(1, PortalGunDoorRender.doorWidth(1, 0), "an open door has its full width");
        near(1, PortalGunDoorRender.doorHeight(1, 0), "an open door has its full height");
        for (int i = 0; i <= 100; i++) {
            float t = i / 100F, width = PortalGunDoorRender.doorWidth(1, t),
                height = PortalGunDoorRender.doorHeight(1, t);
            check(width <= lastWidth && height <= lastHeight, "a closing door never grows, at " + t);
            lastWidth = width;
            lastHeight = height;
        }
        near(0, PortalGunDoorRender.doorHeight(1, 1 - slit), "a closing door first folds back into a slit");
        near(1, PortalGunDoorRender.doorWidth(1, 1 - slit), "the slit keeps its width while the door folds");
        near(0, PortalGunDoorRender.doorWidth(1, 1), "the slit then shrinks away");
        check(
            2 * PortalGunDoorRender.DOOR_HALF_WIDTH >= 1.125
                && 2 * PortalGunDoorRender.DOOR_HALF_HEIGHT == PortalGunGeometry.HEIGHT,
            "the door is at least 1.125 blocks wide and as tall as the portal");
    }

    private static void portalDoorArcs() {
        double halfWidth = PortalGunDoorRender.DOOR_HALF_WIDTH, halfHeight = PortalGunDoorRender.DOOR_HALF_HEIGHT,
            reach = PortalGunDoorRender.ARC_LIFT + PortalGunDoorRender.ARC_REACH;
        int window = 200, quiet = 0, crawled = 0, cornered = 0;
        Set<Long> spots = new HashSet<>();
        for (int tick = 0; tick < window; tick++) {
            List<PortalGunDoorRender.FrameArc> arcs = PortalGunDoorRender.frameArcs(halfWidth, halfHeight, tick),
                next = PortalGunDoorRender.frameArcs(halfWidth, halfHeight, tick + PortalGunDoorRender.ARC_HOP_TICKS);
            if (arcs.isEmpty()) quiet++;
            check(arcs.size() <= 8, "the frame never fills up with arcs, " + arcs.size() + " at tick " + tick);
            for (PortalGunDoorRender.FrameArc arc : arcs) {
                List<Vec3d> path = arc.path(), surge = arc.surge();
                check(
                    path.size() == PortalGunDoorRender.ARC_POINTS && surge.size() == path.size(),
                    "frame arcs have all their points");
                for (Vec3d point : path) check(
                    frameDistance(point, halfWidth, halfHeight) <= reach + 1.0E-9,
                    "frame arcs hug the frame at tick " + tick);
                for (Vec3d point : surge) check(
                    frameDistance(point, halfWidth, halfHeight) < 1.0E-9,
                    "the brightened stretch lies on the frame at tick " + tick);
                check(
                    frameDistance(path.get(0), halfWidth, halfHeight) < 1.0E-9
                        && frameDistance(path.get(path.size() - 1), halfWidth, halfHeight) < 1.0E-9,
                    "frame arcs start and end on the frame line at tick " + tick);
                Vec3d first = path.get(0), last = path.get(path.size() - 1);
                spots.add(Math.round(first.x() * 20) * 1000 + Math.round(first.y() * 20));
                if (Math.abs(Math.abs(first.x()) - halfWidth) < 1.0E-9
                    != (Math.abs(Math.abs(last.x()) - halfWidth) < 1.0E-9)) cornered++;
                for (PortalGunDoorRender.FrameArc later : next) if (later.surge()
                    .get(0)
                    .subtract(surge.get(surge.size() - 1))
                    .length() < 1.0E-9) crawled++;
            }
        }
        check(quiet < window / 4, "the frame crackles most of the time, quiet " + quiet + " of " + window);
        check(spots.size() >= 30, "arcs show up all around the frame, found " + spots.size());
        check(crawled >= 20, "a run crawls on: its next hop starts where the last one ended, found " + crawled);
        check(cornered > 0, "arcs crawl round the corners");
        check(
            PortalGunDoorRender.frameArcs(halfWidth, 0, 5)
                .isEmpty(),
            "a flat slit has no lightning");
        List<PortalGunDoorRender.FrameArc> early = PortalGunDoorRender.frameArcs(halfWidth, halfHeight, 3),
            late = PortalGunDoorRender.frameArcs(halfWidth, halfHeight, 3 + PortalGunDoorRender.ARC_HOP_TICKS - 0.1F);
        check(early.equals(late), "an arc holds its shape for a whole hop instead of flickering every frame");
        near(
            0,
            PortalGunDoorRender.framePoint(halfWidth, halfHeight, 1 - 1.0E-12, 0)
                .subtract(PortalGunDoorRender.framePoint(halfWidth, halfHeight, 0, 0))
                .length(),
            "going once round the frame comes back to the start");
    }

    private static double frameDistance(Vec3d point, double halfWidth, double halfHeight) {
        double dx = Math.abs(point.x()) - halfWidth, dy = Math.abs(point.y()) - halfHeight;
        if (dx < 0 && dy < 0) return Math.min(-dx, -dy);
        return Math.hypot(Math.max(dx, 0), Math.max(dy, 0));
    }

    private static void portalVortexShape() {
        float start = PortalGunVortexRender.GROW_START, peak = 0;
        for (int i = 0; i <= 100; i++) {
            float t = i / 100F, size = PortalGunVortexRender.size(t, 0);
            if (t <= start) near(0, size, "the vortex oval waits for the fluid to swirl first, at " + t);
            peak = Math.max(peak, size);
        }
        check(
            PortalGunVortexRender.size(0.4F, 0) < 0.5F,
            "the oval is still small while the streams swing wide, " + PortalGunVortexRender.size(0.4F, 0));
        check(peak > 1.02F && peak < 1.15F, "the vortex oval pops open with a small overshoot, peak " + peak);
        near(1, PortalGunVortexRender.size(1, 0), "an open vortex has its full size");
        float last = PortalGunVortexRender.size(1, 0);
        for (int i = 0; i <= 100; i++) {
            float c = i / 100F, size = PortalGunVortexRender.size(1, c);
            check(size <= last + 1.0E-6, "a closing vortex only shrinks, at " + c);
            last = size;
        }
        near(0, PortalGunVortexRender.size(1, 1), "a closed vortex is gone");
        check(PortalGunVortexRender.fluid(0.15F, 0) > 0.5F, "the fluid swirls in right after the shot lands");
        near(1, PortalGunVortexRender.fluid(1, 0), "the two streams stay around an open vortex");
        near(0, PortalGunVortexRender.fluid(1, 1), "the streams are gone when the vortex closes");
        near(0, PortalGunVortexRender.settle(0.4F), "the streams spiral while the vortex forms");
        near(1, PortalGunVortexRender.settle(1), "the streams hug the rim once the vortex is open");
        check(PortalGunVortexRender.STREAMS == 2, "the vortex has exactly two streams of fluid");
        double rx = PortalGunVortexRender.RX, ry = PortalGunVortexRender.RY;
        for (int i = 0; i <= 20; i++) {
            float open = i / 20F, size = PortalGunVortexRender.size(open, 0);
            for (int stream = 0; stream < PortalGunVortexRender.STREAMS; stream++)
                for (double[] point : PortalGunVortexRender.flow(stream, open, size, 7)
                    .points())
                    check(
                        Math.hypot(point[0] / rx, point[1] / ry) >= PortalGunVortexRender.OUTSIDE * size - 1.0E-9,
                        "stream " + stream + " stays outside the oval at " + open);
        }
        float forming = PortalGunVortexRender.size(0.45F, 0);
        check(
            radius(head(PortalGunVortexRender.flow(0, 0.45F, forming, 7)), rx, ry) > 1.3 * forming,
            "the streams swing well outside the forming oval");
        for (double[] point : PortalGunVortexRender.flow(0, 1, 1, 7)
            .points()) {
            double r = radius(point, rx, ry);
            check(
                r >= PortalGunVortexRender.OUTSIDE - 1.0E-9 && r <= PortalGunVortexRender.LIQUID_RADIUS + 0.07,
                "an open vortex's stream hugs the rim, found " + r);
        }
        check(
            distance(head(PortalGunVortexRender.flow(0, 1, 1, 0)), head(PortalGunVortexRender.flow(0, 1, 1, 10))) > 0.1,
            "the streams flow around an open vortex");
        float almost = 0.98F;
        check(
            distance(
                head(PortalGunVortexRender.flow(0, almost, PortalGunVortexRender.size(almost, 0), almost * 16)),
                head(PortalGunVortexRender.flow(0, 1, 1, 16))) < 0.15,
            "the streams do not jump when the vortex finishes opening");
        check(
            distance(head(PortalGunVortexRender.flow(0, 1, 1, 7)), head(PortalGunVortexRender.flow(1, 1, 1, 7))) > 1,
            "the two streams run on opposite sides");
        PortalGunVortexRender.Outline outline = PortalGunVortexRender
            .outline(PortalGunVortexRender.flow(0, 0.5F, PortalGunVortexRender.size(0.5F, 0), 8), 0, 8);
        double[] widths = outline.widths();
        int head = PortalGunVortexRender.STREAM_POINTS - 1;
        check(widths[head] > widths[0], "a stream is thicker at its head than at its tail");
        near(0, widths[0], "a stream's tail tapers to a point");
        near(0, PortalGunVortexRender.thickness(1, 0), "a settled stream's tail tapers to a point");
        for (int i = head + 1; i < widths.length; i++)
            check(widths[i] <= widths[i - 1] + 1.0E-9, "the head ends in a rounded cap instead of a separate ball");
        near(0, widths[widths.length - 1], "the rounded cap closes the stream");
        double low = 2, high = 0;
        for (double scale : outline.outer()) {
            low = Math.min(low, scale);
            high = Math.max(high, scale);
        }
        check(high - low > 0.15, "the stream edges wobble instead of running smooth");
        near(0, PortalGunVortexRender.shine(0), "the tail of a stream is plain fluid");
        near(1, PortalGunVortexRender.shine(head), "the head of a stream is glossy white");
        for (int i = 1; i <= head; i++) check(
            PortalGunVortexRender.shine(i) >= PortalGunVortexRender.shine(i - 1),
            "the gloss grows toward the head");
        double settledWidth = PortalGunVortexRender.thickness(1, 1);
        check(
            settledWidth > 0.04 && settledWidth < 0.07,
            "the streams around an open vortex are neither threads nor ropes, " + settledWidth);
        portalVortexDroplets();
        portalVortexCompanions();
        near(0, PortalGunVortexRender.bloom(0.4F), "a forming vortex has a plain liquid rim, no flames yet");
        near(1, PortalGunVortexRender.bloom(1), "the flames bloom once the vortex is open");
        portalVortexFlames();
    }

    private static void portalVortexDroplets() {
        int burst = 0, trail = 0;
        for (int tick = 6; tick <= 10; tick++) burst += PortalGunVortexRender
            .droplets(0, tick, PortalGunVortexRender.size(tick / (float) PortalGunVortexRender.OPEN_TICKS, 0))
            .size();
        for (int tick = 40; tick <= 44; tick++) trail += PortalGunVortexRender.droplets(0, tick, 1)
            .size();
        check(
            trail > 0 && burst >= 2 * trail,
            "a forming vortex throws far more droplets than an open one, " + burst + " vs " + trail);
        List<PortalGunVortexRender.Drop> all = new ArrayList<>();
        Set<Long> spots = new HashSet<>();
        int crowded = 0;
        for (int tick = 0; tick < 400; tick++) {
            float open = Math.min(1, tick / (float) PortalGunVortexRender.OPEN_TICKS),
                size = PortalGunVortexRender.size(open, 0);
            List<PortalGunVortexRender.Drop> drops = new ArrayList<>();
            for (int stream = 0; stream < PortalGunVortexRender.STREAMS; stream++)
                drops.addAll(PortalGunVortexRender.droplets(stream, tick, size));
            all.addAll(drops);
            crowded = Math.max(crowded, crowd(drops));
            for (PortalGunVortexRender.Drop drop : drops)
                if (drop.alpha() > 0.85) spots.add(Math.round(drop.x() * 20) * 1000 + Math.round(drop.y() * 20));
        }
        check(crowded <= 2, "droplets never bunch up in one spot, found " + (crowded + 1) + " together");
        check(spots.size() >= 20, "droplets fly off from all along the stream, found " + spots.size() + " spots");
        long tailed = all.stream()
            .filter(PortalGunVortexRender.Drop::tailed)
            .count();
        check(
            tailed > 0.1 * all.size() && tailed < 0.4 * all.size(),
            "droplets are mostly round dots with a few tailed splashes, " + tailed + " of " + all.size());
        double longest = 0, shortest = 10;
        boolean inward = false, outward = false;
        for (PortalGunVortexRender.Drop drop : all) {
            check(drop.size() > 0 && drop.alpha() >= 0 && drop.alpha() <= 1, "droplets have a size and a fade");
            if (!drop.tailed()) continue;
            check(
                drop.stretch() / PortalGunVortexRender.DROP_HEAD >= 4,
                "a droplet's tail is several times longer than its head, " + drop.stretch());
            check(Math.abs(drop.curve()) >= 0.1, "a droplet's tail curves, " + drop.curve());
            longest = Math.max(longest, drop.stretch());
            shortest = Math.min(shortest, drop.stretch());
            double side = -drop.alongY() * drop.x() + drop.alongX() * drop.y();
            inward |= drop.curve() * side < 0;
            outward |= drop.curve() * side > 0;
        }
        check(longest - shortest > 1.5, "droplet tails differ in length, from " + shortest + " to " + longest);
        check(inward && outward, "droplet tails curve both ways, mostly with the swirl");
        for (int tick = 30; tick < 60; tick++) {
            double sweep = distance(
                head(PortalGunVortexRender.flow(0, 1, 1, tick)),
                head(PortalGunVortexRender.flow(0, 1, 1, tick + 2)));
            for (PortalGunVortexRender.Drop drop : PortalGunVortexRender.droplets(0, tick, 1))
                for (PortalGunVortexRender.Drop later : PortalGunVortexRender.droplets(0, tick + 2, 1))
                    if (later.wobble() == drop.wobble()) check(
                        distance(new double[] { drop.x(), drop.y() }, new double[] { later.x(), later.y() })
                            < 0.5 * sweep,
                        "droplets drift off on their own instead of riding the stream, tick " + tick);
        }
    }

    private static int crowd(List<PortalGunVortexRender.Drop> drops) {
        int most = 0;
        for (PortalGunVortexRender.Drop drop : drops) {
            if (drop.alpha() < 0.3) continue;
            int near = 0;
            for (PortalGunVortexRender.Drop other : drops) if (other != drop && other.alpha() >= 0.3
                && Math.hypot(other.x() - drop.x(), other.y() - drop.y()) < 0.1) near++;
            most = Math.max(most, near);
        }
        return most;
    }

    private static void portalVortexCompanions() {
        check(
            PortalGunVortexRender.STREAMS * PortalGunVortexRender.COMPANIONS >= 2
                && PortalGunVortexRender.STREAMS * PortalGunVortexRender.COMPANIONS <= 6,
            "a few thin side streams follow the main streams");
        float forming = 0.3F, size = PortalGunVortexRender.size(forming, 0);
        for (int stream = 0; stream < PortalGunVortexRender.STREAMS; stream++)
            for (int index = 0; index < PortalGunVortexRender.COMPANIONS; index++) {
                near(0, PortalGunVortexRender.companionShow(stream, index, 0), "no side streams before the shot lands");
                check(
                    PortalGunVortexRender.companionShow(stream, index, forming) > 0.5F,
                    "side streams show while the vortex forms");
                near(
                    0,
                    PortalGunVortexRender.companionShow(stream, index, 1),
                    "side streams are gone once the vortex is open");
                PortalGunVortexRender.Outline side = PortalGunVortexRender.companion(stream, index, forming, size, 5);
                double[] widths = side.widths();
                double widest = 0, length = 0;
                for (double width : widths) widest = Math.max(widest, width);
                for (int i = 1; i < side.points().length; i++)
                    length += distance(side.points()[i - 1], side.points()[i]);
                check(
                    widest < 0.5 * PortalGunVortexRender.thickness(forming, 1),
                    "side streams are much thinner than the main streams");
                near(0, widths[0], "a side stream tapers at its tail");
                near(0, widths[widths.length - 1], "a side stream tapers at its head");
                check(length > 0.8, "side streams are long, found " + length);
            }
    }

    private static void portalVortexFlames() {
        double base = PortalGunVortexRender.FLAME_BASE, depth = PortalGunVortexRender.FLAME_DEPTH;
        check(base < PortalGunVortexRender.RIM_INNER, "the flames start inside the bright rim");
        check(PortalGunVortexRender.INTERIOR >= base, "the light inside reaches under the flames, so no gap shows");
        int samples = 2880;
        for (int tick = 0; tick < 80; tick += 13) {
            List<Double> tips = new ArrayList<>(), angles = new ArrayList<>();
            double low = 1, high = 0;
            double[] radii = new double[samples];
            for (int k = 0; k < samples; k++) {
                radii[k] = PortalGunVortexRender.flameRadius(2 * Math.PI * k / samples, tick);
                low = Math.min(low, radii[k]);
                high = Math.max(high, radii[k]);
            }
            int window = samples / PortalGunVortexRender.FLAMES / 4;
            for (int k = 0; k < samples; k++) {
                boolean deepest = base - radii[k] > 0.3 * depth;
                for (int j = -window; deepest && j <= window; j++) {
                    double other = radii[Math.floorMod(k + j, samples)];
                    deepest = j == 0 || other > radii[k] || other == radii[k] && j > 0;
                }
                if (deepest) {
                    tips.add(base - radii[k]);
                    angles.add(2 * Math.PI * k / samples);
                }
            }
            check(
                low >= base - depth - 1.0E-9 && high <= base + 1.0E-9,
                "the flames stay in their band at tick " + tick);
            check(
                tips.size() >= 5 && tips.size() <= PortalGunVortexRender.FLAMES + 2,
                "the rim has a handful of flames at tick " + tick + ", found " + tips.size());
            double shallowest = tips.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0),
                deepest = tips.stream()
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(0);
            check(deepest - shallowest >= 0.25 * depth, "the flames reach in by different amounts at tick " + tick);
            double widest = 0, narrowest = 2 * Math.PI;
            for (int i = 0; i < angles.size(); i++) {
                double gap = (angles.get((i + 1) % angles.size()) - angles.get(i) + 2 * Math.PI) % (2 * Math.PI);
                widest = Math.max(widest, gap);
                narrowest = Math.min(narrowest, gap);
            }
            check(widest > 1.5 * narrowest, "the flames are spaced unevenly at tick " + tick);
        }
        check(
            Math.abs(PortalGunVortexRender.flameRadius(0.3, 0) - PortalGunVortexRender.flameRadius(0.3, 20)) > 1.0E-3,
            "the flames flicker over time");
    }

    private static double[] head(PortalGunVortexRender.Flow flow) {
        return flow.points()
            .get(
                flow.points()
                    .size() - 1);
    }

    private static double radius(double[] point, double rx, double ry) {
        return Math.hypot(point[0] / rx, point[1] / ry);
    }

    private static double distance(double[] a, double[] b) {
        return Math.hypot(a[0] - b[0], a[1] - b[1]);
    }

    private static void portalVortexSprite(PortalGunPortalEffect effect, BufferedImage sheet) {
        near(
            Math.round(2 * PortalGunVortexRender.INTERIOR * PortalGunVortexRender.RX * effect.pixelsPerBlock),
            effect.frameWidth,
            effect + " light sprite is as wide as the inside of the oval");
        near(
            Math.round(2 * PortalGunVortexRender.INTERIOR * PortalGunVortexRender.RY * effect.pixelsPerBlock),
            effect.frameHeight,
            effect + " light sprite is as tall as the inside of the oval");
        double rx = effect.frameWidth / 2.0, ry = effect.frameHeight / 2.0;
        int holes = 0, spills = 0;
        for (int frame = 0; frame < effect.frames; frame++)
            for (int y = 0; y < effect.frameHeight; y++) for (int x = 0; x < effect.frameWidth; x++) {
                double r = Math.hypot((x + 0.5 - rx) / rx, (y + 0.5 - ry) / ry);
                int alpha = sheet.getRGB(x, frame * effect.frameHeight + y) >>> 24;
                if (r <= 0.9 && alpha != 255) holes++;
                if (r >= 1.05 && alpha != 0) spills++;
            }
        check(holes == 0, effect + " fills its oval with light, found " + holes + " holes");
        check(spills == 0, effect + " keeps its light inside the oval, found " + spills + " stray pixels");
        double lift = PortalGunPortalRender.lift(effect, true);
        check(lift > 0 && lift <= 0.25, effect + " sits just above the floor, lift " + lift);
    }

    private static void configEnumsStayLight(Class<?> section) throws IOException {
        for (Field field : section.getFields()) {
            Class<?> type = field.getType();
            if (type.isEnum() && type.getName()
                .startsWith("com.science.gtnl.")) {
                Path file = SOURCES.resolve(
                    type.getName()
                        .substring("com.science.gtnl.".length())
                        .replace('.', '/') + ".java");
                check(Files.isRegularFile(file), "config enum source " + file);
                if (!Files.isRegularFile(file)) continue;
                String source = Files.readString(file, StandardCharsets.UTF_8);
                check(
                    !Pattern.compile(
                        "\\b(net\\.minecraft|cpw\\.mods|com\\.science\\.gtnl\\.(?!utils\\.enums\\.)|ScienceNotLeisure)")
                        .matcher(
                            source.replaceAll("(?m)^package .*$", "")
                                .replaceAll("(?m)^\\s*//.*$", ""))
                        .find(),
                    type.getSimpleName()
                        + " is read by the early coremod through MainConfig, so it must not load Minecraft or mod classes");
            }
        }
        for (Class<?> nested : section.getClasses()) configEnumsStayLight(nested);
    }

    private static void portalEffect(PortalGunPortalEffect effect) throws IOException {
        Path file = RESOURCES.resolve(effect.texturePath());
        check(Files.isRegularFile(file), effect + " has its sprite " + file);
        if (!Files.isRegularFile(file)) return;
        BufferedImage sheet = ImageIO.read(file.toFile());
        near(effect.frameWidth, sheet.getWidth(), effect + " sprite frame width");
        near(effect.frameHeight * effect.frames, sheet.getHeight(), effect + " holds every frame stacked vertically");
        check(effect.frames > 0 && effect.frameTicks > 0, effect + " animates");
        for (int tick = 0; tick < 3 * effect.frames * effect.frameTicks; tick++) check(
            PortalGunPortalRender.sheetFrame(effect, tick) == (tick / effect.frameTicks) % effect.frames,
            effect + " loops its frames, tick " + tick);
        near(
            0,
            PortalGunPortalRender.lift(effect, false),
            effect + " stays centered when a block right above would cut its top");
        if (effect.kind == Kind.FRAMED) {
            portalDoorSprite(effect, sheet);
            return;
        }
        if (effect.kind == Kind.VORTEX) {
            portalVortexSprite(effect, sheet);
            return;
        }
        check(
            effect.frameWidth > PortalGunGeometry.WIDTH * effect.pixelsPerBlock
                && effect.frameHeight > PortalGunGeometry.HEIGHT * effect.pixelsPerBlock,
            effect + " covers the whole 1 x 2 portal plus its glow");
        int border = 0, top = effect.frameHeight, bottom = 0, left = effect.frameWidth, right = 0;
        for (int y = 0; y < sheet.getHeight(); y++) for (int x = 0; x < sheet.getWidth(); x++) {
            if (sheet.getRGB(x, y) >>> 24 == 0) continue;
            int row = y % effect.frameHeight;
            if (x == 0 || x == effect.frameWidth - 1 || row == 0 || row == effect.frameHeight - 1) border++;
            top = Math.min(top, row);
            bottom = Math.max(bottom, row + 1);
            left = Math.min(left, x);
            right = Math.max(right, x + 1);
        }
        check(border == 0, effect + " never draws on its frame edge, which would cut the glow into straight lines");
        near(
            effect.standHalfHeight,
            Math.max(effect.frameHeight / 2.0 - top, bottom - effect.frameHeight / 2.0),
            effect + " rests its lowest glow on the floor instead of sinking it in");
        check(
            (right - left) / (double) effect.pixelsPerBlock >= 1.25,
            effect + " is drawn at least 1.25 blocks wide, found " + (right - left) + " pixels");
        near(
            -PortalGunGeometry.HEIGHT / 2,
            PortalGunPortalRender.lift(effect, true) - effect.standHalfHeightBlocks(),
            effect + " is lifted so its lowest glow rests on the floor instead of sinking into it");
        check(
            sheet.getRGB(effect.frameWidth / 2, effect.frameHeight / 2) >>> 24 == 255,
            effect + " has an opaque center");
        check(sheet.getRGB(0, 0) >>> 24 == 0, effect + " has transparent corners");
        if (effect == PortalGunPortalEffect.GOLDEN_SWIRL) portalGoldCore(effect, sheet);
    }

    private static void portalGoldCore(PortalGunPortalEffect effect, BufferedImage sheet) {
        int x = effect.frameWidth / 2, y = effect.frameHeight / 2, core = sheet.getRGB(x, y);
        for (int frame = 0; frame < effect.frames; frame++) {
            int top = frame * effect.frameHeight;
            check(
                sheet.getRGB(x, top + y) == core && sheet.getRGB(x - 1, top + y - 1) == core
                    && sheet.getRGB(x - 1, top + y) == core
                    && sheet.getRGB(x, top + y - 1) == core,
                effect + " spins around a bright core right in the middle, frame " + frame);
        }
        int red = core >> 16 & 0xFF, green = core >> 8 & 0xFF, blue = core & 0xFF;
        check(red >= green && green > blue + 40, effect + " is golden, core " + Integer.toHexString(core));
    }

    private static void portalDoorSprite(PortalGunPortalEffect effect, BufferedImage sheet) {
        int clear = 0;
        for (int y = 0; y < sheet.getHeight(); y++)
            for (int x = 0; x < sheet.getWidth(); x++) if (sheet.getRGB(x, y) >>> 24 != 255) clear++;
        check(
            clear == 0,
            effect + " fills the inside of the door with solid space, found " + clear + " see-through pixels");
        near(
            2 * (PortalGunDoorRender.DOOR_HALF_WIDTH - PortalGunDoorRender.DOOR_INSET) * effect.pixelsPerBlock,
            effect.frameWidth,
            effect + " inside sprite is exactly as wide as the inside of the door frame");
        near(
            2 * (PortalGunDoorRender.DOOR_HALF_HEIGHT - PortalGunDoorRender.DOOR_INSET) * effect.pixelsPerBlock,
            effect.frameHeight,
            effect + " inside sprite is exactly as tall as the inside of the door frame");
        double lift = PortalGunPortalRender.lift(effect, true);
        check(
            lift >= PortalGunDoorRender.FRAME_HAZE && lift >= PortalGunDoorRender.cornerGlowRadius(1),
            effect + " is lifted so its bottom beam and corner glows clear the floor, lift " + lift);
        check(lift <= 0.25, effect + " does not visibly float above the floor, lift " + lift);
        portalDoorStars(effect, sheet);
    }

    private static void portalDoorStars(PortalGunPortalEffect effect, BufferedImage sheet) {
        int width = effect.frameWidth, height = effect.frameHeight, starGreen = 80;
        boolean longRays = false;
        for (int frame = 0; frame < effect.frames; frame++) {
            int[] cells = new int[8];
            Set<Integer> shades = new HashSet<>();
            int stars = 0;
            for (int y = 0; y < height; y++) {
                int run = 0;
                for (int x = 0; x < width; x++) {
                    int rgb = sheet.getRGB(x, frame * height + y);
                    if ((rgb >> 8 & 0xFF) < starGreen) {
                        run = 0;
                        continue;
                    }
                    stars++;
                    shades.add(rgb & 0xFFFFFF);
                    cells[x * 2 / width + 2 * (y * 4 / height)]++;
                    if (++run >= 5) longRays = true;
                }
            }
            check(
                Arrays.stream(cells)
                    .allMatch(count -> count > 0),
                effect + " frame " + frame + " spreads its stars over the whole door");
            double share = stars / (double) (width * height);
            check(
                share >= 0.02 && share <= 0.08,
                effect + " frame " + frame + " keeps a sparse star density, found " + share);
            check(
                shades.size() >= 3,
                effect + " frame " + frame + " mixes bright and dim stars, found " + shades.size() + " shades");
        }
        check(longRays, effect + " has big stars with long rays among the small ones");
    }
}
