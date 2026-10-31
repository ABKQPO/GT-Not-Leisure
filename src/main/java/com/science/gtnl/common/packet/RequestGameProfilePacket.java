package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class RequestGameProfilePacket extends ServerboundPacket {

    public String playerName;
    public boolean isCreative, useAE;

    public RequestGameProfilePacket() {}

    public RequestGameProfilePacket(String playerName, boolean isCreative, boolean useAE) {
        this.playerName = playerName;
        this.isCreative = isCreative;
        this.useAE = useAE;
    }

    @Override
    protected void write(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
        buf.writeBoolean(isCreative);
        buf.writeBoolean(useAE);
    }

    @Override
    protected void read(ByteBuf buf) {
        this.playerName = ByteBufUtils.readUTF8String(buf);
        this.isCreative = buf.readBoolean();
        this.useAE = buf.readBoolean();
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ScienceNotLeisure.network
            .sendTo(new PlaceItemInHotbarPacket(ItemUtils.getPlayerSkull(playerName), isCreative, useAE), player);
    }
}
