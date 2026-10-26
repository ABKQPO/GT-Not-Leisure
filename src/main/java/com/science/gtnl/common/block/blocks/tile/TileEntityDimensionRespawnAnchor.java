package com.science.gtnl.common.block.blocks.tile;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;

import com.science.gtnl.api.mixinHelper.IAnchorRespawn;

import lombok.Getter;

@Getter
public class TileEntityDimensionRespawnAnchor extends TileEntity {

    private int energyLevel = 0;

    public void addEnergy(int amount) {
        energyLevel = Math.min(4, energyLevel + amount);
    }

    public void consumeEnergy() {
        if (energyLevel > 0) {
            energyLevel--;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("energy", energyLevel);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyLevel = tag.getInteger("energy");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    // The dimension a player with a charged anchor set respawns in, using up one charge; otherwise the original one.
    public static int respawnDimension(MinecraftServer server, int originalDimension, EntityPlayerMP player) {
        IAnchorRespawn anchor = (IAnchorRespawn) player;
        ChunkCoordinates anchorPos = anchor.getAnchorPos();

        if (anchorPos == null) {
            return originalDimension;
        }

        int anchorDim = anchor.getAnchorDim();
        if (anchorDim == Integer.MIN_VALUE) {
            return originalDimension;
        }

        WorldServer world = server.worldServerForDimension(anchorDim);
        if (world == null) {
            return originalDimension;
        }

        TileEntity tile = world.getTileEntity(anchorPos.posX, anchorPos.posY - 1, anchorPos.posZ);
        if (!(tile instanceof TileEntityDimensionRespawnAnchor respawnAnchorTile)
            || respawnAnchorTile.getEnergyLevel() <= 0) {
            anchor.clearAnchorRespawn();
            player.addChatMessage(new ChatComponentTranslation("gtnl.chat.dimension_respawn_anchor.no_spawn_point"));
            return originalDimension;
        }

        respawnAnchorTile.consumeEnergy();
        world.markBlockForUpdate(respawnAnchorTile.xCoord, respawnAnchorTile.yCoord, respawnAnchorTile.zCoord);

        world.playSoundEffect(
            anchorPos.posX + 0.5,
            anchorPos.posY - 0.5,
            anchorPos.posZ + 0.5,
            RESOURCE_ROOT_ID + ":respawn_anchor.deplete" + (1 + world.rand.nextInt(2)),
            1.0F,
            1.0F);

        return anchorDim;
    }
}
