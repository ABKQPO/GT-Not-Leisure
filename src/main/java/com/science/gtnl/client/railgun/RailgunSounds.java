package com.science.gtnl.client.railgun;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RailgunSounds {

    private static final float BASE_VOLUME = 0.6F;
    private static final int FADE_IN_TICKS = 8;

    private static final Map<Integer, BeamLoopSound> BEAMS = new HashMap<>();
    private static ChargeLoopSound charge;
    private static ChargeRampSound ramp;
    private static int phase;

    private static boolean enabled(Entity entity) {
        return entity instanceof EntityPlayer player && !player.isDead && RailgunChargeTracker.soundEnabled(player);
    }

    private static boolean charging(EntityPlayer player) {
        return enabled(player) && RailgunChargeTracker.charging(player);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            clear();
            return;
        }
        BEAMS.entrySet()
            .removeIf(
                entry -> entry.getValue()
                    .isDonePlaying());
        for (int id : RailgunBeamRenderer.shooters()) {
            Entity entity = mc.theWorld.getEntityByID(id);
            if (RailgunBeamRenderer.active(id) && enabled(entity) && !BEAMS.containsKey(id)) {
                BeamLoopSound sound = new BeamLoopSound(entity);
                BEAMS.put(id, sound);
                mc.getSoundHandler()
                    .playSound(sound);
            }
        }
        if (charging(mc.thePlayer)) {
            startChargeLoop(mc);
            advanceTier(mc.thePlayer);
        } else {
            phase = 0;
            if (charge != null) charge.stop();
            if (ramp != null) ramp.stop();
        }
    }

    public static void clear() {
        BEAMS.values()
            .forEach(FollowSound::stop);
        BEAMS.clear();
        if (charge != null) charge.stop();
        if (ramp != null) ramp.stop();
        charge = null;
        ramp = null;
        phase = 0;
    }

    private static void startChargeLoop(Minecraft mc) {
        if (charge != null && !charge.isDonePlaying()) return;
        charge = new ChargeLoopSound(mc.thePlayer);
        mc.getSoundHandler()
            .playSound(charge);
    }

    private static void advanceTier(EntityPlayer player) {
        int next = RailgunChargeTracker.tier(player)
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
        protected int age;

        protected FollowSound(Entity entity, String name, boolean loop) {
            super(new ResourceLocation("sciencenotleisure", "railgun." + name));
            this.entity = entity;
            repeat = loop;
            volume = BASE_VOLUME;
            follow();
        }

        private void follow() {
            xPosF = (float) entity.posX;
            yPosF = (float) entity.posY;
            zPosF = (float) entity.posZ;
        }

        protected abstract boolean active();

        public void stop() {
            donePlaying = true;
            repeat = false;
            volume = 0;
        }

        @Override
        public void update() {
            age++;
            if (donePlaying || Minecraft.getMinecraft().theWorld != entity.worldObj || !enabled(entity) || !active()) {
                stop();
                return;
            }
            follow();
            volume = BASE_VOLUME * Math.min(1, age / (float) FADE_IN_TICKS);
        }
    }

    public static class ChargeLoopSound extends FollowSound {

        public ChargeLoopSound(EntityPlayer player) {
            super(player, "charge.loop", true);
        }

        @Override
        protected boolean active() {
            return charging((EntityPlayer) entity);
        }
    }

    public static class ChargeRampSound extends FollowSound {

        public ChargeRampSound(EntityPlayer player, int tier) {
            super(player, "charge.ramp", false);
            field_147663_c = 0.8F + 0.15F * tier;
        }

        @Override
        protected boolean active() {
            return age < 30 && charging((EntityPlayer) entity);
        }
    }

    public static class BeamLoopSound extends FollowSound {

        public BeamLoopSound(Entity entity) {
            super(entity, "beam.loop", true);
        }

        @Override
        protected boolean active() {
            return RailgunBeamRenderer.active(entity.getEntityId());
        }
    }
}
