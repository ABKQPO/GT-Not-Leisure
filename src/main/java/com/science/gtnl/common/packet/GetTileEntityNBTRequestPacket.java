package com.science.gtnl.common.packet;

import static com.science.gtnl.ScienceNotLeisure.network;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

import com.gtnllib.network.ServerboundPacket;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class GetTileEntityNBTRequestPacket extends ServerboundPacket {

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
    protected void read(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.blockID = buf.readInt();
        this.blockMeta = buf.readInt();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.blockID);
        buf.writeInt(this.blockMeta);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        WorldServer world = player.getServerForPlayer();
        Block block = world.getBlock(x, y, z);
        if (block != null) {
            TileEntity tileentity = world.getTileEntity(x, y, z);
            if (tileentity != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                tileentity.writeToNBT(nbt);
                nbt.removeTag("x");
                nbt.removeTag("y");
                nbt.removeTag("z");

                network.sendTo(new TileEntityNBTPacket(blockID, blockMeta, nbt), player);
            }
        }
    }
}
