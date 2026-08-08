package com.science.gtnl.common.block.blocks.tile;

import java.util.Comparator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;

/** An essentia input hatch with one shared multi-aspect cache. */
public class TileEntityMultiEssentiaInputHatch extends TileEntityEssentiaHatch {

    private static final String STORED_ASPECTS_KEY = "StoredAspects";
    private static final int TRANSFER_INTERVAL = 1;
    private static final int TRANSFER_PER_TICK = 16;
    private static final int SUCTION = 128;

    public static final int MAX_CAPACITY = 4096;

    private final AspectList storedAspects = new AspectList();
    private int transferTick;
    private Aspect[] cachedSortedAspects;
    private boolean sortedCacheDirty = true;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        storedAspects.aspects.clear();
        if (tag.hasKey(STORED_ASPECTS_KEY)) {
            storedAspects.readFromNBT(tag.getCompoundTag(STORED_ASPECTS_KEY));
        }
        sortedCacheDirty = true;
        removeInvalidAspects();
        trimToCapacity();
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
        AspectList copy = new AspectList();
        for (Aspect aspect : getStoredAspectsSorted()) {
            copy.add(aspect, storedAspects.getAmount(aspect));
        }
        return copy;
    }

    @Override
    public void setAspects(AspectList aspects) {
        storedAspects.aspects.clear();
        if (aspects != null) {
            int remaining = MAX_CAPACITY;
            for (Aspect aspect : getSortedAspects(aspects)) {
                int accepted = Math.min(aspects.getAmount(aspect), remaining);
                if (accepted <= 0) continue;

                storedAspects.add(aspect, accepted);
                remaining -= accepted;
                if (remaining == 0) break;
            }
        }
        markEssentiaChanged();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect != null && getTotalAmount() < MAX_CAPACITY;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) return amount;

        int accepted = Math.min(amount, MAX_CAPACITY - getTotalAmount());
        if (accepted <= 0) return amount;

        storedAspects.add(aspect, accepted);
        markEssentiaChanged();
        return amount - accepted;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (!doesContainerContainAmount(aspect, amount)) return false;

        storedAspects.remove(aspect, amount);
        markEssentiaChanged();
        return true;
    }

    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList aspects) {
        if (!doesContainerContain(aspects)) return false;

        for (Aspect aspect : getSortedAspects(aspects)) {
            storedAspects.remove(aspect, aspects.getAmount(aspect));
        }
        markEssentiaChanged();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return aspect != null && amount >= 0 && storedAspects.getAmount(aspect) >= amount;
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList aspects) {
        if (aspects == null) return false;

        for (Aspect aspect : getSortedAspects(aspects)) {
            if (!doesContainerContainAmount(aspect, aspects.getAmount(aspect))) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return aspect == null ? 0 : storedAspects.getAmount(aspect);
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
        Aspect[] sorted = getStoredAspectsSorted();
        return sorted.length == 0 ? null : sorted[0];
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        Aspect aspect = getEssentiaType(face);
        return aspect == null ? 0 : storedAspects.getAmount(aspect);
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
            || ++transferTick % TRANSFER_INTERVAL != 0
            || getTotalAmount() >= MAX_CAPACITY) {
            return;
        }

        fillCache(TRANSFER_PER_TICK);
    }

    public int getTotalAmount() {
        int total = 0;
        for (Aspect aspect : getStoredAspectsSorted()) {
            total += storedAspects.getAmount(aspect);
        }
        return total;
    }

    public int getStoredTypeCount() {
        return getStoredAspectsSorted().length;
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

    private void removeInvalidAspects() {
        storedAspects.aspects.entrySet()
            .removeIf(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0);
    }

    private void trimToCapacity() {
        int remaining = MAX_CAPACITY;
        AspectList trimmed = new AspectList();
        for (Aspect aspect : getStoredAspectsSorted()) {
            int accepted = Math.min(storedAspects.getAmount(aspect), remaining);
            if (accepted > 0) {
                trimmed.add(aspect, accepted);
                remaining -= accepted;
            }
            if (remaining == 0) break;
        }

        storedAspects.aspects.clear();
        for (Aspect aspect : getSortedAspects(trimmed)) {
            storedAspects.add(aspect, trimmed.getAmount(aspect));
        }
    }

    private Aspect[] getStoredAspectsSorted() {
        if (sortedCacheDirty) {
            cachedSortedAspects = getSortedAspects(storedAspects);
            sortedCacheDirty = false;
        }
        return cachedSortedAspects;
    }

    private static Aspect[] getSortedAspects(AspectList aspects) {
        return aspects.aspects.entrySet()
            .stream()
            .filter(entry -> entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0)
            .map(entry -> entry.getKey())
            .sorted(Comparator.comparing(Aspect::getTag))
            .toArray(Aspect[]::new);
    }

    private static boolean isValidFace(ForgeDirection face) {
        return face != null && face != ForgeDirection.UNKNOWN && face.ordinal() < 6;
    }

    private void markEssentiaChanged() {
        sortedCacheDirty = true;
        markDirty();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
}
