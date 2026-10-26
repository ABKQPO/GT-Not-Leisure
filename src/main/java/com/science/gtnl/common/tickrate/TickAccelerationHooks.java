package com.science.gtnl.common.tickrate;

import static com.science.gtnl.ScienceNotLeisure.LOG;

import com.science.gtnl.api.ITileEntityTickAcceleration;
import com.science.gtnl.api.mixinHelper.IAccelerationState;

import ggfab.mte.MTEAdvAssLine;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;

public class TickAccelerationHooks {

    private TickAccelerationHooks() {}

    public static boolean accelerate(BaseMetaTileEntity tile, int tickAcceleratedRate) {
        if (tile.isActive()) {
            int currentProgress = tile.getProgress();
            int maxProgress = tile.getMaxProgress();
            IMetaTileEntity metaTileEntity = tile.getMetaTileEntity();

            if (metaTileEntity instanceof MTEResearchStation researchStation) {
                if (researchStation instanceof ITileEntityTickAcceleration resAte) {
                    resAte.tickAcceleration(tickAcceleratedRate);
                }
                return true;
            }

            if (metaTileEntity instanceof MTEAdvAssLine advAssLine) {
                if (advAssLine instanceof IAccelerationState accelerationState) {
                    accelerationState.gtnl$setIsAccelerationState(true);
                    try {
                        long tMaxTime = System.nanoTime() + 1000000;

                        for (int i = 0; i < tickAcceleratedRate; i++) {
                            if (accelerationState.gtnl$getMachineAccelerationState()) break;
                            tile.updateEntity();
                            if (System.nanoTime() > tMaxTime) {
                                break;
                            }
                        }

                    } catch (Exception e) {
                        LOG.warn(
                            "An error occurred accelerating TileEntity at ( {}, {}, {}, {})",
                            tile.xCoord,
                            tile.yCoord,
                            tile.zCoord,
                            e.getMessage());
                    } finally {
                        accelerationState.gtnl$setIsAccelerationState(false);
                    }
                }
            }

            if (maxProgress >= 2) {
                int gtnl$modify = Math.min(maxProgress, currentProgress + tickAcceleratedRate);

                if (metaTileEntity instanceof MTEBasicMachine basicMachine) {
                    basicMachine.mProgresstime = gtnl$modify;
                    return true;
                }

                if (metaTileEntity instanceof MTEMultiBlockBase multiBlockBase) {
                    multiBlockBase.mProgresstime = gtnl$modify;
                    return true;
                }

                return false;
            }
        }
        return true;
    }
}
