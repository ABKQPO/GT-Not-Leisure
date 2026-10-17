package com.science.gtnl.client.railgun;

public class RailgunShakeField {

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

        public float gain() {
            return gain;
        }

        public float frequency(int index) {
            return frequencies[index];
        }
    }

    public static final float TICK_SECONDS = 0.05F;

    private static final float[] WEIGHTS = { 0.46F, 0.33F, 0.21F };
    private static final float IMPULSE_DECAY = 7.5F;
    private static final float SUSTAIN_RESPONSE = 16F;
    private static final float NEGLIGIBLE = 0.004F;
    private static final float PHASE_WRAP = 4096F;

    private float impulse;
    private float sustain;
    private float sustainTarget;
    private float seconds;

    public void kick(float degrees) {
        if (degrees <= 0F || !Float.isFinite(degrees)) return;
        impulse = Math.max(impulse, degrees);
    }

    public void hold(float degrees) {
        sustainTarget = Float.isFinite(degrees) ? Math.max(0F, degrees) : 0F;
    }

    public void tick(float deltaSeconds) {
        seconds += deltaSeconds;
        if (seconds > PHASE_WRAP) seconds -= PHASE_WRAP;
        impulse *= (float) Math.exp(-IMPULSE_DECAY * deltaSeconds);
        if (impulse < NEGLIGIBLE) impulse = 0F;
        sustain += (sustainTarget - sustain) * (1F - (float) Math.exp(-SUSTAIN_RESPONSE * deltaSeconds));
        if (sustainTarget <= 0F && sustain < NEGLIGIBLE) sustain = 0F;
    }

    public void tick() {
        tick(TICK_SECONDS);
    }

    public void reset() {
        impulse = 0F;
        sustain = 0F;
        sustainTarget = 0F;
        seconds = 0F;
    }

    public boolean idle() {
        return impulse <= 0F && sustain <= 0F && sustainTarget <= 0F;
    }

    public float intensity(float partialTicks) {
        float decay = (float) Math.exp(-IMPULSE_DECAY * partialTicks * TICK_SECONDS);
        return impulse * decay + sustain;
    }

    public float sample(Axis axis, float partialTicks) {
        float amplitude = intensity(partialTicks);
        if (amplitude <= 0F) return 0F;
        float time = seconds + partialTicks * TICK_SECONDS;
        float value = 0F;
        for (int index = 0; index < WEIGHTS.length; index++) {
            float frequency = axis.frequency(index);
            double modulation = Math.sin(time * frequency * 0.31F + index * 1.7D);
            value += WEIGHTS[index] * (float) Math.sin(time * frequency + modulation);
        }
        return value * amplitude * axis.gain();
    }
}
