package com.science.gtnl.common.railgun;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public record RailgunVec(double x, double y, double z) {

    public static final RailgunVec ZERO = new RailgunVec(0, 0, 0);

    public static RailgunVec of(Vec3 vector) {
        return new RailgunVec(vector.xCoord, vector.yCoord, vector.zCoord);
    }

    public static RailgunVec pos(Entity entity) {
        return new RailgunVec(entity.posX, entity.posY, entity.posZ);
    }

    public static RailgunVec eye(EntityPlayer player) {
        return pos(player).add(0, player.getEyeHeight(), 0);
    }

    public static RailgunVec interpolated(Entity entity, float partialTicks) {
        return new RailgunVec(
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks);
    }

    public static RailgunVec eye(Entity entity, float partialTicks) {
        return interpolated(entity, partialTicks).add(0, entity.getEyeHeight(), 0);
    }

    public static RailgunVec rightOf(RailgunVec direction) {
        RailgunVec right = direction.cross(new RailgunVec(0, 1, 0));
        return right.length() < 1.0E-4 ? new RailgunVec(1, 0, 0) : right.normalize();
    }

    public static RailgunVec look(Entity entity) {
        return of(entity.getLookVec()).normalize();
    }

    public static RailgunVec look(Entity entity, float partialTicks) {
        return entity instanceof EntityLivingBase living ? of(living.getLook(partialTicks)).normalize()
            : of(entity.getLookVec()).normalize();
    }

    public static RailgunVec center(Entity entity) {
        AxisAlignedBB box = entity.boundingBox;
        return new RailgunVec((box.minX + box.maxX) / 2, (box.minY + box.maxY) / 2, (box.minZ + box.maxZ) / 2);
    }

    public RailgunVec add(double x, double y, double z) {
        return new RailgunVec(this.x + x, this.y + y, this.z + z);
    }

    public RailgunVec add(RailgunVec other) {
        return add(other.x, other.y, other.z);
    }

    public RailgunVec subtract(RailgunVec other) {
        return add(-other.x, -other.y, -other.z);
    }

    public RailgunVec scale(double scale) {
        return new RailgunVec(x * scale, y * scale, z * scale);
    }

    public double dot(RailgunVec other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public RailgunVec cross(RailgunVec other) {
        return new RailgunVec(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public double length() {
        return Math.sqrt(dot(this));
    }

    public double distanceToSqr(RailgunVec other) {
        RailgunVec delta = subtract(other);
        return delta.dot(delta);
    }

    public RailgunVec normalize() {
        double length = length();
        return length < 1.0E-8 ? ZERO : scale(1 / length);
    }

    public Vec3 toMc() {
        return Vec3.createVectorHelper(x, y, z);
    }

    public AxisAlignedBB boxTo(RailgunVec to, double padding) {
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
