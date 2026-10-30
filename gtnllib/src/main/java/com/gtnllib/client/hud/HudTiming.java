package com.gtnllib.client.hud;

public enum HudTiming {

    ALWAYS,
    ACTIVE,
    CHANGED;

    public static final long FADE_MS = 500;
    private static final String LANG_PREFIX = "gtnllib.hud.timing.";

    public String lang() {
        return LANG_PREFIX + HudLayout.id(this);
    }

    public float level(float awake, float idleAlpha) {
        return this == CHANGED ? idleAlpha + (1 - idleAlpha) * awake : awake;
    }

    public static float fade(long overdue) {
        return overdue <= 0 ? 1F : Math.max(0F, 1F - overdue / (float) FADE_MS);
    }

    public static class Tracker {

        private boolean signed;
        private int signature;
        private long wokeAt;

        public float awake(HudTiming timing, long now, boolean active, int signature, float idleSeconds) {
            if (!signed || signature != this.signature) {
                signed = true;
                this.signature = signature;
                wokeAt = now;
            }
            if (active) wokeAt = now;
            return switch (timing) {
                case ALWAYS -> 1F;
                case ACTIVE -> active ? 1F : 0F;
                case CHANGED -> fade(now - wokeAt - (long) (idleSeconds * 1000));
            };
        }

        public void reset() {
            signed = false;
        }
    }
}
