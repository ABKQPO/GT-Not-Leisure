package com.science.gtnl.common.packet;

import java.util.List;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.railgun.RailgunClientFx;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.railgun.RailgunNet;
import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunFireFxPacket extends ClientboundPacket {

    public int shooterId;
    public RailgunVec from, firstHit;
    public List<RailgunVec> chainPath;
    public byte tier;
    public float impactRadius;
    public boolean sound;
    private boolean valid;

    public RailgunFireFxPacket() {}

    public RailgunFireFxPacket(int shooterId, RailgunVec from, RailgunVec firstHit, List<RailgunVec> chainPath,
        int tier, float impactRadius, boolean sound) {
        this.shooterId = shooterId;
        this.from = from;
        this.firstHit = firstHit;
        this.chainPath = chainPath;
        this.tier = (byte) tier;
        this.impactRadius = impactRadius;
        this.sound = sound;
    }

    @Override
    protected void read(ByteBuf buffer) {
        try {
            shooterId = buffer.readInt();
            from = RailgunNet.readVec(buffer);
            firstHit = RailgunNet.readVec(buffer);
            chainPath = RailgunNet.readPath(buffer);
            tier = buffer.readByte();
            impactRadius = buffer.readFloat();
            sound = buffer.readBoolean();
            valid = tier >= 1 && tier <= 3 && Float.isFinite(impactRadius) && impactRadius >= 0 && impactRadius <= 32;
        } catch (RuntimeException ignored) {
            valid = false;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(shooterId);
        RailgunNet.writeVec(buffer, from);
        RailgunNet.writeVec(buffer, firstHit);
        RailgunNet.writePath(buffer, chainPath);
        buffer.writeByte(tier);
        buffer.writeFloat(impactRadius);
        buffer.writeBoolean(sound);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        var world = minecraft.theWorld;
        if (valid && world != null)
            minecraft.func_152344_a(() -> { if (minecraft.theWorld == world) RailgunClientFx.playFire(this); });
    }
}
