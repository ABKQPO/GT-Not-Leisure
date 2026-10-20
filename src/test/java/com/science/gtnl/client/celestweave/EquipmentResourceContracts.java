package com.science.gtnl.client.celestweave;

import static com.science.gtnl.client.celestweave.CelestweaveContractTest.RESOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.call;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.check;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.field;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.near;

import java.awt.image.BufferedImage;
import java.io.File;
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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.DoubleConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;

import com.cleanroommc.modularui.api.IJsonSerializable;
import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DrawableSerialization;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.theme.ReloadThemeEvent;
import com.cleanroommc.modularui.theme.SelectableTheme;
import com.cleanroommc.modularui.theme.SlotTheme;
import com.cleanroommc.modularui.theme.TextFieldTheme;
import com.cleanroommc.modularui.theme.ThemeAPI;
import com.cleanroommc.modularui.theme.ThemeManager;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.JsonBuilder;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.IntValue;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.science.gtnl.client.fx.FxColor;
import com.science.gtnl.client.hud.HudColor;
import com.science.gtnl.client.hud.HudElement;
import com.science.gtnl.client.hud.HudTiming;
import com.science.gtnl.client.railgun.RailgunHudRenderer;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveModuleSetting;
import com.science.gtnl.common.equipment.EquipmentSound;
import com.science.gtnl.common.gui.equipment.CelestweaveGui;
import com.science.gtnl.common.gui.equipment.EquipmentSoundScreen;
import com.science.gtnl.common.gui.equipment.EquipmentTheme;
import com.science.gtnl.common.gui.equipment.EquipmentTheme.Colors;
import com.science.gtnl.common.gui.modularui.layout.Layout;
import com.science.gtnl.common.gui.modularui.layout.LayoutMath;
import com.science.gtnl.common.gui.modularui.layout.LayoutTheme;
import com.science.gtnl.common.gui.modularui.layout.Size;
import com.science.gtnl.common.gui.modularui.theme.ButtonPurpose;
import com.science.gtnl.common.gui.modularui.theme.LivePalette;
import com.science.gtnl.common.gui.modularui.theme.PaletteThemes;
import com.science.gtnl.common.gui.modularui.theme.ThemePalette;
import com.science.gtnl.common.gui.modularui.theme.ThemeStyle;
import com.science.gtnl.common.gui.modularui.theme.VariantTheme;
import com.science.gtnl.common.gui.modularui.widget.CardDrawable;
import com.science.gtnl.common.gui.modularui.widget.FillBar;
import com.science.gtnl.common.gui.modularui.widget.FrameDrawable;
import com.science.gtnl.common.gui.modularui.widget.SlideSwitch;
import com.science.gtnl.common.gui.modularui.widget.StepSlider;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.config.EquipmentSoundConfig;
import com.science.gtnl.config.EquipmentSoundConfig.Channel;
import com.science.gtnl.config.MainConfig;

import gregtech.api.modularui2.GTGuiThemes;

public class EquipmentResourceContracts {

    private static final String TEST_THEME = "gtnl:palette_test";
    private static final int CYAN = 0x3DE8FF;
    private static final double HUE_TOLERANCE = 6;

    private EquipmentResourceContracts() {}

    static void run() throws Exception {
        hexTexture();
        model();
        raisedGlow();
        firstPersonArm();
        sounds();
        soundChannels();
        equipmentTheme();
        equipmentScreens();
        layoutRules();
        lang();
    }

    private static void hexTexture() throws ReflectiveOperationException {
        BufferedImage image = (BufferedImage) call(CelestweaveShieldFlash.class, "buildHex", null, new Class<?>[0]);
        check(image.getWidth() == 64 && image.getHeight() == 111, "hex texture size");
        int lines = 0, empty = 0, seam = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int value = alpha(image, x, y);
                if (value > 200) lines++;
                if (value < 10) empty++;
            }
            seam = Math.max(seam, Math.abs(alpha(image, 0, y) - alpha(image, image.getWidth() - 1, y)));
        }
        int total = image.getWidth() * image.getHeight();
        check(lines > total / 50 && lines < total / 3, "hex line coverage " + lines + "/" + total);
        check(empty > total / 3, "hex cell interiors transparent " + empty + "/" + total);
        check(seam < 40, "hex texture tiles horizontally, max seam " + seam);
    }

    private static int alpha(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >>> 24;
    }

    private static void model() throws Exception {
        Path armor = RESOURCES.resolve("models/armor");
        CelestweaveFrames frames;
        try (var reader = Files.newBufferedReader(armor.resolve("celestweave_frames.json"), StandardCharsets.UTF_8)) {
            frames = new Gson().fromJson(reader, CelestweaveFrames.class);
        }
        frames.resolve();
        List<String> problems = new ArrayList<>();
        Map<String, Map<String, int[]>> objs = new HashMap<>();
        for (int type = 0; type < 4; type++) {
            String file = frames.objFile(type);
            check(file != null, "obj file for piece " + type);
            if (file == null) continue;
            Path path = armor.resolve(file);
            check(Files.exists(path), "obj exists " + file);
            if (Files.exists(path)) objs.put(file, obj(path, problems));
        }
        problems.forEach(problem -> check(false, problem));
        Set<String> listed = new HashSet<>();
        for (CelestweaveFrames.Group group : frames.groups) {
            String where = "group " + group.name;
            check(group.piece >= 0, where + " has known obj " + group.obj);
            check(group.bone >= 0, where + " resolves to a bone");
            check(group.frame == null || group.frame.startsWith("bone:") || group.frameRef != null, where + " frame");
            Map<String, int[]> groups = objs.get(group.obj);
            int[] faces = groups == null ? null : groups.get(group.name);
            check(faces != null, where + " exists in " + group.obj);
            if (faces != null) check(faces[0] == group.quads, where + " quad count " + faces[0] + " vs " + group.quads);
            listed.add(group.obj + "#" + group.name);
        }
        for (Map.Entry<String, Map<String, int[]>> entry : objs.entrySet()) for (String name : entry.getValue()
            .keySet()) check(listed.contains(entry.getKey() + "#" + name), "unlisted obj group " + name);
        for (CelestweaveFrames.Frame frame : frames.frames) check(frame.bone >= 0, "frame bone " + frame.name);
        CelestweaveAnimator.bind(frames);
        for (Field field : CelestweaveAnimator.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            if (field.getType() == int.class) check(field.getInt(null) >= 0, "animator frame " + field.getName());
            if (field.getType() == int[].class)
                for (int value : (int[]) field.get(null)) check(value >= 0, "animator frame array " + field.getName());
        }
        for (String anchor : new String[] { "core.shock", "wing.fore.L", "wing.fore.R" })
            check(frames.frame(anchor) != null, "anchor frame " + anchor);
        JsonObject textures = new JsonParser()
            .parse(new String(Files.readAllBytes(armor.resolve("celestweave_frames.json")), StandardCharsets.UTF_8))
            .getAsJsonObject()
            .getAsJsonObject("textures");
        for (Map.Entry<String, JsonElement> texture : textures.entrySet()) {
            String file = texture.getValue()
                .getAsJsonObject()
                .get("file")
                .getAsString();
            check(Files.exists(RESOURCES.resolve("textures/models/armor/" + file)), "texture exists " + file);
        }
    }

    private static Map<String, int[]> obj(Path path, List<String> problems) throws IOException {
        Map<String, int[]> groups = new HashMap<>();
        int vertices = 0, uvs = 0;
        String group = null;
        List<String> faces = new ArrayList<>();
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (line.startsWith("v ")) vertices++;
            else if (line.startsWith("vt ")) uvs++;
            else if (line.startsWith("g ")) {
                group = line.substring(2)
                    .trim();
                if (groups.put(group, new int[] { 0 }) != null) problems.add(path + " duplicate group " + group);
            } else if (line.startsWith("f ")) {
                if (group == null) problems.add(path + " face before any group");
                else groups.get(group)[0]++;
                faces.add(line);
            }
        }
        for (String face : faces) {
            String[] parts = face.substring(2)
                .trim()
                .split("\\s+");
            if (parts.length < 3) problems.add(path + " degenerate face " + face);
            for (String part : parts) {
                String[] index = part.split("/");
                int v = Integer.parseInt(index[0]);
                if (v < 1 || v > vertices) problems.add(path + " vertex index out of range " + face);
                if (index.length > 1 && !index[1].isEmpty()) {
                    int t = Integer.parseInt(index[1]);
                    if (t < 1 || t > uvs) problems.add(path + " uv index out of range " + face);
                }
            }
        }
        return groups;
    }

    private static void raisedGlow() throws Exception {
        Face face = new Face();
        face.vertices = new Vertex[] { new Vertex(0, 0, 0), new Vertex(4, 0, 0), new Vertex(4, 4, 0),
            new Vertex(0, 4, 0) };
        face.textureCoordinates = new TextureCoordinate[] { new TextureCoordinate(0, 0), new TextureCoordinate(1, 0),
            new TextureCoordinate(1, 1), new TextureCoordinate(0, 1) };
        int[] pixels = new int[16];
        Arrays.fill(pixels, 0xFF000000);
        check(
            CelestweaveRaise.raise(List.of(face), pixels, 4, 4, 0.25F)
                .isEmpty(),
            "dark glow face raises nothing");
        pixels[4 + 1] = pixels[4 + 2] = 0xFFFFFFFF;
        List<float[]> quads = CelestweaveRaise.raise(List.of(face), pixels, 4, 4, 0.25F);
        check(quads.size() == 5, "one lit strip raises into a top and four walls");
        float[] top = quads.get(0);
        boolean flat = true, inside = true;
        for (int i = 0; i < 20; i += 5) {
            flat &= Math.abs(top[i + 2] - 0.25F) < 1.0E-5F;
            inside &= top[i] >= 1 - 1.0E-4F && top[i] <= 3 + 1.0E-4F
                && top[i + 1] >= 1.5F - 1.0E-4F
                && top[i + 1] <= 2.5F + 1.0E-4F;
        }
        check(flat, "strip top sits one lift above the face, along its normal");
        check(inside, "strip keeps the lit texels' size and is centred on the face");
        for (float[] quad : quads) {
            float ax = quad[5] - quad[0], ay = quad[6] - quad[1], az = quad[7] - quad[2];
            float bx = quad[10] - quad[0], by = quad[11] - quad[1], bz = quad[12] - quad[2];
            float nx = ay * bz - az * by, ny = az * bx - ax * bz, nz = ax * by - ay * bx;
            float cx = 0, cy = 0, cz = 0;
            for (int i = 0; i < 20; i += 5) {
                cx += quad[i] / 4;
                cy += quad[i + 1] / 4;
                cz += quad[i + 2] / 4;
            }
            check(nx * (cx - 2) + ny * (cy - 2) + nz * (cz - 0.125F) > 0, "raised strip faces point outward");
            for (int i = 0; i < 20; i += 5) {
                int x = Math.min(3, (int) (quad[i + 3] * 4)), y = Math.min(3, (int) (quad[i + 4] * 4));
                check(CelestweaveRaise.lit(pixels[y * 4 + x]), "raised strip samples lit texels");
            }
        }

        Path armor = RESOURCES.resolve("models/armor");
        BufferedImage glow = ImageIO.read(
            armor.getParent()
                .getParent()
                .resolve("textures/models/armor/celestweave_glow.png")
                .toFile());
        int width = glow.getWidth(), height = glow.getHeight();
        int[] argb = glow.getRGB(0, 0, width, height, null, 0, width);
        for (String file : new String[] { "celestweave_oculus.obj", "celestweave_core.obj", "celestweave_conduit.obj",
            "celestweave_stride.obj" }) {
            WavefrontObject obj;
            try (var stream = Files.newInputStream(armor.resolve(file))) {
                obj = new WavefrontObject(file, stream);
            }
            for (GroupObject group : obj.groupObjects) {
                if (!group.name.contains("__glowFaces__")) continue;
                List<float[]> raised = CelestweaveRaise
                    .raise(group.faces, argb, width, height, CelestweaveRaise.HEIGHT);
                check(!raised.isEmpty() && raised.size() % 5 == 0, "glow strips raised for " + group.name);
                boolean sampled = true;
                for (int q = 0; q < raised.size(); q += 5) {
                    float[] cap = raised.get(q);
                    float u = (cap[3] + cap[13]) / 2, v = (cap[4] + cap[14]) / 2;
                    sampled &= CelestweaveRaise.lit(
                        argb[Math.min(height - 1, (int) (v * height)) * width
                            + Math.min(width - 1, (int) (u * width))]);
                }
                check(sampled, "raised strip tops sample lit glow texels in " + group.name);
                for (Face glowFace : group.faces) {
                    List<float[]> strips = CelestweaveRaise
                        .raise(List.of(glowFace), argb, width, height, CelestweaveRaise.HEIGHT);
                    if (strips.isEmpty()) continue;
                    float[] lo = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE },
                        hi = { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };
                    for (int q = 0; q < strips.size(); q += 5)
                        for (int i = 0; i < 20; i += 5) for (int k = 0; k < 3; k++) {
                            lo[k] = Math.min(lo[k], strips.get(q)[i + k]);
                            hi[k] = Math.max(hi[k], strips.get(q)[i + k]);
                        }
                    float[] mid = new float[3];
                    for (Vertex vertex : glowFace.vertices) {
                        mid[0] += vertex.x / glowFace.vertices.length;
                        mid[1] += vertex.y / glowFace.vertices.length;
                        mid[2] += vertex.z / glowFace.vertices.length;
                    }
                    float ex = glowFace.faceNormal.x, ey = glowFace.faceNormal.y, ez = glowFace.faceNormal.z;
                    float dx = (lo[0] + hi[0]) / 2 - mid[0], dy = (lo[1] + hi[1]) / 2 - mid[1],
                        dz = (lo[2] + hi[2]) / 2 - mid[2];
                    float along = dx * ex + dy * ey + dz * ez;
                    check(
                        Math.abs(dx - along * ex) < 1.0E-3F && Math.abs(dy - along * ey) < 1.0E-3F
                            && Math.abs(dz - along * ez) < 1.0E-3F,
                        "glow strips are centred on their face in " + group.name);
                }
            }
        }
    }

    private static void firstPersonArm() throws Exception {
        String mixins = new String(Files.readAllBytes(SOURCES.resolve("mixins/Mixins.java")), StandardCharsets.UTF_8);
        check(mixins.contains("\"minecraft.MixinRenderPlayerFirstPersonArm\""), "first person arm mixin is registered");
        String mixin = new String(
            Files.readAllBytes(SOURCES.resolve("mixins/early/minecraft/MixinRenderPlayerFirstPersonArm.java")),
            StandardCharsets.UTF_8);
        check(
            mixin.contains("CelestweaveArmorModel.renderFirstPersonArm(player, modelBipedMain.bipedRightArm)"),
            "first person arm mixin draws the armor over the vanilla right arm");
        CelestweaveFrames frames;
        try (var reader = Files
            .newBufferedReader(RESOURCES.resolve("models/armor/celestweave_frames.json"), StandardCharsets.UTF_8)) {
            frames = new Gson().fromJson(reader, CelestweaveFrames.class);
        }
        frames.resolve();
        check("rightArm".equals(CelestweaveFrames.BONES[CelestweaveFrames.RIGHT_ARM]), "right arm bone index");
        Set<CelestweaveFrames.Layer> armLayers = new HashSet<>();
        for (CelestweaveFrames.Group group : frames.groups(CelestweaveArmor.CHEST))
            if (group.bone == CelestweaveFrames.RIGHT_ARM) armLayers.add(group.layerKind);
        check(armLayers.contains(CelestweaveFrames.Layer.SOLID), "chest has solid parts on the right arm");
        check(armLayers.contains(CelestweaveFrames.Layer.GLOW_FACES), "chest has glow strips on the right arm");
    }

    private static void sounds() throws IOException {
        JsonObject json = new JsonParser()
            .parse(new String(Files.readAllBytes(RESOURCES.resolve("sounds.json")), StandardCharsets.UTF_8))
            .getAsJsonObject();
        Set<String> vanilla = vanillaSounds();
        if (vanilla == null) System.out.println("Vanilla asset index not found, skipping vanilla sound checks");
        for (Map.Entry<String, JsonElement> event : json.entrySet()) {
            for (JsonElement entry : event.getValue()
                .getAsJsonObject()
                .getAsJsonArray("sounds")) {
                String name = entry.isJsonPrimitive() ? entry.getAsString()
                    : entry.getAsJsonObject()
                        .get("name")
                        .getAsString();
                if (name.startsWith("minecraft:")) {
                    if (vanilla != null) check(
                        vanilla.contains("minecraft/sounds/" + name.substring(10) + ".ogg"),
                        "vanilla sound " + name + " in " + event.getKey());
                } else check(Files.exists(RESOURCES.resolve("sounds/" + name + ".ogg")), "sound file " + name);
            }
        }
        String source = new String(
            Files.readAllBytes(SOURCES.resolve("client/celestweave/CelestweaveSounds.java")),
            StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile("play\\([^;]*?\"([a-z.]+)\"")
            .matcher(source);
        int used = 0;
        while (matcher.find()) {
            used++;
            check(json.has("celestweave." + matcher.group(1)), "sound event celestweave." + matcher.group(1));
        }
        check(used >= 10, "sound events found in source " + used);
    }

    private static Set<String> vanillaSounds() throws IOException {
        Path index = Paths
            .get(System.getProperty("user.home"), ".gradle/caches/retro_futura_gradle/assets/indexes/1.7.10.json");
        if (!Files.exists(index)) return null;
        Set<String> sounds = new HashSet<>();
        JsonObject objects = new JsonParser().parse(new String(Files.readAllBytes(index), StandardCharsets.UTF_8))
            .getAsJsonObject()
            .getAsJsonObject("objects");
        for (Map.Entry<String, JsonElement> entry : objects.entrySet()) sounds.add(entry.getKey());
        return sounds;
    }

    private static void soundChannels() throws Exception {
        Object[][] groups = {
            { EquipmentSoundConfig.Celestweave.class, EquipmentSoundConfig.celestweave,
                EquipmentSound.Group.CELESTWEAVE },
            { EquipmentSoundConfig.Railgun.class, EquipmentSoundConfig.railgun, EquipmentSound.Group.RAILGUN } };
        Set<EquipmentSound> covered = EnumSet.noneOf(EquipmentSound.class);
        for (Object[] entry : groups) {
            EquipmentSound.Group group = (EquipmentSound.Group) entry[2];
            for (Field field : ((Class<?>) entry[0]).getFields()) {
                Channel channel = (Channel) field.get(entry[1]);
                String lang = field.getAnnotation(Config.LangKey.class)
                    .value();
                if (field.getName()
                    .equals("all")) {
                    check(group.all == channel, "sound group " + group + " uses its all channel");
                    check(lang.equals("gtnl.gui.config.sound.all"), "sound all lang " + lang);
                    continue;
                }
                EquipmentSound sound = null;
                for (EquipmentSound candidate : EquipmentSound.values())
                    if (candidate.channel == channel) sound = candidate;
                check(sound != null && sound.group == group, "sound channel " + field.getName() + " has a sound");
                if (sound == null) continue;
                covered.add(sound);
                check(
                    sound.lang()
                        .equals(lang),
                    "sound lang " + sound + " matches config " + lang);
            }
        }
        check(covered.size() == EquipmentSound.values().length, "every sound has a config channel");
        StringBuilder sources = new StringBuilder();
        for (String file : new String[] { "client/celestweave/CelestweaveSounds.java",
            "client/railgun/RailgunSounds.java", "client/railgun/RailgunClientFx.java" })
            sources.append(new String(Files.readAllBytes(SOURCES.resolve(file)), StandardCharsets.UTF_8));
        for (EquipmentSound sound : EquipmentSound.values())
            check(sources.indexOf("EquipmentSound." + sound.name()) >= 0, "sound " + sound + " is played");
        Channel all = EquipmentSound.Group.RAILGUN.all, beam = EquipmentSound.BEAM.channel;
        all.volume = 0.5F;
        beam.volume = 0.5F;
        near(0.25, EquipmentSound.BEAM.volume(), "sound volume multiplies the group volume");
        beam.volume = 7F;
        near(0.5, EquipmentSound.BEAM.volume(), "sound volume clamps to 1");
        all.enabled = false;
        check(!EquipmentSound.BEAM.on() && EquipmentSound.BEAM.volume() == 0F, "group switch mutes its sounds");
        check(EquipmentSound.DASH.on(), "group switch leaves the other group");
        all.enabled = true;
        beam.enabled = false;
        check(!EquipmentSound.BEAM.on() && EquipmentSound.CHAIN.on(), "sound switch mutes only its sound");
        beam.enabled = true;
        all.volume = 1F;
        beam.volume = 1F;
        double[] held = { 0.37 };
        StepSlider slider = new StepSlider().value(() -> held[0], value -> held[0] = value)
            .range(0, 1, 20);
        check(slider.step() == 7, "slider rounds to the nearest notch");
        near(0.35, slider.valueAt(slider.step()), "slider notch value");
        held[0] = 4;
        check(slider.step() == 20, "slider clamps above the range");
        near(0, slider.valueAt(-3), "slider clamps below the range");
        for (boolean vertical : new boolean[] { false, true }) {
            for (int length : new int[] { 14, 60, 92 }) {
                for (int step = 0; step <= 20; step++) {
                    float at = StepSlider.notchPosition(step, 20, length, vertical);
                    check(at >= 1 && at <= length - 2, "slider notch inside the track " + vertical + " " + length);
                    if (length >= 60) check(
                        StepSlider.nearestStep(Math.round(at), 20, length, vertical) == step,
                        "slider notch round trip " + vertical + " " + length + " " + step);
                }
            }
        }
        check(
            StepSlider.notchPosition(0, 20, 60, true) > StepSlider.notchPosition(20, 20, 60, true),
            "vertical slider grows upward");
        check(
            StepSlider.nearestStep(-50, 20, 60, false) == 0 && StepSlider.nearestStep(500, 20, 60, false) == 20,
            "slider clamps the mouse");
        IntValue level = new IntValue(3);
        StepSlider bound = new StepSlider().value(level)
            .range(1, 5, 4);
        check(bound.step() == 2, "slider reads an int value");
        Field setterField = StepSlider.class.getDeclaredField("setter");
        setterField.setAccessible(true);
        ((DoubleConsumer) setterField.get(bound)).accept(3.9999);
        check(level.getIntValue() == 4, "slider rounds into an int value instead of truncating");
        DoubleValue speed = new DoubleValue(1.5);
        StepSlider decimal = new StepSlider().value(speed)
            .range(0.5, 3, 25);
        check(decimal.step() == 10, "slider reads a double value");
        ((DoubleConsumer) setterField.get(decimal)).accept(2.25);
        near(2.25, speed.getDoubleValue(), "slider writes a double value");
        check(StepSlider.compactWidth("100%") == 15, "compact width of 100%");
        check(StepSlider.compactWidth("12.5%") == 17, "compact width counts the narrow dot");
        check(
            StepSlider.compactFits("-12.5%") && !StepSlider.compactFits("1e5") && !StepSlider.compactFits(""),
            "compact font charset");
        Field chars = StepSlider.class.getDeclaredField("COMPACT_CHARS"),
            glyphs = StepSlider.class.getDeclaredField("COMPACT_GLYPHS");
        chars.setAccessible(true);
        glyphs.setAccessible(true);
        check(
            ((String) chars.get(null)).length() == ((int[]) glyphs.get(null)).length,
            "every compact char has a glyph");
        Field tag = EquipmentSoundScreen.class.getDeclaredField("TAG_WIDTH");
        tag.setAccessible(true);
        check(StepSlider.compactWidth("100%") + 4 <= tag.getInt(null), "sound slider tag fits 100%");
    }

    private static void equipmentTheme() throws Exception {
        check(
            HudColor.guide() == 0x80FFFFFF && HudColor.selection() == 0xFFFFFFFF,
            "hud editor framework colors are neutral");
        DrawableSerialization.init();
        EquipmentTheme.register();
        String standard = GTGuiThemes.STANDARD.getId();
        IThemeApi.get()
            .registerTheme(standard, new JsonBuilder());
        reloadThemes();
        ITheme theme = IThemeApi.get()
            .getTheme(EquipmentTheme.ID);
        check(
            theme.getId()
                .equals(EquipmentTheme.ID),
            "equipment theme is registered");
        check(
            theme.getParentTheme()
                .getId()
                .equals(standard),
            "equipment theme inherits the GT standard theme");
        Map<String, Integer> expected = new HashMap<>();
        expected.put("accent", 0xFFFF3D9A);
        expected.put("accentDim", 0x80FF3D9A);
        expected.put("hint", 0xFF8E8796);
        WidgetThemeEntry<Colors> entry = theme.getWidgetTheme(EquipmentTheme.COLORS);
        for (Colors colors : new Colors[] { entry.getTheme(), entry.getHoverTheme(), EquipmentTheme.colors(),
            EquipmentTheme.COLORS.getDefaultValue() }) {
            int fields = 0;
            for (Field field : Colors.class.getFields()) {
                if (field.getType() != int.class || Modifier.isStatic(field.getModifiers())) continue;
                fields++;
                Integer color = expected.get(field.getName());
                check(
                    color != null && field.getInt(colors) == color,
                    "equipment color " + field.getName() + " keeps its old value");
            }
            check(fields == expected.size(), "equipment palette keeps only the colors code reads");
        }
        StepSlider.Theme slider = theme.getWidgetTheme(StepSlider.Theme.KEY)
            .getTheme();
        check(
            slider.track == 0xFF1A1420 && slider.fill == 0xFFFF3D9A
                && slider.cursor == 0xFFFFE6F1
                && slider.cursorText == 0xFF4A0A2A
                && slider.tick == 0x40FFFFFF
                && slider.milestone == 0x99FFFFFF,
            "equipment theme keeps the old sound slider colors");
        StepSlider.Theme defaults = StepSlider.Theme.KEY.getDefaultValue();
        check(
            defaults.track == 0xFF1E1A22 && defaults.fill == 0xFFB98A3C
                && defaults.cursor == 0xFFF4E2B0
                && defaults.cursorText == 0xFF442000
                && defaults.tick == 0x55FFFFFF
                && defaults.milestone == 0xB0FFFFFF,
            "slider default colors stay the old generic ones");
        ITheme plain = IThemeApi.get()
            .getTheme(standard);
        check(
            plain.getWidgetTheme(StepSlider.Theme.KEY)
                .getTheme().fill == defaults.fill,
            "a theme without slider colors falls back to the slider defaults");
        StepSlider themed = new StepSlider();
        check(
            themed.getWidgetTheme(theme)
                .getTheme() == slider,
            "slider takes its widget theme from the screen theme");
        Class<?>[] resolve = { WidgetTheme.class };
        check(
            call(StepSlider.class, "resolveColors", themed, resolve, slider) == slider,
            "slider without explicit colors draws with the theme colors");
        check(
            call(
                StepSlider.class,
                "resolveColors",
                themed,
                resolve,
                plain.getFallback()
                    .getTheme())
                == defaults,
            "slider outside a slider theme draws with the default colors");
        StepSlider.Theme ticks = (StepSlider.Theme) call(
            StepSlider.class,
            "resolveColors",
            new StepSlider().tickColors(1, 2),
            resolve,
            slider);
        check(
            ticks.track == slider.track && ticks.fill == slider.fill
                && ticks.cursor == slider.cursor
                && ticks.cursorText == slider.cursorText
                && ticks.tick == 1
                && ticks.milestone == 2,
            "explicit tick colors override only the ticks");
        StepSlider.Theme main = (StepSlider.Theme) call(
            StepSlider.class,
            "resolveColors",
            new StepSlider().colors(3, 4, 5, 6),
            resolve,
            slider);
        check(
            main.track == 3 && main.fill == 4
                && main.cursor == 5
                && main.cursorText == 6
                && main.tick == slider.tick
                && main.milestone == slider.milestone,
            "explicit colors override only the track, fill and cursor");
        check(
            HudColor.guide() == 0x80FF3D9A && HudColor.selection() == 0xFFFF3D9A,
            "equipment theme hands its accent to the hud editor");
        equipmentControls(plain);
        paletteThemes(standard);
        liveThemes(standard);
        themeChoice();
        Pattern hex = Pattern.compile("0x[0-9A-Fa-f]{6,8}");
        for (String file : new String[] { "common/gui/equipment/EquipmentSoundScreen.java",
            "client/celestweave/CelestweaveWheelScreen.java", "client/hud/HudEditor.java", "client/hud/HudCanvas.java",
            "client/hud/HudSettingsCard.java", "client/hud/HudColor.java",
            "common/gui/modularui/widget/StepSlider.java", "common/gui/modularui/theme/ThemePalette.java",
            "common/gui/modularui/widget/FrameDrawable.java", "common/gui/modularui/widget/CardDrawable.java",
            "common/gui/modularui/widget/SlideSwitch.java", "common/gui/modularui/theme/ThemeStyle.java",
            "common/gui/modularui/widget/FillBar.java", "common/gui/modularui/theme/PaletteThemes.java",
            "common/gui/modularui/theme/ButtonPurpose.java", "common/gui/modularui/layout/Layout.java",
            "common/gui/modularui/layout/LayoutMath.java", "common/gui/modularui/layout/LayoutTheme.java",
            "common/gui/modularui/layout/Size.java" }) {
            String source = new String(Files.readAllBytes(SOURCES.resolve(file)), StandardCharsets.UTF_8);
            check(
                !source.toUpperCase(Locale.ROOT)
                    .contains("3D9A"),
                file + " has no hard-coded equipment pink");
            if (!file.contains("modularui")) check(
                file.contains("client/hud") || !hex.matcher(source)
                    .find(),
                file + " takes every color from the equipment theme");
            else check(
                !source.contains("Equipment") && !source.contains("Celestweave") && !source.contains("Railgun"),
                file + " names no feature");
        }
        reloadThemes(
            new JsonBuilder().add("gtnlEquipment", new JsonBuilder().add("accentColor", 0xFF00FF00))
                .add("gtnlStepSlider", new JsonBuilder().add(StepSlider.Theme.FILL, 0xFF00FF00)));
        Colors overridden = EquipmentTheme.colors();
        StepSlider.Theme overriddenSlider = IThemeApi.get()
            .getTheme(EquipmentTheme.ID)
            .getWidgetTheme(StepSlider.Theme.KEY)
            .getTheme();
        check(
            overridden.accent == 0xFF00FF00 && overridden.hint == expected.get("hint")
                && HudColor.selection() == 0xFF00FF00,
            "an override of one equipment color keeps the others");
        check(
            overriddenSlider.fill == 0xFF00FF00 && overriddenSlider.track == defaults.track,
            "an override replaces the whole slider object");
        reloadThemes(
            new JsonBuilder().add(
                LayoutTheme.KEY.getFullName(),
                new JsonBuilder().add(LayoutTheme.SPACING_SMALL, 5)
                    .add(LayoutTheme.ROW_HEIGHT, 3)
                    .add(LayoutTheme.TEXT_SCALE, -1)));
        LayoutTheme packed = EquipmentTheme.layout();
        check(
            packed.spacingSmall == 5 && packed.padding == ThemeStyle.DEFAULT.padding()
                && packed.rowHeight == packed.buttonHeight
                && packed.textScale == ThemeStyle.DEFAULT.textScale(),
            "a resource pack changes one spacing, keeps the others and cannot make rows shorter than buttons");
        reloadThemes();
        check(
            EquipmentTheme.layout().spacingSmall == ThemeStyle.DEFAULT.spacingSmall(),
            "theme reload restores spacing");
        check(EquipmentTheme.colors().accent == expected.get("accent"), "theme reload restores the accent");
    }

    private static void equipmentScreens() throws IOException {
        for (String file : new String[] { "common/item/items/armor/CelestweaveArmor.java",
            "common/item/items/equipment/ElectromagneticRailgun.java", "common/gui/equipment/EquipmentSoundScreen.java",
            "client/celestweave/CelestweaveWheelScreen.java" }) {
            String source = new String(Files.readAllBytes(SOURCES.resolve(file)), StandardCharsets.UTF_8);
            check(
                source.contains("useTheme(EquipmentTheme.selected())") && !source.contains("EquipmentTheme.ID")
                    && !source.contains("GTGuiThemes"),
                file + " opens with the equipment theme chosen in the config");
        }
        Map<String, String> sources = new HashMap<>();
        for (String name : new String[] { "EquipmentGui", "CelestweaveGui", "RailgunGui" }) sources.put(
            name,
            new String(
                Files.readAllBytes(SOURCES.resolve("common/gui/equipment/" + name + ".java")),
                StandardCharsets.UTF_8));
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String source = entry.getValue();
            for (String gtTexture : new String[] { "GTGuiTextures", "GTGuis", "PROGRESSBAR", "BUTTON_STANDARD" })
                check(!source.contains(gtTexture), entry.getKey() + " leaves " + gtTexture + " to the theme");
            for (String code : new String[] { "§8", "§a", "§c", "§e" })
                check(!source.contains(code), entry.getKey() + " colors its text through the theme, not " + code);
        }
        String base = sources.get("EquipmentGui"), armor = sources.get("CelestweaveGui");
        check(
            base.contains("new ModularPanel(name)") && base.contains("closeButton().size("),
            "settings panels use the theme panel and close button");
        check(base.contains("new FillBar()"), "settings energy bar is a themed fill bar");
        check(base.contains("new SlideSwitch()"), "settings switches are slide switches");
        check(armor.contains("new BoolValue.Dynamic(() -> page == index"), "armor pages are themed tabs");
        check(
            armor.contains("new SlideSwitch().value(() -> (state.getIntValue() & ACTIVE) != 0)")
                && armor.contains(".liveWhen(() -> (state.getIntValue() & SHOWN) != 0)"),
            "armor modules are slide switches that dim when hidden");
        check(armor.contains("new ToggleButton().value(value)"), "narrow arc event flags are themed toggle buttons");
        check(
            armor.contains(".background(IDrawable.EMPTY)") && armor.contains(".disableThemeBackground(false)"),
            "color picker uses the theme panel instead of the vanilla background");
        int swatches = armor.split("disableHoverThemeBackground\\(true\\)", -1).length - 1;
        check(swatches == 2, "color swatches and the picker button turn off both theme backgrounds");
        check(!armor.contains("hidden_mark"), "hidden modules show in the tooltip instead of a label mark");
    }

    private static void layoutRules() throws IOException {
        int available = 160, gap = 2, shortSwitch = SlideSwitch.widthFor(56);
        check(
            SlideSwitch.widthFor(0) == 20 && shortSwitch == 76,
            "a switch needs its track, its label and a small margin");
        check(LayoutMath.columns(available, gap, shortSwitch, 2) == 2, "short labels keep two columns");
        check(
            Arrays.equals(LayoutMath.split(available, gap, 2), new int[] { 79, 79 }),
            "two columns keep the old 79 pixel cells");
        for (int text : new int[] { 62, 120, 400 }) {
            int columns = LayoutMath.columns(available, gap, SlideSwitch.widthFor(text), 2);
            check(
                columns == 1 && LayoutMath.total(LayoutMath.split(available, gap, columns), gap) == available,
                "a " + text + " pixel label drops to one column that stays inside the panel");
        }
        check(
            LayoutMath.columns(available, gap, 79, 2) == 2 && LayoutMath.columns(available, gap, 80, 2) == 1,
            "two columns until a cell no longer fits");
        check(LayoutMath.columns(available, gap, 0, 2) == 2, "unmeasured labels on the server keep the most columns");
        check(LayoutMath.columns(available, 1, 10, 7) == 7, "a column count is an upper bound");
        check(
            Arrays.equals(LayoutMath.split(available, gap, 3), new int[] { 52, 52, 52 }),
            "three tabs keep the old 52 pixel tabs");
        check(
            Arrays.equals(LayoutMath.split(available, 1, 7), new int[] { 22, 22, 22, 22, 22, 22, 22 }),
            "seven arc flags keep the old 22 pixel buttons");
        for (int tabs = 1; tabs <= 8; tabs++) {
            int[] widths = LayoutMath.split(available, gap, tabs);
            int narrowest = Integer.MAX_VALUE, widest = 0;
            for (int width : widths) {
                narrowest = Math.min(narrowest, width);
                widest = Math.max(widest, width);
            }
            check(
                widths.length == tabs && LayoutMath.total(widths, gap) == available && widest - narrowest <= 1,
                tabs + " tabs share the row evenly and fill it");
        }
        check(
            Arrays.equals(
                LayoutMath.line(
                    Arrays.asList(Size.fixed(20), Size.fill(), Size.percent(0.25F), Size.content(24, 3)),
                    available,
                    gap),
                new int[] { 20, 64, 40, 30 }),
            "fixed, fill, percent and content sizes share one row");
        check(
            Arrays.equals(LayoutMath.line(Arrays.asList(Size.fill(1), Size.fill(3)), 100, 0), new int[] { 25, 75 }),
            "fill splits the rest by weight");
        check(
            Arrays.equals(
                LayoutMath.line(
                    Arrays.asList(
                        Size.fill()
                            .atMost(30),
                        Size.fill()),
                    100,
                    0),
                new int[] { 30, 70 }),
            "a capped fill hands the rest to the other fill");
        check(
            Arrays.equals(
                LayoutMath.line(
                    Arrays.asList(
                        Size.fill()
                            .atLeast(80),
                        Size.fill()),
                    100,
                    0),
                new int[] { 80, 20 }),
            "a minimum wins over an even share");
        check(
            Size.percent(0.5F)
                .atMost(30)
                .resolve(100) == 30
                && Size.content(10)
                    .atLeast(72)
                    .resolve(0) == 72
                && Size.fixed(14)
                    .resolve(500) == 14,
            "minimum and maximum clamp every size kind");
        int[] stepper = LayoutMath.line(Arrays.asList(Size.fill(), Size.fixed(32)), available, 4);
        check(
            stepper[0] == 124 && 8 + stepper[0] + 4 == 136,
            "stepper buttons stay at x 136 with the label filling the rest");
        List<Size> colorRow = new ArrayList<>();
        colorRow.add(Size.fill());
        for (int i = 0; i < 7; i++) colorRow.add(Size.fixed(12));
        colorRow.add(Size.fixed(20));
        int[] colors = LayoutMath.line(colorRow, available, gap);
        check(
            colors[0] == 40 && 8 + LayoutMath.total(Arrays.copyOf(colors, 8), gap) + gap == 148,
            "color swatches start at x 50 and the picker stays at x 148");
        check(
            Arrays.equals(LayoutMath.wrap(new int[] { 50, 50, 50 }, 120, 2), new int[] { 0, 0, 1 }),
            "an item that no longer fits moves to the next row");
        check(
            LayoutMath.wrap(new int[] { 200 }, 120, 2)[0] == 0,
            "an item wider than the row still starts the first row");
        int[] shrunk = LayoutMath.shrink(new int[] { 100, 100 }, new int[] { 40, 80 }, 150, 0);
        check(
            LayoutMath.total(shrunk, 0) == 150 && shrunk[0] >= 40 && shrunk[1] >= 80 && shrunk[0] < shrunk[1],
            "shrinking takes the overflow from the room above each minimum");
        check(
            Arrays.equals(LayoutMath.shrink(new int[] { 50, 50 }, new int[] { 50, 50 }, 80, 0), new int[] { 50, 50 }),
            "nothing shrinks below its minimum");
        int[] cards = { 200, 200 }, heights = { 150, 118 };
        check(!LayoutMath.stacks(cards, heights, 10, 414, 200), "a wide screen keeps the sound cards side by side");
        check(LayoutMath.stacks(cards, heights, 10, 380, 300), "a narrow screen stacks the sound cards");
        check(
            !LayoutMath.stacks(cards, heights, 10, 380, 250),
            "a narrow and short screen keeps the cards side by side instead of cutting them off");
        Layout rows = Layout.rows(LayoutTheme.DEFAULT, 176);
        check(
            rows.available() == 160 && rows.inset(4)
                .available() == 168,
            "rows keep the theme padding on both sides");
        List<SlideSwitch> five = new ArrayList<>();
        for (int i = 0; i < 5; i++) five.add(new SlideSwitch());
        List<IWidget> grid = rows.grid(gap, 2, five);
        check(
            grid.size() == 3 && ((Flow) grid.get(0)).getChildren()
                .size() == 2
                && ((Flow) grid.get(2)).getChildren()
                    .size() == 1,
            "a grid fills its rows left to right");
        check(
            rows.wrap(gap, Collections.nCopies(3, Size.fixed(70)), five.subList(0, 3))
                .size() == 2,
            "a wrapping row breaks where the next item no longer fits");
        Map<String, String> sources = new HashMap<>();
        for (String file : new String[] { "common/gui/equipment/EquipmentGui.java",
            "common/gui/equipment/CelestweaveGui.java", "common/gui/equipment/RailgunGui.java",
            "common/gui/equipment/EquipmentSoundScreen.java", "client/celestweave/CelestweaveWheelScreen.java" })
            sources.put(file, new String(Files.readAllBytes(SOURCES.resolve(file)), StandardCharsets.UTF_8));
        String base = compact(sources.get("common/gui/equipment/EquipmentGui.java")),
            armor = compact(sources.get("common/gui/equipment/CelestweaveGui.java")),
            railgun = compact(sources.get("common/gui/equipment/RailgunGui.java")),
            sound = compact(sources.get("common/gui/equipment/EquipmentSoundScreen.java")),
            wheel = compact(sources.get("client/celestweave/CelestweaveWheelScreen.java"));
        String columns = "rows.columns(style.spacingSmall,widestSwitch(labels),MAX_COLUMNS)";
        check(
            armor.contains(columns) && railgun.contains(columns),
            "module and setting switches pick their column count from the widest label");
        check(
            armor.contains("Collections.nCopies(tabs.size(),Size.fill())") && armor.contains("ButtonPurpose.TAB"),
            "armor tabs share the row evenly");
        check(
            base.contains("EquipmentTheme.layout()") && sound.contains("EquipmentTheme.layout()")
                && wheel.contains("EquipmentTheme.layout()"),
            "settings pages, the sound screen and the wheel read their spacing from the selected theme");
        check(
            sound.contains("LayoutMath.stacks(")
                && sound.contains("publicvoidonResize(intwidth,intheight){arrangement.fit(width,height);"),
            "the sound screen stacks its cards on a narrow screen, also after the window is resized");
        check(sound.contains(".atLeast(LABEL_WIDTH)"), "the sound name column grows with the longest name");
        check(
            wheel.contains("LayoutMath.widest(") && wheel.contains(".atLeast(BUTTON_WIDTH)")
                && wheel.contains("(width+style.spacingMedium)/2"),
            "wheel buttons share the width of the longer label and stay centered");
        Pattern oldPixels = Pattern.compile("\\b(79|81|54|98|108|122|136|148|152|160|161)\\b");
        for (Map.Entry<String, String> entry : sources.entrySet()) check(
            !oldPixels.matcher(entry.getValue())
                .find(),
            entry.getKey() + " has no hard-coded column, tab or row positions left");
    }

    private static String compact(String source) {
        return source.replaceAll("\\s+", "");
    }

    private static void equipmentControls(ITheme plain) throws Exception {
        JsonObject registered = IThemeApi.get()
            .getJavaDefaultThemes(EquipmentTheme.ID)
            .get(0)
            .getJson()
            .getAsJsonObject(IThemeApi.PANEL.getFullName());
        check(
            "none".equals(
                registered.get(IThemeApi.BACKGROUND)
                    .getAsString()),
            "MUI2 overwrites the background of a registered theme when it loads it");
        String proxy = new String(Files.readAllBytes(SOURCES.resolve("ClientProxy.java")), StandardCharsets.UTF_8);
        int reloader = proxy.indexOf("EVENT_BUS.register(new PaletteThemes.Reloader())"),
            equipment = proxy.indexOf("EquipmentTheme.register()");
        check(reloader >= 0 && equipment > reloader, "palette themes are rebuilt before every theme reload");
        String soundSource = new String(
            Files.readAllBytes(SOURCES.resolve("common/gui/equipment/EquipmentSoundScreen.java")),
            StandardCharsets.UTF_8);
        check(
            !soundSource.contains("GuiDraw") && !soundSource.contains("disableThemeBackground")
                && soundSource.contains("new SlideSwitch()")
                && soundSource.contains("Layout.card(")
                && soundSource.contains("EquipmentGui.closeButton()"),
            "sound screen draws its panel, cards, switches and close button through the theme");
        String layoutSource = new String(
            Files.readAllBytes(SOURCES.resolve("common/gui/modularui/layout/Layout.java")),
            StandardCharsets.UTF_8);
        check(layoutSource.contains("widgetTheme(CardDrawable.KEY)"), "layout cards draw with the theme card");
        check(
            compact(layoutSource).contains("returnnewScrollingTextWidget(key)") && compact(layoutSource).contains(
                "RenderUtils.drawTextFitted(key.getFormatted(),x,y,width,height,Alignment.Center,theme.getTextColor(),theme.getTextShadow(),style.textScale)"),
            "labels and button captions cut text that does not fit instead of spilling out");
        reloadThemes();
        ITheme theme = IThemeApi.get()
            .getTheme(EquipmentTheme.ID);
        ThemePalette pink = new ThemePalette(
            0xFFFF3D9A,
            0x80FF3D9A,
            0x26FF3D9A,
            0xFFFFFFFF,
            0xFF8E8796,
            0xF2211A27,
            0xF20D0A11,
            0x50000000,
            0x24FFFFFF,
            0x9614141E,
            0xA0FF3D9A,
            0x50FF3D9A,
            0x80000000,
            0xFF3A3640,
            0x50FF3D9A,
            0xFF1A1420,
            0xFF3A3640,
            0xFFF2EEF4,
            0xFF8C8794,
            0xFFF4F0F6,
            0xFF6E6876,
            0xFF9A94A2,
            0xFFFFE6F1,
            0xFF4A0A2A,
            0x40FFFFFF,
            0x99FFFFFF,
            0xFFE5484D);
        check(
            EquipmentTheme.PALETTE.equals(pink),
            "equipment palette keeps every old color: " + EquipmentTheme.PALETTE);
        check(
            ThemeStyle.DEFAULT.equals(new ThemeStyle(6, 2, 3, 2, 10, 2, 4, 10, 8, 14, 16, 1F)),
            "default theme style keeps the old corner, card accent, spacing, button and row sizes");
        paletteControls("equipment", theme, pink, ThemeStyle.DEFAULT);
        SlideSwitch.Theme slide = theme.getWidgetTheme(SlideSwitch.Theme.KEY)
            .getTheme();
        check(
            SlideSwitch.trackColor(slide, false, false) == slide.track
                && SlideSwitch.trackColor(slide, true, true) == slide.trackOn
                && SlideSwitch.trackColor(slide, true, false) == slide.trackDim,
            "switch track shows off, on and on without effect");
        check(
            SlideSwitch.textColor(slide, false, false, true) == slide.textOff
                && SlideSwitch.textColor(slide, false, false, false) == slide.textDim
                && SlideSwitch.textColor(slide, true, true, true) == slide.textOn
                && SlideSwitch.textColor(slide, true, false, true) == slide.textDim,
            "switch label tells off, unavailable, on and on without effect apart");
        check(
            FillBar.filledWidth(0.5, 160) == 80 && FillBar.filledWidth(-1, 160) == 0
                && FillBar.filledWidth(2, 160) == 160
                && FillBar.filledWidth(Double.NaN, 160) == 0,
            "fill bar clamps its value");
        WidgetTheme neutralCard = plain.getWidgetTheme(CardDrawable.KEY)
            .getTheme();
        SlideSwitch.Theme neutralSlide = plain.getWidgetTheme(SlideSwitch.Theme.KEY)
            .getTheme();
        FillBar.Theme neutralBar = plain.getWidgetTheme(FillBar.Theme.KEY)
            .getTheme();
        boolean neutral = neutralCard.getBackground() instanceof CardDrawable && !pink(neutralBar.track)
            && !pink(neutralBar.fill);
        for (Field color : SlideSwitch.Theme.class.getFields())
            if (color.getType() == int.class && !Modifier.isStatic(color.getModifiers()))
                neutral &= !pink(color.getInt(neutralSlide));
        for (String color : new String[] { "fill", "edge", "accent" })
            neutral &= !pink((int) field(neutralCard.getBackground(), color));
        check(neutral, "generic controls fall back to neutral colors outside the equipment theme");
    }

    private static void paletteThemes(String standard) throws Exception {
        JsonElement golden = new JsonParser().parse(
            new String(
                Files.readAllBytes(
                    Paths.get("src/test/resources/com/science/gtnl/client/celestweave/palette_theme.json")),
                StandardCharsets.UTF_8));
        check(
            golden.equals(
                PaletteThemes.build(EquipmentTheme.ID, standard, EquipmentTheme.PALETTE, ThemeStyle.DEFAULT)
                    .getJson()),
            "palette theme builder writes exactly the equipment theme that was written by hand before");
        ThemePalette inverted = EquipmentTheme.PALETTE.map(color -> color ^ 0x00FFFFFF);
        List<Integer> before = components(EquipmentTheme.PALETTE), after = components(inverted);
        check(before.size() == 27 && after.size() == 27, "palette maps every color");
        for (int i = 0; i < after.size(); i++) check(
            !after.get(i)
                .equals(before.get(i)) && !pink(after.get(i)),
            "test palette color " + i + " differs from the equipment palette and is not pink");
        ThemeStyle style = new ThemeStyle(4, 1, 2, 3, 8, 3, 6, 12, 6, 16, 20, 0.75F);
        PaletteThemes.register(TEST_THEME, standard, inverted, style);
        reloadThemes();
        ITheme theme = IThemeApi.get()
            .getTheme(TEST_THEME);
        check(
            theme.getId()
                .equals(TEST_THEME)
                && PaletteThemes.ids()
                    .contains(TEST_THEME)
                && inverted.equals(PaletteThemes.palette(TEST_THEME)),
            "a palette theme registers under its own id");
        paletteControls("test palette", theme, inverted, style);
        reloadThemes();
        sameFrame(
            "test palette panel after a second reload",
            IThemeApi.get()
                .getTheme(TEST_THEME)
                .getPanelTheme()
                .getTheme()
                .getBackground(),
            new int[] { inverted.panelTop(), inverted.panelBottom(), inverted.accentDim(), inverted.accent(),
                style.cornerLength(), style.cornerThickness() });
    }

    private static void liveThemes(String standard) throws Exception {
        ThemePalette palette = EquipmentTheme.PALETTE;
        check(
            palette.follow(0xFF3D9A)
                .equals(palette),
            "the default pink glow gives back every equipment color");
        check(
            palette.follow(0x12FF3D9A)
                .equals(palette),
            "the glow alpha is ignored");
        ThemePalette cyan = palette.follow(CYAN);
        same("cyan accent", cyan.accent(), 0xFF3DE8FF);
        same("cyan accent dim", cyan.accentDim(), 0x803DE8FF);
        same("cyan hover", cyan.hover(), 0x263DE8FF);
        same("cyan button hover", cyan.buttonHover(), 0xA03DE8FF);
        same("cyan tab hover", cyan.tabHover(), 0x503DE8FF);
        same("cyan slot hover", cyan.slotHover(), 0x503DE8FF);
        same("cyan danger", cyan.danger(), palette.danger());
        List<Integer> base = components(palette), tinted = components(cyan);
        for (int i = 0; i < base.size(); i++) {
            int from = base.get(i), to = tinted.get(i);
            double[] before = FxColor.hsl(from & 0xFFFFFF), after = FxColor.hsl(to & 0xFFFFFF);
            check(!pink(to), "cyan color " + i + " has no pink left");
            check(from >>> 24 == to >>> 24, "cyan color " + i + " keeps its transparency");
            if (before[1] < 0.5) check(
                Math.abs(before[2] - after[2]) < 0.01,
                "cyan color " + i + " keeps the brightness of the pink one");
        }
        check(
            FxColor.hsl(cyan.cursor() & 0xFFFFFF)[2] > 0.9 && FxColor.hsl(cyan.cursorText() & 0xFFFFFF)[2] < 0.25,
            "the slider tag stays a pale tint with dark text");
        check(
            Math.abs(hueOffset(cyan.cursor(), cyan.accent()) - hueOffset(palette.cursor(), palette.accent()))
                < HUE_TOLERANCE
                && Math.abs(hueOffset(cyan.panelTop(), cyan.accent()) - hueOffset(palette.panelTop(), palette.accent()))
                    < HUE_TOLERANCE,
            "tints and the dark panel keep their hue next to the new accent");
        ThemePalette navy = palette.follow(0x2040A0);
        check(
            Math.abs(whiteMix(navy.cursor(), navy.accent()) - whiteMix(palette.cursor(), palette.accent())) < 0.02
                && Math
                    .abs(blackMix(navy.cursorText(), navy.accent()) - blackMix(palette.cursorText(), palette.accent()))
                    < 0.02,
            "pale and dark tints keep their white and black mix toward a darker accent");
        check(
            FxColor.hsl(
                palette.follow(0xFFFFFF)
                    .panelTop() & 0xFFFFFF)[1]
                < 0.01,
            "a grey glow leaves the dark panel untinted");
        ThemePalette grey = palette.follow(0x808080), greyToCyan = grey.follow(CYAN);
        same("grey theme accent", grey.accent(), 0xFF808080);
        same("grey theme following cyan, accent", greyToCyan.accent(), 0xFF3DE8FF);
        same("grey theme following cyan, accent dim", greyToCyan.accentDim(), 0x803DE8FF);
        for (int glow : new int[] { 0x000000, 0xFFFFFF, 0x0000FF }) {
            int accent = palette.readableAccent(glow);
            check(
                FxColor.contrast(accent & 0xFFFFFF, palette.panelTop() & 0xFFFFFF) >= 3 && palette.follow(glow)
                    .accent() == accent,
                "a " + Integer.toHexString(glow) + " glow becomes an accent readable on the dark panel");
        }
        check(palette.readableAccent(0x000000) != 0xFF000000, "a black glow is lifted");
        check(FxColor.hsl(palette.readableAccent(0xFFFFFF) & 0xFFFFFF)[2] <= 0.851, "a white glow is toned down");
        same("pink accent after the readability check", palette.readableAccent(0xFF3D9A), 0xFFFF3D9A);
        check(
            palette.selectedText() == palette.text() && palette.selectedTextShadow(),
            "pink tabs keep white text with a shadow");
        ThemePalette white = palette.follow(0xFFFFFF);
        int tab = FxColor.over(white.buttonHover(), white.panelTop() & 0xFFFFFF);
        check(
            white.selectedText() != white.text() && !white.selectedTextShadow()
                && FxColor.contrast(white.selectedText() & 0xFFFFFF, tab)
                    > FxColor.contrast(white.text() & 0xFFFFFF, tab),
            "bright tabs switch to dark text without a shadow");
        check(
            palette.follow(0x000000)
                .selectedText() == palette.text(),
            "dark tabs keep light text");
        reloadThemes();
        new PaletteThemes.Reloader().onThemeReload(new ReloadThemeEvent.Pre());
        ITheme registered = IThemeApi.get()
            .getTheme(EquipmentTheme.ID);
        ITheme parsed = VariantTheme.parse(
            EquipmentTheme.ID,
            IThemeApi.get()
                .getTheme(standard),
            IThemeApi.get()
                .getJavaDefaultThemes(EquipmentTheme.ID)
                .get(0)
                .getJson());
        for (WidgetThemeKey<?> key : IThemeApi.get()
            .getWidgetThemeKeys()) {
            WidgetThemeEntry<?> expected = registered.getWidgetTheme(key), actual = parsed.getWidgetTheme(key);
            check(
                sameWidgetTheme(expected.getTheme(), actual.getTheme())
                    && sameWidgetTheme(expected.getHoverTheme(), actual.getHoverTheme()),
                "a palette variant parses " + key.getFullName() + " the way ModularUI does");
        }
        reloadThemes();
        int[] glow = { LivePalette.NO_ACCENT };
        EquipmentTheme.LIVE.accentFrom(() -> glow[0]);
        ITheme theme = IThemeApi.get()
            .getTheme(EquipmentTheme.ID), view = PaletteThemes.forScreen(theme);
        check(view == EquipmentTheme.LIVE.theme(), "equipment screens get the live theme");
        check(
            PaletteThemes.forScreen(
                IThemeApi.get()
                    .getTheme(standard))
                == IThemeApi.get()
                    .getTheme(standard),
            "other screens keep their own theme");
        check(
            EquipmentTheme.LIVE.current()
                .equals(palette) && view.getPanelTheme() == theme.getPanelTheme(),
            "without a chestplate the live theme is the registered theme");
        glow[0] = 0xFF000000 | CYAN;
        EquipmentTheme.LIVE.refresh();
        check(
            EquipmentTheme.LIVE.current()
                .equals(cyan)
                && EquipmentTheme.LIVE.themed()
                    .equals(palette),
            "a chestplate glow tints the live palette");
        paletteControls("live cyan", view, cyan, ThemeStyle.DEFAULT);
        check(
            HudColor.selection() == cyan.accent() && HudColor.guide() == cyan.accentDim()
                && EquipmentTheme.colors().accent == cyan.accent(),
            "hud editor guides and code colors follow the glow");
        WidgetThemeEntry<WidgetTheme> tintedPanel = view.getPanelTheme();
        check(tintedPanel == view.getPanelTheme(), "a tinted theme is built once per glow");
        reloadThemes();
        check(view.getPanelTheme() != tintedPanel, "a theme reload rebuilds the tinted theme on the new parent");
        LivePalette manual = new LivePalette(() -> EquipmentTheme.ID, () -> false);
        manual.accentFrom(() -> glow[0]);
        check(
            manual.current()
                .equals(palette)
                && manual.accented()
                    .equals(cyan),
            "a live palette that does not follow the glow keeps the theme colors");
        glow[0] = LivePalette.NO_ACCENT;
        EquipmentTheme.LIVE.refresh();
        check(
            view.getPanelTheme() == IThemeApi.get()
                .getTheme(EquipmentTheme.ID)
                .getPanelTheme(),
            "taking the chestplate off goes back to the theme colors");
        EquipmentTheme.LIVE.accentFrom(() -> LivePalette.NO_ACCENT);
        ItemStack chest = new ItemStack(new Item());
        check(CelestweaveThemeAccent.accent(null) == LivePalette.NO_ACCENT, "no chestplate gives no accent");
        check(CelestweaveThemeAccent.accent(chest) == 0xFFFF3D9A, "a chestplate with the default glow gives pink");
        CelestweaveData.setCosmetic(chest, CelestweaveCosmeticOption.GLOW_COLOR, CYAN);
        check(CelestweaveThemeAccent.accent(chest) == 0xFF3DE8FF, "the chestplate glow color becomes the accent");
        check(
            ThemeAPI.class
                .getMethod("getThemeForScreen", String.class, String.class, String.class, String.class, String.class)
                .getReturnType() == ITheme.class,
            "the live theme hook targets an existing ModularUI method");
        String mixin = new String(
            Files.readAllBytes(SOURCES.resolve("mixins/late/modularUI2/MixinThemeAPI.java")),
            StandardCharsets.UTF_8),
            mixins = new String(Files.readAllBytes(SOURCES.resolve("mixins/Mixins.java")), StandardCharsets.UTF_8);
        check(
            mixin.contains("PaletteThemes.forScreen(cir.getReturnValue())") && mixin.contains(
                "getThemeForScreen(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/cleanroommc/modularui/api/ITheme;")
                && mixins.contains("\"modularUI2.MixinThemeAPI\""),
            "screens get live themes through a registered ModularUI hook");
        check(
            new String(
                Files.readAllBytes(SOURCES.resolve("client/celestweave/CelestweaveClientEvents.java")),
                StandardCharsets.UTF_8).contains("CelestweaveThemeAccent.register()")
                && new String(
                    Files.readAllBytes(SOURCES.resolve("common/gui/equipment/CelestweaveGui.java")),
                    StandardCharsets.UTF_8).contains("CelestweaveThemeAccent.editing(panel, color::getIntValue)"),
            "the armor registers its glow and the chestplate settings page shares the color being edited");
    }

    private static void themeChoice() {
        MainConfig.Client defaults = new MainConfig.Client();
        check(
            EquipmentTheme.ID.equals(defaults.equipmentTheme) && defaults.enableEquipmentThemeGlowColor,
            "the equipment theme and following the glow are on by default");
        String savedTheme = MainConfig.client.equipmentTheme;
        boolean savedFollow = MainConfig.client.enableEquipmentThemeGlowColor;
        int[] registrations = { 0 };
        PaletteThemes.onRegister(() -> registrations[0]++);
        PaletteThemes.register(TEST_THEME, PaletteThemes.palette(TEST_THEME));
        check(registrations[0] == 2, "registering a palette theme notifies its listeners");
        List<String> choices = Arrays.asList(EquipmentTheme.choices());
        check(
            choices.contains(EquipmentTheme.ID) && choices.contains(TEST_THEME),
            "every registered palette theme is offered in the config");
        MainConfig.client.equipmentTheme = "gtnl:missing";
        EquipmentTheme.LIVE.refresh();
        check(
            EquipmentTheme.selected()
                .equals(EquipmentTheme.ID)
                && EquipmentTheme.LIVE.themeId()
                    .equals(EquipmentTheme.ID),
            "an unknown theme id falls back to the equipment theme");
        MainConfig.client.equipmentTheme = TEST_THEME;
        EquipmentTheme.LIVE.refresh();
        ThemePalette chosen = PaletteThemes.palette(TEST_THEME);
        ITheme equipment = IThemeApi.get()
            .getTheme(EquipmentTheme.ID);
        check(
            EquipmentTheme.selected()
                .equals(TEST_THEME)
                && PaletteThemes.forScreen(
                    IThemeApi.get()
                        .getTheme(TEST_THEME))
                    == EquipmentTheme.LIVE.theme()
                && PaletteThemes.forScreen(equipment) == equipment,
            "a second registered theme can be chosen and takes over the equipment screens");
        check(
            EquipmentTheme.colors().accent == chosen.accent() && HudColor.selection() == chosen.accent(),
            "code colors come from the chosen theme");
        int[] glow = { 0xFF000000 | CYAN };
        EquipmentTheme.LIVE.accentFrom(() -> glow[0]);
        check(
            EquipmentTheme.LIVE.current()
                .equals(chosen.follow(CYAN)),
            "the chosen theme follows the glow");
        MainConfig.client.enableEquipmentThemeGlowColor = false;
        EquipmentTheme.LIVE.refresh();
        check(
            EquipmentTheme.LIVE.current()
                .equals(chosen) && EquipmentTheme.colors().accent == chosen.accent(),
            "with following off the chosen theme keeps its own colors");
        MainConfig.client.equipmentTheme = savedTheme;
        MainConfig.client.enableEquipmentThemeGlowColor = savedFollow;
        EquipmentTheme.LIVE.accentFrom(() -> LivePalette.NO_ACCENT);
    }

    private static double hueOffset(int color, int accent) {
        return (FxColor.hsl(color & 0xFFFFFF)[0] - FxColor.hsl(accent & 0xFFFFFF)[0] + 360) % 360;
    }

    private static double whiteMix(int color, int accent) {
        double lightness = FxColor.hsl(accent & 0xFFFFFF)[2];
        return (FxColor.hsl(color & 0xFFFFFF)[2] - lightness) / (1 - lightness);
    }

    private static double blackMix(int color, int accent) {
        return FxColor.hsl(color & 0xFFFFFF)[2] / FxColor.hsl(accent & 0xFFFFFF)[2];
    }

    private static boolean sameWidgetTheme(Object expected, Object actual) throws IllegalAccessException {
        if (expected == actual) return true;
        if (expected == null || actual == null || expected.getClass() != actual.getClass()) return false;
        if (expected instanceof IJsonSerializable serializable) {
            JsonObject first = new JsonObject(), second = new JsonObject();
            serializable.saveToJson(first);
            ((IJsonSerializable) actual).saveToJson(second);
            return first.equals(second);
        }
        for (Class<?> type = expected.getClass(); type != Object.class; type = type.getSuperclass())
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                Object first = field.get(expected), second = field.get(actual);
                if (field.getType()
                    .isPrimitive() ? !first.equals(second) : !sameWidgetTheme(first, second)) return false;
            }
        return true;
    }

    private static List<Integer> components(ThemePalette palette) {
        List<Integer> colors = new ArrayList<>();
        palette.map(color -> {
            colors.add(color);
            return color;
        });
        return colors;
    }

    private static void paletteControls(String label, ITheme theme, ThemePalette palette, ThemeStyle style)
        throws ReflectiveOperationException {
        WidgetTheme fallback = theme.getFallback()
            .getTheme();
        check(
            fallback.getTextColor() == palette.text() && fallback.getTextShadow() && fallback.getColor() == 0xFFFFFFFF,
            label + " text defaults to the palette text with a shadow");
        sameFrame(
            label + " panel",
            theme.getPanelTheme()
                .getTheme()
                .getBackground(),
            new int[] { palette.panelTop(), palette.panelBottom(), palette.accentDim(), palette.accent(),
                style.cornerLength(), style.cornerThickness() });
        WidgetThemeEntry<WidgetTheme> button = theme.getButtonTheme();
        sameRectangle(
            label + " button",
            button.getTheme()
                .getBackground(),
            palette.button());
        sameRectangle(
            label + " button hover",
            button.getHoverTheme()
                .getBackground(),
            palette.buttonHover());
        same(
            label + " button text",
            button.getTheme()
                .getTextColor(),
            palette.text());
        same(
            label + " button hover text",
            button.getHoverTheme()
                .getTextColor(),
            palette.selectedText());
        check(
            button.getTheme()
                .getTextShadow()
                && button.getHoverTheme()
                    .getTextShadow() == palette.selectedTextShadow(),
            label + " button text has a shadow, hovered button text follows its background");
        WidgetThemeEntry<WidgetTheme> close = theme.getWidgetTheme(IThemeApi.CLOSE_BUTTON);
        check(
            close.getTheme()
                .getBackground() == IDrawable.EMPTY,
            label + " close button has no background until hovered");
        sameRectangle(
            label + " close button hover",
            close.getHoverTheme()
                .getBackground(),
            palette.hover());
        same(
            label + " close button text",
            close.getTheme()
                .getTextColor(),
            palette.text());
        check(
            close.getTheme()
                .getTextShadow(),
            label + " close button text has a shadow");
        sameCard(
            label,
            theme.getWidgetTheme(CardDrawable.KEY)
                .getTheme()
                .getBackground(),
            new int[] { palette.card(), palette.cardEdge(), palette.accent(), style.cardAccentTop(),
                style.cardAccentWidth(), style.cardAccentHeight() });
        WidgetThemeEntry<SlideSwitch.Theme> slideEntry = theme.getWidgetTheme(SlideSwitch.Theme.KEY);
        SlideSwitch.Theme slide = slideEntry.getTheme();
        same(label + " switch track", slide.track, palette.switchOff());
        same(label + " switch track on", slide.trackOn, palette.accent());
        same(label + " switch track dim", slide.trackDim, palette.accentDim());
        same(label + " switch knob", slide.knob, palette.knob());
        same(label + " switch knob off", slide.knobOff, palette.knobOff());
        same(label + " switch text on", slide.textOn, palette.textOn());
        same(label + " switch text dim", slide.textDim, palette.textDim());
        same(label + " switch text off", slide.textOff, palette.textOff());
        check(slide.getBackground() == null, label + " switch rows have no background until hovered");
        sameRectangle(
            label + " switch hover",
            slideEntry.getHoverTheme()
                .getBackground(),
            palette.hover());
        FillBar.Theme bar = theme.getWidgetTheme(FillBar.Theme.KEY)
            .getTheme();
        same(label + " fill bar track", bar.track, palette.track());
        same(label + " fill bar fill", bar.fill, palette.accent());
        StepSlider.Theme slider = theme.getWidgetTheme(StepSlider.Theme.KEY)
            .getTheme();
        same(label + " slider track", slider.track, palette.track());
        same(label + " slider fill", slider.fill, palette.accent());
        same(label + " slider cursor", slider.cursor, palette.cursor());
        same(label + " slider cursor text", slider.cursorText, palette.cursorText());
        same(label + " slider tick", slider.tick, palette.tick());
        same(label + " slider milestone", slider.milestone, palette.milestone());
        SlotTheme slot = theme.getItemSlotTheme()
            .getTheme();
        sameFrame(
            label + " item slot",
            slot.getBackground(),
            new int[] { palette.well(), palette.well(), palette.wellEdge(), 0, 0, 0 });
        same(label + " item slot hover", slot.getSlotHoverColor(), palette.slotHover());
        for (WidgetThemeKey<SlotTheme> key : Arrays.asList(
            IThemeApi.ITEM_SLOT_PLAYER,
            IThemeApi.ITEM_SLOT_PLAYER_HOTBAR,
            IThemeApi.ITEM_SLOT_PLAYER_MAIN_INV,
            IThemeApi.ITEM_SLOT_PLAYER_ARMOR))
            check(
                theme.getWidgetTheme(key)
                    .getTheme()
                    .getBackground() == slot.getBackground(),
                label + " player inventory slot " + key.getFullName() + " uses the item slot");
        TextFieldTheme field = theme.getTextFieldTheme()
            .getTheme();
        sameFrame(
            label + " text field",
            field.getBackground(),
            new int[] { palette.well(), palette.well(), palette.accentDim(), 0, 0, 0 });
        same(label + " text field text", field.getTextColor(), palette.text());
        same(label + " text field selection", field.getMarkedColor(), palette.accentDim());
        same(label + " text field hint", field.getHintColor(), palette.hint());
        check(!field.getTextShadow(), label + " text field text has no shadow");
        sameRectangle(
            label + " scrollbar",
            theme.getScrollbarTheme()
                .getTheme()
                .getBackground(),
            palette.accentDim());
        WidgetThemeEntry<SelectableTheme> tabs = theme.getToggleButtonTheme();
        SelectableTheme tab = tabs.getTheme(), tabHover = tabs.getHoverTheme();
        sameRectangle(label + " tab", tab.getBackground(), palette.button());
        same(label + " tab text", tab.getTextColor(), palette.textOff());
        sameRectangle(
            label + " selected tab",
            tab.getSelected()
                .getBackground(),
            palette.buttonHover());
        same(
            label + " selected tab text",
            tab.getSelected()
                .getTextColor(),
            palette.selectedText());
        check(
            !tab.getTextShadow() && tab.getSelected()
                .getTextShadow() == palette.selectedTextShadow(),
            label + " unselected tab text has no shadow, selected tab text follows its background");
        sameRectangle(label + " tab hover", tabHover.getBackground(), palette.tabHover());
        sameRectangle(
            label + " selected tab hover",
            tabHover.getSelected()
                .getBackground(),
            palette.buttonHover());
        WidgetThemeEntry<Colors> colors = theme.getWidgetTheme(EquipmentTheme.COLORS);
        for (Colors read : new Colors[] { colors.getTheme(), colors.getHoverTheme() }) {
            same(label + " code accent", read.accent, palette.accent());
            same(label + " code accent dim", read.accentDim, palette.accentDim());
            same(label + " code hint", read.hint, palette.hint());
        }
        LayoutTheme layout = LayoutTheme.of(theme);
        check(
            layout.spacingSmall == style.spacingSmall() && layout.spacingMedium == style.spacingMedium()
                && layout.spacingLarge == style.spacingLarge()
                && layout.padding == style.padding()
                && layout.buttonHeight == style.buttonHeight()
                && layout.rowHeight == style.rowHeight()
                && layout.textScale == style.textScale(),
            label + " spacing, padding, button and row heights and text scale come from the theme style");
        check(
            theme.getWidgetTheme(ButtonPurpose.NORMAL.key) == button
                && theme.getWidgetTheme(ButtonPurpose.TAB.key) == tabs,
            label + " normal buttons and tabs use the plain button and toggle button themes");
        WidgetThemeEntry<?> primary = theme.getWidgetTheme(ButtonPurpose.PRIMARY.key),
            danger = theme.getWidgetTheme(ButtonPurpose.DANGER.key),
            icon = theme.getWidgetTheme(ButtonPurpose.ICON.key);
        sameRectangle(
            label + " primary button",
            primary.getTheme()
                .getBackground(),
            palette.buttonHover());
        sameRectangle(
            label + " primary button hover",
            primary.getHoverTheme()
                .getBackground(),
            palette.accent());
        same(
            label + " primary button text",
            primary.getTheme()
                .getTextColor(),
            palette.selectedText());
        same(
            label + " primary button hover text",
            primary.getHoverTheme()
                .getTextColor(),
            palette.selectedText());
        sameRectangle(
            label + " danger button",
            danger.getTheme()
                .getBackground(),
            palette.button());
        same(
            label + " danger button text",
            danger.getTheme()
                .getTextColor(),
            palette.danger());
        sameRectangle(
            label + " danger button hover",
            danger.getHoverTheme()
                .getBackground(),
            palette.danger());
        same(
            label + " danger button hover text",
            danger.getHoverTheme()
                .getTextColor(),
            palette.text());
        check(
            icon.getTheme()
                .getBackground() == IDrawable.EMPTY,
            label + " icon button has no background until hovered");
        sameRectangle(
            label + " icon button hover",
            icon.getHoverTheme()
                .getBackground(),
            palette.hover());
        same(
            label + " icon button text",
            icon.getTheme()
                .getTextColor(),
            palette.text());
    }

    private static boolean pink(int color) {
        return (color & 0xFFFFFF) == 0xFF3D9A;
    }

    private static void same(String what, int actual, int expected) {
        check(
            actual == expected,
            what + " color: expected " + Integer.toHexString(expected) + ", got " + Integer.toHexString(actual));
    }

    private static void sameRectangle(String what, IDrawable drawable, int color) throws ReflectiveOperationException {
        check(drawable instanceof Rectangle, what + " is a plain rectangle");
        if (drawable instanceof Rectangle)
            for (String corner : new String[] { "colorTL", "colorTR", "colorBL", "colorBR" })
                same(what + " " + corner, (int) field(drawable, corner), color);
    }

    private static void sameFrame(String what, IDrawable drawable, int[] values) throws ReflectiveOperationException {
        check(drawable instanceof FrameDrawable, what + " is a frame");
        if (!(drawable instanceof FrameDrawable)) return;
        String[] names = { "top", "bottom", "border", "corner", "cornerLength", "cornerThickness" };
        for (int i = 0; i < names.length; i++) same(what + " " + names[i], (int) field(drawable, names[i]), values[i]);
    }

    private static void sameCard(String label, IDrawable drawable, int[] values) throws ReflectiveOperationException {
        check(drawable instanceof CardDrawable, label + " card is a card");
        if (!(drawable instanceof CardDrawable)) return;
        String[] names = { "fill", "edge", "accent", "accentTop", "accentWidth", "accentHeight" };
        for (int i = 0; i < names.length; i++)
            same(label + " card " + names[i], (int) field(drawable, names[i]), values[i]);
    }

    private static void reloadThemes(JsonBuilder... overrides) throws ReflectiveOperationException {
        new PaletteThemes.Reloader().onThemeReload(new ReloadThemeEvent.Pre());
        for (JsonBuilder override : overrides) IThemeApi.get()
            .registerTheme(EquipmentTheme.ID, override);
        call(ThemeAPI.class, "onReload", ThemeAPI.INSTANCE, new Class<?>[0]);
        ThemeManager.loadThemes(new HashMap<>());
        new PaletteThemes.Reloader().onThemesLoaded(new ReloadThemeEvent.Post());
    }

    private static void lang() throws Exception {
        Set<String> expected = new TreeSet<>();
        for (CelestweaveModule module : CelestweaveModule.values()) {
            expected.add("item.gtnl.celestweave_module." + module.id + ".name");
            expected.add("item.gtnl.celestweave_module." + module.id + ".tooltip");
            expected.add("item.gtnl.celestweave_module.slots." + module.pieceMask);
            expected.add("item.gtnl.celestweave.short." + module.id);
            if (module.holdsItem) expected.add("item.gtnl.celestweave.gui.held." + module.id);
        }
        for (CelestweaveModuleSetting setting : CelestweaveModuleSetting.values()) expected.add(setting.lang());
        Field pages = CelestweaveGui.class.getDeclaredField("PAGES");
        pages.setAccessible(true);
        for (String page : (String[]) pages.get(null)) expected.add("item.gtnl.celestweave.gui.page." + page);
        for (CelestweaveCosmeticOption option : CelestweaveCosmeticOption.values()) {
            String base = "item.gtnl.celestweave.gui." + option.lang;
            if (option.kind != CelestweaveCosmeticOption.Kind.FLAGS) expected.add(base);
            if (option.minLabel != null) expected.add("item.gtnl.celestweave.gui." + option.minLabel);
            for (String name : option.names()) {
                expected.add(base + "." + name);
                if (option.kind == CelestweaveCosmeticOption.Kind.FLAGS) expected.add(base + "." + name + ".tooltip");
            }
        }
        for (int piece = 0; piece < 4; piece++) expected.add("item.gtnl.celestweave.wheel.piece." + piece);
        for (CelestweaveHudElement element : CelestweaveHudElement.values()) expected.add(element.hud.lang());
        expected.add(RailgunHudRenderer.CHARGE.lang());
        for (String key : new String[] { "open", "help", "reset", "done", "hidden", "show_all", "in_use_only", "undo",
            "redo", "reset_element", "seconds", "empty" }) expected.add("gtnl.hud.editor." + key);
        for (HudElement.Setting setting : HudElement.Setting.values()) expected.add(setting.lang());
        for (HudTiming timing : HudTiming.values()) expected.add(timing.lang());
        for (HudColor color : HudColor.values()) expected.add(color.lang());
        expected.add(HudElement.STANDARD_STYLE.lang());
        for (Field field : MainConfig.Client.class.getFields()) {
            Config.LangKey key = field.getAnnotation(Config.LangKey.class);
            String name = field.getName()
                .toLowerCase();
            if (key == null || !name.contains("celestweave") && !name.contains("hud") && !name.contains("equipment"))
                continue;
            expected.add(key.value());
            expected.add(key.value() + ".tooltip");
        }
        for (Field field : MainConfig.Item.Railgun.class.getFields()) {
            String key = "gtnl.gui.config.general.item.railgun." + field.getName()
                .toLowerCase();
            expected.add(key);
            expected.add(key + ".tooltip");
        }
        for (Class<?> section : new Class<?>[] { MainConfig.Item.Railgun.class, MainConfig.Item.Celestweave.class })
            for (Field field : section.getFields()) if (field.getType() == boolean.class) check(
                field.getName()
                    .startsWith("enable"),
                "config switch starts with enable " + field.getName());
        Pattern literal = Pattern.compile(
            "\"((?:item\\.gtnl\\.celestweave|key\\.gtnl\\.celestweave|gtnl\\.gui\\.config\\.general\\.client\\.celestweave|death\\.attack\\.gtnl\\.celestweave)[A-Za-z0-9_.]*)\"");
        try (Stream<Path> files = Files.walk(SOURCES)) {
            for (Path path : (Iterable<Path>) files.filter(
                file -> file.toString()
                    .endsWith(".java")
                    && (file.toString()
                        .contains("celestweave")
                        || file.getFileName()
                            .toString()
                            .startsWith("Celestweave")))::iterator) {
                Matcher matcher = literal.matcher(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
                while (matcher.find()) if (!matcher.group(1)
                    .endsWith(".")) expected.add(matcher.group(1));
            }
        }
        for (String name : new String[] { "celestweave_oculus", "celestweave_core", "celestweave_conduit",
            "celestweave_stride" }) expected.add("item.gtnl." + name + ".name");
        expected.add("death.attack.gtnl.celestweave.lightning");
        for (EquipmentSound sound : EquipmentSound.values()) expected.add(sound.lang());
        for (EquipmentSound.Group group : EquipmentSound.Group.values()) expected.add(group.lang());
        for (String key : new String[] { "", ".all", ".enabled", ".enabled.tooltip", ".volume", ".volume.tooltip",
            ".tooltip" }) expected.add("gtnl.gui.config.sound" + key);
        expected.add("item.gtnl.equipment.gui.sound");
        expected.add("item.gtnl.equipment.gui.sound.hint");
        Set<String> english = langKeys("en_US.lang"), chinese = langKeys("zh_CN.lang");
        for (String key : expected) {
            check(english.contains(key), "en_US missing " + key);
            check(chinese.contains(key), "zh_CN missing " + key);
        }
        for (String key : english)
            if (equipmentKey(key)) check(chinese.contains(key), "zh_CN missing equipment key " + key);
        for (String key : chinese)
            if (equipmentKey(key)) check(english.contains(key), "en_US missing equipment key " + key);
        check(new File("src/main/resources").isDirectory(), "run from the project root");
    }

    private static boolean equipmentKey(String key) {
        return key.contains("celestweave") || key.contains("railgun") || key.startsWith("gtnl.hud.");
    }

    private static Set<String> langKeys(String file) throws IOException {
        Set<String> keys = new HashSet<>();
        for (String line : Files.readAllLines(RESOURCES.resolve("lang/" + file), StandardCharsets.UTF_8)) {
            int split = line.indexOf('=');
            if (split > 0 && !line.startsWith("#")) keys.add(
                line.substring(0, split)
                    .trim());
        }
        return keys;
    }
}
