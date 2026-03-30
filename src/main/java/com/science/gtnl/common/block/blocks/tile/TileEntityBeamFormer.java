package com.science.gtnl.common.block.blocks.tile;

import java.util.EnumSet;
import java.util.LinkedHashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBlockStateListener;
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
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.core.AELog;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class TileEntityBeamFormer extends AENetworkTile
    implements IBlockStateListener, IGridTickable, IBeamFormer, IPowerChannelState {

    public static final int POWERED_FLAG = 1;
    @Getter
    @Setter
    public int clientFlags = 0; // sent as byte.

    @Getter
    public int beamLength = 0;
    public TileEntityBeamFormer otherBeamFormer = null;
    public IGridConnection connection = null;
    public Long2ObjectLinkedOpenHashMap<BlockPos> listenerLinkedList = null;

    public boolean hideBeam;
    public boolean paired;

    public NBTTagCompound nbtCache = null;

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
    public AENetworkProxy createProxy() {
        return new AENetworkProxy(this, "proxy", GTNLItemList.BlockBeamFormer.get(1), true);
    }

    @Override
    public void onReady() {
        super.onReady();
        this.refreshNetwork();
    }

    public void refreshNetwork() {
        try {
            if (this.getProxy()
                .isReady()) {
                this.getProxy()
                    .getTick()
                    .alertDevice(
                        this.getProxy()
                            .getNode());
            }
        } catch (GridAccessException ignored) {}
    }

    @Override
    public AEColor getColor() {
        return getProxy().getGridColor();
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
    public BlockPos getPos() {
        return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public boolean isPowered() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            return getProxy() != null && getProxy().isPowered();
        }
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

    public void unregisterListener() {
        WorldListener.instance.unregisterBlockStateListener(this);
    }

    public void connect(TileEntityBeamFormer potentialFormer, Iterable<BlockPos> locs) throws FailedConnection {
        var myProxy = this.getProxy();
        var otherProxy = potentialFormer.getProxy();

        if (potentialFormer == this || myProxy == null || otherProxy == null) return;
        var myNode = myProxy.getNode();
        var otherNode = otherProxy.getNode();
        if (myNode == null || otherNode == null || myNode == otherNode) return;

        this.connection = AEApi.instance()
            .createGridConnection(myNode, otherNode);
        potentialFormer.connection = this.connection;
        this.otherBeamFormer = potentialFormer;
        potentialFormer.otherBeamFormer = this;

        if (potentialFormer.hideBeam || this.hideBeam) {
            potentialFormer.hideBeam = this.hideBeam = true;
        }

        this.unregisterListener();
        this.otherBeamFormer.unregisterListener();

        this.listenerLinkedList = new Long2ObjectLinkedOpenHashMap<>();
        for (var loc : locs) this.listenerLinkedList.put(loc.asLong(), loc);

        WorldListener.instance.registerBlockStateListener(this, locs);
        this.beamLength = this.listenerLinkedList.size();
        this.otherBeamFormer.beamLength = 0;

        try {
            this.otherBeamFormer.getProxy()
                .getTick()
                .sleepDevice(otherNode);
        } catch (GridAccessException ignored) {}

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

        if (this.otherBeamFormer != null && this.otherBeamFormer.otherBeamFormer == this) {
            this.otherBeamFormer.beamLength = newBeamB;
            this.otherBeamFormer.connection = null;
            this.otherBeamFormer.otherBeamFormer = null;
            this.otherBeamFormer.markForUpdate();
            this.otherBeamFormer = null;
        }

        this.markForUpdate();
        return true;
    }

    @Override
    public void onBlockChanged(BlockPos pos) {
        try {
            boolean isValid = isTranslucent(this.worldObj, pos.x, pos.y, pos.z);
            if (isValid && this.worldObj.getTileEntity(pos.x, pos.y, pos.z) instanceof TileEntityBeamFormer) {
                isValid = false;
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
            if (this.otherBeamFormer != null) {
                this.otherBeamFormer.hideBeam = this.hideBeam;
                this.otherBeamFormer.markForUpdate();
            }
            this.markForUpdate();
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

    @NotNull
    @Override
    public TickRateModulation tickingRequest(@NotNull IGridNode node, int ticksSinceLastCall) {
        if (!this.getProxy()
            .isReady()) return TickRateModulation.SAME;

        ForgeDirection forward = this.getForward();
        if (forward == null || forward == ForgeDirection.UNKNOWN) return TickRateModulation.SAME;

        boolean isConnectionValid = this.connection != null;
        ForgeDirection opposite = forward.getOpposite();
        BlockPos loc = this.getPos();
        LinkedHashSet<BlockPos> blockSet = new LinkedHashSet<>();

        for (int i = 0; i < MainConfig.machine.beamFormerLength; i++) {
            loc = loc.offset(forward);
            TileEntity te = this.worldObj.getTileEntity(loc.x, loc.y, loc.z);

            if (te instanceof TileEntityBeamFormer potentialFormer) {
                if (potentialFormer == this) continue;

                if (potentialFormer.getForward() == opposite) {
                    if (isConnectionValid && potentialFormer == this.otherBeamFormer) return TickRateModulation.SLEEP;

                    boolean disconnected = this.disconnect(loc);
                    if (potentialFormer.getProxy()
                        .isReady() && potentialFormer.otherBeamFormer == null) {
                        try {
                            this.connect(potentialFormer, blockSet);
                            return TickRateModulation.SLEEP;
                        } catch (FailedConnection | NullPointerException e) {
                            AELog.error(e);
                        }
                    }
                    return disconnected ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
                }
                return this.disconnect(loc) ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
            }

            if (!isTranslucent(this.worldObj, loc.x, loc.y, loc.z)) {
                return this.disconnect(loc) ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
            }
            blockSet.add(loc);
        }
        return TickRateModulation.SLOWER;
    }

    public int getLightLevel() {
        return !this.hideBeam && (this.beamLength != 0 || this.otherBeamFormer != null) && this.isActive() ? 15 : 0;
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
        int flags = 0;
        try {
            if (this.getProxy()
                .isReady()
                && this.getProxy()
                    .getEnergy()
                    .isNetworkPowered()) {
                flags |= POWERED_FLAG;
            }
        } catch (GridAccessException ignored) {}
        this.clientFlags = flags;

        if (this.beamLength > 0) data.setInteger("beamLength", this.beamLength);
        if (this.hideBeam) data.setBoolean("hideBeam", true);
        data.setBoolean("paired", this.otherBeamFormer != null);
        data.setByte("cf", (byte) this.clientFlags);
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
        int oldFlags = this.clientFlags;

        if (data.getTag("beamLength") instanceof NBTTagDouble dbl) {
            this.beamLength = (int) dbl.func_150286_g();
        } else {
            this.beamLength = data.getInteger("beamLength");
        }
        this.hideBeam = data.getBoolean("hideBeam");
        this.paired = data.getBoolean("paired");
        this.clientFlags = data.getByte("cf");

        if (this.worldObj != null && (this.paired != oldPaired || oldFlags != this.clientFlags)) {
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
        int flags = 0;
        try {
            if (this.getProxy()
                .isReady()
                && this.getProxy()
                    .getEnergy()
                    .isNetworkPowered()) {
                flags |= POWERED_FLAG;
            }
        } catch (GridAccessException ignored) {}
        this.clientFlags = flags;

        data.writeInt(this.beamLength);
        data.writeBoolean(this.otherBeamFormer != null);
        data.writeBoolean(this.hideBeam);
        data.writeByte((byte) this.clientFlags);
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public boolean readFromNetwork(final ByteBuf data) {
        int oldBeamLength = this.beamLength;
        boolean oldPaired = this.paired;
        boolean oldHideBeam = this.hideBeam;
        int oldFlags = this.clientFlags;

        this.beamLength = data.readInt();
        this.paired = data.readBoolean();
        this.hideBeam = data.readBoolean();
        this.clientFlags = data.readByte();

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
            || oldFlags != this.clientFlags;
    }
}
