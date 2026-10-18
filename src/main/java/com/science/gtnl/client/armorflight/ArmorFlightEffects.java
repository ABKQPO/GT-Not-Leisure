package com.science.gtnl.client.armorflight;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.armorflight.ArmorFlight;
import com.science.gtnl.common.armorflight.FlightVec;
import com.science.gtnl.common.armorflight.IArmorFlightEffects;
import com.science.gtnl.common.entity.EntityArmorFlightPiece;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArmorFlightEffects implements IArmorFlightEffects {

    private static final int JET = 1, FLYBY = 2, LATCH = 4, LOCK = 8, HISS = 16, UNLOCK = 32, PURGE = 64, LAUNCH = 128,
        BRAKE = 256, PULSE_A = 512, PULSE_B = 1024, PULSE_C = 2048, SNAP = 4096, UNFOLD = 8192, SEGMENT = 16384,
        POOF = 1 << 19, ORBIT = 1 << 20;
    private static final String SPARK = "magicCrit", CLOUD = "cloud", POOF_PARTICLE = "explode";
    private static final Random RANDOM = new Random();

    private static final ResourceLocation SOUND_LAUNCH = sound("launch");
    private static final ResourceLocation SOUND_JET_LOOP = sound("jet_loop");
    private static final ResourceLocation SOUND_TURBINE_LOOP = sound("turbine_loop");
    private static final ResourceLocation SOUND_FLYBY = sound("flyby");
    private static final ResourceLocation SOUND_BRAKE = sound("brake");
    private static final ResourceLocation SOUND_LOCK = sound("lock");
    private static final ResourceLocation SOUND_UNLOCK = sound("unlock");
    private static final ResourceLocation SOUND_PULSE = sound("pulse");
    private static final ResourceLocation SOUND_LATCH = sound("latch");
    private static final ResourceLocation SOUND_HISS = sound("hiss");
    private static final ResourceLocation SOUND_PURGE = sound("purge");

    private static ResourceLocation sound(String name) {
        return new ResourceLocation(ScienceNotLeisure.MODID, "armory." + name);
    }

    @Override
    public float wearerYaw(Entity wearer, float partialTick) {
        Minecraft mc = Minecraft.getMinecraft();
        if (wearer == mc.renderViewEntity && mc.gameSettings.thirdPersonView == 0) {
            return wearer.prevRotationYaw + (wearer.rotationYaw - wearer.prevRotationYaw) * partialTick;
        }
        return ArmorFlight.bodyYaw(wearer, partialTick);
    }

    @Override
    public void tick(EntityArmorFlightPiece piece) {
        if (piece.item() == null) return;
        float age = piece.age(0);
        if (piece.isReturn()) {
            if (age >= 0) tickReturn(piece, age);
        } else {
            tickDeliver(piece, age);
        }
    }

    private void tickDeliver(EntityArmorFlightPiece piece, float age) {
        int flight = piece.duration();
        if (age < 0) {
            if (age >= -ArmorFlight.HATCH_TICKS) {
                FlightVec at = piece.flightPosition(0);
                if (RANDOM.nextInt(2) == 0) vanilla(
                    piece,
                    CLOUD,
                    at.add(jitter(0.25))
                        .add(0, -0.2, 0),
                    new FlightVec(0, 0.04, 0).add(jitter(0.03)));
                if (RANDOM.nextInt(3) == 0) vanilla(piece, SPARK, at.add(jitter(0.2)), jitter(0.08));
            }
            return;
        }
        if (age < flight) {
            once(piece, LAUNCH, () -> {
                play(piece, SOUND_LAUNCH, 1.0F, 0.95F + RANDOM.nextFloat() * 0.1F);
                FlightVec at = piece.flightPosition(0);
                for (int i = 0; i < 6; i++) fx(piece, ArmorFlightFX.Kind.CORE, at.add(jitter(0.1)), jitter(0.05));
                for (int i = 0; i < 14; i++) {
                    double a = i / 14.0 * Math.PI * 2;
                    vanilla(
                        piece,
                        CLOUD,
                        at.add(0, -0.3, 0),
                        new FlightVec(Math.cos(a) * 0.18, 0.01, Math.sin(a) * 0.18));
                }
            });
            once(piece, JET, () -> {
                loop(piece, SOUND_JET_LOOP, ArmorFlightSound.Kind.JET, 0.7F);
                loop(piece, SOUND_TURBINE_LOOP, ArmorFlightSound.Kind.TURBINE, 0.45F);
            });
            double t = age / flight;
            if (flight - age <= 26) once(piece, FLYBY, () -> play(piece, SOUND_FLYBY, 0.8F, 1.0F));
            if (t >= ArmorFlight.BRAKE_AT) {
                once(piece, BRAKE, () -> {
                    play(piece, SOUND_BRAKE, 1.0F, 1.0F);
                    retroBlast(piece, 14);
                });
                retroBlast(piece, 3);
                if (t > 0.9) once(piece, PULSE_A, () -> pulse(piece));
            } else {
                exhaust(piece, t < 0.12 ? 2.0 : 1.0);
            }
            return;
        }
        float hover = age - flight;
        float sinceSnap = age - piece.snapStart();
        int n = ArmorFlight.segments(piece.piece());
        for (int j = 0; j < n && n > 1; j++) {
            if (sinceSnap < ArmorFlight.segmentLockTick(j, n)) continue;
            int seg = j;
            once(piece, SEGMENT << j, () -> {
                play(piece, SOUND_LOCK, 0.85F, 0.72F + 0.14F * seg);
                burst(piece, SPARK, 6, 0.18);
                burstFx(piece, ArmorFlightFX.Kind.CORE, 2, 0.05);
            });
        }
        if (hover < ArmorFlight.HOVER_TICKS) {
            once(piece, UNFOLD, () -> {
                play(piece, SOUND_UNLOCK, 0.8F, 0.7F);
                burst(piece, SPARK, 5, 0.12);
            });
            liftJets(piece);
            if (hover >= 2) once(piece, PULSE_B, () -> pulse(piece));
            if (hover >= 6) once(piece, PULSE_C, () -> pulse(piece));
            return;
        }
        if (piece.orbiting(age)) {
            once(piece, ORBIT, () -> play(piece, SOUND_FLYBY, 0.6F, 1.25F));
            exhaust(piece, 1.2);
            return;
        }
        float snap = sinceSnap;
        if (snap < ArmorFlight.SNAP_TICKS) {
            once(piece, SNAP, () -> play(piece, SOUND_PULSE, 0.8F, 0.75F));
            exhaust(piece, 1.0);
            return;
        }
        float latch = snap - ArmorFlight.SNAP_TICKS;
        once(piece, LATCH, () -> {
            play(piece, SOUND_LATCH, 1.0F, 0.95F + RANDOM.nextFloat() * 0.1F);
            burst(piece, SPARK, 10, 0.3);
            burstFx(piece, ArmorFlightFX.Kind.CORE, 4, 0.06);
        });
        if (latch >= 3) once(piece, LOCK, () -> {
            play(piece, SOUND_LOCK, 0.9F, 1.0F);
            burst(piece, SPARK, 4, 0.12);
        });
        if (latch >= ArmorFlight.LATCH_TICKS - 1) once(piece, HISS, () -> {
            play(piece, SOUND_HISS, 0.7F, 1.0F);
            burst(piece, CLOUD, 3, 0.05);
        });
    }

    private void tickReturn(EntityArmorFlightPiece piece, float age) {
        once(piece, UNLOCK, () -> {
            play(piece, SOUND_UNLOCK, 0.9F, 1.0F);
            burst(piece, SPARK, 6, 0.2);
        });
        once(piece, POOF, () -> {
            play(piece, SOUND_PULSE, 1.0F, 0.55F);
            FlightVec at = piece.flightPosition(0);
            for (int i = 0; i < 16; i++) {
                double a = i / 16.0 * Math.PI * 2;
                vanilla(
                    piece,
                    CLOUD,
                    at,
                    new FlightVec(Math.cos(a) * 0.22, (RANDOM.nextDouble() - 0.3) * 0.12, Math.sin(a) * 0.22));
            }
            burst(piece, POOF_PARTICLE, 6, 0.1);
            burstFx(piece, ArmorFlightFX.Kind.CORE, 3, 0.08);
        });
        if (age < ArmorFlight.PURGE_TICKS) {
            if (age < 4) burst(piece, CLOUD, 1, 0.03);
            return;
        }
        once(piece, PURGE, () -> {
            play(piece, SOUND_PURGE, 1.0F, 1.05F);
            loop(piece, SOUND_JET_LOOP, ArmorFlightSound.Kind.JET, 0.6F);
            burstFx(piece, ArmorFlightFX.Kind.CORE, 5, 0.08);
            burstFx(piece, ArmorFlightFX.Kind.THRUST, 8, 0.12);
        });
        exhaust(piece, 1.6);
    }

    private static FlightVec velocity(EntityArmorFlightPiece piece) {
        return piece.flightPosition(1)
            .subtract(piece.flightPosition(0));
    }

    private static void exhaust(EntityArmorFlightPiece piece, double power) {
        FlightVec now = piece.flightPosition(0);
        FlightVec velocity = velocity(piece);
        double speed = velocity.length();
        if (speed < 1.0E-3) return;
        FlightVec back = velocity.scale(-1 / speed);
        FlightVec nozzle = now.add(back.scale(0.28));
        for (int i = 0; i < 3; i++) {
            FlightVec at = nozzle.add(back.scale(0.12 * i))
                .add(velocity.scale(RANDOM.nextDouble()));
            fx(piece, ArmorFlightFX.Kind.CORE, at, back.scale(0.02 + speed * 0.1));
        }
        int jet = (int) Math.round(4 * power);
        for (int i = 0; i < jet; i++) {
            FlightVec at = nozzle.add(velocity.scale(RANDOM.nextDouble()))
                .add(jitter(0.03));
            FlightVec v = back.scale(0.18 + RANDOM.nextDouble() * 0.12 + speed * 0.15)
                .add(jitter(0.025));
            fx(piece, ArmorFlightFX.Kind.THRUST, at, v);
        }
        fx(
            piece,
            ArmorFlightFX.Kind.TRAIL,
            now.add(back.scale(0.5)),
            back.scale(0.01)
                .add(0, 0.004, 0));
        if (RANDOM.nextInt(3) == 0) vanilla(
            piece,
            SPARK,
            nozzle,
            back.scale(0.25)
                .add(jitter(0.05)));
    }

    private static void retroBlast(EntityArmorFlightPiece piece, int count) {
        FlightVec now = piece.flightPosition(0);
        FlightVec fwd = velocity(piece).normalize();
        for (int i = 0; i < count; i++) {
            double side = RANDOM.nextBoolean() ? 1 : -1;
            FlightVec lateral = new FlightVec(-fwd.z(), 0, fwd.x()).scale(0.18 * side);
            FlightVec at = now.add(lateral)
                .add(fwd.scale(0.2));
            FlightVec v = fwd.scale(0.28 + RANDOM.nextDouble() * 0.15)
                .add(lateral.scale(0.4))
                .add(jitter(0.03));
            fx(piece, i % 3 == 0 ? ArmorFlightFX.Kind.CORE : ArmorFlightFX.Kind.THRUST, at, v);
        }
        if (RANDOM.nextInt(2) == 0) fx(piece, ArmorFlightFX.Kind.TRAIL, now.add(fwd.scale(0.6)), fwd.scale(0.05));
    }

    private static void liftJets(EntityArmorFlightPiece piece) {
        FlightVec at = piece.flightPosition(0)
            .add(0, -0.25, 0);
        fx(piece, ArmorFlightFX.Kind.CORE, at.add(jitter(0.05)), new FlightVec(0, -0.06, 0));
        for (int i = 0; i < 2; i++)
            fx(piece, ArmorFlightFX.Kind.THRUST, at.add(jitter(0.08)), new FlightVec(0, -0.15, 0).add(jitter(0.03)));
        if (RANDOM.nextInt(3) == 0) fx(piece, ArmorFlightFX.Kind.TRAIL, at.add(0, -0.3, 0), new FlightVec(0, -0.01, 0));
    }

    private static void pulse(EntityArmorFlightPiece piece) {
        play(piece, SOUND_PULSE, 0.7F, 0.9F + RANDOM.nextFloat() * 0.3F);
        FlightVec at = piece.flightPosition(0);
        FlightVec dir = jitter(1).multiply(1, 0.3, 1)
            .normalize();
        fx(piece, ArmorFlightFX.Kind.CORE, at.add(dir.scale(0.25)), dir.scale(0.05));
        for (int i = 0; i < 5; i++) fx(
            piece,
            ArmorFlightFX.Kind.THRUST,
            at.add(dir.scale(0.25)),
            dir.scale(0.2)
                .add(jitter(0.04)));
        fx(piece, ArmorFlightFX.Kind.TRAIL, at.add(dir.scale(0.45)), dir.scale(0.03));
    }

    private static void burst(EntityArmorFlightPiece piece, String type, int count, double speed) {
        FlightVec at = piece.flightPosition(0);
        for (int i = 0; i < count; i++) {
            FlightVec v = jitter(speed);
            vanilla(piece, type, at.add(v), v);
        }
    }

    private static void burstFx(EntityArmorFlightPiece piece, ArmorFlightFX.Kind kind, int count, double speed) {
        FlightVec at = piece.flightPosition(0);
        for (int i = 0; i < count; i++) {
            FlightVec v = jitter(speed);
            fx(piece, kind, at.add(v), v);
        }
    }

    private static void vanilla(EntityArmorFlightPiece piece, String type, FlightVec at, FlightVec v) {
        piece.worldObj.spawnParticle(type, at.x(), at.y(), at.z(), v.x(), v.y(), v.z());
    }

    private static void fx(EntityArmorFlightPiece piece, ArmorFlightFX.Kind kind, FlightVec at, FlightVec v) {
        Minecraft mc = Minecraft.getMinecraft();
        int setting = mc.gameSettings.particleSetting;
        if (setting >= 2 || (setting == 1 && RANDOM.nextBoolean())) return;
        EntityLivingBase viewer = mc.renderViewEntity;
        if (viewer != null && viewer.getDistanceSq(at.x(), at.y(), at.z()) > 64 * 64) return;
        mc.effectRenderer
            .addEffect(new ArmorFlightFX(piece.worldObj, kind, at.x(), at.y(), at.z(), v.x(), v.y(), v.z()));
    }

    private static void once(EntityArmorFlightPiece piece, int flag, Runnable action) {
        if ((piece.fxFlags & flag) != 0) return;
        piece.fxFlags |= flag;
        action.run();
    }

    private static void play(EntityArmorFlightPiece piece, ResourceLocation sound, float volume, float pitch) {
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(new ArmorFlightSound(piece, sound, ArmorFlightSound.Kind.ONESHOT, volume, pitch));
    }

    private static void loop(EntityArmorFlightPiece piece, ResourceLocation sound, ArmorFlightSound.Kind kind,
        float gain) {
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(new ArmorFlightSound(piece, sound, kind, gain, 1.0F));
    }

    private static FlightVec jitter(double scale) {
        return new FlightVec(
            RANDOM.nextGaussian() * scale,
            RANDOM.nextGaussian() * scale,
            RANDOM.nextGaussian() * scale);
    }
}
