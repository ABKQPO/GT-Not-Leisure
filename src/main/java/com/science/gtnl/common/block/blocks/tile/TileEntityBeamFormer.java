package com.science.gtnl.common.block.blocks.tile;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBlockStateListener;
import com.science.gtnl.common.beamformer.BeamFormerLink;
import com.science.gtnl.common.render.beamformer.BeamFormerRenderHelper;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkBootingStatusChange;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

public class TileEntityBeamFormer extends AENetworkTile
    implements IBlockStateListener, IGridTickable, IBeamFormer, IPowerChannelState {

    public static final int POWERED_FLAG = 1;

    @Getter
    @Setter
    public int clientFlags = 0;
    @Getter
    @Setter
    public int beamLength = 0;
    @Getter
    @Setter
    public IBeamFormer otherBeamFormer = null;
    @Getter
    @Setter
    public IGridConnection connection = null;
    public final BeamFormerLink beamLink = new BeamFormerLink(this);
    @Getter
    @Setter
    public boolean hideBeam;
    public boolean paired;
    public NBTTagCompound nbtCache = null;
    public AEColor cachedColor = AEColor.Transparent;
    @Getter
    @Setter
    private AEColor beamColor = AEColor.Transparent;
    @Getter
    @Setter
    public double clientOtherOffset = 0;

    public TileEntityBeamFormer() {
        super();
        this.getProxy()
            .setFlags(GridFlags.DENSE_CAPACITY);
        this.getProxy()
            .setIdlePowerUsage(MainConfig.machine.beamFormerEnergyConsume);
        this.getProxy()
            .setValidSides(EnumSet.noneOf(ForgeDirection.class));
    }

    @Override
    public IGridNode getGridNode() {
        return this.getProxy()
            .getNode();
    }

    @Override
    public void markForUpdate() {
        super.markForUpdate();
        if (this.worldObj != null) {
            if (!this.worldObj.isRemote) markDirty();
            this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public void sleepDevice() {
        try {
            this.getProxy()
                .getTick()
                .sleepDevice(this.getGridNode());
        } catch (GridAccessException ignored) {}
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return BeamFormerRenderHelper.getRenderBoundingBox(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        double distance = shouldRenderBeam() ? Math.max(64, beamLength + 16.0) : 64;
        return distance * distance;
    }

    @Override
    public AENetworkProxy createProxy() {
        return new AENetworkProxy(this, "proxy", GTNLItemList.BlockBeamFormer.get(1), true);
    }

    @Override
    public void onReady() {
        updateValidSides();
        super.onReady();
        this.refreshNetwork();
    }

    public void refreshNetwork() {
        updateCachedColor();
        try {
            if (this.getProxy()
                .isReady()) {
                this.getProxy()
                    .getTick()
                    .alertDevice(this.getGridNode());
            }
        } catch (GridAccessException ignored) {}
    }

    @Override
    public AEColor getColor() {
        if (worldObj != null && !worldObj.isRemote) updateCachedColor();
        return cachedColor;
    }

    @Override
    public void setOrientation(final ForgeDirection inForward, final ForgeDirection inUp) {
        boolean changed = getForward() != inForward;
        if (changed) {
            beamLink.unregister();
            beamLink.disconnect();
        }
        super.setOrientation(inForward, inUp);
        updateValidSides();
        if (changed) wakeDevice();
    }

    private void updateValidSides() {
        ForgeDirection forward = getForward();
        getProxy().setValidSides(
            forward == ForgeDirection.UNKNOWN ? EnumSet.noneOf(ForgeDirection.class)
                : EnumSet.of(forward.getOpposite()));
    }

    @Override
    public ForgeDirection getDirection() {
        return this.getForward();
    }

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    @Override
    public boolean isValid() {
        if (this.worldObj == null || this.isInvalid()) return false;
        return !this.worldObj.isRemote || this.worldObj == Minecraft.getMinecraft().theWorld;
    }

    @Override
    public boolean shouldRenderBeam() {
        return !this.hideBeam && this.paired && this.beamLength > 0 && this.isActive();
    }

    @Override
    public double getRenderOffset() {
        return 3.0d / 16.0d;
    }

    @Override
    public BlockPos getPos() {
        return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public boolean isPowered() {
        if (worldObj != null && !worldObj.isRemote) return getProxy().isPowered();
        return (this.clientFlags & POWERED_FLAG) == POWERED_FLAG;
    }

    @Override
    public boolean isActive() {
        return this.isPowered();
    }

    @NotNull
    @Override
    public AECableType getCableConnectionType(@NotNull ForgeDirection dir) {
        return AECableType.DENSE;
    }

    @Override
    public void unregisterListener() {
        beamLink.unregister();
    }

    public boolean disconnect() {
        return beamLink.disconnect();
    }

    @Override
    public void onBlockChanged(BlockPos pos) {
        beamLink.onBlockChanged(pos);
    }

    @Override
    public void wakeDevice() {
        try {
            if (getProxy().isReady()) getProxy().getTick()
                .alertDevice(getGridNode());
        } catch (GridAccessException ignored) {}
    }

    public boolean onActivate(EntityPlayer player, Vec3 pos) {
        if (!player.worldObj.isRemote) {
            this.hideBeam = !this.hideBeam;
            player.addChatMessage(
                new ChatComponentTranslation(
                    this.hideBeam ? "gtnl.machine.beam_former.beam.hidden" : "gtnl.machine.beam_former.beam.shown"));
            this.markForUpdate();
            if (this.otherBeamFormer != null) {
                this.otherBeamFormer.setHideBeam(this.hideBeam);
                this.otherBeamFormer.markForUpdate();
            }
        }
        return true;
    }

    @NotNull
    @Override
    public TickingRequest getTickingRequest(@NotNull IGridNode node) {
        return new TickingRequest(20, 300, false, true);
    }

    @MENetworkEventSubscribe
    public void onPower(MENetworkPowerStatusChange event) {
        this.refreshNetwork();
        this.markForUpdate();
    }

    @MENetworkEventSubscribe
    public void onUpdate(MENetworkBootingStatusChange event) {
        this.refreshNetwork();
        this.markForUpdate();
    }

    @MENetworkEventSubscribe
    public void onChannelChange(MENetworkChannelsChanged event) {
        this.refreshNetwork();
        this.markForUpdate();
    }

    public void updateCachedColor() {
        AENetworkProxy proxy = this.getProxy();
        if (proxy.isReady()) {
            IGridNode node = proxy.getNode();
            ForgeDirection cableSide = this.getForward()
                .getOpposite();
            for (IGridConnection conn : node.getConnections()) {
                IGridNode otherNode = (conn.a() == node) ? conn.b() : conn.a();
                var otherPos = otherNode.getGridBlock()
                    .getLocation();
                if (otherPos != null && otherPos.getWorld() == worldObj
                    && xCoord + cableSide.offsetX == otherPos.x
                    && yCoord + cableSide.offsetY == otherPos.y
                    && zCoord + cableSide.offsetZ == otherPos.z) {
                    this.cachedColor = otherNode.getGridBlock()
                        .getGridColor();
                    return;
                }
            }
        }
        this.cachedColor = AEColor.Transparent;
    }

    @NotNull
    @Override
    public TickRateModulation tickingRequest(@NotNull IGridNode node, int ticksSinceLastCall) {
        if (!getProxy().isReady()) return TickRateModulation.SAME;
        return beamLink.tick();
    }

    @Override
    public void onChunkUnload() {
        this.cleanup();
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        this.cleanup();
        super.invalidate();
    }

    @Override
    public void validate() {
        super.validate();
        if (this.nbtCache != null) {
            internalReadNBT(this.nbtCache);
            this.nbtCache = null;
        }
    }

    public void cleanup() {
        this.unregisterListener();
        this.disconnect();
    }

    @Override
    public boolean requiresTESR() {
        return true;
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeCustomNBT(NBTTagCompound data) {
        data.setBoolean("hideBeam", this.hideBeam);
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readCustomNBT(NBTTagCompound data) {
        if (this.worldObj == null) {
            this.nbtCache = (NBTTagCompound) data.copy();
            return;
        }
        internalReadNBT(data);
    }

    private void internalReadNBT(NBTTagCompound data) {
        this.beamLength = 0;
        this.paired = false;
        this.hideBeam = data.getBoolean("hideBeam");
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public void writeToNetwork(final ByteBuf data) {
        setClientFlags(0);
        try {
            if (this.getProxy()
                .getEnergy()
                .isNetworkPowered()) {
                this.setClientFlags(this.clientFlags | POWERED_FLAG);
            }

            this.setClientFlags(this.populateFlags(this.clientFlags));
        } catch (final GridAccessException e) {
            // meh
        }

        data.writeInt(this.beamLength);
        data.writeBoolean(this.otherBeamFormer != null);
        data.writeBoolean(this.hideBeam);
        data.writeByte((byte) this.clientFlags);
        data.writeByte((byte) this.cachedColor.ordinal());
        data.writeDouble(clientOtherOffset);
        data.writeByte(beamColor.ordinal());
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public boolean readFromNetwork(final ByteBuf data) {
        int oldBeamLength = this.beamLength;
        boolean oldPaired = this.paired;
        boolean oldHideBeam = this.hideBeam;
        int oldFlags = this.clientFlags;
        AEColor oldColor = this.cachedColor;
        AEColor oldBeamColor = this.beamColor;
        double oldOtherOffset = this.clientOtherOffset;

        this.beamLength = data.readInt();
        this.paired = data.readBoolean();
        this.hideBeam = data.readBoolean();
        this.clientFlags = data.readByte();
        this.cachedColor = AEColor.fromOrdinal(data.readUnsignedByte());
        this.clientOtherOffset = data.readDouble();
        this.beamColor = AEColor.fromOrdinal(data.readUnsignedByte());

        if (this.paired != oldPaired || oldFlags != this.clientFlags) {
            this.worldObj.markBlockRangeForRenderUpdate(
                this.xCoord,
                this.yCoord,
                this.zCoord,
                this.xCoord,
                this.yCoord,
                this.zCoord);
        }

        return oldBeamLength != this.beamLength || oldPaired != this.paired
            || oldHideBeam != this.hideBeam
            || oldFlags != this.clientFlags
            || oldColor != this.cachedColor
            || oldBeamColor != this.beamColor
            || oldOtherOffset != this.clientOtherOffset;
    }

    public int populateFlags(final int cf) {
        return cf;
    }
}
