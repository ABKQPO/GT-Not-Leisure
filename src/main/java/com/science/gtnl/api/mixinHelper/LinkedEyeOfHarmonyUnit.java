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

    public long maxHeliumAmount = -1;
    public long maxHydrogenAmount = -1;
    public long maxRawStarMatterSAmount = -1;

    public int x, y, z;

    public MTEEyeOfHarmony mMetaTileEntity;

    public LinkedEyeOfHarmonyUnit(MTEEyeOfHarmony unit) {
        this.mMetaTileEntity = unit;
        IGregTechTileEntity gtTE = unit.getBaseMetaTileEntity();
        this.x = gtTE.getXCoord();
        this.y = gtTE.getYCoord();
        this.z = gtTE.getZCoord();
    }

    /**
     * Construct new link data from a NBT compound. This is intended to sync the linked units to the client.
     *
     * @param nbtData NBT data obtained from writeLinkDataToNBT()
     */
    public LinkedEyeOfHarmonyUnit(NBTTagCompound nbtData, boolean addTE) {
        this.maxHeliumAmount = nbtData.getLong("maxHeliumAmount");
        this.maxHydrogenAmount = nbtData.getLong("maxHydrogenAmount");
        this.maxRawStarMatterSAmount = nbtData.getLong("maxRawStarMatterSAmount");
        NBTTagCompound linkData = nbtData.getCompoundTag("linkData");

        // Load coordinates from link data
        x = linkData.getInteger("x");
        y = linkData.getInteger("y");
        z = linkData.getInteger("z");

        if (!addTE) return;

        World world;
        if (!proxy.isClientSide()) {
            world = DimensionManager.getWorld(nbtData.getInteger("worldID"));
        } else {
            world = Minecraft.getMinecraft().thePlayer.worldObj;
        }

        // Find a TileEntity at this location
        TileEntity te = GTUtil.getTileEntity(world, x, y, z, true);

        if (te instanceof IGregTechTileEntity gtTE
            && gtTE.getMetaTileEntity() instanceof MTEEyeOfHarmony eyeOfHarmony) {
            this.mMetaTileEntity = eyeOfHarmony;
        }
    }

    public LinkedEyeOfHarmonyUnit(NBTTagCompound nbtData, World world) {
        this.maxHeliumAmount = nbtData.getLong("maxHeliumAmount");
        this.maxHydrogenAmount = nbtData.getLong("maxHydrogenAmount");
        this.maxRawStarMatterSAmount = nbtData.getLong("maxRawStarMatterSAmount");
        NBTTagCompound linkData = nbtData.getCompoundTag("linkData");
        // Load coordinates from link data
        x = linkData.getInteger("x");
        y = linkData.getInteger("y");
        z = linkData.getInteger("z");

        // Find a TileEntity at this location
        TileEntity te = GTUtil.getTileEntity(world, x, y, z, true);

        if (te instanceof IGregTechTileEntity gtTE
            && gtTE.getMetaTileEntity() instanceof MTEEyeOfHarmony eyeOfHarmony) {
            this.mMetaTileEntity = eyeOfHarmony;
        }
    }

    public String getStatusString() {
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

        NBTTagCompound linkData = new NBTTagCompound();
        IGregTechTileEntity mte = this.mMetaTileEntity.getBaseMetaTileEntity();
        linkData.setInteger("x", mte.getXCoord());
        linkData.setInteger("y", mte.getYCoord());
        linkData.setInteger("z", mte.getZCoord());
        tag.setTag("linkData", linkData);
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
