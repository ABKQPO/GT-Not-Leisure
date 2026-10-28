package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessCardActions.CLOSE_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_VISUALISATION;
import static com.science.gtnl.common.wireless.WirelessCardActions.HIGHLIGHT;
import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.REFRESH;
import static com.science.gtnl.common.wireless.WirelessCardActions.RENAME;
import static com.science.gtnl.common.wireless.WirelessCardActions.SET_VISUALISATION;
import static com.science.gtnl.common.wireless.WirelessCardActions.TELEPORT;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_AUTO;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_GROUPING;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_PAUSE;
import static com.science.gtnl.common.wireless.WirelessCardActions.isRowAction;

import java.util.Comparator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;

public class WirelessCardActionHandler {

    private final WirelessCardContainer session;
    private long nextRequestTick = Long.MIN_VALUE;

    WirelessCardActionHandler(WirelessCardContainer session) {
        this.session = session;
    }

    void execute(long revision, int action, long targetId, String text, long nameRevision) {
        EntityPlayer player = session.player;
        ItemStack card = session.card;
        WirelessCardBinding binding = session.binding;
        if (action == CLOSE_DETAILS) {
            if (session.view.detailNode != null) {
                session.view.detailNode = null;
                session.update(true);
            }
            return;
        }
        if (isRowAction(action) && action != OPEN_DETAILS
            && action != TOGGLE_PAUSE
            && action != TELEPORT
            && action != HIGHLIGHT) return;
        if (!acceptRequest(player.worldObj.getTotalWorldTime())) return;
        if (action == OPEN_DETAILS) session.update(false);
        if (session.revision != revision) {
            session.update(true);
            return;
        }
        var row = session.snapshot == null ? null : session.snapshot.target(targetId);
        if (isRowAction(action) || action == RENAME) {
            if (binding == null || row == null) {
                message("stale");
                session.update(true);
                return;
            }
        }
        if (action == CYCLE_VISUALISATION) {
            WirelessCardVisualisation.cycle(card, player);
        } else if (action == SET_VISUALISATION) {
            if (targetId >= 0 && targetId < WirelessCardVisualisation.MODE_COUNT)
                WirelessCardVisualisation.set(card, player, (int) targetId);
        } else if (action == RENAME) {
            if (session.view.detailNode == null || !row.members()
                .contains(session.view.detailNode)) return;
            if (session.view.nameRevision() != nameRevision) {
                message("stale");
            } else {
                try {
                    if (!WirelessOperations.rename(player, binding.frequency(), row, text)) message("denied");
                } catch (WirelessOperations.Denied denied) {
                    message("denied");
                } catch (IllegalStateException stale) {
                    message("stale");
                }
            }
        } else if (action == TELEPORT && player instanceof EntityPlayerMP serverPlayer) {
            try {
                switch (WirelessOperations.teleport(serverPlayer, binding.frequency(), row)) {
                    case STALE -> message("stale");
                    case UNAVAILABLE -> message("teleport_unavailable");
                    case DENIED -> message("denied");
                    case APPLIED -> {
                        return;
                    }
                    default -> {}
                }
            } catch (PhysicalClusterTracker.ScanLimitException limit) {
                message("stale");
            }
        } else if (action == HIGHLIGHT) {
            try {
                if (!WirelessClusterManager.isCurrentLink(binding.frequency(), row)) {
                    message("stale");
                    session.update(true);
                    return;
                }
            } catch (PhysicalClusterTracker.ScanLimitException limit) {
                message("stale");
                return;
            }
            if (row.target()
                .dimension() != player.dimension) {
                message(
                    "highlight_dimension",
                    row.target()
                        .dimension());
            } else {
                session.pendingHighlight = new NBTTagList();
                var targets = WirelessClusterManager.highlight(row)
                    .stream()
                    .filter(target -> target.dimension() == player.dimension)
                    .sorted(
                        Comparator.comparingDouble(target -> player.getDistanceSq(target.x(), target.y(), target.z())))
                    .limit(512)
                    .toList();
                for (var target : targets) {
                    NBTTagCompound pos = new NBTTagCompound();
                    pos.setInteger("dimension", target.dimension());
                    pos.setInteger("x", target.x());
                    pos.setInteger("y", target.y());
                    pos.setInteger("z", target.z());
                    session.pendingHighlight.appendTag(pos);
                }
                message("highlight_started", targets.size());
            }
        } else if (action == TOGGLE_AUTO) {
            WirelessAutoConnect.toggleAutomatic(player, card);
        } else if (action == TOGGLE_GROUPING && session.view.detailNode == null) {
            WirelessCardBinding.toggleGrouping(card);
            WirelessCardInventory.sync(player, card);
        } else if (action == OPEN_DETAILS) {
            if (session.view.detailNode == null && row instanceof IWirelessLinkView.ClusterView)
                session.view.detailNode = row.node();
        } else if (action == TOGGLE_PAUSE) {
            try {
                WirelessOperations.setPaused(player, binding.frequency(), row);
            } catch (WirelessOperations.Denied denied) {
                message("denied");
            } catch (IllegalStateException stale) {
                message("stale");
            }
        } else if (action != REFRESH) return;
        session.update(true);
    }

    private boolean acceptRequest(long tick) {
        if (tick < nextRequestTick) return false;
        nextRequestTick = tick + 4;
        return true;
    }

    private void message(String key, Object... args) {
        session.player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui." + key, args));
    }
}
