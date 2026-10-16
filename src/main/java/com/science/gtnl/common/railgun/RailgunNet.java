package com.science.gtnl.common.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import io.netty.buffer.ByteBuf;

public final class RailgunNet {

    public static final int MAX_PATH = 256;

    private RailgunNet() {}

    public static void sendAround(World world, Entity entity, ClientboundPacket packet) {
        ScienceNotLeisure.network.sendToAllAround(
            packet,
            new TargetPoint(world.provider.dimensionId, entity.posX, entity.posY, entity.posZ, 256));
    }

    public static void writeVec(ByteBuf buffer, RailgunVec vector) {
        buffer.writeDouble(vector.x());
        buffer.writeDouble(vector.y());
        buffer.writeDouble(vector.z());
    }

    public static RailgunVec readVec(ByteBuf buffer) {
        RailgunVec vector = new RailgunVec(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        if (!vector.finite()) throw new IllegalArgumentException("Non-finite railgun vector");
        return vector;
    }

    public static void writePath(ByteBuf buffer, List<RailgunVec> path) {
        int size = Math.min(MAX_PATH, path.size()) & ~1;
        buffer.writeShort(size);
        for (int i = 0; i < size; i++) writeVec(buffer, path.get(i));
    }

    public static List<RailgunVec> readPath(ByteBuf buffer) {
        int size = buffer.readUnsignedShort();
        if (size > MAX_PATH || (size & 1) != 0 || size * 24 > buffer.readableBytes())
            throw new IllegalArgumentException("Invalid railgun path length");
        List<RailgunVec> path = new ArrayList<>(size);
        for (int i = 0; i < size; i++) path.add(readVec(buffer));
        return path;
    }

    public static List<RailgunVec> path(RailgunVec origin, List<RailgunChainResolver.Hit> hits) {
        List<RailgunVec> path = new ArrayList<>();
        RailgunVec previous = origin;
        for (var hit : hits) {
            if (hit.penetration() || hit.pulse()) continue;
            RailgunVec current = RailgunVec.center(hit.target());
            if (hit.chainStartAt() != null) previous = hit.chainStartAt();
            if (hit.chainPropagation() && path.size() < MAX_PATH) {
                path.add(previous);
                path.add(current);
            }
            previous = current;
        }
        return path;
    }
}
