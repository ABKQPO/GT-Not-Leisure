package com.science.gtnl.common.part;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.science.gtnl.api.IBeamFormer;
import com.science.gtnl.api.IBlockStateListener;
import com.science.gtnl.common.beamformer.BeamFormerLink;
import com.science.gtnl.common.item.items.ItemPartBeamFormer;
import com.science.gtnl.common.render.beamformer.BeamFormerRenderHelper;
import com.science.gtnl.config.MainConfig;

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
import appeng.api.parts.IPartRenderHelper;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
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
    public final BeamFormerLink beamLink = new BeamFormerLink(this);
    @Getter
    @Setter
    public boolean hideBeam;
    public boolean paired;
    public boolean rendererRegistered;
    @Getter
    @Setter
    public double clientOtherOffset = 0;
    @Getter
    @Setter
    private AEColor beamColor = AEColor.Transparent;

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
        var tile = getTile();
        return tile == null ? null : tile.getWorldObj();
    }

    @Override
    public boolean isValid() {
        var tile = this.getTile();
        return tile != null && tile.getWorldObj() != null
            && !tile.isInvalid()
            && this.getHost()
                .getPart(this.getSide()) == this;
    }

    @Override
    public boolean shouldRenderBeam() {
        return !this.hideBeam && this.paired && this.beamLength > 0 && this.isActive();
    }

    @Override
    public boolean isPowered() {
        World world = getWorld();
        return world != null && (world.isRemote ? super.isPowered() : getProxy().isPowered());
    }

    @Override
    public double getRenderOffset() {
        return 0.25d;
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
        this.disconnect();
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

    @Override
    public boolean onPartActivate(EntityPlayer player, Vec3 pos) {
        if (Platform.isWrench(player, player.getHeldItem(), (int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord)) {
            if (Platform.isServer()) {
                this.hideBeam = !this.hideBeam;
                player.addChatMessage(
                    new ChatComponentTranslation(
                        this.hideBeam ? "gtnl.machine.beam_former.beam.hidden"
                            : "gtnl.machine.beam_former.beam.shown"));
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
        if (!getProxy().isReady()) return TickRateModulation.SAME;
        return beamLink.tick();
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
        rh.setBounds(6, 6, 11, 10, 10, 12);
        rh.renderBlock(x, y, z, renderer);
        if (!(this.isActive() && this.isPowered() && (this.connection != null || this.paired))) {
            rh.setTexture(ItemPartBeamFormer.iconPrism);
            rh.setBounds(6, 6, 11, 10, 10, 12);
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
        rh.setBounds(6, 6, 11, 10, 10, 12);
        rh.renderInventoryBox(renderer);
        rh.setTexture(ItemPartBeamFormer.iconPrism);
        rh.setBounds(6, 6, 11, 10, 10, 12);
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
        data.writeDouble(clientOtherOffset);
        data.writeByte(beamColor.ordinal());
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        boolean shouldRedraw = super.readFromStream(data);
        boolean wasPaired = this.paired;
        boolean wasHidden = this.hideBeam;
        int oldLength = this.beamLength;
        AEColor oldColor = this.beamColor;
        this.beamLength = data.readInt();
        this.paired = data.readBoolean();
        this.hideBeam = data.readBoolean();
        this.clientOtherOffset = data.readDouble();
        this.beamColor = AEColor.fromOrdinal(data.readUnsignedByte());
        return shouldRedraw || wasPaired != paired
            || wasHidden != hideBeam
            || oldLength != beamLength
            || oldColor != beamColor;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        var part = data.getCompoundTag("part");
        part.setBoolean("hideBeam", this.hideBeam);
        data.setTag("part", part);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        var part = data.getCompoundTag("part");
        this.beamLength = 0;
        this.hideBeam = part.getBoolean("hideBeam");
    }
}
