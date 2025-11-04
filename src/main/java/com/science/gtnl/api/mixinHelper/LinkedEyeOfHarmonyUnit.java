package com.science.gtnl.api.mixinHelper;

import static gregtech.GTMod.*;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GTUtil;
import tectech.thing.metaTileEntity.multi.MTEEyeOfHarmony;

/**
 * Small wrapper around a MTEEyeOfHarmony, to be stored in the main purification plant controller. May be useful
 * for storing additional data in the controller that the individual units do not need to know about.
 */
public class LinkedEyeOfHarmonyUnit {

    public int windowID;

    public long maxHeliumAmount = Long.MAX_VALUE;
    public long maxHydrogenAmount = Long.MAX_VALUE;
    public long maxRawStarMatterSAmount = Long.MAX_VALUE;

    /**
     * Whether this unit is active in the current cycle. We need to keep track of this so units cannot come online in
     * the middle of a cycle and suddenly start processing.
     */
    public boolean mIsActive = false;

    public MTEEyeOfHarmony mMetaTileEntity;

    public LinkedEyeOfHarmonyUnit(MTEEyeOfHarmony unit) {
        this.mMetaTileEntity = unit;
    }

    /**
     * Construct new link data from a NBT compound. This is intended to sync the linked units to the client.
     *
     * @param nbtData NBT data obtained from writeLinkDataToNBT()
     */
    public LinkedEyeOfHarmonyUnit(NBTTagCompound nbtData) {
        this.windowID = nbtData.getInteger("windowID");
        this.mIsActive = nbtData.getBoolean("active");
        this.maxHeliumAmount = nbtData.getLong("maxHeliumAmount");
        this.maxHydrogenAmount = nbtData.getLong("maxHydrogenAmount");
        this.maxRawStarMatterSAmount = nbtData.getLong("maxRawStarMatterSAmount");
        NBTTagCompound linkData = nbtData.getCompoundTag("linkData");
        World world;
        if (!proxy.isClientSide()) {
            world = DimensionManager.getWorld(nbtData.getInteger("worldID"));
        } else {
            world = Minecraft.getMinecraft().thePlayer.worldObj;
        }

        // Load coordinates from link data
        int x = linkData.getInteger("x");
        int y = linkData.getInteger("y");
        int z = linkData.getInteger("z");

        // Find a TileEntity at this location
        TileEntity te = GTUtil.getTileEntity(world, x, y, z, true);
        if (te == null) {
            // This is a bug, throw a fatal error.
            throw new NullPointerException("Unit disappeared during server sync. This is a bug.");
        }

        // Cast TileEntity to proper GT TileEntity
        this.mMetaTileEntity = (MTEEyeOfHarmony) ((IGregTechTileEntity) te).getMetaTileEntity();
    }

    public MTEEyeOfHarmony metaTileEntity() {
        return mMetaTileEntity;
    }

    /**
     * Whether this unit is considered as active in the current cycle
     *
     * @return true if this unit is active in the current cycle
     */
    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean active) {
        this.mIsActive = active;
    }

    public String getStatusString() {
        if (this.isActive()) {
            return EnumChatFormatting.GREEN + StatCollector.translateToLocal("GT5U.gui.text.status.active");
        }

        if (this.mMetaTileEntity.mMachine) {
            if (this.mMetaTileEntity.mMaxProgresstime > 0) {
                return EnumChatFormatting.GREEN + StatCollector.translateToLocal("GT5U.gui.text.status.online");
            } else {
                return EnumChatFormatting.YELLOW + StatCollector.translateToLocal("GT5U.gui.text.status.disabled");
            }
        } else {
            return EnumChatFormatting.RED + StatCollector.translateToLocal("GT5U.gui.text.status.incomplete");
        }
    }

    /**
     * Save link data to a NBT tag, so it can be reconstructed at the client side
     */
    public NBTTagCompound writeLinkDataToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("active,", this.mIsActive);

        NBTTagCompound linkData = new NBTTagCompound();
        IGregTechTileEntity mte = this.mMetaTileEntity.getBaseMetaTileEntity();
        linkData.setInteger("x", mte.getXCoord());
        linkData.setInteger("y", mte.getYCoord());
        linkData.setInteger("z", mte.getZCoord());
        tag.setTag("linkData", linkData);
        tag.setInteger("windowID", windowID);
        tag.setLong("maxHeliumAmount", maxHeliumAmount);
        tag.setLong("maxHydrogenAmount", maxHydrogenAmount);
        tag.setLong("maxRawStarMatterSAmount", maxRawStarMatterSAmount);

        tag.setInteger(
            "worldID",
            this.mMetaTileEntity.getBaseMetaTileEntity()
                .getWorld().provider.dimensionId);
        return tag;
    }
}
