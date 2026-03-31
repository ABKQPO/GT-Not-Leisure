package com.science.gtnl.api;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEColor;

public interface IBeamFormer {

    AEColor getColor();

    int getBeamLength();

    void setBeamLength(int length);

    ForgeDirection getDirection();

    World getWorld();

    boolean isValid();

    boolean shouldRenderBeam();

    default double getRenderOffset() {
        return 0.0;
    }

    double getClientOtherOffset();

    void setClientOtherOffset(double offset);

    BlockPos getPos();

    IGridNode getGridNode();

    void setConnection(IGridConnection conn);

    void setOtherBeamFormer(IBeamFormer other);

    IBeamFormer getOtherBeamFormer();

    boolean isHideBeam();

    void setHideBeam(boolean hide);

    void unregisterListener();

    void markForUpdate();

    void sleepDevice();
}
