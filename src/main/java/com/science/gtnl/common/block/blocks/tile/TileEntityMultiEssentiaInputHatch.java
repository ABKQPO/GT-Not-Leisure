package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileEntityMultiEssentiaInputHatch extends TileEntityEssentiaHatch {

    private static final String STORED_ASPECTS_KEY = "StoredAspects";
    private static final int TRANSFER_PER_TICK = 16;
    private static final int SUCTION = 128;

    public static final int MAX_CAPACITY = 4096;

    private final AspectList storedAspects = new AspectList();
    private final MultiEssentiaStorage storage = new MultiEssentiaStorage(storedAspects, MAX_CAPACITY);

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        storedAspects.aspects.clear();
        if (tag.hasKey(STORED_ASPECTS_KEY)) {
            storedAspects.readFromNBT(tag.getCompoundTag(STORED_ASPECTS_KEY));
        }
        storage.reload();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound storedTag = new NBTTagCompound();
        storedAspects.writeToNBT(storedTag);
        tag.setTag(STORED_ASPECTS_KEY, storedTag);
    }

    @Override
    public AspectList getAspects() {
        return storage.copyAspects();
    }

    @Override
    public void setAspects(AspectList aspects) {
        storage.setAspects(aspects);
        markEssentiaChanged();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return storage.doesContainerAccept(aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        int remaining = storage.addToContainer(aspect, amount);
        if (remaining != amount) markEssentiaChanged();
        return remaining;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        boolean taken = storage.takeFromContainer(aspect, amount);
        if (taken) markEssentiaChanged();
        return taken;
    }

    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList aspects) {
        boolean taken = storage.takeFromContainer(aspects);
        if (taken) markEssentiaChanged();
        return taken;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return storage.doesContainerContainAmount(aspect, amount);
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList aspects) {
        return storage.doesContainerContain(aspects);
    }

    @Override
    public int containerContains(Aspect aspect) {
        return storage.getAmount(aspect);
    }

    @Override
    public boolean reduceStoredEssentia(Aspect aspect, int amount) {
        return takeFromContainer(aspect, amount);
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return isValidFace(face);
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return isValidFace(face);
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return canInputFrom(face) && getTotalAmount() < MAX_CAPACITY ? SUCTION : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return canInputFrom(face) ? amount - addToContainer(aspect, amount) : 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        Aspect[] sorted = storage.getStoredAspectsSorted();
        return sorted.length == 0 ? null : sorted[0];
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        Aspect aspect = getEssentiaType(face);
        return aspect == null ? 0 : storage.getAmount(aspect);
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote
            || getTotalAmount() >= MAX_CAPACITY) {
            return;
        }

        fillCache(TRANSFER_PER_TICK);
    }

    public int getTotalAmount() {
        return storage.getTotalAmount();
    }

    public int getStoredTypeCount() {
        return storage.getStoredTypeCount();
    }

    private int fillCache(int maxTransfer) {
        int transferred = 0;
        int remainingBudget = Math.min(maxTransfer, MAX_CAPACITY - getTotalAmount());

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if (remainingBudget <= 0) break;
            if (!canInputFrom(direction)) continue;

            TileEntity tile = ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, direction);
            if (!(tile instanceof IEssentiaTransport source)) continue;

            ForgeDirection sourceSide = direction.getOpposite();
            if (!source.canOutputTo(sourceSide) || source.getEssentiaAmount(sourceSide) <= 0) continue;

            int suction = getSuctionAmount(direction);
            if (source.getSuctionAmount(sourceSide) >= suction || suction < source.getMinimumSuction()) continue;

            Aspect sourceAspect = source.getEssentiaType(sourceSide);
            if (sourceAspect == null || !doesContainerAccept(sourceAspect)) continue;

            int requested = Math.min(remainingBudget, source.getEssentiaAmount(sourceSide));
            int taken = source.takeEssentia(sourceAspect, requested, sourceSide);
            if (taken <= 0) continue;

            int accepted = addEssentia(sourceAspect, taken, direction);
            transferred += accepted;
            remainingBudget -= accepted;
        }

        return transferred;
    }

    private static boolean isValidFace(ForgeDirection face) {
        return face != null && face != ForgeDirection.UNKNOWN && face.ordinal() < 6;
    }

    private void markEssentiaChanged() {
        storage.markDirty();
        markDirty();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
}
