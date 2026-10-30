package com.science.gtnl.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;

import com.science.gtnl.common.packet.SoundPacket;
import com.science.gtnl.common.packet.SoundPacket.SoundInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.client.ElectricJukeboxSound;

@SideOnly(Side.CLIENT)
public class SoundHandler {

    public static Map<String, ElectricJukeboxSound> PLAYING_SOUNDS = new ConcurrentHashMap<>();

    public static void handleSoundPacket(SoundPacket message) {
        if (message.stopAll) {
            for (ElectricJukeboxSound sound : PLAYING_SOUNDS.values()) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .stopSound(sound);
            }
            PLAYING_SOUNDS.clear();
            if (SoundPacket.soundsToSync != null) SoundPacket.soundsToSync.clear();
            return;
        }

        if (SoundPacket.soundsToSync != null) {
            for (ElectricJukeboxSound sound : PLAYING_SOUNDS.values()) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .stopSound(sound);
            }
            PLAYING_SOUNDS.clear();

            for (Map.Entry<String, SoundInfo> entry : SoundPacket.soundsToSync.entrySet()) {
                SoundInfo info = entry.getValue();
                ElectricJukeboxSound sound = new ElectricJukeboxSound(
                    info.resourceLocation(),
                    info.volume(),
                    info.seekMs(),
                    0,
                    0,
                    0);
                PLAYING_SOUNDS.put(entry.getKey(), sound);
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(sound);
            }
        } else {
            ElectricJukeboxSound sound = new ElectricJukeboxSound(
                message.soundResource,
                message.volume,
                message.seekMs,
                0,
                0,
                0);
            PLAYING_SOUNDS.put(message.soundResource.toString(), sound);
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(sound);
        }
    }
}
