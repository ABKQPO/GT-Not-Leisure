package com.science.gtnl.common.railgun;

public final class RailgunDefaults {

    public static final int BEAM_SETTLE_INTERVAL_TICKS = 2;
    public static final int BEAM_RESEND_TICKS = 10;
    public static final int BEAM_USE_CONFLICT_TICKS = 20;
    public static final double BEAM_RESEND_DISTANCE = 0.02;
    public static final double BEAM_RANGE_LIMIT = 512;
    public static final double BEAM_RANGE_RESPONSE = 26;
    public static final double BEAM_RANGE_SNAP = 4;
    public static final int CHARGE_RESUME_GRACE_TICKS = 6;
    public static final int BEAM_RANGE = 64;
    public static final int BEAM_CHAIN_THROTTLE_TICKS = 5;
    public static final int BEAM_MAX_CONCURRENT = 16;
    public static final int ENTITY_QUERY_SEGMENT_LENGTH = 16;
    public static final int MAX_RANGE_MULTIPLIER = 4;
    public static final double CHAIN_DECAY = 0.92;
    public static final int CHAIN_BASE = 3;
    public static final int CHAIN_HARD_CAP = 50;
    public static final int CHAIN_RADIUS = 16;
    public static final int CHAIN_FORK_HARD_CAP = 8;
    public static final int CHARGED_RANGE = 64;
    public static final int PULSE_RADIUS = 10;
    public static final double PULSE_DAMAGE_RATIO = 0.6;
    public static final int PENETRATION_MAX_TARGETS = 5;
    public static final double STORM_DAMAGE_MUL = 1.25;
    public static final int STORM_CHAIN_BONUS = 2;
    public static final int STORM_CHAIN_RADIUS_BONUS = 4;
    public static final int PARALYSIS_DURATION_TICKS = 40;
    public static final double RECOIL_CROUCH_MUL = 0.5;
    public static final double RECOIL_AIRBORNE_MUL = 1.5;
    public static final double PENETRATION_DESTROY_RADIUS = 3;
    public static final double PENETRATION_DESTROY_HARDNESS = 25;

    private RailgunDefaults() {}
}
