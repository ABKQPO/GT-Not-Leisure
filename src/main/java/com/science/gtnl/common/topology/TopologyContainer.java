package com.science.gtnl.common.topology;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyDevice;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologySnapshot;
import com.science.gtnl.client.topology.TopologyViewState;
import com.science.gtnl.common.item.items.NetworkTopologyTool;
import com.science.gtnl.common.packet.WirelessHighlightPacket;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.world.BlockSideTeleport;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.Unpooled;

public class TopologyContainer extends ModularContainer {

    private static final int SNAPSHOT_PACKET = 0;
    private static final int ACTION_PACKET = 1;
    private static final int ACTION_RESCAN = 0;
    private static final int ACTION_HIGHLIGHT = 1;
    private static final int ACTION_TELEPORT = 2;

    public final SnapshotSync sync = new SnapshotSync().allowC2S();
    public final EntityPlayer player;
    public final TopologyBinding binding;

    private final int toolSlot;
    private final boolean heldOnly;
    private final TopologyScanner scanner = new TopologyScanner();

    private ItemStack tool;
    private TopologySnapshot published = TopologySnapshot.EMPTY;
    private long revision;
    private long signature;
    private int ticks;
    private boolean rescanRequested;

    @SideOnly(Side.CLIENT)
    private TopologyViewState viewState;

    public TopologyContainer(EntityPlayer player, int location) {
        this.player = player;
        this.heldOnly = location == 0;
        this.toolSlot = location == 0 ? player.inventory.currentItem : location - 1;
        this.tool = toolSlot >= 0 && toolSlot < player.inventory.getSizeInventory()
            ? player.inventory.getStackInSlot(toolSlot)
            : null;
        this.binding = tool == null ? null : TopologyBinding.read(tool.getTagCompound());
        // The vanilla right-click handler inspects the active slot after the GUI opens, even for an item GUI.
        if (toolSlot >= 0 && toolSlot < player.inventory.getSizeInventory())
            addSlotToContainer(new Slot(player.inventory, toolSlot, -10000, -10000) {

                @Override
                public boolean canTakeStack(EntityPlayer user) {
                    return false;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false;
                }
            });
    }

    @SideOnly(Side.CLIENT)
    public TopologyViewState viewState() {
        if (viewState == null) viewState = new TopologyViewState();
        return viewState;
    }

    @Override
    public boolean canInteractWith(EntityPlayer user) {
        if (user != player || user.isDead || tool == null) return false;
        ItemStack held = toolSlot >= 0 && toolSlot < user.inventory.getSizeInventory()
            ? user.inventory.getStackInSlot(toolSlot)
            : null;
        if (held == null || held.stackSize != 1 || !(held.getItem() instanceof NetworkTopologyTool)) return false;
        if (heldOnly && user.inventory.currentItem != toolSlot) return false;
        if (!Objects.equals(binding, TopologyBinding.read(held.getTagCompound()))) return false;
        if (binding != null && !binding.belongsTo(user.getUniqueID())) return false;
        // Vanilla swaps the held stack for an equal copy after every right-click packet.
        tool = held;
        return true;
    }

    @Override
    public ItemStack slotClick(int slot, int button, int mode, EntityPlayer user) {
        return null;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer user, int slot) {
        return null;
    }

    private void update(boolean force) {
        if (!(player instanceof EntityPlayerMP)) return;
        TopologySnapshot next = binding == null ? TopologySnapshot.EMPTY
            : scanner.scan(
                binding.source()
                    .node(),
                player);
        scanner.retain(next);
        long nextSignature = TopologyTransport.signature(next);
        if (!force && nextSignature == signature) return;
        signature = nextSignature;
        revision++;
        published = next.withRevision(revision);
        send(published);
    }

    private void send(TopologySnapshot snapshot) {
        PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
        final byte[] raw;
        try {
            TopologyCodec.write(payload, snapshot);
            raw = new byte[payload.readableBytes()];
            payload.readBytes(raw);
        } finally {
            payload.release();
        }
        byte[] compressed = TopologyTransport.deflate(raw);
        long sequence = revision;
        for (int offset = 0; offset < compressed.length; offset += TopologyTransport.CHUNK_BYTES) {
            int start = offset;
            byte[] chunk = Arrays
                .copyOfRange(compressed, start, Math.min(compressed.length, start + TopologyTransport.CHUNK_BYTES));
            int totalCompressed = compressed.length;
            sync.syncToClient(SNAPSHOT_PACKET, buffer -> {
                buffer.writeLong(sequence);
                buffer.writeVarIntToBuffer(raw.length);
                buffer.writeVarIntToBuffer(totalCompressed);
                buffer.writeVarIntToBuffer(start);
                buffer.writeShort(chunk.length);
                buffer.writeBytes(chunk);
            });
        }
    }

    public class SnapshotSync extends SyncHandler<SnapshotSync> {

        @Override
        public void detectAndSendChanges(boolean init) {
            int interval = Math.max(5, MainConfig.topology.rescanIntervalTicks);
            boolean due = init || rescanRequested || ++ticks % interval == 0;
            if (!due || !canInteractWith(player)) return;
            boolean force = init || rescanRequested;
            rescanRequested = false;
            update(force);
        }

        @SideOnly(Side.CLIENT)
        public void requestRescan() {
            if (isValid()) syncToServer(ACTION_PACKET, buffer -> buffer.writeByte(ACTION_RESCAN));
        }

        @Override
        public void readOnServer(int id, PacketBuffer buffer) {
            if (id != ACTION_PACKET || buffer.readableBytes() < 1) return;
            int action = buffer.readUnsignedByte();
            if (action == ACTION_RESCAN) {
                ServerThreadUtil.addScheduledTask(() -> { if (allowed()) rescanRequested = true; });
                return;
            }
            if (action != ACTION_HIGHLIGHT && action != ACTION_TELEPORT) return;
            if (buffer.readableBytes() < 3) return;
            int networkId = buffer.readVarIntFromBuffer() - 1;
            int bridgeId = buffer.readVarIntFromBuffer() - 1;
            int deviceId = buffer.readVarIntFromBuffer() - 1;
            ServerThreadUtil.addScheduledTask(() -> {
                if (!allowed()) return;
                // Resolve against the published snapshot, so a client can only act on something it was actually shown.
                GridCoord target = resolve(networkId, bridgeId, deviceId);
                if (target == null) {
                    message("target_unavailable");
                    return;
                }
                if (action == ACTION_HIGHLIGHT) highlight(target);
                else teleport(target);
            });
        }

        @SideOnly(Side.CLIENT)
        public void requestHighlight(int networkId, int bridgeId, int deviceId) {
            request(ACTION_HIGHLIGHT, networkId, bridgeId, deviceId);
        }

        @SideOnly(Side.CLIENT)
        public void requestTeleport(int networkId, int bridgeId, int deviceId) {
            request(ACTION_TELEPORT, networkId, bridgeId, deviceId);
        }

        @SideOnly(Side.CLIENT)
        private void request(int action, int networkId, int bridgeId, int deviceId) {
            if (!isValid()) return;
            syncToServer(ACTION_PACKET, buffer -> {
                buffer.writeByte(action);
                buffer.writeVarIntToBuffer(networkId + 1);
                buffer.writeVarIntToBuffer(bridgeId + 1);
                buffer.writeVarIntToBuffer(deviceId + 1);
            });
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void readOnClient(int id, PacketBuffer buffer) {
            if (id != SNAPSHOT_PACKET) return;
            long sequence = buffer.readLong();
            int rawLength = buffer.readVarIntFromBuffer();
            int total = buffer.readVarIntFromBuffer();
            int offset = buffer.readVarIntFromBuffer();
            int length = buffer.readUnsignedShort();
            if (rawLength <= 0 || rawLength > TopologyTransport.MAX_PAYLOAD_BYTES
                || total <= 0
                || total > TopologyTransport.MAX_PAYLOAD_BYTES
                || offset < 0
                || offset > total
                || length == 0
                || length > TopologyTransport.CHUNK_BYTES
                || length > total - offset
                || length > buffer.readableBytes()) return;
            byte[] chunk = new byte[length];
            buffer.readBytes(chunk);
            Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.func_152344_a(() -> {
                if (!isValid() || minecraft.thePlayer != player || player.openContainer != TopologyContainer.this)
                    return;
                viewState().acceptChunk(sequence, rawLength, total, offset, chunk);
            });
        }
    }

    private boolean allowed() {
        return sync.isValid() && player.openContainer == this && canInteractWith(player);
    }

    private GridCoord resolve(int networkId, int bridgeId, int deviceId) {
        if (deviceId >= 0) {
            TopologyDevice device = published.device(deviceId);
            return device == null ? null : device.position();
        }
        if (bridgeId >= 0) {
            TopologyBridge bridge = published.bridge(bridgeId);
            return bridge == null ? null : bridge.origin();
        }
        TopologyNetwork network = published.network(networkId);
        return network == null ? null : network.pivot();
    }

    private void highlight(GridCoord target) {
        if (!(player instanceof EntityPlayerMP serverPlayer)) return;
        ScienceNotLeisure.network
            .sendTo(new WirelessHighlightPacket(target.dimension(), target.x(), target.y(), target.z()), serverPlayer);
        message(
            player.dimension == target.dimension() ? "highlighted" : "highlighted_other_dimension",
            target.dimension(),
            target.x(),
            target.y(),
            target.z());
    }

    private void teleport(GridCoord target) {
        if (!(player instanceof EntityPlayerMP serverPlayer)) return;
        ForgeDirection side = target.side() == null ? ForgeDirection.UNKNOWN : target.side();
        BlockSideTeleport.Outcome outcome = BlockSideTeleport
            .toBlockSide(serverPlayer, target.dimension(), target.x(), target.y(), target.z(), side);
        message(switch (outcome) {
            case MOVED -> "teleport_done";
            case UNAVAILABLE -> "target_unavailable";
            case NO_SPACE -> "teleport_no_space";
            default -> "teleport_failed";
        }, target.dimension(), target.x(), target.y(), target.z());
    }

    private void message(String key, Object... args) {
        player.addChatMessage(new ChatComponentTranslation("gtnl.topology.gui." + key, args));
    }

    public static TopologySnapshot decode(byte[] raw) throws IOException {
        PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(raw));
        try {
            return TopologyCodec.read(buffer);
        } finally {
            buffer.release();
        }
    }
}
