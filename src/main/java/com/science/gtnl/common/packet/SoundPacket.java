package com.science.gtnl.common.packet;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.packet.client.SoundHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class SoundPacket extends ClientboundPacket {

    public ResourceLocation soundResource;
    public float volume;
    public float pitch;
    public long seekMs;
    public static Map<String, SoundInfo> soundsToSync;
    public boolean syncPacket;
    public boolean stopAll;

    public SoundPacket() {
        this.stopAll = true;
        this.syncPacket = false;
        this.soundResource = null;
        this.volume = 0;
        this.pitch = 0;
        this.seekMs = 0;
    }

    public SoundPacket(ResourceLocation soundResource, float volume, float pitch, long seekMs) {
        this.syncPacket = false;
        this.soundResource = soundResource;
        this.volume = volume;
        this.pitch = pitch;
        this.seekMs = seekMs;
    }

    public SoundPacket(boolean sync) {
        this.syncPacket = sync;
        this.soundResource = null;
        this.volume = 0;
        this.pitch = 0;
        this.seekMs = 0;
    }

    @Override
    protected void read(ByteBuf buf) {
        boolean isStopAll = buf.readBoolean();
        boolean isSyncPacket = buf.readBoolean();
        if (isStopAll) {
            stopAll = true;
            soundResource = null;
            volume = 0;
            pitch = 0;
            seekMs = 0;
            if (soundsToSync != null) {
                soundsToSync.clear();
            }
            return;
        }
        if (isSyncPacket) {
            int mapSize = buf.readInt();
            soundsToSync = new Object2ObjectOpenHashMap<>(mapSize);
            for (int i = 0; i < mapSize; i++) {
                String key = ByteBufUtils.readUTF8String(buf);
                String resourceLocationString = ByteBufUtils.readUTF8String(buf);
                ResourceLocation resource = new ResourceLocation(resourceLocationString);
                float vol = buf.readFloat();
                float ptch = buf.readFloat();
                long seek = buf.readLong();
                soundsToSync.put(key, new SoundInfo(resource, vol, ptch, seek));
            }
            soundResource = null;
            volume = 0;
            pitch = 0;
            seekMs = 0;
        } else {
            soundResource = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
            volume = buf.readFloat();
            pitch = buf.readFloat();
            seekMs = buf.readLong();
        }
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeBoolean(stopAll);
        if (stopAll) {
            buf.writeBoolean(false);
            if (soundsToSync != null) soundsToSync.clear();
            return;
        }
        buf.writeBoolean(syncPacket);
        if (syncPacket && soundsToSync != null) {
            buf.writeInt(soundsToSync.size());
            for (Map.Entry<String, SoundInfo> entry : soundsToSync.entrySet()) {
                ByteBufUtils.writeUTF8String(buf, entry.getKey());
                ByteBufUtils.writeUTF8String(buf, entry.getValue().resourceLocation.toString());
                buf.writeFloat(entry.getValue().volume);
                buf.writeFloat(entry.getValue().pitch);
                buf.writeLong(entry.getValue().seekMs);
            }
        } else {
            if (soundResource != null) {
                ByteBufUtils.writeUTF8String(buf, soundResource.toString());
            } else {
                ByteBufUtils.writeUTF8String(buf, "");
            }
            buf.writeFloat(volume);
            buf.writeFloat(pitch);
            buf.writeLong(seekMs);
        }
    }

    @Override
    public void handleClient(Minecraft minecraft) {
        SoundHandler.handleSoundPacket(this);
    }

    @Desugar
    public record SoundInfo(ResourceLocation resourceLocation, float volume, float pitch, long seekMs) {

    }
}
