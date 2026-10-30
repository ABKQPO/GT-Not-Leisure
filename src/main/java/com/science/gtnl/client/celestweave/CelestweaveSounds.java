package com.science.gtnl.client.celestweave;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.EquipmentSound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveSounds {

    private static final Random RNG = new Random();

    private static void play(EquipmentSound sound, double x, double y, double z, String name, float volume,
        float pitch) {
        World world = Minecraft.getMinecraft().theWorld;
        float scale = sound.volume();
        if (world == null || scale <= 0F) return;
        world.playSound(
            x,
            y,
            z,
            ScienceNotLeisure.RESOURCE_ROOT_ID + ":celestweave." + name,
            volume * scale,
            pitch * (0.95F + 0.1F * RNG.nextFloat()),
            false);
    }

    private static void play(EquipmentSound sound, Entity entity, String name, float volume, float pitch) {
        play(sound, entity.posX, entity.boundingBox.minY + entity.height * 0.6, entity.posZ, name, volume, pitch);
    }

    static void shieldHit(Entity entity) {
        play(EquipmentSound.SHIELD_HIT, entity, "shield.block", 1F, 1F);
        play(EquipmentSound.SHIELD_HIT, entity, "shield.hum", 0.6F, 1F);
    }

    static void shieldSave(Entity entity) {
        play(EquipmentSound.SHIELD_SAVE, entity, "shield.save", 1F, 1F);
        play(EquipmentSound.SHIELD_SAVE, entity, "shield.hum", 1F, 0.6F);
        play(EquipmentSound.SHIELD_SAVE, entity, "zap", 1F, 0.8F);
    }

    static void reflect(double x, double y, double z) {
        play(EquipmentSound.REFLECT, x, y, z, "reflect", 1F, 1F);
        play(EquipmentSound.REFLECT, x, y, z, "zap", 1F, 1F);
    }

    static void lightning(double x, double y, double z) {
        play(EquipmentSound.LIGHTNING, x, y, z, "lightning", 1F, 1F);
        play(EquipmentSound.LIGHTNING, x, y, z, "zap", 1F, 0.9F);
    }

    static void dash(Entity entity) {
        play(EquipmentSound.DASH, entity, "dash", 1F, 1F);
        play(EquipmentSound.DASH, entity, "whoosh", 1F, 1F);
    }

    static void land(double x, double y, double z) {
        play(EquipmentSound.LAND, x, y, z, "land", 1F, 1F);
        play(EquipmentSound.LAND, x, y, z, "zap", 0.6F, 1.1F);
    }

    static void wings(Entity entity, boolean open) {
        if (!open) {
            play(EquipmentSound.WINGS, entity, "wing.close", 1F, 1F);
            return;
        }
        play(EquipmentSound.WINGS, entity, "wing.open", 1F, 1F);
        play(EquipmentSound.WINGS, entity, "whoosh", 0.5F, 1.2F);
        play(EquipmentSound.WINGS, entity, "zap", 0.5F, 1.2F);
    }
}
