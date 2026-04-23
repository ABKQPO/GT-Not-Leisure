package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class NBTUpdatePacket implements IMessage, IMessageHandler<NBTUpdatePacket, IMessage> {

    private int slot;
    private String itemId;
    private int damage;
    private NBTTagCompound tag;

    public NBTUpdatePacket() {}

    public NBTUpdatePacket(int slot, ItemStack stack) {
        this.slot = slot;
        this.itemId = Item.itemRegistry.getNameForObject(stack.getItem());
        this.damage = stack.getItemDamage();
        this.tag = stack.getTagCompound() == null ? null
            : (NBTTagCompound) stack.getTagCompound()
                .copy();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slot = buf.readInt();
        this.itemId = ByteBufUtils.readUTF8String(buf);
        this.damage = buf.readInt();
        this.tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.slot);
        ByteBufUtils.writeUTF8String(buf, this.itemId == null ? "" : this.itemId);
        buf.writeInt(this.damage);
        ByteBufUtils.writeTag(buf, this.tag);
    }

    @Override
    public IMessage onMessage(NBTUpdatePacket message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        ServerThreadUtil.addScheduledTask(() -> {
            if (message.slot < 0 || message.slot >= player.inventory.getSizeInventory()) return;

            ItemStack stack = player.inventory.getStackInSlot(message.slot);
            if (stack == null) return;

            String actualId = Item.itemRegistry.getNameForObject(stack.getItem());
            int actualDamage = stack.getItemDamage();

            if (!actualId.equals(message.itemId) || actualDamage != message.damage) return;

            stack.setTagCompound(message.tag);
        });
        return null;
    }
}
