package com.science.gtnl.common.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import io.netty.buffer.ByteBuf;

public record EquipmentVec(double x, double y, double z) {

    public static final EquipmentVec ZERO = new EquipmentVec(0, 0, 0);
    private static final double PARALLEL_LENGTH = 1.0E-4, ZERO_LENGTH = 1.0E-8;

    public static EquipmentVec read(ByteBuf buffer) {
        EquipmentVec vector = new EquipmentVec(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        if (!vector.finite()) throw new IllegalArgumentException("Non-finite equipment vector");
        return vector;
    }

    public void write(ByteBuf buffer) {
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
    }

    public static EquipmentVec of(Vec3 vector) {
        return new EquipmentVec(vector.xCoord, vector.yCoord, vector.zCoord);
    }

    public static EquipmentVec pos(Entity entity) {
        return new EquipmentVec(entity.posX, entity.posY, entity.posZ);
    }

    public static EquipmentVec eye(EntityPlayer player) {
        return pos(player).add(0, player.getEyeHeight(), 0);
    }

    public static EquipmentVec interpolated(Entity entity, float partialTicks) {
        return new EquipmentVec(
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks);
    }

    public static EquipmentVec eye(Entity entity, float partialTicks) {
        return interpolated(entity, partialTicks).add(0, entity.getEyeHeight(), 0);
    }

    public static EquipmentVec rightOf(EquipmentVec direction) {
        EquipmentVec right = direction.cross(new EquipmentVec(0, 1, 0));
        return right.length() < PARALLEL_LENGTH ? new EquipmentVec(1, 0, 0) : right.normalize();
    }

    public static EquipmentVec look(Entity entity) {
        return of(entity.getLookVec()).normalize();
    }

    public static EquipmentVec look(Entity entity, float partialTicks) {
        return entity instanceof EntityLivingBase living ? of(living.getLook(partialTicks)).normalize()
            : of(entity.getLookVec()).normalize();
    }

    public static EquipmentVec center(Entity entity) {
        AxisAlignedBB box = entity.boundingBox;
        return new EquipmentVec((box.minX + box.maxX) / 2, (box.minY + box.maxY) / 2, (box.minZ + box.maxZ) / 2);
    }

    public EquipmentVec add(double x, double y, double z) {
        return new EquipmentVec(this.x + x, this.y + y, this.z + z);
    }

    public EquipmentVec add(EquipmentVec other) {
        return add(other.x, other.y, other.z);
    }

    public EquipmentVec subtract(EquipmentVec other) {
        return add(-other.x, -other.y, -other.z);
    }

    public EquipmentVec scale(double scale) {
        return new EquipmentVec(x * scale, y * scale, z * scale);
    }

    public double dot(EquipmentVec other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public EquipmentVec cross(EquipmentVec other) {
        return new EquipmentVec(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public double length() {
        return Math.sqrt(dot(this));
    }

    public double distanceToSqr(EquipmentVec other) {
        EquipmentVec delta = subtract(other);
        return delta.dot(delta);
    }

    public EquipmentVec normalize() {
        double length = length();
        return length < ZERO_LENGTH ? ZERO : scale(1 / length);
    }

    public Vec3 toMc() {
        return Vec3.createVectorHelper(x, y, z);
    }

    public AxisAlignedBB boxTo(EquipmentVec to, double padding) {
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
