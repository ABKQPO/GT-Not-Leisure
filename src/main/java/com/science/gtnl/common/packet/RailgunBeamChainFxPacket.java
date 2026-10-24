package com.science.gtnl.common.packet;

import java.util.List;

import net.minecraft.client.Minecraft;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.railgun.RailgunClientFx;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.railgun.RailgunNet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunBeamChainFxPacket extends ClientboundPacket {

    public int shooterId;
    public Vec3d origin;
    public List<Vec3d> path;
    public boolean ehv;
    private boolean valid;

    public RailgunBeamChainFxPacket() {}

    public RailgunBeamChainFxPacket(int shooterId, Vec3d origin, List<Vec3d> path, boolean ehv) {
        this.shooterId = shooterId;
        this.origin = origin;
        this.path = path;
        this.ehv = ehv;
    }

    @Override
    protected void read(ByteBuf buffer) {
        try {
            shooterId = buffer.readInt();
            origin = Vec3d.read(buffer);
            path = RailgunNet.readPath(buffer);
            ehv = buffer.readBoolean();
            valid = true;
        } catch (RuntimeException ignored) {
            valid = false;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(shooterId);
        origin.write(buffer);
        RailgunNet.writePath(buffer, path);
        buffer.writeBoolean(ehv);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (valid) runInWorld(minecraft, () -> RailgunClientFx.playChain(this));
    }
}
