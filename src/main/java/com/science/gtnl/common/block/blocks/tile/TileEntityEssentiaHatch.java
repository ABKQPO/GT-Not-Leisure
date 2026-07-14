package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.Getter;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileEntityEssentiaHatch extends TileEntity implements IEssentiaTransport, IAspectContainer {

    private static final String LOCKED_ASPECT_KEY = "LockedAspect";
    private static final String STORED_AMOUNT_KEY = "StoredAmount";
    public static final int MAX_STORED = 256;

    public int mState = 1;

    private int transferTick;

    @Getter
    private Aspect lockedAspect;
    @Getter
    private final AspectList aspects = new AspectList();

    public void setLockedAspect(Aspect aspect) {
        lockedAspect = aspect;
        if (aspect == null) {
            aspects.aspects.clear();
            markEssentiaChanged();
            return;
        }

        int amount = aspects.getAmount(aspect);
        aspects.aspects.clear();
        if (amount > 0) {
            aspects.add(aspect, amount);
        }
        markEssentiaChanged();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        lockedAspect = Aspect.getAspect(tag.getString(LOCKED_ASPECT_KEY));
        aspects.aspects.clear();
        if (lockedAspect != null) {
            int amount = tag.getInteger(STORED_AMOUNT_KEY);
            if (amount > 0) {
                aspects.add(lockedAspect, amount);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString(LOCKED_ASPECT_KEY, lockedAspect == null ? "" : lockedAspect.getTag());
        tag.setInteger(STORED_AMOUNT_KEY, lockedAspect == null ? 0 : aspects.getAmount(lockedAspect));
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return lockedAspect;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return lockedAspect == null ? 0 : 128;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return insertEssentia(aspect, amount);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote || ++transferTick % 5 != 0 || getStoredAmount() >= MAX_STORED) return;

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity tile = worldObj
                .getTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);
            if (!(tile instanceof IEssentiaTransport source)) continue;

            ForgeDirection sourceSide = side.getOpposite();
            if (!source.canOutputTo(sourceSide)) continue;
            if (source.getEssentiaAmount(sourceSide) <= 0) continue;

            Aspect requestedAspect = getRequestedAspect(source, side, sourceSide);
            if (requestedAspect == null) continue;
            if (source.getSuctionAmount(sourceSide) >= getSuctionAmount(side)) continue;

            int drained = source.takeEssentia(requestedAspect, 1, sourceSide);
            if (drained > 0) {
                addEssentia(requestedAspect, drained, side);
                return;
            }
        }
    }

    @Override
    public void setAspects(AspectList aspectList) {
        lockedAspect = null;
        aspects.aspects.clear();

        if (aspectList != null) {
            for (Aspect aspect : aspectList.getAspects()) {
                int amount = aspectList.getAmount(aspect);
                if (aspect != null && amount > 0) {
                    lockedAspect = aspect;
                    aspects.add(aspect, Math.min(amount, MAX_STORED));
                    break;
                }
            }
        }

        markEssentiaChanged();
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        if (aspect == null) return false;
        if (lockedAspect != null) return lockedAspect == aspect && aspects.getAmount(aspect) < MAX_STORED;
        Aspect storedAspect = getStoredAspect();
        return storedAspect == null || storedAspect == aspect && aspects.getAmount(aspect) < MAX_STORED;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        return amount - insertEssentia(aspect, amount);
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
    public boolean takeFromContainer(AspectList aspectList) {
        if (!doesContainerContain(aspectList)) return false;

        for (Aspect aspect : aspectList.getAspects()) {
            int amount = aspectList.getAmount(aspect);
            if (aspect != null && amount > 0) {
                aspects.remove(aspect, amount);
            }
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
    public boolean doesContainerContain(AspectList aspectList) {
        if (aspectList == null) return false;

        for (Aspect aspect : aspectList.getAspects()) {
            int amount = aspectList.getAmount(aspect);
            if (aspect != null && amount > 0 && !doesContainerContainAmount(aspect, amount)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return aspect == null ? 0 : aspects.getAmount(aspect);
    }

    public boolean reduceStoredEssentia(Aspect aspect, int amount) {
        return takeFromContainer(aspect, amount);
    }

    private int insertEssentia(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            return 0;
        }
        if (lockedAspect != null && lockedAspect != aspect) {
            return 0;
        }
        Aspect storedAspect = getStoredAspect();
        if (storedAspect != null && storedAspect != aspect) {
            return 0;
        }

        lockedAspect = aspect;
        int stored = aspects.getAmount(aspect);
        int accepted = Math.min(amount, MAX_STORED - stored);
        if (accepted <= 0) {
            return 0;
        }

        aspects.add(aspect, accepted);
        markEssentiaChanged();
        return accepted;
    }

    private Aspect getStoredAspect() {
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null && aspects.getAmount(aspect) > 0) {
                return aspect;
            }
        }
        return null;
    }

    private Aspect getRequestedAspect(IEssentiaTransport source, ForgeDirection side, ForgeDirection sourceSide) {
        if (lockedAspect != null) return lockedAspect;

        Aspect storedAspect = getStoredAspect();
        if (storedAspect != null) return storedAspect;

        if (getSuctionAmount(side) >= source.getMinimumSuction()) {
            return source.getEssentiaType(sourceSide);
        }
        return null;
    }

    private int getStoredAmount() {
        Aspect storedAspect = getStoredAspect();
        return storedAspect == null ? 0 : aspects.getAmount(storedAspect);
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        return lockedAspect;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        return lockedAspect == null ? 0 : aspects.getAmount(lockedAspect);
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    private void markEssentiaChanged() {
        markDirty();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
}
