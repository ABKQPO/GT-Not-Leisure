package com.science.gtnl.client.celestweave;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveSettings;
import com.science.gtnl.common.gui.CelestweaveGui;
import com.science.gtnl.common.item.items.CelestweaveModuleItem;
import com.science.gtnl.common.packet.CelestweaveFlightPacket;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnl.config.MainConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class CelestweaveContractTest {

    private static final Path RESOURCES = Paths.get("src/main/resources/assets/sciencenotleisure");
    private static final Path SOURCES = Paths.get("src/main/java/com/science/gtnl");
    private static final List<String> FAILURES = new ArrayList<>();
    private static int assertions;

    private CelestweaveContractTest() {}

    public static void main(String[] args) throws Exception {
        modules();
        cosmetic();
        settings();
        energy();
        backpack();
        packets();
        envelopes();
        hud();
        hexTexture();
        model();
        sounds();
        lang();
        if (!FAILURES.isEmpty()) {
            FAILURES.forEach(failure -> System.out.println("FAIL " + failure));
            throw new AssertionError(FAILURES.size() + " Celestweave contracts failed");
        }
        System.out.println("Celestweave contracts passed: " + assertions + " assertions");
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) FAILURES.add(message);
    }

    private static void near(double expected, double actual, String message) {
        check(Math.abs(expected - actual) < 1.0E-6, message + ": expected " + expected + ", got " + actual);
    }

    private static Object call(Class<?> owner, String name, Object target, Class<?>[] types, Object... args)
        throws ReflectiveOperationException {
        Method method = owner.getDeclaredMethod(name, types);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private static Object field(Object target, String name) throws ReflectiveOperationException {
        Field field = target.getClass()
            .getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static ItemStack plainStack() {
        return new ItemStack(new Item());
    }

    private static void modules() {
        Set<String> ids = new HashSet<>();
        for (CelestweaveModule module : CelestweaveModule.values()) {
            check(ids.add(module.id), "duplicate module id " + module.id);
            check(CelestweaveModule.byId(module.id) == module, "byId round trip " + module.id);
            check(module.slots > 0 && module.slots <= CelestweaveModule.Mask.ANY, "slot mask " + module.id);
            boolean fitsAny = false;
            for (int type = 0; type < 4; type++) fitsAny |= module.fits(type);
            check(fitsAny, "module fits no piece " + module.id);
            check(!module.fits(-1) && !module.fits(4), "fits out of range " + module.id);
            check(module.toggleable() == (module.group != CelestweaveModule.Group.ENERGY), "toggleable " + module.id);
            if (module.hideable()) check(module.fits(0) || module.fits(1) || module.fits(2), "hideable " + module.id);
        }
        check(CelestweaveModule.ENERGY_T3.energyTier() == 3 && CelestweaveModule.SHIELD.energyTier() == 0, "tiers");
        check(CelestweaveModule.of(plainStack()) == null, "plain item is not a module");
    }

    private static void cosmetic() {
        int maxArcs = MainConfig.item.celestweave.maxIdleArcs;
        check(CelestweaveCosmetic.read(new NBTTagCompound()) == CelestweaveCosmetic.DEFAULT, "empty cosmetic");
        CelestweaveCosmetic sample = new CelestweaveCosmetic(0x123456, 3, 1.5F, 2F, 0.5F, 5);
        check(
            CelestweaveCosmetic.read(sample.write())
                .equals(sample),
            "cosmetic round trip");
        NBTTagCompound wild = new NBTTagCompound();
        wild.setInteger("GlowColor", 0xFF123456);
        wild.setInteger("IdleArcs", 99);
        wild.setFloat("ArcFreq", Float.NaN);
        wild.setFloat("ArcSpread", 100F);
        wild.setFloat("ArcBright", -5F);
        wild.setInteger("ArcEvents", -1);
        CelestweaveCosmetic clamped = CelestweaveCosmetic.read(wild);
        check(clamped.glowColor() == 0x123456, "glow color mask");
        check(clamped.idleArcs() == maxArcs, "idle arcs clamped to config");
        near(0.25, clamped.arcFreq(), "NaN arc freq falls back to minimum");
        near(2.5, clamped.arcSpread(), "arc spread max");
        near(0.3, clamped.arcBright(), "arc bright min");
        check(clamped.arcEvents() == CelestweaveCosmetic.EVENT_ALL, "event mask");
        wild.setInteger("IdleArcs", -7);
        check(
            CelestweaveCosmetic.read(wild)
                .idleArcs() == -1,
            "idle arcs min");
    }

    private static void settings() {
        Set<String> keys = new HashSet<>();
        for (CelestweaveSettings.Option option : CelestweaveSettings.OPTIONS) {
            check(keys.add(option.module().id + "." + option.key()), "duplicate option " + option.key());
            check(CelestweaveSettings.option(option.module(), option.key()) == option, "option lookup " + option.key());
            check(option.min() <= option.fallback() && option.fallback() <= option.max(), "fallback " + option.key());
            if (option.bool()) check(option.min() == 0 && option.max() == 1, "bool range " + option.key());
            check(option.get(plainStack()) == option.fallback(), "unset option reads fallback " + option.key());
        }
        check(
            CelestweaveSettings.option(CelestweaveModule.REFLECT, "Ignite")
                .fallback() == 1,
            "ignite defaults on");
        check(
            CelestweaveSettings.option(CelestweaveModule.REFLECT, "Blind")
                .fallback() == 1,
            "blind defaults on");
        ItemStack stack = plainStack();
        check(
            !CelestweaveSettings.apply(stack, CelestweaveModule.FLIGHT, CelestweaveSettings.CFG, "Speed", 3),
            "settings rejected when module not installed");
        check(!CelestweaveSettings.apply(stack, null, CelestweaveSettings.COSMETIC, "Nope", 1), "unknown cosmetic");
        check(
            !CelestweaveSettings.apply(stack, null, CelestweaveSettings.COSMETIC, "GlowColor", Float.NaN),
            "NaN rejected");
        check(
            CelestweaveSettings.apply(stack, null, CelestweaveSettings.COSMETIC, "IdleArcs", 500),
            "cosmetic applied");
        check(
            com.science.gtnl.common.celestweave.CelestweaveData.cosmetic(stack)
                .idleArcs() == MainConfig.item.celestweave.maxIdleArcs,
            "cosmetic write clamps through read");
    }

    private static void energy() throws ReflectiveOperationException {
        double base = MainConfig.item.celestweave.baseCapacity;
        ItemStack stack = plainStack();
        near(0, CelestweaveEnergy.read(stack), "empty charge");
        near(base, CelestweaveEnergy.capacity(stack), "base capacity without modules");
        CelestweaveEnergy.write(stack, base * 10);
        near(base, CelestweaveEnergy.read(stack), "write clamps to capacity");
        CelestweaveEnergy.write(stack, -5);
        near(0, CelestweaveEnergy.read(stack), "write clamps to zero");
        stack.getTagCompound()
            .setDouble("charge", base * 3);
        call(CelestweaveEnergy.class, "deduct", null, new Class<?>[] { ItemStack.class, double.class }, stack, base);
        near(base * 2, CelestweaveEnergy.read(stack), "deduct keeps charge above capacity");
        near(1, CelestweaveEnergy.fraction(stack), "fraction caps at one");
        CelestweaveEnergy.clamp(stack);
        near(base, CelestweaveEnergy.read(stack), "clamp trims to capacity");
        call(
            CelestweaveEnergy.class,
            "deduct",
            null,
            new Class<?>[] { ItemStack.class, double.class },
            stack,
            base * 5);
        near(0, CelestweaveEnergy.read(stack), "deduct floors at zero");
        stack.getTagCompound()
            .setDouble("charge", Double.NaN);
        near(0, CelestweaveEnergy.read(stack), "NaN charge reads zero");
    }

    private static void backpack() throws ReflectiveOperationException {
        Field unsafeField = Class.forName("sun.misc.Unsafe")
            .getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Object unsafe = unsafeField.get(null);
        Item moduleItem = (Item) unsafe.getClass()
            .getMethod("allocateInstance", Class.class)
            .invoke(unsafe, CelestweaveModuleItem.class);
        Field delegate = Item.class.getDeclaredField("delegate");
        Constructor<?> create = delegate.getType()
            .getDeclaredClasses()[0].getDeclaredConstructors()[0];
        create.setAccessible(true);
        long offset = (long) unsafe.getClass()
            .getMethod("objectFieldOffset", Field.class)
            .invoke(unsafe, delegate);
        unsafe.getClass()
            .getMethod("putObject", Object.class, long.class, Object.class)
            .invoke(unsafe, moduleItem, offset, create.newInstance(moduleItem, Item.class));
        for (CelestweaveModule module : CelestweaveModule.values()) {
            ItemStack stack = new ItemStack(moduleItem, 1, module.ordinal());
            check(CelestweaveModule.of(stack) == module, "module meta round trip " + module.id);
            check(
                CelestweaveBackpack.blockedInStorage(stack) == (module == CelestweaveModule.BACKPACK),
                "storage block " + module.id);
        }
        check(!CelestweaveBackpack.blockedInStorage(plainStack()), "plain item allowed in storage");
        check(!CelestweaveBackpack.blockedInStorage(null), "null allowed in storage");
    }

    private static <T> T roundTrip(T packet, Constructor<T> empty) throws ReflectiveOperationException {
        ByteBuf buffer = Unpooled.buffer();
        ((cpw.mods.fml.common.network.simpleimpl.IMessage) packet).toBytes(buffer);
        T copy = empty.newInstance();
        ((cpw.mods.fml.common.network.simpleimpl.IMessage) copy).fromBytes(buffer);
        check(
            buffer.readableBytes() == 0,
            packet.getClass()
                .getSimpleName() + " leaves no trailing bytes");
        return copy;
    }

    private static void packets() throws ReflectiveOperationException {
        CelestweaveFxPacket fx = roundTrip(
            new CelestweaveFxPacket(42, CelestweaveFxPacket.SHIELD_AMBIENT, 1.5, -2.25, 1.0E6),
            CelestweaveFxPacket.class.getConstructor());
        check((int) field(fx, "entityId") == 42, "fx entity id");
        check((byte) field(fx, "type") == CelestweaveFxPacket.SHIELD_AMBIENT, "fx type");
        near(-2.25, (float) field(fx, "y"), "fx y");
        int[] types = { CelestweaveFxPacket.SHIELD_HIT, CelestweaveFxPacket.DASH, CelestweaveFxPacket.LAND,
            CelestweaveFxPacket.REFLECT, CelestweaveFxPacket.SAVE, CelestweaveFxPacket.LIGHTNING,
            CelestweaveFxPacket.SHIELD_AMBIENT };
        Set<Integer> unique = new HashSet<>();
        for (int i = 0; i < types.length; i++) check(unique.add(types[i]) && types[i] == i, "fx types contiguous");
        CelestweaveFlightPacket flight = roundTrip(
            new CelestweaveFlightPacket(7, true),
            CelestweaveFlightPacket.class.getConstructor());
        check((int) field(flight, "entityId") == 7 && (boolean) field(flight, "flying"), "flight packet");
        CelestweaveKeyPacket key = roundTrip(
            new CelestweaveKeyPacket(CelestweaveKeyPacket.OPEN_PIECE, 3),
            CelestweaveKeyPacket.class.getConstructor());
        check((byte) field(key, "action") == CelestweaveKeyPacket.OPEN_PIECE && (byte) field(key, "piece") == 3, "key");
    }

    private static void envelopes() throws ReflectiveOperationException {
        Constructor<CwClientState> constructor = CwClientState.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        CwClientState state = constructor.newInstance();
        check(state.hitGlow(1000) == 0F, "no hit glow before any hit");
        state.hitAt = 1000;
        check(state.hitGlow(999) == 0F && state.hitGlow(2000) == 0F, "hit glow window");
        near(1, state.hitGlow(1040), "hit glow peak");
        check(state.hitGlow(1100) > state.hitGlow(1300) && state.hitGlow(1300) > 0, "hit glow decays");
        state.hitPower = 1.8F;
        near(1.8, state.hitGlow(1040), "hit glow scales with power");
        Class<?>[] floatArg = { float.class };
        near(0, (float) call(CwCoreBurst.class, "easeOut", null, floatArg, 0F), "ease start");
        near(1, (float) call(CwCoreBurst.class, "easeOut", null, floatArg, 1F), "ease end");
        near(1, (float) call(CwCoreBurst.class, "easeOut", null, floatArg, 5F), "ease clamps high");
        near(0, (float) call(CwCoreBurst.class, "easeOut", null, floatArg, -5F), "ease clamps low");
        float previous = 0F;
        for (int i = 1; i <= 20; i++) {
            float value = (float) call(CwCoreBurst.class, "easeOut", null, floatArg, i / 20F);
            check(value >= previous, "ease monotonic");
            previous = value;
        }
    }

    private static void hud() {
        near(1, CwHud.fade(-100), "hud awake before idle");
        near(1, CwHud.fade(0), "hud awake at idle edge");
        near(0.5, CwHud.fade(250), "hud fades halfway");
        near(0, CwHud.fade(10000), "hud fully faded");
        near(0, CwHud.easeOut(-1), "hud slide clamps low");
        near(1, CwHud.easeOut(2), "hud slide clamps high");
        check(CwHud.easeOut(0.5F) > 0.5F, "hud slide eases out");
        near(1, CwHud.segmentWeight(0, 0), "front hit lights front segment");
        near(0, CwHud.segmentWeight(0, 3), "front hit leaves back segment dark");
        near(1, CwHud.segmentWeight(Math.PI / 3, 1), "right hit lights right segment");
        near(1, CwHud.segmentWeight(-Math.PI / 3, 5), "left hit lights left segment");
        near(0.75, CwHud.segmentWeight(Math.PI / 2, 1), "side hit lights the front side segment");
        near(0.75, CwHud.segmentWeight(Math.PI / 2, 2), "side hit lights the back side segment");
        float[] core = CwHud.polygon(8, 0), ring = CwHud.polygon(8, Math.PI / 6);
        near(0, core[0], "core hexagon top vertex x");
        near(-8, core[1], "core hexagon top vertex y");
        near(8, ring[2], "ring hexagon right vertex x");
        near(0, ring[3], "ring hexagon right vertex y");
        check(CwHud.clipBelow(core, 9).length == 0, "empty core has no fill");
        check(Arrays.equals(CwHud.clipBelow(core, -9), core), "full core keeps the hexagon");
        float[] half = CwHud.clipBelow(core, 0);
        check(half.length >= 6, "half core is a polygon");
        for (int i = 1; i < half.length; i += 2) check(half[i] >= -1.0E-5F, "half core stays below the level");
        float[] level = CwHud.span(half, 0);
        check(level != null && Math.abs(level[0] + level[1]) < 1.0E-4F, "fill cap is centred");
        check(level != null && Math.abs(level[1] - 8 * Math.sqrt(3) / 2) < 1.0E-3F, "fill cap reaches the sides");
        check(CwHud.span(core, 3) == null, "no fill cap off the edge");
        near(50, CwHud.drainRate(1000, 0, 3), "drain over one second of samples");
        near(-50, CwHud.drainRate(0, 1000, 3), "charge reads negative");
        near(0, CwHud.drainRate(1000, 0, 1), "one sample has no rate");
        check("0:42".equals(CwHud.duration(42)), "short duration");
        check("1:00:05".equals(CwHud.duration(3605)), "hour duration");
        check(">99h".equals(CwHud.duration(400000)), "capped duration");
        check(
            CwHud.duration(Double.POSITIVE_INFINITY)
                .isEmpty(),
            "no duration when not draining");
    }

    private static int alpha(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >>> 24;
    }

    private static void hexTexture() throws ReflectiveOperationException {
        BufferedImage image = (BufferedImage) call(CwShieldFlash.class, "buildHex", null, new Class<?>[0]);
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

    private static void model() throws Exception {
        Path armor = RESOURCES.resolve("models/armor");
        CwFrames frames;
        try (var reader = Files.newBufferedReader(armor.resolve("celestweave_frames.json"), StandardCharsets.UTF_8)) {
            frames = new Gson().fromJson(reader, CwFrames.class);
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
        for (CwFrames.Group group : frames.groups) {
            String where = "group " + group.name;
            check(group.type >= 0, where + " has known obj " + group.obj);
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
        for (CwFrames.Frame frame : frames.frames) check(frame.bone >= 0, "frame bone " + frame.name);
        CwAnimator.bind(frames);
        for (Field field : CwAnimator.class.getDeclaredFields()) {
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

    private static void lang() throws Exception {
        Set<String> expected = new TreeSet<>();
        for (CelestweaveModule module : CelestweaveModule.values()) {
            expected.add("item.gtnl.celestweave_module." + module.id + ".name");
            expected.add("item.gtnl.celestweave_module." + module.id + ".tooltip");
            expected.add("item.gtnl.celestweave_module.slots." + module.slots);
            if (module.toggleable()) expected.add("item.gtnl.celestweave.short." + module.id);
            if (module.holdsItem) expected.add("item.gtnl.celestweave.gui.held." + module.id);
        }
        for (CelestweaveSettings.Option option : CelestweaveSettings.OPTIONS) expected.add(
            "item.gtnl.celestweave.option." + option.module().id
                + "."
                + option.key()
                    .toLowerCase());
        Field pages = CelestweaveGui.class.getDeclaredField("PAGES"),
            events = CelestweaveGui.class.getDeclaredField("EVENT_KEYS");
        pages.setAccessible(true);
        events.setAccessible(true);
        for (String page : (String[]) pages.get(null)) expected.add("item.gtnl.celestweave.gui.page." + page);
        for (String event : (String[]) events.get(null)) {
            expected.add("item.gtnl.celestweave.gui.event." + event);
            expected.add("item.gtnl.celestweave.gui.event." + event + ".tooltip");
        }
        for (int piece = 0; piece < 4; piece++) expected.add("item.gtnl.celestweave.radial.piece." + piece);
        for (Field field : MainConfig.Client.class.getFields()) {
            Config.LangKey key = field.getAnnotation(Config.LangKey.class);
            if (key == null || !field.getName()
                .startsWith("celestweave")) continue;
            expected.add(key.value());
            expected.add(key.value() + ".tooltip");
        }
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
        Set<String> english = langKeys("en_US.lang"), chinese = langKeys("zh_CN.lang");
        for (String key : expected) {
            check(english.contains(key), "en_US missing " + key);
            check(chinese.contains(key), "zh_CN missing " + key);
        }
        for (String key : english)
            if (key.contains("celestweave")) check(chinese.contains(key), "zh_CN missing celestweave key " + key);
        for (String key : chinese)
            if (key.contains("celestweave")) check(english.contains(key), "en_US missing celestweave key " + key);
        check(new File("src/main/resources").isDirectory(), "run from the project root");
    }
}
