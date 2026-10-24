package com.science.gtnl.common.yogsothoth;

import java.util.UUID;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.yogsothoth.YogTerminalClient;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

public class YogTerminalStatePacket extends ClientboundPacket {

    private static final int ENDED = 0, OPENING = 1, USE = 2;

    private int kind;
    private IntOpenHashSet locked = new IntOpenHashSet();
    private UUID record;
    private int index;
    private boolean sneak;

    public YogTerminalStatePacket() {}

    private YogTerminalStatePacket(int kind, IntSet locked, UUID record, int index, boolean sneak) {
        this.kind = kind;
        this.locked = new IntOpenHashSet(locked);
        this.record = record;
        this.index = index;
        this.sneak = sneak;
    }

    static YogTerminalStatePacket ended() {
        return new YogTerminalStatePacket(ENDED, IntSets.emptySet(), null, -1, false);
    }

    static YogTerminalStatePacket opening(IntSet locked, UUID record, int index) {
        return new YogTerminalStatePacket(OPENING, locked, record, index, false);
    }

    static YogTerminalStatePacket use(IntSet locked, UUID record, int index, boolean sneak) {
        return new YogTerminalStatePacket(USE, locked, record, index, sneak);
    }

    @Override
    protected void read(ByteBuf buf) {
        kind = buf.readUnsignedByte();
        int count = buf.readUnsignedByte();
        for (int slot = 0; slot < count; slot++) locked.add(buf.readByte());
        if (kind == ENDED) return;
        record = new UUID(buf.readLong(), buf.readLong());
        index = buf.readInt();
        sneak = buf.readBoolean();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeByte(kind);
        buf.writeByte(locked.size());
        for (IntIterator it = locked.iterator(); it.hasNext();) buf.writeByte(it.nextInt());
        if (kind == ENDED) return;
        buf.writeLong(record.getMostSignificantBits());
        buf.writeLong(record.getLeastSignificantBits());
        buf.writeInt(index);
        buf.writeBoolean(sneak);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        // Set before the screen-open packet that follows is handled, which may not wait for the main thread.
        YogTerminalLocks.setClient(locked);
        YogTerminalClient.session(kind == ENDED ? null : record, index);
        if (kind == USE) minecraft.func_152344_a(() -> YogTerminalClient.useReady(record, index, sneak));
    }
}
