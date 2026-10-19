package com.science.gtnl.common.celestweave;

import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.config.MainConfig;

public record CelestweaveCosmetic(int glowColor, int idleArcs, float arcFreq, float arcSpread, float arcBright,
    int arcEvents) {

    public static final int EVENT_HIT = 1, EVENT_DASH = 2, EVENT_LAND = 4, EVENT_REFLECT = 8, EVENT_WING = 16,
        EVENT_ANTENNA = 32, EVENT_LOW = 64, EVENT_ALL = 127;
    public static final int DEFAULT_COLOR = 0xFF3D9A;
    public static final CelestweaveCosmetic DEFAULT = new CelestweaveCosmetic(DEFAULT_COLOR, -1, 1F, 1F, 1F, EVENT_ALL);

    public static CelestweaveCosmetic read(NBTTagCompound tag) {
        if (tag == null || tag.hasNoTags()) return DEFAULT;
        return new CelestweaveCosmetic(
            tag.hasKey("GlowColor") ? tag.getInteger("GlowColor") & 0xFFFFFF : DEFAULT_COLOR,
            tag.hasKey("IdleArcs")
                ? Math.max(-1, Math.min(MainConfig.item.celestweave.maxIdleArcs, tag.getInteger("IdleArcs")))
                : -1,
            tag.hasKey("ArcFreq") ? clamp(tag.getFloat("ArcFreq"), 0.25F, 3F) : 1F,
            tag.hasKey("ArcSpread") ? clamp(tag.getFloat("ArcSpread"), 0.3F, 2.5F) : 1F,
            tag.hasKey("ArcBright") ? clamp(tag.getFloat("ArcBright"), 0.3F, 1.6F) : 1F,
            tag.hasKey("ArcEvents") ? tag.getInteger("ArcEvents") & EVENT_ALL : EVENT_ALL);
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("GlowColor", glowColor);
        tag.setInteger("IdleArcs", idleArcs);
        tag.setFloat("ArcFreq", arcFreq);
        tag.setFloat("ArcSpread", arcSpread);
        tag.setFloat("ArcBright", arcBright);
        tag.setInteger("ArcEvents", arcEvents);
        return tag;
    }

    public boolean event(int flag) {
        return (arcEvents & flag) != 0;
    }

    private static float clamp(float value, float min, float max) {
        return Float.isFinite(value) ? Math.max(min, Math.min(max, value)) : min;
    }
}
