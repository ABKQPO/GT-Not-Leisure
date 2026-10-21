package com.science.gtnl.client.railgun;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.EquipmentSound;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnllib.utils.Vec3d;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailgunSounds {

    private static final float BEAM_VOLUME = 0.85F;
    private static final int BEAM_FADE_TICKS = 8;
    private static final float RAMP_VOLUME = 0.75F;
    private static final int RAMP_FADE_TICKS = 3;
    private static final float SUSTAIN_VOLUME = 0.7F;
    private static final int SUSTAIN_FADE_TICKS = 4;
    private static final float BODY_HEIGHT = 0.45F;
    private static final double BEAM_RANGE = 256;
    private static final float MIN_START_VOLUME = 0.001F;
    private static final double CHARGE_HEARING_RANGE = 24;

    private static final Map<Integer, BeamLoopSound> BEAMS = new HashMap<>();
    private static final Map<Integer, ChargeVoice> CHARGES = new HashMap<>();

    private static boolean enabled(Entity entity) {
        return entity instanceof EntityPlayer player && !player.isDead && RailgunData.holding(player);
    }

    private static boolean charging(EntityPlayer player) {
        return enabled(player) && EquipmentSound.CHARGE.on() && RailgunCharge.charging(player);
    }

    private static boolean fullyCharged(EntityPlayer player) {
        return RailgunCharge.units(player) >= RailgunData.FULL_CHARGE_UNITS;
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
            clear();
            return;
        }
        BEAMS.values()
            .removeIf(FollowSound::isDonePlaying);
        trackBeams(minecraft);
        trackCharges(minecraft);
    }

    public static void clear() {
        BEAMS.values()
            .forEach(FollowSound::stop);
        BEAMS.clear();
        CHARGES.values()
            .forEach(ChargeVoice::stop);
        CHARGES.clear();
    }

    private static void trackBeams(Minecraft minecraft) {
        for (int id : RailgunBeamRenderer.shooters()) {
            Entity entity = minecraft.theWorld.getEntityByID(id);
            if (!RailgunBeamRenderer.active(id) || !enabled(entity) || !EquipmentSound.BEAM.on()) continue;
            BeamLoopSound existing = BEAMS.get(id);
            if (existing != null && !existing.isDonePlaying()) continue;
            BEAMS.put(id, play(minecraft, new BeamLoopSound(entity)));
        }
    }

    private static void trackCharges(Minecraft minecraft) {
        for (Object entity : minecraft.theWorld.playerEntities) {
            EntityPlayer player = (EntityPlayer) entity;
            ChargeVoice voice = CHARGES.get(player.getEntityId());
            if (voice == null) {
                if (!charging(player) || !withinHearing(minecraft, player)) continue;
                voice = new ChargeVoice();
                CHARGES.put(player.getEntityId(), voice);
            }
            voice.track(minecraft, player);
        }
        CHARGES.entrySet()
            .removeIf(entry -> {
                ChargeVoice voice = entry.getValue();
                if (minecraft.theWorld.getEntityByID(entry.getKey()) instanceof EntityPlayer && !voice.idle())
                    return false;
                voice.stop();
                return true;
            });
    }

    private static boolean withinHearing(Minecraft minecraft, EntityPlayer player) {
        return player == minecraft.thePlayer
            || player.getDistanceSqToEntity(minecraft.thePlayer) <= CHARGE_HEARING_RANGE * CHARGE_HEARING_RANGE;
    }

    private static <T extends FollowSound> T play(Minecraft minecraft, T sound) {
        minecraft.getSoundHandler()
            .playSound(sound);
        return sound;
    }

    public static void playAt(EquipmentSound channel, Vec3d at, String name, float volume, float pitch, double range) {
        Entity listener = listener();
        if (at == null || listener == null || range <= 0) return;
        float scaled = distanceFalloff(listener, at.x(), at.y(), at.z(), range) * volume * channel.volume();
        if (scaled <= 0F) return;
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(new PointSound(name, scaled, pitch, at));
    }

    private static Entity listener() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft.renderViewEntity != null ? minecraft.renderViewEntity : minecraft.thePlayer;
    }

    private static float distanceFalloff(Entity listener, double x, double y, double z, double range) {
        return (float) Math.max(0, 1 - Math.sqrt(listener.getDistanceSq(x, y, z)) / range);
    }

    public static class PointSound extends PositionedSound {

        public PointSound(String name, float volume, float pitch, Vec3d at) {
            super(new ResourceLocation(ScienceNotLeisure.RESOURCE_ROOT_ID, "railgun." + name));
            this.volume = volume;
            this.field_147663_c = pitch;
            this.xPosF = (float) at.x();
            this.yPosF = (float) at.y();
            this.zPosF = (float) at.z();
            this.field_147666_i = ISound.AttenuationType.NONE;
        }
    }

    private static class ChargeVoice {

        private ChargeRampSound ramp;
        private ChargeSustainSound sustain;
        private boolean wasCharging;

        private void track(Minecraft minecraft, EntityPlayer player) {
            boolean charging = charging(player);
            if (charging && !wasCharging) {
                stop();
                ramp = null;
                sustain = null;
            }
            wasCharging = charging;
            if (!charging) {
                if (ramp != null) ramp.requestStop();
                if (sustain != null) sustain.requestStop();
                return;
            }
            if (fullyCharged(player)) {
                if (ramp != null) ramp.requestStop();
                if (sustain == null || sustain.isDonePlaying())
                    sustain = play(minecraft, new ChargeSustainSound(player));
            } else {
                if (sustain != null) sustain.requestStop();
                if (ramp == null) ramp = play(minecraft, new ChargeRampSound(player));
            }
        }

        private boolean idle() {
            return !wasCharging && (ramp == null || ramp.isDonePlaying())
                && (sustain == null || sustain.isDonePlaying());
        }

        private void stop() {
            if (ramp != null) ramp.stop();
            if (sustain != null) sustain.stop();
        }
    }

    public abstract static class FollowSound extends MovingSound {

        protected final Entity entity;
        private final EquipmentSound channel;
        private final float target;
        private final int fadeTicks;
        private final double range;
        private int fadeIn = 1;
        private int fadeOut = -1;

        protected FollowSound(Entity entity, EquipmentSound channel, String name, boolean loop, float target,
            int fadeTicks, double range) {
            super(new ResourceLocation(ScienceNotLeisure.RESOURCE_ROOT_ID, "railgun." + name));
            this.entity = entity;
            this.channel = channel;
            this.target = target;
            this.fadeTicks = fadeTicks;
            this.range = range;
            this.repeat = loop;
            this.field_147665_h = 0;
            if (range > 0) this.field_147666_i = ISound.AttenuationType.NONE;
            follow();
            this.volume = Math.max(MIN_START_VOLUME, target / fadeTicks * falloff() * channel.volume());
        }

        protected abstract boolean alive();

        public void requestStop() {
            if (fadeOut < 0) fadeOut = fadeTicks;
        }

        public void stop() {
            donePlaying = true;
            repeat = false;
            volume = 0F;
        }

        @Override
        public void update() {
            if (donePlaying) return;
            if (entity.isDead || Minecraft.getMinecraft().theWorld != entity.worldObj
                || !enabled(entity)
                || !channel.on()) {
                stop();
                return;
            }
            if (!alive()) requestStop();
            follow();
            if (fadeOut < 0) {
                if (fadeIn < fadeTicks) fadeIn++;
                volume = target * fadeIn / fadeTicks * falloff() * channel.volume();
            } else if (fadeOut > 0) {
                fadeOut--;
                volume = target * fadeOut / fadeTicks * falloff() * channel.volume();
            } else stop();
        }

        private float falloff() {
            if (range <= 0) return 1F;
            Entity listener = listener();
            return listener == null ? 1F : distanceFalloff(listener, xPosF, yPosF, zPosF, range);
        }

        private void follow() {
            xPosF = (float) entity.posX;
            yPosF = (float) (entity.boundingBox.minY + entity.height * BODY_HEIGHT);
            zPosF = (float) entity.posZ;
        }
    }

    public static class BeamLoopSound extends FollowSound {

        public BeamLoopSound(Entity entity) {
            super(entity, EquipmentSound.BEAM, "beam.loop", true, BEAM_VOLUME, BEAM_FADE_TICKS, BEAM_RANGE);
        }

        @Override
        protected boolean alive() {
            return RailgunBeamRenderer.active(entity.getEntityId());
        }
    }

    public static class ChargeRampSound extends FollowSound {

        public ChargeRampSound(EntityPlayer player) {
            super(player, EquipmentSound.CHARGE, "charge.ramp", false, RAMP_VOLUME, RAMP_FADE_TICKS, 0);
        }

        @Override
        protected boolean alive() {
            EntityPlayer player = (EntityPlayer) entity;
            return charging(player) && !fullyCharged(player);
        }
    }

    public static class ChargeSustainSound extends FollowSound {

        public ChargeSustainSound(EntityPlayer player) {
            super(player, EquipmentSound.CHARGE, "charge.loop", true, SUSTAIN_VOLUME, SUSTAIN_FADE_TICKS, 0);
        }

        @Override
        protected boolean alive() {
            EntityPlayer player = (EntityPlayer) entity;
            return charging(player) && fullyCharged(player);
        }
    }
}
