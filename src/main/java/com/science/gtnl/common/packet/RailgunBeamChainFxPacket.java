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

public class RailgunBeamChainFxPacket extends ClientboundPacket {

    public int shooterId;
    public RailgunVec origin;
    public List<RailgunVec> path;
    public boolean sound, ehv;
    private boolean valid;

    public RailgunBeamChainFxPacket() {}

    public RailgunBeamChainFxPacket(int shooterId, RailgunVec origin, List<RailgunVec> path, boolean sound,
        boolean ehv) {
        this.shooterId = shooterId;
        this.origin = origin;
        this.path = path;
        this.sound = sound;
        this.ehv = ehv;
    }

    @Override
    protected void read(ByteBuf buffer) {
        try {
            shooterId = buffer.readInt();
            origin = RailgunNet.readVec(buffer);
            path = RailgunNet.readPath(buffer);
            sound = buffer.readBoolean();
            ehv = buffer.readBoolean();
            valid = true;
        } catch (RuntimeException ignored) {
            valid = false;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(shooterId);
        RailgunNet.writeVec(buffer, origin);
        RailgunNet.writePath(buffer, path);
        buffer.writeBoolean(sound);
        buffer.writeBoolean(ehv);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        var connection = minecraft.getNetHandler();
        if (valid) minecraft
            .func_152344_a(() -> { if (minecraft.getNetHandler() == connection) RailgunClientFx.playChain(this); });
    }
}
