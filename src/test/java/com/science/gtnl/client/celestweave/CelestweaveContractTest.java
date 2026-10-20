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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.science.gtnl.client.fx.FxAngle;
import com.science.gtnl.client.fx.FxEasing;
import com.science.gtnl.client.fx.world.FxVisibility;
import com.science.gtnl.client.hud.HudElement;
import com.science.gtnl.client.hud.HudLayout;
import com.science.gtnl.client.hud.HudRegistry;
import com.science.gtnl.client.railgun.RailgunBolts;
import com.science.gtnl.client.railgun.RailgunChargeState;
import com.science.gtnl.client.railgun.RailgunHudRenderer;
import com.science.gtnl.client.railgun.RailgunOrbRenderer;
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
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.equipment.IEquipmentVariant;
import com.science.gtnl.common.equipment.MetaLookup;
import com.science.gtnl.common.gui.equipment.CelestweaveGui;
import com.science.gtnl.common.gui.modularui.wheel.WheelActionContext;
import com.science.gtnl.common.gui.modularui.wheel.WheelEntry;
import com.science.gtnl.common.gui.modularui.wheel.WheelGeometry;
import com.science.gtnl.common.gui.modularui.wheel.WheelHighlight;
import com.science.gtnl.common.gui.modularui.wheel.WheelMenuBuilder;
import com.science.gtnl.common.gui.modularui.wheel.WheelMenuModel;
import com.science.gtnl.common.gui.modularui.wheel.WheelNavigator;
import com.science.gtnl.common.gui.modularui.wheel.WheelOpenMode;
import com.science.gtnl.common.gui.modularui.wheel.WheelPageModel;
import com.science.gtnl.common.gui.modularui.wheel.WheelStyle;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.item.items.equipment.CelestweaveModuleItem;
import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;
import com.science.gtnl.common.item.items.equipment.RailgunModuleItem;
import com.science.gtnl.common.packet.CelestweaveFlightPacket;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnl.common.packet.RailgunChargeCapPacket;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunDefaults;
import com.science.gtnl.common.railgun.RailgunModuleType;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CelestweaveContractTest {

    private static final Path RESOURCES = Paths.get("src/main/resources/assets/sciencenotleisure");
    private static final Path SOURCES = Paths.get("src/main/java/com/science/gtnl");
    private static final List<String> FAILURES = new ArrayList<>();
    private static int assertions;

    private CelestweaveContractTest() {}

    public static void main(String[] args) throws Exception {
        modules();
        equipment();
        cosmetic();
        settings();
        energy();
        backpack();
        caches();
        packets();
        envelopes();
        hud();
        hudLayout();
        wheel();
        railgunCharge();
        railgunBolts();
        railgunOrbPulse();
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

    private static void equipment() {
        for (CelestweaveModule module : CelestweaveModule.values()) check(
            CelestweaveModule.LOOKUP.byMeta(module.meta()) == module && module.id()
                .equals(module.id),
            "celestweave module lookup " + module.id);
        for (RailgunModuleType type : RailgunModuleType.values())
            check(RailgunModuleType.LOOKUP.byMeta(type.meta()) == type, "railgun module lookup " + type.id);
        check(RailgunModuleType.LOOKUP.byMeta(-1) == null, "lookup rejects negative meta");
        check(RailgunModuleType.LOOKUP.byMeta(RailgunModuleType.LOOKUP.size()) == null, "lookup rejects meta past end");
        for (int tier = 1; tier <= EnergyModuleItem.TIERS; tier++) {
            EnergyModuleItem.Tier variant = EnergyModuleItem.Tier.of(tier);
            check(variant.meta() == tier - 1, "energy tier meta " + tier);
            check(
                variant.id()
                    .equals("t" + tier),
                "energy tier id keeps lang and texture names " + tier);
        }
        check(EnergyModuleItem.Tier.of(99) == EnergyModuleItem.Tier.T3, "energy tier clamps high");
        check(EnergyModuleItem.Tier.of(0) == EnergyModuleItem.Tier.T1, "energy tier clamps low");
        boolean duplicate = false;
        try {
            new MetaLookup<>(new IEquipmentVariant[] { variant("a", 0), variant("b", 0) });
        } catch (IllegalStateException expected) {
            duplicate = true;
        }
        check(duplicate, "lookup rejects duplicate meta");

        check(FxVisibility.allowed(true, 500, false, 10), "own effects ignore show-others");
        check(!FxVisibility.allowed(false, 1, false, 10), "others hidden when show-others is off");
        check(FxVisibility.allowed(false, 10, true, 10), "others shown at the distance limit");
        check(!FxVisibility.allowed(false, 10.5, true, 10), "others hidden beyond the distance limit");

        ByteBuf buffer = Unpooled.buffer();
        new EquipmentVec(1.5, -2.25, 1.0E7).write(buffer);
        EquipmentVec read = EquipmentVec.read(buffer);
        check(read.equals(new EquipmentVec(1.5, -2.25, 1.0E7)) && buffer.readableBytes() == 0, "vector round trip");
        ByteBuf broken = Unpooled.buffer();
        new EquipmentVec(Double.NaN, 0, 0).write(broken);
        boolean rejected = false;
        try {
            EquipmentVec.read(broken);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "vector read rejects non-finite values");
    }

    private static IEquipmentVariant variant(String id, int meta) {
        return new IEquipmentVariant() {

            @Override
            public String id() {
                return id;
            }

            @Override
            public int meta() {
                return meta;
            }
        };
    }

    private static void modules() {
        Set<String> ids = new HashSet<>();
        for (CelestweaveModule module : CelestweaveModule.values()) {
            check(ids.add(module.id), "duplicate module id " + module.id);
            check(CelestweaveModule.byId(module.id) == module, "byId round trip " + module.id);
            check(module.pieceMask > 0 && module.pieceMask <= CelestweaveModule.Mask.ANY, "piece mask " + module.id);
            boolean fitsAny = false;
            for (int type = 0; type < 4; type++) fitsAny |= module.fits(type);
            check(fitsAny, "module fits no piece " + module.id);
            check(!module.fits(-1) && !module.fits(4), "fits out of range " + module.id);
            if (module.hideable()) check(module.fits(0) || module.fits(1) || module.fits(2), "hideable " + module.id);
        }
        Set<CelestweaveModule> hideable = EnumSet.of(
            CelestweaveModule.REVEALING,
            CelestweaveModule.HOLO,
            CelestweaveModule.BACKPACK,
            CelestweaveModule.FLIGHT);
        Set<CelestweaveModule> holders = EnumSet
            .of(CelestweaveModule.REVEALING, CelestweaveModule.HOLO, CelestweaveModule.BACKPACK);
        Set<CelestweaveModule> behaving = EnumSet.of(
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
        for (CelestweaveFrames.Part part : CelestweaveFrames.Part.values()) check(
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
            check(CelestweaveStripEffects.registered(style), "strip style has an effect " + style.id);
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
                CelestweaveSettings.setCosmetic(chest, CelestweaveCosmeticOption.STRIP_STYLE, style.ordinal()),
                "strip effect " + style.id + " applies");
            check(
                CelestweaveData.cosmetic(chest)
                    .stripStyle() == style,
                "strip effect " + style.id + " is stored");
        }
        int[] gradient = CelestweaveStripEffects.gradientPixels();
        Set<Integer> hues = new HashSet<>();
        for (int rgb : gradient) hues.add(rgb & 0xFFFFFF);
        check(hues.size() > gradient.length / 2, "rainbow gradient spans many hues");
        int[] flow = CelestweaveStripEffects.flowPixels();
        int peak = flow[flow.length / 2] & 255, edge = flow[0] & 255;
        check(peak > 240 && edge < 90 && edge > 40, "flowing light has a bright crest over a dim base");
        near(0, CelestweaveStripEffects.breath(0F), "breathing starts dark");
        near(1, CelestweaveStripEffects.breath(1.8F), "breathing peaks mid-cycle");
        near(0, CelestweaveStripEffects.breath(3.4F), "breathing rests dark before the next breath");
        check(
            CelestweaveStripEffects.breath(0.8F) > 0.2F && CelestweaveStripEffects.breath(0.8F) < 0.8F,
            "breathing rises smoothly");
    }

    private static void settings() {
        Set<String> keys = new HashSet<>();
        for (CelestweaveModuleSetting setting : CelestweaveModuleSetting.values()) {
            check(keys.add(setting.module.id + "." + setting.key), "duplicate setting " + setting.key);
            check(setting.min <= setting.fallback && setting.fallback <= setting.max(), "fallback " + setting.key);
            if (setting.bool()) check(setting.min == 0 && setting.max() == 1, "bool range " + setting.key);
            check(setting.get(plainStack()) == setting.fallback, "unset setting reads fallback " + setting.key);
        }
        check(CelestweaveModuleSetting.REFLECT_IGNITE.on(plainStack()), "ignite defaults on");
        check(CelestweaveModuleSetting.REFLECT_BLIND.on(plainStack()), "blind defaults on");
        ItemStack stack = plainStack();
        check(
            !CelestweaveSettings.setValue(stack, CelestweaveModuleSetting.FLIGHT_SPEED, 3),
            "settings rejected when module not installed");
        check(!CelestweaveSettings.setCosmetic(stack, CelestweaveCosmeticOption.GLOW_COLOR, Float.NaN), "NaN rejected");
        check(CelestweaveSettings.setCosmetic(stack, CelestweaveCosmeticOption.IDLE_ARCS, 500), "cosmetic applied");
        check(
            CelestweaveData.cosmetic(stack)
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
                .equals(EnumSet.of(CelestweaveModule.REVEALING, CelestweaveModule.HOLO)),
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
        ((IMessage) packet).toBytes(buffer);
        T copy = empty.newInstance();
        ((IMessage) copy).fromBytes(buffer);
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
        near(-2.25, ((EquipmentVec) field(fx, "position")).y(), "fx y");
        near(1.0E6, ((EquipmentVec) field(fx, "position")).z(), "fx z keeps precision");
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
        RailgunChargeCapPacket cap = roundTrip(
            new RailgunChargeCapPacket(11, 37),
            RailgunChargeCapPacket.class.getConstructor());
        check((int) field(cap, "shooterId") == 11 && (int) field(cap, "units") == 37, "charge cap names its shooter");
    }

    private static void envelopes() throws ReflectiveOperationException {
        Constructor<CelestweaveClientState> constructor = CelestweaveClientState.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        CelestweaveClientState state = constructor.newInstance();
        check(state.hitGlow(1000) == 0F, "no hit glow before any hit");
        state.hitAt = 1000;
        check(state.hitGlow(999) == 0F && state.hitGlow(2000) == 0F, "hit glow window");
        near(1, state.hitGlow(1040), "hit glow peak");
        check(state.hitGlow(1100) > state.hitGlow(1300) && state.hitGlow(1300) > 0, "hit glow decays");
        state.hitPower = 1.8F;
        near(1.8, state.hitGlow(1040), "hit glow scales with power");
        near(0, FxEasing.easeOutCubic(0F), "ease start");
        near(1, FxEasing.easeOutCubic(1F), "ease end");
        near(1, FxEasing.easeOutCubic(5F), "ease clamps high");
        near(0, FxEasing.easeOutCubic(-5F), "ease clamps low");
        float previous = 0F;
        for (int i = 1; i <= 20; i++) {
            float value = FxEasing.easeOutCubic(i / 20F);
            check(value >= previous, "ease monotonic");
            previous = value;
        }
    }

    private static void railgunCharge() {
        int grace = RailgunDefaults.CHARGE_RESUME_GRACE_TICKS;
        RailgunChargeState state = new RailgunChargeState();
        state.update(100, true, true, () -> 2);
        state.update(101, true, true, () -> 2);
        check(state.live(), "charge starts while using");
        check(state.units(110) == RailgunData.units(10, 2), "charge grows at the rate read on start");
        state.update(103, false, true, () -> 9);
        check(state.live(), "short gap while still holding keeps the charge");
        state.update(105, true, true, () -> 9);
        check(state.units(110) == RailgunData.units(10, 2), "resumed charge keeps its start and rate");
        state.update(106, false, false, () -> 1);
        check(!state.live() && state.units(110) == 0, "letting go ends the charge");
        state.update(106 + grace + 1, false, false, () -> 1);
        check(state.idle(106 + grace + 1), "ended charge goes idle");

        RailgunChargeState waiting = new RailgunChargeState();
        waiting.update(0, true, true, () -> 1);
        waiting.update(grace + 1, false, true, () -> 1);
        check(!waiting.live(), "gap longer than the grace ends the charge");
        waiting.update(grace + 2, true, true, () -> 1);
        check(waiting.units(grace + 12) == RailgunData.units(10, 1), "charge after a long gap starts over");

        RailgunChargeState capped = new RailgunChargeState();
        capped.update(0, true, true, () -> 1);
        capped.cap(5, 3);
        check(capped.units(100) == 5 && capped.units(100, 0.5F) == 5F, "energy cap stops the orb growing");

        RailgunChargeState early = new RailgunChargeState();
        early.cap(4, 50);
        check(!early.idle(50), "cap that arrives first is kept");
        early.update(52, true, false, () -> 1);
        check(early.units(80) == 4, "cap that arrives before the charge is seen still applies");
        RailgunChargeState stale = new RailgunChargeState();
        stale.cap(4, 0);
        check(stale.idle(grace + 1), "old cap alone does not keep a state alive");
        stale.update(100, true, true, () -> 1);
        check(stale.units(120) == RailgunData.units(20, 1), "old cap does not limit a new charge");

        RailgunChargeState fired = new RailgunChargeState();
        fired.update(0, true, true, () -> 1);
        fired.endUntilReleased(0);
        check(!fired.live(), "another player's shot ends their charge");
        fired.update(1, true, true, () -> 1);
        check(!fired.live(), "lingering use flag after firing does not restart the charge");
        fired.update(2, false, false, () -> 1);
        check(fired.idle(2), "fired charge goes idle once the use flag drops");
        fired.update(3, true, true, () -> 1);
        check(fired.live() && fired.units(5) == RailgunData.units(2, 1), "next charge starts fresh");
        RailgunChargeState held = new RailgunChargeState();
        held.update(0, true, true, () -> 1);
        held.endUntilReleased(1);
        held.update(1 + grace, true, true, () -> 1);
        check(!held.live(), "use flag is held back for the grace after a shot");
        held.update(2 + grace, true, true, () -> 1);
        check(
            held.live() && held.units(12 + grace) == RailgunData.units(10, 1),
            "a charge started right after a shot shows up once the grace passes");
        RailgunChargeState own = new RailgunChargeState();
        own.update(0, true, true, () -> 1);
        own.end();
        check(!own.live(), "own shot ends the charge");
        own.update(1, true, true, () -> 1);
        check(own.live() && own.units(5) == RailgunData.units(4, 1), "own new charge is not held back");

        RailgunChargeState slow = new RailgunChargeState();
        slow.update(0, true, false, () -> 0.2);
        check(slow.units(10) == RailgunData.units(10, 1), "charge rate never drops below 1");
    }

    private static void railgunBolts() {
        check(RailgunBolts.orbArcs(0F) == 0 && RailgunBolts.orbArcs(0.04F) == 0, "no orb arcs before charging");
        check(RailgunBolts.orbArcs(RailgunBolts.MIN_INTENSITY) == 1, "orb starts with one surface arc");
        check(RailgunBolts.orbArcs(1F) == RailgunBolts.ORB_MAX_ARCS, "full charge shows every surface arc");
        check(
            RailgunBolts.dischargeSlots(RailgunBolts.DISCHARGE_START) == 0
                && RailgunBolts.dischargeChance(RailgunBolts.DISCHARGE_START) == 0,
            "no discharges before half charge");
        check(RailgunBolts.dischargeSlots(1F) == RailgunBolts.ORB_MAX_DISCHARGES, "full charge allows a discharge");
        check(
            RailgunBolts.orb(1, 0, Float.NaN)
                .isEmpty(),
            "unreadable charge draws no lightning");
        int previousArcs = 0, previousSlots = 0;
        double previousChance = 0;
        for (int step = 0; step <= 20; step++) {
            float charge = step / 20F;
            int arcs = RailgunBolts.orbArcs(charge), slots = RailgunBolts.dischargeSlots(charge);
            double chance = RailgunBolts.dischargeChance(charge);
            check(
                arcs >= previousArcs && slots >= previousSlots && chance >= previousChance,
                "orb lightning never thins out as charge rises " + step);
            previousArcs = arcs;
            previousSlots = slots;
            previousChance = chance;
        }
        check(
            RailgunBolts.orb(7, 100, 0.6F)
                .equals(RailgunBolts.orb(7, 100, 0.6F)),
            "orb lightning is repeatable");
        check(
            !RailgunBolts.orb(7, 100, 0.6F)
                .equals(RailgunBolts.orb(7, 103, 0.6F)),
            "orb lightning flickers");
        check(
            !RailgunBolts.orb(7, 100, 0.6F)
                .equals(RailgunBolts.orb(8, 100, 0.6F)),
            "each shooter has its own bolts");
        EquipmentVec origin = new EquipmentVec(0, 0, 0);
        int fullArcs = RailgunBolts.orbArcs(1F), discharges = 0, ticks = 300;
        double farthest = 0;
        for (long tick = 0; tick < ticks; tick++) {
            check(
                RailgunBolts.orb(3, tick, 0.4F)
                    .size() == RailgunBolts.orbArcs(0.4F),
                "only surface arcs below half charge");
            List<RailgunBolts.Bolt> bolts = RailgunBolts.orb(3, tick, 1F);
            check(bolts.size() <= fullArcs + RailgunBolts.ORB_MAX_DISCHARGES, "orb lightning stays sparse");
            for (int index = 0; index < bolts.size(); index++) {
                List<EquipmentVec> points = bolts.get(index)
                    .points();
                if (index >= fullArcs) {
                    discharges++;
                    check(
                        Math.abs(
                            Math.sqrt(
                                points.get(0)
                                    .distanceToSqr(origin))
                                - 1)
                            < 1.0E-9,
                        "discharge leaves from the orb surface");
                    continue;
                }
                for (EquipmentVec point : points) {
                    double distance = Math.sqrt(point.distanceToSqr(origin));
                    if (distance < RailgunBolts.ARC_LOW - 1.0E-9 || distance > RailgunBolts.ARC_HIGH + 1.0E-9) {
                        check(false, "surface arcs hug the orb");
                        break;
                    }
                }
            }
            farthest = Math.max(farthest, reach(bolts, origin));
        }
        check(
            discharges > ticks / 10 && discharges < ticks * RailgunBolts.ORB_MAX_DISCHARGES,
            "discharges come and go");
        check(
            farthest <= (1 + RailgunBolts.DISCHARGE_MIN_REACH + RailgunBolts.DISCHARGE_REACH_GAIN) * 1.3,
            "discharges stay near the orb");

        check(
            RailgunBolts.body(1, 0, 0.04F)
                .isEmpty(),
            "no gun lightning before charging");
        check(RailgunBolts.flowsPerSide(1F) == RailgunBolts.FLOW_MAX_PER_SIDE, "full charge fills the groove current");
        check(RailgunBolts.sparkChance(RailgunBolts.SPARK_START) == 0, "no sparks before the spark threshold");
        check(RailgunBolts.hopChance(RailgunBolts.HOP_START) == 0, "no hops before the hop threshold");
        check(RailgunBolts.flowPeriod(1F) < RailgunBolts.flowPeriod(0.1F), "current flows faster as charge rises");
        check(
            RailgunBolts.flowSpan(1F) <= RailgunBolts.GROOVE_CENTER - RailgunBolts.GROOVE_FRONT + 1.0E-9
                && RailgunBolts.flowSpan(1F) <= RailgunBolts.GROOVE_BACK - RailgunBolts.GROOVE_CENTER + 1.0E-9,
            "current stays within the groove");
        int flows = 0, sparks = 0, hops = 0, forward = 0, backward = 0, across = 0, liftUp = 0, liftDown = 0;
        int anchorsFront = 0, anchorsBack = 0;
        boolean moves = false, earlyQuiet = true;
        for (int frame = 0; frame < 400; frame++) {
            double time = 1000 + frame * 0.5;
            List<RailgunBolts.Bolt> bolts = RailgunBolts.body(5, time, 1F);
            check(
                bolts.size() <= 2 + RailgunBolts.ANCHORS_PER_SIDE * 2
                    + RailgunBolts.FLOW_MAX_PER_SIDE * 2
                    + 2
                    + RailgunBolts.HOP_SLOTS,
                "gun lightning stays sparse");
            moves |= !bolts.equals(RailgunBolts.body(5, time + 0.25, 1F));
            for (int index = 0; index < 2; index++) {
                List<EquipmentVec> points = bolts.get(index)
                    .points();
                check(
                    Math.abs(
                        points.get(0)
                            .z() - RailgunBolts.GROOVE_CENTER)
                        < 1.0E-9
                        && points.get(points.size() - 1)
                            .equals(RailgunBolts.MUZZLE)
                        && inGroove(points.subList(0, points.size() - 1)),
                    "tether runs from the groove centre to the muzzle");
            }
            int fixed = 2 + RailgunBolts.ANCHORS_PER_SIDE * 2;
            for (int index = 2; index < fixed; index++) {
                List<EquipmentVec> points = bolts.get(index)
                    .points();
                double reach = points.get(points.size() - 1)
                    .z() - RailgunBolts.GROOVE_CENTER;
                check(
                    inGroove(points) && Math.abs(
                        points.get(0)
                            .z() - RailgunBolts.GROOVE_CENTER)
                        < 1.0E-9
                        && Math.abs(reach) <= RailgunBolts.ANCHOR_MIN_REACH + RailgunBolts.ANCHOR_REACH_GAIN + 1.0E-9,
                    "fixed arcs reach out from the groove centre");
                if (reach > 0) anchorsBack++;
                else anchorsFront++;
            }
            for (int index = fixed; index < bolts.size(); index++) {
                RailgunBolts.Bolt bolt = bolts.get(index);
                List<EquipmentVec> points = bolt.points();
                EquipmentVec first = points.get(0), last = points.get(points.size() - 1);
                check(bolt.width() <= 0.0075, "gun lightning is thin");
                if (inGroove(points)) {
                    flows++;
                    check(
                        Math.abs(last.z() - first.z())
                            <= RailgunBolts.FLOW_LENGTH + RailgunBolts.FLOW_LENGTH_GAIN + 1.0E-9,
                        "groove current is short");
                } else if (Math.abs(Math.abs(first.x()) - RailgunBolts.GROOVE_OUTER) < 1.0E-9) {
                    sparks++;
                    check(Math.abs(last.x()) > RailgunBolts.GROOVE_OUTER, "sparks leap outward from the groove");
                } else {
                    hops++;
                    check(onEdge(first) && onEdge(last), "hops jump between points on the gun");
                    if (last.z() > first.z()) forward++;
                    else if (last.z() < first.z()) backward++;
                    if (Math.abs(last.x() - first.x()) > 1.0E-9 || Math.abs(last.y() - first.y()) > 1.0E-9) across++;
                    double bend = points.get(points.size() / 2)
                        .y() - (first.y() + last.y()) / 2;
                    if (bend > 0) liftUp++;
                    else liftDown++;
                }
            }
            List<RailgunBolts.Bolt> early = RailgunBolts.body(5, time, 0.15F);
            for (int index = 2 + RailgunBolts.ANCHORS_PER_SIDE * 2; index < early.size(); index++)
                earlyQuiet &= inGroove(
                    early.get(index)
                        .points());
        }
        check(flows > 0 && sparks > 0 && hops > 0, "full charge shows current, sparks and hops");
        check(forward > 0 && backward > 0, "hops jump both ways along the gun");
        check(anchorsFront > 0 && anchorsBack > 0, "fixed arcs reach toward both ends of the groove");
        check(across > 0, "some hops jump across to another edge");
        check(liftUp > 0 && liftDown > 0, "hops arch in different directions");
        check(moves, "groove current moves smoothly between ticks");
        check(earlyQuiet, "early charge only shows the tether and the groove current");
        int matched = 0, lost = 0;
        for (int frame = 0; frame < 1200; frame++) {
            double age = 2 + frame * 0.05, next = age + 0.05;
            float charge = (float) Math.min(1, age / 60), nextCharge = (float) Math.min(1, next / 60);
            List<double[]> before = currents(RailgunBolts.body(5, age, charge));
            List<double[]> after = currents(RailgunBolts.body(5, next, nextCharge));
            for (double[] current : before) {
                boolean found = false;
                for (double[] later : after)
                    found |= Math.abs(current[0] - later[0]) < 0.03 && Math.abs(current[1] - later[1]) < 0.03;
                if (found) matched++;
                else if (Math.abs(current[1] - RailgunBolts.GROOVE_CENTER) < RailgunBolts.flowSpan(charge) - 0.03)
                    lost++;
            }
        }
        check(matched > 0 && lost == 0, "groove current flows smoothly while the charge rises");
        EquipmentVec legFrom = new EquipmentVec(1, 2, 3), legTo = legFrom.add(0, 0, 0.5);
        List<RailgunBolts.Bolt> legs = RailgunBolts.tethers(9, 40, 0.5F, legFrom, legTo);
        check(legs.size() == 2, "two tethers bind the orb");
        for (RailgunBolts.Bolt leg : legs) check(
            leg.points()
                .get(0)
                .equals(legFrom)
                && Math.sqrt(
                    leg.points()
                        .get(
                            leg.points()
                                .size() - 1)
                        .distanceToSqr(legTo))
                    < 0.25,
            "tether runs from the muzzle into the orb");
        check(
            RailgunBolts.tethers(9, 40, 0.01F, legFrom, legTo)
                .isEmpty()
                && RailgunBolts.tethers(9, 40, 1F, null, legTo)
                    .isEmpty(),
            "no tether before charging or without a muzzle");

        float[] color = new float[3];
        RailgunOrbRenderer.color(RailgunChargeTier.EHV2.ordinal(), 0F, color);
        check(color[2] > color[0], "lightning is blue before full charge");
        RailgunOrbRenderer.color(RailgunChargeTier.EHV3.ordinal(), 0F, color);
        check(color[0] > color[2], "lightning turns pink at full charge");
        RailgunOrbRenderer.color(RailgunChargeTier.EHV3.ordinal(), 1F, color);
        check(color[0] > 0.9F && color[1] > 0.9F && color[2] > 0.9F, "lightning core is near white");
    }

    private static List<double[]> currents(List<RailgunBolts.Bolt> bolts) {
        List<double[]> currents = new ArrayList<>();
        for (int index = 2 + RailgunBolts.ANCHORS_PER_SIDE * 2; index < bolts.size(); index++) {
            List<EquipmentVec> points = bolts.get(index)
                .points();
            if (inGroove(points)) currents.add(
                new double[] { points.get(0)
                    .z(),
                    points.get(points.size() - 1)
                        .z() });
        }
        return currents;
    }

    private static boolean inGroove(List<EquipmentVec> points) {
        for (EquipmentVec point : points) {
            if (point.z() < RailgunBolts.GROOVE_FRONT - 1.0E-9 || point.z() > RailgunBolts.GROOVE_BACK + 1.0E-9
                || point.y() < RailgunBolts.GROOVE_LOW - 1.0E-9
                || point.y() > RailgunBolts.GROOVE_HIGH + 1.0E-9
                || Math.abs(point.x()) < RailgunBolts.GROOVE_INNER - 1.0E-9
                || Math.abs(point.x()) > RailgunBolts.GROOVE_OUTER + 1.0E-9) return false;
        }
        return true;
    }

    private static boolean onEdge(EquipmentVec point) {
        for (RailgunBolts.Edge edge : RailgunBolts.HOP_EDGES) {
            for (int side = -1; side <= 1; side += 2) {
                EquipmentVec from = edge.at(0, side), to = edge.at(1, side);
                EquipmentVec along = to.subtract(from);
                double t = point.subtract(from)
                    .dot(along) / along.dot(along);
                if (t < -1.0E-9 || t > 1 + 1.0E-9) continue;
                if (Math.sqrt(point.distanceToSqr(from.add(along.scale(t)))) < 1.0E-9) return true;
            }
        }
        return false;
    }

    private static double reach(List<RailgunBolts.Bolt> bolts, EquipmentVec origin) {
        double farthest = 0;
        for (RailgunBolts.Bolt bolt : bolts) for (EquipmentVec point : bolt.points())
            farthest = Math.max(farthest, Math.sqrt(point.distanceToSqr(origin)));
        return farthest;
    }

    private static void railgunOrbPulse() {
        near(0, RailgunOrbRenderer.tension(RailgunOrbRenderer.CALM_UNTIL), "orb stays calm early in the charge");
        near(0, RailgunOrbRenderer.tension(Float.NaN), "unreadable charge keeps the orb calm");
        near(1, RailgunOrbRenderer.tension(1F), "orb is at full tension when fully charged");
        float previous = 0F;
        for (int step = 0; step <= 40; step++) {
            float tension = RailgunOrbRenderer.tension(step / 40F);
            check(tension >= previous, "orb tension never drops as charge rises " + step);
            previous = tension;
        }
        double calmSwing = 0, fullSwing = 0, largestJump = 0;
        for (int sample = 0; sample < 400; sample++) {
            double time = 1_000_000 + sample * 0.37;
            calmSwing = Math.max(calmSwing, Math.abs(RailgunOrbRenderer.pulse(0F, time) - 1));
            fullSwing = Math.max(fullSwing, Math.abs(RailgunOrbRenderer.pulse(1F, time) - 1));
            for (int step = 0; step < 100; step++) largestJump = Math.max(
                largestJump,
                Math.abs(
                    RailgunOrbRenderer.pulse((step + 1) / 100F, time) - RailgunOrbRenderer.pulse(step / 100F, time)));
        }
        check(calmSwing <= RailgunOrbRenderer.BREATH_DEPTH + 1.0E-9, "calm orb only breathes gently");
        check(fullSwing > calmSwing * 2, "charged orb throbs harder than a calm one");
        check(largestJump < 0.01, "orb size never jumps as charge rises");
        near(1, RailgunOrbRenderer.tremble(0F, 4, 123), "calm orb does not tremble");
        near(0, RailgunOrbRenderer.shake(0F, 4, 123, 0), "calm orb does not shake");
        boolean trembles = false;
        for (long tick = 0; tick < 40; tick++) {
            double tremble = RailgunOrbRenderer.tremble(1F, 4, tick);
            check(Math.abs(tremble - 1) <= RailgunOrbRenderer.TREMBLE_DEPTH + 1.0E-9, "tremble stays bounded");
            check(
                Math.abs(RailgunOrbRenderer.shake(1F, 4, tick, 1)) <= RailgunOrbRenderer.SHAKE_DEPTH + 1.0E-9,
                "shake stays bounded");
            trembles |= Math.abs(tremble - 1) > 0.01;
        }
        check(trembles, "fully charged orb trembles");
        near(0, RailgunOrbRenderer.flare(RailgunOrbRenderer.FLARE_FROM, 0), "no flare before the critical point");
        near(1, RailgunOrbRenderer.flare(1F, RailgunOrbRenderer.FLARE_PERIOD * 7), "full charge flares at each beat");
        check(
            RailgunOrbRenderer.flare(1F, RailgunOrbRenderer.FLARE_PERIOD * 7 + RailgunOrbRenderer.FLARE_PERIOD / 2.0)
                < 0.2F,
            "flare fades between beats");
    }

    private static void wheel() throws Exception {
        double quarter = Math.PI / 2;
        near(0, FxAngle.angleOf(0, -1), "wheel angle up");
        near(quarter, FxAngle.angleOf(1, 0), "wheel angle right");
        near(Math.PI, FxAngle.angleOf(0, 1), "wheel angle down");
        near(3 * quarter, FxAngle.angleOf(-1, 0), "wheel angle left");
        int[] expected = { 0, 1, 2, 3 };
        for (int sector = 0; sector < 4; sector++) {
            float angle = WheelGeometry.slotAngle(sector, 4);
            check(WheelGeometry.slotAt(angle, 4) == expected[sector], "wheel section centre " + sector);
            near(
                angle,
                FxAngle.angleOf(FxAngle.dirX(angle), FxAngle.dirY(angle)),
                "wheel direction round trip " + sector);
        }
        check(WheelGeometry.slotAt(FxAngle.TAU - 0.01F, 4) == 0, "wheel section wraps past top");
        check(WheelGeometry.slotAt((float) Math.PI / 4 - 0.01F, 4) == 0, "wheel section before boundary");
        check(WheelGeometry.slotAt((float) Math.PI / 4 + 0.01F, 4) == 1, "wheel section after boundary");
        check(WheelGeometry.slotAt(1, 0) == -1, "wheel section empty");
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

    private static void hudLayout() {
        HudElement coreElement = CelestweaveHudElement.CORE.hud;
        HudLayout.Entry fresh = new HudLayout.Entry();
        var core = HudLayout.placement(coreElement, fresh, 480, 270, 1F);
        near(240 - 97 - 8, core.x(), "core keeps its place left of the hotbar");
        near(270 - 3 - 8, core.y(), "core keeps its place above the bottom edge");
        near(
            480 - 97 - 16,
            HudLayout.placement(coreElement, fresh, 960, 540, 2F)
                .x(),
            "core default follows the base scale");
        near(
            -coreElement.minX * 2,
            HudLayout.placement(coreElement, fresh, 480, 270, 2F)
                .x(),
            "oversized core readout kept on screen");
        var ring = HudLayout.placement(CelestweaveHudElement.SHIELD_RING.hud, fresh, 480, 270, 1F);
        near(240, ring.x(), "shield ring centred horizontally");
        near(135, ring.y(), "shield ring centred vertically");
        near(
            240 + 62,
            HudLayout.placement(CelestweaveHudElement.ALTIMETER.hud, fresh, 480, 270, 1F)
                .x(),
            "altimeter right of the crosshair");
        boolean[] reserved = { true };
        HudRegistry.reserveCrosshair(() -> reserved[0]);
        near(
            240 + 26 + 6.5,
            HudLayout.placement(RailgunHudRenderer.CHARGE, fresh, 480, 270, 1F)
                .x(),
            "lance charge clears the armor HUD");
        reserved[0] = false;
        near(
            240 + 10 + 6.5,
            HudLayout.placement(RailgunHudRenderer.CHARGE, fresh, 480, 270, 1F)
                .x(),
            "lance charge hugs the crosshair without armor");

        near(0, HudLayout.anchor(10, 300), "left third anchors left");
        near(0.5, HudLayout.anchor(150, 300), "middle third anchors centre");
        near(1, HudLayout.anchor(290, 300), "right third anchors right");

        HudLayout.Entry entry = new HudLayout.Entry();
        HudLayout.move(entry, 460, 250, 480, 270);
        var moved = HudLayout.placement(coreElement, entry, 480, 270, 1F);
        near(460, moved.x(), "dragged x round trip");
        near(250, moved.y(), "dragged y round trip");
        var wider = HudLayout.placement(coreElement, entry, 960, 540, 1F);
        near(940, wider.x(), "corner element keeps its distance to the right edge");
        near(520, wider.y(), "corner element keeps its distance to the bottom edge");
        HudLayout.move(entry, 5000, -5000, 480, 270);
        var clamped = HudLayout.placement(coreElement, entry, 480, 270, 1F);
        near(480 - coreElement.maxX, clamped.x(), "element kept inside the right edge");
        near(-coreElement.minY, clamped.y(), "element kept inside the top edge");

        near(HudLayout.MIN_SCALE, HudLayout.clampScale(0.1F), "scale floor");
        near(HudLayout.MAX_SCALE, HudLayout.clampScale(9F), "scale ceiling");
        near(1, HudLayout.clampScale(Float.NaN), "scale NaN falls back");

        HudElement altimeterElement = CelestweaveHudElement.ALTIMETER.hud;
        HudLayout.reset();
        HudLayout.move(altimeterElement, 100, 50, 480, 270);
        HudLayout.rescale(altimeterElement, 3);
        HudLayout.toggle(CelestweaveHudElement.DASH_ARC.hud);
        Properties saved = new Properties();
        HudLayout.write(saved);
        saved.setProperty("future_element.scale", "2.0");
        HudLayout.read(saved);
        var altimeter = HudLayout.entry(altimeterElement);
        check(altimeter.moved, "moved flag survives saving");
        near(1.3, altimeter.scale, "element scale survives saving");
        near(
            100,
            HudLayout.placement(altimeterElement, altimeter, 480, 270, 1F)
                .x(),
            "position survives saving");
        check(HudLayout.hidden(CelestweaveHudElement.DASH_ARC.hud), "hidden flag survives saving");
        check(!HudLayout.hidden(coreElement), "untouched element stays visible");
        Properties resaved = new Properties();
        HudLayout.write(resaved);
        check(
            "2.0".equals(resaved.getProperty("future_element.scale")),
            "layout of an element that is not registered yet survives saving");

        Properties broken = new Properties();
        broken.setProperty("core.moved", "true");
        broken.setProperty("core.x", "abc");
        broken.setProperty("core.y", "5");
        broken.setProperty("core.anchorX", "0.3");
        broken.setProperty("core.scale", "99");
        HudLayout.read(broken);
        var repaired = HudLayout.entry(coreElement);
        check(!repaired.moved, "unreadable position falls back to the default place");
        near(0.5, repaired.anchorX, "unknown anchor falls back to centre");
        near(HudLayout.MAX_SCALE, repaired.scale, "oversized scale clamped");
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
            "item.gtnl.celestweave.hud.element.railgun_charge".equals(RailgunHudRenderer.CHARGE.lang()),
            "lance charge keeps its lang key");
        check(RailgunHudRenderer.CHARGE.hasRenderer(), "lance charge is drawn by the shared HUD overlay");
        check(!CelestweaveHudElement.CORE.hud.hasRenderer(), "armor HUD draws itself");

        HudElement plain = HudElement.builder("contract_plain")
            .bounds(-4, -4, 4, 4)
            .build();
        check((HudElement.DEFAULT_LANG_PREFIX + "contract_plain").equals(plain.lang()), "default HUD lang key");
        near(0.5, plain.anchorX, "default HUD anchor x");
        near(0.5, plain.anchorY, "default HUD anchor y");
        check(plain.isVisible(), "HUD element visible by default");
        near(1, plain.baseScale(), "HUD base scale defaults to 1");
        check(!plain.hasRenderer(), "HUD element without renderer draws itself");
        near(
            240,
            HudLayout.placement(plain, new HudLayout.Entry(), 480, 270, plain.baseScale())
                .x(),
            "default HUD element sits at the screen centre");
        check(
            rejects(
                () -> HudElement.builder("contract_flat")
                    .build()),
            "HUD element without bounds rejected");
        check(rejects(() -> HudElement.builder("")), "empty HUD id rejected");

        boolean[] shown = { false };
        double[] scale = { Double.NaN };
        HudElement toggled = HudElement.builder("contract_toggled")
            .bounds(-4, -4, 4, 4)
            .visibleWhen(() -> shown[0])
            .baseScale(() -> scale[0])
            .render((place, partialTicks) -> {})
            .build();
        HudLayout.reset();
        check(HudLayout.place(toggled, 480, 270) == null, "hidden by its own switch");
        shown[0] = true;
        near(
            1,
            HudLayout.place(toggled, 480, 270)
                .scale(),
            "invalid base scale falls back to 1");
        scale[0] = 2;
        near(
            2,
            HudLayout.place(toggled, 480, 270)
                .scale(),
            "base scale comes from the element");
        HudLayout.toggle(toggled);
        check(HudLayout.place(toggled, 480, 270) == null, "editor hide still applies");
        HudLayout.reset();

        HudRegistry.register(toggled);
        check(HudRegistry.byId("contract_toggled") == toggled, "registered HUD element found by id");
        check(rejects(() -> HudRegistry.register(toggled)), "duplicate HUD id rejected");
    }

    private static boolean rejects(Runnable action) {
        try {
            action.run();
            return false;
        } catch (RuntimeException expected) {
            return true;
        }
    }

    private static void hud() {
        near(1, CelestweaveHud.fade(-100), "hud awake before idle");
        near(1, CelestweaveHud.fade(0), "hud awake at idle edge");
        near(0.5, CelestweaveHud.fade(250), "hud fades halfway");
        near(0, CelestweaveHud.fade(10000), "hud fully faded");
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
        check(">99h".equals(CelestweaveHud.duration(400000)), "capped duration");
        check(
            CelestweaveHud.duration(Double.POSITIVE_INFINITY)
                .isEmpty(),
            "no duration when not draining");
    }

    private static int alpha(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >>> 24;
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
        for (int piece = 0; piece < 4; piece++) expected.add("item.gtnl.celestweave.radial.piece." + piece);
        for (CelestweaveHudElement element : CelestweaveHudElement.values()) expected.add(element.hud.lang());
        expected.add(RailgunHudRenderer.CHARGE.lang());
        for (String key : new String[] { "help", "reset", "done", "hidden" })
            expected.add("item.gtnl.celestweave.hud.editor." + key);
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
