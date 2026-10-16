package com.science.gtnl.client.railgun;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunModuleType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class RailgunSounds {

    private static final Map<Integer, BeamLoopSound> BEAMS = new HashMap<>();
    private static ChargeLoopSound charge;
    private static ChargeRampSound ramp;
    private static int phase;

    private static boolean enabled(Entity entity) {
        if (!(entity instanceof EntityPlayer player) || player.isDead) return false;
        ItemStack stack = player.getHeldItem();
        return stack != null && stack.getItem() instanceof ElectromagneticRailgun
            && RailgunData.settings(stack)
                .sound();
    }

    private static boolean charging(EntityPlayer player) {
        return enabled(player) && player.isUsingItem();
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
            if (charge == null || charge.isDonePlaying()) {
                charge = new ChargeLoopSound(mc.thePlayer);
                mc.getSoundHandler()
                    .playSound(charge);
            }
            int units = mc.thePlayer.getItemInUseDuration() * (1 + RailgunData.modules(mc.thePlayer.getHeldItem())
                .count(RailgunModuleType.ACCELERATION));
            int next = RailgunChargeTier.fromUnits(units)
                .ordinal();
            if (next > phase) {
                if (ramp != null) ramp.stop();
                ramp = new ChargeRampSound(mc.thePlayer, next);
                mc.getSoundHandler()
                    .playSound(ramp);
            }
            phase = next;
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

    public abstract static class FollowSound extends MovingSound {

        protected final Entity entity;
        protected int age;

        protected FollowSound(Entity entity, String name, boolean loop) {
            super(new ResourceLocation("sciencenotleisure", "railgun." + name));
            this.entity = entity;
            repeat = loop;
            volume = 0.6F;
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
            volume = 0.6F * Math.min(1, age / 8.0F);
        }
    }

    public static final class ChargeLoopSound extends FollowSound {

        public ChargeLoopSound(EntityPlayer player) {
            super(player, "charge.loop", true);
        }

        @Override
        protected boolean active() {
            return charging((EntityPlayer) entity);
        }
    }

    public static final class ChargeRampSound extends FollowSound {

        public ChargeRampSound(EntityPlayer player, int tier) {
            super(player, "charge.ramp", false);
            field_147663_c = 0.8F + 0.15F * tier;
        }

        @Override
        protected boolean active() {
            return age < 30 && charging((EntityPlayer) entity);
        }
    }

    public static final class BeamLoopSound extends FollowSound {

        public BeamLoopSound(Entity entity) {
            super(entity, "beam.loop", true);
        }

        @Override
        protected boolean active() {
            return RailgunBeamRenderer.active(entity.getEntityId());
        }
    }
}
