package com.science.gtnl.client.celestweave;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleSupplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveModule;
import com.science.gtnl.common.celestweave.CelestweaveModuleSetting;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.equipment.CelestweaveModuleItem;
import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;
import com.science.gtnl.common.item.items.equipment.RailgunModuleItem;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.packet.RailgunFireFxPacket;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunEnergy;
import com.science.gtnl.common.railgun.RailgunModule;
import com.science.gtnl.common.railgun.RailgunSetting;
import com.sun.management.ThreadMXBean;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class EquipmentPerfBench {

    private static final int WARMUP = 200_000, RUNS = 2_000_000;
    private static final ThreadMXBean THREADS = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    private static double sink;

    private EquipmentPerfBench() {}

    public static void main(String[] args) throws Exception {
        Item plain = new Item();
        CelestweaveModuleItem armorModule = item(CelestweaveModuleItem.class, "gtnl:bench_celestweave_module", 31001);
        EnergyModuleItem energyModule = item(EnergyModuleItem.class, "gtnl:bench_energy_module", 31002);
        RailgunModuleItem railgunModule = item(RailgunModuleItem.class, "gtnl:bench_railgun_module", 31003);
        register(plain, "gtnl:bench_plain", 31004);

        ItemStack chest = new ItemStack(plain);
        List<ItemStack> modules = new ObjectArrayList<>();
        modules.add(new ItemStack(energyModule, 1, 2));
        for (CelestweaveModule module : new CelestweaveModule[] { CelestweaveModule.SHIELD, CelestweaveModule.REFLECT,
            CelestweaveModule.PURIFICATION, CelestweaveModule.HAZARD, CelestweaveModule.SOULBOUND,
            CelestweaveModule.REACH }) modules.add(new ItemStack(armorModule, 1, module.meta));
        modules.add(
            CelestweaveData
                .withHeld(new ItemStack(armorModule, 1, CelestweaveModule.BACKPACK.meta), new ItemStack(plain)));
        CelestweaveData.writeModuleStacks(chest, modules.toArray(new ItemStack[0]));
        CelestweaveData.writeFlag(chest, CelestweaveData.DISABLED, CelestweaveModule.HAZARD, true);
        CelestweaveData.writeFlag(chest, CelestweaveData.HIDDEN, CelestweaveModule.BACKPACK, true);
        CelestweaveData.settingsForWrite(chest, CelestweaveModule.REACH)
            .setInteger("Level", 2);
        CelestweaveEnergy.write(chest, CelestweaveEnergy.capacity(chest));

        ItemStack railgun = new ItemStack(plain);
        ItemStack[] slots = new ItemStack[RailgunData.MODULE_SLOTS];
        slots[0] = new ItemStack(energyModule, 1, 2);
        RailgunModule[] types = { RailgunModule.COMPUTE, RailgunModule.COMPUTE, RailgunModule.ACCELERATION,
            RailgunModule.ACCELERATION, RailgunModule.RANGE, RailgunModule.RANGE, RailgunModule.EHV_BEAM };
        for (int i = 0; i < types.length; i++) slots[i + 1] = railgunModule.stack(types[i]);
        RailgunData.writeModuleStacks(railgun, slots);
        EquipmentEnergy.write(railgun, 1.0E8, RailgunEnergy.capacity(railgun));

        check(
            CelestweaveData.installed(chest)
                .size() == 7,
            "armor modules load");
        check(CelestweaveData.energyTier(chest) == 3, "armor energy tier");
        check(
            RailgunData.modules(railgun)
                .count(RailgunModule.COMPUTE) == 2,
            "railgun modules load");

        System.out.println("== 护甲数据访问（胸甲 8 个模块）==");
        bench(
            "CelestweaveData.activeModules",
            () -> CelestweaveData.activeModules(chest)
                .size());
        bench("CelestweaveData.active", () -> CelestweaveData.active(chest, CelestweaveModule.SHIELD) ? 1 : 0);
        bench(
            "CelestweaveData.installed（缓存命中）",
            () -> CelestweaveData.installed(chest)
                .size());
        bench(
            "CelestweaveData.idList(Disabled)",
            () -> CelestweaveData.idList(chest, CelestweaveData.DISABLED)
                .size());
        bench("CelestweaveEnergy.capacity", () -> CelestweaveEnergy.capacity(chest));
        bench("CelestweaveEnergy.fraction", () -> CelestweaveEnergy.fraction(chest));
        bench("CelestweaveModuleSetting.REACH_LEVEL.get", () -> CelestweaveModuleSetting.REACH_LEVEL.get(chest));
        bench(
            "CelestweaveData.cosmetic",
            () -> CelestweaveData.cosmetic(chest)
                .getInt(CelestweaveCosmeticOption.GLOW_COLOR));
        bench(
            "CelestweaveData.held（缓存命中）",
            () -> CelestweaveData.held(chest, CelestweaveModule.BACKPACK) == null ? 0 : 1);
        bench("CelestweaveData.moduleStacks（无缓存）", () -> CelestweaveData.moduleStacks(chest).length);

        System.out.println("== 电磁炮数据访问（8 个模块）==");
        bench(
            "RailgunData.modules",
            () -> RailgunData.modules(railgun)
                .count(RailgunModule.RANGE));
        bench("RailgunEnergy.capacity（IC2 getMaxCharge）", () -> RailgunEnergy.capacity(railgun));
        bench(
            "RailgunData.modules().energyTier（IC2 getTier）",
            () -> RailgunData.modules(railgun)
                .energyTier());
        bench("RailgunSetting.CHAIN.active", () -> RailgunSetting.CHAIN.active(railgun) ? 1 : 0);
        bench("RailgunData.chargeRate", () -> RailgunData.chargeRate(railgun));
        bench("EquipmentEnergy.read", () -> EquipmentEnergy.read(railgun));

        ItemStack chestCopy = chest.copy(), railgunCopy = railgun.copy();
        bench(
            "ItemStack.areItemStacksEqual（胸甲，原版每 tick）",
            () -> ItemStack.areItemStacksEqual(chest, chestCopy) ? 1 : 0);
        bench(
            "ItemStack.areItemStacksEqual（电磁炮，原版每 tick）",
            () -> ItemStack.areItemStacksEqual(railgun, railgunCopy) ? 1 : 0);
        bench("ItemStack.copy（胸甲，电量变化时）", () -> chest.copy().stackSize);

        System.out.println("== HUD 每帧几何 ==");
        float[] core = CelestweaveHud.polygon(8F, 0);
        bench("CelestweaveHud.clipBelow + span", () -> {
            float[] fill = CelestweaveHud.clipBelow(core, 2F);
            float[] span = CelestweaveHud.span(fill, 2F);
            return fill.length + (span == null ? 0 : span[0]);
        });

        System.out.println("== 网络与 NBT 大小 ==");
        size("护甲胸甲 NBT（压缩后，装备同步时发送）", chest);
        size("电磁炮 NBT（压缩后，每次扣电重发）", railgun);
        List<Vec3d> path = new ObjectArrayList<>();
        for (int i = 0; i < 256; i++) path.add(new Vec3d(i, 64, i));
        packet("RailgunFireFxPacket（满 256 点路径）", new RailgunFireFxPacket(1, Vec3d.ZERO, Vec3d.ZERO, path, 3, 10F));
        packet(
            "RailgunBeamChainFxPacket（14 点路径）",
            new RailgunBeamChainFxPacket(1, Vec3d.ZERO, path.subList(0, 14), true));
        packet("RailgunBeamUpdatePacket", RailgunBeamUpdatePacket.move(1, 64, true));
        packet("CelestweaveFxPacket", new CelestweaveFxPacket(1, CelestweaveFxPacket.Type.SHIELD_HIT, 0, 0, 0));
        if (sink == 42) System.out.println();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError("bench setup failed: " + message);
    }

    private static void bench(String name, DoubleSupplier task) {
        for (int i = 0; i < WARMUP; i++) sink += task.getAsDouble();
        long thread = Thread.currentThread()
            .getId();
        long bytes = THREADS.getThreadAllocatedBytes(thread);
        long start = System.nanoTime();
        for (int i = 0; i < RUNS; i++) sink += task.getAsDouble();
        long elapsed = System.nanoTime() - start;
        long allocated = THREADS.getThreadAllocatedBytes(thread) - bytes;
        System.out.printf(
            Locale.ROOT,
            "%-44s %9.1f ns/次 %9.1f B/次%n",
            name,
            elapsed / (double) RUNS,
            allocated / (double) RUNS);
    }

    private static void size(String name, ItemStack stack) throws IOException {
        NBTTagCompound tag = stack.writeToNBT(new NBTTagCompound());
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        CompressedStreamTools.write(tag, new DataOutputStream(raw));
        System.out.printf(
            Locale.ROOT,
            "%-44s %6d B（未压缩 %d B）%n",
            name,
            CompressedStreamTools.compress(tag).length,
            raw.size());
    }

    private static void packet(String name, IMessage message) {
        ByteBuf buffer = Unpooled.buffer();
        message.toBytes(buffer);
        System.out.printf(Locale.ROOT, "%-44s %6d B%n", name, buffer.readableBytes());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item> T item(Class<T> type, String name, int id) throws Exception {
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
        Field stackSize = Item.class.getDeclaredField("maxStackSize");
        stackSize.setAccessible(true);
        stackSize.setInt(item, 64);
        register(item, name, id);
        return item;
    }

    private static void register(Item item, String name, int id) throws Exception {
        Method add = Item.itemRegistry.getClass()
            .getDeclaredMethod("add", int.class, String.class, Object.class, BitSet.class);
        add.setAccessible(true);
        add.invoke(Item.itemRegistry, id, name, item, new BitSet());
        check(Item.getIdFromItem(item) >= 0, "register " + name);
    }
}
