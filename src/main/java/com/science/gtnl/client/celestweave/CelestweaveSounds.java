package com.science.gtnl.client.celestweave;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveSounds {

    private CelestweaveSounds() {}

    private static void play(double x, double y, double z, String name, float volume, float pitch) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null || !MainConfig.client.enableCelestweaveSounds) return;
        world.playSound(
            x,
            y,
            z,
            ScienceNotLeisure.RESOURCE_ROOT_ID + ":celestweave." + name,
            volume,
            pitch * (0.95F + 0.1F * CelestweaveArcs.RNG.nextFloat()),
            false);
    }

    private static void play(Entity entity, String name, float volume, float pitch) {
        play(entity.posX, entity.boundingBox.minY + entity.height * 0.6, entity.posZ, name, volume, pitch);
    }

    static void shieldHit(Entity entity) {
        play(entity, "shield.block", 1F, 1F);
        play(entity, "shield.hum", 0.6F, 1F);
    }

    static void shieldSave(Entity entity) {
        play(entity, "shield.save", 1F, 1F);
        play(entity, "shield.hum", 1F, 0.6F);
        play(entity, "zap", 1F, 0.8F);
    }

    static void reflect(double x, double y, double z) {
        play(x, y, z, "reflect", 1F, 1F);
        play(x, y, z, "zap", 1F, 1F);
    }

    static void lightning(double x, double y, double z) {
        play(x, y, z, "lightning", 1F, 1F);
        play(x, y, z, "zap", 1F, 0.9F);
    }

    static void dash(Entity entity) {
        play(entity, "dash", 1F, 1F);
        play(entity, "whoosh", 1F, 1F);
    }

    static void land(double x, double y, double z) {
        play(x, y, z, "land", 1F, 1F);
        play(x, y, z, "zap", 0.6F, 1.1F);
    }

    static void wings(Entity entity, boolean open) {
        if (!open) {
            play(entity, "wing.close", 1F, 1F);
            return;
        }
        play(entity, "wing.open", 1F, 1F);
        play(entity, "whoosh", 0.5F, 1.2F);
        play(entity, "zap", 0.5F, 1.2F);
    }
}
