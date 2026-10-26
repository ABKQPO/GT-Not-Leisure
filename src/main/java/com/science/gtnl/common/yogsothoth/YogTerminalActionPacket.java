package com.science.gtnl.common.yogsothoth;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class YogTerminalActionPacket extends ServerboundPacket {

    public enum Action {
        CLICK,
        SHIFT_CLICK,
        USE,
        CYCLE,
        DROP_FIRST,
        SYNC,
        SCREEN,
        OPEN_SCREEN,
        TOGGLE_WINDOW,
        DISGUISE
    }

    private static final Action[] ACTIONS = Action.values();

    private Action action;
    private UUID id;
    private int location;
    private int index;
    private boolean flag;
    private boolean creative;
    private ItemStack cursor;

    public YogTerminalActionPacket() {}

    private YogTerminalActionPacket(Action action, UUID id, int location, int index, boolean flag) {
        this.action = action;
        this.id = id;
        this.location = location;
        this.index = index;
        this.flag = flag;
    }

    public static YogTerminalActionPacket click(UUID id, int location, int index, boolean shift) {
        return new YogTerminalActionPacket(shift ? Action.SHIFT_CLICK : Action.CLICK, id, location, index, false);
    }

    public static YogTerminalActionPacket creativeClick(UUID id, int location, int index, ItemStack cursor) {
        YogTerminalActionPacket packet = new YogTerminalActionPacket(Action.CLICK, id, location, index, false);
        packet.creative = true;
        packet.cursor = cursor;
        return packet;
    }

    public static YogTerminalActionPacket use(UUID id, int location, int index, boolean sneak) {
        return new YogTerminalActionPacket(Action.USE, id, location, index, sneak);
    }

    public static YogTerminalActionPacket cycle(boolean backwards) {
        return new YogTerminalActionPacket(Action.CYCLE, null, -1, 0, backwards);
    }

    public static YogTerminalActionPacket dropFirst() {
        return new YogTerminalActionPacket(Action.DROP_FIRST, null, -1, 0, false);
    }

    public static YogTerminalActionPacket screen(boolean open) {
        return new YogTerminalActionPacket(Action.SCREEN, null, -1, 0, open);
    }

    public static YogTerminalActionPacket openScreen() {
        return new YogTerminalActionPacket(Action.OPEN_SCREEN, null, -1, 0, false);
    }

    public static YogTerminalActionPacket toggleWindow(UUID id, int location) {
        return new YogTerminalActionPacket(Action.TOGGLE_WINDOW, id, location, 0, false);
    }

    public static YogTerminalActionPacket disguise(UUID id, int location, int index) {
        return new YogTerminalActionPacket(Action.DISGUISE, id, location, Math.max(-1, index) + 1, false);
    }

    public static YogTerminalActionPacket sync(UUID id) {
        return new YogTerminalActionPacket(Action.SYNC, id, -1, 0, false);
    }

    @Override
    protected void read(ByteBuf buf) {
        int ordinal = buf.readUnsignedByte();
        if (ordinal >= ACTIONS.length) throw new IllegalArgumentException("Unknown Yog-Sothoth action " + ordinal);
        action = ACTIONS[ordinal];
        if (buf.readBoolean()) id = new UUID(buf.readLong(), buf.readLong());
        location = buf.readShort();
        index = buf.readInt();
        flag = buf.readBoolean();
        creative = buf.readBoolean();
        if (creative) cursor = ByteBufUtils.readItemStack(buf);
        if (index < 0) throw new IllegalArgumentException("Negative Yog-Sothoth index");
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeByte(action.ordinal());
        buf.writeBoolean(id != null);
        if (id != null) {
            buf.writeLong(id.getMostSignificantBits());
            buf.writeLong(id.getLeastSignificantBits());
        }
        buf.writeShort(location);
        buf.writeInt(index);
        buf.writeBoolean(flag);
        buf.writeBoolean(creative);
        if (creative) ByteBufUtils.writeItemStack(buf, cursor);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (!YogTerminalStorage.loaded()) return;
            switch (action) {
                case CLICK -> YogTerminalService.click(player, id, location, index, false, creative, cursor);
                case SHIFT_CLICK -> YogTerminalService.click(player, id, location, index, true, false, null);
                case USE -> YogTerminalService.use(player, id, location, index, flag);
                case CYCLE -> YogTerminalService.cycle(player, flag);
                case DROP_FIRST -> YogTerminalService.dropFirst(player);
                case SYNC -> YogTerminalService.requestSync(player, id);
                case SCREEN -> YogTerminalService.clientScreen(player, flag);
                case OPEN_SCREEN -> YogTerminalService.openScreen(player);
                case TOGGLE_WINDOW -> YogTerminalService.toggleWindow(player, id, location);
                case DISGUISE -> YogTerminalService.disguise(player, id, location, index - 1);
            }
        });
    }
}
