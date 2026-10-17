package com.science.gtnl.client.railgun;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.science.gtnl.common.railgun.RailgunData;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RailgunSounds {

    private static final float BASE_VOLUME = 0.6F;
    private static final float CHARGE_VOLUME_FLOOR = 0.5F;
    private static final int FADE_IN_TICKS = 6;
    private static final int FADE_OUT_TICKS = 5;
    private static final long TICK_MILLIS = 50L;

    private static final Map<Integer, BeamLoopSound> BEAMS = new HashMap<>();
    private static final Map<Integer, Long> BEAM_SESSIONS = new HashMap<>();
    private static ChargeLoopSound charge;
    private static ChargeRampSound ramp;
    private static long chargeSessionStart;
    private static boolean wasCharging;
    private static int phase;

    private static boolean enabled(Entity entity) {
        return entity instanceof EntityPlayer player && !player.isDead && RailgunData.soundEnabled(player);
    }

    private static boolean charging(EntityPlayer player) {
        return enabled(player) && RailgunCharge.charging(player);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
            clear();
            return;
        }
        BEAMS.entrySet()
            .removeIf(
                entry -> entry.getValue()
                    .isDonePlaying());
        trackBeams(minecraft);
        boolean charging = charging(minecraft.thePlayer);
        if (charging && !wasCharging) {
            chargeSessionStart = Minecraft.getSystemTime();
            phase = 0;
        }
        wasCharging = charging;
        if (charging) {
            advanceTier(minecraft.thePlayer);
            resumeChargeLoop(minecraft);
            return;
        }
        if (charge != null) charge.requestStop();
        if (ramp != null) {
            ramp.requestStop();
            ramp = null;
        }
    }

    public static void clear() {
        BEAMS.values()
            .forEach(FollowSound::stop);
        BEAMS.clear();
        BEAM_SESSIONS.clear();
        if (charge != null) charge.stop();
        if (ramp != null) ramp.stop();
        charge = null;
        ramp = null;
        chargeSessionStart = 0L;
        wasCharging = false;
        phase = 0;
    }

    private static void trackBeams(Minecraft minecraft) {
        BEAM_SESSIONS.keySet()
            .retainAll(RailgunBeamRenderer.shooters());
        for (int id : RailgunBeamRenderer.shooters()) {
            Entity entity = minecraft.theWorld.getEntityByID(id);
            if (!RailgunBeamRenderer.active(id) || !enabled(entity)) continue;
            BeamLoopSound existing = BEAMS.get(id);
            if (existing != null && !existing.isDonePlaying()) continue;
            long session = BEAM_SESSIONS.computeIfAbsent(id, key -> Minecraft.getSystemTime());
            BeamLoopSound sound = new BeamLoopSound(entity, session);
            BEAMS.put(id, sound);
            minecraft.getSoundHandler()
                .playSound(sound);
        }
    }

    private static void resumeChargeLoop(Minecraft minecraft) {
        if (charge != null && !charge.isDonePlaying()) {
            if (charge.stopping()) charge.resume();
            return;
        }
        charge = new ChargeLoopSound(minecraft.thePlayer, chargeSessionStart);
        minecraft.getSoundHandler()
            .playSound(charge);
    }

    private static void advanceTier(EntityPlayer player) {
        int next = RailgunCharge.tier(player)
            .ordinal();
        if (next <= phase) return;
        if (ramp != null) ramp.stop();
        ramp = new ChargeRampSound(player, next);
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(ramp);
        phase = next;
    }

    public abstract static class FollowSound extends MovingSound {

        protected final Entity entity;
        private final long sessionStart;
        private long stopAt;

        protected FollowSound(Entity entity, String name, boolean loop, long sessionStart) {
            super(new ResourceLocation("sciencenotleisure", "railgun." + name));
            this.entity = entity;
            this.sessionStart = sessionStart;
            this.repeat = loop;
            this.volume = 0F;
            follow();
        }

        protected abstract boolean alive();

        protected float level() {
            return 1F;
        }

        protected float rangeVolumeMultiplier() {
            return 1F;
        }

        public void requestStop() {
            if (stopAt == 0L) stopAt = Minecraft.getSystemTime();
        }

        public void resume() {
            stopAt = 0L;
        }

        public boolean stopping() {
            return stopAt != 0L;
        }

        public void stop() {
            donePlaying = true;
            repeat = false;
            volume = 0F;
        }

        @Override
        public void update() {
            long now = Minecraft.getSystemTime();
            if (donePlaying || Minecraft.getMinecraft().theWorld != entity.worldObj || !enabled(entity)) {
                stop();
                return;
            }
            follow();
            if (stopAt == 0L && !alive()) requestStop();
            float envelope = envelope(now);
            if (envelope <= 0F) {
                if (stopAt != 0L) stop();
                return;
            }
            volume = BASE_VOLUME * envelope * level() * rangeVolumeMultiplier();
        }

        private float envelope(long now) {
            float fadeIn = Math.min(1F, (now - sessionStart) / (float) (FADE_IN_TICKS * TICK_MILLIS));
            if (stopAt == 0L) return fadeIn;
            float fadeOut = 1F - (now - stopAt) / (float) (FADE_OUT_TICKS * TICK_MILLIS);
            return Math.min(fadeIn, Math.max(0F, fadeOut));
        }

        private void follow() {
            xPosF = (float) entity.posX;
            yPosF = (float) entity.posY;
            zPosF = (float) entity.posZ;
        }
    }

    public static class ChargeLoopSound extends FollowSound {

        public ChargeLoopSound(EntityPlayer player, long sessionStart) {
            super(player, "charge.loop", true, sessionStart);
        }

        @Override
        protected boolean alive() {
            return charging((EntityPlayer) entity);
        }

        @Override
        protected float level() {
            float progress = RailgunCharge.progress((EntityPlayer) entity);
            return CHARGE_VOLUME_FLOOR + (1F - CHARGE_VOLUME_FLOOR) * progress;
        }
    }

    public static class ChargeRampSound extends FollowSound {

        public ChargeRampSound(EntityPlayer player, int tier) {
            super(player, "charge.ramp", false, Minecraft.getSystemTime());
            field_147663_c = 0.8F + 0.15F * tier;
        }

        @Override
        protected boolean alive() {
            return charging((EntityPlayer) entity);
        }
    }

    public static class BeamLoopSound extends FollowSound {

        public BeamLoopSound(Entity entity, long sessionStart) {
            super(entity, "beam.loop", true, sessionStart);
        }

        @Override
        protected boolean alive() {
            return RailgunBeamRenderer.active(entity.getEntityId());
        }

        @Override
        protected float rangeVolumeMultiplier() {
            return 16F / BASE_VOLUME;
        }
    }
}
