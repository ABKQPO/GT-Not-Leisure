package com.science.gtnl.common.metoolbox;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import appeng.api.config.PinsRows;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.helpers.MonitorableAction;
import appeng.items.contents.PinList;
import appeng.util.Platform;
import io.netty.buffer.ByteBuf;

public class ToolboxRequestPacket extends ServerboundPacket {

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
        PICKUP_TOGGLE
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
    UUID toolbox;
    MonitorableAction action;
    PinsRows craftingRows;
    PinsRows playerRows;
    SortOrder sortOrder;
    SortDir sortDirection;
    ViewItems viewMode;
    DepositMode depositMode = DepositMode.STACK;
    boolean emptyOnly;
    boolean single;

    public ToolboxRequestPacket() {}

    public static ToolboxRequestPacket begin(int slot, UUID preferredId, long requestId, int windowId) {
        ToolboxRequestPacket packet = new ToolboxRequestPacket();
        packet.kind = Kind.BEGIN;
        packet.slot = slot;
        packet.toolbox = preferredId;
        packet.requestId = requestId;
        packet.windowId = windowId;
        return packet;
    }

    public static ToolboxRequestPacket openFull(int slot, UUID id, int windowId) {
        ToolboxRequestPacket packet = begin(slot, id, 0, windowId);
        packet.kind = Kind.FULL;
        return packet;
    }

    public static ToolboxRequestPacket end(long sessionId) {
        ToolboxRequestPacket packet = new ToolboxRequestPacket();
        packet.kind = Kind.END;
        packet.sessionId = sessionId;
        return packet;
    }

    public static ToolboxRequestPacket resync(long sessionId) {
        ToolboxRequestPacket packet = end(sessionId);
        packet.kind = Kind.RESYNC;
        return packet;
    }

    public static ToolboxRequestPacket action(long sessionId, long actionId, long catalogRevision, long pinRevision,
        int entryId, int nativePin, MonitorableAction action) {
        ToolboxRequestPacket packet = end(sessionId);
        packet.kind = Kind.ACTION;
        packet.actionId = actionId;
        packet.catalogRevision = catalogRevision;
        packet.pinRevision = pinRevision;
        packet.entryId = entryId;
        packet.nativePin = nativePin;
        packet.action = action;
        return packet;
    }

    public static ToolboxRequestPacket pinsRows(long sessionId, long actionId, long pinRevision, PinsRows craftingRows,
        PinsRows playerRows) {
        ToolboxRequestPacket packet = end(sessionId);
        packet.kind = Kind.PIN_ROWS;
        packet.actionId = actionId;
        packet.pinRevision = pinRevision;
        packet.craftingRows = craftingRows;
        packet.playerRows = playerRows;
        return packet;
    }

    public static ToolboxRequestPacket settings(long sessionId, long actionId, int slot, UUID toolbox,
        SortOrder sortOrder, SortDir sortDirection, ViewItems viewMode, int windowId) {
        ToolboxRequestPacket packet = end(sessionId);
        packet.kind = Kind.SETTINGS;
        packet.actionId = actionId;
        packet.slot = slot;
        packet.toolbox = toolbox;
        packet.sortOrder = sortOrder;
        packet.sortDirection = sortDirection;
        packet.viewMode = viewMode;
        packet.windowId = windowId;
        return packet;
    }

    public static ToolboxRequestPacket pickupToggle(long sessionId, long actionId, int slot, UUID toolbox,
        int windowId) {
        ToolboxRequestPacket packet = end(sessionId);
        packet.kind = Kind.PICKUP_TOGGLE;
        packet.actionId = actionId;
        packet.slot = slot;
        packet.toolbox = toolbox;
        packet.windowId = windowId;
        return packet;
    }

    public static ToolboxRequestPacket inventoryDeposit(long sessionId, long actionId, int inventorySlot,
        DepositMode mode) {
        ToolboxRequestPacket packet = end(sessionId);
        packet.kind = Kind.INVENTORY_DEPOSIT;
        packet.actionId = actionId;
        packet.slot = inventorySlot;
        packet.depositMode = mode;
        return packet;
    }

    public static ToolboxRequestPacket inventoryExtract(long sessionId, long actionId, long catalogRevision,
        long pinRevision, int entryId, int nativePin, boolean emptyOnly, boolean single) {
        ToolboxRequestPacket packet = action(
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
        if (buf.readableBytes() > 80) throw new IllegalArgumentException("Toolbox request too large");
        kind = readEnum(buf, Kind.values());
        sessionId = buf.readLong();
        switch (kind) {
            case BEGIN, FULL -> {
                requestId = buf.readLong();
                windowId = buf.readInt();
                slot = buf.readInt();
                if (slot != -1 && !isToolboxAddress(slot)) throw new IllegalArgumentException("Toolbox slot");
                if (buf.readBoolean()) toolbox = new UUID(buf.readLong(), buf.readLong());
            }
            case ACTION -> {
                actionId = buf.readLong();
                catalogRevision = buf.readLong();
                pinRevision = buf.readLong();
                entryId = buf.readInt();
                nativePin = buf.readInt();
                action = readEnum(buf, MonitorableAction.values());
                if (actionId <= 0 || nativePin < -1 || nativePin >= PinList.TOTAL_SLOTS || entryId < -1) {
                    throw new IllegalArgumentException("Toolbox action target");
                }
            }
            case PIN_ROWS -> {
                actionId = buf.readLong();
                pinRevision = buf.readLong();
                craftingRows = readEnum(buf, PinsRows.values());
                playerRows = readEnum(buf, PinsRows.values());
                if (actionId <= 0) throw new IllegalArgumentException("Toolbox pin rows action");
            }
            case SETTINGS, PICKUP_TOGGLE -> {
                actionId = buf.readLong();
                slot = buf.readInt();
                if (!isToolboxAddress(slot)) throw new IllegalArgumentException("Toolbox settings slot");
                toolbox = new UUID(buf.readLong(), buf.readLong());
                if (kind == Kind.SETTINGS) {
                    sortOrder = readEnum(buf, SortOrder.values());
                    sortDirection = readEnum(buf, SortDir.values());
                    viewMode = readEnum(buf, ViewItems.values());
                }
                windowId = buf.readInt();
                if (actionId <= 0) throw new IllegalArgumentException("Toolbox settings action");
            }
            case INVENTORY_DEPOSIT -> {
                actionId = buf.readLong();
                slot = buf.readInt();
                depositMode = readEnum(buf, DepositMode.values());
                if (actionId <= 0 || slot < 0 || slot >= 36) {
                    throw new IllegalArgumentException("Toolbox inventory deposit target");
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
                    throw new IllegalArgumentException("Toolbox inventory extraction target");
                }
            }
            default -> {}
        }
        if (buf.isReadable()) throw new IllegalArgumentException("Trailing toolbox request data");
    }

    private static boolean isToolboxAddress(int slot) {

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
                buf.writeBoolean(toolbox != null);
                if (toolbox != null) {
                    buf.writeLong(toolbox.getMostSignificantBits());
                    buf.writeLong(toolbox.getLeastSignificantBits());
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
            case SETTINGS, PICKUP_TOGGLE -> {
                buf.writeLong(actionId);
                buf.writeInt(slot);
                buf.writeLong(toolbox.getMostSignificantBits());
                buf.writeLong(toolbox.getLeastSignificantBits());
                if (kind == Kind.SETTINGS) {
                    buf.writeByte(sortOrder.ordinal());
                    buf.writeByte(sortDirection.ordinal());
                    buf.writeByte(viewMode.ordinal());
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
        if (ordinal >= values.length) throw new IllegalArgumentException("Toolbox request enum");
        return values[ordinal];
    }

    private static boolean readFlag(ByteBuf buf) {
        int value = buf.readUnsignedByte();
        if (value > 1) throw new IllegalArgumentException("Toolbox request flag");
        return value == 1;
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> ToolboxSessionService.handle(player, this));
    }
}
