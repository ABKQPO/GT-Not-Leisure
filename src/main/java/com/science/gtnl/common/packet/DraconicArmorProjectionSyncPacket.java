package com.science.gtnl.common.packet;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionState;
import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionType;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class DraconicArmorProjectionSyncPacket
    implements IMessage, IMessageHandler<DraconicArmorProjectionSyncPacket, IMessage> {

    private UUID playerId;
    private String projectionTypeId;

    public DraconicArmorProjectionSyncPacket() {}

    public DraconicArmorProjectionSyncPacket(EntityPlayer player, DraconicArmorProjectionType projectionType) {
        playerId = player.getUniqueID();
        projectionTypeId = projectionType == null ? "" : projectionType.getId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerId = new UUID(buf.readLong(), buf.readLong());
        projectionTypeId = readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(playerId.getMostSignificantBits());
        buf.writeLong(playerId.getLeastSignificantBits());
        writeString(buf, projectionTypeId);
    }

    private static void writeString(ByteBuf buf, String value) {
        String safeValue = value == null ? "" : value;
        buf.writeInt(safeValue.length());
        for (int i = 0; i < safeValue.length(); i++) {
            buf.writeChar(safeValue.charAt(i));
        }
    }

    private static String readString(ByteBuf buf) {
        int length = buf.readInt();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(buf.readChar());
        }
        return builder.toString();
    }

    @Override
    public IMessage onMessage(DraconicArmorProjectionSyncPacket message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            onClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void onClient(DraconicArmorProjectionSyncPacket message) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) {
            return;
        }

        EntityPlayer player = world.func_152378_a(message.playerId);
        if (player == null) {
            DraconicArmorProjectionState.clear(message.playerId);
            return;
        }

        if (message.projectionTypeId.isEmpty()) {
            DraconicArmorProjectionState.clear(player);
            return;
        }

        for (DraconicArmorProjectionType type : DraconicArmorProjectionType.values()) {
            if (type.getId()
                .equals(message.projectionTypeId)) {
                DraconicArmorProjectionState.set(player, type);
                break;
            }
        }
    }
}
