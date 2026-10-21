package com.science.gtnl.common.packet;

import java.util.List;

import net.minecraft.client.Minecraft;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.railgun.RailgunClientFx;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunNet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunFireFxPacket extends ClientboundPacket {

    private static final float MAX_IMPACT_RADIUS = 32;

    public int shooterId;
    public Vec3d from, firstHit;
    public List<Vec3d> chainPath;
    public byte tier;
    public float impactRadius;
    private boolean valid;

    public RailgunFireFxPacket() {}

    public RailgunFireFxPacket(int shooterId, Vec3d from, Vec3d firstHit, List<Vec3d> chainPath, int tier,
        float impactRadius) {
        this.shooterId = shooterId;
        this.from = from;
        this.firstHit = firstHit;
        this.chainPath = chainPath;
        this.tier = (byte) tier;
        this.impactRadius = impactRadius;
    }

    @Override
    protected void read(ByteBuf buffer) {
        try {
            shooterId = buffer.readInt();
            from = Vec3d.read(buffer);
            firstHit = Vec3d.read(buffer);
            chainPath = RailgunNet.readPath(buffer);
            tier = buffer.readByte();
            impactRadius = buffer.readFloat();
            valid = RailgunChargeTier.byOrdinal(tier) != RailgunChargeTier.NONE && Float.isFinite(impactRadius)
                && impactRadius >= 0
                && impactRadius <= MAX_IMPACT_RADIUS;
        } catch (RuntimeException ignored) {
            valid = false;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(shooterId);
        from.write(buffer);
        firstHit.write(buffer);
        RailgunNet.writePath(buffer, chainPath);
        buffer.writeByte(tier);
        buffer.writeFloat(impactRadius);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (valid) runInWorld(minecraft, () -> RailgunClientFx.playFire(this));
    }
}
