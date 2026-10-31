package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;

import com.gtnllib.network.ClientboundPacket;
import com.gtnllib.utils.Vec3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class CelestweaveFireballPacket extends ClientboundPacket {

    private int entityId;
    private Vec3d acceleration;

    public CelestweaveFireballPacket() {}

    public CelestweaveFireballPacket(EntityFireball fireball) {
        entityId = fireball.getEntityId();
        acceleration = new Vec3d(fireball.accelerationX, fireball.accelerationY, fireball.accelerationZ);
    }

    @Override
    protected void read(ByteBuf buffer) {
        entityId = buffer.readInt();
        try {
            acceleration = Vec3d.read(buffer);
        } catch (IllegalArgumentException invalid) {
            acceleration = null;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(entityId);
        acceleration.write(buffer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        runInWorld(minecraft, () -> apply(minecraft.theWorld.getEntityByID(entityId)));
    }

    private void apply(Entity entity) {
        if (acceleration == null || !(entity instanceof EntityFireball fireball)) return;
        fireball.accelerationX = acceleration.x();
        fireball.accelerationY = acceleration.y();
        fireball.accelerationZ = acceleration.z();
    }
}
