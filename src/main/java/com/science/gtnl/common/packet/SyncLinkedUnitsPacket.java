package com.science.gtnl.common.packet;

import com.science.gtnl.api.mixinHelper.LinkedEyeOfHarmonyUnit;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class SyncLinkedUnitsPacket implements IMessage {

    public int x,y,z;
    public List<LinkedEyeOfHarmonyUnit> units;

    public SyncLinkedUnitsPacket() {
    }

    public SyncLinkedUnitsPacket(int x, int y, int z, List<LinkedEyeOfHarmonyUnit> units) {
        this.units = units;
        this.x =x;
        this.y =y;
        this.z =z;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagList list = new NBTTagList();
        for (LinkedEyeOfHarmonyUnit unit : units) {
            try {
                list.appendTag(unit.writeLinkDataToNBT());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NBTTagCompound wrapper = new NBTTagCompound();
        wrapper.setTag("units", list);
        ByteBufUtils.writeTag(buf, wrapper);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound wrapper = ByteBufUtils.readTag(buf);
        this.units = new ArrayList<>();

        if (wrapper != null && wrapper.hasKey("units", Constants.NBT.TAG_LIST)) {
            NBTTagList list = wrapper.getTagList("units", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                try {
                    this.units.add(new LinkedEyeOfHarmonyUnit(tag));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Handler implements IMessageHandler<SyncLinkedUnitsPacket, IMessage> {

        @Override
        public IMessage onMessage(SyncLinkedUnitsPacket message, MessageContext ctx) {
            ClientSyncLinkedUnitsHandler.apply(message.x, message.y, message.z, message.units);
            return null;
        }
    }
}
