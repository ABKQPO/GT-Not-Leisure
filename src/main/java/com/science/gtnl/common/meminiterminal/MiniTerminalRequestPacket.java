package com.science.gtnl.common.meminiterminal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import appeng.api.config.PinsRows;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.helpers.MonitorableAction;
import appeng.items.contents.PinList;
import appeng.util.Platform;
import io.netty.buffer.ByteBuf;

public class MiniTerminalRequestPacket extends ServerboundPacket {

    private static final int MAX_PIN_COMPRESSED_BYTES = 24_000;
    private static final int MAX_PIN_NBT_BYTES = 128 * 1024;
    private static final int PIN_DROP_HEADER_BYTES = 33;

    enum Kind {
        BEGIN,
        END,
        ACTION,
        RESYNC,
        FULL,
        PIN_ROWS,
        SETTINGS,
        INVENTORY_DEPOSIT,
        INVENTORY_EXTRACT,
        PICKUP_TOGGLE,
        PIN_DROP
    }

    public enum DepositMode {
        STACK,
        ONE,
        MATCHING,
        REGION,
        NATIVE_REGION
    }

    Kind kind;
    long sessionId;
    long requestId;
    long actionId;
    long catalogRevision;
    long pinRevision;
    int windowId;
    int slot = -1;
    int entryId = -1;
    int nativePin = -1;
    UUID terminalId;
    MonitorableAction action;
    PinsRows craftingRows;
    PinsRows playerRows;
    SortOrder sortOrder;
    SortDir sortDirection;
    ViewItems viewMode;
    DepositMode depositMode = DepositMode.STACK;
    boolean emptyOnly;
    boolean single;
    boolean showFluids;
    NBTTagCompound pinTarget;
    private byte[] pinData;

    public MiniTerminalRequestPacket() {}

    public static MiniTerminalRequestPacket begin(int slot, UUID preferredId, long requestId, int windowId) {
        MiniTerminalRequestPacket packet = new MiniTerminalRequestPacket();
        packet.kind = Kind.BEGIN;
        packet.slot = slot;
        packet.terminalId = preferredId;
        packet.requestId = requestId;
        packet.windowId = windowId;
        return packet;
    }

    public static MiniTerminalRequestPacket openFull(int slot, UUID id, int windowId) {
        MiniTerminalRequestPacket packet = begin(slot, id, 0, windowId);
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

    public static MiniTerminalRequestPacket pinDrop(long sessionId, long actionId, long pinRevision, int nativePin,
        IAEStack<?> target) throws IOException {
        if (!(target instanceof IAEItemStack || target instanceof IAEFluidStack))
            throw new IOException("Unsupported mini terminal pin type");
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.PIN_DROP;
        packet.actionId = actionId;
        packet.pinRevision = pinRevision;
        packet.nativePin = nativePin;
        try {
            packet.pinData = CompressedStreamTools.compress(
                target.copy()
                    .reset()
                    .setStackSize(1)
                    .toNBTGeneric());
            if (packet.pinData.length > MAX_PIN_COMPRESSED_BYTES) throw new IOException("Mini terminal pin too large");
            packet.pinTarget = readPinTarget(packet.pinData);
        } catch (RuntimeException exception) {
            throw new IOException("Invalid mini terminal pin", exception);
        }
        return packet;
    }

    public static MiniTerminalRequestPacket pinsRows(long sessionId, long actionId, long pinRevision,
        PinsRows craftingRows, PinsRows playerRows) {
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.PIN_ROWS;
        packet.actionId = actionId;
        packet.pinRevision = pinRevision;
        packet.craftingRows = craftingRows;
        packet.playerRows = playerRows;
        return packet;
    }

    public static MiniTerminalRequestPacket settings(long sessionId, long actionId, int slot, UUID terminalId,
        SortOrder sortOrder, SortDir sortDirection, ViewItems viewMode, boolean showFluids, int windowId) {
        MiniTerminalRequestPacket packet = end(sessionId);
        packet.kind = Kind.SETTINGS;
        packet.actionId = actionId;
        packet.slot = slot;
        packet.terminalId = terminalId;
        packet.sortOrder = sortOrder;
        packet.sortDirection = sortDirection;
        packet.viewMode = viewMode;
        packet.showFluids = showFluids;
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
        int limit = kind == Kind.PIN_DROP ? PIN_DROP_HEADER_BYTES + MAX_PIN_COMPRESSED_BYTES : 80;
        if (requestBytes > limit) throw new IllegalArgumentException("Mini terminal request too large");
        sessionId = buf.readLong();
        switch (kind) {
            case BEGIN, FULL -> {
                requestId = buf.readLong();
                windowId = buf.readInt();
                slot = buf.readInt();
                if (slot != -1 && !isTerminalAddress(slot)) throw new IllegalArgumentException("Mini terminal slot");
                if (buf.readBoolean()) terminalId = new UUID(buf.readLong(), buf.readLong());
            }
            case ACTION -> {
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
            }
            case PIN_ROWS -> {
                actionId = buf.readLong();
                pinRevision = buf.readLong();
                craftingRows = readEnum(buf, PinsRows.values());
                playerRows = readEnum(buf, PinsRows.values());
                if (actionId <= 0) throw new IllegalArgumentException("Mini terminal pin rows action");
            }
            case PIN_DROP -> {
                actionId = buf.readLong();
                pinRevision = buf.readLong();
                nativePin = buf.readInt();
                int length = buf.readInt();
                if (actionId <= 0 || pinRevision <= 0
                    || nativePin < PinList.PLAYER_OFFSET
                    || nativePin >= PinList.TOTAL_SLOTS
                    || length <= 0
                    || length > MAX_PIN_COMPRESSED_BYTES
                    || length != buf.readableBytes())
                    throw new IllegalArgumentException("Mini terminal pin drop target");
                pinData = new byte[length];
                buf.readBytes(pinData);
                try {
                    pinTarget = readPinTarget(pinData);
                } catch (IOException exception) {
                    throw new IllegalArgumentException("Invalid mini terminal pin NBT", exception);
                }
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
                    showFluids = readFlag(buf);
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
            }
            case ACTION -> {
                buf.writeLong(actionId);
                buf.writeLong(catalogRevision);
                buf.writeLong(pinRevision);
                buf.writeInt(entryId);
                buf.writeInt(nativePin);
                buf.writeByte(action.ordinal());
            }
            case PIN_ROWS -> {
                buf.writeLong(actionId);
                buf.writeLong(pinRevision);
                buf.writeByte(craftingRows.ordinal());
                buf.writeByte(playerRows.ordinal());
            }
            case PIN_DROP -> {
                buf.writeLong(actionId);
                buf.writeLong(pinRevision);
                buf.writeInt(nativePin);
                buf.writeInt(pinData.length);
                buf.writeBytes(pinData);
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
                    buf.writeBoolean(showFluids);
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

    private static NBTTagCompound readPinTarget(byte[] compressed) throws IOException {
        byte[] raw;
        try (GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            raw = input.readNBytes(MAX_PIN_NBT_BYTES + 1);
        }
        if (raw.length > MAX_PIN_NBT_BYTES) throw new IOException("Mini terminal pin NBT too large");
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(raw))) {
            if (input.readUnsignedByte() != 10) throw new IOException("Mini terminal pin must be a compound");
            skipPinBytes(input, input.readUnsignedShort());
            validatePinTag(input, 10, 0);
            if (input.available() != 0) throw new IOException("Trailing mini terminal pin NBT");
        }
        NBTTagCompound target = CompressedStreamTools
            .func_152456_a(new DataInputStream(new ByteArrayInputStream(raw)), new NBTSizeTracker(MAX_PIN_NBT_BYTES));
        String type = target.getString("StackType");
        if (!type.equals("item") && !type.equals("fluid")) throw new IOException("Unsupported mini terminal pin type");
        return target;
    }

    // Vanilla's array-size accounting multiplies ints before widening; validate lengths before it allocates.
    private static void validatePinTag(DataInputStream input, int type, int depth) throws IOException {
        if (depth > 64) throw new IOException("Mini terminal pin NBT too deep");
        switch (type) {
            case 1 -> skipPinBytes(input, 1);
            case 2 -> skipPinBytes(input, 2);
            case 3, 5 -> skipPinBytes(input, 4);
            case 4, 6 -> skipPinBytes(input, 8);
            case 7, 11 -> {
                int length = input.readInt();
                int width = type == 7 ? 1 : 4;
                if (length < 0 || length > input.available() / width)
                    throw new IOException("Mini terminal pin NBT array");
                skipPinBytes(input, length * width);
            }
            case 8 -> skipPinBytes(input, input.readUnsignedShort());
            case 9 -> {
                int elementType = input.readUnsignedByte();
                int length = input.readInt();
                if (elementType > 11 || length < 0 || length > input.available() || elementType == 0 && length != 0)
                    throw new IOException("Mini terminal pin NBT list");
                for (int index = 0; index < length; index++) validatePinTag(input, elementType, depth + 1);
            }
            case 10 -> {
                int childType;
                while ((childType = input.readUnsignedByte()) != 0) {
                    skipPinBytes(input, input.readUnsignedShort());
                    validatePinTag(input, childType, depth + 1);
                }
            }
            default -> throw new IOException("Mini terminal pin NBT tag type");
        }
    }

    private static void skipPinBytes(DataInputStream input, int length) throws IOException {
        if (length > input.available() || input.skipBytes(length) != length)
            throw new IOException("Truncated mini terminal pin NBT");
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
