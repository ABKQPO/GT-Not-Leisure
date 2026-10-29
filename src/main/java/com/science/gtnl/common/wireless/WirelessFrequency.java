package com.science.gtnl.common.wireless;

import java.util.UUID;

import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

public record WirelessFrequency(UUID id, Address legacy) {

    public WirelessFrequency {
        if ((id == null) == (legacy == null))
            throw new IllegalArgumentException("A frequency is either an id or a legacy controller position");
    }

    public static WirelessFrequency of(UUID id) {
        return new WirelessFrequency(id, null);
    }

    public static WirelessFrequency legacy(Address position) {
        return new WirelessFrequency(
            null,
            new Address(position.dimension(), position.x(), position.y(), position.z(), ForgeDirection.UNKNOWN));
    }

    public boolean isLegacy() {
        return legacy != null;
    }
}
