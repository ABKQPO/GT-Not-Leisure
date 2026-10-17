package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.railgun.RailgunBeamRenderer;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.railgun.RailgunDefaults;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunBeamUpdatePacket extends ClientboundPacket {

    public static final byte STATE_STOP = 0;
    public static final byte STATE_HOLD = 1;
    public static final byte STATE_MOVE = 2;

    public int shooterId;
    public byte state;
    public boolean ehv;
    public float range;
    private boolean valid;

    public RailgunBeamUpdatePacket() {}

    private RailgunBeamUpdatePacket(int shooterId, byte state, boolean ehv, float range) {
        this.shooterId = shooterId;
        this.state = state;
        this.ehv = ehv;
        this.range = range;
    }

    public static RailgunBeamUpdatePacket stop(int shooterId) {
        return new RailgunBeamUpdatePacket(shooterId, STATE_STOP, false, 0);
    }

    public static RailgunBeamUpdatePacket hold(int shooterId, boolean ehv) {
        return new RailgunBeamUpdatePacket(shooterId, STATE_HOLD, ehv, 0);
    }

    public static RailgunBeamUpdatePacket move(int shooterId, double range, boolean ehv) {
        return new RailgunBeamUpdatePacket(shooterId, STATE_MOVE, ehv, (float) range);
    }

    public double range() {
        return range;
    }

    @Override
    protected void read(ByteBuf buffer) {
        try {
            shooterId = buffer.readInt();
            state = buffer.readByte();
            ehv = buffer.readBoolean();
            valid = state >= STATE_STOP && state <= STATE_MOVE;
            if (!valid || state != STATE_MOVE) return;
            range = buffer.readFloat();
            valid = Float.isFinite(range) && range > 0 && range <= RailgunDefaults.BEAM_RANGE_LIMIT;
        } catch (RuntimeException ignored) {
            valid = false;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(shooterId);
        buffer.writeByte(state);
        buffer.writeBoolean(ehv);
        if (state != STATE_MOVE) return;
        buffer.writeFloat(range);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (valid) RailgunBeamRenderer.submit(this);
    }
}
