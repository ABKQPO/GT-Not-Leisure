package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import lombok.Getter;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileEntityEssentiaHatch extends TileEntity implements IEssentiaTransport {

    private static final String LOCKED_ASPECT_KEY = "LockedAspect";
    private static final String STORED_AMOUNT_KEY = "StoredAmount";
    private static final int MAX_STORED = 256;

    public int mState = 1;

    @Getter
    private Aspect lockedAspect;
    @Getter
    private final AspectList aspects = new AspectList();

    public void setLockedAspect(Aspect aspect) {
        lockedAspect = aspect;
        if (aspect == null) {
            aspects.aspects.clear();
            return;
        }

        int amount = aspects.getAmount(aspect);
        aspects.aspects.clear();
        if (amount > 0) {
            aspects.add(aspect, amount);
        }
        markDirty();
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
    public boolean isConnectable(net.minecraftforge.common.util.ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canInputFrom(net.minecraftforge.common.util.ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canOutputTo(net.minecraftforge.common.util.ForgeDirection face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(net.minecraftforge.common.util.ForgeDirection face) {
        return lockedAspect;
    }

    @Override
    public int getSuctionAmount(net.minecraftforge.common.util.ForgeDirection face) {
        return lockedAspect == null ? 0 : 128;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, net.minecraftforge.common.util.ForgeDirection face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, net.minecraftforge.common.util.ForgeDirection face) {
        if (aspect == null || amount <= 0) {
            return 0;
        }
        if (lockedAspect != null && lockedAspect != aspect) {
            return 0;
        }

        lockedAspect = aspect;
        int stored = aspects.getAmount(aspect);
        int accepted = Math.min(amount, MAX_STORED - stored);
        if (accepted <= 0) {
            return 0;
        }

        aspects.add(aspect, accepted);
        markDirty();
        return accepted;
    }

    @Override
    public Aspect getEssentiaType(net.minecraftforge.common.util.ForgeDirection face) {
        return lockedAspect;
    }

    @Override
    public int getEssentiaAmount(net.minecraftforge.common.util.ForgeDirection face) {
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
}
