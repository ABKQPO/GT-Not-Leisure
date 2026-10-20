package com.science.gtnl.common.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;

import com.science.gtnl.common.equipment.EquipmentNet;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import io.netty.buffer.ByteBuf;

public class RailgunNet {

    public static final int MAX_PATH = 256;
    private static final double FX_RANGE = 256;

    private RailgunNet() {}

    public static void sendAround(Entity entity, ClientboundPacket packet) {
        EquipmentNet.sendAround(entity, packet, FX_RANGE);
    }

    public static void writePath(ByteBuf buffer, List<EquipmentVec> path) {
        int size = Math.min(MAX_PATH, path.size()) & ~1;
        buffer.writeShort(size);
        for (int i = 0; i < size; i++) path.get(i)
            .write(buffer);
    }

    public static List<EquipmentVec> readPath(ByteBuf buffer) {
        int size = buffer.readUnsignedShort();
        if (size > MAX_PATH || (size & 1) != 0 || size * 24 > buffer.readableBytes())
            throw new IllegalArgumentException("Invalid railgun path length");
        List<EquipmentVec> path = new ArrayList<>(size);
        for (int i = 0; i < size; i++) path.add(EquipmentVec.read(buffer));
        return path;
    }

    public static List<EquipmentVec> path(EquipmentVec origin, List<RailgunChainResolver.Hit> hits) {
        List<EquipmentVec> path = new ArrayList<>();
        EquipmentVec previous = origin;
        for (var hit : hits) {
            if (hit.kind() != RailgunChainResolver.Kind.PRIMARY && hit.kind() != RailgunChainResolver.Kind.CHAIN)
                continue;
            EquipmentVec current = EquipmentVec.center(hit.target());
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
