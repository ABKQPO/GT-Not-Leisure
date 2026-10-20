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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.DoubleConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;

import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.IntValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.science.gtnl.client.railgun.RailgunHudRenderer;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveModuleSetting;
import com.science.gtnl.common.equipment.EquipmentSound;
import com.science.gtnl.common.gui.equipment.CelestweaveGui;
import com.science.gtnl.common.gui.equipment.EquipmentSoundScreen;
import com.science.gtnl.common.gui.modularui.widget.StepSlider;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.config.EquipmentSoundConfig;
import com.science.gtnl.config.EquipmentSoundConfig.Channel;
import com.science.gtnl.config.MainConfig;

public class EquipmentResourceContracts {

    private EquipmentResourceContracts() {}

    static void run() throws Exception {
        hexTexture();
        model();
        raisedGlow();
        firstPersonArm();
        sounds();
        soundChannels();
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
        for (String key : new String[] { "open", "help", "reset", "done", "hidden", "show_all", "in_use_only",
            "empty" }) expected.add("gtnl.hud.editor." + key);
        for (Field field : MainConfig.Client.class.getFields()) {
            Config.LangKey key = field.getAnnotation(Config.LangKey.class);
            String name = field.getName()
                .toLowerCase();
            if (key == null || !name.contains("celestweave") && !name.contains("hud")) continue;
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
