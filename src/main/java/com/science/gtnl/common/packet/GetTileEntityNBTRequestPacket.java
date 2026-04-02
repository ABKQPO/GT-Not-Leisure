package com.science.gtnl.common.packet;

import static com.science.gtnl.ScienceNotLeisure.network;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class GetTileEntityNBTRequestPacket
    implements IMessage, IMessageHandler<GetTileEntityNBTRequestPacket, IMessage> {

    public int x, y, z, blockID, blockMeta;

    public GetTileEntityNBTRequestPacket() {}

    public GetTileEntityNBTRequestPacket(int x, int y, int z, int blockID, int blockMeta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockID = blockID;
        this.blockMeta = blockMeta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.blockID = buf.readInt();
        this.blockMeta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.blockID);
        buf.writeInt(this.blockMeta);
    }

    @Override
    public IMessage onMessage(GetTileEntityNBTRequestPacket message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        WorldServer world = player.getServerForPlayer();
        int x = message.getX();
        int y = message.getY();
        int z = message.getZ();
        Block block = world.getBlock(x, y, z);
        if (block != null) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if (tileentity != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                try {
                    tileentity.writeToNBT(nbt);
                    nbt.removeTag("x");
                    nbt.removeTag("y");
                    nbt.removeTag("z");

                    network.sendTo(new TileEntityNBTPacket(message.getBlockID(), message.getBlockMeta(), nbt), player);

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
