package com.science.gtnl.common.machine.hatch;

import static gregtech.api.enums.Dyes.MACHINE_METAL;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.utils.enums.BlockIcons;

import gregtech.GTMod;
import gregtech.api.enums.Dyes;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.render.TextureFactory;
import gtnhlanth.common.beamline.IConnectsToBeamline;
import gtnhlanth.common.beamline.MTEBeamlinePipe;
import gtnhlanth.common.hatch.MTEHatchInputBeamline;

public class BeamlinePipeMirror extends MTEBeamlinePipe {

    public ForgeDirection[] connectedSides = { null, null };
    public ForgeDirection chainedFrontFacing = null;
    public byte connectionCount = 0;
    public boolean active;

    public BeamlinePipeMirror(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public BeamlinePipeMirror(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity arg0) {
        return new BeamlinePipeMirror(mName);
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_BeamlinePipeMirror_00"),
            StatCollector.translateToLocal("Tooltip_BeamlinePipeMirror_01") };
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, int aConnections,
        int colorIndex, boolean aConnected, boolean aRedstone) {
        return new ITexture[] { TextureFactory
            .of(BlockIcons.BEAMLINE_PIPE_MIRROR, Dyes.getModulation(colorIndex, MACHINE_METAL.getRGBA())) };
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if ((aTick & 31) == 31) {
                mConnections = 0;
                connectedSides[0] = null;
                connectedSides[1] = null;
                connectionCount = 0;

                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    if (connectionCount < 2) {
                        TileEntity tTileEntity = aBaseMetaTileEntity.getTileEntityAtSide(dir);
                        ForgeDirection oppositeDir = dir.getOpposite();

                        boolean canConnect = false;
                        if (tTileEntity instanceof IConnectsToBeamline beamline) {
                            canConnect = beamline.canConnect(oppositeDir);
                        } else if (tTileEntity instanceof IGregTechTileEntity gtTE) {
                            IMetaTileEntity meta = gtTE.getMetaTileEntity();
                            if (meta instanceof IConnectsToBeamline beamline) {
                                canConnect = beamline.canConnect(oppositeDir);
                            }
                        }

                        if (canConnect) {
                            mConnections |= 1 << dir.ordinal();
                            connectedSides[connectionCount] = dir;
                            connectionCount++;
                        }
                    }
                }
            }
        } else if (aBaseMetaTileEntity.isClientSide() && GTMod.clientProxy()
            .changeDetected() == 4) {
                aBaseMetaTileEntity.issueTextureUpdate();
            }
    }

    public ForgeDirection getBendDirection(ForgeDirection dir) {
        if (dir == null || connectionCount < 2) return null;

        ForgeDirection a = connectedSides[0];
        ForgeDirection b = connectedSides[1];
        if (dir == a) {
            chainedFrontFacing = b.getOpposite();
            return b;
        }
        if (dir == b) {
            chainedFrontFacing = a.getOpposite();
            return a;
        }
        return null;
    }

    public IGregTechTileEntity bendAround(ForgeDirection inputSide) {
        ForgeDirection direction = getBendDirection(inputSide);
        if (direction == null) return null;

        ForgeDirection opposite = direction.getOpposite();
        for (short dist = 1; dist <= 129; dist++) {
            IGregTechTileEntity tGTTileEntity = getBaseMetaTileEntity()
                .getIGregTechTileEntityAtSideAndDistance(direction, dist);

            if (tGTTileEntity != null) {
                IMetaTileEntity aMetaTileEntity = tGTTileEntity.getMetaTileEntity();
                if (aMetaTileEntity != null) {
                    if (aMetaTileEntity instanceof BeamlinePipeMirror tMirror) {
                        tGTTileEntity = tMirror.bendAround(opposite);
                        if (tGTTileEntity == null) break;

                        aMetaTileEntity = tGTTileEntity.getMetaTileEntity();
                        chainedFrontFacing = tMirror.chainedFrontFacing;
                        opposite = chainedFrontFacing;
                    }

                    if (aMetaTileEntity instanceof MTEHatchInputBeamline
                        && opposite == tGTTileEntity.getFrontFacing()) {
                        return tGTTileEntity;
                    }

                    else if (aMetaTileEntity instanceof MTEBeamlinePipe beamlinePipe) {
                        beamlinePipe.markUsed();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public float getCollisionThickness() {
        return 0.6f;
    }
}
