package com.science.gtnl.common.railgun;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.packet.RailgunFireFxPacket;
import com.science.gtnl.config.MainConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class RailgunContractTest {

    private static int assertions;

    private RailgunContractTest() {}

    public static void main(String[] args) throws ReflectiveOperationException {
        charge();
        damage();
        vectors();
        settings();
        packets();
        System.out.println("Railgun contracts passed: " + assertions + " assertions");
    }

    private static void charge() {
        check(RailgunChargeTier.fromUnits(9) == RailgunChargeTier.NONE);
        check(RailgunChargeTier.fromUnits(10) == RailgunChargeTier.EHV1);
        check(RailgunChargeTier.fromUnits(23) == RailgunChargeTier.EHV1);
        check(RailgunChargeTier.fromUnits(24) == RailgunChargeTier.EHV2);
        check(RailgunChargeTier.fromUnits(39) == RailgunChargeTier.EHV2);
        check(RailgunChargeTier.fromUnits(40) == RailgunChargeTier.EHV3);
        for (int step = 1; step <= 3; step++) for (int units = 0; units <= 1000; units++) {
            double expected = 0;
            for (int unit = 0; unit < units; unit += step) expected += unit < 24 ? 250 : unit < 40 ? 1000 : 2500;
            equal(expected, RailgunFireService.chargeCostUpTo(units, step));
        }
        equal(821200, RailgunFireService.shotCost(RailgunChargeTier.EHV1));
        equal(2467600, RailgunFireService.shotCost(RailgunChargeTier.EHV2));
        equal(6603600, RailgunFireService.shotCost(RailgunChargeTier.EHV3));
        var config = MainConfig.item.railgun;
        config.chargeEuEhv1 = 100;
        try {
            equal(1000, RailgunFireService.chargeCostUpTo(10, 1));
        } finally {
            config.chargeEuEhv1 = 250;
        }
        equal(64, RailgunRaycast.effectiveRange(64, 0));
        equal(128, RailgunRaycast.effectiveRange(64, 1));
        equal(256, RailgunRaycast.effectiveRange(64, 2));
        equal(256, RailgunRaycast.effectiveRange(64, Integer.MAX_VALUE));
        equal(64, RailgunRaycast.effectiveRange(64, -1));
    }

    private static void damage() {
        equal(52, RailgunDamage.finalDamage(100, 0.4, 0.8));
        equal(84, RailgunDamage.finalDamage(100, 0.8, 0.8));
        equal(100, RailgunDamage.finalDamage(100, 1, 0.8));
        equal(20, RailgunDamage.finalDamage(100, 0, 100));
        equal(0, RailgunDamage.finalDamage(-100, 0, 0));
    }

    private static void vectors() {
        RailgunVec from = new RailgunVec(10, 20, 30), to = new RailgunVec(20, 20, 30);
        equal(
            1,
            new RailgunVec(3, 4, 5).normalize()
                .length());
        equal(
            0,
            RailgunVec.ZERO.normalize()
                .length());
        check(
            new RailgunVec(1, 0, 0).cross(new RailgunVec(0, 1, 0))
                .equals(new RailgunVec(0, 0, 1)));
        check(
            RailgunRaycast.sanitize(from, to, new RailgunVec(15, 20, 30))
                .equals(new RailgunVec(15, 20, 30)));
        check(
            RailgunRaycast.sanitize(from, to, new RailgunVec(15, 25, 30))
                .equals(to));
        check(
            RailgunRaycast.sanitize(from, to, new RailgunVec(30, 20, 30))
                .equals(to));
        check(
            RailgunRaycast.sanitize(from, to, new RailgunVec(Double.NaN, 0, 0))
                .equals(to));
        check(
            RailgunRaycast.sanitize(from, from, from)
                .equals(from));
    }

    private static void settings() {
        ItemStack stack = new ItemStack(new Item());
        equal(0, RailgunEnergy.read(stack));
        check(!stack.hasTagCompound());
        check(
            RailgunData.settings(stack)
                .sound());
        check(!stack.hasTagCompound());
        RailgunData.setSetting(stack, "sound", false);
        check(
            !RailgunData.settings(stack)
                .sound());
        check(
            !stack.getTagCompound()
                .getBoolean("ExtremeHighVoltage"));
        RailgunData.setModuleStacks(stack, new ItemStack[8]);
        check(
            RailgunData.modules(stack)
                .counts()
                .isEmpty());
        RailgunSettings defaults = RailgunSettings.read(new NBTTagCompound());
        check(
            !defaults.terrain() && !defaults.pvp()
                && defaults.sound()
                && defaults.splash()
                && defaults.chain()
                && !defaults.ehvBeam());
        for (int bits = 0; bits < 64; bits++) {
            RailgunSettings settings = new RailgunSettings(
                (bits & 1) != 0,
                (bits & 2) != 0,
                (bits & 4) != 0,
                (bits & 8) != 0,
                (bits & 16) != 0,
                (bits & 32) != 0);
            check(settings.equals(RailgunSettings.read(settings.write())));
        }
    }

    private static void packets() throws ReflectiveOperationException {
        List<RailgunVec> path = new ArrayList<>();
        for (int i = 0; i < 300; i++) path.add(new RailgunVec(i, 0, 0));
        ByteBuf buffer = Unpooled.buffer();
        try {
            RailgunNet.writePath(buffer, path);
            check(
                RailgunNet.readPath(buffer)
                    .equals(path.subList(0, 256)));
            check(buffer.readableBytes() == 0);
            buffer.clear();
            buffer.writeShort(65535);
            rejects(() -> RailgunNet.readPath(buffer));
            buffer.clear();
            buffer.writeShort(1);
            rejects(() -> RailgunNet.readPath(buffer));
            buffer.clear();
            buffer.writeShort(2);
            rejects(() -> RailgunNet.readPath(buffer));
            buffer.clear();
            RailgunNet.writeVec(buffer, new RailgunVec(Double.POSITIVE_INFINITY, 0, 0));
            rejects(() -> RailgunNet.readVec(buffer));
            buffer.clear();
            RailgunFireFxPacket fire = new RailgunFireFxPacket(
                7,
                RailgunVec.ZERO,
                new RailgunVec(64, 0, 0),
                path,
                3,
                12,
                false);
            fire.toBytes(buffer);
            RailgunFireFxPacket received = new RailgunFireFxPacket();
            received.fromBytes(buffer);
            check(
                valid(received) && received.shooterId == 7
                    && received.chainPath.size() == 256
                    && !received.sound
                    && received.tier == 3);
            buffer.clear();
            received.fromBytes(buffer);
            check(!valid(received));
            buffer.clear();
            new RailgunBeamUpdatePacket(8, RailgunVec.ZERO, new RailgunVec(256, 0, 0), true, true).toBytes(buffer);
            RailgunBeamUpdatePacket update = new RailgunBeamUpdatePacket();
            update.fromBytes(buffer);
            check(valid(update) && update.active && update.ehv && update.shooterId == 8);
            buffer.clear();
            new RailgunBeamUpdatePacket(8, RailgunVec.ZERO, new RailgunVec(513, 0, 0), true, false).toBytes(buffer);
            update.fromBytes(buffer);
            check(!valid(update));
            buffer.clear();
            new RailgunBeamChainFxPacket(9, RailgunVec.ZERO, path, true, false).toBytes(buffer);
            RailgunBeamChainFxPacket chain = new RailgunBeamChainFxPacket();
            chain.fromBytes(buffer);
            check(valid(chain) && chain.path.size() == 256 && chain.sound && !chain.ehv);
        } finally {
            buffer.release();
        }
    }

    private static boolean valid(Object packet) throws ReflectiveOperationException {
        Field field = packet.getClass()
            .getDeclaredField("valid");
        field.setAccessible(true);
        return field.getBoolean(packet);
    }

    private static void rejects(Runnable operation) {
        boolean rejected = false;
        try {
            operation.run();
        } catch (IllegalArgumentException exception) {
            rejected = true;
        }
        check(rejected);
    }

    private static void equal(double expected, double actual) {
        check(Math.abs(expected - actual) < 1.0E-7);
    }

    private static void check(boolean condition) {
        assertions++;
        if (!condition) throw new AssertionError("Railgun contract failed at assertion " + assertions);
    }
}
