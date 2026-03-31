package com.science.gtnl.common.part;

import java.io.IOException;
import java.util.LinkedHashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBlockStateListener;
import com.science.gtnl.common.block.blocks.tile.TileEntityBeamFormer;
import com.science.gtnl.common.item.items.ItemPartBeamFormer;
import com.science.gtnl.common.render.beamformer.BeamFormerRenderHelper;
import com.science.gtnl.common.world.WorldListener;
import com.science.gtnl.config.MainConfig;

import appeng.api.AEApi;
import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkBootingStatusChange;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.BusSupport;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.core.AELog;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class PartBeamFormer extends GTNLBasePartState implements IBlockStateListener, IGridTickable, IBeamFormer {

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
    public boolean rendererRegistered;
    @Getter
    @Setter
    public double clientOtherOffset = 0;

    public static final int[][] BOXES = { { 6, 6, 11, 10, 10, 12 }, { 6, 6, 12, 10, 10, 13 }, { 6, 5, 13, 10, 6, 14 },
        { 10, 7, 14, 11, 9, 16 }, { 7, 10, 14, 9, 11, 16 }, { 5, 7, 14, 6, 9, 16 }, { 7, 5, 14, 9, 6, 16 },
        { 6, 10, 13, 10, 11, 14 }, { 5, 6, 13, 6, 10, 14 }, { 10, 7, 12, 11, 9, 13 }, { 5, 7, 12, 6, 9, 13 },
        { 7, 5, 12, 9, 6, 13 }, { 7, 10, 12, 9, 11, 13 }, { 10, 6, 13, 11, 10, 14 } };

    public PartBeamFormer(ItemStack is) {
        super(is);
        this.getProxy()
            .setFlags(GridFlags.PREFERRED);
        this.getProxy()
            .setIdlePowerUsage(MainConfig.machine.beamFormerEnergyConsume);
    }

    @Override
    public boolean canBePlacedOn(BusSupport what) {
        return what != BusSupport.NO_PARTS;
    }

    @Override
    public IGridNode getGridNode() {
        return this.getProxy()
            .getNode();
    }

    @Override
    public void markForUpdate() {
        if (this.getHost() != null) {
            this.getHost()
                .markForUpdate();
            this.getHost()
                .markForSave();
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

    public static boolean isTranslucent(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        return block == null || !block.isOpaqueCube();
    }

    @Override
    public AECableType getCableConnectionType(final ForgeDirection dir) {
        return AECableType.SMART;
    }

    @Override
    public AEColor getColor() {
        return this.getHost()
            .getColor();
    }

    @Override
    public ForgeDirection getDirection() {
        return this.getSide();
    }

    @Override
    public World getWorld() {
        return this.getHost()
            .getTile()
            .getWorldObj();
    }

    @Override
    public boolean isValid() {
        var tile = this.getTile();
        return tile.getWorldObj() == Minecraft.getMinecraft().theWorld && !tile.isInvalid()
            && this.getHost()
                .getPart(this.getSide()) == this;
    }

    @Override
    public boolean shouldRenderBeam() {
        return !this.hideBeam && this.beamLength != 0 && this.isActive() && this.isPowered();
    }

    @Override
    public double getRenderOffset() {
        if (otherBeamFormer == null) return 0.5d;
        if (otherBeamFormer instanceof TileEntityBeamFormer) return 1;
        if (otherBeamFormer instanceof PartBeamFormer) return 0.3d;
        return 0.5d;
    }

    @Override
    public BlockPos getPos() {
        var tile = this.getHost()
            .getTile();
        return new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    @Override
    public void removeFromWorld() {
        this.unregisterListener();
        this.disconnect(null);
        this.getProxy()
            .invalidate();
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        try {
            this.getProxy()
                .getTick()
                .alertDevice(this.getGridNode());
        } catch (GridAccessException ignored) {}
    }

    @Override
    public int getLightLevel() {
        return !this.hideBeam
            && ((Platform.isClient() && this.paired) || this.beamLength != 0 || this.otherBeamFormer != null)
            && (this.isActive() && this.isPowered()) ? 15 : 0;
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

    public boolean disconnect(@Nullable BlockPos breakPos) {
        if (this.connection == null) return false;

        var newBeamA = 0;
        var newBeamB = 0;

        if (breakPos != null && this.listenerLinkedList != null) {
            var iterator = this.listenerLinkedList.long2ObjectEntrySet()
                .fastIterator();
            var hash = breakPos.asLong();
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
        this.markForUpdate();

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

        return true;
    }

    @Override
    public void onBlockChanged(BlockPos pos) {
        try {
            var isValid = isTranslucent(this.getWorld(), pos.x, pos.y, pos.z);
            if (isValid) {
                var te = this.getTile()
                    .getWorldObj()
                    .getTileEntity(pos.x, pos.y, pos.z);
                if (te instanceof IBeamFormer) {
                    isValid = false;
                } else if (te instanceof IPartHost partHost) {
                    if (partHost.getPart(this.getSide()) instanceof IBeamFormer || partHost.getPart(
                        this.getSide()
                            .getOpposite()) instanceof IBeamFormer) {
                        isValid = false;
                    }
                }
            }

            if (this.connection != null && !isValid) {
                this.disconnect(pos);
                this.getProxy()
                    .getTick()
                    .alertDevice(this.getGridNode());
            } else if (isValid && this.connection == null) {
                this.getProxy()
                    .getTick()
                    .alertDevice(this.getGridNode());
            }
        } catch (GridAccessException ignored) {}
    }

    @Override
    public boolean onPartActivate(EntityPlayer player, Vec3 pos) {
        if (Platform.isWrench(player, player.getHeldItem(), (int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord)) {
            if (Platform.isServer()) {
                this.hideBeam = !this.hideBeam;
                player.addChatMessage(
                    new ChatComponentTranslation(this.hideBeam ? "text.beam_former.hide" : "text.beam_former.show"));
                this.markForUpdate();

                if (this.otherBeamFormer != null) {
                    this.otherBeamFormer.setHideBeam(this.hideBeam);
                    this.otherBeamFormer.markForUpdate();
                }
            }
            player.swingItem();
            return !player.worldObj.isRemote;
        }
        return super.onPartActivate(player, pos);
    }

    @NotNull
    @Override
    public TickingRequest getTickingRequest(@NotNull IGridNode node) {
        return new TickingRequest(20, 300, false, true);
    }

    @MENetworkEventSubscribe
    public void onPower(MENetworkPowerStatusChange event) throws GridAccessException {
        if (!this.getProxy()
            .isReady()) return;
        this.getProxy()
            .getTick()
            .alertDevice(this.getGridNode());
    }

    @MENetworkEventSubscribe
    public void onUpdate(MENetworkBootingStatusChange event) throws GridAccessException {
        if (!this.getProxy()
            .isReady()) return;
        this.getProxy()
            .getTick()
            .alertDevice(this.getGridNode());
    }

    @NotNull
    @Override
    public TickRateModulation tickingRequest(@NotNull IGridNode node, int ticksSinceLastCall) {
        if (!this.getProxy()
            .isReady()) return TickRateModulation.SAME;

        var side = this.getSide();
        var tile = this.getTile();
        var loc = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        var world = tile.getWorldObj();
        var opposite = side.getOpposite();
        var blockSet = new LinkedHashSet<BlockPos>();

        for (int i = 0; i < MainConfig.machine.beamFormerLength; i++) {
            loc = loc.offset(side);
            TileEntity te = world.getTileEntity(loc.x, loc.y, loc.z);
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
                    } catch (final FailedConnection | NullPointerException e) {
                        AELog.error(e);
                    }
                }
                return disconnected ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
            }

            if (!isTranslucent(world, loc.x, loc.y, loc.z)) {
                return this.disconnect(loc) ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
            }
            blockSet.add(loc);
        }
        return TickRateModulation.SLOWER;
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        for (int[] b : BOXES) {
            bch.addBox(b[0], b[1], b[2], b[3], b[4], b[5]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer) {
        IIcon status = ItemPartBeamFormer.iconStatusOff;
        if (this.isActive() && this.isPowered()) {
            status = (this.connection != null || this.paired) ? ItemPartBeamFormer.iconStatusBeaming
                : ItemPartBeamFormer.iconStatusOn;
        }
        rh.setTexture(ItemPartBeamFormer.iconBase);
        for (int[] b : BOXES) {
            rh.setBounds(b[0], b[1], b[2], b[3], b[4], b[5]);
            rh.renderBlock(x, y, z, renderer);
        }
        rh.setTexture(status);
        rh.setBounds(10, 10, 12, 6, 6, 11);
        rh.renderBlock(x, y, z, renderer);
        if (!(this.isActive() && this.isPowered() && (this.connection != null || this.paired))) {
            rh.setTexture(ItemPartBeamFormer.iconPrism);
            rh.setBounds(10, 10, 12, 6, 6, 11);
            rh.renderBlock(x, y, z, renderer);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(IPartRenderHelper rh, RenderBlocks renderer) {
        rh.setTexture(ItemPartBeamFormer.iconBase);
        for (int[] b : BOXES) {
            rh.setBounds(b[0], b[1], b[2], b[3], b[4], b[5]);
            rh.renderInventoryBox(renderer);
        }
        rh.setTexture(ItemPartBeamFormer.iconStatusOff);
        rh.setBounds(10, 10, 12, 6, 6, 11);
        rh.renderInventoryBox(renderer);
        rh.setTexture(ItemPartBeamFormer.iconPrism);
        rh.setBounds(10, 10, 12, 6, 6, 11);
        rh.renderInventoryBox(renderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(final double x, final double y, final double z, final IPartRenderHelper rh,
        final RenderBlocks renderer) {
        BeamFormerRenderHelper.renderDynamic(this, x, y, z, Minecraft.getMinecraft().timer.renderPartialTicks);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requireDynamicRender() {
        if (Platform.isClient() && !this.rendererRegistered) {
            this.rendererRegistered = true;
            BeamFormerRenderHelper.init(this);
        }
        return BeamFormerRenderHelper.shouldRenderDynamic(this);
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        data.writeInt(this.beamLength);
        data.writeBoolean(this.otherBeamFormer != null);
        data.writeBoolean(this.hideBeam);
        if (otherBeamFormer == null && beamLength > 0) {
            data.writeDouble(clientOtherOffset);
        } else {
            data.writeDouble(this.otherBeamFormer != null ? this.otherBeamFormer.getRenderOffset() : 0.0);
        }
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        var shouldRedraw = super.readFromStream(data);
        this.beamLength = data.readInt();
        var wasPaired = this.paired;
        this.paired = data.readBoolean();
        if (this.paired != wasPaired) {
            var tile = this.getTile();
            Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate(
                tile.xCoord,
                tile.yCoord,
                tile.zCoord,
                tile.xCoord,
                tile.yCoord,
                tile.zCoord);
        }
        this.hideBeam = data.readBoolean();
        this.clientOtherOffset = data.readDouble();
        return shouldRedraw;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        var part = data.getCompoundTag("part");
        if (this.beamLength > 0) part.setInteger("beamLength", this.beamLength);
        if (this.hideBeam) part.setBoolean("hideBeam", true);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        var part = data.getCompoundTag("part");
        if (part.getTag("beamLength") instanceof NBTTagDouble dbl) {
            this.beamLength = (int) dbl.func_150286_g();
        } else {
            this.beamLength = part.getInteger("beamLength");
        }
        this.hideBeam = part.getBoolean("hideBeam");
    }
}
