package com.science.gtnl.common.packet;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.meminiterminal.MiniTerminalPinSettings;
import com.science.gtnl.common.meminiterminal.MiniTerminalSessionService;
import com.science.gtnl.common.meminiterminal.MiniTerminalStackTypes;

import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.helpers.MonitorableAction;
import appeng.items.contents.PinList;
import appeng.util.Platform;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;

public class MiniTerminalRequestPacket extends ServerboundPacket {

    private static final int MAX_PIN_BYTES = 24_000;
    private static final int PIN_DROP_HEADER_BYTES = 29;
    private static final int MAX_CREATIVE_ACTION_BYTES = 64 * 1024;
    private static final int ACTION_HEADER_BYTES = 1 + 8 + 8 + 8 + 8 + 4 + 4 + 1;

    public enum Kind {
        BEGIN,
        END,
        ACTION,
        RESYNC,
        FULL,
        PIN_SETTINGS,
        SETTINGS,
        INVENTORY_DEPOSIT,
        INVENTORY_EXTRACT,
        PICKUP_TOGGLE,
        PIN_DROP,
        CREATIVE_ACTION
    }

    public enum DepositMode {
        STACK,
        ONE,
        MATCHING,
        REGION,
        NATIVE_REGION
    }

    public Kind kind;
    public long sessionId;
    public long requestId;
    public long actionId;
    public long catalogEpoch;
    public long catalogRevision;
    public long pinRevision;
    public int windowId;
    public int slot = -1;
    public int entryId = -1;
    public int nativePin = -1;
    public UUID terminalId;
    public MonitorableAction action;
    public MiniTerminalPinSettings pinSettings;
    public SortOrder sortOrder;
    public SortDir sortDirection;
    public ViewItems viewMode;
    public DepositMode depositMode = DepositMode.STACK;
    public boolean emptyOnly;
    public boolean single;
    public Reference2BooleanMap<IAEStackType<?>> typeFilters;
    public IAEStack<?> pinTarget;
    public ItemStack creativeCursor;
    public byte[] creativeCursorPayload;

    public MiniTerminalRequestPacket() {}

    public static MiniTerminalRequestPacket begin(int slot, UUID preferredId, long requestId, int windowId,
        long catalogEpoch, long catalogRevision) {
        MiniTerminalRequestPacket packet = new MiniTerminalRequestPacket();
        packet.kind = Kind.BEGIN;
        packet.slot = slot;
        packet.terminalId = preferredId;
        packet.requestId = requestId;
        packet.windowId = windowId;
        packet.catalogEpoch = catalogEpoch;
        packet.catalogRevision = catalogRevision;
        return packet;
    }

    public static MiniTerminalRequestPacket openFull(int slot, UUID id, int windowId) {
        MiniTerminalRequestPacket packet = begin(slot, id, 0, windowId, 0, 0);
        packet.kind = Kind.FULL;
        return packet;
    }

    public static MiniTerminalRequestPacket end(long sessionId) {
        MiniTerminalRequestPacket packet = new MiniTerminalRequestPacket();
        packet.kind = Kind.END;
        packet.sessionId = sessionId;
        return packet;
    }

    public static MiniTerminalRequestPacket resync(long sessionId) {
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.RESYNC;
        return packet;
    }

    public static MiniTerminalRequestPacket action(long sessionId, long actionId, long catalogRevision,
        long pinRevision, int entryId, int nativePin, MonitorableAction action) {
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.ACTION;
        packet.actionId = actionId;
        packet.catalogRevision = catalogRevision;
        packet.pinRevision = pinRevision;
        packet.entryId = entryId;
        packet.nativePin = nativePin;
        packet.action = action;
        return packet;
    }

    public boolean withCreativeCursor(ItemStack cursor) {
        if (kind != Kind.ACTION) throw new IllegalStateException("Creative cursor requires a terminal action");
        if (cursor != null && (cursor.getItemDamage() < 0 || cursor.stackSize <= 0 || cursor.stackSize > 64))
            return false;
        ByteBuf encoded = Unpooled.buffer();
        try {
            ByteBufUtils.writeItemStack(encoded, cursor);
            if (ACTION_HEADER_BYTES + encoded.readableBytes() > MAX_CREATIVE_ACTION_BYTES) return false;
            creativeCursorPayload = new byte[encoded.readableBytes()];
            encoded.getBytes(encoded.readerIndex(), creativeCursorPayload);
        } finally {
            encoded.release();
        }
        kind = Kind.CREATIVE_ACTION;
        creativeCursor = ItemStack.copyItemStack(cursor);
        return true;
    }

    public static MiniTerminalRequestPacket pinDrop(long sessionId, long actionId, long pinRevision, int nativePin,
        IAEStack<?> target) throws IOException {
        if (target == null) throw new IOException("Unsupported mini terminal pin type");
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.PIN_DROP;
        packet.actionId = actionId;
        packet.pinRevision = pinRevision;
        packet.nativePin = nativePin;
        packet.pinTarget = target.copy()
            .reset()
            .setStackSize(1);
        ByteBuf encoded = Unpooled.buffer();
        try {
            Platform.writeStackByte(packet.pinTarget, encoded);
            if (encoded.readableBytes() > MAX_PIN_BYTES) throw new IOException("Mini terminal pin too large");
        } catch (RuntimeException exception) {
            throw new IOException("Invalid mini terminal pin", exception);
        } finally {
            encoded.release();
        }
        return packet;
    }

    public static MiniTerminalRequestPacket pinSettings(long sessionId, long actionId, long pinRevision,
        MiniTerminalPinSettings settings) {
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.PIN_SETTINGS;
        packet.actionId = actionId;
        packet.pinRevision = pinRevision;
        packet.pinSettings = settings;
        return packet;
    }

    public static MiniTerminalRequestPacket settings(long sessionId, long actionId, int slot, UUID terminalId,
        SortOrder sortOrder, SortDir sortDirection, ViewItems viewMode,
        Reference2BooleanMap<IAEStackType<?>> typeFilters, int windowId) {
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.SETTINGS;
        packet.actionId = actionId;
        packet.slot = slot;
        packet.terminalId = terminalId;
        packet.sortOrder = sortOrder;
        packet.sortDirection = sortDirection;
        packet.viewMode = viewMode;
        packet.typeFilters = typeFilters;
        packet.windowId = windowId;
        return packet;
    }

    public static MiniTerminalRequestPacket pickupToggle(long sessionId, long actionId, int slot, UUID terminalId,
        int windowId) {
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.PICKUP_TOGGLE;
        packet.actionId = actionId;
        packet.slot = slot;
        packet.terminalId = terminalId;
        packet.windowId = windowId;
        return packet;
    }

    public static MiniTerminalRequestPacket inventoryDeposit(long sessionId, long actionId, int inventorySlot,
        DepositMode mode) {
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.INVENTORY_DEPOSIT;
        packet.actionId = actionId;
        packet.slot = inventorySlot;
        packet.depositMode = mode;
        return packet;
    }

    public static MiniTerminalRequestPacket inventoryExtract(long sessionId, long actionId, long catalogRevision,
        long pinRevision, int entryId, int nativePin, boolean emptyOnly, boolean single) {
        MiniTerminalRequestPacket packet = action(
            sessionId,
            actionId,
            catalogRevision,
            pinRevision,
            entryId,
            nativePin,
            MonitorableAction.PICKUP_SINGLE);
        packet.kind = Kind.INVENTORY_EXTRACT;
        packet.emptyOnly = emptyOnly;
        packet.single = single;
        return packet;
    }

    @Override
    protected void read(ByteBuf buf) {
        int requestBytes = buf.readableBytes();
        kind = readEnum(buf, Kind.values());
        int limit = switch (kind) {
            case PIN_DROP -> PIN_DROP_HEADER_BYTES + MAX_PIN_BYTES;
            case CREATIVE_ACTION -> MAX_CREATIVE_ACTION_BYTES;
            case SETTINGS -> 128;
            default -> 80;
        };
        if (requestBytes > limit) throw new IllegalArgumentException("Mini terminal request too large");
        sessionId = buf.readLong();
        switch (kind) {
            case BEGIN, FULL -> {
                requestId = buf.readLong();
                windowId = buf.readInt();
                slot = buf.readInt();
                if (slot != -1 && !isTerminalAddress(slot)) throw new IllegalArgumentException("Mini terminal slot");
                if (buf.readBoolean()) terminalId = new UUID(buf.readLong(), buf.readLong());
                catalogEpoch = buf.readLong();
                catalogRevision = buf.readLong();
                if (catalogRevision < 0) throw new IllegalArgumentException("Mini terminal catalog revision");
            }
            case ACTION, CREATIVE_ACTION -> {
                actionId = buf.readLong();
                catalogRevision = buf.readLong();
                pinRevision = buf.readLong();
                entryId = buf.readInt();
                nativePin = buf.readInt();
                action = readEnum(buf, MonitorableAction.values());
                if (action == MonitorableAction.SET_PIN)
                    throw new IllegalArgumentException("Use mini terminal pin drop");
                if (actionId <= 0 || nativePin < -1 || nativePin >= PinList.TOTAL_SLOTS || entryId < -1) {
                    throw new IllegalArgumentException("Mini terminal action target");
                }
                if (kind == Kind.CREATIVE_ACTION) {
                    creativeCursor = ByteBufUtils.readItemStack(buf);
                    if (creativeCursor != null
                        && (creativeCursor.getItem() == null || creativeCursor.getItemDamage() < 0
                            || creativeCursor.stackSize <= 0
                            || creativeCursor.stackSize > 64))
                        throw new IllegalArgumentException("Creative terminal cursor");
                }
            }
            case PIN_SETTINGS -> {
                actionId = buf.readLong();
                pinRevision = buf.readLong();
                pinSettings = new MiniTerminalPinSettings(buf.readUnsignedByte(), buf.readUnsignedByte());
                if (actionId <= 0) throw new IllegalArgumentException("Mini terminal pin settings action");
            }
            case PIN_DROP -> {
                actionId = buf.readLong();
                pinRevision = buf.readLong();
                nativePin = buf.readInt();
                if (actionId <= 0 || pinRevision <= 0
                    || nativePin < PinList.PLAYER_OFFSET
                    || nativePin >= PinList.TOTAL_SLOTS)
                    throw new IllegalArgumentException("Mini terminal pin drop target");
                pinTarget = Platform.readStackByte(buf);
                if (pinTarget == null) throw new IllegalArgumentException("Unsupported mini terminal pin type");
            }
            case SETTINGS, PICKUP_TOGGLE -> {
                actionId = buf.readLong();
                slot = buf.readInt();
                if (!isTerminalAddress(slot)) throw new IllegalArgumentException("Mini terminal settings slot");
                terminalId = new UUID(buf.readLong(), buf.readLong());
                if (kind == Kind.SETTINGS) {
                    sortOrder = readEnum(buf, SortOrder.values());
                    sortDirection = readEnum(buf, SortDir.values());
                    viewMode = readEnum(buf, ViewItems.values());
                    typeFilters = MiniTerminalStackTypes.readFilters(buf);
                }
                windowId = buf.readInt();
                if (actionId <= 0) throw new IllegalArgumentException("Mini terminal settings action");
            }
            case INVENTORY_DEPOSIT -> {
                actionId = buf.readLong();
                slot = buf.readInt();
                depositMode = readEnum(buf, DepositMode.values());
                if (actionId <= 0 || slot < 0 || slot >= 36) {
                    throw new IllegalArgumentException("Mini terminal inventory deposit target");
                }
            }
            case INVENTORY_EXTRACT -> {
                actionId = buf.readLong();
                catalogRevision = buf.readLong();
                pinRevision = buf.readLong();
                entryId = buf.readInt();
                nativePin = buf.readInt();
                emptyOnly = readFlag(buf);
                single = readFlag(buf);
                if (actionId <= 0 || nativePin < -1 || nativePin >= PinList.TOTAL_SLOTS || entryId < -1) {
                    throw new IllegalArgumentException("Mini terminal inventory extraction target");
                }
            }
            default -> {}
        }
        if (buf.isReadable()) throw new IllegalArgumentException("Trailing mini terminal request data");
    }

    private static boolean isTerminalAddress(int slot) {

        return slot >= 0 && slot < 36 || slot >= Platform.baublesSlotsOffset;
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeByte(kind.ordinal());
        buf.writeLong(sessionId);
        switch (kind) {
            case BEGIN, FULL -> {
                buf.writeLong(requestId);
                buf.writeInt(windowId);
                buf.writeInt(slot);
                buf.writeBoolean(terminalId != null);
                if (terminalId != null) {
                    buf.writeLong(terminalId.getMostSignificantBits());
                    buf.writeLong(terminalId.getLeastSignificantBits());
                }
                buf.writeLong(catalogEpoch);
                buf.writeLong(catalogRevision);
            }
            case ACTION, CREATIVE_ACTION -> {
                buf.writeLong(actionId);
                buf.writeLong(catalogRevision);
                buf.writeLong(pinRevision);
                buf.writeInt(entryId);
                buf.writeInt(nativePin);
                buf.writeByte(action.ordinal());
                if (kind == Kind.CREATIVE_ACTION) {
                    if (creativeCursorPayload != null) buf.writeBytes(creativeCursorPayload);
                    else ByteBufUtils.writeItemStack(buf, creativeCursor);
                }
            }
            case PIN_SETTINGS -> {
                buf.writeLong(actionId);
                buf.writeLong(pinRevision);
                buf.writeByte(pinSettings.craftingCount());
                buf.writeByte(pinSettings.playerCount());
            }
            case PIN_DROP -> {
                buf.writeLong(actionId);
                buf.writeLong(pinRevision);
                buf.writeInt(nativePin);
                Platform.writeStackByte(pinTarget, buf);
            }
            case SETTINGS, PICKUP_TOGGLE -> {
                buf.writeLong(actionId);
                buf.writeInt(slot);
                buf.writeLong(terminalId.getMostSignificantBits());
                buf.writeLong(terminalId.getLeastSignificantBits());
                if (kind == Kind.SETTINGS) {
                    buf.writeByte(sortOrder.ordinal());
                    buf.writeByte(sortDirection.ordinal());
                    buf.writeByte(viewMode.ordinal());
                    MiniTerminalStackTypes.writeFilters(buf, typeFilters);
                }
                buf.writeInt(windowId);
            }
            case INVENTORY_DEPOSIT -> {
                buf.writeLong(actionId);
                buf.writeInt(slot);
                buf.writeByte(depositMode.ordinal());
            }
            case INVENTORY_EXTRACT -> {
                buf.writeLong(actionId);
                buf.writeLong(catalogRevision);
                buf.writeLong(pinRevision);
                buf.writeInt(entryId);
                buf.writeInt(nativePin);
                buf.writeBoolean(emptyOnly);
                buf.writeBoolean(single);
            }
            default -> {}
        }
    }

    private static <E extends Enum<E>> E readEnum(ByteBuf buf, E[] values) {
        int ordinal = buf.readUnsignedByte();
        if (ordinal >= values.length) throw new IllegalArgumentException("Mini terminal request enum");
        return values[ordinal];
    }

    private static boolean readFlag(ByteBuf buf) {
        int value = buf.readUnsignedByte();
        if (value > 1) throw new IllegalArgumentException("Mini terminal request flag");
        return value == 1;
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> MiniTerminalSessionService.handle(player, this));
    }
}
