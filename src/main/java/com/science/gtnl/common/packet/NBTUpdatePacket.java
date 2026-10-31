package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class NBTUpdatePacket extends ServerboundPacket {

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
    protected void read(ByteBuf buf) {
        this.slot = buf.readInt();
        this.itemId = ByteBufUtils.readUTF8String(buf);
        this.damage = buf.readInt();
        this.tag = ByteBufUtils.readTag(buf);
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(this.slot);
        ByteBufUtils.writeUTF8String(buf, this.itemId == null ? "" : this.itemId);
        buf.writeInt(this.damage);
        ByteBufUtils.writeTag(buf, this.tag);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (slot < 0 || slot >= player.inventory.getSizeInventory()) return;

            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (stack == null) return;

            String actualId = Item.itemRegistry.getNameForObject(stack.getItem());
            int actualDamage = stack.getItemDamage();

            if (!actualId.equals(itemId) || actualDamage != damage) return;

            stack.setTagCompound(tag);
        });
    }
}
