package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ClientboundPacket;
import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class MEChiselSyncParallel extends ServerboundPacket {

    private int x;
    private int y;
    private int z;
    private int parallel;

    public MEChiselSyncParallel() {

    }

    public MEChiselSyncParallel(TileEntityMEChisel te) {
        x = te.xCoord;
        y = te.yCoord;
        z = te.zCoord;
        parallel = te.getParallel();
    }

    @Override
    protected void read(ByteBuf buf) {
        parallel = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(parallel);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        var world = player.worldObj;
        ServerThreadUtil.addScheduledTask(() -> {
            if (world.getTileEntity(x, y, z) instanceof TileEntityMEChisel te) {
                te.setParallel(parallel);
            }
        });
    }

    public static class Clientbound extends ClientboundPacket {

        private int x;
        private int y;
        private int z;
        private int parallel;

        public Clientbound() {

        }

        public Clientbound(TileEntityMEChisel te) {
            x = te.xCoord;
            y = te.yCoord;
            z = te.zCoord;
            parallel = te.getParallel();
        }

        @Override
        protected void read(ByteBuf buf) {
            parallel = buf.readInt();
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
        }

        @Override
        protected void write(ByteBuf buf) {
            buf.writeInt(parallel);
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void handleClient(Minecraft minecraft) {
            onClient(minecraft);
        }

        @SideOnly(Side.CLIENT)
        private void onClient(Minecraft minecraft) {
            var world = minecraft.thePlayer.worldObj;
            if (world.getTileEntity(x, y, z) instanceof TileEntityMEChisel te) {
                te.setParallel(parallel);
            }
        }
    }
}
