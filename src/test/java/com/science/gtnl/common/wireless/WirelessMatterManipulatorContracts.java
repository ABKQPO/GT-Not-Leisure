package com.science.gtnl.common.wireless;

import static com.gtnltest.ContractAssertions.check;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.lib.Type;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

import com.recursive_pineapple.matter_manipulator.common.building.PendingBlock;
import com.recursive_pineapple.matter_manipulator.common.building.PendingBuild;
import com.recursive_pineapple.matter_manipulator.common.building.PendingMove;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessConstructionSites.Site;
import com.science.gtnl.common.wireless.WirelessLinkData.Claim;
import com.science.gtnl.common.wireless.WirelessLinkData.Slot;
import com.science.gtnl.mixins.late.matterManipulator.MixinPendingBlockWireless;
import com.science.gtnl.mixins.late.matterManipulator.MixinPendingBuildWireless;
import com.science.gtnl.mixins.late.matterManipulator.MixinPendingMoveWireless;

public class WirelessMatterManipulatorContracts {

    private static final Address A = new Address(0, 10, 64, 10, ForgeDirection.UNKNOWN);
    private static final Address B = new Address(0, 90, 64, 90, ForgeDirection.UNKNOWN);
    private static final WirelessFrequency FREQUENCY_A = WirelessFrequency.of(new UUID(0, 10));
    private static final WirelessFrequency FREQUENCY_B = WirelessFrequency.of(new UUID(0, 90));

    public static void run() throws ReflectiveOperationException {
        copiedFrequencies();
        constructionSites();
        copiedLinkQueue();
        WirelessCopiedLinksContracts.run();
        injectionTargets(MixinPendingBlockWireless.class, PendingBlock.class);
        injectionTargets(MixinPendingBuildWireless.class, PendingBuild.class);
        injectionTargets(MixinPendingMoveWireless.class, PendingMove.class);
    }

    private static Claim claim(WirelessFrequency... frequencies) {
        return new Claim("kind", Set.of(frequencies), Set.of(), Set.of());
    }

    private static void copiedFrequencies() {
        Slot down = new Slot(ForgeDirection.DOWN, false);
        Slot north = new Slot(ForgeDirection.NORTH, false);
        Slot external = new Slot(ForgeDirection.UP, true);
        check(
            Map.of(ForgeDirection.DOWN, FREQUENCY_A, ForgeDirection.NORTH, FREQUENCY_B)
                .equals(
                    WirelessCopiedLinks.frequencies(
                        Map.of(down, claim(FREQUENCY_A), north, claim(FREQUENCY_B), external, claim(FREQUENCY_B)))),
            "each part keeps its own frequency and external-facing nodes are not copied");
        check(
            WirelessCopiedLinks.frequencies(Map.of(down, claim(FREQUENCY_A, FREQUENCY_B))) == null,
            "a conflicted node copies no frequency");
        Map<ForgeDirection, WirelessFrequency> rotated = WirelessCopiedLinks.rotate(
            Map.of(ForgeDirection.NORTH, FREQUENCY_A, ForgeDirection.UNKNOWN, FREQUENCY_B),
            side -> ForgeDirection.EAST);
        check(
            Map.of(ForgeDirection.EAST, FREQUENCY_A, ForgeDirection.UNKNOWN, FREQUENCY_B)
                .equals(rotated),
            "a rotated paste moves part frequencies with their sides and keeps the centre cable: " + rotated);
    }

    private static void constructionSites() {
        WirelessConstructionSites.clear();
        Site site = new Site(0, 0, 60, 0, 9, 69, 9);
        Address inside = new Address(0, 4, 64, 4, ForgeDirection.UNKNOWN);
        Object task = new Object();
        Object other = new Object();
        WirelessConstructionSites.update(task, true, site);
        for (int tick = 0; tick < WirelessConstructionSites.QUIET_TICKS; tick++) WirelessConstructionSites.tick();
        check(WirelessConstructionSites.contains(inside), "a build site survives the gap between tool steps");
        WirelessConstructionSites.tick();
        check(!WirelessConstructionSites.contains(inside), "an abandoned build site expires after the quiet ticks");
        WirelessConstructionSites.update(task, true, site);
        WirelessConstructionSites.update(other, true, site);
        WirelessConstructionSites.update(task, false);
        check(
            WirelessConstructionSites.contains(inside),
            "a task finishing over the same area keeps another task's site there protected");
        WirelessConstructionSites.update(other, false);
        check(!WirelessConstructionSites.contains(inside), "a finished build releases its site immediately");
        WirelessConstructionSites.update(task, true, site, site.offset(100, 0, 0));
        check(
            WirelessConstructionSites.contains(new Address(0, 104, 64, 4, ForgeDirection.UNKNOWN)),
            "a move also protects its target");
        WirelessConstructionSites.clear();
    }

    private static void copiedLinkQueue() {
        WirelessCopiedLinks.clear();
        int queued = WirelessCopiedLinks.SCANS_PER_TICK + 7;
        for (int index = 0; index < queued; index++) {
            WirelessCopiedLinks.enqueue(
                UUID.randomUUID(),
                new Address(0, index, 0, 0, ForgeDirection.UNKNOWN),
                null,
                null,
                FREQUENCY_A);
        }
        WirelessCopiedLinks.tick();
        check(WirelessCopiedLinks.waiting() == 7, "one tick handles at most the scan budget");
        WirelessCopiedLinks.clear();
        UUID player = UUID.randomUUID();
        boolean accepted = true;
        for (int index = 0; index < WirelessCopiedLinks.MAX_PENDING_PER_PLAYER; index++) {
            accepted &= WirelessCopiedLinks
                .enqueue(player, new Address(0, index, 0, 0, ForgeDirection.UNKNOWN), null, null, FREQUENCY_A);
        }
        check(accepted, "one player can fill their own quota");
        check(!WirelessCopiedLinks.enqueue(player, A, null, null, FREQUENCY_A), "one player cannot exceed their quota");
        check(
            WirelessCopiedLinks.enqueue(UUID.randomUUID(), B, null, null, FREQUENCY_B),
            "another player retains queue capacity");
        WirelessCopiedLinks.tick();
        check(
            WirelessCopiedLinks.enqueue(player, A, null, null, FREQUENCY_A),
            "finishing entries returns the player's quota");
        WirelessCopiedLinks.clear();
    }

    private static void injectionTargets(Class<?> mixin, Class<?> target) {
        for (Method handler : mixin.getDeclaredMethods()) {
            Inject inject = handler.getAnnotation(Inject.class);
            if (inject == null) continue;
            for (String selector : inject.method()) {
                check(
                    hasTarget(target, selector),
                    mixin.getSimpleName() + " injects into exactly one method "
                        + target.getSimpleName()
                        + "."
                        + selector);
            }
        }
        for (Field shadow : mixin.getDeclaredFields()) {
            if (shadow.getAnnotation(Shadow.class) == null) continue;
            check(
                hasField(target, shadow),
                mixin.getSimpleName() + " shadows " + target.getSimpleName() + "." + shadow.getName());
        }
    }

    private static boolean hasTarget(Class<?> target, String selector) {
        int open = selector.indexOf('(');
        String name = open < 0 ? selector : selector.substring(0, open);
        if (name.equals("<init>")) return target.getDeclaredConstructors().length > 0;
        int matches = 0;
        for (Method method : target.getDeclaredMethods()) {
            if (method.isBridge() || !method.getName()
                .equals(name)) continue;
            if (open < 0 || selector.substring(open)
                .equals(Type.getMethodDescriptor(method))) matches++;
        }
        return matches == 1;
    }

    private static boolean hasField(Class<?> target, Field shadow) {
        try {
            return target.getDeclaredField(shadow.getName())
                .getType() == shadow.getType();
        } catch (NoSuchFieldException missing) {
            return false;
        }
    }
}
