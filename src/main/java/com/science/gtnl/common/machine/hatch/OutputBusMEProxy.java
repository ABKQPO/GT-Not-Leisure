package com.science.gtnl.common.machine.hatch;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.science.gtnl.api.mixinHelper.IOutputME;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.storage.data.IAEItemStack;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtil;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputBusME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OutputBusMEProxy extends MTEHatchOutputBusME {

    public int masterX, masterY, masterZ, masterDim;
    public boolean masterSet = false; // indicate if values of masterX, masterY, masterZ are valid
    public MTEHatchOutputBusME master;
    private long lastProxyFlushTick;

    public OutputBusMEProxy(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OutputBusMEProxy(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OutputBusMEProxy(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (aTimer % 100 == 0 && masterSet && getMaster() == null) {
            trySetMasterFromCoord(masterX, masterY, masterZ, masterDim);
        }
        super.onPostTick(aBaseMetaTileEntity, aTimer);
        if (aBaseMetaTileEntity.isServerSide() && aTimer > lastProxyFlushTick + 40) {
            flushCachedStack();
            lastProxyFlushTick = aTimer;
        }
    }

    @Override
    public ItemStack getVisual() {
        return GTNLItemList.OutputBusMEProxy.get(1);
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_00"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_01"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_02"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_03"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_04"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_05"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_06") };
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("master")) {
            NBTTagCompound masterNBT = tag.getCompoundTag("master");
            currenttip.add(
                EnumChatFormatting.AQUA + "Linked to Output at "
                    + EnumChatFormatting.WHITE
                    + "[Dim "
                    + masterNBT.getInteger("masterDim")
                    + "] "
                    + masterNBT.getInteger("masterX")
                    + ", "
                    + masterNBT.getInteger("masterY")
                    + ", "
                    + masterNBT.getInteger("masterZ")
                    + EnumChatFormatting.RESET);
        } else {
            currenttip.add(EnumChatFormatting.AQUA + "Unlinked");
        }

        super.getWailaBody(itemStack, currenttip, accessor, config);
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {}

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        ((IOutputME) this).setLastClickedPlayer(aPlayer);
        openGui(aPlayer);
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (!masterSet) return;
        NBTTagCompound masterNBT = new NBTTagCompound();
        masterNBT.setInteger("masterDim", masterDim);
        masterNBT.setInteger("masterX", masterX);
        masterNBT.setInteger("masterY", masterY);
        masterNBT.setInteger("masterZ", masterZ);
        aNBT.setTag("master", masterNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (!aNBT.hasKey("master")) return;
        NBTTagCompound masterNBT = aNBT.getCompoundTag("master");
        masterX = masterNBT.getInteger("masterX");
        masterY = masterNBT.getInteger("masterY");
        masterZ = masterNBT.getInteger("masterZ");
        masterDim = masterNBT.getInteger("masterDim");
        masterSet = true;
    }

    @Override
    public NBTTagCompound getCopiedData(EntityPlayer player) {
        NBTTagCompound tag = super.getCopiedData(player);
        if (!masterSet) return tag;
        NBTTagCompound masterNBT = new NBTTagCompound();
        masterNBT.setInteger("masterDim", masterDim);
        masterNBT.setInteger("masterX", masterX);
        masterNBT.setInteger("masterY", masterY);
        masterNBT.setInteger("masterZ", masterZ);
        tag.setTag("master", masterNBT);
        return tag;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound aNBT) {
        boolean result = super.pasteCopiedData(player, aNBT);
        if (aNBT == null || !aNBT.hasKey("master")) return result;
        NBTTagCompound masterNBT = aNBT.getCompoundTag("master");
        int x = masterNBT.getInteger("masterX");
        int y = masterNBT.getInteger("masterY");
        int z = masterNBT.getInteger("masterZ");
        int dim = masterNBT.getInteger("masterDim");
        return trySetMasterFromCoord(x, y, z, dim) != null;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        if (masterSet) {
            NBTTagCompound masterNBT = new NBTTagCompound();
            masterNBT.setInteger("masterDim", masterDim);
            masterNBT.setInteger("masterX", masterX);
            masterNBT.setInteger("masterY", masterY);
            masterNBT.setInteger("masterZ", masterZ);
            tag.setTag("master", masterNBT);
        }
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }

    public void flushCachedStack() {
        MTEHatchOutputBusME outputBus = getMaster();
        if (outputBus == null || !outputBus.canAcceptAnyItem()) {
            return;
        }
        for (IAEItemStack stack : getProvider().getCacheList()) {
            if (stack != null && stack.getStackSize() > 0) {
                outputBus.getProvider()
                    .storeToCache(stack.copy());
            }
        }
        getProvider().getCacheList()
            .forEach(cachedStack -> cachedStack.setStackSize(0));
    }

    public MTEHatchOutputBusME getMaster() {
        if (master == null) return null;
        if (master.getBaseMetaTileEntity() == null) {
            master = null;
        }
        return master;
    }

    public MTEHatchOutputBusME trySetMasterFromCoord(int x, int y, int z, int dim) {
        World world = DimensionManager.getWorld(dim);
        if (world == null) return null;

        TileEntity tileEntity = GTUtil.getTileEntity(world, x, y, z, false);
        if (tileEntity == null) return null;
        if (!(tileEntity instanceof IGregTechTileEntity gregTechTileEntity)) return null;
        if (!(gregTechTileEntity.getMetaTileEntity() instanceof MTEHatchOutputBusME newMaster)) return null;
        if (newMaster instanceof OutputBusMEProxy) return null;

        if (master != newMaster) {
            master = newMaster;
        }
        masterX = x;
        masterY = y;
        masterZ = z;
        masterDim = dim;
        masterSet = true;
        return master;
    }
}
