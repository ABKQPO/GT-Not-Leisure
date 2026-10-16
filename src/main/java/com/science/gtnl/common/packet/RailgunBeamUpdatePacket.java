package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.railgun.RailgunBeamRenderer;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.railgun.RailgunNet;
import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunBeamUpdatePacket extends ClientboundPacket {

    public int shooterId;
    public RailgunVec from, to;
    public boolean active, ehv;
    private boolean valid;

    public RailgunBeamUpdatePacket() {}

    public RailgunBeamUpdatePacket(int shooterId, RailgunVec from, RailgunVec to, boolean active, boolean ehv) {
        this.shooterId = shooterId;
        this.from = from;
        this.to = to;
        this.active = active;
        this.ehv = ehv;
    }

    @Override
    protected void read(ByteBuf buffer) {
        try {
            shooterId = buffer.readInt();
            from = RailgunNet.readVec(buffer);
            to = RailgunNet.readVec(buffer);
            active = buffer.readBoolean();
            ehv = buffer.readBoolean();
            valid = from.distanceToSqr(to) <= 512 * 512;
        } catch (RuntimeException ignored) {
            valid = false;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(shooterId);
        RailgunNet.writeVec(buffer, from);
        RailgunNet.writeVec(buffer, to);
        buffer.writeBoolean(active);
        buffer.writeBoolean(ehv);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        var connection = minecraft.getNetHandler();
        if (valid) minecraft
            .func_152344_a(() -> { if (minecraft.getNetHandler() == connection) RailgunBeamRenderer.update(this); });
    }
}
