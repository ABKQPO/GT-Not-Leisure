package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.science.gtnl.client.railgun.RailgunShakeField.Axis;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RailgunCameraShake {

    private static final int RECOIL_TICKS = 4;
    private static final float PITCH_FLOOR = -90F;
    private static final float RECOIL_KICK_BASE = 0.34F;
    private static final float RECOIL_KICK_STEP = 0.56F;
    private static final float CHARGE_TREMOR_MAX = 0.7F;
    private static final float CHARGE_TIER_KICK = 0.7F;
    private static final float BEAM_TREMOR = 0.16F;

    private static final RailgunShakeField FIELD = new RailgunShakeField();

    private static float pendingPitch;
    private static int recoilTicks;
    private static int chargedTier;
    private static EntityPlayer shaken;
    private static float appliedYaw;
    private static float appliedPitch;

    public static void recoil(float pitch, int tier) {
        if (!Float.isFinite(pitch)) return;
        pendingPitch += Math.max(0F, pitch);
        recoilTicks = RECOIL_TICKS;
        FIELD.kick(RECOIL_KICK_BASE + RECOIL_KICK_STEP * Math.max(0, tier - 1));
    }

    public static void clear() {
        restore();
        FIELD.reset();
        pendingPitch = 0F;
        recoilTicks = 0;
        chargedTier = 0;
    }

    public static float tremor(Axis axis, float partialTicks) {
        return FIELD.sample(axis, partialTicks);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            clear();
            return;
        }
        applyRecoil(player);
        FIELD.hold(sustain(player));
        FIELD.tick();
    }

    @SubscribeEvent
    public void render(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            restore();
            apply(Minecraft.getMinecraft().timer.renderPartialTicks);
        } else restore();
    }

    private static void applyRecoil(EntityPlayer player) {
        if (recoilTicks <= 0) return;
        float change = pendingPitch / recoilTicks;
        player.rotationPitch = Math.max(PITCH_FLOOR, player.rotationPitch - change);
        pendingPitch -= change;
        recoilTicks--;
    }

    private static float sustain(EntityPlayer player) {
        float target = RailgunBeamRenderer.localFiring() ? BEAM_TREMOR : 0F;
        if (!RailgunChargeTracker.charging(player)) {
            chargedTier = 0;
            return target;
        }
        float progress = RailgunChargeTracker.progress(player);
        target = Math.max(target, CHARGE_TREMOR_MAX * progress * (0.35F + 0.65F * progress));
        int tier = RailgunChargeTracker.tier(player)
            .ordinal();
        if (tier > chargedTier) {
            FIELD.kick(CHARGE_TIER_KICK * tier);
            chargedTier = tier;
        }
        return target;
    }

    private static void apply(float partialTicks) {
        var mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || !MainConfig.client.enableRailgunShake) return;
        if (FIELD.idle()) return;
        float yaw = FIELD.sample(Axis.YAW, partialTicks), pitch = FIELD.sample(Axis.PITCH, partialTicks);
        if (Math.abs(yaw) < 1.0E-4F && Math.abs(pitch) < 1.0E-4F) return;
        shaken = mc.thePlayer;
        appliedYaw = yaw;
        appliedPitch = pitch;
        shaken.rotationYaw += yaw;
        shaken.rotationPitch += pitch;
    }

    private static void restore() {
        if (shaken == null) return;
        shaken.rotationYaw -= appliedYaw;
        shaken.rotationPitch -= appliedPitch;
        shaken = null;
        appliedYaw = 0F;
        appliedPitch = 0F;
    }
}
