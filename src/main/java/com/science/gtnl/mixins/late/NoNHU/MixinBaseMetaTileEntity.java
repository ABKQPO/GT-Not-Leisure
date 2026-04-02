package com.science.gtnl.mixins.late.NoNHU;

import static com.science.gtnl.ScienceNotLeisure.LOG;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.api.ITileEntityTickAcceleration;
import com.science.gtnl.api.mixinHelper.IAccelerationState;

import ggfab.mte.MTEAdvAssLine;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.CommonBaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;

@Mixin(value = BaseMetaTileEntity.class, remap = false)
public abstract class MixinBaseMetaTileEntity extends CommonBaseMetaTileEntity implements ITileEntityTickAcceleration {

    @Shadow
    public abstract int getProgress();

    @Shadow
    public abstract int getMaxProgress();

    @Shadow
    public abstract IMetaTileEntity getMetaTileEntity();

    @Shadow
    public abstract boolean isActive();

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean tickAcceleration(int tickAcceleratedRate) {
        if (this.isActive()) {
            // safely calling
            int currentProgress = this.getProgress();
            int maxProgress = this.getMaxProgress();
            IMetaTileEntity metaTileEntity = this.getMetaTileEntity();

            // for accelerating Research Station
            if (metaTileEntity instanceof MTEResearchStation researchStation) {
                if (researchStation instanceof ITileEntityTickAcceleration resAte) {
                    resAte.tickAcceleration(tickAcceleratedRate);
                }
                return true;
            }

            // for accelerating Adv Ass Line
            if (metaTileEntity instanceof MTEAdvAssLine advAssLine) {
                if (advAssLine instanceof IAccelerationState accelerationState) {
                    accelerationState.gtnl$setIsAccelerationState(true);
                    try {

                        // Referenced GTNH to control the performance in 1ms
                        long tMaxTime = System.nanoTime() + 1000000;

                        for (int i = 0; i < tickAcceleratedRate; i++) {
                            // skip if assLine stuck
                            if (accelerationState.gtnl$getMachineAccelerationState()) break;
                            this.updateEntity();
                            if (System.nanoTime() > tMaxTime) {
                                break;
                            }
                        }

                    } catch (Exception e) {
                        LOG.warn(
                            "An error occurred accelerating TileEntity at ( {}, {}, {}, {})",
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            e.getMessage());
                    } finally {
                        accelerationState.gtnl$setIsAccelerationState(false);
                    }
                }
            }

            if (maxProgress >= 2) { // obviously
                // discount for accelerating gregtech machines
                int GTNL$modify = Math.min(maxProgress, currentProgress + tickAcceleratedRate);

                // for accelerating basic machine
                if (metaTileEntity instanceof MTEBasicMachine basicMachine) {
                    basicMachine.mProgresstime = GTNL$modify;
                    return true;
                }

                // for accelerating multi machine
                if (metaTileEntity instanceof MTEMultiBlockBase multiBlockBase) {
                    multiBlockBase.mProgresstime = GTNL$modify;
                    return true;
                }

                return false; // this for accelerating gt machine by executing TE update method

            }
        }
        return true; // this for not acceleration while machine is shutdown
    }
}
