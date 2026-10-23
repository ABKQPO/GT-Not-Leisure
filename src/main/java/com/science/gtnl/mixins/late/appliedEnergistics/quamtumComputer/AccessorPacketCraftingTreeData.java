package com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import appeng.core.sync.packets.PacketCraftingTreeData;
import io.netty.buffer.ByteBuf;

@Mixin(value = PacketCraftingTreeData.class, remap = false)
public interface AccessorPacketCraftingTreeData {

    @Invoker("<init>")
    static PacketCraftingTreeData gtnl$create(ByteBuf chunkData, int chunkId, int totalChunks) {
        throw new AssertionError();
    }
}
