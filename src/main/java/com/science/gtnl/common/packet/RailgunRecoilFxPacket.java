package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.railgun.RailgunCameraShake;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunRecoilFxPacket extends ClientboundPacket {

    private float pitchUp;
    private byte tier;

    public RailgunRecoilFxPacket() {}

    public RailgunRecoilFxPacket(float pitchUp, int tier) {
        this.pitchUp = pitchUp;
        this.tier = (byte) tier;
    }

    @Override
    protected void read(ByteBuf buffer) {
        pitchUp = buffer.readFloat();
        tier = buffer.readByte();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeFloat(pitchUp);
        buffer.writeByte(tier);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (Float.isFinite(pitchUp) && pitchUp >= 0 && pitchUp <= 30 && tier >= 1 && tier <= 3) {
            var world = minecraft.theWorld;
            minecraft.func_152344_a(
                () -> { if (world != null && minecraft.theWorld == world) RailgunCameraShake.recoil(pitchUp, tier); });
        }
    }
}
