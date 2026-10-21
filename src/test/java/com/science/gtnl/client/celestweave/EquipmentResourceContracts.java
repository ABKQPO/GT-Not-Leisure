package com.science.gtnl.client.celestweave;

import static com.gtnllib.GTNLLibContractTest.call;
import static com.gtnllib.GTNLLibContractTest.check;
import static com.gtnllib.GTNLLibContractTest.compact;
import static com.gtnllib.GTNLLibContractTest.field;
import static com.gtnllib.GTNLLibContractTest.near;
import static com.gtnllib.GTNLLibContractTest.read;
import static com.gtnllib.ThemeContracts.paletteControls;
import static com.gtnllib.ThemeContracts.reloadThemes;
import static com.gtnllib.ThemeContracts.same;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.RESOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;

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

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.theme.ThemeAPI;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.JsonBuilder;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.IntValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnllib.client.hud.HudColor;
import com.gtnllib.config.GTNLLibConfig;
import com.gtnllib.gui.layout.LayoutMath;
import com.gtnllib.gui.layout.Size;
import com.gtnllib.gui.theme.LivePalette;
import com.gtnllib.gui.theme.MetalSkin;
import com.gtnllib.gui.theme.PaletteThemes;
import com.gtnllib.gui.theme.ThemePalette;
import com.gtnllib.gui.theme.ThemeStyle;
import com.gtnllib.gui.widget.LayoutTheme;
import com.gtnllib.gui.widget.StepSlider;
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
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.config.EquipmentSoundConfig;
import com.science.gtnl.config.EquipmentSoundConfig.Channel;
import com.science.gtnl.config.MainConfig;

import gregtech.api.modularui2.GTGuiThemes;

public class EquipmentResourceContracts {

    private static final String TEST_THEME = "gtnl:palette_test";
    private static final int CYAN = 0x3DE8FF;

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
        Field chars = StepSlider.class.getDeclaredField("COMPACT_CHARS"),
            glyphs = StepSlider.class.getDeclaredField("COMPACT_GLYPHS");
        chars.setAccessible(true);
        glyphs.setAccessible(true);
        String charset = (String) chars.get(null);
        check(
            "-12.5%+".chars()
                .allMatch(c -> charset.indexOf(c) >= 0) && charset.indexOf('e') < 0,
            "compact font charset");
        check(
            "×1.5 30s".replace(" ", "")
                .chars()
                .allMatch(c -> charset.indexOf(c) >= 0) && StepSlider.compactWidth("×2") == 7,
            "the compact font also draws multipliers and seconds");
        String sliderSource = read(Paths.get("src/main/java/com/gtnllib/gui/widget/StepSlider.java"));
        check(
            !sliderSource.contains("fontRenderer") && !sliderSource.contains("compactDigits")
                && !sliderSource.contains("compactFits"),
            "every slider draws its value tag in the compact font");
        check(charset.length() == ((int[]) glyphs.get(null)).length, "every compact char has a glyph");
        Field tag = EquipmentSoundScreen.class.getDeclaredField("TAG_WIDTH");
        tag.setAccessible(true);
        check(StepSlider.compactWidth("100%") + 4 <= tag.getInt(null), "sound slider tag fits 100%");
    }

    private static void equipmentTheme() throws Exception {
        EquipmentTheme.register();
        String standard = GTGuiThemes.STANDARD.getId();
        IThemeApi.get()
            .registerTheme(standard, new JsonBuilder());
        reloadThemes(EquipmentTheme.ID);
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
        expected.put("accent", 0xFFDF65A0);
        expected.put("accentDim", 0x80DF65A0);
        expected.put("hint", 0xFF8A8F96);
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
                    "equipment color " + field.getName() + " is the module palette value");
            }
            check(fields == expected.size(), "equipment palette keeps only the colors code reads");
        }
        StepSlider.Theme slider = theme.getWidgetTheme(StepSlider.Theme.KEY)
            .getTheme();
        check(
            slider.track == 0xFF0B0C0E && slider.fill == 0xFFDF65A0
                && slider.cursor == 0xFFFFC7E6
                && slider.cursorText == 0xFF4A1030
                && slider.tick == 0x40FFFFFF
                && slider.milestone == 0x99FFFFFF,
            "equipment sliders use the module palette colors");
        check(
            HudColor.guide() == 0x80DF65A0 && HudColor.selection() == 0xFFDF65A0,
            "equipment theme hands its accent to the hud editor");
        equipmentControls();
        liveThemes(standard);
        themeChoice();
        configScreen();
        Pattern hex = Pattern.compile("0x[0-9A-Fa-f]{6,8}");
        for (String file : new String[] { "common/gui/equipment/EquipmentSoundScreen.java",
            "client/equipment/EquipmentWheelScreen.java" }) {
            String source = read(SOURCES.resolve(file));
            check(
                !source.toUpperCase(Locale.ROOT)
                    .contains("3D9A")
                    && !source.toUpperCase(Locale.ROOT)
                        .contains("DF65A0"),
                file + " has no hard-coded equipment pink");
            check(
                !hex.matcher(source)
                    .find(),
                file + " takes every color from the equipment theme");
        }
        reloadThemes(
            EquipmentTheme.ID,
            new JsonBuilder()
                .add(EquipmentTheme.COLORS.getFullName(), new JsonBuilder().add("accentColor", 0xFF00FF00)));
        Colors overridden = EquipmentTheme.colors();
        check(
            overridden.accent == 0xFF00FF00 && overridden.hint == expected.get("hint")
                && HudColor.selection() == 0xFF00FF00,
            "an override of one equipment color keeps the others");
        reloadThemes(EquipmentTheme.ID);
        check(EquipmentTheme.colors().accent == expected.get("accent"), "theme reload restores the accent");
    }

    private static void equipmentScreens() throws IOException {
        for (String file : new String[] { "common/item/items/armor/CelestweaveArmor.java",
            "common/item/items/equipment/ElectromagneticRailgun.java", "common/gui/equipment/EquipmentSoundScreen.java",
            "client/equipment/EquipmentWheelScreen.java" }) {
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
            base.contains("new ModularPanel(name)") && base.contains("new CloseButton().corner(style.padding, style)"),
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
        int available = 160, gap = 2;
        check(
            Arrays.equals(LayoutMath.split(available, gap, 2), new int[] { 79, 79 }),
            "two columns keep the old 79 pixel cells");
        check(
            Arrays.equals(LayoutMath.split(available, gap, 3), new int[] { 52, 52, 52 }),
            "three tabs keep the old 52 pixel tabs");
        check(
            Arrays.equals(LayoutMath.split(available, 1, 7), new int[] { 22, 22, 22, 22, 22, 22, 22 }),
            "seven arc flags keep the old 22 pixel buttons");
        Size sliderSize = Size.percent(0.5F)
            .atLeast(80);
        for (int width : new int[] { available, 200, 240 }) {
            int[] plain = LayoutMath.line(Arrays.asList(Size.fill(), sliderSize), width, 4);
            int[] shared = LayoutMath.shrink(
                LayoutMath.line(Arrays.asList(Size.fill(), Size.content(47), sliderSize), width, 4),
                new int[] { 0, 0, 80 },
                width,
                4);
            check(
                plain[1] == Math.max(80, width / 2) && shared[2] == plain[1]
                    && shared[0] + 4 + shared[1] + 4 == plain[0] + 4,
                "at " + width
                    + " pixels every slider has the same width and starts at the same x, also next to the by-tier switch");
        }
        check(
            LayoutMath.fit(320, 4, 176, 256) == 256 && LayoutMath.fit(176, 4, 176, 256) == 176,
            "settings panels grow from 176 to 256 pixels with the screen");
        check(
            LayoutMath.columns(160, gap, 76, 3) == 2 && LayoutMath.columns(240, gap, 76, 3) == 3,
            "short switch labels keep two columns on a narrow panel and take three on the widest one");
        int narrowTop = 97, wideTop = 72, footer = 93;
        check(
            narrowTop + LayoutMath.fitRows(240 - 8 - narrowTop - footer, 16, 3, 11) + footer <= 240
                && LayoutMath.fitRows(270 - 8 - wideTop - footer, 16, 3, 11) == 96
                && LayoutMath.fitRows(360 - 8 - wideTop - footer, 16, 3, 11) == 176,
            "the armor pages fit the smallest screen and show six rows at GUI scale 4 on a 1080p screen");
        List<Size> colorRow = new ArrayList<>();
        colorRow.add(Size.fill());
        for (int i = 0; i < 7; i++) colorRow.add(Size.fixed(12));
        colorRow.add(Size.fixed(12));
        int[] colors = LayoutMath.line(colorRow, available, gap);
        check(
            colors[0] == 48 && colors[8] == colors[1] && LayoutMath.total(colors, gap) == available,
            "the custom color button is as large as the swatches and the row still fills the panel");
        int[] cards = { 200, 200 }, heights = { 150, 118 };
        check(!LayoutMath.stacks(cards, heights, 10, 414, 200), "a wide screen keeps the sound cards side by side");
        check(LayoutMath.stacks(cards, heights, 10, 380, 300), "a narrow screen stacks the sound cards");
        check(
            !LayoutMath.stacks(cards, heights, 10, 380, 250),
            "a narrow and short screen keeps the cards side by side instead of cutting them off");
        Map<String, String> sources = new HashMap<>();
        for (String file : new String[] { "common/gui/equipment/EquipmentGui.java",
            "common/gui/equipment/CelestweaveGui.java", "common/gui/equipment/RailgunGui.java",
            "common/gui/equipment/EquipmentSoundScreen.java", "client/equipment/EquipmentWheelScreen.java" })
            sources.put(file, read(SOURCES.resolve(file)));
        String base = compact(sources.get("common/gui/equipment/EquipmentGui.java")),
            armor = compact(sources.get("common/gui/equipment/CelestweaveGui.java")),
            railgun = compact(sources.get("common/gui/equipment/RailgunGui.java")),
            sound = compact(sources.get("common/gui/equipment/EquipmentSoundScreen.java")),
            wheel = compact(sources.get("client/equipment/EquipmentWheelScreen.java"));
        String columns = "adaptiveGrid(style.spacingSmall,widestSwitch(labels),MAX_COLUMNS,";
        check(
            armor.contains(columns + "switches)") && armor.contains(columns + "toggles)")
                && railgun.contains(columns + "switches)"),
            "module and setting switches pick their column count from the widest label and the current width");
        check(
            base.contains("LayoutMath.fit(Layout.screenWidth(panel),SCREEN_MARGIN,WIDTH,MAX_WIDTH)")
                && base.contains("panel.width(this::panelWidth,Unit.Measure.PIXEL)")
                && base.contains("returnLayout.rows(style,this::panelWidth);"),
            "settings panels and their rows follow the screen width, also after a resize");
        check(
            base.contains("LayoutMath.fitRows(") && armor.contains(".height(this::pageHeight,Unit.Measure.PIXEL)")
                && railgun.contains("()->headerBottom()+grid.contentHeight()"),
            "armor pages fit the screen height and the railgun panel grows with its switch rows");
        check(
            base.contains("slotRows(moduleSlots,slotRoom())") && base.contains("panel.scheduleResize();")
                && armor.contains("reserveHeld(holders.size());")
                && armor.contains(".top(slotsTop())"),
            "module slots fill the row the panel width leaves, held item slots share it, and the header lays out again when the energy text changes width");
        check(
            armor.contains("scrollDirection(ScrollData.of(GuiAxis.Y,false,style.spacingSmall))")
                && armor.contains(".width(this::listWidth,Unit.Measure.PIXEL)")
                && base.contains("returnpanelWidth()-style.padding+style.spacingSmall;"),
            "the page scrollbar is a thin bar right of the content instead of lying on the panel frame");
        check(
            base.contains("newDivider().color(client?dividerColor():0)")
                && base.contains("returnINVENTORY_BOTTOM+INVENTORY_HEIGHT+style.spacingLarge;"),
            "a divider in a wider gap separates the settings from the player inventory");
        check(
            armor.contains("rows.section(") && armor.contains("newStepSlider()")
                && armor.contains("()->value.getIntValue()==index")
                && !armor.contains("stepButton"),
            "armor settings are grouped by module, numbers use sliders and choices use pick buttons");
        check(
            armor.contains("toggle,sliderControl(") && armor.contains(
                "Arrays.asList(Size.fill(),Size.content(SlideSwitch.widthFor(textWidth(tr(GUI_LANG+option.minLabel)))),sliderSize())")
                && armor.split("sliderSize\\(\\)", -1).length - 1 == 3,
            "the idle arc count and its by-tier switch share one row");
        check(
            armor.contains("client&&max>min?stepSlider(")
                && armor.contains("@SideOnly(Side.CLIENT)privatestaticStepSliderstepSlider("),
            "sliders are only built on the client, the server panel never loads them");
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
        String input = compact(read(SOURCES.resolve("client/equipment/EquipmentClientInput.java")));
        check(
            input.contains(
                "int[]slots=EquipmentWheelScreen.equipmentSlots(player);if(slots.length>0)newEquipmentWheelScreen(EQUIPMENT_SETTINGS,slots).open();")
                && !input.contains("OpenEquipmentGuiPacket"),
            "the settings key scans the inventory once and opens the wheel when any equipment is carried");
        check(
            wheel.contains("stackAt(slot).getDisplayName()") && wheel.contains("newOpenEquipmentGuiPacket(slot)")
                && wheel.contains("for(intslot:slots)if(present(slot))return;close();")
                && !wheel.contains("LANG+\"piece."),
            "every carried equipment gets a wheel slot with its own name, and an open wheel only rechecks its own slots");
        Pattern oldPixels = Pattern.compile("\\b(79|81|54|98|108|122|136|148|152|160|161)\\b");
        for (Map.Entry<String, String> entry : sources.entrySet()) check(
            !oldPixels.matcher(entry.getValue())
                .find(),
            entry.getKey() + " has no hard-coded column, tab or row positions left");
    }

    private static void equipmentControls() throws Exception {
        String proxy = read(SOURCES.resolve("ClientProxy.java"));
        int library = proxy.indexOf("GTNLLibClient.preInit()"), equipment = proxy.indexOf("EquipmentTheme.register()");
        check(
            library >= 0 && equipment > library,
            "GTNLLib starts before the equipment theme registers, so palette themes are rebuilt before every theme reload");
        String soundSource = read(SOURCES.resolve("common/gui/equipment/EquipmentSoundScreen.java"));
        check(
            !soundSource.contains("GuiDraw") && !soundSource.contains("disableThemeBackground")
                && soundSource.contains("new SlideSwitch()")
                && soundSource.contains("Layout.card(")
                && soundSource.contains("new CloseButton().corner(pad, style)")
                && soundSource.contains("new Divider().color(colors.accent)")
                && !soundSource.contains("Rectangle"),
            "sound screen draws its panel, cards, switches, title divider and close button through the theme");
        for (String file : new String[] { "common/packet/OpenEquipmentGuiPacket.java",
            "common/equipment/EquipmentNet.java" })
            check(
                !read(SOURCES.resolve(file)).contains("celestweave"),
                file + " is shared by both pieces of equipment and does not depend on the armor package");
        check(
            EquipmentTheme.PALETTE.equals(
                new ThemePalette(
                    0xFFDF65A0,
                    0x80DF65A0,
                    0x26DF65A0,
                    0xFFFFFFFF,
                    0xFF8A8F96,
                    0xFF1C1F24,
                    0xFF17191D,
                    0x40000000,
                    0x1FFFFFFF,
                    0xFF353940,
                    0xA0DF65A0,
                    0x50DF65A0,
                    0xFF14161A,
                    0xFF353940,
                    0x50DF65A0,
                    0xFF0B0C0E,
                    0xFF353940,
                    0xFFDEDFE2,
                    0xFF797E84,
                    0xFFF0F1F3,
                    0xFF61666D,
                    0xFFA5A7AC,
                    0xFFFFC7E6,
                    0xFF4A1030,
                    0x40FFFFFF,
                    0x99FFFFFF,
                    0xFFE5484D)),
            "equipment palette takes the gunmetal, silver and magenta of the module textures: "
                + EquipmentTheme.PALETTE);
        equipmentPaletteControls(
            "equipment",
            IThemeApi.get()
                .getTheme(EquipmentTheme.ID),
            EquipmentTheme.PALETTE,
            ThemeStyle.DEFAULT);
        LayoutTheme layout = EquipmentTheme.layout();
        check(
            layout.padding == ThemeStyle.DEFAULT.padding() && layout.buttonHeight == ThemeStyle.DEFAULT.buttonHeight(),
            "equipment screens read their spacing from the selected theme");
    }

    private static void equipmentPaletteControls(String label, ITheme theme, ThemePalette palette, ThemeStyle style)
        throws ReflectiveOperationException {
        paletteControls(label, theme, palette, style, MetalSkin.SKIN);
        codeColors(label, theme, palette);
    }

    private static void codeColors(String label, ITheme theme, ThemePalette palette) {
        WidgetThemeEntry<Colors> colors = theme.getWidgetTheme(EquipmentTheme.COLORS);
        for (Colors read : new Colors[] { colors.getTheme(), colors.getHoverTheme() }) {
            same(label + " code accent", read.accent, palette.accent());
            same(label + " code accent dim", read.accentDim, palette.accentDim());
            same(label + " code hint", read.hint, palette.hint());
        }
    }

    private static void liveThemes(String standard) throws Exception {
        ThemePalette palette = EquipmentTheme.PALETTE, cyan = palette.follow(CYAN);
        reloadThemes(EquipmentTheme.ID);
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
        equipmentPaletteControls("live cyan", view, cyan, ThemeStyle.DEFAULT);
        check(
            HudColor.selection() == cyan.accent() && HudColor.guide() == cyan.accentDim()
                && EquipmentTheme.colors().accent == cyan.accent(),
            "hud editor guides and code colors follow the glow");
        WidgetThemeEntry<WidgetTheme> tintedPanel = view.getPanelTheme();
        check(tintedPanel == view.getPanelTheme(), "a tinted theme is built once per glow");
        reloadThemes(EquipmentTheme.ID);
        check(view.getPanelTheme() != tintedPanel, "a theme reload rebuilds the tinted theme on the new parent");
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
        String mixin = read(SOURCES.resolve("mixins/late/modularUI2/MixinThemeAPI.java")),
            mixins = read(SOURCES.resolve("mixins/Mixins.java"));
        check(
            mixin.contains("PaletteThemes.forScreen(cir.getReturnValue())") && mixin.contains(
                "getThemeForScreen(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/cleanroommc/modularui/api/ITheme;")
                && mixins.contains("\"modularUI2.MixinThemeAPI\""),
            "screens get live themes through a registered ModularUI hook");
        check(
            read(SOURCES.resolve("client/celestweave/CelestweaveClientEvents.java"))
                .contains("CelestweaveThemeAccent.register()")
                && read(SOURCES.resolve("common/gui/equipment/CelestweaveGui.java"))
                    .contains("CelestweaveThemeAccent.editing(panel, color::getIntValue)"),
            "the armor registers its glow and the chestplate settings page shares the color being edited");
    }

    private static void themeChoice() throws ReflectiveOperationException {
        MainConfig.Client defaults = new MainConfig.Client();
        check(
            GTNLLibConfig.theme.isEmpty() && EquipmentTheme.selected()
                .equals(EquipmentTheme.ID) && defaults.enableEquipmentThemeGlowColor,
            "the equipment theme and following the glow are on by default");
        String savedTheme = GTNLLibConfig.theme;
        boolean savedFollow = MainConfig.client.enableEquipmentThemeGlowColor;
        PaletteThemes.register(
            TEST_THEME,
            GTGuiThemes.STANDARD.getId(),
            EquipmentTheme.PALETTE.map(color -> color ^ 0x00FFFFFF),
            ThemeStyle.DEFAULT);
        reloadThemes(EquipmentTheme.ID);
        codeColors(
            "test palette",
            IThemeApi.get()
                .getTheme(TEST_THEME),
            PaletteThemes.palette(TEST_THEME));
        List<String> choices = Arrays.asList(GTNLLibConfig.themes());
        check(
            choices.contains(EquipmentTheme.ID) && choices.contains(TEST_THEME),
            "the equipment theme and a second registered theme are offered in the config");
        GTNLLibConfig.theme = "gtnl:missing";
        EquipmentTheme.LIVE.refresh();
        check(
            EquipmentTheme.selected()
                .equals(EquipmentTheme.ID)
                && EquipmentTheme.LIVE.themeId()
                    .equals(EquipmentTheme.ID),
            "an unknown theme id falls back to the equipment theme");
        GTNLLibConfig.theme = TEST_THEME;
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
        GTNLLibConfig.theme = savedTheme;
        MainConfig.client.enableEquipmentThemeGlowColor = savedFollow;
        EquipmentTheme.LIVE.accentFrom(() -> LivePalette.NO_ACCENT);
    }

    private static void configScreen() throws IOException {
        String factory = read(SOURCES.resolve("config/ConfigGuiFactory.java"));
        check(
            compact(read(SOURCES.resolve("ScienceNotLeisure.java")))
                .contains("guiFactory=\"com.science.gtnl.config.ConfigGuiFactory\""),
            "GTNL opens its own config screen");
        check(factory.contains("GTNLLibConfig.class"), "the GTNLLib client settings show in the GTNL config screen");
        Pattern config = Pattern.compile("@Config\\(\\s*modid\\s*=\\s*ScienceNotLeisure\\.MODID");
        try (Stream<Path> files = Files.walk(SOURCES)) {
            for (Path file : (Iterable<Path>) files.filter(
                path -> path.toString()
                    .endsWith(".java"))::iterator) {
                String source = read(file);
                if (!config.matcher(source)
                    .find() || source.contains("@Config.ExcludeFromAutoGui")) continue;
                String name = file.getFileName()
                    .toString()
                    .replace(".java", "");
                check(factory.contains(name + ".class"), name + " shows in the GTNL config screen");
            }
        }
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
        expected.add("item.gtnl.equipment.wheel.title");
        expected.add("item.gtnl.equipment.wheel.sound");
        for (CelestweaveHudElement element : CelestweaveHudElement.values()) expected.add(element.hud.lang());
        expected.add(RailgunHudRenderer.CHARGE.lang());
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
        for (Set<String> keys : Arrays.asList(english, chinese)) check(
            keys.stream()
                .noneMatch(key -> key.startsWith("gtnllib.") || key.startsWith("gtnl.hud.")),
            "GTNL lang files leave the GTNLLib keys to the GTNLLib lang files");
        check(new File("src/main/resources").isDirectory(), "run from the project root");
    }

    private static boolean equipmentKey(String key) {
        return key.contains("celestweave") || key.contains("railgun");
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
