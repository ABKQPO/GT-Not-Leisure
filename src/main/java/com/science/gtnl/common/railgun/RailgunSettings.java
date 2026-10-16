package com.science.gtnl.common.railgun;

import net.minecraft.nbt.NBTTagCompound;

public record RailgunSettings(boolean terrain, boolean pvp, boolean sound, boolean splash, boolean chain,
    boolean ehvBeam) {

    public static RailgunSettings read(NBTTagCompound tag) {
        return new RailgunSettings(
            tag.getBoolean("terrain"),
            tag.getBoolean("pvp"),
            !tag.hasKey("sound") || tag.getBoolean("sound"),
            !tag.hasKey("splash") || tag.getBoolean("splash"),
            !tag.hasKey("chain") || tag.getBoolean("chain"),
            tag.getBoolean("ehvBeam"));
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("terrain", terrain);
        tag.setBoolean("pvp", pvp);
        tag.setBoolean("sound", sound);
        tag.setBoolean("splash", splash);
        tag.setBoolean("chain", chain);
        tag.setBoolean("ehvBeam", ehvBeam);
        return tag;
    }
}
