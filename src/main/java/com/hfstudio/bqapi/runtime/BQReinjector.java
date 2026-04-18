package com.hfstudio.bqapi.runtime;

import net.minecraft.server.MinecraftServer;

import com.hfstudio.bqapi.BQApi;

import betterquesting.handlers.SaveLoadHandler;
import betterquesting.network.handlers.NetChapterSync;
import betterquesting.network.handlers.NetQuestSync;
import betterquesting.network.handlers.NetSettingSync;

public final class BQReinjector {

    private BQReinjector() {}

    public static void reinject(MinecraftServer server) {
        if (server == null) {
            return;
        }

        BQApi.reinject(server);
        NetSettingSync.sendSync(null);
        NetQuestSync.quickSync(null, true, true);
        NetChapterSync.sendSync(null, null);
        SaveLoadHandler.INSTANCE.markDirty();
    }
}
