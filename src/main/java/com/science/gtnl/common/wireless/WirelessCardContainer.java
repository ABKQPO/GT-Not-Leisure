package com.science.gtnl.common.wireless;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.item.items.WirelessFrequencyCard;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class WirelessCardContainer extends ModularContainer {

    public final ViewSync sync = new ViewSync().allowC2S();

    private static final AtomicLong SESSIONS = new AtomicLong();
    final EntityPlayer player;
    ItemStack card;
    private final int cardSlot;
    private final IInventory cardInventory;
    private final boolean heldOnly;
    final WirelessCardBinding binding;
    private final long session = SESSIONS.incrementAndGet();
    long revision;
    final WirelessCardViewBuilder view = new WirelessCardViewBuilder();
    private final WirelessCardActionHandler actions = new WirelessCardActionHandler(this);
    private int ticks;
    WirelessCardSnapshot snapshot;
    NBTTagList pendingHighlight;
    public volatile NBTTagCompound display = new NBTTagCompound();

    public WirelessCardContainer(EntityPlayer player, int location) {
        this.player = player;
        heldOnly = location == 0;
        var slot = WirelessCardInventory.resolve(player, location);
        cardInventory = slot == null ? null : slot.inventory();
        cardSlot = slot == null ? -1 : slot.index();
        card = slot == null ? null : cardInventory.getStackInSlot(cardSlot);
        binding = card == null ? null : WirelessCardBinding.read(card.getTagCompound());
        view.navigation.collapseTypes = WirelessCardBinding.collapseTypes(card == null ? null : card.getTagCompound());
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

    static boolean matchesHeldCard(int expectedSlot, int selectedSlot, ItemStack expected, ItemStack current) {
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
        var next = view
            .build(binding, card, session, data -> sync.syncToClient(2, buf -> ByteBufUtils.writeTag(buf, data)));
        boolean changed = force || !next.sameView(snapshot);
        snapshot = next;
        if (changed) revision++;
        if (changed || pendingHighlight != null) {
            NBTTagCompound data = next.packet(session, revision, pendingHighlight);
            pendingHighlight = null;
            sync.syncToClient(0, buf -> ByteBufUtils.writeTag(buf, data));
        }
    }

    public final class ViewSync extends SyncHandler<ViewSync> {

        @Override
        public void detectAndSendChanges(boolean init) {
            if ((init || ++ticks % 20 == 0) && canInteractWith(player)) update(init);
        }

        @SideOnly(Side.CLIENT)
        public void request(int action, String text, String dimension, int filter, long nameRevision) {
            if (!isValid() || !display.hasKey("session")) return;
            var request = new Action(
                display.getLong("session"),
                display.getLong("revision"),
                action,
                text,
                dimension,
                filter,
                nameRevision);
            syncToServer(1, request::write);
        }

        @Override
        public void readOnServer(int id, PacketBuffer buf) {
            if (id == 2) {
                readSearchNames(buf);
                return;
            }
            if (id != 1) return;
            final Action request;
            try {
                request = Action.read(buf);
            } catch (RuntimeException malformed) {
                return;
            }
            if (request.text() == null || request.dimension() == null) return;
            ServerThreadUtil.addScheduledTask(() -> {
                if (isValid() && player.openContainer == WirelessCardContainer.this) {
                    action(
                        request.session(),
                        request.revision(),
                        request.action(),
                        request.text(),
                        request.dimension(),
                        request.filter(),
                        request.nameRevision());
                }
            });
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void readOnClient(int id, PacketBuffer buf) {
            if (id != 0 && id != 2) return;
            NBTTagCompound data = ByteBufUtils.readTag(buf);
            Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.func_152344_a(() -> {
                if (data == null || !isValid()
                    || minecraft.thePlayer != player
                    || player.openContainer != WirelessCardContainer.this) return;
                if (id == 2) {
                    syncToServer(2, WirelessSearchNames.localize(data)::write);
                    return;
                }
                WirelessCardViewCodec.localizeInterfaceNames(data);
                WirelessCardViewCodec.localizeDimensions(data);
                display = data;
                if (data.hasKey("highlight", 9))
                    com.science.gtnl.client.WirelessWorldHighlight.show(minecraft, data.getTagList("highlight", 10));
            });
        }

        private void readSearchNames(PacketBuffer buf) {
            try {
                var reply = WirelessSearchNames.Reply.read(buf);
                ServerThreadUtil.addScheduledTask(() -> {
                    if (isValid() && player.openContainer == WirelessCardContainer.this
                        && session == reply.session()
                        && canInteractWith(player)) {
                        view.searchNames.accept(reply);
                    }
                });
            } catch (java.io.IOException | RuntimeException malformed) {
                // Ignore malformed display-only replies.
            }
        }
    }

    record Action(long session, long revision, int action, String text, String dimension, int filter,
        long nameRevision) {

        static Action read(PacketBuffer buf) {
            return new Action(
                buf.readLong(),
                buf.readLong(),
                buf.readInt(),
                NetworkUtils.readStringSafe(buf),
                NetworkUtils.readStringSafe(buf),
                buf.readUnsignedByte(),
                buf.readLong());
        }

        void write(PacketBuffer buf) {
            buf.writeLong(session);
            buf.writeLong(revision);
            buf.writeInt(action);
            NetworkUtils.writeStringSafe(buf, text);
            NetworkUtils.writeStringSafe(buf, dimension);
            buf.writeByte(filter);
            buf.writeLong(nameRevision);
        }
    }

    public void action(long session, long revision, int action, String text, String dimension, int filterState,
        long nameRevision) {
        if (!canInteractWith(player) || this.session != session) return;
        actions.execute(revision, action, text, dimension, filterState, nameRevision);
    }
}
