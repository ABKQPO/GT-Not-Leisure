package com.science.gtnl.common.packet;

import java.util.List;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.railgun.RailgunClientFx;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunNet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunFireFxPacket extends ClientboundPacket {

    public int shooterId;
    public EquipmentVec from, firstHit;
    public List<EquipmentVec> chainPath;
    public byte tier;
    public float impactRadius;
    public boolean sound;
    private boolean valid;

    public RailgunFireFxPacket() {}

    public RailgunFireFxPacket(int shooterId, EquipmentVec from, EquipmentVec firstHit, List<EquipmentVec> chainPath,
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
            from = EquipmentVec.read(buffer);
            firstHit = EquipmentVec.read(buffer);
            chainPath = RailgunNet.readPath(buffer);
            tier = buffer.readByte();
            impactRadius = buffer.readFloat();
            sound = buffer.readBoolean();
            valid = RailgunChargeTier.byOrdinal(tier) != RailgunChargeTier.NONE && Float.isFinite(impactRadius)
                && impactRadius >= 0
                && impactRadius <= 32;
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
        buffer.writeBoolean(sound);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (valid) runInWorld(minecraft, () -> RailgunClientFx.playFire(this));
    }
}
