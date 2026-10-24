package com.gtnllib.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import io.netty.buffer.ByteBuf;

public record Vec3d(double x, double y, double z) {

    public static final Vec3d ZERO = new Vec3d(0, 0, 0);
    private static final double PARALLEL_LENGTH = 1.0E-4, ZERO_LENGTH = 1.0E-8;

    public static Vec3d read(ByteBuf buffer) {
        Vec3d vector = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        if (!vector.finite()) throw new IllegalArgumentException("Non-finite vector");
        return vector;
    }

    public void write(ByteBuf buffer) {
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
    }

    public static Vec3d of(Vec3 vector) {
        return new Vec3d(vector.xCoord, vector.yCoord, vector.zCoord);
    }

    public static Vec3d of(ForgeDirection direction) {
        return new Vec3d(direction.offsetX, direction.offsetY, direction.offsetZ);
    }

    public static Vec3d pos(Entity entity) {
        return new Vec3d(entity.posX, entity.posY, entity.posZ);
    }

    public static Vec3d eye(EntityPlayer player) {
        return pos(player).add(0, player.getEyeHeight(), 0);
    }

    public static Vec3d interpolated(Entity entity, float partialTicks) {
        return new Vec3d(
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks);
    }

    public static Vec3d eye(Entity entity, float partialTicks) {
        return interpolated(entity, partialTicks).add(0, entity.getEyeHeight(), 0);
    }

    public static Vec3d rightOf(Vec3d direction) {
        Vec3d right = direction.cross(new Vec3d(0, 1, 0));
        return right.length() < PARALLEL_LENGTH ? new Vec3d(1, 0, 0) : right.normalize();
    }

    public static Vec3d look(Entity entity) {
        return of(entity.getLookVec()).normalize();
    }

    public static Vec3d look(Entity entity, float partialTicks) {
        return entity instanceof EntityLivingBase living ? of(living.getLook(partialTicks)).normalize()
            : of(entity.getLookVec()).normalize();
    }

    public static Vec3d center(Entity entity) {
        AxisAlignedBB box = entity.boundingBox;
        return new Vec3d((box.minX + box.maxX) / 2, (box.minY + box.maxY) / 2, (box.minZ + box.maxZ) / 2);
    }

    public Vec3d add(double x, double y, double z) {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }

    public Vec3d add(Vec3d other) {
        return add(other.x, other.y, other.z);
    }

    public Vec3d subtract(Vec3d other) {
        return add(-other.x, -other.y, -other.z);
    }

    public Vec3d scale(double scale) {
        return new Vec3d(x * scale, y * scale, z * scale);
    }

    public double dot(Vec3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3d cross(Vec3d other) {
        return new Vec3d(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public double length() {
        return Math.sqrt(dot(this));
    }

    public double distanceToSqr(Vec3d other) {
        Vec3d delta = subtract(other);
        return delta.dot(delta);
    }

    public Vec3d normalize() {
        double length = length();
        return length < ZERO_LENGTH ? ZERO : scale(1 / length);
    }

    public Vec3 toMc() {
        return Vec3.createVectorHelper(x, y, z);
    }

    public AxisAlignedBB boxTo(Vec3d to, double padding) {
        return AxisAlignedBB.getBoundingBox(
            Math.min(x, to.x) - padding,
            Math.min(y, to.y) - padding,
            Math.min(z, to.z) - padding,
            Math.max(x, to.x) + padding,
            Math.max(y, to.y) + padding,
            Math.max(z, to.z) + padding);
    }

    public boolean finite() {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }
}
