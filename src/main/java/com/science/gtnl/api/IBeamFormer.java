package com.science.gtnl.api;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import appeng.api.util.AEColor;

public interface IBeamFormer {

    AEColor getColor();

    int getBeamLength();

    ForgeDirection getDirection();

    World getWorld();

    boolean isValid();

    boolean shouldRenderBeam();

    BlockPos getPos();
}
