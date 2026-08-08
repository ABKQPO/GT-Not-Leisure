package com.science.gtnl.common.block.blocks.tile;

import java.util.Comparator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileTubeBuffer;

/** A larger essentia buffer tube that can keep several aspect types in one shared cache. */
public class TileEntityMultiEssentiaTube extends TileTubeBuffer {

    private static final int TRANSFER_INTERVAL = 1;
    private static final int TRANSFER_PER_TICK = 16;
    private static final int BELLOWS_CHECK_INTERVAL = 20;
    private static final int BASE_SUCTION = 63;
    private static final int MAX_SUCTION = 127;

    public static final int MAX_CAPACITY = 64;

    private boolean bellowsInitialized;
    private int transferTick;
    private Aspect[] cachedSortedAspects;
    private boolean sortedCacheDirty = true;

    @Override
    public void readCustomNBT(NBTTagCompound tag) {
        super.readCustomNBT(tag);
        sortedCacheDirty = true;
        removeInvalidAspects();
        trimToCapacity();
    }

    @Override
    public AspectList getAspects() {
        AspectList copy = new AspectList();
        for (Aspect storedAspect : getStoredAspectsSorted()) {
            copy.add(storedAspect, aspects.getAmount(storedAspect));
        }
        return copy;
    }

    @Override
    public void setAspects(AspectList newAspects) {
        aspects.aspects.clear();
        if (newAspects != null) {
            int remaining = MAX_CAPACITY;
            for (Aspect storedAspect : getSortedAspects(newAspects)) {
                int accepted = Math.min(newAspects.getAmount(storedAspect), remaining);
                if (accepted <= 0) continue;

                aspects.add(storedAspect, accepted);
                remaining -= accepted;
                if (remaining == 0) break;
            }
        }
        markEssentiaChanged();
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) return amount;

        int accepted = Math.min(amount, MAX_CAPACITY - getTotalAmount());
        if (accepted <= 0) return amount;

        aspects.add(aspect, accepted);
        markEssentiaChanged();
        return amount - accepted;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (!doesContainerContainAmount(aspect, amount)) return false;

        aspects.remove(aspect, amount);
        markEssentiaChanged();
        return true;
    }

    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList requestedAspects) {
        if (!doesContainerContain(requestedAspects)) return false;

        for (Aspect requestedAspect : getSortedAspects(requestedAspects)) {
            aspects.remove(requestedAspect, requestedAspects.getAmount(requestedAspect));
        }
        markEssentiaChanged();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return aspect != null && amount >= 0 && aspects.getAmount(aspect) >= amount;
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList requestedAspects) {
        if (requestedAspects == null) return false;

        for (Aspect requestedAspect : getSortedAspects(requestedAspects)) {
            if (!doesContainerContainAmount(requestedAspect, requestedAspects.getAmount(requestedAspect))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return aspect != null && getTotalAmount() < MAX_CAPACITY;
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return isValidFace(face) && super.isConnectable(face);
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return isValidFace(face) && super.canInputFrom(face);
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return isValidFace(face) && super.canOutputTo(face);
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        if (!isValidFace(face) || !super.isConnectable(face) || getTotalAmount() >= MAX_CAPACITY) {
            return 0;
        }

        int originalSuction = super.getSuctionAmount(face);
        if (chokedSides[face.ordinal()] != 0) {
            return originalSuction;
        }

        return Math.min(MAX_SUCTION, Math.max(BASE_SUCTION, originalSuction));
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        if (!isValidFace(face) || !canOutputTo(face)) return null;

        /*
         * 查询这个方向相邻设备请求的源质。
         * 例如标签罐的 getSuctionType() 会返回标签要素，
         * 管道便在这个方向提供对应要素。
         */
        if (worldObj != null) {
            TileEntity adjacent = ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, face);

            if (adjacent instanceof IEssentiaTransport target) {
                ForgeDirection targetSide = face.getOpposite();

                if (target.canInputFrom(targetSide)) {
                    Aspect requestedAspect = target.getSuctionType(targetSide);

                    /*
                     * 相邻设备明确请求了一种源质。
                     */
                    if (requestedAspect != null) {
                        return aspects.getAmount(requestedAspect) > 0 ? requestedAspect : null;
                    }
                }
            }
        }

        /*
         * 相邻设备没有指定源质，例如无过滤输入仓。
         * 此时继续提供排序后的第一种源质。
         */
        Aspect[] sorted = getStoredAspectsSorted();
        return sorted.length == 0 ? null : sorted[0];
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        Aspect offeredAspect = getEssentiaType(face);
        return offeredAspect == null ? 0 : aspects.getAmount(offeredAspect);
    }

    @Override
    public void updateEntity() {
        if (worldObj == null) return;

        transferTick++;

        if (!bellowsInitialized || transferTick % BELLOWS_CHECK_INTERVAL == 0) {
            getBellows();
            bellowsInitialized = true;
        }

        if (!worldObj.isRemote && transferTick % TRANSFER_INTERVAL == 0 && getTotalAmount() < MAX_CAPACITY) {
            fillCache(TRANSFER_PER_TICK);
        }
    }

    public int getTotalAmount() {
        int total = 0;
        for (Aspect storedAspect : getStoredAspectsSorted()) {
            total += aspects.getAmount(storedAspect);
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
            if (!source.canOutputTo(sourceSide) || source.getEssentiaAmount(sourceSide) <= 0) {
                continue;
            }

            int suction = getSuctionAmount(direction);
            if (source.getSuctionAmount(sourceSide) >= suction || suction < source.getMinimumSuction()) {
                continue;
            }

            Aspect sourceAspect = source.getEssentiaType(sourceSide);
            if (sourceAspect == null || !doesContainerAccept(sourceAspect)) continue;

            int requested = Math.min(remainingBudget, source.getEssentiaAmount(sourceSide));

            int taken = source.takeEssentia(sourceAspect, requested, sourceSide);

            if (taken > 0) {
                int accepted = addEssentia(sourceAspect, taken, direction);

                transferred += accepted;
                remainingBudget -= accepted;
            }
        }

        return transferred;
    }

    private void removeInvalidAspects() {
        aspects.aspects.entrySet()
            .removeIf(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue() <= 0);
    }

    private void trimToCapacity() {
        int remaining = MAX_CAPACITY;
        AspectList trimmed = new AspectList();

        for (Aspect storedAspect : getStoredAspectsSorted()) {
            int accepted = Math.min(aspects.getAmount(storedAspect), remaining);

            if (accepted > 0) {
                trimmed.add(storedAspect, accepted);
                remaining -= accepted;
            }

            if (remaining == 0) break;
        }

        aspects.aspects.clear();
        for (Aspect storedAspect : getSortedAspects(trimmed)) {
            aspects.add(storedAspect, trimmed.getAmount(storedAspect));
        }
    }

    private Aspect[] getStoredAspectsSorted() {
        if (sortedCacheDirty) {
            cachedSortedAspects = getSortedAspects(aspects);
            sortedCacheDirty = false;
        }
        return cachedSortedAspects;
    }

    private static Aspect[] getSortedAspects(AspectList aspectList) {
        return aspectList.aspects.entrySet()
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
