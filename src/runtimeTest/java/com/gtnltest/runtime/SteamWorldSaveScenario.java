package com.gtnltest.runtime;

import static com.gtnltest.runtime.RuntimeReport.require;

import java.math.BigInteger;
import java.util.UUID;

import com.science.gtnl.common.steam.GlobalSteamWorldSavedData;
import com.science.gtnl.common.steam.SteamWirelessNetworkManager;

/** The verify phase runs in a new JVM against the exact world saved by the seed phase. */
final class SteamWorldSaveScenario implements RuntimeScenario {

    private static final UUID TEAM = UUID.fromString("00000000-0000-0000-0000-000000000663");
    private static final BigInteger BALANCE = new BigInteger("123456789012345678901234567890");
    private final boolean verify;

    SteamWorldSaveScenario(boolean verify) {
        this.verify = verify;
    }

    public String id() {
        return verify ? "world.steam.restart-read" : "world.steam.restart-write";
    }

    public String scope() {
        return "world-disk-persistence";
    }

    public boolean tick(RuntimeFixture fixture, int ticks) {
        require(GlobalSteamWorldSavedData.INSTANCE != null, "real WorldSavedData must have initialized");
        if (verify) {
            require(
                BALANCE.equals(SteamWirelessNetworkManager.GLOBAL_STEAM.get(TEAM)),
                "steam above long range must survive a complete JVM/server restart");
        } else {
            SteamWirelessNetworkManager.GLOBAL_STEAM.put(TEAM, BALANCE);
            GlobalSteamWorldSavedData.INSTANCE.markDirty();
            fixture.world.mapStorage.saveAllData();
        }
        return true;
    }
}
