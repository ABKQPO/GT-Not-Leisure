package com.science.gtnl.client.celestweave;

import static com.science.gtnl.client.celestweave.CelestweaveContractTest.RESOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.SOURCES;
import static com.science.gtnl.client.celestweave.CelestweaveContractTest.roundTrip;
import static com.science.gtnllib.GTNLLibContractTest.call;
import static com.science.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnllib.GTNLLibContractTest.compact;
import static com.science.gtnllib.GTNLLibContractTest.field;
import static com.science.gtnllib.GTNLLibContractTest.near;
import static com.science.gtnllib.GTNLLibContractTest.read;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.science.gtnl.client.celestweave.CelestweaveClientEvents.HitOrigin;
import com.science.gtnl.client.equipment.EquipmentWheelScreen;
import com.science.gtnl.client.fx.world.FxVisibility;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.celestweave.CelestweaveBackpackData;
import com.science.gtnl.common.celestweave.CelestweaveBehaviors;
import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveModuleSetting;
import com.science.gtnl.common.celestweave.CelestweaveStripStyle;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.equipment.EquipmentModules;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.equipment.IEquipmentVariant;
import com.science.gtnl.common.equipment.MetaLookup;
import com.science.gtnl.common.gui.equipment.CelestweaveGui;
import com.science.gtnl.common.gui.equipment.EquipmentGui;
import com.science.gtnl.common.gui.equipment.RailgunGui;
import com.science.gtnl.common.item.items.PortableItem;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.item.items.equipment.CelestweaveModuleItem;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;
import com.science.gtnl.common.item.items.equipment.RailgunModuleItem;
import com.science.gtnl.common.packet.CelestweaveFlightPacket;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.common.packet.CelestweaveKeyPacket;
import com.science.gtnl.common.packet.OpenEquipmentGuiPacket;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunEnergy;
import com.science.gtnl.common.railgun.RailgunModule;
import com.science.gtnl.common.railgun.RailgunSetting;
import com.science.gtnl.config.MainConfig;
import com.science.gtnllib.client.fx.FxEasing;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CelestweaveArmorContracts {

    private static final int DEFAULT_STACK = 64;

    private CelestweaveArmorContracts() {}

    static void run() throws Exception {
        modules();
        equipment();
        cosmetic();
        settings();
        energy();
        backpack();
        backpackStorage();
        caches();
        moduleSlots();
        packets();
        envelopes();
        hitDirection();
    }

    private static void modules() {
        Set<String> ids = new HashSet<>();
        for (CelestweaveModule module : CelestweaveModule.values()) {
            check(ids.add(module.id), "duplicate module id " + module.id);
            check(CelestweaveModule.LOOKUP.byMeta(module.meta) == module, "lookup round trip " + module.id);
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
        check(
            CelestweaveModule.LOOKUP.size() == metaOrder.length,
            "new modules take a new meta after the existing ones");
        Set<Integer> metas = new HashSet<>();
        for (CelestweaveModule module : CelestweaveModule.values())
            check(metas.add(module.meta), "duplicate module meta " + module.id);
        check(EnergyModuleItem.tier(plainStack()) == 0, "plain item is not an energy module");
        check(CelestweaveModule.of(plainStack()) == null, "plain item is not a module");
    }

    private static void equipment() {
        for (CelestweaveModule module : CelestweaveModule.values()) check(
            CelestweaveModule.LOOKUP.byMeta(module.meta()) == module && module.id()
                .equals(module.id),
            "celestweave module lookup " + module.id);
        for (RailgunModule type : RailgunModule.values())
            check(RailgunModule.LOOKUP.byMeta(type.meta()) == type, "railgun module lookup " + type.id);
        check(RailgunModule.LOOKUP.byMeta(-1) == null, "lookup rejects negative meta");
        check(RailgunModule.LOOKUP.byMeta(RailgunModule.LOOKUP.size()) == null, "lookup rejects meta past end");
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

    private static void cosmetic() throws IOException {
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
        String strips = compact(read(SOURCES.resolve("client/celestweave/CelestweaveStripEffects.java")));
        check(
            strips.split("pose\\.brightness", -1).length - 1 == 1 && strips.contains(
                "classFlickerimplementsICelestweaveStripEffect{@Overridepublicbooleanbegin(CelestweavePosepose,CelestweaveCosmeticcosmetic,floatseconds,floatboneY){color(pose.glowR,pose.glowG,pose.glowB,pose.brightness);")
                && strips.contains(
                    "classSteadyimplementsICelestweaveStripEffect{@Overridepublicbooleanbegin(CelestweavePosepose,CelestweaveCosmeticcosmetic,floatseconds,floatboneY){color(pose.glowR,pose.glowG,pose.glowB,pose.steadyBrightness);"),
            "only the flicker strip uses the shimmering brightness, steady and every other effect stay flicker-free");
        String animator = compact(read(SOURCES.resolve("client/celestweave/CelestweaveAnimator.java")));
        check(
            animator.contains("pose.steadyBrightness=steadyBrightness*(1F+1.1F*glow);") && animator
                .contains("floatsteadyBrightness=tier==0?0.3F:0.4F+0.6F*fraction,baseBrightness=steadyBrightness;"),
            "the steady brightness leaves out both the slow shimmer and the low-energy flicker");
        check(
            !CelestweaveStripStyle.FLICKER.usesSpeed && !CelestweaveStripStyle.STEADY.usesSpeed
                && CelestweaveStripStyle.FLICKER.ordinal() == 0
                && CelestweaveCosmetic.DEFAULT.stripStyle() == CelestweaveStripStyle.FLICKER,
            "the old steady look is kept as the flicker style, listed first and used by default");
        check(
            !CelestweaveCosmeticOption.STRIP_SPEED.appliesTo(CelestweaveCosmetic.DEFAULT),
            "strip speed does not apply to the default flicker strips");
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
        check(clamped.stripStyle() == CelestweaveStripStyle.FLICKER, "strip effect defaults to flicker");
        wild.setInteger("StripFx", 99);
        check(
            CelestweaveCosmetic.read(wild)
                .stripStyle() == CelestweaveStripStyle.FLICKER,
            "numeric strip effect past the styles falls back to flicker");
        wild.setInteger("StripFx", CelestweaveStripStyle.BREATH.ordinal());
        check(
            CelestweaveCosmetic.read(wild)
                .stripStyle() == CelestweaveStripStyle.FLICKER,
            "numeric strip effect is not read as a style index");
        wild.setString("StripFx", CelestweaveStripStyle.BREATH.id);
        check(
            CelestweaveCosmetic.read(wild)
                .stripStyle() == CelestweaveStripStyle.BREATH,
            "strip effect reads by id");
        wild.setString("StripFx", "nope");
        check(
            CelestweaveCosmetic.read(wild)
                .stripStyle() == CelestweaveStripStyle.FLICKER,
            "unknown strip effect id falls back to flicker");
        ItemStack chest = plainStack();
        for (CelestweaveStripStyle style : CelestweaveStripStyle.values()) {
            check(
                CelestweaveData.setCosmetic(chest, CelestweaveCosmeticOption.STRIP_STYLE, style.ordinal()),
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
            !CelestweaveData.setValue(stack, CelestweaveModuleSetting.FLIGHT_SPEED, 3),
            "settings rejected when module not installed");
        check(!CelestweaveData.setCosmetic(stack, CelestweaveCosmeticOption.GLOW_COLOR, Float.NaN), "NaN rejected");
        check(CelestweaveData.setCosmetic(stack, CelestweaveCosmeticOption.IDLE_ARCS, 500), "cosmetic applied");
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
        Item moduleItem = bare(CelestweaveModuleItem.class);
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

    private static void backpackStorage() throws ReflectiveOperationException {
        CelestweaveModuleItem moduleItem = registered(
            bare(CelestweaveModuleItem.class),
            "gtnl:contract_backpack_module",
            31105);
        PortableItem portable = registered(bare(PortableItem.class), "gtnl:contract_portable", 31106);
        check(
            "gtnl_celestweave_backpack".equals(CelestweaveBackpackData.NAME),
            "backpack save data carries the armor prefix");
        CelestweaveBackpackData data = new CelestweaveBackpackData(CelestweaveBackpackData.NAME);
        ItemStack first = backpackModule(moduleItem, portable, "shared", "first");
        ItemStack[] armorA = CelestweaveBackpack.persist(new ItemStack[0], new ItemStack[] { first }, data);
        check("first".equals(backpackMarker(data.read("shared"))), "inserting stores the contents");
        check(backpackMarker(heldTag(armorA[0])) == null, "the armor keeps only the bare chest");
        check("shared".equals(CelestweaveBackpack.heldId(armorA[0])), "a new backpack keeps its id");
        ItemStack[] reopened = { CelestweaveBackpack.restore(armorA[0], data) };
        check("first".equals(backpackMarker(heldTag(reopened[0]))), "opening the GUI restores the contents");
        armorA = CelestweaveBackpack.persist(armorA, reopened, data);
        check("first".equals(backpackMarker(data.read("shared"))), "saving again keeps the contents");
        ItemStack copy = backpackModule(moduleItem, portable, "shared", "copy");
        ItemStack[] armorB = CelestweaveBackpack.persist(new ItemStack[0], new ItemStack[] { copy }, data);
        String renewed = CelestweaveBackpack.heldId(armorB[0]);
        check(!renewed.isEmpty() && !"shared".equals(renewed), "a backpack with a taken id gets a new id");
        check("first".equals(backpackMarker(data.read("shared"))), "the taken id keeps its own contents");
        check("copy".equals(backpackMarker(data.read(renewed))), "the renamed backpack keeps its contents");
        CelestweaveBackpack.persist(armorB, new ItemStack[] { null }, data);
        check(data.read(renewed) == null, "taking a backpack out drops its stored contents");
        check("first".equals(backpackMarker(data.read("shared"))), "taking one out leaves the other backpack");
        ItemStack takenOut = CelestweaveBackpack.restore(armorA[0], data);
        CelestweaveBackpack.persist(armorA, new ItemStack[] { null }, data);
        check(data.read("shared") == null, "the last backpack out clears the id");
        check("first".equals(backpackMarker(heldTag(takenOut))), "the taken out module carries its contents");
        ItemStack loose = backpackModule(moduleItem, portable, "loose", "kept");
        ItemStack[] unsaved = CelestweaveBackpack.persist(new ItemStack[0], new ItemStack[] { loose }, null);
        check("kept".equals(backpackMarker(heldTag(unsaved[0]))), "without save data the contents stay in the item");
    }

    private static ItemStack backpackModule(CelestweaveModuleItem moduleItem, PortableItem portable, String id,
        String marker) {
        ItemStack chest = new ItemStack(portable, 1, PortableItem.PortableType.IRON.ordinal());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("PortableID", id);
        NBTTagList items = new NBTTagList();
        NBTTagCompound entry = new NBTTagCompound();
        entry.setString("marker", marker);
        items.appendTag(entry);
        tag.setTag("Items", items);
        chest.setTagCompound(tag);
        return CelestweaveData.withHeld(moduleItem.stack(CelestweaveModule.BACKPACK), chest);
    }

    private static NBTTagCompound heldTag(ItemStack module) {
        ItemStack chest = CelestweaveData.heldOf(module);
        return chest == null ? null : chest.getTagCompound();
    }

    private static String backpackMarker(NBTTagCompound tag) {
        if (tag == null || !tag.hasKey("Items", 9)) return null;
        NBTTagList items = tag.getTagList("Items", 10);
        return items.tagCount() == 0 ? null
            : items.getCompoundTagAt(0)
                .getString("marker");
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
            new ItemStack[] { energyModule.stack(2), railgunModule.stack(RailgunModule.COMPUTE),
                railgunModule.stack(RailgunModule.COMPUTE), railgunModule.stack(RailgunModule.COMPUTE) });
        RailgunData.Modules modules = RailgunData.modules(railgun);
        check(modules.count(RailgunModule.COMPUTE) == 2, "railgun module count capped at limit");
        check(modules.energyTier() == 2, "railgun energy tier");
        check(RailgunData.modules(railgun) == modules, "railgun modules cached while unchanged");
        RailgunData.writeModuleStacks(railgun, new ItemStack[] { railgunModule.stack(RailgunModule.RANGE) });
        RailgunData.Modules changed = RailgunData.modules(railgun);
        check(
            changed.count(RailgunModule.COMPUTE) == 0 && changed.count(RailgunModule.RANGE) == 1
                && changed.energyTier() == 0,
            "railgun module change refreshes cache");
        check(
            readOnly(
                () -> changed.counts()
                    .put(RailgunModule.RANGE, 2)),
            "cached railgun counts are read only");
        check("Railgun".equals(RailgunData.ROOT), "railgun data tag name");
        NBTTagCompound railgunTop = railgun.getTagCompound();
        check(
            !railgunTop.hasKey(RailgunData.MODULES) && RailgunData.root(railgun)
                .hasKey(RailgunData.MODULES, 9),
            "railgun modules live under the railgun tag");
        RailgunSetting.SPLASH.set(railgun, false);
        check(
            !RailgunSetting.SPLASH.on(railgun) && !railgunTop.hasKey("Settings")
                && RailgunData.root(railgun)
                    .getCompoundTag("Settings")
                    .hasKey(RailgunSetting.SPLASH.key),
            "railgun settings live under the railgun tag");
        EquipmentEnergy.write(railgun, 5, 10);
        check(
            railgunTop.hasKey(EquipmentEnergy.CHARGE, 6) && !RailgunData.root(railgun)
                .hasKey(EquipmentEnergy.CHARGE),
            "railgun charge stays at the top level for IC2");
        near(5, RailgunEnergy.available(railgun), "railgun available energy is the stored charge");
        ItemStack untouched = plainStack();
        check(
            RailgunData.root(untouched)
                .hasNoTags() && !untouched.hasTagCompound()
                && RailgunData.root(null)
                    .hasNoTags()
                && RailgunSetting.SPLASH.on(untouched) == RailgunSetting.SPLASH.fallback,
            "reading railgun data creates no tag");
        RailgunData.rootForWrite(untouched);
        check(
            untouched.getTagCompound()
                .hasKey(RailgunData.ROOT, 10),
            "writing railgun data creates the railgun tag");

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

    private static void moduleSlots() throws ReflectiveOperationException, IOException {
        CelestweaveModuleItem armorModule = registered(
            bare(CelestweaveModuleItem.class),
            "gtnl:contract_slot_celestweave_module",
            31107);
        EnergyModuleItem energyModule = registered(
            bare(EnergyModuleItem.class),
            "gtnl:contract_slot_energy_module",
            31108);
        RailgunModuleItem railgunModule = registered(
            bare(RailgunModuleItem.class),
            "gtnl:contract_slot_railgun_module",
            31109);
        for (Item module : new Item[] { armorModule, energyModule, railgunModule })
            setField(Item.class, module, "maxStackSize", DEFAULT_STACK);
        for (RailgunModule type : RailgunModule.values())
            check(type.limit() == type.limit, "railgun module limit comes from its declaration " + type.id);
        for (CelestweaveModule module : CelestweaveModule.values())
            check(module.limit() == 1, "one armor module of each kind " + module.id);
        check(EnergyModuleItem.Tier.T1.limit() == 1, "variants default to one per host");

        ItemStack railgun = plainStack();
        RailgunGui railgunGui = allocate(RailgunGui.class);
        bindGui(railgunGui, railgun);
        Constructor<?> slotsOf = Class.forName(EquipmentGui.class.getName() + "$ModuleSlots")
            .getDeclaredConstructor(EquipmentGui.class, int.class, EquipmentModules.Storage.class, Function.class);
        slotsOf.setAccessible(true);
        Function<ItemStack, RailgunModule> railgunTypes = RailgunModule::of;
        ItemStackHandler railgunSlots = (ItemStackHandler) slotsOf
            .newInstance(railgunGui, RailgunData.MODULE_SLOTS, RailgunData.MODULE_STORAGE, railgunTypes);
        ItemStack compute = railgunModule.stack(RailgunModule.COMPUTE),
            beam = railgunModule.stack(RailgunModule.EHV_BEAM);
        check(railgunSlots.isItemValid(0, compute), "railgun accepts a compute module");
        railgunSlots.setStackInSlot(0, compute);
        railgunSlots.setStackInSlot(1, compute.copy());
        check(!railgunSlots.isItemValid(2, compute), "railgun rejects a compute module past its limit");
        check(railgunSlots.isItemValid(1, compute), "railgun lets a compute module be swapped in place");
        check(railgunSlots.isItemValid(2, beam), "railgun accepts an EHV beam module");
        railgunSlots.setStackInSlot(2, beam);
        check(!railgunSlots.isItemValid(3, beam), "railgun rejects a second EHV beam module");
        check(railgunSlots.isItemValid(3, energyModule.stack(1)), "railgun accepts an energy module");
        railgunSlots.setStackInSlot(3, energyModule.stack(1));
        check(!railgunSlots.isItemValid(4, energyModule.stack(2)), "railgun rejects a second energy module");
        check(!railgunSlots.isItemValid(4, plainStack()), "railgun rejects a plain item");
        check(
            !railgunSlots.isItemValid(4, armorModule.stack(CelestweaveModule.SHIELD)),
            "railgun rejects an armor module");
        RailgunData.Modules installed = RailgunData.modules(railgun);
        check(
            installed.count(RailgunModule.COMPUTE) == 2 && installed.count(RailgunModule.EHV_BEAM) == 1
                && installed.energyTier() == 1,
            "railgun slots save to the railgun");
        RailgunData.writeModuleStacks(railgun, new ItemStack[] { railgunModule.stack(RailgunModule.RANGE) });
        check(
            RailgunModule.of(railgunSlots.getStackInSlot(0)) == RailgunModule.RANGE
                && railgunSlots.getStackInSlot(1) == null,
            "railgun slots reload after an outside change");
        ItemStack stacked = plainStack();
        RailgunGui stackedGui = allocate(RailgunGui.class);
        bindGui(stackedGui, stacked);
        ItemStackHandler stackedSlots = (ItemStackHandler) slotsOf
            .newInstance(stackedGui, RailgunData.MODULE_SLOTS, RailgunData.MODULE_STORAGE, railgunTypes);
        ItemStack fiveCompute = railgunModule.stack(RailgunModule.COMPUTE);
        fiveCompute.stackSize = 5;
        ItemStack rest = stackedSlots.insertItem(0, fiveCompute, false);
        check(
            stackedSlots.getStackInSlot(0).stackSize == RailgunModule.COMPUTE.limit && rest != null
                && rest.stackSize == 5 - RailgunModule.COMPUTE.limit,
            "one slot stacks modules up to the per-host limit and hands back the rest");
        check(
            !stackedSlots.isItemValid(1, railgunModule.stack(RailgunModule.COMPUTE)),
            "a full stack counts against the limit in every other slot");
        ItemStack twoBeams = railgunModule.stack(RailgunModule.EHV_BEAM);
        twoBeams.stackSize = 2;
        rest = stackedSlots.insertItem(1, twoBeams, false);
        ItemStack twoEnergy = energyModule.stack(1);
        twoEnergy.stackSize = 2;
        ItemStack energyRest = stackedSlots.insertItem(2, twoEnergy, false);
        check(
            stackedSlots.getStackInSlot(1).stackSize == 1 && rest.stackSize == 1
                && stackedSlots.getStackInSlot(2).stackSize == 1
                && energyRest.stackSize == 1,
            "single-limit modules and energy modules still take one");
        RailgunData.Modules stackedInstalled = RailgunData.modules(stacked);
        check(
            stackedInstalled.count(RailgunModule.COMPUTE) == RailgunModule.COMPUTE.limit
                && RailgunData.moduleStacks(stacked)[0].stackSize == RailgunModule.COMPUTE.limit,
            "a stacked slot saves its count and every module in it counts");
        check(stackedSlots.getSlotLimit(0) == DEFAULT_STACK, "module slots keep the default stack size");
        check(
            RailgunData.MODULE_SLOTS == RailgunModule.values().length + 1,
            "the railgun has one slot per module kind plus the energy module");
        int[] pieceSlots = { 4, 10, 3, 5 };
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) check(
            CelestweaveData.moduleSlots(piece) == pieceSlots[piece],
            "armor piece " + piece + " has one slot per installable module kind plus the energy module");
        check(
            CelestweaveData.moduleSlots(plainStack()) == 10,
            "an unknown host reads with the most slots any piece has, so nothing is dropped");
        Class<?>[] intArg = { int.class };
        check(
            (int) call(EquipmentGui.class, "slotsPerRow", null, intArg, 10) == 5
                && (int) call(EquipmentGui.class, "slotRowsShift", null, intArg, 10) == 18
                && (int) call(EquipmentGui.class, "slotsPerRow", null, intArg, 5) == 5
                && (int) call(EquipmentGui.class, "slotRowsShift", null, intArg, 5) == 0
                && (int) call(EquipmentGui.class, "slotRowsShift", null, intArg, 0) == 0,
            "ten slots fold into two even rows that push the panel down one row, five stay on one row");
        NBTTagList wide = EquipmentModules.write(
            new ItemStack[] { null, null, null, null, null, null, null, railgunModule.stack(RailgunModule.RANGE) },
            8);
        ItemStack[] folded = EquipmentModules.read(wide, RailgunData.MODULE_SLOTS);
        check(
            folded.length == RailgunData.MODULE_SLOTS && RailgunModule.of(folded[0]) == RailgunModule.RANGE,
            "a module saved in a slot that no longer exists moves into the first free slot instead of vanishing");
        String gui = compact(read(SOURCES.resolve("common/gui/equipment/EquipmentGui.java")));
        check(
            gui.contains("newModuleSlot(handler,i)")
                && gui.contains("publicintgetItemStackLimit(ItemStackstack){returnmodules.allowance(slot,stack);}"),
            "clicks and shift-clicks ask the module slot how many of that module still fit");
        check(
            gui.contains("if(handler.getStackInSlot(slot)==null)GTNLMui2Textures.OVERLAY_SLOT_MODULE.draw(")
                && Files.isRegularFile(RESOURCES.resolve("textures/gui/equipment/module_slot.png")),
            "empty module slots show the module outline texture");
        InventoryPlayer inventory = new InventoryPlayer(null);
        EntityPlayerMP carrier = allocate(EntityPlayerMP.class);
        setField(EntityPlayer.class, carrier, "inventory", inventory);
        ElectromagneticRailgun gun = bare(ElectromagneticRailgun.class);
        inventory.currentItem = 7;
        for (int slot : new int[] { 2, 7, 39 }) inventory.setInventorySlotContents(slot, new ItemStack(gun));
        inventory.setInventorySlotContents(3, plainStack());
        check(
            Arrays.equals(EquipmentWheelScreen.equipmentSlots(carrier), new int[] { 39, 7, 2 }),
            "the wheel lists every carried equipment once: worn first, then the held item, then the inventory");

        ItemStack chest = plainStack();
        CelestweaveGui armorGui = allocate(CelestweaveGui.class);
        bindGui(armorGui, chest);
        setField(CelestweaveGui.class, armorGui, "piece", CelestweaveArmor.CHEST);
        Constructor<?> handlerOf = Class.forName(CelestweaveGui.class.getName() + "$ModuleHandler")
            .getDeclaredConstructor(CelestweaveGui.class);
        handlerOf.setAccessible(true);
        ItemStackHandler armorSlots = (ItemStackHandler) handlerOf.newInstance(armorGui);
        ItemStack shield = armorModule.stack(CelestweaveModule.SHIELD);
        check(armorSlots.isItemValid(0, shield), "armor accepts a module for its piece");
        armorSlots.setStackInSlot(0, shield);
        check(!armorSlots.isItemValid(1, shield), "armor rejects a second module of the same kind");
        check(armorSlots.isItemValid(1, armorModule.stack(CelestweaveModule.REFLECT)), "armor accepts another kind");
        check(
            !armorSlots.isItemValid(1, armorModule.stack(CelestweaveModule.NIGHT_VISION)),
            "chest rejects a helmet module");
        armorSlots.setStackInSlot(1, energyModule.stack(3));
        check(!armorSlots.isItemValid(2, energyModule.stack(1)), "armor rejects a second energy module");
        check(!armorSlots.isItemValid(2, railgunModule.stack(RailgunModule.COMPUTE)), "armor rejects a railgun module");
        check(
            CelestweaveData.installed(chest)
                .contains(CelestweaveModule.SHIELD) && CelestweaveData.energyTier(chest) == 3,
            "armor slots save to the armor");

        NBTTagList written = EquipmentModules
            .write(new ItemStack[] { null, compute, null, beam }, RailgunData.MODULE_SLOTS);
        check(
            written.tagCount() == 2 && written.getCompoundTagAt(0)
                .getByte("Slot") == 1
                && written.getCompoundTagAt(1)
                    .getByte("Slot") == 3,
            "module entries record their slot");
        written.getCompoundTagAt(0)
            .removeTag("Slot");
        ItemStack[] reread = EquipmentModules.read(written, RailgunData.MODULE_SLOTS);
        check(
            reread[0] == null && reread[1] == null && RailgunModule.of(reread[3]) == RailgunModule.EHV_BEAM,
            "module entries without a slot are skipped");
    }

    private static void bindGui(EquipmentGui gui, ItemStack host) throws ReflectiveOperationException {
        EntityPlayerMP player = allocate(EntityPlayerMP.class);
        player.inventory = new InventoryPlayer(player);
        setField(EquipmentGui.class, gui, "data", new EmptyHandData(player));
        setField(EquipmentGui.class, gui, "stack", host);
        setField(EquipmentGui.class, gui, "client", true);
    }

    private static class EmptyHandData extends PlayerInventoryGuiData {

        private EmptyHandData(EntityPlayer player) {
            super(player, null, 0);
        }

        @Override
        public ItemStack getUsedItemStack() {
            return null;
        }
    }

    private static void packets() throws ReflectiveOperationException {
        CelestweaveFxPacket fx = roundTrip(
            new CelestweaveFxPacket(42, CelestweaveFxPacket.Type.SHIELD_AMBIENT, 1.5, -2.25, 1.0E6),
            CelestweaveFxPacket.class.getConstructor());
        check((int) field(fx, "entityId") == 42, "fx entity id");
        check(field(fx, "type") == CelestweaveFxPacket.Type.SHIELD_AMBIENT, "fx type");
        near(-2.25, ((EquipmentVec) field(fx, "position")).y(), "fx y");
        near(1.0E6, ((EquipmentVec) field(fx, "position")).z(), "fx z keeps precision");
        String[] wireOrder = { "SHIELD_HIT", "DASH", "LAND", "REFLECT", "SAVE", "LIGHTNING", "SHIELD_AMBIENT" };
        CelestweaveFxPacket.Type[] types = CelestweaveFxPacket.Type.values();
        check(types.length == wireOrder.length, "fx types keep their wire order");
        for (int i = 0; i < types.length; i++) check(
            types[i].name()
                .equals(wireOrder[i]) && CelestweaveFxPacket.Type.byOrdinal(i) == types[i],
            "fx type wire order " + wireOrder[i]);
        check(
            CelestweaveFxPacket.Type.byOrdinal(-1) == null && CelestweaveFxPacket.Type.byOrdinal(types.length) == null,
            "fx type out of range rejected");
        ByteBuf unknown = Unpooled.buffer();
        unknown.writeInt(42);
        unknown.writeByte(types.length);
        EquipmentVec.ZERO.write(unknown);
        CelestweaveFxPacket dropped = new CelestweaveFxPacket();
        dropped.fromBytes(unknown);
        check(field(dropped, "type") == null, "fx packet with unknown type is dropped");
        CelestweaveFlightPacket flight = roundTrip(
            new CelestweaveFlightPacket(7, true),
            CelestweaveFlightPacket.class.getConstructor());
        check((int) field(flight, "entityId") == 7 && (boolean) field(flight, "flying"), "flight packet");
        CelestweaveKeyPacket key = roundTrip(
            new CelestweaveKeyPacket(CelestweaveKeyPacket.BACKPACK),
            CelestweaveKeyPacket.class.getConstructor());
        check((byte) field(key, "action") == CelestweaveKeyPacket.BACKPACK, "key");
        OpenEquipmentGuiPacket open = roundTrip(
            new OpenEquipmentGuiPacket(39),
            OpenEquipmentGuiPacket.class.getConstructor());
        check(
            (int) field(open, "slot") == 39,
            "open equipment packet carries the inventory slot, armor slots included");
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

    private static void hitDirection() throws ReflectiveOperationException {
        EntityPlayerMP target = allocate(EntityPlayerMP.class);
        setField(Entity.class, target, "boundingBox", AxisAlignedBB.getBoundingBox(-0.3, 64, -0.3, 0.3, 65.8, 0.3));
        target.height = 1.8F;
        EquipmentVec ahead = new HitOrigin(0, 70, 2).offset(target, 0);
        near(2, HitOrigin.forward(ahead, 0), "a hit from the south is ahead when facing south");
        near(0, HitOrigin.lateral(ahead, 0), "a hit from straight ahead is not to the side");
        near(6, ahead.y(), "hit height is measured from the feet");
        near(
            5.1,
            new HitOrigin(0, 70, 2).offset(target, 0.5)
                .y(),
            "hit height is measured from the given body height");
        EquipmentVec west = new HitOrigin(-2, 64, 0).offset(target, 0);
        near(2, HitOrigin.lateral(west, 0), "west is to the right when facing south");
        near(2, HitOrigin.forward(west, 90), "west is ahead when facing west");

        Constructor<CelestweaveClientState> constructor = CelestweaveClientState.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        CelestweaveClientState state = constructor.newInstance();
        Class<?>[] aim = { CelestweaveClientState.class, EntityLivingBase.class, HitOrigin.class, boolean.class };
        double chest = 64 + target.height * 0.72;
        call(CelestweaveClientEvents.class, "aimHit", null, aim, state, target, new HitOrigin(2, chest, 0), false);
        check(
            state.hitDirectional && Math.abs(state.hitDirectionX - 1) < 1.0E-6
                && Math.abs(state.hitDirectionY) < 1.0E-6,
            "a hit from the side aims the core burst sideways");
        call(CelestweaveClientEvents.class, "aimHit", null, aim, state, target, new HitOrigin(2, chest, 0), true);
        check(!state.hitDirectional, "an ambient hit has no direction");
        call(CelestweaveClientEvents.class, "aimHit", null, aim, state, target, new HitOrigin(0.2, chest, 0), false);
        check(!state.hitDirectional, "a hit too close to the chest has no direction");
        call(CelestweaveClientEvents.class, "aimHit", null, aim, state, target, new HitOrigin(0.5, chest, 3), false);
        check(!state.hitDirectional, "a hit mostly from ahead has no direction");
        call(CelestweaveClientEvents.class, "aimHit", null, aim, state, target, new HitOrigin(0, 64, 0.5), false);
        check(state.hitDirectional && state.hitDirectionY > 0.99F, "a hit from below aims the core burst down");
        target.renderYawOffset = 90;
        call(CelestweaveClientEvents.class, "aimHit", null, aim, state, target, new HitOrigin(0, chest, 2), false);
        check(state.hitDirectional && state.hitDirectionX > 0.99F, "the core burst aims by the body yaw");
    }

    private static ItemStack plainStack() {
        return new ItemStack(new Item());
    }

    private static void setField(Class<?> owner, Object target, String name, Object value)
        throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object unsafe() throws ReflectiveOperationException {
        Field unsafeField = Class.forName("sun.misc.Unsafe")
            .getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        return unsafeField.get(null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T allocate(Class<T> type) throws ReflectiveOperationException {
        Object unsafe = unsafe();
        return (T) unsafe.getClass()
            .getMethod("allocateInstance", Class.class)
            .invoke(unsafe, type);
    }

    private static <T extends Item> T bare(Class<T> type) throws ReflectiveOperationException {
        Object unsafe = unsafe();
        T item = allocate(type);
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
}
