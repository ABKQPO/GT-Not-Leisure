package com.science.gtnl.common.wireless;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.wireless.WirelessCardClientView;
import com.science.gtnl.common.item.items.WirelessFrequencyCard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WirelessCardContainer extends ModularContainer {

    public final ViewSync sync = new ViewSync().allowC2S();

    private static final AtomicLong SESSIONS = new AtomicLong();
    public final EntityPlayer player;
    public ItemStack card;
    private final int cardSlot;
    private final IInventory cardInventory;
    private final boolean heldOnly;
    public final WirelessCardBinding binding;
    private final long session = SESSIONS.incrementAndGet();
    public long revision;
    public final WirelessCardViewBuilder view = new WirelessCardViewBuilder();
    private final WirelessCardActionHandler actions = new WirelessCardActionHandler(this);
    private int ticks;
    public WirelessCardSnapshot snapshot;
    public NBTTagList pendingHighlight;
    public volatile NBTTagCompound display = new NBTTagCompound();
    @SideOnly(Side.CLIENT)
    private WirelessCardClientView clientView;

    @SideOnly(Side.CLIENT)
    public WirelessCardClientView clientView() {
        if (clientView == null) clientView = new WirelessCardClientView(this);
        return clientView;
    }

    public WirelessCardContainer(EntityPlayer player, int location) {
        this.player = player;
        heldOnly = location == 0;
        var slot = WirelessCardInventory.resolve(player, location);
        cardInventory = slot == null ? null : slot.inventory();
        cardSlot = slot == null ? -1 : slot.index();
        card = slot == null ? null : cardInventory.getStackInSlot(cardSlot);
        binding = card == null ? null : WirelessCardBinding.read(card.getTagCompound());
        // NetHandlerPlayServer queries this slot after onItemRightClick returns, even for an item GUI.
        if (slot != null) addSlotToContainer(new Slot(cardInventory, cardSlot, -10000, -10000) {

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

    @Override
    public boolean canInteractWith(EntityPlayer user) {
        ItemStack held = cardInventory == null ? null : cardInventory.getStackInSlot(cardSlot);
        boolean valid = user == player && !user.isDead
            && card != null
            && card.getItem() instanceof WirelessFrequencyCard
            && matchesHeldCard(cardSlot, heldOnly ? user.inventory.currentItem : cardSlot, card, held)
            && Objects.equals(binding, WirelessCardBinding.read(held.getTagCompound()))
            && (binding == null || binding.belongsTo(user.getUniqueID()));
        // Vanilla replaces the held stack with an equal copy at the end of every right-click packet.
        // Always mutate the live inventory stack, not the object captured before that copy.
        if (valid) card = held;
        return valid;
    }

    private static boolean matchesHeldCard(int expectedSlot, int selectedSlot, ItemStack expected, ItemStack current) {
        return expectedSlot == selectedSlot && expected != null
            && current != null
            && current.stackSize == 1
            && ItemStack.areItemStacksEqual(expected, current);
    }

    @Override
    public ItemStack slotClick(int slot, int button, int mode, EntityPlayer user) {
        return null;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer user, int slot) {
        return null;
    }

    void update(boolean force) {
        if (!(player instanceof EntityPlayerMP)) return;
        var next = view.build(binding, card);
        boolean changed = force || !next.sameView(snapshot);
        snapshot = next;
        if (changed) revision++;
        if (changed || pendingHighlight != null) {
            NBTTagCompound data = next.packet(session, revision, pendingHighlight);
            pendingHighlight = null;
            sendSnapshot(data);
        }
    }

    private void sendSnapshot(NBTTagCompound data) {
        try {
            byte[] compressed = CompressedStreamTools.compress(data);
            long sequence = revision;
            for (int offset = 0; offset < compressed.length; offset += WirelessCardSnapshot.CHUNK_BYTES) {
                int start = offset;
                byte[] chunk = Arrays.copyOfRange(
                    compressed,
                    start,
                    Math.min(compressed.length, start + WirelessCardSnapshot.CHUNK_BYTES));
                sync.syncToClient(0, buf -> {
                    buf.writeLong(sequence);
                    buf.writeInt(compressed.length);
                    buf.writeInt(start);
                    buf.writeShort(chunk.length);
                    buf.writeBytes(chunk);
                });
            }
        } catch (IOException failure) {
            ScienceNotLeisure.LOG.error("Cannot encode wireless card view", failure);
        }
    }

    public class ViewSync extends SyncHandler<ViewSync> {

        @Override
        public void detectAndSendChanges(boolean init) {
            if ((init || ++ticks % 20 == 0) && canInteractWith(player)) update(init);
        }

        @SideOnly(Side.CLIENT)
        public void request(int action, long target, String text, long nameRevision) {
            if (!isValid() || !display.hasKey("session")) return;
            var request = new Action(
                display.getLong("session"),
                display.getLong("revision"),
                action,
                target,
                text,
                nameRevision);
            syncToServer(1, request::write);
        }

        @Override
        public void readOnServer(int id, PacketBuffer buf) {
            if (id != 1) return;
            final Action request;
            try {
                request = Action.read(buf);
            } catch (IOException | RuntimeException malformed) {
                return;
            }
            if (request.text() == null || request.text()
                .length() > 32) return;
            ServerThreadUtil.addScheduledTask(() -> {
                if (isValid() && player.openContainer == WirelessCardContainer.this
                    && canInteractWith(player)
                    && session == request.session()) {
                    actions.execute(
                        request.revision(),
                        request.action(),
                        request.target(),
                        request.text(),
                        request.nameRevision());
                }
            });
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void readOnClient(int id, PacketBuffer buf) {
            if (id != 0) return;
            long sequence = buf.readLong();
            int total = buf.readInt(), offset = buf.readInt(), length = buf.readUnsignedShort();
            if (total <= 0 || total > 64 * 1024 * 1024
                || offset < 0
                || offset > total
                || length == 0
                || length > WirelessCardSnapshot.CHUNK_BYTES
                || length > total - offset
                || length > buf.readableBytes()) return;
            byte[] chunk = new byte[length];
            buf.readBytes(chunk);
            Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.func_152344_a(() -> {
                if (!isValid() || minecraft.thePlayer != player || player.openContainer != WirelessCardContainer.this)
                    return;
                clientView().acceptChunk(sequence, total, offset, chunk);
            });
        }
    }

    record Action(long session, long revision, int action, long target, String text, long nameRevision) {

        static Action read(PacketBuffer buf) throws IOException {
            return new Action(
                buf.readLong(),
                buf.readLong(),
                buf.readInt(),
                buf.readLong(),
                buf.readStringFromBuffer(32),
                buf.readLong());
        }

        void write(PacketBuffer buf) throws IOException {
            buf.writeLong(session);
            buf.writeLong(revision);
            buf.writeInt(action);
            buf.writeLong(target);
            buf.writeStringToBuffer(text);
            buf.writeLong(nameRevision);
        }
    }
}
