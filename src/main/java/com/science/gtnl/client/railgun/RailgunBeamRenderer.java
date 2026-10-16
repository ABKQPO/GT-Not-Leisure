package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class RailgunBeamRenderer {

    private record Shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv, long until) {}

    private static final List<Shot> SHOTS = new ArrayList<>();

    public static void shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv) {
        if (SHOTS.size() >= 64) SHOTS.remove(0);
        SHOTS.add(new Shot(shooterId, from, to, ehv, Minecraft.getMinecraft().theWorld.getTotalWorldTime() + 6));
    }

    public static void clear() {
        SHOTS.clear();
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        SHOTS.removeIf(shot -> shot.until <= world.getTotalWorldTime());
        if (SHOTS.isEmpty()) return;
        RailgunRenderHelper.render(() -> {
            for (Shot shot : SHOTS) {
                RailgunVec from = RailgunVisuals.muzzle(shot.shooterId, shot.from, event.partialTicks);
                RailgunRenderHelper.ribbon(from, shot.to, 0.2, 0.25F, 0.75F, 1, 0.5F);
                RailgunRenderHelper.ribbon(from, shot.to, 0.04, 0.85F, 0.97F, 1, 1);
            }
        });
    }
}
