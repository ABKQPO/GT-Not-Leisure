package com.science.gtnl.common.wireless;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.utils.world.BlockSideTeleport;

public class WirelessCardTeleport {

    private WirelessCardTeleport() {}

    static boolean teleport(EntityPlayerMP player, Address target) {
        BlockSideTeleport.Outcome outcome = BlockSideTeleport
            .toBlockSide(player, target.dimension(), target.x(), target.y(), target.z(), target.side());
        if (outcome == BlockSideTeleport.Outcome.MOVED) {
            player.addChatMessage(
                new ChatComponentTranslation(
                    "gtnl.wireless.gui.teleport_done",
                    target.dimension(),
                    target.x(),
                    target.y(),
                    target.z()));
            return true;
        }
        return fail(player, switch (outcome) {
            case UNAVAILABLE -> "teleport_unavailable";
            case NO_SPACE -> "teleport_no_space";
            default -> "teleport_failed";
        });
    }

    private static boolean fail(EntityPlayerMP player, String key) {
        player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui." + key));
        return false;
    }
}
