package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailgunCameraShake {

    public enum Axis {

        YAW(1F, 6.3F, 11.7F, 17.9F),
        PITCH(0.85F, 7.1F, 13.3F, 19.7F),
        ROLL(1.5F, 4.3F, 8.9F, 15.1F);

        private final float gain;
        private final float[] frequencies;

        Axis(float gain, float first, float second, float third) {
            this.gain = gain;
            this.frequencies = new float[] { first, second, third };
        }
    }

    private static final float[] WEIGHTS = { 0.46F, 0.33F, 0.21F };
    private static final float IMPULSE_DECAY = 7.5F;
    private static final float SUSTAIN_RESPONSE = 16F;
    private static final float NEGLIGIBLE = 0.004F;
    private static final float PHASE_WRAP = 4096F;
    private static final int RECOIL_TICKS = 4;
    private static final float PITCH_FLOOR = -90F;
    private static final float RECOIL_KICK_BASE = 0.34F;
    private static final float RECOIL_KICK_STEP = 0.56F;
    private static final float CHARGE_TREMOR_MAX = 0.7F;
    private static final float CHARGE_TIER_KICK = 0.7F;
    private static final float BEAM_TREMOR = 0.16F;

    private static float impulse;
    private static float sustain;
    private static float sustainTarget;
    private static float seconds;
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
        kick(RECOIL_KICK_BASE + RECOIL_KICK_STEP * Math.max(0, tier - 1));
    }

    public static void clear() {
        restore();
        impulse = 0F;
        sustain = 0F;
        sustainTarget = 0F;
        seconds = 0F;
        pendingPitch = 0F;
        recoilTicks = 0;
        chargedTier = 0;
    }

    public static float tremor(Axis axis, float partialTicks) {
        float amplitude = intensity(partialTicks);
        if (amplitude <= 0F) return 0F;
        float time = seconds + partialTicks * 0.05F;
        float value = 0F;
        for (int index = 0; index < WEIGHTS.length; index++) {
            float frequency = axis.frequencies[index];
            double modulation = Math.sin(time * frequency * 0.31F + index * 1.7D);
            value += WEIGHTS[index] * (float) Math.sin(time * frequency + modulation);
        }
        return value * amplitude * axis.gain;
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
        sustainTarget = sustain(player);
        advance();
    }

    @SubscribeEvent
    public void render(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            restore();
            apply(Minecraft.getMinecraft().timer.renderPartialTicks);
        } else restore();
    }

    private static void kick(float degrees) {
        if (degrees <= 0F || !Float.isFinite(degrees)) return;
        impulse = Math.max(impulse, degrees);
    }

    private static void advance() {
        seconds += 0.05F;
        if (seconds > PHASE_WRAP) seconds -= PHASE_WRAP;
        impulse *= (float) Math.exp(-IMPULSE_DECAY * 0.05F);
        if (impulse < NEGLIGIBLE) impulse = 0F;
        sustain += (sustainTarget - sustain) * (1F - (float) Math.exp(-SUSTAIN_RESPONSE * 0.05F));
        if (sustainTarget <= 0F && sustain < NEGLIGIBLE) sustain = 0F;
    }

    private static float intensity(float partialTicks) {
        return impulse * (float) Math.exp(-IMPULSE_DECAY * partialTicks * 0.05F) + sustain;
    }

    private static boolean idle() {
        return impulse <= 0F && sustain <= 0F;
    }

    private static void applyRecoil(EntityPlayer player) {
        if (recoilTicks <= 0) return;
        float change = pendingPitch / recoilTicks;
        player.rotationPitch = Math.max(PITCH_FLOOR, player.rotationPitch - change);
        pendingPitch -= change;
        recoilTicks--;
    }

    private static float sustain(EntityPlayer player) {
        float target = RailgunBeamRenderer.localFiring() && RailgunBeamRenderer.active(player.getEntityId())
            ? BEAM_TREMOR
            : 0F;
        if (!RailgunCharge.charging(player)) {
            chargedTier = 0;
            return target;
        }
        float progress = RailgunCharge.progress(player);
        target = Math.max(target, CHARGE_TREMOR_MAX * progress * (0.35F + 0.65F * progress));
        int tier = RailgunCharge.tier(player)
            .ordinal();
        if (tier > chargedTier) {
            kick(CHARGE_TIER_KICK * tier);
            chargedTier = tier;
        }
        return target;
    }

    private static void apply(float partialTicks) {
        var minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || minecraft.theWorld == null || !MainConfig.client.enableRailgunShake) return;
        if (idle()) return;
        float yaw = tremor(Axis.YAW, partialTicks), pitch = tremor(Axis.PITCH, partialTicks);
        if (Math.abs(yaw) < 1.0E-4F && Math.abs(pitch) < 1.0E-4F) return;
        shaken = minecraft.thePlayer;
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
