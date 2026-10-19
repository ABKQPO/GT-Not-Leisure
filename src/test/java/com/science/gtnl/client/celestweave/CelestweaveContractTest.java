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
import java.util.BitSet;
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
import com.science.gtnl.common.celestweave.CelestweaveBehaviors;
import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveModuleSetting;
import com.science.gtnl.common.celestweave.CelestweaveSettings;
import com.science.gtnl.common.celestweave.CelestweaveStripStyle;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.gui.CelestweaveGui;
import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.item.items.CelestweaveModuleItem;
import com.science.gtnl.common.item.items.EnergyModuleItem;
import com.science.gtnl.common.item.items.RailgunModuleItem;
import com.science.gtnl.common.packet.CelestweaveFlightPacket;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunModuleType;
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
        caches();
        packets();
        envelopes();
        hud();
        hexTexture();
        model();
        raisedGlow();
        firstPersonArm();
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
            if (module.hideable()) check(module.fits(0) || module.fits(1) || module.fits(2), "hideable " + module.id);
        }
        Set<CelestweaveModule> hideable = java.util.EnumSet.of(
            CelestweaveModule.REVEALING,
            CelestweaveModule.HOLO,
            CelestweaveModule.BACKPACK,
            CelestweaveModule.FLIGHT);
        Set<CelestweaveModule> holders = java.util.EnumSet
            .of(CelestweaveModule.REVEALING, CelestweaveModule.HOLO, CelestweaveModule.BACKPACK);
        Set<CelestweaveModule> behaving = java.util.EnumSet.of(
            CelestweaveModule.NIGHT_VISION,
            CelestweaveModule.SATURATION,
            CelestweaveModule.PURIFICATION,
            CelestweaveModule.TERRASTEEL,
            CelestweaveModule.REFLECT,
            CelestweaveModule.SHIELD,
            CelestweaveModule.FLIGHT,
            CelestweaveModule.WALK_ASSIST);
        Object none = CelestweaveBehaviors.of(CelestweaveModule.APIARIST);
        for (CelestweaveModule module : CelestweaveModule.values()) {
            check(module.hideable() == hideable.contains(module), "hideable trait " + module.id);
            check(module.holdsItem == holders.contains(module), "held item trait " + module.id);
            check(
                (CelestweaveBehaviors.of(module) != none) == behaving.contains(module),
                "behavior declared " + module.id);
        }
        for (CwFrames.Part part : CwFrames.Part.values()) check(
            part.module == null || part.module.fits(part.piece) && part.module.hideable(),
            "part " + part + " follows a hideable module on its piece");
        String[] metaOrder = { "apiarist", "night_vision", "saturation", "revealing", "holo", "shield", "reflect",
            "purification", "hazard", "soulbound", "reach", "terrasteel", "backpack", "flight", "walk_assist", "dash",
            "dig_affinity" };
        for (int meta = 0; meta < metaOrder.length; meta++) {
            CelestweaveModule module = CelestweaveModule.byMeta(meta);
            check(
                module != null && module.id.equals(metaOrder[meta]),
                "module meta " + meta + " stays " + metaOrder[meta]);
        }
        check(CelestweaveModule.metaCount() == metaOrder.length, "new modules take a new meta after the existing ones");
        Set<Integer> metas = new HashSet<>();
        for (CelestweaveModule module : CelestweaveModule.values())
            check(metas.add(module.meta), "duplicate module meta " + module.id);
        check(EnergyModuleItem.tier(plainStack()) == 0, "plain item is not an energy module");
        check(CelestweaveModule.of(plainStack()) == null, "plain item is not a module");
    }

    private static void cosmetic() {
        int maxArcs = MainConfig.item.celestweave.maxIdleArcs;
        check(CelestweaveCosmetic.read(new NBTTagCompound()) == CelestweaveCosmetic.DEFAULT, "empty cosmetic");
        Set<String> optionKeys = new HashSet<>();
        for (CelestweaveCosmeticOption option : CelestweaveCosmeticOption.values()) {
            check(optionKeys.add(option.key), "duplicate cosmetic key " + option.key);
            check(CelestweaveCosmeticOption.byKey(option.key) == option, "cosmetic lookup " + option.key);
            check(option.clamp(option.fallback) == option.fallback, "cosmetic fallback in range " + option.key);
            check(
                CelestweaveCosmetic.DEFAULT.get(option) == option.fallback,
                "default cosmetic uses fallback " + option.key);
            boolean named = option.kind == CelestweaveCosmeticOption.Kind.CHOICE
                || option.kind == CelestweaveCosmeticOption.Kind.FLAGS;
            check(
                named == (!option.names()
                    .isEmpty()),
                "cosmetic names match kind " + option.key);
        }
        CelestweaveCosmeticOption events = CelestweaveCosmeticOption.ARC_EVENTS;
        int[] eventFlags = { CelestweaveCosmetic.EVENT_HIT, CelestweaveCosmetic.EVENT_DASH,
            CelestweaveCosmetic.EVENT_LAND, CelestweaveCosmetic.EVENT_REFLECT, CelestweaveCosmetic.EVENT_WING,
            CelestweaveCosmetic.EVENT_ANTENNA, CelestweaveCosmetic.EVENT_LOW };
        check(
            events.names()
                .size() == eventFlags.length,
            "every event flag has a name");
        for (int i = 0; i < eventFlags.length; i++) check(events.flag(i) == eventFlags[i], "event flag order " + i);
        check(
            CelestweaveCosmeticOption.STRIP_STYLE.names()
                .size() == CelestweaveStripStyle.values().length,
            "strip effect choices follow the styles");
        for (CelestweaveStripStyle style : CelestweaveStripStyle.values())
            check(CwStripStyles.registered(style), "strip style has an effect " + style.id);
        check(
            !CelestweaveCosmeticOption.STRIP_SPEED.appliesTo(CelestweaveCosmetic.DEFAULT),
            "strip speed does not apply to steady strips");
        check(
            CelestweaveCosmeticOption.STRIP_SPEED.appliesTo(
                CelestweaveCosmetic.DEFAULT
                    .with(CelestweaveCosmeticOption.STRIP_STYLE, CelestweaveStripStyle.FLOW.ordinal())),
            "strip speed applies to flowing strips");
        near(127, events.max(), "event mask covers every named flag");
        CelestweaveCosmetic sample = CelestweaveCosmetic.DEFAULT.with(CelestweaveCosmeticOption.GLOW_COLOR, 0x123456)
            .with(CelestweaveCosmeticOption.IDLE_ARCS, 3)
            .with(CelestweaveCosmeticOption.ARC_FREQ, 1.5F)
            .with(CelestweaveCosmeticOption.ARC_SPREAD, 2F)
            .with(CelestweaveCosmeticOption.ARC_BRIGHT, 0.5F)
            .with(CelestweaveCosmeticOption.ARC_EVENTS, 5)
            .with(CelestweaveCosmeticOption.STRIP_STYLE, CelestweaveStripStyle.GRADIENT.ordinal());
        check(
            sample.write()
                .getString("StripFx")
                .equals(CelestweaveStripStyle.GRADIENT.id),
            "strip effect stored by id");
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
        check(clamped.getInt(CelestweaveCosmeticOption.GLOW_COLOR) == 0x123456, "glow color mask");
        check(clamped.getInt(CelestweaveCosmeticOption.IDLE_ARCS) == maxArcs, "idle arcs clamped to config");
        near(0.25, clamped.get(CelestweaveCosmeticOption.ARC_FREQ), "NaN arc freq falls back to minimum");
        near(2.5, clamped.get(CelestweaveCosmeticOption.ARC_SPREAD), "arc spread max");
        near(0.3, clamped.get(CelestweaveCosmeticOption.ARC_BRIGHT), "arc bright min");
        check(clamped.getInt(CelestweaveCosmeticOption.ARC_EVENTS) == CelestweaveCosmetic.EVENT_ALL, "event mask");
        wild.setInteger("IdleArcs", -7);
        check(
            CelestweaveCosmetic.read(wild)
                .getInt(CelestweaveCosmeticOption.IDLE_ARCS) == -1,
            "idle arcs min");
        check(clamped.stripStyle() == CelestweaveStripStyle.STEADY, "strip effect defaults to steady");
        wild.setInteger("StripFx", 99);
        check(
            CelestweaveCosmetic.read(wild)
                .stripStyle() == CelestweaveStripStyle.GRADIENT,
            "old numeric strip effect clamps to the last style");
        wild.setInteger("StripFx", 1);
        check(
            CelestweaveCosmetic.read(wild)
                .stripStyle() == CelestweaveStripStyle.BREATH,
            "old numeric strip effect still reads");
        wild.setString("StripFx", "nope");
        check(
            CelestweaveCosmetic.read(wild)
                .stripStyle() == CelestweaveStripStyle.STEADY,
            "unknown strip effect id falls back to steady");
        ItemStack chest = plainStack();
        for (CelestweaveStripStyle style : CelestweaveStripStyle.values()) {
            check(
                CelestweaveSettings.apply(
                    chest,
                    null,
                    CelestweaveSettings.COSMETIC,
                    CelestweaveCosmeticOption.STRIP_STYLE.key,
                    style.ordinal()),
                "strip effect " + style.id + " applies");
            check(
                CelestweaveData.cosmetic(chest)
                    .stripStyle() == style,
                "strip effect " + style.id + " is stored");
        }
        int[] gradient = CwStripStyles.gradientPixels();
        Set<Integer> hues = new HashSet<>();
        for (int rgb : gradient) hues.add(rgb & 0xFFFFFF);
        check(hues.size() > gradient.length / 2, "rainbow gradient spans many hues");
        int[] flow = CwStripStyles.flowPixels();
        int peak = flow[flow.length / 2] & 255, edge = flow[0] & 255;
        check(peak > 240 && edge < 90 && edge > 40, "flowing light has a bright crest over a dim base");
        near(0, CwStripStyles.breath(0F), "breathing starts dark");
        near(1, CwStripStyles.breath(1.8F), "breathing peaks mid-cycle");
        near(0, CwStripStyles.breath(3.4F), "breathing rests dark before the next breath");
        check(CwStripStyles.breath(0.8F) > 0.2F && CwStripStyles.breath(0.8F) < 0.8F, "breathing rises smoothly");
    }

    private static void settings() {
        Set<String> keys = new HashSet<>();
        for (CelestweaveModuleSetting setting : CelestweaveModuleSetting.values()) {
            check(keys.add(setting.module.id + "." + setting.key), "duplicate setting " + setting.key);
            check(
                CelestweaveModuleSetting.byKey(setting.module, setting.key) == setting,
                "setting lookup " + setting.key);
            check(setting.min <= setting.fallback && setting.fallback <= setting.max(), "fallback " + setting.key);
            if (setting.bool()) check(setting.min == 0 && setting.max() == 1, "bool range " + setting.key);
            check(setting.get(plainStack()) == setting.fallback, "unset setting reads fallback " + setting.key);
        }
        check(CelestweaveModuleSetting.REFLECT_IGNITE.on(plainStack()), "ignite defaults on");
        check(CelestweaveModuleSetting.REFLECT_BLIND.on(plainStack()), "blind defaults on");
        ItemStack stack = plainStack();
        check(
            !CelestweaveSettings.apply(stack, CelestweaveModule.FLIGHT, CelestweaveSettings.VALUE, "Speed", 3),
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
                .getInt(CelestweaveCosmeticOption.IDLE_ARCS) == MainConfig.item.celestweave.maxIdleArcs,
            "cosmetic write clamps through read");
    }

    private static void energy() throws ReflectiveOperationException {
        double base = MainConfig.item.celestweave.baseCapacity;
        ItemStack stack = plainStack();
        near(0, EquipmentEnergy.read(stack), "empty charge");
        near(base, CelestweaveEnergy.capacity(stack), "base capacity without modules");
        CelestweaveEnergy.write(stack, base * 10);
        near(base, EquipmentEnergy.read(stack), "write clamps to capacity");
        CelestweaveEnergy.write(stack, -5);
        near(0, EquipmentEnergy.read(stack), "write clamps to zero");
        stack.getTagCompound()
            .setDouble("charge", base * 3);
        EquipmentEnergy.settle(stack, base);
        near(base * 2, EquipmentEnergy.read(stack), "deduct keeps charge above capacity");
        near(1, CelestweaveEnergy.fraction(stack), "fraction caps at one");
        CelestweaveEnergy.clamp(stack);
        near(base, EquipmentEnergy.read(stack), "clamp trims to capacity");
        EquipmentEnergy.settle(stack, base * 5);
        near(0, EquipmentEnergy.read(stack), "deduct floors at zero");
        stack.getTagCompound()
            .setDouble("charge", Double.NaN);
        near(0, EquipmentEnergy.read(stack), "NaN charge reads zero");
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
            ItemStack stack = new ItemStack(moduleItem, 1, module.meta);
            check(
                CelestweaveModule.of(stack) == (module.builtIn ? null : module),
                "module meta round trip " + module.id);
            check(
                CelestweaveBackpack.blockedInStorage(stack) == (module == CelestweaveModule.BACKPACK),
                "storage block " + module.id);
        }
        check(!CelestweaveBackpack.blockedInStorage(plainStack()), "plain item allowed in storage");
        check(!CelestweaveBackpack.blockedInStorage(null), "null allowed in storage");
    }

    private static void caches() throws ReflectiveOperationException {
        Item plain = registered(new Item(), "gtnl:contract_plain", 31104);
        CelestweaveModuleItem armorModule = registered(
            bare(CelestweaveModuleItem.class),
            "gtnl:contract_celestweave_module",
            31101);
        EnergyModuleItem energyModule = registered(bare(EnergyModuleItem.class), "gtnl:contract_energy_module", 31102);
        RailgunModuleItem railgunModule = registered(
            bare(RailgunModuleItem.class),
            "gtnl:contract_railgun_module",
            31103);

        ItemStack chest = plainStack();
        CelestweaveData.writeModuleStacks(
            chest,
            new ItemStack[] { energyModule.stack(3), armorModule.stack(CelestweaveModule.SHIELD),
                armorModule.stack(CelestweaveModule.REFLECT) });
        check(
            CelestweaveData.installed(chest)
                .size() == 2,
            "installed ignores the energy module");
        check(CelestweaveData.energyTier(chest) == 3, "energy tier read from modules");
        check(CelestweaveData.active(chest, CelestweaveModule.SHIELD), "installed module starts active");
        CelestweaveData.writeFlag(chest, CelestweaveData.DISABLED, CelestweaveModule.SHIELD, true);
        Set<CelestweaveModule> active = CelestweaveData.activeModules(chest);
        check(!active.contains(CelestweaveModule.SHIELD), "disabling refreshes active modules");
        check(active.contains(CelestweaveModule.REFLECT), "other modules stay active");
        check(CelestweaveData.activeModules(chest) == active, "active modules cached while unchanged");
        check(!CelestweaveData.active(chest, CelestweaveModule.SHIELD), "disabling refreshes active");
        CelestweaveData.writeModuleStacks(
            chest,
            new ItemStack[] { energyModule.stack(1), armorModule.stack(CelestweaveModule.SHIELD),
                armorModule.stack(CelestweaveModule.REFLECT), armorModule.stack(CelestweaveModule.HAZARD) });
        check(
            CelestweaveData.activeModules(chest)
                .contains(CelestweaveModule.HAZARD),
            "module change refreshes active modules under the same disabled list");
        check(CelestweaveData.energyTier(chest) == 1, "module change refreshes energy tier");
        CelestweaveData.writeFlag(chest, CelestweaveData.DISABLED, CelestweaveModule.SHIELD, false);
        check(CelestweaveData.active(chest, CelestweaveModule.SHIELD), "enabling refreshes active");
        CelestweaveData.writeFlag(chest, CelestweaveData.HIDDEN, CelestweaveModule.BACKPACK, true);
        check(
            readOnly(
                () -> CelestweaveData.idList(chest, CelestweaveData.HIDDEN)
                    .add("x")),
            "cached id list is read only");

        ItemStack pack = new ItemStack(plain);
        pack.setTagCompound(new NBTTagCompound());
        pack.getTagCompound()
            .setTag("Items", new NBTTagCompound());
        ItemStack module = CelestweaveData.withHeld(armorModule.stack(CelestweaveModule.BACKPACK), pack);
        pack.getTagCompound()
            .removeTag("Items");
        check(
            CelestweaveData.heldOf(module)
                .getTagCompound()
                .hasKey("Items"),
            "withHeld stores a copy of the held item");
        ItemStack stripped = CelestweaveBackpack.strip(module);
        check(
            !CelestweaveData.heldOf(stripped)
                .getTagCompound()
                .hasKey("Items"),
            "strip removes backpack contents");
        check(
            CelestweaveData.heldOf(module)
                .getTagCompound()
                .hasKey("Items"),
            "strip leaves the original module untouched");

        ItemStack railgun = plainStack();
        check(
            RailgunData.modules(railgun)
                .energyTier() == 0,
            "railgun without modules");
        RailgunData.writeModuleStacks(
            railgun,
            new ItemStack[] { energyModule.stack(2), railgunModule.stack(RailgunModuleType.COMPUTE),
                railgunModule.stack(RailgunModuleType.COMPUTE), railgunModule.stack(RailgunModuleType.COMPUTE) });
        RailgunData.Modules modules = RailgunData.modules(railgun);
        check(modules.count(RailgunModuleType.COMPUTE) == 2, "railgun module count capped at limit");
        check(modules.energyTier() == 2, "railgun energy tier");
        check(RailgunData.modules(railgun) == modules, "railgun modules cached while unchanged");
        RailgunData.writeModuleStacks(railgun, new ItemStack[] { railgunModule.stack(RailgunModuleType.RANGE) });
        RailgunData.Modules changed = RailgunData.modules(railgun);
        check(
            changed.count(RailgunModuleType.COMPUTE) == 0 && changed.count(RailgunModuleType.RANGE) == 1
                && changed.energyTier() == 0,
            "railgun module change refreshes cache");
        check(
            readOnly(
                () -> changed.counts()
                    .put(RailgunModuleType.RANGE, 2)),
            "cached railgun counts are read only");

        check(
            CelestweaveData.builtIn(0)
                .equals(java.util.EnumSet.of(CelestweaveModule.REVEALING, CelestweaveModule.HOLO)),
            "helmet has built-in goggles and holo slots");
        for (int piece = 1; piece < 4; piece++) check(
            CelestweaveData.builtIn(piece)
                .isEmpty(),
            "no built-in slots on piece " + piece);
        check(
            !CelestweaveData.installed(chest)
                .contains(CelestweaveModule.REVEALING),
            "non-armor hosts have no built-in helmet slots");
        ItemStack helmet = plainStack();
        check(CelestweaveData.held(helmet, CelestweaveModule.REVEALING) == null, "built-in slot starts empty");
        CelestweaveData.writeHeld(helmet, CelestweaveModule.REVEALING, new ItemStack(plain));
        ItemStack goggles = CelestweaveData.held(helmet, CelestweaveModule.REVEALING);
        check(goggles != null && goggles.getItem() == plain, "built-in slot stores its item");
        check(CelestweaveData.held(helmet, CelestweaveModule.REVEALING) == goggles, "built-in held item cached");
        check(CelestweaveData.held(helmet, CelestweaveModule.HOLO) == null, "built-in slots are separate");
        CelestweaveData
            .writeModuleStacks(helmet, new ItemStack[] { armorModule.stack(CelestweaveModule.NIGHT_VISION) });
        check(
            CelestweaveData.held(helmet, CelestweaveModule.REVEALING) == goggles,
            "module change keeps the built-in held item");
        CelestweaveData.writeHeld(helmet, CelestweaveModule.REVEALING, null);
        check(CelestweaveData.held(helmet, CelestweaveModule.REVEALING) == null, "built-in slot clears");
    }

    private static boolean readOnly(Runnable write) {
        try {
            write.run();
            return false;
        } catch (UnsupportedOperationException expected) {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item> T bare(Class<T> type) throws ReflectiveOperationException {
        Field unsafeField = Class.forName("sun.misc.Unsafe")
            .getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Object unsafe = unsafeField.get(null);
        T item = (T) unsafe.getClass()
            .getMethod("allocateInstance", Class.class)
            .invoke(unsafe, type);
        Field delegate = Item.class.getDeclaredField("delegate");
        Constructor<?> create = delegate.getType()
            .getDeclaredClasses()[0].getDeclaredConstructors()[0];
        create.setAccessible(true);
        long offset = (long) unsafe.getClass()
            .getMethod("objectFieldOffset", Field.class)
            .invoke(unsafe, delegate);
        unsafe.getClass()
            .getMethod("putObject", Object.class, long.class, Object.class)
            .invoke(unsafe, item, offset, create.newInstance(item, Item.class));
        return item;
    }

    private static <T extends Item> T registered(T item, String name, int id) throws ReflectiveOperationException {
        Method add = Item.itemRegistry.getClass()
            .getDeclaredMethod("add", int.class, String.class, Object.class, BitSet.class);
        add.setAccessible(true);
        add.invoke(Item.itemRegistry, id, name, item, new BitSet());
        return item;
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
        near(0, CwHud.easeOut(0F), "ease start");
        near(1, CwHud.easeOut(1F), "ease end");
        near(1, CwHud.easeOut(5F), "ease clamps high");
        near(0, CwHud.easeOut(-5F), "ease clamps low");
        float previous = 0F;
        for (int i = 1; i <= 20; i++) {
            float value = CwHud.easeOut(i / 20F);
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
        near(50, CwHud.drainRate(1000, 0, 1000), "drain over one second");
        near(-50, CwHud.drainRate(0, 1000, 1000), "charge reads negative");
        near(0, CwHud.drainRate(1000, 0, 0), "no elapsed time has no rate");
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

    private static void raisedGlow() throws Exception {
        net.minecraftforge.client.model.obj.Face face = new net.minecraftforge.client.model.obj.Face();
        face.vertices = new net.minecraftforge.client.model.obj.Vertex[] {
            new net.minecraftforge.client.model.obj.Vertex(0, 0, 0),
            new net.minecraftforge.client.model.obj.Vertex(4, 0, 0),
            new net.minecraftforge.client.model.obj.Vertex(4, 4, 0),
            new net.minecraftforge.client.model.obj.Vertex(0, 4, 0) };
        face.textureCoordinates = new net.minecraftforge.client.model.obj.TextureCoordinate[] {
            new net.minecraftforge.client.model.obj.TextureCoordinate(0, 0),
            new net.minecraftforge.client.model.obj.TextureCoordinate(1, 0),
            new net.minecraftforge.client.model.obj.TextureCoordinate(1, 1),
            new net.minecraftforge.client.model.obj.TextureCoordinate(0, 1) };
        int[] pixels = new int[16];
        Arrays.fill(pixels, 0xFF000000);
        check(
            CwRaise.raise(List.of(face), pixels, 4, 4, 0.25F)
                .isEmpty(),
            "dark glow face raises nothing");
        pixels[4 + 1] = pixels[4 + 2] = 0xFFFFFFFF;
        List<float[]> quads = CwRaise.raise(List.of(face), pixels, 4, 4, 0.25F);
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
                check(CwRaise.lit(pixels[y * 4 + x]), "raised strip samples lit texels");
            }
        }

        Path armor = RESOURCES.resolve("models/armor");
        BufferedImage glow = javax.imageio.ImageIO.read(
            armor.getParent()
                .getParent()
                .resolve("textures/models/armor/celestweave_glow.png")
                .toFile());
        int width = glow.getWidth(), height = glow.getHeight();
        int[] argb = glow.getRGB(0, 0, width, height, null, 0, width);
        for (String file : new String[] { "celestweave_oculus.obj", "celestweave_core.obj", "celestweave_conduit.obj",
            "celestweave_stride.obj" }) {
            net.minecraftforge.client.model.obj.WavefrontObject obj;
            try (var stream = Files.newInputStream(armor.resolve(file))) {
                obj = new net.minecraftforge.client.model.obj.WavefrontObject(file, stream);
            }
            for (net.minecraftforge.client.model.obj.GroupObject group : obj.groupObjects) {
                if (!group.name.contains("__glowFaces__")) continue;
                List<float[]> raised = CwRaise.raise(group.faces, argb, width, height, CwRaise.HEIGHT);
                check(!raised.isEmpty() && raised.size() % 5 == 0, "glow strips raised for " + group.name);
                boolean sampled = true;
                for (int q = 0; q < raised.size(); q += 5) {
                    float[] cap = raised.get(q);
                    float u = (cap[3] + cap[13]) / 2, v = (cap[4] + cap[14]) / 2;
                    sampled &= CwRaise.lit(
                        argb[Math.min(height - 1, (int) (v * height)) * width
                            + Math.min(width - 1, (int) (u * width))]);
                }
                check(sampled, "raised strip tops sample lit glow texels in " + group.name);
                for (net.minecraftforge.client.model.obj.Face glowFace : group.faces) {
                    List<float[]> strips = CwRaise.raise(List.of(glowFace), argb, width, height, CwRaise.HEIGHT);
                    if (strips.isEmpty()) continue;
                    float[] lo = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE },
                        hi = { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };
                    for (int q = 0; q < strips.size(); q += 5)
                        for (int i = 0; i < 20; i += 5) for (int k = 0; k < 3; k++) {
                            lo[k] = Math.min(lo[k], strips.get(q)[i + k]);
                            hi[k] = Math.max(hi[k], strips.get(q)[i + k]);
                        }
                    float[] mid = new float[3];
                    for (net.minecraftforge.client.model.obj.Vertex vertex : glowFace.vertices) {
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
        CwFrames frames;
        try (var reader = Files
            .newBufferedReader(RESOURCES.resolve("models/armor/celestweave_frames.json"), StandardCharsets.UTF_8)) {
            frames = new Gson().fromJson(reader, CwFrames.class);
        }
        frames.resolve();
        check("rightArm".equals(CwFrames.BONES[CwFrames.RIGHT_ARM]), "right arm bone index");
        Set<CwFrames.Layer> armLayers = new HashSet<>();
        for (CwFrames.Group group : frames.groups(CelestweaveArmor.CHEST))
            if (group.bone == CwFrames.RIGHT_ARM) armLayers.add(group.layerKind);
        check(armLayers.contains(CwFrames.Layer.SOLID), "chest has solid parts on the right arm");
        check(armLayers.contains(CwFrames.Layer.GLOW_FACES), "chest has glow strips on the right arm");
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
        for (int piece = 0; piece < 4; piece++) expected.add("item.gtnl.celestweave.radial.piece." + piece);
        for (Field field : MainConfig.Client.class.getFields()) {
            Config.LangKey key = field.getAnnotation(Config.LangKey.class);
            if (key == null || !field.getName()
                .toLowerCase()
                .contains("celestweave")) continue;
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
