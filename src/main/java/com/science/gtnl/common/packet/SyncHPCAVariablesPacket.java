package com.science.gtnl.common.packet;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnllib.network.ClientboundPacket;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.HighPerformanceComputationArray;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import io.netty.buffer.ByteBuf;

public class SyncHPCAVariablesPacket extends ClientboundPacket {

    private UUID uuid;
    private int totalLens;
    private int x, y, z;
    private boolean mMachine;

    public SyncHPCAVariablesPacket() {}

    public SyncHPCAVariablesPacket(UUID uuid, int totalLens, int x, int y, int z, boolean mMachine) {
        this.uuid = uuid;
        this.totalLens = totalLens;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mMachine = mMachine;
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        buf.writeInt(totalLens);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(mMachine);
    }

    @Override
    protected void read(ByteBuf buf) {
        long most = buf.readLong();
        long least = buf.readLong();
        uuid = new UUID(most, least);
        totalLens = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        mMachine = buf.readBoolean();
    }

    @Override
    public void handleClient(Minecraft minecraft) {
        apply(x, y, z, uuid, totalLens, mMachine);
    }

    @SideOnly(Side.CLIENT)
    public void apply(int x, int y, int z, UUID uuid, int totalLens, boolean mMachine) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world != null) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof IGregTechTileEntity gtTE
                && gtTE.getMetaTileEntity() instanceof HighPerformanceComputationArray hpca) {
                hpca.randomUUID = uuid;
                hpca.totalLens = totalLens;
                hpca.mMachine = mMachine;
            }
        }
    }

}
