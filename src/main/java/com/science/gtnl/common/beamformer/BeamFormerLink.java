package com.science.gtnl.common.beamformer;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBlockStateListener;
import com.science.gtnl.common.world.WorldListener;
import com.science.gtnl.config.MainConfig;

import appeng.api.AEApi;
import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.parts.IPartHost;
import appeng.api.util.AEColor;
import appeng.core.AELog;

public class BeamFormerLink implements IBlockStateListener {

    private final IBeamFormer owner;
    private World watchedWorld;
    private BlockPos watchedOrigin;
    private ForgeDirection watchedDirection;
    private int watchedDistance = -1;
    private boolean dirty = true;

    public BeamFormerLink(IBeamFormer owner) {
        this.owner = owner;
    }

    public TickRateModulation tick() {
        World world = owner.getWorld();
        if (world == null || world.isRemote || !owner.isValid()) return TickRateModulation.SLEEP;
        IBeamFormer other = owner.getOtherBeamFormer();
        IGridNode node = owner.getGridNode();
        if (!dirty && other != null
            && other.isValid()
            && other.getOtherBeamFormer() == owner
            && owner.getConnection() != null
            && node != null
            && node.getConnections()
                .contains(owner.getConnection())) {
            AEColor color = connectionColor(other);
            if (color != null) {
                updateBeamColor(other, color);
                return TickRateModulation.SLEEP;
            }
        }
        dirty = false;
        BlockPos origin = owner.getPos();
        ForgeDirection direction = owner.getDirection();
        int x = origin.x;
        int y = origin.y;
        int z = origin.z;
        int distance = 0;
        IBeamFormer target = null;
        int limit = Math.max(0, MainConfig.machine.beamFormerLength);
        if (direction != ForgeDirection.UNKNOWN) {
            for (int step = 0; step < limit; step++) {
                x += direction.offsetX;
                y += direction.offsetY;
                z += direction.offsetZ;
                distance = step + 1;
                // Never load a chunk as a side effect of finding a beam endpoint.
                if (!world.blockExists(x, y, z)) break;
                Block block = world.getBlock(x, y, z);
                if (block.hasTileEntity(world.getBlockMetadata(x, y, z))) {
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile instanceof IBeamFormer former) {
                        if (former.getDirection() == direction.getOpposite()) target = former;
                        break;
                    }
                    if (tile instanceof IPartHost host) {
                        if (host.getPart(direction.getOpposite()) instanceof IBeamFormer former) {
                            target = former;
                            break;
                        }
                        if (host.getPart(direction) instanceof IBeamFormer) break;
                    }
                }
                if (block.isOpaqueCube()) break;
            }
        }
        watch(world, origin, direction, distance);
        AEColor targetColor = target != null && target.isValid() ? connectionColor(target) : null;
        if (target != null && target == other
            && targetColor != null
            && owner.getConnection() != null
            && node != null
            && node.getConnections()
                .contains(owner.getConnection())
            && target.getOtherBeamFormer() == owner) {
            updateBeamColor(target, targetColor);
            return TickRateModulation.SLEEP;
        }
        disconnect();
        if (targetColor == null) return TickRateModulation.SLEEP;
        if (target.getOtherBeamFormer() == null) {
            IGridNode sourceNode = owner.getGridNode();
            IGridNode targetNode = target.getGridNode();
            if (sourceNode != null && targetNode != null && sourceNode != targetNode) {
                try {
                    connect(target, distance, targetColor);
                    return TickRateModulation.SLEEP;
                } catch (FailedConnection exception) {
                    AELog.error(exception);
                }
            }
        }
        // Settled paths wake on block or chunk events; unavailable grid nodes still need a retry.
        return TickRateModulation.SLOWER;
    }

    private AEColor connectionColor(IBeamFormer target) {
        AEColor sourceColor = owner.getColor();
        AEColor targetColor = target.getColor();
        if (!sourceColor.matches(targetColor)) return null;
        return sourceColor == AEColor.Transparent ? targetColor : sourceColor;
    }

    private void updateBeamColor(IBeamFormer target, AEColor color) {
        if (owner.getBeamColor() != color) {
            owner.setBeamColor(color);
            owner.markForUpdate();
        }
        if (target.getBeamColor() != color) {
            target.setBeamColor(color);
            target.markForUpdate();
        }
    }

    private void connect(IBeamFormer target, int distance, AEColor color) throws FailedConnection {
        IGridConnection connection = AEApi.instance()
            .createGridConnection(owner.getGridNode(), target.getGridNode());
        owner.setConnection(connection);
        target.setConnection(connection);
        owner.setOtherBeamFormer(target);
        target.setOtherBeamFormer(owner);
        owner.setBeamColor(color);
        target.setBeamColor(color);
        boolean hidden = owner.isHideBeam() || target.isHideBeam();
        owner.setHideBeam(hidden);
        target.setHideBeam(hidden);
        // Length is the center-to-center distance, including adjacent endpoints.
        owner.setBeamLength(distance);
        target.setBeamLength(0);
        owner.setClientOtherOffset(target.getRenderOffset());
        target.setClientOtherOffset(owner.getRenderOffset());
        // Both endpoints watch their local cable so recoloring can wake either end.
        target.wakeDevice();
        owner.markForUpdate();
        target.markForUpdate();
    }

    public boolean disconnect() {
        IGridConnection connection = owner.getConnection();
        IBeamFormer other = owner.getOtherBeamFormer();
        boolean changed = connection != null || other != null || owner.getBeamLength() != 0;
        owner.setConnection(null);
        owner.setOtherBeamFormer(null);
        owner.setBeamLength(0);
        owner.setClientOtherOffset(0);
        owner.setBeamColor(AEColor.Transparent);
        boolean paired = other != null && other.getOtherBeamFormer() == owner;
        if (paired) {
            other.setConnection(null);
            other.setOtherBeamFormer(null);
            other.setBeamLength(0);
            other.setClientOtherOffset(0);
            other.setBeamColor(AEColor.Transparent);
        }
        // Clear both endpoints before grid destruction can deliver network events.
        if (connection != null) connection.destroy();
        if (changed) owner.markForUpdate();
        if (paired) {
            other.markForUpdate();
            other.wakeDevice();
        }
        return changed;
    }

    @Override
    public void onBlockChanged(BlockPos pos) {
        dirty = true;
        owner.wakeDevice();
    }

    public void unregister() {
        if (watchedWorld != null) {
            WorldListener.INSTANCE.unregisterBlockStateListener(watchedWorld, this);
        }
        watchedWorld = null;
        watchedOrigin = null;
        watchedDirection = null;
        watchedDistance = -1;
        dirty = true;
    }

    private void watch(World world, BlockPos origin, ForgeDirection direction, int distance) {
        if (watchedWorld == world && origin.equals(watchedOrigin)
            && watchedDirection == direction
            && watchedDistance == distance) return;
        unregister();
        watchedWorld = world;
        watchedOrigin = origin;
        watchedDirection = direction;
        watchedDistance = distance;
        if (direction != ForgeDirection.UNKNOWN) {
            // Include the host and its rear cable as well as the optical path.
            BlockPos start = new BlockPos(
                origin.x - direction.offsetX * 2,
                origin.y - direction.offsetY * 2,
                origin.z - direction.offsetZ * 2);
            WorldListener.INSTANCE.registerBlockStateListener(world, this, start, direction, distance + 2);
        }
        dirty = false;
    }
}
