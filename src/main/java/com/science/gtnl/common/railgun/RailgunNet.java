package com.science.gtnl.common.railgun;

import java.util.List;

import net.minecraft.entity.Entity;

import com.gtnllib.network.ClientboundPacket;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.equipment.EquipmentNet;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RailgunNet {

    public static final int MAX_PATH = 256;
    private static final double FX_RANGE = 256;
    private static final int POINT_BYTES = Double.BYTES * 3;

    public static void sendAround(Entity entity, ClientboundPacket packet) {
        EquipmentNet.sendAround(entity, packet, FX_RANGE);
    }

    public static void writePath(ByteBuf buffer, List<Vec3d> path) {
        int size = Math.min(MAX_PATH, path.size()) & ~1;
        buffer.writeShort(size);
        for (int i = 0; i < size; i++) path.get(i)
            .write(buffer);
    }

    public static List<Vec3d> readPath(ByteBuf buffer) {
        int size = buffer.readUnsignedShort();
        if (size > MAX_PATH || (size & 1) != 0 || size * POINT_BYTES > buffer.readableBytes())
            throw new IllegalArgumentException("Invalid railgun path length");
        List<Vec3d> path = new ObjectArrayList<>(size);
        for (int i = 0; i < size; i++) path.add(Vec3d.read(buffer));
        return path;
    }

    public static List<Vec3d> path(Vec3d origin, List<RailgunChainResolver.Hit> hits) {
        List<Vec3d> path = new ObjectArrayList<>();
        Vec3d previous = origin;
        for (var hit : hits) {
            if (hit.kind() != RailgunChainResolver.Kind.PRIMARY && hit.kind() != RailgunChainResolver.Kind.CHAIN)
                continue;
            Vec3d current = Vec3d.center(hit.target());
            if (hit.chainStartAt() != null) previous = hit.chainStartAt();
            if (hit.kind() == RailgunChainResolver.Kind.CHAIN && path.size() < MAX_PATH) {
                path.add(previous);
                path.add(current);
            }
            previous = current;
        }
        return path;
    }
}
