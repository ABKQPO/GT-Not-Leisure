package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.science.gtnl.common.block.blocks.BlockEnderElevator;

import lombok.Getter;

public class TileEntityEnderElevator extends TileEntity {

    @Getter
    private Block disguiseBlock = null;
    @Getter
    private int disguiseMeta = 0;

    private int cacheX, cacheY, cacheZ;
    private boolean hasCache = false;

    public int cooldown = 0;
    public static int MAX_COOLDOWN = 5;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (disguiseBlock != null) {
            compound.setInteger("disguiseId", Block.getIdFromBlock(disguiseBlock));
            compound.setInteger("disguiseMeta", disguiseMeta);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("disguiseId")) {
            this.disguiseBlock = Block.getBlockById(compound.getInteger("disguiseId"));
            this.disguiseMeta = compound.getInteger("disguiseMeta");
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void updateEntity() {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    public boolean isReady() {
        return cooldown <= 0;
    }

    public void startCooldown() {
        this.cooldown = MAX_COOLDOWN;
        this.markDirty();
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setDisguise(Block block, int meta) {
        this.disguiseBlock = block;
        this.disguiseMeta = meta;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void clearDisguise() {
        this.disguiseBlock = null;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public BlockPos findTarget(boolean up, int color, int currentY) {
        if (this.hasCache) {
            boolean cacheDirectionValid = up ? (cacheY > currentY) : (cacheY < currentY);
            if (cacheDirectionValid && isValid(cacheX, cacheY, cacheZ, color)) {
                return new BlockPos(cacheX, cacheY, cacheZ);
            } else {
                this.hasCache = false;
            }
        }

        if (up) {
            for (int ty = currentY + 1; ty < 256; ty++) {
                BlockPos result = scanLayer(ty, color);
                if (result != null) return result;
            }
        } else {
            for (int ty = currentY - 1; ty >= 0; ty--) {
                BlockPos result = scanLayer(ty, color);
                if (result != null) return result;
            }
        }
        return null;
    }

    private BlockPos scanLayer(int ty, int color) {
        for (int dx = -64; dx <= 64; dx++) {
            for (int dz = -64; dz <= 64; dz++) {
                int tx = xCoord + dx;
                int tz = zCoord + dz;
                if (isValid(tx, ty, tz, color)) {
                    this.cacheX = tx;
                    this.cacheY = ty;
                    this.cacheZ = tz;
                    this.hasCache = true;
                    return new BlockPos(tx, ty, tz);
                }
            }
        }
        return null;
    }

    private boolean isValid(int x, int y, int z, int color) {
        Block b = worldObj.getBlock(x, y, z);
        if (!(b instanceof BlockEnderElevator) || worldObj.getBlockMetadata(x, y, z) != color) {
            return false;
        }

        return isSafeToStand(x, y + 1, z) && isSafeToStand(x, y + 2, z);
    }

    private boolean isSafeToStand(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        return block.isAir(worldObj, x, y, z) || !block.getMaterial()
            .isSolid();
    }
}
