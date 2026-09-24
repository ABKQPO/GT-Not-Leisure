package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.item.items.ItemMajoBroom;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import io.netty.buffer.ByteBuf;

public class MajoBroomActionPacket extends ServerboundPacket {

    public static final byte PLACE = 0;
    public static final byte PLACE_AND_RIDE = 1;

    private byte action;

    public MajoBroomActionPacket() {}

    public MajoBroomActionPacket(byte action) {
        this.action = action;
    }

    @Override
    protected void read(ByteBuf buf) {
        action = buf.readByte();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeByte(action);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        if (action != PLACE && action != PLACE_AND_RIDE) return;
        ServerThreadUtil.addScheduledTask(() -> place(player));
    }

    private void place(EntityPlayerMP player) {
        ItemStack held = player.getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemMajoBroom broom)) return;
        World world = player.worldObj;
        if (action == PLACE) {
            MovingObjectPosition hit = player.rayTrace(player.theItemInWorldManager.getBlockReachDistance(), 1.0F);
            if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
            broom.placeOnBlock(held, player, world, hit.blockX, hit.blockY, hit.blockZ, hit.sideHit);
        } else {
            broom.place(held, player, world, player.posX, player.posY + 0.05D, player.posZ, true);
        }
    }
}
