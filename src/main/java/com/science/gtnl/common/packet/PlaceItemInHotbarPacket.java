package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.gtnllib.network.ClientboundPacket;
import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class PlaceItemInHotbarPacket extends ClientboundPacket {

    public ItemStack result;
    public boolean isCreative, useAE;

    public PlaceItemInHotbarPacket() {}

    public PlaceItemInHotbarPacket(ItemStack result, boolean isCreative, boolean useAE) {
        this.result = result;
        this.isCreative = isCreative;
        this.useAE = useAE;
    }

    @Override
    protected void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, result);
        buf.writeBoolean(isCreative);
        buf.writeBoolean(useAE);
    }

    @Override
    protected void read(ByteBuf buf) {
        this.result = ByteBufUtils.readItemStack(buf);
        this.isCreative = buf.readBoolean();
        this.useAE = buf.readBoolean();
    }

    @Override
    public void handleClient(Minecraft minecraft) {
        if (result != null) {
            ItemUtils.placeItemInHotbar(Minecraft.getMinecraft().thePlayer, result, isCreative, useAE);
        }
    }

}
