package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.science.gtnl.client.fx.world.FxShockwaveRenderer;
import com.science.gtnl.client.fx.world.FxVisibility;
import com.science.gtnl.client.hud.HudRegistry;
import com.science.gtnl.common.equipment.EquipmentSound;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunFireFxPacket;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailgunClientFx {

    private static final int IMPACT_SPARKS = 24;
    private static final double SPARK_SPREAD = 0.6;
    private static final int CHAIN_SPARKS = 4;
    private static final double CHAIN_SPARK_SPREAD = 0.4;
    private static final double SHOT_HEARING_RANGE = 256, CHAIN_HEARING_RANGE = 16;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new RailgunClientFx());
        MinecraftForge.EVENT_BUS.register(new RailgunBeamRenderer());
        MinecraftForge.EVENT_BUS.register(new RailgunOrbRenderer());
        MinecraftForge.EVENT_BUS.register(new RailgunHudRenderer());
        HudRegistry.register(RailgunHudRenderer.CHARGE);
        FMLCommonHandler.instance()
            .bus()
            .register(new RailgunCameraShake());
        FMLCommonHandler.instance()
            .bus()
            .register(new RailgunClientInput());
        FMLCommonHandler.instance()
            .bus()
            .register(new RailgunSounds());
    }

    public static void playFire(RailgunFireFxPacket packet) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        RailgunCharge.finish(packet.shooterId);
        if (packet.sound) RailgunSounds.playAt(
            EquipmentSound.FIRE,
            packet.from,
            max(packet) ? "fire.max" : "fire.charged",
            1F,
            1F,
            SHOT_HEARING_RANGE);
        if (!visible(packet.shooterId, packet.from)) {
            impactSound(packet);
            return;
        }
        EquipmentVec muzzle = RailgunMuzzleAnchor
            .muzzle(packet.shooterId, packet.from, Minecraft.getMinecraft().timer.renderPartialTicks);
        RailgunOrbRenderer.launch(packet.shooterId, muzzle, packet.firstHit, packet.tier, () -> impact(packet, muzzle));
    }

    private static boolean max(RailgunFireFxPacket packet) {
        return packet.tier == RailgunChargeTier.EHV3.ordinal();
    }

    private static void impact(RailgunFireFxPacket packet, EquipmentVec muzzle) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        boolean max = max(packet);
        EquipmentVec hit = packet.firstHit;
        float intensity = 1F + 0.22F * Math.max(0, packet.tier - 1);
        RailgunBeamRenderer.shot(packet.shooterId, muzzle, hit, true, intensity);
        double radius = packet.impactRadius;
        if (radius > 0) {
            int waveLife = max ? 80 : 56 + packet.tier * 6;
            shockwave(hit, radius, waveLife);
            shockwave(hit, radius * 0.5, Math.max(35, waveLife - 15));
        }
        for (int index = 0; index + 1 < packet.chainPath.size(); index += 2) {
            EquipmentVec target = packet.chainPath.get(index + 1);
            RailgunArcs.chain(packet.chainPath.get(index), target, max ? 40 : 32, true);
            sparks(world, target, CHAIN_SPARKS, CHAIN_SPARK_SPREAD);
        }
        RailgunArcs.plasma(
            muzzle,
            hit,
            Math.max(
                10,
                (int) Math.round(
                    muzzle.subtract(hit)
                        .length() * 1.2)),
            max ? 0.3 : 0.18,
            max ? 36 : 30);
        if (radius > 0) {
            int bolts = max ? 18 : 8 + packet.tier * 2;
            for (int i = 0; i < bolts; i++) {
                double yaw = world.rand.nextDouble() * Math.PI * 2;
                double pitch = (world.rand.nextDouble() - 0.3) * Math.PI;
                double length = radius * (0.6 + world.rand.nextDouble() * 0.6);
                RailgunArcs.impactSpark(
                    hit,
                    hit.add(
                        Math.cos(yaw) * Math.cos(pitch) * length,
                        Math.sin(pitch) * length,
                        Math.sin(yaw) * Math.cos(pitch) * length),
                    22 + world.rand.nextInt(10));
            }
        }
        sparks(world, hit, IMPACT_SPARKS, SPARK_SPREAD);
        impactSound(packet);
    }

    private static void impactSound(RailgunFireFxPacket packet) {
        if (!packet.sound) return;
        RailgunSounds.playAt(
            EquipmentSound.IMPACT,
            packet.firstHit,
            "fire.impact",
            1F,
            max(packet) ? 0.7F : 0.85F,
            SHOT_HEARING_RANGE);
    }

    public static boolean visible(int shooterId, EquipmentVec at) {
        return FxVisibility.allowed(
            FxVisibility.isViewer(shooterId),
            FxVisibility.distanceToViewer(at),
            MainConfig.client.enableRailgunShowOthersFx,
            MainConfig.client.railgunFxDistance);
    }

    private static void shockwave(EquipmentVec at, double radius, int lifetime) {
        FxShockwaveRenderer.spawn(
            at,
            radius,
            lifetime,
            RailgunOrbRenderer.SHOCKWAVE_RED,
            RailgunOrbRenderer.SHOCKWAVE_GREEN,
            RailgunOrbRenderer.SHOCKWAVE_BLUE);
    }

    private static void sparks(WorldClient world, EquipmentVec at, int count, double spread) {
        for (int spark = 0; spark < count; spark++) world.spawnParticle(
            "fireworksSpark",
            at.x(),
            at.y(),
            at.z(),
            (world.rand.nextDouble() - 0.5) * spread,
            (world.rand.nextDouble() - 0.5) * spread,
            (world.rand.nextDouble() - 0.5) * spread);
    }

    public static void playChain(RailgunBeamChainFxPacket packet) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        if (visible(packet.shooterId, packet.origin)) for (int index = 0; index + 1 < packet.path.size(); index += 2)
            RailgunArcs.chain(packet.path.get(index), packet.path.get(index + 1), 6, packet.ehv);
        if (packet.sound)
            RailgunSounds.playAt(EquipmentSound.CHAIN, packet.origin, "beam.chain", 0.35F, 1.6F, CHAIN_HEARING_RANGE);
    }

    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
        if (!event.world.isRemote) return;
        RailgunBeamRenderer.clear();
        RailgunOrbRenderer.clear();
        RailgunCameraShake.clear();
        RailgunCharge.clear();
        RailgunMuzzleAnchor.clear();
        RailgunSounds.clear();
    }
}
