package com.science.gtnl.common.block.blocks.tile;

import java.util.EnumSet;
import java.util.LinkedHashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBlockStateListener;
import com.science.gtnl.common.part.PartBeamFormer;
import com.science.gtnl.common.world.WorldListener;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;
import appeng.api.exceptions.FailedConnection;
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
import appeng.api.parts.IPartHost;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.core.AELog;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
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
    public Long2ObjectLinkedOpenHashMap<BlockPos> listenerLinkedList = null;
    @Getter
    @Setter
    public boolean hideBeam;
    public boolean paired;
    public NBTTagCompound nbtCache = null;
    public AEColor cachedColor = AEColor.Transparent;
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
            .setValidSides(EnumSet.allOf(ForgeDirection.class));
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
        return AxisAlignedBB.getBoundingBox(
            this.xCoord - 50,
            this.yCoord - 50,
            this.zCoord - 50,
            this.xCoord + 50,
            this.yCoord + 50,
            this.zCoord + 50);
    }

    @Override
    public AENetworkProxy createProxy() {
        return new AENetworkProxy(this, "proxy", GTNLItemList.BlockBeamFormer.get(1), true);
    }

    @Override
    public void onReady() {
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
        return cachedColor;
    }

    @Override
    public void setOrientation(final ForgeDirection inForward, final ForgeDirection inUp) {
        super.setOrientation(inForward, inUp);
        this.getProxy()
            .setValidSides(
                EnumSet.of(
                    this.getForward()
                        .getOpposite()));
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
        return !this.hideBeam && this.beamLength != 0 && this.isActive();
    }

    @Override
    public double getRenderOffset() {
        if (otherBeamFormer == null) return 0.5d;
        if (otherBeamFormer instanceof TileEntityBeamFormer) return 1.7d;
        if (otherBeamFormer instanceof PartBeamFormer) return 1;
        return 0.5d;
    }

    @Override
    public BlockPos getPos() {
        return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public boolean isPowered() {
        return (this.clientFlags & POWERED_FLAG) == POWERED_FLAG;
    }

    @Override
    public boolean isActive() {
        return this.isPowered();
    }

    public static boolean isTranslucent(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        return block == null || !block.isOpaqueCube();
    }

    @NotNull
    @Override
    public AECableType getCableConnectionType(@NotNull ForgeDirection dir) {
        return AECableType.DENSE;
    }

    @Override
    public void unregisterListener() {
        WorldListener.instance.unregisterBlockStateListener(this);
    }

    public void connect(IBeamFormer potentialFormer, Iterable<BlockPos> locs) throws FailedConnection {
        var myNode = this.getGridNode();
        this.connection = AEApi.instance()
            .createGridConnection(myNode, potentialFormer.getGridNode());

        potentialFormer.setConnection(this.connection);
        this.otherBeamFormer = potentialFormer;
        potentialFormer.setOtherBeamFormer(this);

        if (potentialFormer.isHideBeam() || this.hideBeam) {
            potentialFormer.setHideBeam(true);
            this.hideBeam = true;
        }

        this.unregisterListener();
        potentialFormer.unregisterListener();

        this.listenerLinkedList = new Long2ObjectLinkedOpenHashMap<>();
        for (var loc : locs) this.listenerLinkedList.put(loc.asLong(), loc);

        WorldListener.instance.registerBlockStateListener(this, locs);
        this.beamLength = this.listenerLinkedList.size();
        potentialFormer.setBeamLength(0);

        potentialFormer.sleepDevice();

        this.markForUpdate();
        potentialFormer.markForUpdate();
    }

    public boolean disconnect(BlockPos breakPos) {
        if (this.connection == null) return false;

        int newBeamA = 0;
        int newBeamB = 0;

        if (breakPos != null && this.listenerLinkedList != null) {
            var iterator = this.listenerLinkedList.long2ObjectEntrySet()
                .fastIterator();
            long hash = breakPos.asLong();
            while (iterator.hasNext()) {
                if (iterator.next()
                    .getLongKey() == hash) break;
                newBeamA++;
            }
            while (iterator.hasNext()) {
                iterator.next();
                newBeamB++;
            }
        }

        this.beamLength = newBeamA;
        if (this.connection != null) {
            this.connection.destroy();
            this.connection = null;
        }

        IBeamFormer other = this.otherBeamFormer;
        if (other != null && other.getOtherBeamFormer() == this) {
            other.setBeamLength(newBeamB);
            other.setConnection(null);
            other.setOtherBeamFormer(null);
            other.markForUpdate();
            other.setClientOtherOffset(0.5d);
            this.clientOtherOffset = 0.5d;
            this.otherBeamFormer = null;
        }

        this.markForUpdate();
        return true;
    }

    @Override
    public void onBlockChanged(BlockPos pos) {
        try {
            boolean isValid = isTranslucent(this.worldObj, pos.x, pos.y, pos.z);
            if (isValid) {
                TileEntity te = this.worldObj.getTileEntity(pos.x, pos.y, pos.z);
                if (te instanceof IBeamFormer) isValid = false;
                else if (te instanceof IPartHost ph) {
                    if (ph.getPart(this.getForward()) instanceof IBeamFormer || ph.getPart(
                        this.getForward()
                            .getOpposite()) instanceof IBeamFormer)
                        isValid = false;
                }
            }
            if ((this.connection != null && !isValid) || (this.connection == null && isValid)) {
                if (!isValid) this.disconnect(pos);
                this.refreshNetwork();
            }
        } catch (Exception ignored) {}
    }

    public boolean onActivate(EntityPlayer player, Vec3 pos) {
        if (!player.worldObj.isRemote) {
            this.hideBeam = !this.hideBeam;
            player.addChatMessage(
                new ChatComponentTranslation(this.hideBeam ? "text.beam_former.hide" : "text.beam_former.show"));
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
                BlockPos myPos = this.getPos();
                var otherPos = otherNode.getGridBlock()
                    .getLocation();
                if (myPos.x + cableSide.offsetX == otherPos.x && myPos.y + cableSide.offsetY == otherPos.y
                    && myPos.z + cableSide.offsetZ == otherPos.z) {
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
        if (!this.getProxy()
            .isReady()) return TickRateModulation.SAME;
        ForgeDirection forward = this.getForward();
        ForgeDirection opposite = forward.getOpposite();
        BlockPos loc = this.getPos();
        LinkedHashSet<BlockPos> blockSet = new LinkedHashSet<>();

        for (int i = 0; i < MainConfig.machine.beamFormerLength; i++) {
            loc = loc.offset(forward);
            TileEntity te = this.worldObj.getTileEntity(loc.x, loc.y, loc.z);
            IBeamFormer potentialFormer = null;

            if (te instanceof IBeamFormer ibf) {
                if (ibf.getDirection() == opposite) potentialFormer = ibf;
            } else if (te instanceof IPartHost ph) {
                if (ph.getPart(opposite) instanceof IBeamFormer ibf) potentialFormer = ibf;
            }

            if (potentialFormer != null) {
                if (this.connection != null && potentialFormer == this.otherBeamFormer) return TickRateModulation.SLEEP;
                boolean disconnected = this.disconnect(loc);
                if (potentialFormer.getGridNode() != null && potentialFormer.getOtherBeamFormer() == null) {
                    try {
                        this.connect(potentialFormer, blockSet);
                        return TickRateModulation.SLEEP;
                    } catch (FailedConnection | NullPointerException e) {
                        AELog.error(e);
                    }
                }
                return disconnected ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
            }
            if (!isTranslucent(this.worldObj, loc.x, loc.y, loc.z))
                return this.disconnect(loc) ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
            blockSet.add(loc);
        }
        return TickRateModulation.SLOWER;
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
        this.disconnect(null);
    }

    @Override
    public boolean requiresTESR() {
        return true;
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeCustomNBT(NBTTagCompound data) {
        if (this.beamLength > 0) data.setInteger("beamLength", this.beamLength);
        if (this.hideBeam) data.setBoolean("hideBeam", true);
        data.setBoolean("paired", this.otherBeamFormer != null);
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
        boolean oldPaired = this.paired;

        if (data.getTag("beamLength") instanceof NBTTagDouble dbl) {
            this.beamLength = (int) dbl.func_150286_g();
        } else {
            this.beamLength = data.getInteger("beamLength");
        }
        this.hideBeam = data.getBoolean("hideBeam");
        this.paired = data.getBoolean("paired");

        if (this.worldObj != null && (this.paired != oldPaired)) {
            this.worldObj.markBlockRangeForRenderUpdate(
                this.xCoord,
                this.yCoord,
                this.zCoord,
                this.xCoord,
                this.yCoord,
                this.zCoord);
        }
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
        if (otherBeamFormer == null && beamLength > 0) {
            data.writeDouble(clientOtherOffset);
        } else {
            data.writeDouble(this.otherBeamFormer != null ? this.otherBeamFormer.getRenderOffset() : 0.0);
        }
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public boolean readFromNetwork(final ByteBuf data) {
        int oldBeamLength = this.beamLength;
        boolean oldPaired = this.paired;
        boolean oldHideBeam = this.hideBeam;
        int oldFlags = this.clientFlags;
        AEColor oldColor = this.cachedColor;
        double oldOtherOffset = this.clientOtherOffset;

        this.beamLength = data.readInt();
        this.paired = data.readBoolean();
        this.hideBeam = data.readBoolean();
        this.clientFlags = data.readByte();
        this.cachedColor = AEColor.values()[data.readByte()];
        this.clientOtherOffset = data.readDouble();

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
            || oldOtherOffset != this.clientOtherOffset;
    }

    public int populateFlags(final int cf) {
        return cf;
    }
}
