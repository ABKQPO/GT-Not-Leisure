package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class RailgunShockwaveRenderer {

    private record Wave(RailgunVec center, double radius, long born) {}

    private static final List<Wave> ACTIVE = new ArrayList<>();

    public static void spawn(RailgunVec center, double radius) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || radius <= 0 || radius > 32 || !center.finite()) return;
        if (ACTIVE.size() >= 64) ACTIVE.remove(0);
        ACTIVE.add(new Wave(center, radius, world.getTotalWorldTime()));
    }

    public static void clear() {
        ACTIVE.clear();
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        ACTIVE.removeIf(wave -> world.getTotalWorldTime() - wave.born >= 12);
        if (ACTIVE.isEmpty()) return;
        RailgunRenderHelper.render(() -> {
            for (Wave wave : ACTIVE) {
                double progress = Math.min(1, (world.getTotalWorldTime() - wave.born + event.partialTicks) / 12);
                double radius = wave.radius * progress;
                double inner = Math.max(0, radius - 0.15 - 0.25 * progress);
                Tessellator.instance.setColorRGBA_F(1, 0.65F, 0.85F, (float) (1 - progress) * 0.8F);
                for (int i = 0; i < 64; i++) {
                    double a = i * Math.PI / 32, b = (i + 1) * Math.PI / 32;
                    RailgunRenderHelper.vertex(wave.center.add(Math.cos(a) * inner, 0.03, Math.sin(a) * inner));
                    RailgunRenderHelper.vertex(wave.center.add(Math.cos(a) * radius, 0.03, Math.sin(a) * radius));
                    RailgunRenderHelper.vertex(wave.center.add(Math.cos(b) * radius, 0.03, Math.sin(b) * radius));
                    RailgunRenderHelper.vertex(wave.center.add(Math.cos(b) * inner, 0.03, Math.sin(b) * inner));
                }
            }
        });
    }
}
